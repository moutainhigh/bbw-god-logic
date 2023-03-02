package com.bbw.god.db.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.bbw.common.StrUtil;
import com.bbw.god.db.dao.InsCardCommentDao;
import com.bbw.god.db.entity.InsCardComment;
import com.bbw.god.db.service.InsCardCommentService;
import com.bbw.god.game.config.card.CardTool;
import com.bbw.god.game.config.card.CfgCardEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author suchaobin
 * @date 2020/4/7 14:17
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class InsCardCommentServiceImpl extends ServiceImpl<InsCardCommentDao, InsCardComment> implements InsCardCommentService {
	@Autowired
	private InsCardCommentDao insCardCommentDao;

	@Override
	public List<InsCardComment> getHotComments(int serverGroup, int cardId, int minFavorite, int page, int limit) {
		EntityWrapper<InsCardComment> wrapper = new EntityWrapper<>();
		wrapper.eq("server_group", serverGroup).eq("card_id", cardId)
				.ge("favorite_count", minFavorite).orderBy("favorite_count", false)
				.orderBy("comment_time", false);
		Page<InsCardComment> myPage = new Page<>(page, limit);
		return insCardCommentDao.selectPage(myPage, wrapper);
	}

	/**
	 * 获取卡牌热门评论中，包括总页数
	 *
	 * @param serverGroup
	 * @param cardId
	 * @param start
	 * @param end
	 * @param field
	 * @param order
	 * @param page
	 * @param limit
	 * @return
	 */
	@Override
	public Map<String, Object> getComments(Integer serverGroup, Integer cardStar, Integer cardId, String start,
										   String end, String field, String order, int page, int limit) {
		Map<String, Object> map = new HashMap<>(4);
		EntityWrapper<InsCardComment> wrapper = new EntityWrapper<>();
		if (null != serverGroup) {
			wrapper.eq("server_group", serverGroup);
		}
		if (null != cardId) {
			wrapper.eq("card_id", cardId);
		} else if (null != cardStar && cardStar > 0) {
			List<Integer> cardIds = CardTool.getAllCards().stream().filter(s ->
					s.getStar().equals(cardStar)).map(CfgCardEntity::getId).collect(Collectors.toList());
			wrapper.in("card_id", cardIds);
		}
		start += " 00:00:00";
		end += " 23:59:59";
		wrapper.between("comment_time", start, end);

		if (StrUtil.isNotBlank(field) && StrUtil.isNotBlank(order)) {
			boolean sort = order.equals("asc");
			wrapper.orderBy(field, sort);
		} else {
			wrapper.orderBy("comment_time", false);
		}
		Page<InsCardComment> myPage = new Page<>(page, limit);
		List<InsCardComment> insCardComments = insCardCommentDao.selectPage(myPage, wrapper);
		map.put("data", insCardComments);
		map.put("total", insCardCommentDao.selectCount(wrapper));
		return map;
	}


	@Override
	public List<InsCardComment> getRecentComments(int serverGroup, int cardId, int page, int limit, List<Long> excludeIds) {
		EntityWrapper<InsCardComment> wrapper = new EntityWrapper<>();
		wrapper.eq("server_group", serverGroup).eq("card_id", cardId)
				.notIn("id", excludeIds).orderBy("comment_time", false);
		Page<InsCardComment> myPage = new Page<>(page, limit);
		return insCardCommentDao.selectPage(myPage, wrapper);
	}

	@Override
	public InsCardComment getMyComment(int serverGroup, int cardId, long uid) {
		EntityWrapper<InsCardComment> wrapper = new EntityWrapper<>();
		wrapper.eq("server_group", serverGroup).eq("card_id", cardId)
				.eq("rid", uid);
		return this.selectOne(wrapper);
	}

	@Override
	public void addFavorite(long commentId) {
		insCardCommentDao.addFavorite(commentId);
	}

	@Override
	public void updateFavoriteCount(List<Long> commentIds, int newValue) {
		insCardCommentDao.updateFavoriteCount(commentIds, newValue);
	}

}
