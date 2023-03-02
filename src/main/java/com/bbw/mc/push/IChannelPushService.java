package com.bbw.mc.push;

import java.util.List;

/**
 * @author suhq
 * @description: 渠道推送服务
 * @date 2020-02-27 21:52
 **/
public interface IChannelPushService {
    void push(List<PushReceiver> receivers,String title, String content);
}
