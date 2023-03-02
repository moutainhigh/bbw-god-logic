package com.bbw.god.gameuser.kunls;

import com.bbw.god.controller.AbstractController;
import com.bbw.god.game.CR;
import com.bbw.god.gameuser.card.equipment.rd.RdCardZhiBao;
import com.bbw.god.gameuser.kunls.rd.RdInfusionInfo;
import com.bbw.god.gameuser.kunls.rd.RdMakingResult;
import com.bbw.god.rd.RDCommon;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 昆仑山请求接口
 *
 * @author: huanghb
 * @date: 2022/9/15 11:30
 */
@Api(tags = {"昆仑山接口"})
@RestController
public class KunLSCtrl extends AbstractController {
    @Autowired
    private KunLSInfusionLogic kunLSInfusionLogic;
    @Autowired
    private KunLSMakingLogic kunLSMakingLogic;
    @Autowired
    private KunLSRefineLogic kunLSRefineLogic;

    /**
     * 炼制
     *
     * @param zhiBaoDataId     献祭至宝zhiBaoDataId
     * @param essentials       材料id=》材料数量   必选材料 结构为 材料id，材料数量；材料id，材料数量
     * @param mapQuality       图纸品质
     * @param optionals        材料id=》材料数量   可选材料 结构为 材料id，材料数量；材料id，材料数量
     * @param upgradeMaterials 材料id=》材料数量 升阶材料 结构为 材料id，材料数量；材料id，材料数量
     * @return
     */
    @ApiOperation(value = "炼制")
    @GetMapping(CR.KunLS.MAKING)
    public RdMakingResult making(long zhiBaoDataId, Integer mapQuality, String essentials, String optionals, String upgradeMaterials) {

        return kunLSMakingLogic.making(getUserId(), zhiBaoDataId, mapQuality, essentials, optionals, upgradeMaterials);
    }

    /**
     * 进入注灵室
     *
     * @return
     */
    @ApiOperation(value = "进入注灵")
    @GetMapping(CR.KunLS.ENTER_INFUSION)
    public RdInfusionInfo enterInfusion() {
        return kunLSInfusionLogic.enterInfusion(getUserId());
    }

    /**
     * 注灵 可选材料(本源材料) 结构为 材料id，材料数量；材料id，材料数量
     *
     * @param embryoType
     * @param optionals
     * @param infusionPosition
     * @return
     */
    @ApiOperation(value = "注灵")
    @GetMapping(CR.KunLS.INFUSION)
    public RdInfusionInfo infusion(Integer embryoType, String optionals, Integer infusionPosition) {
        return kunLSInfusionLogic.infusion(getUserId(), embryoType, optionals, infusionPosition);
    }

    /**
     * 至宝出世
     *
     * @return
     */
    @ApiOperation(value = "至宝出世")
    @GetMapping(CR.KunLS.BORN)
    public RdCardZhiBao born() {
        return kunLSInfusionLogic.born(getUserId());
    }


    /**
     * 提炼
     *
     * @param zhiBaoDataId 至宝数据id
     * @param embryoType   至宝胚类型
     * @return
     */
    @ApiOperation(value = "提炼")
    @GetMapping(CR.KunLS.REFINE)
    public RDCommon refine(long zhiBaoDataId, Integer embryoType) {
        return kunLSRefineLogic.refine(getUserId(), zhiBaoDataId, embryoType);
    }
}
