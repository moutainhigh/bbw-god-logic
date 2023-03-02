package com.bbw.god.fight;

import com.bbw.god.city.mixd.nightmare.RDNightmareMxd;
import com.bbw.god.city.yeg.YeGuaiEnum;
import com.bbw.god.game.transmigration.rd.RDTransmigrationRecord;
import com.bbw.god.rd.RDCommon;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 战斗结果返回数据
 *
 * @author suhq
 * @date 2019年3月12日 下午2:01:08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RDFightResult extends RDCommon implements Serializable {
	private static final long serialVersionUID = 1L;
	// 是否攻下城池
	private Integer ownCity = null;
	// 城池坐标
	private Integer manor = null;
	// 胜利信息
	private String winDes = null;
	// 获得诛仙阵积分
//	private Integer addedZxzPoint = null;
	// 额外奖励宝箱次数[1,2]
	private Integer awardTimes = 1;
	// 是否胜利
	private Integer win = 0;
	// 战斗类型
	private Integer fightType = null;

	// 野怪类型
	private YeGuaiEnum yeGType = null;

	// 客户端埋点用
	private String infoForBuriedPoint = null;// 攻下第N座X级城
	private RDCommon returnTreasures = null;//战斗失败伪装退回的法宝

	private RDNightmareMxd mxd;

	/** 轮回世界战斗记录 */
	private RDTransmigrationRecord transmigrationRecord;

	/** 是否是商帮特殊野怪宝箱 */
	private boolean isBusinessGang;

}
