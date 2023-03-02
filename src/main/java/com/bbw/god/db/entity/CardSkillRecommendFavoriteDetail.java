package com.bbw.god.db.entity;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

import java.io.Serializable;

/**
 * 技能推荐
 */
@Data
@TableName("card_skill_recommend_favorite_detail")
public class CardSkillRecommendFavoriteDetail implements Serializable {
	private static final long serialVersionUID = 1L;
	@TableId(type = IdType.AUTO)
	private Integer id;
	private Integer recommend_id;
	private Long uid;
	private Integer status;
}
