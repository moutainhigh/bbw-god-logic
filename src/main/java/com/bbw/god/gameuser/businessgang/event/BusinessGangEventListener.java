package com.bbw.god.gameuser.businessgang.event;

import com.bbw.common.DateUtil;
import com.bbw.god.game.config.CfgProductGroup;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.businessgang.BusinessGangCfgTool;
import com.bbw.god.gameuser.businessgang.BusinessGangService;
import com.bbw.god.gameuser.businessgang.UserBusinessGangService;
import com.bbw.god.gameuser.businessgang.cfg.CfgPrestigeEntity;
import com.bbw.god.gameuser.businessgang.user.UserBusinessGangInfo;
import com.bbw.god.gameuser.businessgang.user.UserBusinessGangTaskInfo;
import com.bbw.god.gameuser.pay.UserPayInfo;
import com.bbw.god.gameuser.pay.UserPayInfoService;
import com.bbw.god.gameuser.pay.UserReceipt;
import com.bbw.god.gameuser.treasure.event.*;
import com.bbw.god.pay.DeliverNotifyEvent;
import com.bbw.god.pay.ReceiptService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 商帮监听事件
 *
 * @author fzj
 * @date 2022/1/29 10:24
 */
@Component
@Slf4j
@Async
public class BusinessGangEventListener {
    /** 声望 */
    private final static List<Integer> PRESTIGE = BusinessGangCfgTool.getAllPrestigeEntity().stream().map(CfgPrestigeEntity::getPrestigeId).collect(Collectors.toList());
    @Autowired
    private BusinessGangService businessGangService;
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private UserBusinessGangService userBusinessGangService;
    @Autowired
    private UserPayInfoService userPayInfoService;
    @Autowired
    private ReceiptService receiptService;

    /**
     * 声望增加监听事件
     *
     * @param event
     */
    @Order(1000)
    @EventListener
    public void addPrestige(TreasureAddEvent event) {
        EPTreasureAdd ep = event.getEP();
        Long uid = ep.getGuId();
        List<EVTreasure> addTreasures = ep.getAddTreasures();
        //判断是否添加声望
        List<EVTreasure> prestige = addTreasures.stream().filter(t -> PRESTIGE.contains(t.getId())).collect(Collectors.toList());
        if (prestige.isEmpty()) {
            return;
        }
        //添加声望
        for (EVTreasure treasure : prestige) {
            businessGangService.addPrestige(uid, treasure.getId(), treasure.getNum());
        }
    }

    /**
     * 声望扣除监听事件
     *
     * @param event
     */
    @Order(1000)
    @EventListener
    public void deductPrestige(TreasureDeductEvent event) {
        EPTreasureDeduct ep = event.getEP();
        Long uid = ep.getGuId();
        EVTreasure deductTreasure = ep.getDeductTreasure();
        Integer prestigeId = deductTreasure.getId();
        //判断是否扣除声望
        if (!PRESTIGE.contains(prestigeId)) {
            return;
        }
        CfgPrestigeEntity prestigeEntity = BusinessGangCfgTool.getAllPrestigeEntity()
                .stream().filter(p -> prestigeId.equals(p.getPrestigeId())).findFirst().orElse(null);
        if (null == prestigeEntity) {
            return;
        }
        //扣减声望
        Integer gangId = BusinessGangCfgTool.getBusinessGangData(prestigeEntity.getBusinessGangId()).getBusinessGangId();
        UserBusinessGangInfo businessGang = userBusinessGangService.getOrCreateUserBusinessGang(uid);
        businessGang.deductPrestige(gangId, deductTreasure.getNum());
        gameUserService.updateItem(businessGang);
    }
}
