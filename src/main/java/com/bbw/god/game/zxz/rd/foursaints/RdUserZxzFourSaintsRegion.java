package com.bbw.god.game.zxz.rd.foursaints;

import com.bbw.god.game.zxz.cfg.foursaints.CfgFourSaintsEntity;
import com.bbw.god.game.zxz.cfg.foursaints.CfgFourSaintsTool;
import com.bbw.god.game.zxz.entity.foursaints.UserZxzFourSaintsInfo;
import com.bbw.god.rd.RDSuccess;
import lombok.Data;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 返回每个四圣挑战数据
 * @author: hzf
 * @create: 2022-12-28 09:42
 **/
@Data
public class RdUserZxzFourSaintsRegion extends RDSuccess {
    /** 四圣挑战类型 */
    private Integer challengeType;
    /** 是否进入这个区域 */
    private Boolean ifInto;
    /** 进度 */
    private Integer progress;
    /** 词条等级 */
    private Integer entryLv;
    /** 免费刷新次数 */
    private Integer freeRefreshFrequency;
    /** 复活次数 */
    private Integer surviceTimes;
    /** 金创药使用次数 */
    private Integer jinCyTimes;
    /** 限制类型 */
    private List<Integer> limitTypes;

    public static RdUserZxzFourSaintsRegion instance(UserZxzFourSaintsInfo userZxzFourSaintsInfo,List<Integer> limitTypes){
        RdUserZxzFourSaintsRegion fourSaintsRegion = new RdUserZxzFourSaintsRegion();
        fourSaintsRegion.setChallengeType(userZxzFourSaintsInfo.getChallengeType());
        fourSaintsRegion.setIfInto(userZxzFourSaintsInfo.isInto());
        fourSaintsRegion.setProgress(userZxzFourSaintsInfo.getProgress());
        //获取词条等级
        CfgFourSaintsEntity.CfgEntryRandom entryRandom = CfgFourSaintsTool.getEntryRandom(userZxzFourSaintsInfo.getChallengeType());
        Integer lvStock = entryRandom.getLvStock();
        CfgFourSaintsEntity.CfgFourSaintsChallenge fourSaintsChallenge = CfgFourSaintsTool.getFourSaintsChallenge(userZxzFourSaintsInfo.getChallengeType());
        fourSaintsRegion.setEntryLv(lvStock + fourSaintsChallenge.getLingCEntryLv());
        fourSaintsRegion.setFreeRefreshFrequency(userZxzFourSaintsInfo.getFreeRefreshFrequency());
        fourSaintsRegion.setSurviceTimes(userZxzFourSaintsInfo.getSurviceTimes());
        fourSaintsRegion.setJinCyTimes(userZxzFourSaintsInfo.getJinCyTimes());
        fourSaintsRegion.setLimitTypes(limitTypes);
        return fourSaintsRegion;
    }
}
