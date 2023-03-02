package com.bbw.god.game.combat.data.param;

import com.bbw.god.city.mixd.nightmare.CfgNightmareMiXian;
import com.bbw.god.game.combat.pvp.PvPCombatParam.PvpCard;
import com.bbw.god.game.config.card.CardTool;
import com.bbw.god.game.config.card.CfgCardEntity;
import com.bbw.god.gameuser.biyoupalace.cfg.BYPalaceTool;
import com.bbw.god.gameuser.biyoupalace.cfg.CfgBYPalaceSymbolEntity;
import com.bbw.god.gameuser.card.CardSkillPosEnum;
import com.bbw.god.gameuser.card.UserCard;
import com.bbw.god.gameuser.card.UserCard.UserCardStrengthenInfo;
import com.bbw.god.gameuser.leadercard.UserLeaderCard;
import com.bbw.god.gameuser.yaozu.CfgYaoZuEntity;
import com.bbw.god.gameuser.yaozu.YaoZuTool;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 玩家卡牌初始化参数
 */
@Data
public class CCardParam {
    private int id;// 图像
    private int lv;
    private int hv;
    private int star = 0;//星级 用于判定Mp
    private int type;
    private Integer fashion;
    private int groupSkill = 0;
    private int atk = 0;
    private int hp = 0;
    /** 卡牌是否活的 */
    private boolean alive = true;
    private Integer sex;
    private List<Integer> skills = new ArrayList<>();
    ;
    private Integer isUseSkillScroll = 0;
    private UserCard.UserCardStrengthenInfo strengthenInfo = null;

    public static CCardParam init(UserCard userCard) {
        return init(userCard.getBaseId(), userCard.getLevel(), userCard.getHierarchy(), userCard.getStrengthenInfo());
    }

    public static CCardParam init(int cardId, int cardLv, int cardHv, UserCard.UserCardStrengthenInfo strengthenInfo) {
        CCardParam param = new CCardParam();
        param.setId(cardId);
        param.setLv(cardLv);
        param.setHv(cardHv);
        param.initCardSkills(cardId,strengthenInfo);
        param.setStrengthenInfo(strengthenInfo);
        return param;
    }
    public static CCardParam init(int cardId, int cardLv, int cardHv) {
        return init(cardId, cardLv, cardHv,null);
    }
	public static CCardParam initPVPCard(PvpCard pvpCard) {
        UserCardStrengthenInfo info = new UserCardStrengthenInfo();
        info.setAttackSymbol(pvpCard.getAttackSymbol());
        info.setDefenceSymbol(pvpCard.getDefenceSymbol() );
        info.updateCurrentSkill(CardSkillPosEnum.SKILL_0,pvpCard.getSkill0());
        info.updateCurrentSkill(CardSkillPosEnum.SKILL_5,pvpCard.getSkill5());
        info.updateCurrentSkill(CardSkillPosEnum.SKILL_10,pvpCard.getSkill10());
        CCardParam param=init(pvpCard.getBaseId(),pvpCard.getLv(),pvpCard.getHv(),info);
        param.setIsUseSkillScroll(pvpCard.getIsUseSkillScroll());
		return param;
	}

	public static CCardParam getInstance(UserLeaderCard leaderCard){
        CCardParam param = new CCardParam();
        param.setId(leaderCard.getBaseId());
        param.setLv(leaderCard.getLv());
        param.setHv(leaderCard.getHv());
        param.setStar(leaderCard.getStar());
        param.setAtk(leaderCard.settleTotalAtkWithEquip());
        param.setHp(leaderCard.settleTotalHpWithEquip());
        param.setType(leaderCard.getProperty());
        param.setSex(leaderCard.getSex());
        param.setFashion(leaderCard.getFashion());
        for (int skill : leaderCard.currentSkills()) {
            param.addSkillId(skill);
        }
        return param;
    }

    public static CCardParam initMxdCards(CfgNightmareMiXian.CardParam cardParam,int lv,int hv) {
        UserCardStrengthenInfo info = new UserCardStrengthenInfo();
        info.setAttackSymbol(0);
        info.setDefenceSymbol(0);
        info.updateCurrentSkill(CardSkillPosEnum.SKILL_0,cardParam.getSkill0());
        info.updateCurrentSkill(CardSkillPosEnum.SKILL_5,cardParam.getSkill5());
        info.updateCurrentSkill(CardSkillPosEnum.SKILL_10,cardParam.getSkill10());
        CCardParam param=init(cardParam.getId(),lv,hv,info);
        param.setIsUseSkillScroll(cardParam.getUseScroll());
        return param;
    }

    /**
     * 根据妖族卡组配置文件，获取卡牌数据
     * @param cardParam
     * @param yaoZuId
     * @return
     */
    public static CCardParam initYzCards(CfgYaoZuEntity.CardParam cardParam, int yaoZuId) {
        CfgYaoZuEntity cfgYaoZu = YaoZuTool.getYaoZu(yaoZuId);
        int lv = cfgYaoZu.getCardLv();
        int hv = cfgYaoZu.getCardHv();
        UserCardStrengthenInfo info = new UserCardStrengthenInfo();
        info.setAttackSymbol(0);
        info.setDefenceSymbol(0);
        info.updateCurrentSkill(CardSkillPosEnum.SKILL_0,cardParam.getSkill0());
        info.updateCurrentSkill(CardSkillPosEnum.SKILL_5,cardParam.getSkill5());
        info.updateCurrentSkill(CardSkillPosEnum.SKILL_10,cardParam.getSkill10());
        CCardParam param=init(cardParam.getId(), lv, hv, info);
        return param;
    }

	public void initCardSkills(int cardId, UserCard.UserCardStrengthenInfo strengthenInfo){
        CfgCardEntity cfgCard= CardTool.getCardById(cardId);
        this.atk=cfgCard.getAttack();
        this.hp=cfgCard.getHp();
        this.groupSkill=cfgCard.getGroup();
        this.star = CardTool.getCardStarForFight(cardId, cfgCard.getStar());
        Integer skill0=cfgCard.getZeroSkill();
        Integer skill5=cfgCard.getFiveSkill();
        Integer skill10=cfgCard.getTenSkill();
        this.type=cfgCard.getType();
        if (strengthenInfo !=null){
            skill0 = strengthenInfo.gainSkill0() > 0 ? strengthenInfo.gainSkill0() : skill0;
            skill5 = strengthenInfo.gainSkill5() > 0 ? strengthenInfo.gainSkill5() : skill5;
            skill10 = strengthenInfo.gainSkill10() > 0 ? strengthenInfo.gainSkill10() : skill10;
            if (strengthenInfo.gainAttackSymbol()>0){
                CfgBYPalaceSymbolEntity entity = BYPalaceTool.getSymbolEntity(strengthenInfo.gainAttackSymbol());
                this.atk+=entity.getEffect();
            }
            if (strengthenInfo.gainDefenceSymbol()>0){
                CfgBYPalaceSymbolEntity entity = BYPalaceTool.getSymbolEntity(strengthenInfo.gainDefenceSymbol());
                this.hp+=entity.getEffect();
            }
        }
        if (!cfgCard.getZeroSkill().equals(skill0) || !cfgCard.getFiveSkill().equals(skill5)  || !cfgCard.getTenSkill().equals(skill10)){
            this.isUseSkillScroll=1;
        }
        addSkillId(skill0);
        addSkillId(skill5);
        addSkillId(skill10);
    }

    public void addSkillId(Integer skillId){
        if (this.skills.contains(skillId)){
            this.skills.add(0);
        }else {
            this.skills.add(skillId);
        }
    }

    public boolean ifSpecial(){
        if (isUseSkillScroll>0){
            return true;
        }
        if (strengthenInfo!=null && strengthenInfo.gainAttackSymbol()!=null && strengthenInfo.gainAttackSymbol()>0){
            return true;
        }
        if (strengthenInfo!=null && strengthenInfo.gainDefenceSymbol()!=null && strengthenInfo.gainDefenceSymbol()>0){
            return true;
        }
        return false;
    }

    public String buildSkillAndSymbolStr(){
        String skillStr="";
        for (int i = 0; i < 3; i++) {
            if (i<=skills.size()){
                skillStr+=skills.get(i)+",";
            }else {
                skillStr+="0,";
            }
        }
        if (strengthenInfo!=null && strengthenInfo.gainAttackSymbol()!=null && strengthenInfo.gainAttackSymbol()>0){
            skillStr+=strengthenInfo.gainAttackSymbol()+",";
        }else {
            skillStr+="0,";
        }
        if (strengthenInfo!=null && strengthenInfo.gainDefenceSymbol()!=null && strengthenInfo.gainDefenceSymbol()>0){
            skillStr+=strengthenInfo.gainDefenceSymbol()+";";
        }else {
            skillStr+="0;";
        }
        return skillStr;
    }
}
