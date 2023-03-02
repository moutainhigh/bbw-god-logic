package com.bbw.god.gameuser.comment;

import com.bbw.god.db.entity.InsCardComment;
import com.bbw.god.db.entity.InsCardCommentFavoriteDetail;
import com.bbw.god.db.service.InsCardCommentFavoriteDetailService;
import com.bbw.god.db.service.InsCardCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 玩家卡牌评论信息服务
 *
 * @author: huanghb
 * @date: 2021/12/29 13:45
 */
@Service
public class UserCardCommentInfoService {
    @Autowired
    private InsCardCommentService insCardCommentService;
    @Autowired
    private InsCardCommentFavoriteDetailService favoriteDetailService;
    /** 热评展示数 */
    private static final int HOT_LIMIT = 5;
    /** 热评最低点赞数要求 */
    private static final int MIN_FAVORITE = 10;
    /** 热评第一页 */
    private static final int FIRST_PAGE = 1;

    /**
     * 获得最近卡牌评论
     *
     * @param cardId
     * @param page
     * @param limit
     * @return
     */
    public List<InsCardComment> getRecentCardComments(int cardId, int page, int limit, int serverGroup) {
        List<Long> hotCommentIds = getHotCommentIds(cardId, serverGroup);
        return insCardCommentService.getRecentComments(serverGroup, cardId, page, limit, hotCommentIds);
    }

    /**
     * 获取热门卡牌评论
     *
     * @param cardId
     * @return
     */
    public List<InsCardComment> getHotCardComments(int cardId, int serverGroup) {
        return insCardCommentService.getHotComments(serverGroup, cardId, MIN_FAVORITE, FIRST_PAGE, HOT_LIMIT);
    }

    /**
     * 获取我的卡牌评论
     *
     * @param uid
     * @param cardId
     * @return
     */
    public InsCardComment getMyCardComment(long uid, int cardId, int serverGroup) {
        return insCardCommentService.getMyComment(serverGroup, cardId, uid);
    }

    /**
     * 获得热门评论id集合
     *
     * @param cardId
     * @return
     */
    public List<Long> getHotCommentIds(int cardId, int serverGroup) {
        List<InsCardComment> hotComments = insCardCommentService.getHotComments(serverGroup, cardId, MIN_FAVORITE, FIRST_PAGE, HOT_LIMIT);
        return hotComments.stream().map(InsCardComment::getId).collect(Collectors.toList());
    }

    /**
     * 获得点赞评论id集合
     *
     * @param uid
     * @return
     */
    public List<Long> getFavoriteCommentIds(long uid) {
        List<InsCardCommentFavoriteDetail> favoriteComments = favoriteDetailService.getMyFavorite(uid);
        return favoriteComments.stream().map(InsCardCommentFavoriteDetail::getCommentId).collect(Collectors.toList());
    }

    /**
     * 玩家是否已经点赞过
     *
     * @param uid
     * @param commentId
     * @return
     */
    public boolean isFavorite(long uid, long commentId) {
        List<InsCardCommentFavoriteDetail> myFavorites = favoriteDetailService.getMyFavorite(uid);
        return myFavorites.stream().anyMatch(fc -> fc.getCommentId() == commentId);
    }
}
