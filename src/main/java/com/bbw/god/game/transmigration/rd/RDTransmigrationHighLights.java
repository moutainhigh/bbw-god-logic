package com.bbw.god.game.transmigration.rd;

import com.bbw.god.rd.RDSuccess;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * 高光列表
 *
 * @author: suhq
 * @date: 2021/9/15 11:49 上午
 */
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RDTransmigrationHighLights extends RDSuccess implements Serializable {
    private static final long serialVersionUID = 1L;
    /** 高光记录 */
    private List<RDTransmigrationItem> highLights;


}
