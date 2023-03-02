package com.bbw.god.notify.push;

import com.bbw.exception.CoderException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author suchaobin
 * @description 推送枚举类
 * @date 2019/12/20 15:25
 */
@Getter
@AllArgsConstructor
public enum PushEnum {
    /**
     * name是枚举类的名称，value是对应的值
     */
    GUILD_CHECK("行会审核", 10),
    FRIEND_MONSTER("友怪", 20),
    EIGHT_DIAGRAMS_TASK("八卦求字任务", 30),
    DIACE_FULL("体力已满", 40),
    MO_WANG("魔王活动", 50),
    CHAN_JIE_DOU_FA("阐截斗法", 60),
    FENG_SHEN_TAI("封神台", 70),
    SXDH("神仙大会", 80),
    FU_HAO_RANK("富豪榜", 90),
    MAIL("邮件", 100),
    ;


    private String name;
    private Integer value;

    public static PushEnum fromValue(int value) {
        for (PushEnum item : values()) {
            if (item.getValue() == value) {
                return item;
            }
        }
        throw CoderException.high("无效的推送值-" + value);
    }

    public static PushEnum fromName(String name) {
        for (PushEnum item : values()) {
            if (item.getName().equals(name)) {
                return item;
            }
        }
        throw CoderException.high("无效的推送名-" + name);
    }

    public static List<Integer> getAllPushValueList() {
        List<Integer> pushList = new ArrayList<>();
        PushEnum[] values = PushEnum.values();
        for (PushEnum pushEnum : values) {
            pushList.add(pushEnum.getValue());
        }
        return pushList;
    }
}
