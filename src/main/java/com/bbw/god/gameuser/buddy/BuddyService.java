package com.bbw.god.gameuser.buddy;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import com.bbw.god.gameuser.GameUserService;

/**
 * 好友服务
 * 
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-01-03 13:54
 */
@Service
public class BuddyService {
	@Autowired
	private GameUserService gameUserService;
	@Autowired
	private BuddyLogic buddyLogic;

	/**
	 * 获取好友请求数量
	 * 
	 * @param uid
	 * @return
	 */
	public int getAskCount(Long uid) {
		AskBuddy askBuddy = gameUserService.getSingleItem(uid, AskBuddy.class);
		if (null == askBuddy) {
			return 0;
		}
		return askBuddy.getAskUids().size();
	}

	/**
	 * fromUid向toUid发送好友请求
	 * @param fromUid
	 * @param toUid
	 */
	public void sendAsk(Long fromUid, Long toUid) {
		buddyLogic.sendAsk(fromUid, toUid);
	}

	/**
	 * 获得所有的好友的ID
	 * 
	 * @param uid
	 * @return
	 */
	@NonNull
	public Set<Long> getFriendUids(long uid) {
		return buddyLogic.getFriendUids(uid);
	}

	/**
	 * 得到好友上限
	 * @return
	 */
	public static int getFriendLimit(int level) {
		if (level <= 10) {
			return 20;
		} else {
			return level * 2;
		}
	}
}
