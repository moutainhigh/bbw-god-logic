package com.bbw.god.gameuser.businessgang.digfortreasure;

import com.bbw.common.DateUtil;
import com.bbw.common.ID;
import com.bbw.god.gameuser.UserDataType;
import com.bbw.god.gameuser.UserSingleObj;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 玩家挖宝位置信息
 *
 * @author: huanghb
 * @date: 2023/1/18 13:54
 */
@Data
public class UserDigTreasurePos extends UserSingleObj implements Serializable {
    private static final long serialVersionUID = 1L;
    /** 挖宝位置id集合 */
    private List<Integer> digForTreasurePoses = new ArrayList<>();
    /** 楼层奖励信息 */
    private List<FloorAward> foorAwardIdsInfo = new ArrayList<>();
    /** 记录生成时间 */
    private Date dateTime = DateUtil.now();

    /**
     * 玩家挖宝位置信息初始化
     *
     * @param uid
     * @return
     */
    protected static UserDigTreasurePos instance(Long uid) {
        UserDigTreasurePos userDigTreasurePos = new UserDigTreasurePos();
        userDigTreasurePos.setId(ID.INSTANCE.nextId());
        userDigTreasurePos.setGameUserId(uid);
        return userDigTreasurePos;
    }

    /**
     * 随机生成挖宝位置数据
     *
     * @param gameDigTreasure
     */
    public void randomGenerateDigTreasurePosInfo(GameDigTreasure gameDigTreasure) {
        this.digForTreasurePoses = DigTreasureTool.randomAllRoadIds();
        this.foorAwardIdsInfo = gameDigTreasure.getFoorAwardIdsInfo();
        this.dateTime = DateUtil.now();
    }

    /**
     * 获得楼层奖励
     *
     * @param roadId
     * @return
     */
    public Integer[] getFloorAward(int roadId) {
        int posIndex = this.digForTreasurePoses.indexOf(roadId);
        Integer[] floorAwardIds = new Integer[this.foorAwardIdsInfo.size()];
        for (FloorAward floorAward : this.foorAwardIdsInfo) {
            DigTreasureFloorEnum digTreasureFloorEnum = DigTreasureFloorEnum.fromFloor(floorAward.getFloorId());
            List<Integer> awardIds = floorAward.getFloorAwardIds();
            if (posIndex >= awardIds.size()) {
                floorAwardIds[digTreasureFloorEnum.getOrder()] = 0;
                continue;
            }
            floorAwardIds[digTreasureFloorEnum.getOrder()] = awardIds.get(posIndex);
        }
        return floorAwardIds;
    }

    /**
     * 玩家资源类型
     *
     * @return
     */
    @Override
    public UserDataType gainResType() {
        return UserDataType.USER_DIG_TREASURE_POS;
    }
}
