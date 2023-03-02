package com.bbw.mc;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

/**
 * 消息类型
 */
@Getter
@AllArgsConstructor
public enum MsgType implements Serializable {
    DING_DING("钉钉"),
    MAIL("邮件"),
    // M2C("游戏内通知客户端"),
    BROADCAST("游戏内横幅广播"),
    PUSH("推送");
    // SMS("短信");

    private String name;
}
