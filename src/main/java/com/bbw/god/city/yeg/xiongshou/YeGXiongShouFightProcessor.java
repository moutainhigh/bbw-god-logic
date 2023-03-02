package com.bbw.god.city.yeg.xiongshou;

import com.bbw.god.activity.holiday.processor.holidayspcialyeguai.HolidayYeGuaiProcessor;
import com.bbw.god.city.yeg.AbstractYeGFightProcessor;
import com.bbw.god.city.yeg.YeGuaiEnum;
import com.bbw.god.fight.RDFightEndInfo;
import com.bbw.god.fight.RDFightsInfo;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.rd.RDCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @author 作者 ：lwb
 * @version 创建时间：2020年2月26日 下午11:30:55 类说明 凶兽来袭
 */
@Service
public class YeGXiongShouFightProcessor extends AbstractYeGFightProcessor {
	@Autowired
	private HolidayYeGuaiProcessor holidayYeGuaiProcessor;
	@Autowired
	private XiongShouAwardService xiongShouAwardService;

	@Override
	public YeGuaiEnum getYeGEnum() {
		return YeGuaiEnum.YG_XIONG_SHOU;
	}

	@Override
	public YeGuaiEnum yeGuaiBoxType() {
		return YeGuaiEnum.YG_NORMAL;
	}

	@Override
	public boolean open(long uid) {
		//return holidayYeGuaiProcessor.opened(gameUserService.getActiveSid(uid));
		return false;
	}

	@Override
	public RDFightsInfo getFightsInfo(GameUser gu, int type) {
		RDFightsInfo info = super.getFightsInfo(gu, type);
		// 卡组里面必有度厄真人443和苏全忠246
		changeCard(info, 443);
		changeCard(info, 246);
		info.setNickname("纣王亲兵");
		info.setHead(543);
		info.setHeadIcon(TreasureEnum.HEAD_ICON_JYYG.getValue());
		return info;
	}

	@Override
	public void sendBoxAward(RDFightEndInfo fightEndInfo, GameUser gu, RDCommon rd) {
		super.sendBoxAward(fightEndInfo, gu, rd);
		// 补充额外奖励 70%龙舟 25%粽子 4%艾菖 1%风筝 获得风筝后不再出
		int treasureId = xiongShouAwardService.randomAward(gu.getId());
		TreasureEventPublisher.pubTAddEvent(gu.getId(), treasureId, 1, WayEnum.YG_OPEN_BOX, rd);
	}

	@Override
	public WayEnum getWay() {
		return WayEnum.YG_OPEN_BOX;
	}

	private void changeCard(RDFightsInfo info, int cardId) {
		Optional<RDFightsInfo.RDFightCard> caOptional = info.getCards().stream().filter(p -> p.getBaseId() == cardId).findFirst();
		int maxTimes = info.getCards().size();
		if (!caOptional.isPresent()) {
			int index = 0;
			RDFightsInfo.RDFightCard card0 = info.getCards().get(index);
			while (card0.getBaseId() == 443 || card0.getBaseId() == 246) {
				index++;
				if (index >= maxTimes) {
					return;
				}
				card0 = info.getCards().get(index);
			}
			RDFightsInfo.RDFightCard card = new RDFightsInfo.RDFightCard(cardId, card0.getLevel(),
					card0.getHierarchy());
			info.getCards().remove(index);
			info.getCards().add(index, card);
		}
	}


	@Override
	public int getRunesId() {
		return 0;
	}
}
