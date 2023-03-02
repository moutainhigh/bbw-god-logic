package com.bbw.god.server.special;

import com.bbw.common.PowerRandom;
import com.bbw.god.game.config.city.CfgCityEntity;
import com.bbw.god.game.config.city.CityTool;
import com.bbw.god.game.config.special.CfgSpecialEntity;
import com.bbw.god.game.config.special.SpecialEnum;
import com.bbw.god.game.config.special.SpecialTool;
import com.bbw.god.game.data.GameDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author suchaobin
 * @description 全服特产service
 * @date 2020/11/11 17:48
 **/
@Service
public class GameSpecialService {
    @Autowired
    private GameDataService gameDataService;

    public List<GameSpecialPrice> getGameSpecialPrices(int serverGroup) {
        return gameDataService.getGameDatas(GameSpecialPrice.class).stream().filter(tmp ->
                tmp.getServerGroup() == serverGroup).collect(Collectors.toList());
    }

    public GameSpecialPrice initGameSynthesisSpecialData(int specialId, int serverGroup) {
        Optional<GameSpecialPrice> exist = gameDataService.getGameDatas(GameSpecialPrice.class).stream().filter(tmp ->
                tmp.getSpecialId() == specialId).findFirst();
        // 已经存在了，不重复初始化
        if (exist.isPresent()) {
            return exist.get();
        }
        CfgSpecialEntity cfgSpecialEntity = SpecialTool.getSpecialById(specialId);
        List<Integer> highSellCountries = cfgSpecialEntity.getHighSellCountries();
        int country = PowerRandom.getRandomFromList(highSellCountries);
        int minSellPrice = getMinSellPrice(specialId);
        int maxSellPrice = getMaxSellPrice(specialId);
        GameSpecialPrice instance = GameSpecialPrice.getInstance(specialId, serverGroup, country, minSellPrice, maxSellPrice);
        gameDataService.addGameData(instance);
        return instance;
    }

    public int getSellPrice(int specialId, int serverGroup, CfgCityEntity city) {
        List<GameSpecialPrice> datas = getGameSpecialPrices(serverGroup);
        GameSpecialPrice gameSpecialPrice = datas.stream().filter(tmp -> tmp.getSpecialId() == specialId).findFirst().orElse(null);
        if (null == gameSpecialPrice) {
            gameSpecialPrice = initGameSynthesisSpecialData(specialId, serverGroup);
        }
        Integer highPriceCountry = gameSpecialPrice.getHighPriceCountry();
        int country = CityTool.getCityById(city.getId()).getCountry();
        return highPriceCountry == country ? gameSpecialPrice.getMaxPrice() : gameSpecialPrice.getMinPrice();
    }

    private int getMinSellPrice(int specialId) {
        SpecialEnum specialEnum = SpecialEnum.fromValue(specialId);
        if (null == specialEnum) {
            return 0;
        }
        switch (specialEnum) {
            case HLM:
                return 29845;
            case YG:
                return 49062;
            case NV_ER_JIN:
                return 38760;
            case QTD:
                return 39440;
            case YMT:
                return 53960;
            case LING_ZHU:
                return 60037;
            default:
                return 0;
        }
    }

    public int getMaxSellPrice(int specialId) {
        SpecialEnum specialEnum = SpecialEnum.fromValue(specialId);
        if (null == specialEnum) {
            return 0;
        }
        switch (specialEnum) {
            case HLM:
                return 53934;
            case YG:
                return 85986;
            case NV_ER_JIN:
                return 65280;
            case QTD:
                return 60320;
            case YMT:
                return 90880;
            case LING_ZHU:
                return 97110;
            default:
                return 0;
        }
    }

}
