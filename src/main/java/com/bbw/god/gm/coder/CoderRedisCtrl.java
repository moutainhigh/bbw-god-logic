package com.bbw.god.gm.coder;

import com.alibaba.fastjson.JSONObject;
import com.bbw.App;
import com.bbw.common.DateUtil;
import com.bbw.common.ListUtil;
import com.bbw.common.Rst;
import com.bbw.common.SetUtil;
import com.bbw.db.redis.RedisBase;
import com.bbw.db.redis.RedisHashUtil;
import com.bbw.god.db.entity.CfgServerEntity;
import com.bbw.god.db.service.InsRoleInfoService;
import com.bbw.god.game.config.server.ServerTool;
import com.bbw.god.gameuser.redis.GameUserRedisUtil;
import com.bbw.god.job.game.ClearFromRedisJob;
import com.bbw.god.server.*;
import com.bbw.god.server.maou.bossmaou.auction.ServerMaouAuction;
import com.bbw.god.server.redis.ServerRedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

/**
 * redis操作工具
 *
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-03-01 11:05
 */
@Slf4j
@RestController
@RequestMapping(value = "/coder")
public class CoderRedisCtrl {
    @Value("${bbw-god.redis-userdata-in-days:10}")
    private int cacheDays;
    @Autowired
    private ClearFromRedisJob clearFromRedisJob;
    @Autowired
    private ServerDataService serverDataService;
    @Autowired
    private RedisBase redisBase;
    @Autowired
    private ServerUserService userService;
    @Autowired
    private InsRoleInfoService roleInfo;
    @Autowired
    private ServerRedisUtil serverRedisUtil;
    @Autowired
    private GameUserRedisUtil userRedis;
    @Autowired
    private RedisHashUtil redisHashUtil;
    @Autowired
    private App app;


    @RequestMapping("/removeRedis")
    public void removeRedis() {
        clearFromRedisJob.doJob("0");
    }

    @RequestMapping("/removeRedisUser")
    public void removeRedisUser(Long uid) {
        userService.unloadGameUser(uid);
    }

    /**
     * 清除最近登录为某个时段的玩家
     *
     * @param serverNames
     * @param minBeforeDay
     * @param maxBeforeDay
     * @return
     */
    @RequestMapping("/removeRedisUsers")
    public Object removeRedisUser(String serverNames, int minBeforeDay, int maxBeforeDay) {
        if (minBeforeDay <= cacheDays) {
            return Rst.businessFAIL("不能清除" + cacheDays + "天内的玩家Redis数据");
        }
        List<CfgServerEntity> servers = ServerTool.getServers(serverNames);
        // 排除被合服的区服
        servers = servers.stream().filter(tmp -> tmp.getId().intValue() == tmp.getMergeSid()).collect(Collectors.toList());
        JSONObject js = new JSONObject();
        if (ListUtil.isNotEmpty(servers)) {
            long begin = System.currentTimeMillis();
            Date beforeDate = DateUtil.addDays(DateUtil.now(), -maxBeforeDay);
            Date endDate = DateUtil.addDays(DateUtil.now(), -minBeforeDay);

            for (CfgServerEntity server : servers) {
                // cacheDays天内登录的用户
                List<Long> uids = roleInfo.getUidsLoginBetween(server.getId(), DateUtil.toDateInt(beforeDate), DateUtil.toDateInt(endDate));
                if (ListUtil.isEmpty(uids)) {
                    js.put(server.getName(), "最大清除玩家数：0");
                    continue;
                }
                js.put(server.getName(), "最大清除玩家数：" + uids.size());
                userService.unloadGameUsers(uids, server.getName());
            }
            long end = System.currentTimeMillis();
            js.put("总耗时", (end - begin));
        }
        System.out.println("====================================");
        System.out.println(js);
        return js;
    }

    /**
     * 从Redis清除区服的所有玩家
     *
     * @param serverNames
     * @return
     */
    @RequestMapping("/removeRediServerUsers")
    public Rst removeRediServerUsers(String serverNames) {
        long begin = System.currentTimeMillis();
        List<CfgServerEntity> servers = ServerTool.getServers(serverNames);
        for (CfgServerEntity server : servers) {
            try {
                List<Long> uids = roleInfo.getAllUidsByServer(server.getId());
                if (ListUtil.isNotEmpty(uids)) {
                    userService.unloadGameUsers(uids, server.getName());
                }
            } catch (Exception e) {
                log.error("从Redis清除区服" + server.getName() + "的数据发生异常", e);
            }

        }

        long end = System.currentTimeMillis();
        return Rst.businessOK("总耗时:" + (end - begin));
    }

    /**
     * 从Redis清除区服的所有玩家
     *
     * @param sid
     * @return
     */
    @RequestMapping("/onlyRemoveRedisUsers")
    public Rst onlyRemoveRedisUsers(int sid, int limitNum) {
        long begin = System.currentTimeMillis();
        List<Integer> avaibleSids = Arrays.asList(5001, 5002, 5003, 5004, 5005);
        if (!avaibleSids.contains(sid)) {
            return Rst.businessFAIL("只能移除5001,5002,5003,5004,5005区服对应的玩家数据");
        }

        if (!"prod".equals(app.getActive())) {
            return Rst.businessFAIL("只能移除自营区服对应的玩家数据");
        }
        List<Long> uids = roleInfo.getAllUidsByServer(sid);
        int numToClear = uids.size() > limitNum ? limitNum : uids.size();
        for (int i = 0; i < numToClear; i++) {
            try {
                long uid = uids.get(i);
                log.info("uid:" + uid + " unload data from redis... (" + (i + 1) + "/" + uids.size() + ")");
                userRedis.removeUserRedis(uid);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }

        long end = System.currentTimeMillis();
        return Rst.businessOK("总耗时:" + (end - begin));
    }

    /**
     * 该操作会阻塞Redis操作，生产环境勿用
     *
     * @param patterns
     * @return
     */
    @Deprecated
    @RequestMapping("/delKey")
    public Rst delKey(String patterns, int count) {
        CfgServerEntity server = ServerTool.getAvailableServers().get(0);
        ServerStatus serverStatus = server.getServerStatus();
        if (serverStatus != ServerStatus.MAINTAINING) {
            Rst.businessFAIL("生产环境非维护期间，不可调用模糊删除接口");
        }
        Set<String> keys = redisBase.scan(patterns, count);
        int keyNum = 0;
        if (SetUtil.isNotEmpty(keys)) {
            redisBase.delete(keys);
            keyNum = keys.size();
        }
        Rst rst = Rst.businessOK("删除key的数量：" + keyNum);
        rst.put("keys", keys);
        return rst;
    }

    /**
     * 只将区服数据从Redis移除(未能清理完全)
     *
     * @param mergedServerNames
     * @return
     */
    @RequestMapping("/clearServerDataFromRedis")
    public Rst clearServerDataFromRedis(String mergedServerNames) {
        List<CfgServerEntity> servers = ServerTool.getServers();
        servers = servers.stream().filter(server -> server.getId() != server.getMergeSid() && mergedServerNames.contains(server.getName())).collect(Collectors.toList());
        if (ListUtil.isEmpty(servers)) {
            return Rst.businessOK();
        }
        for (CfgServerEntity server : servers) {
            List<ServerData> sds = new ArrayList<>();
            for (ServerDataType dataType : ServerDataType.values()) {
                List<ServerData> datas = serverDataService.getServerDatas(server.getId(), (Class<ServerData>) dataType.getEntityClass());
                sds.addAll(datas);
            }
            for (ServerData sd : sds) {
                serverRedisUtil.deleteFromRedis(sd);
            }
        }
        return Rst.businessOK();
    }

    /**
     * 清除过期区服loop数据
     *
     * @param serverNames
     * @param dataType
     * @param minBeforeDay
     * @param maxBeforeDay
     * @return
     */
    @RequestMapping("/delHistoryServerLoopData")
    public Rst delHistoryServerLoopData(String serverNames, String dataType, int minBeforeDay, int maxBeforeDay) {
        List<CfgServerEntity> servers = ServerTool.getServers(serverNames);
        if (ListUtil.isEmpty(servers)) {
            return Rst.businessFAIL("没有对应区服");
        }
        if (minBeforeDay > maxBeforeDay) {
            return Rst.businessFAIL("minBeforeDay必须小于maxBeforeDay");
        }
        if (minBeforeDay <= 32) {
            return Rst.businessFAIL("不能清理一个月(31天)内的数据");
        }
        ServerDataType serverDataType = ServerDataType.fromRedisKey(dataType);
        Date now = DateUtil.now();
        for (CfgServerEntity server : servers) {
            int day = minBeforeDay;
            while (day <= maxBeforeDay) {
                Date date = DateUtil.addDays(now, -day);
                String loopKey = DateUtil.toDateInt(date) + "";
                serverDataService.deleteServerDatas(server.getId(), serverDataType.getEntityClass(), loopKey);
                day++;
            }
        }
        return Rst.businessOK();
    }

    /**
     * 清除过期区服数据
     *
     * @param serverNames
     * @param dataType
     * @param beforeDays
     * @return
     */
    @RequestMapping("/delHistoryServerData")
    public Rst delHistoryServerData(String serverNames, String dataType, int beforeDays) throws InstantiationException, IllegalAccessException {
        List<CfgServerEntity> servers = ServerTool.getServers(serverNames);
        if (ListUtil.isEmpty(servers)) {
            return Rst.businessFAIL("没有对应区服");
        }
        if (beforeDays <= 32) {
            return Rst.businessFAIL("不能清理一个月(31天)内的数据");
        }
        ServerDataType serverDataType = ServerDataType.fromRedisKey(dataType);
        Date now = DateUtil.now();
        Date beforeDate = DateUtil.addDays(now, -beforeDays);
        for (CfgServerEntity server : servers) {
            List<? extends ServerData> serverDatas = serverDataService.getServerDatas(server.getId(), serverDataType.getEntityClass());
            if (ListUtil.isEmpty(serverDatas)) {
                continue;
            }
            ServerData instance = serverDatas.get(0);
            if (instance instanceof ServerMaouAuction) {
                List<ServerMaouAuction> serverMaouAuctions = new ArrayList<>();
                for (ServerData serverData : serverDatas) {
                    ServerMaouAuction maouAuction = (ServerMaouAuction) serverData;
                    if (maouAuction.getBeginTime().after(beforeDate)) {
                        continue;
                    }
                    serverMaouAuctions.add(maouAuction);
                }
                serverDataService.deleteServerDatas(serverMaouAuctions, ServerMaouAuction.class);
            }
        }
        return Rst.businessOK();
    }

    /**
     * 删除区服统计key
     *
     * @param serverNames
     * @param subffix
     * @param sinceDate   yyyyMMdd
     * @param endDate     yyyyMMdd
     * @return
     */
    @GetMapping("/delServerStatisticKeys")
    public Rst delServerStatisticKeys(String serverNames, String subffix, int sinceDate, int endDate) {
        if (subffix.contains("*")) {
            return Rst.businessFAIL("suffix不能包含*号");
        }
        Date begin = DateUtil.fromDateInt(sinceDate);
        Date end = DateUtil.fromDateInt(endDate);
        int daysBetween = DateUtil.getDaysBetween(begin, end);
        List<CfgServerEntity> servers = ServerTool.getServers(serverNames);
        List<String> keys = new ArrayList<>();
        for (CfgServerEntity server : servers) {
            int sid = server.getMergeSid();
            for (int i = 0; i <= daysBetween; i++) {
                int dateInt = DateUtil.toDateInt(DateUtil.addDays(begin, i));
                // 今日的记录不删除
                if (dateInt == DateUtil.getTodayInt()) {
                    continue;
                }
                //形如 server:1063:statistic:20200402:login
                String key = "server:" + sid + ":statistic:" + dateInt + ":" + subffix;
                keys.add(key);
            }
        }
        redisHashUtil.delete(keys);
        return Rst.businessOK();
    }
}
