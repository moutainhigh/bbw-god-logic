package com.bbw.god.city.nvwm;

import java.io.Serializable;

import com.bbw.god.city.RDCityInfo;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 到达女娲庙
 *
 * @author suhq
 * @date 2019年3月18日 下午3:52:23
 */
@Getter
@Setter
@ToString(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RDArriveNvWM extends RDCityInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    /** 女娲庙满意度 */
    private Integer satisfaction = 0;
    /** 是否是梦魇女娲庙 */
    private Integer nightmareNvWM = 0;
    /** 泥人总进度 */
    private Integer progressToPinchPeopleTotalValue = 0;
}
