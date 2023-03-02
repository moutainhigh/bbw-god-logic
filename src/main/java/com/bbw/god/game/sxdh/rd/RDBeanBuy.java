package com.bbw.god.game.sxdh.rd;

import com.bbw.god.rd.RDCommon;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 仙豆购买
 *
 * @author suhq
 * @date 2020-04-25 09:10
 **/
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RDBeanBuy extends RDCommon implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer remainTimes;//剩余购买次数
}
