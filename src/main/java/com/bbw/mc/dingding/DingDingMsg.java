package com.bbw.mc.dingding;

import com.bbw.exception.ErrorLevel;
import com.bbw.mc.Msg;
import com.bbw.mc.MsgType;
import com.bbw.mc.Person;
import lombok.Getter;

/**
 * 邮件信息
 *
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2018-10-25 17:10
 */
@Getter
public class DingDingMsg extends Msg {
    private String title;
    private ErrorLevel errorLevel;

    public DingDingMsg(Person person, ErrorLevel errorLevel, String title, String content) {
        this.type = MsgType.DING_DING;
        this.errorLevel = errorLevel;
        this.person = person;
        this.title = title;
        this.content = content;
    }

}
