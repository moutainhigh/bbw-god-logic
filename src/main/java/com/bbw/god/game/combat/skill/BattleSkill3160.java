package com.bbw.god.game.combat.skill;

import com.bbw.common.CloneUtil;
import com.bbw.common.PowerRandom;
import com.bbw.god.game.combat.BattleCardService;
import com.bbw.god.game.combat.BattleSkillService;
import com.bbw.god.game.combat.ClientAnimationService;
import com.bbw.god.game.combat.CombatInitService;
import com.bbw.god.game.combat.data.AnimationSequence;
import com.bbw.god.game.combat.data.CombatCardTools;
import com.bbw.god.game.combat.data.CombatSkillEnum;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.Effect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.param.PerformSkillParam;
import com.bbw.god.game.combat.data.skill.BattleSkill;
import com.bbw.god.game.combat.runes.CombatRunesPerformService;
import com.bbw.god.game.config.TypeEnum;
import com.bbw.god.game.config.card.*;
import com.bbw.god.game.wanxianzhen.WanXianSpecialType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;


/**
 *
 * 分身3160：每回合，复制施法者自身到随机阵位，分身成功时施法者进化。
 * （1）该技能为蚊道人专属。
 * （2）复制的卡牌等级阶数当前状态与原卡牌一致。
 * （3）复制的卡牌会优先生成在下一个空位。
 * （4）复制的卡牌为实体卡牌，允许进入坟场和返回手牌/卡组。
 * （5）进化会使原卡牌按照：蚊道人（一星）→飞蚊（二星）→巨蚊（三星）→龙蚊（四星）的顺序替换原卡牌。
 *
 * @author liuwenbin
 */
@Service
public class BattleSkill3160 extends BattleSkillService {
	private static final int SKILL_ID = 3160;
	@Autowired
	private CombatRunesPerformService runesPerformService;
	/**
	 * 进化链：蚊道人=》飞蚊=》巨蚊=》龙蚊
	 */
	private static final List<Integer> EVOLUTION_LIST= Arrays.asList(CardEnum.WEN_DAO_REN.getCardId(),CardEnum.FEI_WEN.getCardId(),CardEnum.JU_WEN.getCardId(),CardEnum.LONG_WEN.getCardId());
	@Autowired
	private BattleCardService battleCardService;
	@Override
	public int getMySkillId() {
		return SKILL_ID;
	}

	@Override
	protected Action attack(PerformSkillParam psp) {
		Action ar = new Action();
		int[] emptyBattlePos = psp.getPerformPlayer().getEmptyBattlePos(true);
		if (emptyBattlePos.length==0){
			return ar;
		}
		int targetPos= emptyBattlePos[PowerRandom.getRandomBySeed(emptyBattlePos.length)-1];
		int performCardPos = psp.getPerformCard().getPos();
		for (int empty : emptyBattlePos) {
			if (empty>performCardPos){
				//优先当前位置的 下一个空位置
				targetPos=empty;
				break;
			}
		}
		//拷贝卡牌
		BattleCard clone = CloneUtil.clone(psp.getPerformCard());
		clone.setPos(targetPos);
		battleCardService.replaceCard(psp.getPerformPlayer(),clone);
		//自身卡牌升级新卡牌
		BattleCard newCard=getEvolutionCard(psp);
		//加一个释放动画
		ar.addClientAction(ClientAnimationService.getSkillAction(psp.getNextAnimationSeq(),getMySkillId(),performCardPos));
		BattleCard[] cards={clone,newCard};
		for (BattleCard card : cards) {
			if (card==null){
				continue;
			}
			//补充一个添加卡牌动画
			AnimationSequence targetPosAs = new AnimationSequence(psp.getNextAnimationSeq(), Effect.EffectResultType.CARD_ADD);
			AnimationSequence.Animation targetPosAction = new AnimationSequence.Animation();
			targetPosAction.setPos1(performCardPos);
			targetPosAction.setPos2(card.getPos());
			targetPosAction.setSkill(getMySkillId());
			targetPosAction.setCards(CombatCardTools.getCardStr(card,"",card.getPos()));
			targetPosAs.add(targetPosAction);
			ar.addClientAction(targetPosAs);
			if (card.getImgId()==CardEnum.WEN_DAO_REN.getCardId()){
				psp.getPerformPlayer().getStatistics().addYiDaoRen();
			}else if (card.getImgId()==CardEnum.LONG_WEN.getCardId()){
				Optional<BattleCard> first = psp.getMyPlayingCards(true).stream().filter(p -> p != null && p.getImgId() == CardEnum.LONG_WEN.getCardId()).findFirst();
				if (first.isPresent()){
					psp.getPerformPlayer().getStatistics().setSimultaneously2LongWen(true);
				}
			}
		}
		return ar;
	}

	/**
	 * 获取进化的卡牌
	 * 进化会使原卡牌按照：蚊道人（一星）→飞蚊（二星）→巨蚊（三星）→龙蚊（四星）的顺序替换原卡牌。
	 * @return
	 */
	private BattleCard getEvolutionCard(PerformSkillParam psp){
		BattleCard performCard=psp.getPerformCard();
		int index = EVOLUTION_LIST.indexOf(performCard.getImgId());
		if (index<0 || index>=EVOLUTION_LIST.size()){
			//超出进化范围
			return null;
		}
		CfgCardEntity cfgCard= CardTool.getHideCard(EVOLUTION_LIST.get(index+1));
		BattleCard hero=new BattleCard();
		hero.setId(100001);
		if (cfgCard.getPerfect()!=null){
			hero.setIsUseSkillScroll(cfgCard.getPerfect());
		}
		hero.setImgId(cfgCard.getId());
		hero.setStars(cfgCard.getStar());
		hero.setName(cfgCard.getName());
		hero.setType(TypeEnum.fromValue(cfgCard.getType()));
		hero.setHv(performCard.getHv());
		hero.setLv(performCard.getLv());
		if (null != cfgCard.getGroup()) {
			hero.setGroupId(cfgCard.getGroup());
		}
		int initAtk = CombatInitService.getAtk(cfgCard.getAttack(), hero.getLv(), hero.getHv());
		int initHp = CombatInitService.getHp(cfgCard.getHp(), hero.getLv(), hero.getHv());
		hero.setInitAtk(initAtk);
		hero.setInitHp(initHp);
		hero.setRoundAtk(initAtk);
		hero.setRoundHp(initHp);
		hero.setAtk(initAtk);
		hero.setHp(initHp);
		Integer[] skillIds = { cfgCard.getZeroSkill(), cfgCard.getFiveSkill(),cfgCard.getTenSkill() };
		for (int i = 0; i < skillIds.length; i++) {
			if (null == skillIds[i] || 0 == skillIds[i]) {
				continue;
			}
			if (hero.getLv() < i * 5) {
				continue;
			}
			Optional<CfgCardSkill> csOp = CardSkillTool.getCardSkillOpById(skillIds[i]);
			if (!csOp.isPresent()){
				continue;
			}
			BattleSkill skill=BattleSkill.instanceBornSkill(csOp.get());
			hero.addSkill(skill);
		}
		//如果有符 需要为其添加技能，如果是万仙阵需要禁用对应的技能
		runesPerformService.runInitCardRunes(psp.getPerformPlayer(),hero);
		if (psp.getCombat().getWxType()!=null && psp.getCombat().getWxType()== WanXianSpecialType.BEI_SHUI.getVal()){
			hero.getSkills().removeIf(p->p.getId()== CombatSkillEnum.FH.getValue() || p.getId()==CombatSkillEnum.HH.getValue() || p.getId()==CombatSkillEnum.FS.getValue());
		}
		hero.setPos(performCard.getPos());
		battleCardService.replaceCard(psp.getPerformPlayer(),hero);
		return hero;
	}
}
