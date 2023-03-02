package com.bbw.god.city.mixd.nightmare.pos;

import com.bbw.common.PowerRandom;
import com.bbw.god.city.mixd.nightmare.MiXianEnemy;
import com.bbw.god.city.mixd.nightmare.MiXianLevelData;
import com.bbw.god.city.mixd.nightmare.NightmareMiXianPosEnum;
import com.bbw.god.city.mixd.nightmare.UserNightmareMiXian;
import com.bbw.god.fight.RDFightResult;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.game.combat.data.param.CCardParam;
import com.bbw.god.game.config.card.CardTool;
import com.bbw.god.game.config.card.CfgCardEntity;
import com.bbw.god.gameuser.GameUser;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 说明：巡使小队
 *
 * @author lwb
 * date 2021-05-28
 */
@Service
public class XunShiXDProcessor extends AbstractMiXianFightProcessor {
    //妖狐尾巴、孔雀羽毛、猿猴獠牙、牛魔尖角
    private static final List<Integer> treasures = Arrays.asList(11620, 11630, 11640, 11650);

    @Override
    public boolean match(NightmareMiXianPosEnum miXianPosEnum) {
        return NightmareMiXianPosEnum.XUN_SHI_XD.equals(miXianPosEnum);
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
     * 小队的卡牌将从全卡牌中随机
     * 5星卡*5、4星卡*5、1~5星卡*10作为卡组。卡牌默认为0阶，等级根据当前所在层而定。
     *
     * @param uid
     * @param currentMxdLevel
     * @return
     */
    @Override
    public MiXianEnemy buildAiCards(long uid, int currentMxdLevel, MiXianLevelData.PosData posData) {
        int[] cardRules = {0, 0, 0, 5, 5};
        for (int i = 0; i < 10; i++) {
            int randomStar = PowerRandom.getRandomBySeed(5);
            cardRules[randomStar - 1]++;
        }
        List<CfgCardEntity> cardEntities = new ArrayList<>();
        for (int i = 0; i < cardRules.length; i++) {
            List<CfgCardEntity> list = CardTool.getRandomCard(i + 1, cardRules[i]);
            cardEntities.addAll(list);
        }
        List<CCardParam> cardParams = new ArrayList<>();
        int lv = getAiCardLevel(uid, currentMxdLevel);
        for (CfgCardEntity cardEntity : cardEntities) {
            cardParams.add(CCardParam.init(cardEntity.getId(), lv, 0));
        }
        //需要生成对手
        GameUser gu = gameUserService.getGameUser(uid);
        MiXianEnemy enemy = MiXianEnemy.getInstance(posData, currentMxdLevel);
        enemy.setNickname("巡使小队");
        enemy.setCardParams(cardParams);
        enemy.setHead(cardParams.get(cardParams.size() - 1).getId());
        enemy.setLevel(gu.getLevel());
        return enemy;
    }

    @Override
    public List<Award> beatAwards(UserNightmareMiXian nightmareMiXian, RDFightResult rd) {
        List<Award> awards = new ArrayList<>();
        for (Integer id : treasures) {
            if (PowerRandom.hitProbability(5)) {
                awards.add(Award.instance(id, AwardEnum.FB, 1));
            }
        }
        if (!nightmareMiXian.isInTreasureHouse() && nightmareMiXian.awardNumInBag(11660)==0){
            int num = nightmareMiXian.getLevelData().settleXunShiNum();
            //关卡钥匙*1	1/剩余巡使数
            if (PowerRandom.hitProbability(1, num)) {
                awards.add(Award.instance(11660, AwardEnum.FB, 1));
            }
        }
        return awards;
    }

    /**
     * 卡牌等级
     *
     * @param uid
     * @param currentMxdLevel
     * @return
     */
    public int getAiCardLevel(long uid, int currentMxdLevel) {
        if (currentMxdLevel <= 3) {
            return 5;
        }
        if (currentMxdLevel <= 6) {
            return 6;
        }
        if (currentMxdLevel <= 9) {
            return 7;
        }
        if (currentMxdLevel <= 13) {
            return 8;
        }
        if (currentMxdLevel <= 16) {
            return 9;
        }
        if (currentMxdLevel <= 19) {
            return 10;
        }
        if (currentMxdLevel <= 23) {
            return 11;
        }
        if (currentMxdLevel <= 26) {
            return 12;
        }
        return 13;
    }

}