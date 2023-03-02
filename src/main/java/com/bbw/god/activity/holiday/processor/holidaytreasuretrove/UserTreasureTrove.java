package com.bbw.god.activity.holiday.processor.holidaytreasuretrove;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 玩家藏宝秘境
 *
 * @author: huanghb
 * @date: 2021/12/17 15:28
 */
@Data
public class UserTreasureTrove implements Serializable {
    private static final long serialVersionUID = 1L;
    /*宝藏值*/
    private Integer troveValue = 0;
    /*是否构建大奖*/
    private Boolean isBuildBigAward = false;
    /*奖池*/
    private Integer[] mallIds;

    public static UserTreasureTrove instance(List<CfgTreasureTrove.TroveAward> troveAwards) {
        UserTreasureTrove instance = new UserTreasureTrove();
        instance.refreshMallIds(troveAwards);
        return instance;
    }

    /**
     * 更新奖池
     *
     * @param troveAwards
     */
    public void refreshMallIds(List<CfgTreasureTrove.TroveAward> troveAwards) {
        List<Integer> mallIdList = troveAwards.stream().map(CfgTreasureTrove.TroveAward::getId).collect(Collectors.toList());
        mallIds = new Integer[mallIdList.size()];
        mallIds = mallIdList.toArray(mallIds);
    }

    /**
     * 获得消耗元宝商品购买次数
     *
     * @return
     */
    public int gainBoughtNum() {
        int boughtNum = 0;
        for (int i = 0; i < mallIds.length; i++) {

            if (ifMallGoldBought(i)) {
                boughtNum++;
            }
        }

        return boughtNum;
    }

    /**
     * 购买后更新商品数量
     *
     * @param mallIndex
     */
    public void updateMallToBought(int mallIndex, boolean ifBigAward) {
        mallIds[mallIndex] = ifBigAward ? 1 : 0;
    }

    /**
     * 是否元宝商品购买
     *
     * @param mallIndex
     * @return
     */
    public boolean ifMallGoldBought(int mallIndex) {
        return mallIds[mallIndex] == 0;
    }

    /**
     * 增加藏宝值
     *
     * @param addValue
     */
    public void addTroveValue(int addValue) {
        int troveValueLimit = TreasureTroveTool.getTroveCfg().getTroveValueLimit();
        troveValue += addValue;
        //刷出大奖卡池后再次刷新才会更新isToBuildBigAward
        if (isBuildBigAward) {
            isBuildBigAward = false;
            return;
        }

        if (troveValue > troveValueLimit) {
            isBuildBigAward = true;
            troveValue = 1;
        }
    }

    /**
     * 是否构建大奖
     *
     * @return
     */
    public boolean ifToBuildBigAward() {
        int troveValueLimit = TreasureTroveTool.getTroveCfg().getTroveValueLimit();
        return troveValue > 0 && troveValue % troveValueLimit == 0;
    }

    /**
     * 是否未购买该商品
     *
     * @param mallIndex
     * @return 返回false极为错误参数
     */
    public boolean ifNotBought(Integer mallIndex) {
        return 0 != mallIds[mallIndex] && 1 != mallIds[mallIndex];
    }
}
