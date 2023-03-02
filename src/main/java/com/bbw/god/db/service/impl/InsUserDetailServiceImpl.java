package com.bbw.god.db.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;

import com.bbw.god.db.dao.InsUserDetailDao;
import com.bbw.god.db.entity.InsUserDetailEntity;
import com.bbw.god.db.service.InsUserDetailService;


@Service("insUserDetailService")
public class InsUserDetailServiceImpl extends ServiceImpl<InsUserDetailDao, InsUserDetailEntity> implements InsUserDetailService {

}
