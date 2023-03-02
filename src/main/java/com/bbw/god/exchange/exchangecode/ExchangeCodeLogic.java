package com.bbw.god.exchange.exchangecode;

import com.bbw.App;
import com.bbw.common.LM;
import com.bbw.god.detail.service.ExchangeCodeWechatDetailService;
import com.bbw.god.event.BaseEventParam;
import com.bbw.god.exchange.exchangecode.event.ExchangeCodeEnum;
import com.bbw.god.exchange.exchangecode.event.ExchangeCodeEventPublisher;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.gameuser.mail.MailService;
import com.bbw.god.uac.entity.ExchangeCodeEntity;
import com.bbw.god.uac.entity.PacksEntity;
import com.bbw.god.uac.service.ExchangeCodeService;
import com.bbw.god.uac.service.PacksService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author suchaobin
 * @description 兑换礼包逻辑
 * @date 2020/3/10 10:11
 */
@Service
@Slf4j
public class ExchangeCodeLogic {
	@Autowired
	private ExchangeCodeService exchangeCodeService;
	@Autowired
	private PacksService packsService;
	@Autowired
	private MailService mailService;
	@Autowired
	private ExchangeCodeWechatDetailService wechatDetailService;
	@Autowired
	private App app;

	/**
	 * 和成就中的关注公众号成就相关联，如果改了邮件标题，则对应事件那边也需要做处理
	 *
	 * @param playerAccount
	 * @param sid
	 * @param uid
	 * @see com.bbw.god.gameuser.achievement.listener
	 */
	public void dispatchWechatWeeklyPackNewCodeServer(String playerAccount, int sid, Long uid) {
		log.debug("playerAccount=[" + playerAccount + "]" + "  sid=[" + sid + "]" + "  uid=[" + uid + "]");
		// 获取绑定用户的礼包
		// 存在指定给个人的礼包。packs=3代表是微信每周礼包。
		ExchangeCodeEntity code = exchangeCodeService.getValidWechatWeeklyByUser(playerAccount);
		//礼包无效
		if (null == code || !exchangeCodeService.isPackValid(code)) {
			return;
		}
		// 已经领取
		if (wechatDetailService.dispatched(code, sid, uid)) {
			return;
		}
		PacksEntity packs = packsService.fetchOne(code.getPacks());
		if (!app.runAsDev()) {
			String title = LM.I.getMsgByUid(uid,"mail.wechat.award.title");
			String content = packs.getMemo();
			// 发送邮件
			mailService.sendAwardMail(title, content, uid, packs.getAwards());
			// 发布礼包兑换事件
			BaseEventParam bep = new BaseEventParam(uid, WayEnum.Mail);
			ExchangeCodeEventPublisher.pubExchangeAwardBagEvent(ExchangeCodeEnum.WE_CHAT_WEEKLY_BAG, bep);
		}
		// 保存发送记录
		wechatDetailService.dispatch(code, sid, uid);
	}
}
