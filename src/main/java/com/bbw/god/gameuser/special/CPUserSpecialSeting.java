package com.bbw.god.gameuser.special;

import lombok.Data;

/**
 * 特产一键设置参数
 *
 * @author: huanghb
 * @date: 2022/6/13 16:40
 */
@Data
public class CPUserSpecialSeting {
    // 一键购买的特产id集合
    private String specials;
    // 是否出售商会任务特产
    private Boolean sellCoc = false;
    // 是否出售太一府可捐赠特产
    private Boolean sellTyf = true;
    // 是否出售上锁的特产
    private Boolean ifSellLockSpecial = false;
    //是否一键购买商帮好感度道具
    private Boolean ifBuyBusinessGangGifts = true;
    //是否一键购买铜铲子
    private Boolean ifBuyCopperShovel = true;
    //是否一键购买节日活动道具
    private Boolean ifBuyHolidayProps = true;
    //是否一键购买千年灵芝
    private Boolean ifBuyLingzhi = true;


}
