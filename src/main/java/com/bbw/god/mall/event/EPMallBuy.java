package com.bbw.god.mall.event;

import com.bbw.god.event.BaseEventParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 商城购买事件基参数
 *
 * @author fzj
 * @date 2021/8/19 9:42
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class EPMallBuy extends BaseEventParam {
    /** 物品id */
    private Integer goodsId;
    /** 商店类型 */
    private Integer mallType;
    /** 购买数量 */
    private Integer num;

    public EPMallBuy(Integer goodsId, Integer mallType, Integer num, BaseEventParam bep) {
        this.goodsId = goodsId;
        this.mallType = mallType;
        this.num = num;
        setValues(bep);
    }
}
