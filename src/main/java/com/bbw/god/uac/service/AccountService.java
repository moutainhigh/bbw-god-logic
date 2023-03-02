package com.bbw.god.uac.service;

import java.util.Map;

import com.baomidou.mybatisplus.service.IService;
import com.bbw.db.PageUtils;
import com.bbw.god.uac.entity.AccountEntity;

/**
 * 玩家账号表
 *
 * @author shaojun
 * @email lsj@bamboowind.cn
 * @date 2018-04-22 22:28:16
 */
public interface AccountService extends IService<AccountEntity> {

	PageUtils queryPage(Map<String, Object> params);

	/**
	 * 判断账号是否存在
	 * @param accountKey
	 * @return boolean
	 */
	public boolean existsAccount(String accountKey);

	/**
	 * 根据账号名获取唯一账号
	 * @param accountKey
	 * @return
	 */
	public AccountEntity findByAccount(String accountKey);

	/**
	 * 根据账号名获取唯一账号
	 * 同findByAccount方法相同
	 * 由于表结构设计账号名为email，为了可读性和可理解性设计此方法
	 * @param email
	 * @return
	 */
	public AccountEntity findByEmail(String email);

	/**
	 * 修改账号密码
	 * @param email
	 * @param password
	 * @param newPassword
	 * @return
	 */
	public String modify(String email, String password, String newPassword);

	/**
	 * 获取当天的用户注册数量
	 * @param day:yyyyMMdd
	 * @param channel
	 * @return
	 */
	int getRegNum(int day, int channel);
}
