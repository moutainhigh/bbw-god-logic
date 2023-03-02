package com.bbw.god.game.config;

import com.bbw.god.game.award.Award;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 说明：
 * 迷仙洞镇守礼包
 * @author lwb
 * date 2021-06-01
 */
@Data
public class CfgMxdZhenShou implements CfgInterface{
    private List<Award> randomAwards;
    private String key;
    @Override
    public Serializable getId() {
        return key;
    }

    @Override
    public int getSortId() {
        return 0;
    }
}
