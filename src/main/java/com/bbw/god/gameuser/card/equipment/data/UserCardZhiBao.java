package com.bbw.god.gameuser.card.equipment.data;

import com.bbw.common.ID;
import com.bbw.common.ListUtil;
import com.bbw.common.MapUtil;
import com.bbw.god.game.config.TypeEnum;
import com.bbw.god.gameuser.UserData;
import com.bbw.god.gameuser.UserDataType;
import com.bbw.god.gameuser.card.equipment.Enum.CardEquipmentAdditionEnum;
import com.bbw.god.gameuser.card.equipment.cfg.CardEquipmentAddition;
import com.bbw.god.gameuser.kunls.CfgKunLSTool;
import com.bbw.god.gameuser.kunls.data.UserInfusionInfo;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 、至宝
 *
 * @author: huanghb
 * @date: 2022/9/14 16:40
 */
@Data
public class UserCardZhiBao extends UserData implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer cardId = 0;
    /** 至宝ID */
    private Integer zhiBaoId;
    /** 五行属性 */
    private Integer property = TypeEnum.Null.getValue();
    /** 加成  加成类型=》加成值 */
    private Map<String, Integer> additions = new HashMap<>();
    /** 技能组 */
    private Integer[] skillGroup;

    public static UserCardZhiBao getInstance(long uid, int zhibaoId) {
        UserCardZhiBao instance = new UserCardZhiBao();
        instance.setId(ID.INSTANCE.nextId());
        instance.setGameUserId(uid);
        instance.setZhiBaoId(zhibaoId);
        instance.setSkillGroup(new Integer[]{0, 0});
        return instance;
    }

    /**
     * 至宝是否装配在指定卡牌上
     *
     * @param cfgCardId
     * @return
     */
    public boolean ifPutOnCard(int cfgCardId) {
        if (null == cardId || cardId == 0) {
            return false;
        }
        return cardId % 10000 == cfgCardId % 10000;
    }

    /**
     * 是否空技能
     *
     * @return
     */
    public boolean ifNullSkill() {
        if (0 == this.skillGroup.length) {
            return true;
        }
        for (int i = 0; i < this.skillGroup.length; i++) {
            if (0 == this.skillGroup[i]) {
                continue;
            }
            return false;
        }
        return true;
    }

    /**
     * 更新加成
     *
     * @param cardAdditions
     */
    public void updateAdditions(List<CardEquipmentAddition> cardAdditions) {
        if (ListUtil.isEmpty(cardAdditions)) {
            return;
        }
        for (CardEquipmentAddition addition : cardAdditions) {
            this.additions.put(addition.getType() + "", addition.getValue());
        }
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

    /**
     * 获取某类加成的加成
     *
     * @param additionType
     * @return
     */
    public CardEquipmentAddition gainAddition(int additionType) {
        if (null == additions) {
            return new CardEquipmentAddition(additionType, 0);
        }
        Integer additionValue = additions.getOrDefault(additionType + "", 0);
        return new CardEquipmentAddition(additionType, additionValue);
    }

    /**
     * 灵宝出世
     *
     * @param userInfusionInfo
     * @return
     */
    public void born(UserInfusionInfo userInfusionInfo) {
        this.property = userInfusionInfo.getProperty();
        this.skillGroup = userInfusionInfo.getSkillGroup();

        //初始化加成
        if (ListUtil.isNotEmpty(userInfusionInfo.getAdditions())) {
            if (null == additions) {
                additions = new HashMap<>();
            }
            for (CardEquipmentAddition addition : userInfusionInfo.getAdditions()) {
                additions.put(addition.getType().toString(), addition.getValue());
            }
        }
    }

    /**
     * 穿戴
     */
    public void putOn(Integer cardId) {
        this.cardId = cardId;
    }

    /**
     * 脱下
     */
    public void takeDown() {
        this.cardId = 0;
    }

    /**
     * 是否满攻击 1060 代表仙品法器
     *
     * @return
     */
    public Integer isFullAttack() {
        int maxAttack = CfgKunLSTool.getProperty(1060).getMaxAttack();
        CardEquipmentAddition addition = gainAddition(CardEquipmentAdditionEnum.ATTACK.getValue());
        if (maxAttack != addition.getValue()) {
            return 0;
        }
        return 1;
    }

    /**
     * 是否满防御 2060 代表仙品灵宝
     *
     * @return
     */
    public Integer isFullDefense() {
        int maxAttack = CfgKunLSTool.getProperty(2060).getMaxDefense();
        CardEquipmentAddition addition = gainAddition(CardEquipmentAdditionEnum.DEFENSE.getValue());
        if (maxAttack != addition.getValue()) {
            return 0;
        }
        return 1;
    }

    /**
     * 获得某一类当前加成加值
     *
     * @param comprehendType
     * @return
     */
    public Integer gainCurrenAddition(Integer comprehendType) {
        return additions.getOrDefault(comprehendType + "", 0);
    }

    /**
     * 是否穿戴 0 未穿戴 1穿戴
     *
     * @return
     */
    public int ifPutOn() {
        return this.cardId != 0 ? 1 : 0;
    }

    @Override
    public UserDataType gainResType() {
        return UserDataType.USER_CARD_ZHI_BAO;
    }
}
