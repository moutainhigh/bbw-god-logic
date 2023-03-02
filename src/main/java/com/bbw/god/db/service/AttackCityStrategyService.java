package com.bbw.god.db.service;

import com.baomidou.mybatisplus.service.IService;
import com.bbw.god.db.entity.AttackCityStrategyEntity;

/**
 * 攻城攻略
 * 
 * @author lwb
 */
public interface AttackCityStrategyService extends IService<AttackCityStrategyEntity> {
    public AttackCityStrategyEntity queryNightmareAttackHuweiJun(int cityId,long uid);
}
