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
 * ???????????????
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
     * ?????????????????????
     */
    public static final List<Integer> GIFTS = BusinessGangCfgTool.getAllGiftInfos().stream().map(CfgGiftEntity::getGiftId).collect(Collectors.toList());

    /**
     * ???????????????
     *
     * @param userBusinessGang
     * @param cfgNpcInfo
     * @param giftId
     * @param giftNum
     * @param rd
     * @return
     */
    public Integer addFavorability(UserBusinessGangInfo userBusinessGang, CfgNpcInfo cfgNpcInfo, int giftId, int giftNum, RDCommon rd) {
        //npc????????????
        Integer businessGang = cfgNpcInfo.getGangId();
        //??????????????????
        Integer favorability = userBusinessGang.getFavorability(cfgNpcInfo.getId());
        int addedFavorability = favorability;
        //???????????????
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
            //npc????????????
            Integer businessGangPrestige = userBusinessGang.getPrestige(businessGang);
            boolean isReachPrestige = businessGangPrestige >= rules.getNeedBusinessGangPrestige();
            //npc????????????????????????
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
        //????????????
        if (addedFavorability == favorability) {
            if (cfgNpcInfo.getType() == BusinessNpcTypeEnum.ZHANG_DUO_REN.getType()) {
                throw new ExceptionForClientTip("businessGang.not.send.gift");
            }
            throw new ExceptionForClientTip("businessGang.not.send.other.gift");
        }
        //???????????????????????????
        Long uid = userBusinessGang.getGameUserId();
        int hasAddFavorability = addedFavorability - favorability;
        BusinessGangEventPublisher.pubAddGangNpcFavorabilityEvent(uid, cfgNpcInfo.getId(), hasAddFavorability);
        TreasureEventPublisher.pubTDeductEvent(uid, giftId, giftNum, WayEnum.BUSINESS_GANG_SENDGIFTS, rd);
        return addedFavorability;
    }

    /**
     * ????????????
     *
     * @param userBusinessGang
     * @param cfgNpcInfo
     */
    public void checkUnlockBusinessGang(UserBusinessGangInfo userBusinessGang, CfgNpcInfo cfgNpcInfo) {
        if (cfgNpcInfo.getType() != BusinessNpcTypeEnum.ZHANG_DUO_REN.getType()) {
            return;
        }
        //????????????????????????
        Integer gangId = cfgNpcInfo.getGangId();
        boolean unlockBusinessGang = isBusinessGang(userBusinessGang.getGameUserId(), gangId);
        if (unlockBusinessGang) {
            return;
        }
        //????????????????????????
        Integer needFavorability = BusinessGangCfgTool.getBusinessGangInfo().getUnlockBusinessGangNeedFavorability();
        Integer npcInfoId = cfgNpcInfo.getId();
        Integer favorability = userBusinessGang.getFavorability(npcInfoId);
        if (favorability < needFavorability) {
            return;
        }
        userBusinessGang.unlockGang(gangId);
    }


    /**
     * ????????????????????????
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
     * ????????????????????????
     *
     * @param uid
     * @return
     */
    public boolean isJoinBusinessGang(long uid) {
        UserBusinessGangInfo userBusinessGang = userBusinessGangService.getUserBusinessGang(uid);
        return userBusinessGang.getCurrentBusinessGang() > 0;
    }

    /**
     * ???????????????
     *
     * @param uid
     */
    public void initialTask(long uid) {
        //????????????????????????
        int limitTaskNum = userBusinessGangLimitTaskService.getAllTasks(uid).size();
        //????????????????????????
        int shippingTaskNum = userSpecialtyShippingTaskService.getAllTasks(uid).size();
        //??????????????????
        int totalTaskNum = limitTaskNum + shippingTaskNum;
        CfgBusinessGangEntity businessGangInfo = BusinessGangCfgTool.getBusinessGangInfo();
        Integer taskNum = businessGangInfo.getBusinessGangTaskNum();
        for (int i = totalTaskNum; i < taskNum; i++) {
            //??????????????????
            generateTask(uid);
        }
    }

    /**
     * ??????????????????
     *
     * @param uid
     */
    public void generateWeeklyTask(long uid) {
        CfgBusinessGangEntity businessGangInfo = BusinessGangCfgTool.getBusinessGangInfo();
        UserBusinessGangInfo userBusinessGang = userBusinessGangService.getOrCreateUserBusinessGang(uid);
        //????????????????????????????????????
        Integer gang = businessGangInfo.getUnlockWeeklyTaskGang();
        Integer needPrestige = businessGangInfo.getUnlockWeeklyTaskNeedPrestige();
        //????????????????????????
        Integer prestige = userBusinessGang.getPrestige(gang);
        if (prestige < needPrestige) {
            return;
        }
        List<UserBusinessGangWeeklyTask> allTasks = userWeeklyTaskService.getAllTasks(uid);
        //??????????????????????????????????????????
        int weeklyTaskNum = allTasks.size();
        if (weeklyTaskNum == 0) {
            //??????????????????
            userWeeklyTaskService.makeUserTaskInstance(uid, null);
            return;
        }
        //??????????????????
        boolean refresh = userWeeklyTaskService.canRefresh(uid);
        if (!refresh) {
            return;
        }
        //???????????????????????????id
        Integer currentTaskId = allTasks.stream().map(UserCfgObj::getBaseId).findFirst().orElse(null);
        //??????????????????
        userWeeklyTaskService.delAllTask(uid);
        //??????????????????
        userWeeklyTaskService.makeUserTaskInstance(uid, currentTaskId);
    }

    /**
     * ??????????????????
     *
     * @param uid
     */
    public void generateTask(long uid) {
        UserBusinessGangInfo userBusinessGang = userBusinessGangService.getOrCreateUserBusinessGang(uid);
        //???????????????????????????
        Integer prestige = userBusinessGang.getPrestige(BusinessGangEnum.ZHENG_CAI.getType());
        CfgBusinessGangEntity businessGangInfo = BusinessGangCfgTool.getBusinessGangInfo();
        //????????????????????????
        int needPrestige = businessGangInfo.getTaskRefreshProbability().stream().filter(t -> prestige >= t.getNeedPrestige())
                .map(CfgBusinessGangEntity.TaskRefresh::getNeedPrestige).findFirst().orElse(0);
        List<CfgBusinessGangEntity.TaskRefresh> taskDifficultyList = businessGangInfo.getTaskRefreshProbability()
                .stream().filter(t -> needPrestige == t.getNeedPrestige()).collect(Collectors.toList());
        //????????????????????????
        List<Integer> pro = taskDifficultyList.stream().map(CfgBusinessGangEntity.TaskRefresh::getProbability).collect(Collectors.toList());
        int index = PowerRandom.getIndexByProbs(pro, 100);
        Integer difficulty = taskDifficultyList.get(index).getDifficulty();
        //???????????????????????????????????????
        Integer dispatchTaskRefreshPro = businessGangInfo.getDispatchTaskRefreshPro();
        boolean dispatchTask = PowerRandom.hitProbability(dispatchTaskRefreshPro);
        if (dispatchTask) {
            userBusinessGangLimitTaskService.makeUserTaskInstance(uid, difficulty);
            return;
        }
        userSpecialtyShippingTaskService.makeUserTaskInstance(uid, difficulty);
    }

    /**
     * ?????????????????????
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
        //????????????????????????
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
        //?????????????????????
        for (CfgNeedUnlockGoods notUnlockGood : notUnlockGoodsList) {
            Integer goodId = notUnlockGood.getGoodId();
            Integer item = notUnlockGood.getItem();
            rdMallList.getMallGoods().removeIf(g -> g.getRealId().equals(goodId) && g.getItem().equals(item));
        }
    }

    /**
     * ????????????????????????
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
        //???????????????
        int favorability = 0;
        if (unlockGood.getCorrespondNpc() != 0) {
            favorability = userBusinessGang.getFavorability(unlockGood.getCorrespondNpc());
        }
        //??????????????????
        int prestige = 0;
        if (unlockGood.getCorrespondBang() != 0) {
            prestige = userBusinessGang.getPrestige(unlockGood.getCorrespondBang());
        }
        //??????????????????
        if (isUnlock(favorability, prestige, unlockGood)) {
            return;
        }
        throw new ExceptionForClientTip("store.goods.lock");
    }

    /**
     * ????????????
     *
     * @param favorability
     * @param prestige
     * @param unlockGood
     * @return
     */
    private boolean isUnlock(Integer favorability, Integer prestige, CfgNeedUnlockGoods unlockGood) {
        boolean hasFavorability = favorability >= unlockGood.getNeedFavorability();
        boolean hasPrestige = prestige >= unlockGood.getNeedPrestige();
        //??????????????????
        if (hasFavorability && hasPrestige) {
            return true;
        }
        return false;
    }

    /**
     * ??????????????????
     *
     * @param uid
     * @return
     */
    public List<RDTradeInfo.RDCitySpecial> getCityOutPut(long uid) {
        //??????????????????
        List<RDTradeInfo.RDCitySpecial> citySpecialList = new ArrayList<>(refreshGift(uid));
        //??????????????????
        CfgBusinessGangEntity businessGangInfo = BusinessGangCfgTool.getBusinessGangInfo();
        Integer refreshShovelPro = businessGangInfo.getRefreshCopperShovelPro();
        if (PowerRandom.hitProbability(refreshShovelPro)) {
            citySpecialList.addAll(refreshShovel(uid));
        }
        return citySpecialList;
    }

    /**
     * ??????????????????
     *
     * @param uid
     * @return
     */
    public List<RDTradeInfo.RDCitySpecial> getBusinessGangOutput(long uid) {
        List<RDTradeInfo.RDCitySpecial> cityOutPutList = new ArrayList<>();
        //??????
        CfgBusinessGangEntity businessGangInfo = BusinessGangCfgTool.getBusinessGangInfo();
        Integer refreshGiftsPro = businessGangInfo.getRefreshGiftsPro();
        if (PowerRandom.hitProbability(refreshGiftsPro)) {
            cityOutPutList.addAll(refreshGift(uid));
        }
        //?????????
        if (PowerRandom.hitProbability(10)) {
            cityOutPutList.addAll(refreshShovel(uid));
        }
        return cityOutPutList;
    }

    /**
     * ????????????
     *
     * @param uid
     * @return
     */
    private List<RDTradeInfo.RDCitySpecial> refreshShovel(long uid) {
        CfgBusinessGangEntity businessGangInfo = BusinessGangCfgTool.getBusinessGangInfo();
        //????????????
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
     * ????????????
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
        //???????????????????????????
        Integer prestige = userBusinessGang.getPrestige(BusinessGangEnum.ZHENG_CAI.getType());
        Integer needPrestige = businessGangInfo.getUnlockGiftsNeedPrestige();
        if (prestige < needPrestige) {
            return new ArrayList<>();
        }
        //????????????
        List<RDTradeInfo.RDCitySpecial> citySpecialList = new ArrayList<>();
        List<Integer> lowGifts = BusinessGangCfgTool.getLowGifts();
        Integer gift = PowerRandom.getRandomFromList(lowGifts);
        //?????????20???????????????
        if (PowerRandom.hitProbability(20)) {
            List<Integer> advancedGifts = BusinessGangCfgTool.getHighGifts();
            gift = PowerRandom.getRandomFromList(advancedGifts);
        }
        citySpecialList.add(0, new RDTradeInfo.RDCitySpecial(gift, 0, 0));
        return citySpecialList;
    }

    /**
     * ????????????
     *
     * @param uid
     * @param prestigeId
     * @param num
     */
    public void addPrestige(long uid, Integer prestigeId, int num) {
        //??????????????????
        Integer gangId = BusinessGangCfgTool.getAllPrestigeEntity().stream()
                .filter(p -> p.getPrestigeId().equals(prestigeId))
                .map(CfgPrestigeEntity::getBusinessGangId)
                .findFirst().orElse(0);
        //????????????
        UserBusinessGangInfo userBusinessGang = userBusinessGangService.getOrCreateUserBusinessGang(uid);
        userBusinessGang.addPrestige(gangId, num);
        gameUserService.updateItem(userBusinessGang);
    }

    /**
     * ?????????????????????????????????????????????
     *
     * @param uid
     */
    public void checkResetTaskRefreshAndClaim(long uid) {
        UserBusinessGangTaskInfo gangTask = userBusinessGangService.getOrCreateUserBusinessGangTask(uid);
        //?????????????????????
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
     * ????????????????????????
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
     * ??????????????????
     *
     * @param uid
     * @param currentBusinessGang
     */
    public void prestigeDecay(long uid, Integer currentBusinessGang) {
        CfgBusinessGangData businessGangData = BusinessGangCfgTool.getBusinessGangData(currentBusinessGang);
        //????????????????????????
        List<Integer> hostilityGangs = businessGangData.getHostilityGang();
        if (hostilityGangs.isEmpty()) {
            return;
        }
        //?????????????????????
        UserBusinessGangInfo businessGang = userBusinessGangService.getOrCreateUserBusinessGang(uid);
        //??????????????????????????????
        Date lastJoinGangTime = businessGang.getLastJoinGangTime();
        int days = DateUtil.getDaysBetween(lastJoinGangTime, DateUtil.now());
        if (days <= 0) {
            return;
        }
        int decayTimes;
        //????????????????????????
        Date lastDecayTime = businessGang.getLastDecayTime();
        if (null == lastDecayTime) {
            decayTimes = days;
        } else {
            //??????????????????
            int needDecayTimes = DateUtil.getDaysBetween(lastDecayTime, DateUtil.now());
            decayTimes = Math.min(needDecayTimes, days);
        }
        if (decayTimes <= 0) {
            return;
        }
        //?????????????????????
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
            //????????????
            Integer prestigeId = BusinessGangCfgTool.getPrestigeEntity(hostilityGang).getPrestigeId();
            TreasureEventPublisher.pubTDeductEvent(uid, prestigeId, needDeductPrestige, WayEnum.BUSINESS_GANG_DEDUCT, new RDCommon());
        }
        //??????????????????
        businessGang.setLastDecayTime(DateUtil.now());
        gameUserService.updateItem(businessGang);
    }

    /**
     * ??????????????????????????????
     *
     * @param task
     * @return
     */
    public List<Integer> getShippingTaskSpecials(UserBusinessGangSpecialtyShippingTask task) {
        List<String> specialsName = new ArrayList<>(task.getTargetAndProgress().keySet());
        return SpecialTool.getSpecials().stream().filter(s -> specialsName.contains(s.getName())).map(CfgSpecialEntity::getId).collect(Collectors.toList());
    }

    /**
     * ??????????????????????????????
     *
     * @param uid
     * @return
     */
    public boolean isSpecialYeGBox(long uid) {
        UserBusinessGangInfo userBusinessGang = userBusinessGangService.getUserBusinessGang(uid);
        if (null == userBusinessGang) {
            return false;
        }
        //?????????????????????????????????
        Integer unlockSpecialYeGBoxGang = BusinessGangCfgTool.getBusinessGangInfo().getUnlockSpecialYeGBoxGang();
        //????????????
        Integer prestige = userBusinessGang.getPrestige(unlockSpecialYeGBoxGang);
        //????????????
        Integer specialYeGBoxPro = BusinessGangCfgTool.getSpecialYeGBoxPro(prestige);
        if (null == specialYeGBoxPro) {
            return false;
        }
        return PowerRandom.hitProbability(specialYeGBoxPro);
    }

    /**
     * ??????????????????
     *
     * @return
     */
    public int specialUpgrade(GameUser gu, int specialId) {
        //???????????????????????????
        boolean isTaskSpecial = isSpecialTaskNeedSpecial(gu.getId(), specialId);
        if (isTaskSpecial) {
            return specialId;
        }
        UserBusinessGangInfo userBusinessGang = userBusinessGangService.getUserBusinessGang(gu.getId());
        //????????????????????????
        if (null == userBusinessGang) {
            return specialId;
        }
        Integer npcFavorability = userBusinessGang.getFavorability(BusinessGangNpcEnum.LE_ZL.getId());
        Integer permanentOpenNeedFavorability = BusinessGangCfgTool.getPermanentOpenNeedFavorability();
        //??????????????????
        boolean isPermanentOpen = npcFavorability >= permanentOpenNeedFavorability;
        //?????????????????????
        Integer currntBusinessGang = userBusinessGang.getCurrentBusinessGang();
        if (BusinessGangEnum.ZHENG_CAI.getType() != currntBusinessGang && !isPermanentOpen) {
            return specialId;
        }

        //????????????????????????
        int specialUpgradeProb = BusinessGangCfgTool.getSpecialUpgradeProb(npcFavorability);
        //0??????
        if (0 == specialUpgradeProb && !isPermanentOpen) {
            return specialId;
        }
        //????????????
        boolean isUpgrade = PowerRandom.hitProbability(specialUpgradeProb);
        if (!isUpgrade) {
            return specialId;
        }
        //????????????????????????
        CfgSpecialHierarchyMap cfgSpecialHierarchyMap = Cfg.I.get(specialId % 1000, CfgSpecialHierarchyMap.class);
        if (null == cfgSpecialHierarchyMap) {
            return specialId;
        }
        return specialId / 1000 * 1000 + cfgSpecialHierarchyMap.getToSpecialId();
    }

    /**
     * ??????????????????????????????
     *
     * @param uid
     * @param specialId
     * @return
     */
    public boolean isSpecialTaskNeedSpecial(long uid, Integer specialId) {
        List<UserBusinessGangSpecialtyShippingTask> tasks = userSpecialtyShippingTaskService.getTasks(uid);
        //?????????????????????
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
            //??????????????????
            CfgTaskRules cfgTaskRules = BusinessGangCfgTool.getShippingTaskRules(difficulty);
            if (null == cfgTaskRules) {
                continue;
            }
            //??????????????????
            Map<String, Integer> targetAndProgress = task.getTargetAndProgress();
            if (MapUtil.isEmpty(targetAndProgress)) {
                continue;
            }
            for (Map.Entry<String, Integer> entry : targetAndProgress.entrySet()) {
                if (!cfgSpecialEntity.getName().equals(entry.getKey())) {
                    continue;
                }
                //??????????????????????????????????????????
                specialtyShippingTaskNeedNum += cfgTaskRules.getShippingNum() - entry.getValue();
            }
        }
        //??????????????????
        int bagSpecialNum = userSpecialService.getOwnUnLockSpecialsByBaseId(uid, cfgSpecialId).size();
        userSpecialService.getOwnUnLockSpecialsByBaseId(uid, cfgSpecialId).size();
        //????????????????????????????????????
        if (specialtyShippingTaskNeedNum <= bagSpecialNum) {
            return false;
        }
        return true;
    }

    /**
     * ??????????????????????????????
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
        //????????????
        service.increment(uid, DateUtil.getTodayInt(), 1);
    }

    /**
     * ?????????????????????????????????
     *
     * @return
     */
    @Override
    public List<Integer> getAbleTradeGoodIds() {
        List<Integer> tradeGoodIds = new ArrayList<>();
        //?????????????????????
        tradeGoodIds.addAll(BusinessGangCfgTool.getAllGiftInfos().stream().map(CfgGiftEntity::getGiftId).collect(Collectors.toList()));
        //???????????????
        tradeGoodIds.add(TreasureEnum.COPPER_SHOVEL.getValue());
        return tradeGoodIds;
    }

    /**
     * ??????????????????????????????
     *
     * @param goodId
     * @return
     */
    @Override
    public int getTradeBuyPrice(int goodId) {
        //???????????????
        if (TreasureEnum.COPPER_SHOVEL.getValue() == goodId) {
            return BusinessGangCfgTool.getBusinessGangInfo().getBuyCopperShovelNeedCopper();
        }
        //???????????????????????????
        CfgGiftEntity giftInfo = BusinessGangCfgTool.getGiftInfo(goodId);
        if (giftInfo.getGrade() == GiftsGradeEnum.ADVANCED.getGrade()) {
            return BusinessGangCfgTool.getBusinessGangInfo().getBuyHightGiftNeedCopper();
        }
        //?????????????????????
        return BusinessGangCfgTool.getBusinessGangInfo().getBuyLowGiftNeedCopper();
    }

    /**
     * ?????????????????????????????????????????????????????????
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
