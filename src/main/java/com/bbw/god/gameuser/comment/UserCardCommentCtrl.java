package com.bbw.god.gameuser.comment;

import com.bbw.common.Rst;
import com.bbw.common.SensitiveWordUtil;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.controller.AbstractController;
import com.bbw.god.game.CR;
import com.bbw.god.login.LoginPlayer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author suchaobin
 * @description 玩家卡牌评论
 * @date 2020/4/7 14:27
 */
@RestController
public class UserCardCommentCtrl extends AbstractController {
	@Autowired
	private UserCardCommentService userCardCommentService;

	@RequestMapping(CR.CardComment.GET_COMMENTS)
	public RDCardCommentList getComments(int cardId) {
		return userCardCommentService.getComments(getUserId(), cardId);
	}

	@RequestMapping(CR.CardComment.GET_RECENT_COMMENTS)
	public RDCardCommentList getRecentComments(int cardId, int page, int limit) {
		return userCardCommentService.getRecentComments(getUserId(), cardId, page, limit);
	}

	@RequestMapping(CR.CardComment.ADD_FAVORITE)
	public Rst addFavorite(int cardId, long commentId) {
		userCardCommentService.addFavorite(getUserId(), cardId, commentId);
		return Rst.businessOK();
	}

	@RequestMapping(CR.CardComment.COMMENT)
	public Rst comment(int cardId, String content) {
		LoginPlayer user = getUser();
		if (SensitiveWordUtil.isNotPass(content, user.getChannelId(), user.getOpenId())) {
			throw ExceptionForClientTip.fromi18nKey("input.not.sensitive.words");
		}
		userCardCommentService.comment(getUserId(), cardId, content);
		return Rst.businessOK();
	}

	@RequestMapping(CR.CardComment.UPDATE_COMMENT)
	public Rst updateComment(int cardId, long commentId, String content) {
		userCardCommentService.updateComment(getUserId(), cardId, commentId, content);
		return Rst.businessOK();
	}
}
