package com.bbw.god.db.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;

import com.bbw.god.db.dao.InsUserDataDao;
import com.bbw.god.db.entity.InsUserDataEntity;
import com.bbw.god.db.service.InsUserDataService;


@Service("insUserDataService")
public class InsUserDataServiceImpl extends ServiceImpl<InsUserDataDao, InsUserDataEntity> implements InsUserDataService {

}
