package com.bbw.god.db.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

import lombok.Data;

/**
 * 账号、区服、角色明细
 * 
 * @author shaojun
 * @email lsj@bamboowind.cn
 * @date 2019-03-07 14:21:28
 */
@Data
@TableName("god_detail.account_role_map")
public class AccountRoleMapEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	@TableId(type = IdType.INPUT)
	private String id; //
	private Integer cid; //渠道。对应base_plat.plat。
	private Integer sid; //服务器id.对应server.id。
	private String serverName;//区服名称
	private Long uid; //区服玩家id。对应game_user.id
	private String account = ""; //玩家账号。对应account.email。
	private String nickname; //区服玩家昵称。对应game_user.nickname
	private String deviceid; //设备标识
	private Integer regDate; //注册日期
	private String regIp; //注册ip

	public static AccountRoleMapEntity from(InsRoleInfoEntity roleInfo) {
		AccountRoleMapEntity e = new AccountRoleMapEntity();
		e.setId(roleInfo.getUid().toString());
		e.setCid(roleInfo.getCid());
		e.setSid(roleInfo.getSid());
		e.setServerName(roleInfo.getServerName());
		e.setUid(roleInfo.getUid());
		e.setAccount(roleInfo.getUsername());
		e.setNickname(roleInfo.getNickname());
		e.setRegDate(roleInfo.getRegDate());
		e.setRegIp(roleInfo.getRegIp());
		e.setDeviceid(roleInfo.getRegDevice());
		return e;
	}
}
