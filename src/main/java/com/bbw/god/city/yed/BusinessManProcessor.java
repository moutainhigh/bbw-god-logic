package com.bbw.god.city.yed;

import com.bbw.common.DateUtil;
import com.bbw.god.game.config.city.YdEventEnum;
import com.bbw.god.game.config.mall.CfgMallEntity;
import com.bbw.god.game.config.mall.MallTool;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.login.DynamicMenuEnum;
import com.bbw.god.rd.RDAdvance;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author suchaobin
 * @description 云游商人处理器
 * @date 2020/6/2 10:27
 **/
@Service
public class BusinessManProcessor extends BaseYeDEventProcessor {
	/**
	 * 获取当前野地事件id
	 */
	@Override
	public int getMyId() {
		return YdEventEnum.YYSR.getValue();
	}

	/**
	 * 野地事件生效
	 *
	 * @param gameUser
	 * @param rdArriveYeD
	 * @param rd
	 */
	@Override
	public void effect(GameUser gameUser, RDArriveYeD rdArriveYeD, RDAdvance rd) {
		AdventureGoodsEnum goodsEnum = AdventureGoodsEnum.getRandom();
		CfgMallEntity mallEntity = MallTool.getAdventureMallTreasure(goodsEnum.getGoodsId());
		if (null == mallEntity) {
			return;
		}
		long uid = gameUser.getId();
		UserAdventure adventure = UserAdventure.instanceBusiness(gameUser.getId(), AdventureType.YYSR.getValue(),
				mallEntity.getId(), mallEntity.getGoodsId());
		gameUserService.addItem(uid, adventure);
		rdArriveYeD.setGoodsId(mallEntity.getGoodsId());
		rdArriveYeD.setMallId(mallEntity.getId());
		rdArriveYeD.setNum(mallEntity.getNum());
		rdArriveYeD.setOriginalPrice(mallEntity.getOriginalPrice());
		rdArriveYeD.setCurrentPrice(mallEntity.getPrice());
		Date generateTime = adventure.getGenerateTime();
		Date date = DateUtil.addMinutes(generateTime, 60);
		long remainTime = date.getTime() - System.currentTimeMillis();
		rdArriveYeD.setRemainTime(remainTime);
		m2cService.sendDynamicMenu(uid, DynamicMenuEnum.ADVENTURE, 1);
	}
}
