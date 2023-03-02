package com.bbw.god.db.entity;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import com.bbw.common.DateUtil;
import com.bbw.common.ID;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.card.CardSkillPosEnum;
import com.bbw.god.gameuser.card.UserCard;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author 作者 ：lwb
 * @version 创建时间：2019年9月30日 下午2:05:09
 * 类说明  PVE战斗结果明细
 */
@Data
@TableName("user_special_card")
public class UserSpecialCardEntity implements Serializable{
	private static final long serialVersionUID = 1L;
	@TableId(type = IdType.INPUT)
	private Long id;
	private Integer gid;
	private Integer sid;
	private Long uid;
	private String nickname;
	private Integer lv;
	private Integer head;
	private Integer icon;
	private Integer cardId;
	private String cardName;
	private Integer skill0=0;
	private Integer skill5=0;
	private Integer skill10=0;
	private Date updatedTime;
	private Integer updated;

	public static UserSpecialCardEntity instance(UserCard userCard, int gid,int sid){
		UserSpecialCardEntity entity=new UserSpecialCardEntity();
		entity.setId(ID.INSTANCE.nextId());
		entity.setGid(gid);
		entity.setUid(userCard.getGameUserId());
		entity.setSid(sid);
		entity.setCardId(userCard.getBaseId());
		entity.setCardName(userCard.getName());
		UserCard.UserCardStrengthenInfo info = userCard.getStrengthenInfo();
		if (info!=null){
			entity.setSkill0(info.gainSkill0());
			entity.setSkill5(info.gainSkill5());
			entity.setSkill10(info.gainSkill10());
		}
		entity.setUpdatedTime(DateUtil.now());
		entity.setUpdated(DateUtil.getTodayInt());
		return entity;
	}

	/**
	 * 更新技能
	 * @param info
	 */
	public void updateSkill(UserCard.UserCardStrengthenInfo info){
		if (info==null){
			return;
		}
		this.skill0=info.gainSkill0();
		this.skill5=info.gainSkill5();
		this.skill10=info.gainSkill10();
		this.updatedTime= DateUtil.now();
		this.updated= DateUtil.getTodayInt();
	}

	/**
	 * 更新名称 头像 和头像框
	 * @param info
	 */
	public void updateRoleInfo(GameUser.RoleInfo info){
		this.nickname=info.getNickname();
		this.head=info.getHead();
		this.icon=info.getHeadIcon();
	}
}
