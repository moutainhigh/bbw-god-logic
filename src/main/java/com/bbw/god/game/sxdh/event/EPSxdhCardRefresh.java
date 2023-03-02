package com.bbw.god.game.sxdh.event;

import com.bbw.god.event.BaseEventParam;
import lombok.Data;

/**
 * @author 作者 ：lwb
 * @version 创建时间：2019年11月25日 上午10:31:57
 * 类说明 神仙大会称号变动事件
 */
@Data
public class EPSxdhCardRefresh extends BaseEventParam {
    private int refreshCardNum;

    public static EPSxdhCardRefresh instance(BaseEventParam bep, int refreshCardNum) {
        EPSxdhCardRefresh ep = new EPSxdhCardRefresh();
        ep.setRefreshCardNum(refreshCardNum);
        ep.setValues(bep);
        return ep;
    }
}
