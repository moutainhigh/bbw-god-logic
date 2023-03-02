package com.bbw.god.db.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.bbw.god.db.dao.InsGameDataDao;
import com.bbw.god.db.entity.InsGameDataEntity;
import com.bbw.god.db.service.InsGameDataService;

@Service("insGameDataService")
public class InsGameDataServiceImpl extends ServiceImpl<InsGameDataDao, InsGameDataEntity> implements InsGameDataService {

	@Override
	public List<InsGameDataEntity> selectByDataType(String dataType) {
		return this.selectList(new EntityWrapper<InsGameDataEntity>().eq("data_type", dataType));
	}

}
