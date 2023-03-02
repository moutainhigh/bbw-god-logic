package com.bbw.god.game.config.city;

import lombok.Data;

import java.util.List;

/**
 * 升级法坛
 *
 * @author fzj
 * @date 2021/11/11 9:27
 */
@Data
public class CfgUpgradeFaTan {
    /** 法坛等级 */
    private Integer faTanLv;
    /** 需要的铜钱 */
    private Integer needCopper;
    /** 需要的修缮值 */
    private Integer needRepairValue;
    /** 升级成功概率 */
    private Integer probability;
    /** 升级需要的法宝 */
    private List<CfgNeedTreasure> needTreasure;

}
