package com.bbw.god.gameuser.treasure;

import com.bbw.App;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.mall.CfgMallEntity;
import com.bbw.god.game.config.mall.MallTool;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.biyoupalace.cfg.BYPalaceTool;
import com.bbw.god.gameuser.biyoupalace.cfg.CfgBYPalaceSymbolEntity;
import com.bbw.god.gameuser.businessgang.digfortreasure.DigTreasureService;
import com.bbw.god.gameuser.businessgang.luckybeast.LuckyBeastService;
import com.bbw.god.gameuser.businessgang.luckybeast.LuckyBeastTool;
import com.bbw.god.gameuser.businessgang.luckybeast.RDLuckyBeastInfo;
import com.bbw.god.gameuser.card.CardChecker;
import com.bbw.god.gameuser.card.UserCard;
import com.bbw.god.gameuser.card.UserCardService;
import com.bbw.god.gameuser.res.ResChecker;
import com.bbw.god.gameuser.res.ResEventPublisher;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.gameuser.treasure.processor.TreasureProcessorFactory;
import com.bbw.god.gameuser.treasure.processor.TreasureUseProcessor;
import com.bbw.god.mall.MallLogic;
import com.bbw.god.rd.RDCommon;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
public class UserTreasureLogic {
	@Autowired
	private GameUserService gameUserService;
	@Autowired
	private MallLogic mallLogic;
	@Autowired
	private TreasureProcessorFactory treasureProcessorFactory;
	@Autowired
	private UserCardService userCardService;
	@Autowired
	private UserTreasureService userTreasureService;
	@Autowired
	private DigTreasureService digTreasureService;
	@Autowired
	private LuckyBeastService luckyBeastService;
	@Autowired
	private App app;

	//万能灵石iD
	private static final List<Integer> wnlsIds = Arrays.asList(810, 820, 830, 840, 850);
	//默认法宝使用次数
	private static Integer DEFAULT_TREASURE_USE_TIMES = 1;

	/**
	 * 使用地图法宝
	 *
	 * @param guId
	 * @param param
	 * @return
	 */
	public RDUseMapTreasure useMapTreasure(long guId, CPUseTreasure param) {
		RDUseMapTreasure rd = new RDUseMapTreasure();
		int treasureId = param.getProId();
		// 法宝是否存在
		TreasureChecker.checkIsExist(treasureId);
		GameUser gu = gameUserService.getGameUser(guId);
		// 法宝处理实例
		TreasureUseProcessor tup = treasureProcessorFactory.getTreasureUseProcessor(treasureId);
		// 检查参数
		tup.check(gu, param);
		if (tup.isSelfToDeductTreasure(guId)){
			// 自行实现法宝检测和扣除
			tup.effect(gu, param, rd);
			return rd;
		}
		int needNum = tup.getNeedNum(gu, param.getUseTimes(), WayEnum.fromValue(param.getWay()));
		// 非自动购买的法宝，数量不足给客户端提示
		if (!tup.isAutoBuy()) {
			TreasureChecker.checkIsEnough(treasureId, needNum, guId);
		}
		int ownNum = userTreasureService.getTreasureNum(guId, treasureId);
		int needDeductForClient = needNum;
		int needBuyTime = needNum - ownNum;// 还需要几个
		// 不够自动购买
		if (needBuyTime > 0) {
			needDeductForClient = ownNum;
			CfgMallEntity mallEntity = MallTool.getMallTreasure(treasureId);
			if (mallEntity == null) {
				throw new ExceptionForClientTip("mall.cant.auto.buy");
			}
			RDCommon buyResult = mallLogic.buy(guId, mallEntity.getId(), needBuyTime);
			// 返回消耗的铜钱或者元宝
			rd.setAddedCopper(buyResult.getAddedCopper());
			rd.setAddedGold(buyResult.getAddedGold());
		}
		//法宝使用次数
		Integer treasureUserTime = DEFAULT_TREASURE_USE_TIMES;
		//是否是宝箱类
		if (tup.isChestType()) {
			treasureUserTime = param.getUseTimes();
		}
		// 使用生效
		for (int i = 0; i < treasureUserTime; i++) {
			tup.effect(gu, param, rd);
		}

		TreasureEventPublisher.pubTDeductEvent(guId, treasureId, needNum, WayEnum.TREASURE_USE, rd);
		//招财兽信息
		RDLuckyBeastInfo luckyBeastInfo = luckyBeastService.arriveLuckyBeast(gu.getId());
		if (LuckyBeastTool.getOwnEffectTReasureIds().contains(treasureId) && null != luckyBeastInfo) {
			rd.setArriveLuckyBeast(luckyBeastInfo);
			log.info("使用id为" + treasureId + "的法宝触发招财兽,坐标为" + luckyBeastInfo.getPosition());
		}
		// 返回客户端消耗了几个法宝
		rd.setUseNum(needDeductForClient);

		return rd;
	}

	/**
	 * 使用战斗法宝
	 *
	 * @param guId
	 * @param proId
	 */
	public RDCommon useFightTreasure(long guId, int proId) {
		RDCommon rd = new RDCommon();
		TreasureChecker.checkIsExist(proId);
		TreasureChecker.checkIsEnough(proId, 1, guId);
		TreasureEventPublisher.pubTDeductEvent(guId, proId, 1, WayEnum.TREASURE_USE, rd);
		return rd;
	}

	/**
	 * 升级符箓
	 *
	 * @param param
	 * @return
	 */
	public RDUpdateSymbol updateSymbol(long uid, CPUpdateSymbol param) {
		int symbolId = param.getSymbol();
		// 检查符箓是否满级
		CfgBYPalaceSymbolEntity symbolEntity = BYPalaceTool.getSymbolEntity(symbolId);
		if (symbolEntity.isTopSymbol()) {
			throw new ExceptionForClientTip("treasure.ful.cant.update");
		}

		int upgradeTimes = param.getUpgradeTimes();
		//符箓升级次数是否符合要求
		boolean isUpgradeTimesValid = upgradeTimes == 1 || upgradeTimes == 5 || upgradeTimes == 10;
		if (!isUpgradeTimesValid) {
			throw new ExceptionForClientTip("request.param.not.valid");
		}
		int cardId = param.getCardId();
		//检测卡牌是否拥有
		if (param.getCardId() != 0) {
			UserCard ownCard = userCardService.getUserCard(uid, cardId);
			CardChecker.checkIsOwn(ownCard);
		}
		//检测符箓是否拥有
		UserTreasure userSymbol = userTreasureService.getUserTreasure(uid, symbolId);
		boolean isOwnSymbol = null == userSymbol || userSymbol.gainTotalNum() == 0;
		if (isOwnSymbol) {
			throw new ExceptionForClientTip("treasure.not.exist", param.getSymbol());
		}

		//计算所需的铜钱,符箓和万能符箓
		long needCopper = symbolEntity.getCopperToNext() * upgradeTimes;
		// 每级升级一次需要符箓为3
		int perLevelNeedSymbol = 3;
		int totalNeedSymbol = perLevelNeedSymbol * upgradeTimes;
		//检测升级符箓是否装配，已装配则需要的符箓减一；等于0表示无装配，大于0表示已装配
		if (cardId > 0) {
			totalNeedSymbol--;
		}
		int needWanNFL = 0;
		boolean isUseWanNFL = param.isUseWanNFL();
		if (isUseWanNFL && totalNeedSymbol > userSymbol.gainTotalNum()) {
			//使用符箓升级,不够的使用万能符箓
			needWanNFL = totalNeedSymbol - userSymbol.gainTotalNum();
			totalNeedSymbol = userSymbol.gainTotalNum();
		}

		//检测铜钱 符箓,万能符箓是否足够；
		GameUser gu = gameUserService.getGameUser(uid);
		ResChecker.checkCopper(gu, needCopper);
		TreasureChecker.checkIsEnough(symbolId, totalNeedSymbol, uid);
		if (isUseWanNFL) {
			TreasureChecker.checkIsEnough(TreasureEnum.WangNFL.getValue(), needWanNFL, uid);

		}

		//处理需要的资源道具
		RDUpdateSymbol rd = new RDUpdateSymbol();
		ResEventPublisher.pubCopperDeductEvent(uid, needCopper, WayEnum.UPDATE_SYMBOL, rd);
		TreasureEventPublisher.pubTDeductEvent(uid, symbolId, totalNeedSymbol, WayEnum.UPDATE_SYMBOL, rd);
		TreasureEventPublisher.pubTDeductEvent(uid, TreasureEnum.WangNFL.getValue(), needWanNFL, WayEnum.UPDATE_SYMBOL, rd);

		//更新符箓升级相关数据
		CfgBYPalaceSymbolEntity nextSymbolEntity = BYPalaceTool.getNextSymbolEntity(symbolId);
		//升级符箓符箓,等于0表示无装配，大于0表示已装配
		if (cardId > 0) {
			//升级装备的符箓
			UserCard uc = userCardService.getUserCard(uid, cardId);
			uc.updateSymbol(nextSymbolEntity);
			gameUserService.updateItem(uc);
		} else if (cardId == 0) {
			// 升级未装备的符箓
			TreasureEventPublisher.pubTAddEvent(uid, nextSymbolEntity.getId(), upgradeTimes, WayEnum.UPDATE_SYMBOL, rd);
		}

		//返回需要的符箓和万能符箓
		rd.setUseFLNum(totalNeedSymbol);
		rd.setUseWanNFLNum(needWanNFL);
		return rd;
	}

	/**
	 * 查看奖励内容
	 *
	 * @param treasureId
	 * @return
	 */
	public RDSeeAward seeAward(Integer treasureId) {
		TreasureUseProcessor tup = treasureProcessorFactory.getTreasureUseProcessor(treasureId);
		return tup.seeAward(treasureId);
	}

	/**
	 * 合成法宝
	 * @param treasureId  消耗的灵石ID
	 * @param num 消耗的灵石数量
	 * @return
	 */
	public RDCommon synthesisTreasure(long uid,int treasureId,int num){
		RDCommon rd=new RDCommon();
		if (!wnlsIds.contains(treasureId) || treasureId==TreasureEnum.WNLS5.getValue()){
			//不是万能灵石 不能合成
			throw new ExceptionForClientTip("treasure.synthesis.fail");
		}
		int newTid= wnlsIds.get(wnlsIds.indexOf(treasureId)+1);
		TreasureChecker.checkIsEnough(treasureId,num,uid);
		int change=num/3;
		TreasureEventPublisher.pubTDeductEvent(uid,treasureId,change*3,WayEnum.SYNTHESIS_LINGSHI,rd);
		TreasureEventPublisher.pubTAddEvent(uid,newTid,change,WayEnum.SYNTHESIS_LINGSHI,rd);
		return rd;
	}
}
