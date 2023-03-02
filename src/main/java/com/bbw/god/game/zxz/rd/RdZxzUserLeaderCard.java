package com.bbw.god.game.zxz.rd;

import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.game.zxz.entity.UserZxzCardGroupInfo;
import com.bbw.god.game.zxz.entity.ZxzUserLeaderCard;
import com.bbw.god.gameuser.leadercard.equipment.UserLeaderEquipment;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 诛仙阵返回主角卡
 * @author: hzf
 * @create: 2022-12-27 15:23
 **/
@Data
public class RdZxzUserLeaderCard {
    /** 卡牌是否活着（战败） */
    private Boolean alive = true;
    /** 主角卡牌id */
    private Integer cardId;
    /** 主角卡名称 */
    private String cardName;
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


    public static RdZxzUserLeaderCard getInstance(ZxzUserLeaderCard userLeaderCard,String cardName){
        RdZxzUserLeaderCard rd = new RdZxzUserLeaderCard();
        rd.setCardId(userLeaderCard.getCardId());
        rd.setAlive(userLeaderCard.getAlive());
        rd.setProperty(userLeaderCard.getProperty());
        rd.setSex(userLeaderCard.getSex());
        rd.setLv(userLeaderCard.getLv());
        rd.setHv(userLeaderCard.getHv());
        rd.setStar(userLeaderCard.getStar());
        rd.setAtk(userLeaderCard.getAtk());
        rd.setHp(userLeaderCard.getHp());
        rd.setCardName(cardName);
        rd.setFashion(userLeaderCard.getFashion());
        rd.setSkills(userLeaderCard.getSkills());
        rd.setEquips(userLeaderCard.getEquips());
        rd.setBeasts(userLeaderCard.getBeasts());
        return rd;
    }
}
