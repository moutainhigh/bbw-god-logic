package com.bbw.god.gameuser.treasure.processor;

import com.bbw.common.PowerRandom;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.treasure.CfgTreasureEntity;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.game.config.treasure.TreasureTool;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.res.ResEventPublisher;
import com.bbw.god.gameuser.treasure.CPUseTreasure;
import com.bbw.god.gameuser.treasure.RDUseMapTreasure;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.server.maou.alonemaou.ServerAloneMaou;
import com.bbw.god.server.maou.alonemaou.ServerAloneMaouService;
import com.bbw.god.server.maou.alonemaou.attackinfo.AloneMaouAttackSummary;
import com.bbw.god.server.maou.alonemaou.attackinfo.AloneMaouAttackSummaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * 魔王要骰子奖励
 *
 * @author suhq
 * @date 2018年11月29日 下午2:05:55
 */
@Service
public class MaouDiceProcessor extends TreasureUseProcessor {
    @Autowired
    private ServerAloneMaouService serverAloneMaouService;
    @Autowired
    private AloneMaouAttackSummaryService aloneMaouAttackSummaryService;

    public MaouDiceProcessor() {
        this.treasureEnum = TreasureEnum.MoWTZ;
        this.isAutoBuy = false;
    }

    @Override
    public void effect(GameUser gu, CPUseTreasure param, RDUseMapTreasure rd) {
        // 包裹开卡包、宝箱
        int treasureId = param.getProId();
        CfgTreasureEntity treasureEntity = TreasureTool.getTreasureById(treasureId);
        WayEnum way = WayEnum.fromName(treasureEntity.getName());
        Integer useTimes = param.getUseTimes();
        for (int i = 0; i < useTimes; i++) {
            int diceNO = PowerRandom.getRandomBySeed(6);
            rd.setMaouDiceNo(diceNO);
            switch (diceNO) {
                case 1:
                case 2:
                    ResEventPublisher.pubEleAddEvent(gu.getId(), 2, way, rd);
                    break;
                case 3:
                    TreasureEventPublisher.pubTAddEvent(gu.getId(), TreasureEnum.MoWH.getValue(), 6, way, rd);
                    break;
                case 4:
                    int sid = gu.getServerId();
                    Optional<ServerAloneMaou> optional = this.serverAloneMaouService.getCurAloneMaou(sid);
                    if (!optional.isPresent()) {
                        throw new ExceptionForClientTip("maoualone.already.end");
                    }
                    AloneMaouAttackSummary myAttack = this.aloneMaouAttackSummaryService.getMyAttackInfo(gu.getId(), optional.get());
                    myAttack.addFreeTime();
                    this.aloneMaouAttackSummaryService.setMyAttackInfo(gu.getId(), optional.get(), myAttack);
                    rd.addAloneMaouFreeTimes(1);
                    break;
                case 5:
                    TreasureEventPublisher.pubTAddEvent(gu.getId(), TreasureEnum.HY.getValue(), 1, way, rd);
                    break;
                case 6:
                    int fightTreasureId = TreasureTool.getRandomFightTreasure().getId();
                    TreasureEventPublisher.pubTAddEvent(gu.getId(), fightTreasureId, 1, way, rd);
                    break;
                default:
                    break;
            }
        }
    }
}
