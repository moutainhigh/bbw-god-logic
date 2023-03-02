package com.bbw.god.db.entity;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

import java.io.Serializable;

/**
 * 卡牌技能统计
 *
 * @author: suhq
 * @date: 2021/9/10 8:29 下午
 */
@Data
@TableName("statistic_in_card_skill")
public class StatisticInCardSkillEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	@TableId(type = IdType.INPUT)
	private Long id;
	private Integer cardId;
	private String skills;
	private Integer num;
	private Integer statisticDate;
}
