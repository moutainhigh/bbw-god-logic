package com.bbw.god.db.entity;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import com.bbw.common.JSONUtil;
import com.bbw.god.gameuser.statistic.BaseStatistic;
import com.bbw.god.gameuser.statistic.StatisticDataType;
import lombok.Data;

import java.io.Serializable;

/**
 * @author suchaobin
 * @description 玩家统计数据
 * @date 2020/4/27 11:13
 */
@Data
@TableName("ins_user_statistic")
public class InsUserStatistic implements Serializable {
	private static final long serialVersionUID = -857888914160633280L;
	@TableId(type = IdType.INPUT)
	private String id;
	private Integer sid;
	private Long uid;
	private Integer clazzValue;
	private String dataJson;

	public static InsUserStatistic fromBaseStatistic(long uid, int sid, String key, BaseStatistic statistic) {
		InsUserStatistic insUserStatistic = new InsUserStatistic();
		insUserStatistic.setId(key);
		insUserStatistic.setUid(uid);
		insUserStatistic.setSid(sid);
		String clazz = statistic.getClass().getTypeName();
		insUserStatistic.setClazzValue(StatisticDataType.fromClazz(clazz).getValue());
		insUserStatistic.setDataJson(JSONUtil.toJson(statistic));
		return insUserStatistic;
	}
}
