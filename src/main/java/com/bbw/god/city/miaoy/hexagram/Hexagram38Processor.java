package com.bbw.god.city.miaoy.hexagram;

import com.bbw.god.city.fud.RDArriveFuD;
import com.bbw.god.game.config.city.CfgCityEntity;
import com.bbw.god.game.config.city.CfgRoadEntity;
import com.bbw.god.game.config.city.CityTool;
import com.bbw.god.game.config.city.RoadTool;
import com.bbw.god.gameuser.GameUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 天地否卦
 *
 * 移动到随机地点
 *
 * @author liuwenbin
 *
 */
@Slf4j
@Service
public class Hexagram38Processor extends AbstractHexagram{
    @Override
    public int getHexagramId() {
        return 38;
    }

    @Override
    public HexagramLevelEnum getHexagramLevel() {
        return HexagramLevelEnum.MID_MID;
    }


    @Override
    public void effect(long uid, RDHexagram rd) {
        GameUser gameUser = gameUserService.getGameUser(uid);
        CfgRoadEntity road = RoadTool.getRandomRoad(gameUser.getLocation().getPosition());
        int direction = road.getDirectionByRandom();
        gameUser.moveTo(road.getId(), direction);
        rd.setPos(road.getId());
        rd.setDirection(direction);
        Integer addedGold = rd.getAddedGold();
        arrive(uid,road.getId(),rd);
        CfgCityEntity cityEntity = CityTool.getCityByRoadId(road.getId());
        if (cityEntity.isFD()) {
            rd.setAddedGold(addedGold);
            RDArriveFuD fuD = (RDArriveFuD) rd.getCityInfo();
            if (null == fuD) {
                log.error("福地信息为空返回卦象信息{}", rd.toString());
            }
            fuD.setAddedGold(null == fuD.getAddedGold() ? 0 : fuD.getAddedGold() + 20);
            rd.setCityInfo(fuD);
        }
    }


}
