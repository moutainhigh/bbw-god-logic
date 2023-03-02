package com.bbw.god.game.combat.pve;

import com.bbw.common.PowerRandom;
import com.bbw.common.Rst;
import com.bbw.god.fight.FightTypeEnum;
import com.bbw.god.game.combat.RoundService;
import com.bbw.god.game.combat.data.Combat;
import com.bbw.god.game.combat.data.param.CCardParam;
import com.bbw.god.game.combat.data.param.CPlayerInitParam;
import com.bbw.god.game.combat.data.param.CombatPVEParam;
import com.bbw.god.game.combat.weapon.CfgWeapon;
import com.bbw.god.game.combat.weapon.WeaponTool;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.card.CardTool;
import com.bbw.god.game.config.card.CfgCardEntity;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.rd.RDCommon;
import com.bbw.god.rd.RDSuccess;
import com.bbw.god.server.ServerUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 *
 * 战斗相关测试
 * @author lwb
 */
@RestController
@RequestMapping("/gm/combat/")
public class GMCombatCtrl {
    @Qualifier("combatPVEInitService")
    @Autowired
    private CombatPVEInitService pveInitService;

    @Autowired
    private RoundService roundService;
    @Autowired
    private ServerUserService serverUserService;

    @RequestMapping("fightTest")
    public RDSuccess fightTest(Integer num){
        if (num==null||num<=0){
            num=10;
        }
        System.err.println("开始战斗随机卡组测试！本次战斗场次："+num);
        long begin=System.currentTimeMillis();
        List<CfgCardEntity> allCards = CardTool.getAllCards();
        CombatPVEParam pveParam=new CombatPVEParam();
        pveParam.setFightType(FightTypeEnum.TRAINING.getValue());
        for (int j=1;j<=num;j++){
            long timeMillis=System.currentTimeMillis();
            List<CfgCardEntity> cards1 = PowerRandom.getRandomsFromList(20, allCards);
            List<CfgCardEntity> cards2 = PowerRandom.getRandomsFromList(20, allCards);
            Combat combat = pveInitService.initCombatTestPVE(initParam(cards1), initParam(cards2), pveParam);
            for (int i = 0; i < 31; i++) {
                combat.getAnimationList().clear();
                //布阵
                roundService.deployPVE(combat, 1, "");
                //战斗开始
                roundService.run(combat);
                //战斗结束
                roundService.after(combat);
                if (combat.hadEnded()) {
                    break;
                }
            }
            System.err.println("战斗回合数："+combat.getRound()+",总共耗时："+(System.currentTimeMillis()-timeMillis));
        }
        System.err.println("本次战斗场次："+num+",总共耗时："+(System.currentTimeMillis()-begin));
        return new RDSuccess();
    }

    @RequestMapping("addTreasure")
    public Rst addTreasure(Integer sid,String nickname,Integer num){
        if (sid==null){
            sid=96;
        }
        if (num==null){
            num=20;
        }
        Optional<Long> uidOptional = this.serverUserService.getUidByNickName(sid, nickname);
        if (!uidOptional.isPresent()) {
            return Rst.businessFAIL("不存在该角色");
        }
        long uid=uidOptional.get();
        List<CfgWeapon> allCfgWeapons = WeaponTool.getAllCfgWeapons();
        for (CfgWeapon weapon:allCfgWeapons){
            TreasureEventPublisher.pubTAddEvent(uid,weapon.getId(),num,WayEnum.NONE,new RDCommon());
        }
        return Rst.businessOK();
    }

    private CPlayerInitParam initParam(List<CfgCardEntity> cards){
        CPlayerInitParam param=new CPlayerInitParam();
        param.setNickname("测试");
        param.setLv(120);
        param.setHeadImg(101);
        param.setUid(-1L);
        param.setCardFromUid(-1L);
        List<CCardParam> cardParams=new ArrayList<>();
        for (CfgCardEntity cardEntity:cards){
            CCardParam p=new CCardParam();
            p.setAtk(cardEntity.getAttack());
            p.setHv(PowerRandom.getRandomBySeed(10));
            p.setLv(PowerRandom.getRandomBetween(10,20));
            p.setId(cardEntity.getId());
            p.setGroupSkill(cardEntity.getGroup());
            p.setType(cardEntity.getType());
            p.setStar(cardEntity.getStar());
            p.setHp(cardEntity.getHp());
            List<Integer> skills=new ArrayList<>();
            if (cardEntity.getZeroSkill()!=0){
                skills.add(cardEntity.getZeroSkill());
            }
            if (cardEntity.getFiveSkill()!=0){
                skills.add(cardEntity.getFiveSkill());
            }
            if (cardEntity.getTenSkill()!=0){
                skills.add(cardEntity.getTenSkill());
            }
            p.setSkills(skills);
            cardParams.add(p);
        }
        param.setCards(cardParams);
        param.setWeapons(new ArrayList<>());
        return param;
    }
}
