package com.bbw.god.gameuser.nightmarenvwam;

import com.bbw.common.PowerRandom;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.gameuser.nightmarenvwam.cfg.CfgNightmareNvmEntity;
import com.bbw.god.gameuser.nightmarenvwam.cfg.CfgNightmareNvmOutputEntity;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.config.Cfg;
import com.bbw.god.gameuser.nightmarenvwam.godsaltar.GodsAltarEnum;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 梦魇女娲庙工具类
 *
 * @author fzj
 * @date 2022/5/4 14:16
 */
public class NightmareNvWamCfgTool {

    /**
     * 获得配置信息
     *
     * @return
     */
    public static CfgNightmareNvmEntity getNightmareNvm() {
        return Cfg.I.getUniqueConfig(CfgNightmareNvmEntity.class);
    }

    /**
     * 获得翻倍临界值
     *
     * @return
     */
    public static Integer getDoubleCritical() {
        return getNightmareNvm().getClayFigurineValueDoubled();
    }

    /**
     * 获得泥人进度值单位
     *
     * @return
     */
    public static Integer getClayFigurineValueUnit() {
        return getNightmareNvm().getClayFigurineValueUnit();
    }

    /**
     * 根据捏人标识获取分数
     *
     * @param sign
     * @return
     */
    public static Integer getPinchPeopleScoreBySign(int sign) {
        return getNightmareNvm().getPinchPeopleScore().stream().filter(s -> s.getSign() == sign)
                .map(CfgNightmareNvmEntity.PinchPeopleScore::getScore).findFirst().orElse(0);
    }

    /**
     * 获取标识数量
     *
     * @return
     */
    public static Integer getSignNum() {
        return getNightmareNvm().getPinchPeopleScore().size();
    }

    /**
     * 捏人需要的进度值
     *
     * @return
     */
    public static Integer getPinchPeopleNeedValue() {
        return getNightmareNvm().getPinchPeopleNeedValue();
    }

    /**
     * 获取分数有效次数
     *
     * @return
     */
    public static Integer getDayPinchPeopleValidTimes() {
        return getNightmareNvm().getDayPinchPeopleValidTimes();
    }

    /**
     * 获得挑战奖励
     *
     * @param score
     * @return
     */
    public static List<Award> getChallengeAwards(double score) {
        return getNightmareNvm().getDayChallengeAwards().stream()
                .filter(s -> score >= s.getMinScore() && score <= s.getMaxScore())
                .map(CfgNightmareNvmEntity.DayChallengeAwards::getAwards).findFirst().orElse(null);
    }

    /**
     * 获取捏人累计奖励需要的分数
     *
     * @return
     */
    public static Integer getPinchPeopleAwardNeedValue() {
        return getNightmareNvm().getPinchPeopleAwardNeedValue();
    }

    /**
     * 根据概率获取奖励类型
     *
     * @return
     */
    public static Integer getTotalScoreAwardType() {
        List<CfgNightmareNvmEntity.TotalScoreAwards> totalScoreAwards = getNightmareNvm().getTotalScoreAwards();
        int index = PowerRandom.hitProbabilityIndex(totalScoreAwards.stream().map(CfgNightmareNvmEntity.TotalScoreAwards::getProgress).collect(Collectors.toList()));
        return totalScoreAwards.get(index).getAwardType();
    }

    /**
     * 获取配置
     *
     * @return
     */
    public static List<CfgNightmareNvmOutputEntity> getNightmareNvmOutput() {
        return Cfg.I.get(CfgNightmareNvmOutputEntity.class);
    }

    /**
     * 根据类型随机一个道具Id
     *
     * @param type
     * @return
     */
    public static Integer getTreasureIdByType(int type) {
        List<Integer> treasureIds = getNightmareNvmOutput().stream().filter(n -> n.getType() == type)
                .map(CfgNightmareNvmOutputEntity::getTreasureId).collect(Collectors.toList());
        return PowerRandom.getRandomFromList(treasureIds);
    }

    /**
     * 根据概率获取奖励类型
     *
     * @return
     */
    public static Integer getDayGiftAwardType() {
        List<CfgNightmareNvmEntity.DayGiftAwards> totalScoreAwards = getNightmareNvm().getDayGiftAwards();
        int index = PowerRandom.hitProbabilityIndex(totalScoreAwards.stream().map(CfgNightmareNvmEntity.DayGiftAwards::getProgress).collect(Collectors.toList()));
        return totalScoreAwards.get(index).getAwardType();
    }

    /**
     * 获得卡牌相关道具
     *
     * @param cardId
     * @return
     */
    public static List<Integer> getTreasureByCardId(int cardId) {
        return getNightmareNvmOutput().stream().filter(n -> n.getCardId() == cardId)
                .map(CfgNightmareNvmOutputEntity::getTreasureId).collect(Collectors.toList());
    }

    /**
     * 获得卡牌id
     *
     * @param treasureId
     * @return
     */
    public static Integer getCardIdByTreasure(int treasureId) {
        return getNightmareNvmOutput().stream().filter(n -> n.getTreasureId() == treasureId)
                .map(CfgNightmareNvmOutputEntity::getCardId).findFirst().orElse(null);
    }

    /**
     * 检查道具是否属于神格牌
     *
     * @param treasureId
     */
    public static void checkGodHeadCard(int treasureId) {
        List<Integer> treasureIds = getNightmareNvmOutput().stream().filter(n -> n.getType() == GodsAltarEnum.GOD_HEAD_CARD.getValue())
                .map(CfgNightmareNvmOutputEntity::getTreasureId).collect(Collectors.toList());
        if (!treasureIds.contains(treasureId)) {
            throw new ExceptionForClientTip("exchange.not.define.unit");
        }
    }

    /**
     * 获得神格牌
     *
     * @param cardId
     * @return
     */
    public static Integer getGodHeadCard(int cardId) {
        return getNightmareNvm().getGodHeadCard().stream().filter(g -> g.getCardId() == cardId).map(CfgNightmareNvmEntity.GodHeadCard::getGodHeadCardId).findFirst().orElse(null);
    }

    /**
     * 获得卡牌id
     *
     * @return
     */
    public static List<Integer> getGodHeadCardId() {
        return getNightmareNvm().getGodHeadCard().stream().map(CfgNightmareNvmEntity.GodHeadCard::getCardId).collect(Collectors.toList());
    }

    /**
     * 获得租赁价格
     *
     * @return
     */
    public static Integer getRentalPrice() {
        return getNightmareNvm().getRentalPrice();
    }

    /**
     * 获得租赁时间
     *
     * @return
     */
    public static Integer getLeaseTimeLimit() {
        return getNightmareNvm().getLeaseTimeLimit();
    }

    /**
     * 获得出价数量上限
     *
     * @return
     */
    public static Integer getMaxPriceWay() {
        return getNightmareNvm().getMaxPriceWay();
    }

    /**
     * 获得商品数量上限
     *
     * @return
     */
    public static Integer getMaxShelvesNum() {
        return getNightmareNvm().getMaxShelvesNum();
    }

    /**
     * 最大讨价数量
     *
     * @return
     */
    public static Integer getMaxBargainNum() {
        return getNightmareNvm().getMaxBargainNum();
    }

}
