package com.bbw.god.gameuser.task;

import com.bbw.common.ListUtil;
import com.bbw.god.game.award.RDAward;
import com.bbw.god.rd.RDSuccess;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 派遣信息
 *
 * @author: suhq
 * @date: 2021/8/10 9:42 上午
 */
@Slf4j
@Data
public class RDDispatchInfo extends RDSuccess implements Serializable {
    private static final long serialVersionUID = -2194371713246181712L;
    /** 任务ID */
    private Integer id;
    /** 持久化数据ID */
    protected Long dataId = null;
    /** 所需体力 */
    private Integer needDice;
    /** 卡牌精力 */
    private Integer needCardVigor;
    /** 额外星级 */
    private Integer needStar;
    /** 额外技能 */
    private List<Integer> skills;
    /** 派遣状态 */
    private Integer status;
    /** 对应状态剩余时间 */
    private Long remainTime;
    /** 需要时长 */
    protected Integer costTime = null;

    /** 派遣卡牌 未派遣没传 */
    private List<RDCardVigor> dispatcheds;
    /** 奖励 */
    private List<RDAward> awards;
    /** 额外奖励 */
    private List<RDAward> extAwards;
    /** 成功率,未派遣没传 */
    private Integer successRate;
    /** 额外奖励概率,未派遣没传 */
    private Integer extAwardRate = 0;
    /** 任务是否可以执行 0可以 1不可以 */
    private Integer isExecutable = 0;

    /**
     * 添加派遣的卡牌及剩余体力
     *
     * @param cardId
     * @param remainVigor
     */
    public void addCardVigor(int cardId, int remainVigor, int maxCardVigor) {
        if (ListUtil.isEmpty(dispatcheds)) {
            dispatcheds = new ArrayList<>();
        }
        dispatcheds.add(new RDCardVigor(cardId, remainVigor, maxCardVigor));
    }


}
