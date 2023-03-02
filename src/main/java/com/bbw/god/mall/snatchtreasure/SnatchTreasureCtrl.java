package com.bbw.god.mall.snatchtreasure;

import com.bbw.god.controller.AbstractController;
import com.bbw.god.game.CR;
import com.bbw.god.rd.RDCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author suchaobin
 * @description 夺宝接口
 * @date 2020/6/29 16:48
 **/
@RestController
public class SnatchTreasureCtrl extends AbstractController {
	@Autowired
	private SnatchTreasureService snatchTreasureService;

	/**
	 * 进入夺宝界面
	 *
	 * @return
	 */
	@RequestMapping(CR.SnatchTreasure.ENTER_SNATCH_TREASURE)
	public RDSnatchTreasureInfo enterSnatchTreasure() {
		return snatchTreasureService.enterSnatchTreasure(getUserId());
	}

	/**
	 * 抽奖
	 *
	 * @param drawTimes
	 * @return
	 */
	@RequestMapping(CR.SnatchTreasure.DRAW)
	public RDSnatchTreasureDraw draw(int drawTimes) {
		return snatchTreasureService.draw(getUserId(), drawTimes);
	}

	/**
	 * 开启周累计宝箱
	 *
	 * @param boxId
	 * @return
	 */
	@RequestMapping(CR.SnatchTreasure.OPEN_WEEK_BOX)
	public RDCommon openWeekBox(int boxId) {
		return snatchTreasureService.openWeekBox(getUserId(), boxId);
	}

	/**
	 * 周累计宝箱奖励预览
	 *
	 * @param boxId
	 * @return
	 */
	@RequestMapping(CR.SnatchTreasure.GET_WEEK_BOX_AWARD)
	public RDSnatchTreasureBoxAward getBoxAward(int boxId) {
		return snatchTreasureService.getBoxAward(boxId);
	}
}
