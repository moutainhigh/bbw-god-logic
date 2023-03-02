package com.bbw.god.game.transmigration.rd;

import com.bbw.god.rd.RDSuccess;
import com.bbw.god.rd.item.RDAchievableItem;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * 轮回世界主页信息
 *
 * @author: suhq
 * @date: 2021/9/15 10:34 上午
 */
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RDTransmigrationInfo extends RDSuccess implements Serializable {
    private static final long serialVersionUID = 1L;
    /** 开始时间 休赛期返回 */
    private Long beginDate;
    /** 结束时间 */
    private Long endDate;
    /** 总评分 */
    private Integer myTotalScore;
    /** 当前排名 */
    private Integer curRank;
    /** 已战胜守将数 */
    private Integer defenderBeatedNum;
    /** 全服高光记录 */
    private List<RDTransmigrationItem> globalHightlights;
    /** 个人高光记录 */
    private List<RDTransmigrationItem> personalHighlights;
    /** 目标奖励 */
    private List<RDAchievableItem> targetAwards;


}
