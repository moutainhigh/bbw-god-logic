package com.bbw.god.gameuser.chamberofcommerce.server;

import com.bbw.god.fight.FightTypeEnum;
import com.bbw.god.game.combat.event.CombatFightWinEvent;
import com.bbw.god.game.combat.event.EPFightEnd;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.chamberofcommerce.CocConstant;
import com.bbw.god.gameuser.res.copper.CopperAddEvent;
import com.bbw.god.gameuser.res.copper.EPCopperAdd;
import com.bbw.god.gameuser.special.event.EPSpecialDeduct;
import com.bbw.god.gameuser.special.event.SpecialDeductEvent;
import com.bbw.god.gameuser.treasure.event.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;

import java.util.List;

/**
 * 商会 任务进度监听器
 *
 * @author lwb
 * @version 1.0
 * @date 2019年4月17日
 */
@Deprecated
//@Component
public class CocTaskListener {
    @Autowired
    private GameUserService gameUserService;

    @Autowired
    private UserCocTaskService userCocTaskService;
    @Autowired
    private UserCocExpTaskService userCocExpTaskService;
    @Autowired
    private UserCocInfoService userCocInfoService;

    // 特产卖出监听
    @Async
    @EventListener
    public void sellSpecials(SpecialDeductEvent event) {
        EPSpecialDeduct ep = event.getEP();
        if (WayEnum.TRADE != ep.getWay()) {
            return;// 非城市卖特产
        }
        GameUser gu = gameUserService.getGameUser(ep.getGuId());
        int pos = ep.getPos();
        List<EPSpecialDeduct.SpecialInfo> specialInfoList = ep.getSpecialInfoList();
        for (EPSpecialDeduct.SpecialInfo info : specialInfoList) {
            userCocTaskService.updateProgress(gu.getId(), pos, info.getBaseSpecialIds());
        }
    }

    /**
     * 练兵监听
     *
     * @param event
     */
    @Async
    @EventListener
    public void trainingWin(CombatFightWinEvent event) {
        EPFightEnd ep = (EPFightEnd) event.getSource();
        if (FightTypeEnum.TRAINING == ep.getFightType()) {
            userCocExpTaskService.updateTaskProgress(CocConstant.TASK_TYPE_TRIANING, 1, ep.getGuId());
        }
    }

    /**
     * 铜钱变动
     *
     * @param event
     */
    @Async
    @EventListener
    @Order(1000)
    public void addCopper(CopperAddEvent event) {
        EPCopperAdd ep = event.getEP();
        if (WayEnum.TRADE != ep.getWay()) {
            return;// 非交易类型 不统计
        }
        userCocExpTaskService.updateTaskProgress(CocConstant.TASK_TYPE_TRADE, ep.gainAddCopper(), ep.getGuId());
    }

    @Order(9999)
    @EventListener
    public void cocAddHonorEvent(TreasureAddEvent event) {
        EPTreasureAdd ep = event.getEP();
        for (EVTreasure evt : ep.getAddTreasures()) {
            if (evt.getId() == TreasureEnum.SHJF.getValue()) {
                userCocInfoService.updateCocLv(ep.getGuId());
                return;
            }
        }
    }

    @Async
    @EventListener
    public void cocAddHonorEvent(TreasureDeductEvent event) {
        EPTreasureDeduct ep = event.getEP();
        if (ep.getDeductTreasure().getId() == TreasureEnum.SHJF.getValue()) {
            userCocInfoService.updateCocLv(ep.getGuId());
        }
    }
}
