package com.bbw.god.activity.holiday.lottery;

import lombok.Data;

import java.util.List;

/**
 * 五气朝元抽奖结果
 *
 * @author: huanghb
 * @date: 2022/5/12 13:50
 */
@Data
public class DrawResult {
    // 珠子排序集合
    private List<Integer> result;

    public static DrawResult getInstance(List<Integer> result) {
        DrawResult instance = new DrawResult();
        instance.setResult(result);
        return instance;
    }
}
