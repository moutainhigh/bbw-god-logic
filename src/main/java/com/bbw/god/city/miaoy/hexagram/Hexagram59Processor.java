package com.bbw.god.city.miaoy.hexagram;

import com.bbw.god.game.config.TypeEnum;
import com.bbw.god.game.config.city.CfgRoadEntity;
import com.bbw.god.game.config.city.RoadTool;
import com.bbw.god.gameuser.GameUser;
import org.springframework.stereotype.Service;

/**
 * 水山蹇卦
 *
 * 随机传送到水区，且接下来2个回合原地不动
 *
 * @author liuwenbin
 *
 */
@Service
public class Hexagram59Processor extends AbstractHexagram{
    @Override
    public int getHexagramId() {
        return 59;
    }

    @Override
    public HexagramLevelEnum getHexagramLevel() {
        return HexagramLevelEnum.DOWN_DOWN;
    }


    @Override
    public void effect(long uid, RDHexagram rd) {
        GameUser gameUser = gameUserService.getGameUser(uid);
        CfgRoadEntity mainCityRoad = RoadTool.getRandomRoadByProperty(TypeEnum.Water);
        int direction = mainCityRoad.getDirectionByRandom();
        rd.setPos(mainCityRoad.getId());
        rd.setDirection(direction);
        gameUser.moveTo(mainCityRoad.getId(), direction);
        addHexagramBuff(uid,2,rd);
        arrive(uid,mainCityRoad.getId(),rd);
    }


}
