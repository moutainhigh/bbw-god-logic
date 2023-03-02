package com.bbw.god.db.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.bbw.god.db.dao.InsHelperMessageDao;
import com.bbw.god.db.entity.InsHelperMessageEntity;
import com.bbw.god.db.service.InsHelperMessageRoomStatusService;
import com.bbw.god.db.service.InsHelperMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * 帮助消息记录功能实现逻辑
 * Message record
 *
 * @author: huanghb
 * @date: 2021/10/21 17:52
 */
@Slf4j
@Service
public class InsHelperMessageServiceImpl extends ServiceImpl<InsHelperMessageDao, InsHelperMessageEntity> implements InsHelperMessageService {
    @Autowired
    private InsHelperMessageDao helperMessageDao;
    @Autowired
    private InsHelperMessageRoomStatusService roomStatusService;

    /**
     * 从数据库获取某个房间（玩家）的最近50条记录 *
     *
     * @return
     */
    @Override
    public List<InsHelperMessageEntity> readMessage(Long uid) {
        EntityWrapper<InsHelperMessageEntity> entityWrapper = new EntityWrapper<>();
        List<InsHelperMessageEntity> entityList;
        long chatRoom = uid;
        entityWrapper.eq("chat_room", chatRoom);
        entityWrapper.orderDesc(Arrays.asList("send_time"));
        entityWrapper.last("limit 50");
        entityList = selectList(entityWrapper);
        return entityList;
    }


}