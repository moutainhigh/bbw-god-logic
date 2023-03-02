package com.bbw.god.game.wanxianzhen;

import com.bbw.common.DateUtil;
import com.bbw.common.ID;
import com.bbw.god.gameuser.UserCfgObj;
import com.bbw.god.gameuser.UserDataType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lwb
 * @date 2020/4/22 14:51
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class UserWanXian extends UserCfgObj implements Serializable {
    private static final long serialVersionUID = 1126125793677847130L;
    private boolean showLogMenu=false;//是否显示战报图标
    //常规赛赛季
    private Integer regularRace = 0;
    //常规赛卡组 ID集
    private List<WanXianCard> raceCards = null;
    //今日积分
    private Integer todayScore = 0;
    //基础分
    private Integer baseScore = 0;
    //连胜次数
    private Integer continuingWins = 0;
    private Integer lastWin = 0;
    //万仙阵分组
    private String groupNumber = null;
    private List<Long> fightWin = null;
    private List<Long> fightFail = null;
    //万仙阵玩家数据最后更新日期
    private Integer wanXianLastUpdateTime = DateUtil.getTodayInt();
    private Integer saveSeasonCard = null;
    private List<String> fightLogs = new ArrayList<>();

    public static UserWanXian instance(long uid) {
        UserWanXian userWanXian = new UserWanXian();
        userWanXian.setGameUserId(uid);
        userWanXian.setId(ID.INSTANCE.nextId());
        return userWanXian;
    }

    public void restNewSeason() {
        todayScore = 0;
        baseScore = 0;
        regularRace = 0;
        continuingWins = 0;
        groupNumber = null;
        raceCards = null;
        fightWin = null;
        fightFail = null;
        fightLogs = new ArrayList<>();
        saveSeasonCard = 0;
        wanXianLastUpdateTime = DateUtil.getTodayInt();
    }

    public void restTodayData() {
        todayScore = 0;
        continuingWins = 0;
        fightWin = null;
        fightFail = null;
        wanXianLastUpdateTime = DateUtil.getTodayInt();
    }

    public void updateRaceRes(boolean win, WanXianEmailEnum emailEnum) {
        if (win) {
            if (lastWin != emailEnum.getVal()) {
                if (continuingWins % 2 == 0) {
                    continuingWins++;
                } else {
                    continuingWins = 1;
                }
            } else {
                continuingWins++;
            }
            baseScore += 6;
            lastWin = emailEnum.getVal();
        } else {
            continuingWins = 0;
        }
    }
    public void addWinUid(long uid) {
        if (fightWin == null) {
            fightWin = new ArrayList<>();
        }
        fightWin.add(uid);
    }

    public void addFailUid(long uid) {
        if (fightFail == null) {
            fightFail = new ArrayList<>();
        }
        fightFail.add(uid);
    }

    public void addTodayScore(int val) {
        if (todayScore == null) {
            todayScore = 0;
        }
        todayScore += val;
    }

    /**
     * 是否参加本届常规赛事
     *
     * @return
     */
    public boolean hasSignUpRegularRace() {
        return regularRace != 0 && regularRace == WanXianTool.getThisSeason();
    }

    @Override
    public UserDataType gainResType() {
        return UserDataType.USER_WAN_XIAN;
    }

    public List<String> getFightLogsKeys(List<Integer> preKey) {
        List<String> list = new ArrayList<>();
        if (fightLogs == null || fightLogs.isEmpty()) {
            return list;
        }
        for (int i = fightLogs.size() - 1; i >= 0; i--) {
            String logkey = fightLogs.get(i);
            String[] keys = logkey.split("N", 2);
            if (preKey.contains(Integer.parseInt(keys[0]))) {
                list.add(logkey);
            }
        }
        return list;
    }

}
