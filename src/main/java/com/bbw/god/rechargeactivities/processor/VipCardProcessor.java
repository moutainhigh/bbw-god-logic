package com.bbw.god.rechargeactivities.processor;

import com.alibaba.fastjson.JSON;
import com.bbw.common.DateUtil;
import com.bbw.common.IpUtil;
import com.bbw.common.ListUtil;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.activity.config.ActivityTool;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardService;
import com.bbw.god.game.award.AwardStatus;
import com.bbw.god.game.config.CfgProductGroup;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.gameuser.pay.UserPayInfo;
import com.bbw.god.gameuser.pay.UserPayInfoService;
import com.bbw.god.pay.RDProductList;
import com.bbw.god.rechargeactivities.RDRechargeActivity;
import com.bbw.god.rechargeactivities.RechargeActivityEnum;
import com.bbw.god.rechargeactivities.RechargeActivityItemEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 各种卡
 *
 * @author lwb
 * @date 2020/7/2 11:11
 */
@Service
public class VipCardProcessor extends AbstractRechargeActivityProcessor {
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private AwardService awardService;
    @Autowired
    private UserPayInfoService userPayInfoService;
    private static int ykActivityId = 1006;
    private static int jkActivityId = 1007;
    private static int jkForeverActivityId = 1008;

    @Override
    public RechargeActivityEnum getParent() {
        return RechargeActivityEnum.CARD_PACK;
    }

    @Override
    public RechargeActivityItemEnum getCurrentEnum() {
        return RechargeActivityItemEnum.MONTH_CARD;
    }

    @Override
    public boolean isShow(long uid) {
        return true;
    }

    @Override
    public int getCanGainAwardNum(long uid) {
        UserPayInfo userPayInfo = userPayInfoService.getUserPayInfo(uid);
        int num = 0;
        if (AwardStatus.ENABLE_AWARD.equals(getVipCardAwardStatus(userPayInfo.getYkEndTime(), userPayInfo.getYkAwardTime()))) {
            num++;
        }
        if (AwardStatus.ENABLE_AWARD.equals(getVipCardAwardStatus(userPayInfo.getJkEndTime(), userPayInfo.getJkAwardTime()))) {
            num++;
        }
        return num;
    }

    @Override
    public RDRechargeActivity listAwards(long uid) {
        CfgProductGroup productGroup = productService.getAppProductGroup(gameUserService.getActiveSid(uid));
        RDProductList rdp = productService.getProductsList(productGroup, gameUserService.getGameUser(uid), IpUtil.getIpAddr(request));
        RDRechargeActivity rd = new RDRechargeActivity();
        List<RDRechargeActivity.GoldPackInfo> goodsInfos = new ArrayList<>();
        //月卡 季卡 永久季卡
        List<RDProductList.RDProduct> list = rdp.getProducts().stream().filter(p -> p.getId() == CfgProductGroup.CfgProduct.YUEKA_ID || p.getId() == CfgProductGroup.CfgProduct.JIKA_ID || p.getId() == CfgProductGroup.CfgProduct.JIKA_ID_FOREVER).collect(Collectors.toList());
        for (RDProductList.RDProduct product : list) {
            goodsInfos.add(RDRechargeActivity.GoldPackInfo.instance(product));
        }
        rd.setProducts(goodsInfos);
        UserPayInfo userPayInfo = userPayInfoService.getUserPayInfo(uid);
        //月卡
        int ykDays = 0;
        if (userPayInfo.getYkEndTime() != null && DateUtil.millisecondsInterval(userPayInfo.getYkEndTime(), new Date()) > 0) {
            ykDays = DateUtil.getDaysBetween(new Date(), userPayInfo.getYkEndTime());
            ykDays = 1 + ykDays;
        }
        rd.setYkDays(ykDays);
        rd.setYkAwardStatus(-1);
        if (AwardStatus.ENABLE_AWARD.equals(getVipCardAwardStatus(userPayInfo.getYkEndTime(), userPayInfo.getYkAwardTime()))) {
            rd.setYkAwardStatus(1);
        }
        //季卡
        rd.setForeverJiKa(0);
        int jkDays = 0;
        if (userPayInfo.hadForeverJiKa()) {
            //永久季卡
            jkDays = 9999;
            rd.setForeverJiKa(1);
        } else if (userPayInfo.getJkEndTime() != null && DateUtil.millisecondsInterval(userPayInfo.getJkEndTime(), new Date()) > 0) {
            jkDays = DateUtil.getDaysBetween(new Date(), userPayInfo.getJkEndTime());
            jkDays = 1 + jkDays;
        }
        rd.setJkDays(jkDays);
        rd.setJkAwardStatus(-1);
        if (AwardStatus.ENABLE_AWARD.equals(getVipCardAwardStatus(userPayInfo.getJkEndTime(), userPayInfo.getJkAwardTime()))) {
            rd.setJkAwardStatus(1);
        }
        return rd;
    }

    @Override
    public RDRechargeActivity gainAwards(long uid, int pid) {
        RDRechargeActivity rd = new RDRechargeActivity();
        UserPayInfo userPayInfo = userPayInfoService.getUserPayInfo(uid);
        if (CfgProductGroup.CfgProduct.YUEKA_ID == pid) {
            if (!AwardStatus.ENABLE_AWARD.equals(getVipCardAwardStatus(userPayInfo.getYkEndTime(), userPayInfo.getYkAwardTime()))) {
                //已领取
                throw new ExceptionForClientTip("rechargeActivity.cant.award");
            }
            awardService.fetchAward(uid, ActivityTool.getActivity(ykActivityId).getAwards(), WayEnum.YK, "【" + WayEnum.YK.getName() + "】活动", rd);
            userPayInfo.setYkAwardTime(DateUtil.now());
            gameUserService.updateItem(userPayInfo);
        } else if (CfgProductGroup.CfgProduct.JIKA_ID == pid || CfgProductGroup.CfgProduct.JIKA_ID_FOREVER == pid) {
            if (!AwardStatus.ENABLE_AWARD.equals(getVipCardAwardStatus(userPayInfo.getJkEndTime(), userPayInfo.getJkAwardTime()))) {
                //已领取
                throw new ExceptionForClientTip("rechargeActivity.cant.award");
            }
            if (userPayInfo.hadForeverJiKa()) {
                awardService.fetchAward(uid, ActivityTool.getActivity(jkForeverActivityId).getAwards(), WayEnum.JK, "【" + WayEnum.JK.getName() + "】活动", rd);
            } else {
                awardService.fetchAward(uid, ActivityTool.getActivity(jkActivityId).getAwards(), WayEnum.JK, "【" + WayEnum.JK.getName() + "】活动", rd);
            }
            userPayInfo.setJkAwardTime(new Date());
            gameUserService.updateItem(userPayInfo);
        } else {
            return super.gainAwards(uid, pid);
        }
        return rd;
    }

    private AwardStatus getVipCardAwardStatus(Date cardExpireTime, Date awardDate) {
        Date now = new Date();
        if (cardExpireTime == null || DateUtil.millisecondsInterval(cardExpireTime, now) < 0) {
            return AwardStatus.UNAWARD;
        }
        if (awardDate == null || DateUtil.getDaysBetween(awardDate, now) != 0) {
            return AwardStatus.ENABLE_AWARD;
        }
        return AwardStatus.UNAWARD;
    }

    /**
     * 一键领取
     * @param uid
     * @return
     */
    @Override
    public RDRechargeActivity gainAllAvailableAwards(long uid){
        RDRechargeActivity rd = new RDRechargeActivity();
        UserPayInfo userPayInfo = userPayInfoService.getUserPayInfo(uid);
        List<Award> awards=new ArrayList<>();
        if (AwardStatus.ENABLE_AWARD.equals(getVipCardAwardStatus(userPayInfo.getYkEndTime(), userPayInfo.getYkAwardTime()))) {
            userPayInfo.setYkAwardTime(DateUtil.now());
            awards.addAll(JSON.parseArray(ActivityTool.getActivity(ykActivityId).getAwards(), Award.class));
        }
        if (AwardStatus.ENABLE_AWARD.equals(getVipCardAwardStatus(userPayInfo.getJkEndTime(), userPayInfo.getJkAwardTime()))) {
            if (userPayInfo.hadForeverJiKa()) {
                awards.addAll(JSON.parseArray(ActivityTool.getActivity(jkForeverActivityId).getAwards(), Award.class));
            } else {
                awards.addAll(JSON.parseArray(ActivityTool.getActivity(jkActivityId).getAwards(), Award.class));
            }
            userPayInfo.setJkAwardTime(new Date());
        }
        if (ListUtil.isNotEmpty(awards)){
            gameUserService.updateItem(userPayInfo);
            awardService.fetchAward(uid, awards, WayEnum.RECEIVE_JK_AND_YK_AWARD, "【" + WayEnum.YK.getName() + "】活动", rd);
        }
        return rd;
    }
}
