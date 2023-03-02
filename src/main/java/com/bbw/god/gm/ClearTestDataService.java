package com.bbw.god.gm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.bbw.common.SpringContextUtil;
import com.bbw.db.redis.RedisBase;
import com.bbw.god.db.dao.CfgServerDao;
import com.bbw.god.db.entity.CfgServerEntity;
import com.bbw.god.db.pool.PlayerDataDAO;
import com.bbw.god.db.service.InsRoleInfoService;
import com.bbw.god.game.config.Cfg;
import com.bbw.god.server.ServerStatus;

/**
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-04-09 23:01
 */
@Service
public class ClearTestDataService {
	@Autowired
	private RedisBase redisBase;
	@Autowired
	private InsRoleInfoService roleInfo;
	@Autowired
	private CfgServerDao cfgServerDao;
	@Autowired
	private JdbcTemplate jdbc;

	/**
	 * 标志区服为发布状态,需要通知所有逻辑服务器
	 * 
	 * @param sid
	 * @return
	 */
	public boolean releaseServer(int sid) {
		CfgServerEntity server = Cfg.I.get(sid, CfgServerEntity.class);
		server.setMemo("正式运行");
		int b = cfgServerDao.updateById(server);
		Cfg.I.reload(server.getId(), CfgServerEntity.class);
		return b > 0;
	}

	/**
	 * 清除某个区服的数据，仅测试时候可用
	 * 
	 * @param sid
	 * @return
	 */
	public boolean clear(int sid) {
		// 这个行为太危险，必须要有保险丝。在server设置为开发测试或者预告中的情况下才可以删除
		CfgServerEntity server = Cfg.I.get(sid, CfgServerEntity.class);
		if (server.isDevTest() || server.getServerStatus() == ServerStatus.PREDICTING) {
			clearUserData(sid);
			jdbc.execute("DELETE  FROM ins_role_info WHERE sid=" + sid);
		}
		return true;
	}

	/**
	 * 清除区服的用户数据
	 * 
	 * @param sid
	 * @return
	 */
	public boolean clearUserData(int sid) {
		CfgServerEntity server = Cfg.I.get(sid, CfgServerEntity.class);
		if (server.isDevTest()) {
			// 删除redis用户数据
			String blear = "usr:??????" + String.format("%04d", sid) + "?????*";
			redisBase.deleteBlear(blear);
			PlayerDataDAO pdd = SpringContextUtil.getBean(PlayerDataDAO.class, server.getMergeSid());
			pdd.dbDelPlayers();
		}
		return true;
	}
}
