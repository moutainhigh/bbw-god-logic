package com.bbw.god.gameuser.buddy;

import java.util.List;

import com.bbw.god.gameuser.buddy.RDBuddyList.RDBuddyUser;
import com.bbw.god.rd.RDSuccess;

import lombok.Getter;
import lombok.Setter;

/**
 * 查询返回的对象
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2018-12-27 15:45
 */
@Getter
@Setter
public class RDSearchAddList extends RDSuccess {
	private List<RDBuddyUser> gus;//玩家请求列表
}
