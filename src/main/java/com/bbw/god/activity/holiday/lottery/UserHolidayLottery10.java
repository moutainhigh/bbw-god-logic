package com.bbw.god.activity.holiday.lottery;

import com.bbw.common.CloneUtil;
import com.bbw.common.ID;
import com.bbw.god.activity.holiday.lottery.rd.RDHolidayLotteryAward;
import com.bbw.god.game.award.Award;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author suchaobin
 * @description 类型=10的玩家节日抽奖
 * @date 2020/9/17 14:04
 **/
@Data
@EqualsAndHashCode(callSuper = true)
public class UserHolidayLottery10 extends BaseUserHolidayLottery implements Serializable {
    private static final long serialVersionUID = -285483481165355828L;
    // 用于展示给客户端的奖品集合
    private List<RDHolidayLotteryAward> awards = new ArrayList<>();
    // 初始化时的所有奖品id集合
    private List<Integer> lotteryIds = new ArrayList<>();
    // 未领取的下标集合，对应CfgHolidayLotteryAwards的id
    private List<Integer> remainIndexList = new ArrayList<>();
    // 已经领取的下标集合
    private List<Integer> awardedIndexList = new ArrayList<>();
    // 刷新次数
    private Integer refreshTimes = 0;

    public static UserHolidayLottery10 getInstance(long uid, List<Integer> ids) {
        UserHolidayLottery10 userHolidayLottery = new UserHolidayLottery10();
        userHolidayLottery.setId(ID.INSTANCE.nextId());
        userHolidayLottery.setGameUserId(uid);
        List<Integer> remainIndexList = new ArrayList<>();
        for (int i = 0; i < ids.size(); i++) {
            remainIndexList.add(i);
        }
        userHolidayLottery.setRemainIndexList(remainIndexList);
        userHolidayLottery.setLotteryIds(ids);
        // 洗牌
        List<Integer> list = new ArrayList<>(ids);
        Collections.shuffle(list);
        for (Integer id : list) {
            CfgHolidayLotteryAwards cfgHolidayLotteryAwards = HolidayLotteryTool.getById(id);
            List<Award> awards = CloneUtil.clone(cfgHolidayLotteryAwards).getAwards();
            List<RDHolidayLotteryAward> awardList = awards.stream().map(s ->
                    RDHolidayLotteryAward.getInstance(s, false)).collect(Collectors.toList());
            userHolidayLottery.getAwards().addAll(awardList);
        }
        return userHolidayLottery;
    }

    public void refresh(List<Integer> ids, List<Integer> showIds) {
        this.setLotteryIds(ids);
        List<Integer> remainIndexList = new ArrayList<>();
        for (int i = 0; i < ids.size(); i++) {
            remainIndexList.add(i);
        }
        if (9 == this.awardedIndexList.size()) {
            this.refreshTimes = 0;
        } else {
            this.refreshTimes++;
        }
        this.setRemainIndexList(remainIndexList);
        this.setAwardedIndexList(new ArrayList<>());
        List<RDHolidayLotteryAward> awards = new ArrayList<>();
        for (Integer id : showIds) {
            CfgHolidayLotteryAwards cfgHolidayLotteryAwards = HolidayLotteryTool.getById(id);
            List<Award> awardList = CloneUtil.clone(cfgHolidayLotteryAwards).getAwards();
            awards.addAll(awardList.stream().map(s -> RDHolidayLotteryAward.getInstance(s, false)).collect(Collectors.toList()));
        }
        this.setAwards(awards);
    }

    /**
     * 抽奖
     *
     * @param index 奖品下标
     */
    public void draw(int index) {
        this.remainIndexList.remove((Integer) index);
        this.awardedIndexList.add(index);
        Integer cfgId = this.lotteryIds.get(index);
        Integer awardId = HolidayLotteryTool.getById(cfgId).getAwards().get(0).getAwardId();
        // 修改状态
        this.awards.stream().filter(tmp -> tmp.getAwardId().equals(awardId) && !tmp.getIsAwarded()).findFirst()
                .ifPresent(tmp -> tmp.setIsAwarded(true));
    }

    public List<RDHolidayLotteryAward> gainAwardedLotteryAwards() {
        List<RDHolidayLotteryAward> awardedLotteryAwards = new ArrayList<>();
        for (int i = 0; i < this.awardedIndexList.size(); i++) {
            Integer index = this.awardedIndexList.get(i);
            Integer cfgId = this.lotteryIds.get(index);
            Award award = HolidayLotteryTool.getById(cfgId).getAwards().get(0);
            RDHolidayLotteryAward instance = RDHolidayLotteryAward.getInstance(award, index, true);
            awardedLotteryAwards.add(instance);
        }
        return awardedLotteryAwards;
    }
}
