package com.bbw.god.detail.async;

import com.alibaba.fastjson.JSONObject;
import com.bbw.App;
import com.bbw.common.DateUtil;
import com.bbw.common.SpringContextUtil;
import com.bbw.god.db.entity.CfgChannelEntity;
import com.bbw.god.db.entity.InsGamePveDetailEntity;
import com.bbw.god.db.service.InsGamePveDetailService;
import com.bbw.god.detail.kafka.KafkaProudcerService;
import com.bbw.god.detail.kafka.KafkaTopicConfiguration;
import com.bbw.god.fight.FightTypeEnum;
import com.bbw.god.game.combat.data.Combat;
import com.bbw.god.game.combat.data.CombatResultEnum;
import com.bbw.god.game.config.Cfg;
import com.bbw.god.game.config.WorldType;
import com.bbw.god.game.config.server.ServerTool;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.card.UserCard;
import com.bbw.god.gameuser.card.UserCardService;
import com.bbw.god.gameuser.pay.UserReceipt;
import com.bbw.god.pay.ReceiptService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 玩家战斗明细异步处理器
 *
 * @author: suhq
 * @date: 2021/12/16 11:26 上午
 */
@Slf4j
@Async
@Component
public class PveDetailAsyncHandler {
	@Autowired
	private App app;
	@Autowired
	private GameUserService gameUserService;
	@Autowired
	private static ReceiptService receiptService;
	@Autowired
	private static UserCardService userCardService;


	/**
	 * 记录明细
	 *
	 * @param detailData
	 */

	public void log(InsGamePveDetailEntity detailData, Combat combat) {
//		System.out.println("===========" + Thread.currentThread().getName());
		log(detailData, combat, false);

	}

	public void log(InsGamePveDetailEntity detailData, Combat combat, boolean isGame) {
//		System.out.println("===========////" + Thread.currentThread().getName());
		try {
			saveToDb(detailData);
			sendToKafaka(detailData, combat, isGame);
		} catch (Exception e) {
			log.error("访问日志明细数据保存失败！\n" + detailData);
			log.error(e.getMessage(), e);
		}

	}

	/**
	 * 保存到MySQL
	 *
	 * @param detailData
	 */
	private void saveToDb(InsGamePveDetailEntity detailData) {
//		System.out.println("===========saveToDb，" + Thread.currentThread().getName());
		FightTypeEnum fightType = FightTypeEnum.fromValue(detailData.getFightType());
		if (!FightTypeEnum.needLogToPVEDetail(fightType)) {
			return;
		}
		InsGamePveDetailService pvpDetailService = SpringContextUtil.getBean(InsGamePveDetailService.class);
		pvpDetailService.insert(detailData);
	}


	/**
	 * 发送到kafka
	 *
	 * @param detailData
	 * @param combat
	 * @param isGame
	 */
	private void sendToKafaka(InsGamePveDetailEntity detailData, Combat combat, boolean isGame) {
		if (!app.isPushToKafka) {
			return;
		}
		JSONObject js = (JSONObject) JSONObject.toJSON(detailData);
		GameUser gameUser = gameUserService.getGameUser(detailData.getUid());
		js.put("serverName", ServerTool.getServer(detailData.getSid()).getName());
		js.put("cid", gameUser.getRoleInfo().getChannelId());
		js.put("channelName", Cfg.I.get(gameUser.getRoleInfo().getChannelId(), CfgChannelEntity.class).getName());
		FightTypeEnum fightType = FightTypeEnum.fromValue(detailData.getFightType());
		if (FightTypeEnum.FST == fightType && isGame) {
			js.put("fightTypeName", "跨服-" + fightType.getName());
		} else {
			js.put("fightTypeName", fightType.getName());
		}

		js.put("resultTypeName", CombatResultEnum.fromValue(detailData.getResultType()).getMemo());
		Date recordingTime = DateUtil.fromDateLong(detailData.getRecordingTime());
		js.put("recordDateTime", recordingTime);
		if (null != gameUser.getRoleInfo().getRegTime()) {
			js.put("roleLifeMinutes", (recordingTime.getTime() - gameUser.getRoleInfo().getRegTime().getTime()) / 60000);
		}
		WorldType worldType = WorldType.fromValue(gameUser.getStatus().getCurWordType());
		if (null != worldType) {
			js.put("worldType", worldType.getValue());
			js.put("worldTypeName", worldType.getName());
		}
		Long aiUid = combat.getP2().getCardFromUid();
		js.put("aiUid", aiUid);
		List<UserReceipt> receipts = receiptService.getAllReceipts(detailData.getUid());
		Integer rechargeNum = receipts.stream().collect(Collectors.summingInt(UserReceipt::getPrice));
		js.put("rechargeNum", rechargeNum);
		List<UserCard> userCards = userCardService.getUserCards(detailData.getUid());
		js.put("cardNum", userCards.size());

		KafkaProudcerService kafkaProudcerService = SpringContextUtil.getBean(KafkaProudcerService.class);
		kafkaProudcerService.send(KafkaTopicConfiguration.PVE_DETAIL_TOPIC, js.toJSONString());
	}

}
