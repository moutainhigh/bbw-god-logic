package com.bbw.god.city.yeg;

import com.bbw.god.game.combat.runes.RunesTool;
import com.bbw.god.game.config.WayEnum;
import org.springframework.stereotype.Service;

/**
 * @author 作者 ：lwb
 * @version 创建时间：2020年2月26日 下午11:30:55 类说明 普通野怪
 */
@Service
public class YeGNormalFightProcessor extends AbstractYeGFightProcessor {

	@Override
	public YeGuaiEnum getYeGEnum() {
		return YeGuaiEnum.YG_NORMAL;
	}

	@Override
	public boolean open(long uid) {
		return true;
	}

	@Override
	public WayEnum getWay() {
		return WayEnum.YG_OPEN_BOX;
	}

	@Override
	public int getRunesId() {
		return RunesTool.getRandomYGRunesId(false);
	}
}
