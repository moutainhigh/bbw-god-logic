package com.bbw.god.server.flx;

import java.util.List;

import com.bbw.god.event.BaseEventParam;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.server.flx.event.FlxEventPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bbw.common.ListUtil;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.city.CityLogic;
import com.bbw.god.controller.AbstractController;
import com.bbw.god.game.CR;
import com.bbw.god.game.config.city.CityTypeEnum;
import com.bbw.god.rd.RDCommon;
import com.bbw.god.validator.GodValidator;

/**
 * 富临轩入口
 * 
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-03-14 14:05
 */
@RestController
public class FlxCtrl extends AbstractController {
	@Autowired
	private CityLogic cityLogic;
	@Autowired
	private FuLXProcessor fuLXProcessor;

	/**
	 * 数馆投注
	 *
	 * @param param
	 * @return
	 */
	@GetMapping(CR.City.FLX_BET_SG)
	public RDCommon betSG(CPCaiShuZiBet param) {
		if (param.getBetNum() < 0) {
			throw ExceptionForClientTip.fromi18nKey("flx.caishuzi.need.num");
		}
		GodValidator.validateEntity(param);
		RDCommon rdCommon = cityLogic.cityHandleProcessorDispatch(getUserId(), param, CityTypeEnum.FLX);
		FlxEventPublisher.pubCaiShuZiBetEvent(new BaseEventParam(getUserId(), WayEnum.FLX_SG, rdCommon));
		return rdCommon;
	}

	/**
	 * 元素馆投注
	 * 
	 * @param betEles
	 * @return
	 */
	@GetMapping(CR.City.FLX_BET_YSG)
	public RDCommon betYSG(String betEles) {
		//
		checkStrNotBlank(betEles);
		if (3 != betEles.split(",").length) {
			throw new ExceptionForClientTip("city.flx.ysg.param.required");
		}
		List<Integer> eles = ListUtil.parseStrToInts(betEles);
		CPYaYaLeGBet param = new CPYaYaLeGBet(eles.get(0), eles.get(1), eles.get(2));
		RDCommon rdCommon = cityLogic.cityHandleProcessorDispatch(getUserId(), param, CityTypeEnum.FLX);
		FlxEventPublisher.pubYaYaLeBetEvent(new BaseEventParam(getUserId(), WayEnum.FLX_SG, rdCommon));
		return rdCommon;
	}

	/**
	 * 获得福临轩往期开奖结果
	 * 
	 * @return
	 */
	@GetMapping(CR.City.FLX_LIST_LAST_FLX_RESULTS)
	public RDFlxBetResults listLastFlxResults(int type) {
		return fuLXProcessor.listLastFlxResults(getServerId(), type);
	}

}
