package com.bbw.god.job.server;

import com.bbw.common.ListUtil;
import com.bbw.db.redis.RedisValueUtil;
import com.bbw.god.db.entity.CfgServerEntity;
import com.bbw.god.game.config.Cfg;
import com.bbw.god.game.config.CfgFst;
import com.bbw.god.server.ServerDataService;
import com.bbw.god.server.ServerDataType;
import com.bbw.god.server.ServerService;
import com.bbw.god.server.fst.FstRanking;
import com.bbw.god.server.fst.FstTool;
import com.bbw.god.server.fst.server.FstServerService;
import com.bbw.god.server.redis.ServerRedisKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 封神台积分增长定时器,以00:20为基准，每20分钟执行1次
 * 
 * @author suhq
 * @date 2019年3月17日 下午6:19:02
 */
@Slf4j
@Component("fstPointIncreaseJob")
public class FstPointIncreaseJob extends ServerJob {
	@Autowired
	private ServerService serverService;
	@Autowired
	private ServerDataService serverDataService;
	@Autowired
	private RedisValueUtil<FstRanking> serverDataRedis;
	@Autowired
	private FstServerService fstServerService;

	@Override
	public void job(CfgServerEntity server) {
		Set<TypedTuple<Long>> rankers = fstServerService.getAllRankers(server.getMergeSid());
		List<Long> uids = new ArrayList<>();
		Map<Long, Integer> uidsWithRank = new HashMap<>();
		int rank = 0;
		for (TypedTuple<Long> ranker : rankers) {
			rank++;
			Long uid = Optional.ofNullable(ranker.getValue()).orElse(-1L);
			if (uid > 0) {
				uids.add(uid);
			}
			uidsWithRank.put(uid, rank);
		}
		Set<String> keys = new HashSet<>();
		for (Long uid : uids) {
			//server:2071:fstRanking:190509207507697
			keys.add(ServerRedisKey.getServerDataKey(server.getMergeSid(), ServerDataType.FSTPVPRanking, uid));
		}
		List<FstRanking> fstRankers = serverDataRedis.getBatch(keys);
		// 没有排名不做任何处理
		if (ListUtil.isEmpty(fstRankers)) {
			return;
		}
		CfgFst cfgFst = Cfg.I.getUniqueConfig(CfgFst.class);
		List<FstRanking> needToUpdate = new ArrayList<>();
		for (FstRanking ranker : fstRankers) {
			// 已达可领积分上限，不做任何处理
			if (ranker.getIncrementPoints() >= cfgFst.getPointAwardLimit()) {
				continue;
			}
			int myRank = uidsWithRank.get(ranker.getId());
			// 获得排名积分奖励
			int addedPoint = FstTool.getPoinByRank(myRank);
			// 积分不超过上限
			if (ranker.getIncrementPoints() + addedPoint > cfgFst.getPointAwardLimit()) {
				addedPoint = cfgFst.getPointAwardLimit() - ranker.getIncrementPoints();
			}
			ranker.addIncrementPoints(addedPoint);
			needToUpdate.add(ranker);
			// log.info("{} 排名{},定时增长{}积分", ranker.getUid(), ranker.getRanking(), addedPoint);
		}
		// 更新需要更新积分的玩家
		if (ListUtil.isNotEmpty(needToUpdate)) {
			serverDataService.updateServerData(needToUpdate);
		}
	}

	@Override
	public void doJob(String sendMail) {
		super.doJob(sendMail);
	}

	// 必须重载，否则定时任务引擎认不到方法
	@Override
	public String getJobDesc() {
		return "封神台积分增长";
	}

}
