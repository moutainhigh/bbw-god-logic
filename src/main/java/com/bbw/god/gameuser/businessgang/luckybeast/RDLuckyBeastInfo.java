package com.bbw.god.gameuser.businessgang.luckybeast;

import com.bbw.god.rd.RDCommon;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 招财兽信息
 *
 * @author: huanghb
 * @date: 2022/1/24 16:21
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RDLuckyBeastInfo extends RDCommon implements Serializable {
    private static final long serialVersionUID = 7409189386250719040L;
    /** 卡牌id */
    private Integer cardId;
    /** 玩家剩余攻击次数 */
    private Integer remainAttackTimes;
    /** 玩家总攻击次数 */
    private Integer totalAttackTimes;
    /** 玩家剩余购买攻击的次数 */
    private Integer remainBuyAttackTimes;
    /** 技能id */
    private List<Integer> skillIds;
    /** 技能加成 */
    private Integer[] skillAdds;
    /** 招财兽Id */
    private Integer luckyBeastId;
    /** 血量 */
    private Integer hp;
    /** 位置 */
    private Integer position;
    /** 已使用卡牌id */
    private List<Integer> excludedCards = new ArrayList<>();

    /**
     * 返回招财兽信息初始化
     *
     * @param userLuckBeast
     * @return
     */
    public static RDLuckyBeastInfo getInstance(UserLuckyBeast userLuckBeast) {
        RDLuckyBeastInfo rd = new RDLuckyBeastInfo();
        rd.setCardId(userLuckBeast.getCardId());
        rd.setRemainAttackTimes(userLuckBeast.getRemainfreeAttackTimes());
        rd.setTotalAttackTimes(userLuckBeast.getTotalAttackTimes());
        rd.setLuckyBeastId(userLuckBeast.getLuckyBeastId());
        rd.setSkillAdds(userLuckBeast.getSkillBuffs());
        rd.setPosition(userLuckBeast.getPosition());
        rd.setSkillIds(userLuckBeast.getSkillIds());
        rd.setRemainBuyAttackTimes(userLuckBeast.getRemainBuyAttacksTimes());
        rd.setHp(userLuckBeast.getHp());
        rd.setPosition(userLuckBeast.getPosition());
        rd.setExcludedCards(userLuckBeast.getExcludedCards());
        return rd;
    }
}
