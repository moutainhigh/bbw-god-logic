package com.bbw.god.game.transmigration.entity;

import com.bbw.common.DateUtil;
import com.bbw.common.ID;
import com.bbw.common.ListUtil;
import com.bbw.god.game.award.AwardStatus;
import com.bbw.god.game.config.city.CfgCityEntity;
import com.bbw.god.game.config.city.CityTool;
import com.bbw.god.gameuser.UserCfgObj;
import com.bbw.god.gameuser.UserDataType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 轮回城池信息
 *
 * @author: suhq
 * @date: 2021/9/10 4:21 下午
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class UserTransmigrationCity extends UserCfgObj {
    /** 卡组 */
    private List<Integer> cardGroup;
    /** 符册 */
    private Integer fuCe = 0;
    /** 是否打下 */
    private boolean own = false;
    /** 奖励状态 */
    private int[] awardStatus = {AwardStatus.UNAWARD.getValue(), AwardStatus.UNAWARD.getValue(), AwardStatus.UNAWARD.getValue()};
    /** 打下的时间 */
    private Date ownTime;

    public static UserTransmigrationCity getInstance(CfgCityEntity cityEntity, long uid) {
        UserTransmigrationCity userNightMareCity = new UserTransmigrationCity();
        userNightMareCity.setBaseId(cityEntity.getId());
        userNightMareCity.setName(cityEntity.getName());
        userNightMareCity.setId(ID.INSTANCE.nextId());
        userNightMareCity.setGameUserId(uid);
        return userNightMareCity;
    }

    /**
     * 更新占有的相关信息
     */
    public void updateAsOwn() {
        this.own = true;
        this.ownTime = DateUtil.now();
    }

    /**
     * 重置。新的一轮开始后，玩家第一次请求轮回主页信息时重置
     */
    public void reset() {
        ownTime = null;
        own = false;
        cardGroup = null;
        awardStatus = new int[]{AwardStatus.UNAWARD.getValue(), AwardStatus.UNAWARD.getValue(), AwardStatus.UNAWARD.getValue()};
    }

    /**
     * 根据评分更新可领取状态
     *
     * @param score
     */
    public void updateToEnableAward(int score) {
        if (awardStatus[0] == AwardStatus.UNAWARD.getValue()) {
            awardStatus[0] = AwardStatus.ENABLE_AWARD.getValue();
        }
        if (awardStatus[1] == AwardStatus.UNAWARD.getValue() && score >= 70) {
            awardStatus[1] = AwardStatus.ENABLE_AWARD.getValue();
        }
        if (awardStatus[2] == AwardStatus.UNAWARD.getValue() && score >= 80) {
            awardStatus[2] = AwardStatus.ENABLE_AWARD.getValue();
        }
    }

    /**
     * 获取领取状态
     *
     * @param index
     * @return
     */
    public int gainStatus(int index) {
        return awardStatus[index];
    }

    /**
     * 更新状态未已领取
     *
     * @param index
     */
    public void updateToAwarded(int index) {
        awardStatus[index] = AwardStatus.AWARDED.getValue();
    }


    public List<Integer> getCardGroup() {
        if (ListUtil.isEmpty(cardGroup)) {
            cardGroup = new ArrayList<>();
        }
        return cardGroup;
    }

    public CfgCityEntity gainCity() {
        return CityTool.getCityById(this.getBaseId());
    }


    @Override
    public UserDataType gainResType() {
        return UserDataType.USER_TRANSMIGRATION_CITY;
    }


}
