package com.bbw.god.game.zxz.service.foursaints;

import com.bbw.common.ListUtil;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardService;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.card.CardEnum;
import com.bbw.god.game.zxz.cfg.ZxzTool;
import com.bbw.god.game.zxz.cfg.foursaints.CfgFourSaintsEntity;
import com.bbw.god.game.zxz.cfg.foursaints.CfgFourSaintsTool;
import com.bbw.god.game.zxz.entity.UserZxzCard;
import com.bbw.god.game.zxz.entity.ZxzFuTu;
import com.bbw.god.game.zxz.entity.ZxzUserLeaderCard;
import com.bbw.god.game.zxz.entity.foursaints.UserZxzFourSaintsCardGroupInfo;
import com.bbw.god.game.zxz.entity.foursaints.UserZxzFourSaintsInfo;
import com.bbw.god.game.zxz.entity.foursaints.ZxzFourSaints;
import com.bbw.god.game.zxz.entity.foursaints.ZxzFourSaintsInfo;
import com.bbw.god.game.zxz.enums.ZxzFourSaintsEnum;
import com.bbw.god.game.zxz.enums.ZxzStatusEnum;
import com.bbw.god.game.zxz.rd.RdUserCardGroup;
import com.bbw.god.game.zxz.rd.foursaints.*;
import com.bbw.god.game.zxz.service.ZxzAnalysisService;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.card.UserCard;
import com.bbw.god.gameuser.card.UserCardService;
import com.bbw.god.gameuser.leadercard.UserLeaderCard;
import com.bbw.god.gameuser.leadercard.beast.UserLeaderBeastService;
import com.bbw.god.gameuser.leadercard.equipment.UserLeaderEquimentService;
import com.bbw.god.gameuser.leadercard.equipment.UserLeaderEquipment;
import com.bbw.god.gameuser.leadercard.service.LeaderCardService;
import com.bbw.god.gameuser.yuxg.UserFuCe;
import com.bbw.god.rd.RDCommon;
import com.bbw.god.rd.RDSuccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 诛仙阵 四圣挑战 逻辑
 * @author: hzf
 * @create: 2022-12-27 18:00
 **/
@Service
public class ZxzFourSaintsLogic {
    @Autowired
    private UserCardService userCardService;
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private UserZxzFourSaintsService zxzFourSaintsService;
    @Autowired
    private LeaderCardService leaderCardService;
    @Autowired
    private UserLeaderEquimentService userLeaderEquimentService;
    @Autowired
    private UserLeaderBeastService userLeaderBeastService;
    @Autowired
    private GameZxzFourSaintsService gameZxzFourSaintsService;
    @Autowired
    private AwardService awardService;
    /**
     * 进入四圣挑战
     * @param uid
     * @return
     */
    public RdUserZxzFourSaints enterFourSaints(Long uid) {
        //判断是否还在维护中
        ZxzTool.ifMaintain();   
        //判断是否可以解锁麒麟挑战
        boolean ifUnlockQiLin = zxzFourSaintsService.ifUnlockQiLin(uid);
        if (ifUnlockQiLin) {
            zxzFourSaintsService.unlockFourSaintsQiLin(uid);
        }

        RdUserZxzFourSaints rd = new RdUserZxzFourSaints();
        List<RdUserZxzFourSaints.RdUserZxzFourSaint> rdUserZxzFourSaintArrayList = new ArrayList<>();
        for (ZxzFourSaintsEnum saintsEnum : ZxzFourSaintsEnum.values()) {
            CfgFourSaintsEntity.CfgEntryRandom entryRandom = CfgFourSaintsTool.getEntryRandom(saintsEnum.getChallengeType());
            //判断玩家是否有四圣挑战信息
            UserZxzFourSaintsInfo userZxzFourSaints = zxzFourSaintsService.getUserZxzFourSaints(uid, saintsEnum.getChallengeType());
            RdUserZxzFourSaints.RdUserZxzFourSaint fourSaint;
            if (null == userZxzFourSaints) {
                fourSaint = RdUserZxzFourSaints.RdUserZxzFourSaint.getInstance(saintsEnum.getChallengeType());
            } else {
                CfgFourSaintsEntity.CfgFourSaintsChallenge fourSaintsChallenge = CfgFourSaintsTool.getFourSaintsChallenge(saintsEnum.getChallengeType());
                fourSaint = RdUserZxzFourSaints.RdUserZxzFourSaint.getInstance(saintsEnum.getChallengeType(),
                        userZxzFourSaints.getExploratoryPoint(),
                        ZxzStatusEnum.ABLE_ATTACK.getStatus(), entryRandom.getLvStock()+fourSaintsChallenge.getLingCEntryLv(),
                        userZxzFourSaints.getAwarded(),
                        userZxzFourSaints.getFreeRefreshFrequency());
            }
            rdUserZxzFourSaintArrayList.add(fourSaint);
        }
        rd.setZxzFourSaints(rdUserZxzFourSaintArrayList);
        return rd;
    }

    /**
     * 解锁四圣难度
     * @param uid
     * @param clearanceScore
     * @param difficulty
     * @return
     */
    public RDSuccess unlockFourSaints(long uid, Integer clearanceScore,Integer difficulty) {
        zxzFourSaintsService.unlockFourSaints(uid, clearanceScore,difficulty);
        return new RDSuccess();
    }

    /**
     * 进入四圣挑战战斗前
     * @param challengeType
     * @return
     */
    public RdUserZxzFourSaintsDefender enterFourSaintsChallenge(Long uid, Integer challengeType) {
        UserZxzFourSaintsInfo userZxzFourSaints = zxzFourSaintsService.getUserZxzFourSaints(uid, challengeType);
        if (null == userZxzFourSaints) {
            throw new ExceptionForClientTip("zxz.four.Saints.challengeType.not.exist");
        }
        boolean ifAttack = userZxzFourSaints.ifAttack();
        if (!ifAttack) {
            //锁定符册
            zxzFourSaintsService.checkAndLockFuCe(uid,challengeType);
            //锁定 卡组
            zxzFourSaintsService.checkAndLockCardGroup(uid,challengeType);
        }
        //更新进入到四圣挑战
        UserZxzFourSaintsInfo zxzFourSaints = zxzFourSaintsService.getUserZxzFourSaints(uid, challengeType);
        zxzFourSaints.setInto(true);
        gameUserService.updateItem(zxzFourSaints);


        //获取用户卡组
        UserZxzFourSaintsCardGroupInfo userCardGroup = zxzFourSaintsService.getUserZxzFourSaintsCardGroup(uid, challengeType);
        String nickname = gameUserService.getGameUser(uid).getRoleInfo().getNickname();
        return RdUserZxzFourSaintsDefender.instance(userZxzFourSaints,userCardGroup,nickname);
    }

    /**
     * 进去四圣挑战区域
     * @param uid
     * @param challengeType
     * @return
     */
    public RdUserZxzFourSaintsRegion enterFourSaintsRegion(Long uid, Integer challengeType) {
        //获取四圣敌方配置
        ZxzFourSaintsInfo zxzFourSaintsInfo = gameZxzFourSaintsService.getZxzFourSaintsInfo();
        //获取四圣区域
        ZxzFourSaints zxzFourSaints = zxzFourSaintsInfo.gainZxzFourSaintsRegion(challengeType);
        //获取区域编组限制类型
        List<Integer> limitTypes = zxzFourSaints.getLimitTypes();
        UserZxzFourSaintsInfo userZxzFourSaints = zxzFourSaintsService.getUserZxzFourSaints(uid, challengeType);
        if (null == userZxzFourSaints) {
            throw new ExceptionForClientTip("zxz.four.Saints.challengeType.not.exist");
        }
        return RdUserZxzFourSaintsRegion.instance(userZxzFourSaints,limitTypes);
    }

    /**
     * 四圣挑战编辑卡组
     * @param uid
     * @param cardIds
     * @param challengeType
     * @return
     */
    public RDSuccess editFourSaintsCardGroup(Long uid, String cardIds, Integer challengeType) {
        //获取卡卡配ids
        List<Integer> cardIdList = ListUtil.parseStrToInts(cardIds);
        if (ListUtil.isEmpty(cardIdList)) {
            throw new ExceptionForClientTip("zxz.card.not");
        }
        UserZxzFourSaintsInfo userZxzFourSaints = zxzFourSaintsService.getUserZxzFourSaints(uid, challengeType);
        if (userZxzFourSaints.ifAttack()) {
            throw new ExceptionForClientTip("zxz.four.saints.challengeType.lock");
        }
        //获取玩家卡牌
        List<UserCard> userCards = userCardService.getUserCards(uid, cardIdList);

        //构建诛仙阵玩家卡牌
        List<UserZxzCard> userZxzCards = UserZxzCard.getInstance(userCards);


        //获取用户卡组
        UserZxzFourSaintsCardGroupInfo userCardGroup = zxzFourSaintsService.getUserZxzFourSaintsCardGroup(uid, challengeType);
        // 查看是否添加了主角卡
        ZxzUserLeaderCard zxzUserLeaderCard = null;
        if (cardIdList.contains(CardEnum.LEADER_CARD.getCardId())) {
            UserLeaderCard uLeaderCard = leaderCardService.getUserLeaderCard(uid);
            UserLeaderEquipment[] equipments = userLeaderEquimentService.getTakedEquipments(uid);
            int[] beasts = userLeaderBeastService.getTakedBeasts(uid);
            zxzUserLeaderCard = ZxzUserLeaderCard.instance(uLeaderCard,equipments,beasts);
        }
        RDSuccess rd = new RDSuccess();
        if (userCardGroup == null) {
            UserZxzFourSaintsCardGroupInfo userZxzCardGroup = UserZxzFourSaintsCardGroupInfo.getInstance(uid, challengeType, userZxzCards,0,zxzUserLeaderCard);
            gameUserService.addItem(uid,userZxzCardGroup);
            return rd;
        }
        userCardGroup.setCards(userZxzCards);
        userCardGroup.setZxzUserLeaderCard(zxzUserLeaderCard);
        gameUserService.updateItem(userCardGroup);
        return rd;
    }

    /**
     * 四圣挑战编辑符册
     * @param uid
     * @param challengeType
     * @param fuCeDataId
     * @return
     */
    public RDSuccess setFourSaintsFuCe(Long uid, Integer challengeType, long fuCeDataId) {
        RDSuccess rd = new RDSuccess();
        UserZxzFourSaintsInfo userZxzFourSaints = zxzFourSaintsService.getUserZxzFourSaints(uid, challengeType);
        if (userZxzFourSaints.ifAttack()) {
            throw new ExceptionForClientTip("zxz.four.saints.challengeType.lock");
        }
        //获取用户卡组
        UserZxzFourSaintsCardGroupInfo userCardGroup = zxzFourSaintsService.getUserZxzFourSaintsCardGroup(uid, challengeType);
        if (fuCeDataId == 0) {
            userCardGroup.setFuCeDataId(0);
            userCardGroup.setRunes(new ArrayList<>());
            gameUserService.updateItem(userCardGroup);
            return rd;
        }
        Optional<UserFuCe> userFuCe = gameUserService.getUserData(uid, fuCeDataId, UserFuCe.class);
        if (!userFuCe.isPresent()) {
            throw new ExceptionForClientTip("zxz.not.fuCe");
        }

        //如果当前卡组为空
        if (null == userCardGroup) {
            UserZxzFourSaintsCardGroupInfo userZxzCardGroupInfo = UserZxzFourSaintsCardGroupInfo.getInstance(uid,challengeType,fuCeDataId);
            gameUserService.addItem(uid,userZxzCardGroupInfo);
            return rd;
        }
        userCardGroup.setFuCeDataId(fuCeDataId);
        gameUserService.updateItem(userCardGroup);
        return rd;
    }

    /**
     * 四圣挑战查看卡组
     * @param uid
     * @param challengeType
     * @return
     */
    public RdUserCardGroup getFourSaintsCardGroup(Long uid, Integer challengeType) {
        UserZxzFourSaintsCardGroupInfo userCardGroup = zxzFourSaintsService.getUserZxzFourSaintsCardGroup(uid, challengeType);
        RdUserCardGroup rd = new RdUserCardGroup();
        List<Integer> cardIds = new ArrayList<>();
        if (userCardGroup != null) {
            rd.setFuCeDataId(userCardGroup.getFuCeDataId());
            cardIds = userCardGroup.getCards().stream().map(UserZxzCard::getCardId)
                    .collect(Collectors.toList());
            if (null != userCardGroup.getZxzUserLeaderCard()) {
                cardIds.add(CardEnum.LEADER_CARD.getCardId());
            }
            rd.setUserCardGroup(cardIds);
        }
        //获取区域信息
        UserZxzFourSaintsInfo userZxzFourSaints = zxzFourSaintsService.getUserZxzFourSaints(uid, challengeType);
        //判断区域是否被攻打
        if (userZxzFourSaints.ifAttack()) {
            List<ZxzFuTu> zxzFuTus = ZxzAnalysisService.gainRunes(userCardGroup.getRunes());
            rd.setFuTus(zxzFuTus);
        }
        return rd;
    }

    /**
     * 查看词条
     * @param challengeType
     * @return
     */
    public RdZxzFourSaintsEntry getFourSaintsEntry(Integer challengeType) {
        ZxzFourSaintsInfo zxzFourSaintsInfo = gameZxzFourSaintsService.getZxzFourSaintsInfo();
        ZxzFourSaints zxzFourSaints = zxzFourSaintsInfo.gainZxzFourSaintsRegion(challengeType);
        List<RdZxzFourSaintsEntry.RdFourSaintsEntry> rdFourSaintsEntries = RdZxzFourSaintsEntry.RdFourSaintsEntry.gainEntries(zxzFourSaints.gainUserEntrys());
        RdZxzFourSaintsEntry rd = new RdZxzFourSaintsEntry();
        rd.setFourSaintsEntrys(rdFourSaintsEntries);
        return rd;
    }

    /**
     * 四圣查看敌方配置
     * @param challengeType
     * @return
     */
    public RdFourSaintsEnemyRegion getFourSaintsEnemyRegion(Integer challengeType) {
        ZxzFourSaints zxzFourSaints = gameZxzFourSaintsService.getZxzFourSaints(challengeType);
       return RdFourSaintsEnemyRegion.getInstance(zxzFourSaints);
    }

    /**
     * 四圣挑战 开宝箱
     * @param uid
     * @param challengeType
     * @return
     */
    public RDCommon openFourSaintsBox(long uid, Integer challengeType) {
        CfgFourSaintsEntity.CfgFourSaintsAwad fourSaintsAwad = CfgFourSaintsTool.getFourSaintsAwad(challengeType);
        if (null == fourSaintsAwad) {
            throw new ExceptionForClientTip("zxz.four.Saints.challengeType.not.exist");
        }
        UserZxzFourSaintsInfo userZxzFourSaints = zxzFourSaintsService.getUserZxzFourSaints(uid, challengeType);
        if (null == userZxzFourSaints) {
            throw new ExceptionForClientTip("zxz.four.Saints.challengeType.not.exist");
        }
        if (userZxzFourSaints.ifAwarded()) {
            throw new ExceptionForClientTip("zxz.receive.box");
        }
        if (!userZxzFourSaints.ifUpToFourExploratoryPoint()) {
            throw new ExceptionForClientTip("zxz.four.saints.exploratoryPoint.Insufficient.four");
        }
        List<Award> awards = new ArrayList<>();
        RDCommon rd = new RDCommon();
        awards.add(fourSaintsAwad.getAward());
        awardService.fetchAward(uid,awards, WayEnum.ZXZ_FOUR_SAINTS_BOX_AWARD,WayEnum.ZXZ_FOUR_SAINTS_BOX_AWARD.getName(),rd);
        //更新宝箱已经领取
        userZxzFourSaints.receiveBox();
        gameUserService.updateItem(userZxzFourSaints);
        return rd;
    }


}
