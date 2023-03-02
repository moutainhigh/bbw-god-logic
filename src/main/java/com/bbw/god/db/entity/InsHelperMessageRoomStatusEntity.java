package com.bbw.god.db.entity;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.bbw.god.chat.customer.HelperMessageRoomStastusEnum;
import lombok.Data;

import java.io.Serializable;

/**
 * 客服消息-房间状态
 *
 * @author: huanghb
 * @date: 2021/11/12 14:12
 */
@Data
@TableName("ins_helper_message_room_status")
public class InsHelperMessageRoomStatusEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableId
    private Long dataId;
    private Long chatRoom;
    /** 聊天室房间状态，0或null代表房间不隐藏，1代表房间隐藏 */
    private Integer roomStatus;

    public int getNewChatRoomStatus(Integer roomStatus) {
        return roomStatus == HelperMessageRoomStastusEnum.NOT_Hide.getValue() ? HelperMessageRoomStastusEnum.Hide.getValue() : HelperMessageRoomStastusEnum.NOT_Hide.getValue();
    }
}
