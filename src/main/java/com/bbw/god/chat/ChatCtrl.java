package com.bbw.god.chat;

import com.bbw.App;
import com.bbw.common.BbwSensitiveWordUtil;
import com.bbw.common.SensitiveWordUtil;
import com.bbw.common.StrUtil;
import com.bbw.db.redis.RedisValueUtil;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.db.entity.CfgServerEntity;
import com.bbw.god.game.config.server.ServerTool;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.redis.UserRedisKey;
import com.bbw.god.rd.RDCommon;
import com.bbw.god.security.param.SecurityParam;
import com.bbw.god.server.guild.UserGuild;
import com.bbw.god.uac.entity.AccountEntity;
import com.bbw.god.uac.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashSet;
import java.util.Set;

/**
 * 提供给聊天服务器调用的服务接口
 *
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-05-23 14:12
 */
@RestController
@RequestMapping(value = "/chat")
public class ChatCtrl {
    @Autowired
    private App app;
    @Autowired
    private ChatService chatService;
    @Autowired
    private GameUserService userService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private RedisValueUtil<SecurityParam> valueRedis;// 玩家
    @Value("${chat-server-room-max-user:2000}")
    private int serverRoomMaxUser;
    @Value("${chat-org-room-max-user:50}")
    private int orgRoomMaxUser;

    /**
     * 聊天房间列表
     *
     * @return
     */
    @RequestMapping(value = "/roomList")
    public ChatRoomSetting roomList() {
        ChatRoomSetting setting = chatService.getChatRoomSetting();
        setting.setOrgRoomMaxUser(orgRoomMaxUser);
        setting.setServerRoomMaxUser(orgRoomMaxUser);
        return setting;
    }

    /**
     * 区服聊天室创建
     *
     * @return
     */
    @RequestMapping(value = "/creat!server")
    public void creatServerChat(int sid) {
        chatService.createServerChatRoom(sid);
    }

    /**
     * 行会聊天室创建 如果创建行会时没创建成功则调用该接口即可
     *
     * @return
     */
    @RequestMapping(value = "/creat!guild")
    public void creatGuildChat(int uid) {
        chatService.createGuildChatRoom(uid);
    }

    @RequestMapping(value = "/chatLogin")
    public ChatLoginResult chatLogin(long rid, String token) {
        if (app.runAsProd()) {
            Long sid = rid / 100000 % 10000;
            CfgServerEntity server = ServerTool.getServer(sid.intValue());
            if (server == null) {
                throw ExceptionForClientTip.fromMsg("不存在rid[" + rid + "]的玩家！");
            }
        }
        // TODO:IP限制，如果不是聊天服务器发起的请求，则不予处理
        GameUser usr = null;
        try {
            usr = userService.getGameUser(rid);
        } catch (Exception e) {
            throw e;
        }
        if (null == usr) {
            throw ExceptionForClientTip.fromMsg("不存在rid[" + rid + "]的玩家！");
        }
        Set<String> tokens = new HashSet<>();
        if (!app.runAsDev()) {
            String redisKey = UserRedisKey.getRunTimeVarKey(rid, "token");
            SecurityParam tk = valueRedis.get(redisKey);
            if (null == tk) {
                throw ExceptionForClientTip.fromMsg("玩家[" + rid + "]不存在令牌集！");
            }
            if (!tk.getTokens().contains(token)) {
                throw ExceptionForClientTip.fromMsg("玩家[" + rid + "]提供的令牌[" + token + "]无效！");
            }
            tokens = tk.getTokens();
        }

        ChatLoginResult clr = ChatLoginResult.instance(usr, userService.getOriServer(rid), tokens);
        clr.setUid(usr.getRoleInfo().getUserName());
        UserGuild userGuild = userService.getSingleItem(rid, UserGuild.class);
        if (null == userGuild || userGuild.getGuildId() == 0) {
            clr.setOrgId(-1);
        } else {
            clr.setOrgId(userGuild.getGuildId());
        }
        return clr;
    }

    @Deprecated
    @RequestMapping("/payChat")
    public RDCommon payServerChat(long rid) {
        /*RDCommon rd = new RDCommon();
        ResChecker.checkCopper(userService.getGameUser(rid), 10 * 10000);
        ResEventPublisher.pubCopperDeductEvent(rid, 10 * 10000L, WayEnum.Chat, rd);
        return rd;*/
        return new RDCommon();
    }

    /**
     * 敏感词检查，返回检查后的文本
     *
     * @param txt
     * @return
     */
    @RequestMapping("/checkWord")
    public RDChat check(String txt, Long uid) throws UnsupportedEncodingException {
        String nTxt = URLDecoder.decode(txt, "utf-8");
        if (null != uid) {
            GameUser gameUser = userService.getGameUser(uid);
            AccountEntity account = accountService.findByAccount(gameUser.getRoleInfo().getUserName());
            if (SensitiveWordUtil.isNotPass(nTxt, gameUser.getRoleInfo().getChannelId(), account.getOpenId())) {
                throw ExceptionForClientTip.fromi18nKey("input.not.sensitive.words");
            }
        }
        if (!StrUtil.isBlank(nTxt) && txt.indexOf("zf_") < 0) {
            nTxt = BbwSensitiveWordUtil.replaceSensitiveWord(nTxt);
        }
        return RDChat.returnTxt(nTxt);
    }
}
