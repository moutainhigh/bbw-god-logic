package com.bbw.god.gameuser.businessgang.digfortreasure;

import com.bbw.common.CloneUtil;
import com.bbw.common.DateUtil;
import com.bbw.common.ListUtil;
import com.bbw.common.PowerRandom;
import com.bbw.god.PrepareDataService;
import com.bbw.god.game.config.city.RoadTool;
import com.bbw.god.game.config.server.ServerTool;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTimeConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 每周挖宝配置服务
 *
 * @author: huanghb
 * @date: 2023/1/13 14:18
 */
@Slf4j
@Service
public class GameWeekDigTreasureConfigService implements PrepareDataService {
    @Autowired
    private GameDigTreasureService gameDigTreasureService;
    /** 神魔专服id */
    private int GODS_AND_DEMONS_GROUP_Id = 17;

    /**
     * 生成今天之后days天的数据
     *
     * @param days
     */
    @Override
    public void prepareDatas(int days) {
        //获得所有区服组挖宝信息
        List<GameDigTreasure> digTreasures = gameDigTreasureService.getGameDigTreasures();
        //需要修复数据
        List<GameDigTreasure> needFixDigTreasures = new ArrayList<>();
        //获取所有有效区服组
        List<Integer> serverGroups = getAllVaildServerGroup();
        for (Integer serverGroup : serverGroups) {
            List<GameDigTreasure> gameDigTreasures = new ArrayList<>();
            for (int day = 0; day < days; day += DateTimeConstants.DAYS_PER_WEEK) {
                Date generationDateTime = DateUtil.addSimpleDays(DateUtil.now(), day);
                //获得挖宝全服数据
                Optional<GameDigTreasure> gameDigTreasure = digTreasures.stream().filter(tmp -> tmp.isMatchServerGroup(serverGroup) && tmp.isMatchWeek(generationDateTime)).findFirst();
                if (gameDigTreasure.isPresent()) {
                    continue;
                }
                GameDigTreasure newDigTreasure = generateDigTreasureInfo(serverGroup, generationDateTime);
                gameDigTreasures.add(newDigTreasure);
            }
            if (ListUtil.isEmpty(gameDigTreasures)) {
                log.info("区服组{}{}天之后挖宝数据检查通过", serverGroup, days);
                continue;
            }
            log.info("开始生成区服组{}{}天之后挖宝数据", serverGroup, days);
            needFixDigTreasures.addAll(gameDigTreasures);
            log.info("生成区服组{}{}天之后挖宝数据成功", serverGroup, days);
        }
        if (ListUtil.isEmpty(needFixDigTreasures)) {
            log.info("{}天内挖宝数据检查通过", days);
            return;
        }
        gameDigTreasureService.addGameDigTreasures(needFixDigTreasures);
        log.info("生成{}天内挖宝数据成功", days);
    }

    /**
     * 生成区服组挖宝信息
     *
     * @param serverGroup        区服组id
     * @param generationDateTime 生成时间
     * @return
     */
    private GameDigTreasure generateDigTreasureInfo(int serverGroup, Date generationDateTime) {
        //挖宝规则:挖宝周期从这周一8点到下周一8点
        Date beginTimeReduceHour = DateUtil.addHours(generationDateTime, -DigTreasureTool.getWeekBeginHour());
        Date weekBeginDateTime = DateUtil.getWeekBeginDateTime(beginTimeReduceHour);
        Date weekBeginTimeAddHour = DateUtil.addHours(weekBeginDateTime, +DigTreasureTool.getWeekBeginHour());
        //生成每层奖池
        List<Integer> digTreasureIdPool = new ArrayList<>();

        //生成区服组挖宝数据
        List<CfgDigTreasure.Floor> floors = DigTreasureTool.getCfg().getFloors();
        //楼层奖励分布信息
        List<FloorAward> floorAwardInfos = new ArrayList<>();
        for (CfgDigTreasure.Floor floor : floors) {
            //随机获得本次本楼层奖励id集合
            List<Integer> floorAwardIds = DigTreasureTool.getFloorTreasureTroveIds(floor.getId());
            digTreasureIdPool.addAll(floorAwardIds);
            List<Integer> floorTreasureTroveIds = PowerRandom.getRandomsFromList(digTreasureIdPool, floor.getTreasureTroveNum());
            //填充空奖励
            int rodaSize = RoadTool.getRoads().size();
            int emptyAwardIdNum = rodaSize - floor.getTreasureTroveNum();
            List<Integer> emptyAwardIds = Stream.generate(() -> 0).limit(emptyAwardIdNum).collect(Collectors.toList());
            floorTreasureTroveIds.addAll(emptyAwardIds);
            //洗牌
            Collections.shuffle(floorTreasureTroveIds);
            //保存本周奖励id集合
            FloorAward floorAward = FloorAward.instance(floor.getId(), floorTreasureTroveIds);
            floorAwardInfos.add(floorAward);
            //移除已添加到楼层的奖励id
            digTreasureIdPool.removeAll(floorTreasureTroveIds);
        }
        return GameDigTreasure.getInstance(serverGroup, floorAwardInfos, weekBeginTimeAddHour);
    }

    @Override
    public boolean check(Date date) {
        //是否周一
        Date dateReduceHours = DateUtil.addHours(date, -DigTreasureTool.getWeekBeginHour());
        boolean isWeekBeginTime = DateUtil.isWeekBeginDate(dateReduceHours);
        //是否需要检查数据
        boolean isNeedCheck = DateUtil.isToday(date) || isWeekBeginTime;
        if (!isNeedCheck) {
            return true;
        }
        log.info("---------------开始对 所有区服组[" + DateUtil.toDateString(date) + "]的挖宝信息数据进行健康检查-----------------");
        List<GameDigTreasure> digTreasures = gameDigTreasureService.getGameDigTreasuresByDate(date);
        List<Integer> serverGroups = getAllVaildServerGroup();
        List<GameDigTreasure> needFixDigTreasures = new ArrayList<>();
        for (Integer serverGroup : serverGroups) {
            List<GameDigTreasure> serverGroupNeedFixDigTreasures = new ArrayList<>();
            Optional<GameDigTreasure> gameDigTreasure = digTreasures.stream().filter(tmp -> tmp.isMatchServerGroup(serverGroup)).findFirst();
            if (gameDigTreasure.isPresent()) {
                log.info("区服组{}[" + DateUtil.toDateString(date) + "]挖宝数据检查通过!", serverGroup);
                continue;
            }
            log.error("错误!!!区服组{}没有[" + DateUtil.toDateString(date) + "]挖宝数据!", serverGroup);
            //生成挖宝数据
            GameDigTreasure gameDigTreasureData = generateDigTreasureInfo(serverGroup, date);
            serverGroupNeedFixDigTreasures.add(gameDigTreasureData);
            log.info("---------------对 所有区服组[" + DateUtil.toDateString(dateReduceHours) + "]的挖宝信息数据进行修复完成-----------------");
            needFixDigTreasures.addAll(serverGroupNeedFixDigTreasures);

        }
        if (ListUtil.isNotEmpty(needFixDigTreasures)) {
            gameDigTreasureService.addGameDigTreasures(needFixDigTreasures);
            log.info("生成所有区服组[" + DateUtil.toDateString(dateReduceHours) + "]的挖宝信息数据");
            return needFixDigTreasures.isEmpty();
        }
        log.info("---------------对 所有区服组[" + DateUtil.toDateString(dateReduceHours) + "]的挖宝信息数据进行检查完成-----------------");
        return needFixDigTreasures.isEmpty();
    }

    /**
     * 获取所有有效区服组
     *
     * @return
     */
    private List<Integer> getAllVaildServerGroup() {
        List<Integer> serverGroups = ServerTool.getServerGroups();
        List<Integer> cloneServerGroups = CloneUtil.cloneList(serverGroups);
        cloneServerGroups.removeIf(tmp -> tmp == GODS_AND_DEMONS_GROUP_Id);
        return cloneServerGroups;
    }
}
