package com.bbw.god.game.sxdh.config;

import lombok.Data;

import java.io.Serializable;

/**
 * 神仙大会商店
 *
 * @author suhq
 * @date 2019-06-18 10:29:04
 */
@Deprecated
@Data
public class CfgSxdhShopProduct implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;
    private String name;
    private Integer serial;
    private Integer type;
    private Integer goodId;
    private Integer num;
    private Integer needGold;
    private Integer needBean;
    private Integer limit;
    private Integer minTitle;
    private String titleName = null;
    private Boolean isValid;

    public int getGoldPrice(int discount) {
        return (int) Math.ceil(needGold * discount / 100.0);
    }

    public int getBeanPrice(int discount) {
        return (int) Math.ceil(needBean * discount / 100.0);
    }

}
