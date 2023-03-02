package com.bbw.god.server.god.processor;

import com.bbw.god.game.config.god.GodEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.god.UserGod;
import com.bbw.god.rd.RDCommon;
import org.springframework.stereotype.Component;

/**
 * 虾兵
 *
 * @author suhq
 * @date 2018年10月19日 下午2:12:25
 */
@Component
public class XBProcessor extends AbstractGodProcessor {

    public XBProcessor() {
        this.godType = GodEnum.XB;
    }

    @Override
    public void processor(GameUser gameUser, UserGod userGod, RDCommon rd) {
//		addGod(gameUser, userGod, rd);
        rd.setGodAttachInfo(userGod.getBaseId());
    }

}
