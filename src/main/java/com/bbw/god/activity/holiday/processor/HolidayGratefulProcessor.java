package com.bbw.god.activity.holiday.processor;

import com.bbw.common.DateUtil;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.activity.IActivity;
import com.bbw.god.activity.cfg.CfgThanksGiving;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.activity.processor.AbstractActivityProcessor;
import com.bbw.god.activity.rd.RDActivityExchangeInfo;
import com.bbw.god.cache.TimeLimitCacheUtil;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.gameuser.task.timelimit.cunz.CunZNPCEnum;
import com.bbw.god.gameuser.treasure.TreasureChecker;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 感恩节活动（感恩之举）
 *
 * @author fzj
 * @date 2021/11/16 15:18
 */
@Service
public class HolidayGratefulProcessor extends AbstractActivityProcessor {
    /** 可兑换的道具 */
    public static final List<Integer> TREASURE = Arrays.asList(50010, 11410, 11060, 11400, 10110);

    public HolidayGratefulProcessor() {
        this.activityTypeList = Arrays.asList(ActivityEnum.GRATEFUL);
    }

    /**
     * 是否在ui中展示
     *
     * @return
     */
    @Override
    public boolean isShowInUi(long uid) {
        return true;
    }

    @Override
    protected long getRemainTime(long uid, int sid, IActivity a) {
        if (a.gainEnd() != null) {
            return a.gainEnd().getTime() - System.currentTimeMillis();
        }
        return NO_TIME;
    }

    /**
     * 获得活动信息
     *
     * @param uid
     * @return
     */
    public RDActivityExchangeInfo getActivityInfo(long uid) {
        int sid = gameUserService.getActiveSid(uid);
        if (!isOpened(sid)) {
            throw new ExceptionForClientTip("activity.not.exist");
        }
        List<RDActivityExchangeInfo.RDExchangeInfo> rdExchangeInfos = new ArrayList<>();
        for (CunZNPCEnum npcEnum : CunZNPCEnum.values()) {
            int npcId = npcEnum.getType();
            rdExchangeInfos.add(RDActivityExchangeInfo.getInstance(npcId, getGratitude(uid, npcId), getExchangeTimes(uid, npcId)));
        }
        RDActivityExchangeInfo rd = new RDActivityExchangeInfo();
        //添加活动时间
        ActivityEnum activityEnum = ActivityEnum.fromValue(ActivityEnum.GRATEFUL.getValue());
        IActivity a = this.activityService.getActivity(sid, activityEnum);
        rd.setDateInfo(getRemainTime(uid, sid, a));

        rd.setExchangeInfo(rdExchangeInfos);
        return rd;
    }

    /**
     * 赠送食物获得好感度
     *
     * @param uid
     * @param npcId
     * @param foodId
     * @param num
     * @return
     */
    public RDActivityExchangeInfo getNpcGratitude(long uid, int npcId, int foodId, int num) {
        if (!isOpened(gameUserService.getActiveSid(uid))) {
            throw new ExceptionForClientTip("activity.is.timeout");
        }
        //检查食物数量
        TreasureChecker.checkIsEnough(foodId, num, uid);
        //获得好感度
        Integer gratitude = getGratitude(uid, npcId);
        Map<Integer, List<List<Integer>>> cunZNpcGratitude = CfgThanksGiving.getThanksGivingInfo().getCunZNpcGratitude();
        List<Integer> foodGratitude = cunZNpcGratitude.get(npcId).stream().filter(g -> g.get(0) == foodId).findFirst().orElse(null);
        RDActivityExchangeInfo rd = new RDActivityExchangeInfo();
        if (null == foodGratitude) {
            return rd;
        }

        if (CunZNPCEnum.LAO_ZHE.getType() != npcId) {
            //npc好感度满值以及可兑换次数
            List<List<Integer>> gratitudeAndTimes = CfgThanksGiving.getThanksGivingInfo().getNpcFullGratitudeAndTimes();
            //npc好感度上限
            Integer npcGratitude = gratitudeAndTimes.get(npcId / 10 - 1).get(0);
            //兑换次数上限
            Integer npcExchangeTimes = gratitudeAndTimes.get(npcId / 10 - 1).get(1);
            //当前兑换次数
            Integer exchangeTimes = getExchangeTimes(uid, npcId);
            //最终可兑换次数
            int finalExchangeTimes = exchangeTimes > 0 ? exchangeTimes : npcExchangeTimes;
            //好感度增加上限
            int gratitudeLimit = finalExchangeTimes * npcGratitude - gratitude;
            //溢出次数
            int overflowsNum = gratitudeLimit % foodGratitude.get(1) > 0 ? 1 : 0;
            //次数上限
            int numLimit = gratitudeLimit / foodGratitude.get(1) + overflowsNum;
            //最终兑换次数
            num = num > numLimit ? numLimit : num;
        }
        gratitude += foodGratitude.get(1) * num;

        //扣除食物
        TreasureEventPublisher.pubTDeductEvent(uid, foodId, num, WayEnum.THANKSGIVING_DAY, rd);
        //保存数据
        TimeLimitCacheUtil.cacheBothLocalAndRedis(uid, CunZNPCEnum.fromValue(npcId).getName(), gratitude, DateUtil.SECOND_ONE_DAY * 10);
        List<RDActivityExchangeInfo.RDExchangeInfo> rdExchangeInfo = new ArrayList<>();
        rdExchangeInfo.add(RDActivityExchangeInfo.getInstance(npcId, gratitude));
        rd.setExchangeInfo(rdExchangeInfo);
        return rd;
    }

    /**
     * 使用好感度兑换
     *
     * @param uid
     * @param npcId
     */
    public RDActivityExchangeInfo useGratitudeExchange(long uid, int npcId) {
        List<List<Integer>> gratitudeAndTimes =  CfgThanksGiving.getThanksGivingInfo().getNpcFullGratitudeAndTimes();
        Integer npcGratitude = gratitudeAndTimes.get(npcId / 10 - 1).get(0);
        Integer npcExchangeTimes = gratitudeAndTimes.get(npcId / 10 - 1).get(1);
        RDActivityExchangeInfo rd = new RDActivityExchangeInfo();
        //检查npc好感度
        Integer gratitude = getGratitude(uid, npcId);
        if (gratitude < npcGratitude) {
            throw new ExceptionForClientTip("exchange.not.valid");
        }
        //检查兑换次数
        Integer exchangeTimes = getExchangeTimes(uid, npcId);
        if (npcExchangeTimes != 0 && exchangeTimes >= npcExchangeTimes) {
            throw new ExceptionForClientTip("store.goods.limit");
        }
        //发放奖励
        sendAwards(uid, npcId, gratitude, exchangeTimes, rd);
        return rd;
    }

    /**
     * 发放奖励
     *
     * @param uid
     * @param npcId
     * @param rd
     */
    private void sendAwards(long uid, int npcId, int gratitude, int exchangeTimes, RDActivityExchangeInfo rd) {
        //发放老者奖励
        if (CunZNPCEnum.LAO_ZHE.getType() == npcId) {
            sendLaoZheAward(uid, rd);
            return;
        }
        //发放其他NPC奖励
        int num = 1;
        if (CunZNPCEnum.XIAO_HONG.getType() == npcId) {
            num = 9;
        }
        //扣除好感度
        List<List<Integer>> gratitudeAndTimes =  CfgThanksGiving.getThanksGivingInfo().getNpcFullGratitudeAndTimes();
        int nowGratitude = gratitude - gratitudeAndTimes.get(npcId / 10 - 1).get(0);
        TimeLimitCacheUtil.cacheBothLocalAndRedis(uid, CunZNPCEnum.fromValue(npcId).getName(), nowGratitude, DateUtil.SECOND_ONE_DAY * 10);
        //更新可兑换次数
        TimeLimitCacheUtil.cacheBothLocalAndRedis(uid, CunZNPCEnum.fromValue(npcId).getName() + "兑换", exchangeTimes + 1, DateUtil.SECOND_ONE_DAY * 10);
        //发放奖励
        TreasureEventPublisher.pubTAddEvent(uid, TREASURE.get(npcId / 10 - 1), num, WayEnum.THANKSGIVING_DAY,  rd);
        //回传
        List<RDActivityExchangeInfo.RDExchangeInfo> rdExchangeInfo = new ArrayList<>();
        rdExchangeInfo.add(RDActivityExchangeInfo.getInstance(npcId, nowGratitude, exchangeTimes + 1));
        rd.setExchangeInfo(rdExchangeInfo);
    }

    /**
     * 发放老者的奖励
     *
     * @param uid
     * @param rd
     */
    private void sendLaoZheAward(long uid, RDActivityExchangeInfo rd) {
        //扣除好感度
        Integer npcGratitude = CfgThanksGiving.getThanksGivingInfo().getNpcFullGratitudeAndTimes().get(CunZNPCEnum.LAO_ZHE.getType() / 10 - 1).get(0);
        int gratitude = getGratitude(uid, CunZNPCEnum.LAO_ZHE.getType()) - npcGratitude;
        TimeLimitCacheUtil.cacheBothLocalAndRedis(uid, CunZNPCEnum.LAO_ZHE.getName(), gratitude, DateUtil.SECOND_ONE_DAY * 10);
        //根据概率发放奖励
        Award award = CfgThanksGiving.randomAwardByProb(CfgThanksGiving.getThanksGivingInfo().getOldManRandomAwards());
        awardService.fetchAward(uid, Arrays.asList(award), WayEnum.THANKSGIVING_DAY, "", rd);
        //回传
        List<RDActivityExchangeInfo.RDExchangeInfo> rdExchangeInfo = new ArrayList<>();
        rdExchangeInfo.add(RDActivityExchangeInfo.getInstance(CunZNPCEnum.LAO_ZHE.getType(), gratitude, 0));
        rd.setExchangeInfo(rdExchangeInfo);
    }

    /**
     * 获取npc好感度
     *
     * @param uid
     * @param npcId
     * @return
     */
    private Integer getGratitude(long uid, int npcId) {
        //获取对应npc好感度
        Integer gratitude = TimeLimitCacheUtil.getFromCache(uid, CunZNPCEnum.fromValue(npcId).getName(), Integer.class);
        return null == gratitude ? 0 : gratitude;
    }

    /**
     * 获取npc可兑换次数
     *
     * @param uid
     * @param npcId
     * @return
     */
    private Integer getExchangeTimes(long uid, int npcId) {
        //获取对应npc的可兑换次数
        Integer exchangeTimes = TimeLimitCacheUtil.getFromCache(uid, CunZNPCEnum.fromValue(npcId).getName() + "兑换", Integer.class);
        return null == exchangeTimes ? 0 : exchangeTimes;
    }

    /**
     * 是否在活动期间
     *
     * @param sid
     * @return
     */
    private boolean isOpened(int sid) {
        ActivityEnum activityEnum = ActivityEnum.fromValue(ActivityEnum.GRATEFUL.getValue());
        IActivity a = this.activityService.getActivity(sid, activityEnum);
        return a != null;
    }


}
