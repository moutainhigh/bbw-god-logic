package com.bbw.god.server.maou.bossmaou;

import com.bbw.common.DateUtil;
import com.bbw.common.ListUtil;
import com.bbw.common.PowerRandom;
import com.bbw.god.db.entity.CfgServerEntity;
import com.bbw.god.game.config.Cfg;
import com.bbw.god.game.config.CfgBossMaou;
import com.bbw.god.game.config.card.CfgCardEntity;
import com.bbw.god.game.config.treasure.CfgTreasureEntity;
import com.bbw.god.game.config.treasure.TreasureTool;
import com.bbw.god.random.config.RandomStrategy;
import com.bbw.god.random.service.RandomCardService;
import com.bbw.god.random.service.RandomParam;
import com.bbw.god.random.service.RandomResult;
import com.bbw.god.server.PrepareServerDataService;
import com.bbw.god.server.maou.bossmaou.attackinfo.ServerBosssMaouRoundService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 每日魔王Boss配置服务
 *
 * @author 1061434963@qq.com
 * @version 1.0.0
 * @date 2019-12-18 14:14
 */
@Slf4j
@Service
public class ServerBossMaouDayConfigService extends PrepareServerDataService<ServerBossMaou> {
    private int no_repeat_common_days = 5;// 5天内不重复卡牌
    private int no_repeat_specials_days = 13;// 13天内不重复限定卡牌
    @Autowired
    private ServerBosssMaouRoundService bosssMaouRoundService;

    @Override
    protected void clearVar() {
        super.clearVar();
    }

    @Override
    protected void generateByDate(int sid, int dateInt) {
        // 最近几天的结果，避免短期重复
        CfgBossMaou config = BossMaouTool.getConfig();
        List<ServerBossMaou> sbms = this.serverData.getServerDatas(sid, ServerBossMaou.class, dateInt + "");
        Map<Integer, List<CfgBossMaou.BossMaou>> bossMaouGroup = config.getMaous().stream().collect(Collectors.groupingBy(CfgBossMaou.BossMaou::getAttackTime));
        Set<Integer> attackTimeSet = bossMaouGroup.keySet();
        attackTimeSet.forEach(tmp -> {
            List<CfgBossMaou.BossMaou> bossMaous = bossMaouGroup.get(tmp);
            CfgCardEntity commonCard = getCommonCard(sid, dateInt);
            CfgCardEntity specialCard = getSpecialCard(sid, dateInt);
            CfgTreasureEntity killerTreasure = getKillerTreasure();
            for (CfgBossMaou.BossMaou bossMaou : bossMaous) {
                //已生成则跳过
                if (sbms.stream().anyMatch(sbm -> sbm.getBaseMaouId().intValue() == bossMaou.getId())) {
                    continue;
                }
                ServerBossMaou sbm = null;
                if (bossMaou.getMaouLevel() == BossMaouLevel.ShenYMZ.getValue() && specialCard != null) {
                    //高手区魔王且是特定日期
                    sbm = ServerBossMaou.instance(sid, bossMaou, dateInt, specialCard, killerTreasure);
                } else {
                    sbm = ServerBossMaou.instance(sid, bossMaou, dateInt, commonCard, killerTreasure);
                }
                this.serverData.addServerData(sbm);
                this.bosssMaouRoundService.initRoundData(sbm);
            }
        });
    }

    private List<String> getCardNamesBeforeDays(int sid, int days, int sinceDateInt) {
        List<ServerBossMaou> results = getResultBeforeDays(sid, days, sinceDateInt);
        List<String> all = new ArrayList<>();
        for (int i = 0; i < results.size(); i++) {
            all.add(results.get(i).getCardAward().toString());
        }
        return all;
    }

    /**
     * 获取前几天的结果数据，包含sinceDateInt
     *
     * @param days
     * @return
     */
    public List<ServerBossMaou> getResultBeforeDays(int sid, int days, int sinceDateInt) {
        Date sinceDate = DateUtil.fromDateInt(sinceDateInt);
        List<ServerBossMaou> noSinceDate = new ArrayList<>();
        // 包含今天
        for (int i = 0; i <= days; i++) {
            Date preDay = DateUtil.addDays(sinceDate, -i);
            List<ServerBossMaou> result = this.getResultByDate(sid, DateUtil.toDateInt(preDay));
            if (!result.isEmpty()) {
                noSinceDate.addAll(result);
            }
        }
        return noSinceDate;
    }

    // 随机获取一个奖励法宝
    protected CfgTreasureEntity getKillerTreasure() {
        CfgBossMaou bossMaou = Cfg.I.getUniqueConfig(CfgBossMaou.class);
        String treasureName = PowerRandom.getRandomFromList(bossMaou.getKillerAwards());
        CfgTreasureEntity treasureEntity = TreasureTool.getTreasureByName(treasureName);
        return treasureEntity;
    }

    @Override
    public boolean check(CfgServerEntity server, Date date) {
        String loopKey = this.getLoopKeyByDate(date);
        List<ServerBossMaou> bossMaous = this.serverData.getServerDatas(server.getMergeSid(), ServerBossMaou.class, loopKey);
        if (ListUtil.isEmpty(bossMaous) || bossMaous.size() < 4) {
            log.warn("错误!!![" + server.getMergeSid() + "][" + server.getName() + "]区服没有[" + DateUtil.toDateString(date) + "]的魔王Boss数据!");
            generateByDate(server.getMergeSid(), DateUtil.toDateInt(date));
            log.warn("生成[" + server.getMergeSid() + "][" + server.getName() + "]区服[" + DateUtil.toDateString(date) + "]的魔王Boss数据!");
            return false;
        }
        return true;
    }

    /**
     * 获得普通卡牌，no_repeat_common_days内不重复
     *
     * @param sid
     * @param dateInt
     * @return
     */
    private CfgCardEntity getCommonCard(int sid, int dateInt) {
        List<String> existsDatas = getCardNamesBeforeDays(sid, this.no_repeat_common_days, dateInt);
        RandomParam param = new RandomParam();
        param.set("$排除卡牌", existsDatas);
        String strategyKey = "魔王_常规日期";
        RandomStrategy strategy = RandomCardService.getSetting(strategyKey);
        log.info("========区服{},日期{}========", sid, dateInt);
        log.info("排除卡牌:" + existsDatas);
        log.info("strategy:" + strategy.toString());
        RandomResult randomResult = RandomCardService.getRandomList(strategy, param);
        return randomResult.getFirstCard().get();
    }

    /**
     * 获得限定卡牌，如果今日没有限定卡牌则返回空。no_repeat_specials_days内不重复
     *
     * @param sid
     * @param dateInt
     * @return
     */
    private CfgCardEntity getSpecialCard(int sid, int dateInt) {
        CfgBossMaou config = BossMaouTool.getConfig();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(DateUtil.fromDateInt(dateInt));
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        if (!config.getSpecialCardDates().contains(dayOfMonth)) {
            return null;
        }
        List<String> existsDatas = getCardNamesBeforeDays(sid, this.no_repeat_specials_days, dateInt);
        RandomParam param = new RandomParam();
        param.set("$排除卡牌", existsDatas);
        String strategyKey = "魔王_特殊日期";
        RandomStrategy strategy = RandomCardService.getSetting(strategyKey);
        log.info("========区服{},日期{}========", sid, dateInt);
        log.info("排除卡牌:" + existsDatas);
        log.info("strategy:" + strategy.toString());
        RandomResult randomResult = RandomCardService.getRandomList(strategy, param);
        return randomResult.getFirstCard().get();
    }

}
