package com.bbw.god.gameuser.task;

import com.bbw.common.ListUtil;
import com.bbw.common.Rst;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.activity.holiday.processor.holidayspecialcity.HolidayCelebrationInviteProcessor;
import com.bbw.god.controller.AbstractController;
import com.bbw.god.game.CR;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.task.businessgang.BusinessGangTaskProcessor;
import com.bbw.god.gameuser.task.timelimit.*;
import com.bbw.god.gameuser.task.timelimit.cunz.CunZTaskProcessor;
import com.bbw.god.gameuser.task.timelimit.dragonboatfestival.DragonBoatFestivalTaskProcessor;
import com.bbw.god.gameuser.task.timelimit.newyearandchrist.NewYearAndChristTaskProcessor;
import com.bbw.god.gameuser.task.timelimit.pailifawn.PaiLiFawnTaskProcessor;
import com.bbw.god.gameuser.task.timelimit.qingming.QingMingTaskProcessor;
import com.bbw.god.gameuser.task.timelimit.springfestival.SpringFestivalTaskProcessor;
import com.bbw.god.gameuser.task.timelimit.thanksgiving.ThanksgivingTaskProcessor;
import com.bbw.god.gameuser.task.timelimit.wansj.WanSJTaskProcessor;
import com.bbw.god.notify.rednotice.RedNoticeService;
import com.bbw.god.rd.RDCommon;
import com.bbw.god.rd.RDSuccess;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

/**
 * 任务相关接口，包括新手/进阶任务、每日任务、主线任务
 *
 * @author suhq
 * @date 2018年11月1日 上午10:36:52
 */
@RestController
public class UserTaskCtrl extends AbstractController {
    @Autowired
    private UserTaskLogic taskLogic;
    @Autowired
    private RedNoticeService redNoticeService;
    @Autowired
    private CunZTaskProcessor cunZTaskProcessor;
    @Autowired
    private WanSJTaskProcessor wanSJTaskProcessor;
    @Autowired
    private UserDispatchTaskLogic userDispatchTaskLogic;
    @Autowired
    private UserTimeLimitTaskService userTimeLimitTaskService;
    @Autowired
    private ThanksgivingTaskProcessor thanksgivingTaskProcessor;
    @Autowired
    private HolidayCelebrationInviteProcessor holidayCelebrationInviteProcessor;
    @Autowired
    private NewYearAndChristTaskProcessor newYearAndChristTaskProcessor;
    @Autowired
    private SpringFestivalTaskProcessor springFestivalTaskProcessor;
    @Autowired
    private BusinessGangTaskProcessor businessGangTaskProcessor;
    @Autowired
    private QingMingTaskProcessor qingMingTaskProcessor;
    @Autowired
    private DragonBoatFestivalTaskProcessor dragonBoatFestivalTaskProcessor;
    @Autowired
    private UserDispatchCardVigorLogic userDispatchCardVigorService;
    @Autowired
    private UserTimeLimitTaskLogic userTimeLimitTaskLogic;
    @Autowired
    private PaiLiFawnTaskProcessor paiLiFawnTaskProcessor;

    /**
     * 获得新手/进阶任务
     *
     * @return
     */
    @GetMapping(CR.Task.GAIN_TASKS)
    public RDTaskList getTasks(Long uid, int type, Integer days) {
        uid = uid == null ? getUserId() : uid;
        return this.taskLogic.getTasks(uid, type, days);
    }

    @GetMapping(CR.Task.GAIN_TASKS_V2)
    public RDTaskList getTaskItems(Long uid, int type, Integer days) {
        uid = uid == null ? getUserId() : uid;
        return this.taskLogic.getTasks(uid, type, days);
    }

    @GetMapping(CR.Task.SET_TASK_AWARD_INDEX)
    public Rst setAwardIndex(int type, int id, String awardIndex) {
        awardIndex = awardIndex == null ? "-1" : awardIndex;
        this.taskLogic.setTaskAwardIndex(getUserId(), type, id, awardIndex);
        return Rst.businessOK();
    }

    /**
     * 获得任务奖励
     *
     * @param id
     * @return
     */
    @GetMapping(CR.Task.GAIN_AWARD)
    public RDCommon gainTaskAward(Long uid, int type, int id, String awardIndex) {
        //LoginPlayer player = this.getUser();
        uid = uid == null ? getUserId() : uid;
        awardIndex = awardIndex == null ? "-1" : awardIndex;
        return this.taskLogic.gainTaskAward(uid, type, id, awardIndex);
    }

    /**
     * 任务奖励一键领取
     *
     * @param type
     * @return
     */
    @GetMapping(CR.Task.GAIN_BATCH_AWARD)
    public RDCommon gainBatchAward(int type) {
        return this.taskLogic.gainBatchTaskAward(getUserId(), type);
    }

    /**
     * 获得可重复任务奖励
     *
     * @param dataId
     * @return
     */
    @GetMapping(CR.Task.GAIN_REPEATABLE_TASK_AWARD)
    public RDCommon gainRepeatableTaskAward(long dataId) {
        Optional<UserTimeLimitTask> utOp = gameUserService.getUserData(getUserId(), dataId, UserTimeLimitTask.class);
        if (!utOp.isPresent()) {
            throw ExceptionForClientTip.fromi18nKey("task.not.exist");
        }
        UserTimeLimitTask ut = utOp.get();
        TaskGroupEnum taskGroup = TaskGroupEnum.fromValue(ut.getGroup());
        switch (taskGroup){
            case WAN_SHENG_JIE_TASK:
                return wanSJTaskProcessor.gainTaskAward(ut);
            case THANKS_GIVING_TASK:
                return thanksgivingTaskProcessor.gainTaskAward(ut);
            case CELEBRATION_INVITATION_TASK:
                return holidayCelebrationInviteProcessor.gainTaskAward(ut);
            case NEW_YEAR_AND_CHRISTMAS_TASK:
                return newYearAndChristTaskProcessor.gainTaskAward(ut);
            case SPRING_FESTIVAL_TASK:
                return springFestivalTaskProcessor.gainTaskAward(ut);
            case QING_MING_TASK:
                return qingMingTaskProcessor.gainTaskAward(ut);
            case DRAGON_BOAT_FESTIVAL_TASK:
                return dragonBoatFestivalTaskProcessor.gainTaskAward(ut);
            case PAI_LI_FAWN_51:
                return paiLiFawnTaskProcessor.gainTaskAward(ut);
            default:
                return cunZTaskProcessor.gainTaskAward(ut);
        }
    }

    @ApiOperation(value = "领取商帮任务奖励")
    @GetMapping(CR.Task.BUSINESS_GANG_TASK_AWARD)
    public RDCommon joinBusinessGang(Integer type, long dataId) {
        return businessGangTaskProcessor.gainTaskAward(getUserId(), type, dataId);
    }

    @GetMapping(CR.Task.GAIN_TASK_NOTICE)
    public RDTaskNotice gainTaskNotices() {
        RDTaskNotice rd = new RDTaskNotice();
        rd.addRedNotice(redNoticeService.getDailyTaskNotice(getUserId()));
        GameUser gu = gameUserService.getGameUser(getUserId());
        rd.addRedNotice(redNoticeService.getGodTrainingTaskNotice(gu));
        return rd;
    }

    @GetMapping(CR.Task.GET_TASK_INFO)
    public RDSuccess getTaskInfo(int taskType) {
        if (TaskTypeEnum.THANKS_GIVING_TASK.getValue() == taskType) {
            return thanksgivingTaskProcessor.getTask(getUserId());
        }
        if (TaskTypeEnum.NEW_YEAR_AND_CHRISTMAS_TASK.getValue() == taskType) {
            return newYearAndChristTaskProcessor.getTask(getUserId());
        }
        if (TaskTypeEnum.SPRING_FESTIVAL_TASK.getValue() == taskType) {
            return springFestivalTaskProcessor.getTask(getUserId());
        }
        if (TaskTypeEnum.QING_MING_TASK.getValue() == taskType) {
            return qingMingTaskProcessor.getTask(getUserId());
        }
        if (TaskTypeEnum.DRAGON_BOAT_FESTIVAL_TASK.getValue() == taskType) {
            return dragonBoatFestivalTaskProcessor.getTask(getUserId());
        }
        return wanSJTaskProcessor.getTask(getUserId());
    }

    /**
     * 放弃任务
     *
     * @param dataId
     * @return
     */
    @GetMapping(CR.Task.ABANDOM_TASK)
    public RDSuccess abandom(long dataId) {
        return userTimeLimitTaskLogic.abandom(getUserId(), dataId);
    }

    /**
     * 派遣信息
     *
     * @param dataId
     * @return
     */
    @GetMapping(CR.Task.GET_TASK_DISPATCHED_INFO)
    public RDSuccess getDispatchInfo(long dataId) {
        return userDispatchTaskLogic.getDispatchInfo(getUserId(), dataId);
    }

    /**
     * 获取派遣卡牌列表
     *
     * @return
     */
    @GetMapping(CR.Task.LIST_TASK_DISPATCH_CARDS)
    public RDSuccess listDispatchCards() {
        return userDispatchCardVigorService.getCardVigors(getUserId(), TaskGroupEnum.CUN_ZHUANG_TASK);
    }

    /**
     * 派遣
     *
     * @param dataId
     * @return
     */
    @GetMapping(CR.Task.TASK_DISPATCH)
    public RDSuccess dispatch(long dataId, String cardIds) {
        List<Integer> cardIdList = ListUtil.parseStrToInts(cardIds);
        userDispatchTaskLogic.disPatchCards(getUserId(), dataId, cardIdList);
        return new RDSuccess();
    }

    /**
     * 重新开始
     *
     * @param dataId
     * @param cardIds
     * @return
     */
    @GetMapping(CR.Task.RESTART)
    public RDSuccess restart(long dataId, String cardIds) {
        userTimeLimitTaskService.reset(getUserId(), dataId);
        return dispatch(dataId, cardIds);
    }

    /**
     * 派遣加速
     *
     * @param dataId
     * @param speedupWay 10元宝 60神行符
     * @return
     */
    @GetMapping(CR.Task.TASK_DISPATCH_SPEEDUP)
    public RDCommon dispatchSpeedup(long dataId, int speedupWay) {
        return userDispatchTaskLogic.disPatchSpeedup(getUserId(), dataId, speedupWay);
    }

    /**
     * 恢复卡牌精力
     * @param cardIds
     * @param beePulpNum
     * @return
     */
    @ApiOperation(value = "恢复卡牌精力")
    @GetMapping(CR.Task.RECOVER_VIGOR)
    public RDCommon recoverCardVigor(String cardIds, Integer beePulpNum) {
        return userDispatchCardVigorService.recoverCardVigor(getUserId(), cardIds, beePulpNum);
    }

    @Deprecated
    @GetMapping(CR.Task.GAIN_DAILY_TASKS)
    public Rst error1() {
        return Rst.businessFAIL("当前版本过旧，请重新启动游戏以完成更新！");
    }

    @Deprecated
    @GetMapping(CR.Task.REFRESH_DAILY_TASKS)
    public Rst error2() {
        return Rst.businessFAIL("当前版本过旧，请重新启动游戏以完成更新！");
    }
    @Deprecated
    @GetMapping(CR.Task.GAIN_MAIN_TASK_AWARD)
    public Rst error3() {
        return Rst.businessFAIL("当前版本过旧，请重新启动游戏以完成更新！");
    }
    @Deprecated
    @GetMapping(CR.Task.GAIN__DAILY_AWARD)
    public Rst error4() {
        return Rst.businessFAIL("当前版本过旧，请重新启动游戏以完成更新！");
    }
}
