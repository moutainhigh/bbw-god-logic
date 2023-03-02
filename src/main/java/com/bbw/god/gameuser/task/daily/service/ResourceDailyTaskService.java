package com.bbw.god.gameuser.task.daily.service;

import com.bbw.god.game.award.AwardEnum;
import org.springframework.stereotype.Service;

/**
 * @author suchaobin
 * @description 资源每日任务service
 * @date 2020/12/2 17:59
 **/
@Service
public abstract class ResourceDailyTaskService extends BaseDailyTaskService {
    /**
     * 获取当前service对应的资源枚举
     *
     * @return 当前service对应的资源枚举
     */
    public abstract AwardEnum getMyAwardEnum();
}
