package com.bbw.god.gameuser.yaozu;

import com.bbw.common.DateUtil;
import com.bbw.common.PowerRandom;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.cache.TimeLimitCacheUtil;
import com.bbw.god.city.UserCityService;
import com.bbw.god.city.event.CityEventPublisher;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.city.CfgRoadEntity;
import com.bbw.god.game.config.city.CfgGodRoadEntity;
import com.bbw.god.game.config.city.CityTool;
import com.bbw.god.game.config.city.RoadTool;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.statistic.StatisticServiceFactory;
import com.bbw.god.gameuser.statistic.StatisticTypeEnum;
import com.bbw.god.gameuser.statistic.behavior.BehaviorStatisticService;
import com.bbw.god.gameuser.statistic.behavior.BehaviorType;
import com.bbw.god.gameuser.statistic.behavior.yaozu.YaoZuStatistic;
import com.bbw.god.gameuser.statistic.behavior.yaozu.YaoZuStatisticService;
import com.bbw.god.gameuser.yaozu.rd.RDYaoZuPos;
import com.bbw.god.gameuser.yaozu.rd.RDYaoZuPoses;
import com.bbw.god.server.god.ServerGod;
import com.bbw.god.server.god.ServerGodDayConfigService;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.implementation.bytecode.Throw;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 妖族生成处理器
 *
 * @author fzj
 * @date 2021/9/6 17:06
 */
@Slf4j
@Service
public class YaoZuGenerateProcessor {

    @Autowired
    UserCityService userCityService;
    @Autowired
    GameUserService gameUserService;
    @Autowired
    ServerGodDayConfigService serverGodDayConfigService;
    @Autowired
    UserYaoZuInfoService userYaoZuInfoService;
    @Autowired
    YaoZuStatisticService yaoZuStatisticService;
    @Autowired
    StatisticServiceFactory statisticServiceFactory;
    @Autowired
    YaoZuLogic yaoZuLogic;


    /** 所有妖族枚举集合 */
    private static final List<YaoZuEnum> YAOZU_ENUM_LIST = Arrays.asList(YaoZuEnum.YE_ZHU_YAO, YaoZuEnum.GOU_DA_XIAN, YaoZuEnum.PI_PA_JING, YaoZuEnum.ZHI_JI_JING, YaoZuEnum.YAO_HU_XIAN);
    /** 所有城池类型属性集合 */
    private static final List<Integer> cityTypes = Arrays.asList(10, 20, 30, 40, 50);
    /** 界碑位置 */
    private static final List<Integer> jieBeiPos = Arrays.asList(1732, 1815, 3515, 3633);

    /**
     * 判断是否通关妖族来犯
     *
     * @param uid
     * @return
     */
    public boolean isPassYaoZu(Long uid) {
        BehaviorStatisticService service = statisticServiceFactory.getByBehaviorType(BehaviorType.YAO_ZU_WIN);
        YaoZuStatistic statistic = service.fromRedis(uid, StatisticTypeEnum.NONE, DateUtil.getTodayInt());
        return statistic.getTotal() >= YaoZuTool.getAllYaoZu().size();
    }

    /**
     * 生成妖族
     *
     * @param uid
     * @return
     */
    public RDYaoZuPoses generateYaoZuByUserId(long uid) {
        //判断是否统一梦魇世界
        if (CityTool.getCcCount() != userCityService.getUserOwnNightmareCities(uid).size() && !isPassYaoZu(uid)) {
            throw new ExceptionForClientTip("梦魇世界未统一！");
        }
        // 防止重复生成
        if (userYaoZuInfoService.getUserYaoZu(uid).size() > 0 || isPassYaoZu(uid)){
            throw new ExceptionForClientTip("不能重复生成妖族！");
        }
        List<CfgRoadEntity> roads = RoadTool.getRoads();
        //获取当日神仙
        List<ServerGod> todayGods = serverGodDayConfigService.getTodayGods(gameUserService.getActiveSid(uid));
        //排除神仙,界碑和玩家自身的位置
        List<CfgRoadEntity> notGodsAndJieBeiAndUserPos = excludeGodAndUserPos(uid, todayGods, roads);
        //生成所有妖族
        List<UserYaoZuInfo> userYaoZuInfo = new ArrayList<>();
        //已有妖族的位置
        List<Integer> existYaoZuPos = new ArrayList<>();
        for (int i = 0; i < YAOZU_ENUM_LIST.size(); i++) {
            List<CfgYaoZuEntity> yaoZuTypeList = YaoZuTool.getYaoZuByYaoZuType(YAOZU_ENUM_LIST.get(i).getType());
            //洗牌
            Collections.shuffle(yaoZuTypeList);
            int index = 0;
            for (int cityType : cityTypes) {
                //获得指定区域的位置集合
                List<Integer> notGodsAndUserCityPos = notGodsAndJieBeiAndUserPos.stream().filter(pos -> pos.getCountry() == cityType)
                        .map(CfgRoadEntity::getId).collect(Collectors.toList());
                //排除已经存在妖族的位置
                List<Integer> notGodsAndUserAndYaoZuPos = new ArrayList<>();
                for (int Pos : notGodsAndUserCityPos){
                    if (!existYaoZuPos.contains(Pos)){
                        notGodsAndUserAndYaoZuPos.add(Pos);
                    }
                }
                //获得随机的位置
                List<Integer> randomsPos = PowerRandom.getRandomsFromList(notGodsAndUserAndYaoZuPos, yaoZuTypeList.size() / cityTypes.size());
                //更新妖族位置
                for (int a = 0; a < randomsPos.size(); a++) {
                    int yaoZuId = yaoZuTypeList.get(index).getYaoZuId();
                    UserYaoZuInfo userYaoZu = UserYaoZuInfo.getInstance(uid, yaoZuId, randomsPos.get(a));
                    existYaoZuPos.add(userYaoZu.getPosition());
                    userYaoZuInfo.add(userYaoZu);
                    index++;
                }
            }
        }
        gameUserService.addItems(userYaoZuInfo);
        RDYaoZuPoses rd = new RDYaoZuPoses();
        rd.setYaoZuPos(userYaoZuInfo.stream().map(yz -> new RDYaoZuPos(yz)).collect(Collectors.toList()));
        return rd;
    }



    /**
     * 撤退生成新的妖族
     *
     * @param uid
     * @param
     */
    public RDYaoZuPos retreat(long uid) {
        ArriveYaoZuCache cache = TimeLimitCacheUtil.getArriveCache(uid, ArriveYaoZuCache.class);
        int yaoZuId = cache.getYaoZuId();
        UserYaoZuInfo yaoZuInfo = userYaoZuInfoService.getUserYaoZuInfo(uid, yaoZuId);
        yaoZuInfo = yaoZuLogic.isExistYaoZuData(uid, yaoZuInfo, cache.getYaoZuId());
        //进度重置
        yaoZuInfo.setProgress(0);
        //获取所有位置
        List<CfgRoadEntity> roads = RoadTool.getRoads();
        //获取当日神仙
        List<ServerGod> todayGods = serverGodDayConfigService.getTodayGods(gameUserService.getActiveSid(uid));
        //排除神仙和玩家自身的位置
        List<Integer> notGodsAndJieBeiAndUserPos = excludeGodAndUserPos(uid, todayGods, roads)
                .stream().map(CfgRoadEntity::getId).collect(Collectors.toList());
        //排除妖族位置
        List<Integer> notYaoZuPos = excludeYaoZuPos(uid, notGodsAndJieBeiAndUserPos);
        //在没有神仙，玩家和妖族的位置上随机一个新的位置
        Integer newRandomPos = PowerRandom.getRandomFromList(notYaoZuPos);
        //更新位置
        RDYaoZuPos rd = new RDYaoZuPos();
        yaoZuInfo.setPosition(newRandomPos);
        gameUserService.updateItem(yaoZuInfo);
        //撤退触发格子事件
        int userPos = gameUserService.getGameUser(uid).getLocation().getPosition();
        // 到达
        CityEventPublisher.publCityArriveEvent(uid, userPos, WayEnum.NONE, rd);
        rd.setId(yaoZuId);
        rd.setPosition(newRandomPos);
        return rd;
    }

    /**
     * 排除神仙,界碑和玩家自身的位置
     *
     * @param
     * @param roads
     * @return
     */
    public List<CfgRoadEntity> excludeGodAndUserPos(long uid, List<ServerGod> todayGods, List<CfgRoadEntity> roads) {
        List<CfgRoadEntity> notGodsAndUserPos = new ArrayList<>();
        List<Integer> godPos = todayGods.stream().map(ServerGod::getPosition).collect(Collectors.toList());
        List<Integer> godRoad = RoadTool.getGodRoads().stream().map(CfgGodRoadEntity::getId).collect(Collectors.toList());
        int userPos = gameUserService.getGameUser(uid).getLocation().getPosition();
        for (CfgRoadEntity roadPos : roads) {
            int roadPosition = roadPos.getId();
            boolean containsJieBei = jieBeiPos.contains(roadPosition);
            boolean containsSpecialRoad = godRoad.contains(roadPosition);
            if (!godPos.contains(roadPosition) && roadPosition != userPos && !containsJieBei && !containsSpecialRoad){
                notGodsAndUserPos.add(roadPos);
            }
        }
        return notGodsAndUserPos;
    }

    /**
     * 排除已经存在妖族的位置
     *
     * @param uid
     * @param notGodsAndUserCityPos
     * @return
     */
    public List<Integer> excludeYaoZuPos(long uid, List<Integer> notGodsAndUserCityPos) {
        List<Integer> notGodsAndUserAndYaoZuPos = new ArrayList<>();
        List<Integer> userYaoZuPos = userYaoZuInfoService.getUserYaoZu(uid).stream().map(UserYaoZuInfo::getPosition).collect(Collectors.toList());
        if (userYaoZuPos.isEmpty()) {
            return notGodsAndUserCityPos;
        }
        for (int pos : notGodsAndUserCityPos){
            if (!userYaoZuPos.contains(pos)){
                notGodsAndUserAndYaoZuPos.add(pos);
            }
        }
        return notGodsAndUserAndYaoZuPos;
    }
}
