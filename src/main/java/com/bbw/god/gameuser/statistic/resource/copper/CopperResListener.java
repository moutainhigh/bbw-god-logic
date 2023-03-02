package com.bbw.god.gameuser.statistic.resource.copper;

import com.bbw.common.DateUtil;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.gameuser.res.copper.CopperAddEvent;
import com.bbw.god.gameuser.res.copper.CopperDeductEvent;
import com.bbw.god.gameuser.res.copper.EPCopperAdd;
import com.bbw.god.gameuser.res.copper.EPCopperDeduct;
import com.bbw.god.gameuser.special.event.EPSpecialDeduct;
import com.bbw.god.gameuser.special.event.SpecialDeductEvent;
import com.bbw.god.gameuser.statistic.StatisticTypeEnum;
import com.bbw.god.gameuser.statistic.event.StatisticEventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author suchaobin
 * @description 铜钱统计监听类
 * @date 2020/4/20 15:38
 */
@Component
@Slf4j
@Async
public class CopperResListener {
    @Autowired
    private CopperResStatisticService copperStatisticService;

    @Order(2)
    @EventListener
    public void addCopper(CopperAddEvent event) {
        try {
            EPCopperAdd ep = event.getEP();
            long sum = ep.gainAddCopper();
            // 富豪榜
            long fhbCopper = ep.getWeekCopper();
            long profit = 0;
            // 满足添加条件
            if (shouldAdd(ep.getWay(), fhbCopper)) {
                profit = fhbCopper;
            }
            copperStatisticService.addCopper(ep.getGuId(), DateUtil.getTodayInt(), sum, profit, ep.getWay());
            CopperStatistic statistic = copperStatisticService.fromRedis(ep.getGuId(), StatisticTypeEnum.GAIN,
                    DateUtil.getTodayInt());
            StatisticEventPublisher.pubCopperResourceEvent(ep.getGuId(), ep.getWay(), ep.getRd(), statistic);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private boolean shouldAdd(WayEnum way, long fhbCopper) {
        if (fhbCopper <= 0) {
            return false;
        }
        switch (way) {
            case SALARY_COPPER:// 俸禄不算入富豪榜
            case EXCHANGE_FST:// 封神台兑换收入不算入富豪榜
            case Mail:// 邮件不纳入富豪榜
                return false;
            default:
                return true;
        }
    }

    @Order(2)
    @EventListener
    public void deductCopper(CopperDeductEvent event) {
        try {
            EPCopperDeduct ep = event.getEP();
            long deductCopper = ep.getDeductCopper();
            copperStatisticService.deductCopper(ep.getGuId(), DateUtil.getTodayInt(), deductCopper, ep.getWay());
            CopperStatistic statistic = copperStatisticService.fromRedis(ep.getGuId(), StatisticTypeEnum.CONSUME,
                    DateUtil.getTodayInt());
            StatisticEventPublisher.pubCopperResourceEvent(ep.getGuId(), ep.getWay(), ep.getRd(), statistic);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @EventListener
    @Order(2)
    public void deductSpecial(SpecialDeductEvent event) {
        try {
            EPSpecialDeduct ep = event.getEP();
            WayEnum way = ep.getWay();
            if (way != WayEnum.TRADE) {
                return;
            }
            List<EPSpecialDeduct.SpecialInfo> specialInfoList = ep.getSpecialInfoList();
            // 亏损的铜钱数
            int deductCopper = 0;
            for (EPSpecialDeduct.SpecialInfo info : specialInfoList) {
                Integer buyPrice = info.getBuyPrice();
                Integer sellPrice = info.getSellPrice();
                if (sellPrice < buyPrice) {
                    deductCopper += buyPrice - sellPrice;
                }
            }
            copperStatisticService.incProfitStatistic(ep.getGuId(), StatisticTypeEnum.CONSUME, DateUtil.getTodayInt(),
                    deductCopper);
            CopperStatistic statistic = copperStatisticService.fromRedis(ep.getGuId(), StatisticTypeEnum.CONSUME,
                    DateUtil.getTodayInt());
            StatisticEventPublisher.pubResourceStatisticEvent(ep.getGuId(), ep.getWay(), ep.getRd(), statistic);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
