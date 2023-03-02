package com.bbw.god.db.service;

import java.util.List;
import java.util.Set;

import com.baomidou.mybatisplus.service.IService;
import com.bbw.god.db.entity.InsUserEntity;

/**
 * 玩家数据
 *
 * @author shaojun
 * @email lsj@bamboowind.cn
 * @date 2019-02-27 09:44:18
 */
public interface InsUserService extends IService<InsUserEntity> {

	/**
	 * 根据昵称关键字模糊查询,随机limit结果集
	 * @param sid
	 * @param limit
	 * @param keyword
	 * @return
	 */
	List<InsUserEntity> getRandResultNicknameLike(int sid, int limit, String keyword, Set<Long> exclude);

	/**
	 * 从服务器上获取limit个对象，排除exclude，随机limit结果集
	 * @param sid
	 * @param limit
	 * @param exclude
	 * @return
	 */
	List<InsUserEntity> getRandResultfromServer(int sid, int limit, Set<Long> exclude);
}
