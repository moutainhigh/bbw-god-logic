package com.bbw.god.server.maou.attack;

import com.bbw.god.game.combat.data.CombatSkillEnum;
import com.bbw.god.game.config.TypeEnum;
import com.bbw.god.game.config.card.CfgCardEntity;
import com.bbw.god.gameuser.card.UserCard;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author suhq
 * @description: 魔王攻击卡牌
 * @date 2019-12-26 10:30
 **/
@Data
public class MaouAttackCard implements Serializable {
    private int id;// 本次战斗中的卡牌ID
    // ---------卡牌初始化配置值------------
    private int lv = 0;// 等级
    private int hv = 0;// 阶级
    private TypeEnum type = TypeEnum.Gold;// 属性
    private int stars = 1;// 星级
    private int groupId = -1;// 组合ID
    private List<Integer> activeSkill;
    // ---------当前值------------
    private int atk = 0;// 当前物理攻击

    public static MaouAttackCard getInstance(UserCard userCard) {
        MaouAttackCard attackCard = new MaouAttackCard();
        attackCard.setId(userCard.getBaseId());
        attackCard.setLv(userCard.getLevel());
        attackCard.setHv(userCard.getHierarchy());
        CfgCardEntity cfgCard = userCard.gainCard();
        attackCard.setType(TypeEnum.fromValue(cfgCard.getType()));
        attackCard.setStars(cfgCard.getStar());
        attackCard.setGroupId(cfgCard.getGroup());
        List<Integer> activedSkills = userCard.gainActivedSkills();
        activedSkills.add(0, CombatSkillEnum.NORMAL_ATTACK.getValue());
        attackCard.setActiveSkill(activedSkills);
        attackCard.setAtk(userCard.gainAttack());
        return attackCard;
    }
}
