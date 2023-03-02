package com.bbw.god.game.online;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bbw.common.HttpClientUtil;
import com.bbw.db.redis.RedisHashUtil;
import com.bbw.god.game.config.Cfg;
import com.bbw.god.game.config.CfgGame;
import com.bbw.god.game.data.redis.GameRedisKey;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.server.redis.ServerRedisKey;
import com.bbw.sys.session.SingleUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 实名上下线上报
 *
 * @author suhq
 * @date 2021-05-18 14:23
 **/
@Slf4j
@Service
public class RealNameOnlineBehaviorReporter {
    @Autowired
    private RedisHashUtil<Long, String> onlineReported;
    @Autowired
    private GameOnlineService gameOnlineService;
    @Autowired
    private SingleUserService singleUserService;
    @Autowired
    private GameUserService gameUserService;

    /**
     * 在线数据统计接口
     */
    public void reportOnline() {
        List<Long> uidsFiveMinutesAgo = gameOnlineService.getOnlineUids(5);
//        System.out.println("uidsFiveMinutesAgo:" + JSONUtil.toJson(uidsFiveMinutesAgo));
        List<Long> uidsTenMinutesAgo = gameOnlineService.getOnlineUids(10);
//        System.out.println("uidsTenMinutesAgo:" + JSONUtil.toJson(uidsTenMinutesAgo));

        List<Long> bothOnlines = new ArrayList<>();
        for (Long uid5 : uidsFiveMinutesAgo) {
            boolean match = uidsTenMinutesAgo.stream().anyMatch(tmp -> tmp.longValue() == uid5);
            if (match) {
                bothOnlines.add(uid5);
            }
        }
        //新上线的玩家
        uidsFiveMinutesAgo.removeAll(bothOnlines);
        //已下线的玩家
        uidsTenMinutesAgo.removeAll(bothOnlines);
        List<String> onlineSessionIds = singleUserService.getSessionIds(uidsFiveMinutesAgo);
        List<String> offlineSessionIds = singleUserService.getSessionIds(uidsTenMinutesAgo);
        JSONArray jsonArray = new JSONArray();
        HashMap<Long, String> onlineToReported = new HashMap<>();
        for (int i = 0; i < onlineSessionIds.size(); i++) {
            long uid = uidsFiveMinutesAgo.get(i);
            JSONObject js = new JSONObject();
            js.put("uid", uid);
            js.put("si", onlineSessionIds.get(i));
            js.put("account", gameUserService.getAccount(uid));
            js.put("bt", 1);
            onlineToReported.put(uid, onlineSessionIds.get(i));
            jsonArray.add(js);
        }

        Long[] offlineToRemove = new Long[offlineSessionIds.size()];
        for (int i = 0; i < offlineSessionIds.size(); i++) {
            JSONObject js = new JSONObject();
            long uid = uidsTenMinutesAgo.get(i);
            js.put("uid", uid);
            String sessionId = getSession(uid);
            if (null == sessionId) {
                sessionId = offlineSessionIds.get(i);
            }
            js.put("si", sessionId);
            js.put("account", gameUserService.getAccount(uid));
            js.put("bt", 0);
            offlineToRemove[i] = uid;
            jsonArray.add(js);
        }
        if (jsonArray.size() == 0) {
            log.info("实名上下线上报：" + 0);
            return;
        }
        String reportData = jsonArray.toJSONString();
        try {
            HttpClientUtil.doGet(Cfg.I.getUniqueConfig(CfgGame.class).getUacBaseUrl() + "accountVerified!loginout?data=" + URLEncoder.encode(reportData, "UTF-8"));
            addToReported(onlineToReported);
            removeFromReported(offlineToRemove);
            log.info("实名上下线上报：" + reportData);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return;
        }

    }


    private void addToReported(Map<Long, String> values) {
        if (values.keySet().size() == 0) {
            return;
        }
        onlineReported.putAllField(getRedisKey(), values);
    }

    private void removeFromReported(Long... uids) {
        if (uids.length == 0) {
            return;
        }
        onlineReported.removeField(getRedisKey(), uids);
    }

    private String getSession(Long uid) {
        return onlineReported.getField(getRedisKey(), uid);
    }

    private String getRedisKey() {
        String key = GameRedisKey.getRunTimeVarKey("online") + ServerRedisKey.SPLIT + "realNameReported";
        return key;
    }

}
