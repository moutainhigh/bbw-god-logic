package com.bbw.god.db.entity;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import com.bbw.common.StrUtil;
import com.bbw.god.detail.DetailData;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

/**
 * 玩家资源明细表
 *
 * @author shaojun
 * @email lsj@bamboowind.cn
 * @date 2019-03-26 20:14:11
 */
@Slf4j
@Data
@TableName("ins_user_detail")
public class InsUserDetailEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	@TableId(type = IdType.INPUT)
	private Long id; //
	private Integer sid; //区服ID
	private Long uid; //玩家ID
	private Integer userLevel; //玩家等级
	private Integer opdate; //操作日期
	private Integer optime; //操作时间
	private Integer awardType; //资源类型。AwardEnum.java
	private Integer awardId = 0; //资源ID
	private String awardName = "";//奖励名称
	private Long valueChange; //变化数量。正数为加，负数为扣除。
	private Long afterValue; //变化后的值
	private Integer way; //途经。WayEnum.java
	private String wayName = "";//途径名称

	public static InsUserDetailEntity fromDetailData(DetailData data) {
		InsUserDetailEntity entity = new InsUserDetailEntity();
		entity.setId(data.getId());
		entity.setSid(data.getSid());
		entity.setUid(data.getUid());
		entity.setUserLevel(data.getUserLevel());
		entity.setOpdate(data.getOpdate());
		entity.setOptime(data.getOptime());
		entity.setAwardType(data.getAwardDetail().getAwardType().getValue());
		entity.setAwardId(data.getAwardDetail().getAwardId());
		entity.setAwardName(data.getAwardDetail().getAwardName());
		if (StrUtil.isNull(entity.getAwardName())) {
			//			if (entity.getAwardType() == AwardEnum.KP.getValue()) {
			//				CfgCardEntity cfgCard = Cfg.I.get(entity.getAwardId(), CfgCardEntity.class);
			//				entity.setAwardName(cfgCard.getName());
			//			} else {
			entity.setAwardName("noset");
			//	}
		}
		entity.setValueChange(data.getAwardDetail().getValueChange());
		entity.setAfterValue(data.getAfterValue());
		entity.setWay(data.getWay().getValue());
		entity.setWayName(data.getWay().getName());
		if (StrUtil.isNull(entity.getWayName())) {
			log.error("明细错误，未指定来源!" + data.toString());
		}
		return entity;
	}
}
