package com.bbw.god.gameuser.businessgang;

import com.bbw.common.DateUtil;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.businessgang.Enum.BusinessNpcTypeEnum;
import com.bbw.god.gameuser.businessgang.cfg.CfgBusinessGangData;
import com.bbw.god.gameuser.businessgang.cfg.CfgBusinessGangEntity;
import com.bbw.god.gameuser.businessgang.cfg.CfgNpcInfo;
import com.bbw.god.gameuser.businessgang.rd.RDBusinessGangInfo;
import com.bbw.god.gameuser.businessgang.rd.RDBusinessGangNpcInfo;
import com.bbw.god.gameuser.businessgang.rd.RDEnterBusinessGang;
import com.bbw.god.gameuser.businessgang.user.UserBusinessGangInfo;
import com.bbw.god.gameuser.businessgang.user.UserBusinessGangTaskInfo;
import com.bbw.god.gameuser.card.UserCardService;
import com.bbw.god.gameuser.res.ResChecker;
import com.bbw.god.gameuser.res.ResEventPublisher;
import com.bbw.god.gameuser.task.TaskStatusEnum;
import com.bbw.god.gameuser.task.TaskTypeEnum;
import com.bbw.god.gameuser.task.businessgang.BusinessGangTaskProcessor;
import com.bbw.god.gameuser.task.businessgang.UserBusinessGangSpecialtyShippingTask;
import com.bbw.god.gameuser.task.businessgang.UserSpecialtyShippingTaskService;
import com.bbw.god.gameuser.task.timelimit.UserTimeLimitTask;
import com.bbw.god.gameuser.task.timelimit.businessGang.UserBusinessGangLimitTaskService;
import com.bbw.god.gameuser.treasure.TreasureChecker;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.rd.RDCommon;
import com.bbw.god.rd.RDSuccess;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 商帮主要逻辑
 *
 * @author fzj
 * @date 2022/1/14 14:47
 */
@Service
@Slf4j
public class BusinessGangLogic {
    @Autowired
    GameUserService gameUserService;
    @Autowired
    UserCardService userCardService;
    @Autowired
    BusinessGangService businessGangService;
    @Autowired
    UserBusinessGangService userBusinessGangService;
    @Autowired
    BusinessGangTaskProcessor businessGangTaskProcessor;
    @Autowired
    UserSpecialtyShippingTaskService userSpecialtyShippingTaskService;
    @Autowired
    UserBusinessGangLimitTaskService userBusinessGangLimitTaskService;

    /** 商帮任务集合 */
    private static final List<Integer> BUSINESS_GANG_TASKS = Arrays.asList(
            TaskTypeEnum.BUSINESS_GANG_DISPATCH_TASK.getValue(),
            TaskTypeEnum.BUSINESS_GANG_SHIPPING_TASK.getValue());

    /**
     * 进入商帮
     *
     * @param uid
     * @return
     */
    public RDEnterBusinessGang enter(long uid) {
        //检查是否达到开启等级
        CfgBusinessGangEntity businessGangInfo = BusinessGangCfgTool.getBusinessGangInfo();
        Integer openLevel = businessGangInfo.getOpenLevel();
        GameUser gameUser = gameUserService.getGameUser(uid);
        if (gameUser.getLevel() < openLevel) {
            throw new ExceptionForClientTip("businessGang.not.open");
        }
        //获得玩家商帮信息
        UserBusinessGangInfo userBusinessGang = userBusinessGangService.getOrCreateUserBusinessGang(uid);
        List<Integer> openedBusinessGangs = userBusinessGang.getOpenedBusinessGangs();
        RDEnterBusinessGang rd = new RDEnterBusinessGang();
        rd.setOpenedBusinessGangs(openedBusinessGangs);
        //获取未开启商帮
        List<Integer> unopenedBusinessGangs = businessGangInfo.getBusinessGangData().stream().filter(b -> !openedBusinessGangs
                .contains(b.getBusinessGangId())).map(CfgBusinessGangData::getBusinessGangId).collect(Collectors.toList());
        rd.setUnopenedBusinessGangs(unopenedBusinessGangs);
        Integer currentBusinessGang = userBusinessGang.getCurrentBusinessGang();
        if (0 == currentBusinessGang) {
            return rd;
        }
        //重置商帮任务领奖次数和刷新次数
        businessGangService.checkResetTaskRefreshAndClaim(uid);
        //衰减敌对商帮声望
        businessGangService.prestigeDecay(uid, currentBusinessGang);
        //初始化商帮任务
        businessGangService.initialTask(uid);
        //返回商帮信息
        RDBusinessGangInfo rdBusinessGangInfo = getBusinessGangInfo(userBusinessGang, currentBusinessGang);
        rd.setCurrentBusinessGang(currentBusinessGang);
        rd.setBusinessGangInfo(rdBusinessGangInfo);
        return rd;
    }

    /**
     * 加入商帮
     *
     * @param uid
     * @param joinBusinessGangId
     * @return
     */
    public RDEnterBusinessGang joinBusinessGang(long uid, Integer joinBusinessGangId) {
        UserBusinessGangInfo userBusinessGang = userBusinessGangService.getUserBusinessGang(uid);
        if (businessGangService.isJoinBusinessGang(uid)) {
            throw new ExceptionForClientTip("businessGang.not.join");
        }
        List<Integer> openedBusinessGangs = userBusinessGang.getOpenedBusinessGangs();
        if (!openedBusinessGangs.contains(joinBusinessGangId)) {
            throw new ExceptionForClientTip("businessGang.not.join");
        }
        userBusinessGang.setCurrentBusinessGang(joinBusinessGangId);
        userBusinessGang.setLastJoinGangTime(DateUtil.now());
        gameUserService.updateItem(userBusinessGang);
        RDEnterBusinessGang rd = new RDEnterBusinessGang();
        rd.setBusinessGangInfo(getBusinessGangInfo(userBusinessGang, joinBusinessGangId));
        return rd;
    }

    /**
     * 退出商帮
     *
     * @param uid
     * @return
     */
    public RDSuccess quitBusinessGang(long uid) {
        UserBusinessGangInfo userBusinessGang = userBusinessGangService.getUserBusinessGang(uid);
        userBusinessGang.setCurrentBusinessGang(0);
        gameUserService.updateItem(userBusinessGang);
        return new RDSuccess();
    }

    /**
     * 赠送礼物
     *
     * @param uid
     * @param npcId
     * @param giftsInfo
     * @return
     */
    public RDCommon sendGifts(long uid, Integer npcId, String giftsInfo) {
        CfgNpcInfo cfgNpcInfo = BusinessGangCfgTool.getNpcInfo(npcId);
        checkSendGifts(uid, cfgNpcInfo);
        UserBusinessGangInfo userBusinessGang = userBusinessGangService.getUserBusinessGang(uid);
        int addFavorability = 0;
        //赠送礼物
        RDBusinessGangNpcInfo rd = new RDBusinessGangNpcInfo();
        String[] gifts = giftsInfo.split(",");
        for (String gift : gifts) {
            int giftId = Integer.parseInt(gift.split("_")[0]);
            int num = Integer.parseInt(gift.split("_")[1]);
            TreasureChecker.checkIsEnough(giftId, num, uid);
            addFavorability = businessGangService.addFavorability(userBusinessGang, cfgNpcInfo, giftId, num, rd);
            //加好感度
            userBusinessGang.setNpcFavorability(npcId, addFavorability);
        }

        //解锁商帮
        businessGangService.checkUnlockBusinessGang(userBusinessGang, cfgNpcInfo);
        gameUserService.updateItem(userBusinessGang);

        rd.setNpcId(cfgNpcInfo.getId());
        rd.setFavorability(addFavorability);
        return rd;
    }

    /**
     * 是否可以送礼
     *
     * @param uid
     * @param cfgNpcInfo
     */
    private void checkSendGifts(long uid, CfgNpcInfo cfgNpcInfo) {
        if (cfgNpcInfo.getType() == BusinessNpcTypeEnum.ZHANG_DUO_REN.getType()) {
            return;
        }
        Integer gangId = cfgNpcInfo.getGangId();
        boolean unlockBusinessGang = businessGangService.isBusinessGang(uid, gangId);
        if (!unlockBusinessGang) {
            throw new ExceptionForClientTip("businessGang.unlock");
        }
    }

    /**
     * 拜访商帮
     *
     * @param uid
     * @param visitGangId
     * @return
     */
    public RDEnterBusinessGang visitBusinessGang(long uid, int visitGangId) {
        UserBusinessGangInfo userBusinessGang = userBusinessGangService.getOrCreateUserBusinessGang(uid);
        //获取商帮信息
        RDEnterBusinessGang rd = new RDEnterBusinessGang();
        rd.setCurrentBusinessGang(userBusinessGang.getCurrentBusinessGang());
        rd.setBusinessGangInfo(getBusinessGangInfo(userBusinessGang, visitGangId));
        return rd;
    }

    /**
     * 刷新任务
     *
     * @param uid
     * @param dataId
     * @param type
     */
    public RDCommon refreshTask(long uid, long dataId, int type) {
        if (!BUSINESS_GANG_TASKS.contains(type)) {
            throw new ExceptionForClientTip("businessGang.not.refresh");
        }
        taskRefreshCheck(uid, dataId, type);
        RDCommon rd = new RDCommon();
        //检查免费刷新次数
        UserBusinessGangTaskInfo userBusinessGangTask = userBusinessGangService.getOrCreateUserBusinessGangTask(uid);
        Integer freeRefreshNum = userBusinessGangTask.getFreeRefreshTaskNum();
        if (freeRefreshNum > 0) {
            userBusinessGangTask.setFreeRefreshTaskNum(freeRefreshNum - 1);
            gameUserService.updateItem(userBusinessGangTask);
            //生成新任务
            businessGangService.generateTask(uid);
            return rd;
        }
        //检查元宝
        Integer needGold = BusinessGangCfgTool.getBusinessGangInfo().getRefreshTaskNeedGold();
        GameUser gu = gameUserService.getGameUser(uid);
        ResChecker.checkGold(gu, needGold);
        //扣除元宝
        ResEventPublisher.pubGoldDeductEvent(uid, needGold, WayEnum.BUSINESS_GANG_REFRESH_TASK, rd);
        //生成新任务
        businessGangService.generateTask(uid);
        return rd;
    }

    /**
     * 任务刷新检查
     *
     * @param uid
     * @param dataId
     * @param type
     */
    private void taskRefreshCheck(long uid, long dataId, int type) {
        //派遣任务
        if (type == TaskTypeEnum.BUSINESS_GANG_DISPATCH_TASK.getValue()) {
            UserTimeLimitTask task = userBusinessGangLimitTaskService.getTask(uid, dataId);
            Integer status = task.getStatus();
            if (status == TaskStatusEnum.DOING.getValue() || status == TaskStatusEnum.ACCOMPLISHED.getValue()) {
                throw new ExceptionForClientTip("businessGang.not.refresh");
            }
            userBusinessGangLimitTaskService.delTask(uid, dataId);
        }
        //运送任务
        if (type == TaskTypeEnum.BUSINESS_GANG_SHIPPING_TASK.getValue()) {
            UserBusinessGangSpecialtyShippingTask task = userSpecialtyShippingTaskService.getTask(uid, dataId);
            Integer status = task.getStatus();
            if (status == TaskStatusEnum.ACCOMPLISHED.getValue()) {
                throw new ExceptionForClientTip("businessGang.not.refresh");
            }
            userSpecialtyShippingTaskService.delTask(uid, dataId);
        }
    }

    /**
     * 获取各商帮掌舵人信息
     *
     * @param uid
     * @return
     */
    public RDBusinessGangInfo gainBusinessGangNpcInfo(long uid) {
        UserBusinessGangInfo userBusinessGang = userBusinessGangService.getOrCreateUserBusinessGang(uid);
        RDBusinessGangInfo rd = new RDBusinessGangInfo();
        List<RDBusinessGangNpcInfo> rdnpcInfos = new ArrayList<>();
        List<CfgNpcInfo> npcInfos = BusinessGangCfgTool.getNpcInfos(BusinessNpcTypeEnum.ZHANG_DUO_REN.getType());
        for (CfgNpcInfo npc : npcInfos) {
            RDBusinessGangNpcInfo npcInfo = new RDBusinessGangNpcInfo();
            npcInfo.setNpcId(npc.getId());
            Integer favorability = userBusinessGang.getFavorability(npc.getId());
            npcInfo.setFavorability(favorability);
            npcInfo.setBangId(npc.getGangId());
            Integer prestige = userBusinessGang.getPrestige(npc.getGangId());
            npcInfo.setPrestige(prestige);
            rdnpcInfos.add(npcInfo);
        }
        rd.setNpcInfos(rdnpcInfos);
        return rd;
    }

    /**
     * 兑换领取次数
     *
     * @param uid
     * @return
     */
    public RDCommon exchangeAvailableTimes(long uid) {
        //检查令牌数量
        TreasureChecker.checkIsEnough(TreasureEnum.SHLP.getValue(), 1, uid);
        RDCommon rd = new RDCommon();
        //扣除令牌
        TreasureEventPublisher.pubTDeductEvent(uid, TreasureEnum.SHLP.getValue(), 1, WayEnum.BUSINESS_GANG_EXCHANGE, rd);
        //增加次数
        UserBusinessGangTaskInfo gangTask = userBusinessGangService.getOrCreateUserBusinessGangTask(uid);
        gangTask.addAwardableNum(1);
        gameUserService.updateItem(gangTask);
        return rd;
    }

    /**
     * 获得商帮信息
     *
     * @param userBusinessGang
     * @return
     */
    private RDBusinessGangInfo getBusinessGangInfo(UserBusinessGangInfo userBusinessGang, Integer businessGangId) {
        RDBusinessGangInfo rd = new RDBusinessGangInfo();
        rd.setId(businessGangId);
        //获得声望
        Integer prestige = userBusinessGang.getPrestige(businessGangId);
        rd.setPrestige(prestige);
        //获得商帮npc集合
        List<CfgNpcInfo> cfgNpcInfos = BusinessGangCfgTool.getNpcInfosByGang(businessGangId);
        List<RDBusinessGangNpcInfo> rdNpcInfos = new ArrayList<>();
        for (CfgNpcInfo cfgNpcInfo : cfgNpcInfos) {
            RDBusinessGangNpcInfo rdNpcInfo = new RDBusinessGangNpcInfo();
            rdNpcInfo.setNpcId(cfgNpcInfo.getId());
            Integer favorability = userBusinessGang.getFavorability(cfgNpcInfo.getId());
            rdNpcInfo.setFavorability(favorability);
            rdNpcInfos.add(rdNpcInfo);
        }
        rd.setNpcInfos(rdNpcInfos);
        return rd;
    }

}
