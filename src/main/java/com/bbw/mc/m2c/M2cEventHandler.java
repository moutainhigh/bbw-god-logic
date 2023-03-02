package com.bbw.mc.m2c;

import com.bbw.App;
import com.bbw.common.HttpClientUtil;
import com.bbw.common.JSONUtil;
import com.bbw.god.game.config.server.ServerTool;
import com.bbw.god.gameuser.GameUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 客户端通知消息发送处理器
 *
 * @author suhq
 * @date 2019-06-03 10:29:19
 */
@Slf4j
@Async
@Component
public class M2cEventHandler {
    @Autowired
    private App app;
    @Autowired
    private GameUserService gameUserService;

    public void sendMsgToClient(M2c msg) {
        if (app.runAsDev()) {
            return;
        }
        try {
            long uid = msg.getGuId();
            int sid = gameUserService.getActiveSid(uid);
            String url = ServerTool.getServerGroupInfo(sid).getWsUrl() + "mark!redDot?" + "rid=" + uid + "&info=" + HttpClientUtil.urlEncode(JSONUtil.toJson(msg.getInfo()));
            HttpClientUtil.doGet(url);
        } catch (Exception e) {
            if (app.runAsProd()) {
                log.error(e.getMessage(), e);
            }
        }
    }
}
