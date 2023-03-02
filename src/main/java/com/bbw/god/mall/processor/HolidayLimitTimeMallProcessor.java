package com.bbw.god.mall.processor;

import com.bbw.common.PowerRandom;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.activity.holiday.processor.HolidayLimitTimeMallPackProcessor;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.game.award.AwardService;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.mall.CfgMallEntity;
import com.bbw.god.game.config.mall.MallEnum;
import com.bbw.god.game.config.mall.MallTool;
import com.bbw.god.mall.MallService;
import com.bbw.god.mall.RDMallList;
import com.bbw.god.mall.UserMallRecord;
import com.bbw.god.rd.RDCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 节日限时礼包
 *
 */
@Qualifier
@Service
public class HolidayLimitTimeMallProcessor extends AbstractMallProcessor {
    @Autowired
    private MallService mallService;
    @Autowired
    private AwardService awardService;
    @Autowired
    private HolidayLimitTimeMallPackProcessor holidayLimitTimeMallPackProcessor;
    protected int[] wnls;
    protected int[] wnlIds;
    protected ActivityEnum activityType;

    HolidayLimitTimeMallProcessor() {
        this.mallType = MallEnum.HOLIDAY_MALL_LIMIT_PACK;
        wnls = new int[]{5000, 3000, 1250, 500, 250};
        wnlIds = new int[]{810, 820, 830, 840, 850};
        activityType = ActivityEnum.LIMIT_TIME_MALL_PACK;
    }

    @Override
    public RDMallList getGoods(long guId) {
        int sid=gameUserService.getActiveSid(guId);
        if (!holidayLimitTimeMallPackProcessor.opened(sid, activityType)) {
            return new RDMallList();
        }
        List<CfgMallEntity> fMalls = geMalls();
        RDMallList rd = new RDMallList();
        toRdMallList(guId, fMalls, false, rd);
        return rd;
    }

    @Override
    public void deliver(long guId, CfgMallEntity mall, int buyNum, RDCommon rd) {
        int goodId = mall.getGoodsId();
        List<Award> awards = holidayLimitTimeMallPackProcessor.getAwards(goodId, ActivityEnum.LIMIT_TIME_MALL_PACK);
        for (Award award : awards) {
            if (award.getItem()== AwardEnum.WNLS.getValue()){
                int seed=PowerRandom.getRandomBySeed(10000);
                int sum=0;
                for (int i=0;i<5;i++) {
                    sum+=wnls[i];
                    if (sum>=seed){
                        award.setAwardId(wnlIds[i]);
                        award.setItem(60);
                        break;
                    }
                }
            }
        }
        awardService.fetchAward(guId, awards, WayEnum.MALL_BUY, WayEnum.MALL_BUY.getName(), rd);
    }

    @Override
    protected List<UserMallRecord> getUserMallRecords(long guId) {
        List<UserMallRecord> favorableRecords = this.mallService.getUserMallRecord(guId, this.mallType);
        List<UserMallRecord> validRecords = favorableRecords.stream().filter(umr -> umr.ifValid())
                .collect(Collectors.toList());
        return validRecords;
    }

    /**
     * 获得商城信息
     *
     * @return
     */
    protected List<CfgMallEntity> geMalls() {
        return MallTool.getMallConfig().getHolidayLimitTimeMalls();
    }
}
