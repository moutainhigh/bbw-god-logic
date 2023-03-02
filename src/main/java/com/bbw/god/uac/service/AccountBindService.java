package com.bbw.god.uac.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.baomidou.mybatisplus.service.IService;
import com.bbw.db.PageUtils;
import com.bbw.god.uac.entity.AccountBindEntity;

/**
 * 账号绑定
 *
 * @author shaojun
 * @email lsj@bamboowind.cn
 * @date 2018-05-17 22:09:32
 */
public interface AccountBindService extends IService<AccountBindEntity> {
	PageUtils queryPage(Map<String, Object> params);

	/**
	 * 账号是否与其他账号绑定
	 * @param playerAccount：玩家账号
	 * @param bindType: 详见Const.UcBindAccount
	 * @return
	 */
	public boolean isPlayerAccountBinded(String playerAccount, int bindType);

	/**
	 * 根据绑定类型和绑定值获取绑定对象
	 * @param bindType
	 * @param bindKey
	 * @return 
	 */
	public List<AccountBindEntity> getAccountBindEntity(int bindType, String bindKey);

	/**
	 * 微信绑定的游客账号
	 * @param bindKey
	 * @return
	 */
	public Optional<AccountBindEntity> getWechatBindGuessAccountEntity(String bindKey);

	/**
	 * 获取微信绑定对象
	 * @param bindKey
	 * @return
	 */
	public List<AccountBindEntity> getWechatBindAccountEntity(String bindKey);

	/**
	 * 获取手机号绑定对象
	 * @param bindKey
	 * @return
	 */
	public List<AccountBindEntity> getMobilephoneBindAccountEntity(String bindKey);

	/**
	 * 获取支付宝绑定对象
	 * @param bindKey
	 * @return
	 */
	public List<AccountBindEntity> getAlipaytBindAccountEntity(String bindKey);

}
