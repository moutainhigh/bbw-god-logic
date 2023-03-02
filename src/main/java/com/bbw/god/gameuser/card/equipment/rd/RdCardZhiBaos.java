package com.bbw.god.gameuser.card.equipment.rd;

import com.bbw.common.ListUtil;
import com.bbw.god.gameuser.card.equipment.data.UserCardZhiBao;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * 卡牌至宝
 *
 * @author: huanghb
 * @date: 2022/9/15 10:12
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RdCardZhiBaos {
    private static final long serialVersionUID = -1L;
    /** 装备至宝 */
    private List<RdCardZhiBao> cardZhiBaos = new ArrayList<>();

    public static RdCardZhiBaos instance(List<UserCardZhiBao> userCardZhiBaos) {
        RdCardZhiBaos rd = new RdCardZhiBaos();
        if (ListUtil.isEmpty(userCardZhiBaos)) {
            return rd;
        }
        List<RdCardZhiBao> rdCardZhiBaos = new ArrayList<>();
        for (UserCardZhiBao userCardZhiBao : userCardZhiBaos) {
            RdCardZhiBao rdCardZhiBao = RdCardZhiBao.instance(userCardZhiBao);
            rdCardZhiBaos.add(rdCardZhiBao);
        }
        rd.setCardZhiBaos(rdCardZhiBaos);
        return rd;

    }
}
