package com.bbw.god.city.miaoy.hexagram;

import com.bbw.god.game.config.god.GodEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.server.god.GodEventPublisher;
import com.bbw.god.server.god.GodService;
import com.bbw.god.server.god.ServerGod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 泽火革卦
 *
 * 获得神仙-天将，且有效战斗为十场
 *
 * @author liuwenbin
 *
 */
@Service
public class Hexagram11Processor extends AbstractHexagram{
    @Autowired
    private GodService godService;
    @Override
    public int getHexagramId() {
        return 11;
    }

    @Override
    public HexagramLevelEnum getHexagramLevel() {
        return HexagramLevelEnum.UP_UP;
    }


    @Override
    public void effect(long uid, RDHexagram rd) {
        GameUser gameUser = gameUserService.getGameUser(uid);
        ServerGod serverGod = this.godService.getUnrealServerGod(gameUser.getServerId(), GodEnum.TJ.getValue());
        GodEventPublisher.pubAttachHexagramGodEvent(gameUser.getId(), serverGod, rd,GodEnum.TJ.getEffect()*10);
    }


}
