package com.bbw.god.uac.service;

import java.util.Map;

import com.baomidou.mybatisplus.service.IService;
import com.bbw.db.PageUtils;
import com.bbw.god.uac.entity.BasePlatEntity;

/**
 * 
 *
 * @author shaojun
 * @email lsj@bamboowind.cn
 * @date 2018-04-24 14:17:10
 */
public interface BasePlatService extends IService<BasePlatEntity> {
	PageUtils queryPage(Map<String, Object> params);

	/**
	 * 根据客户端指定的渠道字符串获取渠道ID(base_plat.id).
	 * @param plat_code: 对应base_plat.plat_code字段
	 * @return
	 */
	public int platCodeToPlatId(String plat_code);

	/**
	 * 获取对象，使用了本机缓存。
	 * @param id
	 * @return
	 */
	public BasePlatEntity fetchOne(int id);

	/**
	 * 获取对象，使用了本机缓存。
	 * @param plat_code
	 * @return
	 */
	public BasePlatEntity fetchOne(String plat_code);
}
