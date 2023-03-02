package com.bbw.god.game.dfdj.rd;

import com.bbw.god.rd.RDSuccess;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author suchaobin
 * @description 巅峰对决金豆信息
 * @date 2021/1/5 14:00
 **/
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RDDfdjBeanInfo extends RDSuccess implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer bean = null;//总仙豆
    private Integer remainTimes = null;//剩余购买次数
    private Integer limitTimes = null;//限制次数
    private String expireInfo = null;//过期描述，如果没有过期数量则不传

}
