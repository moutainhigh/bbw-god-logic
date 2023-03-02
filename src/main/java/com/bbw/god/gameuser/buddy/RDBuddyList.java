package com.bbw.god.gameuser.buddy;

import com.bbw.common.SpringContextUtil;
import com.bbw.god.db.entity.InsUserEntity;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.rd.RDSuccess;
import com.bbw.god.server.ServerUserService;
import com.bbw.god.server.fst.FstRanking;
import com.bbw.god.server.fst.server.FstServerService;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 好友列表返回对象
 * 
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2018-12-27 15:45
 */
@Getter
@Setter
class RDBuddyList extends RDSuccess {
	protected int friendCount;// 当前好友数量
	protected int friendCaps;// 好友数量上限

	/**
	 * @author lsj@bamboowind.cn
	 * @version 1.0.0
	 * @date 2018-12-21 09:20
	 */
	@Data
	static class RDBuddyUser {
		private long id;//好友ID，或者申请人ID
		private long userId;
		private int head;
		private Integer iconId;// 头像框
		private String name;// 昵称
		private int level;
		private int status;// 1:0。在线状态
		private int pvpRanking;// 封神台排行
		private int winRate;// 封神台胜率

		public static List<RDBuddyUser> fromInsUserEntity(List<InsUserEntity> userList) {
			if (null == userList || userList.isEmpty()) {
				return new ArrayList<RDBuddyUser>();
			}
			ServerUserService ss = SpringContextUtil.getBean(ServerUserService.class);
			FstServerService fst = SpringContextUtil.getBean(FstServerService.class);
			GameUserService gameUserService = SpringContextUtil.getBean(GameUserService.class);
			ArrayList<RDBuddyUser> infoList = new ArrayList<>(userList.size());
			for (InsUserEntity user : userList) {
				RDBuddyUser info = new RDBuddyUser();
				info.setId(user.getUid());
				info.setUserId(user.getUid());
				info.setHead(user.getHead());
				info.setName(user.getNickname());
				info.setLevel(user.getLevel());
				boolean online = ss.isOnline(info.getUserId());
				info.setStatus(online ? 1 : 0);
				info.setIconId(gameUserService.getGameUser(user.getUid()).getRoleInfo().getHeadIcon());
				// 封神台信息
				int winRate = 0;
				int pvpRanking = 0;
				Optional<FstRanking> pvp = fst.getFstRanking(info.getUserId());
				if (pvp.isPresent()) {
					if (pvp.get().getChallengeTotalTimes() > 0) {
						winRate = 100 * pvp.get().getWinTimes() / pvp.get().getChallengeTotalTimes();
					} else {
						winRate = 0;
					}
					pvpRanking = fst.getFstRank(info.getUserId());
				}
				info.setWinRate(winRate);
				info.setPvpRanking(pvpRanking);

				infoList.add(info);
			}
			return infoList;
		}

	}

}
