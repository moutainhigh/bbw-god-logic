package com.bbw.god.city.yed;

import com.bbw.god.activity.monthlogin.MonthLoginLogic;
import com.bbw.god.city.CityProcessorFactory;
import com.bbw.god.city.UserCityService;
import com.bbw.god.city.chengc.ChengCProcessor;
import com.bbw.god.city.miaoy.hexagram.HexagramBuffEnum;
import com.bbw.god.city.miaoy.hexagram.HexagramBuffService;
import com.bbw.god.game.flx.FlxYYLTipService;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.card.UserCardService;
import com.bbw.god.gameuser.special.UserSpecialService;
import com.bbw.god.gameuser.treasure.UserTreasureEffectService;
import com.bbw.god.rd.RDAdvance;
import com.bbw.god.server.god.GodService;
import com.bbw.god.server.god.processor.GodProcessorFactory;
import com.bbw.god.server.maou.alonemaou.ServerAloneMaouService;
import com.bbw.god.server.maou.alonemaou.attackinfo.AloneMaouAttackSummaryService;
import com.bbw.mc.m2c.M2cService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author suchaobin
 * @description 基础野地事件处理器
 * @date 2020/6/1 10:46
 **/
@Service
public abstract class BaseYeDEventProcessor {
	@Autowired
	protected YeDProcessor yeDProcessor;
	@Autowired
	protected UserCityService userCityService;
	@Autowired
	protected GodService godService;
	@Autowired
	protected UserSpecialService userSpecialService;
	@Autowired
	protected UserTreasureEffectService userTreasureEffectService;
	@Autowired
	protected FlxYYLTipService yylTipsService;
	@Autowired
	protected GameUserService gameUserService;
	@Autowired
	protected UserCardService userCardService;
	@Autowired
	protected ServerAloneMaouService serverAloneMaouService;
	@Autowired
	protected AloneMaouAttackSummaryService aloneMaouAttackSummaryService;
	@Autowired
	protected CityProcessorFactory cityProcessorFactory;
	@Autowired
	protected M2cService m2cService;
	@Autowired
	protected GodProcessorFactory godProcessorFactory;
	@Autowired
	protected ChengCProcessor chengCProcessor;
	@Autowired
	protected MonthLoginLogic monthLoginLogic;
	@Autowired
	private HexagramBuffService hexagramBuffService;

	/**
	 * 获取当前野地事件id
	 */
	public abstract int getMyId();

	/**
	 * 野地事件生效
	 *
	 * @param gameUser
	 * @param rdArriveYeD
	 * @param rd
	 */
	public abstract void effect(GameUser gameUser, RDArriveYeD rdArriveYeD, RDAdvance rd);


	public boolean hasHexagramBuff(long uid){
		return hexagramBuffService.isHexagramBuff(uid, HexagramBuffEnum.HEXAGRAM_15.getId());
	}
}
