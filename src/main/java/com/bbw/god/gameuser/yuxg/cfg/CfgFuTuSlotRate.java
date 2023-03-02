package com.bbw.god.gameuser.yuxg.cfg;

import lombok.Data;

import java.io.Serializable;

/**
 * 符图槽加成
 *
 * @author: huanghb
 * @date: 2022/5/19 17:02
 */
@Data
public class CfgFuTuSlotRate implements Serializable {
    private static final long serialVersionUID = 3095926888734005517L;
    /** 符图槽基本加成 */
    public static Integer FUTU_SLOT_BASE_RATE = 100;
    /** 符图槽位置 */
    Integer fuTuSlotPos;
    /** 法坛总等级 */
    Integer faTanTotalLevel;
    /** 加成 */
    Integer rate;
}
