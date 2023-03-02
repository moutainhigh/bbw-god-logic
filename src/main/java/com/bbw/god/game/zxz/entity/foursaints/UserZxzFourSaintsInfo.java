package com.bbw.god.game.zxz.entity.foursaints;

import com.bbw.common.DateUtil;
import com.bbw.common.ID;
import com.bbw.god.game.zxz.cfg.foursaints.CfgFourSaintsTool;
import com.bbw.god.game.zxz.enums.ZxzDefenderEnum;
import com.bbw.god.game.zxz.enums.ZxzStatusEnum;
import com.bbw.god.gameuser.UserData;
import com.bbw.god.gameuser.UserDataType;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 玩家四圣挑战数据
 * @author: hzf
 * @create: 2022-12-27 16:40
 **/
@Data
public class UserZxzFourSaintsInfo extends UserData implements Serializable {
    private static final long serialVersionUID = 7073473097670406575L;

    /** 四圣挑战类型 */
    private Integer challengeType;
    /** 每周第一次通关  true:已经通过*/
    private Boolean weeklyFirstClearance;
    /** 探索点 */
    private Integer exploratoryPoint;
    /** 复活次数 */
    private Integer surviceTimes;
    /** 金创药使用次数 */
    private Integer jinCyTimes;
    /** 诛仙阵：状态信息：参考 ZxzStatusEnum 枚举类 */
    private Integer status;
    /** 是否进入区域中，进入区域不可以扫荡 */
    private boolean into;
    /** 进度 */
    private Integer progress;
    /** 关卡数据 */
    private List<UserFourSaintsDefender> fourSaintsDefenders;
    /** 是否领取宝箱 0:不可领取，1：可领取，2：已经被领取 */
    private Integer awarded;
    /** 是否通关过 */
    private Boolean ifClearance;
    /** 最近一次刷新时间 */
    private Date lastRefreshDate = new Date();
    /** 免费刷新次数 */
    private Integer freeRefreshFrequency;
    /** 首次使用四圣挑战令 每天零点刷新 */
    private Date freeRefreshDate = new Date();


    public boolean ifAttack(){
        return isInto();
    }

    public static UserZxzFourSaintsInfo instance(long uid,Integer challengeType){
        UserZxzFourSaintsInfo userZxzFourSaintsInfo = new UserZxzFourSaintsInfo();
        userZxzFourSaintsInfo.setId(ID.INSTANCE.nextId());
        userZxzFourSaintsInfo.setGameUserId(uid);
        userZxzFourSaintsInfo.setChallengeType(challengeType);
        userZxzFourSaintsInfo.setWeeklyFirstClearance(false);
        userZxzFourSaintsInfo.setExploratoryPoint(0);
        userZxzFourSaintsInfo.setFourSaintsDefenders(new ArrayList<>());
        userZxzFourSaintsInfo.setSurviceTimes(0);
        userZxzFourSaintsInfo.setJinCyTimes(0);
        userZxzFourSaintsInfo.setInto(false);
        userZxzFourSaintsInfo.setProgress(0);
        userZxzFourSaintsInfo.setAwarded(0);
        userZxzFourSaintsInfo.setFreeRefreshFrequency(CfgFourSaintsTool.getCfg().getFreeRefreshFrequency());
        userZxzFourSaintsInfo.setIfClearance(false);
        userZxzFourSaintsInfo.setLastRefreshDate(new Date());
        userZxzFourSaintsInfo.setFreeRefreshDate(new Date());
        List<UserFourSaintsDefender> userFourSaintsDefenders = UserFourSaintsDefender.initFourSaintsDefender(challengeType);
        userZxzFourSaintsInfo.setFourSaintsDefenders(userFourSaintsDefenders);
        return userZxzFourSaintsInfo;
    }

    /**
     * 获取关卡
     * @param defenderId
     * @return
     */
    public UserFourSaintsDefender gainFourSaintsDefender(Integer defenderId) {
        return fourSaintsDefenders.stream()
                .filter(defender -> defender.getDefenderId().equals(defenderId))
                .findFirst().orElse(null);

    }

    /**
     * 是否达到四个探索点
     * @return
     */
    public boolean ifUpToFourExploratoryPoint(){
       return 4 == exploratoryPoint;
    }


    /**
     * 添加探索点
     */
    public void addExploratoryPoint(){
        if (4 == exploratoryPoint) {
            exploratoryPoint = 4;
            return;
        }
        exploratoryPoint++;
        changeBoxStatus(exploratoryPoint);
    }

    /**
     * 更改宝箱状态
     * @param exploratoryPoint
     */
    public void changeBoxStatus(Integer exploratoryPoint){
        if (exploratoryPoint == 4) {
            awarded = 1;
        }
    }




    /**
     * 添加金创药使用次数
     */
    public void addJinCyTime(){
        jinCyTimes++;
    }

    /**
     * 添加复活次数
     */
    public void addSurviceTimes(){
        surviceTimes++;
    }

    /**
     * 减少免费刷新次数
     */
    public void reduceFreeRefreshFrequency(){
        if (0 >= freeRefreshFrequency) {
            freeRefreshFrequency = 0;
            return;
        }
        freeRefreshFrequency--;
    }

    /**
     * 自动刷新
     */
    public void autoRefreshFourSaints(){
        refreshFourSaints();
        lastRefreshDate = new Date();
        weeklyFirstClearance = false;
    }

    /**
     * 每天刷新 首次使用四圣挑战令 每天零点刷新
     */
    public void autoFreeTime(){
        //每天零点刷新
        freeRefreshDate = new Date();
        freeRefreshFrequency = CfgFourSaintsTool.getCfg().getFreeRefreshFrequency();
    }

    /**
     * 每周首胜
     */
    public void firstWinEveryWeek(){
        weeklyFirstClearance = true;
    }

    /**
     * 通关
     */
    public void clearance(){
        ifClearance = true;
    }

    /**
     * 奖励是否已经领取
     * @return
     */
    public boolean ifAwarded(){
        return 2 == awarded;
    }

    /**
     * 领取宝箱
     */
    public void receiveBox() {
        awarded = 2;
    }


    /**
     * 刷新
     */
    public void refreshFourSaints(){
        List<UserFourSaintsDefender> fourSaintsDefenderList = this.fourSaintsDefenders;
        //处理关卡数据
        for (UserFourSaintsDefender fourSaintsDefender : fourSaintsDefenderList) {
            Integer defender = CfgFourSaintsTool.defender(fourSaintsDefender.getDefenderId());
            if (defender == ZxzDefenderEnum.DEFENDER_1.getDefenderId()) {
                fourSaintsDefender.setStatus(ZxzStatusEnum.ABLE_ATTACK.getStatus());
            }else {
                fourSaintsDefender.setStatus(ZxzStatusEnum.NOT_OPEN.getStatus());
            }
        }

        progress = 0;
        surviceTimes = 0;
        jinCyTimes = 0;
        into = false;
        status = ZxzStatusEnum.ABLE_ATTACK.getStatus();
        this.fourSaintsDefenders = fourSaintsDefenderList;
    }

    /**
     * 获取攻打进度
     * @return
     */
    public Integer gainProgress(){
        int sum = 0;
        for (UserFourSaintsDefender defender : fourSaintsDefenders) {
            if (defender.getStatus() == ZxzStatusEnum.PASSED.getStatus()) {
                sum += 1;
            }
        }
        return sum;
    }



    @Override
    public UserDataType gainResType() {
        return UserDataType.USER_ZXZ_FOUR_SAINTS;
    }
}
