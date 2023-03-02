package com.bbw.god.statistics.userstatistic;

import com.bbw.god.game.config.WayEnum;
import com.bbw.god.gameuser.special.event.EPSpecialDeduct;
import com.bbw.god.gameuser.special.event.SpecialDeductEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author suchaobin
 * @date 2019/12/12 16:36
 */
@Component
@Async
public class UserSpecialListener {
  @Autowired
  private UserStatisticService userStatisticService;

  @EventListener
  @Order(1000)
  public void specialStatistic(SpecialDeductEvent event) {
    EPSpecialDeduct ep = event.getEP();
    if (!ep.getWay().equals(WayEnum.TRADE)) {
      return;
    }
    List<EPSpecialDeduct.SpecialInfo> specialInfoList = ep.getSpecialInfoList();
    Integer totalBoughtPrice = 0;
    Integer totalSellPrice = 0;
    Integer dealTimes = 0;
    for (EPSpecialDeduct.SpecialInfo info : specialInfoList) {
      totalBoughtPrice += info.getBuyPrice();
      totalSellPrice += info.getSellPrice();
      dealTimes += 1;
    }
    userStatisticService.specialStatistic(ep.getGuId(), "总成本", totalBoughtPrice);
    userStatisticService.specialStatistic(ep.getGuId(), "总售价", totalSellPrice);
    userStatisticService.specialStatistic(ep.getGuId(), "交易次数", dealTimes);
  }
}
