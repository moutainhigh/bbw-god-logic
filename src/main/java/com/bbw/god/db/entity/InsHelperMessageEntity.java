package com.bbw.god.db.entity;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 帮助消息记录数据
 *
 * @author: huanghb
 * @date: 2021/10/21 17:34
 */
@Data
@TableName("ins_helper_message")
public class InsHelperMessageEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    /** 消息记录id */
    @TableId
    private Long dataId;
    /** 发送者id（客服 启用sys的user_id） */
    private Long senderId;
    /** 发送者名称 */
    private String sender;
    /** 发送者身份 0代表客服，1代表用户 */
    private Integer senderType;
    /** 区服组id */
    private Integer serverGroupId;
    /** 区服id */
    private Integer sid;
    /** 渠道id */
    private Integer channelId;
    /** 聊天房间id */
    private Long chatRoom;
    /** 发送内容 */
    private String content;
    /** 发送时间 */
    private Date sendTime = new Date();
    /** 留言是否被读  0 正常 1 新留言 */
    private Integer status;
    
}
