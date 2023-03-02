package com.bbw.god.gameuser.task.timelimit;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 限时战斗任务卡池
 *
 * @author fzj
 * @date 2022/1/5 14:56
 */
@Data
public class TimeLimitFightCardPool implements Serializable {
    private static final long serialVersionUID = 1L;
    /** 卡牌id */
    private Integer cardId;
    /** 技能 */
    private List<Integer> skills = new ArrayList<>();
}
