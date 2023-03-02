package com.bbw.god.city.miaoy.hexagram;

import com.bbw.god.game.config.god.GodEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.server.god.GodEventPublisher;
import com.bbw.god.server.god.GodService;
import com.bbw.god.server.god.ServerGod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 火山旅卦
 *
 * 获得神仙-衰神
 *
 * @author liuwenbin
 *
 */
@Service
public class Hexagram62Processor extends AbstractHexagram{
    @Autowired
    private GodService godService;
    @Override
    public int getHexagramId() {
        return 62;
    }

    @Override
    public HexagramLevelEnum getHexagramLevel() {
        return HexagramLevelEnum.DOWN_DOWN;
    }


    @Override
    public void effect(long uid, RDHexagram rd) {
        GameUser gameUser = gameUserService.getGameUser(uid);
        ServerGod serverGod = this.godService.getUnrealServerGod(gameUser.getServerId(), GodEnum.SS.getValue());
        GodEventPublisher.pubAttachHexagramGodEvent(gameUser.getId(), serverGod, rd);
    }


}
