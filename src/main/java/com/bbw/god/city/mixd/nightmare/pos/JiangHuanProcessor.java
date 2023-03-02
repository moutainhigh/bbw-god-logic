package com.bbw.god.city.mixd.nightmare.pos;

import com.bbw.god.city.mixd.nightmare.*;
import com.bbw.god.fight.FightSubmitParam;
import com.bbw.god.fight.RDFightResult;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.combat.data.CombatSkillEnum;
import com.bbw.god.game.combat.data.param.CCardParam;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.gameuser.card.UserCard;
import com.bbw.god.gameuser.card.event.CardEventPublisher;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 说明：巡使头领
 *
 * @author lwb
 * date 2021-05-28
 */
@Service
public class JiangHuanProcessor extends AbstractMiXianFightProcessor {

    @Override
    public boolean match(NightmareMiXianPosEnum miXianPosEnum) {
        return NightmareMiXianPosEnum.XUN_SHI_JIANG_HUAN.equals(miXianPosEnum);
    }

    @Override
    public boolean isXunShi() {
        return true;
    }

    @Override
    public int fightFailDeductBlood(long uid) {
        return 1;
    }

    /**
     *
     */
    @Override
    public MiXianEnemy buildAiCards(long uid, int currentMxdLevel, MiXianLevelData.PosData posData) {
        CfgNightmareMiXian cfg = NightmareMiXianTool.getCfg();
        List<CfgNightmareMiXian.CardParam> cards = cfg.getJiangHuanCards();
        List<CCardParam> cardParams=new ArrayList<>();
        for (CfgNightmareMiXian.CardParam card : cards) {
            cardParams.add(CCardParam.initMxdCards(card,20,10));
        }
        long aiId = NightmareMiXianTool.buildMxdAiId(currentMxdLevel, posData.getPos(), NightmareMiXianPosEnum.XUN_SHI_JIANG_HUAN.getType());
        MiXianEnemy enemy= MiXianEnemy.getInstance(aiId,posData);
        enemy.setNickname("巡使-姜环");
        enemy.setCardParams(cardParams);
        enemy.setHead(256);
        enemy.setLevel(135);
        enemy.setBuff(131420);
        return enemy;
    }

    @Override
    public List<Award> beatAwards(UserNightmareMiXian nightmareMiXian, RDFightResult rd) {
        long uid=nightmareMiXian.getGameUserId();
        CardEventPublisher.pubCardAddEvent(uid,256, WayEnum.MXD_FIGHT,WayEnum.MXD_FIGHT.getName(),rd);
        return new ArrayList<>();
    }
    /**
     * 巡使-姜环: 回到迷仙洞，巡使图标消失，并扣除1点生命值（不可再战）；
     * @param uid
     * @param param
     * @param rd
     */
    @Override
    public void fail(long uid, FightSubmitParam param, RDFightResult rd) {
        RDNightmareMxd rdNightmareMxd = RDNightmareMxd.getFightResultInstance(-fightFailDeductBlood(uid));
        rd.setMxd(rdNightmareMxd);
        UserNightmareMiXianEnemy xianEnemy = nightmareMiXianService.getAndCreateUserNightmareMiXianEnemy(uid);
        Optional<MiXianEnemy> optional = xianEnemy.getMiXianEnemies().stream().filter(p -> p.getEnemyId().equals(param.getOpponentId())).findFirst();
        if (!optional.isPresent()){
            return;
        }
        MiXianEnemy enemy = optional.get();
        xianEnemy.getMiXianEnemies().remove(enemy);
        gameUserService.updateItem(xianEnemy);
        UserNightmareMiXian nightmareMiXian = nightmareMiXianService.getAndCreateUserNightmareMiXian(uid);
        nightmareMiXian.takeCurrentPosToEmptyType();
        gameUserService.updateItem(nightmareMiXian);
    }
    @Override
    public List<Integer> excludeUserCards(List<UserCard> userCards, long uid) {
        return userCards.stream().filter(p->p.ifOwnSkillId(CombatSkillEnum.SIS.getValue())).map(UserCard::getBaseId).collect(Collectors.toList());
    }
}
