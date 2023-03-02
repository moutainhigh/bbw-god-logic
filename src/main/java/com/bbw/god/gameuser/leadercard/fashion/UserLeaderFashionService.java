package com.bbw.god.gameuser.leadercard.fashion;

import com.bbw.cache.UserCacheService;
import com.bbw.common.ListUtil;
import com.bbw.common.PowerRandom;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.game.config.Cfg;
import com.bbw.god.game.config.special.CfgSpecialHierarchyMap;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.businessgang.BusinessGangService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 时装服务
 *
 * @author suhq
 * @date 2021-03-26 17:25
 **/
@Service
public class UserLeaderFashionService {
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private UserCacheService userCacheService;
    @Autowired
    private BusinessGangService businessGangService;


    /**
     * 时装效果
     *
     * @param uid
     * @param fashionId
     * @return
     */
    public List<Award> getExtraAward(long uid, TreasureEnum fashionId) {
        //法术分身
        if (TreasureEnum.FASHION_FaSFS == fashionId) {
            return new ArrayList<>();
        }
        UserLeaderFashion fashion = getFashion(uid, fashionId.getValue());
        if (null == fashion) {
            return new ArrayList<>();
        }
        List<Award> awards = new ArrayList<>();
        //风酒年华效果
        if (fashionId == TreasureEnum.FASHION_ShiJNH) {
            int addNum = 0;
            int effect1Prop = fashion.getLevel() * 2 + 60;
            if (PowerRandom.hitProbability(effect1Prop)) {
                addNum += 1;
            }
            int effect2Prop = Math.abs(fashion.getLevel() - 20) + 5;
            if (PowerRandom.hitProbability(effect2Prop)) {
                addNum += 2;
            }
            if (addNum > 0) {
                awards.add(new Award(TreasureEnum.FEN_SHEN_JYD.getValue(), AwardEnum.FB, addNum));
            }
        }
        return awards;
    }

    /**
     * 时装效果：铜币加成
     *
     * @param gu
     * @return
     */
    public double getFashionCopperBuff(GameUser gu) {
        List<UserLeaderFashion> fashions = getFashions(gu.getId());
        //获取玩家装配的时装是否为金光飒飒
        Optional<UserLeaderFashion> userLeaderFashion = fashions.stream().filter(f -> (f.getFashionId() == TreasureEnum.FASHION_JinFSS.getValue())).findFirst();
        //如果未装配金光飒飒时装执行
        if (!userLeaderFashion.isPresent()) {
            return 0;
        }
        //获取当前时装对象
        UserLeaderFashion fashion = userLeaderFashion.get();
        //金光飒飒时装等级上限
        int fishionLevelLimit = 50;
        //时装每级增加概率
        double perLevelAddrate = 0.005;
        //时装等级
        int fishionLevel = fashion.getLevel() > fishionLevelLimit ? fishionLevelLimit : fashion.getLevel();
        //金风飒飒时装初始概率；
        double fishionRate = 0.05;
        fishionRate = fishionRate + fishionLevel * perLevelAddrate;
        //装配金光时装的概率
        return fishionRate;
    }

    /**
     * 风起霓裳时装效果：消耗的铜钱减少最高为20%
     *
     * @param gu
     * @return
     */
    public double getFashionFengQNSCopperBuff(GameUser gu) {
        List<UserLeaderFashion> fashions = getFashions(gu.getId());
        //获取玩家装配的时装是否为风起霓裳
        Optional<UserLeaderFashion> userLeaderFashion = fashions.stream().filter(f -> (f.getFashionId() == TreasureEnum.FASHION_FENGQNS.getValue())).findFirst();
        //如果未装配风起霓裳时装执行
        if (!userLeaderFashion.isPresent()) {
            return 0;
        }
        //获取当前时装对象
        UserLeaderFashion fashion = userLeaderFashion.get();
        //风起霓裳时装等级上限
        int fishionLevelLimit = 50;
        //时装每级增加消耗的铜钱减少加成
        double perLevelAddrate = 0.003;
        //时装等级
        int fishionLevel = fashion.getLevel() > fishionLevelLimit ? fishionLevelLimit : fashion.getLevel();
        //风起霓裳时装初始加成；
        double fishionRate = 0.05;
        fishionRate = fishionRate + fishionLevel * perLevelAddrate;
        //装配风起霓裳的加成
        return fishionRate;
    }

    /**
     * 是否提升阶级
     *
     *
     * @return
     */
    public int specialUpgrade(GameUser gu, int specialId) {
        //是特产任务需要特产
        boolean isTaskSpecial = businessGangService.isSpecialTaskNeedSpecial(gu.getId(), specialId);
        if (isTaskSpecial) {
            return specialId;
        }
        //时装升阶效果
        boolean isUpgrade = PowerRandom.hitProbability(getFashionChengFPLBuff(gu));
        CfgSpecialHierarchyMap cfgSpecialHierarchyMap = Cfg.I.get(specialId % 1000, CfgSpecialHierarchyMap.class);
        if (!isUpgrade || null == cfgSpecialHierarchyMap) {
            return specialId;
        }
        return specialId / 1000 * 1000 + cfgSpecialHierarchyMap.getToSpecialId();
    }

    /**
     * 乘风破浪时装效果：有概率提升特产阶级最高为60%
     *
     * @param gu
     * @return
     */
    public int getFashionChengFPLBuff(GameUser gu) {
        List<UserLeaderFashion> fashions = getFashions(gu.getId());
        //获取玩家装配的时装是否为乘风破浪
        Optional<UserLeaderFashion> userLeaderFashion = fashions.stream().filter(f -> (f.getFashionId() == TreasureEnum.FASHION_CHENGFPL.getValue())).findFirst();
        //如果未装配乘风破浪时装执行
        if (!userLeaderFashion.isPresent()) {
            return 0;
        }
        //获取当前时装对象
        UserLeaderFashion fashion = userLeaderFashion.get();
        //乘风破浪时装等级上限
        int fishionLevelLimit = 50;
        //时装每级增加触发概率
        int perLevelAddrate = 1;
        //时装等级
        int fishionLevel = fashion.getLevel() > fishionLevelLimit ? fishionLevelLimit : fashion.getLevel();
        //乘风破浪时装初始加成；
        int fishionRate = 10;
        fishionRate = fishionRate + fishionLevel * perLevelAddrate;
        //装配乘风破浪的加成
        return fishionRate;
    }

    public UserLeaderFashion getFashion(long uid, int fashionId) {
        List<UserLeaderFashion> fashions = getFashions(uid);
        if (ListUtil.isEmpty(fashions)) {
            return null;
        }
        return fashions.stream().filter(tmp -> tmp.getFashionId() == fashionId).findFirst().orElse(null);
    }

    public List<UserLeaderFashion> getFashions(long uid) {
        List<UserLeaderFashion> leaderFashions = userCacheService.getUserDatas(uid, UserLeaderFashion.class);
        return leaderFashions;
    }

    public void addUserLeaderFashion(UserLeaderFashion fashion) {
        userCacheService.addUserData(fashion);
    }
}
