package com.bbw.god.db.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

import lombok.Data;

/** 
* @author 作者 ：lwb
* @version 创建时间：2019年9月30日 下午2:05:09 
* 类说明  PVE战斗结果明细
*/
@Data
@TableName("ins_game_city_war")
public class InsGameCityWarEntity implements Serializable{
	private static final long serialVersionUID = 1L;
	@TableId(type = IdType.AUTO)
	private Long id;
	private Long detailId;
	private Integer cityId;// 战斗地点Id
	private String city;// 战斗地点
	private Long uid;//玩家ID
	private String card_name;// 卡名称
	private Integer card_lv;//卡牌等级
	private Integer card_hv;//卡牌阶级
	private String cardJson;// 卡牌详情 UserCard
}
