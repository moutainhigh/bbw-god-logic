package com.bbw.util;

import com.bbw.common.PowerRandom;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-01-08 10:32
 */
public class TestArrayList {

    @Test
    public void test() {
        int diceNum = 3;
        int remainNum = 10;
        for (int i = 0; i < 1000; i++) {
            splitNum(diceNum, remainNum);
        }
    }

    public void splitNum(int diceNum, int pathLength) {
        List<Integer> diceResult = Stream.generate(() -> 1).limit(diceNum).collect(Collectors.toList());
        for (int i = 0; i < pathLength - diceNum; i++) {
            Integer value = 0;
            int index = 0;
            do {
                index = PowerRandom.getRandomBySeed(diceNum) - 1;
                value = diceResult.get(index);
            } while (value >= 6);
            diceResult.set(index, value + 1);
        }
        if (diceResult.contains(0)) {
            System.out.println(diceResult);
        }
    }

    private static final Integer PAGE_SIZE = 3;

    @Test
    public void main() {

        List<String> ygWins = Stream.of("0,0,0,0,0".split(",")).collect(Collectors.toList());

        ygWins.add("0");
        List<Long> datas = Arrays.asList(new Long[]{1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L});

        //总记录数
        Integer totalCount = datas.size();

        //分多少次处理
        Integer requestCount = totalCount / PAGE_SIZE;

        for (int i = 0; i <= requestCount; i++) {
            Integer fromIndex = i * PAGE_SIZE;
            //如果总数少于PAGE_SIZE,为了防止数组越界,toIndex直接使用totalCount即可
            int toIndex = Math.min(totalCount, (i + 1) * PAGE_SIZE);
            List<Long> subList = datas.subList(fromIndex, toIndex);
            System.out.println(subList);
            //总数不到一页或者刚好等于一页的时候,只需要处理一次就可以退出for循环了
            if (toIndex == totalCount) {
                break;
            }
        }

    }
}
