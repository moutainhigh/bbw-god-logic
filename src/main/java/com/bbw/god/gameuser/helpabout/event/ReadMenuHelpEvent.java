package com.bbw.god.gameuser.helpabout.event;

import com.bbw.god.event.IEventParam;
import org.springframework.context.ApplicationEvent;

/**
 * @description: 客户端 菜单-帮助 按钮点击事件
 * @author: suchaobin
 * @createTime: 2019-11-21 13:42
 **/
public class ReadMenuHelpEvent extends ApplicationEvent implements IEventParam {
    private static final long serialVersionUID = 1L;

    public ReadMenuHelpEvent(EPReadMenuHelp eventParam) {
        super(eventParam);
    }

    @SuppressWarnings("unchecked")
    @Override
    public EPReadMenuHelp getEP() {
        return (EPReadMenuHelp) getSource();
    }
}
