package com.bbw.god.server.flx;

import com.bbw.common.DateUtil;
import com.bbw.common.ListUtil;
import com.bbw.god.db.entity.CfgServerEntity;
import com.bbw.god.game.config.Cfg;
import com.bbw.god.game.config.card.CfgCardEntity;
import com.bbw.god.game.flx.FlxDayResult;
import com.bbw.god.game.flx.FlxDayResultService;
import com.bbw.god.random.config.RandomStrategy;
import com.bbw.god.random.service.RandomCardService;
import com.bbw.god.random.service.RandomParam;
import com.bbw.god.random.service.RandomResult;
import com.bbw.god.server.PrepareServerDataService;
import com.bbw.god.server.ServerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-03-19 11:30
 */
@Slf4j
@Service
public class ServerFlxResultService extends PrepareServerDataService<ServerFlxResult> {
    private int no_repeat_days = 10;// 10天内不重复结果
    @Autowired
    private FlxDayResultService gameResultService;
    @Autowired
    private ServerService serverService;

    private static final String COMMON_DATE_KEY = "福临轩_普通日期";
    private static final String SPECIAL_DATE_KEY = "福临轩_特殊日期";
    private static final String SPECIAL_DATE_BEFORE_WEEKS_KEY = "福临轩_前6周特殊日期";
    // 选牌策略集
    private static final String[] RND_STRATEGY = {COMMON_DATE_KEY, SPECIAL_DATE_KEY, SPECIAL_DATE_BEFORE_WEEKS_KEY};

    @Override
    protected void generateByDate(int sid, int dateInt) {
        // 获取今天的数据
        Optional<FlxDayResult> gameResult = gameResultService.getResultByDate(dateInt);
        if (!gameResult.isPresent()) {
            // 生成数据，并重新赋值
            gameResultService.check(DateUtil.fromDateInt(dateInt));
            gameResult = gameResultService.getResultByDate(dateInt);
        }
        // 最近no_repeat_days天的结果，避免短期重复
        List<String> existsDatas = getCardNamesInDays(sid, no_repeat_days);
        RandomParam param = new RandomParam();
        param.set("$排除卡牌", existsDatas);
        String strategyKey = getStrategy(sid, dateInt);
        RandomStrategy strategy = RandomCardService.getSetting(strategyKey);
        RandomResult randomResult = RandomCardService.getRandomList(strategy, param);
        Optional<CfgCardEntity> card = randomResult.getFirstCard();
        ServerFlxResult serverResult = ServerFlxResult.fromFlxDayResult(sid, gameResult.get(), card.get());
        serverData.addServerData(serverResult);
    }

    private List<String> getCardNamesInDays(int sid, int days) {
        List<ServerFlxResult> results = getResultInDays(sid, days);
        List<String> all = new ArrayList<>();
        for (int i = 0; i < results.size(); i++) {
            all.add(results.get(i).getAwardCardName());
        }
        return all;
    }

    /**
     * 获得今日福临轩的结果数据
     *
     * @return
     */
    public Optional<ServerFlxResult> getTodayResult(int sid) {
        return getSingleResultByDate(sid, DateUtil.toDateInt(DateUtil.now()));
    }

    /**
     * 获取前几天的结果数据
     *
     * @param days
     * @return
     */
    public List<ServerFlxResult> getResultInDays(int sid, int days) {
        // 前几天的数据，不包含今天
        Date today = DateUtil.now();
        //
        List<ServerFlxResult> noToday = new ArrayList<>();
        // 不包含今天
        for (int i = 1; i <= days; i++) {
            Date preDay = DateUtil.addDays(today, -i);
            Optional<ServerFlxResult> result = this.getSingleResultByDate(sid, DateUtil.toDateInt(preDay));
            // 有数据,则添加
            if (result.isPresent()) {
                noToday.add(result.get());
            }
        }
        //
        noToday = noToday.stream().filter(result -> result.getDateInt() != DateUtil.toDateInt(today))
                .collect(Collectors.toList());
        return noToday;
    }

    // 根据日期获取选牌策略
    private String getStrategy(int sid, int dateInt) {
        boolean isNotEndWith31 = !(dateInt + "").endsWith("31");
        boolean isEndWith1 = 1 == (dateInt % 10) && isNotEndWith31;
        if (isEndWith1) {
            Date date = DateUtil.fromDateInt(dateInt);
            int openWeek = serverService.getOpenWeek(sid, date);
            if (openWeek <= 6) {
                return SPECIAL_DATE_BEFORE_WEEKS_KEY;
            }
            return SPECIAL_DATE_KEY;
        }
        return COMMON_DATE_KEY;
    }

    @Override
    public boolean check(CfgServerEntity server, Date date) {
        boolean b = true;
        for (String strategyKey : RND_STRATEGY) {
            RandomStrategy ss = Cfg.I.get(strategyKey, RandomStrategy.class);
            if (null == ss) {
                log.error("错误!!!福临轩没有[" + strategyKey + "]随机策略数据!");
            }
            b = b && (null != ss);
        }
        log.info("---------------开始对 [" + server.getName() + "][" + DateUtil.toDateString(date)
                + "]的数据进行健康检查-----------------");
        Optional<ServerFlxResult> result = getSingleResultByDate(server.getMergeSid(), DateUtil.toDateInt(date));
        if (result.isPresent()) {
            log.info("[" + server.getName() + "][" + DateUtil.toDateString(date) + "]福临轩数据检查通过!");
        } else {
            log.error("错误!!!没有[" + server.getName() + "][" + DateUtil.toDateString(date) + "]福临轩数据!");
            generateByDate(server.getMergeSid(), DateUtil.toDateInt(date));
            log.info("生成区服[" + server.getName() + "][" + DateUtil.toDateString(date) + "]福临轩数据!");
            b = false;
        }

        return b;
    }

    @Override
    protected String getLoopKeyByDate(Date date) {
        return null;
    }

    public Optional<ServerFlxResult> getSingleResultByDate(int sid, int dateInt) {
        List<ServerFlxResult> list = getResultByDate(sid, dateInt);
        if (ListUtil.isEmpty(list)) {
            return Optional.empty();
        } else {
            return Optional.of(list.get(0));
        }
    }

    /**
     * 获取指定日期的数据
     *
     * @param sid
     * @param dateInt
     * @return
     */
    @Override
    @NonNull
    public List<ServerFlxResult> getResultByDate(int sid, int dateInt) {
        List<ServerFlxResult> results = serverData.getServerDatas(sid, getTClass());
        List<ServerFlxResult> todayObj = results.stream().filter(sfr -> sfr.getDateInt() == dateInt)
                .collect(Collectors.toList());
        //		if (todayObj.isEmpty()) {
        //			String msg = "获取不到[" + dateInt + "]的[" + getTClass().getSimpleName() + "]数据！";
        //			log.error(msg);
        //		}
        return todayObj;
    }
}
