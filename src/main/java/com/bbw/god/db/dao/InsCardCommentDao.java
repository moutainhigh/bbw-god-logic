package com.bbw.god.db.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.bbw.god.db.entity.InsCardComment;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * @author suchaobin
 * @date 2020/4/7 14:11
 */
public interface InsCardCommentDao extends BaseMapper<InsCardComment> {
	/**
	 * 更新点赞数
	 *
	 * @param commentId 评论id
	 */
	@Update("update ins_card_comment set favorite_count = favorite_count + 1 where id = #{commentId}")
	void addFavorite(@Param("commentId") long commentId);

	/**
	 * 修改点赞数
	 *
	 * @param commentIds
	 * @param newValue
	 */
	@Update({"<script>",
			"update ins_card_comment set favorite_count = #{newValue} where id in " +
			"<foreach item='id' collection='commentIds' open='(' separator=',' close=')'>",
			"#{id}",
			"</foreach>",
			"</script>",})
	void updateFavoriteCount(@Param("commentIds") List<Long> commentIds, @Param("newValue") int newValue);
}
