package com.bbw.god.game.zxz.cfg;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 诛仙阵的基本配置
 * @author: hzf
 * @create: 2022-09-14 15:47
 **/
@Data
public class CfgZxzLevel implements Serializable {
    private static final long serialVersionUID = 7073473097670406575L;
    /** 难度类型 */
    private Integer difficulty;
    /** needUnlockScore */
    private Integer unlockNeedScore;
    /** 区域Id */
    private List<Integer> regions;
    /** 卡牌压缩等级 */
    private Integer reduceCardLv;
    /** 所带的词条初始范围*/
    private List<Integer> entryGears;
    /**词条 档位上限 */
    private Integer unlockEntryGearLimit;
    /** 词条的初始等级 */
    private  Integer entryInitLv;
    /** 词条等级上限 */
    private Integer entryLvLimit;



}
