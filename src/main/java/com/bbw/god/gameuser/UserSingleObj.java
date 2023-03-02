package com.bbw.god.gameuser;

/**
 * 玩家对应有多条记录的都为该类的子类
 * 
 * @author suhq
 * @date 2018年11月23日 上午10:22:27
 */
public abstract class UserSingleObj extends UserData {

	@Override
	public void setGameUserId(Long gameUserId) {
		super.setGameUserId(gameUserId);
	}

}
