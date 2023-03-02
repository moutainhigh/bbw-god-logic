package com.bbw.god.db.entity;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import com.bbw.common.DateUtil;
import lombok.Data;

import java.util.Date;

/**
 * @author suchaobin
 * @date 2020/4/7 14:10
 */
@Data
@TableName("god_game.ins_card_comment_favorite_detail")
public class InsCardCommentFavoriteDetail {
	@TableId(type = IdType.AUTO)
	private Integer id;
	private Integer cardId;
	private Long commentId;
	private Long rid;
	private Date favoriteTime = DateUtil.now();

	public static InsCardCommentFavoriteDetail getInstance(int cardId, long commentId, long rid) {
		InsCardCommentFavoriteDetail detail = new InsCardCommentFavoriteDetail();
		detail.setCardId(cardId);
		detail.setCommentId(commentId);
		detail.setRid(rid);
		return detail;
	}
}
