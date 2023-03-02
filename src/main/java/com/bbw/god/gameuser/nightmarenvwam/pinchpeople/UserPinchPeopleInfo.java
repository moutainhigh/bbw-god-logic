package com.bbw.god.gameuser.nightmarenvwam.pinchpeople;

import com.bbw.common.ID;
import com.bbw.god.gameuser.UserDataType;
import com.bbw.god.gameuser.UserSingleObj;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 玩家捏土造人数据
 *
 * @author fzj
 * @date 2022/5/4 11:11
 */
@Data
public class UserPinchPeopleInfo extends UserSingleObj implements Serializable {
    private static final long serialVersionUID = 1L;
    /** 每日捐赠总铜钱 */
    private Long dayDonateTotalCopper = 0L;
    /** 捐献时间 */
    private Date lastDonateTime;
    /** 每日捏人评分 */
    private List<Integer> dayPinchPeopleScore = new ArrayList<>();
    /** 累计评分 */
    private Integer pinchPeopleTotalScore = 0;
    /** 泥人进度值 */
    private Integer progressToPinchPeople = 0;
    /** 捏人时间 */
    private Date pinchPeopleTime;


    public static UserPinchPeopleInfo getInstance(long uid) {
        UserPinchPeopleInfo userPinchPeopleInfo = new UserPinchPeopleInfo();
        userPinchPeopleInfo.setId(ID.INSTANCE.nextId());
        userPinchPeopleInfo.setGameUserId(uid);
        return userPinchPeopleInfo;
    }

    /**
     * 加每日捐赠铜钱
     *
     * @param addValue
     */
    public void addDayDonateTotalCopper(long addValue) {
        this.dayDonateTotalCopper += addValue;
    }

    /**
     * 加泥人进度值
     *
     * @param addValue
     */
    public void addClayFigurineValue(int addValue) {
        this.progressToPinchPeople += addValue;
    }

    /**
     * 扣除泥人进度值
     *
     * @param delValue
     */
    public void delClayFigurineValue(int delValue) {
        this.progressToPinchPeople -= delValue;
    }

    /**
     * 加累计评分
     *
     * @param addValue
     */
    public void addKneadSoilTotalScore(int addValue) {
        this.pinchPeopleTotalScore += addValue;
    }

    /**
     * 加累计评分
     *
     * @param delValue
     */
    public void delKneadSoilTotalScore(int delValue) {
        this.pinchPeopleTotalScore -= delValue;
    }

    @Override
    public UserDataType gainResType() {
        return UserDataType.USER_KNEAD_SOIL_INFO;
    }
}
