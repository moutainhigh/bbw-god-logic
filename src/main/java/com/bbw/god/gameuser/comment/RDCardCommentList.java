package com.bbw.god.gameuser.comment;

import com.bbw.common.DateUtil;
import com.bbw.god.db.entity.InsCardComment;
import com.bbw.god.rd.RDSuccess;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * @author suchaobin
 * @description 返回给客户端的卡牌评论集合
 * @date 2020/4/7 14:40
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode(callSuper = true)
public class RDCardCommentList extends RDSuccess implements Serializable {
	private static final long serialVersionUID = 6667386415815882264L;
	private List<RDCardCommentInfo> comments;
	private RDCardCommentInfo myCardComment;

	public static RDCardCommentList getInstance(List<RDCardCommentInfo> comments, RDCardCommentInfo myCardComment) {
		RDCardCommentList rdCardCommentList = new RDCardCommentList();
		rdCardCommentList.setComments(comments);
		rdCardCommentList.setMyCardComment(myCardComment);
		return rdCardCommentList;
	}

	public static RDCardCommentList getInstance(List<RDCardCommentInfo> comments) {
		RDCardCommentList rdCardCommentList = new RDCardCommentList();
		rdCardCommentList.setComments(comments);
		return rdCardCommentList;
	}

	@Data
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class RDCardCommentInfo implements Serializable {
		private static final long serialVersionUID = -6743822138245706595L;
		private Long commentId;
		private String nickname;
		private String content;
		private String commentTime;
		private Integer favoriteCount;
		private Boolean isMyFavorite;

		/**
		 * 初始化
		 *
		 * @param nickname
		 * @param insCardComment
		 * @param favoriteComments
		 * @return
		 */
		public static RDCardCommentInfo getInstance(String nickname, InsCardComment insCardComment, List<Long> favoriteComments) {
			if (insCardComment == null) {
				return null;
			}
			RDCardCommentInfo rd = new RDCardCommentInfo();
			rd.setContent(insCardComment.getContent());
			String commentTime = DateUtil.toDateTimeString(insCardComment.getCommentTime());
			rd.setCommentTime(commentTime);
			rd.setFavoriteCount(insCardComment.getFavoriteCount());
			rd.setCommentId(insCardComment.getId());
			Boolean isMyFavorite = favoriteComments.contains(insCardComment.getId());
			rd.setIsMyFavorite(isMyFavorite);
			rd.setNickname(nickname);
			return rd;
		}
	}

}
