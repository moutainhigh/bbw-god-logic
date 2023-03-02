package com.bbw.god.game.config;

import lombok.Data;

import java.util.List;

/**
 * 区服组配置
 *
 * @author suhq
 * @date 2019年3月11日 下午10:16:05
 */
@Data
public class CfgServerGroup implements CfgInterface {
    // 区服组ID
    private Integer groupId;
    private Integer appProductGroupId;// APP使用的产品分组
    private Integer wechatProductGroupId;// 微信公众号使用的产品分组
    // 强联网地址
    private String wsUrl;
    // 玩家竞技ip
    private List<String> fsFightIps;

    @Override
    public Integer getId() {
        return groupId;
    }

    @Override
    public int getSortId() {
        return groupId;
    }

}
