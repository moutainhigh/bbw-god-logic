package com.bbw.god.game.award;

import java.util.List;

/**
 * @author suchaobin
 * @description 奖品工具
 * @date 2020/9/21 14:09
 **/
public class AwardTool {
    /**
     * 根据awardId添加或者更新award对象，id相同的更新数量，否则添加对象
     *
     * @param totalAwards
     * @param toAddAwards
     */
    public static void addOrUpdateNumById(List<Award> totalAwards, List<Award> toAddAwards) {
        for (Award award : toAddAwards) {
            Award equalAward = totalAwards.stream().filter(a -> a.getItem() == award.getItem()
                    && a.getAwardId().equals(award.getAwardId())).findFirst().orElse(null);
            if (null == equalAward) {
                totalAwards.add(award);
            } else {
                equalAward.setNum(equalAward.getNum() + award.getNum());
            }
        }
    }
}
