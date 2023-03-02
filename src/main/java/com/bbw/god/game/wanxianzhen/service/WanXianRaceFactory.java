package com.bbw.god.game.wanxianzhen.service;

import com.bbw.common.DateUtil;
import com.bbw.god.game.wanxianzhen.service.race.AbstractWanXianRace;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author lwb
 * @date 2020/5/8 9:41
 */
@Service
@Slf4j
public class WanXianRaceFactory {
    @Autowired
    private List<AbstractWanXianRace> wanXianRaceServerList;

    public AbstractWanXianRace match(int wanXianType){
        for (AbstractWanXianRace wanXianRace:wanXianRaceServerList){
            if (wanXianRace.getWanxianType()==wanXianType){
                return wanXianRace;
            }
        }
        log.error("找不到对应的万仙阵赛事，错误的时间："+wanXianType);
        return null;
    }

    public AbstractWanXianRace matchByWeekDay(int weekday){
        for (AbstractWanXianRace wanXianRace:wanXianRaceServerList){
            if (wanXianRace.todayRace(weekday)){
                return wanXianRace;
            }
        }
        log.error("找不到对应的万仙阵赛事，错误的时间："+weekday);
        return null;
    }
    public AbstractWanXianRace todayRace(){
        return matchByWeekDay(DateUtil.getToDayWeekDay());
    }
}
