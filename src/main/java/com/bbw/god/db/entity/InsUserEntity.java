package com.bbw.god.db.entity;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import com.bbw.god.gameuser.GameUser;
import lombok.Data;

import java.io.Serializable;

/**
 * 玩家属性、资源等数据
 *
 * @author shaojun
 * @email lsj@bamboowind.cn
 * @date 2019-02-27 09:44:18
 */
@Data
@TableName("ins_user")
public class InsUserEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	@TableId(type = IdType.INPUT)
	private Long uid; //区服角色ID
	private int sid;//区服ID
	private String nickname; //区服角色昵称
	private String username; //游戏账号
	private int level;//等级
	private long experience;//经验
	private int gold;//元宝
	private long copper;//铜钱
	private int head;//头像
	private String dataJson; //玩家数据JSON

	public static InsUserEntity fromGameUser(GameUser gameUser) {
		InsUserEntity user = new InsUserEntity();
		user.setUid(gameUser.getId());
		user.setSid(gameUser.getServerId());
		user.setNickname(gameUser.getRoleInfo().getNickname());
		user.setUsername(gameUser.getRoleInfo().getUserName());
		user.setLevel(gameUser.getLevel());
		user.setExperience(gameUser.getExperience());
		user.setGold(gameUser.getGold());
		user.setHead(gameUser.getRoleInfo().getHead());
		user.setCopper(gameUser.getCopper());
		user.setDataJson(JSON.toJSONString(gameUser));
		return user;
	}
}
