package com.bbw.god.city;

import com.bbw.god.rd.RDSuccess;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RDCityInfo extends RDSuccess implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer arriveCityId = null;
    private String handleStatus = null;// 玩家到达一个地方后可操作的次数/状态
    private Integer digTimes = null;//挖宝次数
    /** 是否体验过了 */
    private Boolean isExped;

}
