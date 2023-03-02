package com.bbw.god.game.flx;

import com.bbw.god.game.config.TypeEnum;
import com.bbw.god.game.data.GameDataType;
import com.bbw.god.game.data.GameDayData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 福临轩开奖结果
 *
 * @author suhq
 * @date 2018年10月30日 上午11:16:38
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class FlxDayResult extends GameDayData {
    //猜数字结果
    private Integer sgNum;// 数字结果
    //压压乐结果
    private Integer ysgBet1;// 元素1
    private Integer ysgBet2;// 元素2
    private Integer ysgBet3;// 元素3

    /**
     * 获得押押乐开奖结果的元素描述
     *
     * @return
     */
    public String getYsgEleNames() {
        return String.format("%s、%s、%s", TypeEnum.fromValue(ysgBet1).getName(), TypeEnum.fromValue(ysgBet2).getName(), TypeEnum.fromValue(ysgBet3).getName());
    }

    @Override
    public GameDataType gainDataType() {
        return GameDataType.FLXRESULT;
    }
}
