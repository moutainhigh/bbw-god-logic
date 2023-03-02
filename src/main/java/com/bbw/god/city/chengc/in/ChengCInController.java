package com.bbw.god.city.chengc.in;

import com.bbw.common.StrUtil;
import com.bbw.god.city.chengc.in.RDBuildingOutputs.RDBuildingOutput;
import com.bbw.god.controller.AbstractController;
import com.bbw.god.fight.FightTypeEnum;
import com.bbw.god.fight.RDFightsInfo;
import com.bbw.god.game.CR;
import com.bbw.god.game.config.city.BuildingEnum;
import com.bbw.god.gameuser.guide.NewerGuideService;
import com.bbw.god.rd.RDCommon;
import com.bbw.god.rd.RDSuccess;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 城内各建筑操作接口
 *
 * @author suhq
 * @date 2018年11月2日 下午5:41:39
 */
@RestController
public class ChengCInController extends AbstractController {

	@Autowired
	private ChengCInProcessor chengCInProcessor;

	/**
	 * 进入城池
	 *
	 * @return
	 */
	@GetMapping(CR.ChengCIn.INTO_CITY)
	public RDCityInInfo intoCity() {
		return chengCInProcessor.intoChengC(getUserId());
	}

	/**
	 * 练兵对手战斗数据
	 * @return
	 */
	@Deprecated
	@GetMapping(CR.ChengCIn.TRAINING)
	public RDFightsInfo training(int type) {
		if (type== FightTypeEnum.PROMOTE.getValue()){
			return chengCInProcessor.getPromoteFight(getUserId());
		}
		return chengCInProcessor.getTrainingInfo(getUserId());
	}


	/**
	 * 建筑升级
	 * @param type  建筑类型
	 * @param useTianGongTu
	 * @param useQianKunTu  是否使用乾坤图（法坛升级时需要）
	 * @return
	 */
    @GetMapping(CR.ChengCIn.UPDATE)
    public RDBuildingUpdateInfo update(int type, @RequestParam(defaultValue = "0") Integer useTianGongTu, @RequestParam(defaultValue = "0") Integer useQianKunTu) {
        return chengCInProcessor.updatBuilding(getUserId(), type, useTianGongTu == 1, useQianKunTu == 1);
    }

	/**
	 * 炼丹房升级某张卡牌
	 *
	 * @param cards
	 * @return
	 */
	@GetMapping(CR.ChengCIn.LDF_GET)
	public RDBuildingOutput promoteCard(String cards) {
		return chengCInProcessor.gainBuildingAward(getUserId(), BuildingEnum.LDF, cards);
	}

	/**
	 * 炼宝炉随机得一法宝
	 *
	 * @return
	 */
	@GetMapping(CR.ChengCIn.LBL_GET)
	public RDBuildingOutput lblGet() {
		RDBuildingOutput rd = chengCInProcessor.gainBuildingAward(getUserId(), BuildingEnum.LBL, null);
		// 兼容旧版
		rd.setMessage("");
		return rd;
	}

	/**
	 * 聚贤庄领取卡牌
	 *
	 * @param cardId
	 * @param treasureId
	 * @return
	 */
	@GetMapping(CR.ChengCIn.JXZ_GET_CARD)
	public RDBuildingOutput jxzGetCard(int cardId, String treasureId) {
		if (StrUtil.isBlank(treasureId)) {
			treasureId = "0";
		}
		String param = cardId + "," + treasureId;
		return chengCInProcessor.gainBuildingAward(getUserId(), BuildingEnum.JXZ, param);
	}

	/**
	 * 聚贤庄领取卡牌
	 *
	 * @return
	 */
	@GetMapping(CR.ChengCIn.JXZ_GET)
	public RDBuildingOutput jxzGet(Integer newerGuide) {
			/*if (null != newerGuide) {
				return newerGuideService.gainBuildingAward(getUserId(), BuildingEnum.JXZ, null, newerGuide);
			}*/
		return chengCInProcessor.gainBuildingAward(getUserId(), BuildingEnum.JXZ, null);
	}

	/**
	 * 钱庄领一次税收
	 *
	 * @return
	 */
	@GetMapping(CR.ChengCIn.QZ_GET)
	public RDBuildingOutput qzGet() {
		return chengCInProcessor.gainBuildingAward(getUserId(), BuildingEnum.QZ, null);
	}

	/**
	 * 矿场领取元素
	 *
	 * @param eles
	 * @return
	 */
	@GetMapping(CR.ChengCIn.KC_GET)
	public RDBuildingOutput kcGetEle(String eles) {
		return chengCInProcessor.gainBuildingAward(getUserId(), BuildingEnum.KC, eles);
	}

	/**
	 * 一键领取
	 *
	 * @return
	 */
	@GetMapping(CR.ChengCIn.GET_ALL_OUTPUT)
	public RDBuildingOutputs gainAllOutput() {
		return chengCInProcessor.getAllOutput(getUserId());
	}

	/**
	 * 设定默认的矿场收集顺序
	 *
	 * @param useDefaultKcEles
	 * @return
	 */
	@GetMapping(CR.ChengCIn.SET_DEFAULT_ELES)
	public RDSuccess setDefaultEles(int useDefaultKcEles, String defaultKcEles) {
		return chengCInProcessor.setKC(getUserId(), useDefaultKcEles, defaultKcEles);
	}

    /**
     * 设置炼丹房卡牌
     *
     * @param cardId
     * @return
     */
    @GetMapping(CR.ChengCIn.SET_LDF_CARD)
    public RDSuccess setLdfCard(int cardId) {
        return chengCInProcessor.setLdfCard(getUserId(), cardId);
    }

    /**
     * 解锁法坛
     *
     * @return
     */
    @ApiOperation(value = "解锁法坛")
    @GetMapping(CR.ChengCIn.UNLOCK_FA_TAN)
    public RDCommon unlockFaTan() {
        return chengCInProcessor.unlockFaTan(getUserId());
    }
}
