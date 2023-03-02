package com.bbw.god.gameuser.businessgang.cfg;

import lombok.Data;

import java.io.Serializable;

/**
 * 需要解锁的商品
 *
 * @author fzj
 * @date 2022/1/18 11:48
 */
@Data
public class CfgNeedUnlockGoods implements Serializable {
    private static final long serialVersionUID = 3095926888734005517L;
    /** 物品id */
    private Integer goodId;
    /** 物品类别 */
    private Integer item;
    /** 对应npc */
    private Integer correspondNpc;
    /** 需要好感度 */
    private Integer needFavorability;
    /** 对应商帮 */
    private Integer correspondBang;
    /** 需要声望 */
    private Integer needPrestige;
}
