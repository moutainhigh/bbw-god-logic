package com.bbw.god.gameuser.guide.v1;

import com.bbw.common.PowerRandom;
import com.bbw.god.event.EventParam;
import com.bbw.god.game.config.StarEnum;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.card.CfgCardEntity;
import com.bbw.god.game.config.god.GodEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.card.UserCardService;
import com.bbw.god.gameuser.card.event.CardEventPublisher;
import com.bbw.god.gameuser.god.UserGod;
import com.bbw.god.gameuser.guide.GuideConfig;
import com.bbw.god.gameuser.guide.NewerGuideService;
import com.bbw.god.rd.RDAdvance;
import com.bbw.god.rd.RDCommon;
import com.bbw.god.server.god.*;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author suhq
 * @description: 新手引导神仙切片
 * @date 2019-12-26 14:41
 **/
@Aspect
@Component
public class NewerGuideGodAspect {
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private GodService godService;
    @Autowired
    private UserCardService userCardService;
    @Autowired
    private GuideConfig guideConfig;
    @Autowired
    private NewerGuideService newerGuideService;


    @Around("execution(* com.bbw.god.server.god.GodListener.attachGod(..))")
    public void attachGod(ProceedingJoinPoint point) throws Throwable {
        Object[] args = point.getArgs();
        GodAttachEvent event = (GodAttachEvent) args[0];
        EventParam<Integer> ep = (EventParam<Integer>) event.getSource();
        RDAdvance rd = (RDAdvance) ep.getRd();
        if (!this.newerGuideService.isPassNewerGuide(ep.getGuId())) {
            GameUser gu = this.gameUserService.getGameUser(ep.getGuId());
            attachGodAsNewerGuide(gu, rd);
            // 有神仙附体
            if (rd.getAttachedGod() != null) {
                RDAttachGod rdAttachGod = RDAttachGod.fromRDCommon(rd);
                rd.setAttachGod(rdAttachGod);
            }
            // 兼容旧版本
            rd.setGodRemainStep(rd.getGodRemainCell());
            return;
        }
        point.proceed();

    }

    @Around("execution(* com.bbw.god.server.god.processor.DFSProcessor.processor(..))")
    public void handleGodAttachGuide(ProceedingJoinPoint point) throws Throwable {
        Object[] args = point.getArgs();
        GameUser gu = (GameUser) args[0];
        UserGod userGod = (UserGod) args[1];
        RDCommon rd = (RDCommon) args[2];
        NewerGuideEnum guide = NewerGuideEnum.fromValue(this.newerGuideService.getNewerGuide(gu.getId()));
        if (guide == NewerGuideEnum.XIANRENDONG) {
            rd.setGodAttachInfo(userGod.getBaseId());
            List<Integer> cardIds = getCardsForDFS(gu.getId(), gu.getRoleInfo().getCountry());
            CardEventPublisher.pubCardAddEvent(gu.getId(), cardIds, WayEnum.DFS, "遇到" + WayEnum.DFS.getName(), rd);
            return;
        }
        point.proceed();
    }

    private List<Integer> getCardsForDFS(long guId, int country) {
        // 所有未拥有的卡牌
        List<CfgCardEntity> notOwnCards = this.userCardService.getNotOwnCards(guId, StarEnum.One.getValue(), Arrays.asList(0));
        // 未拥有的属性卡
        List<CfgCardEntity> typeNotOwnCards = notOwnCards.stream()
                .filter(card -> card.getType() == country).collect(Collectors.toList());
        Optional<Integer> cardIdOp = PowerRandom.getRandomsFromList(typeNotOwnCards, 1).stream().map(CfgCardEntity::getId).findFirst();
        if (!cardIdOp.isPresent()) {
            return new ArrayList<>();
        }
        int cardId = cardIdOp.get();
        List<Integer> awardCards = new ArrayList<>();
        awardCards.add(cardId);
        awardCards.add(this.guideConfig.getDropCardsAsDfs().get(country / 10 - 1));
        return awardCards;
    }

    /**
     * 新手引导神仙
     *
     * @param gameUser
     * @param rd
     */
    private void attachGodAsNewerGuide(GameUser gameUser, RDAdvance rd) {
        int newerGuide = this.newerGuideService.getUserNewerGuide(gameUser.getId()).getNewerGuide();
        if (newerGuide == NewerGuideEnum.XIANRENDONG.getStep()) {
            ServerGod serverGod = this.godService.getUnrealServerGod(gameUser.getServerId(), GodEnum.DFS.getValue());// 生成配置
            GodEventPublisher.pubAttachNewGodEvent(gameUser.getId(), serverGod, rd);
        } else if (newerGuide > NewerGuideEnum.XIANRENDONG.getStep()) {
            Optional<UserGod> userGod = this.godService.getAttachGod(gameUser);
            if (userGod.isPresent()) {
                UserGod uGod = userGod.get();
                uGod.setRemainStep(uGod.getRemainStep() - 1);
                this.gameUserService.updateItem(uGod);
                if (uGod.getRemainStep() > 0) {
                    rd.setGodRemainCell(uGod.getRemainStep());
                }
            }
        }
    }
}
