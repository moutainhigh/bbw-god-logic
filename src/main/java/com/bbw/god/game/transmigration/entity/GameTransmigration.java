package com.bbw.god.game.transmigration.entity;

import com.bbw.common.ID;
import com.bbw.god.game.config.city.CfgCityEntity;
import com.bbw.god.game.config.city.CityTool;
import com.bbw.god.game.data.GameData;
import com.bbw.god.game.data.GameDataType;
import com.bbw.god.game.transmigration.cfg.TransmigrationTool;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 轮回世界
 *
 * @author: suhq
 * @date: 2021/9/10 10:55 上午
 */
@Data

public class GameTransmigration extends GameData implements Serializable {
    private static final long serialVersionUID = 3352313347452828250L;
    /** 区服组ID **/
    private Integer sgId;
    /** 开始 */
    private Date begin;
    /** 结束 */
    private Date end;
    /** 主城属性 */
    private List<Integer> mainCityDefenderTypes;
    /** 守卫 城池ID -> 守卫卡牌 */
    private Map<String, TransmigrationDefender> defenders = new HashMap<>();

    public static GameTransmigration getInstance(int sgId, Date begin, Date end, List<Integer> mainCityDefenderTypes) {
        GameTransmigration instance = new GameTransmigration();
        instance.setId(ID.INSTANCE.nextId());
        instance.setSgId(sgId);
        instance.setBegin(begin);
        instance.setEnd(end);
        instance.setMainCityDefenderTypes(mainCityDefenderTypes);
        return instance;
    }

    /**
     * 获取城池守将
     *
     * @param cityId
     * @return
     */
    public TransmigrationDefender gainCityDefender(int cityId) {
        return defenders.get(cityId + "");
    }


    /**
     * 获取城池属性
     *
     * @param cityId
     * @return
     */
    public int gainDefenderType(int cityId) {
        return getDefenders().get(cityId + "").gainDefenderType();
    }

    /**
     * 获取城池当前的区域属性
     *
     * @param cityId
     * @return
     */
    public int gainCityAreaType(int cityId) {
        CfgCityEntity city = CityTool.getCityById(cityId);
        int cityArea = TransmigrationTool.getCityArea(city);
        return getMainCityDefenderTypes().get(cityArea / 10 - 1);
    }

    @Override
    public GameDataType gainDataType() {
        return GameDataType.TRANSMIGRATION;
    }
}
