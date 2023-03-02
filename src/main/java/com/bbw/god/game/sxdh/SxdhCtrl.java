package com.bbw.god.game.sxdh;

import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.controller.AbstractController;
import com.bbw.god.game.CR;
import com.bbw.god.game.sxdh.rd.RDBeanBuy;
import com.bbw.god.game.sxdh.rd.RDBeanInfo;
import com.bbw.god.game.sxdh.rd.RDSxdhExchangeTicket;
import com.bbw.god.game.sxdh.rd.RDSxdhFighter;
import com.bbw.god.rd.RDCommon;
import com.bbw.god.rd.RDSuccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 神仙大会接口
 *
 * @author suhq
 * @date 2019-06-21 09:33:11
 */
@RestController
public class SxdhCtrl extends AbstractController {
    @Autowired
    private SxdhLogic sxdhLogic;

    /**
     * 获得玩家神仙大会信息
     *
     * @return
     */
    @GetMapping(CR.Sxdh.GET_FIGHTER_INFO)
    public RDSxdhFighter getFighterInfo() {
        return sxdhLogic.getFighterInfo(getUserId());
    }

    /**
     * 兑换门票
     *
     * @param id  消耗的法宝
     * @param num 兑换数量
     * @return
     */
    @GetMapping(CR.Sxdh.EXCHANGE_TICKET)
    public RDSxdhExchangeTicket exchangeTicket(int id, int num) {
        return sxdhLogic.exchangeTicket(getUserId(), id, num);
    }

    /**
     * 领取仙豆
     *
     * @return
     */
    @GetMapping(CR.Sxdh.GET_BEAN_INFO)
    public RDBeanInfo getBeanInfo() {
        return sxdhLogic.getBeanInfo(getUserId());
    }

    /**
     * 神仙大会商店
     *
     * @return
     */
    @GetMapping(CR.Sxdh.BUY_BEAN)
    public RDBeanBuy buyBean(int num) {
        if (num <= 0) {
            throw ExceptionForClientTip.fromi18nKey("buy.num.unvalid");
        }
        return sxdhLogic.buyBean(getUserId(), Math.abs(num));
    }

    /**
     * 购买丹药
     *
     * @param id  丹药
     * @param num 数量
     * @return
     */
    @GetMapping(CR.Sxdh.BUY_MEDICINE)
    public RDCommon buyMedicine(int id, int num) {
        if (num <= 0) {
            throw ExceptionForClientTip.fromi18nKey("buy.num.unvalid");
        }
        return sxdhLogic.buyMedicine(getUserId(), id, Math.abs(num));
    }

    /**
     * 启用丹药
     *
     * @param roomId
     * @param mechineId
     * @param enable
     * @return
     */
    @GetMapping(CR.Sxdh.ENABLE_MECHINE)
    public RDSuccess enableMechine(int roomId, int mechineId, int enable) {
        return sxdhLogic.enableMechine(getUserId(), roomId, mechineId, enable == 1);
    }

    /**
     * 每日冲刺福利
     *
     * @return
     */
    @GetMapping(CR.Sxdh.GET_SPRINT_AWARD)
    public RDSuccess getSprintAward() {
        return sxdhLogic.getSprintAward(getUserId());
    }
}
