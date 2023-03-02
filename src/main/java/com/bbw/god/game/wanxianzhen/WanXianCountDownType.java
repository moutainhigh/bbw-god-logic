package com.bbw.god.game.wanxianzhen;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author lwb
 * @date 2020/5/21 10:14
 */
@Getter
@AllArgsConstructor
public enum WanXianCountDownType {
    END_SIGN_UP(0,"报名截止倒计时"),
    BEGIN(1,"开始倒计时"),
    NEXT(2,"下一轮倒计时"),
    ELIMINATION(3,"淘汰赛分组倒计时"),
    BEGIN_CHAMPION_PREDICTION(5,"预测冠军倒计时"),
    END_CHAMPION_PREDICTION(5,"可预测时间"),
    END_SEASON(6,"赛季结束倒计时");
    private int val;
    private String memo;
}
