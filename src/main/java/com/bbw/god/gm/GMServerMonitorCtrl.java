package com.bbw.god.gm;

import com.bbw.common.PowerRandom;
import com.bbw.common.SetUtil;
import com.bbw.db.redis.RedisSetUtil;
import com.bbw.db.redis.RedisValueUtil;
import com.bbw.god.db.entity.CfgServerEntity;
import com.bbw.god.game.config.server.ServerTool;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.UserDataType;
import com.bbw.god.gameuser.redis.UserRedisKey;
import com.bbw.god.server.ServerDataType;
import com.bbw.god.server.ServerUserService;
import com.bbw.god.server.redis.ServerRedisKey;
import com.gexin.fastjson.JSON;
import com.gexin.fastjson.serializer.SerializerFeature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

import static com.bbw.god.game.data.redis.RedisKeyConst.SPLIT;

/**
 * @author suchaobin
 * @description
 * @date 2020/9/3 10:25
 **/
@RestController
@RequestMapping("/gm")
public class GMServerMonitorCtrl {
    @Autowired
    private ServerUserService serverUserService;
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private RedisSetUtil<String> redisSetUtil;
    @Autowired
    private RedisValueUtil<Object> redisValueUtil;


    @RequestMapping("getMonitorData")
    public Map<String, Object> getMonitorData(String serverNames, int daysIn, int personNum) {
        Map<String, Object> dataMap = new LinkedHashMap<>();
        List<CfgServerEntity> servers = ServerTool.getServers(serverNames);
        for (CfgServerEntity server : servers) {
            Integer sid = server.getId();
            monitorServerData(sid, dataMap);
            List<Long> uidList = getUidsOrderByLevel(sid, daysIn, personNum);
            for (Long uid : uidList) {
                monitorUserData(uid, sid, dataMap);
            }
        }
        return dataMap;
    }

    private void monitorUserData(Long uid, int sid, Map<String, Object> dataMap) {
        UserDataType[] dataTypes = UserDataType.values();
        dataMap.put(sid + SPLIT + "userData" + SPLIT + uid, "==============================");
        for (UserDataType dataType : dataTypes) {
            String dataTypeKey = UserRedisKey.getDataTypeKey(uid, dataType);
            Set<String> members = redisSetUtil.members(dataTypeKey);
            byte[] jsonBytes = new byte[0];
            if (SetUtil.isNotEmpty(members)) {
                String random = PowerRandom.getRandomFromSet(members);
                Object value = redisValueUtil.get(random);
                jsonBytes = JSON.toJSONBytes(value, SerializerFeature.WriteClassName);
            }
            int singleSize = jsonBytes.length;
            int totalSize = singleSize * members.size();
            String singlePart = "单个大小：" + singleSize;
            String totalPart = "总大小：" + totalSize;
            if (singleSize > 1024) {
                singlePart += "#############";
            }
            if (totalSize > 102400) {
                totalPart += "#############";
            }
            dataMap.put(sid + SPLIT + "userData" + SPLIT + uid + SPLIT + dataType.getRedisKey(), "数量:" + members.size() + "," + singlePart + "," + totalPart);
        }
    }

    private void monitorServerData(int sid, Map<String, Object> dataMap) {
        ServerDataType[] serverDataTypes = ServerDataType.values();
        for (ServerDataType serverDataType : serverDataTypes) {
            String dataTypeKey = ServerRedisKey.getDataTypeKey(sid, serverDataType);
            Set<String> members = redisSetUtil.members(dataTypeKey);
            dataMap.put(sid + SPLIT + "serverData" + SPLIT + serverDataType.getRedisKey(), members.size());
        }
    }

    private List<Long> getUidsOrderByLevel(int sid, int daysIn, int personNum) {
        Set<Long> uids = serverUserService.getUidsInDays(sid, daysIn);
        return uids.stream().sorted(Comparator.comparing(u ->
                gameUserService.getGameUser((Long) u).getLevel()).reversed())
                .limit(personNum).collect(Collectors.toList());
    }
}
