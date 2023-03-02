package com.bbw.god.game.dfdj.rd;

import com.bbw.god.rd.RDCommon;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author suchaobin
 * @description 金豆购买
 * @date 2021/1/5 14:09
 **/
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RDDfdjBeanBuy extends RDCommon implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer remainTimes;//剩余购买次数
}
