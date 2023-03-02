package com.bbw.god.game.zxz.cfg;

import com.bbw.god.game.config.CfgEntityInterface;
import lombok.Data;

import java.io.Serializable;

/**
 * 灵装词条效果配置
 * @author: hzf
 * @create: 2022-12-15 11:28
 **/
@Data
public class CfgLingZhuangEntryEntity implements CfgEntityInterface, Serializable {
    private static final long serialVersionUID = 7073473097670406575L;
    /** 灵装词条等级 */
    private Integer lv;
    /** 仙决 等级 */
    private Integer xianJueLv;
    /** 品质====>淬星 */
    private Integer quality;
    /** 参悟值 % */
    private Integer comprehension;
    /** 攻 */
    private Integer attack;
    /** 防 */
    private Integer defense;
    /** 强度 */
    private Integer strength;
    /** 韧度 */
    private Integer tenacity;

    @Override
    public Serializable getId() {
        return lv;
    }

    @Override
    public int getSortId() {
        return lv;
    }
}
