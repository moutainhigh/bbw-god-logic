package com.bbw.god.server;

import com.bbw.god.login.validator.CheckServerId;
import com.bbw.god.login.validator.CheckUserName;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * 创建角色请求参数
 * 
 * @author suhq
 * @date 2018年11月5日 下午4:19:05
 */
@Data
public class RoleVO {
	@CheckServerId
	private int serverId = 99;// 原始区服ID
	@CheckUserName
	private String userName;// 账号。对应account.email
	@NotBlank(message = "昵称不能为空！")
	@Length(min = 1, max = 6, message = "昵称为1~6个字符")
	private String nickname;
	private String invitationCode = "";// 注册时候输入的邀请码

	private String myInviCode;// 邀请码

	private String deviceId;// 设备ID
	private String oaid = "";

	@Range(min = 1, max = 2, message = "无效的玩家性别")
	private int sex = 1;// 性别

	private int head = 1;// 头像

	@Pattern(regexp = "10|20|30|40|50", message = "无效的玩家属性")
	private String property;// 五元属性

	private String channelCode;// 客户端渠道编码
	private String ip;//
	private Long id;// 只是用来测试的
	private String pushToken;// 推送，设备唯一标示符

}
