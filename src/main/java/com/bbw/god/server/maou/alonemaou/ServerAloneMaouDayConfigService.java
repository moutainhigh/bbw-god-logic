package com.bbw.god.server.maou.alonemaou;

import com.bbw.common.DateUtil;
import com.bbw.common.ListUtil;
import com.bbw.common.PowerRandom;
import com.bbw.god.db.entity.CfgServerEntity;
import com.bbw.god.game.config.Cfg;
import com.bbw.god.game.config.CfgAloneMaou;
import com.bbw.god.server.PrepareServerDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 独战魔王生成服务
 *
 * @author 1061434963@qq.com
 * @version 1.0.0
 * @date 2019-12-18 14:14
 */
@Slf4j
@Service
public class ServerAloneMaouDayConfigService extends PrepareServerDataService<ServerAloneMaou> {
    private int no_repeat_days = 3;// 3天内不重复结果

    @Override
    protected void clearVar() {
        super.clearVar();
    }

    @Override
    protected void generateByDate(int sid, int dateInt) {
        // 最近no_repeat_days天的结果，避免短期重复
        List<Integer> existsDatas = getTypesBeforeDays(sid, this.no_repeat_days, dateInt);
        int maouType = 0;
        do {
            maouType = PowerRandom.getRandomBySeed(5) * 10;
        } while (existsDatas.contains(maouType));
        CfgAloneMaou config = Cfg.I.getUniqueConfig(CfgAloneMaou.class);
        ServerAloneMaou maou = ServerAloneMaou.getInstance(sid, maouType, dateInt, config);
        this.serverData.addServerData(maou);
    }

    private List<Integer> getTypesBeforeDays(int sid, int days, int sinceDate) {
        List<ServerAloneMaou> results = getResultBeforeDays(sid, days, sinceDate);
        List<Integer> all = new ArrayList<>();
        for (int i = 0; i < results.size(); i++) {
            all.add(results.get(i).getType());
        }
        return all;
    }

    /**
     * 获取前几天的结果数据,不包括sinceDate
     *
     * @param days
     * @return
     */
    public List<ServerAloneMaou> getResultBeforeDays(int sid, int days, int sinceDateInt) {
        // 前几天的数据，不包含今天
        Date sinceDate = DateUtil.fromDateInt(sinceDateInt);
        //
        List<ServerAloneMaou> noSinceDate = new ArrayList<>();
        // 不包含
        for (int i = 1; i <= days; i++) {
            Date preDay = DateUtil.addDays(sinceDate, -i);
            List<ServerAloneMaou> result = this.getResultByDate(sid, DateUtil.toDateInt(preDay));
            if (!result.isEmpty()) {
                noSinceDate.addAll(result);
            }
        }
        return noSinceDate;
    }

    @Override
    public boolean check(CfgServerEntity server, Date date) {
        String loopKey = this.getLoopKeyByDate(date);
        List<ServerAloneMaou> maous = this.serverData.getServerDatas(server.getMergeSid(), ServerAloneMaou.class, loopKey);
        if (ListUtil.isEmpty(maous)) {
            log.warn("错误!!![" + server.getMergeSid() + "][" + server.getName() + "]区服没有[" + DateUtil.toDateString(date) + "]的独战魔王数据!");
            generateByDate(server.getMergeSid(), DateUtil.toDateInt(date));
            log.warn("生成[" + server.getMergeSid() + "][" + server.getName() + "]区服[" + DateUtil.toDateString(date) + "]的独战魔王数据!");
            return false;
        }
        return true;
    }

}
