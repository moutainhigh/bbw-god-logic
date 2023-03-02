package com.bbw.god.game.sxdh.rd;

import com.bbw.god.rd.RDSuccess;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * 商店
 *
 * @author suhq
 * @date 2019-06-21 10:35:11
 */
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@Deprecated
public class RDSxdhShop extends RDSuccess implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer sxdhLv;
    private List<RDShopProduct> shopProducts;

    @Data
    public static class RDShopProduct {
        private Integer id;
        private Integer goodId;
        private Integer type;// 类型
        private Integer needGold;// 元宝
        private Integer needBean;// 仙豆
        private Integer discount;// 折扣
        private Integer minTitle;// 最小称号
        private Integer remainTime;// 剩余次数
        private Integer limit;// 购买限制
    }

}
