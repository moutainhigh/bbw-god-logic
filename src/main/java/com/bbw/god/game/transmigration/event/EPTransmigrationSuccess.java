package com.bbw.god.game.transmigration.event;

import com.bbw.common.DateUtil;
import com.bbw.god.event.BaseEventParam;
import com.bbw.god.game.transmigration.entity.GameTransmigration;
import com.bbw.god.game.transmigration.entity.UserTransmigrationRecord;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class EPTransmigrationSuccess extends BaseEventParam {
	/** 轮回开始时间 */
	private String transmigrationBeginDate;
	/** 挑战的城池ID */
	private Integer cityId;
	/** 基础评分 */
	private Integer score;
	/** 是否是新纪录 */
	private Boolean isNewRecord;
	/** 是否首次挑战成功 */
	private Boolean isFirstSuccess;


	public EPTransmigrationSuccess(GameTransmigration transmigration, UserTransmigrationRecord record, Boolean isFirstSuccess, BaseEventParam bep) {
		setValues(bep);
		this.transmigrationBeginDate = DateUtil.toDateTimeString(transmigration.getBegin());
		this.cityId = record.getCityId();
		this.score = record.gainScore();
		this.isNewRecord = record.isNewRecord();
		this.isFirstSuccess = isFirstSuccess;
	}
}
