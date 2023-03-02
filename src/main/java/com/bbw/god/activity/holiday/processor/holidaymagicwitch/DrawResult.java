package com.bbw.god.activity.holiday.processor.holidaymagicwitch;

import lombok.Data;

import java.util.List;

/**
 * 抽奖结果
 *
 * @author: huanghb
 * @date: 2022/12/13 9:40
 */
@Data
public class DrawResult {
    // 排序集合
    private List<Integer> result;

    public static DrawResult getInstance(List<Integer> result) {
        DrawResult instance = new DrawResult();
        instance.setResult(result);
        return instance;
    }
}
