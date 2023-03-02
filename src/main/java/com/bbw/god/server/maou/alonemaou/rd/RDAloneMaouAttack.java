package com.bbw.god.server.maou.alonemaou.rd;

import com.bbw.god.rd.RDCommon;
import com.bbw.god.server.maou.ServerMaouStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * 进入攻打界面返回的魔王信息
 *
 * @author suhq
 * @date 2019年1月8日 下午7:07:06
 */
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RDAloneMaouAttack extends RDCommon implements Serializable {
    private static final long serialVersionUID = 1L;
    private RDAloneMaouAttackingInfo attackingInfo;
    private Integer beatBlood = null;
    private Integer beatShield = null;
    private RDMaouSkillAction maouSkillAction = null;
    private Integer maouStatus = null;//魔王状态

    public static RDAloneMaouAttack getInstanceAsMaouOver() {
        RDAloneMaouAttack rd = new RDAloneMaouAttack();
        rd.setMaouStatus(ServerMaouStatus.OVER.getValue());
        return rd;
    }

    @Data
    public static class RDMaouSkillAction {
        private Integer maouSkill;
        private List<Integer> targetCards;

        public RDMaouSkillAction(int maouSkill, List<Integer> targetCards) {
            this.maouSkill = maouSkill;
            this.targetCards = targetCards;
        }
    }
}
