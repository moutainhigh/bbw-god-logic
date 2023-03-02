package com.bbw.god.db.entity;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import com.bbw.common.DateUtil;
import com.bbw.god.gameuser.GameUser;
import lombok.Data;

import java.io.Serializable;

/**
 * 角色信息表
 * 
 * @author shaojun
 * @email lsj@bamboowind.cn
 * @date 2019-04-02 10:25:53
 */
@Data
@TableName("ins_role_info")
public class InsRoleInfoEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	@TableId(type = IdType.INPUT)
	private Long uid; // 区服玩家id。对应game_user.id
	private Integer sid; // 当前所属区服ID
	private String serverName; // 区服名称
	private String nickname; // 区服玩家昵称。对应game_user.nickname
	private String username = ""; // 玩家账号。对应account.email。
	private Integer originSid; // 注册时候的区服ID
	private Integer cid; // 渠道ID。对应base_plat.id。
	private Integer level; // 等级
	private String inviCode;// username账号的邀请码
	private String regDevice;// 注册时候的设备标识
	private Integer regDate; // 注册日期
	private Integer lastLoginDate; // 注册日期
	private Integer loginTimes = 0; // 登录次数
	private Integer pay = 0; // 累积充值
	private String regIp; // 注册ip
	private Integer accountRegDate;//账号注册日期

	public static InsRoleInfoEntity fromGameUser(GameUser user, CfgServerEntity server, Integer accountRegDate) {
		InsRoleInfoEntity entity = new InsRoleInfoEntity();
		entity.setUid(user.getId());
		entity.setOriginSid(server.getId());
		entity.setSid(server.getMergeSid());
		entity.setCid(user.getRoleInfo().getChannelId());
		entity.setInviCode(user.getRoleInfo().getMyInvitationCode());
		entity.setLevel(user.getLevel());
		entity.setNickname(user.getRoleInfo().getNickname());
		entity.setRegDate(DateUtil.getTodayInt());
		entity.setLastLoginDate(entity.getRegDate());
		entity.setUsername(user.getRoleInfo().getUserName());
		if (accountRegDate!=null){
			entity.setAccountRegDate(accountRegDate);
		}
		return entity;
	}
}
