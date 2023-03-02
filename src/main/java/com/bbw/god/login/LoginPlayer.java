package com.bbw.god.login;

import com.bbw.common.DateUtil;
import com.bbw.god.db.entity.CfgServerEntity;
import com.bbw.god.gameuser.GameUser;
import lombok.Data;

import java.io.Serializable;

/**
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2018年9月27日 下午5:32:21
 */
@Data
public class LoginPlayer implements Serializable {
	public static String REQUEST_ATTR_KEY = "user";
	private static final long serialVersionUID = 1L;
	private String account;// 账号名
	private int loginSid;// 登录区服ID
	private int serverId;// 区服ID
	private int channelId;// 区服ID
	private Long uid;// 区服角色ID
	private long tokenVersion = 0;// 版本号
	private String nickName;// 区服角色名
	private String clientIp;// 客户端ip
	private String deviceId;// 设备号
	private String oaid;// 设备号
	private String pushToken;
	private String openId;

	public static LoginPlayer fromGameUser(GameUser user, CfgServerEntity loginServer, String clientIp, String deviceId, String oaid, String pushToken) {
		LoginPlayer player = new LoginPlayer();
		player.setAccount(user.getRoleInfo().getUserName());
		player.setLoginSid(loginServer.getId());
		player.setServerId(loginServer.getMergeSid());
		player.setUid(user.getId());
		player.setChannelId(user.getRoleInfo().getChannelId());
		player.setNickName(user.getRoleInfo().getNickname());
		player.setTokenVersion(DateUtil.toDateTimeLong());
		player.setClientIp(clientIp);
		player.setDeviceId(deviceId);
		player.setOaid(oaid);
		player.setPushToken(pushToken);

		return player;
	}
}
