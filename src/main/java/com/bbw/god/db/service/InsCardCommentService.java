package com.bbw.god.db.service;

import com.baomidou.mybatisplus.service.IService;
import com.bbw.god.db.entity.InsCardComment;

import java.util.List;
import java.util.Map;

/**
 * @author suchaobin
 * @date 2020/4/7 14:13
 */
public interface InsCardCommentService extends IService<InsCardComment> {
	/**
	 * 获取卡牌热门评论中
	 *
	 * @param serverGroup
	 * @param cardId
	 * @param minFavorite
	 * @param page
	 * @param limit
	 * @return
	 */
	List<InsCardComment> getHotComments(int serverGroup, int cardId, int minFavorite, int page, int limit);

	/**
	 * 获取卡牌热门评论中，包括总页数
	 *
	 * @param serverGroup
	 * @param cardStar
	 * @param cardId
	 * @param start
	 * @param end
	 * @param field
	 * @param order
	 * @param page
	 * @param limit
	 * @return
	 */
	Map<String, Object> getComments(Integer serverGroup, Integer cardStar, Integer cardId, String start, String end,
									String field, String order, int page, int limit);

	/**
	 * 获取卡牌最近评论
	 *
	 * @param serverGroup
	 * @param cardId
	 * @param page
	 * @param limit
	 * @param excludeIds
	 * @return
	 */
	List<InsCardComment> getRecentComments(int serverGroup, int cardId, int page, int limit, List<Long> excludeIds);

	/**
	 * 获取我的评论
	 *
	 * @param serverGroup
	 * @param cardId
	 * @param uid
	 * @return
	 */
	InsCardComment getMyComment(int serverGroup, int cardId, long uid);

	/**
	 * 点赞更新总数
	 *
	 * @param commentId
	 */
	void addFavorite(long commentId);

	/**
	 * 修改点赞数
	 *
	 * @param commentIds
	 * @param newValue
	 */
	void updateFavoriteCount(List<Long> commentIds, int newValue);
}
