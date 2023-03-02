package com.bbw.god.game.maou.ctrl;

import com.bbw.god.rd.RDSuccess;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;

/**
 * 跨服魔王信息
 *
 * @author: suhq
 * @date: 2021/12/17 10:43 上午
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RDGameMaouBloodInfo extends RDSuccess implements Serializable {
    private static final long serialVersionUID = 7409189386250719040L;
    /** 剩余血量 */
    private Integer remainBlood;
    /** 开始剩余时间 */
    private Long remainTimeToBegin = 0L;
}
