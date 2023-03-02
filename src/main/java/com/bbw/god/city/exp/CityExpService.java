package com.bbw.god.city.exp;

import com.bbw.god.game.config.city.CfgCityEntity;
import com.bbw.god.game.config.city.CityTypeEnum;
import com.bbw.god.gameuser.GameUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * 体验逻辑
 *
 * @author: suhq
 * @date: 2021/11/9 3:39 下午
 */
@Service
public class CityExpService {
    private static List<CityTypeEnum> SUPPORT_EXP_CITY_TYPE = Arrays.asList(
            CityTypeEnum.MY,
            CityTypeEnum.TYF,
            CityTypeEnum.MXD,
            CityTypeEnum.LT);
    @Autowired
    private GameUserService gameUserService;

    /**
     * 是否支持体验
     *
     * @param cityType
     * @return
     */
    public boolean isSupportExp(CityTypeEnum cityType) {
        return SUPPORT_EXP_CITY_TYPE.contains(cityType);
    }


    /**
     * 获取体验记录，如果没有则创建
     *
     * @param uid
     * @return
     */
    public UserCityExpRecords getOrCreateExpRecords(long uid) {
        UserCityExpRecords expRecords = gameUserService.getSingleItem(uid, UserCityExpRecords.class);
        if (null == expRecords) {
            expRecords = UserCityExpRecords.instance(uid);
            gameUserService.addItem(uid, expRecords);
        }
        return expRecords;
    }

    /**
     * 是否已体验过
     *
     * @param uid
     * @param city
     * @return
     */
    public boolean hasExped(long uid, CfgCityEntity city) {
        UserCityExpRecords expRecords = getOrCreateExpRecords(uid);
        return expRecords.getRecord(city.getId()) > 0;

    }
}
