package com.bbw.god.gameuser.kunls.rd;

import com.bbw.god.game.config.TypeEnum;
import com.bbw.god.gameuser.card.equipment.Enum.QualityEnum;
import com.bbw.god.gameuser.card.equipment.cfg.CardEquipmentAddition;
import com.bbw.god.gameuser.kunls.data.UserInfusionInfo;
import com.bbw.god.rd.RDCommon;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * 注灵室
 *
 * @author: huanghb
 * @date: 2022/9/15 10:12
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RdInfusionInfo extends RDCommon {
    private static final long serialVersionUID = -1L;
    /** 至宝胚类型 */
    private Integer embryoType;
    /** 五行属性 */
    private Integer property = TypeEnum.Null.getValue();
    /** 品质 */
    private Integer quality = QualityEnum.NONE.getValue();
    /** 注灵次数 */
    private Integer infusionTimes = 0;
    /** 加成 */
    private List<CardEquipmentAddition> cardEquipmentAdditions = new ArrayList<>();
    /** 技能组 */
    private Integer[] skillGroup;

    public static RdInfusionInfo getInstance(UserInfusionInfo userInfusionInfo) {
        RdInfusionInfo rd = new RdInfusionInfo();
        rd.setEmbryoType(userInfusionInfo.getEmbryoType());
        rd.setProperty(userInfusionInfo.getProperty());
        rd.setQuality(userInfusionInfo.getQuality());
        rd.setInfusionTimes(userInfusionInfo.getInfusionTimes());
        rd.setCardEquipmentAdditions(userInfusionInfo.getAdditions());
        rd.setSkillGroup(userInfusionInfo.getSkillGroup());
        return rd;
    }
}
