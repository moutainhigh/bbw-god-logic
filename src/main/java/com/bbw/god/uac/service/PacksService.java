package com.bbw.god.uac.service;

import java.util.Map;

import com.baomidou.mybatisplus.service.IService;
import com.bbw.db.PageUtils;
import com.bbw.god.uac.entity.PacksEntity;

/**
 * 
 *
 * @author shaojun
 * @email lsj@bamboowind.cn
 * @date 2018-04-26 15:19:23
 */
public interface PacksService extends IService<PacksEntity> {
	PageUtils queryPage(Map<String, Object> params);

	/**
	 * 根据礼包类型获取礼包。请注意，礼包类型应为唯一值
	 * @param type
	 * @return
	 */
	public PacksEntity getPackByType(int type);

	/**
	 * 获取微信绑定礼包
	 * @return
	 */
	public PacksEntity getWechatBindedPack();

	/**
	 * 获取微信每周礼包
	 * @return
	 */
	public PacksEntity getWechatWeeklyPack();

	/**
	 * 获取唯一对象，使用了缓存。
	 * @param id
	 * @return
	 */
	public PacksEntity fetchOne(int id);

}
