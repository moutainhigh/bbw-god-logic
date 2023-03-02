package com.bbw.god.activity.holiday.processor.HolidayGroceryShop.rd;

import com.bbw.god.activity.holiday.config.GroceryShopTool;
import com.bbw.god.activity.holiday.processor.HolidayGroceryShop.HolidayUserGroceryShop;
import com.bbw.god.rd.RDSuccess;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 杂货小铺返回类
 * @author: hzf
 * @create: 2022-12-09 10:25
 **/
@Data
public class RdGroceryShop extends RDSuccess implements Serializable {
    private static final long serialVersionUID = 7073473097670406575L;
    /**玩家选择的大奖id*/
    private Integer userChoiceGrandPrixId;
    /** 盲盒奖励 */
    private List<RdBlindBoxAward> userBlindBoxAwards;
    /**是否领取大奖*/
    private Boolean ifReiverGrandPrix;
    /** 是否可以重选 */
    private Boolean ifReselect;
    /** 够买次数 */
    private Integer buyNum;
    public RdGroceryShop(Integer userChoiceGrandPrixId) {
        this.userChoiceGrandPrixId = userChoiceGrandPrixId;
    }

    public RdGroceryShop() {
    }

    public static RdGroceryShop instance(HolidayUserGroceryShop groceryShop){
        //还没选择大奖
        if (null == groceryShop || groceryShop.getUserChoiceGrandPrixId() == 0) {
            return new RdGroceryShop(0);
        }
        RdGroceryShop rdGroceryShop = new RdGroceryShop();
        rdGroceryShop.setUserChoiceGrandPrixId(groceryShop.getUserChoiceGrandPrixId());
        rdGroceryShop.setIfReiverGrandPrix(groceryShop.isIfReiverGrandPrix());
        Map<String, HolidayUserGroceryShop.UserBlindBoxAward> userReceiveBlindBoxAwardMap = groceryShop.getUserReceiveBlindBoxAwardMap();
        rdGroceryShop.setUserBlindBoxAwards(getBlindBoxAwards(userReceiveBlindBoxAwardMap));
        rdGroceryShop.setBuyNum(groceryShop.gainBuyNum());
        if (groceryShop.gainBuyNum() >= GroceryShopTool.getCfg().getBlindBoxNeedNum()) {
            rdGroceryShop.setIfReselect(true);
        } else {
            rdGroceryShop.setIfReselect(false);
        }
        return rdGroceryShop;
    }

    /**
     * 获取玩家获得盲盒
     * @param userReceiveBlindBoxAwardMap
     * @return
     */
    public static List<RdBlindBoxAward> getBlindBoxAwards(Map<String, HolidayUserGroceryShop.UserBlindBoxAward> userReceiveBlindBoxAwardMap){
        List<RdBlindBoxAward> rdBlindBoxAwards = initBlindBox();
        if (null == userReceiveBlindBoxAwardMap) {
            return rdBlindBoxAwards;
        }
        for (RdBlindBoxAward rdBlindBoxAward : rdBlindBoxAwards) {
            HolidayUserGroceryShop.UserBlindBoxAward userBlindBoxAward = userReceiveBlindBoxAwardMap.get(String.valueOf(rdBlindBoxAward.getPos()));
            if (null == userBlindBoxAward) {
               continue;
            }

            rdBlindBoxAward.setAwardId(userBlindBoxAward.getAwardId());
            rdBlindBoxAward.setItem(userBlindBoxAward.getItem());
            rdBlindBoxAward.setNum(userBlindBoxAward.getNum());

        }
        return rdBlindBoxAwards;
    }

    /**
     * 初始化三十个盲盒
     * @return
     */
    public static List<RdBlindBoxAward> initBlindBox(){
        List<RdBlindBoxAward> rdUserBlindBoxAwards = new ArrayList<>();
        for (int i = 1; i <= 30 ; i++) {
            RdBlindBoxAward rd = new RdBlindBoxAward();
            rd.setPos(i);
            rdUserBlindBoxAwards.add(rd);
        }
        return rdUserBlindBoxAwards;
    }
}
