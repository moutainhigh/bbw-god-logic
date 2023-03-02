package com.bbw.god.db.entity;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

import java.util.Date;

/**
 * 战斗策略
 *
 * @author: suhq
 * @date: 2021/9/23 1:09 上午
 */
@Data
public class AbstractAttackStrategyEntity {
	@TableId(type = IdType.INPUT)
	private Long id;
	private Integer gid;
	private String serverPrefix;
	private Long uid;//玩家ID
	private Integer lv;//玩家等级
	private String nickname;
	private Integer head;
	private Integer icon;
	private Integer cards;//卡牌数量
	private Integer specialCards;//修改技能的卡牌数
	private Integer useWeapons;// 法宝使用数量
	private Integer aiLv;//AI对手等级
	private String aiNickname;//AI昵称
	private Integer aiHead;
	private Integer round;//对战回合数
	private Integer resultType;//胜负类型：召唤师血量、卡牌用完、达到最大回合
	private Date recordedTime;//记录时间
	private Integer recordedDate;//记录日期
	private String recordedUrl;//OSS地址
	private Integer seq;//攻打序列
}
