package com.bbw.god.city.mixd.nightmare.pos;

import com.bbw.common.PowerRandom;
import com.bbw.god.city.mixd.nightmare.*;
import com.bbw.god.fight.FightSubmitParam;
import com.bbw.god.fight.RDFightResult;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.combat.CombatInitService;
import com.bbw.god.game.combat.data.param.CCardParam;
import com.bbw.god.game.config.card.CardTool;
import com.bbw.god.game.config.card.CfgCardEntity;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.card.UserCard;
import com.bbw.god.gameuser.card.UserCardService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 说明：
 *
 * @author lwb
 * date 2021-05-27
 */
public abstract class AbstractMiXianFightProcessor extends AbstractMiXianPosProcessor {
    @Autowired
    protected UserCardService userCardService;
    @Autowired
    protected GameUserService gameUserService;
    @Autowired
    private ZhuLongProcessor zhuLongProcessor;
    @Autowired
    private JiangHuanProcessor jiangHuanProcessor;

    /**
     * 是否是巡使
     *
     * @return
     */
    public abstract boolean isXunShi();

    /**
     * 战斗发起之前
     *
     * @param uid
     */
    public void fightBefore(long uid, NightmareMiXianPosEnum type) {
        UserNightmareMiXian nightmareMiXian = nightmareMiXianService.getAndCreateUserNightmareMiXian(uid);
        nightmareMiXian.incBlood(-fightFailDeductBlood(uid));
        nightmareMiXian.setFightingType(type.getType());
        gameUserService.updateItem(nightmareMiXian);
    }

    ;

    /**
     * 战斗失败需要扣除的血量
     *
     * @return
     */
    public abstract int fightFailDeductBlood(long uid);

    /**
     * 生成AI卡牌
     *
     * @param uid
     * @return
     */
    public abstract MiXianEnemy buildAiCards(long uid, int currentMxdLevel, MiXianLevelData.PosData posData);

    /**
     * 击败奖励
     *
     * @param nightmareMiXian
     * @return
     */
    public abstract List<Award> beatAwards(UserNightmareMiXian nightmareMiXian, RDFightResult rd);

    /**
     * 到达位置 生成对手
     *
     * @param nightmareMiXian
     * @param rd
     * @param posData
     */
    @Override
    public void touchPos(UserNightmareMiXian nightmareMiXian, RDNightmareMxd rd, MiXianLevelData.PosData posData) {
        long uid = nightmareMiXian.getGameUserId();
        UserNightmareMiXianEnemy mxdEnemy = nightmareMiXianService.getAndCreateUserNightmareMiXianEnemy(uid);
        Optional<MiXianEnemy> optional = mxdEnemy.getEnemy(posData.getPos(), nightmareMiXian.getCurrentLevel(), posData.getTye());
        MiXianEnemy enemy = null;
        if (!optional.isPresent()) {
            if (isXunShi()) {
                if ((buildJiangHuan(nightmareMiXian.getCurrentLevel()) && nightmareMiXian.awardNumInBag(11660) > 0)) {
                    enemy = jiangHuanProcessor.buildAiCards(uid, nightmareMiXian.getCurrentLevel(), posData);
                    posData.setTye(NightmareMiXianPosEnum.XUN_SHI_JIANG_HUAN.getType());
                    rd.setCurrentPosType(NightmareMiXianPosEnum.XUN_SHI_JIANG_HUAN.getType());
                } else if (nightmareMiXian.hitZhuLongXunShi()) {
                    //烛龙
                    enemy = zhuLongProcessor.buildAiCards(uid, nightmareMiXian.getCurrentLevel(), posData);
                    nightmareMiXian.setKillXunShiNum(0);
                    posData.setTye(NightmareMiXianPosEnum.XUN_SHI_ZHU_LONG.getType());
                    rd.setCurrentPosType(NightmareMiXianPosEnum.XUN_SHI_ZHU_LONG.getType());

                }
            }
            if (enemy == null) {
                enemy = buildAiCards(uid, nightmareMiXian.getCurrentLevel(), posData);
            }
            mxdEnemy.getMiXianEnemies().add(enemy);
            gameUserService.updateItem(mxdEnemy);
        } else {
            enemy = optional.get();
        }
        rd.setOpponentId(enemy.getEnemyId());
    }

    public List<Integer> excludeUserCards(List<UserCard> userCards, long uid) {
        return new ArrayList<>();
    }

    /**
     * 玩家在于巡使战斗时将从全卡牌中随机5星卡*5、4星卡*5、1~5星卡*10作为与巡使战斗时使用的卡组。
     * 随机的卡牌中，玩家已拥有的卡牌将使用自身的数据，未拥有的卡牌则为0级0阶
     */
    public List<CCardParam> buildUserFightCardGroup(long uid) {
        List<UserCard> userCards = userCardService.getUserCards(uid);
        List<Integer> excludeUserCards = excludeUserCards(userCards, uid);
        int[] cardRules = {0, 0, 0, 5, 5};
        for (int i = 0; i < 10; i++) {
            int randomStar = PowerRandom.getRandomBySeed(5);
            cardRules[randomStar - 1]++;
        }
        List<CfgCardEntity> cardEntities = new ArrayList<>();
        for (int i = 0; i < cardRules.length; i++) {
            List<CfgCardEntity> list = CardTool.getRandomCard(i + 1, cardRules[i], excludeUserCards);
            cardEntities.addAll(list);
        }
        List<CCardParam> cardParams = new ArrayList<>();
        for (CfgCardEntity cardEntity : cardEntities) {
            Optional<UserCard> optional = userCards.stream().filter(p -> p.getBaseId().equals(cardEntity.getId()) || p.getBaseId() == CardTool.getDeifyCardId(cardEntity.getId())).findFirst();
            if (optional.isPresent()) {
                cardParams.add(CCardParam.init(optional.get()));
            } else {
                cardParams.add(CCardParam.init(cardEntity.getId(), 0, 0));
            }
        }
        return cardParams;
    }

    /**
     * 未能击败的巡使将会记录当前的剩余血量及非坟场卡牌，玩家下一次与之战斗时继承数据。
     * （
     * （2）巡使-烛龙:玩家战斗失败，则清零玩家该层迷仙洞生命值，并重新累计次数。且下一次进入时根据当前所在层退回指定层。
     */
    public void fail(long uid, FightSubmitParam param, RDFightResult rd) {
        rd.setMxd(RDNightmareMxd.getFightResultInstance(-fightFailDeductBlood(uid)));
        UserNightmareMiXianEnemy xianEnemy = nightmareMiXianService.getAndCreateUserNightmareMiXianEnemy(uid);
        Optional<MiXianEnemy> optional = xianEnemy.getMiXianEnemies().stream().filter(p -> p.getEnemyId().equals(param.getOpponentId())).findFirst();
        if (!optional.isPresent()) {
            return;
        }
        MiXianEnemy enemy = optional.get();
        int blood = CombatInitService.getPlayerInitHp(enemy.getLevel()) - param.getOppLostBlood();
        if (blood > 0) {
            enemy.setBlood(blood);
        }
        List<Integer> collect = param.getOppKilledCards().stream().map(FightSubmitParam.SubmitCardParam::getId).collect(Collectors.toList());
        enemy.setCardParams(enemy.getCardParams().stream().filter(p -> !collect.contains(p.getId())).collect(Collectors.toList()));
        gameUserService.updateItem(xianEnemy);
    }

    /**
     * 击败AI
     *
     * @param uid
     * @param aiId
     * @param rd
     */
    public void handleFightAward(long uid, long aiId, RDFightResult rd) {
        UserNightmareMiXianEnemy xianEnemy = nightmareMiXianService.getAndCreateUserNightmareMiXianEnemy(uid);
        Optional<MiXianEnemy> optional = xianEnemy.getMiXianEnemies().stream().filter(p -> p.getEnemyId() == aiId).findFirst();
        if (!optional.isPresent()) {
            return;
        }
        xianEnemy.getMiXianEnemies().remove(optional.get());
        gameUserService.updateItem(xianEnemy);
        UserNightmareMiXian nightmareMiXian = nightmareMiXianService.getAndCreateUserNightmareMiXian(uid);
        RDNightmareMxd rdNightmareMxd = new RDNightmareMxd();
        rd.setMxd(rdNightmareMxd);
        List<Award> awards = beatAwards(nightmareMiXian, rd);
        nightmareMiXian.addAwardToBag(awards);
        rdNightmareMxd.setGainAwards(awards);
        if (isXunShi()) {
            //巡使格
            nightmareMiXian.takeCurrentPosToEmptyType();
            nightmareMiXian.setKillXunShiNum(nightmareMiXian.getKillXunShiNum() + 1);
            if (nightmareMiXian.ifKillAllXunShi() && !nightmareMiXian.isInTreasureHouse()) {
                for (MiXianLevelData.PosData posData : nightmareMiXian.getLevelData().getPosDatas()) {
                    posData.setShow(true);
                }
                rdNightmareMxd.setShowAll(1);
            }
        }
        //把预先扣除的血加回来
        nightmareMiXian.incBlood(fightFailDeductBlood(uid));
        gameUserService.updateItem(nightmareMiXian);
    }

    private boolean buildJiangHuan(int currentLevel) {
        int seed = 1;
        if (currentLevel <= 19) {
            seed = 3;
        } else if (currentLevel <= 29) {
            seed = 7;
        }
        return PowerRandom.hitProbability(seed);
    }


}