package com.bbw.god.gameuser.card.equipment;

import com.bbw.common.PowerRandom;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.game.config.TypeEnum;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.card.CardChecker;
import com.bbw.god.gameuser.card.UserCard;
import com.bbw.god.gameuser.card.UserCardService;
import com.bbw.god.gameuser.card.equipment.Enum.CardEquipmentAdditionEnum;
import com.bbw.god.gameuser.card.equipment.Enum.ProtectEnum;
import com.bbw.god.gameuser.card.equipment.Enum.ResultStatusEnum;
import com.bbw.god.gameuser.card.equipment.Enum.XianJueTypeEnum;
import com.bbw.god.gameuser.card.equipment.cfg.CfgXianJueStarMap;
import com.bbw.god.gameuser.card.equipment.cfg.CfgXianJueStrengthen;
import com.bbw.god.gameuser.card.equipment.data.UserCardXianJue;
import com.bbw.god.gameuser.card.equipment.event.CardEquipmentEventPublisher;
import com.bbw.god.gameuser.card.equipment.rd.RdCardXianJueInfo;
import com.bbw.god.gameuser.card.equipment.rd.RdComprehendInfos;
import com.bbw.god.gameuser.card.equipment.rd.RdXianJueStarMapUpdate;
import com.bbw.god.gameuser.card.equipment.rd.RdXianJueStrength;
import com.bbw.god.gameuser.res.ResChecker;
import com.bbw.god.gameuser.res.ResEventPublisher;
import com.bbw.god.gameuser.treasure.TreasureChecker;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 卡牌仙诀逻辑
 *
 * @author: huanghb
 * @date: 2022/9/15 14:16
 */
@Service
public class UserCardXianJueLogic {
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private UserCardXianJueService userCardXianJueService;
    @Autowired
    private UserCardService userCardService;

    /**
     * 激活仙诀
     *
     * @param uid
     * @param treasureId
     * @param cardId
     * @return
     */
    public RdCardXianJueInfo activeXianJue(long uid, Integer cardId, Integer treasureId, Integer xianJueType) {
        //仙诀类型检测
        XianJueTypeEnum xianJueTypeEnum = XianJueTypeEnum.fromValue(xianJueType);
        if (null == xianJueTypeEnum) {
            throw new ExceptionForClientTip("xianJue.is.error.type");
        }
        //检测法宝是否激活仙诀需要的法宝
        Boolean isActiveTreasure = isCardActiveTreasure(uid, cardId, treasureId);
        if (!isActiveTreasure) {
            throw new ExceptionForClientTip("xianJue.not.treasure");
        }
        //卡牌检测
        UserCard userCard = userCardService.getUserCard(uid, cardId);
        CardChecker.checkIsOwn(userCard);
        //获取仙诀信息
        UserCardXianJue userCardXianJue = userCardXianJueService.getUserCardXianJue(uid, cardId, xianJueType);
        //是否已经激活
        Boolean isActive = null != userCardXianJue;
        if (isActive) {
            throw new ExceptionForClientTip("xianJue.is.active");
        }
        //激活
        userCardXianJue = UserCardXianJue.getInstance(uid, cardId, xianJueType);
        RdCardXianJueInfo rd = RdCardXianJueInfo.instance(userCardXianJue);
        //扣除道具
        TreasureEventPublisher.pubTDeductEvent(uid, treasureId, 1, WayEnum.CARD_XIAN_JUE_ACTIVE, rd);
        //发布激活事件
        CardEquipmentEventPublisher.pubXianJueActiveEvent(uid, userCard.gainCard().getType(), userCard.gainCard().getStar());
        //更新记录
        userCardXianJueService.cacheXianJue(userCardXianJue);
        return rd;

    }

    /**
     * 获得仙诀信息
     *
     * @param uid
     * @param cardId
     * @return
     */
    public RdCardXianJueInfo getXianJueInfo(long uid, Integer cardId, Integer xianJueType) {
        //仙诀类型检测
        XianJueTypeEnum xianJueTypeEnum = XianJueTypeEnum.fromValue(xianJueType);
        if (null == xianJueTypeEnum) {
            throw new ExceptionForClientTip("xianJue.is.error.type");
        }
        //卡牌检测
        UserCard userCard = userCardService.getUserCard(uid, cardId);
        CardChecker.checkIsOwn(userCard);
        //获得仙诀信息
        UserCardXianJue userCardXianJue = userCardXianJueService.getUserCardXianJue(uid, cardId, xianJueType);
        if (null == userCardXianJue) {
            throw new ExceptionForClientTip("xianJue.is.not.active");
        }
        return RdCardXianJueInfo.instance(userCardXianJue);
    }

    /**
     * 研习（强化）
     *
     * @param uid
     * @param
     * @param xianJueDataId 仙诀数据id
     * @return
     */
    public RdXianJueStrength xianJueStrength(long uid, long xianJueDataId) {
        //检查是否激活
        UserCardXianJue userCardXianJue = userCardXianJueService.getUserCardXianJue(uid, xianJueDataId);
        if (null == userCardXianJue) {
            throw ExceptionForClientTip.fromi18nKey("xianJue.is.not.active");
        }
        //检查等级
        int maxLevel = CfgXianJueTool.getMaxLevelLimit();
        if (userCardXianJue.getLevel() >= maxLevel) {
            throw ExceptionForClientTip.fromi18nKey("xianJue.streagth.level.top");
        }
        //检查等级上限
        int levelLimit = CfgXianJueTool.getLevelLimit(userCardXianJue.getQuality());
        if (userCardXianJue.getLevel() >= levelLimit) {
            throw ExceptionForClientTip.fromi18nKey("xianJue.streagth.level.limited");
        }

        RdXianJueStrength rd = new RdXianJueStrength();
        GameUser gu = gameUserService.getGameUser(uid);
        CfgXianJueStrengthen strengthenConf = CfgXianJueTool.getXianJueStrengthen(userCardXianJue.getLevel() + 1);
        // 扣除铜钱
        ResChecker.checkCopper(gu, strengthenConf.getNeedCopper());
        ResEventPublisher.pubCopperDeductEvent(uid, Long.valueOf(strengthenConf.getNeedCopper()), WayEnum.CARD_XIAN_JUE_STUDY, rd);
        // 概率成功
        boolean isStrengthSuccess = strengthenConf.getSuccessRate() >= PowerRandom.getRandomBySeed(10000);
        if (isStrengthSuccess) {
            userCardXianJue.addLevel();
            userCardXianJueService.cacheXianJue(userCardXianJue);
        }
        if (maxLevel == userCardXianJue.getLevel()) {
            CardEquipmentEventPublisher.pubXianJueStudyEvent(uid, userCardXianJue.getXianJueType());
        }
        //是否强化成功 0是成功 1是失败
        rd.setIsSuccess(isStrengthSuccess ? ResultStatusEnum.SUCCESS.getValue() : ResultStatusEnum.FAIL.getValue());
        return rd;
    }

    /**
     * 升级星图
     *
     * @param uid
     * @param xianJueDataId
     * @param protect
     * @return
     */
    public RdXianJueStarMapUpdate updateStarMap(long uid, long xianJueDataId, int protect) {
        //是否保护
        boolean isProtected = protect == ProtectEnum.PROTECT.getValue();
        //检查是否激活
        UserCardXianJue userCardXianJue = userCardXianJueService.getUserCardXianJue(uid, xianJueDataId);
        if (null == userCardXianJue) {
            throw ExceptionForClientTip.fromi18nKey("xianJue.is.not.active");
        }
        //检查品质
        int maxQuality = CfgXianJueTool.getMaxQuality();
        if (userCardXianJue.getQuality() >= maxQuality) {
            throw ExceptionForClientTip.fromi18nKey("xianJue.quality.top");
        }
        int nextStar = userCardXianJue.getStarMapProgress() + 1;
        CfgXianJueStarMap xianJueStarMap = CfgXianJueTool.getXianJueStarMap(userCardXianJue.getQuality(), nextStar);
        //添加碎星保护符
        if (isProtected) {
            xianJueStarMap.getNeeds().add(new Award(TreasureEnum.XING_TU_BAO_HF.getValue(), AwardEnum.FB, 1));
        }
        GameUser gameUser = gameUserService.getGameUser(uid);
        //检查所需道具
        for (Award need : xianJueStarMap.getNeeds()) {
            if (0 != need.getAwardId()) {
                TreasureChecker.checkIsEnough(need.getAwardId(), need.getNum(), uid);
            }
            ResChecker.checkCopper(gameUser, need.getNum());
        }
        RdXianJueStarMapUpdate rd = new RdXianJueStarMapUpdate();
        //扣除道具
        for (Award need : xianJueStarMap.getNeeds()) {
            if (0 != need.getAwardId()) {
                TreasureEventPublisher.pubTDeductEvent(uid, need.getAwardId(), need.getNum(), WayEnum.LEADER_EQUIPMENT_STAR_MAP_UPDATE, rd);
            continue;
            }
            ResEventPublisher.pubCopperDeductEvent(uid, Long.valueOf(need.getNum()), WayEnum.LEADER_EQUIPMENT_STAR_MAP_UPDATE, rd);
        }
        // 概率成功
        boolean isStrengthSuccess = isStrengthSuccess(xianJueStarMap);
        int result = isStrengthSuccess ? ResultStatusEnum.SUCCESS.getValue() : ResultStatusEnum.FAIL.getValue();
        if (isStrengthSuccess) {
            userCardXianJue.addStarProgress();
            //更新仙诀
            userCardXianJueService.cacheXianJue(userCardXianJue);
        } else {
            boolean isToDeduct = isToDeduct();
            if (isToDeduct && !isProtected) {
                userCardXianJue.deductStarProgress();
                userCardXianJueService.cacheXianJue(userCardXianJue);
                result = ResultStatusEnum.BIG_FAILURE.getValue();
            }
        }
        if (CfgXianJueTool.getMaxQuality() == userCardXianJue.getQuality()) {
            CardEquipmentEventPublisher.pubXianJueUpdataStarEvent(uid, userCardXianJue.getXianJueType(), userCardXianJue.getQuality());
        }
        rd.setResult(result);
        return rd;
    }

    /**
     * 是否强化成功
     *
     * @param equipmentStarMap
     * @return
     */
    private boolean isStrengthSuccess(CfgXianJueStarMap equipmentStarMap) {
        return equipmentStarMap.getSuccessRate() >= PowerRandom.getRandomBySeed(100);
    }

    /**
     * 是否扣除
     *
     * @return
     */
    private boolean isToDeduct() {
        return 10 >= PowerRandom.getRandomBySeed(100);
    }

    /**
     * 参悟
     *
     * @param uid
     * @param xianJueDataId  仙诀数据id
     * @param comprehendType 参悟类别 30 为强度 40为韧度
     * @return
     */
    public RdComprehendInfos comprehend(long uid, long xianJueDataId, Integer comprehendType) {
        //参悟类型检测
        if (CardEquipmentAdditionEnum.STRENGTH_RATE.getValue() != comprehendType && CardEquipmentAdditionEnum.TENACITY_RATE.getValue() != comprehendType) {
            throw ExceptionForClientTip.fromi18nKey("xianJue.is.error.comprehend.type");

        }
        //检查是否激活
        UserCardXianJue userCardXianJue = userCardXianJueService.getUserCardXianJue(uid, xianJueDataId);
        if (null == userCardXianJue) {
            throw ExceptionForClientTip.fromi18nKey("xianJue.is.not.active");
        }
        //总参悟进度上限检测
        int totalComprehendLimit = CfgXianJueTool.getComprehendLimitInfo();
        if (userCardXianJue.gainCurrenAddition(comprehendType) >= totalComprehendLimit) {
            throw ExceptionForClientTip.fromi18nKey("xianJue.comprehend.progress.top");
        }
        //当前参悟上限检测
        int currentComprehendLimit = CfgXianJueTool.getComprehendLimitInfo(userCardXianJue.getQuality());
        if (userCardXianJue.gainCurrenAddition(comprehendType) >= currentComprehendLimit) {
            throw ExceptionForClientTip.fromi18nKey("xianJue.comprehend.progress.limited");
        }
        //道具检测
        Integer comprehendNeedTreasureId = CfgXianJueTool.getComprehendNeedTreasureId(comprehendType);
        TreasureChecker.checkIsEnough(comprehendNeedTreasureId, 1, uid);
        //参悟
        userCardXianJue.addComprehendValue(comprehendType);

        //扣除道具
        RdComprehendInfos rd = RdComprehendInfos.instance(userCardXianJue.gainAdditions());
        TreasureEventPublisher.pubTDeductEvent(uid, comprehendNeedTreasureId, 1, WayEnum.CARD_XIAN_JUE_COMPREHEND, rd);

        //发布满参悟事件
        if (totalComprehendLimit == userCardXianJue.gainCurrenAddition(comprehendType)) {
            CardEquipmentEventPublisher.pubXianJueComprehendEvent(uid, comprehendType);
        }
        userCardXianJueService.cacheXianJue(userCardXianJue);
        return rd;
    }

    /**
     * 是否该卡牌的激活法宝
     *
     * @param cardId     active
     * @param treasureId 激活法宝的id范围50331 -50355 属性种类 5 属性的值为10，20，30，40，50
     * @return
     */
    private Boolean isCardActiveTreasure(long uid, Integer cardId, Integer treasureId) {
        UserCard userCard = userCardService.getUserCard(uid, cardId);
        //参数检测
        if (treasureId < TreasureEnum.ONE_STAR_JIN_XIAN_JUE.getValue()) {
            return false;
        }
        if (treasureId > TreasureEnum.FIVE_STAR_TU_XIAN_JUE.getValue()) {
            return false;
        }
        //获得激活法宝的星级
        Integer propertySize = TypeEnum.values().length - 1;
        Integer activeTreasureStar = (treasureId - TreasureEnum.ONE_STAR_JIN_XIAN_JUE.getValue()) / propertySize + 1;
        //如果激活法宝星级与卡牌星级不匹配
        if (!userCard.gainCard().getStar().equals(activeTreasureStar)) {
            return false;
        }
        //获得激活法宝的属性
        Integer activeTreasureProperty = (treasureId - TreasureEnum.ONE_STAR_JIN_XIAN_JUE.getValue()) % propertySize + 1;
        //如果激活法宝属性与卡牌属性类型不匹配   属性的值为10，20，30，40，50 所有activeTreasureProperty需要*10
        if (!userCard.gainCard().getType().equals(activeTreasureProperty * 10)) {
            return false;
        }
        return true;
    }
}
