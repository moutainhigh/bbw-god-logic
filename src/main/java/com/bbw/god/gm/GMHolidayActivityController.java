package com.bbw.god.gm;

import com.bbw.god.activity.holiday.processor.HolidayDigForTreasureProcessor;
import com.bbw.god.activity.holiday.processor.HolidayHorseRacingProcessor;
import com.bbw.god.job.game.HolidayHorseJob;
import com.bbw.god.rd.RDSuccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * 节日活动修复
 * @author：lwb
 * @date: 2021/3/9 15:33
 * @version: 1.0
 */
@RestController
@RequestMapping("gm/holiday/")
public class GMHolidayActivityController {
    @Autowired
    private HolidayHorseRacingProcessor horseRacingProcessor;
    @Autowired
    private HolidayDigForTreasureProcessor digForTreasureProcessor;
    @Autowired
    private HolidayHorseJob holidayHorseJob;

    @RequestMapping("horseRacing!settle")
    public RDSuccess horseRacingSettle(int gid,long settleData){
        System.err.println("开始重新结算赛马结果，轮次："+settleData+",平台号："+gid);
        horseRacingProcessor.settleBySettleDate(gid,settleData);
        System.err.println("结算完成："+settleData+",平台号："+gid);
        return new RDSuccess();
    }

    @RequestMapping("horseRacing!settle2")
    public RDSuccess horseRacingSettle(int gid){
        System.err.println("开始重新结算赛马结果，轮次：,平台号："+gid);
        horseRacingProcessor.settleNowByGid(gid);
        System.err.println("结算完成：,平台号："+gid);
        return new RDSuccess();
    }

    @RequestMapping("digForTreasure!init")
    public RDSuccess initDigForTreasure(int gid){
        System.err.println("初始化节日挖宝,平台号："+gid);
        digForTreasureProcessor.initMapData(gid);
        System.err.println("初始化节日挖宝完成,平台号："+gid);
        return new RDSuccess();
    }

    @RequestMapping("horseRacing!bet")
    public RDSuccess bet(long uid,int number,int multiple){
        horseRacingProcessor.bet(uid,number,multiple);
        return new RDSuccess();
    }

    @RequestMapping("horseRacing!job")
    public RDSuccess job(){
        holidayHorseJob.doJob();
        return new RDSuccess();
    }
}
