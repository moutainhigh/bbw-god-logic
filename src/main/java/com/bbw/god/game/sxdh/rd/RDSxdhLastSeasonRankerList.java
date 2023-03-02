package com.bbw.god.game.sxdh.rd;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 神仙大会上赛季排行
 *
 * @author suhq
 * @date 2020-05-16 12:41
 **/
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RDSxdhLastSeasonRankerList extends RDSxdhRankerList implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer myZone;
    private Integer myRank;
    private Integer myScore;

    private Integer totalSize;
}
