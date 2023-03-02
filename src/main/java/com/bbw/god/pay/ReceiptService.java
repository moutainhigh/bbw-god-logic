package com.bbw.god.pay;

import com.bbw.common.DateUtil;
import com.bbw.common.ListUtil;
import com.bbw.god.activity.ActivityService;
import com.bbw.god.activity.IActivity;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.game.config.CfgProductGroup.CfgProduct;
import com.bbw.god.game.config.mall.CfgMallEntity;
import com.bbw.god.game.config.mall.MallTool;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.pay.UserPayInfo;
import com.bbw.god.gameuser.pay.UserPayInfoService;
import com.bbw.god.gameuser.pay.UserReceipt;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ReceiptService {
    @Autowired
    private GameUserService userService;
    @Autowired
    private ActivityService activityService;
    @Autowired
    private UserPayInfoService userPayInfoService;

    public static final Date FIRST_RECHARGE_RESET_TIME = DateUtil.fromDateTimeString("2020-11-03 15:10:00");

    /**
     * 是否首充。需要玩家的原始区服ID。
     *
     * @param uid
     * @return
     */
    public boolean isFirstBought(long uid) {
        UserPayInfo userPayInfo = userPayInfoService.getUserPayInfo(uid);
        // 是否首充
        if (!userPayInfo.isFirstBought()) {
            return false;
        }
        List<UserReceipt> receipts = this.userService.getMultiItems(uid, UserReceipt.class);
        // 存在不是直冲产品的充值记录
        // Optional<UserReceipt> chongzhi = receipts.stream().filter(r -> !r.isZhiChong()).findAny();
        boolean b = ListUtil.isNotEmpty(receipts);
        if (!b) {
            userPayInfo.setFirstBought(false);
            userService.updateItem(userPayInfo);
        }
        return b;
    }

    /**
     * 获取是否今日首冲元宝订单(非直冲)
     *
     * @param uid
     * @return
     */
    public boolean isTodayFirstGoldPackReceipt(long uid) {
        List<UserReceipt> todayReceipts = getTodayReceipts(uid);
        if (todayReceipts.isEmpty()) {
            return false;
        }
        // 是否今日首冲
        List<CfgMallEntity> fMalls = MallTool.getMallConfig().getGoldRechargeMalls();
        List<Integer> goldPackIds = fMalls.stream().map(CfgMallEntity::getGoodsId).collect(Collectors.toList());
        return todayReceipts.stream().filter(p->goldPackIds.contains(p.getProductId())).count() == 1;
    }

    /**
     * 获取是否今日首冲钻石订单(非直冲),首冲双倍不计入首冲
     *
     * @param uid
     * @return
     */
    public boolean isTodayFirstDiamondPackReceipt(long uid) {
        List<UserReceipt> todayReceipts = getTodayReceipts(uid);
        if (todayReceipts.isEmpty()) {
            return false;
        }
        //获得最新订单
        UserReceipt lastReceipt = todayReceipts.stream().max(Comparator.comparing(UserReceipt::getDeliveryTime)).orElse(null);
        if (!lastReceipt.ifPayForDiamond()) {
            return false;
        }
        todayReceipts = todayReceipts.stream().filter(UserReceipt::ifPayForDiamond).collect(Collectors.toList());
        //非首冲
        if (todayReceipts.size() != 1) {
            return false;
        }
        return true;
    }

    /**
     * 今日充值记录
     *
     * @param uid
     * @return
     */
    private List<UserReceipt> getTodayReceipts(long uid) {
        List<UserReceipt> receipts = this.userService.getMultiItems(uid, UserReceipt.class);
        if (receipts.isEmpty()) {
            return receipts;
        }
        // 今日充值记录
        return receipts.stream().filter(receipt -> DateUtil.isToday(receipt.getDeliveryTime())).collect(Collectors.toList());
    }

    /**
     * 返回首购重置后的购买记录，需要玩家的原始区服ID。
     *
     * @param uid
     * @param sid
     * @return
     */
    public List<UserReceipt> getValidUserReceipt(long uid, int sid) {
        ResetDate reset = this.getRestDate(sid);
        Date now = DateUtil.now();
        List<UserReceipt> receipts = this.userService.getMultiItems(uid, UserReceipt.class);
        // 如果当前时间在最近一次首购重置时间之后，
        if (now.after(reset.getThisResetDate())) {
            List<UserReceipt> buy = receipts.stream().filter(r -> this.matchAfterRest(r, reset.getThisResetDate()))
                    .collect(Collectors.toList());
            return buy;
        }
        if (now.before(reset.getThisResetDate())) {
            List<UserReceipt> buy = receipts.stream().filter(r -> this.matchAfterAndBeforeRest(r, reset))
                    .collect(Collectors.toList());
            return buy;
        }
        return receipts;
    }

    public int getProductBuyTimes(long uid, int pId) {
        List<UserReceipt> receipts = this.userService.getMultiItems(uid, UserReceipt.class);
        List<UserReceipt> buy = receipts.stream().filter(r -> r.getProductId() == pId)
                .collect(Collectors.toList());
        if (ListUtil.isNotEmpty(buy)) {
            return buy.size();
        }
        return 0;
    }

    // 首购时间重置
    private boolean matchAfterRest(UserReceipt r, Date after) {
        return r.getDeliveryTime().after(after);
    }

    private boolean matchAfterAndBeforeRest(UserReceipt r, ResetDate reset) {
        return r.getDeliveryTime().before(reset.getLastResetDate()) && r.getDeliveryTime().after(reset.getThisResetDate());
    }

    /**
     * 速战卡是否已经购买，需要玩家的原始区服ID。
     *
     * @param uid
     * @return
     */
    public boolean suzhanHasBuy(long uid) {
        UserPayInfo userPayInfo = userPayInfoService.getUserPayInfo(uid);
        if (null != userPayInfo.getEndFightBuyTime()) {
            return true;
        }
        List<UserReceipt> receipts = this.userService.getMultiItems(uid, UserReceipt.class);
        // 速战卡
        Optional<UserReceipt> suzhanka = receipts.stream().filter(r -> r.getProductId() == CfgProduct.SUZHANKA_ID)
                .findAny();
        boolean b = suzhanka.isPresent();
        if (b) {
            userPayInfo.setEndFightBuyTime(suzhanka.get().getDeliveryTime());
            userService.updateItem(userPayInfo);
        }
        return b;
    }

    /**
     * 获取首购重置时间
     *
     * @return
     */
    public ResetDate getRestDate(int sid) {
        Date lastResetDate = DateUtil.fromDateInt(20190629);
        Date thisResetDate = DateUtil.fromDateInt(20190629);
        List<IActivity> activities = this.activityService.getActivitiesIncludeHistory(sid, ActivityEnum.RESET_FIRST_DOUBLE_R);
        if (ListUtil.isNotEmpty(activities)) {
            int size = activities.size();
            thisResetDate = activities.get(size - 1).gainBegin();
            if (size >= 2) {
                lastResetDate = activities.get(size - 2).gainBegin();
            }
        }
        ResetDate reset = new ResetDate();
        reset.setLastResetDate(lastResetDate);
        reset.setThisResetDate(thisResetDate);
        return reset;
    }

    public List<UserReceipt> getAllReceipts(long uid) {
        List<UserReceipt> receipts = this.userService.getMultiItems(uid, UserReceipt.class);
        return receipts;
    }

    /**
     * 获得某个时间后的充值记录
     *
     * @param uid
     * @param productId
     * @param since
     * @return
     */
    public List<UserReceipt> getReceiptsSinceDate(long uid, int productId, Date since) {
        List<UserReceipt> receipts = this.userService.getMultiItems(uid, UserReceipt.class);
        receipts = receipts.stream()
                .filter(tmp -> tmp.getProductId() == productId && tmp.getDeliveryTime().after(since))
                .collect(Collectors.toList());
        return receipts;
    }
}
