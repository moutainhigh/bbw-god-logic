package com.bbw.god.gameuser.businessgang.rd;

import com.bbw.god.rd.RDCommon;
import lombok.Data;

/**
 * 商帮npc信息
 *
 * @author fzj
 * @date 2022/1/14 15:23
 */
@Data
public class RDBusinessGangNpcInfo extends RDCommon {
    /** npc所在商帮 */
    private Integer bangId;
    /** 商帮声望值 */
    private Integer prestige;
    /** npcId */
    private Integer npcId;
    /** 好感度 */
    private Integer favorability;
}
