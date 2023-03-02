package com.bbw.god.notify.push;

import com.bbw.common.DateUtil;
import com.bbw.common.ListUtil;
import com.bbw.common.SetUtil;
import com.bbw.common.StrUtil;
import com.bbw.god.activityrank.ActivityRankEnum;
import com.bbw.god.activityrank.ActivityRankService;
import com.bbw.god.activityrank.server.fuhao.event.EPFuHaoRankUp;
import com.bbw.god.activityrank.server.fuhao.event.FuHaoRankUpEvent;
import com.bbw.god.event.EventParam;
import com.bbw.god.game.config.server.ServerTool;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.buddy.FriendBuddy;
import com.bbw.god.gameuser.mail.UserMail;
import com.bbw.god.gameuser.mail.event.ReceiveMailEvent;
import com.bbw.god.gameuser.redis.GameUserRedisUtil;
import com.bbw.god.login.LoginPlayer;
import com.bbw.god.login.event.LoginEvent;
import com.bbw.god.server.ServerUserService;
import com.bbw.god.server.ServerUserTmpStatusService;
import com.bbw.god.server.fst.event.EVFstWin;
import com.bbw.god.server.fst.event.FstWinEvent;
import com.bbw.god.server.fst.server.FstServerService;
import com.bbw.god.server.guild.GuildInfo;
import com.bbw.god.server.guild.event.EPGuildEightDiagramsTask;
import com.bbw.god.server.guild.event.EPGuildJoin;
import com.bbw.god.server.guild.event.GuildEightDiagramsTaskEvent;
import com.bbw.god.server.guild.event.GuildJoinEvent;
import com.bbw.god.server.monster.event.EPFriendMonsterAdd;
import com.bbw.god.server.monster.event.FriendMonsterAddEvent;
import com.bbw.god.statistics.serverstatistic.GodServerStatisticService;
import com.bbw.mc.push.PushAction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Async
@Service
public class PushListener {
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private ServerUserService serverUserService;
    @Autowired
    private ServerUserTmpStatusService userTmpStatusService;
    @Autowired
    private FstServerService fstService;
    @Autowired
    GameUserRedisUtil userRedis;
    @Autowired
    private PushAction pushAction;
    @Autowired
    private ActivityRankService activityRankService;
    @Autowired
    private GodServerStatisticService godServerStatisticService;

    @EventListener
    public void cacheToken(LoginEvent event) {
        LoginPlayer loginPlayer = event.getLoginPlayer();
        String pushToken = loginPlayer.getPushToken();
        if (StrUtil.isBlank(pushToken)) {
            return;
        }
        long uid = loginPlayer.getUid();
        int channelId = loginPlayer.getChannelId();
        this.pushAction.cachePushReceiver(uid, pushToken, channelId);
    }

    @EventListener
    public void friendMonsterAdd(FriendMonsterAddEvent event) {
        EPFriendMonsterAdd ep = event.getEP();
        Long guId = ep.getGuId();
        FriendBuddy friendBuddy = this.gameUserService.getSingleItem(guId, FriendBuddy.class);
        if (friendBuddy == null) {
            return;
        }
        Set<Long> friendUids = friendBuddy.getFriendUids();
        int sid = gameUserService.getActiveSid(ep.getGuId());
        List<Long> uids = getAblePushUids(sid, friendUids, PushEnum.FRIEND_MONSTER);
        String title = "【" + ServerTool.getServer(sid).getShortName() + "】的好友被野怪欺负了，TA需要你的帮助！";
        String content = "前方出现友怪，进入游戏消灭它吧！";
        if (ListUtil.isNotEmpty(uids)) {
            this.pushAction.push(uids, title, content);
            uids.forEach(u -> {
                // 更新redis中的最新推送友怪时间
                UserPush userPush = this.gameUserService.getSingleItem(u, UserPush.class);
                userPush.setLastPushFriendMonsterTime(DateUtil.now());
                this.gameUserService.updateItem(userPush);
            });
        }
    }

    @EventListener
    public void joinGuild(GuildJoinEvent event) {
        EPGuildJoin ep = event.getEP();
        GuildInfo guild = ep.getGuild();
        List<Long> uids = Arrays.asList(guild.getBossId(), guild.getViceBossId());
        uids = uids.stream().filter(u -> u != null).collect(Collectors.toList());
        int sid = gameUserService.getActiveSid(ep.getGuId());
        uids = getAblePushUids(sid, uids, PushEnum.GUILD_CHECK);
        String title = "在【" + ServerTool.getServer(sid).getShortName() + "】有召唤师申请加入您的行会，快去看看吧！";
        String content = "又有召唤师申请加入您的行会，快去看看吧！";
        if (ListUtil.isNotEmpty(uids)) {
            this.pushAction.push(uids, title, content);
        }
    }

    @EventListener
    public void eightDiagramsTaskHelp(GuildEightDiagramsTaskEvent event) {
        EPGuildEightDiagramsTask ep = event.getEP();
        GuildInfo guild = ep.getGuild();
        List<Long> uids = guild.getMembers();
        uids.remove(ep.getGuId());// 自己不推送
        int sid = gameUserService.getActiveSid(ep.getGuId());
        uids = getAblePushUids(sid, uids, PushEnum.EIGHT_DIAGRAMS_TASK);
        String title = "在【" + ServerTool.getServer(sid).getShortName() + "】有行会成员请求帮助，八卦任务急需支援！";
        String content = "行会成员请求帮助，八卦任务急需支援！";
        if (ListUtil.isNotEmpty(uids)) {
            this.pushAction.push(uids, title, content);
            // 更新行会上次推送八卦任务的时间
            guild.setLastPushHelpTime(DateUtil.now());
        }
    }

    /**
     * 收到私人邮件
     *
     * @param event
     */
    @EventListener
    public void receiveMail(ReceiveMailEvent event) {
        EventParam<UserMail> ep = (EventParam<UserMail>) event.getSource();
        UserMail mail = ep.getValue();
        if (!godServerStatisticService.getLoginUids(gameUserService.getActiveSid(mail.getReceiverId()), DateUtil.now()).contains(mail.getReceiverId())) {
            return;
        }
        GameUser gu = this.gameUserService.getGameUser(mail.getReceiverId());
        String title = ServerTool.getServer(gu.getServerId()).getShortName() + "【" + gu.getRoleInfo().getNickname() + "】收到邮件【" + mail.getTitle() + "】";
        int sid = this.gameUserService.getActiveSid(mail.getReceiverId());
        push(sid, mail.getReceiverId(), title, mail.getContent());
    }

//	@EventListener
//	public void sxdhPush(SxdhTitleChangeEvent event) {
//		SxdhTitleChange ep = event.getEP();
//		Integer sxdhTitle = ep.getTitle();
//		if (sxdhTitle < Title.SHANG_XIAN.getValue()) {
//			return;
//		}
//		Map<Long, Title> oldRankMap = ep.getOldRankMap();
//		List<Long> uids = getAblePushUids(oldRankMap.keySet(), PushEnum.SXDH);
//		// 只推送头衔下滑的玩家
//		uids = uids.stream().filter(u -> sxdhRankService.getTitle(RankType.RANK, u).getValue() < oldRankMap.get(u).getValue()).collect(Collectors.toList());
//		int sid = gameUserService.getActiveSid(ep.getGuId());
//		String title = "在【" + ServerTool.getServer(sid).getShortName() + "】有召唤师夺取了您的神仙大会头衔，快去夺取您应有的荣耀吧！";
//		String content = "有召唤师夺取了您的神仙大会头衔，快去夺取您应有的荣耀吧！";
//		if (ListUtil.isNotEmpty(uids)) {
//			this.pushAction.push(uids, title, content);
//		}
//		uids.forEach(u -> this.userTmpStatusService.setTmpStatus(gameUserService.getActiveSid(u), u, ServerUserTmpStatusService.PUSH_SXDH_RANK_DOWN));
//	}

    @EventListener
    public void fhbPush(FuHaoRankUpEvent event) {
        EPFuHaoRankUp ep = event.getEP();
        Map<Long, Integer> oldRankMap = ep.getOldRankMap();
        int sid = gameUserService.getActiveSid(ep.getGuId());
        List<Long> uids = getAblePushUids(sid, oldRankMap.keySet(), PushEnum.FU_HAO_RANK);
        // 只推送排名下滑的玩家
        uids = uids.stream().filter(u -> activityRankService.getRank(u, ActivityRankEnum.FUHAO_RANK) > oldRankMap.get(u)).collect(Collectors.toList());
        String title = "在【" + ServerTool.getServer(sid).getShortName() + "】您的富豪榜排名已下滑，快去跑商挣取更多铜钱吧！";
        String content = "您的富豪榜排名已下滑，快去跑商挣取更多铜钱吧！";
        if (ListUtil.isNotEmpty(uids)) {
            this.pushAction.push(uids, title, content);
        }
        uids.forEach(u -> this.userTmpStatusService.setTmpStatus(gameUserService.getActiveSid(u), u, ServerUserTmpStatusService.PUSH_FHB_RANK_DOWN));
    }

    /**
     * 封神台排名下滑推送
     *
     * @param event
     */
    @EventListener
    public void fstPush(FstWinEvent event) {
        EventParam<EVFstWin> ep = (EventParam<EVFstWin>) event.getSource();
        EVFstWin evFst = ep.getValue();
        int sId = this.gameUserService.getActiveSid(ep.getGuId());
        long oppId = evFst.getOppId();
        if (oppId < 0) {
            // 机器人不推
            return;
        }
        // 登录前只推送一次
        if (this.userTmpStatusService.isSeted(sId, oppId, ServerUserTmpStatusService.PUSH_FST_RANK_DOWN)) {
            return;
        }
        if (!godServerStatisticService.getLoginUids(sId, DateUtil.now()).contains(oppId)) {
            return;
        }
        int oldOppRank = evFst.getOldOppRank();

        int sid = gameUserService.getActiveSid(ep.getGuId());
        String title = "在【" + ServerTool.getServer(sid).getShortName() + "】有召唤师正在对您进行攻击，您的封神台排名已下滑。";
        String content = "有召唤师在封神台战胜了您，您的排名已下滑";
        if (this.fstService.getFstRank(oppId) > oldOppRank) { // 名次下降
            pushFstRankDown(sid, oppId, title, content);
        }
    }

    /**
     * 推送封神台名次下降
     *
     * @param sid
     * @param opponentId
     * @param title
     * @param content
     */
    private void pushFstRankDown(int sid, long opponentId, String title, String content) {
        // 不在Redis的玩家说明玩家太久没登录，没办法进行推送，直接跳过
        if (!this.userRedis.existsUser(opponentId)) {
            log.warn(opponentId + "太久没登录，没办法进行推送");
            return;
        }
        UserPush userPush = this.gameUserService.getSingleItem(opponentId, UserPush.class);
        if (null == userPush) {
            return;
        }
        if (!userPush.ableToPush(PushEnum.FENG_SHEN_TAI)) {
            return;
        }
        pushAction.push(opponentId, title, content);
        userTmpStatusService.setTmpStatus(sid, opponentId, ServerUserTmpStatusService.PUSH_FST_RANK_DOWN);
    }

    private void push(int sid, long uid, String title, String content) {
        // Set<Long> onlineUids = gameOnlineService.getLastOnlineUids(sid);
        // // 玩家在线不推送
        // if (onlineUids.contains(uid)) {
        // log.warn(uid + "在线，不推送");
        // return;
        // }
        // 不在Redis的玩家说明玩家太久没登录，没办法进行推送，直接跳过
        if (!this.userRedis.existsUser(uid)) {
            log.warn(uid + "太久没登录，没办法进行推送");
            return;
        }
        pushAction.push(uid, title, content);
    }

    /**
     * 筛选出能够推送的玩家id集合
     *
     * @param uids     需要筛选的玩家id集合
     * @param pushEnum 要推送的功能枚举类
     * @return
     */
    private List<Long> getAblePushUids(int sid, Collection<Long> uids, PushEnum pushEnum) {
        List<Long> uidsToPush = new ArrayList<>();
        Set<Long> loginUids = godServerStatisticService.getLoginUids(sid, DateUtil.now());
        if (SetUtil.isNotEmpty(loginUids)) {
            return uidsToPush;
        }
        uidsToPush = uids.stream().filter(uid -> loginUids.contains(uid)).collect(Collectors.toList());
        uidsToPush = uidsToPush.stream().filter(uid -> {
            UserPush userPush = this.gameUserService.getSingleItem(uid, UserPush.class);
            if (userPush != null && !userPush.ableToPush(pushEnum)) {
                return false;
            }
            if (pushEnum == PushEnum.SXDH && this.userTmpStatusService.isSeted(sid, uid, ServerUserTmpStatusService.PUSH_SXDH_RANK_DOWN)) {
                return false;
            }
            if (pushEnum == PushEnum.FU_HAO_RANK && this.userTmpStatusService.isSeted(sid, uid, ServerUserTmpStatusService.PUSH_FHB_RANK_DOWN)) {
                return false;
            }
            return true;
        }).collect(Collectors.toList());
        return uidsToPush;
    }
}
