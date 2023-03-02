package com.bbw.god.gm;

import com.bbw.App;
import com.bbw.common.ListUtil;
import com.bbw.common.Rst;
import com.bbw.common.SpringContextUtil;
import com.bbw.common.StrUtil;
import com.bbw.god.activity.ActivityService;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.city.UserCityService;
import com.bbw.god.city.chengc.UserCity;
import com.bbw.god.city.event.CityEventPublisher;
import com.bbw.god.city.event.EPCityAdd;
import com.bbw.god.controller.AbstractController;
import com.bbw.god.db.entity.CfgServerEntity;
import com.bbw.god.db.pool.DetailDataDAO;
import com.bbw.god.fight.RDFightResult;
import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.game.config.city.CfgCityEntity;
import com.bbw.god.game.config.city.CityTool;
import com.bbw.god.game.config.server.ServerTool;
import com.bbw.god.gameuser.achievement.AchievementTool;
import com.bbw.god.gameuser.achievement.CfgAchievementEntity;
import com.bbw.god.gameuser.achievement.UserAchievementInfo;
import com.bbw.god.gameuser.achievement.UserAchievementLogic;
import com.bbw.god.nightmarecity.chengc.UserNightmareCity;
import com.bbw.god.server.ServerUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 玩家数据相关的操作
 *
 * @author suhq
 * @date 2019年4月13日 下午1:49:18
 */
@RestController
@RequestMapping("/gm")
public class GMUserCtrl extends AbstractController {
    @Autowired
    private UserGmService userGmService;
    @Autowired
    private ServerUserService serverUserService;
    @Autowired
    private ActivityService activityService;
    @Autowired
    private UserCityService userCityService;
    @Autowired
    private App app;
    @Autowired
    private UserAchievementLogic userAchievementLogic;

    /**
     * 限制登录
     *
     * @param sId
     * @param nickname
     * @param endDate
     * @return
     */
    @RequestMapping("user!limitLogin")
    public Rst limitLogin(int sId, String nickname, String endDate) {
        return this.userGmService.limitLogin(sId, nickname, endDate);
    }

    /**
     * 更新玩家到特定等级
     *
     * @param sId
     * @param nickname
     * @param level
     * @return
     */
    @RequestMapping("user!updateToLevel")
    public Rst updateToLevel(int sId, String nickname, int level) {
        return this.userGmService.updateToLevel(sId, nickname, level);
    }

    /**
     * 更新玩家到差1经验升级到指定等级
     *
     * @param sId
     * @param nickname
     * @param level
     * @return
     */
    @RequestMapping("user!updateToAlmostLevel")
    public Rst updateToAlmostLevel(int sId, String nickname, int level) {
        return this.userGmService.updateToAlmostLevel(sId, nickname, level);
    }

    /**
     * 设置新手引导
     *
     * @param sId
     * @param nickname
     * @param guideStatus
     * @return
     */
    @RequestMapping("user!setGuideStatus")
    public Rst setGuideStatus(int sId, String nickname, int guideStatus) {
        return this.userGmService.setGuideStatus(sId, nickname, guideStatus);
    }

    /**
     * 设置性别
     *
     * @param sId
     * @param nickname
     * @param sex
     * @return
     */
    @RequestMapping("user!setSex")
    public Rst setSex(int sId, String nickname, int sex) {
        return this.userGmService.setSex(sId, nickname, sex);
    }


    /**
     * 增加月卡天数
     *
     * @param sId
     * @param nickname
     * @param days
     * @return
     */
    @RequestMapping("user!addYKEndTime")
    public Rst addYKEndTime(int sId, String nickname, int days) {
        return this.userGmService.addYKEndTime(sId, nickname, days);
    }

    /**
     * 季卡
     *
     * @param sId
     * @param nickname
     * @param days
     * @return
     */
    @RequestMapping("user!addJKEndTime")
    public Rst addJKEndTime(int sId, String nickname, int days) {
        return this.userGmService.addJKEndTime(sId, nickname, days);
    }

    /**
     * 速战卡
     *
     * @param sId
     * @param nickname
     * @return
     */
    @RequestMapping("user!addSZK")
    public Rst addSZK(int sId, String nickname) {
        return this.userGmService.addSZK(sId, nickname);
    }

    /**
     * 加铜钱
     *
     * @param sId
     * @param nickname
     * @param num
     * @return
     */
    @RequestMapping("user!addCopper")
    public Rst addCopper(int sId, String nickname, int num) {
        return this.userGmService.addCopper(sId, nickname, num);
    }

    /**
     * 加元宝
     *
     * @param sId
     * @param nickname
     * @param num
     * @return
     */
    @RequestMapping("user!addGold")
    public Rst addGold(int sId, String nickname, int num) {
        return this.userGmService.addGold(sId, nickname, num);
    }

    /**
     * 加钻石
     *
     * @param sId
     * @param nickname
     * @param num
     * @return
     */
    @RequestMapping("user!addDiamond")
    public Rst addDiamond(int sId, String nickname, int num) {
        return this.userGmService.addDiamond(sId, nickname, num);
    }

    /**
     * 加体力
     *
     * @param sId
     * @param nickname
     * @param num
     * @return
     */
    @RequestMapping("user!addDice")
    public Rst addDice(int sId, String nickname, int num) {
        return this.userGmService.addDice(sId, nickname, num);
    }

    /**
     * 加元素
     *
     * @param sId
     * @param nickname
     * @param type
     * @param num
     * @return
     */
    @RequestMapping("user!addEle")
    public Rst addEle(int sId, String nickname, int type, int num) {
        return this.userGmService.addEle(sId, nickname, type, num);
    }

    /**
     * 达成成就
     *
     * @param serverName
     * @param nickname
     * @param achievementId
     * @return
     */
    @GetMapping("user!accomplishAchievement")
    public Rst accomplishAchievement(String serverName, String nickname, int achievementId) {
        CfgServerEntity server = ServerTool.getServer(serverName);
        if (server == null) {
            return Rst.businessFAIL("无效的区服");
        }
        int sId = server.getMergeSid();
        Optional<Long> uidOptional = this.serverUserService.getUidByNickName(sId, nickname);
        if (!uidOptional.isPresent()) {
            return Rst.businessFAIL("该区服不存在该角色");
        }
        CfgAchievementEntity achievement = AchievementTool.getAchievement(achievementId);
        if (null == achievement) {
            return Rst.businessFAIL("错误的成就id");
        }
        Long uid = uidOptional.get();
        UserAchievementInfo info = gameUserService.getSingleItem(uid, UserAchievementInfo.class);
        if (null == info) {
            info = userAchievementLogic.initUserAchievementInfo(uid);
            gameUserService.addItem(uid, info);
        }
        info.accomplishAchievement(achievementId);
        gameUserService.updateItem(info);
        return Rst.businessOK();
    }

    /**
     * 加特产
     *
     * @param sId
     * @param nickname
     * @param num
     * @return
     */
    @RequestMapping("user!addSpecial")
    public Rst addSpecial(int sId, String nickname, String specials, int num) {
        if (num < 0) {
            return Rst.businessFAIL("无效的数量");
        }
        return this.userGmService.addSpecial(sId, nickname, specials, num);
    }

    /**
     * 重置玩家回归活动
     *
     * @param uid
     * @return
     */
    @RequestMapping("user!addHerobackSign")
    public Rst addSign(Long uid, int add) {
        this.activityService.handleUaProgress(uid, this.gameUserService.getActiveSid(uid), add, ActivityEnum.HERO_BACK_SIGIN);
        return Rst.businessOK();
    }

    /**
     * 删除玩家回归活动
     *
     * @param uids
     * @return
     */
    @RequestMapping("user!delHeroback")
    public Rst delHeroback(String uids) {
        String[] uidStr = uids.split(",");
        for (String str : uidStr) {
            Long uid = Long.parseLong(str);
            this.activityService.delHeroback(uid, this.gameUserService.getActiveSid(uid));
        }
        return Rst.businessOK();
    }

    /**
     * 展示特定数据类型的数据
     *
     * @param sid
     * @param nickname
     * @param dataType
     * @return
     */
    @RequestMapping("user!showUserData")
    public Rst showUserData(int sid, String nickname, String dataType) {
        return this.userGmService.showUserData(sid, nickname, dataType);
    }


    /**
     * 攻下某个区域城池
     *
     * @param sId
     * @param nickname
     * @param country
     * @return
     */
    @RequestMapping("user!cityAttack")
    public Rst cityAttack(int sId, String nickname, int country) {
        Optional<Long> guId = this.serverUserService.getUidByNickName(sId, nickname);
        if (!guId.isPresent()) {
            return Rst.businessFAIL("无效用户名");
        }
        long uid = guId.get();
        List<CfgCityEntity> cfgcities = CityTool.getCountryCities(country);
        List<UserCity> cities = userCityService.getOwnCitiesByCountry(uid, country);
        List<Integer> ids = cities.stream().map(UserCity::getBaseId).collect(Collectors.toList());
        for (CfgCityEntity city : cfgcities) {
            if (city.isCC() && !ids.contains(city.getId())) {
                CityEventPublisher.pubUserCityAddEvent(uid, new EPCityAdd(city.getId(), false), new RDFightResult());
            }
        }
        return Rst.businessOK();
    }

    /**
     * 攻下某个区域梦魇城池
     *
     * @param sId
     * @param nickname
     * @param country
     * @return
     */
    @RequestMapping("user!nightmareCityAttack")
    public Rst nightmareCityAttack(int sId, String nickname, int country) {
        Optional<Long> guId = this.serverUserService.getUidByNickName(sId, nickname);
        if (!guId.isPresent()) {
            return Rst.businessFAIL("无效用户名");
        }
        long uid = guId.get();
        List<CfgCityEntity> cfgcities = CityTool.getCountryCities(country);
        List<UserNightmareCity> cities = userCityService.getOwnNightmareCitiesByCountry(uid, country);
        List<Integer> ids = cities.stream().map(UserNightmareCity::getBaseId).collect(Collectors.toList());
        for (CfgCityEntity city : cfgcities) {
            if (city.isCC() && !ids.contains(city.getId())) {
                CityEventPublisher.pubUserCityAddEvent(uid, new EPCityAdd(city.getId(), true), new RDFightResult());
            }
        }
        return Rst.businessOK();
    }

    /**
     * 升阶某个区域城池阶数
     *
     * @param sId
     * @param nickname
     * @param country
     * @return
     */
    @RequestMapping("user!cityPromotes")
    public Rst cityPromote(int sId, String nickname, int country, int hierarchy) {
        Optional<Long> guId = this.serverUserService.getUidByNickName(sId, nickname);
        if (!guId.isPresent()) {
            return Rst.businessFAIL("无效用户名");
        }
        long uid = guId.get();
        List<UserCity> cities = userCityService.getOwnCitiesByCountry(uid, country);
        for (UserCity city : cities) {
            city.setHierarchy(hierarchy);
            gameUserService.updateItem(city);
        }
        return Rst.businessOK();
    }

    /**
     * 升阶某个区域城池阶数
     *
     * @param sId
     * @param nickname
     * @param cityName
     * @param hierarchy
     * @return
     */
    @RequestMapping("user!cityPromote")
    public Rst cityPromote(int sId, String nickname, String cityName, int hierarchy) {
        Optional<Long> guId = this.serverUserService.getUidByNickName(sId, nickname);
        if (!guId.isPresent()) {
            return Rst.businessFAIL("无效用户名");
        }
        long uid = guId.get();
        List<UserCity> cities = userCityService.getUserCities(uid);
        if (StrUtil.isBlank(cityName)) {
            return Rst.businessFAIL("无效城池名");
        }
        for (UserCity city : cities) {
            if (cityName.contains(city.getName())) {
                city.setHierarchy(hierarchy);
                gameUserService.updateItem(city);
            }
        }
        return Rst.businessOK();
    }


    /**
     * 累计奖励获取
     *
     * @param uids      玩家uids
     * @param awardType 奖励类型
     * @param awardId   奖励ID
     * @param from      进行累计的开始时间 eg：20221101
     * @param to        进行累计的结束时间 eg：20221110
     * @return
     */
    @RequestMapping("user!totalAwarded")
    public Rst totalAwarded(String uids, int awardType, int awardId, int from, int to) {
        Rst rst = new Rst();
        List<Long> uidList = ListUtil.parseStrToLongs(uids, ",");
        AwardEnum award = AwardEnum.fromValue(awardType);
        for (Long uid : uidList) {
            int sid = gameUserService.getActiveSid(uid);
            DetailDataDAO detailDataDAO = SpringContextUtil.getBean(DetailDataDAO.class, sid);
            Long value = detailDataDAO.dbTotalAwarded(uid, award, awardId, from, to);
            rst.put(uid.toString(), value);
        }
        return rst;
    }
}
