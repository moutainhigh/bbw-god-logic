package com.bbw.god.gameuser.statistic.resource;

import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.gameuser.statistic.BaseStatisticService;
import com.bbw.god.gameuser.statistic.StatisticTypeEnum;
import org.springframework.stereotype.Service;

import static com.bbw.god.game.data.redis.RedisKeyConst.SPLIT;

/**
 * @author suchaobin
 * @description 资源统计service
 * @date 2020/4/16 9:44
 */
@Service
public abstract class ResourceStatisticService extends BaseStatisticService {
	/**
	 * 获取类型总数，例：城池统计只有获得，没有消耗，返回1 元宝统计，有获得也有消耗，返回2
	 *
	 * @return 类型总数
	 */
	public abstract int getMyTypeCount();

	/**
	 * 获取当前资源类型
	 *
	 * @return 当前资源类型
	 */
	public abstract AwardEnum getMyAwardEnum();

	/**
	 * 获取redis的key
	 *
	 * @param uid      玩家id
	 * @param typeEnum 类型枚举
	 * @return redis的key
	 */
	@Override
	public String getKey(long uid, StatisticTypeEnum typeEnum) {
		StringBuilder sb = new StringBuilder();
		sb.append("usr");
		sb.append(SPLIT);
		sb.append(uid);
		sb.append(SPLIT);
		sb.append("0statistic");
		sb.append(SPLIT);
		sb.append("resource");
		sb.append(SPLIT);
		sb.append(getMyAwardEnum().getValue() );
		sb.append(SPLIT);
		sb.append(typeEnum.getValue());
		return sb.toString();
//		return "usr" + SPLIT + uid + SPLIT + "0statistic" + SPLIT + "resource" + SPLIT +
//				getMyAwardEnum().getValue() + SPLIT + typeEnum.getValue();
	}

	/**
	 * 清理统计数据
	 *
	 * @param uid 玩家id
	 */
	@Override
	public void clean(long uid) {
		cleanByKey(getKey(uid, StatisticTypeEnum.GAIN));
		cleanByKey(getKey(uid, StatisticTypeEnum.CONSUME));
	}
}
