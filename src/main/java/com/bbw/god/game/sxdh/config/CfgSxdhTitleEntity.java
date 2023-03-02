package com.bbw.god.game.sxdh.config;

import lombok.Data;

import java.io.Serializable;

/**
 * 神仙大会称号
 *
 * @author suhq
 * @date 2019-06-18 10:29:04
 */
@Deprecated
@Data
public class CfgSxdhTitleEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;
    private String titleName;
    private Integer segment;// 段位
    private Integer minScore;// 达到称号的最小积分
    private Integer maxScore;// 该称号的最大积分
    private Integer limitNum = 10000000;// 人数限制
    private Integer beanNum = 0;// 根据称号可领取的仙豆
    private Integer discount = 100;// 折扣

}
