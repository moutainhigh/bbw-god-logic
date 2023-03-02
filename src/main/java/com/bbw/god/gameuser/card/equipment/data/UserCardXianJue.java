package com.bbw.god.gameuser.card.equipment.data;

import com.bbw.common.ID;
import com.bbw.common.ListUtil;
import com.bbw.god.gameuser.UserData;
import com.bbw.god.gameuser.UserDataType;
import com.bbw.god.gameuser.card.equipment.CfgXianJueTool;
import com.bbw.god.gameuser.card.equipment.Enum.QualityEnum;
import com.bbw.god.gameuser.card.equipment.cfg.CardEquipmentAddition;
import com.bbw.god.gameuser.leadercard.equipment.CfgEquipmentTool;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 仙诀
 *
 * @author: huanghb
 * @date: 2022/9/14 16:40
 */
@Data
public class UserCardXianJue extends UserData implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer cardId;
    /** 装备ID */
    private Integer xianJueType;
    /** 等级 */
    private Integer level = 0;
    /** 品质 */
    private Integer quality = QualityEnum.FAN_PIN.getValue();
    /** 星图进度 */
    private Integer starMapProgress = 0;
    /** 参悟值 加成类型=》加成值 */
    private Map<String, Integer> additions = new HashMap<>();

    public static UserCardXianJue getInstance(long uid, Integer cardId, Integer xianJueType) {
        UserCardXianJue instance = new UserCardXianJue();
        instance.setId(ID.INSTANCE.nextId());
        instance.setGameUserId(uid);
        instance.setCardId(cardId);
        instance.setXianJueType(xianJueType);
        return instance;
    }

    /**
     * 仙诀是否装配在指定卡牌上
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
     * 更新多个仙诀加成
     *
     * @param cardAdditions
     */
    public void updateAdditions(List<CardEquipmentAddition> cardAdditions) {
        if (ListUtil.isEmpty(cardAdditions)) {
            return;
        }
        for (CardEquipmentAddition addition : cardAdditions) {
            int currentAddition = this.additions.getOrDefault(addition.getType() + "", 0);
            this.additions.put(addition.getType() + "", currentAddition + addition.getValue());
        }
    }

    /**
     * 更新仙诀加成
     *
     * @param addition
     */
    public void updateAddition(CardEquipmentAddition addition) {
        if (null == addition) {
            return;
        }
        //获得当前加成
        int currentAddition = this.additions.getOrDefault(addition.getType() + "", 0);
        //添加最终加成
        this.additions.put(addition.getType() + "", currentAddition + addition.getValue());

    }

    /**
     * 获取仙诀加成
     *
     * @return
     */
    public List<CardEquipmentAddition> gainAdditions() {
        List<CardEquipmentAddition> cardAdditions = new ArrayList<>();
        if (null == cardAdditions) {
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
        //获得当前加成
        Integer additionValue = gainCurrenAddition(additionType);
        return new CardEquipmentAddition(additionType, additionValue);
    }

    /**
     * 升级装备
     */
    public void addLevel() {
        level += 1;
    }

    /**
     * 升级品阶
     */
    public void addQuality() {
        quality += 10;
        starMapProgress = 0;
    }

    /**
     * 添加星图进度
     */
    public void addStarProgress() {
        starMapProgress++;
        int needStarNum = CfgEquipmentTool.getNeedStarNum();
        if (starMapProgress >= needStarNum) {
            addQuality();
        }
    }

    /**
     * 扣除星图进度
     */
    public void deductStarProgress() {
        if (starMapProgress == 0) {
            return;
        }
        starMapProgress--;
    }


    /**
     * 参悟
     *
     * @param comprehendType
     */
    public void addComprehendValue(Integer comprehendType) {
        //获得当前参悟值
        Integer currentComprehendValue = gainCurrenAddition(comprehendType);
        //随机获得参悟加值
        Integer comprehendAddValue = CfgXianJueTool.getComprehendAddalue();
        //获得当前参悟上限
        Integer comprehendAddValueLimit = CfgXianJueTool.getComprehendLimitInfo(this.quality);
        Integer finalComprehendValue = Math.min(comprehendAddValueLimit, comprehendAddValue + currentComprehendValue);
        this.additions.put(comprehendType + "", finalComprehendValue);
        return;
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

    @Override

    public UserDataType gainResType() {
        return UserDataType.USER_CARD_XIAN_JUE;
    }
}
