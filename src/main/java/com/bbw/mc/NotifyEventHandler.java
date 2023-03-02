package com.bbw.mc;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * 通知类消息发送处理器，支持的类型见MsgType
 *
 * @author: suhq
 * @date: 2021/12/16 2:08 下午
 */
@Slf4j
@Async
@Component
public class NotifyEventHandler {
    @Autowired
    @Lazy
    private List<NotifyService> notifyServices;

    public void notify(Msg msg) {
        try {
            Optional<NotifyService> optional = notifyServices.stream().filter(tmp -> tmp.isSupport(msg.getType())).findFirst();
            if (optional.isPresent()) {
                optional.get().notify(msg);
            }
        } catch (Exception e) {
            log.error(msg.toString());
            e.printStackTrace();
        }
    }
}
