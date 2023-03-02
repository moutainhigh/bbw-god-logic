package com.bbw.god.game.zxz.fight;

import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.cache.TimeLimitCacheUtil;
import com.bbw.god.fight.FightSubmitParam;
import com.bbw.god.fight.FightTypeEnum;
import com.bbw.god.fight.RDFightResult;
import com.bbw.god.fight.processor.AbstractFightProcessor;
import com.bbw.god.game.combat.CombatInitService;
import com.bbw.god.game.combat.data.CombatBuff;
import com.bbw.god.game.combat.data.param.CCardParam;
import com.bbw.god.game.combat.data.param.CPlayerInitParam;
import com.bbw.god.game.combat.data.param.CombatPVEParam;
import com.bbw.god.game.combat.runes.RunesEnum;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.zxz.cfg.foursaints.CfgFourSaintsEntity;
import com.bbw.god.game.zxz.cfg.foursaints.CfgFourSaintsTool;
import com.bbw.god.game.zxz.entity.*;
import com.bbw.god.game.zxz.entity.foursaints.UserFourSaintsDefender;
import com.bbw.god.game.zxz.entity.foursaints.UserZxzFourSaintsCardGroupInfo;
import com.bbw.god.game.zxz.entity.foursaints.UserZxzFourSaintsInfo;
import com.bbw.god.game.zxz.entity.foursaints.ZxzFourSaintsDefender;
import com.bbw.god.game.zxz.enums.ZxzStatusEnum;
import com.bbw.god.game.zxz.service.ZxzAnalysisService;
import com.bbw.god.game.zxz.service.foursaints.GameZxzFourSaintsService;
import com.bbw.god.game.zxz.service.foursaints.UserZxzFourSaintsService;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.card.CardSkillPosEnum;
import com.bbw.god.gameuser.card.UserCard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 诛仙阵-四圣挑战 战斗结算
 * @author: hzf
 * @create: 2022-12-29 08:52
 **/
@Service
public class ZxzFourSaintsFightProcessor extends AbstractFightProcessor{
    @Autowired
    private ZxzFightCacheService zxzFightCacheService;
    @Autowired
    private UserZxzFourSaintsService userZxzFourSaintsService;
    @Autowired
    private GameZxzFourSaintsService gameZxzFourSaintsService;

    @Override
    public FightTypeEnum getFightType() {
        return FightTypeEnum.ZXZ_FOUR_SAINTS;
    }

    @Override
    public WayEnum getWay() {
        return WayEnum.FIGHT_ZXZ;
    }

    @Override
    public CombatPVEParam getOpponentInfo(Long uid, Long oppId, boolean fightAgain) {

        //初始化对手数据
        Integer defenderId = oppId.intValue();

        //进入战斗前清理缓存
        TimeLimitCacheUtil.removeCache(uid, RDFightResult.class);
        //获得挑战类型
        Integer challengeType = CfgFourSaintsTool.getChallengeType(defenderId);
        //获取玩家四圣数据
        UserZxzFourSaintsInfo userZxzFourSaints = userZxzFourSaintsService.getUserZxzFourSaints(uid, challengeType);
        //缓存四圣关卡id
        zxzFightCacheService.cacheFourSaintsDefenderId(uid, defenderId);
        //缓存四圣上次刷新的时间
        zxzFightCacheService.cacheFourSaintsLastRefreshDate(uid,userZxzFourSaints.getLastRefreshDate().getTime());

        //战斗前保存玩家血量跟血量为0的卡牌
        UserZxzFourSaintsCardGroupInfo userCardGroup = userZxzFourSaintsService.getUserZxzFourSaintsCardGroup(uid, challengeType);

        if (userCardGroup.getHp() <= 0) {
            throw new ExceptionForClientTip("zxz.user.die");
        }
        userCardGroup.addLastUser(String.valueOf(uid), userCardGroup.getHp());
        if (null != userCardGroup.getZxzUserLeaderCard()) {
            if (!userCardGroup.getLastCardId().contains(userCardGroup.getZxzUserLeaderCard().getCardId()) && !userCardGroup.getZxzUserLeaderCard().getAlive()) {
                userCardGroup.addLastCardId(userCardGroup.getZxzUserLeaderCard().getCardId());
            }
        }
        //记录玩家卡牌为血量为0的卡牌ID
        for (UserZxzCard card : userCardGroup.getCards()) {
            if (!userCardGroup.getLastCardId().contains(card.getCardId()) && !card.getAlive()) {
                userCardGroup.addLastCardId(card.getCardId());
            }
        }

        gameUserService.updateItem(userCardGroup);

        return buildPveParam(uid, challengeType, defenderId, fightAgain);


    }
    /**
     * 构建战斗参数
     *
     * @param uid
     * @param challengeType 挑战类型
     * @param defenderId 守卫（关卡）ID
     * @param fightAgain 是否是重复战斗
     * @return
     */
    private CombatPVEParam buildPveParam(long uid, int challengeType, int defenderId, boolean fightAgain) {
        ZxzFourSaintsDefender defender = gameZxzFourSaintsService.getZxzFourSaintsDefender(challengeType, defenderId);
        CfgFourSaintsEntity.CfgFourSaintsDefenderCardRule defenderCardRule = CfgFourSaintsTool.getDefenderCardRule(defenderId);

        // 构建卡牌数据
        List<CCardParam> cardParams = new ArrayList<>();
        List<ZxzCard> zxzCards = ZxzAnalysisService.gainCards(defender.getDefenderCards());
        for (ZxzCard card : zxzCards) {
            UserCard.UserCardStrengthenInfo strengthenInfo = new UserCard.UserCardStrengthenInfo();
            strengthenInfo.updateCurrentSkill(CardSkillPosEnum.SKILL_0, card.getSkills().get(0));
            strengthenInfo.updateCurrentSkill(CardSkillPosEnum.SKILL_5, card.getSkills().get(1));
            strengthenInfo.updateCurrentSkill(CardSkillPosEnum.SKILL_10, card.getSkills().get(2));
            cardParams.add(CCardParam.init(card.getCardId(), card.getLv(),card.getHv(), strengthenInfo));
        }
        //召唤师头像跟名称
        CfgFourSaintsEntity.CfgNameAndHeadImg summonerNameAndHeadImg = CfgFourSaintsTool.getNameAndHeadImg(defenderCardRule.getChallengeType(), defenderCardRule.getKind());
        //构建召唤师初始化参数
        CPlayerInitParam ai = new CPlayerInitParam();
        ai.setHeadImg(summonerNameAndHeadImg.getHeadImg());
        ai.setInitBloodBarNum(defenderCardRule.getBloodBarNum());
        ai.setNickname(summonerNameAndHeadImg.getName());
        ai.setLv(defender.getSummonerLv());
        ai.setInitHP(CombatInitService.getPlayerInitHp(ai.getLv()));
        ai.setCards(cardParams);
        //处理符图
        List<CombatBuff> buffs = new ArrayList<>();
        List<Integer> runes = defender.getRunes();
        for (Integer fuTuId : runes) {
            CombatBuff fuTuBuff = new CombatBuff();
            fuTuBuff.setLevel(10);
            fuTuBuff.setRuneId(fuTuId);
            buffs.add(fuTuBuff);
        }

        //处理敌方召唤师带有长生词条
        CombatBuff changShengBuff = new CombatBuff();
        changShengBuff.setRuneId(RunesEnum.CHANG_SHENG_ENTRY.getRunesId());
        changShengBuff.setLevel(defenderCardRule.getBloodBarNum() - 1);
        buffs.add(changShengBuff);
        ai.setBuffs(buffs);

        //构建战斗参数
        CombatPVEParam pveParam = new CombatPVEParam();
        pveParam.setAiPlayer(ai);
        pveParam.setFightType(FightTypeEnum.ZXZ_FOUR_SAINTS.getValue());
        pveParam.setFightAgain(fightAgain);
        return pveParam;
    }

    @Override
    public void failure(GameUser gu, RDFightResult rd, FightSubmitParam param) {
        Integer defenderId = zxzFightCacheService.getFourSaintsDefenderId(gu.getId());
        //获取挑战类型
        Integer challengeType = CfgFourSaintsTool.getChallengeType(defenderId);
        //获取用户卡组
        UserZxzFourSaintsCardGroupInfo userCardGroup = userZxzFourSaintsService.getUserZxzFourSaintsCardGroup(gu.getId(), challengeType);
        userCardGroup.setHp(0);
        gameUserService.updateItem(userCardGroup);
    }

    @Override
    public void handleAward(GameUser gu, RDFightResult rd, FightSubmitParam param) {
        Integer defenderId = zxzFightCacheService.getFourSaintsDefenderId(gu.getId());
        Integer challengeType = CfgFourSaintsTool.getChallengeType(defenderId);

        //获取用户卡组
        UserZxzFourSaintsCardGroupInfo userCardGroup = userZxzFourSaintsService.getUserZxzFourSaintsCardGroup(gu.getId(), challengeType);
        int hp = userCardGroup.getHp() - param.getLostBlood();
        userCardGroup.setHp(Math.max(hp, 0));

        for (FightSubmitParam.SubmitCardParam killedCard : param.getKilledCards()) {
            //判断是否有主角卡
            if (null != userCardGroup.getZxzUserLeaderCard()) {
                if (userCardGroup.getZxzUserLeaderCard().getCardId() == killedCard.getId()) {
                    userCardGroup.getZxzUserLeaderCard().setAlive(false);
                }
            }
            for (UserZxzCard userCard : userCardGroup.getCards()) {
                if (userCard.getCardId() == killedCard.getId()) {
                    userCard.setAlive(false);
                }
            }
        }
        gameUserService.updateItem(userCardGroup);

        UserZxzFourSaintsInfo userZxzFourSaints = userZxzFourSaintsService.getUserZxzFourSaints(gu.getId(), challengeType);
        long lastRefreshDate = zxzFightCacheService.getFourSaintsLastRefreshDate(gu.getId());
        //当前的刷新时间等于缓存的刷新时间需要更新状态
        boolean isToUpdate = userZxzFourSaints.getLastRefreshDate().getTime() == lastRefreshDate;
        if (isToUpdate) {
            //更新区域状态
            updateRegionStatus(gu.getId(),defenderId);
        }
        //得到探索点
        obtainExploratoryPoint(gu.getId(),defenderId);
    }

    /**
     * 得到探索点
     * @param uid
     * @param defenderId
     */
    private void obtainExploratoryPoint(long uid, Integer defenderId) {
        Integer challengeType = CfgFourSaintsTool.getChallengeType(defenderId);
        UserZxzFourSaintsInfo userZxzFourSaints = userZxzFourSaintsService.getUserZxzFourSaints(uid, challengeType);

        //获取玩家关卡
        UserFourSaintsDefender uDefender = userZxzFourSaintsService.getUserFourSaintsDefender(uid, challengeType, defenderId);
        //判断是不是圣兽野怪
        if (!uDefender.ifKingTherion()) {
            return;
        }
        //判断是不是每周第一次
        if (userZxzFourSaints.getWeeklyFirstClearance()) {
            return;
        }
        userZxzFourSaints.addExploratoryPoint();
        userZxzFourSaints.firstWinEveryWeek();
        userZxzFourSaints.clearance();
        gameUserService.updateItem(userZxzFourSaints);
    }
    /**
     * 更新关卡状态
     * @param uid
     * @param defenderId
     */
    private void updateRegionStatus(long uid, Integer defenderId){
        Integer challengeType = CfgFourSaintsTool.getChallengeType(defenderId);
        //更改关卡的状态
        UserZxzFourSaintsInfo userZxzFourSaints = userZxzFourSaintsService.getUserZxzFourSaints(uid, challengeType);
        //当前关卡通关
        UserFourSaintsDefender defender = userZxzFourSaints.gainFourSaintsDefender(defenderId);
        defender.setStatus(ZxzStatusEnum.PASSED.getStatus());
        //判断是不是boss关卡
        UserFourSaintsDefender uDefender = userZxzFourSaintsService.getUserFourSaintsDefender(uid, challengeType, defenderId);
        if (uDefender.ifKingTherion()) {
            //该挑战区域已通关
            userZxzFourSaints.setStatus(ZxzStatusEnum.PASSED.getStatus());
        } else {
            //获取下一关
            Integer nextDefendeId = userZxzFourSaintsService.nextDefendeId(defenderId);
            UserFourSaintsDefender nextDefender = userZxzFourSaints.gainFourSaintsDefender(nextDefendeId);
            nextDefender.setStatus(ZxzStatusEnum.ABLE_ATTACK.getStatus());
        }
        //获取进度
        Integer regionProgress = userZxzFourSaints.gainProgress();
        userZxzFourSaints.setProgress(regionProgress);
        gameUserService.updateItem(userZxzFourSaints);
    }
}
