package com.bbw.god.activity.holiday.processor.holidayspecialcity;

import com.bbw.common.PowerRandom;
import com.bbw.god.activity.IActivity;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.activity.holiday.config.CfgHolidayKoiPray;
import com.bbw.god.activity.rd.RDKoiPrayInfo;
import com.bbw.god.city.chengc.RDTradeInfo;
import com.bbw.god.city.chengc.trade.BuyGoodInfo;
import com.bbw.god.city.chengc.trade.IChengChiTradeService;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.privilege.PrivilegeService;
import com.bbw.god.gameuser.treasure.TreasureChecker;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.rd.RDAdvance;
import com.bbw.god.rd.RDCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 锦鲤祈愿
 *
 * @author: huanghb
 * @date: 2022/9/16 14:57
 */
@Service
public class HolidayKoiPrayProcessor extends AbstractSpecialCityProcessor implements IChengChiTradeService {
    @Autowired
    PrivilegeService privilegeService;

    public HolidayKoiPrayProcessor() {
        this.activityTypeList = Arrays.asList(ActivityEnum.KOI_PRAY);
    }

    /**
     * 是否在ui中展示
     *
     * @param uid
     * @return
     */
    @Override
    public boolean isShowInUi(long uid) {
        return true;
    }

    @Override
    protected long getRemainTime(long uid, int sid, IActivity a) {
        if (a.gainEnd() != null) {
            return a.gainEnd().getTime() - System.currentTimeMillis();
        }
        return NO_TIME;
    }

    /**
     * 是否在活动期间
     *
     * @param sid
     * @return
     */
    @Override
    public boolean isOpened(int sid) {
        ActivityEnum activityEnum = ActivityEnum.fromValue(ActivityEnum.KOI_PRAY.getValue());
        IActivity a = this.activityService.getActivity(sid, activityEnum);
        return a != null;
    }

    /**
     * 触发村庄事件
     *
     * @param uid
     * @param rd
     */
    @Override
    public void cunZTriggerEvent(long uid, RDAdvance rd) {
        checkAndTrigger(uid, rd);
    }


    /**
     * 触发活动事件
     *
     * @param rd
     */
    public void checkAndTrigger(long uid, RDAdvance rd) {
        if (!isOpened(gameUserService.getActiveSid(uid))) {
            return;
        }
        int pro = CfgHolidayKoiPray.getCfg().getCunZTriggerPro();
        if (!PowerRandom.hitProbability(pro)) {
            return;
        }
        // 概率触发活动事件
        doEventTrigger(uid, rd);
    }

    /**
     * 执行活动事件
     *
     * @param uid
     * @param rd
     */
    private void doEventTrigger(long uid, RDAdvance rd) {
        rd.setActivityEvenType(0);
        Award outPut = CfgHolidayKoiPray.randomAwardByProb(CfgHolidayKoiPray.getCfg().getCunZOutPut());
        TreasureEventPublisher.pubTAddEvent(uid, outPut.getAwardId(), outPut.getNum(), WayEnum.KOI_PRAY, rd);
    }

    /**
     * 野怪额外产出
     *
     * @param uid
     * @return
     */
    public List<Award> yeGuaiBoxExtraAwards(long uid) {
        int sid = gameUserService.getActiveSid(uid);
        if (!isOpened(sid)) {
            return new ArrayList<>();
        }
        //获得概率
        int yeGuaiTriggerPro = CfgHolidayKoiPray.getCfg().getYeGuaiTriggerPro();
        if (!PowerRandom.hitProbability(yeGuaiTriggerPro)) {
            return new ArrayList<>();
        }
        //额外奖励
        List<Award> awards = getRandomAwardsByPro(CfgHolidayKoiPray.getCfg().getYeGuaiBoxOutPut());
        return awards;
    }

    /**
     * 根据概率获得奖励
     *
     * @param awards
     * @return
     */
    public static List<Award> getRandomAwardsByPro(List<Award> awards) {
        List<Award> awardList = new ArrayList<>();
        //所有概率
        List<Integer> pro = awards.stream().map(Award::getProbability).collect(Collectors.toList());
        int index = PowerRandom.hitProbabilityIndex(pro);
        awardList.add(awards.get(index));
        return awardList;
    }

    public RDCommon pray(long uid, String koiStr) {
        //;代表和，,代表有多少个

        String[] koisrc = koiStr.split(";");

        Map<Integer, Integer> koiInfos = new HashMap<>();
        for (String koiInfo : koisrc) {
            String[] koi = koiInfo.split(",");
            koiInfos.put(Integer.valueOf(koi[0]), Integer.valueOf(koi[1]));
        }
        //检查法宝道具是否足够
        for (Integer koiId : koiInfos.keySet()) {
            TreasureChecker.checkIsEnough(koiId, koiInfos.get(koiId), uid);
        }
        RDKoiPrayInfo rd = new RDKoiPrayInfo();
        //扣除法宝道具
        for (Integer koiId : koiInfos.keySet()) {
            TreasureEventPublisher.pubTDeductEvent(uid, koiId, koiInfos.get(koiId), WayEnum.KOI_PRAY, rd);
        }
        Integer prayValue = 0;
        //获得随机倍数
        Integer multiplea = CfgHolidayKoiPray.randomMultipleByProb();
        //返回倍数，前端显示用。配置时数值扩大了10，需要除以10
        rd.setMultiplea((double) multiplea / 10);
        Map<Integer, Integer> koiExchangeBases = CfgHolidayKoiPray.getCfg().getKoiExchangeBases();
        //获得祈愿基数
        for (Integer koiId : koiInfos.keySet()) {
            prayValue += koiInfos.get(koiId) * koiExchangeBases.get(koiId);
        }
        prayValue = prayValue * multiplea;
        //发送许愿币
        TreasureEventPublisher.pubTAddEvent(uid, TreasureEnum.WISH_COIN.getValue(), prayValue, WayEnum.KOI_PRAY, rd);
        return rd;
    }

    /**
     * 特产交易产出
     *
     * @param uid
     * @return
     */
    public List<RDTradeInfo.RDCitySpecial> specialExtraAwards(long uid) {
        List<RDTradeInfo.RDCitySpecial> citySpecialList = new ArrayList<>();
        if (!isOpened(gameUserService.getActiveSid(uid))) {
            return citySpecialList;
        }
        //没有获得特产
        if (!PowerRandom.hitProbability(CfgHolidayKoiPray.getCfg().getSpecialsTriggerPro())) {
            return citySpecialList;
        }
        //获得特产
        Award outPut = CfgHolidayKoiPray.randomAwardByProb(CfgHolidayKoiPray.getCfg().getSpecialsOutPut());
        citySpecialList.add(new RDTradeInfo.RDCitySpecial(outPut.getAwardId(), 0, 0));
        return citySpecialList;
    }
    
    @Override
    public List<Integer> getAbleTradeGoodIds() {
        return CfgHolidayKoiPray.getCfg().getInitialSellingPrice()
                .stream()
                .map(CfgHolidayKoiPray.InitialSellingPrice::getTreasureId)
                .collect(Collectors.toList());
    }

    /**
     * 获取某个批次中某个子服务的物品购买信息
     *
     * @param uid
     * @param specialIds
     * @return
     */
    @Override
    public List<BuyGoodInfo> getTradeBuyInfo(long uid, List<Integer> specialIds) {
        if (!isOpened(gameUserService.getActiveSid(uid))) {
            return new ArrayList<>();
        }
        return IChengChiTradeService.super.getTradeBuyInfo(uid, specialIds);
    }

    @Override
    public int getTradeBuyPrice(int goodId) {
        CfgHolidayKoiPray.InitialSellingPrice initialSellingPrice = CfgHolidayKoiPray.getCfg()
                .getInitialSellingPrice()
                .stream()
                .filter(tmp -> tmp.getTreasureId() == goodId).findFirst().orElse(null);
        if (null == initialSellingPrice) {
            return 0;
        }
        return initialSellingPrice.getPrice();
    }
}
