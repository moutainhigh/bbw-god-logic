package com.bbw.god.chat.customer;

import com.bbw.common.BbwSensitiveWordUtil;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.db.entity.InsHelperMessageEntity;
import com.bbw.god.db.service.InsHelperMessageService;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 帮助消息记录功能实现逻辑
 *
 * @author: huanghb
 * @date: 2021/12/9 18:42
 */
@Service
public class HelpMessageLogic {
    @Autowired
    private InsHelperMessageService helperMessageService;
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private HelpMessageRoomStatusLogic messageRoomStatisLogic;

    /**
     * 记录新的消息
     *
     * @param content
     * @return
     */
    @SneakyThrows
    public RDHelperMessages writeMessage(Long uid, String content) {
        //参数检测
        if (content.isEmpty()) {
            throw new ExceptionForClientTip("message.content.null");
        }
        //敏感词检测
        content = wordCheck(content);
        //生成消息对象
        InsHelperMessageEntity helperMessage = getInsHelperMessageEntity(content, uid);
        //写入消息到数据库
        helperMessageService.insert(helperMessage);
        //更新聊天房间的的状态
        messageRoomStatisLogic.updateRoomStatusByChatRoom(helperMessage.getChatRoom(), HelperMessageRoomStastusEnum.NOT_Hide.getValue());
        //返回消息对象
        List<InsHelperMessageEntity> insHelperMessageEntityList = Collections.singletonList(helperMessage);
        RDHelperMessages messages = RDHelperMessages.instance(insHelperMessageEntityList);
        return messages;
    }

    /**
     * 生成一个消息对象
     *
     * @param content
     * @param uid
     * @return
     */
    private InsHelperMessageEntity getInsHelperMessageEntity(String content, Long uid) {
        InsHelperMessageEntity helperMessage = new InsHelperMessageEntity();
        long senderId = uid;
        helperMessage.setSenderId(senderId);
        GameUser gameUser = gameUserService.getGameUser(uid);
        helperMessage.setSender(gameUser.getRoleInfo().getNickname());
        helperMessage.setSenderType(HelperMessageTypeEnum.Player.getValue());
        helperMessage.setServerGroupId(gameUserService.getActiveGid(uid));
        helperMessage.setSid(gameUserService.getActiveSid(uid));
        helperMessage.setChannelId(gameUser.getRoleInfo().getChannelId());
        long chatRoom = uid;
        helperMessage.setChatRoom(chatRoom);
        helperMessage.setContent(content);
        helperMessage.setSendTime(new Date());
        helperMessage.setStatus(HelperMessageStatusEnum.NOT_READ.getValue());
        return helperMessage;
    }

    /**
     * 获取某个房间（玩家）的最近50条记录
     *
     * @return
     */
    public List<InsHelperMessageEntity> readMessage(Long uid) {
        //获取最新的50条记录
        List<InsHelperMessageEntity> entityList = helperMessageService.readMessage(uid);
        //如果无聊天记录返回；
        if (entityList.isEmpty()) {
            return entityList;
        }
        //对数据进行排序
        Collections.reverse(entityList);

        //筛选出玩家未读信息
        List<InsHelperMessageEntity> list = entityList.stream().
                filter(t -> t.getStatus() == HelperMessageStatusEnum.NOT_READ.getValue() && t.getSenderType() == HelperMessageTypeEnum.GM.getValue())
                .collect(Collectors.toList());
        //如果无未读信息直接返回
        if (list.isEmpty()) {
            return entityList;
        }
        //更新所有客服对该玩家的信息更新为已读
        for (InsHelperMessageEntity data : list) {
            data.setStatus(HelperMessageStatusEnum.READ.getValue());
        }
        //数据更新到数据库
        helperMessageService.updateBatchById(list, list.size());
        return entityList;
    }

    /**
     * 敏感词检测
     *
     * @param content
     * @return
     * @throws UnsupportedEncodingException
     */
    public String wordCheck(String content) throws UnsupportedEncodingException {
        content = URLDecoder.decode(content, "utf-8");
        content = BbwSensitiveWordUtil.replaceSensitiveWord(content);
        return content;
    }

}
