package com.bbw.god.gameuser.biyoupalace.event;

import com.bbw.god.event.BaseEventParam;
import lombok.Getter;
import lombok.Setter;

/**
 * 碧游宫领悟达成事件参数
 *
 * @author suhq
 * @date 2021/7/2 上午9:12
 **/
@Getter
@Setter
public class EPBiyouRealized extends BaseEventParam {
    /** 篇章 */
    private Integer chapter;

    public static EPBiyouRealized instance(BaseEventParam ep, Integer chapter) {
        EPBiyouRealized ew = new EPBiyouRealized();
        ew.setValues(ep);
        ew.setChapter(chapter);
        return ew;
    }
}
