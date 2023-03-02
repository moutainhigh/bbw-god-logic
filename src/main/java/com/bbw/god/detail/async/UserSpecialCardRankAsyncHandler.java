package com.bbw.god.detail.async;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.bbw.common.ListUtil;
import com.bbw.common.SpringContextUtil;
import com.bbw.db.Query;
import com.bbw.god.db.entity.UserSpecialCardRankEntity;
import com.bbw.god.db.service.UserSpecialCardRankService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 玩家卡牌打技能存储
 *
 * @author: suhq
 * @date: 2021/12/16 1:56 下午
 */
@Slf4j
@Async
@Component
public class UserSpecialCardRankAsyncHandler {
	private static final int MAX_COUNT = 10;

	/**
	 * 记录明细
	 *
	 * @param detailData
	 */
	public void log(UserSpecialCardRankEntity detailData) {
		try {
			long uid = detailData.getUid();
			int cardId = detailData.getCardId();
			UserSpecialCardRankService specialCardRankService = SpringContextUtil.getBean(UserSpecialCardRankService.class);
			EntityWrapper<UserSpecialCardRankEntity> ew = new EntityWrapper<>();
			ew.eq("fight_type", uid).eq("card_id", cardId).eq("gid", detailData.getGid());
			int selectCount = specialCardRankService.selectCount(ew);
			if (selectCount >= MAX_COUNT) {
				//删除多余的，倒叙查询出多余的数据
				ew.orderBy("fight_rank", false);
				Map<String, Object> params = new HashMap<>();
				params.put("limit", String.valueOf(selectCount - MAX_COUNT + 1));
				Page<UserSpecialCardRankEntity> page = specialCardRankService.selectPage(new Query<UserSpecialCardRankEntity>(params).getPage(), ew);
				List<UserSpecialCardRankEntity> records = page.getRecords();
				if (ListUtil.isNotEmpty(records)) {
					//取出最后一条，删除其余的
					UserSpecialCardRankEntity cardRankEntity = records.get(records.size() - 1);
					List<Long> collect = records.stream().map(UserSpecialCardRankEntity::getId).collect(Collectors.toList());
					collect.remove(cardRankEntity.getId());
					specialCardRankService.deleteBatchIds(collect);
					if (detailData.getFightRank() <= cardRankEntity.getFightRank()) {
						//说明 将要新增的比 数据库中的更好，所以替换数据库中的
						specialCardRankService.deleteById(cardRankEntity.getId());
					} else {
						//说明 数据库中的更好，所以不新增
						return;
					}
				}
			}
			//新增数据
			specialCardRankService.insert(detailData);
		} catch (Exception e) {
			log.error("卡牌炼技-排名信息更新失败！");
			log.error(e.getMessage(), e);
		}
	}

}
