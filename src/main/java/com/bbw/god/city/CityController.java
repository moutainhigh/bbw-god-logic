package com.bbw.god.city;

import com.bbw.common.StrUtil;
import com.bbw.god.city.event.CityEventPublisher;
import com.bbw.god.city.exp.CityExpLogic;
import com.bbw.god.city.lut.CPLtTribute;
import com.bbw.god.city.nvwm.RDNvWM;
import com.bbw.god.city.taiyf.mytaiyf.MYTaiYFProcessor;
import com.bbw.god.controller.AbstractController;
import com.bbw.god.event.BaseEventParam;
import com.bbw.god.game.CR;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.city.CityTypeEnum;
import com.bbw.god.rd.RDCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

/**
 * 各建筑操作接口
 * 
 * @author suhq
 * @date 2018年11月2日 下午5:41:39
 */
@RestController
public class CityController extends AbstractController {

	@Autowired
	private CityLogic cityLogic;

	@Autowired
	private MYTaiYFProcessor myTaiYFProcessor;
	@Autowired
	private CityExpLogic cityExpLogic;

	/**
	 * 庙宇抽签 上上签 上签 中签 下签 庙宇概率（30求贤） 3 48 34 15 庙宇概率（20求宝） 5 50 30 15 庙宇概率（10求财） 20
	 * 35 30 15
	 *
	 * @param type
	 * @return
	 */
	@GetMapping(CR.City.FLX_DRAW_LOTS)
	public RDCommon drawLots(int type) {
		return cityLogic.cityHandleProcessorDispatch(getUserId(), type, CityTypeEnum.MY);
	}

	/**
	 * 黑市购买宝物
	 * 
	 * @return
	 */
	@GetMapping(CR.City.HS_BUY)
	public RDCommon buyHS(int proId) {
		return cityLogic.cityHandleProcessorDispatch(getUserId(), proId, CityTypeEnum.HEIS);
	}

	/**
	 * 女娲庙捐赠铜钱
	 * 
	 * @return
	 */
	@GetMapping(CR.City.NWM_DRAW)
	public RDNvWM donateNWM(int copper) {
		return (RDNvWM) cityLogic.cityHandleProcessorDispatch(getUserId(), copper, CityTypeEnum.NWM);
	}

	/**
	 * 太一府填特产
	 * 
	 * @return
	 */
	@GetMapping(CR.City.TYF_FILL)
	public RDCommon fillTYF(int specialId) {
		return cityLogic.cityHandleProcessorDispatch(getUserId(), specialId, CityTypeEnum.TYF);
	}

	/**
	 * 梦魇太一府兑换
	 *
	 * @return
	 */
	@GetMapping(CR.City.MYTYF_CONVERT)
	public RDCommon convertMYTYF(int cardId) {
		return myTaiYFProcessor.cardSoulConvert(getGameUser(),cardId);
	}

	/**
	 * 客栈招募卡牌
	 * 
	 * @param cardId
	 * @param treasureId
	 * @return
	 */
	@GetMapping(CR.City.KZ_RECRUIT)
	public RDCommon recruitCard(int cardId, String treasureId) {
		treasureId = StrUtil.isBlank(treasureId) ? "0" : treasureId;
		return cityLogic.cityHandleProcessorDispatch(getUserId(), cardId + "," + treasureId, CityTypeEnum.KZ);
	}

	/**
	 * 游商馆购买特产
	 * 
	 * @param specials-id,id
	 * @return
	 */
	@GetMapping(CR.City.YSG_BUY)
	public RDCommon buySpecial(String specials) {
		checkStrNotBlank(specials);
		return cityLogic.cityHandleProcessorDispatch(getUserId(), specials, CityTypeEnum.YSG);
	}

	/**
	 * 鹿台进贡
	 *
	 * @param param
	 * @return
	 */
	@GetMapping(CR.City.LT_TRIBUTE)
	public RDCommon tribute(CPLtTribute param) {
		return cityLogic.cityHandleProcessorDispatch(getUserId(), param, CityTypeEnum.LT);
	}

	/**
	 * 出迷仙洞
	 * 
	 * @param road-id,id,id,id
	 * @return
	 */
	@GetMapping(CR.City.MXD_OUT)
	public RDCommon outMXD(String road) {
		if (StrUtil.isNull(road)) {
			CityEventPublisher.pubOutMxdEvent(new ArrayList<>(), new BaseEventParam(getUserId(), WayEnum.MXD));
			return new RDCommon();
		}
		return cityLogic.cityHandleProcessorDispatch(getUserId(), road, CityTypeEnum.MXD);
	}

	/**
	 * 体验特殊建筑
	 *
	 * @return
	 */
	@GetMapping(CR.City.EXP)
	public RDCityInfo exp() {
		return cityExpLogic.exp(getUserId());
	}

}
