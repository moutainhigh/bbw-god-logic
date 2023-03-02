package com.bbw.god.game.zxz.service.foursaints;

import com.bbw.common.DateUtil;
import com.bbw.common.ListUtil;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.game.combat.data.CombatBuff;
import com.bbw.god.game.combat.data.param.CCardParam;
import com.bbw.god.game.combat.data.param.CPCardGroup;
import com.bbw.god.game.config.card.CardEnum;
import com.bbw.god.game.zxz.cfg.foursaints.CfgFourSaintsEntity;
import com.bbw.god.game.zxz.cfg.foursaints.CfgFourSaintsTool;
import com.bbw.god.game.zxz.constant.ZxzConstant;
import com.bbw.god.game.zxz.entity.*;
import com.bbw.god.game.zxz.entity.foursaints.UserFourSaintsDefender;
import com.bbw.god.game.zxz.entity.foursaints.UserZxzFourSaintsCardGroupInfo;
import com.bbw.god.game.zxz.entity.foursaints.UserZxzFourSaintsInfo;
import com.bbw.god.game.zxz.entity.foursaints.ZxzFourSaints;
import com.bbw.god.game.zxz.enums.ZxzFourSaintsEnum;
import com.bbw.god.game.zxz.service.ZxzAnalysisService;
import com.bbw.god.game.zxz.service.ZxzRefreshService;
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
import java.util.*;
import java.util.stream.Collectors;

/**
 * 玩家诛仙阵 四圣挑战 service
 * @author: hzf
 * @create: 2022-12-27 18:07
 **/
@Service
public class UserZxzFourSaintsService {
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private UserCardService userCardService;
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
    private GameZxzFourSaintsService gameZxzFourSaintsService;
    @Autowired
    private UserZxzFourSaintsService zxzFourSaintsService;

    /**
     * 获取玩家四圣挑战（集合）
     *
     * @param uid
     * @return
     */
    public List<UserZxzFourSaintsInfo> getUserZxzFourSaints(long uid) {
        return gameUserService.getMultiItems(uid, UserZxzFourSaintsInfo.class);
    }

    /**
     * 获取四圣挑战卡组(集合)
     *
     * @param uid
     * @return
     */
    public List<UserZxzFourSaintsCardGroupInfo> getUserZxzFourSaintsCardGroups(long uid) {
        return gameUserService.getMultiItems(uid, UserZxzFourSaintsCardGroupInfo.class);
    }

    /**
     * 获取玩家四圣挑战（单条）
     *
     * @param uid
     * @param challengeType
     * @return
     */
    public UserZxzFourSaintsInfo getUserZxzFourSaints(long uid, Integer challengeType) {
        UserZxzFourSaintsInfo userZxzFourSaintsInfo = getUserZxzFourSaints(uid).stream()
                .filter(tmp -> tmp.getChallengeType().equals(challengeType))
                .findFirst().orElse(null);
        if (null == userZxzFourSaintsInfo) {
            return null;
        }
        boolean isNeedToUpdate = false;
        //判断是否刷新
        boolean isToRefresh = ZxzRefreshService.fourSaintsIsToRefresh(userZxzFourSaintsInfo.getLastRefreshDate());
        if (isToRefresh) {
            isNeedToUpdate = true;
            //刷新规则
            userZxzFourSaintsInfo.autoRefreshFourSaints();
        }

        boolean ifFreeRefreshDate = DateUtil.getDaysBetween(userZxzFourSaintsInfo.getFreeRefreshDate(), new Date()) > 0;
        if (ifFreeRefreshDate) {
            //每天刷新四圣挑战的免费次数
            userZxzFourSaintsInfo.autoFreeTime();
        }
        if (isNeedToUpdate) {
            gameUserService.updateItem(userZxzFourSaintsInfo);
            //刷新的时候重置掉麒麟卡组
            if (challengeType == ZxzFourSaintsEnum.CHALLENGE_TYPE_50.getChallengeType()) {
                UserZxzFourSaintsCardGroupInfo userZxzFourSaintsCardGroup = zxzFourSaintsService.getUserZxzFourSaintsCardGroup(uid, challengeType);
                gameUserService.deleteItem(userZxzFourSaintsCardGroup);
            }
        }
        return userZxzFourSaintsInfo;
    }

    /**
     * 解锁四圣挑战
     * @param uid
     * @param clearanceScore
     * @param difficulty
     * @return
     */
    public void unlockFourSaints(long uid,Integer clearanceScore, Integer difficulty) {


        List<CfgFourSaintsEntity.CfgFourSaintsChallenge> fourSaintsChallenges = CfgFourSaintsTool.getFourSaintsChallenge(clearanceScore, difficulty);
        if (ListUtil.isEmpty(fourSaintsChallenges) ) {
            return;
        }
        List<UserZxzFourSaintsInfo> userZxzFourSaintsInfos = new ArrayList<>();
        for (CfgFourSaintsEntity.CfgFourSaintsChallenge fourSaintsChallenge : fourSaintsChallenges) {
            int challengeType = fourSaintsChallenge.getChallengeType();

            UserZxzFourSaintsInfo userZxzFourSaints = getUserZxzFourSaints(uid,challengeType);
            if (null != userZxzFourSaints) {
                continue;
            }
            Integer cardId = getFourSaintsCard(challengeType);
            //判断玩家是否有该卡牌
            UserCard userCard = userCardService.getUserNormalCardOrDeifyCard(uid, cardId);
            if (null == userCard) {
                continue;
            }
            UserZxzFourSaintsInfo userZxzFourSaintsInfo = UserZxzFourSaintsInfo.instance(uid, challengeType);
            userZxzFourSaintsInfos.add(userZxzFourSaintsInfo);
        }
        gameUserService.addItems(userZxzFourSaintsInfos);
    }

    /**
     * 构建四圣兽卡牌
     * @param challengeType
     * @return
     */
    private Integer getFourSaintsCard(Integer challengeType){
        /** 挑战类型  ===> 卡牌id */
        Map<Integer,Integer> map = new HashMap<Integer,Integer>();
        map.put(10,142);
        map.put(20,236);
        map.put(30,347);
        map.put(40,401);
        return map.get(challengeType);
    }

    /**
     * 解锁四圣挑战之麒麟挑战
     * @param uid
     */
    public void unlockFourSaintsQiLin(long uid) {
        CfgFourSaintsEntity.CfgFourSaintsChallenge fourSaintsChallenge = CfgFourSaintsTool.getFourSaintsChallenge(ZxzFourSaintsEnum.CHALLENGE_TYPE_50.getChallengeType());
        if (null == fourSaintsChallenge) {
            return;
        }
        int challengeType = fourSaintsChallenge.getChallengeType();
        UserZxzFourSaintsInfo userZxzFourSaints = getUserZxzFourSaints(uid,challengeType);
        if (null != userZxzFourSaints) {
            return;
        }
        UserZxzFourSaintsInfo userZxzFourSaintsInfo = UserZxzFourSaintsInfo.instance(uid, challengeType);
        gameUserService.addItem(uid,userZxzFourSaintsInfo);
    }

    /**
     * 获取玩家关卡数据
     *
     * @param uid
     * @param challengeType
     * @param defenderId
     * @return
     */
    public UserFourSaintsDefender getUserFourSaintsDefender(long uid, Integer challengeType, Integer defenderId) {
        UserZxzFourSaintsInfo userZxzFourSaints = getUserZxzFourSaints(uid, challengeType);
        return userZxzFourSaints.getFourSaintsDefenders().stream()
                .filter(tmp -> tmp.getDefenderId().equals(defenderId))
                .findFirst().orElse(null);
    }

    /**
     * 判断是否可以解锁麒麟挑战
     * @param uid
     * @return
     */
    public boolean ifUnlockQiLin(long uid){
        UserZxzFourSaintsInfo fourSaintsQiLin = getUserZxzFourSaints(uid, ZxzFourSaintsEnum.CHALLENGE_TYPE_50.getChallengeType());
        //麒麟挑战已经解锁
        if (null != fourSaintsQiLin) {
            return false;
        }
        for (ZxzFourSaintsEnum fourSaints : ZxzFourSaintsEnum.values()) {
            //移除麒麟枚举
            if (fourSaints.getChallengeType() == ZxzFourSaintsEnum.CHALLENGE_TYPE_50.getChallengeType()) {
                continue;
            }
            UserZxzFourSaintsInfo userZxzFourSaints = getUserZxzFourSaints(uid, fourSaints.getChallengeType());
            //为空或者还没通关过
            if (null == userZxzFourSaints || !userZxzFourSaints.getIfClearance() ) {
                return false;
            }
        }
        return true;
    }



    /**
     * 获取四圣挑战卡组（单条）
     *
     * @param uid
     * @param challengeType
     * @return
     */
    public UserZxzFourSaintsCardGroupInfo getUserZxzFourSaintsCardGroup(long uid, Integer challengeType) {
        return getUserZxzFourSaintsCardGroups(uid)
                .stream().filter(tmp -> tmp.getChallengeType().equals(challengeType))
                .findFirst().orElse(null);
    }

    /**
     * 锁定符册
     *
     * @param uid
     * @param challengeType
     */
    public void checkAndLockFuCe(Long uid, Integer challengeType) {
        //获取玩家的卡组
        UserZxzFourSaintsCardGroupInfo userCardGroup = getUserZxzFourSaintsCardGroup(uid, challengeType);
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
     *
     * @param uid
     * @param challengeType
     */
    public void checkAndLockCardGroup(Long uid, Integer challengeType) {
        //获取玩家的卡组
        UserZxzFourSaintsCardGroupInfo userCardGroup = getUserZxzFourSaintsCardGroup(uid, challengeType);
        if (userCardGroup == null) {
            throw new ExceptionForClientTip("zxz.card.not");
        }
        CfgFourSaintsEntity.CfgFourSaintsChallenge fourSaintsChallenge = CfgFourSaintsTool.getFourSaintsChallenge(challengeType);
        //减少的卡牌等级
        Integer reduceCardLv = fourSaintsChallenge.getReduceCardLv();
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
            zxzUserLeaderCard = ZxzUserLeaderCard.instance(uLeaderCard, equipments, beasts);
        }

        //锁定卡组
        GameUser gameUser = gameUserService.getGameUser(uid);
        userCardGroup.lockCardGroup(gameUser, userZxzCards, zxzUserLeaderCard);
        gameUserService.updateItem(userCardGroup);

    }

    /**
     * 获取下一关
     *
     * @param defenderId
     * @return
     */
    public Integer nextDefendeId(Integer defenderId) {
        //当前第几关
        int parseInt = Integer.parseInt(String.valueOf(defenderId).substring(4, 5));
        if (parseInt == ZxzConstant.LAST_DEFENDER) {
            return 0;
        }
        return defenderId + 1;
    }

    public CPCardGroup getZxzFourSaintsCards(long uid, Long defenderId) {

        Integer challengeType = CfgFourSaintsTool.getChallengeType(defenderId.intValue());
        UserZxzFourSaintsCardGroupInfo userCardGroup = getUserZxzFourSaintsCardGroup(uid, challengeType);
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
        ZxzFourSaints zxzFourSaints = gameZxzFourSaintsService.getZxzFourSaints(challengeType);
        ZxzEntry zxzEntry = new ZxzEntry();
        List<CombatBuff> entryCombatBuffs = zxzEntry.gainEntryCombatBuff(zxzFourSaints.gainUserEntrys());
        combatBuffs.addAll(entryCombatBuffs);
        //构建战斗卡组
        CPCardGroup instance = CPCardGroup.getInstance(uid, cCardParams);
        instance.setHp(userCardGroup.getHp());
        instance.setBuffs(combatBuffs);
        return instance;
    }

    /**
     * 构建主角卡信息
     *
     * @param leaderCard
     * @return
     */
    public static CCardParam instanceZxzUserLeader(ZxzUserLeaderCard leaderCard) {
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
