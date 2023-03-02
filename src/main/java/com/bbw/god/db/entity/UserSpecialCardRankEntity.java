package com.bbw.god.db.entity;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import com.bbw.common.DateUtil;
import com.bbw.common.ID;
import com.bbw.god.game.config.server.ServerTool;
import com.bbw.god.gameuser.GameUser;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author 作者 ：lwb
 * @version 创建时间：2019年9月30日 下午2:05:09
 * 类说明  PVE战斗结果明细
 */
@Data
@TableName("user_special_card_rank")
public class UserSpecialCardRankEntity implements Serializable{
	private static final long serialVersionUID = 1L;
	@TableId(type = IdType.INPUT)
	private Long id;
	private Integer gid=0;
	private Integer sid;
	private Integer cardId;
	private String cardName;
	private Integer fightType;
	private Integer fightRank=0;
	private Integer skill0=0;
	private Integer skill5=0;
	private Integer skill10=0;
	private Long uid;
	private String nickname;
	private Integer lv;
	private Integer head;
	private Integer icon;
	private String cardGroup;
	private Date updatedTime;
	private Integer updated;

	public static UserSpecialCardRankEntity instance(int cardId, GameUser gu){
		UserSpecialCardRankEntity entity=new UserSpecialCardRankEntity();
		entity.setId(ID.INSTANCE.nextId());
		entity.setCardId(cardId);
		entity.setSid(gu.getServerId());
		entity.setGid(ServerTool.getServerGroup(entity.getSid()));
		entity.setUid(gu.getId());
		entity.setNickname(gu.getRoleInfo().getNickname());
		entity.setLv(gu.getLevel());
		entity.setHead(gu.getRoleInfo().getHead());
		entity.setIcon(gu.getRoleInfo().getHeadIcon());
		entity.setUpdatedTime(DateUtil.now());
		entity.setUpdated(DateUtil.toDateInt(entity.getUpdatedTime()));
		return entity;
	}
}
