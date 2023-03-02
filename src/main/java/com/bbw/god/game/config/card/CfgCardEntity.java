package com.bbw.god.game.config.card;

import com.bbw.common.PowerRandom;
import com.bbw.god.game.config.CfgEntityInterface;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * 
 * @author shaojun
 * @email lsj@bamboowind.cn
 * @date 2018-11-27 21:57:48
 */
@Data
public class CfgCardEntity implements CfgEntityInterface, Serializable {
	private static final long serialVersionUID = 1L;

	private Integer id; //
	private boolean canDeify=false;//是否可以封神
	private String name; //
	private Integer type; //
	private Integer star; //
	private Integer attack; //
	private Integer hp; //
	private Integer zeroSkill; //
	private Integer fiveSkill; //
	private Integer tenSkill; //
	private String comment=""; //
	private Integer group; //
	private Integer way;
	private Integer perfect=null;

	public static CfgCardEntity instance(CfgDeifyCardEntity deifyCardEntity){
		CfgCardEntity cardEntity=new CfgCardEntity();
		cardEntity.setId(deifyCardEntity.getId());
		cardEntity.setName(deifyCardEntity.getName());
		cardEntity.setType(deifyCardEntity.getType());
		cardEntity.setStar(deifyCardEntity.getStar());
		cardEntity.setAttack(deifyCardEntity.getAttack());
		cardEntity.setHp(deifyCardEntity.getHp());
		cardEntity.setZeroSkill(deifyCardEntity.getZeroSkill());
		cardEntity.setFiveSkill(deifyCardEntity.getFiveSkill());
		cardEntity.setTenSkill(deifyCardEntity.getTenSkill());
		cardEntity.setGroup(deifyCardEntity.getGroup());
		cardEntity.setWay(-1);
		return cardEntity;
	}
	public Integer getTypeStar() {
		return type + star;
	}

	public Integer getSoulId() {
		return id + 1000;
	}

	public int getPrice() {
		switch (star) {
		case 1:
			return 0;
		case 2:
			return PowerRandom.getRandomBetween(2500, 4500);
		case 3:
			return PowerRandom.getRandomBetween(35000, 58000);
		case 4:
			return PowerRandom.getRandomBetween(450000, 680000);
		case 5:
			return PowerRandom.getRandomBetween(3500000, 5500000);
		}
		return 0;
	}

	public boolean isZCCard() {
		int zcId = SkillEnum.ZC.getValue();
		return (zeroSkill != null && zeroSkill == zcId) || (fiveSkill != null && fiveSkill == zcId) || (tenSkill != null && tenSkill == zcId);
	}

	public List<Integer> getSkills(){
		List<Integer> skills = new ArrayList<>();
		skills.add(gainZeroSkill());
		skills.add(gainFiveSkill());
		skills.add(gainTenSkill());
		return skills;
	}

	public List<Integer> getSkills(int level){
		List<Integer> skills = new ArrayList<>();
		skills.add(gainZeroSkill());
		if (level >= 5) {
			skills.add(gainFiveSkill());
		}
		if (level >= 10) {
			skills.add(gainTenSkill());
		}
		return skills;
	}

	/**
	 * 获取某个位置的技能
	 *
	 * @param levelIndex
	 * @return
	 */
	public Integer getSkill(int levelIndex) {
		switch (levelIndex) {
			case 0:
				return gainZeroSkill();
			case 5:
				return gainFiveSkill();
			case 10:
				return gainTenSkill();
			default:
				return 0;
		}
	}

	public String getSkillsInfo() {
		String skills = "";
		skills += gainZeroSkill();
		skills += ",";
		skills += gainFiveSkill();
		skills += ",";
		skills += gainTenSkill();
		return skills;
	}

	/**
	 * 获取卡牌0级技能
	 *
	 * @return 如果未拥有该技能则返回0
	 */
	public Integer gainZeroSkill() {
		return getZeroSkill() == null ? 0 : getZeroSkill();
	}

	/**
	 * 获取卡牌5级技能
	 *
	 * @return 如果未拥有该技能则返回0
	 */
	public Integer gainFiveSkill() {
		return getFiveSkill() == null ? 0 : getFiveSkill();
	}

	/**
	 * 获取卡牌10级技能
	 *
	 * @return 如果未拥有该技能则返回0
	 */
	public Integer gainTenSkill() {
		return getTenSkill() == null ? 0 : getTenSkill();
	}

	@Override
	public int getSortId() {
		return this.getId();
	}
}
