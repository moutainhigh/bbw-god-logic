package com.bbw.god.chat.customer;

import com.bbw.god.db.entity.InsHelperMessageEntity;
import com.bbw.god.rd.RDSuccess;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * 返回（一个）客户端 的所有消息记录
 *
 * @author: huanghb
 * @date: 2021/10/26 17:13
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode(callSuper = true)
public class RDHelperMessages extends RDSuccess {
    private static final long serialVersionUID = 1L;
    /** 返回一个chatroom（房间）的所有内容 */
    private List<RdMessage> messageInfos = new ArrayList<>();

    @Data
    public static class RdMessage {
        /** 发送人的姓名 */
        private String sender;
        /** 发送时间 */
//        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
        private long sendTime;
        /** 发送内容 */
        private String content;
    }


    public static RDHelperMessages instance(List<InsHelperMessageEntity> entitys) {
        RDHelperMessages helperMessages = new RDHelperMessages();
        List<RdMessage> messageInfos = new ArrayList<>();
        entitys.stream().forEach(l -> {
            RdMessage messageInfo = new RdMessage();
            messageInfo.setSender(l.getSender());
            messageInfo.setContent(l.getContent());
            messageInfo.setSendTime(l.getSendTime().getTime());
            messageInfos.add(messageInfo);
        });
        helperMessages.setMessageInfos(messageInfos);
        return helperMessages;
    }
}
