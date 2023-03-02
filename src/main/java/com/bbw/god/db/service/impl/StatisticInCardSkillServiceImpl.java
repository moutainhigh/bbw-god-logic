package com.bbw.god.db.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.bbw.god.db.dao.StatisticInCardSkillDao;
import com.bbw.god.db.entity.StatisticInCardSkillEntity;
import com.bbw.god.db.service.StatisticInCardSkillService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StatisticInCardSkillServiceImpl extends ServiceImpl<StatisticInCardSkillDao, StatisticInCardSkillEntity> implements StatisticInCardSkillService {
    @Override
    public boolean delete(int dateInt) {
        EntityWrapper wrapper = new EntityWrapper<StatisticInCardSkillEntity>();
        wrapper.eq("statistic_date", dateInt);
        return delete(wrapper);
    }

    @Override
    public List<StatisticInCardSkillEntity> getCardsSkills() {
        int maxDate = baseMapper.getMaxDate().intValue();
        EntityWrapper wrapper = new EntityWrapper<StatisticInCardSkillEntity>();
        wrapper.eq("statistic_date", maxDate).and().ge("num", 2);
        return selectList(wrapper);
    }
}
