package com.bbw.god.gameuser.buddy;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bbw.common.Rst;
import com.bbw.common.StrUtil;
import com.bbw.db.redis.RedisSetUtil;
import com.bbw.exception.CoderException;
import com.bbw.exception.ErrorLevel;
import com.bbw.god.controller.AbstractController;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.buddy.RDBuddyList.RDBuddyUser;
import com.bbw.god.gameuser.buddy.event.BuddyEventPublisher;
import com.bbw.god.login.LoginPlayer;
import com.bbw.god.rd.RDCommon;
import com.bbw.god.server.ServerUserService;

/**
 * 好友控制器
 * 
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2018-12-19 10:03
 */
@RestController
public class BuddyCtrl extends AbstractController {
	@Autowired
	private ServerUserService serverUserService;
	@Autowired
	private BuddyLogic buddyLogic;
	@Autowired
	protected RedisSetUtil<Long> typeKeySet;

	/**
	 * 显示好友列表
	 * 
	 * @param ifApprove://0表示 好友列表 1表示申请列表
	 * @return
	 */
	// TODO:目前没有进行分页处理
	@RequestMapping("buddy!listBuddies")
	public RDBuddyList listBuddies(@RequestParam(defaultValue = "0") int ifApprove) {
		RDBuddyList list = null;
		LoginPlayer player = this.getUser();
		switch (ifApprove) {
		case 1:// 请求列表
			List<RDBuddyUser> appliers = buddyLogic.getAskList(player.getUid(), player.getServerId());
			list = new RDASKList();
			((RDASKList) list).setAppliers(appliers);
			break;
		default:// 好友列表
			List<RDBuddyUser> buddies = buddyLogic.getFriendList(player.getUid(), player.getServerId());
			list = new RDFriendsList();
			((RDFriendsList) list).setBuddies(buddies);
			break;
		}
		// 好友数量
		Long friendCount = buddyLogic.getFriendCount(this.getUserId());
		// 好友上限
		int friendCaps = BuddyService.getFriendLimit(gameUserService.getGameUser(this.getUserId()).getLevel());
		list.setFriendCount(friendCount.intValue());
		list.setFriendCaps(friendCaps);
		return list;
	}

	/**
	 * 申请添加好友 userId有值，是从好友界面搜素申请的。 buddyName有值，是从邮件界面发起申请的。
	 *
	 * @param userId
	 * @param buddyName
	 * @return
	 */
	@RequestMapping("buddy!apply")
	public Rst apply(@RequestParam(defaultValue = "0") long userId, @RequestParam(defaultValue = "") String buddyName) {
		// 取个容易理解的变量名
		long hisUid = userId;
		LoginPlayer player = this.getUser();
		if (userId == 0 && StrUtil.isNotNull(buddyName)) {
			Optional<Long> uid = serverUserService.getUidByNickName(player.getServerId(), buddyName);
			if (uid.isPresent()) {
				hisUid = uid.get();
			}
		}
		long myUid = player.getUid();
		int hisSid=gameUserService.getActiveSid(hisUid);
		if (getServerId()!=hisSid) {
			return Rst.failFromLocalMessage("buddy.not.mysid");
		}
		boolean existUser = serverUserService.existsUid(hisUid);
		// 对方不存在
		if (!existUser) {
			throw CoderException.fromLocalMessage(ErrorLevel.NORMAL, "server.user.not.exists", player.getServerId(), hisUid);
		}
		// 不能添加自己为好友
		if (hisUid == myUid) {
			return Rst.failFromLocalMessage("buddy.can.not.myself");
		}
		GameUser me = gameUserService.getGameUser(myUid);
		// 好友数量

		Optional<FriendBuddy> myFriend = buddyLogic.getFriendBuddy(myUid);
		int friendCount = 0;
		if (myFriend.isPresent()) {
			friendCount = myFriend.get().getFriendUids().size();
		}
		// 好友上限
		int friendCaps = BuddyService.getFriendLimit(me.getLevel());
		// 达到了好友上限
		if (friendCount >= friendCaps) {
			return Rst.failFromLocalMessage("buddy.friends.count.limit");
		}
		// 已经是好友,我的好友列表里已经有他，理论上这个条件不会成立
		if (myFriend.isPresent() && myFriend.get().getFriendUids().contains(hisUid)) {
			GameUser he = gameUserService.getGameUser(hisUid);
			return Rst.failFromLocalMessage("buddy.friends.already", he.getRoleInfo().getNickname());
		}
		// 对方已经向我发出申请,我的申请列表里已经有他
		Optional<AskBuddy> myAskBuddy = buddyLogic.getAskBuddy(myUid);
		if (myAskBuddy.isPresent() && myAskBuddy.get().getAskUids().contains(hisUid)) {
			GameUser he = gameUserService.getGameUser(hisUid);
			return Rst.failFromLocalMessage("buddy.ask.he.already", he.getRoleInfo().getNickname());
		}
		// 已经向对方发出申请,即：对方的好友请求列表中有我
		Optional<AskBuddy> hisAskBuddy = buddyLogic.getAskBuddy(hisUid);
		if (hisAskBuddy.isPresent() && hisAskBuddy.get().getAskUids().contains(me.getId())) {
			GameUser he = gameUserService.getGameUser(hisUid);
			return Rst.failFromLocalMessage("buddy.ask.he.already", he.getRoleInfo().getNickname());
		}
		// 添加请求,给对方添加一条请求数据
		buddyLogic.sendAsk(myUid, hisUid);
		return Rst.businessOK();
	}

	/**
	 * 删除好友
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping("buddy!delete")
	public Rst delete(long id) {
		long hisUid = id;
		buddyLogic.delete(this.getUserId(), hisUid);
		return Rst.businessOK();
	}

	/**
	 * 审批
	 * 
	 * @param buddyId
	 * @param flag：flag 1 同意 0 拒绝
	 * @return
	 */
	@RequestMapping("buddy!approve")
	public RDCommon approve(long buddyId, @RequestParam(defaultValue = "1") int flag) {
		RDCommon rd = new RDCommon();
		LoginPlayer player = this.getUser();
		long hisUid = buddyId;
		switch (flag) {
		case 0:
			// 拒绝
			buddyLogic.reject(player.getUid(), hisUid);
			break;
		default:
			// 同意
			buddyLogic.accept(player.getUid(), hisUid);
			BuddyEventPublisher.pubAcceptEvent(player.getUid(), hisUid, rd);
		}
		return rd;
	}

	/**
	 * 查找添加好友
	 * 
	 * @param keyword
	 * @return
	 */
	@RequestMapping("buddy!searchToAdd")
	public RDSearchAddList searchToAdd(@RequestParam(defaultValue = "") String keyword) {
		RDSearchAddList list = new RDSearchAddList();
		List<RDBuddyUser> gus;
		if ("".equals(keyword)) {
			gus = buddyLogic.getUidsNoKeyword(this.getServerId(), this.getUserId());
		} else {
			gus = buddyLogic.getUidsByKeyword(this.getServerId(), this.getUserId(), keyword);
		}
		list.setGus(gus);
		return list;
	}

}
