package com.bbw.god.gameuser.achievement.resource.nightmarecity;

import com.bbw.god.city.UserCityService;
import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.game.config.TypeEnum;
import com.bbw.god.game.config.city.CfgCityEntity;
import com.bbw.god.game.config.city.CityTool;
import com.bbw.god.gameuser.achievement.UserAchievementInfo;
import com.bbw.god.gameuser.achievement.resource.ResourceAchievementService;
import com.bbw.god.nightmarecity.chengc.UserNightmareCity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author suchaobin
 * @description 成就id=14790的service
 * @date 2020/9/15 10:53
 **/
@Service
public class AchievementService14790 extends ResourceAchievementService {
    @Autowired
    private UserCityService userCityService;

    /**
     * 获取当前资源类型
     *
     * @return 当前资源类型
     */
    @Override
    public AwardEnum getMyAwardEnum() {
        return AwardEnum.NIGHTMARE_CITY;
    }

    /**
     * 获取当前成就id
     *
     * @return 当前成就id
     */
    @Override
    public int getMyAchievementId() {
        return 14790;
    }

    /**
     * 获取当前成就进度(用于展示给客户端)
     *
     * @param uid  玩家id
     * @param info 成就对象信息
     * @return 当前成就进度
     */
    @Override
    public int getMyProgress(long uid, UserAchievementInfo info) {
        if (isAccomplished(info)) {
            return getMyNeedValue();
        }
        List<UserNightmareCity> userCities = userCityService.getUserOwnNightmareCities(uid);
        List<UserNightmareCity> list = userCities.stream().filter(tmp -> {
            CfgCityEntity city = CityTool.getCityById(tmp.getBaseId());
            return TypeEnum.Gold.getValue() == city.getCountry() && city.getLevel() <= 2;
        }).collect(Collectors.toList());
        return list.size();
    }
}
