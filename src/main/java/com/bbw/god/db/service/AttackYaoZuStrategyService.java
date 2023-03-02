package com.bbw.god.db.service;

import com.baomidou.mybatisplus.service.IService;
import com.bbw.god.db.entity.AttackCityStrategyEntity;
import com.bbw.god.db.entity.AttackYaoZuStrategyEntity;

/**
 * 妖族攻略
 *
 * @author fzj
 * @date 2021/9/24 14:51
 */
public interface AttackYaoZuStrategyService extends IService<AttackYaoZuStrategyEntity> {
    AttackYaoZuStrategyEntity queryAttackOntology(int yaoZuId, long uid);
}