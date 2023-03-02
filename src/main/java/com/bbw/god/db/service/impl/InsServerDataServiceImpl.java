package com.bbw.god.db.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;

import com.bbw.god.db.dao.InsServerDataDao;
import com.bbw.god.db.entity.InsServerDataEntity;
import com.bbw.god.db.service.InsServerDataService;


@Service("insServerDataService")
public class InsServerDataServiceImpl extends ServiceImpl<InsServerDataDao, InsServerDataEntity> implements InsServerDataService {

}
