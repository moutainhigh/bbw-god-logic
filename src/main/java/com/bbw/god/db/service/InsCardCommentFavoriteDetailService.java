package com.bbw.god.db.service;

import com.baomidou.mybatisplus.service.IService;
import com.bbw.god.db.entity.InsCardCommentFavoriteDetail;

import java.util.List;

/**
 * @author suchaobin
 * @date 2020/4/7 14:14
 */
public interface InsCardCommentFavoriteDetailService extends IService<InsCardCommentFavoriteDetail> {
	/**
	 * 删除评论点赞数据
	 *
	 * @param commentId
	 */
	void delByCommentId(long commentId);

	/**
	 * 获取我点赞的数据
	 *
	 * @param uid
	 * @return
	 */
	List<InsCardCommentFavoriteDetail> getMyFavorite(long uid);

	/**
	 * 通过评论id删除
	 *
	 * @param commentIds
	 */
	void delByCommentIds(List<Long> commentIds);
}
