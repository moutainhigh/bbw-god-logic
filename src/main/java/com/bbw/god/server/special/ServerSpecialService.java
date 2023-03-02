package com.bbw.god.server.special;

import com.bbw.god.game.config.city.CfgCityEntity;
import com.bbw.god.game.config.city.RoadTool;
import com.bbw.god.game.config.special.CfgSpecialEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ServerSpecialService {

    /**
     * 获得某一特产在某一城池的售价
     *
     * @param special 特产
     * @param city    城池
     * @return
     */
    public int getSellingPrice(CfgSpecialEntity special, CfgCityEntity city) {
        int specialCountry = special.getCountry();// 特产产区
        int basePrice = special.getPrice();// 特产城池基础售价
        // 城池所属区域，用于和特产区域比较
        int cityCountry = RoadTool.getRoadById(city.getAddress1()).getCountry();

        // 区域关系
        int areaRelation = getAreaRelation(specialCountry, cityCountry);
        // 同属一个区域
        if (areaRelation == -1) {
            return basePrice / 2;
        }
        int priceRate = getPriceRate(special, areaRelation);
        // 无效的比率
        if (priceRate == -1) {
            log.error("无效的特产价格比率，不应执行到此处");
            return basePrice / 2;
        }
        // 初始化价格
        int price = (int) (priceRate * basePrice / 100.0);
        return price;
    }

    /**
     * 获得特产产区与城池所属区域的关系 0中部特产；1中部城池；2相邻；3相对
     *
     * @param specialCountry
     * @param cityCountry
     * @return
     */
    private int getAreaRelation(int specialCountry, int cityCountry) {
        // 同产区特产固定半价
        if (specialCountry == cityCountry) {
            return -1;
        }

        // 中部特产
        if (specialCountry == 50) {
            return 0;
        }
        // 中部城池
        if (cityCountry == 50) {
            return 1;
        }
        // 相邻区域
        if ((specialCountry == 10 && (cityCountry == 30 || cityCountry == 40)) || (specialCountry == 20 && (cityCountry == 30 || cityCountry == 40)) || (specialCountry == 30 && (cityCountry == 10 || cityCountry == 20)) || (specialCountry == 40 && (cityCountry == 10 || cityCountry == 20))) {
            return 2;
        }
        // 相对区域
        if ((specialCountry == 10 && cityCountry == 20) || (specialCountry == 20 && cityCountry == 10) || (specialCountry == 30 && cityCountry == 40) || (specialCountry == 40 && cityCountry == 30)) {
            return 3;
        }

        return -1;// 无效的关系
    }

    /**
     * 根据特产在某个城池的价格比率（相对于基础价格）
     *
     * @param special
     * @param areaRelation 区域关系
     * @return
     */
    private int getPriceRate(CfgSpecialEntity special, int areaRelation) {
        // 中部特产
        if (areaRelation == 0) {
            if (special.isNormalSpecial()) {
                return 150;
            }
            if (special.isHighSpecial()) {
                return 130;
            }
            return 120;
        }
        // 普通特产
        if (special.isNormalSpecial()) {
            if (areaRelation == 3) {
                return 225;
            }
            if (areaRelation == 2) {
                return 175;
            }
            return 150;
        }
        // 高级特产
        if (special.isHighSpecial()) {
            if (areaRelation == 3) {
                return 178;
            }
            if (areaRelation == 2) {
                return 146;
            }
            return 130;
        }
        // 顶级特产
        if (special.isTopSpecial()) {
            if (areaRelation == 3) {
                return 156;
            }
            if (areaRelation == 2) {
                return 132;
            }
            return 120;
        }

        return -1;// 无效的比率,正常不执行到此处
    }
}
