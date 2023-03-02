package com.bbw.god.game.award.impl;

import com.bbw.common.PowerRandom;
import com.bbw.god.game.award.AwardService;
import com.bbw.god.game.award.YuxgAward;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.treasure.TreasureType;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.gameuser.yuxg.YuXGTool;
import com.bbw.god.gameuser.yuxg.cfg.CfgFuTuEntity;
import com.bbw.god.rd.RDCommon;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 玉虚宫奖励
 *
 * @author fzj
 * @date 2021/11/1 11:50
 */
//@Service
public class YuXGAwardService extends AwardService<YuxgAward> {
    @Override
    public void fetchAward(long uid, String awardJson, WayEnum way, String broadcastWayInfo, RDCommon rd) {
        List<YuxgAward> awards = parseAwardJson(awardJson, YuxgAward.class);
        fetchAward(uid, awards, way, broadcastWayInfo, rd);
    }

    /**
     * 发放奖励
     *
     * @param award
     */
    @Override
    public void deliverTreasure(long uid, YuxgAward award, WayEnum way, RDCommon rd) {
        TreasureType treasureType = TreasureType.fromValue(award.getAwardType());
        switch (treasureType) {
            case FUTU:
                deliverFuTu(uid, award, way, rd);
                break;
            case YUSUI:
                deliverYuSui(uid, award, way, rd);
                break;
            default:
                break;
        }
    }

    /**
     * 发放符图
     *
     * @param award
     */
    private void deliverFuTu(long uid, YuxgAward award, WayEnum way, RDCommon rd) {
        int quality = award.getQuality();
        List<Integer> yxGExceptFuTus = YuXGTool.getYxGExceptFuTus();
        // 过滤长生、冷箭符图
        List<Integer> fuTuList = YuXGTool.getAllFuTuInfos().stream()
                .filter(fu -> fu.getQuality() == quality && !yxGExceptFuTus.contains(fu.getFuTuId()))
                .map(CfgFuTuEntity::getFuTuId).collect(Collectors.toList());
        Integer fuTuId = PowerRandom.getRandomFromList(fuTuList);
        TreasureEventPublisher.pubTAddFuTuEvent(uid, fuTuId, award.getNum(), way, rd);
    }

    /**
     * 发放玉髓
     *
     * @param award
     */
    private void deliverYuSui(long uid, YuxgAward award, WayEnum way, RDCommon rd) {
        int quality = award.getQuality();
        Integer yuSuiId = YuXGTool.getYuSuiInfo(quality).getYuSuiId();
        TreasureEventPublisher.pubTAddEvent(uid, yuSuiId, award.getNum(), way, rd);
    }
}