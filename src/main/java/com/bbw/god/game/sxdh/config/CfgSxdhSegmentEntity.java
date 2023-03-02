package com.bbw.god.game.sxdh.config;

import lombok.Data;

import java.io.Serializable;

/**
 * 神仙大会段位
 *
 * @author suhq
 * @date 2019-06-18 10:29:23
 */
@Data
public class CfgSxdhSegmentEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;
    private String name;
    private Integer minScore;// 最小积分
    private Integer maxScore;// 最大积分

}
