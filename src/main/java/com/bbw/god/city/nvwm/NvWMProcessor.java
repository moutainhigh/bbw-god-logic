package com.bbw.god.city.nvwm;

import com.bbw.coder.CoderNotify;
import com.bbw.common.PowerRandom;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.activity.monthlogin.MonthLoginEnum;
import com.bbw.god.activity.monthlogin.MonthLoginLogic;
import com.bbw.god.city.ICityArriveProcessor;
import com.bbw.god.city.ICityHandleProcessor;
import com.bbw.god.city.UserCityService;
import com.bbw.god.city.event.CityEventPublisher;
import com.bbw.god.event.BaseEventParam;
import com.bbw.god.game.config.TypeEnum;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.card.CfgCardEntity;
import com.bbw.god.game.config.city.CfgCityEntity;
import com.bbw.god.game.config.city.CityTypeEnum;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.card.UserCard;
import com.bbw.god.gameuser.card.UserCardRandomService;
import com.bbw.god.gameuser.card.UserCardService;
import com.bbw.god.gameuser.card.event.CardEventPublisher;
import com.bbw.god.gameuser.nightmarenvwam.pinchpeople.PinchPeopleLogic;
import com.bbw.god.gameuser.nightmarenvwam.pinchpeople.UserKneadSoilService;
import com.bbw.god.gameuser.nightmarenvwam.pinchpeople.UserPinchPeopleInfo;
import com.bbw.god.gameuser.res.ResChecker;
import com.bbw.god.gameuser.res.ResEventPublisher;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.random.config.RandomKeys;
import com.bbw.god.random.service.RandomParam;
import com.bbw.god.rd.RDAdvance;
import com.bbw.god.rd.RDCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * 女娲庙
 *
 * @author suhq
 * @date 2018年10月24日 下午5:49:46
 */
@Component
public class NvWMProcessor implements ICityArriveProcessor, ICityHandleProcessor {
    private List<CityTypeEnum> cityTypes = Arrays.asList(CityTypeEnum.NWM);
    @Autowired
    private UserCardRandomService userCardRandomService;
    @Autowired
    private UserCardService userCardService;
    @Autowired
    private MonthLoginLogic monthLoginLogic;
    @Autowired
    private UserCityService userCityService;
    @Autowired
    private PinchPeopleLogic kneadSoilLogic;
    @Autowired
    private UserKneadSoilService userKneadSoilService;

    @Override
    public List<CityTypeEnum> getCityTypes() {
        return cityTypes;
    }

    @Override
    public Class<RDArriveNvWM> getRDArriveClass() {
        return RDArriveNvWM.class;
    }

    @Override
    public RDArriveNvWM arriveProcessor(GameUser gameUser, CfgCityEntity city, RDAdvance rd) {
        RDArriveNvWM rdArriveNvWM = new RDArriveNvWM();
        rdArriveNvWM.setSatisfaction(gameUser.getStatus().getSatisfaction());
        rdArriveNvWM.setHandleStatus("1");
        if (isActive(gameUser)) {
            UserPinchPeopleInfo kneadSoilInFo = userKneadSoilService.getOrCreatUserKneadSoilInFo(gameUser.getId());
            Integer clayFigurineValue = kneadSoilInFo.getProgressToPinchPeople();
            rdArriveNvWM.setProgressToPinchPeopleTotalValue(clayFigurineValue);
            rdArriveNvWM.setNightmareNvWM(1);
        }
        return rdArriveNvWM;
    }

    @Override
    public RDNvWM handleProcessor(GameUser gu, Object param) {
        RDNvWM rd = new RDNvWM();
        Long needCopper = Long.valueOf(param.toString());
        // 500万检验
        if (needCopper == 5000000 && gu.getCopper() < 50000000) {
            throw new ExceptionForClientTip("city.nvm.not.valid500");
        }
        // 铜钱检验
        ResChecker.checkCopper(gu, needCopper);
        ResEventPublisher.pubCopperDeductEvent(gu.getId(), needCopper, WayEnum.NWM, rd);

        int oldSatisfaction = gu.getStatus().getSatisfaction();
        int addedSatisfaction = getNvmSatisfaction(needCopper.intValue());
        if (monthLoginLogic.isExistEvent(gu.getId(),MonthLoginEnum.GOOD_NW) && PowerRandom.hitProbability(25)){
            addedSatisfaction*=2;
        }
        int satisfaction = oldSatisfaction + addedSatisfaction;
        // 发放灵石奖励
        sendLSAward(gu.getId(), satisfaction, oldSatisfaction, rd);
        // 发放卡牌奖励
        if (oldSatisfaction < 100 && satisfaction >= 100) {
            // ------------刘少军 修改 为从 抽卡策略获取卡牌 2019-04-10
            RandomParam randomParams = new RandomParam();
            List<UserCard> ownCards = userCardService.getUserCards(gu.getId());
            randomParams.setRoleCards(ownCards);
            randomParams.setExtraCardsToMap(ownCards);
            String strategyKey = RandomKeys.NvWM;
            Optional<CfgCardEntity> card = userCardRandomService.getRandomCard(gu.getId(), strategyKey, randomParams);

            int cardId = 0;
            if (card.isPresent()) {
                cardId = card.get().getId();
            } else {
                String title = "卡牌策略[" + strategyKey + "]错误!";
                String msg = "区服sid[" + gu.getServerId() + "]玩家[" + gu.getId() + "," + gu.getRoleInfo().getNickname() + "]";
                msg += "未能从[女娲庙]获得卡牌！";
                CoderNotify.notifyCoderInfo(title, msg);
                // ------------ 2019-04-10 之前的原来的算法------------------------
                // 五星卡牌奖励
                cardId = userCardService.getCard5ForCityDontation(gu.getId());
            }
            CardEventPublisher.pubCardAddEvent(gu.getId(), cardId, WayEnum.NWM, "女娲庙捐献", rd);
            // 满意度重置
            satisfaction -= 100;
            //超出的进度可能获得灵思奖励
            sendLSAward(gu.getId(), satisfaction, 0, rd);
        }
        // 更新满意度
        gu.getStatus().setSatisfaction(satisfaction);
        gu.updateStatus();

        rd.setSatisfaction(satisfaction);
        // 兼容客户端
        rd.setAddedCopper(null);
        CityEventPublisher.pubNwmDonateEvent(addedSatisfaction, new BaseEventParam(gu.getId(), WayEnum.NWM, rd));

        //梦魇女娲庙
        if (isActive(gu)) {
            kneadSoilLogic.donate(gu.getId(), needCopper, rd);
        }
        return rd;
    }

    // 1万（1点）、10万（2~3点）、30万（4~5点）、50万（6~7点）、100万（8~10点）、500万（15点~18点）
    private int getNvmSatisfaction(int copperType) {
        switch (copperType) {
            case 10000:
                return PowerRandom.getRandomBetween(1, 2);// 100
            case 100000:
                return PowerRandom.getRandomBetween(3, 4);// 40
            case 300000:
                return PowerRandom.getRandomBetween(5, 6);// 20
            case 500000:
                return PowerRandom.getRandomBetween(7, 8);// 15
            case 1000000:
                return PowerRandom.getRandomBetween(9, 10);// 11
            case 5000000:
                return 20;// 5
            default:
                return 0;
        }
    }

    @Override
    public String getTipCodeForAlreadyHandle() {
        return "city.nvm.already.donate";
    }

    /**
     * 发放灵石奖励
     *
     * @param satisfaction
     * @param oldSatisfaction
     */
    private void sendLSAward(long guId, int satisfaction, int oldSatisfaction, RDCommon rd) {

        if (oldSatisfaction < 10 && satisfaction >= 10) {
            TreasureEventPublisher.pubTAddEvent(guId, TreasureEnum.WNLS1.getValue(), 3, WayEnum.NWM, rd);
        }

        if (oldSatisfaction < 20 && satisfaction >= 20) {
            TreasureEventPublisher.pubTAddEvent(guId, TreasureEnum.WNLS1.getValue(), 4, WayEnum.NWM, rd);
        }

        if (oldSatisfaction < 30 && satisfaction >= 30) {
            TreasureEventPublisher.pubTAddEvent(guId, TreasureEnum.WNLS2.getValue(), 2, WayEnum.NWM, rd);
        }

        if (oldSatisfaction < 40 && satisfaction >= 40) {
            TreasureEventPublisher.pubTAddEvent(guId, TreasureEnum.WNLS2.getValue(), 3, WayEnum.NWM, rd);
        }

        if (oldSatisfaction < 50 && satisfaction >= 50) {
            TreasureEventPublisher.pubTAddEvent(guId, TreasureEnum.WNLS3.getValue(), 1, WayEnum.NWM, rd);
        }
        if (oldSatisfaction < 60 && satisfaction >= 60) {
            TreasureEventPublisher.pubTAddEvent(guId, TreasureEnum.WNLS3.getValue(), 2, WayEnum.NWM, rd);
        }
        if (oldSatisfaction < 70 && satisfaction >= 70) {
            TreasureEventPublisher.pubTAddEvent(guId, TreasureEnum.WNLS4.getValue(), 1, WayEnum.NWM, rd);
        }
        if (oldSatisfaction < 80 && satisfaction >= 80) {
            TreasureEventPublisher.pubTAddEvent(guId, TreasureEnum.WNLS4.getValue(), 1, WayEnum.NWM, rd);
        }
        if (oldSatisfaction < 90 && satisfaction >= 90) {
            TreasureEventPublisher.pubTAddEvent(guId, TreasureEnum.WNLS5.getValue(), 1, WayEnum.NWM, rd);
        }
    }

    /**
     * 是否激活建筑功能
     *
     * @param gu
     * @return
     */
    private boolean isActive(GameUser gu) {
        boolean isActive = userCityService.isOwnLowNightmareCityAsCountry(gu.getId(), TypeEnum.Wood.getValue());
        return isActive && gu.getStatus().ifNotInFsdlWorld();
    }
}
