package com.bbw.god.gameuser.card.equipment.rd;

import com.bbw.common.ListUtil;
import com.bbw.god.gameuser.card.equipment.data.UserCardXianJue;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * 卡牌仙诀
 *
 * @author: huanghb
 * @date: 2022/9/15 10:12
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RdCardXianJues {
    private static final long serialVersionUID = -1L;
    /** 装备至宝 */
    private List<RdCardXianJueInfo> cardXianJues = new ArrayList<>();

    public static RdCardXianJues instance(List<UserCardXianJue> userCardXianJues) {
        RdCardXianJues rd = new RdCardXianJues();
        if (ListUtil.isEmpty(userCardXianJues)) {
            return rd;
        }
        List<RdCardXianJueInfo> rdCardXianJueInfos = new ArrayList<>();
        for (UserCardXianJue userCardXianJue : userCardXianJues) {
            RdCardXianJueInfo rdCardXianJueInfo = RdCardXianJueInfo.instance(userCardXianJue);
            rdCardXianJueInfos.add(rdCardXianJueInfo);
        }
        rd.setCardXianJues(rdCardXianJueInfos);
        return rd;

    }
}
