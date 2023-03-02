package com.bbw.god.channel.maoer;

import java.util.HashMap;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.bbw.common.HttpClientUtil;
import com.bbw.common.MD5Tool;
import com.bbw.god.db.entity.CfgServerEntity;
import com.bbw.god.game.config.Cfg;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.server.RoleVO;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * 猫耳
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-05-05 11:40
 */
@Slf4j
@Service
public class MaoerRoleService {
	private String mailVerifyUrl = "https://api.maoergame.com/game/role/response";

	public Result roleNameVerify(RoleVO role) {
		HashMap<String, String> param = new HashMap<>();
		param.put("game_code", "fjfsc");
		param.put("server_id", String.valueOf(role.getServerId()));
		CfgServerEntity server = Cfg.I.get(role.getServerId(), CfgServerEntity.class);
		param.put("server_name", server.getName());
		param.put("role_name", role.getNickname());
		param.put("sender_uid", role.getUserName());
		param.put("sender_rid", "");
		param.put("sender_name", "");
		param.put("sender_level", "1");
		param.put("sender_vip_level", "0");
		long timestamp = System.currentTimeMillis() / 1000;
		param.put("timestamp", String.valueOf(timestamp));
		String key = MD5Tool.md5Encode("c345a165b566d1c421afd8a748373d7f" + timestamp).toLowerCase();
		param.put("key", key);
		try {
			String json = HttpClientUtil.doPost(mailVerifyUrl, param);
			log.info(JSON.toJSONString(param));
			log.info("猫耳角色校验返回：" + json);
			Result rst = JSON.parseObject(json, Result.class);
			return rst;
		} catch (Exception e) {
			log.error("猫耳角色校验异常" + e.getMessage(), e);
			Result r = new Result();
			r.setResult("success");
			return r;
		}
	}

	public Result roleNameVerify(GameUser usr, String newNickname) {
		HashMap<String, String> param = new HashMap<>();
		param.put("game_code", "fjfsc");
		param.put("server_id", usr.getServerId().toString());
		CfgServerEntity server = Cfg.I.get(usr.getServerId(), CfgServerEntity.class);
		param.put("server_name", server.getName());
		param.put("role_name", newNickname);
		param.put("sender_uid", usr.getRoleInfo().getUserName());
		param.put("sender_rid", usr.getId().toString());
		param.put("sender_name", usr.getRoleInfo().getNickname());
		param.put("sender_level", usr.getLevel().toString());
		param.put("sender_vip_level", "0");
		long timestamp = System.currentTimeMillis() / 1000;
		param.put("timestamp", String.valueOf(timestamp));
		String key = MD5Tool.md5Encode("c345a165b566d1c421afd8a748373d7f" + timestamp).toLowerCase();
		param.put("key", key);
		try {
			String json = HttpClientUtil.doPost(mailVerifyUrl, param);
			log.info(JSON.toJSONString(param));
			log.info("猫耳改昵称校验返回：" + json);
			Result rst = JSON.parseObject(json, Result.class);
			return rst;
		} catch (Exception e) {
			log.error("猫耳猫耳改昵称校验异常" + e.getMessage(), e);
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
		private String content;//

		public boolean success() {
			return "success".equals(result);
		}

	}
}
