package com.bbw.god.gameuser.businessgang.digfortreasure;

import com.bbw.common.DateUtil;
import com.bbw.common.ID;
import com.bbw.god.game.data.GameData;
import com.bbw.god.game.data.GameDataType;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 全服挖宝对象
 *
 * @author: huanghb
 * @date: 2022/12/28 15:09
 */
@Data
public class GameDigTreasure extends GameData {
    /** 区服组id */
    private Integer serverGroup;
    /** 开始时间 */
    private Date beginDateTime;
    /** 楼层奖励集合信息 */
    private List<FloorAward> foorAwardIdsInfo;


    /**
     * 初始化
     *
     * @param serverGroup
     * @param floorAwardInfos
     * @return
     */
    public static GameDigTreasure getInstance(int serverGroup, List<FloorAward> floorAwardInfos, Date beginDateTime) {
        GameDigTreasure gameDigTreasure = new GameDigTreasure();
        gameDigTreasure.setId(ID.INSTANCE.nextId());
        gameDigTreasure.setServerGroup(serverGroup);
        gameDigTreasure.setFoorAwardIdsInfo(floorAwardInfos);
        gameDigTreasure.setBeginDateTime(beginDateTime);
        return gameDigTreasure;
    }

    /**
     * 区服组是否匹配
     *
     * @param serverGroup
     * @return
     */
    public boolean isMatchServerGroup(int serverGroup) {
        return this.serverGroup == serverGroup;
    }

    /**
     * 是否同一周
     *
     * @param generationDateTime 生成时间
     * @return
     */
    public boolean isMatchWeek(Date generationDateTime) {
        Date generationTimeReduceHours = DateUtil.addHours(generationDateTime, -DigTreasureTool.getWeekBeginHour());
        return DateUtil.isEqualWeek(generationTimeReduceHours, this.beginDateTime);
    }


    /**
     * 资源类型的字符串
     *
     * @return
     */
    @Override
    public GameDataType gainDataType() {
        return GameDataType.GAME_DIG_TREASURE;
    }
}
