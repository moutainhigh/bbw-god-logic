package com.bbw.god.detail;

import com.bbw.common.DateUtil;
import com.bbw.common.ID;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.gameuser.GameUser;
import lombok.Data;

import java.io.Serializable;

/**
 * 明细
 *
 * @author suhq
 * @date 2019年3月13日 下午2:15:48
 */
@Data
public class DetailData implements Serializable {
	private static final long serialVersionUID = 1L;
	private Long id; //
	private Integer sid; //区服ID
	private Long uid; //玩家ID
	private Integer userLevel; //玩家等级
	private Integer opdate; //操作日期
	private Integer optime; //操作时间
	private Long afterValue; //变化后的值
	private WayEnum way; //途经
	private AwardDetail awardDetail;//明细

	public static DetailData instance(GameUser user, WayEnum way, AwardDetail awardDetail) {
		DetailData data = new DetailData();
		data.setId(ID.getNextDetailId());
		data.setSid(user.getServerId());
		data.setUid(user.getId());
		data.setUserLevel(user.getLevel());
		data.setOpdate(DateUtil.getTodayInt());
		data.setOptime(DateUtil.toHMSInt(DateUtil.now()));
		data.setAwardDetail(awardDetail);
		data.setWay(way);
		return data;
	}

}
