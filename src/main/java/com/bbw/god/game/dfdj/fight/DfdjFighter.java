package com.bbw.god.game.dfdj.fight;

import com.bbw.common.DateUtil;
import com.bbw.common.ID;
import com.bbw.god.gameuser.UserDataType;
import com.bbw.god.gameuser.UserSingleObj;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * @author suchaobin
 * @description 巅峰对决参与者信息
 * @date 2021/1/5 14:22
 **/
@Data
@EqualsAndHashCode(callSuper = true)
public class DfdjFighter extends UserSingleObj {

    private Integer beanBoughtTimes = 0;//金豆购买次数
    private Date beatBoughtResetDate;
    private Integer joinTimes = 0;//参赛次数
    private Integer winTimes = 0;// 胜利次数
    private Integer streak = 0;// 连胜纪录
    private Integer maxStreak = 0;//最长连胜
    private Date lastGotDate;// 最近一次获取时间
    private Date joinDate;// 加入神仙大会的时间
    private Date lastSprintAwarded;//最近一次领取冲刺奖励

    private Integer eleTimes=0;//获取元素次数
    private Integer initEleTimesDate=20210323;

    public static DfdjFighter instance(long uid) {
        DfdjFighter fighter = new DfdjFighter();
        fighter.setId(ID.INSTANCE.nextId());
        fighter.setGameUserId(uid);
        Date now = DateUtil.now();
        fighter.setLastGotDate(now);
        fighter.setJoinDate(now);
        return fighter;
    }

    /**
     * 新赛季重置
     */
    public void resetForNewSeason() {
        this.joinTimes = 0;
        this.winTimes = 0;
        this.streak = 0;
        this.maxStreak = 0;
    }

    public void addBeanBoughtTimes(int num) {
        this.beanBoughtTimes += num;
    }

    @Override
    public UserDataType gainResType() {
        return UserDataType.DFDJ_FIGHTER;
    }

}

