package com.bbw.god.game.zxz.entity.foursaints;

import com.bbw.common.DateUtil;
import com.bbw.common.ID;
import com.bbw.god.game.data.GameData;
import com.bbw.god.game.data.GameDataType;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 四圣挑战要保留的数据
 * @author: hzf
 * @create: 2022-12-26 11:33
 **/
@Data
public class ZxzFourSaintsInfo extends GameData implements Serializable {
    private static final long serialVersionUID = 7073473097670406575L;
    /** 四圣挑战 */
    List<ZxzFourSaints> zxzFourSaintss;
    /** 生成时间 */
    private Long generateTime;
    /** 结束时间 */
    private Long endTime;

    /**
     * 判断是否有效
     * @return
     */
    public boolean ifValid(){
        //获取当前的时间戳
        long currentTime = System.currentTimeMillis();
        if (currentTime >= getGenerateTime() &&currentTime <= getEndTime() ) {
            return true;
        }
        return false;
    }

    /**
     * 判断是否有效
     * @param beginDate
     * @return
     */
    public boolean ifValid(Integer beginDate){
        int beginTime = DateUtil.toDateInt(new Date(getGenerateTime()));
        int endTime = DateUtil.toDateInt(new Date(getEndTime()));
        if (beginDate >= beginTime && beginDate <= endTime ) {
            return true;
        }
        return false;
    }

    /**
     * 获取四圣挑战区域
     * @return
     */
    public ZxzFourSaints gainZxzFourSaintsRegion(Integer challengeType){
        return zxzFourSaintss.stream()
                .filter(tmp -> tmp.getChallengeType().equals(challengeType))
                .findFirst().orElse(null);
    }

    /**
     * 实例化敌方配置
     *
     * @param zxzFourSaintss 四圣挑战数据
     * @return
     */
    public static ZxzFourSaintsInfo instance(List<ZxzFourSaints> zxzFourSaintss) {
        //获取当前周一 0点的时间戳
        long generateTime = DateUtil.getThisWeekBeginDateTime().getTime();
        ZxzFourSaintsInfo zxzFourSaintsInfo = new ZxzFourSaintsInfo();
        zxzFourSaintsInfo.setId(ID.INSTANCE.nextId());
        zxzFourSaintsInfo.setZxzFourSaintss(zxzFourSaintss);
        zxzFourSaintsInfo.setGenerateTime(generateTime);
        //计算七天后的时间
        Date endTime = DateUtil.addDays(new Date(generateTime), 6);
        zxzFourSaintsInfo.setEndTime(endTime.getTime());
        return zxzFourSaintsInfo;
    }


    @Override
    public GameDataType gainDataType() {
        return GameDataType.ZXZ_FOUR_SAINTS;
    }
}
