package com.bbw.god.server.maou.alonemaou.rd;

import com.bbw.god.game.award.Award;
import com.bbw.god.rd.RDSuccess;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * @author suchaobin
 * @description 独占魔王奖励信息
 * @date 2020/7/22 14:39
 **/
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RDAloneMaouAwards extends RDSuccess implements Serializable {
    private static final long serialVersionUID = -1424645416950306251L;
    // 已通过的独占魔王等级
    private Integer passedAloneMaouLevel;
    private List<Award> awards;

    public static RDAloneMaouAwards getInstance(Integer passedAloneMaouLevel, List<Award> awards) {
        RDAloneMaouAwards aloneMaouAwards = new RDAloneMaouAwards();
        aloneMaouAwards.setPassedAloneMaouLevel(passedAloneMaouLevel);
        aloneMaouAwards.setAwards(awards);
        return aloneMaouAwards;
    }
}
