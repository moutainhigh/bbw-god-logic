package com.bbw.god.server.god.processor;

import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.card.CfgCardEntity;
import com.bbw.god.game.config.god.GodEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.card.UserCardService;
import com.bbw.god.gameuser.card.event.CardEventPublisher;
import com.bbw.god.gameuser.god.UserGod;
import com.bbw.god.random.config.RandomKeys;
import com.bbw.god.random.service.RandomCardService;
import com.bbw.god.random.service.RandomParam;
import com.bbw.god.random.service.RandomResult;
import com.bbw.god.rd.RDCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 送子观音
 *
 * @author suhq
 * @date 2018年10月19日 下午2:13:01
 */
@Component
public class SZGYProcessor extends AbstractGodProcessor {
    @Autowired
    private UserCardService userCardService;

    public SZGYProcessor() {
        this.godType = GodEnum.SZGY;
    }

    @Override
    public void processor(GameUser gameUser, UserGod userGod, RDCommon rd) {
//		addGod(gameUser, userGod, rd);
        rd.setGodAttachInfo(userGod.getBaseId());
        CfgCardEntity card = getCardForSZGY(gameUser.getId());
        CardEventPublisher.pubCardAddEvent(gameUser.getId(), card.getId(), WayEnum.SZGY, "遇到" + WayEnum.SZGY.getName(), rd);

    }

    private CfgCardEntity getCardForSZGY(long uid) {
        RandomParam randomParam = new RandomParam();
        randomParam.setExtraCardsToMap(userCardService.getUserCards(uid));
        RandomResult result = RandomCardService.getRandomList(RandomKeys.SZGY, randomParam);
        return result.getFirstCard().get();
    }

}
