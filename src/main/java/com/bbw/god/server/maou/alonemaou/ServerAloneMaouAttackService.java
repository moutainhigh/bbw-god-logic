package com.bbw.god.server.maou.alonemaou;

import com.bbw.common.ListUtil;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.game.config.CfgAloneMaou;
import com.bbw.god.game.config.card.CardTool;
import com.bbw.god.game.config.card.CfgCardEntity;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.card.UserCard;
import com.bbw.god.gameuser.card.UserCardService;
import com.bbw.god.gameuser.res.ResChecker;
import com.bbw.god.server.maou.ServerMaouStatus;
import com.bbw.god.server.maou.alonemaou.attackinfo.AloneMaouAttackSummary;
import com.bbw.god.server.maou.alonemaou.attackinfo.AloneMaouLevelInfo;
import com.bbw.god.server.maou.alonemaou.attackinfo.AloneMaouLevelService;
import com.bbw.god.server.maou.alonemaou.maouskill.BaseMaouSkillService;
import com.bbw.god.server.maou.alonemaou.maouskill.MaouSkillEnum;
import com.bbw.god.server.maou.alonemaou.maouskill.MaouSkillServiceFactory;
import com.bbw.god.server.maou.alonemaou.rd.RDAloneMaouAttack;
import com.bbw.god.server.maou.attack.MaouAttackType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 独战魔王逻辑
 *
 * @author suhq
 * @date 2019年12月19日 上午11:46:27
 */
@Slf4j
@Service
public class ServerAloneMaouAttackService {
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private AloneMaouLevelService maouLevelService;
    @Autowired
    private UserCardService userCardService;
    @Autowired
    private MaouSkillServiceFactory maouSkillServiceFactory;

    public BeforeAloneAttack beforeAttack(long uid, int attackTypeInt, AloneMaouParam param) {

        List<Integer> attackCardIds = param.getUserMaouData().getAttackCards();
        CfgAloneMaou config = param.getConfig();
        //卡牌编组是否足够3张
        if (ListUtil.isEmpty(attackCardIds) || attackCardIds.size() < config.getCardLimit()) {
            throw new ExceptionForClientTip("maouboss.card.group.no.enough", config.getCardLimit());
        }
        ServerAloneMaou aloneMaou = param.getMaou();
        AloneMaouLevelInfo maouLevelInfo = param.getLevelInfo();
        // 打到一半刷新了
        if (null == maouLevelInfo) {
            throw new ExceptionForClientTip("maoualone.already.refreash");
        }
        //第七层魔王要同属性卡牌
        List<CfgCardEntity> cfgCardEntities = CardTool.getCards(attackCardIds);
        if (maouLevelInfo.getMaouLevel() == 7) {
            boolean isMatchType = cfgCardEntities.stream().anyMatch(tmp -> tmp.getType().intValue() == aloneMaou.getType());
            if (!isMatchType) {
                throw new ExceptionForClientTip("maoualone.card.no.match.type");
            }
        }

        ServerMaouStatus maouStatus = maouLevelInfo.gainMaouStatus();
        if (maouStatus == ServerMaouStatus.KILLED) {
            throw new ExceptionForClientTip("maou.already.die");
        } else if (maouStatus == ServerMaouStatus.LEAVE) {
            throw new ExceptionForClientTip("maoualone.already.leave");
        }
        AloneMaouAttackSummary myAttackInfo = param.getMyAttack();
        MaouAttackType attackType = MaouAttackType.fromValue(attackTypeInt);
        if (attackType == MaouAttackType.COMMON_ATTACK && myAttackInfo.getFreeAttackTimes() <= 0) {
            throw new ExceptionForClientTip("maoualone.attack.no.freetimes");
        }
        int needGold = getNeedGold(attackType, myAttackInfo.getBoughtTimes());
        GameUser gu = this.gameUserService.getGameUser(uid);
        ResChecker.checkGold(gu, needGold);
        return new BeforeAloneAttack(gu, needGold);
    }

    public int attacking(long uid, int attackType, AloneMaouParam param, RDAloneMaouAttack rd) {
        AloneMaouLevelInfo maouLevelInfo = param.getLevelInfo();
        int beatedBlood = this.getBeatedBlood(uid, param, rd);
        int beatedShield = 0;
        if (beatedBlood > 0) {
            int remainShield = maouLevelInfo.getRemainShield();
            if (remainShield > 0) {
                beatedShield = Math.min(beatedBlood, remainShield);
                beatedBlood = 0;
            }
            beatedBlood = beatedBlood > maouLevelInfo.getRemainBlood() ? maouLevelInfo.getRemainBlood() : beatedBlood;
            maouLevelInfo.lostBlood(beatedBlood);
            maouLevelInfo.lostShield(beatedShield);
        }
        //魔王未死，处理回血、回合技能更新
        if (!maouLevelInfo.isKilled()) {
            int recoverBlood = 0;
            if (maouLevelInfo.getMaouSkill() == MaouSkillEnum.RECOVER_BLOOD.getValue() && maouLevelInfo.getRound() % 2 == 1) {
                recoverBlood = (int) (maouLevelInfo.getTotalBlood() * 0.02);
            } else if (maouLevelInfo.getMaouLevel() == 10 && maouLevelInfo.getRound() % 3 == 2) {
                //更新10级回合技能
                if (maouLevelInfo.getRemainShield() > 0) {
                    recoverBlood = maouLevelInfo.getRemainShield();
                }
                maouLevelInfo.updateMaouSkill();
            }
            //回血
            maouLevelInfo.addBlood(recoverBlood);
        }
        //增加回合
        maouLevelInfo.addRound();
        //更新魔王
        this.maouLevelService.saveMaouLevelInfo(param.getMaou(), maouLevelInfo);
        //更新本轮攻击记录
        AloneMaouAttackSummary myAttack = param.getMyAttack();
        myAttack.updateAfterAttack(attackType != MaouAttackType.COMMON_ATTACK.getValue(), beatedBlood,
                maouLevelInfo.getMaouLevel());

        rd.setBeatBlood(beatedBlood);
        if (beatedShield > 0) {
            rd.setBeatShield(beatedShield);
        }
        return beatedBlood;
    }

    private int getBeatedBlood(long uid, AloneMaouParam param, RDAloneMaouAttack rd) {
        List<UserCard> attackCards = userCardService.getUserCards(uid, param.getUserMaouData().getAttackCards());
        AloneMaouLevelInfo maouLevelInfo = param.getLevelInfo();
        MaouSkillEnum maouSkill = MaouSkillEnum.fromValue(maouLevelInfo.getMaouSkill());
        BaseMaouSkillService service = maouSkillServiceFactory.getById(maouSkill);
        return service.getBeatedBlood(uid, param, attackCards, rd);
    }

    private int getNeedGold(MaouAttackType attackType, int boughtTimes) {
        if (attackType == MaouAttackType.COMMON_ATTACK) {
            return 0;
        }
        return 10 + (boughtTimes / 2) * 5;
    }
}
