package com.bbw.god.gameuser.yuxg.cfg;

import lombok.Data;

import java.io.Serializable;

/**
 * 符图槽数量
 *
 * @author fzj
 * @date 2021/11/8 13:29
 */
@Data
public class CfgFuTuSlotNum implements Serializable {
    private static final long serialVersionUID = 3095926888734005517L;
    /** 符图槽数量 */
    Integer fuTuSlotNum;
    /** 法坛总等级 */
    Integer faTanAllLv;
}
