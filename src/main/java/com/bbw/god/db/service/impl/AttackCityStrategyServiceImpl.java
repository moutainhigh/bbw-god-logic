package com.bbw.god.db.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.bbw.god.db.dao.AttackCityStrategyDao;
import com.bbw.god.db.entity.AttackCityStrategyEntity;
import com.bbw.god.db.service.AttackCityStrategyService;
import org.springframework.stereotype.Service;

/**
 * @author lwb
 */
@Service
public class AttackCityStrategyServiceImpl extends ServiceImpl<AttackCityStrategyDao, AttackCityStrategyEntity> implements AttackCityStrategyService {

    @Override
    public AttackCityStrategyEntity queryNightmareAttackHuweiJun(int cityId, long uid) {
        EntityWrapper<AttackCityStrategyEntity> ew=new EntityWrapper<>();
        ew.eq("city_id",cityId).eq("uid",uid).eq("huweijun",1).eq("nightmare",1);
        ew.orderBy("recorded_time",false);
        return this.selectOne(ew);
    }
}
