package com.bbw.god.city.miaoy.hexagram;

import com.bbw.god.city.event.CityEventPublisher;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.treasure.UserTreasureRecordService;
import com.bbw.god.road.RoadEventPublisher;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 卦象
 * @author liuwenbin
 */
public abstract class AbstractHexagram {
    @Autowired
    protected GameUserService gameUserService;
    @Autowired
    protected HexagramBuffService hexagramBuffService;
    @Autowired
    private UserTreasureRecordService userTreasureRecordService;
    /**
     * 获取卦象ID
     * @return
     */
    public abstract int getHexagramId();

    /**
     * 卦象级别
     * @return
     */
    public abstract HexagramLevelEnum getHexagramLevel();
    /**
     * 是否可以生效该卦象
     * @param uid
     * @return
     */
    public boolean canEffect(long uid){
        return true;
    }

    /**
     * 获取途径
     * @return
     */
    public WayEnum getWay(){
        return WayEnum.HEXAGRAM;
    }
    /**
     * 卦象生效
     * @return
     */
    public abstract void effect(long uid,RDHexagram rd);

    /**
     * 添加卦象BUFF
     */
    public void addHexagramBuff(long uid,int effectTimes,RDHexagram rd){
        hexagramBuffService.addHexagramBuff(uid,getHexagramId(),effectTimes);
    }

    /**
     * 传送到达
     * @param uid
     * @param position
     * @param rd
     */
    public void arrive(long uid,int position,RDHexagram rd){
        // 到达
        CityEventPublisher.publCityArriveEvent(uid, position, WayEnum.NONE, rd);
        // 行走触发格子事件（界碑）
        RoadEventPublisher.publishRoadEvent(uid, position, WayEnum.TREASURE_USE, rd);
        // 重置定风珠、醒酒毡使用记录
        userTreasureRecordService.resetTreasureRecordAsNewPos(uid, WayEnum.TREASURE_USE);
    }
}
