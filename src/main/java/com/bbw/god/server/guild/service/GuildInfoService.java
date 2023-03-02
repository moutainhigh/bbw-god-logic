package com.bbw.god.server.guild.service;

import com.bbw.common.DateUtil;
import com.bbw.common.ID;
import com.bbw.common.LM;
import com.bbw.common.StrUtil;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.channel.maoer.MaoerSociatyService;
import com.bbw.god.chat.ChatService;
import com.bbw.god.event.BaseEventParam;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.UserLoginInfo;
import com.bbw.god.gameuser.mail.MailService;
import com.bbw.god.gameuser.res.ResChecker;
import com.bbw.god.gameuser.res.ResEventPublisher;
import com.bbw.god.rd.RDCommon;
import com.bbw.god.server.ServerDataService;
import com.bbw.god.server.guild.*;
import com.bbw.god.server.guild.GuildRD.PlayerInfo;
import com.bbw.god.server.guild.event.EPGuildCreate;
import com.bbw.god.server.guild.event.EPGuildJoin;
import com.bbw.god.server.guild.event.GuildEventPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author lwb 行会相关信息处理
 * @version 1.0
 * @date 2019年5月14日
 */

@Service
public class GuildInfoService {
    @Autowired
    private ServerDataService serverDataService;
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private MailService mailService;
    @Autowired
    private ChatService chatService;
    @Autowired
    private MaoerSociatyService maoerSociatyService;
    @Autowired
    private GuildUserService guildUserService;

    /*
     * 获取行会列表
     */
    public GuildRD list(long uid, int sid, int page, int pageSize, String key) {
        if (!guildUserService.opend(uid)) {
            throw new ExceptionForClientTip("guild.not.creat", GuildConstant.OPEN_LEVEL);
        }
        List<GuildInfo> guildInfos = serverDataService.getServerDatas(sid, GuildInfo.class);
        if (!StrUtil.isBlank(key)) {
            guildInfos = guildInfos.stream()
                    .filter(g -> g.getGuildName().indexOf(key) > -1 || g.getBossName().indexOf(key) > -1)
                    .sorted(Comparator.comparing(GuildInfo::getLv).reversed()).collect(Collectors.toList());
        }
        int totalPage = guildInfos.size();
        guildInfos = guildInfos.stream().skip((page - 1) * pageSize).limit(pageSize).collect(Collectors.toList());
        List<GuildRD.RdGuildInfo> guilds = new ArrayList<>();
        guildInfos.stream().forEach(g -> {
            if (g.getStatus() == GuildConstant.MEMBER_CAN_JOIN) {
                boolean isApply = g.getExamineUids().contains(uid);
                g.setStatus(isApply ? 1 : 0);
            }
            GuildRD.RdGuildInfo info = new GuildRD.RdGuildInfo();
            info.setId(g.getId());
            info.setGuildName(g.getGuildName());
            info.setBossName(g.getBossName());
            info.setLv(g.getLv());
            info.setStatus(g.getStatus());
            info.setPeopleProgress(g.getPeopleProgress() + "/" + g.getLimitPeople());
            guilds.add(info);
        });
        GuildRD rd = new GuildRD();
        rd.setGuilds(guilds);
        rd.setTotalPage(totalPage);
        return rd;
    }

    // 创建
    public void create(long uid, String guildName) {
        GameUser user = gameUserService.getGameUser(uid);
        if (checkHaveName(user.getServerId(), guildName)) {
            throw new ExceptionForClientTip("guild.name.exists");
        }
        UserGuild userGuild = guildUserService.getUserGuild(uid);
        if (0 != userGuild.getGuildId().intValue()) {
            throw new ExceptionForClientTip("guild.user.have");
        }
        ResChecker.checkGold(user, 100);
        ResEventPublisher.pubGoldDeductEvent(uid, 100, WayEnum.Guild_create, new RDCommon());
        GuildInfo guild = new GuildInfo();
        guild.setId(ID.INSTANCE.nextId());
        guild.setSid(user.getServerId());
        guild.setBossId(uid);
        guild.setBossName(user.getRoleInfo().getNickname());
        guild.setGuildName(guildName);
        guild.setMaxJoinNum(GuildConstant.MAX_JOIN);
        List<GuildInfo.GuildWords> words = new ArrayList<>();
        GuildInfo.GuildWords uWords = new GuildInfo.GuildWords();
        uWords.setSender(user.getRoleInfo().getNickname());
        uWords.setContent("大家好，我是" + user.getRoleInfo().getNickname() + ",请多多关照!");
        uWords.setUid(uid);
        words.add(uWords);
        guild.setWords(words);

        List<Long> member = new ArrayList<>();
        member.add(uid);
        guild.setMembers(member);
        serverDataService.addServerData(guild);

        userGuild.setGameUserId(uid);
        userGuild.setGuildLv(1);
        userGuild.setGuildId(guild.getId());
        userGuild.setOperateTime(new Date());

        gameUserService.updateItem(userGuild);
        // 广播
        EPGuildCreate ep = EPGuildCreate.instance(new BaseEventParam(uid), guild.getGuildName());
        GuildEventPublisher.pubGuildCreateEvent(ep);
        // 创建聊天室
        chatService.createGuildChatRoom(uid);
        chatService.joinGuildChatRoom(uid);
    }

    // 加入
    public GuildRD join(long uid, int sid, long guildId) {
        GuildRD rd = new GuildRD();
        UserGuild userGuild = guildUserService.getUserGuild(uid);
        if (0 != userGuild.getGuildId()) {
            throw new ExceptionForClientTip("guild.user.have");
        }
        //临时注释，合服完成后取消
//		if (userGuild.getOperateTime() != null
//				&& DateUtil.getHourBetween(userGuild.getOperateTime(), new Date()) < 24) {
//			throw new ExceptionForClientTip("guild.join.ban", 24 - DateUtil.getHourBetween(userGuild.getOperateTime(), new Date()) + "");
//		}
        GuildInfo guild = getGuildInfoBydataId(sid, guildId);
        if (GuildConstant.MEMBER_JOINING == guild.getStatus()) {
            throw new ExceptionForClientTip("guild.join.doing");
        }
        if (GuildConstant.MEMBER_FILL == guild.getStatus()) {
            throw new ExceptionForClientTip("guild.is.fill");
        }
        List<Long> examineList = guild.getExamineUids();
        if (null == examineList) {
            examineList = new ArrayList<>();
            guild.setExamineUids(examineList);
        }

        // 发布申请加入行会事件
        GuildEventPublisher.pubGuildJoinEvent(EPGuildJoin.instance(uid, WayEnum.Guild_Join, rd, guild));

        examineList.add(uid);
        guild.setExamineNum(examineList.size());
        serverDataService.updateServerData(guild);
        return rd;
    }

    // 转让会长
    public void transfer(long uid, int sid, long otherid) {
        GuildInfo guildInfo = getGuildInfo(uid, sid);
        if (guildInfo.getBossId().longValue() != uid) {
            throw new ExceptionForClientTip("guild.not.leader");
        }
        if (guildInfo.getMembers().contains(otherid)) {
            guildInfo.setBossId(otherid);
            guildInfo.setBossName(gameUserService.getGameUser(otherid).getRoleInfo().getNickname());
            serverDataService.updateServerData(guildInfo);
        } else {
            throw new ExceptionForClientTip("guild.user.not.exist");
        }
    }

    // 行会信息
    public GuildRD info(long uid, int sid) {
        GuildRD rd = new GuildRD();
        UserGuild userGuild = guildUserService.getUserGuild(uid);
        if (0 == userGuild.getGuildId()) {
            throw new ExceptionForClientTip("guild.user.not.join");
        }
        GuildInfo guild = getGuildInfoBydataId(sid, userGuild.getGuildId());
        if (guild.getBossId() == uid) {
            GameUser user = gameUserService.getGameUser(uid);
            guild.setBossName(user.getRoleInfo().getNickname());
            guild.setLeaveDay(0L);
        } else {
            UserLoginInfo loginInfo = gameUserService.getSingleItem(guild.getBossId(), UserLoginInfo.class);
            long interval = DateUtil.getHourBetween(loginInfo.getLastLoginTime(), new Date()) / 24;
            guild.setLeaveDay(interval);
        }
        serverDataService.updateServerData(guild);
        for (GuildInfo.GuildWords word : guild.getWords()) {
            if (word.getUid().longValue() == uid) {
                rd.setMyWord(word.getContent());
            }
            for (UserGuild.WordsStatus ws : userGuild.getWords()) {
                if (word.getUid().longValue() == ws.getUid().longValue()) {
                    if (DateUtil.getSecondsBetween(word.getWriteDate(), ws.getWriteDate()) != 0) {
                        word.setStatus(1);
                    } else {
                        word.setStatus(ws.isRead() ? 0 : 1);
                    }
                    break;
                }
            }
        }
        List<UserGuild.WordsStatus> list = new ArrayList<>();
        // 重新遍历生成的原因在于清理已经退出行会的留言信息
        guild.getWords().stream().forEach(p -> {
            UserGuild.WordsStatus ws = new UserGuild.WordsStatus();
            ws.setRead(p.getStatus() == 0);
            ws.setUid(p.getUid());
            ws.setWriteDate(p.getWriteDate());
            list.add(ws);
        });
        userGuild.setWords(list);
        gameUserService.updateItem(userGuild);
        int num = guild.getLimitPeople() - userGuild.getWords().size();
        for (int i = 0; i < num; i++) {
            UserGuild.WordsStatus ws = new UserGuild.WordsStatus();
            ws.setRead(true);
            ws.setUid(0L);
            ws.setWriteDate(null);
            userGuild.getWords().add(ws);
        }
        Integer exp = guild.getTargetExp() - guild.getBaseExp();
        exp = exp == 0 ? guild.getTargetExp() : exp;
        guild.setExpProgres(guild.getExp() + "/" + exp);
        guild.setWords(null);
        rd.setContrbution(userGuild.getContrbution());
        rd.setWords(userGuild.getWords());
        rd.setGuildInfo(guild);

        return rd;
    }
    // 阅读留言

    public GuildRD readWords(long uid, int sid, long wuid) {
        GuildRD rd = new GuildRD();
        UserGuild userGuild = guildUserService.getUserGuild(uid);
        if (userGuild.getGuildId() == 0) {
            throw new ExceptionForClientTip("guild.user.not.join");
        }
        GuildInfo guild = getGuildInfoBydataId(sid, userGuild.getGuildId());
        for (GuildInfo.GuildWords w : guild.getWords()) {
            if (w.getUid() == wuid) {
                w.setSender(gameUserService.getGameUser(wuid).getRoleInfo().getNickname());
                rd.setWord(w);
                break;
            }
        }
        for (UserGuild.WordsStatus w : userGuild.getWords()) {
            if (w.getUid() == wuid) {
                w.setRead(true);
                break;
            }
        }
        gameUserService.updateItem(userGuild);
        return rd;
    }

    // 行会更名
    public GuildRD rename(long uid, int sid, String newName) {
        newName = newName.trim();
        if (checkHaveName(sid, newName)) {
            throw new ExceptionForClientTip("guild.name.exists");
        }
        GuildRD rd = new GuildRD();
        GuildInfo guild = getGuildInfo(uid, sid);
        if (guild.getBossId() != uid) {
            throw new ExceptionForClientTip("guild.not.leader");
        }
        ResChecker.checkGold(gameUserService.getGameUser(uid), 500);
        ResEventPublisher.pubGoldDeductEvent(uid, 500, WayEnum.Guild_impeach, rd);
        guild.setGuildName(newName);
        serverDataService.updateServerData(guild);
        rd.setName(newName);
        return rd;
    }

    // 弹劾
    public GuildRD impeach(long uid, int sid) {
        GuildRD rd = new GuildRD();
        GuildInfo guild = getGuildInfo(uid, sid);
        Long bossId = guild.getBossId();
        UserLoginInfo bossLoginInfo = gameUserService.getSingleItem(bossId, UserLoginInfo.class);
        long interval = DateUtil.getHourBetween(bossLoginInfo.getLastLoginTime(), new Date()) / 24;
        if (interval >= 0) {
            ResChecker.checkGold(gameUserService.getGameUser(uid), 100);
            ResEventPublisher.pubGoldDeductEvent(uid, 100, WayEnum.Guild_impeach, rd);
            guild.setBossId(uid);
            if (null != guild.getViceBossId() && uid == guild.getViceBossId()) {
                guild.setViceBossId(null);
            }
            serverDataService.updateServerData(guild);
            exit(bossId, sid, false);
        } else {
            throw new ExceptionForClientTip("guild.cant.impeach");
        }
        return rd;
    }

    // 待审核列表
    public GuildRD listExamine(long uid, int sid) {
        GuildInfo guiInfo = getGuildInfo(uid, sid);
        List<Long> uidList = guiInfo.getExamineUids();
        List<GuildRD.PlayerInfo> playerInfos = new ArrayList<GuildRD.PlayerInfo>();
        uidList.stream().forEach(u -> {
            GameUser user = gameUserService.getGameUser(u);
            PlayerInfo playerInfo = new PlayerInfo();
            playerInfo.setId(u);
            playerInfo.setLv(user.getLevel());
            playerInfo.setName(user.getRoleInfo().getNickname());
            playerInfos.add(playerInfo);
        });
        GuildRD rd = new GuildRD();
        rd.setPlayers(playerInfos);
        return rd;
    }

    // 拒绝申请
    public void memberRefuse(long uid, int sid, long examineId) {
        GuildInfo guild = getGuildInfo(uid, sid);
        if (guild.getBossId() != uid && guild.getViceBossId() != uid) {
            throw new ExceptionForClientTip("guild.not.leader");
        }
        List<Long> uidList = guild.getExamineUids();
        uidList.remove(examineId);
        guild.setExamineNum(uidList.size());
        serverDataService.updateServerData(guild);

    }

    // 接受申请要求
    public void memberAccept(long uid, int sid, long examineId) {
        GuildInfo guildInfo = getGuildInfo(uid, sid);
        if (guildInfo.getBossId() != uid && guildInfo.getViceBossId() != uid) {
            throw new ExceptionForClientTip("guild.not.leader");
        }
        guildInfo.getExamineUids().remove(examineId);
        guildInfo.setExamineNum(guildInfo.getExamineUids().size());
        if (GuildConstant.MEMBER_FILL == guildInfo.getStatus()) {
            serverDataService.updateServerData(guildInfo);
            throw new ExceptionForClientTip("guild.is.fill");
        }
        if (null == guildInfo.getLimitJoinDate() || guildInfo.getLimitJoinDate() != DateUtil.toDateInt(new Date()) || null == guildInfo.getMaxJoinNum()) {
            guildInfo.setLimitJoinDate(DateUtil.toDateInt(new Date()));
            guildInfo.setMaxJoinNum(GuildConstant.MAX_JOIN);
        }
        if (guildInfo.getMaxJoinNum().intValue() == 0) {
            serverDataService.updateServerData(guildInfo);
            throw new ExceptionForClientTip("guild.today.fill", GuildConstant.MAX_JOIN);
        }
        List<Long> membersList = guildInfo.getMembers();
        //
        if (!membersList.contains(examineId)) {
            if (joinNotSuccess(examineId, guildInfo)) {
                serverDataService.updateServerData(guildInfo);
                throw new ExceptionForClientTip("guild.join.other");
            }
            membersList.add(examineId);
            guildInfo.setMaxJoinNum(guildInfo.getMaxJoinNum() - 1);
            GuildInfo.GuildWords uWords = new GuildInfo.GuildWords();
            GameUser gu = gameUserService.getGameUser(examineId);
            uWords.setSender(gu.getRoleInfo().getNickname());
            uWords.setContent("大家好，我是" + gu.getRoleInfo().getNickname() + ",请多多关照!");
            uWords.setUid(examineId);
            guildInfo.getWords().add(uWords);
            guildInfo.setPeopleProgress(guildInfo.getPeopleProgress() + 1);
            guildInfo.setStatus(guildInfo.getLimitPeople() > guildInfo.getPeopleProgress() ? GuildConstant.MEMBER_CAN_JOIN : GuildConstant.MEMBER_FILL);
            // 审核通过 发送邮件通知玩家
            serverDataService.updateServerData(guildInfo);
            String title = LM.I.getMsgByUid(examineId, "mail.guild.application.pass.title");
            String content = LM.I.getMsgByUid(examineId, "mail.guild.application.pass.content", guildInfo.getGuildName());
            mailService.sendSystemMail(title, content, examineId);
            chatService.joinGuildChatRoom(examineId);
        } else {
            serverDataService.updateServerData(guildInfo);
        }

    }

    // 玩家行会信息加锁
    private synchronized boolean joinNotSuccess(long uid, GuildInfo guildInfo) {
        UserGuild userGuild = guildUserService.getUserGuild(uid);
        if (0 != userGuild.getGuildId()) {
            return true;
        }
        userGuild.setGuildId(guildInfo.getId());
        userGuild.setOperateTime(new Date());
        userGuild.setGuildLv(guildInfo.getLv());
        gameUserService.updateItem(userGuild);
        return false;
    }

    // 成员列表
    public GuildRD listMember(GameUser gu) {
        UserGuild userGuild = guildUserService.getUserGuild(gu.getId());
        if (userGuild.getGuildId() == 0) {
            throw new ExceptionForClientTip("guild.user.not.join");
        }
        GuildInfo guild = getGuildInfoBydataId(gu.getServerId(), userGuild.getGuildId());
        List<GuildRD.RdMember> members = new ArrayList<>();
        GameUser user;
        UserLoginInfo info;
        UserGuild uGuild;
        GuildRD.RdMember member;
        for (Long g : guild.getMembers()) {
            member = new GuildRD.RdMember();
            if (g.longValue() != gu.getId().longValue()) {
                uGuild = gameUserService.getSingleItem(g, UserGuild.class);
                user = gameUserService.getGameUser(g);
                info = gameUserService.getSingleItem(g, UserLoginInfo.class);
                if (info == null) {
                    member.setLeaveDay(30l);
                } else {
                    member.setLeaveDay(DateUtil.getHourBetween(info.getLastLoginTime(), new Date()) / 24);
                }
            } else {
                member.setLeaveDay(0L);
                uGuild = userGuild;
                user = gu;
            }
            member.setContribution(uGuild.getWeekContrbution());
            member.setId(g);
            member.setLv(user.getLevel());
            member.setName(user.getRoleInfo().getNickname());
            if (guild.getBossId().longValue() == member.getId().longValue()) {
                member.setGrade(GuildConstant.GRADE_BOSS);
            } else if (guild.getViceBossId() != null && guild.getViceBossId().longValue() == member.getId().longValue()) {
                member.setGrade(GuildConstant.GRADE_VICE_BOSS);
            }
            members.add(member);
        }
        GuildRD rd = new GuildRD();
        rd.setMembers(members);
        return rd;
    }

    // 踢出成员
    public void expulsion(long uid, int sid, long expulsionId) {
        GuildInfo guild = getGuildInfo(uid, sid);
        if (guild.getBossId() != uid && guild.getViceBossId() != uid) {
            throw new ExceptionForClientTip("guild.not.leader");
        }
        exit(expulsionId, sid, false);
    }

    // 留言
    public void writeWord(GameUser user, String content) {
        content = content.trim();
        if (!maoerSociatyService.sociatyNoticeVerify(user, content)) {
            throw new ExceptionForClientTip("guild.fail.name");
        }
        GuildInfo guild = getGuildInfo(user.getId(), user.getServerId());
        List<GuildInfo.GuildWords> wordsList = guild.getWords();
        boolean newWords = true;
        for (GuildInfo.GuildWords uWords : wordsList) {
            if (uWords.getUid().longValue() == user.getId().longValue()) {
                uWords.setContent(content);
                uWords.setSender(user.getRoleInfo().getNickname());
                uWords.setWriteDate(new Date());
                newWords = false;
                break;
            }
        }
        if (newWords) {
            GuildInfo.GuildWords words = new GuildInfo.GuildWords();
            words.setContent(content);
            words.setUid(user.getId());
            words.setWriteDate(new Date());
            words.setSender(user.getRoleInfo().getNickname());
            wordsList.add(words);
        }
        serverDataService.updateServerData(guild);
    }

    /**
     * 退出行会
     *
     * @param uid
     * @param sid
     * @param isOneself 是否是玩家自己主动退出
     */
    public void exit(long uid, int sid, boolean isOneself) {
        GuildInfo guild = getGuildInfo(uid, sid);
        if (uid == guild.getBossId().longValue()) {
            if (guild.getMembers().size() > 1) {
                // 行会人数大于1 不能退出
                throw new ExceptionForClientTip("guild.canot.exit");
            } else {
                // 行会仅剩 会长一人，退出即为解散
                UserGuild userGuild = gameUserService.getSingleItem(uid, UserGuild.class);
                userGuild.setGuildId(0L);
                userGuild.setGuildLv(0);
                userGuild.setOperateTime(DateUtil.fromDateInt(20190101));
                userGuild.setWords(null);
                chatService.removeGuildChatRoom(uid);
                gameUserService.updateItem(userGuild);
                serverDataService.deleteServerData(guild);
                return;
            }
        }
        if (guild.getMembers().remove(uid)) {
            guild.setPeopleProgress(guild.getMembers().size());
            UserGuild userGuild = gameUserService.getSingleItem(uid, UserGuild.class);
            userGuild.setGuildId(0L);
            userGuild.setGuildLv(0);
            userGuild.setWeekContrbution(0);
            if (isOneself) {
                userGuild.setOperateTime(new Date());
            } else {
                String title = LM.I.getMsgByUid(uid, "mail.guild.remove.title");
                String content = LM.I.getMsgByUid(uid, "mail.guild.remove.content", guild.getGuildName());
                mailService.sendSystemMail(title, content, uid);
                userGuild.setOperateTime(DateUtil.fromDateInt(20190101));
            }
            List<GuildInfo.GuildWords> wordsList = guild.getWords();
            wordsList = wordsList.stream().filter(p -> p.getUid().longValue() != uid).collect(Collectors.toList());
            guild.setWords(wordsList);
            userGuild.setWords(null);
            guild.setStatus(GuildConstant.MEMBER_CAN_JOIN);
            chatService.leaveGuildChatRoom(uid);
            gameUserService.updateItem(userGuild);
        }
        serverDataService.updateServerData(guild);
    }

    public GuildInfo getGuildInfo(long uid, int sid) {
        UserGuild userGuild = guildUserService.getUserGuild(uid);
        if (userGuild.getGuildId() == 0) {
            throw new ExceptionForClientTip("guild.user.not.join");
        }
        GuildInfo guild = serverDataService.getServerData(sid, GuildInfo.class, userGuild.getGuildId());
        if (null == guild) {
            throw new ExceptionForClientTip("guild.not.exist");
        }
        if (null == guild.getEightDiagramsBuildDate() || guild.getEightDiagramsBuildDate().intValue() != DateUtil.toDateInt(new Date())) {
            // 重置八卦字牌 以及每日加入人数上限
            List<Integer> eightDiagrams = new ArrayList<>(Arrays.asList(0, 0, 0, 0, 0, 0, 0, 0));
            guild.setEightDiagrams(eightDiagrams);
            guild.setMaxJoinNum(GuildConstant.MAX_JOIN);
            guild.setEightDiagramsBuildDate(DateUtil.toDateInt(new Date()));
            serverDataService.updateServerData(guild);
        }
        if (null == guild.getLimitJoinDate() || guild.getLimitJoinDate() != DateUtil.toDateInt(new Date())) {
            guild.setLimitJoinDate(DateUtil.toDateInt(new Date()));
            guild.setMaxJoinNum(GuildConstant.MAX_JOIN);
            serverDataService.updateServerData(guild);
        }
        return guild;
    }

    public GuildInfo getGuildInfoBydataId(int sid, long dataid) {
        GuildInfo info = serverDataService.getServerData(sid, GuildInfo.class, dataid);
        if (null == info) {
            throw new ExceptionForClientTip("guild.not.exist");
        }
        if (null == info.getEightDiagramsBuildDate() || info.getEightDiagramsBuildDate().intValue() != DateUtil.toDateInt(new Date())) {
            // 重置八卦字牌
            List<Integer> eightDiagrams = new ArrayList<>(Arrays.asList(0, 0, 0, 0, 0, 0, 0, 0));
            info.setEightDiagrams(eightDiagrams);
            info.setEightDiagramsBuildDate(DateUtil.toDateInt(new Date()));
            serverDataService.updateServerData(info);
        }
        if (null == info.getLimitJoinDate() || info.getLimitJoinDate() != DateUtil.toDateInt(new Date())) {
            info.setLimitJoinDate(DateUtil.toDateInt(new Date()));
            info.setMaxJoinNum(GuildConstant.MAX_JOIN);
            serverDataService.updateServerData(info);
        }
        return info;
    }

    /**
     * 获取任务完成标识
     *
     * @param uid
     * @return
     */
    public Integer getTaskRemind(long uid) {
        UserGuildTaskInfo taskInfo = gameUserService.getSingleItem(uid, UserGuildTaskInfo.class);
        if (null == taskInfo) {
            return 0;
        }
        long count = taskInfo.getTasks().stream().filter(tmp -> tmp.getStatus() == GuildConstant.STATUS_FINISHED).count();
        return (int) count;
    }

    /**
     * 检查是否有重复的名称
     *
     * @param sid
     * @param name
     * @return
     */
    private boolean checkHaveName(int sid, String name) {
        List<GuildInfo> guildInfos = serverDataService.getServerDatas(sid, GuildInfo.class);
        for (GuildInfo info : guildInfos) {
            if (info.getGuildName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 授权副队长
     *
     * @param opUid
     * @param sid
     */
    public void setViceBossId(long opUid, long muid, int sid) {
        GuildInfo guild = getGuildInfo(opUid, sid);
        if (guild.getViceBossId() != null && guild.getViceBossId() > 0 && guild.getMembers().contains(guild.getViceBossId())) {
            //存在副队
            throw new ExceptionForClientTip("guild.vicebossid.full");
        }
        Optional<Long> memberOp = guild.getMembers().stream().filter(p -> p.longValue() == muid).findFirst();
        if (!memberOp.isPresent()) {
            //新副队长 非本会会员
            throw new ExceptionForClientTip("guild.user.not.exist");
        }
        guild.setViceBossId(muid);
        serverDataService.updateServerData(guild);
    }

    /**
     * 降级为成员
     *
     * @param opUid
     * @param muid
     * @param sid
     */
    public void setNormalMember(long opUid, long muid, int sid) {
        GuildInfo guild = getGuildInfo(opUid, sid);
        if (guild.getBossId() != opUid) {
            throw new ExceptionForClientTip("guild.not.leader");
        }
        Optional<Long> memberOp = guild.getMembers().stream().filter(p -> p.longValue() == muid).findFirst();
        if (!memberOp.isPresent()) {
            //新副队长 非本会会员
            throw new ExceptionForClientTip("guild.user.not.exist");
        }
        if (guild.getViceBossId() != null && guild.getViceBossId() == muid) {
            guild.setViceBossId(null);
        }
        serverDataService.updateServerData(guild);
    }

    /**
     * 增加行会经验
     *
     * @param uid
     */
    public void addGuildExp(long uid, int addExp) {
        GuildInfo info = getGuildInfo(uid, gameUserService.getActiveSid(uid));
        if (info == null) {
            return;
        }
        if (info.getLv() >= GuildTools.getMaxLevel()) {
            // 经验已满
            return;
        }
        boolean isUpLevel = info.addExpUpLevel(addExp);
        serverDataService.updateServerData(info);
        if (isUpLevel) {
            // 更新所有成员记载的商会等级
            for (Long mb : info.getMembers()) {
                Optional<UserGuild> optional = guildUserService.getUserGuildOp(mb);
                if (optional.isPresent()) {
                    UserGuild userGuild = optional.get();
                    userGuild.setGuildLv(info.getLv());
                    gameUserService.updateItem(userGuild);
                }
            }
        }
    }
}
