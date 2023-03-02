package com.bbw.god.gameuser.yuxg.cfg;

import com.bbw.god.game.award.YuxgAward;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 符图、玉髓产出
 *
 * @author fzj
 * @date 2021/11/1 11:23
 */
@Data
public class CfgPrayPro implements Serializable {
    private static final long serialVersionUID = 3095926888734005517L;
    /** 晶石id */
    private Integer sparId;
    /** 符坛 */
    private Integer fuTan;
    /** 祈福消耗晶石数量 */
    private Integer prayConsumeSparNum;
    /** 产出 */
    private List<YuxgAward> awards;
}
