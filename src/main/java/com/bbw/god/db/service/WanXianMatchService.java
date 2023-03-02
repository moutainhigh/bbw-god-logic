package com.bbw.god.db.service;

import com.baomidou.mybatisplus.service.IService;
import com.bbw.god.db.entity.WanXianMatchEntity;

import java.util.List;

public interface WanXianMatchService extends IService<WanXianMatchEntity> {

    public boolean insert(List<WanXianMatchEntity> list);
}
