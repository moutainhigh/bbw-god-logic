package com.bbw.god.game.transmigration.rd;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 玩家排行
 *
 * @author: suhq
 * @date: 2021/9/15 10:54 上午
 */
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RDTransmigrationRanker implements Serializable {
    private static final long serialVersionUID = 1L;
    /** 排行 */
    private Integer rank;
    /** 评分 */
    private Integer score;
    /** 区服 */
    private String server;
    /** 昵称 */
    private String nickname;
    /** 已战胜守将数 */
    private Integer defenderBeatedNum;
}
