package com.bbw.god.server.monster;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bbw.common.DateUtil;
import com.bbw.common.JSONUtil;
import com.bbw.common.ListUtil;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.cache.TimeLimitCacheUtil;
import com.bbw.god.city.yeg.YeGuaiEnum;
import com.bbw.god.fight.RDFightResult;
import com.bbw.god.fight.RDFightsInfo;
import com.bbw.god.game.config.Cfg;
import com.bbw.god.game.config.CfgMonster;
import com.bbw.god.game.config.card.CardTool;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.unique.UserMonster;
import com.bbw.god.server.ServerService;
import com.bbw.god.server.monster.RDMonsterList.RDMonsterInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 帮好友打怪
 * 
 * @author suhq
 * @date 2018年12月27日 下午3:00:55
 */
@Service
public class MonsterLogic {
	@Autowired
	private GameUserService gameUserService;
	@Autowired
	private ServerService serverService;
	@Autowired
	private MonsterService monsterService;

	/**
	 * 获得好友打不过的野怪
	 * 
	 * @param guId
	 * @return
	 */
	public RDMonsterList getBuddyMonster(long guId, int sid) {
		RDMonsterList rd = new RDMonsterList();
		List<ServerMonster> serverMonsters = monsterService.getBuddyMonsters(guId, sid);
		List<RDMonsterInfo> monsters = new ArrayList<>();
		if (ListUtil.isNotEmpty(serverMonsters)) {
			monsters = serverMonsters.stream().filter(p->p.getBlood()>0).map(sm -> {
				JSONObject soliders = JSONObject.parseObject(sm.getSoliders());
				RDMonsterInfo monsterInfo = new RDMonsterInfo();
				JSONArray cards = soliders.getJSONArray("cards");
				monsterInfo.setId(sm.getId());
				monsterInfo.setGuName(sm.getFinderName());
				if (sm.getHead() == null) {
					int monsterHead = cards.getJSONObject(0).getIntValue("baseId");
					monsterInfo.setHead(monsterHead);
					String name = CardTool.getCardById(monsterHead).getName();
					monsterInfo.setMonsterName(name);
				} else {
					monsterInfo.setHead(sm.getHead());
					monsterInfo.setMonsterName(sm.getMonsterName());
				}
				monsterInfo.setHeadIcon(sm.getHeadIcon());
				monsterInfo.setLevel(soliders.getIntValue("level"));
				monsterInfo.setBlood(sm.getBlood());
				Long remainTime = sm.getEacapeTime().getTime() - System.currentTimeMillis();
				monsterInfo.setRemainTime(remainTime.intValue());
				return monsterInfo;
			}).collect(Collectors.toList());
		}
		rd.setMonsters(monsters);
		rd.setNextBeatTime(this.monsterService.getRemainTimeToBeat(guId));
		return rd;
	}

	/**
	 * 帮好友打野怪,获取野怪卡牌，并更新玩家打怪时间
	 * 
	 * @param guId
	 * @param monsterId
	 * @return
	 */
	public RDFightsInfo attackMonster(long guId, long monsterId) {
		ServerMonster monster = serverService.getServerData(gameUserService.getActiveSid(guId), monsterId, ServerMonster.class);
		// 是否存在或者逃跑
		if (monster == null || System.currentTimeMillis() - monster.getEacapeTime().getTime() > 1000) {
			throw new ExceptionForClientTip("monster.not.exist");
		}
		// 是否打败
		if (monster.getBeDefeated()) {
			throw new ExceptionForClientTip("monster.is.defeated");
		}

		UserMonster umHelp = gameUserService.getSingleItem(guId, UserMonster.class);
		if (umHelp != null) {
			int remainMinutes = (int) ((umHelp.getNextBeatTime().getTime() - System.currentTimeMillis()) / 1000 / 60);
			// 冷却时间是否已过
			if (remainMinutes > 0) {
				throw new ExceptionForClientTip("monster.next.remainTime", remainMinutes);
			}
			umHelp.setNextBeatTime(DateUtil.addSeconds(DateUtil.now(), Cfg.I.getUniqueConfig(CfgMonster.class).getMonsterColdTime()));
			gameUserService.updateItem(umHelp);
		} else {
			umHelp = UserMonster.instance(guId);
			gameUserService.addItem(guId, umHelp);
		}
		RDFightsInfo fightsInfo = JSONUtil.fromJson(monster.getSoliders(), RDFightsInfo.class);
		fightsInfo.setBlood(monster.getBlood());
		if (monster.getYeGuaiEnum() == null) {
			fightsInfo.setYgType(YeGuaiEnum.YG_FRIEND.getType());
		} else {
			fightsInfo.setYgType(monster.getYeGuaiEnum().getType());
		}
		if (fightsInfo.getNickname()==null){
			if (monster.getMonsterName()==null){
				Integer baseId = fightsInfo.getCards().get(0).getBaseId();
				fightsInfo.setNickname(CardTool.getCardById(baseId).getName());
				fightsInfo.setHead(baseId);
			}else {
				fightsInfo.setNickname(monster.getMonsterName());
				fightsInfo.setHead(monster.getHead());
			}
			fightsInfo.setHeadIcon(monster.getHeadIcon());
		}
		// 初始战斗为未结算
		TimeLimitCacheUtil.removeCache(guId, RDFightResult.class);
		return fightsInfo;
	}
}
