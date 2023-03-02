package com.bbw.god.db.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.bbw.god.db.dao.WanXianMatchDao;
import com.bbw.god.db.entity.WanXianMatchEntity;
import com.bbw.god.db.service.WanXianMatchService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WanXianMatchServiceImpl extends ServiceImpl<WanXianMatchDao, WanXianMatchEntity> implements WanXianMatchService {

    @Override
    public boolean insert(List<WanXianMatchEntity> list) {
        return this.insertBatch(list,list.size());
    }
}
