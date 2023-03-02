package com.bbw.god.activity.holiday.processor.HolidayGroceryShop;

import com.bbw.common.ListUtil;
import com.bbw.common.PowerRandom;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.activity.holiday.config.CfgGroceryShop;
import com.bbw.god.activity.holiday.config.GroceryShopTool;
import com.bbw.god.activity.holiday.processor.HolidayGroceryShop.rd.RdGrandPrixAward;
import com.bbw.god.activity.holiday.processor.HolidayGroceryShop.rd.RdGroceryShop;
import com.bbw.god.activity.processor.AbstractActivityProcessor;
import com.bbw.god.activity.rd.RDActivityList;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.gameuser.treasure.TreasureChecker;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.rd.RDCommon;
import com.bbw.god.rd.RDSuccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 杂货小铺
 * @author: hzf
 * @create: 2022-12-09 09:53
 **/
@Service
public class HolidayGroceryShopProcessor extends AbstractActivityProcessor {
    @Autowired
    private HolidayUserGroceryShopService holidayUserGroceryShopService;

    public HolidayGroceryShopProcessor() {
        this.activityTypeList = Arrays.asList(ActivityEnum.GROCERY_SHOP_51);
    }

    /**
     * 获取活动返回
     * @param uid
     * @param activityType
     * @return
     */
    @Override
    public RDSuccess getActivities(long uid, int activityType) {
        RDActivityList rdActivity = (RDActivityList) super.getActivities(uid, activityType);
        HolidayUserGroceryShop userGroceryShop = getUserGroceryShop(uid);
        return RdGroceryShop.instance(userGroceryShop);
    }
    /**
     * 获取玩家杂货小铺的数据
     * @param uid
     * @return
     */
    public HolidayUserGroceryShop getUserGroceryShop(long uid) {
        return holidayUserGroceryShopService.getSingleData(uid);
    }

    /**
     * 杂货小铺选择大奖
     * @param uid
     * @param treasureId
     * @return
     */
    public RdGroceryShop choiceGrandPrix(long uid, Integer treasureId) {
        //这个是否大奖里面的
        if (null == treasureId || !GroceryShopTool.ifGrandPrix(treasureId)) {
            throw new ExceptionForClientTip("activity.game.grocery.shop.not.exist");
        }
        HolidayUserGroceryShop userGroceryShop = getUserGroceryShop(uid);
        //添加大奖
        if (null == userGroceryShop && GroceryShopTool.ifGrandPrix(treasureId)) {
            userGroceryShop = HolidayUserGroceryShop.instance(uid,treasureId);
            holidayUserGroceryShopService.addData(userGroceryShop);
            return RdGroceryShop.instance(userGroceryShop);
        }
        //判断盲盒是否已经领取
        if (!userGroceryShop.ifGrandPrix(treasureId)) {
            throw new ExceptionForClientTip("activity.game.grocery.shop.all.receive");
        }

        //道具是否相同
        boolean identicalGrandPrix =  userGroceryShop.getUserChoiceGrandPrixId().equals(treasureId);
        //够买次数小于25
        boolean buyNumLess25  = userGroceryShop.gainBuyNum() < GroceryShopTool.getCfg().getBlindBoxNeedNum();
        //相同过滤，次数还没到25次
        if (identicalGrandPrix && buyNumLess25){
            throw new ExceptionForClientTip("activity.game.grocery.shop.but.frequency.less25");
        }
        //重选大奖,大奖要可领取。排除已经领取的
        if (!identicalGrandPrix && userGroceryShop.ifGrandPrix(treasureId)) {
            userGroceryShop.choiceUserGroceryShop(treasureId);
            holidayUserGroceryShopService.updateData(userGroceryShop);
        }
        return RdGroceryShop.instance(userGroceryShop);
    }

    @Override
    public boolean isShowInUi(long uid) {
        return true;
    }

    /**
     * 杂货小铺够买盲盒
     * @param uid
     * @param positions 位置，多个用逗号分开
     * @return
     */
    public RDCommon butBlindBox(long uid,String positions) {
        RDCommon rd = new RDCommon();
        HolidayUserGroceryShop userGroceryShop = getUserGroceryShop(uid);
        //判断是否全部领取完
        if (userGroceryShop.gainBuyNum().equals(GroceryShopTool.getCfg().getBlindBoxMaxNum()) &&
                userGroceryShop.getUserBlindBoxAwards().size() == GroceryShopTool.getCfg().getBlindBoxMaxNum()){
            throw new ExceptionForClientTip("activity.game.grocery.shop.all.receive");
        }
        List<Integer> poss = ListUtil.parseStrToInts(positions);
        //判断位置是否被领取
        if (userGroceryShop.ifReiverPos(poss)) {
            throw new ExceptionForClientTip("activity.game.grocery.shop.pos.receive");
        }
        CfgGroceryShop.CfgBlindBoxGrandPrix grandPrix = GroceryShopTool.getGrandPrix(userGroceryShop.getUserChoiceGrandPrixId());
        //判断扣除道具
        rd = deductTreasure(uid, grandPrix.getNeedTreasure(), grandPrix.getNum() * poss.size());
        //发送奖励
        List<Award> awards = new ArrayList<>();
        for (Integer pos : poss) {
            //随机获取一个奖励
            CfgGroceryShop.CfgBlindBoxAward blindBoxAwards = GroceryShopTool.getBlindBoxAwards(userGroceryShop.getUserChoiceGrandPrixId());
            //更新领取
            userGroceryShop.addBuyBum();
            //判断是不是必得大奖
            boolean mustGetGrandPrix = !userGroceryShop.isIfReiverGrandPrix() && userGroceryShop.gainBuyNum().equals(GroceryShopTool.getCfg().getNeedMustGetNum());
            Award award = new Award();
            if (mustGetGrandPrix) {
                award = grandPrix.getAward();
            } else {
                award = PowerRandom.getRandomFromList(blindBoxAwards.getAwards(),userGroceryShop.conversionReceiveBlindBoxAward());
            }
            userGroceryShop.userReceiveBlindBoxAwardMap(pos,award);
            awards.add(award);
        }
        awardService.fetchAward(uid,awards, WayEnum.GROCERY_SHOP,"", rd);
        holidayUserGroceryShopService.updateData(userGroceryShop);
        return rd;
    }


    public RDCommon deductTreasure(long uid, Integer needTreasureId, Integer needNum){
        //检查是否有足够道具
        TreasureChecker.checkIsEnough(needTreasureId, needNum, uid);
        RDCommon rd = new  RDCommon();
        TreasureEventPublisher.pubTDeductEvent(uid, needTreasureId, needNum, WayEnum.GROCERY_SHOP, rd);
        return rd;
    }

    /**
     * 杂货小铺查看大奖
     * @param uid
     * @return
     */
    public RdGrandPrixAward getGrandPrixAward(long uid) {
        HolidayUserGroceryShop userGroceryShop = getUserGroceryShop(uid);
        return RdGrandPrixAward.instance(userGroceryShop);
    }

    /**
     * 重选大奖
     * @param uid
     * @return
     */
    public RDSuccess reselectGrandPrix(long uid) {
        HolidayUserGroceryShop userGroceryShop = getUserGroceryShop(uid);
        if (null == userGroceryShop) {
            return new RDSuccess();
        }
        //够买次数小于25ss
        boolean buyNumLess25  = userGroceryShop.getBuyNum() < GroceryShopTool.getCfg().getBlindBoxNeedNum();
        //相同过滤，次数还没到25次
        if (buyNumLess25){
            throw new ExceptionForClientTip("activity.game.grocery.shop.but.frequency.less25");
        }
        userGroceryShop.reselectGrandPrix();
        holidayUserGroceryShopService.updateData(userGroceryShop);
        return new RDSuccess();
    }

}
