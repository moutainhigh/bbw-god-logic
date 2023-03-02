package com.bbw.god.channel.maoer;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.bbw.common.HttpClientUtil;
import com.bbw.common.MD5Tool;
import com.bbw.god.db.entity.CfgServerEntity;
import com.bbw.god.game.config.Cfg;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.mail.UserMail;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * 猫耳邮件
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-05-05 11:40
 */
@Slf4j
@Service
public class MaoerMailService {
	@Autowired
	private GameUserService userService;
	private String mailVerifyUrl = "https://api.maoergame.com/game/mail/response";

	public Result maoerMailVerify(UserMail usermail, GameUser usr) {
		HashMap<String, String> param = new HashMap<>();
		param.put("game_code", "fjfsc");
		param.put("server_id", usr.getServerId().toString());
		CfgServerEntity server = Cfg.I.get(usr.getServerId(), CfgServerEntity.class);
		param.put("server_name", server.getName());
		param.put("mail_type", "1");
		param.put("mail_title", usermail.getTitle());
		param.put("mail_content", usermail.getContent());
		param.put("mail_envelope", "");
		param.put("sender_uid", usr.getRoleInfo().getUserName());
		param.put("sender_name", usermail.getSenderNickName());
		param.put("sender_level", usr.getLevel().toString());
		param.put("sender_rid", usermail.getSenderId().toString());
		GameUser receiver = userService.getGameUser(usermail.getReceiverId());
		param.put("receiver_uid", receiver.getRoleInfo().getUserName());
		param.put("receiver_rid", usermail.getReceiverId().toString());
		param.put("receiver_name", receiver.getRoleInfo().getNickname());
		param.put("receiver_level", receiver.getLevel().toString());
		long timestamp = System.currentTimeMillis() / 1000;
		param.put("timestamp", String.valueOf(timestamp));
		String key = MD5Tool.md5Encode("c345a165b566d1c421afd8a748373d7f" + timestamp).toLowerCase();
		param.put("key", key);
		try {
			String json = HttpClientUtil.doPost(mailVerifyUrl, param);
			log.info(JSON.toJSONString(param));
			log.info("猫耳邮件校验返回：" + json);
			Result rst = JSON.parseObject(json, Result.class);
			return rst;
		} catch (Exception e) {
			log.error("猫耳邮件校验异常" + e.getMessage(), e);
			Result r = new Result();
			r.setResult("success");
			return r;
		}
	}

	/**
	 * 
	 * @author lsj@bamboowind.cn
	 * @version 1.0.0
	 * @date 2019-05-05 11:44
	 */
	@Data
	public static class Result {
		private String result;//success|fail|verify_error
		private int code;//-1,
		private String mail_title;//
		private String mail_content;//

		public boolean success() {
			return "success".equals(result);
		}

	}
}
