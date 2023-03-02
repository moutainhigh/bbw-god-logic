package com.bbw.god.gameuser.businessgang;

import com.bbw.common.DateUtil;
import com.bbw.common.ListUtil;
import com.bbw.common.MapUtil;
import com.bbw.common.PowerRandom;
import com.bbw.db.redis.RedisHashUtil;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.city.chengc.RDTradeInfo;
import com.bbw.god.city.chengc.trade.BuyGoodInfo;
import com.bbw.god.city.chengc.trade.IChengChiTradeService;
import com.bbw.god.game.config.Cfg;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.mall.CfgMallEntity;
import com.bbw.god.game.config.special.CfgSpecialEntity;
import com.bbw.god.game.config.special.CfgSpecialHierarchyMap;
import com.bbw.god.game.config.special.SpecialTool;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.UserCfgObj;
import com.bbw.god.gameuser.businessgang.Enum.BusinessGangEnum;
import com.bbw.god.gameuser.businessgang.Enum.BusinessGangNpcEnum;
import com.bbw.god.gameuser.businessgang.Enum.BusinessNpcTypeEnum;
import com.bbw.god.gameuser.businessgang.Enum.GiftsGradeEnum;
import com.bbw.god.gameuser.businessgang.cfg.*;
import com.bbw.god.gameuser.businessgang.event.BusinessGangEventPublisher;
import com.bbw.god.gameuser.businessgang.user.UserBusinessGangInfo;
import com.bbw.god.gameuser.businessgang.user.UserBusinessGangTaskInfo;
import com.bbw.god.gameuser.privilege.PrivilegeService;
import com.bbw.god.gameuser.special.UserSpecialService;
import com.bbw.god.gameuser.statistic.StatisticServiceFactory;
import com.bbw.god.gameuser.statistic.StatisticTypeEnum;
import com.bbw.god.gameuser.statistic.behavior.BehaviorStatisticService;
import com.bbw.god.gameuser.statistic.behavior.BehaviorType;
import com.bbw.god.gameuser.task.TaskGroupEnum;
import com.bbw.god.gameuser.task.TaskTool;
import com.bbw.god.gameuser.task.businessgang.UserBusinessGangSpecialtyShippingTask;
import com.bbw.god.gameuser.task.businessgang.UserBusinessGangWeeklyTask;
import com.bbw.god.gameuser.task.businessgang.UserSpecialtyShippingTaskService;
import com.bbw.god.gameuser.task.businessgang.UserWeeklyTaskService;
import com.bbw.god.gameuser.task.timelimit.businessGang.UserBusinessGangLimitTaskService;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.mall.RDMallList;
import com.bbw.god.rd.RDCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 商帮服务类
 *
 * @author fzj
 * @date 2022/1/18 14:29
 */
@Service
public class BusinessGangService implements IChengChiTradeService {
    @Autowired
    UserBusinessGangService userBusinessGangService;
    @Autowired
    UserBusinessGangLimitTaskService userBusinessGangLimitTaskService;
    @Autowired
    UserSpecialtyShippingTaskService userSpecialtyShippingTaskService;
    @Autowired
    UserWeeklyTaskService userWeeklyTaskService;
    @Autowired
    GameUserService gameUserService;
    @Autowired
    PrivilegeService privilegeService;
    @Autowired
    StatisticServiceFactory statisticServiceFactory;
    @Autowired
    protected RedisHashUtil<String, Integer> redisHashUtil;
    @Autowired
    private UserSpecialService userSpecialService;

    /**
     * 商帮好感度礼物
     */
    public static final List<Integer> GIFTS = BusinessGangCfgTool.getAllGiftInfos().stream().map(CfgGiftEntity::getGiftId).collect(Collectors.toList());

    /**
     * 增加好感度
     *
     * @param userBusinessGang
     * @param cfgNpcInfo
     * @param giftId
     * @param giftNum
     * @param rd
     * @return
     */
    public Integer addFavorability(UserBusinessGangInfo userBusinessGang, CfgNpcInfo cfgNpcInfo, int giftId, int giftNum, RDCommon rd) {
        //npc所在商帮
        Integer businessGang = cfgNpcInfo.getGangId();
        //获得加好感度
        Integer favorability = userBusinessGang.getFavorability(cfgNpcInfo.getId());
        int addedFavorability = favorability;
        //计算好感度
        int addFavorability = BusinessGangCfgTool.getGiftFavorability(cfgNpcInfo, giftId);
        for (int i = 1; i <= giftNum; i++) {
            CfgFavorabilityRules rules = BusinessGangCfgTool.getFavorabilityRule(addedFavorability, cfgNpcInfo.getType());
            if (null == rules) {
                continue;
            }
            Integer rulesFavorability = rules.getFavorability();
            addedFavorability += addFavorability;
            if (addedFavorability < rulesFavorability) {
                continue;
            }
            //npc商帮声望
            Integer businessGangPrestige = userBusinessGang.getPrestige(businessGang);
            boolean isReachPrestige = businessGangPrestige >= rules.getNeedBusinessGangPrestige();
            //npc商帮掌舵人好感度
            CfgNpcInfo npcInfo = BusinessGangCfgTool.getNpcInfo(businessGang, BusinessNpcTypeEnum.ZHANG_DUO_REN.getType());
            Integer zhangDrFavorability = userBusinessGang.getFavorability(npcInfo.getId());
            boolean isReachFavorability = zhangDrFavorability >= rules.getNeedZhangDrFavorability();
            if (isReachPrestige && isReachFavorability) {
                continue;
            }
            giftNum = i;
            addedFavorability = rulesFavorability - 1;
            break;
        }
        //扣除礼物
        if (addedFavorability == favorability) {
            if (cfgNpcInfo.getType() == BusinessNpcTypeEnum.ZHANG_DUO_REN.getType()) {
                throw new ExceptionForClientTip("businessGang.not.send.gift");
            }
            throw new ExceptionForClientTip("businessGang.not.send.other.gift");
        }
        //发布增加好感度事件
        Long uid = userBusinessGang.getGameUserId();
        int hasAddFavorability = addedFavorability - favorability;
        BusinessGangEventPublisher.pubAddGangNpcFavorabilityEvent(uid, cfgNpcInfo.getId(), hasAddFavorability);
        TreasureEventPublisher.pubTDeductEvent(uid, giftId, giftNum, WayEnum.BUSINESS_GANG_SENDGIFTS, rd);
        return addedFavorability;
    }

    /**
     * 解锁商帮
     *
     * @param userBusinessGang
     * @param cfgNpcInfo
     */
    public void checkUnlockBusinessGang(UserBusinessGangInfo userBusinessGang, CfgNpcInfo cfgNpcInfo) {
        if (cfgNpcInfo.getType() != BusinessNpcTypeEnum.ZHANG_DUO_REN.getType()) {
            return;
        }
        //判断是否已经解锁
        Integer gangId = cfgNpcInfo.getGangId();
        boolean unlockBusinessGang = isBusinessGang(userBusinessGang.getGameUserId(), gangId);
        if (unlockBusinessGang) {
            return;
        }
        //是否达到解锁要求
        Integer needFavorability = BusinessGangCfgTool.getBusinessGangInfo().getUnlockBusinessGangNeedFavorability();
        Integer npcInfoId = cfgNpcInfo.getId();
        Integer favorability = userBusinessGang.getFavorability(npcInfoId);
        if (favorability < needFavorability) {
            return;
        }
        userBusinessGang.unlockGang(gangId);
    }


    /**
     * 判断是否解锁商帮
     *
     * @param uid
     * @param gangId
     * @return
     */
    public boolean isBusinessGang(long uid, int gangId) {
        UserBusinessGangInfo userBusinessGang = userBusinessGangService.getOrCreateUserBusinessGang(uid);
        List<Integer> openedBusinessGangs = userBusinessGang.getOpenedBusinessGangs();
        if (openedBusinessGangs.contains(gangId)) {
            return true;
        }
        return false;
    }

    /**
     * 判断是否加入商帮
     *
     * @param uid
     * @return
     */
    public boolean isJoinBusinessGang(long uid) {
        UserBusinessGangInfo userBusinessGang = userBusinessGangService.getUserBusinessGang(uid);
        return userBusinessGang.getCurrentBusinessGang() > 0;
    }

    /**
     * 初始化任务
     *
     * @param uid
     */
    public void initialTask(long uid) {
        //获取派遣任务数量
        int limitTaskNum = userBusinessGangLimitTaskService.getAllTasks(uid).size();
        //获取运送任务数量
        int shippingTaskNum = userSpecialtyShippingTaskService.getAllTasks(uid).size();
        //当前任务数量
        int totalTaskNum = limitTaskNum + shippingTaskNum;
        CfgBusinessGangEntity businessGangInfo = BusinessGangCfgTool.getBusinessGangInfo();
        Integer taskNum = businessGangInfo.getBusinessGangTaskNum();
        for (int i = totalTaskNum; i < taskNum; i++) {
            //随机生成任务
            generateTask(uid);
        }
    }

    /**
     * 生成周常任务
     *
     * @param uid
     */
    public void generateWeeklyTask(long uid) {
        CfgBusinessGangEntity businessGangInfo = BusinessGangCfgTool.getBusinessGangInfo();
        UserBusinessGangInfo userBusinessGang = userBusinessGangService.getOrCreateUserBusinessGang(uid);
        //获得开启周常任务对应条件
        Integer gang = businessGangInfo.getUnlockWeeklyTaskGang();
        Integer needPrestige = businessGangInfo.getUnlockWeeklyTaskNeedPrestige();
        //获取条件商帮声望
        Integer prestige = userBusinessGang.getPrestige(gang);
        if (prestige < needPrestige) {
            return;
        }
        List<UserBusinessGangWeeklyTask> allTasks = userWeeklyTaskService.getAllTasks(uid);
        //判断当前是否已经存在周常任务
        int weeklyTaskNum = allTasks.size();
        if (weeklyTaskNum == 0) {
            //生成周常任务
            userWeeklyTaskService.makeUserTaskInstance(uid, null);
            return;
        }
        //是否可以刷新
        boolean refresh = userWeeklyTaskService.canRefresh(uid);
        if (!refresh) {
            return;
        }
        //获得当前周常任务的id
        Integer currentTaskId = allTasks.stream().map(UserCfgObj::getBaseId).findFirst().orElse(null);
        //清除周常任务
        userWeeklyTaskService.delAllTask(uid);
        //生成周常任务
        userWeeklyTaskService.makeUserTaskInstance(uid, currentTaskId);
    }

    /**
     * 生成商帮任务
     *
     * @param uid
     */
    public void generateTask(long uid) {
        UserBusinessGangInfo userBusinessGang = userBusinessGangService.getOrCreateUserBusinessGang(uid);
        //获取正财商帮的声望
        Integer prestige = userBusinessGang.getPrestige(BusinessGangEnum.ZHENG_CAI.getType());
        CfgBusinessGangEntity businessGangInfo = BusinessGangCfgTool.getBusinessGangInfo();
        //获取声望对应规则
        int needPrestige = businessGangInfo.getTaskRefreshProbability().stream().filter(t -> prestige >= t.getNeedPrestige())
                .map(CfgBusinessGangEntity.TaskRefresh::getNeedPrestige).findFirst().orElse(0);
        List<CfgBusinessGangEntity.TaskRefresh> taskDifficultyList = businessGangInfo.getTaskRefreshProbability()
                .stream().filter(t -> needPrestige == t.getNeedPrestige()).collect(Collectors.toList());
        //根据概率随机难度
        List<Integer> pro = taskDifficultyList.stream().map(CfgBusinessGangEntity.TaskRefresh::getProbability).collect(Collectors.toList());
        int index = PowerRandom.getIndexByProbs(pro, 100);
        Integer difficulty = taskDifficultyList.get(index).getDifficulty();
        //根据概率获取生成任务的类型
        Integer dispatchTaskRefreshPro = businessGangInfo.getDispatchTaskRefreshPro();
        boolean dispatchTask = PowerRandom.hitProbability(dispatchTaskRefreshPro);
        if (dispatchTask) {
            userBusinessGangLimitTaskService.makeUserTaskInstance(uid, difficulty);
            return;
        }
        userSpecialtyShippingTaskService.makeUserTaskInstance(uid, difficulty);
    }

    /**
     * 移除未解锁商品
     *
     * @param uid
     * @param rdMallList
     */
    public void removeUnlockGoods(long uid, RDMallList rdMallList) {
        UserBusinessGangInfo userBusinessGang = userBusinessGangService.getUserBusinessGang(uid);
        if (null == userBusinessGang) {
            return;
        }
        CfgBusinessGangEntity businessGangInfo = BusinessGangCfgTool.getBusinessGangInfo();
        //获取需要解锁商品
        List<CfgNeedUnlockGoods> needUnlockGoods = businessGangInfo.getNeedUnlockGoods();
        List<CfgNeedUnlockGoods> notUnlockGoodsList = new ArrayList<>();
        for (CfgNeedUnlockGoods needUnlockGood : needUnlockGoods) {
            int favorability = 0;
            if (needUnlockGood.getCorrespondNpc() != 0) {
                favorability = userBusinessGang.getFavorability(needUnlockGood.getCorrespondNpc());
            }
            int prestige = 0;
            if (needUnlockGood.getCorrespondBang() != 0) {
                prestige = userBusinessGang.getPrestige(needUnlockGood.getCorrespondBang());
            }
            if (isUnlock(favorability, prestige, needUnlockGood)) {
                continue;
            }
            notUnlockGoodsList.add(needUnlockGood);
        }
        //移除未解锁商品
        for (CfgNeedUnlockGoods notUnlockGood : notUnlockGoodsList) {
            Integer goodId = notUnlockGood.getGoodId();
            Integer item = notUnlockGood.getItem();
            rdMallList.getMallGoods().removeIf(g -> g.getRealId().equals(goodId) && g.getItem().equals(item));
        }
    }

    /**
     * 检查商品是否解锁
     *
     * @param uid
     * @param mall
     */
    public void checkUnlock(long uid, CfgMallEntity mall) {
        UserBusinessGangInfo userBusinessGang = userBusinessGangService.getUserBusinessGang(uid);
        if (null == userBusinessGang) {
            return;
        }
        CfgNeedUnlockGoods unlockGood = BusinessGangCfgTool.getNeedUnlockGood(mall.getGoodsId(), mall.getItem());
        if (null == unlockGood) {
            return;
        }
        //获得好感度
        int favorability = 0;
        if (unlockGood.getCorrespondNpc() != 0) {
            favorability = userBusinessGang.getFavorability(unlockGood.getCorrespondNpc());
        }
        //获得商帮声望
        int prestige = 0;
        if (unlockGood.getCorrespondBang() != 0) {
            prestige = userBusinessGang.getPrestige(unlockGood.getCorrespondBang());
        }
        //判断是否解锁
        if (isUnlock(favorability, prestige, unlockGood)) {
            return;
        }
        throw new ExceptionForClientTip("store.goods.lock");
    }

    /**
     * 是否解锁
     *
     * @param favorability
     * @param prestige
     * @param unlockGood
     * @return
     */
    private boolean isUnlock(Integer favorability, Integer prestige, CfgNeedUnlockGoods unlockGood) {
        boolean hasFavorability = favorability >= unlockGood.getNeedFavorability();
        boolean hasPrestige = prestige >= unlockGood.getNeedPrestige();
        //判断是否解锁
        if (hasFavorability && hasPrestige) {
            return true;
        }
        return false;
    }

    /**
     * 获取城池产出
     *
     * @param uid
     * @return
     */
    public List<RDTradeInfo.RDCitySpecial> getCityOutPut(long uid) {
        //获得产出礼物
        List<RDTradeInfo.RDCitySpecial> citySpecialList = new ArrayList<>(refreshGift(uid));
        //获得产出铲子
        CfgBusinessGangEntity businessGangInfo = BusinessGangCfgTool.getBusinessGangInfo();
        Integer refreshShovelPro = businessGangInfo.getRefreshCopperShovelPro();
        if (PowerRandom.hitProbability(refreshShovelPro)) {
            citySpecialList.addAll(refreshShovel(uid));
        }
        return citySpecialList;
    }

    /**
     * 产出商帮物品
     *
     * @param uid
     * @return
     */
    public List<RDTradeInfo.RDCitySpecial> getBusinessGangOutput(long uid) {
        List<RDTradeInfo.RDCitySpecial> cityOutPutList = new ArrayList<>();
        //礼物
        CfgBusinessGangEntity businessGangInfo = BusinessGangCfgTool.getBusinessGangInfo();
        Integer refreshGiftsPro = businessGangInfo.getRefreshGiftsPro();
        if (PowerRandom.hitProbability(refreshGiftsPro)) {
            cityOutPutList.addAll(refreshGift(uid));
        }
        //铜铲子
        if (PowerRandom.hitProbability(10)) {
            cityOutPutList.addAll(refreshShovel(uid));
        }
        return cityOutPutList;
    }

    /**
     * 刷新铲子
     *
     * @param uid
     * @return
     */
    private List<RDTradeInfo.RDCitySpecial> refreshShovel(long uid) {
        CfgBusinessGangEntity businessGangInfo = BusinessGangCfgTool.getBusinessGangInfo();
        //获得条件
        UserBusinessGangInfo userBusinessGang = userBusinessGangService.getOrCreateUserBusinessGang(uid);
        Integer favorability = userBusinessGang.getFavorability(BusinessGangNpcEnum.CHI_ZL.getId());
        if (favorability < businessGangInfo.getUnlockCopperShovelNeedFavorability()) {
            return new ArrayList<>();
        }
        List<RDTradeInfo.RDCitySpecial> citySpecialList = new ArrayList<>();
        int copperShovel = TreasureEnum.COPPER_SHOVEL.getValue();
        citySpecialList.add(0, new RDTradeInfo.RDCitySpecial(copperShovel, 0, 0));
        return citySpecialList;
    }

    /**
     * 刷新礼物
     *
     * @param uid
     * @return
     */
    private List<RDTradeInfo.RDCitySpecial> refreshGift(long uid) {
        CfgBusinessGangEntity businessGangInfo = BusinessGangCfgTool.getBusinessGangInfo();
        UserBusinessGangInfo userBusinessGang = userBusinessGangService.getUserBusinessGang(uid);
        if (null == userBusinessGang) {
            return new ArrayList<>();
        }
        //获取正财商帮的声望
        Integer prestige = userBusinessGang.getPrestige(BusinessGangEnum.ZHENG_CAI.getType());
        Integer needPrestige = businessGangInfo.getUnlockGiftsNeedPrestige();
        if (prestige < needPrestige) {
            return new ArrayList<>();
        }
        //随机礼物
        List<RDTradeInfo.RDCitySpecial> citySpecialList = new ArrayList<>();
        List<Integer> lowGifts = BusinessGangCfgTool.getLowGifts();
        Integer gift = PowerRandom.getRandomFromList(lowGifts);
        //百分之20出高级礼物
        if (PowerRandom.hitProbability(20)) {
            List<Integer> advancedGifts = BusinessGangCfgTool.getHighGifts();
            gift = PowerRandom.getRandomFromList(advancedGifts);
        }
        citySpecialList.add(0, new RDTradeInfo.RDCitySpecial(gift, 0, 0));
        return citySpecialList;
    }

    /**
     * 增加声望
     *
     * @param uid
     * @param prestigeId
     * @param num
     */
    public void addPrestige(long uid, Integer prestigeId, int num) {
        //获得对应商帮
        Integer gangId = BusinessGangCfgTool.getAllPrestigeEntity().stream()
                .filter(p -> p.getPrestigeId().equals(prestigeId))
                .map(CfgPrestigeEntity::getBusinessGangId)
                .findFirst().orElse(0);
        //增加声望
        UserBusinessGangInfo userBusinessGang = userBusinessGangService.getOrCreateUserBusinessGang(uid);
        userBusinessGang.addPrestige(gangId, num);
        gameUserService.updateItem(userBusinessGang);
    }

    /**
     * 重置商帮任务刷新次数和领奖次数
     *
     * @param uid
     */
    public void checkResetTaskRefreshAndClaim(long uid) {
        UserBusinessGangTaskInfo gangTask = userBusinessGangService.getOrCreateUserBusinessGangTask(uid);
        //上一次重置时间
        Date lastResetTime = gangTask.getLastResetTime();
        boolean isToday = DateUtil.isToday(lastResetTime);
        if (isToday) {
            return;
        }
        UserBusinessGangTaskInfo.reset(gangTask);
        privilegeHand(uid, gangTask);
        gameUserService.updateItem(gangTask);
    }

    /**
     * 商帮任务特权处理
     *
     * @param uid
     * @param gangTask
     */
    public void privilegeHand(long uid, UserBusinessGangTaskInfo gangTask) {
        boolean ownTianLing = privilegeService.isOwnTianLing(uid);
        int newTotalAwardableNum = 0;
        if (ownTianLing) {
            newTotalAwardableNum = gangTask.getTotalAwardableNum() + 1;
        }
        boolean ownDiLing = privilegeService.isOwnDiLing(uid);
        int newTotalFreeRefreshNum = 0;
        if (ownDiLing) {
            newTotalFreeRefreshNum = gangTask.getTotalFreeRefresNum() + 2;
        }
        if (newTotalAwardableNum == 6) {
            gangTask.addTotalAwardableNum(1);
            gangTask.addAwardableNum(1);
            gameUserService.updateItem(gangTask);
        }
        if (newTotalFreeRefreshNum == 5) {
            gangTask.addTotalFreeRefreshTaskNum(2);
            gangTask.addFreeRefreshTaskNum(2);
            gameUserService.updateItem(gangTask);
        }
    }

    /**
     * 商帮声望衰减
     *
     * @param uid
     * @param currentBusinessGang
     */
    public void prestigeDecay(long uid, Integer currentBusinessGang) {
        CfgBusinessGangData businessGangData = BusinessGangCfgTool.getBusinessGangData(currentBusinessGang);
        //获得商帮敌对帮派
        List<Integer> hostilityGangs = businessGangData.getHostilityGang();
        if (hostilityGangs.isEmpty()) {
            return;
        }
        //获得商帮的信息
        UserBusinessGangInfo businessGang = userBusinessGangService.getOrCreateUserBusinessGang(uid);
        //最后一次加入商帮时间
        Date lastJoinGangTime = businessGang.getLastJoinGangTime();
        int days = DateUtil.getDaysBetween(lastJoinGangTime, DateUtil.now());
        if (days <= 0) {
            return;
        }
        int decayTimes;
        //最后一次衰减时间
        Date lastDecayTime = businessGang.getLastDecayTime();
        if (null == lastDecayTime) {
            decayTimes = days;
        } else {
            //获得衰减次数
            int needDecayTimes = DateUtil.getDaysBetween(lastDecayTime, DateUtil.now());
            decayTimes = Math.min(needDecayTimes, days);
        }
        if (decayTimes <= 0) {
            return;
        }
        //对立商帮的声望
        for (Integer hostilityGang : hostilityGangs) {
            Integer prestige = businessGang.getPrestige(hostilityGang);
            int needDeductPrestige = 0;
            for (int i = 1; i <= decayTimes; i++) {
                CfgReputationAndDecay decay = BusinessGangCfgTool.getDecayRule(prestige);
                if (decay == null) {
                    continue;
                }
                Integer decayNum = decay.getDecay();
                if (decayNum == 0) {
                    break;
                }
                prestige -= decayNum;
                needDeductPrestige += decayNum;
            }
            if (0 == needDeductPrestige) {
                continue;
            }
            //声望扣除
            Integer prestigeId = BusinessGangCfgTool.getPrestigeEntity(hostilityGang).getPrestigeId();
            TreasureEventPublisher.pubTDeductEvent(uid, prestigeId, needDeductPrestige, WayEnum.BUSINESS_GANG_DEDUCT, new RDCommon());
        }
        //更新衰减时间
        businessGang.setLastDecayTime(DateUtil.now());
        gameUserService.updateItem(businessGang);
    }

    /**
     * 获取特产运送任务特产
     *
     * @param task
     * @return
     */
    public List<Integer> getShippingTaskSpecials(UserBusinessGangSpecialtyShippingTask task) {
        List<String> specialsName = new ArrayList<>(task.getTargetAndProgress().keySet());
        return SpecialTool.getSpecials().stream().filter(s -> specialsName.contains(s.getName())).map(CfgSpecialEntity::getId).collect(Collectors.toList());
    }

    /**
     * 是否触发特殊野怪宝箱
     *
     * @param uid
     * @return
     */
    public boolean isSpecialYeGBox(long uid) {
        UserBusinessGangInfo userBusinessGang = userBusinessGangService.getUserBusinessGang(uid);
        if (null == userBusinessGang) {
            return false;
        }
        //获得解锁特殊野怪的商帮
        Integer unlockSpecialYeGBoxGang = BusinessGangCfgTool.getBusinessGangInfo().getUnlockSpecialYeGBoxGang();
        //获得声望
        Integer prestige = userBusinessGang.getPrestige(unlockSpecialYeGBoxGang);
        //获得概率
        Integer specialYeGBoxPro = BusinessGangCfgTool.getSpecialYeGBoxPro(prestige);
        if (null == specialYeGBoxPro) {
            return false;
        }
        return PowerRandom.hitProbability(specialYeGBoxPro);
    }

    /**
     * 是否提升阶级
     *
     * @return
     */
    public int specialUpgrade(GameUser gu, int specialId) {
        //是特产任务需要特产
        boolean isTaskSpecial = isSpecialTaskNeedSpecial(gu.getId(), specialId);
        if (isTaskSpecial) {
            return specialId;
        }
        UserBusinessGangInfo userBusinessGang = userBusinessGangService.getUserBusinessGang(gu.getId());
        //是否开启商帮功能
        if (null == userBusinessGang) {
            return specialId;
        }
        Integer npcFavorability = userBusinessGang.getFavorability(BusinessGangNpcEnum.LE_ZL.getId());
        Integer permanentOpenNeedFavorability = BusinessGangCfgTool.getPermanentOpenNeedFavorability();
        //是否永久开启
        boolean isPermanentOpen = npcFavorability >= permanentOpenNeedFavorability;
        //是否在正财商帮
        Integer currntBusinessGang = userBusinessGang.getCurrentBusinessGang();
        if (BusinessGangEnum.ZHENG_CAI.getType() != currntBusinessGang && !isPermanentOpen) {
            return specialId;
        }

        //获得特产升阶概率
        int specialUpgradeProb = BusinessGangCfgTool.getSpecialUpgradeProb(npcFavorability);
        //0概率
        if (0 == specialUpgradeProb && !isPermanentOpen) {
            return specialId;
        }
        //是否升阶
        boolean isUpgrade = PowerRandom.hitProbability(specialUpgradeProb);
        if (!isUpgrade) {
            return specialId;
        }
        //获得升阶对应关系
        CfgSpecialHierarchyMap cfgSpecialHierarchyMap = Cfg.I.get(specialId % 1000, CfgSpecialHierarchyMap.class);
        if (null == cfgSpecialHierarchyMap) {
            return specialId;
        }
        return specialId / 1000 * 1000 + cfgSpecialHierarchyMap.getToSpecialId();
    }

    /**
     * 是否特产任务需要特产
     *
     * @param uid
     * @param specialId
     * @return
     */
    public boolean isSpecialTaskNeedSpecial(long uid, Integer specialId) {
        List<UserBusinessGangSpecialtyShippingTask> tasks = userSpecialtyShippingTaskService.getTasks(uid);
        //是否有运输任务
        if (ListUtil.isEmpty(tasks)) {
            return false;
        }
        int cfgSpecialId = specialId % 1000;
        CfgSpecialEntity cfgSpecialEntity = SpecialTool.getSpecialById(cfgSpecialId);
        if (null == cfgSpecialEntity) {
            return false;
        }
        int specialtyShippingTaskNeedNum = 0;
        for (UserBusinessGangSpecialtyShippingTask task : tasks) {
            Integer difficulty = TaskTool.getTaskDifficulty(TaskGroupEnum.BUSINESS_GANG_SPECIALTY_SHIPPING_TASK, task.getBaseId());
            //获得任务规则
            CfgTaskRules cfgTaskRules = BusinessGangCfgTool.getShippingTaskRules(difficulty);
            if (null == cfgTaskRules) {
                continue;
            }
            //获得任务进度
            Map<String, Integer> targetAndProgress = task.getTargetAndProgress();
            if (MapUtil.isEmpty(targetAndProgress)) {
                continue;
            }
            for (Map.Entry<String, Integer> entry : targetAndProgress.entrySet()) {
                if (!cfgSpecialEntity.getName().equals(entry.getKey())) {
                    continue;
                }
                //计算商帮运输任务需要特产数量
                specialtyShippingTaskNeedNum += cfgTaskRules.getShippingNum() - entry.getValue();
            }
        }
        //背包特产数量
        int bagSpecialNum = userSpecialService.getOwnUnLockSpecialsByBaseId(uid, cfgSpecialId).size();
        userSpecialService.getOwnUnLockSpecialsByBaseId(uid, cfgSpecialId).size();
        //任务特产需要特产是否足够
        if (specialtyShippingTaskNeedNum <= bagSpecialNum) {
            return false;
        }
        return true;
    }

    /**
     * 更新商帮成就统计进度
     *
     * @param userBusinessGang
     * @param achievementId
     */
    public void updateGangAchievementProgress(UserBusinessGangInfo userBusinessGang, int achievementId) {
        Long uid = userBusinessGang.getGameUserId();
        BehaviorStatisticService service = statisticServiceFactory.getByBehaviorType(BehaviorType.BUSINESS_GANG);
        String key = service.getKey(uid, StatisticTypeEnum.NONE);
        switch (achievementId) {
            case 17080:
                Integer zhengCaiPrestige = userBusinessGang.getPrestige(BusinessGangEnum.ZHENG_CAI.getType());
                redisHashUtil.increment(key, BusinessGangEnum.ZHENG_CAI.getName(), zhengCaiPrestige);
                break;
            case 17090:
                Integer zhaoBaoPrestige = userBusinessGang.getPrestige(BusinessGangEnum.ZHAO_BAO.getType());
                redisHashUtil.increment(key, BusinessGangEnum.ZHAO_BAO.getName(), zhaoBaoPrestige);
                break;
            case 17100:
                Integer zhaoCaiPrestige = userBusinessGang.getPrestige(BusinessGangEnum.ZHAO_CAI.getType());
                redisHashUtil.increment(key, BusinessGangEnum.ZHAO_CAI.getName(), zhaoCaiPrestige);
                break;
            case 17130:
                Integer zhaoFavorability = userBusinessGang.getFavorability(BusinessGangNpcEnum.ZHAO_GM.getId());
                redisHashUtil.increment(key, BusinessGangNpcEnum.ZHAO_GM.getName(), zhaoFavorability);
                break;
            case 17140:
                Integer xiaoFavorability = userBusinessGang.getFavorability(BusinessGangNpcEnum.XIAO_S.getId());
                redisHashUtil.increment(key, BusinessGangNpcEnum.XIAO_S.getName(), xiaoFavorability);
                break;
            case 17150:
                Integer chenFavorability = userBusinessGang.getFavorability(BusinessGangNpcEnum.CHEN_JG.getId());
                redisHashUtil.increment(key, BusinessGangNpcEnum.CHEN_JG.getName(), chenFavorability);
                break;
            default:
                return;
        }
        //全量统计
        service.increment(uid, DateUtil.getTodayInt(), 1);
    }

    /**
     * 获取子服务可购买的物品
     *
     * @return
     */
    @Override
    public List<Integer> getAbleTradeGoodIds() {
        List<Integer> tradeGoodIds = new ArrayList<>();
        //添加好感度道具
        tradeGoodIds.addAll(BusinessGangCfgTool.getAllGiftInfos().stream().map(CfgGiftEntity::getGiftId).collect(Collectors.toList()));
        //添加铜铲子
        tradeGoodIds.add(TreasureEnum.COPPER_SHOVEL.getValue());
        return tradeGoodIds;
    }

    /**
     * 获取要购买的物品价格
     *
     * @param goodId
     * @return
     */
    @Override
    public int getTradeBuyPrice(int goodId) {
        //铜铲子价格
        if (TreasureEnum.COPPER_SHOVEL.getValue() == goodId) {
            return BusinessGangCfgTool.getBusinessGangInfo().getBuyCopperShovelNeedCopper();
        }
        //高级好感度道具价格
        CfgGiftEntity giftInfo = BusinessGangCfgTool.getGiftInfo(goodId);
        if (giftInfo.getGrade() == GiftsGradeEnum.ADVANCED.getGrade()) {
            return BusinessGangCfgTool.getBusinessGangInfo().getBuyHightGiftNeedCopper();
        }
        //低级好感度道具
        return BusinessGangCfgTool.getBusinessGangInfo().getBuyLowGiftNeedCopper();
    }

    /**
     * 获取某个批次中某个子服务的物品购买信息
     *
     * @param uid
     * @param specialIds
     * @return
     */
    @Override
    public List<BuyGoodInfo> getTradeBuyInfo(long uid, List<Integer> specialIds) {
        return IChengChiTradeService.super.getTradeBuyInfo(uid, specialIds);
    }
}
