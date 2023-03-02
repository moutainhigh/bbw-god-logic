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
import java.util.Optional;

/**
 * 说明：巡使头领
 *
 * @author lwb
 * date 2021-05-28
 */
@Service
public class XunShiLeaderProcessor extends AbstractMiXianFightProcessor {
    //妖狐尾巴、孔雀羽毛、猿猴獠牙、牛魔尖角
    private static final List<Integer> treasures = Arrays.asList(11620, 11630, 11640, 11650);
    private static final int[] priorityCards = {357, 458, 256, 552, 553};

    @Override
    public boolean match(NightmareMiXianPosEnum miXianPosEnum) {
        return NightmareMiXianPosEnum.XUN_SHI_LEADER.equals(miXianPosEnum);
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
     * 头领的卡牌将从全卡牌中随机5星卡*5、4星卡*5、1~5星卡*10作为卡组。卡牌默认为5阶，等级恒定14级。
     * 优先随机（一星-冤魂、二星-食尸鬼、三星-姜环、四星-邱引、五星-烛龙）。
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
            int cardId = priorityCards[i];
            Optional<CfgCardEntity> optional = list.stream().filter(p -> p.getId() == cardId).findFirst();
            if (!optional.isPresent() && PowerRandom.hitProbability(50)) {
                if (list.size() > 0) {
                    list.remove(0);
                }
                list.add(CardTool.getCardById(cardId));
            }
            cardEntities.addAll(list);
        }
        List<CCardParam> cardParams = new ArrayList<>();
        int lv = 14;
        int hv = 5;
        for (CfgCardEntity cardEntity : cardEntities) {
            cardParams.add(CCardParam.init(cardEntity.getId(), lv, hv));
        }
        //需要生成对手
        GameUser gu = gameUserService.getGameUser(uid);
        MiXianEnemy enemy = MiXianEnemy.getInstance(posData, currentMxdLevel);
        enemy.setNickname("巡使头领");
        enemy.setHead(cardParams.get(cardParams.size() - 1).getId());
        enemy.setCardParams(cardParams);
        enemy.setLevel(gu.getLevel());
        return enemy;
    }

    @Override
    public List<Award> beatAwards(UserNightmareMiXian nightmareMiXian, RDFightResult rd) {
        List<Award> awards = new ArrayList<>();
        for (Integer id : treasures) {
            if (PowerRandom.hitProbability(15)) {
                awards.add(Award.instance(id, AwardEnum.FB, 1));
            }
        }
        if (!nightmareMiXian.isInTreasureHouse() && nightmareMiXian.awardNumInBag(11660)==0){
            int num = nightmareMiXian.getLevelData().settleXunShiNum();
            //关卡钥匙*1	1/剩余巡使数
            if (PowerRandom.getRandomBySeed(num) == 1) {
                awards.add(Award.instance(11660, AwardEnum.FB, 1));
            }
        }
        return awards;
    }
}