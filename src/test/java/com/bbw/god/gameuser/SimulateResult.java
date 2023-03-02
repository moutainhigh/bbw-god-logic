package com.bbw.god.gameuser;

import com.bbw.god.rd.RDCommon;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @author suhq
 * @description: 模拟结果
 * @date 2020-02-10 18:37
 **/
@Data
public class SimulateResult {
    private Long uid;
    private String nickname;
    private Integer addedLevel;

    private Integer addedGold = 0;// 元宝
    private Integer deductedGold = 0;// 元宝

    private Long addedCopper = 0L;// 铜钱
    private Long deductedCopper = 0L;// 铜钱

    private Integer addedDices = 0;// 体力
    private Integer deductedDices = 0;// 体力

    private Integer addedGoldEle = 0;// 金元素
    private Integer deductGoldEle = 0;// 金元素

    private Integer addedWoodEle = 0;// 木元素
    private Integer deductedWoodEle = 0;// 木元素

    private Integer addedWaterEle = 0;// 水元素
    private Integer deductedWaterEle = 0;// 水元素

    private Integer addedFireEle = 0;// 火元素
    private Integer deductFireEle = 0;// 火元素

    private Integer addedEarthEle = 0;// 土元素
    private Integer deductEarthEle = 0;// 土元素

    private Map<Integer, Integer> addedCards = new HashMap<>();// 卡牌

    private Map<Integer, Integer> addedtreasures = new HashMap<>();// 法宝
    private Map<Integer, Integer> deductedTreasures = new HashMap<>();// 扣除法宝

    public void updateCopper(RDCommon rd) {
        if (rd.getAddedCopper() == null) return;
        if (rd.getAddedCopper() > 0) {
            this.addedCopper += rd.getAddedCopper();
        } else {
            this.deductedCopper += rd.getAddedCopper();
        }
    }


    private int add(Integer num1, Integer num2) {
        int int1 = num1 == null ? 0 : num1;
        int int2 = num2 == null ? 0 : num2;
        return int1 + int2;
    }
}
