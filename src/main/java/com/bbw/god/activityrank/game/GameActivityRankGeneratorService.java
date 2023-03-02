package com.bbw.god.activityrank.game;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bbw.god.activity.config.ActivityConfig;
import com.bbw.god.game.data.GameDataService;
import com.bbw.mc.mail.MailAction;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class GameActivityRankGeneratorService {
	@Autowired
	private MailAction notify;
	@Autowired
	private GameDataService gameDataService;
	@Autowired
	private ActivityConfig activityConfig;

	/**
	 * 添加仙缘榜
	 * 
	 * @param week
	 */
	public void addXianYuanRank(int serverGroup, Date beginDate, Date endDate) {

	}
}
