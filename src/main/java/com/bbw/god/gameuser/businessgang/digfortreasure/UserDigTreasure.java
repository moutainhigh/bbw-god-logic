package com.bbw.god.gameuser.businessgang.digfortreasure;

import com.bbw.common.DateUtil;
import com.bbw.common.ID;
import com.bbw.god.gameuser.UserCfgObj;
import com.bbw.god.gameuser.UserDataType;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 玩家挖宝信息
 *
 * @author: huanghb
 * @date: 2022/1/18 8:42
 */
@Data
public class UserDigTreasure extends UserCfgObj implements Serializable {
    private static final long serialVersionUID = 1L;
    /** 挖宝奖励 */
    private Integer[] digForTreasureAwards;
    /** 挖宝状态 */
    private Integer[] digTreasureStatus;
    /** 挖宝结果奖励id集合 */
    private Integer[] resultAwardIds;
    /** 记录生成时间 */
    private Date dateTime;

    /**
     * 玩家挖宝信息初始化
     *
     * @param uid
     * @param roadId
     * @param floorAwardIds
     * @return
     */
    protected static UserDigTreasure instance(Long uid, Integer roadId, Integer[] floorAwardIds) {
        UserDigTreasure userDigTreasure = new UserDigTreasure();
        userDigTreasure.setId(ID.INSTANCE.nextId());
        userDigTreasure.setGameUserId(uid);
        userDigTreasure.setBaseId(roadId);
        userDigTreasure.setDateTime(DateUtil.now());
        userDigTreasure.setDigForTreasureAwards(floorAwardIds);
        userDigTreasure.setDigTreasureStatus(new Integer[]{0, 0, 0});
        userDigTreasure.setResultAwardIds(new Integer[]{0, 0, 0});
        return userDigTreasure;
    }

    /**
     * 挖宝
     *
     * @return
     */
    protected void digTreasure() {
        this.digTreasureStatus[getCurrentFloor()] = DigTreasureStatusEnum.DUG_TREASURE.getDigTreasureStatus();
    }

    /**
     * 当前挖宝层数
     *
     * @return
     */
    protected Integer getCurrentFloor() {
        return this.digTreasureStatus[0] + this.digTreasureStatus[1] + this.digTreasureStatus[2];
    }

    /**
     * 是否可以挖
     *
     * @return
     */
    protected boolean ifCanDig() {
        return this.digTreasureStatus[getCurrentFloor()] != DigTreasureStatusEnum.DUG_TREASURE.getDigTreasureStatus();
    }

    /**
     * 是否挖完
     *
     * @return
     */
    protected boolean isDugAll() {
        if (getCurrentFloor() == this.digTreasureStatus.length) {
            return true;
        }
        return false;
    }

    /**
     * 获得当前楼层宝藏
     *
     * @param currentFloor
     * @return
     */
    protected Integer getCurrentFloorTreasureTroveId(int currentFloor) {
        return this.digForTreasureAwards[currentFloor];
    }

    /**
     * 获得当前位置所有已挖掘宝藏id
     *
     * @return
     */
    protected List<Integer> getCurrentPosAllDugTreasureTroveIds() {
        List<Integer> dugTreasureTroveIds = new ArrayList<>();
        for (int i = 0; i < this.digTreasureStatus.length; i++) {
            if (this.digTreasureStatus[i] != DigTreasureStatusEnum.DUG_TREASURE.getDigTreasureStatus()) {
                dugTreasureTroveIds.add(0);
                continue;
            }
            dugTreasureTroveIds.add(this.digForTreasureAwards[i]);
        }
        return dugTreasureTroveIds;
    }

    protected void updateResultId(int pos, int awardId) {
        if (null == this.resultAwardIds) {
            this.resultAwardIds = new Integer[]{0, 0, 0};
        }
        this.resultAwardIds[--pos] = awardId;
    }

    /**
     * 玩家资源类型
     *
     * @return
     */
    @Override
    public UserDataType gainResType() {
        return UserDataType.USER_DIG_TREASURE;
    }
}
