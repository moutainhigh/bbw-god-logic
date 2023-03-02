package com.bbw.god.db.service;

import java.util.List;

import com.baomidou.mybatisplus.service.IService;
import com.bbw.god.db.entity.InsGameDayDataEntity;

/**
 * 全服每日相关数据
 *
 * @author shaojun
 * @email lsj@bamboowind.cn
 * @date 2019-03-20 09:07:08
 */
public interface InsGameDayDataService extends IService<InsGameDayDataEntity> {
	/**
	 * 根据数据类型载入
	 * @param dataType
	 * @return
	 */
	List<InsGameDayDataEntity> selectByDataType(String dataType);
}
