package com.bbw.god.gameuser.buddy;

import com.bbw.common.ListUtil;
import com.bbw.common.SpringContextUtil;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.db.entity.InsUserEntity;
import com.bbw.god.db.pool.PlayerDataDAO;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.buddy.RDBuddyList.RDBuddyUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-01-03 13:54
 */
@Service
public class BuddyLogic {
    @Value("${buddy.searchToAdd.limitCount:10}")
    private int limitCount;
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private BuddyService buddyService;

    /**
     * 获取好友对象
     *
     * @param uid
     * @return
     */
    Optional<FriendBuddy> getFriendBuddy(Long uid) {
        FriendBuddy buddy = gameUserService.getSingleItem(uid, FriendBuddy.class);
        if (null != buddy) {
            //修复迁移的数据
            Set<Long> uids = buddy.getFriendUids().stream().filter(l -> null != l).collect(Collectors.toSet());
            buddy.setFriendUids(uids);
            gameUserService.updateItem(buddy);
        }
        return Optional.ofNullable(buddy);
    }

    /**
     * 获取请求列表
     *
     * @param uid
     * @return
     */
    Optional<AskBuddy> getAskBuddy(Long uid) {
        AskBuddy buddy = gameUserService.getSingleItem(uid, AskBuddy.class);
        return Optional.ofNullable(buddy);
    }

    /**
     * fromUid向toUid发送好友请求
     *
     * @param fromUid
     * @param toUid
     */
    void sendAsk(Long fromUid, Long toUid) {
        Optional<AskBuddy> hisAskBuddy = getAskBuddy(toUid);
        if (hisAskBuddy.isPresent()) {
            hisAskBuddy.get().getAskUids().add(fromUid);
            gameUserService.updateItem(hisAskBuddy.get());
        } else {
            AskBuddy buddy = new AskBuddy();
            buddy.setGameUserId(toUid);
            buddy.getAskUids().add(fromUid);
            gameUserService.addItem(toUid, buddy);
        }
    }

    /**
     * 接受申请
     *
     * @param myUid
     * @param hisUid
     */
    void accept(Long myUid, Long hisUid) {
        // 我的好友里添加对方
        Optional<FriendBuddy> myFriend = getFriendBuddy(myUid);
        if (myFriend.isPresent()) {
            myFriend.get().getFriendUids().add(hisUid);
            gameUserService.updateItem(myFriend.get());
        } else {
            FriendBuddy myBuddy = new FriendBuddy();
            myBuddy.setGameUserId(myUid);
            myBuddy.getFriendUids().add(hisUid);
            gameUserService.addItem(myUid, myBuddy);
        }
        // 对方的好友里添加我
        Optional<FriendBuddy> hisFriend = getFriendBuddy(hisUid);
        if (hisFriend.isPresent()) {
            hisFriend.get().getFriendUids().add(myUid);
            gameUserService.updateItem(hisFriend.get());
        } else {
            FriendBuddy hisBuddy = new FriendBuddy();
            hisBuddy.setGameUserId(hisUid);
            hisBuddy.getFriendUids().add(myUid);
            gameUserService.addItem(hisUid, hisBuddy);
        }
        // 从我的好友请求列表中删除对方
        Optional<AskBuddy> myAsk = getAskBuddy(myUid);
        if (myAsk.isPresent() && myAsk.get().getAskUids().contains(hisUid)) {
            myAsk.get().getAskUids().remove(hisUid);
            gameUserService.updateItem(myAsk.get());
        }
    }

    // 从我的好友请求列表中删除对方
    void reject(Long myUid, Long hisUid) {
        int level = gameUserService.getGameUser(myUid).getLevel();
        if (level < 10 && getFriendCount(myUid) == 0 && buddyService.getAskCount(myUid) == 1) {
            //小于10级时 当玩家没有好友 且 好友请求数为1 时  不能拒绝
            throw new ExceptionForClientTip("buddy.cant.refuse");
        }
        Optional<AskBuddy> myAsk = getAskBuddy(myUid);
        if (myAsk.isPresent() && myAsk.get().getAskUids().contains(hisUid)) {
            myAsk.get().getAskUids().remove(hisUid);
            gameUserService.updateItem(myAsk.get());
        }
    }

    void delete(Long myUid, Long hisUid) {
        // 我的好友里删除对方
        Optional<FriendBuddy> myFriend = getFriendBuddy(myUid);
        if (myFriend.isPresent() && myFriend.get().getFriendUids().contains(hisUid)) {
            myFriend.get().getFriendUids().remove(hisUid);
            gameUserService.updateItem(myFriend.get());
        }
        // 对方的好友里删除我
        Optional<FriendBuddy> hisFriend = getFriendBuddy(hisUid);
        if (hisFriend.isPresent() && hisFriend.get().getFriendUids().contains(myUid)) {
            hisFriend.get().getFriendUids().remove(myUid);
            gameUserService.updateItem(hisFriend.get());
        }
    }

    /**
     * 获取好友数量
     *
     * @param uid
     * @return
     */
    long getFriendCount(Long uid) {
        Optional<FriendBuddy> friend = this.getFriendBuddy(uid);
        if (!friend.isPresent()) {
            return 0;
        }
        return friend.get().getFriendUids().size();
    }

    /**
     * 获取好友请求列表
     *
     * @param uid
     * @return
     */
    @NonNull
    List<RDBuddyUser> getAskList(Long uid, int sid) {
        Optional<AskBuddy> askBuddy = this.getAskBuddy(uid);
        if (!askBuddy.isPresent() || askBuddy.get().getAskUids().isEmpty()) {
            return new ArrayList<>(0);
        }
        PlayerDataDAO pdd = SpringContextUtil.getBean(PlayerDataDAO.class, sid);
        List<InsUserEntity> users = pdd.dbGetByIds(askBuddy.get().getAskUids());
        List<RDBuddyUser> buddyUsers = RDBuddyUser.fromInsUserEntity(users);
        return buddyUsers;
    }

    /**
     * 获取好友列表
     *
     * @param uid
     * @return
     */
    List<RDBuddyUser> getFriendList(Long uid, int sid) {
        Optional<FriendBuddy> friendBuddy = this.getFriendBuddy(uid);
        if (!friendBuddy.isPresent() || friendBuddy.get().getFriendUids().isEmpty()) {
            return new ArrayList<>(0);
        }
        PlayerDataDAO pdd = SpringContextUtil.getBean(PlayerDataDAO.class, sid);
        List<InsUserEntity> users = pdd.dbGetByIds(friendBuddy.get().getFriendUids());
        if (ListUtil.isNotEmpty(users) && users.size() < friendBuddy.get().getFriendUids().size()) {
            Set<Long> existUid = users.stream().map(InsUserEntity::getUid).collect(Collectors.toSet());
            friendBuddy.get().setFriendUids(existUid);
            gameUserService.updateItem(friendBuddy.get());
        }
        List<RDBuddyUser> buddyUsers = RDBuddyUser.fromInsUserEntity(users);
        if (buddyUsers != null && !buddyUsers.isEmpty()) {
            buddyUsers = buddyUsers.stream()
                    .sorted(Comparator.comparing(RDBuddyUser::getLevel).reversed())
                    .sorted(Comparator.comparing(RDBuddyUser::getStatus).reversed())
                    .collect(Collectors.toList());
        }
        return buddyUsers;
    }

    @NonNull
    Set<Long> getFriendUids(long uid) {
        Optional<FriendBuddy> friendBuddy = this.getFriendBuddy(uid);
        if (!friendBuddy.isPresent()) {
            return new HashSet<>();
        }
        Set<Long> uids = friendBuddy.get().getFriendUids().stream().filter(l -> null != l).collect(Collectors.toSet());

        return uids;
    }

    /**
     * 根据关键字查询，返回uid列表
     *
     * @param serverId
     * @param uid
     * @param keyword
     * @return
     */
    @NonNull
    List<RDBuddyUser> getUidsByKeyword(int serverId, Long uid, String keyword) {
        Set<Long> friends = getFriendUids(uid);
        friends.add(uid);
        PlayerDataDAO pdd = SpringContextUtil.getBean(PlayerDataDAO.class, serverId);
        List<InsUserEntity> users = pdd.dbGetRandResultNicknameLike(serverId, limitCount, keyword, friends);
        List<RDBuddyUser> buddyUsers = RDBuddyUser.fromInsUserEntity(users);
        return buddyUsers;
    }

    /**
     * 没有关键字
     */
    @NonNull
    List<RDBuddyUser> getUidsNoKeyword(int serverId, Long uid) {
        // TODO:如果没有输入查询关键字，则随机显示limitCount个玩家。优先显示已经载入内存中的
        Set<Long> friends = getFriendUids(uid);
        friends.add(uid);
        PlayerDataDAO pdd = SpringContextUtil.getBean(PlayerDataDAO.class, serverId);
        List<InsUserEntity> users = pdd.dbGetRandResultfromServer(serverId, limitCount, friends);
        List<RDBuddyUser> buddyUsers = RDBuddyUser.fromInsUserEntity(users);
        return buddyUsers;
    }

    /**
     * 是否是我的好友
     *
     * @param myuid
     * @param Fuid
     * @return
     */
    public boolean myFriendUid(long myuid, Long Fuid) {
        Set<Long> friends = getFriendUids(myuid);
        if (Fuid != null && friends.contains(Fuid)) {
            return true;
        }
        return false;
    }
}
