package com.bbw.god.server.god.processor;

import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.god.GodEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.god.UserGod;
import com.bbw.god.gameuser.res.ResEventPublisher;
import com.bbw.god.rd.RDCommon;
import org.springframework.stereotype.Component;

/**
 * 小福神
 *
 * @author suhq
 * @date 2018年10月19日 下午2:11:52
 */
@Component
public class XFSProcessor extends AbstractGodProcessor {

    public XFSProcessor() {
        this.godType = GodEnum.XFS;
    }

    @Override
    public void processor(GameUser gameUser, UserGod userGod, RDCommon rd) {
//		addGod(gameUser, userGod, rd);
        rd.setGodAttachInfo(userGod.getBaseId());
        ResEventPublisher.pubEleAddEvent(gameUser.getId(), 1, WayEnum.XFS, rd);

    }

}
