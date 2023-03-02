package com.bbw.god.activity.holiday.processor.HolidayGroceryShop.rd;

import com.bbw.god.activity.holiday.config.CfgGroceryShop;
import com.bbw.god.activity.holiday.config.GroceryShopTool;
import com.bbw.god.activity.holiday.processor.HolidayGroceryShop.HolidayUserGroceryShop;
import com.bbw.god.activity.holiday.processor.HolidayGroceryShop.enums.GroceryShopReceiveStatusEnum;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.rd.RDSuccess;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 大奖奖励
 * @author: hzf
 * @create: 2022-12-12 08:47
 **/
@Data
public class RdGrandPrixAward extends RDSuccess implements Serializable {
    private static final long serialVersionUID = 7073473097670406575L;
    /** 大奖列表 */
    private List<RdBlindBoxAward> grandPrixAwards;
    /** 当前选择的大奖*/
    private RdBlindBoxAward currentGrandPrixAward;

    public static RdGrandPrixAward instance(HolidayUserGroceryShop userGroceryShop) {
        RdGrandPrixAward rd = new RdGrandPrixAward();
        if (null == userGroceryShop) {
           rd.setCurrentGrandPrixAward(null);
           rd.setGrandPrixAwards(getFirstAward());
           return rd;
        }
        //玩家选择的大奖对应奖励
        CfgGroceryShop.CfgBlindBoxGrandPrix grandPrix = GroceryShopTool.getGrandPrix(userGroceryShop.getUserChoiceGrandPrixId());
        //玩家已经领取大奖的id
        List<Integer> userReceiveGrandPrixIds = userGroceryShop.getUserReceiveGrandPrixIds();
        //不等于0说明有选择大奖
        if (userGroceryShop.getUserChoiceGrandPrixId() == 0) {
            rd.setCurrentGrandPrixAward(new RdBlindBoxAward());
        } else {
            rd.setCurrentGrandPrixAward(gainCurrentGrandPrixAward(grandPrix,userReceiveGrandPrixIds));
        }
        rd.setGrandPrixAwards(getRdBlindBoxAwards(userReceiveGrandPrixIds));
        return rd;
    }

    /**
     * 处理大奖集合
     * @param userReceiveGrandPrixIds
     * @return
     */
    public static List<RdBlindBoxAward> getRdBlindBoxAwards(List<Integer> userReceiveGrandPrixIds){
        List<RdBlindBoxAward> rdBlindBoxAwards = getFirstAward();
        for (RdBlindBoxAward rdBlindBoxAward : rdBlindBoxAwards) {
            if (userReceiveGrandPrixIds.contains(rdBlindBoxAward.getAwardId())) {
                rdBlindBoxAward.setStatus(GroceryShopReceiveStatusEnum.RECEIVED.getValue());
            } else {
                rdBlindBoxAward.setStatus(GroceryShopReceiveStatusEnum.UNCLAIMED.getValue());
            }
        }
        return rdBlindBoxAwards;
    }

    /**
     * 处理当前大奖
     * @param grandPrix
     * @param userReceiveGrandPrixIds
     * @return
     */
    public static RdBlindBoxAward gainCurrentGrandPrixAward(CfgGroceryShop.CfgBlindBoxGrandPrix grandPrix, List<Integer> userReceiveGrandPrixIds){
        RdBlindBoxAward rd = new RdBlindBoxAward();
        rd.setNum(grandPrix.getAward().getNum());
        rd.setAwardId(grandPrix.getAward().getAwardId());
        rd.setItem(grandPrix.getAward().getItem());
        if (userReceiveGrandPrixIds.contains(grandPrix.getTreasureId())) {
            rd.setStatus(GroceryShopReceiveStatusEnum.RECEIVED.getValue());
        } else {
            rd.setStatus(GroceryShopReceiveStatusEnum.UNCLAIMED.getValue());
        }
        return rd;
    }

    /**
     * 获取最初奖励（配置将奖励）
     * @return
     */
    public static List<RdBlindBoxAward> getFirstAward(){
        List<Award> grandPrixAward = GroceryShopTool.getGrandPrixAward();
        List<RdBlindBoxAward> blindBoxAwards = new ArrayList<>();
        for (Award award : grandPrixAward) {
            RdBlindBoxAward rdBlindBoxAward = new RdBlindBoxAward();
            rdBlindBoxAward.setPos(null);
            rdBlindBoxAward.setAwardId(award.getAwardId());
            rdBlindBoxAward.setItem(award.getItem());
            rdBlindBoxAward.setStatus(GroceryShopReceiveStatusEnum.UNCLAIMED.getValue());
            rdBlindBoxAward.setNum(award.getNum());
            blindBoxAwards.add(rdBlindBoxAward);
        }
        return blindBoxAwards;
    }
}
