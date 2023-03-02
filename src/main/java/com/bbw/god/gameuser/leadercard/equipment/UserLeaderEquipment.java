package com.bbw.god.gameuser.leadercard.equipment;

import com.bbw.common.ID;
import com.bbw.god.event.BaseEventParam;
import com.bbw.god.gameuser.UserData;
import com.bbw.god.gameuser.UserDataType;
import com.bbw.god.gameuser.leadercard.event.EPLeaderEquipmentQualityFinish;
import com.bbw.god.gameuser.leadercard.event.LeaderCardEventPublisher;
import lombok.Data;

/**
 * 穿戴的装备
 *
 * @author suhq
 * @date 2021-03-26 15:57
 **/
@Data
public class UserLeaderEquipment extends UserData {
    /** 装备ID */
    private Integer equipmentId;
    /** 等级 */
    private Integer level = 0;
    /** 品质 */
    private Integer quality = 10;
    /** 星图进度 */
    private Integer starMapProgress = 0;
    /** 是否装上 0表示未装备 1表示装备*/
    private Integer isPutOn = 1;

    public static UserLeaderEquipment getInstance(long uid, int equipmentId) {
        UserLeaderEquipment instance = new UserLeaderEquipment();
        instance.setId(ID.INSTANCE.nextId());
        instance.setGameUserId(uid);
        instance.setEquipmentId(equipmentId);
        return instance;
    }


    /**
     * 升级装备
     *
     * @param addLevel
     */
    public void addLevel(int addLevel) {
        level += addLevel;
    }

    /**
     * 升级品阶
     */
    public void addQuality() {
        quality += 10;
        starMapProgress = 0;
    }

    /**
     * 添加星图进度
     */
    public void addStarProgress() {
        starMapProgress++;
        int needStarNum = CfgEquipmentTool.getNeedStarNum();
        if (starMapProgress >= needStarNum) {
            addQuality();
            EPLeaderEquipmentQualityFinish ep = EPLeaderEquipmentQualityFinish.instance(new BaseEventParam(gameUserId), equipmentId, quality);
            LeaderCardEventPublisher.pubLeaderEquipmentQualityFinishEvent(ep);
        }
    }

    /**
     * 扣除星图进度
     */
    public void deductStarProgress() {
        if (starMapProgress == 0) {
            return;
        }
        starMapProgress--;
    }

    @Override

    public UserDataType gainResType() {
        return UserDataType.USER_LEADER_EQUIPMENT;
    }
}
