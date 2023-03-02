package com.bbw.god.pay;

import com.bbw.exception.CoderException;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 游戏产品组
 */
@Getter
@AllArgsConstructor
public enum ProductGroup {
    Normal(1, "游戏内产品组"), //渠道自己的支付方式
    WxPayJSAPI(2, "公众号产品组"), //微信JSAPI,公众号，小程序
    ;

    private int value;
    private String name;

    public static ProductGroup fromValue(int value) {
        for (ProductGroup item : values()) {
            if (item.getValue() == value) {
                return item;
            }
        }
        throw CoderException.fatal("没有键值为[" + value + "]的数据类型！");
    }
}
