package com.bbw.god.gameuser.kunls;

import com.bbw.common.PowerRandom;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.game.award.AwardService;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.gameuser.card.equipment.UserCardZhiBaoService;
import com.bbw.god.gameuser.card.equipment.data.UserCardZhiBao;
import com.bbw.god.gameuser.kunls.cfg.CfgKunLS;
import com.bbw.god.gameuser.treasure.TreasureChecker;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.rd.RDCommon;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 昆仑山提炼室逻辑
 *
 * @author: huanghb
 * @date: 2022/9/15 11:33
 */
@Slf4j
@Service
public class KunLSRefineLogic {
    @Autowired
    private UserCardZhiBaoService userCardZhiBaoService;
    @Autowired
    private AwardService awardService;

    /**
     * 提炼
     *
     * @param uid
     * @param zhiBaoDataId 至宝的dataId
     * @param embryoType   至宝的id
     * @return
     */
    protected RDCommon refine(long uid, long zhiBaoDataId, Integer embryoType) {
        List<Integer> allZhiBaoType = CfgKunLSTool.getAllZhiBaoType();
        //至宝胚类型检测
        if (0 != embryoType && !allZhiBaoType.contains(embryoType)) {
            throw new ExceptionForClientTip("zhiBao.is.error.type");
        }
        UserCardZhiBao userCardZhiBao = userCardZhiBaoService.getUserCardZhiBao(uid, zhiBaoDataId);
        //提炼至宝是否存在
        if (0 != zhiBaoDataId && null == userCardZhiBao) {
            throw new ExceptionForClientTip("kunLs.refine.zhiBao.no.exist");
        }
        //是否穿戴
        if (0 != zhiBaoDataId && 0 != userCardZhiBao.ifPutOn()) {
            throw new ExceptionForClientTip("zhiBao.is.putedOn");
        }
        //提炼至宝
        if (0 != zhiBaoDataId && null != userCardZhiBao) {
            userCardZhiBaoService.deductZhiBao(uid, zhiBaoDataId);
        }
        RDCommon rd = new RDCommon();
        //提炼至宝胚
        if (0 == zhiBaoDataId) {
            int zhiBaoEmbryoTreasureId = CfgKunLSTool.getEmbryoTreasureIds(embryoType);
            TreasureChecker.checkIsEnough(zhiBaoEmbryoTreasureId, 1, uid);
            TreasureEventPublisher.pubTDeductEvent(uid, zhiBaoEmbryoTreasureId, 1, WayEnum.KUNLS_REFINE, rd);
        }
        List<Award> awards = getRefineAwards(embryoType);
        awardService.sendNeedMergedAwards(uid, awards, WayEnum.KUNLS_REFINE, "", rd);
        return rd;
    }

    /**
     * 获得提炼返回奖励
     *
     * @param embryoType
     * @return
     */
    private List<Award> getRefineAwards(Integer embryoType) {
        CfgKunLS.Refine refine = CfgKunLSTool.getRefine(embryoType);
        List<CfgKunLS.ReturnMaterial> returnMaterials = refine.getReturnMaterials();
        List<Award> awards = new ArrayList<>();
        for (CfgKunLS.ReturnMaterial returnMaterial : returnMaterials) {
            int returnMaterialNum = PowerRandom.getRandomBetween(returnMaterial.getMinNum(), returnMaterial.getMaxNum());
            awards.add(new Award(returnMaterial.getTreasureId(), AwardEnum.FB, returnMaterialNum));
        }
        return awards;
    }
}
