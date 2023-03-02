package com.bbw.god.gameuser.leadercard.fashion;

import com.bbw.god.rd.RDSuccess;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 时装列表
 *
 * @author: suhq
 * @date: 2021/8/5 10:35 上午
 */
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RDFashionList extends RDSuccess {
    private Integer usingFashion;
    private List<RDFashion> fashions;

    public void addFashions(List<UserLeaderFashion> leaderFashions) {
        List<RDFashion> rdFashions = leaderFashions.stream().map(tmp -> new RDFashion(tmp.getFashionId(), tmp.getLevel())).collect(Collectors.toList());
        this.fashions = rdFashions;
    }

    @Data
    public static class RDFashion {
        private Integer fashionId;
        private Integer level;

        public RDFashion(int fashionId, int level) {
            this.fashionId = fashionId;
            this.level = level;
        }
    }
}
