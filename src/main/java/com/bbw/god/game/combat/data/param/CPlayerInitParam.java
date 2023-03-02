package com.bbw.god.game.combat.data.param;

import com.bbw.common.ListUtil;
import com.bbw.god.game.combat.data.CombatBuff;
import com.bbw.god.game.combat.data.weapon.Weapon;
import com.bbw.god.game.combat.runes.RunesEnum;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.GameUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 玩家初始化参数
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-07-04 18:42
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CPlayerInitParam {
	private String nickname;
	private Integer lv = 10;
	private Integer headImg = 10;
	private Integer headIcon = TreasureEnum.HEAD_ICON_Normal.getValue();
	private Long uid = -1L;
	private Long cardFromUid = -1L;
	/** 外部系统传递（如友怪、诛仙阵） */
	private Integer hp = 0;
	private Integer initHP;
	private Integer initMP;
	private Integer initBloodBarNum;
	private List<CCardParam> cards = new ArrayList<>();
	private List<Weapon> weapons;
	private List<CombatBuff> buffs = new ArrayList<>();
	/** 额外卡牌加成 */
	private Double extraCardRate;

	public static CPlayerInitParam initParam(GameUser gu, List<CCardParam> cards, List<CombatBuff> buffs, List<Weapon> weapons) {
		CPlayerInitParam param = new CPlayerInitParam();
		param.setNickname(gu.getRoleInfo().getNickname());
		param.setLv(gu.getLevel());
		param.setHeadImg(gu.getRoleInfo().getHead());
		param.setHeadIcon(gu.getRoleInfo().getHeadIcon());
		param.setUid(gu.getId());
		param.setCardFromUid(gu.getId());
		param.setCards(cards);
		//自动装备 卡牌装备buff
		boolean hasCardEquipmentRunes = buffs.stream().anyMatch(tmp -> RunesEnum.CARD_EQUIPMENT.getRunesId() == tmp.getRuneId());
		if (!hasCardEquipmentRunes) {
			buffs.add(new CombatBuff(RunesEnum.CARD_EQUIPMENT.getRunesId(), 0));
		}
		param.setBuffs(buffs);
		param.setWeapons(weapons);
		return param;
	}

	public static CPlayerInitParam initParam(int lv, String nickname, int headImg, int headIcon) {
		CPlayerInitParam param = new CPlayerInitParam();
		param.setNickname(nickname);
		param.setLv(lv);
		param.setHeadImg(headImg);
		param.setHeadIcon(headIcon);
		return param;
	}

	/**
	 * 添加buff
	 *
	 * @param buff
	 */
	public void addBuff(int buff) {
		buffs.add(new CombatBuff(buff, 0));
	}

	/**
	 * 添加buff
	 *
	 * @param buffs
	 */
	public void addBuffs(List<Integer> buffs) {
		for (int i = 0; i < buffs.size(); i++) {
			this.buffs.add(new CombatBuff(buffs.get(i), 0));
		}
	}

	/**
	 * 获取buff
	 *
	 * @param buff
	 * @return
	 */
	public CombatBuff gainBuff(Integer buff) {
		return buffs.stream().filter(tmp -> tmp.getRuneId() == buff).findFirst().orElse(null);
	}


	/**
	 * 是否持有某个护符
	 *
	 * @param rune
	 * @return
	 */
	public boolean hasBuff(RunesEnum rune) {
		if (ListUtil.isEmpty(buffs)) {
			return false;
		}
		return buffs.stream().anyMatch(tmp -> tmp.getRuneId() == rune.getRunesId());
	}

	public List<Weapon> getWeapons() {
		if (ListUtil.isEmpty(weapons)) {
			return new ArrayList<>();
		}
		if (hasBuff(RunesEnum.QUN_CHAO)) {
			return weapons.stream().filter(p -> p.getId() != 460).collect(Collectors.toList());
		}
		return weapons;
	}

	/**
	 * 额外卡牌攻防加成
	 *
	 * @return
	 */
	public double gainExtraCardRate() {
		return null == extraCardRate ? 0 : extraCardRate;
	}
}