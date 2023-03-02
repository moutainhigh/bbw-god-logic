package com.bbw.god.gameuser.kunls.data;

import com.bbw.common.ID;
import com.bbw.god.game.config.TypeEnum;
import com.bbw.god.gameuser.UserDataType;
import com.bbw.god.gameuser.UserSingleObj;
import com.bbw.god.gameuser.card.equipment.Enum.QualityEnum;
import com.bbw.god.gameuser.card.equipment.cfg.CardEquipmentAddition;
import com.bbw.god.gameuser.kunls.Enum.InfusionPositionEnum;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 注灵室
 *
 * @author: huanghb
 * @date: 2022/9/14 16:40
 */
@Data
public class UserInfusionInfo extends UserSingleObj implements Serializable {
    private static final long serialVersionUID = -2480062032552290952L;
    /** 五行属性 */
    private Integer property;
    /** 至宝id */
    private Integer embryoType;
    /** 品质 */
    private Integer quality;
    /** 注灵次数 */
    private Integer infusionTimes;
    /** 加成 */
    private List<CardEquipmentAddition> additions;
    /** 技能组 */
    private Integer[] skillGroup;

    public static UserInfusionInfo instance(long uid) {
        UserInfusionInfo info = new UserInfusionInfo();
        info.setId(ID.INSTANCE.nextId());
        info.setGameUserId(uid);
        info.setProperty(TypeEnum.Null.getValue());
        info.setQuality(QualityEnum.NONE.getValue());
        info.setInfusionTimes(0);
        info.setAdditions(new ArrayList<>());
        info.setSkillGroup(new Integer[]{0, 0});
        return info;
    }

    public Integer getCurrentInfusionTimes() {
        return this.infusionTimes + 1;
    }

    /**
     * 注灵技能
     *
     * @param infusionPosition
     * @param skill
     */
    public void infusionSKill(int infusionPosition, Integer skill) {
        if (InfusionPositionEnum.INFUSION_POSITION_TWO.getValue() == infusionPosition) {
            this.skillGroup[0] = skill;
            return;
        }
        if (InfusionPositionEnum.INFUSION_POSITION_THREE.getValue() == infusionPosition) {
            this.skillGroup[1] = skill;
            return;
        }
        return;
    }

    /**
     * 增加注灵次数
     */
    public void addInfusionTimes() {
        this.infusionTimes++;
        return;
    }

    /**
     * 玩家资源类型
     *
     * @return
     */
    @Override
    public UserDataType gainResType() {
        return UserDataType.USER_INFUSION_INFO;
    }
}
