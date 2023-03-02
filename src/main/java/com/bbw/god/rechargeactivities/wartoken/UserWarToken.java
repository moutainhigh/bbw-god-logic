package com.bbw.god.rechargeactivities.wartoken;

import com.bbw.common.DateUtil;
import com.bbw.common.ID;
import com.bbw.god.gameuser.UserDataType;
import com.bbw.god.gameuser.UserSingleObj;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 说明：玩家战令数据
 *
 * @author lwb
 * date 2021-06-02
 */
@Data
public class UserWarToken extends UserSingleObj {
    /**
     * 战令活动ID
     */
    private Long activityId;
    /**
     * 战令进阶
     */
    private Integer supToken =0;
    /**
     * 经验
     */
    private Integer exp=0;
    /**
     * 每周任务经验。只记录上限部分
     */
    private Integer weekTaskExp=0;
    /**
     * 初始化时间
     */
    private Integer initDate;
    private Integer refreshTimes=0;

    private List<Integer> gainedBaseLevelAwards=new ArrayList<>();


    private List<Integer> gainedAllAwards=new ArrayList<>();

    public static UserWarToken getInstance(long uid,long activityId){
        UserWarToken userWarToken=new UserWarToken();
        userWarToken.setGameUserId(uid);
        userWarToken.setId(ID.INSTANCE.nextId());
        userWarToken.setInitDate(DateUtil.getTodayInt());
        userWarToken.setActivityId(activityId);
        return userWarToken;
    }

    @Override
    public UserDataType gainResType() {
        return UserDataType.USER_WAR_TOKEN;
    }

    /**
     * 每级恒定2000点经验。
     * @return
     */
    @JsonIgnore
    public int getLevel(){
        return exp/WarTokenTool.getUpLevelNeedExp();
    }

    /**
     * 获取当前经验
     * @return
     */
    @JsonIgnore
    public int getCurrentLevelExp(){
        return exp%WarTokenTool.getUpLevelNeedExp();
    }

    /**
     * 初始化周数据
     */
    public void initWeekData(){
        this.setWeekTaskExp(0);
        this.setInitDate(DateUtil.getTodayInt());
        this.setRefreshTimes(0);
    }

    /**
     * 初始化
     */
    public void init(long activityId){
        this.setWeekTaskExp(0);
        this.setInitDate(DateUtil.getTodayInt());
        this.setRefreshTimes(0);
        this.setSupToken(0);
        this.setActivityId(activityId);
        this.setGainedAllAwards(new ArrayList<>());
        this.setGainedBaseLevelAwards(new ArrayList<>());
        this.setExp(0);
    }

    /**
     * 增加经验
     * @param addExp
     */
    public void addExp(int addExp){
        if (addExp<0){
            return;
        }
        this.exp+=addExp;
        this.exp=Math.min(this.exp,WarTokenTool.getFullLevelNeedExp());
    }
    public boolean ifOnlyGainedBaseLevelAward(int lv){
        return gainedBaseLevelAwards.contains(lv);
    }

    public boolean ifGainedAward(int lv){
        return gainedAllAwards.contains(lv);
    }

    public void addGainedLevelAward(Integer lv,boolean isBaseLevelAward){
        if (isBaseLevelAward){
            gainedBaseLevelAwards.add(lv);
        }else {
            gainedBaseLevelAwards.remove(lv);
            gainedAllAwards.add(lv);
        }
    }
}
