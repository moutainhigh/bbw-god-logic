package com.bbw.god.activity.holiday.processor.HolidayGroceryShop;

import com.bbw.common.ID;
import com.bbw.common.ListUtil;
import com.bbw.god.activity.holiday.config.GroceryShopTool;
import com.bbw.god.cache.tmp.AbstractTmpData;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardEnum;
import lombok.Data;

import java.io.Serializable;
import java.util.*;

/**
 * 玩家杂货小铺数据
 *
 * @author: hzf
 * @create: 2022-12-09 09:28
 **/
@Data
public class HolidayUserGroceryShop extends AbstractTmpData implements Serializable {
    private static final long serialVersionUID = 7073473097670406575L;
    /**玩家id */
    private long gameUserId;
    /**玩家选择的大奖id*/
    private Integer userChoiceGrandPrixId;
    /**是否领取大奖*/
    private boolean ifReiverGrandPrix;
    /** 玩家领取记录 */
    private Map<String,UserBlindBoxAward> userReceiveBlindBoxAwardMap = new HashMap<>();
    /** 玩家领取大奖的id集合 */
    private  List<Integer> userReceiveGrandPrixIds;
    /** 够买次数 */
    private Integer buyNum;

    public Integer gainBuyNum() {
        return null == buyNum ? 0 : buyNum;
    }

    /**
     * 添加够买次数
     */
    public void addBuyBum(){
        if (GroceryShopTool.getCfg().getBlindBoxMaxNum().equals(buyNum)) {
            return;
        }
        buyNum++;
    }

    /**
     * 领取盲盒奖励
     * @param pos
     * @param award
     */
    public void userReceiveBlindBoxAwardMap(Integer pos,Award award){
        if (award.getAwardId().equals(userChoiceGrandPrixId)) {
            userReceiveGrandPrixIds.add(userChoiceGrandPrixId);
            ifReiverGrandPrix = true;
        }
        UserBlindBoxAward userBlindBoxAward = getUserBlindBoxAward(award.getItem(), award.getAwardId(), award.getNum());
        userReceiveBlindBoxAwardMap.put(String.valueOf(pos),userBlindBoxAward);
    }


    /**
     * 判断该位置是否已经被领取
     * @param poss
     * @return
     */
    public boolean ifReiverPos(List<Integer> poss){
        if (ListUtil.isEmpty(poss)) {
            return false;
        }
        for (Integer pos : poss) {
            UserBlindBoxAward userBlindBoxAward = userReceiveBlindBoxAwardMap.get(String.valueOf(pos));
            if (null != userBlindBoxAward) {
                return true;
            }
        }
        return false;
    }




    /**
     * 构建实例
     * @param uid
     * @param treasureId
     * @return
     */
    public static HolidayUserGroceryShop instance(long uid,Integer treasureId){
        HolidayUserGroceryShop holidayUserGroceryShop = new HolidayUserGroceryShop();
        holidayUserGroceryShop.setId(ID.INSTANCE.nextId());
        holidayUserGroceryShop.setGameUserId(uid);
        holidayUserGroceryShop.setBuyNum(0);
        holidayUserGroceryShop.setUserChoiceGrandPrixId(treasureId);
        holidayUserGroceryShop.setIfReiverGrandPrix(false);
        holidayUserGroceryShop.setUserReceiveGrandPrixIds(new ArrayList<>());
        return holidayUserGroceryShop;
    }
    /**
     * 判断大奖是否被领取
     * @param treasureId
     * @return
     */
    public boolean ifGrandPrix(Integer treasureId){
        //满足的大奖
        boolean grandPrix = GroceryShopTool.ifGrandPrix(treasureId);
        //该大奖已经被领取
        boolean userReceive = userReceiveGrandPrixIds.contains(treasureId);
        //是大奖并且奖励还没被领取
        if (grandPrix && !userReceive){
            return true;
        }
        return false;
    }

    public List<UserBlindBoxAward> getUserBlindBoxAwards(){
        Map<String, UserBlindBoxAward> userReceiveBlindBoxAwardMap = this.getUserReceiveBlindBoxAwardMap();
        if (null == userReceiveBlindBoxAwardMap) {
         return new ArrayList<>();
        }
        return new ArrayList<>(userReceiveBlindBoxAwardMap.values());
    }

    /**
     * 转化为Award
     * @return
     */
    public List<Award> conversionReceiveBlindBoxAward(){
        List<UserBlindBoxAward> userReceiveBlindBoxAwards = getUserBlindBoxAwards();
        if (ListUtil.isEmpty(userReceiveBlindBoxAwards)) {
            return new ArrayList<>();
        }
        List<Award> awards = new ArrayList<>();

        for (UserBlindBoxAward userReceiveBlindBoxAward : userReceiveBlindBoxAwards) {
            Award award = new Award();
            if (userReceiveBlindBoxAward.getItem() != AwardEnum.TQ.getValue()) {
               award.setAwardId(userReceiveBlindBoxAward.getAwardId());
            }
            award.setItem(userReceiveBlindBoxAward.getItem());
            award.setNum(userReceiveBlindBoxAward.getNum());
            awards.add(award);
        }
        return awards;
    }
    /**
     * 选择杂货小铺的大奖
     * @param treasureId
     */
    public void choiceUserGroceryShop(Integer treasureId){
        buyNum = 0;
        userChoiceGrandPrixId = treasureId;
        ifReiverGrandPrix = false;
        userReceiveBlindBoxAwardMap = new HashMap<>();
    }

    /**
     * 重新选择大奖（清除上次盲盒）
     */
    public void reselectGrandPrix(){
        buyNum = null;
        userChoiceGrandPrixId = 0;
        ifReiverGrandPrix = Boolean.parseBoolean(null);
        userReceiveBlindBoxAwardMap = new HashMap<>();
    }



    /**
     * 查询奖励
     * @param item
     * @param awardId
     * @param num
     * @return
     */
    public UserBlindBoxAward getUserBlindBoxAward(Integer item, Integer awardId, Integer num) {
        UserBlindBoxAward userBlindBoxAward = new UserBlindBoxAward();
        userBlindBoxAward.setAwardId(awardId);
        userBlindBoxAward.setItem(item);
        userBlindBoxAward.setNum(num);
        return userBlindBoxAward;
    }

    @Data
    public static class UserBlindBoxAward  implements Serializable {
        private static final long serialVersionUID = 7073473097670406575L;
        /**道具类型 */
        private Integer item;
        /**奖励id */
        private Integer awardId;
        /**奖励数量  */
        private Integer num;
    }

}
