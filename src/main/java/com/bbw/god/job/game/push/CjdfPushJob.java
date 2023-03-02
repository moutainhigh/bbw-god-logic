package com.bbw.god.job.game.push;

import com.bbw.common.ListUtil;
import com.bbw.god.game.sxdh.config.SxdhTool;
import com.bbw.god.gameuser.config.GameUserConfig;
import com.bbw.god.server.ServerUserService;
import com.bbw.mc.push.PushAction;
import com.bbw.god.notify.push.PushEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 给客户端推送阐截斗法定时器
 * 
 * @author suhq
 * @date 2019-08-20 14:47:51
 */
@Component("cjdfPushJob")
public class CjdfPushJob extends PushJob {
	@Autowired
	private PushAction pushAction;
	@Autowired
	private ServerUserService serverUserService;

	// 必须重载，否则定时任务引擎认不到方法
	@Override
	public String getJobDesc() {
		return "客户端阐截斗法推送";
	}

	@Override
	public void doJob(String sendMail) {
		super.doJob(sendMail);
	}

	@Override
	public List<Long> getUids() {
		Set<Long> uids = serverUserService.getUidsLevelBetween(SxdhTool.getSxdh().getPvpUnlockLevel(), GameUserConfig.bean().getGuTopLevel());
		return uids.stream().collect(Collectors.toList());
	}

	@Override
	public void job() {
		List<Long> allUids = getUids();
		toPush(allUids);
	}

	@Override
	public void toPush(List<Long> uids) {
		uids = getAblePushUids(uids, PushEnum.CHAN_JIE_DOU_FA);
		if (ListUtil.isNotEmpty(uids)) {
			String title = "教派战斗一触即发，召唤师速速前往！";
			String content = "教派战斗一触即发，召唤师速速前往！";
			pushAction.push(uids, title, content);
		}
	}
}
