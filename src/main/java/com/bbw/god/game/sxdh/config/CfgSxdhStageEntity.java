package com.bbw.god.game.sxdh.config;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 神仙大会阶段
 *
 * @author suhq
 * @date 2019-06-18 10:29:23
 */
@Data
public class CfgSxdhStageEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;
    private String name;
    private List<Integer> segments;//阶段对应段位
    private Integer matchMinSegment;//匹配最小段位
    private Integer matchMaxSegment;//匹配最大段位

}
