package com.bbw.god.mall.store;

import com.bbw.god.rd.RDCommon;
import lombok.Data;

import java.util.List;

/**
 * @author lwb
 * @date 2020/3/24 10:15
 */
@Data
public class RDStore extends RDCommon {
    private static final long serialVersionUID = -3385871042737202361L;
    private List<RDStoreGoodsInfo> integralGoods = null;// 返回积分商店商品
    private int currency = 0;//当前积分值
}
