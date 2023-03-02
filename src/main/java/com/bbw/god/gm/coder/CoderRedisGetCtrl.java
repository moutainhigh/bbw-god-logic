package com.bbw.god.gm.coder;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.bbw.common.DateUtil;
import com.bbw.common.JSONUtil;
import com.bbw.common.ListUtil;
import com.bbw.common.Rst;
import com.bbw.god.db.entity.CfgServerEntity;
import com.bbw.god.db.service.InsRoleInfoService;
import com.bbw.god.game.config.server.ServerTool;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.UserData;
import com.bbw.god.gameuser.UserDataType;
import com.bbw.god.gameuser.redis.GameUserRedisUtil;
import com.bbw.god.gameuser.statistic.StatisticServiceFactory;
import com.bbw.god.server.ServerData;
import com.bbw.god.server.ServerDataService;
import com.bbw.god.server.ServerDataType;
import com.bbw.god.server.ServerUserService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
public class CoderRedisGetCtrl {
    @Value("${bbw-god.redis-userdata-in-days:10}")
    private int cacheDays;
    @Autowired
    private ServerDataService serverDataService;
    @Autowired
    private ServerUserService userService;
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private StatisticServiceFactory statisticServiceFactory;
    @Autowired
    private InsRoleInfoService roleInfo;
    @Autowired
    private GameUserRedisUtil gameUserRedisUtil;

    @RequestMapping("/showUserRedisDataSize")
    public Object showUserRedisDataSize(String serverNames) {
        List<CfgServerEntity> servers = ServerTool.getServers(serverNames);
        if (ListUtil.isEmpty(servers)) {
            return Rst.businessOK();
        }

        Date endDate = DateUtil.now();
        Date beforeDate = DateUtil.addDays(endDate, -cacheDays + 2);
        List<DataSizeResult> results = new ArrayList<>();
        for (CfgServerEntity server : servers) {
            // cacheDays天内登录的用户
            List<Long> uids = roleInfo.getUidsLoginBetween(server.getId(), DateUtil.toDateInt(beforeDate), DateUtil.toDateInt(endDate));
            if (ListUtil.isEmpty(uids)) {
                results.add(new DataSizeResult(server.getName(), "登录玩家数：0"));
                continue;
            }
            for (long uid : uids) {
                if (!gameUserRedisUtil.existsUser(uid)) {
                    System.out.println(uid + "不在Redis中");
                    continue;
                }
                DataSizeResult dataSizeResult = getDataSizeResults(uid).get(0);
                dataSizeResult.setDataType(server.getName() + "_" + uid + "_" + dataSizeResult.getDataType());
                results.add(dataSizeResult);
            }
        }
        results.sort((r1, r2) -> r2.getSize() - r1.getSize());
        JSONObject js = new JSONObject(true);
        long totalSize = 0L;
        long totalNum = 0L;
        int realUserNum = 0;
        for (DataSizeResult result : results) {
            if (result.getSize() <= 0) {
                continue;
            }
            totalSize += result.getSize();
            totalNum += result.getNum();
            realUserNum++;
        }
        js.put("自" + DateUtil.toDateTimeString(beforeDate) + "以来" + realUserNum + "个玩家平均数据量", totalNum / results.size() + "个," + totalSize / results.size() + "b");
        for (DataSizeResult result : results) {
            js.put(result.getDataType(), result.getNum() + "个，" + result.getSize() + "b");
        }
        return js;
    }


    @RequestMapping("/showSingleUserRedisDataSize")
    public JSONObject showSingleUserRedisDataSize(long uid) {
        List<DataSizeResult> results = getDataSizeResults(uid);
        JSONObject js = new JSONObject(true);
        for (DataSizeResult result : results) {
            js.put(result.getDataType(), result.getNum() + "个，" + result.getSize() + "b");
        }
        return js;
    }

    private List<DataSizeResult> getDataSizeResults(long uid) {
        List<DataSizeResult> results = new ArrayList<>();
        GameUser gu = userService.getGameUser(uid);
        int guDataSize = JSONUtil.toJson(gu).getBytes().length;
        int sum = guDataSize;
        int keyNum = 1;
        results.add(new DataSizeResult("gameuser", 1, guDataSize));
        for (UserDataType dataType : UserDataType.values()) {
            try {
                List<? extends UserData> multiItems = gameUserService.getMultiItems(uid, dataType.getEntityClass());
//                System.out.println(JSON.toJSONString(multiItems, SerializerFeature.WriteClassName));
                int dataSize = JSON.toJSONBytes(multiItems, SerializerFeature.WriteClassName).length;
                sum += dataSize;
                keyNum += multiItems.size();
                results.add(new DataSizeResult(dataType.getRedisKey(), multiItems.size(), dataSize));
            } catch (Exception e) {
                results.add(new DataSizeResult(dataType.getRedisKey(), e.getMessage()));
            }

        }
        Map<String, Map<String, Object>> allStaticResult = statisticServiceFactory.getAllKeyMap(uid);
        int statisticSize = JSON.toJSONBytes(allStaticResult).length;
        sum += statisticSize;
        keyNum += allStaticResult.keySet().size();
        results.add(new DataSizeResult("statistic", allStaticResult.keySet().size(), statisticSize));
        results.add(new DataSizeResult("总数据(" + gu.getLevel() + "LV)", keyNum, sum));
        results.sort((r1, r2) -> r2.getSize() - r1.getSize());
        return results;
    }

    @RequestMapping("/showServerRedisDataSize")
    public JSONObject showServerRedisDataSize(String serverName) {
        CfgServerEntity server = ServerTool.getServer(serverName);
        List<DataSizeResult> results = new ArrayList<>();
        int sum = 0;
        int num = 0;
        for (ServerDataType dataType : ServerDataType.values()) {
            try {
                List<ServerData> datas = serverDataService.getServerDatas(server.getId(), (Class<ServerData>) dataType.getEntityClass());
//                System.out.println(JSON.toJSONString(multiItems, SerializerFeature.WriteClassName));
                int dataSize = JSON.toJSONBytes(datas, SerializerFeature.WriteClassName).length;
                sum += dataSize;
                num += datas.size();
                results.add(new DataSizeResult(dataType.getRedisKey(), datas.size(), dataSize));
            } catch (Exception e) {
                results.add(new DataSizeResult(dataType.getRedisKey(), e.getMessage()));
            }
        }
        results.add(new DataSizeResult("总数据", num, sum));
        results.sort((r1, r2) -> r2.getSize() - r1.getSize());
        JSONObject js = new JSONObject(true);
        for (DataSizeResult result : results) {
            js.put(result.getDataType(), result.getNum() + "个，" + result.getSize() + "b");
        }
        return js;
    }

    @Data
    @AllArgsConstructor
    static class DataSizeResult {
        private String dataType;
        private int num;
        private int size;
        private String error;

        public DataSizeResult(String dataType, int num, int size) {
            this.dataType = dataType;
            this.num = num;
            this.size = size;
        }

        public DataSizeResult(String dataType, String error) {
            this.dataType = dataType;
            this.error = error;
        }
    }
}
