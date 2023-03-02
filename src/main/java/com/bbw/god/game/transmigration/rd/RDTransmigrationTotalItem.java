package com.bbw.god.game.transmigration.rd;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 高光全服列表项
 *
 * @author: suhq
 * @date: 2021/9/15 10:34 上午
 */
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RDTransmigrationTotalItem extends RDTransmigrationItem {
    private static final long serialVersionUID = 1L;
    /** 区服 */
    private String server;
    /** 昵称 */
    private String nickname;
    /** 城池 */
    private Integer cityId;
}
