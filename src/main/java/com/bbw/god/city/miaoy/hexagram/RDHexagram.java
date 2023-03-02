package com.bbw.god.city.miaoy.hexagram;

import com.bbw.god.rd.RDAdvance;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author LWB
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RDHexagram extends RDAdvance {

    /**
     * 抽到的卦象ID
     */
    private Integer hexagramId;
    /**
     * 穿越地
     */
    private Integer pos = null;
    /**
     * 穿越地方向
     */
    private Integer direction = null;

    private RDAdvance shakeDice =null;

    /**
     * 当前卦BUFF
     */
    private BuffInfo currentHexagram = null;

    @Data
    @AllArgsConstructor
    public static class BuffInfo{
        private int hexagramId;
        private int times;
    }
}
