package com.bbw.god.city.mixd.nightmare;

import com.alibaba.fastjson.JSON;
import com.bbw.App;
import com.bbw.common.DateUtil;
import com.bbw.common.LM;
import com.bbw.common.ListUtil;
import com.bbw.common.PowerRandom;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.city.mixd.event.EPPassTier;
import com.bbw.god.city.mixd.event.MiXDEventPublisher;
import com.bbw.god.city.mixd.nightmare.pos.AbstractMiXianFightProcessor;
import com.bbw.god.city.mixd.nightmare.pos.AbstractMiXianPosProcessor;
import com.bbw.god.city.mixd.nightmare.pos.CengZhuProcessor;
import com.bbw.god.city.mixd.nightmare.pos.EmptyProcessor;
import com.bbw.god.event.BaseEventParam;
import com.bbw.god.fight.FightSubmitParam;
import com.bbw.god.fight.RDFightResult;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.game.award.AwardService;
import com.bbw.god.game.combat.data.param.CCardParam;
import com.bbw.god.game.combat.event.CombatEventPublisher;
import com.bbw.god.game.combat.event.EPCombatAchievement;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.server.ServerTool;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.mail.MailService;
import com.bbw.god.gameuser.res.ResChecker;
import com.bbw.god.gameuser.res.ResEventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 说明：
 * 梦魇迷仙洞逻辑
 *
 * @author lwb
 * date 2021-05-26
 */
@Slf4j
@Service
public class NightmareMiXianLogic {
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private NightmareMiXianService nightmareMiXianService;
    @Autowired
    private List<AbstractMiXianPosProcessor> posServiceList;
    @Autowired
    private List<AbstractMiXianFightProcessor> fightProcessors;
    @Autowired
    private EmptyProcessor emptyService;
    @Autowired
    private AwardService awardService;
    @Autowired
    private CengZhuProcessor cengZhuProcessor;
    @Autowired
    private App app;
    @Autowired
    private MailService mailService;
    /**
     * 层主的位置
     */
    private static final List<Integer> cengZhuPos = Arrays.asList(12, 13, 17, 18);
    /**
     * 守卫的位置
     */
    private static final List<Integer> shouWeiPos = Arrays.asList(28, 29, 33, 34);

    /**
     * 进入迷仙洞:
     * 掉线重连=》恢复数据
     * 失败或者手动退出=》初始化信息
     *
     * @param uid
     * @return
     */
    public RDNightmareMxd intoMxd(long uid) {
        UserNightmareMiXian nightmareMiXian = nightmareMiXianService.getAndCreateUserNightmareMiXian(uid);
        // 增加可挑战层数
        nightmareMiXianService.incChallengeLayers(nightmareMiXian);
        if (nightmareMiXian.ifFail()) {
            nightmareMiXian.setContinuePassLevel(1);
            initNewLevel(nightmareMiXian, nightmareMiXian.getNextInitLevel());
        } else if (nightmareMiXian.getFightingType() == NightmareMiXianPosEnum.LEVEL_LEADER.getType() || nightmareMiXian.getFightingType() == NightmareMiXianPosEnum.XUN_SHI_ZHU_LONG.getType()) {
            //去除格子
            for (MiXianLevelData.PosData posData : nightmareMiXian.getLevelData().getPosDatas()) {
                if (posData.getTye().equals(nightmareMiXian.fightingType)) {
                    posData.setTye(NightmareMiXianPosEnum.EMPTY.getType());
                }
            }
            gameUserService.updateItem(nightmareMiXian);
        }
        // 检测是否需要更新每日元宝累计信息
        isInitDailyGoldAndTime(nightmareMiXian);
        RDNightmareMxd rd = new RDNightmareMxd();
        getCurrentMxdData(nightmareMiXian, rd);
        return rd;
    }

    /**
     * 进入迷仙洞宝库
     *
     * @param uid
     * @return
     */
    public RDNightmareMxd intoMxdTreasureHouse(long uid) {
        UserNightmareMiXian nightmareMiXian = nightmareMiXianService.getAndCreateUserNightmareMiXian(uid);
        if (nightmareMiXian.ifFail()) {
            throw new ExceptionForClientTip("mxd.not.blood");
        }
        RDNightmareMxd rd = new RDNightmareMxd();
        nightmareMiXian.setInTreasureHouse(true);
        gameUserService.updateItem(nightmareMiXian);
        getCurrentMxdData(nightmareMiXian, rd);
        return rd;
    }

    /**
     * 获取当前迷仙洞数据
     *
     * @param nightmareMiXian
     * @param rd
     */
    private void getCurrentMxdData(UserNightmareMiXian nightmareMiXian, RDNightmareMxd rd) {
        rd.addLevelData(nightmareMiXian);
        rd.setInTreasureHouse(nightmareMiXian.isInTreasureHouse() ? 1 : 0);
        rd.setBlood(nightmareMiXian.getBlood());
        rd.setPos(nightmareMiXian.getPos());
        rd.setBag(nightmareMiXian.getBag());
        rd.setCurrentLevel(nightmareMiXian.getCurrentLevel());

        Integer reLayers = nightmareMiXian.getRemainChallengeLayers();
        Integer maxLayers = nightmareMiXian.getMaxChallengeLayers();
        rd.setRemainChallengeLayers(reLayers);
        // 处理客户端挑战倒计时 （下一次恢复时间 - 当前时间）
        long time = nightmareMiXian.getLayersNextIncTime().getTime() - DateUtil.now().getTime();
        rd.setLayersLastIncTime(time);
        if (reLayers.equals(maxLayers)){
            // 剩余挑战层数 等于 最大挑战层数（与客户端约定值：0L）
            rd.setLayersLastIncTime(0L);
        }
    }

    /**
     * 玩家进入迷仙洞新层
     *
     * @param userNightmareMiXian 玩家数据
     * @param targetLevel         目标层级
     */
    public void initNewLevel(UserNightmareMiXian userNightmareMiXian, int targetLevel) {
        MiXianLevelData levelData = MiXianLevelData.getInstance();
        if (targetLevel == 10 || targetLevel == 20 || targetLevel == 30) {
            initSpecialLevel(targetLevel, levelData);
        } else {
            initNormalLevel(userNightmareMiXian, targetLevel, levelData);
        }
        userNightmareMiXian.setCardGroup(new ArrayList<>());
        userNightmareMiXian.setLevelData(levelData);
        userNightmareMiXian.intoNewLevel();
        userNightmareMiXian.setToFail(false);
        userNightmareMiXian.setInTreasureHouse(false);
        userNightmareMiXian.setTreasureHouseData(null);
        userNightmareMiXian.setBeInjured(false);
        userNightmareMiXian.setFindYbBox(false);
        gameUserService.updateItem(userNightmareMiXian);
    }

    /**
     * 初始化普通层数据
     *
     * @param userNightmareMiXian
     * @param targetLevel
     */
    public void initNormalLevel(UserNightmareMiXian userNightmareMiXian, int targetLevel, MiXianLevelData levelData) {
        List<NightmareMiXianPosEnum> buildPos = new ArrayList<>(50);
        List<CfgNightmareMiXian.LevelData> levelDataList = NightmareMiXianTool.getLevelData(targetLevel);
        //根据配置文件生成
        for (CfgNightmareMiXian.LevelData data : levelDataList) {
            int buildNum = PowerRandom.getRandomBetween(data.getLeast(), data.getMost());
            NightmareMiXianPosEnum posEnum = data.getMiXianPosEnum();
            for (int i = 0; i < buildNum; i++) {
                //21层开始出现，每格巡使格在生成时有20%概率生成头领
                if (targetLevel > 20 && posEnum.equals(NightmareMiXianPosEnum.XUN_SHI_XD) && PowerRandom.hitProbability(20)) {
                    buildPos.add(NightmareMiXianPosEnum.XUN_SHI_LEADER);
                } else {
                    buildPos.add(posEnum);
                }
            }
        }
        //尝试生成宝库
        if (app.runAsDevFZJ() || (userNightmareMiXian.getTreasureHousePsb() > 0 && PowerRandom.hitProbability(userNightmareMiXian.getTreasureHousePsb(), 10000))) {
            //生成宝库
            buildPos.add(NightmareMiXianPosEnum.TREASURE_HOUSE);
            userNightmareMiXian.setTreasureHousePsb(0);
        }
        //从11层开始，每层有5%概率将1格空白格替换为熔炉格。
        if (targetLevel > 10 && (PowerRandom.hitProbability(5) || app.runAsDevFZJ())) {
            buildPos.add(NightmareMiXianPosEnum.FURNACE);
        }
        buildPos.add(NightmareMiXianPosEnum.PLAYER);
        //补充空白格子
        int max = 50 - buildPos.size();
        for (int i = 0; i < max; i++) {
            buildPos.add(NightmareMiXianPosEnum.EMPTY);
        }
        //打乱位置
        PowerRandom.shuffle(buildPos);
        int index = buildPos.indexOf(NightmareMiXianPosEnum.PLAYER);
        levelData.setPos(index + 1);
        levelData.joinPosData(buildPos, false);
        levelData.setLevel(targetLevel);
    }

    /**
     * 特殊层：10，20，30 层 初始化只有  守卫、层主、玩家、大门
     *
     * @param levelData
     * @param targetLevel
     */
    public void initSpecialLevel(int targetLevel, MiXianLevelData levelData) {
        List<NightmareMiXianPosEnum> buildPos = new ArrayList<>(50);
        int gatePos = 5;
        int playerPos = PowerRandom.getRandomBetween(46, 50);
        for (int index = 1; index <= 50; index++) {
            if (cengZhuPos.contains(index)) {
                buildPos.add(NightmareMiXianPosEnum.LEVEL_LEADER);
            } else if (shouWeiPos.contains(index)) {
                buildPos.add(NightmareMiXianPosEnum.SHOU_WEI);
            } else if (gatePos == index) {
                buildPos.add(NightmareMiXianPosEnum.GATE);
            } else {
                buildPos.add(NightmareMiXianPosEnum.EMPTY);
            }
        }
        levelData.setPos(playerPos);
        levelData.joinPosData(buildPos, true);
        levelData.setLevel(targetLevel);
    }

    /**
     * 到达非空白位置
     *
     * @param uid
     * @param pos
     * @param passPath 已走过的路线
     */
    public RDNightmareMxd touchPos(long uid, int pos, String passPath) {
        UserNightmareMiXian nightmareMiXian = nightmareMiXianService.getAndCreateUserNightmareMiXian(uid);
        MiXianLevelData.PosData postData = nightmareMiXian.getPostData(pos);
        NightmareMiXianPosEnum type = NightmareMiXianPosEnum.fromType(postData.getTye());
        nightmareMiXian.updatePosData(pos, passPath);
        RDNightmareMxd rd = new RDNightmareMxd();
        rd.setCurrentPosType(postData.getTye());
        Optional<AbstractMiXianPosProcessor> posServiceOp = posServiceList.stream().filter(p -> p.match(type)).findFirst();
        AbstractMiXianPosProcessor posService = emptyService;
        if (posServiceOp.isPresent()) {
            posService = posServiceOp.get();
        }
        posService.touchPos(nightmareMiXian, rd, postData);
        gameUserService.updateItem(nightmareMiXian);
        rd.setBlood(nightmareMiXian.getBlood());
        return rd;
    }

    /**
     * 获取玩家的卡牌
     *
     * @param uid
     * @param aiId
     * @return
     */
    public List<CCardParam> getUserCardGroup(long uid, Long aiId) {
        List<CCardParam> cardParams = new ArrayList<>();
        NightmareMiXianPosEnum type = NightmareMiXianTool.getTypeByAiId(aiId);
        for (AbstractMiXianFightProcessor processor : fightProcessors) {
            if (processor.match(type)) {
                cardParams = processor.buildUserFightCardGroup(uid);
            }
        }
        return cardParams;
    }

    /**
     * 获取AI的卡牌
     *
     * @param uid
     * @param aiId
     * @return
     */
    public MiXianEnemy getEnemyById(long uid, Long aiId) {
        NightmareMiXianPosEnum type = NightmareMiXianTool.getTypeByAiId(aiId);
        if (NightmareMiXianPosEnum.LEVEL_LEADER.equals(type)) {
            int gid = gameUserService.getActiveGid(uid);
            Optional<MiXianEnemy> owner = cengZhuProcessor.getLevelOwner(gid, NightmareMiXianTool.getLevelByAiId(aiId), new Date());
            if (owner.isPresent()) {
                return owner.get();
            }
        }
        UserNightmareMiXianEnemy miXianEnemy = nightmareMiXianService.getAndCreateUserNightmareMiXianEnemy(uid);
        Optional<MiXianEnemy> optional = miXianEnemy.getMiXianEnemies().stream().filter(p -> p.getEnemyId().equals(aiId)).findFirst();
        if (optional.isPresent()) {
            return optional.get();
        }
        throw new ExceptionForClientTip("mxd.ai.done");
    }

    /**
     * 战斗发起之前
     *
     * @param uid
     * @param aiId
     */
    public void fightBefore(long uid, long aiId) {
        NightmareMiXianPosEnum type = NightmareMiXianTool.getTypeByAiId(aiId);
        for (AbstractMiXianFightProcessor processor : fightProcessors) {
            if (processor.match(type)) {
                processor.fightBefore(uid, type);
            }
        }
    }

    /**
     * 发放战斗胜利奖励
     *
     * @param uid
     * @param aiId
     * @param rd
     */
    public void handleFightAward(long uid, long aiId, RDFightResult rd) {
        NightmareMiXianPosEnum type = NightmareMiXianTool.getTypeByAiId(aiId);
        for (AbstractMiXianFightProcessor processor : fightProcessors) {
            if (processor.match(type)) {
                processor.handleFightAward(uid, aiId, rd);
            }
        }
        UserNightmareMiXian miXian = nightmareMiXianService.getAndCreateUserNightmareMiXian(uid);
        miXian.setFightingType(0);
        gameUserService.updateItem(miXian);
    }

    /**
     * 战斗失败
     *
     * @param uid
     * @param param
     * @param rd
     */
    public void handleFightFail(long uid, FightSubmitParam param, RDFightResult rd) {
        NightmareMiXianPosEnum type = NightmareMiXianTool.getTypeByAiId(param.getOpponentId());
        for (AbstractMiXianFightProcessor processor : fightProcessors) {
            if (processor.match(type)) {
                processor.fail(uid, param, rd);
            }
        }
        UserNightmareMiXian miXian = nightmareMiXianService.getAndCreateUserNightmareMiXian(uid);
        miXian.setFightingType(0);
        if (!type.equals(NightmareMiXianPosEnum.LEVEL_LEADER)) {
            miXian.setBeInjured(true);
        }
        gameUserService.updateItem(miXian);
    }

    /**
     * 生成巡使头领
     * 头领将从21层开始出现，每格巡使格在生成时有20%概率生成头领，每层至少拥有1格头领。
     *
     * @return
     */
    public boolean isBuildXunShiLeader(int level, int hasXunShiLeader) {
        if (level <= 20) {
            return false;
        }
        if (hasXunShiLeader == 0) {
            return true;
        }
        return PowerRandom.hitProbability(20);
    }

    /**
     * 保存战斗卡组
     *
     * @param uid
     * @param cards
     * @return
     */
    public RDNightmareMxd saveFightCards(long uid, List<Integer> cards) {
        UserNightmareMiXian nightmareMiXian = nightmareMiXianService.getAndCreateUserNightmareMiXian(uid);
        nightmareMiXian.setCardGroup(cards);
        gameUserService.updateItem(nightmareMiXian);
        return new RDNightmareMxd();
    }

    /**
     * 进入下一层、宝库传送回迷仙洞
     *
     * @param uid
     * @return
     */
    public RDNightmareMxd nextLevel(long uid) {
        RDNightmareMxd rd = new RDNightmareMxd();
        UserNightmareMiXian nightmareMiXian = nightmareMiXianService.getAndCreateUserNightmareMiXian(uid);
        if (nightmareMiXian.ifFail()) {
            //没有生命值
            throw new ExceptionForClientTip("mxd.not.blood");
        }
        if (nightmareMiXian.isInTreasureHouse()) {
            if (nightmareMiXian.getTreasureHouseData() != null) {
                int awardNum = nightmareMiXian.getTreasureHouseData().gainTreasureHourseAwardNum();
                if (awardNum != 0) {
                    EPCombatAchievement ep = EPCombatAchievement.instance(new BaseEventParam(uid), 15420);
                    CombatEventPublisher.pubCombatAchievement(ep);
                } else {
                    nightmareMiXian.setGainTreasureHourse(true);
                }
            }
            //传送回迷仙洞
            nightmareMiXian.setInTreasureHouse(false);
            nightmareMiXian.takeCurrentPosToEmptyType();
            nightmareMiXian.setTreasureHouseData(null);
        } else {
            //进入下一层
            if (nightmareMiXian.getCurrentLevel() == 30) {
                throw new ExceptionForClientTip("mxd.max.level");
            }
            int continuePassLevel = nightmareMiXian.getContinuePassLevel() + 1;
            int need = Math.min(35, continuePassLevel * 5);
            ResChecker.checkDice(gameUserService.getGameUser(uid), need);
            ResEventPublisher.pubDiceDeductEvent(uid, need, WayEnum.MXD_PASS_LEVEL, rd);
            nightmareMiXian = toPassLevel(uid, rd);
            initNewLevel(nightmareMiXian, nightmareMiXian.getNextInitLevel());
            nightmareMiXian.setContinuePassLevel(continuePassLevel);
            nightmareMiXian.setGainTreasureHourse(false);
        }
        gameUserService.updateItem(nightmareMiXian);
        getCurrentMxdData(nightmareMiXian, rd);
        return rd;
    }

    /**
     * 放弃挑战
     *
     * @param uid
     * @return
     */
    public RDNightmareMxd toGiveUp(long uid) {
        UserNightmareMiXian nightmareMiXian = nightmareMiXianService.getAndCreateUserNightmareMiXian(uid);
        log.info("玩家:{}，离开迷仙洞时背包的数据{}", nightmareMiXian.getGameUserId(), JSON.toJSONString(nightmareMiXian.getBag()));
        //没有生命值且拾取了宝藏
        if (nightmareMiXian.ifFail() && nightmareMiXian.isGainTreasureHourse()) {
            EPCombatAchievement ep = EPCombatAchievement.instance(new BaseEventParam(uid), 15430);
            CombatEventPublisher.pubCombatAchievement(ep);
        }
        //重置是否拾取宝藏
        nightmareMiXian.setGainTreasureHourse(false);
        nightmareMiXian.incBlood(-nightmareMiXian.getBlood());
        nightmareMiXian.setBag(new ArrayList<>());
        gameUserService.updateItem(nightmareMiXian);
        return new RDNightmareMxd();
    }

    /**
     * 通过关卡 选择退出
     * 发送背包奖励
     *
     * @param uid
     * @return
     */
    public UserNightmareMiXian toPassLevel(long uid, RDNightmareMxd rd) {
        UserNightmareMiXian nightmareMiXian = nightmareMiXianService.getAndCreateUserNightmareMiXian(uid);
        if (!nightmareMiXian.ifHasGateKey()) {
            throw new ExceptionForClientTip("mxd.not.own.key");
        }
        //允许进入下一层
        int oldLevel = nightmareMiXian.getCurrentLevel();
        List<Award> awards = nightmareMiXian.getBag().stream().filter(p -> p.getAwardId() != TreasureEnum.MXD_LEVEL_KEY.getValue()).collect(Collectors.toList());
        int blood = nightmareMiXian.getBlood();
        boolean beInjured = nightmareMiXian.isBeInjured();
        nightmareMiXian.getBag().clear();
        nightmareMiXian.setBlood(0);
        nightmareMiXian.setNextInitLevel(nightmareMiXian.getCurrentLevel() + 1);
        nightmareMiXian.setContinuePassLevel(1);
        // 层数处理
        nightmareMiXianService.incChallengeLayers(nightmareMiXian);
        nightmareMiXianService.redChallengeLayers(nightmareMiXian);
        // 累计每日元宝数
        addDailyGold(nightmareMiXian, awards);
        gameUserService.updateItem(nightmareMiXian);
        if (ListUtil.isNotEmpty(awards)) {
            awardService.fetchAward(uid, awards, WayEnum.MXD_PASS_LEVEL, WayEnum.MXD_PASS_LEVEL.getName(), rd);
        }
        if (oldLevel == 10 || oldLevel == 20 || oldLevel == 30) {
            boolean hasCengZhu = false;
            for (MiXianLevelData.PosData posData : nightmareMiXian.getLevelData().getPosDatas()) {
                if (posData.getTye() == NightmareMiXianPosEnum.LEVEL_LEADER.getType()) {
                    hasCengZhu = true;
                    break;
                }
            }
            if (hasCengZhu) {
                Optional<MiXianEnemy> optional = cengZhuProcessor.getLevelOwner(gameUserService.getActiveGid(uid), oldLevel, new Date());
                if (optional.isPresent() && optional.get().getUid() > 0 && !optional.get().getUid().equals(uid)) {
                    //有玩家层主
                    GameUser gu = gameUserService.getGameUser(uid);
                    String title = LM.I.getMsgByUid(optional.get().getUid(), "mail.nightmare.mxd.guard.award.title", nightmareMiXian.getCurrentLevel());
                    String content = LM.I.getMsgByUid(optional.get().getUid(), "mail.nightmare.mxd.guard.award.content", ServerTool.getServerShortName(gu.getServerId()), gu.getRoleInfo().getNickname());
                    mailService.sendAwardMail(title, content, optional.get().getUid(), Arrays.asList(Award.instance(11670, AwardEnum.FB, 1)));
                    MiXDEventPublisher.pubCZBeatDefierEvent(new BaseEventParam(optional.get().getUid()));
                }
            }
            int[] achievement = {15260, 15270, 15280};
            EPCombatAchievement ep = EPCombatAchievement.instance(new BaseEventParam(uid), achievement[oldLevel / 10 - 1]);
            CombatEventPublisher.pubCombatAchievement(ep);
        }
        MiXDEventPublisher.pubPassTierEvent(EPPassTier.instance(new BaseEventParam(uid), blood, beInjured));
        return nightmareMiXian;
    }

    /**
     * 玩家消耗100元宝  重置到第一层
     *
     * @param uid
     * @return
     */
    public RDNightmareMxd reset(long uid) {
        RDNightmareMxd rdNightmareMxd = new RDNightmareMxd();
        int need = 100;
        ResChecker.checkGold(gameUserService.getGameUser(uid), need);
        ResEventPublisher.pubGoldDeductEvent(uid, need, WayEnum.MXD_RESET, rdNightmareMxd);
        //即放弃挑战 然后初始化下次挑战的层为1
        toGiveUp(uid);
        UserNightmareMiXian nightmareMiXian = nightmareMiXianService.getAndCreateUserNightmareMiXian(uid);
        nightmareMiXian.setNextInitLevel(1);
        nightmareMiXian.setContinuePassLevel(1);
        initNewLevel(nightmareMiXian, nightmareMiXian.getNextInitLevel());
        getCurrentMxdData(nightmareMiXian, rdNightmareMxd);
        return rdNightmareMxd;
    }

    /**
     * 是否需要初始化每日元宝累计以及时间
     *
     * @param userNightmareMiXian 用户迷仙洞数据
     */
    private void isInitDailyGoldAndTime(UserNightmareMiXian userNightmareMiXian) {
        // 当前初始化的时间
        String initDailyGoldTime = userNightmareMiXian.getInitDailyGoldNumTime();
        // 为空，代表需要更新旧数据
        if (initDailyGoldTime == null) {
            userNightmareMiXian.setInitDailyGoldNumTime(DateUtil.toString(new Date(), DateUtil.DATE_STRING_PATTERN));
            gameUserService.updateItem(userNightmareMiXian);
            return;
        }
        // 判断为是否为同一天
        if (!DateUtil.isToday(DateUtil.fromDateString(initDailyGoldTime))) {
            // 不为同一天，则更新时间，并初始化每日元宝累计值 为 0
            userNightmareMiXian.setDailyGoldNum(0);
            userNightmareMiXian.setInitDailyGoldNumTime(DateUtil.toString(new Date(), DateUtil.DATE_STRING_PATTERN));
            gameUserService.updateItem(userNightmareMiXian);
        }
    }

    /**
     * 增加每日元宝累计数
     *
     * @param userNightmareMiXian 用户迷仙洞数据
     */
    private void addDailyGold(UserNightmareMiXian userNightmareMiXian, List<Award> awardList) {
        // 防止用户已在迷仙洞，而当前用户数据为旧数据，需要处理时间值
        dealTimeOfAddDailyGold(userNightmareMiXian);
        // 获取奖励为元宝
        List<Award> ybList = awardList.stream().filter(award -> award.getItem() == AwardEnum.YB.getValue()).collect(Collectors.toList());
        // 计算总数
        int sumYb = ybList.stream().mapToInt(Award::getNum).sum();
        // 记录数据
        userNightmareMiXian.setDailyGoldNum(userNightmareMiXian.getDailyGoldNum() + sumYb);
    }

    /**
     * 处理在更新累计元宝的初始化时间的问题
     *
     * @param userNightmareMiXian
     */
    private void dealTimeOfAddDailyGold(UserNightmareMiXian userNightmareMiXian) {
        // 当前初始化的时间
        String initDailyGoldTime = userNightmareMiXian.getInitDailyGoldNumTime();
        // 为空，代表需要更新旧数据
        if (initDailyGoldTime == null) {
            userNightmareMiXian.setInitDailyGoldNumTime(DateUtil.toString(new Date(), DateUtil.DATE_STRING_PATTERN));
            return;
        }
        // 判断为是否为同一天
        if (!DateUtil.isToday(DateUtil.fromDateString(initDailyGoldTime))) {
            // 不为同一天，则更新时间，并初始化每日元宝累计值 为 0
            userNightmareMiXian.setDailyGoldNum(0);
            userNightmareMiXian.setInitDailyGoldNumTime(DateUtil.toString(new Date(), DateUtil.DATE_STRING_PATTERN));
        }
    }
}