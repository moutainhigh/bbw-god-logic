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

import java.util.List;
import java.util.stream.Collectors;

/**
 * 大福神
 *
 * @author suhq
 * @date 2018年10月19日 下午2:13:54
 */
@Component
public class DFSProcessor extends AbstractGodProcessor {
    @Autowired
    private UserCardService userCardService;

    public DFSProcessor() {
        this.godType = GodEnum.DFS;
    }

    @Override
    public void processor(GameUser gu, UserGod userGod, RDCommon rd) {
        rd.setGodAttachInfo(userGod.getBaseId());

        List<Integer> cardIds = getCardsForDFS(gu.getId());
        CardEventPublisher.pubCardAddEvent(gu.getId(), cardIds, WayEnum.DFS, "遇到" + WayEnum.DFS.getName(), rd);
    }

    private List<Integer> getCardsForDFS(long uid) {
        RandomParam randomParam = new RandomParam();
        randomParam.setExtraCardsToMap(userCardService.getUserCards(uid));
        RandomResult result = RandomCardService.getRandomList(RandomKeys.DFS, randomParam);
        return result.getCardList().stream().map(CfgCardEntity::getId).collect(Collectors.toList());
    }

}
