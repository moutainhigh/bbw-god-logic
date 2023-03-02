package com.bbw.god.gameuser;

import com.bbw.common.PowerRandom;
import com.bbw.exception.CoderException;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.activityrank.server.expedition.ExpeditionRankEventPublisher;
import com.bbw.god.cache.TimeLimitCacheUtil;
import com.bbw.god.city.event.CityEventPublisher;
import com.bbw.god.city.miaoy.hexagram.HexagramBuffEnum;
import com.bbw.god.city.miaoy.hexagram.HexagramBuffService;
import com.bbw.god.city.miaoy.hexagram.event.HexagramEventPublisher;
import com.bbw.god.city.yed.RDYeDEventCache;
import com.bbw.god.event.BaseEventParam;
import com.bbw.god.game.config.Cfg;
import com.bbw.god.game.config.CfgGame;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.city.CfgRoadEntity;
import com.bbw.god.game.config.city.RoadTool;
import com.bbw.god.game.config.city.YdEventEnum;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.businessgang.digfortreasure.DigTreasureService;
import com.bbw.god.gameuser.businessgang.luckybeast.LuckyBeastService;
import com.bbw.god.gameuser.businessgang.luckybeast.RDLuckyBeastInfo;
import com.bbw.god.gameuser.guide.NewerGuideService;
import com.bbw.god.gameuser.res.ResChecker;
import com.bbw.god.gameuser.res.ResEventPublisher;
import com.bbw.god.gameuser.shake.ShakeEventPublish;
import com.bbw.god.gameuser.shake.ShakeRandomParam;
import com.bbw.god.gameuser.shake.ShakeService;
import com.bbw.god.gameuser.treasure.TreasureChecker;
import com.bbw.god.gameuser.treasure.UserTreasureEffect;
import com.bbw.god.gameuser.treasure.UserTreasureEffectService;
import com.bbw.god.gameuser.treasure.UserTreasureRecordService;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.rd.RDAdvance;
import com.bbw.god.rd.RDCommon;
import com.bbw.god.road.PathRoad;
import com.bbw.god.road.RoadEventPublisher;
import com.bbw.god.road.RoadPathService;
import com.bbw.god.server.god.GodEventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
public class GameUserShakeLogic {
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private ShakeService shakeService;
    @Autowired
    private UserTreasureRecordService userTreasureRecordService;
    @Autowired
    private UserTreasureEffectService userTreasureEffectService;
    @Autowired
    private NewerGuideService newerGuideService;
    @Autowired
    private RoadPathService roadPathService;
    @Autowired
    private HexagramBuffService hexagramBuffService;
    @Autowired
    private DigTreasureService digTreasureService;
    @Autowired
    private LuckyBeastService luckyBeastService;

    public RDAdvance shakeDice(Long uid, int diceNum) {
        /*
         * 体力判定 可否摇骰子判定——路口和漫步靴判定 骰子数判定——四不像、青鸾判定 获得本次步数——新手引导步数控制、随机数 获取路径 -格子事件 -神仙事件 -神仙效果 -建筑事件 -建筑交互 状态更新
         */
        RDAdvance rd = new RDAdvance();
        GameUser gu = this.gameUserService.getGameUser(uid);
        boolean hasBuff = hexagramBuffService.isHexagramBuff(gu.getId(), HexagramBuffEnum.HEXAGRAM_21.getId());
        if (!hasBuff) {
            this.checkShake(gu, diceNum);
        }
        Integer hexagramBuff = hexagramBuffService.hasStandHexagramBuff(uid);
        if (hexagramBuff != null) {
            rd.setStand(1);
            CfgRoadEntity road = RoadTool.getRoadById(gu.getLocation().getPosition());
            // 到达
            CityEventPublisher.publCityArriveEvent(gu.getId(), gu.getLocation().getPosition(), WayEnum.NONE, rd);
            // 扣体力
            CfgGame config = Cfg.I.getUniqueConfig(CfgGame.class);
            ResEventPublisher.pubDiceDeductEvent(gu.getId(), config.getDiceOneShake(), WayEnum.TREASURE_USE, rd);
            // 行走触发格子事件（界碑）
            RoadEventPublisher.publishRoadEvent(gu.getId(), road.getId(), WayEnum.TREASURE_USE, rd);

            //招财兽信息
            RDLuckyBeastInfo luckyBeastInfo = luckyBeastService.arriveLuckyBeast(gu.getId());
            if (null != luckyBeastInfo) {
                rd.setArriveLuckyBeast(luckyBeastInfo);
            }


            //重置铲子
            this.userTreasureRecordService.resetTreasureRecordAsChangZi(gu.getId());
            rd.setDirection(gu.getLocation().getDirection());
            rd.setPoss(new ArrayList<>());
            rd.setDirs(new ArrayList<>());
            rd.setMbxRemainForcross(0);
            //模拟筛子点数
            List<Integer> dicesNums = new ArrayList<>();
            for (int i = 0; i < diceNum; i++) {
                dicesNums.add(PowerRandom.getRandomBySeed(6));
            }
            rd.setRandoms(dicesNums);
            // 重置定风珠、醒酒毡使用记录
            userTreasureRecordService.resetTreasureRecordAsNewPos(uid, WayEnum.TREASURE_USE);
            HexagramEventPublisher.pubHexagramBuffDeductEvent(new BaseEventParam(uid), hexagramBuff, 1);
            return rd;
        }
        List<PathRoad> roadPath = null;
        if (!newerGuideService.isPassNewerGuide(uid)) {
            List<Integer> diceResult = this.shakeService.getDiceResult(gu, diceNum);
            roadPath = roadPathService.getAssignPath(gu.getLocation().getPosition(), gu.getLocation().getDirection(), diceResult.get(0));
        } else {
            RDYeDEventCache cache = TimeLimitCacheUtil.getYeDEventCache(uid);
            if (cache != null) {
                Set<Integer> eventIds = cache.getEventIds();
                if (eventIds.contains(YdEventEnum.ZJZF.getValue())) {
                    roadPath = roadPathService.getAssignPath(gu.getLocation().getPosition(), gu.getLocation().getDirection(), diceNum * 6);
                    eventIds.remove(YdEventEnum.ZJZF.getValue());
                    TimeLimitCacheUtil.setYeDEventCache(uid, cache);
                } else if (eventIds.contains(YdEventEnum.CBNX.getValue())) {
                    diceNum = 1;
                    roadPath = roadPathService.getAssignPath(gu.getLocation().getPosition(), gu.getLocation().getDirection(), 1);
                    eventIds.remove(YdEventEnum.CBNX.getValue());
                    TimeLimitCacheUtil.setYeDEventCache(uid, cache);
                }
            }
        }
        if (hexagramBuffService.isHexagramBuff(gu.getId(), HexagramBuffEnum.HEXAGRAM_64.getId())) {
            HexagramEventPublisher.pubHexagramBuffDeductEvent(new BaseEventParam(gu.getId()), HexagramBuffEnum.HEXAGRAM_64.getId(), 1);
            if (diceNum == 1 && roadPath == null) {
                roadPath = roadPathService.getAssignPath(gu.getLocation().getPosition(), gu.getLocation().getDirection(), 1);
            }
        }
        if (roadPath == null) {
            ShakeRandomParam shakeRandomParam = new ShakeRandomParam();
            shakeRandomParam.setGuLevel(gu.getLevel());
            roadPath = roadPathService.getRandomPath(gu.getLocation().getPosition(), gu.getLocation().getDirection(), diceNum * 6, diceNum, shakeRandomParam);
        }
        List<Integer> diceResult = splitNum(diceNum, roadPath.size());

        ShakeEventPublish.pubShakeEvent(diceResult, new BaseEventParam(uid, WayEnum.SHAKE_DICE, rd));
        this.getRoads(gu, roadPath, WayEnum.SHAKE_DICE, rd);

        //招财兽信息
        RDLuckyBeastInfo luckyBeastInfo = luckyBeastService.arriveLuckyBeast(gu.getId());
        if (null != luckyBeastInfo) {
            rd.setArriveLuckyBeast(luckyBeastInfo);
        }
        rd.setRandoms(diceResult);
        HexagramEventPublisher.pubHexagramBuffDeductEvent(new BaseEventParam(uid), HexagramBuffEnum.HEXAGRAM_56.getId(), 1);
        return rd;
    }

    /**
     * 使用漫步鞋到路口选择方向 如果有剩余步数 就会走完
     *
     * @param guId
     * @param direction
     * @return
     */
    public RDAdvance chooseDirection(long guId, int direction) {
        RDAdvance rd = new RDAdvance();
        GameUser gu = this.gameUserService.getGameUser(guId);
        UserTreasureEffect mbxEffect = this.userTreasureEffectService.getEffect(guId, TreasureEnum.MBX.getValue());
        if (mbxEffect == null || mbxEffect.getRemainEffect() == 0) {
            throw new ExceptionForClientTip("gu.not.chooseDir");
        }
        WayEnum way = WayEnum.MBX_CHOOSE_DIR;
        int currCellId = gu.getLocation().getPosition();
        // 刚好到达路口，直接改方向即可
        if (mbxEffect.getRemainEffect() == -1) {
            gu.moveTo(currCellId, direction);
            // 神仙检测和附体（另：神仙飞走 交给客户端自己去判断，服务器不管）
            GodEventPublisher.pubGodAttachEvent(gu.getId(), currCellId, way, rd);
            // 触发地点事件
            CityEventPublisher.publCityArriveEvent(gu.getId(), currCellId, way, rd);
            TreasureEventPublisher.pubMBXEffectSetEvent(gu.getId(), 0, way);
        } else if (mbxEffect.getRemainEffect() > 0) {
            // 继续往下走
            List<PathRoad> roadPath = roadPathService.getAssignPath(gu.getLocation().getPosition(), direction, mbxEffect.getRemainEffect());
            this.getRoads(gu, roadPath, way, rd);
        }
        if (hexagramBuffService.isHexagramBuff(gu.getId(), HexagramBuffEnum.HEXAGRAM_12.getId())) {
            HexagramEventPublisher.pubHexagramBuffDeductEvent(new BaseEventParam(gu.getId(), WayEnum.TREASURE_USE, rd), HexagramBuffEnum.HEXAGRAM_12.getId(), 1);
        }
        //招财兽信息
        RDLuckyBeastInfo luckyBeastInfo = luckyBeastService.arriveLuckyBeast(gu.getId());
        if (null != luckyBeastInfo) {
            rd.setArriveLuckyBeast(luckyBeastInfo);
        }
        return rd;
    }

    /**
     * 检测摇骰子是否可以正常进行
     *
     * @param gu
     * @param diceNum 骰子数
     */
    private void checkShake(GameUser gu, int diceNum) {
        // 检查体力
        CfgGame config = Cfg.I.getUniqueConfig(CfgGame.class);
        ResChecker.checkDice(gu, config.getDiceOneShake());
        // 检查漫步靴
        if (this.userTreasureEffectService.isTreasureEffect(gu.getId(), TreasureEnum.MBX.getValue())) {
            throw new ExceptionForClientTip("treasure.effect.mbx.toChooseDir");
        }
        // 检查骰子数是否有效
        if (this.userTreasureEffectService.isTreasureEffect(gu.getId(), TreasureEnum.QL.getValue()) && diceNum > 3) {
            throw new ExceptionForClientTip("gu.dice.not.valid");
        }
        if (this.userTreasureEffectService.isTreasureEffect(gu.getId(), TreasureEnum.SBX.getValue()) && diceNum > 2) {
            throw new ExceptionForClientTip("gu.dice.not.valid");
        }
    }

    /**
     * 获得路径和到达结果
     *
     * @param gu
     * @param roadPath
     * @param way
     * @param rd
     */
    public void getRoads(GameUser gu, List<PathRoad> roadPath, WayEnum way, RDAdvance rd) {
        getRoads(gu, roadPath, way, rd, true);
    }

    public void getRoads(GameUser gu, List<PathRoad> roadPath, WayEnum way, RDAdvance rd, boolean needDice) {

        List<Integer> roads = new ArrayList<Integer>();
        List<Integer> dirs = new ArrayList<Integer>();
        boolean isMBXOpen = gu.ifMbxOpen();
        boolean isOwnMBX = TreasureChecker.hasTreasure(gu.getId(), TreasureEnum.MBX.getValue());
        boolean hasFreeMBXBuff = hexagramBuffService.isHexagramBuff(gu.getId(), HexagramBuffEnum.HEXAGRAM_12.getId());
        PathRoad lastPathRoad = null;
        boolean isToChooseDir = false;
        for (PathRoad pathRoad : roadPath) {
            CfgRoadEntity road = pathRoad.getRoad();
            // 检测经过的格子
            RoadEventPublisher.publishRoadEvent(gu.getId(), road.getId(), way, rd);
            roads.add(road.getId());
            dirs.add(pathRoad.getDir());
            lastPathRoad = pathRoad;
            /** 漫步靴,遇到路口 **/
            if (road.isCross()) {
                if (hasFreeMBXBuff) {
                    isToChooseDir = true;
                    break;
                } else if (isMBXOpen && isOwnMBX) {
                    isToChooseDir = true;
                    TreasureEventPublisher.pubTDeductEvent(gu.getId(), TreasureEnum.MBX.getValue(), 1, way, rd);
                    break;
                }
            }
        }
        // 漫步靴剩余步数
        int mbxRemain = 0;
        if (way == WayEnum.MBX_CHOOSE_DIR || hasFreeMBXBuff || (isMBXOpen && isOwnMBX)) {
            mbxRemain = roadPath.size() - roads.size();
        }
        if (hasFreeMBXBuff || (isMBXOpen && isOwnMBX)) {
            rd.setMbxRemainForcross(1);// 让玩家自己选择
            if (isToChooseDir && mbxRemain == 0) {
                mbxRemain = -1;// 标志玩家刚好到路口，未选择方向
            }
        } else {
            rd.setMbxRemainForcross(0); // 让玩家自己选择
        }
        int lastDir = lastPathRoad.getDir();
        //非漫步靴防止路口撞墙
        if (!isToChooseDir && lastPathRoad.getRoad().getCellByNextDirection(lastDir) == null) {
            lastDir = lastPathRoad.getRoad().getNextDirection(lastDir, 5 - lastDir);
        }
        rd.setDirection(lastDir);
        rd.setPoss(roads);
        rd.setDirs(dirs);
        gu.moveTo(lastPathRoad.getRoad().getId(), lastDir);

        if (!isToChooseDir) {// 如果漫步鞋生效了，我们当成筛子没走完，一定要选完方向才算完成
            // 触发地点事件
            // 神仙检测和附体（另：神仙飞走 交给客户端自己去判断，服务器不管）
            GodEventPublisher.pubGodAttachEvent(gu.getId(), lastPathRoad.getRoad().getId(), way, rd);
            // !!!神仙附体先于到达事件的处理，否则新手引导会出错
            CityEventPublisher.publCityArriveEvent(gu.getId(), lastPathRoad.getRoad().getId(), way, rd);
        }
        // 法宝效果处理
        TreasureEventPublisher.pubMBXEffectSetEvent(gu.getId(), mbxRemain, way);
        TreasureEventPublisher.pubTEffectDeductEvent(gu.getId(), TreasureEnum.CSZ.getValue(), roads.size(), way);
        this.userTreasureRecordService.resetTreasureRecordAsNewPos(gu.getId(), way);
        // 选方向不扣体力和道具剩余步数
        if (way != WayEnum.MBX_CHOOSE_DIR) {
            CfgGame config = Cfg.I.getUniqueConfig(CfgGame.class);
            if (needDice && !hexagramBuffService.isHexagramBuff(gu.getId(), HexagramBuffEnum.HEXAGRAM_21.getId())) {
                ResEventPublisher.pubDiceDeductEvent(gu.getId(), config.getDiceOneShake(), way, rd);
            } else {
                HexagramEventPublisher.pubHexagramBuffDeductEvent(new BaseEventParam(gu.getId(), WayEnum.SHAKE_DICE, rd), HexagramBuffEnum.HEXAGRAM_21.getId(), 1);
            }
            TreasureEventPublisher.pubTEffectDeductEvent(gu.getId(), TreasureEnum.QL.getValue(), 1, way);
            TreasureEventPublisher.pubTEffectDeductEvent(gu.getId(), TreasureEnum.SBX.getValue(), 1, way);
        }
        ExpeditionRankEventPublisher.pubStepEvent(gu.getId(), roadPath.size());
    }

    /**
     * 更改漫步靴使用状态
     */
    public RDCommon changeStatusForMBX(long guId, int useShoeStatus) {
        RDCommon rd = new RDCommon();
        // 更新漫步靴的状态
        GameUser gu = this.gameUserService.getGameUser(guId);
        gu.getSetting().setActiveMbx(useShoeStatus);
        gu.updateSetting();
        // 充值漫步靴
        TreasureEventPublisher.pubMBXEffectSetEvent(guId, 0, WayEnum.NONE);
        return rd;
    }

    public List<Integer> splitNum(int diceNum, int pathLength) {
        if (pathLength > diceNum * 6) {
            throw CoderException.high("无效的路径长度");
        }
        List<Integer> diceResult = Stream.generate(() -> 1).limit(diceNum).collect(Collectors.toList());
        for (int i = 0; i < pathLength - diceNum; i++) {
            Integer value = 0;
            int index = 0;
            do {
                index = PowerRandom.getRandomBySeed(diceNum) - 1;
                value = diceResult.get(index);
            } while (value >= 6);
            diceResult.set(index, value + 1);
        }
        return diceResult;
    }

}
