package com.bbw.god.city.miaoy;

import com.bbw.App;
import com.bbw.common.PowerRandom;
import com.bbw.common.StrUtil;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.activity.monthlogin.MonthLoginEnum;
import com.bbw.god.activity.monthlogin.MonthLoginLogic;
import com.bbw.god.city.*;
import com.bbw.god.city.event.CityEventPublisher;
import com.bbw.god.city.exp.CityExpService;
import com.bbw.god.game.config.TypeEnum;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.card.CfgCardEntity;
import com.bbw.god.game.config.city.CfgCityEntity;
import com.bbw.god.game.config.city.CityConfig;
import com.bbw.god.game.config.city.CityTypeEnum;
import com.bbw.god.game.config.god.GodEnum;
import com.bbw.god.game.config.treasure.CfgTreasureEntity;
import com.bbw.god.game.config.treasure.TreasureTool;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.card.UserCardService;
import com.bbw.god.gameuser.card.event.CardEventPublisher;
import com.bbw.god.gameuser.god.UserGod;
import com.bbw.god.gameuser.res.ResChecker;
import com.bbw.god.gameuser.res.ResEventPublisher;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.random.config.RandomKeys;
import com.bbw.god.random.service.RandomCardService;
import com.bbw.god.random.service.RandomParam;
import com.bbw.god.rd.RDAdvance;
import com.bbw.god.server.god.GodService;
import com.bbw.god.server.god.ServerGod;
import com.bbw.god.server.god.processor.AbstractGodProcessor;
import com.bbw.god.server.god.processor.GodProcessorFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * 庙宇 - 抽签
 *
 * @author suhq
 * @date 2018年10月24日 下午5:49:46
 */
@Component
public class MiaoYProcessor implements ICityArriveProcessor, ICityHandleProcessor, ICityExpProcessor {
    private List<CityTypeEnum> cityTypes = Arrays.asList(CityTypeEnum.MY);
    @Autowired
    private GodService godService;
    @Autowired
    private GodProcessorFactory godProcessorFactory;
    @Autowired
    private UserCardService userCardService;
    @Autowired
    private MonthLoginLogic monthLoginLogic;
    @Autowired
    private App app;
    @Autowired
    private UserCityService userCityService;
    @Autowired
    private CityExpService cityExpService;

    @Override
    public List<CityTypeEnum> getCityTypes() {
        return cityTypes;
    }

    @Override
    public Class<RDArriveMiaoY> getRDArriveClass() {
        return RDArriveMiaoY.class;
    }

    @Override
    public RDArriveMiaoY arriveProcessor(GameUser gameUser, CfgCityEntity city, RDAdvance rd) {
        RDArriveMiaoY rdArriveMiaoY = new RDArriveMiaoY();
        rdArriveMiaoY.setHandleStatus("1");
        rdArriveMiaoY.setHexagram(0);
        if (isActive(gameUser)) {
            rdArriveMiaoY.setHexagram(1);
        }
        rdArriveMiaoY.setIsExped(cityExpService.hasExped(gameUser.getId(), city));
        return rdArriveMiaoY;
    }

    @Override
    public RDCityInfo exp(GameUser gu, CfgCityEntity city) {
        RDArriveMiaoY rdArriveMiaoY = new RDArriveMiaoY();
        rdArriveMiaoY.setHandleStatus("1");
        rdArriveMiaoY.setHexagram(1);
        return rdArriveMiaoY;
    }

    @Override
    public RDDrawMiaoY handleProcessor(GameUser gu, Object param) {
        RDDrawMiaoY rd = new RDDrawMiaoY();
        int type = (Integer) param;
        DrawType drawType = DrawType.fromValue(type);
        // 抽签类型检测
        if (drawType == null) {
            throw new ExceptionForClientTip("city.my.unvalid.drawtype");
        }
        int needGold = CityConfig.bean().getOcDATA().getMyNeedGold();
        // 检查元宝
        ResChecker.checkGold(gu, needGold);
        // 处理结果
        DrawResult result = DrawResult.DOWN;
        switch (drawType) {
            case DRAW_MONEY:// 求财
                result = deliverAsMoney(gu, rd);
                break;
            case DRAW_TREASURE:// 求宝
                result = deliverAsTreasure(gu, rd);
                break;
            case DRAW_CARD:// 求贤
                result = deliverAsCard(gu, rd);
                break;
        }
        // 中签不消耗元宝
        if (result == DrawResult.MIDDLE) {
            needGold = 0;
        }
        // 扣除元宝
        ResEventPublisher.pubGoldDeductEvent(gu.getId(), needGold, WayEnum.MY, rd);
        // 庙宇抽签结果
        EPMiaoYDrawEnd ep = new EPMiaoYDrawEnd(drawType, result);
        CityEventPublisher.pubMiaoYDrawEndEvent(gu.getId(), ep, rd);

        rd.setResult(result.getValue());

        return rd;
    }

    private DrawResult deliverAsMoney(GameUser gu, RDDrawMiaoY rd) {
        DrawResult result = getDrawLotsResult(20, 35, 30, 15,gu.getId());
        switch (result) {
            case UP_UP:// 大财神效果
                ServerGod serverGod1 = godService.getUnrealServerGod(gu.getServerId(), GodEnum.DCS.getValue());// 生成配置
                UserGod newGod1 = UserGod.instance(gu.getId(), serverGod1);// 根据配置生成实例
                // 附体新的神仙
                godService.attachGod(gu, newGod1);// 状态标识
                AbstractGodProcessor godProcessor1 = godProcessorFactory.create(gu.getId(), newGod1.getBaseId());
                godProcessor1.processor(gu, newGod1, rd);// 业务处理
                rd.setAttachGod(rd.getAttachedGod());
                break;
            case UP:// 小财神效果
                ServerGod serverGod2 = godService.getUnrealServerGod(gu.getServerId(), GodEnum.XCS.getValue());// 生成配置
                UserGod newGod2 = UserGod.instance(gu.getId(), serverGod2);// 根据配置生成实例
                // 附体新的神仙
                godService.attachGod(gu, newGod2);// 状态标识
                AbstractGodProcessor godProcessor2 = godProcessorFactory.create(gu.getId(), newGod2.getBaseId());// 业务处理
                godProcessor2.processor(gu, newGod2, rd);// 业务处理
                rd.setAttachGod(rd.getAttachedGod());
                break;
        }
        return result;
    }

    private DrawResult deliverAsTreasure(GameUser gu, RDDrawMiaoY rd) {
        DrawResult result = getDrawLotsResult(5, 50, 30, 15,gu.getId());
        int random = PowerRandom.getRandomBySeed(100);
        CfgTreasureEntity treasure = null;
        switch (result) {
            case UP_UP:
                if (random <= 30) {
                    treasure = TreasureTool.getRandomOldTreasure(5);
                } else{
                    treasure = TreasureTool.getRandomOldTreasure(4);
                }
                break;
            case UP:// 一般法宝
                if (random <= 60) {
                    treasure = TreasureTool.getRandomOldTreasure(3);
                } else if (random <= 80) {
                    treasure = TreasureTool.getRandomOldTreasure(2);
                } else {
                    treasure = TreasureTool.getRandomOldTreasure(1);
                }
                break;
        }
        if (treasure != null) {
            TreasureEventPublisher.pubTAddEvent(gu.getId(), treasure.getId(), 1, WayEnum.MY, rd);
        }
        return result;
    }

    private DrawResult deliverAsCard(GameUser gu, RDDrawMiaoY rd) {
        DrawResult result = getDrawLotsResult(2, 48, 35, 15,gu.getId());
        String strategy = "";
        switch (result) {
            case UP_UP:
                strategy = RandomKeys.MiaoY_UPUP;
                break;
            case UP:// 一般卡牌
                strategy = RandomKeys.MiaoY_UP;
                break;
        }
        if (!StrUtil.isBlank(strategy)) {
            RandomParam randomParam = new RandomParam();
            randomParam.setExtraCardsToMap(userCardService.getUserCards(gu.getId()));
            CfgCardEntity card = RandomCardService.getRandomList(strategy, randomParam).getFirstCard().get();
            CardEventPublisher.pubCardAddEvent(gu.getId(), card.getId(), WayEnum.MY, "在庙宇抽签获得", rd);
        }
        return result;
    }

    /**
     * <pre>
     * 庙宇抽签 结果 上上签 上签 中签 下签
     * 庙宇概率（30求贤） 2 48 35 15
     * 庙宇概率（20求宝） 5 50 30 15
     * 庙宇概率（10求财）20 35 30 15
     * </pre>
     *
     * @param ss 上上签概率
     * @param s  上签概率
     * @param z  中签概率
     * @param x  下签概率
     * @return 1上上签 2上签 3中签 4下签
     */
    private DrawResult getDrawLotsResult(int ss, int s, int z, int x,long uid) {
        boolean noXXQ=monthLoginLogic.isExistEvent(uid,MonthLoginEnum.GOOD_WWM);
        DrawResult result = DrawResult.DOWN;
        int num=100;
        do {
            num--;
            int rand = PowerRandom.getRandomBySeed(100);
            if (rand <= ss) {
                result = DrawResult.UP_UP;
            } else if (rand <= ss + s) {
                result = DrawResult.UP;
            } else if (rand <= ss + s + z) {
                result = DrawResult.MIDDLE;
            }
        }while (noXXQ && result.getValue()==DrawResult.DOWN.getValue() && num>0);
        if (num<=0 && result.getValue()==DrawResult.DOWN.getValue()){
            result = DrawResult.MIDDLE;
        }
        return result;
    }

    @Override
    public String getTipCodeForAlreadyHandle() {
        return "city.my.already.drawLot";
    }

    /**
     * 是否激活建筑功能
     *
     * @param gu
     * @return
     */
    public boolean isActive(GameUser gu) {
        boolean isActive = userCityService.isOwnLowNightmareCityAsCountry(gu.getId(), TypeEnum.Gold.getValue());
        return isActive && gu.getStatus().ifNotInFsdlWorld();
    }
}
