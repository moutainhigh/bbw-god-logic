package com.bbw.god.game.dfdj.rd;

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
 * @author suchaobin
 * @description 巅峰对决排行
 * @date 2021/1/5 13:56
 **/
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RDDfdjRankerList extends RDSuccess implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<RDDfdjRanker> rankers;

    @Data
    public static class RDDfdjRanker {
        private Long id;
        private String server;
        private String nickname;
        private Integer head;
        private Integer iconId = TreasureEnum.HEAD_ICON_Normal.getValue();// 头像框
        private Integer score;
        private Integer rank;
    }

}
