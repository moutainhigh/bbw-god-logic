package com.bbw.mc.dingding;

import com.bbw.exception.ErrorLevel;
import com.bbw.mc.Msg;
import com.bbw.mc.MsgType;
import com.bbw.mc.NotifyService;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiRobotSendRequest;
import com.dingtalk.api.response.OapiRobotSendResponse;
import com.taobao.api.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class DingdingService extends NotifyService {

    // 发送群机器人消息，来自钉钉配置
    @Value("${spring.dingding.url:https://oapi.dingtalk.com/robot/send?access_token=}")
    private String url;
    // 接收者配置信息
    @Value("${spring.dingding.to.error-coder:98e1b75a0c2de1caf13dc1f553cc99ee6081fc5d52995b7ad029fdc5b61c6400}")
    private String errorCoder; // 程序员
    @Value("${spring.dingding.to.info-coder:a84dcb5634da1978e9b0a52513f23466aaee76fd3af9bbd79f66498187357619}")
    private String infoCoder; // 程序员
    @Value("${spring.dingding.to.operator:ab0f3feb316eec5885ad305f7fb52187f1864ff62e3c2612b5788d4c1f4450e1}")
    private String operator; // 运营人员
    @Value("${spring.dingding.to.yd-operator:be721569eb96f7469e9833c29bd87df316e332f903756845a2d0d66082434df7}")
    private String ydOperator; // 运营人员
    @Value("${spring.dingding.to.manager:cac066e03af66a3c91f92da99da6cb0802800efa1474df6427568c3fe160d0a2}")
    private String manager; // 管理人员

    DingdingService() {
        this.type = MsgType.DING_DING;
    }

    @Override
    public void notify(Msg msg) {
        if (msg instanceof DingDingMsg) {
            DingDingMsg dingdingMsg = (DingDingMsg) msg;
            try {
                switch (dingdingMsg.getPerson()) {
                    case Coder:
                        sendMailToCoder(dingdingMsg);
                        break;
                    case Manager:
                        sendMailToManager(dingdingMsg);
                        break;
                    case Operator:
                        sendMailToOperator(dingdingMsg);
                        break;
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        } else {
            log.error("无效的邮件数据{}", msg.toString());
        }
    }

    /**
     * 发给运营人员
     */
    public void sendMailToOperator(DingDingMsg mailMsg) {
        String title = mailMsg.getTitle();
        String content = mailMsg.getContent();
        sendSimpleTextMailActual(title + "\n\n内容：" + content, operator, null);
    }

    /**
     * 发给游动运营人员
     */
    public void sendMailToYdOperator(DingDingMsg mailMsg) {
        String title = mailMsg.getTitle();
        String content = mailMsg.getContent();
        sendSimpleTextMailActual(title + "\n\n内容：" + content, ydOperator, null);
    }

    /**
     * 发给管理人员
     */
    public void sendMailToManager(DingDingMsg mailMsg) {
        String title = mailMsg.getTitle();
        String content = mailMsg.getContent();
        sendSimpleTextMailActual(title + "\n\n内容：" + content, manager, null);
    }

    /**
     * 发给程序员
     */
    public void sendMailToCoder(DingDingMsg mailMsg) {
        String title = mailMsg.getTitle();
        String content = mailMsg.getContent();
        if (mailMsg.getErrorLevel() == ErrorLevel.NONE) {
            sendSimpleTextMailActual(title + "\n\n内容：" + content, infoCoder, null);
            return;
        }
        sendSimpleTextMailActual(title + "\n\n内容：" + content, errorCoder, null);
    }

    /**
     * 发送消息
     *
     * @param content
     * @param toWho
     */
    public void sendSimpleTextMail(String content, String[] toWho) {
        if (null == toWho) {
            return;
        }
        for (String who : toWho) {
            sendSimpleTextMailActual(content, who, null);
        }
    }

    /**
     * 发送消息
     *
     * @param content
     * @param toWho
     * @return
     */
    public boolean sendSimpleTextMail(String content, String toWho) {
        return sendSimpleTextMailActual(content, toWho, null);
    }

    /**
     * 发送给群机器人
     *
     * @param content     内容
     * @param accessToken 钉钉群机器人获取，仅能在pc客户端看到
     * @param atMobiles
     * @用户手机号列表
     */
    private boolean sendSimpleTextMailActual(String content, String accessToken, List<String> atMobiles) {
        if (null == content || null == accessToken || accessToken.length() == 0) {
            log.error("钉钉-> {} 无法继续执行，因为缺少基本的参数：内容，accessToken", content);
            throw new RuntimeException("钉钉无法继续发送，因为缺少必要的参数！");
        }
        boolean ret = false;
        try {
            DingTalkClient client = new DefaultDingTalkClient(url + accessToken);
            OapiRobotSendRequest request = new OapiRobotSendRequest();
            request.setMsgtype("text");
            OapiRobotSendRequest.Text text = new OapiRobotSendRequest.Text();
            text.setContent(content);
            request.setText(text);
            OapiRobotSendRequest.At at = new OapiRobotSendRequest.At();
            if (null != atMobiles && !atMobiles.isEmpty()) {
                at.setAtMobiles(atMobiles);
            }
            request.setAt(at);
            OapiRobotSendResponse response = client.execute(request);
            String code = response.getErrorCode();
            if ("0".equals(code)) {
                ret = true;
            } else {
                log.error(response.getBody());
            }
        } catch (ApiException e) {
            log.error(e.getMessage(), e);
        }
        return ret;
    }
}
