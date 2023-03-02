package com.bbw.god.gameuser.task.timelimit;

import com.bbw.common.DateUtil;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.card.UserCard;
import com.bbw.god.gameuser.card.UserCardService;
import com.bbw.god.gameuser.privilege.PrivilegeService;
import com.bbw.god.gameuser.res.ResChecker;
import com.bbw.god.gameuser.res.ResEventPublisher;
import com.bbw.god.gameuser.task.*;
import com.bbw.god.gameuser.task.timelimit.cunz.UserCunZTaskService;
import com.bbw.god.gameuser.treasure.TreasureChecker;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.rd.RDCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * 派遣类任务公共接口
 *
 * @author: suhq
 * @date: 2021/8/9 11:10 上午
 */
@Service
public class UserDispatchTaskLogic {
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private UserCardService userCardService;
    @Autowired
    private UserTimeLimitTaskService userTimeLimitTaskService;
    @Autowired
    private UserDispatchTaskService userDispatchTaskService;
    @Autowired
    private PrivilegeService privilegeService;
    @Autowired
    private UserCunZTaskService userCunZTaskService;
    @Autowired
    private UserDispatchCardVigorService dispatchCardVigorService;

    /**
     * 派遣卡牌
     *
     * @param uid
     * @param taskId
     * @param dispatchCardIds
     */
    public void disPatchCards(long uid, long taskId, List<Integer> dispatchCardIds) {
        //检查任务有效性
        UserTimeLimitTask ut = userTimeLimitTaskService.getTask(uid, taskId);
        if (null == ut) {
            throw ExceptionForClientTip.fromi18nKey("task.not.exist");
        }
        //获得是所有任务
        TaskGroupEnum taskGroupEnum = TaskGroupEnum.fromValue(ut.getGroup());
        List<UserTimeLimitTask> tasks = userTimeLimitTaskService.getTasks(uid, taskGroupEnum);
        //检查可执行任务是否达到上限
        int doingTaskNum = userTimeLimitTaskService.getDoingTaskNum(tasks);
        Integer maxTaskNum = userTimeLimitTaskService.getMaxDoingTaskNum(uid, taskGroupEnum);
        boolean isMaxDoingTaskNum = doingTaskNum >= maxTaskNum;

        //获得任务规则
        CfgTimeLimitTaskRules cfgTimeLimitTaskRules = TimeLimitTaskTool.getRules(taskGroupEnum);
        //检查排队中任务是否达到上限
        Integer maxQueuingTaskNum = cfgTimeLimitTaskRules.getMaxQueuingTaskNum();
        boolean isMaxQueuingTaskNum = true;
        if (maxQueuingTaskNum > 0) {
            int queuingTaskNum = userDispatchTaskService.getQueuingTaskNum(tasks);
            isMaxQueuingTaskNum = queuingTaskNum >= maxQueuingTaskNum;
        }
        //超过最大数量限制
        if (isMaxDoingTaskNum && isMaxQueuingTaskNum) {
            throw ExceptionForClientTip.fromi18nKey("task.not.implement");
        }
        //检查派遣卡牌
        List<UserCard> userCards = userCardService.getUserCards(uid, dispatchCardIds);
        checkDispatchCards(userCards, dispatchCardIds, cfgTimeLimitTaskRules);
        //检查任务状态
        checkTaskStatus(ut);
        // 精力数据
        UserCardVigor userCardVigor = gameUserService.getSingleItem(uid, UserCardVigor.class);
        if (null == userCardVigor) {
            userCardVigor = UserCardVigor.getInstance(uid);
            gameUserService.addItem(uid, userCardVigor);
        }
        //检查是否满足精力值
        CfgDispatchTaskRules dispatchRule = TimeLimitTaskTool.getDispatchRule(TaskGroupEnum.fromValue(ut.getGroup()), ut.getBaseId());
        for (UserCard userCard : userCards) {
            int cardVigor = dispatchCardVigorService.getCardVigor(TaskGroupEnum.fromValue(ut.getGroup()), userCard, userCardVigor);
            if (cardVigor < dispatchRule.getNeedCardVigor()) {
                throw new ExceptionForClientTip("task.dispatch.card.vigor.not.enough", userCard.getName());
            }
        }
        //更新精力值
        userCardVigor.updateCardVigors(userCards, dispatchRule.getNeedCardVigor());
        gameUserService.updateItem(userCardVigor);
        //更新任务状态
        if (isMaxDoingTaskNum) {
            ut.enterQueuing(dispatchCardIds);
        } else {
            ut.setDispatchCards(dispatchCardIds);
            Date dispatchDate = userDispatchTaskService.getDispatchDate(ut);
            ut.enterDoing(dispatchDate);
        }
        gameUserService.updateItem(ut);
    }

    /**
     * 检查派遣卡牌
     *
     * @param userCards
     * @param dispatchCardIds
     * @param cfgTimeLimitTaskRules
     */
    private void checkDispatchCards(List<UserCard> userCards, List<Integer> dispatchCardIds, CfgTimeLimitTaskRules cfgTimeLimitTaskRules) {
        //检查卡牌数量
        if (dispatchCardIds.size() != cfgTimeLimitTaskRules.getDispatchCardNum()) {
            throw new ExceptionForClientTip("task.dispatch.card.unvalid.num", cfgTimeLimitTaskRules.getDispatchCardNum());
        }
        //检查派遣卡牌是否存在
        if (userCards.size() < dispatchCardIds.size()) {
            throw ExceptionForClientTip.fromi18nKey("task.dispatch.card.not.own");
        }
    }

    /**
     * 检查任务状态
     *
     * @param ut
     */
    private void checkTaskStatus(UserTimeLimitTask ut) {
        //检查任务状态
        TaskStatusEnum taskStatus = ut.gainTaskStatus(DateUtil.now());
        if (TaskStatusEnum.TIME_OUT == taskStatus) {
            throw ExceptionForClientTip.fromi18nKey("task.time.out");
        }
        if (TaskStatusEnum.WAITING != taskStatus && TaskStatusEnum.FAIL != taskStatus) {
            throw ExceptionForClientTip.fromi18nKey("task.already.dispatch");
        }
        if (TaskStatusEnum.FAIL == taskStatus && ut.getGroup() != TaskGroupEnum.BUSINESS_GANG_DISPATCH_TASK.getValue()) {
            throw ExceptionForClientTip.fromi18nKey("task.is.over");
        }
    }

    /**
     * 派遣加速
     *
     * @param uid
     * @param speedupWay 加速方式 10 元宝 60神行符
     */
    public RDCommon disPatchSpeedup(long uid, long taskId, int speedupWay) {
        //检查状态，是否已派发，是否已达成
        //检查任务有效性
        Optional<UserTimeLimitTask> utOp = gameUserService.getUserData(uid, taskId, UserTimeLimitTask.class);
        if (!utOp.isPresent()) {
            throw ExceptionForClientTip.fromi18nKey("task.not.exist");
        }
        UserTimeLimitTask ut = utOp.get();
        //检查是否为派遣任务
        CfgTaskEntity taskEntity = TaskTool.getTaskEntity(TaskGroupEnum.fromValue(ut.getGroup()), ut.getBaseId());
        if (taskEntity.getType() != TaskTypeEnum.TIME_LIMIT_DISPATCH_TASK.getValue()) {
            throw ExceptionForClientTip.fromi18nKey("task.not.dispatch");
        }
        //检查任务状态
        TaskStatusEnum taskStatus = ut.gainTaskStatus(DateUtil.now());
        if (TaskStatusEnum.TIME_OUT == taskStatus) {
            throw ExceptionForClientTip.fromi18nKey("task.time.out");
        } else if (TaskStatusEnum.DOING != taskStatus) {
            throw ExceptionForClientTip.fromi18nKey("task.not.dispatch");
        }
        //检查需要的资源
        RDCommon rd = new RDCommon();
        CfgTimeLimitTaskRules rules = TimeLimitTaskTool.getRules(TaskGroupEnum.fromValue(ut.getGroup()));
        int remainSeconds = (int) ((ut.getTimeEnd().getTime() - System.currentTimeMillis()) / 1000);
        if (speedupWay == 10) {
            int needGold = remainSeconds / rules.getGoldSpeedUpSeconds() + (remainSeconds % rules.getGoldSpeedUpSeconds() == 0 ? 0 : 1);
            ResChecker.checkGold(gameUserService.getGameUser(uid), needGold);
            ResEventPublisher.pubGoldDeductEvent(uid, needGold, WayEnum.DISPATCH_TASK, rd);
        } else {
            int needSxf = remainSeconds / rules.getShenXSpeedUpSeconds() + (remainSeconds % rules.getShenXSpeedUpSeconds() == 0 ? 0 : 1);
            TreasureChecker.checkIsEnough(TreasureEnum.SHEN_XING_FU.getValue(), needSxf, uid);
            TreasureEventPublisher.pubTDeductEvent(uid, TreasureEnum.SHEN_XING_FU.getValue(), needSxf, WayEnum.DISPATCH_TASK, rd);
        }
        //更新时间及状态
        ut.setTimeEnd(DateUtil.addSeconds(DateUtil.now(), -1));
        userDispatchTaskService.updateDispatchResult(ut);
        return rd;
    }

    /**
     * 获取派遣信息
     *
     * @param uid
     * @param taskDataId
     * @return
     */
    public RDDispatchInfo getDispatchInfo(long uid, long taskDataId) {
        //检查任务有效性
        Optional<UserTimeLimitTask> utOp = gameUserService.getUserData(uid, taskDataId, UserTimeLimitTask.class);
        if (!utOp.isPresent()) {
            throw ExceptionForClientTip.fromi18nKey("task.not.exist");
        }
        UserTimeLimitTask ut = utOp.get();
        //检查任务状态
        TaskStatusEnum taskStatus = ut.gainTaskStatus(DateUtil.now());

        if (TaskStatusEnum.AWARDED == taskStatus) {
            throw ExceptionForClientTip.fromi18nKey("task.already.award");
        }
        return userDispatchTaskService.doGetDispatchInfo(ut, true);
    }
}
