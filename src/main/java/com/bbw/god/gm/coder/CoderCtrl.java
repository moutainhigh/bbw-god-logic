package com.bbw.god.gm.coder;

import com.alibaba.fastjson.JSONObject;
import com.bbw.cache.LocalCache;
import com.bbw.common.ListUtil;
import com.bbw.common.Rst;
import com.bbw.common.ShareCodeUtil;
import com.bbw.common.StrUtil;
import com.bbw.db.redis.RedisBase;
import com.bbw.god.db.entity.CfgServerEntity;
import com.bbw.god.db.pool.PlayerPool;
import com.bbw.god.db.pool.ServerDataPool;
import com.bbw.god.db.pool.UserDataPool;
import com.bbw.god.db.service.InsRoleInfoService;
import com.bbw.god.game.config.server.ServerTool;
import com.bbw.god.gameuser.redis.UserRedisKey;
import com.bbw.god.server.ServerData;
import com.bbw.god.server.ServerDataService;
import com.bbw.god.server.ServerDataType;
import com.bbw.god.server.redis.ServerRedisKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/**
 * 程序员工具
 *
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-03-01 11:05
 */
@Slf4j
@RestController
@RequestMapping(value = "/coder")
public class CoderCtrl {
    @Value("${bbw-god.redis-userdata-in-days:10}")
    private int cacheDays;
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private ServerDataService serverDataService;
    @Autowired
    private InsRoleInfoService roleInfoService;
    @Autowired
    private RedisBase redisBase;
    @Autowired
    private PlayerPool playerPool;
    @Autowired
    private UserDataPool userDataPool;
    @Autowired
    private ServerDataPool serverDataPool;


    @RequestMapping(value = "healthCheck")
    public void healthCheck() {
        // 提供给负载均衡做健康检查用
    }

    @RequestMapping(value = "saveUser")
    public void saveUser() {
        playerPool.saveToDB();
    }

    @RequestMapping(value = "saveUserData")
    public void saveUserData() {
        userDataPool.saveToDB();
    }

    @RequestMapping(value = "saveServerData")
    public void saveServerData() {
        serverDataPool.saveToDB();
    }

    @RequestMapping("/params")
    public Rst codeParams() {
        long timestamp = StrUtil.getLong(request.getParameter("tk"), 0);
        String pn = "p" + ShareCodeUtil.toSerialCode(timestamp);
        String p = request.getParameter(pn);
        Rst rst = Rst.businessOK();
        rst.put("tk", timestamp);
        rst.put(pn, p);
        return rst;
    }

    @RequestMapping("/saveAllPlayer")
    public Rst saveAllPlayer() {
        Rst rst = Rst.businessOK();
        List<CfgServerEntity> servers = ServerTool.getAvailableServers();
        for (CfgServerEntity server : servers) {
            if (server.getGroupId() == 20 && server.getId() < 1065) {
                continue;
            }
            log.error("开始处理:" + server.getName());
            rst.put(server.getId().toString(), server.getName());
            List<Long> uids = roleInfoService.getAllUidsByServer(server.getId());
            for (Long uid : uids) {
                String key = UserRedisKey.getGameUserKey(uid);
                if (redisBase.exists(key)) {
                    playerPool.addToUpdatePool(uid);
                }
            }
        }
        log.error("全部区服处理完成");
        long timestamp = StrUtil.getLong(request.getParameter("tk"), 0);
        String pn = "p" + ShareCodeUtil.toSerialCode(timestamp);
        String p = request.getParameter(pn);

        rst.put(pn, p);
        return rst;
    }

    /**
     * 保存某个区服的ServerData到MySQL
     *
     * @param serverNames
     * @return
     */
    @RequestMapping("/saveServerDataToDB")
    public Object saveServerDataToDB(String serverNames) {
        List<CfgServerEntity> servers = ServerTool.getServers(serverNames);
        JSONObject js = new JSONObject();
        if (ListUtil.isEmpty(servers)) {
            return js;
        }
        List<ServerDataType> dataTypes = Arrays.asList(ServerDataType.Guild_Info);
        for (CfgServerEntity server : servers) {
            int sid = server.getMergeSid();
            List<ServerData> sds = new ArrayList<>();
            for (ServerDataType dataType : dataTypes) {
                List<ServerData> datas = serverDataService.getServerDatas(sid, (Class<ServerData>) dataType.getEntityClass());
                sds.addAll(datas);
            }
            HashSet<String> serverDataKeys = new HashSet<>();
            if (ListUtil.isNotEmpty(sds)) {
                sds.forEach(tmp -> {
                    String redisKey = ServerRedisKey.getServerDataKey(tmp);
                    serverDataKeys.add(redisKey);
                });
            }
            // 保存数据到数据库
            log.info("need to save or update num:" + serverDataKeys.size());
            if (serverDataKeys.size() == 0) {
                continue;
            }
            serverDataPool.dbSaveOrUpdate(serverDataKeys);
        }
        return Rst.businessOK();
    }

    @RequestMapping("/clearCache")
    public Rst clearCache() {
        LocalCache.getInstance().clear();
        return Rst.businessOK().put("清除本地缓存", "完成");
    }
}
