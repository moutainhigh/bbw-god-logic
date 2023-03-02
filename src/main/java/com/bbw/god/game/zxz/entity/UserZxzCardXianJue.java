package com.bbw.god.game.zxz.entity;

import com.bbw.common.ListUtil;
import com.bbw.common.MapUtil;
import com.bbw.god.gameuser.card.equipment.Enum.QualityEnum;
import com.bbw.god.gameuser.card.equipment.cfg.CardEquipmentAddition;
import com.bbw.god.gameuser.card.equipment.data.UserCardXianJue;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 诛仙阵：卡牌的仙决信息
 * @author: hzf
 * @create: 2022-10-11 09:11
 **/
@Data
public class UserZxzCardXianJue implements Serializable {
    private static final long serialVersionUID = 1L;
    /** 装备ID */
    private Integer xianJueType;
    /** 等级 */
    private Integer level = 0;
    /** 品质 */
    private Integer quality = QualityEnum.FAN_PIN.getValue();

    /** 参悟值 加成类型=》加成值 */
    private Map<String, Integer> additions;

    private Map<String, Integer> gainAdditions(Map<String, Integer> additions){
        if (additions == null || additions.isEmpty()) {
            return null;
        }
        return additions;
    }


    /**
     * 构建 List<UserZxzCardXianJue> 实例
     * @param xianJues
     * @return
     */
    public static List<UserZxzCardXianJue> getInstances(List<UserCardXianJue> xianJues){
        if (ListUtil.isEmpty(xianJues)) {
            return null;
        }
        List<UserZxzCardXianJue> zxzUserCardXianJues = new ArrayList<>();
        for (UserCardXianJue xianJue : xianJues) {
            UserZxzCardXianJue zxzUserCardXianJue = new UserZxzCardXianJue();
            zxzUserCardXianJue.setXianJueType(xianJue.getXianJueType());
            zxzUserCardXianJue.setLevel(xianJue.getLevel());
            zxzUserCardXianJue.setQuality(xianJue.getQuality());
            Map<String, Integer> additions = zxzUserCardXianJue.gainAdditions(xianJue.getAdditions());
            zxzUserCardXianJue.setAdditions(additions);
            zxzUserCardXianJues.add(zxzUserCardXianJue);
        }
        return zxzUserCardXianJues;
    }
    /**
     * 获取仙决加成
     *
     * @return
     */
    public List<CardEquipmentAddition> gainAdditions() {
        List<CardEquipmentAddition> cardAdditions = new ArrayList<>();
        if (MapUtil.isEmpty(additions)) {
            return cardAdditions;
        }
        for (Map.Entry<String, Integer> entry : additions.entrySet()) {
            cardAdditions.add(new CardEquipmentAddition(Integer.valueOf(entry.getKey()), entry.getValue()));
        }
        return cardAdditions;
    }

}