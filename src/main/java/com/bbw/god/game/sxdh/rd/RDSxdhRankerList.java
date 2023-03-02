package com.bbw.god.game.sxdh.rd;

import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.rd.RDSuccess;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * 神仙大会排行
 *
 * @author suhq
 * @date 2019-06-21 10:05:16
 */
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RDSxdhRankerList extends RDSuccess implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<RDSxdhRanker> rankers;

    @Data
    public static class RDSxdhRanker {
        private Long id;
        private String server;
        private String nickname;
        private Integer head;
        private Integer iconId = TreasureEnum.HEAD_ICON_Normal.getValue();// 头像框
        //		private Integer title;
        private Integer score;
        private Integer rank;
    }

}
