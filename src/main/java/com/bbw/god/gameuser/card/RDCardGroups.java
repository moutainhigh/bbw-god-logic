package com.bbw.god.gameuser.card;

import com.bbw.common.ListUtil;
import com.bbw.god.rd.RDSuccess;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lwb
 * @date 2020/4/7 17:28
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RDCardGroups extends RDSuccess implements Serializable {
    private static final long serialVersionUID = -4492507071675923604L;
    private List<RDCardGroup> cardGroups = new ArrayList<>();
    private Integer cardGroupStatus = null;//梦魇卡组状态 -1不可编组，0为可以

    public void addCardIds(CardGroupWay way, CardGroup cardGroup) {
        cardGroups.add(new RDCardGroup(cardGroup.getCardIds(), cardGroup.getFuCeId(), way.getValue()));
    }

    public void addCardIds(CardGroupWay way, List<Integer> cardIds, Integer fuCeId) {
        cardGroups.add(new RDCardGroup(cardIds, fuCeId, way.getValue()));
    }

    public boolean isEmpty(CardGroupWay cardGroupWay) {
        RDCardGroup rdCardGroup = cardGroups.stream().filter(tmp -> tmp.getCardGroupType() == cardGroupWay.getValue()).findFirst().orElse(null);
        if (null == rdCardGroup) {
            return true;
        }
        return ListUtil.isEmpty(rdCardGroup.getCardIds());
    }
}
