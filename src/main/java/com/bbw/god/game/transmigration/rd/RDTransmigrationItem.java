package com.bbw.god.game.transmigration.rd;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 高光列表项
 *
 * @author: suhq
 * @date: 2021/9/15 10:34 上午
 */
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RDTransmigrationItem implements Serializable {
    private static final long serialVersionUID = 1L;
    /** 评分 */
    private Integer score;
    /** 录像地址 */
    private String videoUrl;
}
