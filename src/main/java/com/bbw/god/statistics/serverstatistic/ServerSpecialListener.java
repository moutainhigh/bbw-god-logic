package com.bbw.god.statistics.serverstatistic;

import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.special.SpecialTool;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.special.event.EPSpecialDeduct;
import com.bbw.god.gameuser.special.event.SpecialDeductEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author suchaobin
 * @description: 区服特产统计，具体到某个特产
 * @date 2019/12/12 16:35
 */
@Component
@Async
public class ServerSpecialListener {
  @Autowired
  private GameUserService gameUserService;

  @Autowired
  private GodServerStatisticService godServerStatisticService;

  @EventListener
  @Order(1000)
  public void specialStatistic(SpecialDeductEvent event) {
    EPSpecialDeduct ep = event.getEP();
    if (!ep.getWay().equals(WayEnum.TRADE)) {
      return;
    }
    List<EPSpecialDeduct.SpecialInfo> specialInfoList = ep.getSpecialInfoList();
    int sid = gameUserService.getActiveSid(ep.getGuId());
    // 根据特产id分类，获取到对应的总成本，总售价，交易次数后再存放到redis
    Map<Integer, List<EPSpecialDeduct.SpecialInfo>> collect = specialInfoList.stream().collect(Collectors.groupingBy(
            EPSpecialDeduct.SpecialInfo::getBaseSpecialIds));
    Set<Integer> specialKeySet = collect.keySet();
    for (Integer specialId : specialKeySet) {
      String specialName = SpecialTool.getSpecialById(specialId).getName();
      List<EPSpecialDeduct.SpecialInfo> infoList = collect.get(specialId);
      int totalBuyPrice = infoList.stream().mapToInt(EPSpecialDeduct.SpecialInfo::getBuyPrice).sum();
      int totalSellPrice = infoList.stream().mapToInt(EPSpecialDeduct.SpecialInfo::getSellPrice).sum();
      godServerStatisticService.specialStatistic(sid, "总成本", totalBuyPrice, specialName);
      godServerStatisticService.specialStatistic(sid, "总售价", totalSellPrice, specialName);
      godServerStatisticService.specialStatistic(sid, "交易次数", infoList.size(), specialName);
    }
  }
}
