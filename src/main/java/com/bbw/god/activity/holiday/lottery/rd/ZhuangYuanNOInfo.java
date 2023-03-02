package com.bbw.god.activity.holiday.lottery.rd;

import com.bbw.god.activity.holiday.lottery.service.bocake.WangZhongWang;
import com.bbw.god.activity.holiday.lottery.service.bocake.WangZhongWangLevelEnum;
import lombok.Data;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author suchaobin
 * @description 状元奖券信息
 * @date 2020/9/28 15:53
 **/
@Data
public class ZhuangYuanNOInfo {
    private String number;
    private Integer resultLevel;

    public ZhuangYuanNOInfo(String number) {
        this.number = number;
    }

    public ZhuangYuanNOInfo(String number, Integer resultLevel) {
        this.number = number;
        this.resultLevel = resultLevel;
    }

    /**
     * 获取我的王中王中将记录
     *
     * @param wzw
     * @param zhuangYuanNOList
     * @return
     */
    public static List<ZhuangYuanNOInfo> getMyZhuangYuanInfos(WangZhongWang wzw, List<String> zhuangYuanNOList) {
        List<ZhuangYuanNOInfo> infos = new ArrayList<>();
        for (String number : zhuangYuanNOList) {
            int level = WangZhongWangLevelEnum.PARTICIPATE.getValue();
            if (wzw.getFirstPrize().contains(number)) {
                level = WangZhongWangLevelEnum.FIRST.getValue();
            } else if (wzw.getSecondPrize().contains(number)) {
                level = WangZhongWangLevelEnum.SECOND.getValue();
            } else if (wzw.getThirdPrize().contains(number)) {
                level = WangZhongWangLevelEnum.THIRD.getValue();
            } else if (wzw.getFourthPrize().contains(number)) {
                level = WangZhongWangLevelEnum.FOURTH.getValue();
            }
            infos.add(new ZhuangYuanNOInfo(number, level));
        }
        return infos.stream().sorted(Comparator.comparing(ZhuangYuanNOInfo::getResultLevel)).collect(Collectors.toList());
    }
}
