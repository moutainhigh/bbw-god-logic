package com.bbw.god.game.wanxianzhen;

import com.bbw.god.gameuser.card.RDCardStrengthen;
import com.bbw.god.rd.RDSuccess;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 说明：
 *
 * @author lwb
 * date 2021-06-22
 */
@Data
public class RDWanXianCardGroup extends RDSuccess {
    private List<RDCardStrengthen> cardGroup=null;
    private Integer currentWxType=null;
    private Integer myStatus=null;

    public void updateCardGroup(List<WanXianCard> list){
        cardGroup=new ArrayList<>();
        for (WanXianCard wanXianCard : list) {
            cardGroup.add(RDCardStrengthen.getInstance(wanXianCard));
        }
    }
}
