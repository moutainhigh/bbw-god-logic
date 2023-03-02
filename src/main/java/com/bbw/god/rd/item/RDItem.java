package com.bbw.god.rd.item;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;

/**
 * 项
 *
 * @author suhq
 * @date 2020-11-19 11:26
 **/
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RDItem implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long dataId = null;
    private Integer id = null;
}
