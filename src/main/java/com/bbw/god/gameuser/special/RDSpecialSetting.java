package com.bbw.god.gameuser.special;

import com.bbw.god.rd.RDCommon;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author suchaobin
 * @description 特产设置数据
 * @date 2020/12/23 14:50
 **/
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class RDSpecialSetting extends RDCommon {
    private static final long serialVersionUID = 8016491211459721617L;
    // 一键购买的特产id集合
    private List<Integer> autoBuySpecialIds = new ArrayList<>();
    // 是否出售商会任务特产
    private Boolean ifSellCocTaskSpecial = false;
    // 是否出售太一府可捐赠特产
    private Boolean ifSellTyfSpecial = true;
    // 是否出售上锁的特产
    private boolean ifSellLockSpecial = false;
    //是否一键购买商帮好感度道具
    private Boolean ifBuyBusinessGangGifts = true;
    //是否一键购买铜铲子
    private Boolean ifBuyCopperShovel = true;
    //是否一键购买节日活动道具
    private Boolean ifBuyHolidayProps = true;
    //是否一键购买千年灵芝
    private Boolean ifBuyLingzhi = true;

    public static RDSpecialSetting getInstance(UserSpecialSetting userSpecialSetting) {
        RDSpecialSetting rd = new RDSpecialSetting();
        rd.setAutoBuySpecialIds(userSpecialSetting.getAutoBuySpecialIds());
        rd.setIfSellTyfSpecial(userSpecialSetting.getIfSellTyfSpecial());
        rd.setIfSellCocTaskSpecial(userSpecialSetting.getIfSellCocTaskSpecial());
        rd.setIfSellLockSpecial(userSpecialSetting.getIfSellLockSpecial());
        rd.setIfBuyBusinessGangGifts(userSpecialSetting.getIfBuyBusinessGangGifts());
        rd.setIfBuyCopperShovel(userSpecialSetting.getIfBuyCopperShovel());
        rd.setIfBuyHolidayProps(userSpecialSetting.getIfBuyHolidayProps());
        rd.setIfBuyLingzhi(userSpecialSetting.getIfBuyLingzhi());
        return rd;
    }
}
