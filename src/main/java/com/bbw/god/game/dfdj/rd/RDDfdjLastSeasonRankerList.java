package com.bbw.god.game.dfdj.rd;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 巅峰对决上赛季排行
 *
 * @author suhq
 * @date 2020-05-16 12:41
 **/
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RDDfdjLastSeasonRankerList extends RDDfdjRankerList implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer myZone;
    private Integer myRank;
    private Integer myScore;

    private Integer totalSize;
}
