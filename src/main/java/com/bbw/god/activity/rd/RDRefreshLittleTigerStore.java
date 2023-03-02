package com.bbw.god.activity.rd;

import com.bbw.god.game.award.RDAward;
import com.bbw.god.rd.RDCommon;
import lombok.Data;

/**
 * 小虎商店
 *
 * @author fzj
 * @date 2022/3/9 15:29
 */
@Data
public class RDRefreshLittleTigerStore extends RDCommon {
    /** 新的奖励 */
    private RDAward newAward;
}
