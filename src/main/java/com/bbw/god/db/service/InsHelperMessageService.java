package com.bbw.god.db.service;

import com.baomidou.mybatisplus.service.IService;
import com.bbw.god.db.entity.InsHelperMessageEntity;

import java.util.List;

/**
 * 帮助消息数据功能的业务处理
 *
 * @author: huanghb
 * @date: 2021/10/23 18:20
 */
public interface InsHelperMessageService extends IService<InsHelperMessageEntity> {
    /**
     * 从数据库获取某个房间（玩家）的最近50条记录
     *
     * @param chatRoom
     * @return
     */
    List<InsHelperMessageEntity> readMessage(Long chatRoom);
}