package com.bbw.god.gameuser.treasure;

import com.bbw.common.DateUtil;
import com.bbw.common.ID;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.gameuser.UserData;
import com.bbw.god.gameuser.UserDataType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 玩家道具使用记录
 *
 * @author suhq 2018年9月30日 下午2:15:38
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class UserTreasureRecord extends UserData implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer baseId;//配置ID
    private Integer useTimes;
    private Map<String, Integer> guaranteeProgresses;
    private Integer lastUsePos;// 可用于更新定风珠、醒酒毡使用次数
    private Date lastUseDate;// 最近使用时间
    private WayEnum way = WayEnum.TREASURE_USE;

    /**
     * 实例化  默认初始使用次数为1
     *
     * @param guId
     * @param treasureId
     * @return
     */
    public static UserTreasureRecord instance(long guId, int treasureId) {
        UserTreasureRecord utr = new UserTreasureRecord();
        utr.setId(ID.INSTANCE.nextId());
        utr.setBaseId(treasureId);
        utr.setGameUserId(guId);
        utr.setUseTimes(1);
        utr.setLastUseDate(DateUtil.now());
        return utr;
    }

    /**
     * 使用特定法宝时才实例化
     *
     * @param guId
     * @param treasureId
     * @param pos
     * @return
     */
    public static UserTreasureRecord instance(long guId, int treasureId, int pos) {
        UserTreasureRecord utr = instance(guId, treasureId);
        utr.setLastUsePos(pos);
        return utr;
    }

    public static UserTreasureRecord instance(long guId, int treasureId, int pos, WayEnum way) {
        UserTreasureRecord utr = instance(guId, treasureId);
        utr.setLastUsePos(pos);
        utr.setWay(way);
        return utr;
    }

    public void addTimes() {
        this.useTimes++;
    }

    public void deductTimes() {
        this.useTimes--;
    }

    /**
     * 添加保底记录
     *
     * @param award
     * @param progress
     */
    public void setGuaranteProgress(String award, int progress) {
        if (null == guaranteeProgresses) {
            guaranteeProgresses = new HashMap<>();
        }
        guaranteeProgresses.put(award, progress);
    }

    /**
     * 重置保底
     *
     * @param award
     */
    public void resetGuaranteProgress(String award) {
        if (null == guaranteeProgresses) {
            return;
        }
        guaranteeProgresses.remove(award);
    }

    /**
     * 获得保底进度
     *
     * @param award
     * @return
     */
    public int gainGuaranteProgress(String award) {
        if (null == guaranteeProgresses) {
            return 0;
        }
        Integer value = guaranteeProgresses.getOrDefault(award, 0);
        return value;
    }

    @Override
    public UserDataType gainResType() {
        return UserDataType.TREASURE_RECORD;
    }
}
