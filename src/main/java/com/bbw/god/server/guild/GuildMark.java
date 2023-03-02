package com.bbw.god.server.guild;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 行会功能标识类型
 *
 * @author suhq
 * @date 2020-02-12 11:07:34
 */
@Getter
@AllArgsConstructor
public enum GuildMark {
    TASK("行会任务", 10),
    APPLY("行会申请", 20);

    private String name;
    private int value;
}
