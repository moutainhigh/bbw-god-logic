package com.bbw.god.game.award;

import com.bbw.common.ListUtil;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 返给客户端的奖励的数据结构
 *
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2018-11-13 17:03
 */
@Data
public class RDAward {
    private Integer awardId = null;// 指定的ID
    private Integer item = null;// 见AwardEnum
    private Integer num = null;// 数量
    private Integer star = null;// 卡牌星级,或者法宝星级
    private Integer type = null;// 属性
    private Integer isNotOwn = null;// 是否未拥有
    private String formats = null;//格式化值

    public static RDAward getInstance(Award award) {
        RDAward rdAward = new RDAward();
        rdAward.setAwardId(award.getAwardId());
        rdAward.setItem(award.getItem());
        rdAward.setNum(award.getNum());
        rdAward.setStar(award.getStar());
        rdAward.setIsNotOwn(award.getIsNotOwn());
        return rdAward;
    }

    public static List<RDAward> getInstances(List<Award> awards) {
        if (ListUtil.isEmpty(awards)) {
            return new ArrayList<>();
        }
        List<RDAward> instances = awards.stream().map(tmp -> getInstance(tmp)).collect(Collectors.toList());
        return instances;
    }

    private RDAward() {
    }

    public RDAward(AwardEnum item, int star, int isNotOwn, int num) {
        this.item = item.getValue();
        this.star = star;
        this.isNotOwn = isNotOwn;
        this.num = num;
    }

    public RDAward(int awardId, AwardEnum item, int num) {
        this.awardId = awardId;
        this.item = item.getValue();
        this.num = num;
    }

    public RDAward(int awardId, int item, int num) {
        this.awardId = awardId;
        this.item = item;
        this.num = num;
    }

    public RDAward(int awardId, int item, int type, int num) {
        this.awardId = awardId;
        this.item = item;
        this.type = type;
        this.num = num;
    }

    public RDAward(AwardEnum item, int num) {
        this.item = item.getValue();
        this.num = num;
    }

}
