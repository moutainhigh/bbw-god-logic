package com.bbw.god.job.game.push;

import com.bbw.common.ListUtil;
import com.bbw.mc.push.PushAction;
import com.bbw.god.notify.push.PushEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 给客户端推送魔王定时器
 * 
 * @author suhq
 * @date 2019-08-20 14:47:51
 */
@Component("maouPushJob")
public class MaouPushJob extends PushJob {
	@Autowired
	private PushAction pushAction;

	// 必须重载，否则定时任务引擎认不到方法
	@Override
	public String getJobDesc() {
		return "客户端魔王推送";
	}

	@Override
	public void doJob(String sendMail) {
		super.doJob(sendMail);
	}

	@Override
	public void job() {
		List<Long> allUids = getUids();
		toPush(allUids);
	}

	@Override
	public void toPush(List<Long> uids) {
		uids = getAblePushUids(uids, PushEnum.MO_WANG);
		if (ListUtil.isNotEmpty(uids)) {
			String title = "魔王还有5分钟到达战场，请做好准备！";
			String content = "魔王即将来袭，请做好准备！";
			pushAction.push(uids, title, content);
		}
	}
}
