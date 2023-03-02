package com.bbw.god.game.zxz.entity;

import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.leadercard.UserLeaderCard;
import com.bbw.god.gameuser.leadercard.equipment.UserLeaderEquipment;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 诛仙阵 玩家主角卡
 * @author: hzf
 * @create: 2022-12-14 17:28
 **/
@Data
public class ZxzUserLeaderCard {
    /** 卡牌是否活着（战败） */
    private Boolean alive = true;
    /** 主角卡牌id */
    private Integer cardId;
    /** 五行属性 */
    private Integer property;
    /** 性别 */
    private Integer sex = 0;
    /** 等级 */
    private Integer lv = 0;
    /** 阶级 */
    private Integer hv = 0;
    /** 星级 */
    private Integer star = 1;
    /** 攻击 */
    private Integer atk = 0;
    /** 防御 */
    private Integer hp = 0;
    /* 主角卡技能组 */
    private List<Integer> skills;
    /** 当前使用的时装 */
    private Integer fashion = TreasureEnum.FASHION_FaSFS.getValue();
    /**
     * 装备
     */
    private UserLeaderEquipment[] equips = null;
    /**
     * 宠物
     */
    private int[] beasts = null;


    /**
     * 构建 诛仙阵 主角卡
     * @param userLeaderCard
     * @return
     */
    public static ZxzUserLeaderCard instance(UserLeaderCard userLeaderCard,UserLeaderEquipment[] equips,int[] beasts) {
        ZxzUserLeaderCard zxzUserLeaderCard = new ZxzUserLeaderCard();
        zxzUserLeaderCard.setCardId(userLeaderCard.getBaseId());
        zxzUserLeaderCard.setProperty(userLeaderCard.getProperty());
        zxzUserLeaderCard.setSex(userLeaderCard.getSex());
        zxzUserLeaderCard.setLv(userLeaderCard.getLv());
        zxzUserLeaderCard.setHv(userLeaderCard.getHv());
        zxzUserLeaderCard.setStar(userLeaderCard.getStar());
        zxzUserLeaderCard.setAtk(userLeaderCard.settleTotalAtkWithEquip());
        zxzUserLeaderCard.setHp(userLeaderCard.settleTotalHpWithEquip());
        zxzUserLeaderCard.setFashion(userLeaderCard.getFashion());
        List<Integer> skillIds = new ArrayList<>();
        for (int skill : userLeaderCard.currentSkills()) {
            skillIds.add(skill);
        }
        zxzUserLeaderCard.setSkills(skillIds);
        zxzUserLeaderCard.setEquips(equips);
        zxzUserLeaderCard.setBeasts(beasts);
       return zxzUserLeaderCard;
    }
}
