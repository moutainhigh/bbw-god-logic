package com.bbw.god.gameuser.businessgang.event;

import com.bbw.god.event.BaseEventParam;
import com.bbw.god.gameuser.task.TaskGroupEnum;
import lombok.Data;

/**
 * 增加商帮NPC好感度参数
 *
 * @author fzj
 * @date 2022/1/29 13:37
 */
@Data
public class EPAddGangNpcFavorability extends BaseEventParam {
    /** npcId */
    private int npcId;
    /** 增加的好感度 */
    private int addFavorability;

    public EPAddGangNpcFavorability(int npcId, int addFavorability, BaseEventParam bep) {
        setValues(bep);
        this.npcId = npcId;
        this.addFavorability = addFavorability;
    }
}
