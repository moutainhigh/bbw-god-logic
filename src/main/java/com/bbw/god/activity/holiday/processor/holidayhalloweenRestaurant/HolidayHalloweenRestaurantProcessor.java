package com.bbw.god.activity.holiday.processor.holidayhalloweenRestaurant;

import com.bbw.common.CloneUtil;
import com.bbw.common.ListUtil;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.activity.IActivity;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.activity.holiday.config.HolidayHalloweenRestaurantOrder;
import com.bbw.god.activity.processor.AbstractActivityProcessor;
import com.bbw.god.activity.rd.RDActivityList;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.mall.CfgMallEntity;
import com.bbw.god.game.config.mall.MallTool;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.treasure.TreasureChecker;
import com.bbw.god.gameuser.treasure.UserTreasure;
import com.bbw.god.gameuser.treasure.UserTreasureService;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.mall.MallLogic;
import com.bbw.god.mall.RDMallList;
import com.bbw.god.mall.processor.HolidayHalloweenRestaurantRedemptionMallProcessor;
import com.bbw.god.rd.RDCommon;
import com.bbw.god.rd.RDSuccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 万圣餐厅
 *
 * @author: huanghb
 * @date: 2022/10/10 17:06
 */
@Service
public class HolidayHalloweenRestaurantProcessor extends AbstractActivityProcessor {
    @Autowired
    private HolidayHalloweenRestaurantDateService holidayHalloweenRestaurantDateService;
    @Autowired
    private HolidayHalloweenRestaurantRedemptionMallProcessor holidayHalloweenRestaurantRedemptionMallProcessor;
    @Autowired
    private MallLogic mallLogic;
    @Autowired
    private UserTreasureService userTreasureService;

    public HolidayHalloweenRestaurantProcessor() {
        this.activityTypeList = Arrays.asList(ActivityEnum.HALLOWEEN_RESTAURANT);
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

    /**
     * 是否在活动期间
     *
     * @param sid
     * @return
     */
    public boolean isOpened(int sid) {
        ActivityEnum activityEnum = ActivityEnum.fromValue(ActivityEnum.HALLOWEEN_RESTAURANT.getValue());
        IActivity a = this.activityService.getActivity(sid, activityEnum);
        return a != null;
    }

    /**
     * 获取活动详情
     *
     * @param uid
     * @param activityType
     * @return
     */
    @Override
    public RDSuccess getActivities(long uid, int activityType) {
        RDActivityList rd = (RDActivityList) super.getActivities(uid, activityType);
        //盘子上食物信息获得和检测
        List<Integer> plateFoodInfos = getAndcheckPlateFoodInfo(uid).values().stream().collect(Collectors.toList());
        rd.setPlateFoodInfos(plateFoodInfos);
        //获得商品
        RDMallList goods = holidayHalloweenRestaurantRedemptionMallProcessor.getGoods(uid);
        rd.setRdMallList(goods);
        /** 获得离线收益*/
        Map<Integer, String> map = holidayHalloweenRestaurantDateService.getOfflineRevenue(uid);
        int offlineRevenue = holidayHalloweenRestaurantDateService.getOfflineRevenues(uid, map.keySet().stream().collect(Collectors.toList()));
        rd.setOfflineRevenue(offlineRevenue);
        return rd;
    }

    /**
     * 获得并检查盘子食物信息
     *
     * @param uid
     * @return
     */
    private Map<Integer, Integer> getAndcheckPlateFoodInfo(long uid) {
        //获得盘子食物信息
        Map<Integer, Integer> plateFoodInfos = holidayHalloweenRestaurantDateService.getPlateFoodInfos(uid);
        List<Integer> plateFoodInfoList = plateFoodInfos.values().stream().collect(Collectors.toList());
        //获得所有食物类别信息 食物法宝id=》食物商品id
        Map<Integer, Integer> foodMallIdsMap = HolidayHalloweenRestaurantTool.getFoodMallIds();
        List<Integer> treasureIds = new ArrayList<>(foodMallIdsMap.keySet());
        List<UserTreasure> userTreasures = userTreasureService.getUserTreasures(uid, treasureIds);
        for (Map.Entry<Integer, Integer> entry : foodMallIdsMap.entrySet()) {
            int treasureId = entry.getKey();
            //获得法宝数量
            int treasureNum = 0;
            UserTreasure userTreasure = userTreasures.stream().filter(tmp -> tmp.getBaseId() == treasureId).findFirst().orElse(null);
            if (null != userTreasure) {
                treasureNum = userTreasure.gainTotalNum();
            }

            //获得食物数量
            int foodNum = (int) plateFoodInfoList.stream().filter(tmp -> tmp.intValue() == entry.getValue()).count();
            //食物法宝数量和食物商品数量相等
            if (foodNum == treasureNum) {
                continue;
            }
            //食物法宝数量小于食物商品数量
            if (foodNum > treasureNum) {
                TreasureEventPublisher.pubTAddEvent(uid, entry.getKey(), foodNum - treasureNum, WayEnum.HALLOWEEN_RESTAURANT, new RDCommon());
            }
            //食物法宝数量大于于食物商品数量
            if (foodNum < treasureNum) {
                for (int i = 0; i < treasureNum - foodNum; i++) {
                    //获得空盘子位置
                    Integer emptyPlatePos = getEmptyPlatePos(plateFoodInfos);
                    if (null == emptyPlatePos) {
                        break;
                    }
                    //把食物商品放到空盘子位置上
                    plateFoodInfos.put(emptyPlatePos, entry.getValue());
                }

            }
        }
        //更新盘子上食物位置
        holidayHalloweenRestaurantDateService.updatePlateFoodInfos(uid, plateFoodInfos);
        return plateFoodInfos.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldVal, newVal) -> newVal, LinkedHashMap::new));
    }

    /**
     * 合成
     *
     * @param uid
     * @param foodPos 合成食物的位置
     * @return
     */
    public RdCompound compound(long uid, String foodPos) {
        //参数检测
        List<Integer> platePos = ListUtil.parseStrToInts(foodPos);
        checkParam(platePos);
        //是否开启活动
        if (!isOpened(gameUserService.getActiveSid(uid))) {
            throw new ExceptionForClientTip("activity.not.exist");
        }
        //盘子是否解锁
        Map<Integer, Integer> plateFoodInfos = holidayHalloweenRestaurantDateService.getPlateFoodInfos(uid);
        for (Integer pos : platePos) {
            boolean isUnlock = isUnlock(plateFoodInfos, pos);
            if (!isUnlock) {
                throw new ExceptionForClientTip("plate.is.not.unlock");
            }
        }
        //是否同一种食物
        if (!isSameFood(uid, platePos)) {
            throw new ExceptionForClientTip("food.not.same");
        }
        //食物id
        Integer foodMallId = plateFoodInfos.get(platePos.get(0));
        Integer foodId = HolidayHalloweenRestaurantTool.getFoodIdByMallId(foodMallId);
        //食物合成需要数量
        Integer foodCompoundNeedNum = HolidayHalloweenRestaurantTool.getFoodCompoundNeedNum();
        //食物拥有数量检测
        TreasureChecker.checkIsEnough(foodId, foodCompoundNeedNum, uid);
        //下一个食物id
        Integer nextFoodId = HolidayHalloweenRestaurantTool.getNextFoodId(foodId);
        Integer nextFoodMallId = HolidayHalloweenRestaurantTool.getMallIdByFoodId(nextFoodId);
        //合成
        plateFoodInfos.put(platePos.get(0), nextFoodMallId);
        plateFoodInfos.put(platePos.get(1), 0);
        RdCompound rd = RdCompound.instance(plateFoodInfos);
        //扣除合成食物
        TreasureEventPublisher.pubTDeductEvent(uid, foodId, foodCompoundNeedNum, WayEnum.HALLOWEEN_RESTAURANT_SYNTHETIC, rd);
        //增加下一级食物
        TreasureEventPublisher.pubTAddEvent(uid, nextFoodId, 1, WayEnum.HALLOWEEN_RESTAURANT_SYNTHETIC, rd);
        //更新盘子信息
        holidayHalloweenRestaurantDateService.updatePlateFoodInfos(uid, plateFoodInfos);
        //更新离线收益信息
        if (TreasureEnum.CANDY_PUMPKIN_BUCKET.getValue() != nextFoodId) {
            return rd;
        }
        holidayHalloweenRestaurantDateService.updateOfflineRevenueInfo(uid, nextFoodMallId, platePos.get(0));
        return rd;
    }

    /**
     * 是否同一种食物
     *
     * @param uid
     * @param platePoss 盘子位置
     * @return
     */
    private Boolean isSameFood(long uid, List<Integer> platePoss) {
        Map<Integer, Integer> plateFoodInfos = holidayHalloweenRestaurantDateService.getPlateFoodInfos(uid);
        Integer compoundFoodId = null;
        for (Integer platePos : platePoss) {
            Integer foodId = plateFoodInfos.get(platePos);
            if (null == foodId) {
                return false;
            }
            if (null == compoundFoodId) {
                compoundFoodId = foodId;
                continue;
            }
            if (!compoundFoodId.equals(foodId)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 解锁盘子
     *
     * @param uid
     * @return
     */
    public RDCommon unlockPlate(long uid, Integer foodPos) {
        Map<Integer, Integer> plateFoodInfos = holidayHalloweenRestaurantDateService.getPlateFoodInfos(uid);
        boolean isUnlock = isUnlock(plateFoodInfos, foodPos);
        if (isUnlock) {
            throw new ExceptionForClientTip("plate.is.unlock");
        }
        //下一个解锁位置
        int unlockTime = (int) plateFoodInfos.values().stream().filter(tmp -> tmp >= 0).count();

        //需要消耗法宝道具数量
        Integer needTreasureNum = HolidayHalloweenRestaurantTool.getUnlockPlateNeedTreasureNum(unlockTime);
        //检查
        Integer unlockPlateNeedTreasure = HolidayHalloweenRestaurantTool.getUnlockPlateNeedTreasure();
        TreasureChecker.checkIsEnough(unlockPlateNeedTreasure, needTreasureNum, uid);

        RDCommon rd = new RDCommon();
        TreasureEventPublisher.pubTDeductEvent(uid, unlockPlateNeedTreasure, needTreasureNum, WayEnum.HALLOWEEN_RESTAURANT, rd);
        //解锁
        plateFoodInfos.put(foodPos, 0);
        holidayHalloweenRestaurantDateService.updatePlateFoodInfos(uid, plateFoodInfos);
        return rd;
    }

    /**
     * 交换食物位置
     *
     * @param uid
     * @return
     */
    public RDSuccess swapFoodPos(long uid, String foodPos) {
        //参数检测
        List<Integer> platePos = ListUtil.parseStrToInts(foodPos);
        checkParam(platePos);
        //是否开启活动
        if (!isOpened(gameUserService.getActiveSid(uid))) {
            throw new ExceptionForClientTip("activity.not.exist");
        }
        Map<Integer, Integer> plateFoodInfos = holidayHalloweenRestaurantDateService.getPlateFoodInfos(uid);
        for (Integer pos : platePos) {
            boolean isUnlock = isUnlock(plateFoodInfos, pos);
            if (!isUnlock) {
                throw new ExceptionForClientTip("plate.is.not.unlock");
            }
        }
        //食物交换位置
        int firstFood = CloneUtil.clone(plateFoodInfos.get(platePos.get(0)));
        int secondFood = CloneUtil.clone(plateFoodInfos.get(platePos.get(1)));
        plateFoodInfos.put(platePos.get(0), secondFood);
        plateFoodInfos.put(platePos.get(1), firstFood);
        //更新
        holidayHalloweenRestaurantDateService.updatePlateFoodInfos(uid, plateFoodInfos);
        RdCompound rd = new RdCompound();
        rd.setPlateFoodInfos(plateFoodInfos.values().stream().collect(Collectors.toList()));
        return rd;
    }

    /**
     * 获得订单信息
     *
     * @param uid
     * @return
     */
    public RdHalloweenRestaurantOrderInfo getOrderInfos(long uid) {
        if (!isOpened(gameUserService.getActiveSid(uid))) {
            throw new ExceptionForClientTip("activity.not.exist");
        }
        List<HolidayHalloweenRestaurantOrder> orders = holidayHalloweenRestaurantDateService.getOrders(uid);
        return RdHalloweenRestaurantOrderInfo.instance(orders);
    }

    /**
     * 接受订单
     *
     * @param uid
     * @return
     */
    public RDSuccess acceptOrder(long uid, long orderId) {
        HolidayHalloweenRestaurantOrder order = holidayHalloweenRestaurantDateService.getOrder(uid, orderId);
        if (null == order) {
            throw new ExceptionForClientTip("order.is.not.exist");
        }
        Integer numberOfOrdersAccepted = holidayHalloweenRestaurantDateService.getNumberOfOrdersAccepted(uid);
        Integer orderLimit = HolidayHalloweenRestaurantTool.getAcceptOrderLimit();
        if (orderLimit <= numberOfOrdersAccepted) {
            throw new ExceptionForClientTip("order.is.limit");
        }
        order.acceptOrder();
        holidayHalloweenRestaurantDateService.updateOrder(uid, order);
        return new RDSuccess();
    }

    /**
     * 完成订单
     *
     * @param uid
     * @param orderId
     * @param foodPos
     * @return
     */
    public RDCommon completeOrder(long uid, long orderId, String foodPos) {
        List<Integer> platePos = ListUtil.parseStrToInts(foodPos);
        //参数检测
        checkParam(platePos);
        if (!isOpened(gameUserService.getActiveSid(uid))) {
            throw new ExceptionForClientTip("activity.not.exist");
        }
        //获得订单
        HolidayHalloweenRestaurantOrder order = holidayHalloweenRestaurantDateService.getOrder(uid, orderId);
        //订单是否为空
        if (null == order) {
            throw new ExceptionForClientTip("order.is.not.exist");
        }
        //食物检测
        List<Integer> foods = order.getFoods();
        for (int i = 0; i < foods.size(); i++) {
            //相同食物检测
            List<Integer> distinctFoods=foods.stream().distinct().collect(Collectors.toList());
            if(distinctFoods.size()!=foods.size()){
                //检测只执行一次
                if(0!=i){
                    continue;
                }
                TreasureChecker.checkIsEnough( foods.get(i),foods.size(),uid);
                continue;
            }
            //不同食物检测
            TreasureChecker.checkHasTreasure(uid, foods.get(i));
        }
        //扣除食物
        RDCommon rd = new RDCommon();
        for (int i = 0; i < foods.size(); i++) {
            TreasureEventPublisher.pubTDeductEvent(uid, foods.get(i), 1, WayEnum.HALLOWEEN_RESTAURANT_SYNTHETIC, rd);
        }
        //更新盘子信息
        Map<Integer, Integer> plateFoodInfos = holidayHalloweenRestaurantDateService.getPlateFoodInfos(uid);
        for (int i = 0; i < platePos.size(); i++) {
            plateFoodInfos.put(platePos.get(i), 0);
        }
        //获得离线信息
        Integer offlineRevenue = holidayHalloweenRestaurantDateService.getOfflineRevenues(uid, platePos);
        if (0 != offlineRevenue) {
            TreasureEventPublisher.pubTAddEvent(uid, TreasureEnum.CANDY.getValue(), offlineRevenue, WayEnum.HALLOWEEN_RESTAURANT, rd);
        }
        order.completeOrder();
        TreasureEventPublisher.pubTAddEvent(uid, TreasureEnum.HALLOWEEN_PUMPKIN.getValue(), order.getPrice(), WayEnum.HALLOWEEN_RESTAURANT_SYNTHETIC, rd);
        holidayHalloweenRestaurantDateService.updatePlateFoodInfos(uid, plateFoodInfos);
        holidayHalloweenRestaurantDateService.updateOrder(uid, order);

        //更新离线信息
        holidayHalloweenRestaurantDateService.removeOfflineRevenueInfo(uid, platePos);
        return rd;
    }

    /**
     * 领取离线收益
     *
     * @param uid
     * @return
     */
    public RDCommon receiveOfflineRevenue(long uid) {
        Map<Integer, String> map = holidayHalloweenRestaurantDateService.getOfflineRevenue(uid);
        int offlineRevenue=holidayHalloweenRestaurantDateService.getOfflineRevenues(uid,map.keySet().stream().collect(Collectors.toList()));
        if (0 == offlineRevenue) {
            throw new ExceptionForClientTip("activity.awarded");
        }
        RDCommon rd = new RDCommon();
        TreasureEventPublisher.pubTAddEvent(uid, HolidayHalloweenRestaurantTool.getPlateOutPutTreasure(), offlineRevenue, WayEnum.HALLOWEEN_RESTAURANT, rd);
        //更新离线收益信息
        holidayHalloweenRestaurantDateService.updateOfflineRevenueInfo(uid, map);
        return rd;
    }

    /**
     * 卖出食物
     *
     * @param uid
     * @param mallId
     * @param buyNum
     * @param foodPos
     * @return
     */
    public RDCommon sellFood(long uid, int mallId, int buyNum, int foodPos) {
        RDCommon rd = mallLogic.buy(uid, mallId, buyNum);
        holidayHalloweenRestaurantDateService.sellFood(uid, foodPos);
        Integer offlineRevenue = holidayHalloweenRestaurantDateService.getOfflineRevenue(uid, foodPos);
        if (0 != offlineRevenue) {
            TreasureEventPublisher.pubTAddEvent(uid, TreasureEnum.CANDY.getValue(), offlineRevenue, WayEnum.HALLOWEEN_RESTAURANT, rd);
        }
        //更新离线信息
        holidayHalloweenRestaurantDateService.removeOfflineRevenueInfo(uid, foodPos);
        return rd;
    }

    /**
     * 购买食物
     *
     * @param uid
     * @param mallId
     * @param buyNum
     * @return
     */
    public RDCommon buyFood(long uid, int mallId, int buyNum) {
        //获得盘子上食物信息
        Map<Integer, Integer> plateFoodInfos = holidayHalloweenRestaurantDateService.getPlateFoodInfos(uid);
        Integer emptyPlatePos = getEmptyPlatePos(plateFoodInfos);
        if (null == emptyPlatePos) {
            throw new ExceptionForClientTip("emptyPlate.is.not.exist");
        }
        RDCommon rd = mallLogic.buy(uid, mallId, buyNum);
        //获得食物id
        CfgMallEntity cfgMallEntity = MallTool.getMall(mallId);
        int foodId = cfgMallEntity.getGoodsId();
        //获得食物商品id
        int foodMallId = HolidayHalloweenRestaurantTool.getMallIdByFoodId(foodId);
        //购买食物
        holidayHalloweenRestaurantDateService.buyFood(uid, foodMallId, emptyPlatePos);
        return rd;
    }

    /**
     * 获得空盘子位置
     *
     * @param plateFoodInfos
     * @return
     */
    private Integer getEmptyPlatePos(Map<Integer, Integer> plateFoodInfos) {
        Integer foodPos = null;
        for (Map.Entry<Integer, Integer> entry : plateFoodInfos.entrySet()) {
            if (null == entry.getValue()) {
                continue;
            }
            if (0 != entry.getValue()) {
                continue;
            }
            return entry.getKey();
        }
        return foodPos;
    }

    /**
     * 是否解锁
     *
     * @param plateFoodInfos
     * @return
     */
    private boolean isUnlock(Map<Integer, Integer> plateFoodInfos, Integer foodPos) {
        if (!plateFoodInfos.containsKey(foodPos)) {
            return false;
        }
        if (null == plateFoodInfos.get(foodPos)) {
            return false;
        }
        if (0 > plateFoodInfos.get(foodPos)) {
            return false;
        }
        return true;
    }

    /**
     * 检测参数
     *
     * @param foodPos
     */
    private void checkParam(List<Integer> foodPos) {
        //参数检测
        List<Integer> wrongParam = foodPos.stream().filter(tmp -> 0 > tmp.intValue() || 8 < tmp.intValue()).collect(Collectors.toList());
        if (ListUtil.isNotEmpty(wrongParam)) {
            throw new ExceptionForClientTip("client.request.unvalid.arg");
        }
        if (2 > foodPos.size()) {
            throw new ExceptionForClientTip("client.request.unvalid.arg");
        }
    }
}
