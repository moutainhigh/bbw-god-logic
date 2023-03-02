package com.bbw.god.game.combat.pve;

import com.bbw.App;
import com.bbw.god.cache.TimeLimitCacheUtil;
import com.bbw.god.city.UserCityService;
import com.bbw.god.city.miaoy.hexagram.HexagramBuffEnum;
import com.bbw.god.city.miaoy.hexagram.HexagramBuffService;
import com.bbw.god.city.miaoy.hexagram.event.HexagramEventPublisher;
import com.bbw.god.city.yeg.YeGuaiEnum;
import com.bbw.god.event.BaseEventParam;
import com.bbw.god.fight.FightSubmitParam;
import com.bbw.god.fight.FightTypeEnum;
import com.bbw.god.fight.RDFightResult;
import com.bbw.god.fight.processor.FightProcessorFactory;
import com.bbw.god.game.combat.CombatRedisService;
import com.bbw.god.game.combat.FightAchievementCache;
import com.bbw.god.game.combat.attackstrategy.service.AbstractStrategyLogic;
import com.bbw.god.game.combat.attackstrategy.service.StrategyLogicFactory;
import com.bbw.god.game.combat.cache.CombatAchievementService;
import com.bbw.god.game.combat.data.Combat;
import com.bbw.god.game.combat.data.CombatInfo;
import com.bbw.god.game.combat.data.Player;
import com.bbw.god.game.combat.data.skill.SkillSection;
import com.bbw.god.game.combat.data.weapon.Weapon;
import com.bbw.god.game.combat.event.CombatEventPublisher;
import com.bbw.god.game.combat.event.EPCombatAchievement;
import com.bbw.god.game.combat.exaward.ExAwardCheckFactory;
import com.bbw.god.game.combat.video.service.CombatVideoService;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.treasure.CfgTreasureEntity;
import com.bbw.god.game.config.treasure.TreasureTool;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.rd.RDCommon;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class PVEResultService {
    @Autowired
    private ExAwardCheckFactory exAwardCheckFactory;//
    @Autowired
    private FightProcessorFactory fightProcessorFactory;
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private CombatVideoService videoService;
    @Autowired
    private UserCityService userCityService;
    @Autowired
    private CombatRedisService redisService;
    @Autowired
    private StrategyLogicFactory strategyLogicFactory;
    @Autowired
    private HexagramBuffService hexagramBuffService;
    @Autowired
    private CombatAchievementService combatAchievementService;
    @Autowired
    private PVEResultLogService pveResultLogService;
    @Autowired
    private App app;

    /**
     * 结算战斗
     *
     * @param combat
     * @param uid
     * @return
     */
    public RDFightResult takePVEResult(Combat combat, long uid) {
        //是否已经结算
        if (combat.hasSubmitFightResult()) {
            return combat.getResult();
        }
        CombatInfo combatInfo = redisService.getCombatInfo(combat.getId());
        FightSubmitParam param = getFightSubmitParam(combat, combatInfo);
        RDFightResult result = fightProcessorFactory.makeFightProcessor(combat.getFightType()).submitFightResult(uid, param);
        if (result.getWinDes() == null && !combat.getAwardDesc().equals("")) {
            result.setWinDes(combat.getAwardDesc());
        }
        result.setReturnTreasures(settleTreasure(param.getWin() == 1, combat.getP1()));
        combat.setResult(result);
        pveResultLogService.logPVEResult(combat);
        checkCombatAchievement(combat);
        AbstractStrategyLogic strategyLogic = strategyLogicFactory.getLogic(combat.getFightType());
        if (null != strategyLogic) {
            strategyLogic.logCombatToStrategy(combat);
        }
        checkResultEvent(combat);
        return result;
    }

    public FightSubmitParam getFightSubmitParam(Combat combat,CombatInfo combatInfo) {
        FightSubmitParam fsp = new FightSubmitParam();
        Player user=combat.getP1();
        if(combatInfo!=null){
            fsp.setOpponentId(combatInfo.getOpponent());
            fsp.setFinishedTask(exAwardCheckFactory.hasGainExAward(combat, combatInfo.getAwardId()));
            fsp.setOpponentId(combatInfo.getOpponent());
            fsp.setOpponentName(combatInfo.getOpponentName());
            fsp.setMonsterId(combatInfo.getMonsterId());
            fsp.setMonsterId(combatInfo.getMonsterId());
            fsp.setFightTaskId(combatInfo.getFightTaskId());
            fsp.setFightAgain(combatInfo.isFightAgain());
            if (combatInfo.getYeguaiType() == null) {
                if (combat.getFightType().equals(FightTypeEnum.YG)) {
                    fsp.setYeGuaiType(YeGuaiEnum.YG_NORMAL);
                } else if (combat.getFightType().equals(FightTypeEnum.HELP_YG)) {
                    fsp.setYeGuaiType(YeGuaiEnum.YG_FRIEND);
                }
            } else {
                fsp.setYeGuaiType(YeGuaiEnum.fromValue(combatInfo.getYeguaiType()));
            }
        }
        fsp.setWin(combat.getWinnerId() == 1 ? 1 : 0);
        fsp.setBeginBlood(user.getBeginHp());
        fsp.setLostBlood(user.getBeginHp() - user.getHp());
        fsp.setNewerGuide(combat.getNewerGuide());

        fsp.setWeaponUsedNum(user.getWeaponsInUse().size());
        fsp.setKilledCards(user.gainKilledCard());
        Player aiPlayer = combat.getP2();
        fsp.setOppBeginBlood(aiPlayer.getBeginHp());
        fsp.setOppLv(aiPlayer.getLv());
        fsp.setOppLostBlood(aiPlayer.getBeginHp() - aiPlayer.getHp());
        // 胜负;对手召唤师等级,打掉的召唤师血量;卡牌1!星级1!等级1!阶数1,卡牌!2星级2!等级2!阶数2; ; 招财生效次数;
        // 是否完成额外目标
        fsp.setOppKilledCards(aiPlayer.gainKilledCard());
        fsp.setZcTimes(combat.getP1().getStatistics().getZhaoCaiEffectTimes());
        fsp.setAdditionExp(combat.getP1().getStatistics().getCaiShenAddRate());
        if (hexagramBuffService.isHexagramBuff(user.getUid(), HexagramBuffEnum.HEXAGRAM_47.getId())) {
            fsp.setZcTimes(0);
            HexagramEventPublisher.pubHexagramBuffDeductEvent(new BaseEventParam(user.getUid()), HexagramBuffEnum.HEXAGRAM_47.getId(), 1);
        }
        fsp.setFightType(combat.getFightType().getValue());

        fsp.setRound(combat.getRound());
        fsp.setCombatId(combat.getId());
        fsp.setGainJYD(user.getStatistics().isGainJYD());
        return fsp;
    }

    public RDCommon settleTreasure(boolean win,Player player){
        RDCommon rd=new RDCommon();
        List<RDCommon.RDTreasureInfo> treasures = new ArrayList<>();
        try{
            if (!player.isOwnTGCF()){
                return rd;
            }
            List<Weapon> weaponsInUse = player.getWeaponsInUse();
            long uid=player.getUid();
            for (Weapon weapon : weaponsInUse) {
                if (win){
                    //扣除
                    TreasureEventPublisher.pubTDeductEvent(uid,weapon.getId(),weapon.getNum(), WayEnum.FIGHT_ATTACK_EXPEND,new RDCommon());
                }else {
                    //输了则 构造一个获得
                    CfgTreasureEntity cfgTreasure = TreasureTool.getTreasureById(weapon.getId());
                    treasures.add(new RDCommon.RDTreasureInfo(weapon.getId(),weapon.getNum(),cfgTreasure.getType()));
                }
            }
        }catch (Exception e){
            log.error("天官赐福结算异常，"+e.getMessage(),e);
        }
        rd.setTreasures(treasures);
        return rd;
    }
    /**
     * 检查是否完成成就
     *
     * @param combat
     */
    public void checkCombatAchievement(Combat combat) {
        checkCombatAchievement(combat.getFirstPlayer(), combat);
        checkCombatAchievement(combat.getSecondPlayer(), combat);
        combatAchievementService.checkAchievement(combat,redisService.getCombatInfo(combat.getId()));
    }

    private void checkCombatAchievement(Player player, Combat combat) {
        Long uid = player.getUid();
        if (uid == null || uid < 0) {
            return;
        }
        List<Integer> list = new ArrayList<Integer>();
        if (player.getStatistics().isSimultaneously2LongWen()){
            //我方场上同时存在2只龙蚊
            list.add(15440);
        }
        if (player.getStatistics().getYiDaoRen()>=10){
            //单场战斗中分身出10个蚊道人
            list.add(15450);
        }
        boolean win = combat.getWinnerId() == player.getId().getValue();
        FightAchievementCache cache = TimeLimitCacheUtil.getFightAchievementCache(uid);
        if (null != cache) {
            if (cache.getDingSDEffect().size() >= 3) {
                // 使用定神丹后，抵挡3种及以上技能
                list.add(14710);
            }
            if (cache.getEffectFenS() >= 10) {
                // 一场战斗中使用封神将10张卡牌拉回战场
                list.add(14680);
            }
            if (cache.getEffectZanXD() >= 10) {
                // 一场战斗中使攒心钉生效10次
                list.add(14660);
            }
            if (cache.getEffectZhongDuDie() >= 10) {
                // 一场战斗中使10张卡牌中毒死亡
                list.add(14690);
            }
            if (cache.isEffectDiscardToGuiB()) {
                // 一次性使对方坟场10张卡牌变为鬼兵
                list.add(14670);
            }
            if (2400 == cache.getLoseHpByQianKG()) {
                // 一场战斗中使用乾坤弓造成2400点伤害
                list.add(14620);
            }
            if (win && combat.getFightType().getValue() == FightTypeEnum.SXDH.getValue() && !cache.isHas4or5StarCard()) {
                // 不出张4星及以上卡牌获得神仙大会胜利
                list.add(14720);
            }
        }
        // 一场战斗中使用所有剑类法宝
        SkillSection section = SkillSection.getAllJianWeapons();
        long nums = player.getWeaponsInUse().stream().filter(p -> section.contains(p.getId())).count();
        if (nums == section.getSkills().length) {
            list.add(14590);
        }
        if (player.getWeaponsInUse().size() >= 10) {
            // 一场战斗中使用10种以上战斗法宝
            list.add(14600);
        }
        if (player.getStatistics().getZhaoCaiEffectTimes() >= 20 && win) {
            // 一场战斗中使用20次招财技能并获胜
            list.add(14700);
        }

        if (list.isEmpty()) {
            TimeLimitCacheUtil.setFightAchievementCache(uid, null);
            return;
        }
        EPCombatAchievement ep=EPCombatAchievement.instance(new BaseEventParam(uid),list);
        CombatEventPublisher.pubCombatAchievement(ep);
        TimeLimitCacheUtil.setFightAchievementCache(uid, null);
    }

    public void checkResultEvent(Combat combat){
        try{
            Player p1 = combat.getP1();
            Player p2 = combat.getP2();
            int p1KillNum=p2.getDegenerator().size()+p2.getDiscard().size();
            int p2KillNum=p1.getDegenerator().size()+p1.getDiscard().size();
            int p1UseTreasureNum=0;
            for (Weapon weapon : p1.getWeaponsInUse()) {
                p1UseTreasureNum+=weapon.getNum();
            }
            int p2UseTreasureNum=0;
            for (Weapon weapon : p2.getWeaponsInUse()) {
                p2UseTreasureNum+=weapon.getNum();
            }
            if (p1.getUid()>0){
                CombatEventPublisher.pubResultDataEvent(p1.getUid(), combat.getFightType().getValue(),p1KillNum,p1UseTreasureNum);
            }
            if (p2.getUid()>0){
                CombatEventPublisher.pubResultDataEvent(p2.getUid(), combat.getFightType().getValue(),p2KillNum,p2UseTreasureNum);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
