package com.bbw.god.city.miaoy.hexagram;

import com.bbw.common.PowerRandom;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserShakeLogic;
import com.bbw.god.rd.RDAdvance;
import com.bbw.god.road.PathRoad;
import com.bbw.god.road.RoadPathService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * 山风蛊卦
 *
 * 掷一个骰子，后退所示点数
 *
 * @author liuwenbin
 *
 */
@Service
public class Hexagram42Processor extends AbstractHexagram{
    @Autowired
    private RoadPathService roadPathService;
    @Autowired
    private GameUserShakeLogic shakeLogic;

    @Override
    public int getHexagramId() {
        return 42;
    }

    @Override
    public HexagramLevelEnum getHexagramLevel() {
        return HexagramLevelEnum.MID_MID;
    }


    @Override
    public void effect(long uid, RDHexagram rd) {
//        TimeLimitCacheUtil.setArriveCache(uid,null);
        int seed = PowerRandom.getRandomBySeed(6);
        seed=1;
        GameUser gu = gameUserService.getGameUser(uid);
        int dir=gu.getLocation().getDirection();
        switch (dir){
            case 1: dir=4;break;
            case 4: dir=1;break;
            case 3:
                int last=gu.getLocation().getLastDirection();
                if (last==2){
                    dir=PowerRandom.getRandomFromList(Arrays.asList(1,3,4));break;
                }
                dir=last;
                break;
            default:
        }
        List<PathRoad> roadPath = roadPathService.getAssignPath(gu.getLocation().getPosition(), dir, seed);
        RDAdvance rdAdvance=new RDAdvance();
        shakeLogic.getRoads(gu,roadPath, WayEnum.HEXAGRAM,rdAdvance,false);
        rdAdvance.setRandoms(Arrays.asList(seed));
        rd.setShakeDice(rdAdvance);
        gu = gameUserService.getGameUser(uid);
        arrive(uid,gu.getLocation().getPosition(),rd);
    }


}
