package com.bbw.god.city.miaoy.hexagram;

import com.bbw.common.PowerRandom;
import com.bbw.god.game.config.god.GodEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.server.god.GodEventPublisher;
import com.bbw.god.server.god.GodService;
import com.bbw.god.server.god.ServerGod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * 地火明夷卦
 * 获得【随机恶神】(显示神仙名称），且不能使用请神符。
 * @author liuwenbin
 *
 */
@Service
public class Hexagram48Processor extends AbstractHexagram{
    @Autowired
    private GodService godService;
    private static final List<GodEnum> badGod= Arrays.asList(GodEnum.XB,GodEnum.XJ,GodEnum.QS,GodEnum.SIS,GodEnum.SS);
    @Override
    public int getHexagramId() {
        return 48;
    }

    @Override
    public HexagramLevelEnum getHexagramLevel() {
        return HexagramLevelEnum.MID_DOWN;
    }


    @Override
    public void effect(long uid, RDHexagram rd) {
        GameUser gameUser = gameUserService.getGameUser(uid);
        GodEnum godEnum = PowerRandom.getRandomFromList(badGod);
        ServerGod serverGod = godService.getUnrealServerGod(gameUser.getServerId(), godEnum.getValue());
        GodEventPublisher.pubAttachCantUseSSFGodEvent(gameUser.getId(), serverGod, rd,null);
    }

}
