package com.bbw.god.rechargeactivities.processor.timelimit;

import com.bbw.common.ListUtil;
import com.bbw.common.PowerRandom;
import com.bbw.god.city.UserCityService;
import com.bbw.god.city.chengc.UserCity;
import com.bbw.god.city.event.EPCityAdd;
import com.bbw.god.city.event.UserCityAddEvent;
import com.bbw.god.event.EventParam;
import com.bbw.god.game.config.card.CardTool;
import com.bbw.god.game.config.card.CfgCardEntity;
import com.bbw.god.game.config.city.CfgCityEntity;
import com.bbw.god.game.config.city.CityTool;
import com.bbw.god.game.config.mall.MallEnum;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.biyoupalace.cfg.Chapter;
import com.bbw.god.gameuser.biyoupalace.event.BiyouRealizedEvent;
import com.bbw.god.gameuser.biyoupalace.event.EPBiyouRealized;
import com.bbw.god.gameuser.biyoupalace.event.EPSecretBiographyUnlock;
import com.bbw.god.gameuser.biyoupalace.event.SecretBiographyUnlockEvent;
import com.bbw.god.gameuser.card.event.EPCardAdd;
import com.bbw.god.gameuser.card.event.UserCardAddEvent;
import com.bbw.god.gameuser.treasure.event.EPTreasureDeduct;
import com.bbw.god.gameuser.treasure.event.EVTreasure;
import com.bbw.god.gameuser.treasure.event.TreasureDeductEvent;
import com.bbw.god.login.DynamicMenuEnum;
import com.bbw.god.mall.MallService;
import com.bbw.god.mall.UserMallRecord;
import com.bbw.god.rd.RDCommon;
import com.bbw.god.rechargeactivities.processor.RechargeStatusEnum;
import com.bbw.mc.m2c.M2cService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 限时礼包触发监听
 *
 * @author suhq
 * @date 2021/7/1 下午5:50
 **/
@Component
public class RoleTimeLimitBagListener {
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private UserCityService userCityService;
    @Autowired
    private M2cService m2cService;
    @Autowired
    private MallService mallService;


    @Async
    @Order(1000)
    @EventListener
    public void addCard(UserCardAddEvent event) {
        EPCardAdd ep = event.getEP();
        List<RDCommon.RDCardInfo> cards = ep.getRd().getCards();
        if (!ListUtil.isNotEmpty(cards)) {
            return;
        }

        //浅拷贝，极大减少java.util.ConcurrentModificationException: null
        List<RDCommon.RDCardInfo> cardInfos = new ArrayList<>();
        for (int i = 0; i < cards.size(); i++) {
            cardInfos.add(cards.get(i));
        }
        int star4Num = 0;
        int star5Num = 0;
        for (int i = 0; i < cardInfos.size(); i++) {
            CfgCardEntity card = CardTool.getCardById(cardInfos.get(i).getCard());
            if (card.getStar() == 5) {
                star5Num++;
            } else if (card.getStar() == 4) {
                star4Num++;
            }
        }
        if (star4Num == 0 && star5Num == 0) {
            return;
        }
        UserLimitBagCondition limitBagCondition = getConditionStatistic(ep.getGuId());
        limitBagCondition.addCardNum(4, star4Num);
        limitBagCondition.addCardNum(5, star5Num);
        gameUserService.updateItem(limitBagCondition);
        int num = limitBagCondition.getCardStar4Num() + limitBagCondition.getCardStar5Num();
        //一次性最大获得10张
        if (num >= 5 && num <= 15) {
            addRecord(ep.getGuId(), 185003, true, 7200);
        }
        //玩家几乎不可能同时获得5张五星卡
        if (limitBagCondition.getCardStar5Num() >= 1 && limitBagCondition.getCardStar5Num() <= 5) {
            addRecord(ep.getGuId(), 185004, true, 7200);
        }

    }

    @Order(2)
    @EventListener
    public void addUserCity(UserCityAddEvent event) {
        EventParam<EPCityAdd> ep = (EventParam<EPCityAdd>) event.getSource();
        long uid = ep.getGuId();
        CfgCityEntity city = CityTool.getCityById(ep.getValue().getCityId());
        if (city.getLevel() == 4) {
            int ownCityNumAsLevel = userCityService.getOwnCityNumAsLevel(uid, 4);
            if (ownCityNumAsLevel == 2) {
                addRecord(uid, 185001, true, 7200);
            }
        }
        List<UserCity> ownCitiesByCountry = userCityService.getOwnCitiesByCountry(uid, city.getCountry());
//        System.out.println("该区域已攻下次数：" + ownCitiesByCountry.size());
        if (ListUtil.isNotEmpty(ownCitiesByCountry) && ownCitiesByCountry.size() == 16) {
            addRecord(uid, 185009 + (city.getCountry() / 10 - 1), true, 7200);
        }

    }

    @Async
    @Order(1000)
    @EventListener
    public void deductTreasure(TreasureDeductEvent event) {
        EPTreasureDeduct ep = event.getEP();
        long uid = ep.getGuId();
        EVTreasure deductTreasure = ep.getDeductTreasure();
        int treasureId = deductTreasure.getId();
        if (treasureId != TreasureEnum.TongTCJ.getValue() && treasureId != TreasureEnum.XZY.getValue()) {
            return;
        }
        UserLimitBagCondition limitBagCondition = getConditionStatistic(uid);
        limitBagCondition.addTreasureNum(deductTreasure.getId(), deductTreasure.getNum());
        int consumeNum = limitBagCondition.gainConsumeNum(deductTreasure.getId());

        if (treasureId == TreasureEnum.TongTCJ.getValue()) {
            int prob = 0;
            int minConsume = 1000;
            if (consumeNum >= minConsume) {
                prob = 10 + (consumeNum - minConsume) / 10;
                prob = Math.min(prob, 100);
            }
            int random = PowerRandom.getRandomBySeed(100);
            if (random <= prob) {
                boolean isAdded = addRecord(uid, 185002, false, 7200);
                if (isAdded) {
                    limitBagCondition.resetConsumeNum(treasureId);
                }
            }
        } else if (treasureId == TreasureEnum.XZY.getValue()) {
            if (consumeNum >= 100 && consumeNum <= 110) {
                addRecord(uid, 185007, true, 7200);
            }
            if (consumeNum >= 300 && consumeNum <= 310) {
                addRecord(uid, 185008, true, 7200);
            }
        }
        gameUserService.updateItem(limitBagCondition);


    }

    @Async
    @Order(1000)
    @EventListener
    public void realized(BiyouRealizedEvent event) {
        EPBiyouRealized ep = event.getEP();
        if (ep.getChapter() != Chapter.Five.getValue()
                && ep.getChapter() == Chapter.SB1.getValue()
                && ep.getChapter() == Chapter.SB2.getValue()) {
            return;
        }
        int prob = 20;
        if (PowerRandom.getRandomBySeed(100) > prob) {
            return;
        }
        long uid = ep.getGuId();
        addRecord(uid, 185005, false, 28800);
    }

    @Async
    @Order(1000)
    @EventListener
    public void secretBiographyUnlock(SecretBiographyUnlockEvent event) {
        EPSecretBiographyUnlock ep = event.getEP();
        UserLimitBagCondition limitBagCondition = getConditionStatistic(ep.getGuId());
        limitBagCondition.addSecretBiographyNum();
        gameUserService.updateItem(limitBagCondition);
        int prob = 20;
        if (limitBagCondition.getSbUnlockNum() == 1) {
            prob = 100;
        }
        if (PowerRandom.getRandomBySeed(100) > prob) {
            return;
        }
        long uid = ep.getGuId();
        addRecord(uid, 185006, false, 28800);
    }

    private boolean addRecord(long uid, int mallId, boolean isOnce, int seconds) {
        List<UserMallRecord> umrs = mallService.getRecords(uid);
        umrs = umrs.stream().filter(tmp -> tmp.getBaseId() == mallId).collect(Collectors.toList());
        if (umrs.size() > 0) {
            //一次性的不在触发
            if (isOnce) {
                return false;
            }
            // 可重复触发，已经触发过了看当前是否生效中
            umrs = umrs.stream().filter(tmp -> tmp.ifValid()).collect(Collectors.toList());
            if (umrs.size() > 0) {
                Optional<UserMallRecord> option = umrs.stream().filter(tmp -> tmp.getStatus() != null && tmp.getStatus() != RechargeStatusEnum.DONE.getStatus()).findFirst();
                if (option.isPresent()) {
                    return false;
                }
            }
        }
        UserMallRecord umr = UserMallRecord.instanceTimeLimitRecord(uid, MallEnum.ROLE_TIME_LIMIT_BAG.getValue(), mallId, seconds);
        mallService.addRecord(umr);
        //通知客户端触发
        m2cService.sendDynamicMenu(uid, DynamicMenuEnum.ROLE_TIME_LIMIT_BAG, 1);
        return true;
    }

    private UserLimitBagCondition getConditionStatistic(long uid) {
        UserLimitBagCondition limitBagCondition = gameUserService.getSingleItem(uid, UserLimitBagCondition.class);
        if (null == limitBagCondition) {
            limitBagCondition = UserLimitBagCondition.instance(uid);
            gameUserService.addItem(uid, limitBagCondition);
        }
        return limitBagCondition;

    }
}
