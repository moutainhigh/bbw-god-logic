package com.bbw.god.city.mixd.nightmare.pos;

import com.bbw.common.PowerRandom;
import com.bbw.god.city.mixd.nightmare.*;
import com.bbw.god.fight.FightSubmitParam;
import com.bbw.god.fight.RDFightResult;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.game.combat.data.CombatSkillEnum;
import com.bbw.god.game.combat.data.param.CCardParam;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.gameuser.card.UserCard;
import com.bbw.god.gameuser.card.event.CardEventPublisher;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 说明：巡使-烛龙
 *
 * @author lwb
 * date 2021-05-28
 */
@Service
public class ZhuLongProcessor extends AbstractMiXianFightProcessor {
    //妖狐尾巴、孔雀羽毛、猿猴獠牙、牛魔尖角
    private static final List<Integer> treasures= Arrays.asList(11620,11630,11640,11650);
    @Override
    public boolean match(NightmareMiXianPosEnum miXianPosEnum) {
        return NightmareMiXianPosEnum.XUN_SHI_ZHU_LONG.equals(miXianPosEnum);
    }

    @Override
    public boolean isXunShi() {
        return true;
    }

    @Override
    public int fightFailDeductBlood(long uid) {
        return 0;
    }

    @Override
    public void fightBefore(long uid, NightmareMiXianPosEnum type) {
        UserNightmareMiXian nightmareMiXian = nightmareMiXianService.getAndCreateUserNightmareMiXian(uid);
        nightmareMiXian.incBlood(fightFailDeductBlood(uid));
        nightmareMiXian.setFightingType(type.getType());
        nightmareMiXian.setToFail(true);
        nightmareMiXian.setKillXunShiNum(0);
        gameUserService.updateItem(nightmareMiXian);
    }

    /**
     *
     */
    @Override
    public MiXianEnemy buildAiCards(long uid, int currentMxdLevel, MiXianLevelData.PosData posData) {
        CfgNightmareMiXian cfg = NightmareMiXianTool.getCfg();
        List<CfgNightmareMiXian.CardParam> cards = cfg.getZhuLongCards();
        List<CCardParam> cardParams=new ArrayList<>();
        for (CfgNightmareMiXian.CardParam card : cards) {
            cardParams.add(CCardParam.initMxdCards(card,20,10));
        }
        long aiId = NightmareMiXianTool.buildMxdAiId(currentMxdLevel, posData.getPos(), NightmareMiXianPosEnum.XUN_SHI_ZHU_LONG.getType());
        MiXianEnemy enemy= MiXianEnemy.getInstance(aiId,posData);
        enemy.setNickname("巡使-烛龙");
        enemy.setCardParams(cardParams);
        enemy.setHead(553);
        enemy.setLevel(135);
        enemy.setBuff(131070);
        return enemy;
    }

    /**
     * 则获得卡牌-烛龙，并重新累计次数；
     */
    @Override
    public List<Award> beatAwards(UserNightmareMiXian nightmareMiXian, RDFightResult rd) {
        long uid=nightmareMiXian.getGameUserId();
        CardEventPublisher.pubCardAddEvent(uid,553, WayEnum.MXD_FIGHT,WayEnum.MXD_FIGHT.getName(),rd);
        List<Award> awards=new ArrayList<>();
        if (!nightmareMiXian.isInTreasureHouse() && nightmareMiXian.awardNumInBag(11660)==0){
            int num = nightmareMiXian.getLevelData().settleXunShiNum();
            //关卡钥匙*1	1/剩余巡使数
            if (num==0 || PowerRandom.getRandomBySeed(num)==1) {
                awards.add(Award.instance(11660, AwardEnum.FB,1));
            }
        }
        return awards;
    }

    /**
     * 玩家战斗失败，则清零玩家该层迷仙洞生命值，并重新累计次数。且下一次进入时根据当前所在层退回指定层。
     * 当前层	指定层
     * 1~10	     1
     * 11~20	11
     * 21~30	21
     * @param uid
     * @param param
     * @param rd
     */
    @Override
    public void fail(long uid, FightSubmitParam param, RDFightResult rd) {
        RDNightmareMxd rdNightmareMxd = RDNightmareMxd.getFightResultInstance(fightFailDeductBlood(uid));
        rd.setMxd(rdNightmareMxd);
        UserNightmareMiXianEnemy xianEnemy = nightmareMiXianService.getAndCreateUserNightmareMiXianEnemy(uid);
        xianEnemy.setMiXianEnemies(new ArrayList<>());
        gameUserService.updateItem(xianEnemy);
        UserNightmareMiXian nightmareMiXian = nightmareMiXianService.getAndCreateUserNightmareMiXian(uid);
        rdNightmareMxd.setAddedBlood(-nightmareMiXian.getBlood());
        gameUserService.updateItem(nightmareMiXian);
    }

    @Override
    public void handleFightAward(long uid, long aiId, RDFightResult rd) {
        super.handleFightAward(uid, aiId, rd);
        UserNightmareMiXian nightmareMiXian = nightmareMiXianService.getAndCreateUserNightmareMiXian(uid);
        nightmareMiXian.setToFail(false);
        gameUserService.updateItem(nightmareMiXian);
    }

    @Override
    public List<Integer> excludeUserCards(List<UserCard> userCards, long uid) {
        return userCards.stream().filter(p->p.ifOwnSkillId(CombatSkillEnum.SIS.getValue())).map(UserCard::getBaseId).collect(Collectors.toList());
    }
}
