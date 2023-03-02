package com.bbw.god.gameuser.achievement;

import com.bbw.god.game.award.Award;
import com.bbw.god.game.config.CfgEntityInterface;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class CfgAchievementEntity implements CfgEntityInterface, Serializable {
    private static final long serialVersionUID = 1L;
    private Integer id; //
    private Integer type;
    private Integer serial; // 系列
    private Integer order;// 排序
    private String name; //
    private String detail; //
    private Integer value; //
    private List<Award> awards; //
    private Boolean isValid;
    private Integer score;
    private Boolean isBroadCast;//是否需要跑马灯

    @Override
    public int getSortId() {
        return this.getId();
    }

}
