package com.bbw.god.gameuser.statistic.behavior.leader.equipment;

import com.bbw.god.gameuser.statistic.behavior.BehaviorStatistic;
import com.bbw.god.gameuser.statistic.behavior.BehaviorType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/** 法外分身装备统计
 * @author lzc
 * @description
 * @date 2021/4/15 11:28
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class LeaderEquipmentStatistic extends BehaviorStatistic {
	/** （0未获得、凡品10、中20、上品30、精品40、极品50、仙品60） */
	/** 武器品质 */
	private int weaponQuality = 0;
	/** 衣服品质 */
	private int clothesQuality = 0;
	/** 戒指品质 */
	private int ringQuality = 0;
	/** 项链品质 */
	private int necklaceQuality = 0;
	/** 武器等级 */
	private int weaponLv = 0;
	/** 衣服等级 */
	private int clothesLv = 0;
	/** 戒指等级 */
	private int ringLv = 0;
	/** 项链等级 */
	private int necklaceLv = 0;

	public LeaderEquipmentStatistic() {
		super(BehaviorType.LEADER_EQUIPMENT);
	}

	public LeaderEquipmentStatistic(Integer weaponQuality,Integer clothesQuality,Integer ringQuality,Integer necklaceQuality,Integer weaponLv,Integer clothesLv,Integer ringLv,Integer necklaceLv) {
		super(BehaviorType.LEADER_EQUIPMENT);
		this.weaponQuality = weaponQuality;
		this.clothesQuality = clothesQuality;
		this.ringQuality = ringQuality;
		this.necklaceQuality = necklaceQuality;
		this.weaponLv = weaponLv;
		this.clothesLv = clothesLv;
		this.ringLv = ringLv;
		this.necklaceLv = necklaceLv;
	}
}
