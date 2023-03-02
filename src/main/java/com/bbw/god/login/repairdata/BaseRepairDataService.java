package com.bbw.god.login.repairdata;

import com.bbw.god.gameuser.GameUser;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author suchaobin
 * @description 基础修复数据service
 * @date 2020/7/7 14:42
 **/
@Service
public interface BaseRepairDataService {
	/**
	 * 修复数据
	 *
	 * @param gu            玩家对象
	 * @param lastLoginDate 上次登录时间
	 */
	void repair(GameUser gu, Date lastLoginDate);
}
