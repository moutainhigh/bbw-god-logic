package com.bbw.god.game.wanxianzhen;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author lwb
 * @date 2020/5/12 9:58
 */
@Getter
@AllArgsConstructor
public enum WanXianPageType {
    SING_UP("报名阶段",0),
    QUALIFYING_RACE("资格赛阶段",12),
    ELIMINATION_SERIES_RACE("淘汰赛阶段",345),
    GROUP_STAGE_CP("小组赛冠军预测",68),
    GROUP_STAGE("小组赛阶段",6),
    FINALS_RACE_CP("决赛冠军预测",74),
    FINALS_RACE("决赛阶段",7);
    private String memo;
    private int val;

    public static WanXianPageType fromVal(Integer val){
        if (val==null){
            return null;
        }
        for (WanXianPageType type:values()){
            if (type.getVal()==val){
                return type;
            }
        }
        return null;
    }
}
