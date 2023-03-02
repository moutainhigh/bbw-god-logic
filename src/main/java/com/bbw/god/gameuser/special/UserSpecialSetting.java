package com.bbw.god.gameuser.special;

import com.bbw.common.ID;
import com.bbw.god.game.config.special.CfgSpecialEntity;
import com.bbw.god.game.config.special.SpecialTool;
import com.bbw.god.gameuser.UserDataType;
import com.bbw.god.gameuser.UserSingleObj;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author suchaobin
 * @description 玩家特产设置
 * @date 2020/12/23 14:46
 **/
@Data
@EqualsAndHashCode(callSuper = true)
public class UserSpecialSetting extends UserSingleObj {
    // 一键购买的特产id集合
    private List<Integer> autoBuySpecialIds = new ArrayList<>();
    // 是否出售商会任务特产
    private Boolean ifSellCocTaskSpecial = false;
    // 是否出售太一府可捐赠特产
    private Boolean ifSellTyfSpecial = true;
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

    public static UserSpecialSetting getInstance(long uid) {
        UserSpecialSetting setting = new UserSpecialSetting();
        setting.setGameUserId(uid);
        setting.setId(ID.INSTANCE.nextId());
        List<Integer> specialIds = SpecialTool.getSpecials().stream().filter(tmp ->
                !tmp.isSyntheticSpecialty()).map(CfgSpecialEntity::getId).collect(Collectors.toList());
        setting.setAutoBuySpecialIds(specialIds);
        return setting;
    }

    /**
     * 更新玩家一键购买设置
     *
     * @param specialIds
     * @param cpUserSpecialSeting
     */
    protected void updateUserSpecialSetting(List<Integer> specialIds, CPUserSpecialSeting cpUserSpecialSeting) {
        // 一键购买的特产id集合，逗号隔开
        this.autoBuySpecialIds = specialIds;
        // 是否出售商会任务特产
        this.ifSellCocTaskSpecial = cpUserSpecialSeting.getSellCoc();
        // 是否出售太一府可捐赠特产
        this.ifSellTyfSpecial = cpUserSpecialSeting.getSellTyf();
        // 是否出售上锁的特产
        this.ifSellLockSpecial = cpUserSpecialSeting.getIfSellLockSpecial();
        //是否一键购买商帮好感度道具
        this.ifBuyBusinessGangGifts = cpUserSpecialSeting.getIfBuyBusinessGangGifts();
        //是否一键购买铜铲子
        this.ifBuyCopperShovel = cpUserSpecialSeting.getIfBuyCopperShovel();
        //是否一键购买节日活动道具
        this.ifBuyHolidayProps = cpUserSpecialSeting.getIfBuyHolidayProps();
        //是否一键购买千年灵芝
        this.ifBuyLingzhi = cpUserSpecialSeting.getIfBuyLingzhi();
    }

    /**
     * 玩家资源类型
     *
     * @return
     */
    @Override
    public UserDataType gainResType() {
        return UserDataType.SPECIAL_SETTING;
    }
}
