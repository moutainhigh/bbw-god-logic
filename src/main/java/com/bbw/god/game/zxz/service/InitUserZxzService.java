package com.bbw.god.game.zxz.service;

import com.bbw.cache.UserCacheService;
import com.bbw.common.ListUtil;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.city.UserCityService;
import com.bbw.god.city.chengc.UserCity;
import com.bbw.god.game.zxz.cfg.CfgZxzDefenderCardRule;
import com.bbw.god.game.zxz.cfg.CfgZxzLevel;
import com.bbw.god.game.zxz.cfg.ZxzTool;
import com.bbw.god.game.zxz.entity.UserZxzDifficulty;
import com.bbw.god.game.zxz.entity.UserZxzInfo;
import com.bbw.god.game.zxz.entity.UserZxzRegionDefender;
import com.bbw.god.game.zxz.entity.UserZxzRegionInfo;
import com.bbw.god.game.zxz.enums.ZxzDefenderEnum;
import com.bbw.god.game.zxz.enums.ZxzDifficultyEnum;
import com.bbw.god.game.zxz.enums.ZxzStatusEnum;
import com.bbw.god.gameuser.GameUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 玩家诛仙阵初始化 service
 * @author: hzf
 * @create: 2022-10-13 14:13
 **/
@Service
public class InitUserZxzService {
    @Autowired
    private ZxzService zxzService;
    @Autowired
    private UserCityService userCityService;
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private UserCacheService userCacheService;

    /**
     * 判断玩家是否开始诛仙阵
     * @param uid
     */
    public void checkZxzIsOpend(Long uid){
        //获取玩家的等级
        Integer level = gameUserService.getGameUser(uid).getLevel();
        //获取玩家的城池
        UserCity userCity = userCityService.getUserCities(uid)
                .stream().filter(uCity -> uCity.gainCity().getLevel() >= ZxzTool.getCfg().getUnlockNeedCityLevel5())
                .findFirst().orElse(null);
        //判断玩家是否达到25或者是否拥有一个5级城池
        if (userCity == null && level < ZxzTool.getCfg().getUnlockNeedUserLevel()) {
            throw new ExceptionForClientTip("zxz.not.open");
        }

    }

    /**
     * 初始化玩家诛仙阵信息
     * @param uid
     */
    public void initUserZxz(long uid){
        UserZxzInfo userZxzInfo = zxzService.getUserZxz(uid);
        List<UserZxzRegionInfo> userZxzRegionInfos = zxzService.getUserZxzRegions(uid);
        if (null == userZxzInfo) {
            //初始化玩家的难度信息
            UserZxzInfo uInfo = initUserZxzLevel(uid);
            gameUserService.addItem(uid, uInfo);
        }
        if (ListUtil.isEmpty(userZxzRegionInfos)) {
            CfgZxzLevel zxzDefault = ZxzTool.getZxzLevel(ZxzDifficultyEnum.DIFFICULTY_10.getDifficulty());
            //初始化简单难度的攻打信息
            UserZxzRegionInfo userZxzRegionInfo = initUserZxzRegion(uid,ZxzDifficultyEnum.DIFFICULTY_10,zxzDefault.getRegions().get(0));
            userCacheService.addUserData(userZxzRegionInfo);
        }
    }
    /**
     * 初始化玩家的难度信息
     * @param uid
     * @return
     */
    private UserZxzInfo initUserZxzLevel(long uid) {
        List<UserZxzDifficulty> levels = new ArrayList<>();
        for (ZxzDifficultyEnum zxzDifficultyEnum : ZxzDifficultyEnum.values()) {
            //初始化用户的诛仙阵难度信息
            UserZxzDifficulty zxzLevel = UserZxzDifficulty.getInstance(zxzDifficultyEnum);
            levels.add(zxzLevel);
        }
        return UserZxzInfo.getInstance(levels, uid);
    }
    /**
     * 初始化玩家的区域攻打记录
     * @param uid
     * @return
     */
    public UserZxzRegionInfo initUserZxzRegion(long uid,ZxzDifficultyEnum difficulty,Integer regionId){
        UserZxzRegionInfo userZxzRegionInfo = new UserZxzRegionInfo();
        //初始化玩家关卡的信息
        List<UserZxzRegionDefender> userZxzRegionDefenders = initUserZxzRegionDefender(ZxzDifficultyEnum.DIFFICULTY_10.getDifficulty(), regionId,true);
        userZxzRegionInfo = userZxzRegionInfo.getInstance(difficulty.getDifficulty(),regionId,userZxzRegionDefenders,uid);
        return userZxzRegionInfo;
    }

    /**
     * 初始化玩家关卡的信息
     *
     * @param difficulty
     * @param regionId
     * @return
     * @refreshAward 是否刷新宝箱领取
     */
    public List<UserZxzRegionDefender> initUserZxzRegionDefender(Integer difficulty, Integer regionId, boolean refreshAward) {
        List<UserZxzRegionDefender> userDefenders = new ArrayList<>();
        //获取关卡配置信息
        List<CfgZxzDefenderCardRule> defenderRules = ZxzTool.getZxzDefenderCards(difficulty);
        for (CfgZxzDefenderCardRule defender : defenderRules) {
            UserZxzRegionDefender userZxzRegionDefender = new UserZxzRegionDefender();
            userZxzRegionDefender.setDefenderId(regionId, defender.getDefender());
            userZxzRegionDefender.setKind(defender.getKind());
            if (refreshAward) {
                userZxzRegionDefender.setAwarded(0);
            }
            //将第一关设置成可攻打，其他关卡不可攻打
            if (defender.getDefender() == ZxzDefenderEnum.DEFENDER_1.getDefenderId()) {
                userZxzRegionDefender.setStatus(ZxzStatusEnum.ABLE_ATTACK.getStatus());
            }else {
                userZxzRegionDefender.setStatus(ZxzStatusEnum.NOT_OPEN.getStatus());
            }
            userDefenders.add(userZxzRegionDefender);
        }
        return userDefenders;
    }
}
