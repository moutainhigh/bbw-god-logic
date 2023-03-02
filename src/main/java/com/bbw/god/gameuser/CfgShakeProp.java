package com.bbw.god.gameuser;

import com.bbw.god.game.config.CfgInterface;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.List;

@Slf4j
@Data
public class CfgShakeProp implements CfgInterface, Serializable {
    private static final long serialVersionUID = 6223779702493163783L;
    private String key;
    // 战斗金币收益
    private List<CfgCityProp> cityProps;

    @Override
    public Serializable getId() {
        return key;
    }

    @Override
    public int getSortId() {
        return 0;
    }

    @Data
    public static class CfgCityProp implements Serializable {
        private static final long serialVersionUID = -3967840783101188233L;
        // 城市类型名
        private String name;
        // 类型
        private Integer type;
        // 概率
        private Integer prop;
    }
}
