package com.bbw.god.game.config;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class WayEnumTest {

    @Test
    public void fromName() {
        WayTool.init();
        List<String> wayNames = Arrays.asList("每日元素包", "每日卡包", "每日体力", "每周跑商", "每周神沙", "上仙礼包", "元宝福袋", "进阶宝袋", "每日任务箱子", "每日商会", "每日商令", "每日仙缘", "每周城建", "每周聚灵", "每周商贸", "神沙大礼包", "小通天残卷礼包", "中通天残卷礼包", "大通天残卷礼包", "超大通天残卷礼包", "升级符箓", "英雄回归巅峰宝箱", "英雄回归每日任务", "开万仙阵宝箱", "每周仙石礼包", "神仙大礼包");
        long start = System.currentTimeMillis();
        for (String wayName : wayNames) {
            WayEnum way = WayEnum.fromName(wayName);
        }
        System.out.println(System.currentTimeMillis() - start);
    }
}