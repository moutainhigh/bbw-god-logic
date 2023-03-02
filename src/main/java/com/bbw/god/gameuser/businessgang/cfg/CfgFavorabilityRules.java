package com.bbw.god.gameuser.businessgang.cfg;

import lombok.Data;

import java.io.Serializable;

/**
 * 好感度规则
 *
 * @author fzj
 * @date 2022/1/18 9:09
 */
@Data
public class CfgFavorabilityRules implements Serializable {
    private static final long serialVersionUID = 3095926888734005517L;
    /** npc类型 */
    private Integer npcType;
    /** 好感度 */
    private Integer favorability;
    /** 需要的掌舵人好感度 */
    private Integer needZhangDrFavorability;
    /** 需要商帮声望 */
    private Integer needBusinessGangPrestige;
}
