package com.bbw.god.game.dfdj.rd;

import com.bbw.god.rd.RDCommon;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.io.Serializable;

/**
 * @author suchaobin
 * @description 巅峰对决战斗前校验
 * @date 2021/1/5 17:56
 **/
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
public class RDDfdjFightCheck extends RDCommon implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer zone = null;
    /** 大段 */
    private Integer stage = null;
}
