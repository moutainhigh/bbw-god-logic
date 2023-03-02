package com.bbw.god.chat.customer;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.bbw.god.db.entity.InsHelperMessageRoomStatusEntity;
import com.bbw.god.db.service.InsHelperMessageRoomStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 聊天房间的状态实现逻辑
 *
 * @author: huanghb
 * @date: 2021/12/9 19:13
 */
@Service
public class HelpMessageRoomStatusLogic {
    @Autowired
    private InsHelperMessageRoomStatusService messageRoomStatusService;

    /**
     * 修改聊天房间的状态  0 和空为不隐藏，  1 为隐藏
     *
     * @param uid
     * @param roomStatus
     * @return
     */
    public Boolean updateRoomStatusByChatRoom(Long uid, Integer roomStatus) {
        EntityWrapper<InsHelperMessageRoomStatusEntity> roomStatusWrapper = new EntityWrapper<>();
        long chatRoom = uid;
        roomStatusWrapper.eq("chat_room", chatRoom);
        InsHelperMessageRoomStatusEntity roomStatusEntity = messageRoomStatusService.selectOne(roomStatusWrapper);
        if (roomStatusEntity == null) {
            roomStatusEntity = new InsHelperMessageRoomStatusEntity();
            roomStatusEntity.setChatRoom(chatRoom);
            roomStatusEntity.setRoomStatus(roomStatus);
            return messageRoomStatusService.insert(roomStatusEntity);
        }
        roomStatusEntity.setRoomStatus(roomStatus);
        return messageRoomStatusService.updateById(roomStatusEntity);
    }
}
