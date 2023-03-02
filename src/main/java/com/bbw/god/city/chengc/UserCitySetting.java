package com.bbw.god.city.chengc;

import com.bbw.common.ID;
import com.bbw.god.gameuser.UserDataType;
import com.bbw.god.gameuser.UserSingleObj;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.List;

/**
 * 创建角色时初始化用户城池设置，如矿场、炼丹房默认卡牌
 *
 * @author suhq 2018年10月8日 下午2:19:33
 */
@Getter
@Setter
public class UserCitySetting extends UserSingleObj {
    private Integer useDefaultKcEles = 0;// 是否自定义产出
    private List<Integer> defaultKcEles;// 产出优先级
    private Integer ldfCard = 0;// 炼丹房卡牌

    public static UserCitySetting instance(long guId) {
        UserCitySetting userCitySetting = new UserCitySetting();
        userCitySetting.setId(ID.INSTANCE.nextId());
        userCitySetting.setGameUserId(guId);
        userCitySetting.setUseDefaultKcEles(0);
        userCitySetting.setDefaultKcEles(Arrays.asList(10, 20, 30, 40, 50));
        userCitySetting.setLdfCard(0);
        return userCitySetting;
    }

    @Override
    public UserDataType gainResType() {
        return UserDataType.CITY_SETTING;
    }
}
