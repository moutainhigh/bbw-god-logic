package com.bbw.god.activity.holiday.lottery.service.bocake;

/**
 * 王中王
 *
 * @author: huanghb
 * @date: 2022/1/11 10:23
 */

import com.bbw.common.PowerRandom;
import com.bbw.common.SetUtil;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 王中王抽奖
 *
 * @author: huanghb
 * @date: 2022/1/11 10:06
 */
@Data
@AllArgsConstructor
public class WangZhongWang {
    private List<String> firstPrize;
    private List<String> secondPrize;
    private List<String> thirdPrize;
    private List<String> fourthPrize;

    /**
     * 初始化王中王
     *
     * @param zhuangYuanMap
     * @return
     */
    public static WangZhongWang instanceWangZhongWang(Map<String, Long> zhuangYuanMap) {
        Set<String> zhuangYuanNOSet = zhuangYuanMap.keySet();
        List<String> firstPrizeList = getZhuangYuanNOList(zhuangYuanNOSet, 1);
        List<String> secondPrizeList = getZhuangYuanNOList(zhuangYuanNOSet, 2);
        List<String> thirdPrizeList = getZhuangYuanNOList(zhuangYuanNOSet, 6);
        List<String> fourthPrizeList = getZhuangYuanNOList(zhuangYuanNOSet, 8);
        return new WangZhongWang(firstPrizeList, secondPrizeList, thirdPrizeList, fourthPrizeList);
    }

    /**
     * 获得状元奖券清单
     *
     * @param zhuangYuanNOSet
     * @param num
     * @return
     */
    private static List<String> getZhuangYuanNOList(Set<String> zhuangYuanNOSet, int num) {
        List<String> numberList = new ArrayList<>();
        if (SetUtil.isEmpty(zhuangYuanNOSet)) {
            return numberList;
        }
        int finalNum = Math.min(zhuangYuanNOSet.size(), num);
        numberList = new ArrayList<>(PowerRandom.getRandomsFromSet(zhuangYuanNOSet, finalNum));
        zhuangYuanNOSet.removeAll(numberList);
        return numberList;
    }
}