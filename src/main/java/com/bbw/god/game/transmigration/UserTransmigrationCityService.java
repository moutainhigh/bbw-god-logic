package com.bbw.god.game.transmigration;

import com.bbw.god.game.transmigration.entity.UserTransmigrationCity;
import com.bbw.god.gameuser.GameUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 各城池挑战信息服务
 *
 * @author: suhq
 * @date: 2021/9/15 3:19 下午
 */
@Service
public class UserTransmigrationCityService {
    @Autowired
    private GameUserService gameUserService;

    /**
     * 获取轮回世界城池挑战信息
     *
     * @return
     */
    public List<UserTransmigrationCity> getTransmigrationCities(long uid) {
        List<UserTransmigrationCity> cities = gameUserService.getMultiItems(uid, UserTransmigrationCity.class);
        return cities;
    }

    /**
     * 获取某座城池的挑战信息
     *
     * @param uid
     * @param cityId
     * @return
     */
    public UserTransmigrationCity getTransmigrationCity(long uid, int cityId) {
        UserTransmigrationCity transmigrationCity = gameUserService.getCfgItem(uid, cityId, UserTransmigrationCity.class);
        return transmigrationCity;
    }


}
