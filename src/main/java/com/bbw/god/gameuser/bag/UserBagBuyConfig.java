package com.bbw.god.gameuser.bag;

/**
 * @author suchaobin
 * @description 玩家背包购买配置类
 * @date 2020/11/27 15:11
 **/
public class UserBagBuyConfig {
    /** 购买上限 */
    public static final int BUY_LIMIT = 30;
    /** 需要花费的元宝配置二维数组，例如{600, 6, 10} 表示购买第6-10个格子的时候，需要花费600元宝 */
    private static int[][] array = {{300, 1, 5}, {600, 6, 10}, {900, 11, 15}, {1200, 16, 20}, {1500, 21, 25}, {1800, 26, 30}};

    /**
     * 获取对应购买次数所需要的元宝数量
     *
     * @param buyTimes 购买次数
     * @return
     */
    public static int getNeedPayGold(int buyTimes) {
        // 根据购买次数返回对应需要花费的元宝数量
        for (int[] arr : array) {
            if (in(arr[1], arr[2], buyTimes)) {
                return arr[0];
            }
        }
        return array[array.length - 1][0];
    }

    /**
     * 是否在区间范围内
     *
     * @param value
     * @param min
     * @param max
     * @return
     */
    private static boolean in(int min, int max, int value) {
        return value >= min && value <= max;
    }
}
