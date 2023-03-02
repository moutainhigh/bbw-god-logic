package com.bbw.god.gameuser.businessgang.cfg;

import lombok.Data;

import java.io.Serializable;

/**
 * 声望及对应每日衰减量
 *
 * @author fzj
 * @date 2022/1/17 10:28
 */
@Data
public class CfgReputationAndDecay implements Serializable {
    private static final long serialVersionUID = 3095926888734005517L;
    /** 声望 */
    private Integer prestige;
    /** 衰减量 */
    private Integer decay;
}
