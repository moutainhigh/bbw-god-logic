package com.bbw.god.db.entity;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import com.bbw.common.DateUtil;
import lombok.Data;

import java.io.Serializable;

/** 
* @author 作者 ：lwb
* @version 创建时间：2019年9月30日 下午2:05:09 
* 类说明  PVE战斗结果明细
*/
@Data
@TableName("ins_game_pve_detail")
public class InsGamePveDetailEntity implements Serializable{
	private static final long serialVersionUID = 1L;
	@TableId(type = IdType.INPUT)
	private Long id;
	private Integer sid;
	private Integer fightType; //战斗类型
	private Integer siteId;// 战斗地点Id
	private String site;// 战斗地点
	private Integer cityOrder;// 攻下/进阶第几座当前级别的城池
	private Integer cityHv;
	private Integer cityLv;
	private Long uid;//玩家ID
	private Integer lv;//玩家等级
	private Integer aiLv;//AI对手等级
	private Integer isPlayer=0;
	private Integer round;//对战回合数
	private String useWeapon;// 法宝使用数量
	private Integer isWin;//胜负
	private Integer isSkip;//是否跳过
	private Integer isAuto;//是否点过自动
	private Integer resultType;//胜负类型：召唤师血量、卡牌用完、达到最大回合
	private Long recordingTime;//结束时间
	
	public InsGamePveDetailEntity() {
		this.recordingTime=DateUtil.toDateTimeLong();
	}
}
