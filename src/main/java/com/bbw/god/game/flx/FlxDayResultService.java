package com.bbw.god.game.flx;

import com.bbw.common.DateUtil;
import com.bbw.common.PowerRandom;
import com.bbw.god.PrepareDataService;
import com.bbw.god.game.config.TypeEnum;
import com.bbw.god.game.data.GameDataID;
import com.bbw.god.game.data.GameDayDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-03-19 11:30
 */
@Slf4j
@Service
public class FlxDayResultService implements PrepareDataService {
    @Autowired
    private GameDayDataService dataService;
    @Autowired
    private FlxYYLTipService flxYYLtipService;

    /**
     * 生成今天之后days天的数据
     *
     * @param days
     */
    @Override
    public void prepareDatas(int days) {
        Date today = DateUtil.now();
        //所有福临轩的数据
        List<FlxDayResult> results = dataService.getGameData(FlxDayResult.class);
        //找出目前缺少的数据日期
        List<Integer> noDataDateInt = new ArrayList<Integer>();
        //最多不超过一个月
        int maxDays = days > 31 ? 31 : days;
        for (int i = 0; i < maxDays; i++) {
            Date nextDay = DateUtil.addDays(today, i);
            //没有数据
            if (!results.stream().filter(result -> result.getDateInt() == DateUtil.toDateInt(nextDay)).findFirst().isPresent()) {
                noDataDateInt.add(DateUtil.toDateInt(nextDay));
            }
        }
        //生成数据
        List<FlxDayResult> newResults = generateResultByDate(noDataDateInt);
        dataService.addGameDatas(newResults);
    }

    private List<FlxDayResult> generateResultByDate(List<Integer> noDataDateInt) {
        //生成数据
        List<FlxDayResult> results = new ArrayList<>();
        for (int i = 0; i < noDataDateInt.size(); i++) {
            FlxDayResult result = generateResultByDate(noDataDateInt.get(i));
            results.add(result);
        }
        return results;
    }

    //生成某一天的福临轩结果
    private FlxDayResult generateResultByDate(Integer nextDay) {
        FlxDayResult result = new FlxDayResult();
        result.setId(GameDataID.generateDataIdByConfig(DateUtil.fromDateInt(nextDay), GameDataID.ConfigDataTypeEnum.FLX, 1));
        result.setDateInt(nextDay);
        result.setSgNum(PowerRandom.getRandomBetween(1, 36));
        result.setYsgBet1(TypeEnum.randomTypeVal());
        result.setYsgBet2(TypeEnum.randomTypeVal());
        result.setYsgBet3(TypeEnum.randomTypeVal());
        return result;
    }

    /**
     * 获取指定日期的福临轩数据
     *
     * @param date
     * @return
     */
    @NonNull
    public Optional<FlxDayResult> getResultByDate(int date) {
        //所有记录
        List<FlxDayResult> results = dataService.getGameData(FlxDayResult.class);
        Optional<FlxDayResult> result = results.stream().filter(tmp -> tmp.getDateInt() == date).findFirst();
        if (!result.isPresent()) {
            String msg = "获取不到全服[" + date + "]的福临轩数据！";
            log.warn(msg);
        }
        return result;
    }

    @Override
    public boolean check(Date date) {
        log.info("---------------开始对 全服[" + DateUtil.toDateString(date) + "]的开奖数据进行健康检查-----------------");
        Optional<FlxDayResult> result = getResultByDate(DateUtil.toDateInt(date));
        if (result.isPresent()) {
            log.info("全服[" + DateUtil.toDateString(date) + "]福临轩开奖数据检查通过!");
        } else {
            log.error("错误!!!全服没有[" + DateUtil.toDateString(date) + "]福临轩开奖数据!");
            List<FlxDayResult> results = generateResultByDate(Arrays.asList(DateUtil.toDateInt(date)));
            dataService.addGameDatas(results);
            log.info("生成全服[" + DateUtil.toDateString(date) + "]福临轩开奖数据!");
            flxYYLtipService.checkAndGenerateTips();
        }
        boolean b = null != result;
        return b;

    }

}
