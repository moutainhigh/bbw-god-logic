package com.bbw.god.game.transmigration.rd;

import com.bbw.god.rd.RDSuccess;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * 轮回世界城池挑战信息
 *
 * @author: suhq
 * @date: 2021/9/15 10:34 上午
 */
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RDTransmigrationChallengeInfo extends RDSuccess implements Serializable {
    private static final long serialVersionUID = 1L;
    /** 排名 */
    private Integer isCityNo1;
    /** 评分 */
    private Integer score;
    /** 高分记录 */
    private List<RDTransmigrationItem> tops;
    /** 守将信息 */
    private RDTransmigrationDefenderInfo defenderInfo;
    /** 战斗回合评分,使用法宝评分,剩余血量评分,死亡神将评分,扣除血量评分,击杀神将评分 */
    private List<Integer> scoreCompositions;
    /** 奖励领取状态 */
    private int[] awardStatus;


}
