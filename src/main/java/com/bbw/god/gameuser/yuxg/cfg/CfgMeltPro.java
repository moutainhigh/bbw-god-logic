package com.bbw.god.gameuser.yuxg.cfg;

import com.bbw.god.game.award.Award;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 晶石产出
 *
 * @author fzj
 * @date 2021/11/1 11:19
 */
@Data
public class CfgMeltPro implements Serializable {
    private static final long serialVersionUID = 3095926888734005517L;
    /** 法宝等级 */
    private Integer treasureStar;
    /** 产出 */
    private List<Award> awards;
    /** 熔炼值 */
    private Integer meltValue;
}
