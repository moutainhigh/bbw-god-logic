package com.bbw.god.game.sxdh.event;

import com.bbw.god.event.BaseEventParam;
import com.bbw.god.game.sxdh.config.Title;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author 作者 ：lwb
 * @version 创建时间：2019年11月25日 上午10:31:57
 * 类说明 神仙大会称号变动事件
 */
@Deprecated
@Data
public class SxdhTitleChange extends BaseEventParam {
    private List<Integer> sids;// 战区区服
    private Integer title;
    // 获取战区排名变化前的排名集合，key是玩家id，value是对应的称号枚举
    private Map<Long, Title> oldRankMap;

    public static SxdhTitleChange instance(BaseEventParam ep, List<Integer> sids, Integer title, Map<Long, Title> oldRankMap) {
        SxdhTitleChange gsb = new SxdhTitleChange();
        gsb.sids = sids;
        gsb.title = title;
        gsb.setValues(ep);
        gsb.setOldRankMap(oldRankMap);
        return gsb;
    }
}
