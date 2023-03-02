package com.bbw.god.game.maou.processor;

import com.bbw.god.activity.IActivity;
import com.bbw.god.game.maou.cfg.GameMaouType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author suhq
 * @date 2018年12月6日 上午10:57:04
 */
@Service
public class GameMaouProcessorFactory {
	@Autowired
	@Lazy
	private List<AbstractGameMaouProcessor> gameMaouProcessors;

	/**
	 * 根据商品类型获取物品服务实现对象
	 *
	 * @param activity
	 * @return
	 */
	public AbstractGameMaouProcessor getMaouProcessor(IActivity activity) {
		GameMaouType gameMaouType = GameMaouType.fromActivity(activity.gainType());
		return gameMaouProcessors.stream().filter(mp -> mp.isMatch(gameMaouType)).findFirst().orElse(null);
	}

}
