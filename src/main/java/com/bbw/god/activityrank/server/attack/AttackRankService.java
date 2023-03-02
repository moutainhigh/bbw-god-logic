package com.bbw.god.activityrank.server.attack;

import com.bbw.god.game.config.city.CfgCityEntity;
import org.springframework.stereotype.Service;

/**
 * 王者之路榜单业务
 *
 * @author longwh
 * @date 2022/12/6 10:11
 */
@Service
public class AttackRankService {

    /**
     * 获得积分
     *
     * @param city
     * @return
     */
    public int getPoint(CfgCityEntity city) {
        switch (city.getLevel()) {
            case 1:
            case 2:
                return 1;
            case 3:
            case 4:
                return 2;
            case 5:
                return 3;
            default:
                return 0;
        }
    }
}