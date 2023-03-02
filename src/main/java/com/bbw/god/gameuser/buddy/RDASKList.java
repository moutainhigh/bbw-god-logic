package com.bbw.god.gameuser.buddy;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * 好友请求列表返回对象
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2018-12-27 15:45
 */
@Getter
@Setter
class RDASKList extends RDBuddyList {
	private List<RDBuddyUser> appliers;//玩家请求列表
}
