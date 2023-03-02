package com.bbw.god.gm;

import com.bbw.common.DateUtil;
import com.bbw.common.Rst;
import com.bbw.god.db.entity.CfgServerEntity;
import com.bbw.god.game.config.card.CardTool;
import com.bbw.god.game.config.server.ServerTool;
import com.bbw.god.game.data.GameData;
import com.bbw.god.game.data.GameDayDataService;
import com.bbw.god.game.flx.FlxDayResult;
import com.bbw.god.game.flx.FlxDayResultService;
import com.bbw.god.game.flx.FlxYYLTipService;
import com.bbw.god.server.ServerDataService;
import com.bbw.god.server.ServerService;
import com.bbw.god.server.flx.FlxService;
import com.bbw.god.server.flx.FlxYaYaLeBet;
import com.bbw.god.server.flx.ServerFlxResult;
import com.bbw.god.server.flx.ServerFlxResultService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author suchaobin
 * @description 福临轩操作接口
 * @date 2020/4/20 9:03
 */
@RestController
@RequestMapping("/gm/flx")
@Slf4j
public class GMFlxCtrl {
	@Autowired
	private ServerService serverService;
	@Autowired
	private GameDayDataService gameDayDataService;
	@Autowired
	private FlxDayResultService flxDayResultService;
	@Autowired
	private FlxYYLTipService tipService;
	@Autowired
	private FlxService flxService;
	@Autowired
	private ServerFlxResultService serverFlxResultService;
	@Autowired
	private ServerDataService serverDataService;

	@RequestMapping("changeAward")
	public Rst changeAward(int dateInt, int oldCardId, int newCardId) {
		List<CfgServerEntity> servers = ServerTool.getServers();
		for (CfgServerEntity server : servers) {
			ServerFlxResult result = serverService.getServerDatas(server.getMergeSid(), ServerFlxResult.class)
					.stream().filter(flx -> flx.getDateInt() == dateInt).findFirst().orElse(null);
			if (null != result && result.getAwardCardId() == oldCardId) {
				result.setAwardCardId(newCardId);
				result.setAwardCardName(CardTool.getCardById(newCardId).getName());
				serverService.updateServerData(result);
			}
		}
		return Rst.businessOK();
	}

	@RequestMapping("regenerateData")
	public Rst regenerateData(int beginDate, int days) {
		//删除福临轩结果数据
		List<FlxDayResult> results = gameDayDataService.getGameData(FlxDayResult.class);
		List<FlxDayResult> delDatas = results.stream().filter(tmp ->
				tmp.getDateInt() >= beginDate).collect(Collectors.toList());
		gameDayDataService.deleteGameDatas(delDatas.stream()
				.map(GameData::getId).collect(Collectors.toList()), FlxDayResult.class);
		// 重新生成结果数据
		flxDayResultService.prepareDatas(days);
		// 重新生成提示数据
		tipService.regenerateData(beginDate);
		return Rst.businessOK();
	}

	@RequestMapping("resendFlxAward")
	public Rst resendFlxAward(int date) {
		sendFlxMailAward(date);
		return Rst.businessOK();
	}

	private void sendFlxMailAward(int dateInt) {
		// 全服 压压乐 投入
		List<FlxYaYaLeBet> ysgBets = flxService.getALLYaYaLeBets(dateInt);
		// 按照区服分类
		Map<Long, List<FlxYaYaLeBet>> serverYylBets = ysgBets.stream().collect(Collectors.groupingBy(FlxYaYaLeBet::getServerFlxResult));

		List<CfgServerEntity> servers = ServerTool.getAvailableServers();
		for (CfgServerEntity server : servers) {
			// 投注开奖
			Optional<ServerFlxResult> dateResult = serverFlxResultService.getSingleResultByDate(server.getId(), dateInt);
			if (dateResult.isPresent()) {
				ServerFlxResult sfr = dateResult.get();
				List<FlxYaYaLeBet> yyl = serverYylBets.get(sfr.getId());
				flxService.sendYsgMailAward(sfr, yyl, dateInt);
				log.info("{}压压乐奖励{}发送完毕！", server.getId() + server.getName(), DateUtil.nowToString());
			} else {
				log.error("错误!!!{}福临轩奖励没有发放!", DateUtil.nowToString());
			}
		}
	}
}
