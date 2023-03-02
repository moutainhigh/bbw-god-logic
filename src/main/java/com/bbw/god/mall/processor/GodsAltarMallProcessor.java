package com.bbw.god.mall.processor;

import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.mall.CfgMallEntity;
import com.bbw.god.game.config.mall.MallEnum;
import com.bbw.god.game.config.mall.MallTool;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.mall.RDMallList;
import com.bbw.god.mall.UserMallRecord;
import com.bbw.god.rd.RDCommon;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 封神祭坛
 *
 * @author fzj
 * @date 2022/5/10 11:45
 */
@Service
public class GodsAltarMallProcessor extends AbstractMallProcessor {

    GodsAltarMallProcessor() {
        this.mallType = MallEnum.GODS_ALTAR;
    }

    @Override
    public RDMallList getGoods(long guId) {
        RDMallList rd = new RDMallList();
        List<CfgMallEntity> fMalls = MallTool.getMallConfig().getGodsAltarMalls().stream()
                .sorted(Comparator.comparing(CfgMallEntity::getId)).collect(Collectors.toList());
        toRdMallList(guId, fMalls, false, rd);
        return rd;
    }

    @Override
    public void deliver(long guId, CfgMallEntity mall, int buyNum, RDCommon rd) {
        TreasureEventPublisher.pubTAddEvent(guId, mall.getGoodsId(), buyNum, WayEnum.GODS_ALTAR_MALL, rd);
    }

    @Override
    protected List<UserMallRecord> getUserMallRecords(long guId) {
        List<UserMallRecord> userMallRecords = this.mallService.getUserMallRecord(guId, this.mallType);
        return userMallRecords.stream().filter(UserMallRecord::ifValid).collect(Collectors.toList());
    }
}
