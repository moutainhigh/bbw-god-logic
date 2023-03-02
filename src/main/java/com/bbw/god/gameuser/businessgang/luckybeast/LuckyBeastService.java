package com.bbw.god.gameuser.businessgang.luckybeast;

import com.bbw.common.DateUtil;
import com.bbw.common.PowerRandom;
import com.bbw.exception.CoderException;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.config.city.CfgGodRoadEntity;
import com.bbw.god.game.config.city.CfgRoadEntity;
import com.bbw.god.game.config.city.RoadTool;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.businessgang.Enum.BusinessGangEnum;
import com.bbw.god.gameuser.businessgang.UserBusinessGangService;
import com.bbw.god.gameuser.businessgang.user.UserBusinessGangInfo;
import com.bbw.god.gameuser.yaozu.UserYaoZuInfo;
import com.bbw.god.server.god.ServerGod;
import com.bbw.god.server.god.ServerGodDayConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 招财兽信息服务
 *
 * @author: huanghb
 * @date: 2022/1/30 17:21
 */
@Slf4j
@Service
public class LuckyBeastService {
    @Autowired
    private ServerGodDayConfigService serverGodDayConfigService;
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private UserBusinessGangService userBusinessGangService;

    /**
     * 到达招财兽
     *
     * @param uid
     * @return
     */
    public RDLuckyBeastInfo arriveLuckyBeast(long uid) {
        //声望检测
        if (!isOpenLuckyBeast(uid)) {
            return null;
        }
        //获取玩家招财兽信息
        UserLuckyBeast userLuckyBeast = getUserLuckyBeast(uid);
        //玩家位置
        int userPos = gameUserService.getGameUser(uid).getLocation().getPosition();
        boolean isArrive = userPos == userLuckyBeast.getPosition();
        //未到达
        if (!isArrive) {
            return null;
        }
        //招财攻击次数检测
        boolean isOWnAttackTimes = isOwnAttackTimesCheck(userLuckyBeast);
        if (!isOWnAttackTimes) {
            return null;
        }
        return RDLuckyBeastInfo.getInstance(userLuckyBeast);
    }

    /**
     * 获取招财兽信息
     *
     * @param uid
     * @return
     */
    protected UserLuckyBeast getUserLuckyBeast(long uid) {
        //获得玩家招财兽信息
        UserLuckyBeast userLuckyBeast = gameUserService.getSingleItem(uid, UserLuckyBeast.class);
        //存在招财兽信息，直接返回
        if (null != userLuckyBeast) {
            return userLuckyBeast;
        }
        //是否可以生成招财兽
        int refreshLuckyBeastMinPrestige = LuckyBeastTool.getRefreshLuckyBeastMinPrestige();
        boolean isCanRefreshLuckyBeast = getPrestige(uid) >= refreshLuckyBeastMinPrestige;
        if (!isCanRefreshLuckyBeast) {
            throw CoderException.high("程序员没有编写声望值=" + getPrestige(uid) + "的招财兽");
        }
        //生成招财兽
        userLuckyBeast = generateLuckyBeast(uid);
        //添加拥有项
        gameUserService.addItem(uid, userLuckyBeast);
        return userLuckyBeast;
    }

    /**
     * 生成招财兽
     *
     * @param uid
     * @return
     */
    private UserLuckyBeast generateLuckyBeast(long uid) {
        //生成招财兽信息
        UserLuckyBeast userLuckyBeast;
        CfgLuckyBeast.LuckyBeastInfo luckyBeastInfo = LuckyBeastTool.getLuckyBeastInfo(getPrestige(uid));
        //生成招财兽位置
        Integer position = generateLuckyBeastRandomPosition(uid);
        userLuckyBeast = UserLuckyBeast.instance(uid, luckyBeastInfo, position);
        return userLuckyBeast;
    }

    /**
     * 获得随机攻击奖励
     *
     * @param uid
     * @return
     */
    public List<Award> getRandomAttackAwards(long uid) {
        UserLuckyBeast userLuckyBeast = getUserLuckyBeast(uid);
        //获得奖励规则
        Integer awardType = LuckyBeastTool.getCurrentAwardType(userLuckyBeast.gainAwardTypeRule());
        String luckyBeastAwardRule = userLuckyBeast.luckyBeastAwardRule(awardType.toString());
        //返回奖励
        return LuckyBeastTool.getCurrentAwards(luckyBeastAwardRule);

    }

    /**
     * 排除神仙,界碑和玩家自身的位置
     *
     * @param
     * @param roads
     * @return
     */
    public List<CfgRoadEntity> excludeGodAndUserPos(long uid, List<ServerGod> todayGods, List<CfgRoadEntity> roads) {
        List<CfgRoadEntity> notGodsAndUserPos = new ArrayList<>();
        //神仙位置
        List<Integer> godPos = todayGods.stream().map(ServerGod::getPosition).collect(Collectors.toList());
        List<Integer> godRoad = RoadTool.getGodRoads().stream().map(CfgGodRoadEntity::getId).collect(Collectors.toList());
        //玩家位置
        int userPos = gameUserService.getGameUser(uid).getLocation().getPosition();
        //排除神仙,界碑和玩家自身的位置
        for (CfgRoadEntity roadPos : roads) {
            int roadPosition = roadPos.getId();
            boolean containsJieBei = LuckyBeastTool.getJieBeiPosList().contains(roadPosition);
            boolean containsSpecialRoad = godRoad.contains(roadPosition);
            if (!godPos.contains(roadPosition) && roadPosition != userPos && !containsJieBei && !containsSpecialRoad) {
                notGodsAndUserPos.add(roadPos);
            }
        }
        return notGodsAndUserPos;
    }

    /**
     * 生成招财兽随机位置
     *
     * @param uid
     * @return
     */
    protected Integer generateLuckyBeastRandomPosition(long uid) {
        List<CfgRoadEntity> roads = RoadTool.getRoads();
        //获取当日神仙
        List<ServerGod> todayGods = serverGodDayConfigService.getTodayGods(gameUserService.getActiveSid(uid));
        //排除神仙,界碑和玩家自身的位置
        List<CfgRoadEntity> notGodsAndJieBeiAndUserPos = excludeGodAndUserPos(uid, todayGods, roads);
        //获得指定区域的位置集合
        List<Integer> notGodsAndUserCityPos = notGodsAndJieBeiAndUserPos.stream().map(CfgRoadEntity::getId).collect(Collectors.toList());
        List<Integer> notGodsAndUserAndYaoZuPos = new ArrayList<>();
        //排除妖族
        List<UserYaoZuInfo> userYaoZuInfos = gameUserService.getMultiItems(uid, UserYaoZuInfo.class);
        List<Integer> userYaoPos = userYaoZuInfos.stream().map(UserYaoZuInfo::getPosition).collect(Collectors.toList());
        for (int Pos : notGodsAndUserCityPos) {
            if (!userYaoPos.contains(Pos)) {
                notGodsAndUserAndYaoZuPos.add(Pos);
            }
        }
        //获得随机的位置
        return PowerRandom.getRandomsFromList(notGodsAndUserAndYaoZuPos, 1).get(0);
    }

    /**
     * 是否下一个刷新时间（每天0点）
     *
     * @param uid
     * @return
     */
    protected UserLuckyBeast nextRefreshTimeCheckAndHandle(long uid) {
        //获得玩家招财兽信息
        UserLuckyBeast userLuckyBeast = gameUserService.getSingleItem(uid, UserLuckyBeast.class);
        if (null == userLuckyBeast) {
            return userLuckyBeast;
        }
        Date nextRefreshTime = DateUtil.getDateEnd(userLuckyBeast.getDateTime());
        boolean isRefresh = DateUtil.now().getTime() >= nextRefreshTime.getTime();
        if (!isRefresh) {
            return userLuckyBeast;
        }
        gameUserService.deleteItem(userLuckyBeast);
        return null;
    }

    /**
     * 是否拥有攻击次数检测
     *
     * @param userLuckyBeast
     * @return
     */
    protected boolean isOwnAttackTimesCheck(UserLuckyBeast userLuckyBeast) {
        if (userLuckyBeast.getRemainfreeAttackTimes() <= 0) {
            return false;
        }
        return true;
    }

    /**
     * 获得商帮声望值
     *
     * @return
     */
    protected Integer getPrestige(long uid) {
        UserBusinessGangInfo userBusinessGang = userBusinessGangService.getOrCreateUserBusinessGang(uid);
        return userBusinessGang.getPrestige(BusinessGangEnum.ZHAO_CAI.getType());
    }


    /**
     * 登陆获得招财兽位置
     *
     * @return
     */
    public RDLuckyBeastInfo getluckyBeastPosByLogin(long uid) {
        //声望检测
        if (!isOpenLuckyBeast(uid)) {
            return null;
        }
        //下一个刷新宝藏时间检查和处理
        nextRefreshTimeCheckAndHandle(uid);
        //获取玩家招财兽信息
        UserLuckyBeast userLuckyBeast = getUserLuckyBeast(uid);

        //招财攻击次数检测
        boolean isOWnAttackTimes = isOwnAttackTimesCheck(userLuckyBeast);
        if (!isOWnAttackTimes) {
            return null;
        }
        return RDLuckyBeastInfo.getInstance(userLuckyBeast);
    }

    /**
     * 是否开启招财兽功能
     *
     * @param uid
     * @return
     */
    public boolean isOpenLuckyBeast(long uid) {
        if (getPrestige(uid) >= LuckyBeastTool.getRefreshLuckyBeastMinPrestige()) {
            return true;
        }
        return false;
    }

}
