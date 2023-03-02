package com.bbw.god.db.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.bbw.god.db.entity.StatisticInCardSkillEntity;
import org.apache.ibatis.annotations.Select;

public interface StatisticInCardSkillDao extends BaseMapper<StatisticInCardSkillEntity> {


    @Select("SELECT Max(statistic_date) FROM statistic_in_card_skill")
    Long getMaxDate();
}
