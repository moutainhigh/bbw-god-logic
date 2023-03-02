package com.bbw.coder;

import com.bbw.common.SpringContextUtil;
import com.bbw.exception.ErrorLevel;
import com.bbw.exception.GodException;
import com.bbw.mc.mail.MailAction;

/**
 * 通知程序员
 *
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-03-30 15:47
 */
public class CoderNotify {
    protected static MailAction notify = SpringContextUtil.getBean(MailAction.class);

    /**
     * 通知程序员【消息通知】
     */
    public static void notifyCoderInfo(String title, String content) {
        notify.notifyCoder(title, content);
    }

    /**
     * 通知程序员【错误级别正常】
     */
    public static void notifyCoderNormal(String title, Exception e) {
        notify.notifyCoder(title, GodException.getStackMessage(e));
    }

    /**
     * 通知程序员【错误级别高】
     */
    public static void notifyCoderHigh(String title, Exception e) {
        notify.notifyCoder(ErrorLevel.HIGH, title, GodException.getStackMessage(e));
    }

    /**
     * 通知程序员【错误级别高】
     */
    public static void notifyCoderFatal(String title, Exception e) {
        notify.notifyCoder(ErrorLevel.FATAL, title, GodException.getStackMessage(e));
    }

}
