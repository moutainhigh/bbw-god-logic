package com.bbw.god.gameuser.businessgang.rd;

import com.bbw.god.gameuser.task.RDTaskList;
import lombok.Data;

import java.util.List;

/**
 * 商帮任务信息
 *
 * @author fzj
 * @date 2022/1/14 15:32
 */
@Data
public class RDBusinessGangTaskInfo extends RDTaskList {
    /** 可领取任务奖励次数 */
    private Integer awardableNum;
    /** 可免费刷新次数 */
    private Integer freeRefreshTaskNum;
}
