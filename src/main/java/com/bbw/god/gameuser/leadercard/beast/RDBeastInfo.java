package com.bbw.god.gameuser.leadercard.beast;

import com.bbw.god.rd.RDSuccess;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * 神兽信息
 *
 * @author: suhq
 * @date: 2021/8/4 2:30 下午
 */
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RDBeastInfo extends RDSuccess {
    private List<Integer> unactiveSkills;
}
