package com.bbw.god.gameuser.helpabout.event;

import com.bbw.common.SpringContextUtil;
import com.bbw.god.event.BaseEventParam;
import com.bbw.god.gameuser.GameUserService;

/**
 * @description: 客户端 菜单-帮助 按钮点击事件推送器
 * @author: suchaobin
 * @createTime: 2019-11-21 13:48
 **/
public class ReadMenuHelpEventPublisher {
    private static GameUserService gameUserService = SpringContextUtil.getBean(GameUserService.class);

    public static void pubMenuHelpEvent(BaseEventParam bep) {
        SpringContextUtil.publishEvent(new ReadMenuHelpEvent(new EPReadMenuHelp(bep)));
    }
}
