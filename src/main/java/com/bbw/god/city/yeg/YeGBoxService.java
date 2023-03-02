package com.bbw.god.city.yeg;

import com.bbw.common.ListUtil;
import com.bbw.common.PowerRandom;
import com.bbw.god.activity.ActivityService;
import com.bbw.god.activity.holiday.processor.holidaycutetugermarket.HolidayCuteTigerMarketProcessor;
import com.bbw.god.activity.holiday.processor.holidayspecialcity.HolidayKoiPrayProcessor;
import com.bbw.god.activity.holiday.processor.holidayspecialcity.HolidayLaborGloriousProcessor;
import com.bbw.god.activity.holiday.processor.holidayspecialcity.HolidaySkyLanternWorkShopProcessor;
import com.bbw.god.activity.holiday.processor.holidayspecialcity.HolidayWZJZProcessor;
import com.bbw.god.cache.TimeLimitCacheUtil;
import com.bbw.god.city.yeg.event.EPOpenYeGuaiBox;
import com.bbw.god.city.yeg.event.YeGuaiEventPublisher;
import com.bbw.god.event.BaseEventParam;
import com.bbw.god.fight.processor.YeGFightProcessor;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.game.award.AwardService;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.card.CardTool;
import com.bbw.god.game.config.card.CfgCardEntity;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.card.UserCardRandomService;
import com.bbw.god.gameuser.card.UserCardService;
import com.bbw.god.gameuser.res.ResEventPublisher;
import com.bbw.god.gameuser.res.ResWayType;
import com.bbw.god.gameuser.res.copper.EPCopperAdd;
import com.bbw.god.random.box.AbstractBoxService;
import com.bbw.god.random.box.BoxGood;
import com.bbw.god.random.service.RandomParam;
import com.bbw.god.rd.RDCommon;
import com.bbw.god.server.god.GodService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 野怪宝箱事件
 *
 * @author: huanghb
 * @date: 2021/12/7 22:33
 */
@Slf4j
@Service
public class YeGBoxService extends AbstractBoxService {
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private UserCardService userCardService;
    @Autowired
    private AwardService awardService;
    @Autowired
    private YeGFightProcessor yeGFightProcessor;
    @Autowired
    private ActivityService activityService;
    @Autowired
    private HolidaySkyLanternWorkShopProcessor holidaySkyLanternWorkShopProcessor;
    @Autowired
    private GodService godService;
    @Autowired
    private UserCardRandomService userCardRandomService;
    @Autowired
    private HolidayCuteTigerMarketProcessor holidayCuteTigerMarketProcessor;
    @Autowired
    private HolidayWZJZProcessor holidayWZJZProcessor;
    @Autowired
    private HolidayKoiPrayProcessor holidayKoiPrayProcessor;
    @Autowired
    private HolidayLaborGloriousProcessor holidayLaborGloriousProcessor;

    /**
     * 开野怪宝箱
     *
     * @param guId
     * @param boxKey
     * @param copperAddRate
     * @param way
     * @param rd
     */
    public void openYeGuaiBox(long guId, YeGuaiEnum yeGuaiEnum, String boxKey, List<Integer> awardedTypes, double copperAddRate, WayEnum way, boolean isBusinessGang, RDCommon rd) {
        List<Award> awards = null;
        int awardType = 0;
        do {
            awards = getAward(guId, boxKey);
            if (awards.isEmpty()) {
                log.error("野怪宝箱" + boxKey + "不存在");
                return;
            }
            awardType = awards.get(0).getItem();
        } while (!isBusinessGang && awardType == AwardEnum.TQ.getValue() && awardedTypes.contains(awardType));
        awardedTypes.add(awardType);

        if (YeGuaiEnum.YG_ELITE == yeGuaiEnum) {
            TimeLimitCacheUtil.setYeGuaiBoxCache(guId, awards);
        }
        List<Award> openAwards = new ArrayList<>();
        if (!awards.isEmpty()) {
            for (Award award : awards) {
                if (award.getItem() == AwardEnum.TQ.getValue()) {
                    //铜钱加成
                    int addCopper = award.getNum();
                    GameUser gu = gameUserService.getGameUser(guId);
                    addCopper *= (1 + yeGFightProcessor.getBaseCopperBuf(gu));
                    EPCopperAdd copperAdd = new EPCopperAdd(new BaseEventParam(guId, way, rd), addCopper, addCopper);
                    int extraCopper = (int) (addCopper * copperAddRate);
                    copperAdd.addCopper(ResWayType.Extra, extraCopper);
                    ResEventPublisher.pubCopperAddEvent(copperAdd);
                    continue;
                }
                openAwards.add(award);
            }
        }
        //野怪宝箱60%额外获得天宫材料包
        if (PowerRandom.hitProbability(60)) {
            Award award = holidaySkyLanternWorkShopProcessor.getExtraAwardByYGBox(guId);
            if (null != award) {
                openAwards.add(award);
            }
        }
        //萌虎集市野怪额外产出
        List<Award> cuteTigerAward = holidayCuteTigerMarketProcessor.yeGuaiBoxExtraAwards(guId);
        if (!cuteTigerAward.isEmpty()) {
            openAwards.addAll(cuteTigerAward);
        }
        //暑气来袭野怪额外产出
        List<Award> summerHeatAward = holidayWZJZProcessor.yeGuaiBoxExtraAwards(guId);
        if (!summerHeatAward.isEmpty()) {
            openAwards.addAll(summerHeatAward);
        }
        //锦鲤祈愿野怪额外产出
        List<Award> koiPrayAward = holidayKoiPrayProcessor.yeGuaiBoxExtraAwards(guId);
        if (ListUtil.isNotEmpty(koiPrayAward)) {
            openAwards.addAll(koiPrayAward);
        }
        //劳动光荣野怪额外产出
        List<Award> laborGloriousAward = holidayLaborGloriousProcessor.yeGuaiBoxExtraAwards(guId, yeGuaiEnum.getType());
        if (ListUtil.isNotEmpty(laborGloriousAward)) {
            openAwards.addAll(laborGloriousAward);
        }

        BaseEventParam bep = new BaseEventParam(guId, way, rd);
        YeGuaiEventPublisher.pubOpenYeGuaiBoxEvent(new EPOpenYeGuaiBox(yeGuaiEnum, bep));
        if (openAwards.isEmpty()) {
            return;
        }
        awardService.fetchAward(guId, openAwards, way, "", rd);
    }

    @Override
    public List<Award> toCardAwards(long uid, BoxGood cardGood) {
        RandomParam randomParams = new RandomParam();
        randomParams.setExtraCardsToMap(userCardService.getUserCards(uid));
        // 神仙
        GameUser gu = gameUserService.getGameUser(uid);
        int cardDropRate = this.godService.getCardDropRate(gu);

        String strategyKey = cardGood.getGood();
        Optional<CfgCardEntity> card = userCardRandomService.getRandomCard(uid, strategyKey, randomParams, cardDropRate);
        CfgCardEntity cardEntity = null;
        if (card.isPresent()) {
            cardEntity = card.get();
        } else {
            cardEntity = CardTool.getRandomNotSpecialCard(1);
        }
        List<Award> awards = new ArrayList<>();
        awards.add(new Award(cardEntity.getId(), AwardEnum.KP, 1));
        return awards;
    }
}
