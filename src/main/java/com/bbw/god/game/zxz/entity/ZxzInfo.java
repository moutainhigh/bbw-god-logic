package com.bbw.god.game.zxz.entity;

import com.bbw.common.DateUtil;
import com.bbw.common.ID;
import com.bbw.god.game.data.GameData;
import com.bbw.god.game.data.GameDataType;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 诛仙阵敌方配置
 * @author: hzf
 * @create: 2022-09-17 08:57
 **/
@Data
public class ZxzInfo extends GameData implements Serializable {
    private static final long serialVersionUID = 7073473097670406575L;
    /** 难度集合 */
    private List<ZxzDifficulty> difficultys;
    /** 生成时间 */
    private Long generateTime;
    /** 结束时间 */
    private Long endTime;


    /**
     * 实例化敌方配置
     *
     * @param difficultys 难度数据
     * @return
     */
    public static ZxzInfo getInstance(List<ZxzDifficulty> difficultys) {
        //获取当前周一 0点的时间戳
        long generateTime = DateUtil.getThisWeekBeginDateTime().getTime();
        ZxzInfo zxzInfo = new ZxzInfo();
        zxzInfo.setId(ID.INSTANCE.nextId());
        zxzInfo.setDifficultys(difficultys);
        zxzInfo.setGenerateTime(generateTime);
        //计算七天后的时间
        Date endTime = DateUtil.addDays(new Date(generateTime), 6);
        zxzInfo.setEndTime(endTime.getTime());
        return zxzInfo;
    }


    /**
     * 判断是否是有效
     * @return
     */
    public boolean ifValidZxz(){
        //获取当前的时间戳
        long currentTime = System.currentTimeMillis();
        if (currentTime >= getGenerateTime() &&currentTime <= getEndTime() ) {
            return true;
        }
        return false;
    }
    public boolean ifValidZxz(Integer beginDate){
        int beginTime = DateUtil.toDateInt(new Date(getGenerateTime()));
        int endTime = DateUtil.toDateInt(new Date(getEndTime()));
        if (beginDate >= beginTime && beginDate <= endTime ) {
            return true;
        }
        return false;
    }

    /**
     * 拼接时间 ： eg：generateTime-endTime
     * @return
     */
    public String gainSplitTime(){
        int begin = DateUtil.toDateInt(new Date(generateTime));
        int end = DateUtil.toDateInt(new Date(endTime));
        return String.valueOf(begin) + "-" +String.valueOf(end);
    }

    @Override
    public GameDataType gainDataType() {
        return GameDataType.ZXZ;
    }
}
