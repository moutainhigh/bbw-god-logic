package com.bbw.god.game.combat.exaward;

import com.bbw.exception.CoderException;
import com.bbw.god.game.combat.data.Combat;
import com.bbw.god.game.combat.data.PlayerId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-08-12 14:15
 */
@Service
public class ExAwardCheckFactory {
	@Lazy
	@Autowired
	private List<ExAwardCheck> services;

	public ExAwardCheck getExAwardCheckService(int exAwardId) {
		for (ExAwardCheck service : services) {
			if (service.support(exAwardId)) {
				return service;
			}
		}
		throw CoderException.high("没有ID=" + exAwardId + "的额外奖励！");
	}
	
	/**
	 * 检查是否完成获得额外奖励
	 * @param combat
	 * @param playerId
	 * @param exAwardId
	 * @return
	 */
	public boolean hasGainExAward(Combat combat,int exAwardId) {
		for (ExAwardCheck service : services) {
			if (service.support(exAwardId)) {
				return service.check(combat);
			}
		}
		return false;
	}
}
