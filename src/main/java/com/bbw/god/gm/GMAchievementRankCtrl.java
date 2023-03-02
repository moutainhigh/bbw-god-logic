package com.bbw.god.gm;

import com.bbw.common.Rst;
import com.bbw.god.controller.AbstractController;
import com.bbw.god.db.entity.CfgServerEntity;
import com.bbw.god.game.config.server.ServerTool;
import com.bbw.god.gameuser.achievement.AchievementRankService;
import com.bbw.god.gameuser.achievement.AchievementTool;
import com.bbw.god.gameuser.achievement.CfgAchievementEntity;
import com.bbw.god.gameuser.achievement.UserAchievementFixService;
import com.bbw.god.server.ServerUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

/**
 * 成就排名相关接口
 * 
 * @author suhq
 * @date 2020年03月06日 上午11:55:43
 */
@Slf4j
@RestController
@RequestMapping("/gm")
public class GMAchievementRankCtrl extends AbstractController {
	@Autowired
	private AchievementRankService achievementRankService;
	@Autowired
	private ServerUserService serverUserService;
	@Autowired
	private UserAchievementFixService userAchievementFixService ;


	/**
	 * 重置成就排名，此操作会清空榜单
	 * @param serverNames
	 * @return
	 */
	@GetMapping("server!resetAchievementRank")
	public Rst resetServerAchievementRank(String serverNames) {
		List<CfgServerEntity> servers = ServerTool.getServers(serverNames);
		for (CfgServerEntity server : servers) {
			try {
				int sId=server.getId();
				achievementRankService.removeServerRank(sId);
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}

		}
		return Rst.businessOK();
	}

	@GetMapping("game!resetAchievementRank")
	public Rst resetGameAchievementRank(int serverGroup) {
		achievementRankService.removeGameRank(serverGroup);
		return Rst.businessOK();
	}

	@GetMapping("user!resetUserAchievementRank")
	public Rst resetGameAchievementRank(int sId, String nickname) {
		Optional<Long> uidOptional = this.serverUserService.getUidByNickName(sId, nickname);
		if (!uidOptional.isPresent()) {
			return Rst.businessFAIL("不存在该角色");
		}
		Long uid = uidOptional.get();
		userAchievementFixService.fixUserAchievementScore(uid);
		return Rst.businessOK();
	}

	/**
	 * 获取成就基础配置
	 * @return
	 */
	@GetMapping("game!cfgAchievement")
	public Rst cfgAchievement() {
		List<CfgAchievementEntity> all = AchievementTool.getAllAchievements();
		int allScore = 0;//全部分数
		int score_10 = 0;//type = 10 的分数
		int score_20 = 0;//type = 20 的分数
		int score_30 = 0;//type = 30 的分数
		int score_40 = 0;//type = 40 的分数
		int score_50 = 0;//type = 50 的分数
		int score_60 = 0;//type = 60 的分数
		int score_70 = 0;//type = 70 的分数
		int num_10 = 0;//type = 10 的个数
		int num_20 = 0;//type = 20 的个数
		int num_30 = 0;//type = 30 的个数
		int num_40 = 0;//type = 40 的个数
		int num_50 = 0;//type = 50 的个数
		int num_60 = 0;//type = 60 的个数
		int num_70 = 0;//type = 70 的个数
		for(CfgAchievementEntity achievement : all){
			allScore += achievement.getScore();
			switch (achievement.getType()){
				case 10:
					score_10 += achievement.getScore();
					num_10 += 1;
					break;
				case 20:
					score_20 += achievement.getScore();
					num_20 += 1;
					break;
				case 30:
					score_30 += achievement.getScore();
					num_30 += 1;
					break;
				case 40:
					score_40 += achievement.getScore();
					num_40 += 1;
					break;
				case 50:
					score_50 += achievement.getScore();
					num_50 += 1;
					break;
				case 60:
					score_60 += achievement.getScore();
					num_60 += 1;
					break;
				case 70:
					score_70 += achievement.getScore();
					num_70 += 1;
					break;
			}

		}
		return Rst.businessOK()
				.put("全部分数",allScore)
				.put("个人",num_10)
				.put("卡牌",num_20)
				.put("商途",num_30)
				.put("征战",num_40)
				.put("竞技",num_50)
				.put("历练",num_60)
				.put("秘闻",num_70)
				.put("个人分数",score_10)
				.put("卡牌分数",score_20)
				.put("商途分数",score_30)
				.put("征战分数",score_40)
				.put("竞技分数",score_50)
				.put("历练分数",score_60)
				.put("秘闻分数",score_70);
	}

}
