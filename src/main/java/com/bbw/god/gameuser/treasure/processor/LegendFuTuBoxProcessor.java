package com.bbw.god.gameuser.treasure.processor;

import com.bbw.common.CloneUtil;
import com.bbw.common.ListUtil;
import com.bbw.common.PowerRandom;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.gameuser.UserCfgObj;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.gameuser.yuxg.Enum.FuTuTypeEnum;
import com.bbw.god.gameuser.yuxg.UserYuXGService;
import com.bbw.god.gameuser.yuxg.YuXGTool;
import com.bbw.god.gameuser.yuxg.cfg.CfgFuTuEntity;
import com.bbw.god.rd.RDCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 传奇符图宝箱
 *
 * @author: huanghb
 * @date: 2023/2/24 9:31
 */
@Service
public class LegendFuTuBoxProcessor {
    @Autowired
    private UserYuXGService userYuXGService;
    /** 符图最低品质 */
    private static final Integer FU_TU_MIN_QUALITY = 3;

    /**
     * 下发随机符图
     *
     * @param uid
     * @param num
     * @param way
     * @param rd
     */
    public void deliverRandomFuTu(long uid, int num, WayEnum way, RDCommon rd) {
        CfgFuTuEntity cfgFuTuEntity = getOpenFuTu(uid);
        int fuTuId = cfgFuTuEntity.getFuTuId();
        TreasureEventPublisher.pubTAddFuTuEvent(uid, fuTuId, num, way, rd);
    }

    /**
     * 获取开到的卡
     *
     * @param uid
     * @return
     */
    private CfgFuTuEntity getOpenFuTu(long uid) {
        //获得所有三阶以上技能符图
        List<CfgFuTuEntity> allAwardFuTus = YuXGTool.getFuTuByMinQuality(FU_TU_MIN_QUALITY).stream()
                .filter(tmp -> FuTuTypeEnum.SKILLS_FU_TU.getType() == tmp.getType()).collect(Collectors.toList());
        List<Integer> ownFuTuIds = userYuXGService.getUserAllFuTus(uid).stream().map(UserCfgObj::getBaseId)
                .distinct().collect(Collectors.toList());
        List<Integer> yxGExceptFuTus = YuXGTool.getYxGExceptFuTus();
        List<CfgFuTuEntity> cloneAllAwardFuTus = CloneUtil.cloneList(allAwardFuTus);
        cloneAllAwardFuTus.removeIf(tmp -> ownFuTuIds.contains(tmp.getFuTuId()) || yxGExceptFuTus.contains(tmp.getFuTuId()));
        //是否已全部拥有
        if (ListUtil.isEmpty(cloneAllAwardFuTus)) {
            return PowerRandom.getRandomFromList(allAwardFuTus);
        }
        return PowerRandom.getRandomFromList(cloneAllAwardFuTus);
    }
}
