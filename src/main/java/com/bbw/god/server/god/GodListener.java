package com.bbw.god.server.god;

import com.bbw.common.DateUtil;
import com.bbw.god.event.EventParam;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.god.GodEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.god.UserGod;
import com.bbw.god.rd.RDAdvance;
import com.bbw.god.server.god.processor.AbstractGodProcessor;
import com.bbw.god.server.god.processor.GodProcessorFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class GodListener {
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private GodService godService;
    @Autowired
    private GodProcessorFactory godProcessorFactory;

    @EventListener
    public void attachGod(GodAttachEvent event) {
        EventParam<Integer> ep = (EventParam<Integer>) event.getSource();
        GameUser gu = this.gameUserService.getGameUser(ep.getGuId());
        RDAdvance rd = (RDAdvance) ep.getRd();
        attachGod(gu, ep.getValue(), rd);
        // 有神仙附体
        if (rd.getAttachedGod() != null) {
            RDAttachGod rdAttachGod = RDAttachGod.fromRDCommon(rd);
            rd.setAttachGod(rdAttachGod);
        }
        // 兼容旧版本
        rd.setGodRemainStep(rd.getGodRemainCell());

    }

    @EventListener
    public void attachNewGod(AttachNewGodEvent event) {
        EventParam<ServerGod> ep = (EventParam<ServerGod>) event.getSource();
        GameUser gu = this.gameUserService.getGameUser(ep.getGuId());
        ServerGod serverGod = ep.getValue();
        // 生成神仙
        UserGod newGod = UserGod.instance(gu.getId(), serverGod);
        newGod.setCanUseSSF(event.isCanUseSSF());
        RDAdvance rd = (RDAdvance) ep.getRd();
        GodEnum godEnum = GodEnum.fromValue(serverGod.getGodId());
        int godExt = 1;
        if (ep.getWay() != null && WayEnum.HEXAGRAM.equals(ep.getWay())) {
            newGod.setAttachWay(ep.getWay());
            if (event.getEffect() != null) {
                if (godEnum.getType() == 20) {
                    newGod.setAttachEndTime(DateUtil.addSeconds(DateUtil.now(), event.getEffect()));
                    rd.setGodRemainTime(event.getEffect() * 1000);
                } else {
                    newGod.setRemainStep(event.getEffect());
                    rd.setGodRemainStep(event.getEffect());
                }
            }
            godExt = 4;
        }
        if (godEnum.equals(GodEnum.XZ)) {
            rd.setGodExt(godExt);
        }
        // 附体新的神仙
        this.godService.attachGod(gu, newGod);
        // 通知
        AbstractGodProcessor godProcessor = this.godProcessorFactory.create(gu.getId(), serverGod.getGodId());
        if (godProcessor != null) {
            godProcessor.processor(gu, newGod, rd);
        }
    }

    /**
     * 日常神仙
     *
     * @param gu
     * @param roadId
     * @param rd
     */
    private void attachGod(GameUser gu, int roadId, RDAdvance rd) {
        Optional<ServerGod> serverGod = this.godService.getGodRemainOnRoad(gu.getServerId(), roadId, gu.getId());//
        // 当前位置有神仙
        if (serverGod.isPresent()) {
            // 附体新的神仙
            GodEventPublisher.pubAttachNewGodEvent(gu.getId(), serverGod.get(), rd);
            return;
        }
        // 当前位置没有神仙，但是身上有生效的神仙
        Optional<UserGod> userGod = this.godService.getAttachGod(gu);//
        if (userGod.isPresent()) {
            UserGod uGod = userGod.get();
            GodEnum godEnum = GodEnum.fromValue(uGod.getBaseId());
            if (godEnum.getType() == 10) {
                // 已附体的步数神仙扣步数
                uGod.deductRemainStep(1);
                this.gameUserService.updateItem(uGod);
                rd.setGodRemainCell(uGod.getRemainStep());
                if (0 == uGod.getRemainStep()) {
                    // 剩下0步，则设置为失效
                    this.godService.setUnvalid(gu, userGod.get());
                }
            }
            if (godEnum.equals(GodEnum.XZ)) {
                rd.setGodExt(uGod.getAttachWay().getValue() == WayEnum.HEXAGRAM.getValue() ? 4 : 1);
            }
        }
    }
}
