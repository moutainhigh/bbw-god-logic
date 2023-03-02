package com.bbw.god.channel.maoer;

import java.util.HashMap;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.bbw.common.HttpClientUtil;
import com.bbw.common.MD5Tool;
import com.bbw.god.db.entity.CfgServerEntity;
import com.bbw.god.game.config.Cfg;
import com.bbw.god.gameuser.GameUser;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
* @author lwb  
* @date 2019年6月5日  
* @version 1.0  
* 猫耳公会验证接口
*/
@Slf4j
@Service
public class MaoerSociatyService {
	private String mailVerifyUrl = "https://api.maoergame.com/game/sociaty/response";

	public boolean sociatyNameVerify(GameUser gu,String name) {
		HashMap<String, String> param=getparams(gu,2,name,"");
		try {
			String json = HttpClientUtil.doPost(mailVerifyUrl, param);
			log.info(JSON.toJSONString(param));
			log.info("猫耳行会命名校验返回：" + json);
			Result rst = JSON.parseObject(json, Result.class);
			return rst.success();
		} catch (Exception e) {
			log.error("猫耳行会命名校验异常" + e.getMessage(), e);
			return false;
		}
	}

	public boolean sociatyNoticeVerify(GameUser gu,String notice) {
		HashMap<String, String> param=getparams(gu,1,"",notice);
		try {
			String json = HttpClientUtil.doPost(mailVerifyUrl, param);
			log.info(JSON.toJSONString(param));
			log.info("猫耳行会留言校验返回：" + json);
			Result rst = JSON.parseObject(json, Result.class);
			return rst.success();
		} catch (Exception e) {
			log.error("猫耳行会留言校验异常" + e.getMessage(), e);
			return false;
		}
	}
	/**
	 * 操作类型，1公告编辑 2修改公会名
	 * @param gu
	 * @return
	 */
	private HashMap<String, String> getparams(GameUser gu,int type,String name,String notice){
		HashMap<String, String> param = new HashMap<>();
		param.put("game_code", "fjfsc");
		param.put("server_id", String.valueOf(gu.getServerId()));
		CfgServerEntity server = Cfg.I.get(gu.getServerId(), CfgServerEntity.class);
		param.put("server_name", server.getName());
		param.put("op_type",type+"");
		param.put("name",name);
		param.put("leader_uid", gu.getRoleInfo().getUserName());
		param.put("leader_rid", gu.getId().toString());
		param.put("leader_name",gu.getRoleInfo().getNickname());
		param.put("vice_leader_uid", "");
		param.put("vice_leader_rid", "");
		param.put("vice_leader_name", "");
		param.put("edit_uid",gu.getRoleInfo().getUserName());
		param.put("edit_rid", gu.getId().toString());
		param.put("edit_name", gu.getRoleInfo().getNickname());
		param.put("notice", notice);
		long timestamp = System.currentTimeMillis() / 1000;
		param.put("timestamp", String.valueOf(timestamp));
		String key = MD5Tool.md5Encode("c345a165b566d1c421afd8a748373d7f" + timestamp).toLowerCase();
		param.put("key", key);
		return param;
	}

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
