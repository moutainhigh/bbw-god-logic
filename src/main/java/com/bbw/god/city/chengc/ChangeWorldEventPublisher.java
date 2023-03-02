package com.bbw.god.city.chengc;

import com.bbw.common.SpringContextUtil;
import com.bbw.god.event.BaseEventParam;

/**
 * @author suchaobin
 * @description 世界跳转事件发布器
 * @date 2020/9/24 11:22
 **/
public class ChangeWorldEventPublisher {

    public static void pubChangeWorldEvent(int oldWorldType, int newWorldType, BaseEventParam bep) {
        SpringContextUtil.publishEvent(new ChangeWorldEvent(new EPChangeWorld(oldWorldType, newWorldType, bep)));
    }
}
