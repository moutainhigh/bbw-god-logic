package com.bbw.god.city.yeg;

import com.bbw.god.city.miaoy.hexagram.HexagramBuffEnum;
import com.bbw.god.city.miaoy.hexagram.HexagramBuffService;
import com.bbw.god.city.miaoy.hexagram.event.HexagramEventPublisher;
import com.bbw.god.event.BaseEventParam;
import com.bbw.god.gameuser.GameUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author 作者 ：lwb
 * @version 创建时间：2020年2月24日 上午10:10:52 类说明 野怪分发工厂
 */
@Service
public class YeGFightProcessorFactory {

	@Autowired
	private List<IYegFightProcessor> iYegFightProcessors;
	@Autowired
	private HexagramBuffService hexagramBuffService;

	/**
	 * 获得野怪处理器
	 * 
	 * @param fightTypeEnum
	 * @return
	 */
	public IYegFightProcessor makeYeGFightProcessor(YeGuaiEnum fightTypeEnum) {
		for (IYegFightProcessor fightProcessor : iYegFightProcessors) {
			if (fightProcessor.isMatch(fightTypeEnum)) {
				return fightProcessor;
			}
		}
		return null;
	}

	public IYegFightProcessor randomYeGFightProcessor(GameUser gameUser) {
		long uid = gameUser.getId();
		YeGuaiEnum type = YeGuaiEnum.randomYeGuai(gameUser.getStatus().ifInTransmigrateWord());
		if (hexagramBuffService.isHexagramBuff(uid, HexagramBuffEnum.HEXAGRAM_43.getId())) {
			type = YeGuaiEnum.YG_ELITE;
			HexagramEventPublisher.pubHexagramBuffDeductEvent(new BaseEventParam(uid), HexagramBuffEnum.HEXAGRAM_43.getId(), 1);
		}
		IYegFightProcessor iProcessor = makeYeGFightProcessor(type);
		if (iProcessor == null || !iProcessor.open(uid)) {
			return makeYeGFightProcessor(YeGuaiEnum.YG_NORMAL);
		}
		return iProcessor;
	}
}
