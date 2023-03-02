package com.bbw.god.city.yed;

import com.bbw.common.DateUtil;
import com.bbw.common.ListUtil;
import com.bbw.common.PowerRandom;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.activity.holiday.processor.holidaycutetugermarket.HolidayCuteTigerMarketProcessor;
import com.bbw.god.city.ICityArriveProcessor;
import com.bbw.god.city.UserCityService;
import com.bbw.god.city.miaoy.hexagram.HexagramBuffEnum;
import com.bbw.god.city.miaoy.hexagram.event.HexagramEventPublisher;
import com.bbw.god.event.BaseEventParam;
import com.bbw.god.game.award.AwardStatus;
import com.bbw.god.game.config.Cfg;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.city.CfgCityEntity;
import com.bbw.god.game.config.city.CityTypeEnum;
import com.bbw.god.game.config.city.YdEventEnum;
import com.bbw.god.game.config.mall.CfgMallEntity;
import com.bbw.god.game.config.mall.MallTool;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.businessgang.luckybeast.LuckyBeastService;
import com.bbw.god.gameuser.businessgang.luckybeast.RDLuckyBeastInfo;
import com.bbw.god.gameuser.card.UserCard;
import com.bbw.god.gameuser.card.UserCardService;
import com.bbw.god.gameuser.card.event.CardEventPublisher;
import com.bbw.god.gameuser.task.timelimit.cunz.UserCunZTaskService;
import com.bbw.god.rd.RDAdvance;
import com.bbw.god.rd.RDCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 野地 - 随机事件
 *
 * @author suhq
 * @date 2018年10月24日 下午5:33:49
 */
@Component
public class YeDProcessor implements ICityArriveProcessor {
    @Autowired
    private YeDEventProcessorFactory eventProcessorFactory;
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private UserCityService userCityService;
    @Autowired
    private UserCardService userCardService;
    @Autowired
    private UserCunZTaskService userCunZTaskService;
    @Autowired
    private LuckyBeastService luckyBeastService;
    @Autowired
    private HolidayCuteTigerMarketProcessor holidayCuteTigerMarketProcessor;

    private List<CityTypeEnum> cityTypes = Arrays.asList(CityTypeEnum.YD);
    private List<Integer> YD_EVENT_ID = Arrays.asList(17, 27);

    @Override
    public List<CityTypeEnum> getCityTypes() {
        return cityTypes;
    }

    @Override
    public Class<RDArriveYeD> getRDArriveClass() {
        return RDArriveYeD.class;
    }

    @Override
    public RDArriveYeD arriveProcessor(GameUser gameUser, CfgCityEntity city, RDAdvance rd) {
        CfgYeDiEventEntity ydEvent = getRandomYDEvent();
        int event = ydEvent.getId();
        Long uid = gameUser.getId();
        if ((event == YdEventEnum.KC.getValue() || event == YdEventEnum.HC.getValue() || event == YdEventEnum.TAX.getValue())
                && ListUtil.isEmpty(userCityService.getUserCities(uid))) {
            ydEvent = getYDEventById(YdEventEnum.NONE.getValue());
        }
        RDArriveYeD rdArriveYeD = new RDArriveYeD();
        rdArriveYeD.updateEvent(ydEvent);
        BaseYeDEventProcessor processor = eventProcessorFactory.getBaseProcessorById(ydEvent.getId());
        processor.effect(gameUser, rdArriveYeD, rd);
        //招财兽信息
        RDLuckyBeastInfo luckyBeastInfo = luckyBeastService.arriveLuckyBeast(uid);
        if (YD_EVENT_ID.contains(ydEvent.getId()) && null != luckyBeastInfo) {
            rd.setArriveLuckyBeast(luckyBeastInfo);
        }
        //萌虎集市
        holidayCuteTigerMarketProcessor.specialYeDiEvent(uid, rdArriveYeD);
        rdArriveYeD.updateByRdCommon(rd);
        if (processor.hasHexagramBuff(uid)) {
            HexagramEventPublisher.pubHexagramBuffDeductEvent(new BaseEventParam(uid, WayEnum.YD, rd), HexagramBuffEnum.HEXAGRAM_15.getId(), 1);
        }
        return rdArriveYeD;
    }

    private CfgYeDiEventEntity getRandomYDEvent() {
        List<CfgYeDiEventEntity> ydEvents = getEvents();
        int random = PowerRandom.getRandomBySeed(100);
        int sum = 0;
        for (CfgYeDiEventEntity ydEvent : ydEvents) {
            sum += ydEvent.getProbability();
            if (sum >= random) {
                return ydEvent;
            }
        }
        return getYDEventById(YdEventEnum.NONE.getValue());
    }

    private List<CfgYeDiEventEntity> getEvents() {
        return Cfg.I.get(CfgYeDiEventEntity.class);
    }

    protected CfgYeDiEventEntity getYDEventById(Integer id) {
        return Cfg.I.get(id, CfgYeDiEventEntity.class);
    }

    /**
     * 进入奇遇页面
     *
     * @param uid
     * @return
     */
    public RDAdventures listAdventures(long uid) {
        List<RDAdventures.RDAdventureInfo> adventureList = new ArrayList<>();
        if (userCunZTaskService.getTaskCount(uid) > 0) {
            adventureList.add(RDAdventures.RDAdventureInfo.getInstance(0L, AdventureType.CUN_ZHUANG_TASK.getValue(), AwardStatus.UNAWARD.getValue()));
        }
        List<UserAdventure> timeOutList = new ArrayList<>();
        List<UserAdventure> userAdventures = gameUserService.getMultiItems(uid, UserAdventure.class);
        for (UserAdventure userAdventure : userAdventures) {
            if (AdventureType.YYSR.getValue() == userAdventure.getType().intValue()) {
                if (!userAdventure.isValid(userAdventure.getType())) {
                    timeOutList.add(userAdventure);
                    continue;
                }
                Long dataId = userAdventure.getId();
                int status = AwardStatus.ENABLE_AWARD.getValue();
                RDAdventures.RDAdventureInfo info = RDAdventures.RDAdventureInfo.getInstance(dataId, 20, status);
                adventureList.add(info);
                continue;
            }
            if (AdventureType.XRSY.getValue() == userAdventure.getType().intValue()) {
                if (!userAdventure.isValid(userAdventure.getType())) {
                    timeOutList.add(userAdventure);
                    continue;
                }
                Long dataId = userAdventure.getId();
                int waitMinute = (userAdventure.getAbleMaxExp() - 1000) / 1000 * 5 + 10;
                Date date = DateUtil.addMinutes(userAdventure.getGenerateTime(), waitMinute);
                long remainTime = date.getTime() - System.currentTimeMillis();
                int status = remainTime > 0 ? AwardStatus.UNAWARD.getValue() : AwardStatus.ENABLE_AWARD.getValue();
                RDAdventures.RDAdventureInfo info = RDAdventures.RDAdventureInfo.getInstance(dataId, 10, status);
                adventureList.add(info);
            }
        }
        gameUserService.deleteItems(uid, timeOutList);
        return new RDAdventures(adventureList);
    }

    public RDArriveYeD getAdventureInfo(long uid, long dataId) {
        List<UserAdventure> userAdventures = gameUserService.getMultiItems(uid, UserAdventure.class);
        UserAdventure userAdventure = userAdventures.stream().filter(ua -> ua.isValid(ua.getType())
                && ua.getId().equals(dataId)).findFirst().orElse(null);
        if (null == userAdventure) {
            throw new ExceptionForClientTip("yeD.error.dataId");
        }
        Integer baseId = userAdventure.getBaseId();
        RDArriveYeD rdArriveYeD = new RDArriveYeD();
        // 云游商人
        if (AdventureType.YYSR.getValue() == userAdventure.getType().intValue()) {
            rdArriveYeD.setGoodsId(baseId);
            CfgMallEntity mallEntity = MallTool.getMall(userAdventure.getMallId());
            rdArriveYeD.setMallId(mallEntity.getId());
            rdArriveYeD.setNum(mallEntity.getNum());
            rdArriveYeD.setOriginalPrice(mallEntity.getOriginalPrice());
            rdArriveYeD.setCurrentPrice(mallEntity.getPrice());
            Date generateTime = userAdventure.getGenerateTime();
            Date date = DateUtil.addMinutes(generateTime, 60);
            long remainTime = date.getTime() - System.currentTimeMillis();
            rdArriveYeD.setRemainTime(remainTime);
            rdArriveYeD.setDataId(userAdventure.getId());
        } else if (AdventureType.XRSY.getValue() == userAdventure.getType().intValue()) {
            rdArriveYeD.setCardId(baseId);
            Integer ableMaxExp = userAdventure.getAbleMaxExp();
            int waitMinute = (ableMaxExp - 1000) / 1000 * 5 + 10;
            Date date = DateUtil.addMinutes(userAdventure.getGenerateTime(), waitMinute);
            long remainTime = date.getTime() - System.currentTimeMillis();
            remainTime = Math.max(remainTime, 0L);
            rdArriveYeD.setRemainTime(remainTime);
            rdArriveYeD.setDataId(userAdventure.getId());
            rdArriveYeD.setExp(ableMaxExp);
        }
        return rdArriveYeD;
    }

    /**
     * 仙人授业获取卡牌经验
     *
     * @param uid
     * @param dataId
     * @param rd
     * @return
     */
    public RDCommon gainCardExp(long uid, long dataId, RDCommon rd) {
        UserAdventure userAdventure = gameUserService.getMultiItems(uid, UserAdventure.class).stream()
                .filter(ua -> ua.getId().equals(dataId)).findFirst().orElse(null);
        if (null == userAdventure) {
            throw new ExceptionForClientTip("yeD.error.dataId",dataId);
        }
        Integer ableMaxExp = userAdventure.getAbleMaxExp();
        int waitMinute = (ableMaxExp - 1000) / 1000 * 5 + 10;
        Date date = DateUtil.addMinutes(userAdventure.getGenerateTime(), waitMinute);
        long remainTime = date.getTime() - System.currentTimeMillis();
        if (remainTime > 0) {
            throw new ExceptionForClientTip("yeD.can.not.gain.card.exp");
        }
        Integer cardId = userAdventure.getBaseId();
        UserCard userCard = userCardService.getUserNormalCardOrDeifyCard(uid, cardId);
        cardId=userCard.getBaseId();
        BaseEventParam bep = new BaseEventParam(uid, WayEnum.YD, rd);
        CardEventPublisher.pubCardExpAddEvent(bep, cardId, ableMaxExp);
        gameUserService.deleteItem(userAdventure);
        return rd;
    }
}
