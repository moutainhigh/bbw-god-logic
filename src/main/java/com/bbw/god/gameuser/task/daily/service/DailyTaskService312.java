package com.bbw.god.gameuser.task.daily.service;

import com.bbw.god.gameuser.task.UserTaskInfo;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * @author suchaobin
 * @description 任务id%1000=312的任务对应的service
 * @date 2020/11/25 15:47
 **/
@Service
public class DailyTaskService312 extends BaseDailyTaskService {
    /**
     * 获取当前service对应的任务id集合
     *
     * @return 当前service对应的任务id集合
     */
    @Override
    public List<Integer> getMyTaskIds() {
        return Arrays.asList(21312, 22312, 23312, 24312, 25312);
    }

    /**
     * 获取当前任务进度(用于判断任务是否完成)
     *
     * @param uid    玩家id
     * @param level  玩家等级
     * @param taskId 任务id
     * @param info   任务对象信息
     * @return 当前任务进度
     */
    @Override
    public int doGetProgress(long uid, int level, int taskId, UserTaskInfo info) {
        return 0;
    }
}
