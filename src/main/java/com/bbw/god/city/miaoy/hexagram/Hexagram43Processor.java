package com.bbw.god.city.miaoy.hexagram;

import com.bbw.god.game.config.city.CfgRoadEntity;
import com.bbw.god.game.config.city.RoadTool;
import com.bbw.god.gameuser.GameUser;
import org.springframework.stereotype.Service;

/**
 * 地雷复卦
 *
 * 随机传送到野怪点，且必为精英野怪
 *
 * @author liuwenbin
 *
 */
@Service
public class Hexagram43Processor extends AbstractHexagram{
    @Override
    public int getHexagramId() {
        return 43;
    }

    @Override
    public HexagramLevelEnum getHexagramLevel() {
        return HexagramLevelEnum.MID_MID;
    }


    @Override
    public void effect(long uid, RDHexagram rd) {
        GameUser gameUser = gameUserService.getGameUser(uid);
        CfgRoadEntity mainCityRoad = RoadTool.getRandomYdRoad();
        int direction = mainCityRoad.getDirectionByRandom();
        rd.setPos(mainCityRoad.getId());
        rd.setDirection(direction);
        gameUser.moveTo(mainCityRoad.getId(), direction);
        addHexagramBuff(uid,1,rd);
        arrive(uid,mainCityRoad.getId(),rd);
    }


}
