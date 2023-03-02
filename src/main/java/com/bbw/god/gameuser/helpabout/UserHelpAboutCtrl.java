package com.bbw.god.gameuser.helpabout;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bbw.god.controller.AbstractController;
import com.bbw.god.game.CR;
import com.bbw.god.rd.RDCommon;

/**
 * 文字帮助 阅读奖励
* @author lwb  
* @date 2019年4月10日  
* @version 1.0  
*/
@RestController
public class UserHelpAboutCtrl extends AbstractController{
	@Autowired
	private UserHelpAboutService userHelpService;
	
	/*
	 * 获取帮助列表
	 */
	@GetMapping(CR.HelpAbout.LIST_HELPABOUT)
	public RDHelpAbout listHelpAbouts() {
		return userHelpService.getRDHelpAboutList(getUserId());
	}
	/*
	 * 领取奖励
	 */
	@GetMapping(CR.HelpAbout.GAIN_AWARD)
	public RDCommon gainAward(Long uid, int helpid) {
		return userHelpService.gainAward(getUserId(),helpid);
	}
}
