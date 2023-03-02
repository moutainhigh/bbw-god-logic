package com.bbw.god.job.game;

import com.bbw.common.ListUtil;
import com.bbw.common.PowerRandom;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.activity.game.GameActivity;
import com.bbw.god.activity.game.GameActivityService;
import com.bbw.god.game.config.server.ServerTool;
import com.bbw.god.game.config.special.CfgSpecialEntity;
import com.bbw.god.game.config.special.SpecialTool;
import com.bbw.god.game.config.special.SpecialTypeEnum;
import com.bbw.god.game.data.GameDataService;
import com.bbw.god.server.special.GameSpecialPrice;
import com.bbw.god.server.special.GameSpecialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 以00:00为基准, 每8个小时执行合成特产高价区域变动
 *
 * @author suhq
 * @date 2019年3月11日 下午5:19:59
 */
@Component("gameSpecialPriceJob")
public class GameSpecialPriceJob extends GameJob {
	@Autowired
	private GameDataService gameDataService;
	@Autowired
	private GameSpecialService gameSpecialService;
	@Autowired
	private GameActivityService gameActivityService;

	/**
	 * 获取任务描述
	 *
	 * @return
	 */
	@Override
	public String getJobDesc() {
		return "每8个小时执行合成特产高价区域变动";
	}

	/**
	 * 具体的任务
	 */
	@Override
	public void job() {
		List<Integer> serverGroups = ServerTool.getServerGroups();
		for (Integer serverGroup : serverGroups) {
			GameActivity ga = gameActivityService.getGameActivity(serverGroup, ActivityEnum.TCHC);
			if (null == ga) {
				continue;
			}
			// 获取数据
			List<GameSpecialPrice> datas = gameSpecialService.getGameSpecialPrices(serverGroup);
			// 获取全部合成特产
			List<CfgSpecialEntity> specials = SpecialTool.getSpecials(SpecialTypeEnum.SYNTHETIC);
			// 判断每种合成特产是否都有数据，如果没有就生成
			for (CfgSpecialEntity special : specials) {
				boolean exist = datas.stream().anyMatch(tmp -> tmp.getSpecialId().equals(special.getId()));
				if (!exist) {
					gameSpecialService.initGameSynthesisSpecialData(special.getId(), serverGroup);
				}
			}
			// 重新获取数据
			datas = gameSpecialService.getGameSpecialPrices(serverGroup);
			// 每个合成特产修改高价区域
			for (GameSpecialPrice gameSpecialPrice : datas) {
				CfgSpecialEntity cfgSpecial = SpecialTool.getSpecialById(gameSpecialPrice.getSpecialId());
				Integer random = PowerRandom.getRandomFromList(cfgSpecial.getHighSellCountries());
				gameSpecialPrice.setHighPriceCountry(random);
			}
			// 保存数据
			if (ListUtil.isNotEmpty(datas)) {
				gameDataService.updateGameDatas(datas);
			}
		}
	}
}
