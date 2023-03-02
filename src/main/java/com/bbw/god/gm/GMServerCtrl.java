package com.bbw.god.gm;

import com.bbw.common.DateUtil;
import com.bbw.common.HttpClientUtil;
import com.bbw.common.Rst;
import com.bbw.db.redis.RedisHashUtil;
import com.bbw.god.activity.server.ServerActivityGeneratorService;
import com.bbw.god.activityrank.server.ServerActivityRankGeneratorService;
import com.bbw.god.chat.ChatService;
import com.bbw.god.controller.AbstractController;
import com.bbw.god.db.dao.CfgServerDao;
import com.bbw.god.db.entity.CfgServerEntity;
import com.bbw.god.db.entity.InsRoleInfoEntity;
import com.bbw.god.db.service.InsRoleInfoService;
import com.bbw.god.detail.LogUtil;
import com.bbw.god.game.config.Cfg;
import com.bbw.god.game.config.CfgGame;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.server.ServerTool;
import com.bbw.god.game.dfdj.zone.DfdjZoneService;
import com.bbw.god.game.sxdh.SxdhZoneService;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.statistic.StatisticTypeEnum;
import com.bbw.god.gameuser.statistic.resource.gold.GoldResStatisticService;
import com.bbw.god.gameuser.statistic.resource.gold.GoldStatistic;
import com.bbw.god.login.repairdata.RepairPushService;
import com.bbw.god.server.*;
import com.bbw.god.server.fst.FstLogic;
import com.bbw.god.server.special.ServerSpecialCityPrice;
import com.bbw.god.statistics.serverstatistic.GodServerStatisticService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * 区服相关服务
 *
 * @author suhq
 * @date 2019年4月13日 下午1:49:30
 */
@Slf4j
@RestController
@RequestMapping("/gm")
public class GMServerCtrl extends AbstractController {
    @Autowired
    private ServerActivityGeneratorService serverActivityGeneratorService;
    @Autowired
    private ServerActivityRankGeneratorService serverActivityRankGeneratorService;
    @Autowired
    private ServerService serverService;
    @Autowired
    private ServerUserService serverUserService;
    @Autowired
    private ClearTestDataService clearTestDataService;
    @Autowired
    private InsRoleInfoService roleInfo;
    @Autowired
    private CfgServerDao serverDao;
    @Autowired
    private ChatService chatService;
    @Autowired
    private SxdhZoneService sxdhZoneService;
    @Autowired
    private FstLogic fstLogic;
    @Autowired
    private CacheGMService cacheGMService;
    @Value("${game-data-result-days:30}")
    private int prepareDays;// 提前生成多少天的结果数据
    @Autowired
    private RepairPushService repairPushService;
    @Autowired
    @Lazy
    private List<PrepareServerDataService<?>> prepareServerData;
    @Autowired
    private GodServerStatisticService godServerStatisticService;
    @Autowired
    private RedisHashUtil<String, Integer> redisHashUtil;
    @Autowired
    private UserGmService userGmService;
    @Autowired
    private DfdjZoneService dfdjZoneService;
    @Autowired
    private GoldResStatisticService goldResStatisticService;

    /**
     * 群组维护
     *
     * @param serverGroup
     * @return
     */
    @GetMapping("server!maintainServerGroup")
    public Rst maintainServerGroup(int serverGroup, Date mtBegin, Date mtEnd) {
        List<CfgServerEntity> servers = ServerTool.getGroupServers(serverGroup);
        for (CfgServerEntity server : servers) {
            // 更新维护时间
            server.setMtBeginTime(mtBegin);
            server.setMtEndTime(mtEnd);
            serverDao.updateById(server);
            Cfg.I.reload(server.getId(), CfgServerEntity.class);
            log.info("{}设置维护成功", LogUtil.getLogServerPart(server));
        }
        return Rst.businessOK();
    }

    /**
     * 区服维护
     *
     * @param serverName
     * @return
     */
    @GetMapping("server!maintainServer")
    public Rst maintainServer(String serverName, Date mtBegin, Date mtEnd) {
        CfgServerEntity server = ServerTool.getServer(serverName);
        // 更新维护时间
        server.setMtBeginTime(mtBegin);
        server.setMtEndTime(mtEnd);
        serverDao.updateById(server);
        Cfg.I.reload(server.getId(), CfgServerEntity.class);
        log.info("{}设置维护成功", LogUtil.getLogServerPart(server));
        return Rst.businessOK();
    }

    /**
     * 开服初始化
     *
     * @param serverName
     * @return
     */
    @RequestMapping("server!initServerData")
    public Rst initServerData(String serverName) {
        CfgServerEntity server = ServerTool.getServer(serverName);
        int sId = server.getMergeSid();

        if (!(server.isDevTest() || server.getServerStatus() == ServerStatus.PREDICTING)) {
            return Rst.businessFAIL("服务器已开服无法执行开服初始化");
        }

        // 清除区服数据
        for (ServerDataType sdType : ServerDataType.values()) {
            serverService.deleteServerDatas(sId, sdType.getEntityClass());
        }
        // 清除区服、玩家数据
        clearTestDataService.clear(sId);

        // 初始化活动
        serverActivityGeneratorService.initServerActivityForNewServer(server);
        // 初始化冲榜活动
        serverActivityRankGeneratorService.initServerActivityRankForNewServer(server);
        // 初始化特产价格
        //        serverSpecialService.initSpecialPriceForNewServer(server);

        // 初始化魔王、富临轩、神仙
        for (PrepareServerDataService<?> service : prepareServerData) {
            service.prepareDatasByServer(sId, prepareDays);
        }
        fstLogic.initServerFst(sId);
        // 初始化一个玩家（用于新手引导的好友）
        createFirstRole(sId);

        // 加入神仙大会战区
        sxdhZoneService.joinZone(server);
        // 加入巅峰对决战区
        dfdjZoneService.joinZone(server);

        cacheGMService.reloadServerCache(sId);
        // 创建区服聊天室
        log.info("创建新服{}的聊天室", LogUtil.getLogServerPart(server));
        chatService.createServerChatRoom(sId);

        //向区服组合区服信息
//        if (app.runAsProd()) {
//        }
        String url = ServerTool.getServerGroupInfo(sId).getWsUrl() + "serverRegister!addServer?sid=" + server.getMergeSid() + "&serverGroup=" + server.getGroupId();
        HttpClientUtil.doGet(url);
        return Rst.businessOK();
    }

    /**
     * 删除区服特产价格
     *
     * @param serverNames
     * @return
     */
    @RequestMapping("server!deleteServerSpecial")
    public Rst initSpecialPrice(String serverNames) {
        List<CfgServerEntity> servers = ServerTool.getServers(serverNames);
        for (CfgServerEntity server : servers) {
            // 删除原有特产价格记录
            serverService.deleteServerDatas(server.getId(), ServerSpecialCityPrice.class);
        }
        return Rst.businessOK();
    }

    /**
     * 批量初始化玩家
     */
    @RequestMapping("server!createRolesByConfig")
    public void createRolesByConfig(int sid) {
        List<String> testAccounts = new ArrayList<>();
        for (String account : testAccounts) {
            try {
                Optional<InsRoleInfoEntity> role = roleInfo.getUidAtLoginServer(sid, account);
                // 账号已存在，则不再创建
                if (role.isPresent()) {
                    return;
                }
                // 初始化一个角色
                RoleVO roleVO = new RoleVO();
                roleVO.setServerId(sid);
                roleVO.setUserName(account);
                roleVO.setNickname(serverUserService.getRandomNickName() + "t");
                roleVO.setChannelCode("10");
                roleVO.setProperty("10");
                roleVO.setIp("127.0.0.1");
                serverUserService.newGameUser(roleVO, 1000, DateUtil.getTodayInt());
                userGmService.setGuideStatus(sid, roleVO.getNickname(), 10);
                userGmService.updateToLevel(sid, roleVO.getNickname(), 10);
            } catch (Exception e) {
                System.err.println("出错：" + account);
            }

        }
    }

    /**
     * 创建初始角色
     *
     * @param sId
     */
    private void createFirstRole(int sId) {
        String firstAccount = Cfg.I.getUniqueConfig(CfgGame.class).getFirstAccount();
        Optional<InsRoleInfoEntity> role = roleInfo.getUidAtLoginServer(sId, firstAccount);
        // 账号已存在，则不再创建
        if (role.isPresent()) {
            return;
        }
        // 初始化一个角色
        RoleVO roleVO = new RoleVO();
        roleVO.setServerId(sId);
        roleVO.setUserName(firstAccount);
        roleVO.setNickname(serverUserService.getRandomNickName());
        roleVO.setChannelCode("10");
        roleVO.setProperty("10");
        roleVO.setIp("127.0.0.1");
        serverUserService.newGameUser(roleVO, 1000, DateUtil.getTodayInt());
    }

    /**
     * 修复 更改UserPush路径后引发的问题
     *
     * @param serverNames
     * @param days
     * @return
     */
    @RequestMapping("server!reInitUserPush")
    public Rst reInitUserPush(String serverNames, int days) {
        List<CfgServerEntity> servers = ServerTool.getServers(serverNames);
        long start = System.currentTimeMillis();
        for (CfgServerEntity server : servers) {
            int sId = server.getMergeSid();
            Set<Long> uids = serverUserService.getUidsInDays(sId, days);
            for (Long uid : uids) {
                repairPushService.reInitUserPush(uid);
            }
        }
        long end = System.currentTimeMillis();
        Rst rst = Rst.businessOK();
        rst.put("time", end - start);
        return rst;
    }

    /**
     * 获取区服某种资源的统计值
     *
     * @param serverNames
     * @param awardName
     * @param wayValue
     * @param dateTime
     * @param type
     * @return
     */
    @RequestMapping("server!getAwardStatisticValue")
    public Rst getAwardStatisticValueByWay(String serverNames, String awardName, Integer wayValue, String dateTime,
                                           int type) {
        Date date = DateUtil.fromDateTimeString(dateTime);
        WayEnum way = WayEnum.fromValue(wayValue);
        List<CfgServerEntity> servers = ServerTool.getServers(serverNames);
        int sum = 0;
        for (CfgServerEntity server : servers) {
            String key;
            if (0 == type) {
                key = godServerStatisticService.getDateStatisticOutputKey(server.getId(), awardName, date);
            } else {
                key = godServerStatisticService.getDateStatisticConsumeKey(server.getId(), awardName, date);
            }
            int value = redisHashUtil.getField(key, way.getName()) == null ?
                    0 : redisHashUtil.getField(key, way.getName());
            sum += value;
        }
        Rst rst = Rst.businessOK();
        rst.put("num", sum);
        return rst;
    }

    @GetMapping("server!resetGrowTaskStatus")
    public Rst resetGrowTaskStatus(String serverNames, int days) {
        List<CfgServerEntity> servers = ServerTool.getServers(serverNames);
        for (CfgServerEntity server : servers) {
            Set<Long> uids = serverUserService.getUidsInDays(server.getMergeSid(), days);
            for (Long uid : uids) {
                GameUser gu = gameUserService.getGameUser(uid);
                Date regTime = gu.getRoleInfo().getRegTime();
                if (null == regTime) {
                    continue;
                }
                int daysBetween = DateUtil.getDaysBetween(regTime, DateUtil.now());
                if (daysBetween <= 7) {
                    gu.getStatus().setGrowTaskCompleted(false);
                    gu.updateStatus();
                }
            }
        }
        return Rst.businessOK();
    }

    @GetMapping("server!getSynthesisSkillScrollUser")
    public Rst getSynthesisSkillScrollUser(String serverNames, int days) {
        Rst rst = Rst.businessOK();
        List<String> userList = new ArrayList<>();
        List<CfgServerEntity> servers = ServerTool.getServers(serverNames);
        for (CfgServerEntity server : servers) {
            Set<Long> uids = serverUserService.getUidsInDays(server.getMergeSid(), days);
            for (Long uid : uids) {
                GoldStatistic statistic = goldResStatisticService.fromRedis(uid, StatisticTypeEnum.CONSUME, DateUtil.getTodayInt());
                Map<WayEnum, Integer> todayMap = statistic.getTodayMap();
                if (todayMap.containsKey(WayEnum.SYNTHESIS_SKILL_SCROLL)) {
                    String nickname = gameUserService.getGameUser(uid).getRoleInfo().getNickname();
                    String name = server.getShortName() + " " + nickname;
                    userList.add(name);
                }
            }
        }
        rst.put("userList", userList);
        return rst;
    }
}
