package com.bbw.mc.mail;

import com.bbw.App;
import com.bbw.common.DateUtil;
import com.bbw.common.IpUtil;
import com.bbw.common.StrUtil;
import com.bbw.exception.AppSecurityException;
import com.bbw.exception.ErrorLevel;
import com.bbw.exception.GodException;
import com.bbw.exception.SecurityLevel;
import com.bbw.mc.Msg;
import com.bbw.mc.NotifyEventHandler;
import com.bbw.mc.Person;
import com.bbw.mc.dingding.DingDingMsg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 对外邮件接口
 *
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2018年8月22日 下午3:45:31
 */
//TODO 06v1合并后重构
@Service
public class MailAction {
    @Autowired
    private App app;
    @Autowired
    private NotifyEventHandler notifyEventHandler;

    private String getTitle(String title) {
        return "[" + app.getActive() + "][" + DateUtil.toDateTimeString(DateUtil.now()) + "]\n标题：" + title;
    }

    private Msg buildMsg(Person person, ErrorLevel errorLevel, String title, String content) {
        return new DingDingMsg(person, errorLevel, title, content);
    }

    /**
     * 通知管理人员
     *
     * @param title
     * @param content
     */
    public void notifyManager(String title, String content) {
        Msg msg = buildMsg(Person.Manager, ErrorLevel.NONE, getTitle(title), content);
        notifyEventHandler.notify(msg);
    }

    /**
     * 通知运营人员
     *
     * @param title
     * @param content
     */
    public void notifyOperator(String title, String content) {
        if (app.runAsDev()) {
            return;
        }
        Msg msg = buildMsg(Person.Operator, ErrorLevel.NONE, getTitle(title), content);
        notifyEventHandler.notify(msg);
    }

    /**
     * 通知程序员
     *
     * @param errorLevel
     * @param title
     * @param content
     */
    public void notifyCoder(ErrorLevel errorLevel, String title, String content) {
        String newTitle = title;
        if (!ErrorLevel.NONE.equals(errorLevel)) {
            newTitle = "[" + errorLevel.name() + "]级[错误]事件:" + title;
        }
        if (app.runAsDev()) {
            return;
        }
        if (StrUtil.isNotNull(content) && content.length()>500){
            content = content.substring(0, 500) + "\n更多堆栈前往服务器IP：" + IpUtil.getInet4Address();
        }else {
            content += "\n更多堆栈前往服务器IP：" + IpUtil.getInet4Address();
        }
        Msg msg = buildMsg(Person.Coder, errorLevel, getTitle(newTitle), content);
        notifyEventHandler.notify(msg);
    }

    /**
     * 通知
     *
     * @param title
     * @param content
     */
    public void notifyCoder(String title, String content) {
        notifyCoder(ErrorLevel.NONE, title, content);
    }

    /**
     * 通知程序员
     *
     * @param level
     * @param title
     * @param content
     */
    public void notifyCoder(SecurityLevel level, String title, String content) {
        String newTitle = "[" + level.name() + "]级[安全]事件:" + title;
        notifyCoder(newTitle, content);
    }

    public void notifyCoderHighLevel(GodException exception) {
        if (exception instanceof AppSecurityException) {
            notifyCoder(SecurityLevel.HIGH, getTitle(exception.getMsg()), exception.getStackMessage());
        } else {
            notifyCoder(ErrorLevel.HIGH, getTitle(exception.getMsg()), exception.getStackMessage());
        }
    }
}
