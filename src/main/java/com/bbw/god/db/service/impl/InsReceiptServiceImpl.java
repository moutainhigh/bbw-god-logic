package com.bbw.god.db.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.bbw.god.db.dao.InsReceiptDao;
import com.bbw.god.db.entity.InsReceiptEntity;
import com.bbw.god.db.service.InsReceiptService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("insReceiptService")
public class InsReceiptServiceImpl extends ServiceImpl<InsReceiptDao, InsReceiptEntity> implements InsReceiptService {
}
