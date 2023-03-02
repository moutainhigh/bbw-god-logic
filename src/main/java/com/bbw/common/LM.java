package com.bbw.common;

import com.bbw.sys.session.RequestContextHolder;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * 国际化类
 *
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2018-10-16 17:48
 */
public enum LM {
    I;
    private static String DEFAULT_MESSAGE="";
    private static MessageSource messageSource = (MessageSource) SpringContextUtil.getBean("messageSource");
    private static Map<String, Locale> SUPPORT_LANGUAGE = new HashMap<String, Locale>() {
        private static final long serialVersionUID = 5068599144061400730L;

        {
            put("default", LocaleContextHolder.getLocale());
            put("zh", Locale.SIMPLIFIED_CHINESE);
            put("tc", Locale.TAIWAN);
        }
    };
    private static Map<Long, Locale> USER_LOCAL = new HashMap<>(1024);

    /**
     * 获取语言
     *
     * @param uid
     * @return
     */
    public Locale getLocal(long uid) {
        Locale locale = USER_LOCAL.get(uid);
        if (null != locale) {
            return locale;
        }
        return SUPPORT_LANGUAGE.get("default");
    }

    /**
     * 设置语言
     *
     * @param uid
     * @param lang
     */
    public void setLocal(long uid, String lang) {
        if (USER_LOCAL.containsKey(uid)) {
            return;
        }
        Locale locale = SUPPORT_LANGUAGE.get(lang);
        if (null != locale) {
            USER_LOCAL.put(uid, locale);
        }

    }


    /**
     * 对应地区的语言消息字符串
     *
     * @param key：对应文本配置的key.
     * @return 对应地区的语言消息字符串
     */
    public String getMsg(String key) {
        Locale locale = SUPPORT_LANGUAGE.get(RequestContextHolder.getValue());
        if (null == locale) {
            locale = LocaleContextHolder.getLocale();
        }
        return this.getMsg(key, null, DEFAULT_MESSAGE, locale);
    }

    /**
     * @param key
     * @param formatArgs
     * @return
     */
    public String getFormatMsg(String key, Object... formatArgs) {
        String msgTpl = LM.I.getMsg(key);
        String msg = String.format(msgTpl, formatArgs);
        return msg;
    }

    /**
     * 异步广播调用
     *
     * @param uid
     * @param key
     * @param args
     * @return
     */
    public String getMsgByUid(long uid, String key, Object... args) {
        String message = messageSource.getMessage(key, null, DEFAULT_MESSAGE, getLocal(uid));
        if (null != args && args.length > 0) {
            message = String.format(message, args);
        }
        return message;
    }

    private String getMsg(String key, Object[] args, String defaultMessage, Locale locale) {
        return messageSource.getMessage(key, args, defaultMessage, locale);
    }
}
