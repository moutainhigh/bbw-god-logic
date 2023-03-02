package com.bbw.god.game.combat;

import com.bbw.exception.CoderException;
import com.bbw.god.game.combat.data.Player;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.param.CCardParam;
import com.bbw.god.game.combat.data.skill.BattleSkill;
import com.bbw.god.game.config.TypeEnum;
import com.bbw.god.game.config.card.CardEnum;
import com.bbw.god.game.config.card.CardSkillTool;
import com.bbw.god.game.config.card.CfgCardSkill;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author 作者 ：lwb
 * @version 创建时间：2019年10月15日 下午3:57:25
 * 类说明
 */
public abstract class CombatInitService {
	@Autowired
	protected CombatRedisService redisService;

	/**
	 * 牌堆洗牌
	 *
	 * @param cards
	 */
	protected static void shuffleDrawCards(List<BattleCard> cards) {
		// TODO：可以考虑星数少的排前面
		Collections.shuffle(cards);
	}

	/**
	 * 角色血量计算
	 *
	 * @param lv：玩家等级
	 * @return
	 */
	public static int getPlayerInitHp(int lv)  {
		return 1000 + (lv - 1) * 380;
	}

	/**
	 * 角色初始魔法值计算
	 *
	 * @param lv
	 * @return
	 */
	protected static int getPlayerInitMp(int lv) {
		if (lv <= 60) {
			return 3 + lv / 12;
		}
		return 8;
	}

	public static BattleCard initBattleCard(CCardParam data, int id) {
		BattleCard hero = new BattleCard();
		hero.setImgId(data.getId());
		hero.setHv(data.getHv());
		hero.setLv(data.getLv());
		hero.setIsUseSkillScroll(data.getIsUseSkillScroll());
		hero.setGroupId(data.getGroupSkill());
		hero.setAlive(data.isAlive());
		int initAtk=data.getAtk();
		int initHp=data.getHp();
		if (data.getId()!= CardEnum.LEADER_CARD.getCardId()){
			initAtk = getAtk(data.getAtk(), hero.getLv(), hero.getHv());
			initHp = getHp(data.getHp(), hero.getLv(), hero.getHv());
		}
		// 在牌堆里，默认需要法力值8
		hero.setInitAtk(initAtk);
		hero.setInitHp(initHp);
		hero.setRoundAtk(initAtk);
		hero.setRoundHp(initHp);
		hero.setAtk(initAtk);
		hero.setHp(initHp);
		hero.setType(TypeEnum.fromValue(data.getType()));
		hero.setStars(data.getStar());
		if (data.getSex() != null) {
			hero.setSex(data.getSex());
		}
		hero.setFashion(data.getFashion());
		// 物理攻击技能
		int size = data.getSkills().size();
		for (int i = 0; i < size; i++) {
			if (i < 3 && i * 5 > hero.getLv()) {
				continue;
			}
			int skillId = data.getSkills().get(i);
			battleCardAddSKill(hero, skillId);
		}
		return hero;
	}

	public static void battleCardAddSKill(BattleCard hero, int skillId) {
		if (skillId == 0 || hero.existSkill(skillId)) {
			return;
		}
		Optional<CfgCardSkill> cardSkillOp = CardSkillTool.getCardSkillOpById(skillId);
		if (!cardSkillOp.isPresent()) {
			return;
		}
		CfgCardSkill cfgCardSkill = cardSkillOp.get();
		hero.addSkill(BattleSkill.instanceBornSkill(cfgCardSkill));
		for (int ownSkill : cfgCardSkill.getOwnSkills()) {
			if (hero.existSkill(ownSkill)) {
				continue;
			}
			Optional<CfgCardSkill> ownSkillOp = CardSkillTool.getCardSkillOpById(ownSkill);
			if (ownSkillOp.isPresent()){
				BattleSkill bornSkill = BattleSkill.instanceBornSkill(ownSkillOp.get());
				bornSkill.setParent(cfgCardSkill.getId());
				hero.addSkill(bornSkill);
			}
		}
	}

	/**
	 * 当前攻击力
	 *
	 * @param basis：基础值。0级的攻击力
	 * @param lv：卡牌级别
	 * @param hv：卡牌阶级
	 * @return
	 */
	public static int getAtk(int basis, int lv, int hv) {
		if (lv < 0 || hv < 0) {
			throw CoderException.high("等级或阶级参数错误。lv=" + lv + " hv=" + hv);
		}
		// 物理攻击=0级攻击+0级攻击*(0.1+阶级*0.025)*等级, 向下取整
		float att = basis + basis * (0.1f + hv * 0.025f) * lv;
		return (int) att;
	}

	/**
	 * 当前Hp
	 *
	 * @param basis：基础值。0级的hp
	 * @param lv：卡牌级别
	 * @param hv：卡牌阶级
	 * @return
	 */
	public static int getHp(int basis, int lv, int hv) {
		if (lv < 0 || hv < 0) {
			throw CoderException.high("等级或阶级参数错误。lv=" + lv + " hv=" + hv);
		}
		// 血量=0级血量+0级血量*(0.1+阶级*0.025)*等级, 向下取整
		float hp = basis + basis * (0.1f + hv * 0.025f) * lv;
		return (int) hp;
	}

	protected int getInt(Double b) {
		return b.intValue();
	}

	/**
	 *从召唤师血量与出战卡组防御值总和中选取最高值作为本场战斗的血量。
	 * @param player
	 */
	public void setHpByMaxOfPlayerHpOrCardsSumHp(Player player){
		Integer collect = player.getDrawCards().stream().collect(Collectors.summingInt(BattleCard::getInitHp));
		player.updateHighHp(collect);
	}
}
