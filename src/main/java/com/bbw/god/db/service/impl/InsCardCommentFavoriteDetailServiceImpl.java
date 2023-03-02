package com.bbw.god.db.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.bbw.god.db.dao.InsCardCommentFavoriteDetailDao;
import com.bbw.god.db.entity.InsCardCommentFavoriteDetail;
import com.bbw.god.db.service.InsCardCommentFavoriteDetailService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author suchaobin
 * @date 2020/4/7 14:15
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class InsCardCommentFavoriteDetailServiceImpl extends ServiceImpl<InsCardCommentFavoriteDetailDao,
		InsCardCommentFavoriteDetail> implements InsCardCommentFavoriteDetailService {

	@Override
	public void delByCommentId(long commentId) {
		EntityWrapper<InsCardCommentFavoriteDetail> wrapper = new EntityWrapper<>();
		wrapper.eq("comment_id", commentId);
		this.delete(wrapper);
	}

	@Override
	public List<InsCardCommentFavoriteDetail> getMyFavorite(long uid) {
		EntityWrapper<InsCardCommentFavoriteDetail> wrapper = new EntityWrapper<>();
		wrapper.eq("rid", uid);
		return this.selectList(wrapper);
	}

	@Override
	public void delByCommentIds(List<Long> commentIds) {
		EntityWrapper<InsCardCommentFavoriteDetail> wrapper = new EntityWrapper<>();
		wrapper.in("comment_id", commentIds);
		this.delete(wrapper);
	}

}
