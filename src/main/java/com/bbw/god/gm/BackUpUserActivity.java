package com.bbw.god.gm;

import com.bbw.god.game.config.CfgEntityInterface;
import lombok.Data;

import java.io.Serializable;

/**
 * @author suchaobin
 * @description TODO
 * @date 2020/9/3 23:55
 **/
@Data
public class BackUpUserActivity implements CfgEntityInterface, Serializable {
    private static final long serialVersionUID = 1L;
    private Long id; //
    private Long gameUserId;
    private Integer baseId;//配置ID
    private String name; //名称
    private Long aId;// 活动实例ID
    private Integer round = 1;
    private Integer progress;// 进度
    private Integer status;// 状态
    private Integer awardIndex;// 指定奖励
    private String date;// 活动参与时间

    @Override
    public int getSortId() {
        return this.baseId;
    }

}
