package com.bbw.god.gameuser.res;

import com.bbw.common.ListUtil;
import com.bbw.common.SpringContextUtil;
import com.bbw.god.event.BaseEventParam;
import com.bbw.god.game.config.TypeEnum;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.gameuser.res.copper.CopperAddEvent;
import com.bbw.god.gameuser.res.copper.CopperDeductEvent;
import com.bbw.god.gameuser.res.copper.EPCopperAdd;
import com.bbw.god.gameuser.res.copper.EPCopperDeduct;
import com.bbw.god.gameuser.res.diamond.DiamondAddEvent;
import com.bbw.god.gameuser.res.diamond.DiamondDeductEvent;
import com.bbw.god.gameuser.res.diamond.EPDiamondAdd;
import com.bbw.god.gameuser.res.diamond.EPDiamondDeduct;
import com.bbw.god.gameuser.res.dice.*;
import com.bbw.god.gameuser.res.ele.*;
import com.bbw.god.gameuser.res.exp.EPExpAdd;
import com.bbw.god.gameuser.res.exp.ExpAddEvent;
import com.bbw.god.gameuser.res.gold.EPGoldAdd;
import com.bbw.god.gameuser.res.gold.EPGoldDeduct;
import com.bbw.god.gameuser.res.gold.GoldAddEvent;
import com.bbw.god.gameuser.res.gold.GoldDeductEvent;
import com.bbw.god.rd.RDCommon;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 资源事件发布器
 *
 * @author suhq
 * @date 2018年11月24日 下午8:48:08
 */
public class ResEventPublisher {

    public static void pubCopperAddEvent(Long guId, long addedCopper, WayEnum way, RDCommon rd) {
        if (addedCopper <= 0) {
            return;
        }
        BaseEventParam bep = new BaseEventParam(guId, way, rd);
        EPCopperAdd evCopperAdd = new EPCopperAdd(bep, addedCopper, addedCopper);
        SpringContextUtil.publishEvent(new CopperAddEvent(evCopperAdd));
    }

    public static void pubCopperAddEvent(EPCopperAdd ep) {
        if (ep.gainAddCopper() <= 0) {
            return;
        }
        SpringContextUtil.publishEvent(new CopperAddEvent(ep));
    }

    public static void pubCopperDeductEvent(Long guId, Long deductCopper, WayEnum way, RDCommon rd) {
        if (deductCopper <= 0) {
            return;
        }
        BaseEventParam bep = new BaseEventParam(guId, way, rd);
        EPCopperDeduct epCopperDeduct = new EPCopperDeduct(bep, deductCopper);
        SpringContextUtil.publishEvent(new CopperDeductEvent(epCopperDeduct));
    }

    public static void pubGoldAddEvent(Long guId, int addGold, WayEnum way, RDCommon rd) {
        if (addGold <= 0) {
            return;
        }
        BaseEventParam bep = new BaseEventParam(guId, way, rd);
        EPGoldAdd epGoldAdd = new EPGoldAdd(bep, addGold);
        SpringContextUtil.publishEvent(new GoldAddEvent(epGoldAdd));
    }

    public static void pubGoldAddEvent(EPGoldAdd ep) {
        if (ep.gainAddGold() <= 0) {
            return;
        }
        SpringContextUtil.publishEvent(new GoldAddEvent(ep));
    }

    public static void pubGoldDeductEvent(Long guId, Integer deductGold, WayEnum way, RDCommon rd) {
        if (deductGold <= 0) {
            return;
        }
        BaseEventParam bep = new BaseEventParam(guId, way, rd);
        EPGoldDeduct ep = new EPGoldDeduct(bep, deductGold);
        SpringContextUtil.publishEvent(new GoldDeductEvent(ep));
    }

    /**
     * 发布钻石增加事件
     *
     * @param ep
     */
    public static void pubDiamondAddEvent(EPDiamondAdd ep) {
        if (ep.gainAddDiamond() <= 0) {
            return;
        }
        SpringContextUtil.publishEvent(new DiamondAddEvent(ep));
    }

    /**
     * 发布钻石增加事件
     *
     * @param guId
     * @param addDiamond
     * @param way
     * @param rd
     */
    public static void pubDiamondAddEvent(Long guId, int addDiamond, WayEnum way, RDCommon rd) {
        if (addDiamond <= 0) {
            return;
        }
        BaseEventParam bep = new BaseEventParam(guId, way, rd);
        EPDiamondAdd epGoldAdd = new EPDiamondAdd(bep, addDiamond);
        SpringContextUtil.publishEvent(new DiamondAddEvent(epGoldAdd));
    }

    /**
     * 发布钻石扣除事件
     *
     * @param guId
     * @param deductDiamond
     * @param way
     * @param rd
     */
    public static void pubDiamondDeductEvent(Long guId, Integer deductDiamond, WayEnum way, RDCommon rd) {
        if (deductDiamond <= 0) {
            return;
        }
        BaseEventParam bep = new BaseEventParam(guId, way, rd);
        EPDiamondDeduct ep = new EPDiamondDeduct(bep, deductDiamond);
        SpringContextUtil.publishEvent(new DiamondDeductEvent(ep));
    }

    public static void pubDiceAddEvent(Long guId, Integer addDice, WayEnum way, RDCommon rd) {
        if (addDice <= 0) {
            return;
        }
        BaseEventParam bep = new BaseEventParam(guId, way, rd);
        EPDiceAdd ep = new EPDiceAdd(bep, addDice);
        SpringContextUtil.publishEvent(new DiceAddEvent(ep));
    }

    public static void pubDiceDeductEvent(Long guId, Integer deductDice, WayEnum way, RDCommon rd) {
        if (deductDice <= 0) {
            return;
        }
        BaseEventParam bep = new BaseEventParam(guId, way, rd);
        EPDiceDeduct ep = new EPDiceDeduct(bep, deductDice);
        SpringContextUtil.publishEvent(new DiceDeductEvent(ep));
    }

    public static void pubDiceFullEvent(EPDiceFull ep) {
        SpringContextUtil.publishEvent(new DiceFullEvent(ep));
    }


    /**
     * @param guId
     * @param type 0 全属性；非0 特定属性
     * @param num
     * @param way
     * @param rd
     */
    public static void pubEleAddEvent(long guId, int type, int num, WayEnum way, RDCommon rd) {
        if (num <= 0) {
            return;
        }
        List<EVEle> eles = null;
        if (type == 0) {
            eles = Arrays.asList(new EVEle(10, num), new EVEle(20, num), new EVEle(30, num), new EVEle(40, num), new EVEle(50, num));
        } else {
            eles = Arrays.asList(new EVEle(type, num));
        }
        BaseEventParam bep = new BaseEventParam(guId, way, rd);
        EPEleAdd ep = new EPEleAdd(bep, eles);
        SpringContextUtil.publishEvent(new EleAddEvent(ep));
    }

    /**
     * 随机元素专用
     *
     * @param guId
     * @param num
     * @param way
     * @param rd
     */
    public static void pubEleAddEvent(long guId, int num, WayEnum way, RDCommon rd) {
        if (num <= 0) {
            return;
        }
        List<EVEle> eles = Arrays.asList(new EVEle(num));
        BaseEventParam bep = new BaseEventParam(guId, way, rd);
        EPEleAdd ep = new EPEleAdd(bep, eles);
        SpringContextUtil.publishEvent(new EleAddEvent(ep));
    }

    public static void pubPerEleAddEvent(long guId, int num, WayEnum way, RDCommon rd) {
        if (num <= 0) {
            return;
        }
        List<EVEle> eles = Arrays.asList(new EVEle(TypeEnum.Gold.getValue(), num), new EVEle(TypeEnum.Wood.getValue(), num), new EVEle(TypeEnum.Water.getValue(), num), new EVEle(TypeEnum.Fire.getValue(), num), new EVEle(TypeEnum.Earth.getValue(), num));
        pubEleAddEvent(guId, eles, way, rd);
    }

    public static void pubEleAddEvent(long guId, List<EVEle> eles, WayEnum way, RDCommon rd) {
        BaseEventParam bep = new BaseEventParam(guId, way, rd);
        EPEleAdd ep = new EPEleAdd(bep, eles);
        SpringContextUtil.publishEvent(new EleAddEvent(ep));
    }

    public static void pubEleDeductEvent(long guId, List<EVEle> eles, WayEnum way, RDCommon rd) {
        eles = eles.stream().filter(ele -> ele.getNum() > 0).collect(Collectors.toList());
        if (ListUtil.isEmpty(eles)) {
            return;
        }
        BaseEventParam bep = new BaseEventParam(guId, way, rd);
        EPEleDeduct ep = new EPEleDeduct(bep, eles);
        SpringContextUtil.publishEvent(new EleDeductEvent(ep));
    }

    public static void pubEleDeductEvent(long guId, int type, int num, WayEnum way, RDCommon rd) {
        if (num <= 0) {
            return;
        }
        List<EVEle> eles = null;
        if (type == 0) {
            eles = Arrays.asList(new EVEle(10, num), new EVEle(20, num), new EVEle(30, num), new EVEle(40, num), new EVEle(50, num));
        } else {
            eles = Arrays.asList(new EVEle(type, num));
        }
        BaseEventParam bep = new BaseEventParam(guId, way, rd);
        EPEleDeduct ep = new EPEleDeduct(bep, eles);
        SpringContextUtil.publishEvent(new EleDeductEvent(ep));
    }

    public static void pubExpAddEvent(Long guId, Integer addedExp, WayEnum way, RDCommon rd) {
        if (addedExp <= 0) {
            return;
        }
        BaseEventParam bep = new BaseEventParam(guId, way, rd);
        EPExpAdd ep = new EPExpAdd(bep, addedExp);
        SpringContextUtil.publishEvent(new ExpAddEvent(ep));
    }

}
