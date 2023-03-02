package com.bbw.god.mall;

import lombok.Data;

/**
 * 商城购买需要项
 *
 * @author suhq
 * @date 2020-11-19 14:45
 **/
@Data
public class MallNeed {
    private Integer id = null;// 指定的ID
    private Integer item = null;// 见AwardEnum
    private Integer price = null;//价格
    private Integer originalPrice = null;//原始价格
    private Integer discount = null;//折扣
}
