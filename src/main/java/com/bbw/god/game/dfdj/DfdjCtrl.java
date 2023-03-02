package com.bbw.god.game.dfdj;

import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.controller.AbstractController;
import com.bbw.god.game.CR;
import com.bbw.god.game.dfdj.rd.RDDfdjBeanBuy;
import com.bbw.god.game.dfdj.rd.RDDfdjBeanInfo;
import com.bbw.god.game.dfdj.rd.RDDfdjFighter;
import com.bbw.god.rd.RDCommon;
import com.bbw.god.rd.RDSuccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author suchaobin
 * @description 巅峰对决接口
 * @date 2021/1/5 10:50
 **/
@RestController
public class DfdjCtrl extends AbstractController {
    @Autowired
    private DfdjLogic dfdjLogic;

    /**
     * 获得玩家巅峰对决信息
     *
     * @return
     */
    @GetMapping(CR.Dfdj.GET_FIGHTER_INFO)
    public RDDfdjFighter getFighterInfo() {
        return dfdjLogic.getFighterInfo(getUserId());
    }

    /**
     * 获取金豆信息
     *
     * @return
     */
    @GetMapping(CR.Dfdj.GET_BEAN_INFO)
    public RDDfdjBeanInfo getBeanInfo() {
        return dfdjLogic.getBeanInfo(getUserId());
    }

    /**
     * 巅峰对决商店
     *
     * @return
     */
    @GetMapping(CR.Dfdj.BUY_BEAN)
    public RDDfdjBeanBuy buyBean(int num) {
        if (num <= 0) {
            throw ExceptionForClientTip.fromi18nKey("buy.num.unvalid");
        }
        return dfdjLogic.buyBean(getUserId(), num);
    }

    /**
     * 购买丹药
     *
     * @param id  丹药
     * @param num 数量
     * @return
     */
    @GetMapping(CR.Dfdj.BUY_MEDICINE)
    public RDCommon buyMedicine(int id, int num) {
        if (num <= 0) {
            throw ExceptionForClientTip.fromi18nKey("buy.num.unvalid");
        }
        return dfdjLogic.buyMedicine(getUserId(), id, num);
    }

    /**
     * 启用丹药
     *
     * @param roomId
     * @param mechineId
     * @param enable
     * @return
     */
    @GetMapping(CR.Dfdj.ENABLE_MEDICINE)
    public RDSuccess enableMechine(int roomId, int mechineId, int enable) {
        return dfdjLogic.enableMedicine(getUserId(), roomId, mechineId, enable == 1);
    }

    /**
     * 每日冲刺福利
     *
     * @return
     */
    @GetMapping(CR.Dfdj.GET_SPRINT_AWARD)
    public RDSuccess getSprintAward() {
        return dfdjLogic.getSprintAward(getUserId());
    }

    /**
     * 获得卡组
     *
     * @return
     */
    @GetMapping(CR.Dfdj.GET_CARD_GROUP)
    public RDSuccess getCardGroup() {
        return dfdjLogic.getCardGroup(getUserId());
    }

    /**
     * 设置卡组
     *
     * @return
     */
    @GetMapping(CR.Dfdj.SET_CARD_GROUP)
    public RDSuccess setCardGroup(String cardIds) {
        return dfdjLogic.setCardGroup(getUserId(), cardIds);
    }
}
