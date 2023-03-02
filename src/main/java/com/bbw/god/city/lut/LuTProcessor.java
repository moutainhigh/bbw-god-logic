package com.bbw.god.city.lut;

import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.cache.TimeLimitCacheUtil;
import com.bbw.god.city.*;
import com.bbw.god.city.event.CityEventPublisher;
import com.bbw.god.city.exp.CityExpService;
import com.bbw.god.event.BaseEventParam;
import com.bbw.god.game.config.TypeEnum;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.card.CardExpTool;
import com.bbw.god.game.config.card.CardHvTool;
import com.bbw.god.game.config.card.CfgCardEntity;
import com.bbw.god.game.config.city.CfgCityEntity;
import com.bbw.god.game.config.city.CityConfig;
import com.bbw.god.game.config.city.CityConfig.OCData;
import com.bbw.god.game.config.city.CityTypeEnum;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.card.CardChecker;
import com.bbw.god.gameuser.card.UserCard;
import com.bbw.god.gameuser.card.UserCardService;
import com.bbw.god.gameuser.card.event.CardEventPublisher;
import com.bbw.god.gameuser.leadercard.fashion.UserLeaderFashionService;
import com.bbw.god.gameuser.res.ResChecker;
import com.bbw.god.gameuser.res.ResEventPublisher;
import com.bbw.god.gameuser.res.ele.EVEle;
import com.bbw.god.gameuser.treasure.UserTreasureService;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.rd.RDAdvance;
import com.bbw.god.rd.RDCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * 鹿台 - 升级卡牌
 *
 * @author suhq
 * @date 2018年10月24日 下午5:49:46
 */
@Component
public class LuTProcessor implements ICityArriveProcessor, ICityHandleProcessor, ICityExpProcessor {
    private List<CityTypeEnum> cityTypes = Arrays.asList(CityTypeEnum.LT);
    @Autowired
    private UserTreasureService userTreasureService;
    @Autowired
    private UserCardService userCardService;
    @Autowired
    private UserCityService userCityService;
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private CityExpService cityExpService;
    @Autowired
    private UserLeaderFashionService userLeaderFashionService;

    @Override
    public List<CityTypeEnum> getCityTypes() {
        return cityTypes;
    }

    @Override
    public Class<RDArriveLuT> getRDArriveClass() {
        return RDArriveLuT.class;
    }

    @Override
    public RDArriveLuT arriveProcessor(GameUser gameUser, CfgCityEntity city, RDAdvance rd) {
        OCData ocData = CityConfig.bean().getOcDATA();
        RDArriveLuT rdArriveLuT = RDArriveLuT.getInstance(ocData);
        //攻占梦魇世界土区所有城池
        rdArriveLuT.setNightmare(isActive(gameUser));
        rdArriveLuT.setIsExped(cityExpService.hasExped(gameUser.getId(), city));
        return rdArriveLuT;
    }

    @Override
    public RDCityInfo exp(GameUser gu, CfgCityEntity city) {
        OCData ocData = CityConfig.bean().getOcDATA();
        RDArriveLuT rdArriveLuT = RDArriveLuT.getInstance(ocData);
        rdArriveLuT.setNightmare(true);
        return rdArriveLuT;
    }

    @Override
    public RDCommon handleProcessor(GameUser gu, Object param) {
        RDCommon rd = new RDCommon();
        CPLtTribute cpLtTribute = (CPLtTribute) param;
        int cardId = cpLtTribute.getCardId();
        int tributeType = cpLtTribute.getTributeType();
        int type = cpLtTribute.getType();
        UserCard userCard = userCardService.getUserCard(gu.getId(), cardId);
        if (userCard == null) {
            throw new ExceptionForClientTip("city.lt.card.null");
        }
        if (type == LuTTypeEnum.CARD_LV_BACK.getValue()) {
            //重置卡牌
            resetLv(gu.getId(), userCard, rd);
            CityEventPublisher.pubLtBackEvent(gu.getId(), rd);
            return rd;
        }
        if (type == LuTTypeEnum.CARD_HV_BACK.getValue()) {
            //重置卡牌
            resetHv(gu.getId(), userCard, rd);
            return rd;
        }
        if (type == LuTTypeEnum.CARD_UP.getValue()) {
            // 卡牌是否满级
            CardChecker.checkIsFullUpdate(userCard);
            // 升到下级需要的经验
            long nextLevelExp = CardExpTool.getExpByLevel(userCard.gainCard(), userCard.getLevel() + 1);
            int addedExp = Long.valueOf(nextLevelExp - userCard.getExperience()).intValue();
            if (tributeType == 10) {
                // 进贡铜钱
                long needCopper = Long.valueOf(addedExp) * CityConfig.bean().getOcDATA().getLtCopperPerExp();
                //风起霓裳时装效果：消耗的铜钱减少最高为20%
                needCopper -= needCopper * userLeaderFashionService.getFashionFengQNSCopperBuff(gu);
                ResChecker.checkCopper(gu, needCopper);
                ResEventPublisher.pubCopperDeductEvent(gu.getId(), needCopper, WayEnum.LT, rd);
            }
            if (tributeType == 20) {
                // 进贡元素
                int needEle = addedExp / CityConfig.bean().getOcDATA().getLtExpPerEle();
                int eleType = userCard.gainCard().getType();
                int eleCount = gu.getEleCount(TypeEnum.fromValue(eleType));
                if (eleCount >= needEle) {
                    // 元素够的直接扣
                    ResChecker.checkEle(gu, eleType, needEle);
                    ResEventPublisher.pubEleDeductEvent(gu.getId(), Arrays.asList(new EVEle(eleType, needEle)), WayEnum.LT, rd);
                } else {
                    // 元素不够，看看用神沙补上去够不够
                    int sx = userTreasureService.getTreasureNum(gu.getId(), TreasureEnum.SS.getValue());
                    if (sx + eleCount < needEle) {
                        ResChecker.checkEle(gu, eleType, needEle);
                    } else {
                        int num = needEle - eleCount;
                        ResEventPublisher.pubEleDeductEvent(gu.getId(), Arrays.asList(new EVEle(eleType, eleCount)), WayEnum.LT, rd);
                        TreasureEventPublisher.pubTDeductEvent(gu.getId(), TreasureEnum.SS.getValue(), num, WayEnum.LT, rd);
                    }
                }
            }
            // 处理卡牌
            BaseEventParam bp = new BaseEventParam(gu.getId(), WayEnum.LT, rd);
            // +1，防止卡牌刚好升满时计算出来的等级仍是旧的
            CardEventPublisher.pubCardExpAddEvent(bp, cardId, addedExp + 1);
            CityEventPublisher.pubLtTributeEvent(gu.getId(), rd);
        }
        return rd;
    }

    /**
     * 是否已操作
     *
     * @param gu
     * @param param
     */
    @Override
    public void checkIsHandle(GameUser gu, Object param) {
        RDArriveLuT luTInfo = TimeLimitCacheUtil.getArriveCache(gu.getId(), getRDArriveClass());
        CPLtTribute cpLtTribute = (CPLtTribute) param;
        if (cpLtTribute.getType() == LuTTypeEnum.CARD_UP.getValue() && luTInfo.isCardExpAdd()) {
            throw new ExceptionForClientTip(getTipCodeForAlreadyHandle());
        }
        if (cpLtTribute.getType() == LuTTypeEnum.CARD_LV_BACK.getValue() && luTInfo.isCardLvBack()) {
            throw new ExceptionForClientTip("city.lt.already.back");
        }
        if (cpLtTribute.getType() == LuTTypeEnum.CARD_HV_BACK.getValue() && luTInfo.isCardHvBack()) {
            throw new ExceptionForClientTip("city.lt.already.back");
        }
    }

    /**
     * 标记已处理
     *
     * @param gu
     * @param param
     */
    @Override
    public void setHandleStatus(GameUser gu, Object param) {
        CPLtTribute cpLtTribute = (CPLtTribute) param;
        RDArriveLuT luTInfo = TimeLimitCacheUtil.getArriveCache(gu.getId(), getRDArriveClass());
        LuTTypeEnum luTType = LuTTypeEnum.fromValue(cpLtTribute.getType());
        switch (luTType) {
            case CARD_UP:
                luTInfo.setCardExpAdd(true);
                break;
            case CARD_LV_BACK:
                luTInfo.setCardLvBack(true);
                break;
            case CARD_HV_BACK:
                luTInfo.setCardHvBack(true);
                break;
        }
        TimeLimitCacheUtil.setArriveCache(gu.getId(), luTInfo);
    }

    @Override
    public String getTipCodeForAlreadyHandle() {
        return "city.lt.already.tribute";
    }

    /**
     * 重置卡牌等级
     *
     * @param uid
     * @param userCard
     * @param rd
     */
    private void resetLv(long uid,UserCard userCard,RDCommon rd){
        //可选择一张5级及以上卡牌，将该卡牌的等级清0，并返还给玩家70%的同属性元素及铜钱。
        if(userCard.getLevel() < 5){
            throw new ExceptionForClientTip("city.lt.reset.lv");
        }

        int backPercent = CityConfig.bean().getOcDATA().getLtLvBackPercent();
        //返还铜钱
        long backCopper = userCard.getExperience() * CityConfig.bean().getOcDATA().getMYLTCopperPerExp() / 2 * backPercent / 100;
        ResEventPublisher.pubCopperAddEvent(uid, backCopper, WayEnum.LT, rd);
        //返还元素
        int backEle = (int) (userCard.getExperience() / (CityConfig.bean().getOcDATA().getLtExpPerEle() * 2) * backPercent / 100);
        int eleType = userCard.gainCard().getType();
        ResEventPublisher.pubEleAddEvent(uid, Arrays.asList(new EVEle(eleType, backEle)), WayEnum.LT, rd);
        //卡牌重置
        userCard.resetLv();
        gameUserService.updateItem(userCard);
    }

    /**
     * 重置阶级
     *
     * @param uid
     * @param userCard
     * @param rd
     */
    private void resetHv(long uid, UserCard userCard, RDCommon rd) {
        if (userCard.getHierarchy() <= 0) {
            throw new ExceptionForClientTip("city.lt.hv.0");
        }
        CfgCardEntity cardEntity = userCard.gainCard();
        int star = cardEntity.getStar();
        Integer[] ltHvBackPercent = CityConfig.bean().getOcDATA().getLtHvBackPercent();
        int backLingShi = 0;
        int backHunDXS = 0;
        for (int i = 0; i < userCard.getHierarchy(); i++) {
            int hv = i + 1;
            backLingShi += CardHvTool.getNeededLingshiForUpdate(hv - 1) * ltHvBackPercent[i] / 100;
            backHunDXS += CardHvTool.getNeededHYXSForUpdate(hv - 1, star) * ltHvBackPercent[i] / 100;
        }
        //资源返还
        if (backLingShi > 0) {
            userCard.addLingshi(backLingShi);
            rd.addCard(new RDCommon.RDCardInfo(userCard.getBaseId(), cardEntity.getSoulId(), backLingShi));
        }
        if (backHunDXS > 0) {
            TreasureEventPublisher.pubTAddEvent(uid, TreasureEnum.HDXS.getValue(), backHunDXS, WayEnum.LT, rd);
        }
        //重置阶数
        userCard.setHierarchy(0);
        gameUserService.updateItem(userCard);
    }

    /**
     * 是否攻占梦魇世界土区1、2、3级城池，并且在梦魇世界中
     *
     * @param gu
     * @return
     */
    private boolean isActive(GameUser gu) {
        boolean isActive = userCityService.isOwnLowNightmareCityAsCountry(gu.getId(), TypeEnum.Earth.getValue());
        return isActive && gu.getStatus().ifNotInFsdlWorld();
    }
}
