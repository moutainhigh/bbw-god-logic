package com.bbw.god.game.zxz.rd;

import com.bbw.common.ListUtil;
import com.bbw.god.game.config.TypeEnum;
import com.bbw.god.game.config.card.equipment.randomrule.CardZhiBaoRandomRule;
import com.bbw.god.game.zxz.entity.UserZxzCardZhiBao;
import com.bbw.god.gameuser.card.equipment.cfg.CardEquipmentAddition;
import com.bbw.god.gameuser.card.equipment.rd.RdCardZhiBao;
import com.bbw.god.rd.RDSuccess;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 诛仙阵 返回至宝
 * @author: hzf
 * @create: 2022-10-11 08:49
 **/
@Data
public class RdZxzCardZhiBao extends RDSuccess {
    private static final long serialVersionUID = 1366511178569740530L;
    /** 至宝id */
    private Integer zhiBaoId;
    /** 五行属性 */
    private Integer property = TypeEnum.Null.getValue();
    /** 加成 */
    private List<CardEquipmentAddition> additions = new ArrayList<>();
    /** 技能组 */
    private Integer[] skillGroup;

    public static List<RdZxzCardZhiBao> instanceEnemy(List<CardZhiBaoRandomRule> zhiBaos) {
        List<RdZxzCardZhiBao> cardZhiBaos = new ArrayList<>();
        if (ListUtil.isEmpty(zhiBaos)) {
            return cardZhiBaos;
        }
        for (CardZhiBaoRandomRule zhiBao : zhiBaos) {
            RdZxzCardZhiBao rd = new RdZxzCardZhiBao();
            rd.setZhiBaoId(zhiBao.getZhiBaoId());
//            rd.setZhiBaoDataId(zhiBao.getZhiBaoDataId());
            rd.setProperty(zhiBao.getProperty());
            rd.setAdditions(zhiBao.gainAdditions());
            rd.setSkillGroup(zhiBao.getSkillGroup());
            cardZhiBaos.add(rd);
        }
        return cardZhiBaos;
    }

    public static List<RdZxzCardZhiBao> instance(List<UserZxzCardZhiBao> zhiBaos) {
        List<RdZxzCardZhiBao> cardZhiBaos = new ArrayList<>();
        if (ListUtil.isEmpty(zhiBaos)) {
            return cardZhiBaos;
        }
        for (UserZxzCardZhiBao zhiBao : zhiBaos) {
            RdZxzCardZhiBao rd = new RdZxzCardZhiBao();
            rd.setZhiBaoId(zhiBao.getZhiBaoId());
//            rd.setZhiBaoDataId(zhiBao.getZhiBaoDataId());
            rd.setProperty(zhiBao.getProperty());
            rd.setAdditions(zhiBao.gainAdditions());
            rd.setSkillGroup(zhiBao.getSkillGroup());
            cardZhiBaos.add(rd);
        }
        return cardZhiBaos;
    }
    /**
     * 返回至宝信息
     * @return
     */
    public static List<RdCardZhiBao> gainRdCardZhiBaos(List<UserZxzCardZhiBao> zhiBaos){
        List<RdCardZhiBao> rdCardZhiBaos = new ArrayList<>();
        List<RdZxzCardZhiBao> zxzCardZhiBaos = RdZxzCardZhiBao.instance(zhiBaos);
        for (RdZxzCardZhiBao zxzCardZhiBao : zxzCardZhiBaos) {
            RdCardZhiBao rd = new RdCardZhiBao();
//            rd.setCardId(zxzCardZhiBao.getCardId());
            rd.setZhiBaoId(zxzCardZhiBao.getZhiBaoId());
//            rd.setZhiBaoDataId(zxzCardZhiBao.getZhiBaoDataId());
            rd.setProperty(zxzCardZhiBao.getProperty());
            rd.setAdditions(zxzCardZhiBao.getAdditions());
            rd.setSkillGroup(zxzCardZhiBao.getSkillGroup());
            rdCardZhiBaos.add(rd);
        }
        return rdCardZhiBaos;
    }
    public static List<RdCardZhiBao> gainCardZhiBaos(List<RdZxzCardZhiBao> zxzCardZhiBaos){
        List<RdCardZhiBao> rdCardZhiBaos = new ArrayList<>();
        for (RdZxzCardZhiBao zxzCardZhiBao : zxzCardZhiBaos) {
            RdCardZhiBao rd = new RdCardZhiBao();
//            rd.setCardId(zxzCardZhiBao.getCardId());
            rd.setZhiBaoId(zxzCardZhiBao.getZhiBaoId());
//            rd.setZhiBaoDataId(zxzCardZhiBao.getZhiBaoDataId());
            rd.setProperty(zxzCardZhiBao.getProperty());
            rd.setAdditions(zxzCardZhiBao.getAdditions());
            rd.setSkillGroup(zxzCardZhiBao.getSkillGroup());
            rdCardZhiBaos.add(rd);
        }
        return rdCardZhiBaos;
    }
}
