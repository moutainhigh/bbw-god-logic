package com.bbw.god.game.zxz.entity;

import com.bbw.common.ListUtil;
import com.bbw.common.MapUtil;
import com.bbw.god.game.config.TypeEnum;
import com.bbw.god.gameuser.card.equipment.cfg.CardEquipmentAddition;
import com.bbw.god.gameuser.card.equipment.data.UserCardZhiBao;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 诛仙阵卡牌的至宝信息
 * @author: hzf
 * @create: 2022-10-11 09:09
 **/
@Data
public  class UserZxzCardZhiBao implements Serializable {
    private static final long serialVersionUID = 1L;
    /** 至宝ID */
    private Integer zhiBaoId;
    /** 五行属性 */
    private Integer property = TypeEnum.Null.getValue();
    /** 加成  加成类型=》加成值 */
    private Map<String, Integer> additions;
    /** 技能组 */
    private Integer[] skillGroup;

    /**
     *  获取 加成
     * @param additions
     * @return
     */
    private Map<String, Integer> gainAdditions(Map<String, Integer> additions){
        if (null == additions || additions.isEmpty()) {
            return null;
        }
        return additions;
    }


    /**
     * 构建 List<UserZxzCardZhiBao>
     * @param zhiBaos
     * @return
     */
    public static List<UserZxzCardZhiBao> getInstances(List<UserCardZhiBao> zhiBaos){
        if (ListUtil.isEmpty(zhiBaos)) {
            return null;
        }
        List<UserZxzCardZhiBao> zxzUserCardZhiBaos = new ArrayList<>();
        for (UserCardZhiBao zhiBao : zhiBaos) {
            UserZxzCardZhiBao uZhiBao = new UserZxzCardZhiBao();
            uZhiBao.setZhiBaoId(zhiBao.getZhiBaoId());
            uZhiBao.setProperty(zhiBao.getProperty());
            Map<String, Integer> additions = uZhiBao.gainAdditions(zhiBao.getAdditions());
            uZhiBao.setAdditions(additions);
            uZhiBao.setSkillGroup(zhiBao.getSkillGroup());
            zxzUserCardZhiBaos.add(uZhiBao);
        }
        return zxzUserCardZhiBaos;
    }
    /**
     * 获取至宝加成
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
