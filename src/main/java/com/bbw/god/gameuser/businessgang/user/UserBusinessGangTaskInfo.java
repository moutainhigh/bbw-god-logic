package com.bbw.god.gameuser.businessgang.user;

import com.bbw.common.DateUtil;
import com.bbw.common.ID;
import com.bbw.common.SpringContextUtil;
import com.bbw.god.gameuser.UserDataType;
import com.bbw.god.gameuser.UserSingleObj;
import com.bbw.god.gameuser.businessgang.BusinessGangService;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;


/**
 * 玩家商帮任务
 *
 * @author fzj
 * @date 2022/1/14 13:50
 */
@Data
public class UserBusinessGangTaskInfo extends UserSingleObj implements Serializable {
    private static BusinessGangService businessGangService = SpringContextUtil.getBean(BusinessGangService.class);
    private static final long serialVersionUID = 1L;
    /** 总领取任务次数 */
    private Integer totalAwardableNum;
    /** 可领取任务奖励次数 */
    private Integer awardableNum;
    /** 总刷新次数 */
    private Integer totalFreeRefresNum;
    /** 可免费刷新次数 */
    private Integer freeRefreshTaskNum;
    /** 最后一次重置时间 */
    private Date lastResetTime;

    public static UserBusinessGangTaskInfo getInstance(long uid) {
        UserBusinessGangTaskInfo task = new UserBusinessGangTaskInfo();
        task.setId(ID.INSTANCE.nextId());
        task.setGameUserId(uid);
        reset(task);
        task.setLastResetTime(DateUtil.now());
        return task;
    }

    public static void reset(UserBusinessGangTaskInfo gangTask) {
        gangTask.setTotalAwardableNum(5);
        gangTask.setAwardableNum(5);
        gangTask.setTotalFreeRefresNum(3);
        gangTask.setFreeRefreshTaskNum(3);
        gangTask.setLastResetTime(DateUtil.now());
    }

    /**
     * 增加可领奖次数
     *
     * @param addNum
     */
    public void addAwardableNum(int addNum) {
        setAwardableNum(getAwardableNum() + addNum);
    }

    /**
     * 扣除可领奖次数
     *
     * @param delNum
     */
    public void delAwardableNum(int delNum) {
        setAwardableNum(getAwardableNum() - delNum);
    }

    /**
     * 增加刷新次数
     *
     * @param addNum
     */
    public void addFreeRefreshTaskNum(int addNum) {
        setFreeRefreshTaskNum(getFreeRefreshTaskNum() + addNum);
    }

    /**
     * 增加总可领奖次数
     *
     * @param addNum
     */
    public void addTotalAwardableNum(int addNum) {
        setTotalAwardableNum(getTotalAwardableNum() + addNum);
    }

    /**
     * 增加总刷新次数
     *
     * @param addNum
     */
    public void addTotalFreeRefreshTaskNum(int addNum) {
        setTotalFreeRefresNum(getTotalFreeRefresNum() + addNum);
    }

    @Override
    public UserDataType gainResType() {
        return UserDataType.USER_BUSINESS_GANG_TASK;
    }
}
