package com.bbw.god.db.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.bbw.god.db.dao.AttackYaoZuStrategyDao;
import com.bbw.god.db.entity.AttackYaoZuStrategyEntity;
import com.bbw.god.db.service.AttackYaoZuStrategyService;
import org.springframework.stereotype.Service;

/**
 * @author fzj
 * @date 2021/9/24 17:23
 */
@Service
public class AttackYaoZuStrategyServiceImpl extends ServiceImpl<AttackYaoZuStrategyDao, AttackYaoZuStrategyEntity> implements AttackYaoZuStrategyService {
    @Override
    public AttackYaoZuStrategyEntity queryAttackOntology(int yaoZuId, long uid) {
        EntityWrapper<AttackYaoZuStrategyEntity> ew = new EntityWrapper<>();
        ew.eq("yao_zu_id", yaoZuId).eq("uid", uid).eq("ontology", 0);
        ew.orderBy("recorded_time", false);
        return this.selectOne(ew);
    }
}
