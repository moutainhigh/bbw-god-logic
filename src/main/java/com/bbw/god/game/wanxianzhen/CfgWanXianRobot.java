package com.bbw.god.game.wanxianzhen;

import com.bbw.god.game.config.CfgEntityInterface;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class CfgWanXianRobot implements CfgEntityInterface {
    private Integer key;
    private List<RobotInfo> guCards;
    @Override
    public Serializable getId() {
        return key;
    }

    @Override
    public int getSortId() {
        return key;
    }

    @Data
    public static class RobotInfo{
        private Long uid;
        private List<String> cards;
    }
}
