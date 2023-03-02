package com.bbw.god.db.entity;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import com.bbw.common.DateUtil;
import com.bbw.common.ID;
import lombok.Data;

import java.util.Date;

/**
 * @author suchaobin
 * @date 2020/4/7 14:06
 */
@Data
@TableName("god_game.ins_card_comment")
public class InsCardComment {
	@TableId(type = IdType.INPUT)
	private Long id;
	private Integer cardId;
	@TableField(exist = false)
	private String cardName;
	private Integer serverGroup;
	private Long rid;
	@TableField(exist = false)
	private String nickname;
	@TableField(exist = false)
	private String server;
	private String content;
	private Date commentTime = DateUtil.now();
	@TableField(exist = false)
	private String commentTimeStr;
	private Integer favoriteCount = 0;

	public static InsCardComment getInstance(int cardId, int serverGroup, long rid, String content) {
		InsCardComment insCardComment = new InsCardComment();
		insCardComment.setId(ID.INSTANCE.nextId());
		insCardComment.setCardId(cardId);
		insCardComment.setServerGroup(serverGroup);
		insCardComment.setRid(rid);
		insCardComment.setContent(content);
		return insCardComment;
	}

	public void updateComment(String content) {
		this.setFavoriteCount(0);
		this.setContent(content);
		this.setCommentTime(DateUtil.now());
	}
}
