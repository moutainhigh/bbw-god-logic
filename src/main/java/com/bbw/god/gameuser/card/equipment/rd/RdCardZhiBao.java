package com.bbw.god.gameuser.card.equipment.rd;

import com.bbw.god.game.config.TypeEnum;
import com.bbw.god.game.zxz.rd.RdZxzCardZhiBao;
import com.bbw.god.gameuser.card.equipment.cfg.CardEquipmentAddition;
import com.bbw.god.gameuser.card.equipment.data.UserCardZhiBao;
import com.bbw.god.rd.RDCommon;
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
public class RdCardZhiBao extends RDCommon {
    private static final long serialVersionUID = -1L;
    /** 唯一 */
    private Long zhiBaoDataId;
    /** 卡牌id */
    private Integer cardId;
    /** 至宝id */
    private Integer zhiBaoId;
    /** 五行属性 */
    private Integer property = TypeEnum.Null.getValue();
    /** 加成 */
    private List<CardEquipmentAddition> additions = new ArrayList<>();
    /** 技能组 */
    private Integer[] skillGroup;
    /** 是否装上 0表示未装备 1表示装备 */
    private Integer putedOn;

    public static RdCardZhiBao instance(UserCardZhiBao userCardZhiBao) {
        RdCardZhiBao info = new RdCardZhiBao();
        info.setCardId(userCardZhiBao.getCardId());
        info.setZhiBaoId(userCardZhiBao.getZhiBaoId());
        info.setZhiBaoDataId(userCardZhiBao.getId());
        info.setProperty(userCardZhiBao.getProperty());
        info.setAdditions(userCardZhiBao.gainAdditions());
        info.setSkillGroup(userCardZhiBao.getSkillGroup());
        info.setPutedOn(userCardZhiBao.ifPutOn());
        return info;

    }
}
