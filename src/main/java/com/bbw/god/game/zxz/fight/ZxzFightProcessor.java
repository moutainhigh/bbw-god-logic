package com.bbw.god.game.zxz.fight;

import com.bbw.common.DateUtil;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.cache.TimeLimitCacheUtil;
import com.bbw.god.fight.FightSubmitParam;
import com.bbw.god.fight.FightTypeEnum;
import com.bbw.god.fight.RDFightResult;
import com.bbw.god.fight.processor.AbstractFightProcessor;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardService;
import com.bbw.god.game.combat.CombatInitService;
import com.bbw.god.game.combat.data.CombatBuff;
import com.bbw.god.game.combat.data.param.CCardParam;
import com.bbw.god.game.combat.data.param.CPlayerInitParam;
import com.bbw.god.game.combat.data.param.CombatPVEParam;
import com.bbw.god.game.combat.runes.RunesEnum;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.card.CardTool;
import com.bbw.god.game.zxz.cfg.*;
import com.bbw.god.game.zxz.cfg.award.ZxzAwardTool;
import com.bbw.god.game.zxz.entity.*;
import com.bbw.god.game.zxz.enums.ZxzStatusEnum;
import com.bbw.god.game.zxz.event.ZxzEventPublisher;
import com.bbw.god.game.zxz.rank.ZxzRankService;
import com.bbw.god.game.zxz.service.ZxzAnalysisService;
import com.bbw.god.game.zxz.service.ZxzEnemyService;
import com.bbw.god.game.zxz.service.ZxzService;
import com.bbw.god.game.zxz.service.foursaints.UserZxzFourSaintsService;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.card.CardSkillPosEnum;
import com.bbw.god.gameuser.card.UserCard;
import com.bbw.god.gameuser.yuxg.UserFuCe;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;


/**
 * 诛仙阵战斗结算
 * @author: hzf
 * @create: 2022-09-23 11:53
 **/
@Service
public class ZxzFightProcessor extends AbstractFightProcessor {
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private ZxzService zxzService;
    @Autowired
    private ZxzEnemyService zxzEnemyService;
    @Autowired
    private AwardService awardService;
    @Autowired
    private ZxzRankService zxzRankService;
    @Autowired
    private ZxzFightCacheService zxzFightCacheService;
    @Autowired
    private UserZxzFourSaintsService zxzFourSaintsService;

    @Override
    public FightTypeEnum getFightType() {
        return FightTypeEnum.ZXZ;
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

        Integer regionId = ZxzTool.getRegionId(defenderId);
        Integer difficulty = ZxzTool.getDifficulty(regionId);

        UserZxzRegionInfo userZxzRegion = zxzService.getUserZxzRegion(uid, regionId);

        zxzFightCacheService.cacheDefenderId(uid, defenderId);

        zxzFightCacheService.cacheLastRefreshDate(uid,userZxzRegion.getLastRefreshDate().getTime());

        //战斗前保存玩家血量跟血量为0的卡牌
        UserZxzCardGroupInfo userCardGroup = zxzService.getUserCardGroup(uid, regionId);
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
//                cardIds.add(card.getCardId());
                userCardGroup.addLastCardId(card.getCardId());
            }
        }

        gameUserService.updateItem(userCardGroup);

        return buildPveParam(uid, difficulty, defenderId, fightAgain);

    }


    @Override
    public void failure(GameUser gu, RDFightResult rd, FightSubmitParam param) {

        Integer defenderId = zxzFightCacheService.getDefenderId(gu.getId());
        //获取区域Id
        Integer regionId = ZxzTool.getRegionId(defenderId);
        //获取用户卡组
        UserZxzCardGroupInfo userCardGroup = zxzService.getUserCardGroup(gu.getId(), regionId);
        userCardGroup.setHp(0);
        gameUserService.updateItem(userCardGroup);
    }

    @Override
    public void handleAward(GameUser gu, RDFightResult rd, FightSubmitParam param) {
        Integer defenderId = zxzFightCacheService.getDefenderId(gu.getId());
        Integer regionId = ZxzTool.getRegionId(defenderId);

        //获取用户卡组
        UserZxzCardGroupInfo userCardGroup = zxzService.getUserCardGroup(gu.getId(), regionId);
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

        UserZxzRegionInfo userZxzRegion = zxzService.getUserZxzRegion(gu.getId(), regionId);
        long lastRefreshDate = zxzFightCacheService.getLastRefreshDate(gu.getId());
        //当前的刷新时间等于缓存的刷新时间需要更新状态
        boolean isToUpdate = userZxzRegion.getLastRefreshDate().getTime() == lastRefreshDate;
        if (isToUpdate) {
            //更新区域状态
            updateRegionStatus(gu.getId(),defenderId);
        }


        //发放关卡奖励
        grantDefenderWinAwards(gu.getId(),defenderId,rd);
        //判断是否全通
        handleRegionAllClearance(gu.getId(),defenderId,rd);

    }

    /**
     * 更新区域关卡状态
     * @param uid
     * @param defenderId
     */
    private void updateRegionStatus(long uid, Integer defenderId){
        Integer regionId = ZxzTool.getRegionId(defenderId);
        Integer difficulty = ZxzTool.getDifficulty(regionId);
        //更改关卡的状态
        UserZxzRegionInfo userZxzRegion = zxzService.getUserZxzRegion(uid, regionId);
        //判断是不是boss关卡
        UserZxzRegionDefender uDefender = zxzService.getUserZxzRegionDefender(uid, defenderId);
        if (uDefender.ifKingChief()) {
            userZxzRegion.setStatus(ZxzStatusEnum.PASSED.getStatus());
            //区域携带的词条等级升级
            upgradeEntry(userZxzRegion);
            //计算区域等级
            Integer regionLv = userZxzRegion.computeRegionLv();
            ZxzInfo zxzInfo = zxzEnemyService.getZxzInfo();
            if (regionLv > userZxzRegion.gainClearanceLv()) {
                int addRegionLv = regionLv - userZxzRegion.getLastClearanceLv();
                int zxzBeginDate = DateUtil.toDateInt(new Date(zxzInfo.getGenerateTime()));
                //添加区域榜单
                zxzRankService.incrementRankValue(uid,addRegionLv,difficulty,regionId, zxzBeginDate);
                //保留通关卡组
                UserPassRegionCardGroupInfo passRegionCardGroup = zxzService.getUserPassRegionCardGroup(uid, regionId);
                UserZxzCardGroupInfo userCardGroup = zxzService.getUserCardGroup(uid, regionId);
                //获取符册名称
                String fuCeName = "";
                if (0 != userCardGroup.getFuCeDataId()) {
                    Optional<UserFuCe> userFuCe = gameUserService.getUserData(uid, userCardGroup.getFuCeDataId(), UserFuCe.class);
                    if (userFuCe.isPresent()) {
                        fuCeName = userFuCe.get().getName();
                    }
                }

                if (null == passRegionCardGroup) {
                    passRegionCardGroup = UserPassRegionCardGroupInfo.instance(uid,userCardGroup,userZxzRegion.getEntries(),regionLv,fuCeName,zxzInfo.getGenerateTime(),zxzInfo.getEndTime());
                    gameUserService.addItem(uid,passRegionCardGroup);
                } else {
                    passRegionCardGroup.updateUserPassRegionCardGroup(userCardGroup,userZxzRegion.getEntries(),regionLv,fuCeName);
                    gameUserService.updateItem(passRegionCardGroup);
                }
                userZxzRegion.setClearanceLv(regionLv);
            }
            userZxzRegion.setLastClearanceLv(regionLv);
            UserZxzRegionDefender defender = userZxzRegion.gainRegionDefender(defenderId);
            defender.setStatus(ZxzStatusEnum.PASSED.getStatus());

        } else {
            UserZxzRegionDefender regionDefender = userZxzRegion.gainRegionDefender(defenderId);
            regionDefender.setStatus(ZxzStatusEnum.PASSED.getStatus());

            //获取下一关
            Integer nextDefendeId = zxzService.nextDefendeId(defenderId);
            UserZxzRegionDefender regionNextDefender = userZxzRegion.gainRegionDefender(nextDefendeId);
            regionNextDefender.setStatus(ZxzStatusEnum.ABLE_ATTACK.getStatus());
        }
        //获取进度
        Integer regionProgress = userZxzRegion.gainRegionProgress();
        userZxzRegion.setProgress(regionProgress);
        gameUserService.updateItem(userZxzRegion);
    }

    private boolean ifRetainPassCardGroup(long uid, Integer difficulty,Integer regionId,Integer zxzBeginDate){
        int rank = zxzRankService.getRank(uid, difficulty, regionId, zxzBeginDate);
        Integer rankLimit = ZxzTool.getCfg().getRankLimit();
        //当前排名在限制排名范围内
        if (rankLimit < rank) {
            return false;
        }
        return true;
    }

    /**
     * 判断区域是否全通
     * @param uid
     * @param defenderId
     * @param rd
     */
    private void handleRegionAllClearance(long uid, Integer defenderId, RDFightResult rd){
        Integer regionId = ZxzTool.getRegionId(defenderId);
        Integer difficulty = ZxzTool.getDifficulty(regionId);

        //判断是否全通
        if (!zxzService.ifRegionAllClearance(uid,difficulty)) {
            return;
        }

        //获取玩家诛仙阵数据
        UserZxzInfo userZxz = zxzService.getUserZxz(uid);

        //全通结算
        Integer regionLvs = zxzService.gainRegionLvs(uid, difficulty);
        int clearanceScore = regionLvs * 8 / 10;
        //诛仙阵成就事件
        ZxzEventPublisher.pubZhiBaoAddEvent(uid, difficulty, clearanceScore);
        /**开启下个区域 */
        Integer newDifficulty = zxzService.openNextDifficulty(uid, difficulty, clearanceScore);
        if (newDifficulty > 0) {
            //解锁下个难度，更新该难度难度的状态
            UserZxzDifficulty userZxzDifficulty = userZxz.gainUserZxzLevel(newDifficulty);
            userZxzDifficulty.setStatus(ZxzStatusEnum.ABLE_ATTACK.getStatus());
        }
        //获取难度数据
        UserZxzDifficulty userZxzDifficulty = userZxz.gainUserZxzLevel(difficulty);
        userZxzDifficulty.settlement(clearanceScore);

        //解锁四圣挑战
        zxzFourSaintsService.unlockFourSaints(uid, clearanceScore,difficulty);


        gameUserService.updateItem(userZxz);
    }

    /**
     * 战斗奖励
     * @param uid
     * @param defenderId
     * @param rd
     */
    public void grantDefenderWinAwards(long uid, Integer defenderId, RDFightResult rd) {
        Integer regionId = ZxzTool.getRegionId(defenderId);
        Integer difficulty = ZxzTool.getDifficulty(regionId);
        // 战斗奖励
        UserZxzRegionDefender uDefender = zxzService.getUserZxzRegionDefender(uid, defenderId);
        //计算区域等级
        UserZxzRegionInfo userZxzRegion = zxzService.getUserZxzRegion(uid, regionId);
        Integer regionLv = userZxzRegion.computeRegionLv();

        UserZxzDifficulty userZxzDifficulty = zxzService.getUserZxzLevel(uid, difficulty);
        //首次通关可能为0，会造空物品掉落
        Integer clearanceNum = userZxzDifficulty.gainClearanceNum();
        List<Award> awards = ZxzAwardTool.getDefenderWinAwards(difficulty, uDefender.getKind(), regionLv,clearanceNum);
        awardService.sendNeedMergedAwards(uid, awards, WayEnum.ZXZ_DROP_AWARD, WayEnum.ZXZ_DROP_AWARD.getName(), rd);
    }

    /**
     * 升级词条
     *
     * @param userZxzRegion
     */
    public void upgradeEntry(UserZxzRegionInfo userZxzRegion) {
        long uid = userZxzRegion.getGameUserId();
        int difficulty = userZxzRegion.getDifficulty();
        //获取用户词条
        UserEntryInfo userEntry = zxzService.getUserEntry(uid, difficulty);

        //处理解锁档位
        CfgZxzLevel cfgZxzLevel = ZxzTool.getZxzLevel(difficulty);
        Integer entryLvLimit = cfgZxzLevel.getEntryLvLimit();
        Integer currentGearMax = Collections.max(userEntry.getEntryGears());
        List<CfgZxzEntryEntity> currentCfgGearEntry = ZxzEntryTool.getEntryByGear(currentGearMax);
        List<Integer> currentCfgGearEntryIds = currentCfgGearEntry.stream().map(CfgZxzEntryEntity::getEntryId).collect(Collectors.toList());
        //当前档位的词条
        List<UserEntryInfo.UserEntry> userEntries = ZxzAnalysisService.gainUserEntrys(userEntry.getUserEntry());
        List<UserEntryInfo.UserEntry> currentUserGearEntry = userEntries.stream()
                .filter(entry -> entry.getEntryLv() >= entryLvLimit)
                .collect(Collectors.toList());
        //当前档位的词条id
        List<Integer> currentUserGearEntryIds = currentUserGearEntry.stream().map(UserEntryInfo.UserEntry::getEntryId).collect(Collectors.toList());
        //判断两个是否相等
        if (currentUserGearEntryIds.containsAll(currentCfgGearEntryIds)) {
            //解锁档位
            int gear = currentGearMax + 1 > cfgZxzLevel.getUnlockEntryGearLimit() ? cfgZxzLevel.getUnlockEntryGearLimit() : currentGearMax + 1;
            //档位不存在时候在添加
            if (!userEntries.contains(gear)) {
                userEntry.addEntryGear(gear);
            }
        }


        //获取携带的词条
        List<ZxzEntry> zxzEntries = userZxzRegion.gainEntrys();
        for (ZxzEntry zxzEntry : zxzEntries) {
            userEntry.upgradeEntryLv(zxzEntry.getEntryId());
        }




        gameUserService.updateItem(userEntry);
    }

    /**
     * 构建战斗参数
     *
     * @param uid
     * @param difficulty 难度
     * @param defenderId 守卫（关卡）ID
     * @param fightAgain 是否是重复战斗
     * @return
     */
    private CombatPVEParam buildPveParam(long uid, int difficulty, int defenderId, boolean fightAgain) {
        CfgZxzDefenderCardRule zxzDefenderCard = ZxzTool.getZxzDefenderCard(defenderId);

        ZxzRegionDefender defender = zxzEnemyService.getZxzRegionDefender(defenderId);
        // 构建卡牌数据
        List<CCardParam> cardParams = new ArrayList<>();
        List<ZxzCard> zxzCards = ZxzAnalysisService.gainCards(defender.getDefenderCards());
        for (ZxzCard card : zxzCards) {
            UserCard.UserCardStrengthenInfo strengthenInfo = new UserCard.UserCardStrengthenInfo();
            strengthenInfo.updateCurrentSkill(CardSkillPosEnum.SKILL_0, card.getSkills().get(0));
            strengthenInfo.updateCurrentSkill(CardSkillPosEnum.SKILL_5, card.getSkills().get(1));
            strengthenInfo.updateCurrentSkill(CardSkillPosEnum.SKILL_10, card.getSkills().get(2));
            cardParams.add(CCardParam.init(card.getCardId(), card.getLv(), card.getHv(), strengthenInfo));
        }
        //构建召唤师初始化参数
        CPlayerInitParam ai = new CPlayerInitParam();
        int head = cardParams.get(0).getId();
        ai.setHeadImg(head);
        ai.setNickname(CardTool.getCardById(head).getName());
        ai.setLv(defender.getSummonerLv());
        ai.setInitHP(CombatInitService.getPlayerInitHp(ai.getLv()));
        ai.setCards(cardParams);
        ai.setInitBloodBarNum(zxzDefenderCard.getBloodBarNum());
        ai.addBuffs(defender.getRunes());

        ZxzRegion zxzRegion = zxzEnemyService.getZxzRegion(ZxzTool.getRegionId(defenderId));
        //处理词条
        List<CombatBuff> combatBuffs = ai.getBuffs();
        List<CombatBuff> buffs = addEntry(uid, zxzRegion.gainEntrys(), defenderId);
        combatBuffs.addAll(buffs);
        ai.setBuffs(combatBuffs);

        //构建战斗参数
        CombatPVEParam pveParam = new CombatPVEParam();
        pveParam.setAiPlayer(ai);
        pveParam.setFightType(FightTypeEnum.ZXZ.getValue());
        pveParam.setFightAgain(fightAgain);
        return pveParam;
    }

    //处理敌方佩戴的词条
    public List<CombatBuff> addEntry(long uid,List<ZxzEntry> zxzEntries,Integer defenderId){
        List<CombatBuff> combatBuffs = new ArrayList<>();
        for (ZxzEntry entry : zxzEntries) {
            CombatBuff combatBuff = new CombatBuff();
            //处理长生词条
            if (entry.getEntryId() == RunesEnum.CHANG_SHENG_ENTRY.getRunesId()) {
                CfgZxzDefenderCardRule zxzDefenderCard = ZxzTool.getZxzDefenderCard(defenderId);
                combatBuff.setRuneId(entry.getEntryId());
                combatBuff.setLevel(zxzDefenderCard.getBloodBarNum() - 1);
                combatBuffs.add(combatBuff);
            }
            //处理狂暴
            if (entry.getEntryId() == RunesEnum.KUANG_BAO_ERTRY.getRunesId()) {
                // 获取区域Id
                Integer regionId = ZxzTool.getRegionId(defenderId);
                // 换取难度
                Integer difficulty = ZxzTool.getDifficulty(regionId);
                //狂暴词条的等级 ==> 通关区域数量 * 2
                Integer clearanceNum = zxzService.countClearanceNum(uid, difficulty);

                combatBuff.setRuneId(entry.getEntryId());
                combatBuff.setLevel(clearanceNum * 2);
                combatBuffs.add(combatBuff);
            }
        }
        return combatBuffs;
    }

}
