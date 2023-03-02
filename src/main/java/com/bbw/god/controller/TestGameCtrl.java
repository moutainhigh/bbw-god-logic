package com.bbw.god.controller;

import com.bbw.common.DateUtil;
import com.bbw.god.game.GameService;
import com.bbw.god.game.flx.FlxDayResultService;
import com.bbw.god.game.online.TapdbReporter;
import com.bbw.god.job.server.RemoveServerTempData;
import com.bbw.god.server.ServerUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 区服服务测试
 *
 * @author shaojun
 * @email lsj@bamboowind.cn
 * @date 2018-09-12 15:15:04
 */
@RestController
@RequestMapping(value = "/coder")
public class TestGameCtrl {
    @Autowired
    private GameService gs;
    @Autowired
    private FlxDayResultService flxDayResultService;
    //	@Autowired
//	private ServerMaouDayConfigService mowangService;
    @Autowired
    private TapdbReporter tapdbReporter;
    @Autowired
    private ServerUserService serverUserService;
    @Autowired
    private RemoveServerTempData removeServerTempData;

    @RequestMapping("removeServerTempData")
    public void removeServerTempData(Long uid) {
		this.removeServerTempData.doJob("0");
    }

    @RequestMapping("unloadUser")
    public void gameInit(Long uid) {
		this.serverUserService.unloadGameUser(uid);
    }

    @RequestMapping("Test!init")
    public void gameInit(Integer serverId) {
		this.gs.init();
    }

    @RequestMapping("Test!tapdbOnlines")
    public void tapdbOnlines(Integer serverId) {
		this.tapdbReporter.reportOnline();
    }

    @RequestMapping("Test!flxresult")
    public void flxResult(Integer days) {
		this.flxDayResultService.prepareDatas(days);
		this.flxDayResultService.check(DateUtil.now());
        //System.out.println(JSON.toJSONString(flxDayResultService.getTodayResult()));
        //System.out.println(JSON.toJSONString(flxDayResultService.getLastFLXBetResults(5)));
    }

    @RequestMapping("Test!mowang")
    public void mowang(Integer days) {
//		mowangService.prepareDatas(days);
    }

}
