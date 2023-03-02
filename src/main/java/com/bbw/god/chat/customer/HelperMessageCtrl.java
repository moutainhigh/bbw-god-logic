package com.bbw.god.chat.customer;

import com.bbw.common.SensitiveWordUtil;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.controller.AbstractController;
import com.bbw.god.db.entity.InsHelperMessageEntity;
import com.bbw.god.game.CR;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.uac.entity.AccountEntity;
import com.bbw.god.uac.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 消息记录功能入口
 *
 * @author: huanghb
 * @date: 2021/10/21 17:15
 */
@RestController
public class HelperMessageCtrl extends AbstractController {
    @Autowired
    private HelpMessageLogic helpMessageLogic;
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private AccountService accountService;

    /**
     * 记录新的消息
     *
     * @param content 消息内容
     * @return
     */
    @RequestMapping(CR.HelperMessage.MESSAGE_WRITE)
    public RDHelperMessages writeMessage(Long uid, String content) {
        if (null != uid && uid > 0) {
            GameUser gameUser = gameUserService.getGameUser(uid);
            AccountEntity account = accountService.findByAccount(gameUser.getRoleInfo().getUserName());
            if (SensitiveWordUtil.isNotPass(content, gameUser.getRoleInfo().getChannelId(), account.getOpenId())) {
                throw ExceptionForClientTip.fromi18nKey("input.not.sensitive.words");
            }
        }
        return helpMessageLogic.writeMessage(uid, content);
    }

    /**
     * 获取某个房间（玩家）的最近50条记录
     *
     * @return
     */
    @RequestMapping(CR.HelperMessage.MESSAGE_READ)
    public RDHelperMessages readMessage(Long uid) {
        //获取消息记录
        List<InsHelperMessageEntity> helperMessageList = helpMessageLogic.readMessage(uid);

        RDHelperMessages messages = RDHelperMessages.instance(helperMessageList);
        return messages;


    }

}
