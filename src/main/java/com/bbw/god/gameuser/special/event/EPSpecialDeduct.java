package com.bbw.god.gameuser.special.event;

import com.bbw.god.event.BaseEventParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 用于扣除特产事件传值,EV***
 *
 * @author suhq
 * @date 2018年10月23日 下午6:17:58
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class EPSpecialDeduct extends BaseEventParam {
    private Integer pos = 0;// 初始事件时的玩家位置，丢弃特产时该值为0
    private List<SpecialInfo> specialInfoList;

    public static EPSpecialDeduct instance(BaseEventParam bep, List<SpecialInfo> specialInfoList) {
        return instance(bep, 0, specialInfoList);
    }

    public static EPSpecialDeduct instance(BaseEventParam bep, int pos, List<SpecialInfo> specialInfoList) {
        EPSpecialDeduct ev = new EPSpecialDeduct();
        ev.setValues(bep);
        ev.setPos(pos);
        ev.setSpecialInfoList(specialInfoList);
        return ev;
    }

    @Data
    public static class SpecialInfo {
        private Long userSpecialId;// 玩家特产ID
        private Integer baseSpecialIds;// 特产基本ID
        private Integer buyPrice = 0;// 成本
        private Integer sellPrice = 0;// 售价

        public static SpecialInfo getInstance(Long userSpecialId, Integer baseSpecialIds, Integer buyPrice) {
            SpecialInfo info = new SpecialInfo();
            info.setUserSpecialId(userSpecialId);
            info.setBaseSpecialIds(baseSpecialIds);
            info.setBuyPrice(buyPrice);
            return info;
        }

        public static SpecialInfo getInstance(Long userSpecialId, Integer baseSpecialIds, Integer buyPrice, Integer sellPrice) {
            SpecialInfo info = new SpecialInfo();
            info.setUserSpecialId(userSpecialId);
            info.setBaseSpecialIds(baseSpecialIds);
            info.setBuyPrice(buyPrice);
            info.setSellPrice(sellPrice);
            return info;
        }
    }
}
