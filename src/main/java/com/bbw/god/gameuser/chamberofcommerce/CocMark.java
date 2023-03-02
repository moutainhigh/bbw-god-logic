package com.bbw.god.gameuser.chamberofcommerce;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 商会功能标识类型
 *
 * @author suhq
 * @date 2019-06-03 11:07:34
 */
@Getter
@AllArgsConstructor
public enum CocMark {
    TASK("商会任务", 10);

    private String name;
    private int value;
}
