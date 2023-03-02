package com.bbw.god.db.service;

import com.baomidou.mybatisplus.service.IService;
import com.bbw.god.db.entity.StatisticInCardSkillEntity;

import java.util.List;

/**
 * 卡牌技能统计
 *
 * @author: suhq
 * @date: 2021/9/10 8:32 下午
 */
public interface StatisticInCardSkillService extends IService<StatisticInCardSkillEntity> {
    /**
     * 根据日期删除
     *
     * @param dateInt
     * @return
     */
    boolean delete(int dateInt);

    /**
     * 查找当前推荐的技能
     *
     * @return
     */
    List<StatisticInCardSkillEntity> getCardsSkills();
}
