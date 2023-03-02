package com.bbw.god.game.zxz.service;

import com.bbw.cache.UserCacheService;
import com.bbw.common.DateUtil;
import com.bbw.common.ListUtil;
import com.bbw.common.StrUtil;
import com.bbw.common.lock.redis.annotation.RedisLock;
import com.bbw.common.lock.redis.annotation.RedisLockParam;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardService;
import com.bbw.god.game.combat.runes.RunesEnum;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.card.CardEnum;
import com.bbw.god.game.zxz.cfg.CfgZxzLevel;
import com.bbw.god.game.zxz.cfg.ZxzTool;
import com.bbw.god.game.zxz.cfg.award.ZxzAwardTool;
import com.bbw.god.game.zxz.entity.*;
import com.bbw.god.game.zxz.enums.ZxzDefenderKindEnum;
import com.bbw.god.game.zxz.enums.ZxzStatusEnum;
import com.bbw.god.game.zxz.rank.ZxzRankService;
import com.bbw.god.game.zxz.rank.ZxzRanker;
import com.bbw.god.game.zxz.rd.*;
import com.bbw.god.gameuser.GameUser;
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
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


/**
 * 诛仙阵 逻辑
 * @author: hzf
 * @create: 2022-09-20 10:17
 **/
@Service
public class ZxzLogic {
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private UserCacheService userCacheService;
    @Autowired
    private ZxzService zxzService;
    @Autowired
    private ZxzEnemyService zxzEnemyService;
    @Autowired
    private InitUserZxzService initUserZxzService;
    @Autowired
    private UserCardService userCardService;
    @Autowired
    private AwardService awardService;
    @Autowired
    private ZxzRankService zxzRankService;
    @Autowired
    private LeaderCardService leaderCardService;
    @Autowired
    private UserLeaderEquimentService userLeaderEquimentService;
    @Autowired
    private UserLeaderBeastService userLeaderBeastService;
    /**
     * 进入诛仙阵
     * @return
     */
    public RdZxzLevel enter(Long uid) {
        //判断是否还在维护中
        ZxzTool.ifMaintain();
        //判断玩家是否可以进入诛仙阵
        initUserZxzService.checkZxzIsOpend(uid);
        //初始化玩家诛仙阵信息
        initUserZxzService.initUserZxz(uid);
        //获取玩家诛仙阵难度数据
        UserZxzInfo userZxzInfo = zxzService.getUserZxz(uid);
        //获取玩家难度数据
        List<UserZxzDifficulty> levelInfo = userZxzInfo.getDifficultyInfo();
        //获取玩家诛仙阵区域数据
        List<UserZxzRegionInfo> userRegions = gameUserService.getMultiItems(uid, UserZxzRegionInfo.class);
        RdZxzLevel rd = new RdZxzLevel();
        return rd.getInstance(levelInfo, userRegions, userZxzInfo.getEnterDifficulty());
    }
    /**
     * 进入难度
     * @param uid 用户Id
     * @param difficulty 难度
     * @param regionId 区域Id
     * @return
     */
    public RdZxzRegion enterLevel(long uid, Integer difficulty, Integer regionId) {
        //判断难度
        ZxzTool.ifDifficulty(difficulty);
        //判断区域
        ZxzTool.ifRegion(regionId);
       //更新进入的难度
        UserZxzInfo userZxzInfo = zxzService.getUserZxz(uid);
        userZxzInfo.setEnterDifficulty(difficulty);
        gameUserService.updateItem(userZxzInfo);

        //获取用户诛仙阵攻打记录
        UserZxzRegionInfo userRegion = zxzService.getUserZxzRegion(uid, regionId);
        if (null == userRegion) {
            throw new ExceptionForClientTip("zxz.region.no.exist");
        }
        //获取用户的难度信息
        UserZxzDifficulty userLevel = zxzService.getUserZxzLevel(uid, difficulty);
        //进入的区域
        Integer enterRegion = userLevel.getEnterRegion();
        return RdZxzRegion.getInstance(userRegion,enterRegion);
    }

    /**
     * 进入区域
     * @param uid
     * @param regionId 区域Id
     * @return
     */
    public RdZxzRegionDefender enterRegion(long uid, Integer regionId) {
        //判断区域
        ZxzTool.ifRegion(regionId);

        //获取用户诛仙阵攻打信息
        UserZxzRegionInfo userRegion = zxzService.getUserZxzRegion(uid, regionId);
        if (userRegion.computeRegionLv() < 1) {
            throw new ExceptionForClientTip("zxz.regionLv.greater.than.0");
        }

        boolean ifDifficutyAttack = userRegion.ifDifficutyRegionAttack();
        if (!ifDifficutyAttack) {
            //锁定符册
            zxzService.checkAndLockFuCe(uid,regionId);
            //锁定 卡组
            zxzService.checkAndLockCardGroup(uid,regionId);
        }
        //更新进入的区域
        userRegion.setInto(true);
        gameUserService.updateItem(userRegion);

        Integer difficulty = ZxzTool.getDifficulty(regionId);
        //更新是否进入到区域中
        UserZxzInfo userZxzInfo = zxzService.getUserZxz(uid);
        userZxzInfo.updateEnterRegion(difficulty,regionId);
        gameUserService.updateItem(userZxzInfo);

        //获取用户卡组
        UserZxzCardGroupInfo userCardGroup = zxzService.getUserCardGroup(uid, regionId);

        UserZxzDifficulty zxzDifficulty = zxzService.getUserZxzLevel(uid, difficulty);
        Integer allPassedAwarded = null;
        if (zxzService.ifRegionAllClearance(uid,difficulty) && !zxzDifficulty.ifAllPassedAwarded()) {
            //获取全通奖励领取详情
            allPassedAwarded  = zxzDifficulty.getAllPassedAwarded();
        }
        String nickname = gameUserService.getGameUser(uid).getRoleInfo().getNickname();
        return RdZxzRegionDefender.getInstance(userRegion,userCardGroup,allPassedAwarded,nickname);
    }

    /**
     * 诛仙阵：编辑用户卡组
     * @param uid
     * @param cardIds 卡牌id,卡牌id,卡牌id
     * @param regionId 区域id
     * @return
     */
    public RDSuccess editCardGroup(long uid, String cardIds, Integer regionId) {
        //判断区域
        ZxzTool.ifRegion(regionId);
        Integer difficulty = ZxzTool.getDifficulty(regionId);

        //获取卡卡配ids
        List<Integer> cardIdList = ListUtil.parseStrToInts(cardIds);
        if (ListUtil.isEmpty(cardIdList)) {
            throw new ExceptionForClientTip("zxz.card.not");
        }

        //获取玩家卡牌
        List<UserCard> userCards = userCardService.getUserCards(uid, cardIdList);
        //构建诛仙阵玩家卡牌
        List<UserZxzCard> userZxzCards = UserZxzCard.getInstance(userCards);


        //获取用户卡组
        UserZxzCardGroupInfo userCardGroup = zxzService.getUserCardGroup(uid, regionId);
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
            UserZxzCardGroupInfo userZxzCardGroup = UserZxzCardGroupInfo.getInstance(uid, difficulty, regionId, userZxzCards,0,zxzUserLeaderCard);
            userCacheService.addUserData(userZxzCardGroup);
            return rd;
        }
        userCardGroup.setCards(userZxzCards);
        userCardGroup.setZxzUserLeaderCard(zxzUserLeaderCard);
        gameUserService.updateItem(userCardGroup);
        return rd;
    }

    /**
     * 设置符册
     * @param uid
     * @param regionId
     * @param fuCeDataId 为空相当于卸载符册
     * @return
     */
    public RDSuccess setFuCe(Long uid, Integer regionId, long fuCeDataId) {

        //判断区域
        ZxzTool.ifRegion(regionId);

        Integer difficulty = ZxzTool.getDifficulty(regionId);

        RDSuccess rd = new RDSuccess();

        //获取用户卡组
        UserZxzCardGroupInfo userCardGroup = zxzService.getUserCardGroup(uid, regionId);
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
            UserZxzCardGroupInfo userZxzCardGroupInfo = UserZxzCardGroupInfo.getInstance(uid,difficulty, regionId,fuCeDataId);
            gameUserService.addItem(uid,userZxzCardGroupInfo);
            return rd;
        }
        userCardGroup.setFuCeDataId(fuCeDataId);
        gameUserService.updateItem(userCardGroup);
        return rd;
    }
    /**
     * 编辑词条
     * @param uid
     * @param entries 词条id@等级,词条id@等级,词条id@等级
     * @param regionId 区域Id
     * @param unEntries  卸载 词条id@等级,词条id@等级,词条id@等级
     * @return
     */
    public RdEntry editEntry(long uid, String entries, Integer regionId, String unEntries) {

        if (null != entries && null != unEntries && entries.equals(unEntries)) {
            throw new ExceptionForClientTip("zxz.assembly.equally");
        }
        //获取用户诛仙阵攻打信息
        UserZxzRegionInfo userZxzRegionInfo = zxzService.getUserZxzRegion(uid, regionId);
        if (null == userZxzRegionInfo) {
            throw new ExceptionForClientTip("zxz.region.no.exist");
        }
        //判断安装词条是否为空
        if (StringUtils.isNotBlank(entries)) {
            List<String> entryList = ListUtil.parseStrToStrs(entries);
            if (userZxzRegionInfo.getEntries().contains(entries)) {
                throw new ExceptionForClientTip("zxz.entry.is.have");
            }
            //添加词条
            userZxzRegionInfo.addEntries(entryList);
        }
        //判断卸载的词条是否为空
        if (StringUtils.isNotBlank(unEntries)) {
            List<String> unEntriesList = ListUtil.parseStrToStrs(unEntries);
            if (!userZxzRegionInfo.getEntries().contains(unEntries)) {
                throw new ExceptionForClientTip("zxz.entry.not.have");
            }
            //删除词条
            userZxzRegionInfo.delEntries(unEntriesList);
        }
        gameUserService.updateItem(userZxzRegionInfo);

        Integer regionLv = userZxzRegionInfo.computeRegionLv();
        RdEntry rd = new RdEntry();
        rd.setRegionLv(regionLv);
        return rd;
    }

    /**
     * 扫荡
     * @param uid
     * @param difficulty 难度类型
     * @return
     */
    public RDCommon mopUp(long uid, Integer difficulty) {
        //判断难度
        ZxzTool.ifDifficulty(difficulty);
        UserZxzDifficulty userZxzLevel = zxzService.getUserZxzLevel(uid, difficulty);
        //判断该难度当前有没有在攻略且通过评分大于0
        if (!userZxzLevel.ifClearanceScore() ||
                zxzService.ifDifficutyAttack(uid, difficulty)) {
            throw new ExceptionForClientTip("zxz.not.mopUp");
        }

        //获取难度数据
        UserZxzInfo userZxz = zxzService.getUserZxz(uid);
        //获取对应的难度数据
        UserZxzDifficulty userZxzDifficulty = userZxz.gainUserZxzLevel(difficulty);
        //获取通关数
        Integer clearanceNum = userZxzDifficulty.gainClearanceNum();
        //获取基准等级
        Integer referenceLv = userZxzDifficulty.gainReferenceLv();
        List<Award> awards = new ArrayList<>();
        List<UserZxzRegionInfo> userZxzRegions = zxzService.getUserZxzRegions(uid, difficulty);
        //野怪掉落奖励
        for (UserZxzRegionInfo userZxzRegion : userZxzRegions) {

            //获取野怪数据
            for (UserZxzRegionDefender defender : userZxzRegion.getRegionDefenders()) {
                //关卡奖励
                List<Award> winAwards = ZxzAwardTool.getDefenderWinAwards(difficulty, defender.getKind(), referenceLv);
                awards.addAll(winAwards);
                //精英宝箱
                if (defender.ifKingElite() && defender.ifReceiveBox()) {
                    List<Award> eliteBoxAward = ZxzAwardTool.getDefenderBoxAwards(uid,difficulty, ZxzDefenderKindEnum.KIND_20.getKind(), referenceLv);
                    awards.addAll(eliteBoxAward);
                    defender.receiveBoxAwarded();
                }
                //首领宝箱
                if (defender.ifKingChief() && defender.ifReceiveBox()) {
                    List<Award> chiefBoxAward = ZxzAwardTool.getDefenderBoxAwards(uid,difficulty, ZxzDefenderKindEnum.KIND_30.getKind(), referenceLv);
                    awards.addAll(chiefBoxAward);
                    defender.receiveBoxAwarded();
                }
                //扫荡 修改数据
                userZxzRegion.mopUp();
            }
        }
        gameUserService.updateItems(userZxzRegions);
       //判断全通奖励是否被领取
        if (!userZxzDifficulty.ifAllPassedAwarded()) {
            //获取区域等级和
            Integer referenceLvs = userZxzDifficulty.gainReferenceLvs();
            //全通奖励
            List<Award> allAward = ZxzAwardTool.getAllPassAward(difficulty, referenceLvs,clearanceNum);
            awards.addAll(allAward);
            //更新领取状态
            userZxzDifficulty.receiveAllPassedAwarded();
        }
        //更改通关状态
        userZxzDifficulty.setStatus(ZxzStatusEnum.PASSED.getStatus());
        gameUserService.updateItem(userZxz);

        RDCommon rdAward = new RDCommon();
        //掉落奖励
        awardService.sendNeedMergedAwards(uid,awards, WayEnum.ZXZ_MOPUP_DROP_AWARD,WayEnum.ZXZ_MOPUP_DROP_AWARD.getName(),rdAward);
        return rdAward;
    }

    /**
     * 敌方配置
     * @param regionId 区域Id
     * @return
     */
    public RdEnemyRegion enemyRegion(Integer regionId) {
        //判断区域
        ZxzTool.ifRegion(regionId);

        ZxzRegion zxzRegion = zxzEnemyService.getZxzRegion(regionId);
        return RdEnemyRegion.getInstance(zxzRegion);
    }

    /**
     * 查看词条
     * @param uid
     * @param difficulty
     * @return
     */
    public RdZxzEntry getEntry(long uid, Integer difficulty) {
        //判断难度
        ZxzTool.ifDifficulty(difficulty);

        CfgZxzLevel cfgZxzLevel = ZxzTool.getZxzLevel(difficulty);
        List<Integer> entryGears = new ArrayList<>();
        List<RdZxzEntry.RdEntry> entryList = new ArrayList<>();
        //获取用户词条
        UserEntryInfo uEntry = zxzService.getUserEntry(uid, difficulty);
        if (null == uEntry) {
            entryGears = cfgZxzLevel.getEntryGears();
            UserEntryInfo userEntryInfo = UserEntryInfo.instance(uid, difficulty, entryGears);
            gameUserService.addItem(uid,userEntryInfo);
        } else {

            List<UserEntryInfo.UserEntry> userEntries = ZxzAnalysisService.gainUserEntrys(uEntry.getUserEntry());
            entryList = RdZxzEntry.RdEntry.getInstances(userEntries);

            List<Integer> entryGearsDistinct = uEntry.getEntryGears().stream().distinct().collect(Collectors.toList());
            entryGears = entryGearsDistinct;

        }

        RdZxzEntry rd = new RdZxzEntry();
        //词条的初始等级
        rd.setEntryInitLv(cfgZxzLevel.getEntryInitLv());
        //初始档位
        rd.setEntryGears(entryGears);
        rd.setEntrys(entryList);
        return rd;
    }


    /**
     * 查看用户卡组
     * @param uid
     * @param regionId
     * @return
     */
    public RdUserCardGroup getUserCardGroup(long uid, Integer regionId) {

        //判断区域
        ZxzTool.ifRegion(regionId);

        UserZxzCardGroupInfo userCardGroup = zxzService.getUserCardGroup(uid, regionId);
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
        UserZxzRegionInfo userZxzRegion = zxzService.getUserZxzRegion(uid, regionId);
        //判断区域是否被攻打
        if (userZxzRegion.ifDifficutyRegionAttack()) {
            List<ZxzFuTu> zxzFuTus = ZxzAnalysisService.gainRunes(userCardGroup.getRunes());
            rd.setFuTus(zxzFuTus);
        }
        return rd;
    }

    public RdUserRankCardGroup getUserRankCardGroup(long uid, Integer regionId, Integer beginDate) {
        UserPassRegionCardGroupInfo userPassRegionCardGroup = null;
        if (null == beginDate) {
            userPassRegionCardGroup  = zxzService.getUserPassRegionCardGroup(uid,regionId);
        } else {
            userPassRegionCardGroup  = zxzService.getUserPassRegionCardGroup(uid,regionId,beginDate);
        }
        return RdUserRankCardGroup.getInstance(userPassRegionCardGroup,regionId);
    }

    /**
     * 开宝箱
     * @param uid
     * @param defenderId
     * @return
     */
    public RDCommon openBox(long uid, Integer defenderId) {
        //获取关卡信息
        UserZxzRegionDefender regionDefender = zxzService.getUserZxzRegionDefender(uid, defenderId);
        //判断是不是精英或者首领关卡
        if (!regionDefender.ifKingChief() && !regionDefender.ifKingElite()) {
            throw new ExceptionForClientTip("zxz.not.chiefOrElite");
        }
        if (!regionDefender.getStatus().equals(ZxzStatusEnum.PASSED.getStatus())) {
            throw new ExceptionForClientTip("zxz.not.passed");
        }
        //判断宝箱是否被领取
        if (!regionDefender.ifReceiveBox()) {
            throw new ExceptionForClientTip("zxz.receive.box");
        }
        Integer regionId = ZxzTool.getRegionId(defenderId);
        Integer difficulty = ZxzTool.getDifficulty(regionId);

        UserZxzRegionInfo userZxzRegion = zxzService.getUserZxzRegion(uid, regionId);
        //获取区域等级
        Integer regionLv = userZxzRegion.computeRegionLv();
        //获取宝箱奖励
        List<Award> awardList = ZxzAwardTool.getDefenderBoxAwards(uid, difficulty, regionDefender.getKind(), regionLv);
        RDCommon rd = new RDCommon();
        awardService.sendNeedMergedAwards(uid,awardList, WayEnum.ZXZ_BOX_AWARD,WayEnum.ZXZ_BOX_AWARD.getName(),rd);
        //更新领取状态
        userZxzRegion.receiveDefenderBox(defenderId);
        gameUserService.updateItem(userZxzRegion);
        return rd;
    }

    /**
     * 领取全通宝箱
     * @param uid
     * @param difficulty
     * @return
     */
    public RDCommon openDifficultyPassBox(long uid, Integer difficulty) {
        //获取难度
        UserZxzInfo userZxz = zxzService.getUserZxz(uid);
        UserZxzDifficulty userZxzDifficulty = userZxz.gainUserZxzLevel(difficulty);
        //判断全通宝箱是否可以领取
        if (userZxzDifficulty.ifAllPassedAwarded()) {
            throw new ExceptionForClientTip("zxz.receive.box");
        }
        Integer regionLvs = zxzService.gainRegionLvs(uid, difficulty);

        //全通宝箱
        List<Award> passDifficultyAward = ZxzAwardTool.getAllPassAward(difficulty, regionLvs);

        //首次开启简单难度全通宝箱时，将额外获得凡品炼制图纸
        if (userZxz.getFirstClearance()) {
            List<Award> firstClearanceAward = ZxzAwardTool.getFirstClearanceAward();
            passDifficultyAward.addAll(firstClearanceAward);
            userZxz.setFirstClearance(false);
        }

        RDCommon rd = new RDCommon();
        //发送宝箱
        awardService.sendNeedMergedAwards(uid, passDifficultyAward, WayEnum.ZXZ_ALL_PASS_AWARD, WayEnum.ZXZ_ALL_PASS_AWARD.getName(), rd);

        //更新状态
        userZxzDifficulty.receiveAllPassedAwarded();
        gameUserService.updateItem(userZxz);
        return rd;
    }

    /**
     * 获取区域榜单
     * @param uid
     * @param difficulty
     * @param regionId
     * @param beginDate 开始时间
     * @param startRank
     * @param endRank
     * @return
     */
    public RdZxzRank getZxzRank(long uid, Integer difficulty, Integer regionId, Integer beginDate, Integer startRank, Integer endRank) {
        //判断难度
        ZxzTool.ifDifficulty(difficulty);
        //判断区域
        ZxzTool.ifRegion(regionId);

        if (startRank == null) {
            startRank = 1;
        }
        if (endRank == null) {
            endRank = ZxzTool.getCfg().getRankLimit();
        }
        ZxzInfo zxzInfo = null;
        if (beginDate == null) {
            zxzInfo = zxzEnemyService.getZxzInfo();
            //将时间戳转化成yyyymmdd
            int beginTime = DateUtil.toDateInt(new Date(zxzInfo.getGenerateTime()));
            beginDate = beginTime;
        } else {
            zxzInfo = zxzEnemyService.getZxzInfo(beginDate);
        }
        //判断诛仙阵数据是否有效无效返回空榜单
        if (null == zxzInfo) {
            return new RdZxzRank();
        }

        //获取区服组
        int serverGroupId = gameUserService.getActiveGid(uid);
        List<ZxzRanker> rankers = zxzRankService.getRankers(serverGroupId, difficulty, regionId, beginDate, startRank, endRank);
        List<RdZxzRank.RdRank> rdRanks = new ArrayList<>();
        for (ZxzRanker ranker : rankers) {
            GameUser gameUser = gameUserService.getGameUser(ranker.getUid());
            RdZxzRank.RdRank rdRank = RdZxzRank.RdRank.getInstance(gameUser, ranker);
            rdRanks.add(rdRank);
        }

        List<ZxzInfo> zxzInfos = zxzEnemyService.getZxzInfos();
        List<String> times = RdZxzRank.gainRankTime(zxzInfos);

        return RdZxzRank.getInstance(rdRanks,times,zxzInfo.gainSplitTime());
    }


    /**
     * 获取当前难度诅咒效果
     *
     * @param uid
     * @param difficulty
     * @return
     */
    public RdZxzZuZhou getZuZhou(long uid, Integer difficulty) {
        RdZxzZuZhou rdZxzZuZhou = new RdZxzZuZhou();
        List<RdZxzZuZhou.RdRegionZuZhou> rdRegionZuZhous = new ArrayList<>();
        ZxzDifficulty zxzLevel = zxzEnemyService.getZxzLevel(difficulty);
        for (ZxzRegion region : zxzLevel.getRegions()) {
            List<ZxzEntry> entries = region.gainEntrys();
            List<ZxzEntry> entrieList = new ArrayList<>();
            for (ZxzEntry entry : entries) {
                if (entry.getEntryId() == RunesEnum.CHANG_SHENG_ENTRY.getRunesId()) {
                    continue;
                }
                if (entry.getEntryId() == RunesEnum.KUANG_BAO_ERTRY.getRunesId()) {
                    entry.setEntryLv(1);
                }
                entrieList.add(entry);
            }
            RdZxzZuZhou.RdRegionZuZhou rdRegionZuZhou = RdZxzZuZhou.getInstance(entrieList, region.getRegionId());
            rdRegionZuZhous.add(rdRegionZuZhou);
        }
        rdZxzZuZhou.setRegionZuZhous(rdRegionZuZhous);
        return rdZxzZuZhou;
    }
}
