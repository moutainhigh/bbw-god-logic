package com.bbw.god.city.chengc;

import com.bbw.common.ListUtil;
import com.bbw.god.city.chengc.in.ChengCInProcessor;
import com.bbw.god.city.chengc.investigate.InvestigateLogic;
import com.bbw.god.city.chengc.investigate.RDInvestigate;
import com.bbw.god.controller.AbstractController;
import com.bbw.god.fight.FightSubmitParam;
import com.bbw.god.fight.RDFightResult;
import com.bbw.god.game.CR;
import com.bbw.god.gameuser.card.RDCardGroups;
import com.bbw.god.rd.RDCommon;
import com.bbw.god.rd.RDSuccess;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * 城池相关接口
 *
 * @author suhq
 * @date 2018年11月2日 下午5:53:56
 */
@Slf4j
@RestController
public class ChengCController extends AbstractController {
	@Autowired
	private ChengChiLogic chengChiLogic;
	@Autowired
	private ChengCProcessor chengCProcessor;
	@Autowired
	private ChengCInProcessor chengCInProcessor;
	@Autowired
	private InvestigateLogic investigateLogic;
	@Autowired
	private NightmareLogic nightmareLogic;

	/**
	 * 提交攻击城池结果
	 *
	 * @param param
	 * @return
	 */
	@Deprecated
	@GetMapping(CR.ChengC.SUBMIT_ATTACK_CITY)
	public RDFightResult submitAttackCity(FightSubmitParam param) {
		return null;
	}

	/**
	 * 获取城池交易信息 交易新接口
	 *
	 * @return
	 */
	@GetMapping(CR.ChengC.LIST_CITY_TRADE_INFO)
	public RDTradeInfo listCityTradeInfo() {
		return chengChiLogic.listCityTradeInfo(getUserId());
	}

	/**
	 * 刷新本城可购买的特产
	 *
	 * @return
	 */
	@GetMapping(CR.ChengC.REFRESH_TRADE_SPECIALS)
	public RDTradeRefresh refreshTradeSpecials() {
		return chengChiLogic.refreshTradeSpecials(getUserId());
	}

	/**
	 * 交易-购买特产
	 *
	 * @param specials
	 * @return
	 */
	@GetMapping(CR.ChengC.BUY_SPECIAL)
	public RDTradeBuy buy(String specials) {
		checkStrNotBlank(specials);
		List<Integer> specialIds = ListUtil.parseStrToInts(specials);
		return chengChiLogic.buySpecial(getUserId(), specialIds);
	}

	/**
	 * 交易-卖出特产
	 *
	 * @param specials
	 * @return
	 */
	@RequestMapping(CR.ChengC.SELL_SPECIAL)
	public RDTradeSell sell(String specials) {
		checkStrNotBlank(specials);
		List<Long> specialDataIds = ListUtil.parseStrToLongs(specials);
		RDTradeSell rdTradeSell = chengChiLogic.sellSpecial(getUserId(), specialDataIds);
		rdTradeSell.setReduceSpecial(new ArrayList<>());
		rdTradeSell.setSellingSpecials(new ArrayList<>());
		return rdTradeSell;
	}

	/**
	 * 获取城池振兴信息，用于使用乾坤图展示
	 *
	 * @return
	 */
	@GetMapping(CR.ChengC.GET_PROMOTE_INFO)
	public RDPromoteInfo getPromoteInfo(int pos) {
		return chengCInProcessor.getPromoteInfo(getUserId(), pos);
	}

	/**
	 * 侦查
	 *
	 * @return
	 */
	@GetMapping(CR.ChengC.INVESTIGATE_CITY)
	public RDInvestigate investigate() {
		return investigateLogic.investigate(getUserId());
	}

	/**
	 * 世界跳转（原世界->梦魇世界，梦魇世界->原世界）
	 *
	 * @return
	 */
	@GetMapping(CR.ChengC.CHANGE_WORD)
	public RDChangeWorld changeWorld(Integer worldType) {
		return chengCProcessor.changeWorld(getUserId(), worldType);
	}

	@GetMapping(CR.ChengC.GET_CITY_INFO)
	public RDArriveChengC getCityInfo(int cityId) {
		return chengChiLogic.getCityShowInfo(getUserId(),cityId);
	}

	/**
	 * 获取当前城池的 城池访问信息
	 * @return
	 */
	@GetMapping(CR.ChengC.ARRIVE_CITY_INFO)
	public RDArriveChengC arriveCityGetInfo() {
		return chengChiLogic.arriveCity(getUserId());
	}

	@GetMapping(CR.ChengC.GAIN_LEVEL_AWARD)
	public RDCommon gainLevelAward(int cityId, int level){
		return chengChiLogic.gainLevelAward(getUserId(),cityId,level);
	}

	/**
	 * 一键购买
	 * @return
	 */
	@GetMapping(CR.ChengC.AUTO_BUY_SPECIAL)
	public RDTradeBuy autoBuy() {
		return chengChiLogic.autoBuySpecial(getUserId());
	}
	/**
	 * 一键出售
	 * @return
	 */
	@GetMapping(CR.ChengC.AUTO_SELL_SPECIAL)
	public RDTradeSell autoSell() {
		return chengChiLogic.autoSellSpecial(getUserId());
	}

	/**
	 * 设置梦魇攻城卡组
	 * @return
	 */
	@RequestMapping(CR.ChengC.SET_ATTACK_CARD_GROUP)
	public RDSuccess setAttackCardGroup(String cardIds, Integer type){
		nightmareLogic.setAttackCardGroup(getUserId(),cardIds,type);
		return new RDCommon();
	}

	/**
	 * 设置梦魇攻城卡组
	 *
	 * @return
	 */
	@RequestMapping(CR.ChengC.GET_ATTACK_CARD_GROUP)
	public RDCardGroups getAttackCardGroup() {
		return nightmareLogic.getAttackCardGroup(getUserId());
	}

	/**
	 * 同步梦魇攻城卡组
	 *
	 * @return
	 */
	@RequestMapping(CR.ChengC.SYN_ATTACK_CARD_GROUP)
	public RDCardGroups synAttackCardGroup() {
		return nightmareLogic.synAttackCardGroup(getUserId());
	}



}