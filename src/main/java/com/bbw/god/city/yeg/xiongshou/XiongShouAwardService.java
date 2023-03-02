package com.bbw.god.city.yeg.xiongshou;

import com.bbw.common.ID;
import com.bbw.common.LM;
import com.bbw.common.PowerRandom;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.mail.MailService;
import com.bbw.god.gameuser.treasure.UserTreasureService;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.rd.RDCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/** 
* @author 作者 ：lwb
* @version 创建时间：2020年3月10日 上午10:44:36 
* 类说明 
*/
@Service
public class XiongShouAwardService {
	@Autowired
	private GameUserService gameUserService;
	@Autowired
	private MailService mailService;
	@Autowired
	private UserTreasureService userTreasureService;
	public UserXionShouAward getXionShouAwardObj(long uid) {
		UserXionShouAward xionShouAward = gameUserService.getSingleItem(uid, UserXionShouAward.class);
		if (xionShouAward == null) {
			xionShouAward = new UserXionShouAward();
			xionShouAward.setGameUserId(uid);
			xionShouAward.setId(ID.INSTANCE.nextId());
			gameUserService.addItem(uid, xionShouAward);
		}
		return xionShouAward;
	}

	/**
	 * 随机额外奖励
	 * 
	 * @param uid
	 * @return
	 */
	public int randomAward(long uid) {
		UserXionShouAward xionShouAward = getXionShouAwardObj(uid);
		boolean hit = xionShouAward.randomHit();
		gameUserService.updateItem(xionShouAward);
		if (hit) {
			return TreasureEnum.FZ.getValue();
		}
		return getAwardId();
	}

	private int getAwardId() {
		int seed = PowerRandom.getRandomBySeed(100);
		if (seed <= 70) {
			// 龙舟
			return TreasureEnum.LZ.getValue();
		}
		if (seed <= 95) {
			// 粽子
			return TreasureEnum.ZZ.getValue();
		}
		// 艾菖
		return TreasureEnum.AC.getValue();
	}

	/**
	 * 自动兑换花朵
	 * 
	 * @param uid
	 */
	public void exchangeDateOutFlower(long uid) {
		int copperNum = 0;
		// 兑换的道具
		List<TreasureEnum> treasureEnums = Arrays.asList(TreasureEnum.JU, TreasureEnum.MEI, TreasureEnum.ZHU,
				TreasureEnum.LAN, TreasureEnum.LIAN_HUA, TreasureEnum.MU_DAN);
		for (TreasureEnum tEnum : treasureEnums) {
			copperNum += delTreasure(uid, tEnum);
		}
		UserXionShouAward xionShouAward = gameUserService.getSingleItem(uid, UserXionShouAward.class);
		if (xionShouAward != null) {
			gameUserService.deleteItem(xionShouAward);
		}
		if (copperNum <= 0) {
			return;
		}
		//每个道具固定兑换1000铜钱
		copperNum = copperNum * 1000;
		String title = LM.I.getMsgByUid(uid,"mail.sqhy.activity.over.title");
		String content = LM.I.getMsgByUid(uid,"mail.sqhy.activity.over.content");
		Award award = new Award(AwardEnum.TQ, copperNum);
		mailService.sendAwardMail(title, content, uid, Arrays.asList(award));
	}

	private int delTreasure(long uid, TreasureEnum treasure) {
		int owmNum = userTreasureService.getTreasureNum(uid, treasure.getValue());
		if (owmNum > 0) {
			TreasureEventPublisher.pubTDeductEvent(uid, treasure.getValue(), owmNum, WayEnum.TIME_OUT_EXCHANGE,
					new RDCommon());
		}
		return owmNum;
	}
}
