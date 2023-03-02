package com.bbw.god.game.zxz.service;

import com.bbw.cache.UserCacheService;
import com.bbw.common.ListUtil;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.game.combat.data.CombatBuff;
import com.bbw.god.game.combat.data.CombatSkillEnum;
import com.bbw.god.game.combat.data.param.CCardParam;
import com.bbw.god.game.combat.data.param.CPCardGroup;
import com.bbw.god.game.combat.runes.RunesEnum;
import com.bbw.god.game.config.card.CardEnum;
import com.bbw.god.game.zxz.cfg.CfgZxzLevel;
import com.bbw.god.game.zxz.cfg.ZxzTool;
import com.bbw.god.game.zxz.constant.ZxzConstant;
import com.bbw.god.game.zxz.entity.*;
import com.bbw.god.game.zxz.enums.ZxzDifficultyEnum;
import com.bbw.god.game.zxz.enums.ZxzStatusEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.card.UserCard;
import com.bbw.god.gameuser.card.UserCardService;
import com.bbw.god.gameuser.card.equipment.CardEquipmentService;
import com.bbw.god.gameuser.card.equipment.UserCardXianJueService;
import com.bbw.god.gameuser.card.equipment.UserCardZhiBaoService;
import com.bbw.god.gameuser.card.equipment.cfg.CardFightAddition;
import com.bbw.god.gameuser.card.equipment.data.UserCardXianJue;
import com.bbw.god.gameuser.card.equipment.data.UserCardZhiBao;
import com.bbw.god.gameuser.leadercard.UserLeaderCard;
import com.bbw.god.gameuser.leadercard.beast.BeastTool;
import com.bbw.god.gameuser.leadercard.beast.UserLeaderBeastService;
import com.bbw.god.gameuser.leadercard.equipment.UserLeaderEquimentService;
import com.bbw.god.gameuser.leadercard.equipment.UserLeaderEquipment;
import com.bbw.god.gameuser.leadercard.service.LeaderCardService;
import com.bbw.god.gameuser.yuxg.UserFuCe;
import com.bbw.god.gameuser.yuxg.UserFuTu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 诛仙阵Service
 * @author: hzf
 * @create: 2022-09-20 09:58
 **/
@Service
public class ZxzService {
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private UserCacheService userCacheService;
    @Autowired
    private UserCardService userCardService;
    @Autowired
    private InitUserZxzService initUserZxzService;
    @Autowired
    private CardEquipmentService cardEquipmentService;
    @Autowired
    private UserCardZhiBaoService userCardZhiBaoService;
    @Autowired
    private UserCardXianJueService userCardXianJueService;
    @Autowired
    private LeaderCardService leaderCardService;
    @Autowired
    private UserLeaderEquimentService userLeaderEquimentService;
    @Autowired
    private UserLeaderBeastService userLeaderBeastService;
    @Autowired
    private ZxzEnemyService zxzEnemyService;

    /**
     * 获取玩家诛仙阵数据(如果需要刷新会自动刷新)
     *
     * @param uid
     * @return
     */
    public UserZxzInfo getUserZxz(long uid) {
        //获取玩家诛仙阵数据
        UserZxzInfo zxzInfo = gameUserService.getSingleItem(uid, UserZxzInfo.class);
        if (null == zxzInfo) {
            return zxzInfo;
        }
        boolean isNeedToUpdate = false;
        for (UserZxzDifficulty zxzLevel : zxzInfo.getDifficultyInfo()) {
            boolean isToRefresh = ZxzRefreshService.isToRefresh(zxzLevel.getDifficulty(), zxzLevel.getLastRefreshDate());
            //判断是否重置通关等级和通关评分
            boolean isToResetLvAndScore = ZxzRefreshService.isToRefresh(zxzLevel.getLastRefreshDate());
            if (zxzLevel.getStatus().equals(ZxzStatusEnum.NOT_OPEN.getStatus())) {
                continue;
            }
            if (isToRefresh) {
                isNeedToUpdate = true;
                zxzLevel.autoRefresh();
                //自动刷新每个区域的关卡数据
                List<UserZxzRegionInfo> zxzRegionIs = getUserZxzRegions(uid, zxzLevel.getDifficulty());
                for (UserZxzRegionInfo userZxzRegion : zxzRegionIs) {
                    List<UserZxzRegionDefender> regionDefenders = userZxzRegion.getRegionDefenders();
                    userZxzRegion.autoRefreshRegion(regionDefenders);
                    //重置通关等级
                    if (isToResetLvAndScore) {
                        userZxzRegion.resetClearanceLv();
                    }
                }
                gameUserService.updateItems(zxzRegionIs);
            }
            //重置通关评分
            if (isToResetLvAndScore) {
                zxzLevel.resetClearanceScore();
            }
        }
        if (isNeedToUpdate) {
            gameUserService.updateItem(zxzInfo);
        }
        return zxzInfo;
    }

    /**
     * 获取玩家的难度数据
     *
     * @param uid
     * @param difficulty
     * @return
     */
    public UserZxzDifficulty getUserZxzLevel(long uid, Integer difficulty) {
        //获取玩家诛仙阵数据
        return getUserZxz(uid).getDifficultyInfo().stream()
                .filter(uZxz -> uZxz.getDifficulty().equals(difficulty))
                .findFirst().orElse(null);
    }

    /**
     * 获取用户诛仙阵区域攻打信息
     * @param uid
     * @param regionId
     * @return
     */
    public UserZxzRegionInfo getUserZxzRegion(long uid, Integer regionId){
        Integer difficulty = ZxzTool.getDifficulty(regionId);
        return getUserZxzRegions(uid,difficulty).stream()
                .filter(userZxzRegion -> userZxzRegion.getRegionId().equals(regionId) && userZxzRegion.getDifficulty().equals(difficulty))
                .findFirst().orElse(null);
    }

    /**
     * 获取玩家区域数据不走本地缓存
     * @param uid
     * @return
     */
    public List<UserZxzRegionInfo> getRedisUserZxzRegions(long uid){
       return gameUserService.getMultiItems(uid,UserZxzRegionInfo.class);
    }
    /**
     * 获取玩家的区域攻打记录集合
     * @param uid
     * @return
     */
    public List<UserZxzRegionInfo> getUserZxzRegions(long uid) {
        List<UserZxzRegionInfo> regions = userCacheService.getUserDatas(uid, UserZxzRegionInfo.class);
        List<UserZxzRegionInfo> toUpdates = new ArrayList<>();
        for (UserZxzRegionInfo region : regions) {
            Integer difficulty = region.getDifficulty();
            boolean isToRefresh = ZxzRefreshService.isToRefresh(difficulty, region.getLastRefreshDate());
            if (isToRefresh) {
                List<UserZxzRegionDefender> defenders = region.getRegionDefenders();
                region.autoRefreshRegion(defenders);
                toUpdates.add(region);
            }
        }
        if (ListUtil.isNotEmpty(toUpdates)) {
            gameUserService.updateItems(toUpdates);
        }
        return regions;
    }
    /**
     * 获取玩家的区域攻打记录集合
     * @param uid
     * @param difficulty
     * @return
     */
    public List<UserZxzRegionInfo> getUserZxzRegions(long uid, Integer difficulty){
        return getUserZxzRegions(uid).stream()
                .filter(userZxzRegion -> userZxzRegion.getDifficulty().equals(difficulty))
                .collect(Collectors.toList());
    }

    /**
     * 获取关卡集合
     * @param uid
     * @param regionId
     * @return
     */
    public List<UserZxzRegionDefender> getUserZxzRegionDefenders(long uid, Integer regionId){
        UserZxzRegionInfo userZxzRegion = getUserZxzRegion(uid, regionId);
        return userZxzRegion.getRegionDefenders();
    }
    /**
     * 获取关卡
     * @param uid
     * @param defenderId
     * @return
     */
    public UserZxzRegionDefender getUserZxzRegionDefender(long uid, Integer defenderId){
        Integer regionId = ZxzTool.getRegionId(defenderId);
        UserZxzRegionDefender regionDefender = getUserZxzRegionDefenders(uid, regionId).stream()
                .filter(defender -> defender.getDefenderId().equals(String.valueOf(defenderId)))
                .findFirst().orElse(null);
        if(null == regionDefender){
            throw new ExceptionForClientTip("zxz.defender.no.exist");
        }
        return regionDefender;
    }

    /**
     * 获取用户词条
     * @param uid
     * @return
     */
    public List<UserEntryInfo> getUserEntry(long uid){
        return gameUserService.getMultiItems(uid, UserEntryInfo.class);
    }
    /**
     * 获取用户词条
     * @param uid
     * @param difficulty
     * @return
     */
    public UserEntryInfo getUserEntry(long uid, Integer difficulty){
        return  getUserEntry(uid).stream()
                .filter(entry -> entry.getDifficulty().equals(difficulty))
                .findFirst().orElse(null);
    }

    /**
     * 获取通关卡组 集合
     * @param uid
     * @return
     */
    public List<UserPassRegionCardGroupInfo> getUserPassRegionCardGroupInfos(long uid){
        return gameUserService.getMultiItems(uid,UserPassRegionCardGroupInfo.class);
    }
    /**
     * 获取通关卡组
     * @param uid
     * @param regionId
     * @return
     */
    public UserPassRegionCardGroupInfo getUserPassRegionCardGroup(long uid,Integer regionId){
        return getUserPassRegionCardGroupInfos(uid).stream()
                .filter(cardGroup -> cardGroup.getGameUserId() == uid && cardGroup.getRegionId().equals(regionId) && cardGroup.ifValid() )
                .findFirst().orElse(null);
    }
    /**
     * 获取通关卡组
     * @param uid
     * @param regionId
     * @param beginDate
     * @return
     */
    public UserPassRegionCardGroupInfo getUserPassRegionCardGroup(long uid,Integer regionId,Integer beginDate){
        return getUserPassRegionCardGroupInfos(uid).stream()
                .filter(cardGroup -> cardGroup.getGameUserId() == uid  && cardGroup.getRegionId().equals(regionId) && cardGroup.ifValid(beginDate))
                .findFirst().orElse(null);
    }

    /**
     * 获取用户的区域卡组集合
     * @param uid
     * @return
     */
    public List<UserZxzCardGroupInfo> getUserZxzCardGroup(long uid){
      return userCacheService.getUserDatas(uid,UserZxzCardGroupInfo.class);
    }

    /**
     * 获取用户的区域卡组集合 不走本地缓存
     * @param uid
     * @return
     */
    public List<UserZxzCardGroupInfo> getRedisUserZxzCardGroup(long uid){
        return gameUserService.getMultiItems(uid,UserZxzCardGroupInfo.class);
    }

    /**
     * 获取用户区域卡组信息
     * @param uid
     * @param regionId
     * @return
     */
    public UserZxzCardGroupInfo getUserCardGroup(long uid, Integer regionId){
        return getUserZxzCardGroup(uid).stream()
                .filter(cardGroup -> cardGroup.getRegionId().equals(regionId))
                .findFirst().orElse(null);
    }
    /**
     * 获取区域等级和
     * @param uid
     * @param difficulty
     * @return
     */
    public Integer gainRegionLvs(long uid, Integer difficulty){
        Integer regionLv = 0;
        List<UserZxzRegionInfo> userZxzRegions = getUserZxzRegions(uid, difficulty);
        for (UserZxzRegionInfo userZxzRegion : userZxzRegions) {
            Integer lv = userZxzRegion.gainClearanceLv();
            regionLv += lv;
        }
        return regionLv;
    }

    /**
     * 判断难度是否在攻打
     * @param uid
     * @param difficulty
     * @return
     */
    public boolean ifDifficutyAttack(long uid, Integer difficulty){
        List<UserZxzRegionInfo> userZxzRegions = getUserZxzRegions(uid, difficulty);
        for (UserZxzRegionInfo userZxzRegion : userZxzRegions) {
            if (userZxzRegion.ifDifficutyRegionAttack()) {
                return true;
            }
        }
        return false;
    }
    /**
     * 判断全部区域是否通关
     * @param uid
     * @param difficulty
     * @return
     */
    public boolean ifRegionAllClearance(long uid, Integer difficulty){
        List<UserZxzRegionInfo> userZxzRegions = getUserZxzRegions(uid, difficulty);
        for (UserZxzRegionInfo userZxzRegion : userZxzRegions) {
            if (!userZxzRegion.ifRegionClearance()) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断某个难度下,区域有一个通关
     * @param uid
     * @param difficulty
     * @return
     */
    public boolean ifRegionAtLeastClearance(long uid, Integer difficulty){
        List<UserZxzRegionInfo> userZxzRegions = getUserZxzRegions(uid, difficulty);
        for (UserZxzRegionInfo userZxzRegion : userZxzRegions) {
            if (userZxzRegion.ifRegionClearance()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取通关区域的次数
     * @param uid
     * @param difficulty
     * @return
     */
    public Integer countClearanceNum(long uid, Integer difficulty){
        int clearanceNum = 0;
        List<UserZxzRegionInfo> userZxzRegions = getUserZxzRegions(uid, difficulty);
        for (UserZxzRegionInfo userZxzRegion : userZxzRegions) {
            if (userZxzRegion.ifRegionClearance()) {
                clearanceNum++;
            }
        }
        return clearanceNum;
    }


    /**
     * 获取下一关
     * @param defenderId
     * @return
     */
    public Integer nextDefendeId(Integer defenderId){
        //当前第几关
        int parseInt = Integer.parseInt(String.valueOf(defenderId).substring(3,4));
        if (parseInt == ZxzConstant.LAST_DEFENDER) {
            return 0;
        }
        return defenderId + 1;
    }


    /**
     * 解锁下个难度
     * @param uid
     * @param clearanceScore
     */
    public Integer openNextDifficulty(long uid, Integer currentDifficulty, Integer clearanceScore){
        if (currentDifficulty.equals(ZxzConstant.LAST_DIFFICULTY)) {
            return 0;
        }
        //下个难度
        Integer difficulty = currentDifficulty + 10;

        CfgZxzLevel zxzLevel = ZxzTool.getZxzLevel(difficulty);
        if (clearanceScore < zxzLevel.getUnlockNeedScore()) {
            return 0;
        }
        //判断下个难度是否已经开启
        UserZxzDifficulty userZxzDifficulty = getUserZxzLevel(uid, difficulty);
        if (!userZxzDifficulty.gainStatus().equals(ZxzStatusEnum.NOT_OPEN.getStatus())) {
            return 0;
        }
        //初始化区域数据
        CfgZxzLevel nextZxzLevel = ZxzTool.getZxzLevel(difficulty);
        List<UserZxzRegionInfo> userZxzRegionInfoList = new ArrayList<>();
        for (Integer regionId : nextZxzLevel.getRegions()) {
            UserZxzRegionInfo userZxzRegionInfo = initUserZxzService.initUserZxzRegion(uid, ZxzDifficultyEnum.fromZxzDifficulty(difficulty), regionId);
            userZxzRegionInfoList.add(userZxzRegionInfo);
        }
        userCacheService.addUserDatas(userZxzRegionInfoList);
        return difficulty;
    }


    /**
     * 锁定符册
     * @param uid
     * @param regionId
     */
    public void checkAndLockFuCe(long uid, Integer regionId){
        //获取玩家的卡组
        UserZxzCardGroupInfo userCardGroup = getUserCardGroup(uid, regionId);
        if (userCardGroup == null) {
            throw new ExceptionForClientTip("zxz.card.not");
        }
        if (0 == userCardGroup.getFuCeDataId()) {
            return;
        }
        //记录符册信息
        Optional<UserFuCe> userData = gameUserService.getUserData(uid, userCardGroup.getFuCeDataId(), UserFuCe.class);
        if (!userData.isPresent()) {
            return;
        }
        List<String> zxzFuTus = new ArrayList<>();
        List<UserFuCe.FuTu> fuTus = userData.get().getFuTus();
        for (UserFuCe.FuTu fuTu : fuTus) {
            if (0 == fuTu.getDataId()) {
                continue;
            }
            Optional<UserFuTu> userFuTu = gameUserService.getUserData(uid, fuTu.getDataId(), UserFuTu.class);
            if (!userFuTu.isPresent()) {
                continue;
            }
            ZxzFuTu zxzFuTu = new ZxzFuTu();
            String zxzFuTuString = zxzFuTu.instance(fuTu.getPos(), userFuTu.get());
            zxzFuTus.add(zxzFuTuString);
        }
        userCardGroup.setRunes(zxzFuTus);
        gameUserService.updateItem(userCardGroup);
    }
    /**
     * 锁定卡组
     * @param uid
     * @param regionId
     */
    public void checkAndLockCardGroup (long uid, Integer regionId){
        //获取玩家的卡组
        UserZxzCardGroupInfo userCardGroup = getUserCardGroup(uid, regionId);
        if (userCardGroup == null) {
            throw new ExceptionForClientTip("zxz.card.not");
        }
        Integer difficulty = ZxzTool.getDifficulty(regionId);
        CfgZxzLevel zxzLevel = ZxzTool.getZxzLevel(difficulty);
        //减少的卡牌等级
        Integer reduceCardLv = zxzLevel.getReduceCardLv();
        //卡牌id集合
        List<Integer> cardIdList = userCardGroup.getCards().stream().map(UserZxzCard::getCardId).collect(Collectors.toList());
        List<UserCard> userCards = userCardService.getUserCards(uid, cardIdList);

        //减少卡牌等级
        for (UserCard userCard : userCards) {
           int cardLv = Math.max(userCard.getLevel() - reduceCardLv, 0);
            userCard.setLevel(cardLv);
        }
        //实例诛仙阵卡组
        List<UserZxzCard> userZxzCards = UserZxzCard.getInstance(userCards);
        //获取诛仙阵卡牌
        List<CardFightAddition> equipmentAdditions = cardEquipmentService.getEquipmentAdditions(uid, cardIdList);
        for (UserZxzCard userZxzCard : userZxzCards) {
            //处理至宝
            List<UserCardZhiBao> userCardZhiBaos = userCardZhiBaoService.getUserCardZhiBaos(uid, userZxzCard.getCardId());
            List<UserZxzCardZhiBao> zxzUserCardZhiBaos = UserZxzCardZhiBao.getInstances(userCardZhiBaos);
            userZxzCard.setZhiBaos(zxzUserCardZhiBaos);
            //处理仙决
            List<UserCardXianJue> userCardXianJues = userCardXianJueService.getUserCardXianJues(uid, userZxzCard.getCardId());
            List<UserZxzCardXianJue> zxzUserCardXianJues = UserZxzCardXianJue.getInstances(userCardXianJues);
            userZxzCard.setXianJues(zxzUserCardXianJues);
            //处理仙决至宝加成属性
            for (CardFightAddition equipmentAddition : equipmentAdditions) {
                if (equipmentAddition.getCardId().equals(userZxzCard.getCardId())) {
                    userZxzCard.setAddition(equipmentAddition);
                }
            }
        }
        ZxzUserLeaderCard zxzUserLeaderCard = userCardGroup.getZxzUserLeaderCard();
        if (null != zxzUserLeaderCard && zxzUserLeaderCard.getCardId() == CardEnum.LEADER_CARD.getCardId()) {
            UserLeaderCard uLeaderCard = leaderCardService.getUserLeaderCard(uid);
            UserLeaderEquipment[] equipments = userLeaderEquimentService.getTakedEquipments(uid);
            int[] beasts = userLeaderBeastService.getTakedBeasts(uid);
            zxzUserLeaderCard = ZxzUserLeaderCard.instance(uLeaderCard,equipments,beasts);
        }

        //锁定卡组
        GameUser gameUser = gameUserService.getGameUser(uid);
        userCardGroup.lockCardGroup(gameUser,userZxzCards,zxzUserLeaderCard);
        gameUserService.updateItem(userCardGroup);

    }

    /**
     * 构建诛仙阵卡组
     * @param uid
     * @param defenderId
     * @return
     */
    public CPCardGroup getZxzUserCard(long uid, Long defenderId) {

        Integer regionId = ZxzTool.getRegionId(defenderId.intValue());
        UserZxzCardGroupInfo userCardGroup = getUserCardGroup(uid, regionId);
        //获取卡牌初始化化参数
        List<CCardParam> cCardParams = UserZxzCard.gainCCardParam(userCardGroup.getCards());
        // 添加主角卡
        if (null != userCardGroup.getZxzUserLeaderCard() && userCardGroup.getZxzUserLeaderCard().getCardId() == CardEnum.LEADER_CARD.getCardId()) {
            CCardParam cCardParam = instanceZxzUserLeader(userCardGroup.getZxzUserLeaderCard());
            cCardParams.add(cCardParam);
            cCardParam.setAlive(userCardGroup.getZxzUserLeaderCard().getAlive());
        }


        List<CombatBuff> combatBuffs = new ArrayList<>();
        //处理符图
        ZxzFuTu fuTu = new ZxzFuTu();
        List<ZxzFuTu> zxzFuTus = ZxzAnalysisService.gainRunes(userCardGroup.getRunes());
        List<CombatBuff> fuTuCombatBuffs = fuTu.gainFuTuCombatBuff(zxzFuTus);
        combatBuffs.addAll(fuTuCombatBuffs);
        //处理词条
        UserZxzRegionInfo userZxzRegion = getUserZxzRegion(uid, regionId);
        ZxzEntry zxzEntry = new ZxzEntry();
        List<CombatBuff> entryCombatBuffs = zxzEntry.gainEntryCombatBuff(userZxzRegion.gainEntrys());
        combatBuffs.addAll(entryCombatBuffs);
        //处理诅咒词条
        List<CombatBuff> zuZhouEntryCombatBuffs = handleZuZhouEntry(uid, regionId);
        combatBuffs.addAll(zuZhouEntryCombatBuffs);
        //构建战斗卡组
        CPCardGroup instance = CPCardGroup.getInstance(uid, cCardParams);
        instance.setHp(userCardGroup.getHp());
        instance.setBuffs(combatBuffs);
        return instance;
    }
    //处理玩家佩戴诅咒词条
    private List<CombatBuff> handleZuZhouEntry(long uid,Integer regionId){
        List<CombatBuff> combatBuffs = new ArrayList<>();
        Integer difficulty = ZxzTool.getDifficulty(regionId);
        List<UserZxzRegionInfo> userZxzRegions = getUserZxzRegions(uid, difficulty);
        for (UserZxzRegionInfo userZxzRegion : userZxzRegions) {
            //如果已经通关
            if (userZxzRegion.ifRegionClearance()) {
                ZxzRegion zxzRegion = zxzEnemyService.getZxzRegion(userZxzRegion.getRegionId());
                for (ZxzEntry entry : zxzRegion.gainEntrys()) {
                    CombatBuff combatBuff = new CombatBuff();
                    //排除长生词条与狂暴词条
                    boolean excludeEntry = entry.getEntryId() != RunesEnum.CHANG_SHENG_ENTRY.getRunesId() && entry.getEntryId() != RunesEnum.KUANG_BAO_ERTRY.getRunesId();
                    if (excludeEntry) {
                        combatBuff.setRuneId(entry.getEntryId());
                        combatBuff.setLevel(entry.getEntryLv());
                        combatBuffs.add(combatBuff);
                    }
                }
            }
        }
        return combatBuffs;

    }

    /**
     * 构建主角卡信息
     * @param leaderCard
     * @return
     */
    public static CCardParam instanceZxzUserLeader(ZxzUserLeaderCard leaderCard){
        CCardParam param = new CCardParam();
        param.setId(leaderCard.getCardId());
        param.setLv(leaderCard.getLv());
        param.setHv(leaderCard.getHv());
        param.setStar(leaderCard.getStar());
        param.setAtk(leaderCard.getAtk());
        param.setHp(leaderCard.getHp());
        param.setType(leaderCard.getProperty());
        param.setSex(leaderCard.getSex());
        param.setFashion(leaderCard.getFashion());
        param.setSkills(leaderCard.getSkills());
        param.getSkills().addAll(0,getBeastSkill(leaderCard.getBeasts()));
        return param;
    }

    public static List<Integer> getBeastSkill(int[] beasts){
        List<Integer> skills = new ArrayList<>();
        for (Integer beastId : beasts) {
            if (0 == beastId) {
                continue;
            }
            List<Integer> beastIdSkill = BeastTool.getSkills(beastId);
            skills.addAll(beastIdSkill);
        }
        return skills;
    }
}
