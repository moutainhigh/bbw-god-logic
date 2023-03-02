package com.bbw.god.activity.worldcup.cfg;

import com.bbw.god.game.config.CfgEntityInterface;
import lombok.Data;

import java.io.Serializable;

/**
 * 世界杯国家配置
 * @author: hzf
 * @create: 2022-11-12 01:56
 **/
@Data
public class CfgCountry implements CfgEntityInterface, Serializable {
    private static final long serialVersionUID = 1L;

    /** 每个国家对应的编号 */
    private int countryId;
    /** 国家名字 */
    private String name;

    @Override
    public Serializable getId() {
        return countryId;
    }

    @Override
    public int getSortId() {
        return countryId;
    }
}
