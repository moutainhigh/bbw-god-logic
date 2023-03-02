package com.bbw.god.db.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.bbw.god.db.dao.InsGameDayDataDao;
import com.bbw.god.db.entity.InsGameDayDataEntity;
import com.bbw.god.db.service.InsGameDayDataService;

@Service("insGameDayDataService")
public class InsGameDayDataServiceImpl extends ServiceImpl<InsGameDayDataDao, InsGameDayDataEntity> implements InsGameDayDataService {

	@Override
	public List<InsGameDayDataEntity> selectByDataType(String dataType) {
		EntityWrapper<InsGameDayDataEntity> wrapper = new EntityWrapper<>();
		wrapper.eq("data_type", dataType);
		return this.selectList(wrapper);
	}

}
