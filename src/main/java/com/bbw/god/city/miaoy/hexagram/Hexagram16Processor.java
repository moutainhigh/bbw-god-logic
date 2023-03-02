package com.bbw.god.city.miaoy.hexagram;

import com.bbw.god.game.config.god.GodEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.server.god.GodEventPublisher;
import com.bbw.god.server.god.GodService;
import com.bbw.god.server.god.ServerGod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 天风姤卦
 *
 * 获得神仙-小福神
 *
 * @author liuwenbin
 *
 */
@Service
public class Hexagram16Processor extends AbstractHexagram{
    @Autowired
    private GodService godService;
    @Override
    public int getHexagramId() {
        return 16;
    }

    @Override
    public HexagramLevelEnum getHexagramLevel() {
        return HexagramLevelEnum.UP_UP;
    }


    @Override
    public void effect(long uid, RDHexagram rd) {
        GameUser gameUser = gameUserService.getGameUser(uid);
        ServerGod serverGod = this.godService.getUnrealServerGod(gameUser.getServerId(), GodEnum.XFS.getValue());
        GodEventPublisher.pubAttachNewGodEvent(gameUser.getId(), serverGod, rd);
    }


}
