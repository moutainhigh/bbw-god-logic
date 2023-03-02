package com.bbw.god.activity.holiday.processor;

import com.bbw.common.PowerRandom;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.activity.IActivity;
import com.bbw.god.activity.cfg.CfgThanksGiving;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.activity.processor.AbstractActivityProcessor;
import com.bbw.god.city.chengc.RDTradeInfo;
import com.bbw.god.city.chengc.trade.BuyGoodInfo;
import com.bbw.god.city.chengc.trade.IChengChiTradeService;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.city.CfgCityEntity;
import com.bbw.god.game.config.city.RoadTool;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.treasure.TreasureChecker;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.rd.RDCommon;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 感恩节活动（烹饪美食）
 *
 * @author fzj
 * @date 2021/11/16 15:17
 */
@Service
public class HolidayCookingFoodProcessor extends AbstractActivityProcessor implements IChengChiTradeService {
    /** 城市属性对应的产出食材 */
    public static List<Integer> INGREDIENTS = Arrays.asList(50140, 50141, 50142, 50143, 50144);
    /** 野怪所在城区对应的产出调料 */
    private static final List<Integer> SEASONING = Arrays.asList(50145, 50146, 50147, 50148, 50149);
    /** 购买天灯材料需要的铜钱 */
    private static final Integer BUY_NEED_COPPER = 5000;

    public HolidayCookingFoodProcessor() {
        this.activityTypeList = Arrays.asList(ActivityEnum.COOKING_FOOD);
    }

    /**
     * 是否在ui中展示
     *
     * @return
     */
    @Override
    public boolean isShowInUi(long uid) {
        return true;
    }

    @Override
    protected long getRemainTime(long uid, int sid, IActivity a) {
        if (a.gainEnd() != null) {
            return a.gainEnd().getTime() - System.currentTimeMillis();
        }
        return NO_TIME;
    }

    @Override
    public List<Integer> getAbleTradeGoodIds() {
        return INGREDIENTS;
    }

    @Override
    public int getTradeBuyPrice(int goodId) {
        return BUY_NEED_COPPER;
    }

    @Override
    public List<BuyGoodInfo> getTradeBuyInfo(long uid, List<Integer> specialIds) {
        if (!isOpened(gameUserService.getActiveSid(uid))) {
            return new ArrayList<>();
        }
        return IChengChiTradeService.super.getTradeBuyInfo(uid, specialIds);
    }

    /**
     * 是否在活动期间
     *
     * @param sid
     * @return
     */
    private boolean isOpened(int sid) {
        ActivityEnum activityEnum = ActivityEnum.fromValue(ActivityEnum.COOKING_FOOD.getValue());
        IActivity a = this.activityService.getActivity(sid, activityEnum);
        return a != null;
    }

    /**
     * 获取城区对应的食材
     *
     * @param gu
     * @param city
     * @return
     */
    public List<RDTradeInfo.RDCitySpecial> getIngredients(GameUser gu, CfgCityEntity city) {
        List<RDTradeInfo.RDCitySpecial> citySpecialList = new ArrayList<>();
        if (!isOpened(gu.getServerId())) {
            return citySpecialList;
        }
        //根据城市属性获取产出的食材
        citySpecialList.add(0, new RDTradeInfo.RDCitySpecial(INGREDIENTS.get(city.getCountry() / 10 - 1), 0, 0));
        return citySpecialList;
    }

    /**
     * 获得对应城区的调料
     *
     * @param gu
     * @return
     */
    public void getSeasoning(GameUser gu, RDCommon rd) {
        boolean isGainSeasoning = PowerRandom.hitProbability(60, 100);
        if (!isOpened(gu.getServerId()) || !isGainSeasoning) {
            return;
        }
        int position = gu.getLocation().getPosition();
        int country = RoadTool.getRoadById(position).getCountry() / 10 - 1;
        TreasureEventPublisher.pubTAddEvent(gu.getId(), SEASONING.get(country), 1, WayEnum.WAN_S_ACTIVITY_BOX, rd);
    }

    /**
     * 使用食材获取食物
     *
     * @param uid
     * @param ingredientId 食材id
     * @param seasoningId  调料id
     * @param num          烹饪数量
     * @return
     */
    public RDCommon cookingFoods(long uid, Integer ingredientId, Integer seasoningId, Integer num) {
        if (!isOpened(gameUserService.getActiveSid(uid))) {
            throw new ExceptionForClientTip("activity.not.exist");
        }
        //检查食材和调料
        TreasureChecker.checkIsEnough(ingredientId, num, uid);
        TreasureChecker.checkIsEnough(seasoningId, num, uid);
        RDCommon rd = new RDCommon();
        //扣除食物和调料
        TreasureEventPublisher.pubTDeductEvent(uid, ingredientId, num, WayEnum.THANKSGIVING_DAY, rd);
        TreasureEventPublisher.pubTDeductEvent(uid, seasoningId, num, WayEnum.THANKSGIVING_DAY, rd);
        //根据材料获取对应食物
        List<Integer> ingredientAndSeasoning = Arrays.asList(ingredientId, seasoningId);
        Integer foodId = getFoodId(ingredientAndSeasoning);
        if (null == foodId) {
            return rd;
        }
        //发放食物道具
        TreasureEventPublisher.pubTAddEvent(uid, foodId, num, WayEnum.THANKSGIVING_DAY, rd);
        return rd;
    }

    /**
     * 根据调料和食材获得食物id
     *
     * @param ingredientAndSeasoning
     * @return
     */
    public Integer getFoodId(List<Integer> ingredientAndSeasoning) {
        Map<Integer, List<List<Integer>>> seasoningCookFoods = CfgThanksGiving.getThanksGivingInfo().getSeasoningCookFoods();
        for (Map.Entry<Integer, List<List<Integer>>> entry : seasoningCookFoods.entrySet()) {
            if (!entry.getValue().contains(ingredientAndSeasoning)) {
                continue;
            }
            return entry.getKey();
        }
        return null;
    }



}
