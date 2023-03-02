package com.bbw.god.gameuser.comment;

import com.bbw.common.BbwSensitiveWordUtil;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.db.entity.CfgServerEntity;
import com.bbw.god.db.entity.InsCardComment;
import com.bbw.god.db.entity.InsCardCommentFavoriteDetail;
import com.bbw.god.db.service.InsCardCommentFavoriteDetailService;
import com.bbw.god.db.service.InsCardCommentService;
import com.bbw.god.game.config.card.CardTool;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.card.CardChecker;
import com.bbw.god.gameuser.card.UserCard;
import com.bbw.god.gameuser.card.UserCardService;
import com.bbw.god.gameuser.comment.RDCardCommentList.RDCardCommentInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author suchaobin
 * @description 玩家卡牌评论逻辑层
 * @date 2020/4/7 14:37
 */
@Service
public class UserCardCommentService {
	@Autowired
	private GameUserService gameUserService;
	@Autowired
	private InsCardCommentService insCardCommentService;
	@Autowired
	private InsCardCommentFavoriteDetailService favoriteDetailService;
	@Autowired
	private UserCardService userCardService;
	@Autowired
	private UserCardCommentInfoService userCardCommentInfoService;
	/** 评论字数限制 */
	private static final int COMMENT_LIMIT = 100;

	/**
	 * 获取卡牌热门评论和我的评论
	 *
	 * @param uid
	 * @param cardId
	 * @return
	 */
	public RDCardCommentList getComments(long uid, int cardId) {
		//校验卡牌Id是否存在对应卡牌
		checkCardId(cardId);
		//卡牌热门评论
		List<Long> favoriteComments = userCardCommentInfoService.getFavoriteCommentIds(uid);
		CfgServerEntity server = gameUserService.getOriServer(uid);
		List<InsCardComment> insHotComments = userCardCommentInfoService.getHotCardComments(cardId, server.getGroupId());
		List<RDCardCommentInfo> rdHotComments = castCommentInfos(insHotComments, favoriteComments);
		//我的评论
		InsCardComment insMyComment = userCardCommentInfoService.getMyCardComment(uid, cardId, server.getGroupId());
		GameUser gameUser = gameUserService.getGameUser(uid);
		RDCardCommentInfo rdMyComment = RDCardCommentInfo.getInstance(gameUser.getRoleInfo().getNickname(), insMyComment, favoriteComments);
		return RDCardCommentList.getInstance(rdHotComments, rdMyComment);
	}

	/**
	 * 获取卡牌最近评论
	 *
	 * @param uid
	 * @param cardId
	 * @param page
	 * @param limit
	 * @return
	 */
	public RDCardCommentList getRecentComments(long uid, int cardId, int page, int limit) {
		//校验卡牌Id是否存在对应卡牌
		checkCardId(cardId);
		//获取卡牌最近评论
		List<Long> favoriteComments = userCardCommentInfoService.getFavoriteCommentIds(uid);
		CfgServerEntity server = gameUserService.getOriServer(uid);
		List<InsCardComment> insRecentCardComments = userCardCommentInfoService.getRecentCardComments(cardId, page, limit, server.getGroupId());
		List<RDCardCommentInfo> rdRecentCardComments = castCommentInfos(insRecentCardComments, favoriteComments);
		return RDCardCommentList.getInstance(rdRecentCardComments);
	}

	/**
	 * 评论点赞
	 *
	 * @param uid
	 * @param cardId
	 * @param commentId
	 */
	public void addFavorite(long uid, int cardId, long commentId) {
		//校验卡牌Id是否存在对应卡牌
		checkCardId(cardId);
		// 评论不存在的
		InsCardComment favoriteComment = insCardCommentService.selectById(commentId);
		if (favoriteComment == null) {
			throw new ExceptionForClientTip("comment.not.exist");
		}
		// 玩家是否已经点赞过
		boolean isFavorite = userCardCommentInfoService.isFavorite(uid, commentId);
		if (isFavorite) {
			throw new ExceptionForClientTip("comment.already.favorite");
		}
		// 更新点赞总数以及添加点赞明细
		insCardCommentService.addFavorite(commentId);
		favoriteDetailService.insert(InsCardCommentFavoriteDetail.getInstance(cardId, commentId, uid));
	}

	/**
	 * 卡牌评论
	 *
	 * @param uid
	 * @param cardId
	 * @param content
	 */
	public void comment(long uid, int cardId, String content) {
		//卡牌id和评论内容检查
		checkCardIdAndContent(uid, cardId, content);
		//获取我的评论
		CfgServerEntity server = gameUserService.getOriServer(uid);
		InsCardComment myComment = userCardCommentInfoService.getMyCardComment(uid, cardId, server.getGroupId());
		if (myComment != null) {
			throw new ExceptionForClientTip("card.already.comment");
		}
		// 屏蔽字转成*号
		content = BbwSensitiveWordUtil.replaceSensitiveWord(content);
		//保存数据
		InsCardComment insCardComment = InsCardComment.getInstance(cardId, server.getGroupId(), uid, content);
		insCardCommentService.insert(insCardComment);
	}

	/**
	 * 修改评论
	 *
	 * @param uid
	 * @param cardId
	 * @param commentId
	 * @param content
	 */
	public void updateComment(long uid, int cardId, long commentId, String content) {
		//卡牌id和评论内容检查
		checkCardIdAndContent(uid, cardId, content);
		//获取我的评论
		InsCardComment insCardComment = insCardCommentService.selectById(commentId);
		if (insCardComment == null) {
			throw new ExceptionForClientTip("comment.not.exist");
		}
		// 屏蔽字转成*号
		content = BbwSensitiveWordUtil.replaceSensitiveWord(content);
		//更新数据
		insCardComment.updateComment(content);
		insCardCommentService.updateById(insCardComment);
		favoriteDetailService.delByCommentId(commentId);
	}

	/**
	 * 将数据对象集合转为客户端需要的
	 *
	 * @param insCardComments
	 * @param favoriteComments
	 * @return
	 */
	private List<RDCardCommentInfo> castCommentInfos(List<InsCardComment> insCardComments, List<Long> favoriteComments) {
		return insCardComments.stream().map(comment -> RDCardCommentInfo.getInstance(getNickName(comment.getRid()),
				comment, favoriteComments)).collect(Collectors.toList());
	}

	/**
	 * 获取角色名
	 *
	 * @param uid
	 * @return
	 */
	private String getNickName(long uid) {
		return gameUserService.getGameUser(uid).getRoleInfo().getNickname();
	}

	/**
	 * 卡牌id和评论内容检查
	 *
	 * @param uid
	 * @param cardId
	 * @param content
	 */
	private void checkCardIdAndContent(long uid, int cardId, String content) {
		// 长度超过限制
		if (content.length() > COMMENT_LIMIT) {
			throw new ExceptionForClientTip("comment.out.of.length");
		}
		// 是否拥有
		UserCard userCard = userCardService.getUserCard(uid, cardId);
		CardChecker.checkIsOwn(userCard);
	}

	/**
	 * 通过获取对应卡牌来校验卡牌Id是否存在对应卡牌
	 *
	 * @param cardId
	 */
	private void checkCardId(int cardId) {
		CardTool.getCardById(cardId);
	}
}
