package com.bbw.mc.m2c;

import com.bbw.god.login.DynamicMenuEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 通知客户端信息服务
 *
 * @author suhq
 * @date 2019-06-03 10:16:42
 */
@Service
public class M2cService {
	@Autowired
	private M2cEventHandler m2cEventHandler;

	/**
	 * 发送红点通知
	 *
	 * @param uid
	 * @param redNotices
	 */
	public void sendRedNotice(long uid, List<String> redNotices) {
		RDM2c rd = new RDM2c();
		rd.setRedNotices(redNotices);
		M2c m2c = new M2c();
        m2c.setGuId(uid);
		m2c.setInfo(rd);
		m2cEventHandler.sendMsgToClient(m2c);
    }

	public void sendFsHeplerMsg(long uid) {
		RDM2c rd = new RDM2c();
		M2c m2c = new M2c();
		rd.setFsHepler(1);
		m2c.setGuId(uid);
		m2c.setInfo(rd);
		m2cEventHandler.sendMsgToClient(m2c);
	}

	public void sendDynamicMenu(long uid, DynamicMenuEnum menu, int num) {
		RDM2c rd = new RDM2c();
		rd.addOpenMenu(menu, num);
		M2c m2c = new M2c();
		rd.setFsHepler(1);
		m2c.setGuId(uid);
		m2c.setInfo(rd);
		m2cEventHandler.sendMsgToClient(m2c);
	}

	public void sendHexagramDoneMsg(long uid) {
		RDM2c rd = new RDM2c();
		M2c m2c = new M2c();
		rd.setCurrentHexagram(0);
		m2c.setGuId(uid);
		m2c.setInfo(rd);
		m2cEventHandler.sendMsgToClient(m2c);
	}
}
