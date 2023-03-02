package com.bbw.god.game.transmigration.entity;

import com.bbw.common.DateUtil;
import com.bbw.common.ID;
import com.bbw.common.ListUtil;
import com.bbw.god.gameuser.UserData;
import com.bbw.god.gameuser.UserDataType;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 轮回记录
 *
 * @author: suhq
 * @date: 2021/9/10 4:20 下午
 */
@Data
public class UserTransmigrationRecord extends UserData implements Serializable {
	private static final long serialVersionUID = 6012478103138202577L;
	private int cityId;
	/** 是否是新纪录 */
	private boolean isNewRecord;
	/** 战斗回合评分,使用法宝评分,剩余血量评分,死亡神将评分,扣除血量评分,击杀神将评分 */
	private List<Integer> scoreCompositions;
	private String videoUrl;
	private Date date;

	public static UserTransmigrationRecord getInstance(long uid, int cityId, List<Integer> scoreCompositions) {
		UserTransmigrationRecord record = new UserTransmigrationRecord();
		record.setId(ID.INSTANCE.nextId());
		record.setGameUserId(uid);
		record.setCityId(cityId);
		record.setScoreCompositions(scoreCompositions);
		record.setDate(DateUtil.now());
		return record;
	}

	/**
	 * 获取评分
	 *
	 * @return
	 */
	public int gainScore() {
		if (ListUtil.isEmpty(scoreCompositions)) {
			return 0;
		}
		return ListUtil.sumInt(scoreCompositions);
	}

	@Override
	public UserDataType gainResType() {
		return UserDataType.USER_TRANSMIGRATION_RECORD;
	}
}
