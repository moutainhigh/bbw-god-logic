package com.bbw.god.mall.event;

import com.bbw.god.activity.ActivityService;
import com.bbw.god.activity.UserActivity;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.activity.config.ActivityStatusEnum;
import com.bbw.god.activity.config.ActivityTool;
import com.bbw.god.db.entity.CfgActivityEntity;
import com.bbw.god.event.EventParam;
import com.bbw.god.game.config.CfgProductGroup.CfgProduct;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.mall.CfgMallEntity;
import com.bbw.god.game.config.mall.FavorableBagEnum;
import com.bbw.god.game.config.mall.MallTool;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.level.EPGuLevelUp;
import com.bbw.god.gameuser.level.GuLevelUpEvent;
import com.bbw.god.gameuser.pay.UserReceipt;
import com.bbw.god.mall.MallService;
import com.bbw.god.mall.UserMallRecord;
import com.bbw.god.pay.DeliverNotifyEvent;
import com.bbw.god.pay.ProductService;
import com.bbw.god.rd.RDCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
public class MallListener {
    @Autowired
    private MallService mallService;
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private ProductService productService;
    @Autowired
    private ActivityService activityService;

    // @EventListener
    // @Order(1000)
    public void activeZLLB(GuLevelUpEvent event) {
        EPGuLevelUp ep = event.getEP();
        long guId = ep.getGuId();
        int guLevel = ep.getNewLevel();
        RDCommon rd = ep.getRd();
        if (guLevel == 10) {
            mallService.addZLRecord(guId, 1281, rd);
        } else if (guLevel == 15) {
            mallService.addZLRecord(guId, 1282, rd);
        } else if (guLevel == 19) {
            mallService.addZLRecord(guId, 1283, rd);
        }
    }

    // @EventListener
    public void activeZLLB(ZLActiveEvent event) {
        EventParam<Integer> ep = (EventParam<Integer>) event.getSource();
        long guId = ep.getGuId();
        WayEnum way = ep.getWay();
        RDCommon rd = ep.getRd();
        GameUser gu = gameUserService.getGameUser(guId);
        if (gu.getLevel() < 10 && (way == WayEnum.FIGHT_ATTACK || way == WayEnum.FIGHT_TRAINING || way == WayEnum.FIGHT_PROMOTE || way == WayEnum.FIGHT_YG || way == WayEnum.FIGHT_HELP_YG)) {
            mallService.addZLRecord(guId, 1281, rd);
        }
    }

    /**
     * 产品发放通知事件，处理商城等直冲的购买记录
     *
     * @param event
     */
    @EventListener
    @Order(1000)
    public void deliverNotify(DeliverNotifyEvent event) {
        UserReceipt userReceipt = event.getParam();
        long guId = userReceipt.getGameUserId();
        CfgProduct product = productService.getCfgProduct(userReceipt.getProductId());
        // 处理直冲相关的购买记录
        if (product.getIsZhiChong()) {
            int goodsId = MallTool.getGoodsId(product.getId());
            if (goodsId < 0) {
                return;
            }
            FavorableBagEnum favorableBag = FavorableBagEnum.fromValue(goodsId);
            CfgMallEntity mall = MallTool.getMall(favorableBag.getType(), goodsId);
            int mallId = mall.getId();
            // 处理购买纪录
            UserMallRecord umRecord = mallService.getUserMallRecord(guId, mallId);
            if (umRecord == null) {
                umRecord = UserMallRecord.instance(guId, mallId, mall.getType(), 1);
                mallService.addRecord(umRecord);
            } else {
                umRecord.addNum(1);
                gameUserService.updateItem(umRecord);
            }
        }

    }

    @EventListener
    @Order(1000)
    public void deliverMXLB(DeliverNotifyEvent event) {
        UserReceipt userReceipt = event.getParam();
        CfgProduct product = productService.getCfgProduct(userReceipt.getProductId());
        // 萌新礼包
        if (99001211 == product.getId() || 99001210 == product.getId()) {
            long guId = userReceipt.getGameUserId();
            CfgActivityEntity ca = ActivityTool.getActivitiesByType(ActivityEnum.NEWER_PACKAGE).stream()
                    .filter(a -> a.getId().equals(10056)).findFirst().orElse(null);
            int sid = gameUserService.getActiveSid(guId);
            Long aId = activityService.getActivity(sid, ActivityEnum.NEWER_PACKAGE).gainId();
            UserActivity userActivity = activityService.getUserActivity(guId, aId, ca.getId());
            if (null == userActivity) {
                userActivity = UserActivity.fromActivity(guId, aId, 0, ca);
                gameUserService.addItem(guId, userActivity);
            }
            userActivity.setStatus(ActivityStatusEnum.AWARDED0.getValue());
            gameUserService.updateItem(userActivity);
        }
    }
}
