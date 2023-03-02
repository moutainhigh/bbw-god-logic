package com.bbw.god.city.taiyf.mytaiyf;

import com.bbw.coder.CoderNotify;
import com.bbw.common.ListUtil;
import com.bbw.common.PowerRandom;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.activity.monthlogin.MonthLoginEnum;
import com.bbw.god.activity.monthlogin.MonthLoginLogic;
import com.bbw.god.city.*;
import com.bbw.god.city.event.CityEventPublisher;
import com.bbw.god.city.exp.CityExpService;
import com.bbw.god.event.BaseEventParam;
import com.bbw.god.game.config.TypeEnum;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.card.CardTool;
import com.bbw.god.game.config.card.CfgCardEntity;
import com.bbw.god.game.config.city.CfgCityEntity;
import com.bbw.god.game.config.city.CityConfig;
import com.bbw.god.game.config.city.CityTypeEnum;
import com.bbw.god.game.config.special.CfgSpecialEntity;
import com.bbw.god.game.config.special.SpecialTool;
import com.bbw.god.game.config.treasure.TreasureTool;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.card.UserCard;
import com.bbw.god.gameuser.card.UserCardRandomService;
import com.bbw.god.gameuser.card.UserCardService;
import com.bbw.god.gameuser.card.event.CardEventPublisher;
import com.bbw.god.gameuser.special.SpecialChecker;
import com.bbw.god.gameuser.special.UserSpecial;
import com.bbw.god.gameuser.special.UserSpecialService;
import com.bbw.god.gameuser.special.event.EPSpecialDeduct;
import com.bbw.god.gameuser.special.event.SpecialEventPublisher;
import com.bbw.god.gameuser.treasure.event.EVTreasure;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.random.config.RandomKeys;
import com.bbw.god.random.service.RandomParam;
import com.bbw.god.rd.RDAdvance;
import com.bbw.god.rd.RDCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 梦魇世界太一府
 *
 * @author lzc
 * @date 2021年03月19日
 */
@Component
public class MYTaiYFProcessor implements ICityArriveProcessor, ICityHandleProcessor, ICityExpProcessor {
    private List<CityTypeEnum> cityTypes = Arrays.asList(CityTypeEnum.TYF);
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private UserCardRandomService userCardRandomService;
    @Autowired
    private UserCardService userCardService;
    @Autowired
    private UserSpecialService userSpecialService;
    @Autowired
    private MonthLoginLogic monthLoginLogic;
    @Autowired
    private UserCityService userCityService;
    @Autowired
    private CityExpService cityExpService;
    // 选卡策略
    private static final String[] STRATEGY_KEYS = {RandomKeys.TAIYF_5, RandomKeys.TAIYF_10, RandomKeys.TAIYF_15, RandomKeys.TAIYF_20, RandomKeys.TAIYF_25};
    //能捐献的最高特产id
    private static final Integer MyTyfMaxSpecialId = 40;
    //卡牌灵石兑换万能灵石（5 换 3）
    private static final Integer cardSoul = 5;

    @Override
    public List<CityTypeEnum> getCityTypes() {
        return cityTypes;
    }

    @Override
    public Class<RDArriveMYTaiYF> getRDArriveClass() {
        return RDArriveMYTaiYF.class;
    }

    /**
     * 到达梦魇太一府
     *
     * @param gu
     * @param city
     * @param rd
     */
    @Override
    public RDArriveMYTaiYF arriveProcessor(GameUser gu, CfgCityEntity city, RDAdvance rd) {
        UserMYTyfFillRecord fillRecord = getFillRecord(gu.getId());
        //重置兑换状态
        if (fillRecord.getIsConvert() == 1 || fillRecord.getConvertLevel() != 0) {
            fillRecord.resetConvertInfo();
            gameUserService.updateItem(fillRecord);
        }
        List<Integer> roundSpecialCells = getRoundSpecialIds(gu.getId(), false);
        RDArriveMYTaiYF rdArriveMYTaiYF = new RDArriveMYTaiYF();
        rdArriveMYTaiYF.setSpecialCells(fillRecord.getSpecialIds());
        rdArriveMYTaiYF.setHandleStatus("1");
        rdArriveMYTaiYF.setRoundSpecialCells(roundSpecialCells);
        rdArriveMYTaiYF.setIsExped(cityExpService.hasExped(gu.getId(), city));
        rdArriveMYTaiYF.setNightmare(isActive(gu.getId()));

        return rdArriveMYTaiYF;
    }

    @Override
    public RDCityInfo exp(GameUser gu, CfgCityEntity city) {
        UserMYTyfFillRecord fillRecord = getFillRecord(gu.getId());
        fillRecord.setIsConvert(1);
        fillRecord.setConvertLevel(5);
        fillRecord.setRemainConvertTimes(UserMYTyfFillRecord.MAX_CONVERT_TIMES);
        gameUserService.updateItem(fillRecord);
        return new RDCityInfo();
    }

    /**
     * 梦魇太一府操作逻辑
     *
     * @param gu
     * @param param
     * @return
     */
    @Override
    public RDCommonMYTaiYF handleProcessor(GameUser gu, Object param) {
        RDCommonMYTaiYF rd = new RDCommonMYTaiYF();
        long uid = gu.getId();
        int specialId = (Integer) param;
        // 太一府只能捐献普通和高级特产
        if (specialId > MyTyfMaxSpecialId) {
            throw new ExceptionForClientTip("city.tyf.not.topSpecial");
        }
        List<Integer> roundSpecialIds = getRoundSpecialIds(uid,false);
        boolean isRoundSpecial = roundSpecialIds.contains(specialId);
        if (!isRoundSpecial) {
            throw new ExceptionForClientTip("city.mytyf.round");
        }
        // 检查捐献的特产是否拥有
        UserSpecial userSpecial = userSpecialService.getOwnSpecialBySpecialId(uid, specialId);
        SpecialChecker.checkIsOwnSpecial(userSpecial);

        // 该特产是否已捐献
        UserMYTyfFillRecord fillRecord = getFillRecord(uid);
        List<Integer> filledSpecialIds = fillRecord.getSpecialIds();
        boolean isFilled = filledSpecialIds.contains(specialId);
        if (isFilled) {
            throw new ExceptionForClientTip("city.tyf.already.fillTheOne");
        }
        int filledCount = filledSpecialIds.size() + 1;

        // 扣除特产
        BaseEventParam bep = new BaseEventParam(uid, WayEnum.TYF, rd);
        CfgSpecialEntity special = SpecialTool.getSpecialById(specialId);
        EPSpecialDeduct.SpecialInfo info = EPSpecialDeduct.SpecialInfo.getInstance(userSpecial.getId(), specialId, special.getBuyPrice(userSpecial.getDiscount()));
        EPSpecialDeduct ep = EPSpecialDeduct.instance(bep, gu.getLocation().getPosition(), Arrays.asList(info));
        SpecialEventPublisher.pubSpecialDeductEvent(ep);

        // 奖励发放
        int star = getAwardStar(filledCount);
        if (star > 0) {
            int cardId = 0;
            // 指定参数
            RandomParam randomParams = new RandomParam();
            List<UserCard> ownCards = userCardService.getUserCards(uid);
            //卡牌排序
            ownCards = sortOwnCards(ownCards);
            randomParams.setRoleCards(ownCards);
            randomParams.setExtraCardsToMap(ownCards);
            String strategyKey = STRATEGY_KEYS[star - 1];
            Optional<CfgCardEntity> card = userCardRandomService.getRandomCard(uid, strategyKey, randomParams);

            if (card.isPresent()) {
                cardId = card.get().getId();
            } else {
                String title = "卡牌策略[" + strategyKey + "]错误!";
                String msg = "区服sid[" + gu.getServerId() + "]玩家[" + uid + "," + gu.getRoleInfo().getNickname() + "]";
                msg += "未能从[梦魇太一府]获得卡牌！";
                CoderNotify.notifyCoderInfo(title, msg);
                // ------------ 2019-04-10 之前的原来的算法------------------------
                if (star < 5) {
                    cardId = CardTool.getRandomNotSpecialCard(star).getId();
                } else {
                    // 五星卡牌奖励
                    cardId = userCardService.getCard5ForCityDontation(uid);
                }
            }

            if (monthLoginLogic.isExistEvent(gu.getId(), MonthLoginEnum.GOOD_TY)) {
                CardEventPublisher.pubCardAddEvent(uid, Arrays.asList(cardId, cardId), WayEnum.TYF, "梦魇太一府捐献特产", rd);
            } else {
                CardEventPublisher.pubCardAddEvent(uid, cardId, WayEnum.TYF, "梦魇太一府捐献特产", rd);
            }
            //是否可兑换
            if (isActive(uid)) {
                //已攻占梦魇世界火区1、2、3级城池
                fillRecord.setIsConvert(1);
                fillRecord.setConvertLevel(star);
                rd.setIsConvert(1);
                rd.setConvertLevel(star);
            }
        }

        //开启下一轮
        if (filledCount == CityConfig.bean().getOcDATA().getMYTyfNeedFillNum()) {
            // 将本轮标注已满
            fillRecord.setIsFillAll(true);
            fillRecord.resetConvertInfo();
            gameUserService.updateItem(fillRecord);
            // 设置新的一轮
            nextMYTyfFillRecord(gu.getId(), rd.getIsConvert(), rd.getConvertLevel());
            getRoundSpecialIds(gu.getId(), true);
        }

        // 捐献记录
        fillRecord.addFillSpecial(specialId);
        gameUserService.updateItem(fillRecord);
        CityEventPublisher.pubTyfFillEvent(gu.getId(), rd);

        return rd;
    }

    /**
     * 对拥有的卡牌排序
     *
     * @param ownCards
     * @return
     */
    private List<UserCard> sortOwnCards(List<UserCard> ownCards) {
        //按星级从高到低
        ownCards.sort((card1, card2) -> card2.gainCard().getStar().compareTo(card1.gainCard().getStar()));
        //按阶级从高到低
        ownCards = ownCards.stream().sorted(Comparator.comparing(UserCard::getHierarchy).reversed()).collect(Collectors.toList());
        return ownCards;
    }

    /**
     * 是否可兑换(已攻占梦魇世界火区1、2、3级城池)
     *
     * @param uid
     * @return
     */
    public boolean isActive(long uid) {
        return userCityService.isOwnLowNightmareCityAsCountry(uid, TypeEnum.Fire.getValue());
    }

    /**
     * 梦魇太一府兑换万能灵石(5个换3个)
     *
     * @param gu
     * @param cardId
     * @return
     */
    public RDCommon cardSoulConvert(GameUser gu, int cardId) {
        RDCommon rd = new RDCommon();
        //是否可兑换
        UserMYTyfFillRecord fillRecord = getFillRecord(gu.getId());

        if (fillRecord.getIsConvert() == 0) {
            throw new ExceptionForClientTip("city.mytyf.convert");
        }
        //获取兑换等级
        int star = fillRecord.getConvertLevel();
        if (star == 0) {
            throw new ExceptionForClientTip("city.mytyf.convert");
        }
        //兑换次数检测
        if (fillRecord.getRemainConvertTimes() <= 0) {
            throw new ExceptionForClientTip("city.mytyf.convert.over");
        }
        //校验可捐献灵石等级
        UserCard userCard = userCardService.getUserCard(gu.getId(), cardId);
        if (userCard.gainCard().getStar() > star) {
            throw new ExceptionForClientTip("city.mytyf.convert.short");
        }
        // 灵石是否足够
        if (userCard.getLingshi() < cardSoul) {
            throw new ExceptionForClientTip("card.update.not.enough.lingshi");
        }
        //扣除灵石
        userCard.deductLingshi(cardSoul);
        this.gameUserService.updateItem(userCard);
        List<RDCommon.RDCardLingshi> lingshis = new ArrayList<>();
        lingshis.add(RDCommon.RDCardLingshi.instance(userCard.getBaseId(), -Math.abs(cardSoul)));
        rd.setDeductedCardLingshi(lingshis);
        //发放万能灵石
        List<EVTreasure> treasures = new ArrayList<>();// 灵石
        int convertNum = fillRecord.getRemainConvertTimes();
        treasures.add(new EVTreasure(getCommonSoulId(userCard.gainCard().getStar()), convertNum));
        TreasureEventPublisher.pubTAddEvent(gu.getId(), treasures, WayEnum.MYTYF, rd);
        //更改兑换状态
        fillRecord.updateRemainConverTimes();
        gameUserService.updateItem(fillRecord);
        return rd;
    }

    /**
     * 获取万能灵石ID
     *
     * @param star
     * @return
     */
    public Integer getCommonSoulId(int star) {
        return TreasureTool.getTreasureById(800 + 10 * star).getId();
    }

    public UserMYTyfFillRecord getFillRecord(long uid) {
        List<UserMYTyfFillRecord> fillRecords = gameUserService.getMultiItems(uid, UserMYTyfFillRecord.class);
        // 没有任何的UserTyfFillRecord
        if (ListUtil.isEmpty(fillRecords)) {
            return addNewMYTyfFillRecord(uid);
        }
        Optional<UserMYTyfFillRecord> optional = fillRecords.stream().filter(tmp -> !tmp.getIsFillAll()).findFirst();
        // 没有捐献中的记录
        if (!optional.isPresent()) {
            return addNewMYTyfFillRecord(uid);
        }
        return optional.get();
    }

    /**
     * 获取本轮特产列表
     *
     * @param uid
     * @param isReset (是否重置)
     * @return
     */
    public List<Integer> getRoundSpecialIds(long uid,boolean isReset) {
        List<UserMYTyfRoundSpecial> roundSpecials = gameUserService.getMultiItems(uid, UserMYTyfRoundSpecial.class);
        if (ListUtil.isEmpty(roundSpecials)) {
            UserMYTyfRoundSpecial newRoundSpecial = UserMYTyfRoundSpecial.instance(uid,randomRoundSpecialIds());
            gameUserService.addItem(uid, newRoundSpecial);
            return newRoundSpecial.getSpecialIds();
        }
        UserMYTyfRoundSpecial roundSpecial = roundSpecials.get(0);
        if(isReset){
            roundSpecial.setSpecialIds(randomRoundSpecialIds());
            gameUserService.updateItem(roundSpecial);
        }
        return roundSpecial.getSpecialIds();
    }

    /**
     * 随机本轮特产
     *
     * @return
     */
    private List<Integer> randomRoundSpecialIds(){
        List<Integer> specialIds = SpecialTool.getSpecials().stream().filter(s -> s.getId() < MyTyfMaxSpecialId + 1).map(CfgSpecialEntity::getId).collect(Collectors.toList());
        return PowerRandom.getRandomsFromList(specialIds,CityConfig.bean().getOcDATA().getMYTyfNeedFillNum());
    }

    /**
     * 设置新的一轮
     *
     * @param guId
     * @return
     */
    private UserMYTyfFillRecord addNewMYTyfFillRecord(long guId) {
        UserMYTyfFillRecord fillRecord = UserMYTyfFillRecord.instance(guId, new ArrayList<>());
        gameUserService.addItem(guId, fillRecord);
        return fillRecord;
    }

    /**
     * 设置下一轮
     *
     * @param guId
     * @return
     */
    private UserMYTyfFillRecord nextMYTyfFillRecord(long guId,int isConvert,int convertLevel) {
        UserMYTyfFillRecord fillRecord = UserMYTyfFillRecord.next(guId, new ArrayList<>(),isConvert,convertLevel);
        gameUserService.addItem(guId, fillRecord);
        return fillRecord;
    }

    /**
     * 获得奖励的星级
     *
     * @param filledCount
     * @return
     */
    private int getAwardStar(int filledCount) {
        int star = 0;
        if (filledCount % 5 == 0) {
            star = filledCount / 5;
        }
        return star;
    }

    @Override
    public String getTipCodeForAlreadyHandle() {
        return "city.tyf.already.fill";
    }

    @Override
    public boolean isNightmare() {
        return true;
    }
}
