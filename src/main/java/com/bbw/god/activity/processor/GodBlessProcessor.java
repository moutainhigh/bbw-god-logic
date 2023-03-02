package com.bbw.god.activity.processor;

import com.bbw.common.DateUtil;
import com.bbw.common.ListUtil;
import com.bbw.common.PowerRandom;
import com.bbw.common.lock.SyncLockUtil;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.activity.IActivity;
import com.bbw.god.activity.UserActivity;
import com.bbw.god.activity.UserGodBlessRecord;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.activity.config.CfgGodBlessAward;
import com.bbw.god.activity.config.GodBlessTool;
import com.bbw.god.activity.rd.RDActivityItem;
import com.bbw.god.activity.rd.RDActivityList;
import com.bbw.god.db.entity.CfgActivityEntity;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardStatus;
import com.bbw.god.game.award.RDAward;
import com.bbw.god.game.award.RDAwards;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.pay.UserReceipt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author suchaobin
 * @description 上仙祝福处理器
 * @date 2020/10/20 9:57
 **/
@Service
public class GodBlessProcessor extends AbstractActivityProcessor {
    @Autowired
    private SyncLockUtil syncLockUtil;

    private static final Date BEGIN_TIME = DateUtil.fromDateTimeString("2020-11-03 15:30:00");

    public GodBlessProcessor() {
        this.activityTypeList = Arrays.asList(ActivityEnum.GOD_BLESS);
    }

    /**
     * 是否领取的该类活动的所有奖励
     *
     * @param uid
     * @param a
     * @return
     */
    @Override
    public Boolean isJoinAllActivities(long uid, IActivity a) {
        List<UserActivity> userActivities = activityService.getUserActivities(uid, a.gainId(), ActivityEnum.GOD_BLESS);
        return this.isAwardedAllAwards(uid, userActivities, ActivityEnum.GOD_BLESS);
    }

    /**
     * 该活动类别有多少个可领取的
     *
     * @param gu
     * @param a
     * @return
     */
    @Override
    public int getAbleAwardedNum(GameUser gu, IActivity a) {
        boolean isShow = gameUserService.getMultiItems(gu.getId(), UserReceipt.class).stream()
                .anyMatch(tmp -> tmp.getDeliveryTime().after(BEGIN_TIME));
        // 没充过钱，没有可领取的
        if (!isShow) {
            return 0;
        }
        RDActivityList rd = getActivities(gu.getId(), ActivityEnum.GOD_BLESS.getValue());
        List<RDActivityItem> activities = rd.getItems();
        List<Integer> statusList = activities.stream().map(RDActivityItem::getStatus).collect(Collectors.toList());
        int size = (int) statusList.stream().filter(tmp -> tmp.equals(AwardStatus.ENABLE_AWARD.getValue())).count();
        UserGodBlessRecord record = getUserGodBlessRecord(gu.getId());
        List<Integer> awardedCfgIds = record.gainAwardedCfgIds();
        awardedCfgIds = awardedCfgIds.stream().filter(tmp ->
                GodBlessTool.getAwardsById(tmp).getType().equals(10)).collect(Collectors.toList());
        // 普通奖励全部领取完了
        if (awardedCfgIds.size() == GodBlessTool.getAwards(10).size()) {
            return size;
        }
        // 普通奖励没有领取完，判断今天是否领取过普通奖励
        Date lastUpdateTime = record.getLastUpdateTime();
        // 今天已经领取过了
        if (null != lastUpdateTime && DateUtil.getTodayInt() == DateUtil.toDateInt(lastUpdateTime)) {
            return size;
        }
        return size + 1;
    }

    /**
     * 获得奖励
     *
     * @param uid
     * @param sId
     * @param ca
     * @param awardIndex
     * @return
     */
    @Override
    public RDAwards joinActivity(Long uid, int sId, int caId, CfgActivityEntity ca, int awardIndex) {
        RDAwards rd = new RDAwards();
        UserGodBlessRecord record = getUserGodBlessRecord(uid);
        check(record, awardIndex);
        CfgGodBlessAward cfgGodBlessAward = null;
        // 随机抽取普通奖励
        if (0 == caId) {
            List<Integer> awardedCfgIds = record.gainAwardedCfgIds();
            List<CfgGodBlessAward> cfgAwards = GodBlessTool.getAwards(10).stream().filter(tmp ->
                    !awardedCfgIds.contains(tmp.getId())).collect(Collectors.toList());
            if (ListUtil.isEmpty(cfgAwards)) {
                throw new ExceptionForClientTip("god.bless.get.all.awards");
            }
            // 从奖池中随机获取一个尚未获得过的奖励
            cfgGodBlessAward = PowerRandom.getRandomFromList(cfgAwards);
        } else {
            // 获取指定连线奖励
            cfgGodBlessAward = GodBlessTool.getAwardsById(caId);
        }
        List<Award> awards = cfgGodBlessAward.getAwards();
        // 发放奖励
        this.deliver(uid, WayEnum.ACTIVITY, "仙人祝福", awards, rd);
        // 添加记录
        rd.setAwards(awards.stream().map(RDAward::getInstance).collect(Collectors.toList()));
        record.addRecord(cfgGodBlessAward.getId(), awardIndex);
        record.setLastUpdateTime(DateUtil.now());
        gameUserService.updateItem(record);
        return rd;
    }

    /**
     * 是否领取所有奖励
     *
     * @param userActivities
     * @param activityEnum
     * @return
     */
    @Override
    public boolean isAwardedAllAwards(long uid, List<UserActivity> userActivities, ActivityEnum activityEnum) {
        UserGodBlessRecord record = getUserGodBlessRecord(uid);
        List<Integer> awardedCfgIds = record.gainAwardedCfgIds();
        return awardedCfgIds.size() == GodBlessTool.getAwards().size();
    }

    private void check(UserGodBlessRecord record, int index) {
        long uid = record.getGameUserId();
        boolean isShow = gameUserService.getMultiItems(uid, UserReceipt.class).stream()
                .anyMatch(tmp -> tmp.getDeliveryTime().after(BEGIN_TIME));
        // 没充过钱，没有可领取的
        if (!isShow) {
            throw new ExceptionForClientTip("god.bless.no.recharge");
        }
        // 普通奖励要判断今日是否开启过
        if (index < 26) {
            Date lastUpdateTime = record.getLastUpdateTime();
            // 检查今天是否已经开过了
            if (null != lastUpdateTime && DateUtil.getTodayInt() == DateUtil.toDateInt(lastUpdateTime)) {
                throw new ExceptionForClientTip("god.bless.today.opened");
            }
        }
        Map<String, String> recordMap = record.getRecordMap();
        Collection<String> values = recordMap.values();
        if (values.contains(String.valueOf(index))) {
            throw new ExceptionForClientTip("god.bless.index.repeat");
        }
        // 状态是否正确
        if (index >= 26) {
            CfgGodBlessAward cfgGodBlessAward = GodBlessTool.getAwardsByIndex(index);
            List<Integer> awardedIndexList = recordMap.values().stream().map(Integer::parseInt).collect(Collectors.toList());
            AwardStatus status = getStatus(cfgGodBlessAward.getId(), index, awardedIndexList);
            if (AwardStatus.ENABLE_AWARD != status) {
                throw new ExceptionForClientTip("god.bless.status.error");
            }
        }
    }

    /**
     * 获取活动详情
     *
     * @param uid
     * @param activityType
     * @return
     */
    @Override
    public RDActivityList getActivities(long uid, int activityType) {
        int sid = gameUserService.getActiveSid(uid);
        IActivity a = this.activityService.getActivity(sid, ActivityEnum.GOD_BLESS);
        if (a == null) {
            throw new ExceptionForClientTip("activity.not.exist");
        }
        RDActivityList rd = new RDActivityList();
        List<RDActivityItem> rdActivities = new ArrayList<>();
        UserGodBlessRecord userGodBlessRecord = getUserGodBlessRecord(uid);
        Map<String, String> record = userGodBlessRecord.getRecordMap();
        List<Integer> awardedIndexList = record.values().stream().map(Integer::parseInt).collect(Collectors.toList());
        Set<Map.Entry<String, String>> entries = record.entrySet();
        for (Map.Entry<String, String> entry : entries) {
            Integer cfgId = Integer.parseInt(entry.getKey());
            Integer index = Integer.parseInt(entry.getValue());
            CfgGodBlessAward cfgAward = GodBlessTool.getAwardsById(cfgId);
            RDActivityItem rdActivity = new RDActivityItem();
            rdActivity.setId(cfgId);
            rdActivity.setStatus(AwardStatus.AWARDED.getValue());
            List<Award> awards = cfgAward.getAwards();
            rdActivity.setAwards(awards);
            rdActivity.setIndex(index);
            rdActivity.setType(cfgAward.getType());
            rdActivities.add(rdActivity);
        }
        List<Integer> awardedCfgIds = userGodBlessRecord.gainAwardedCfgIds();
        // 未领取的连线奖励配置集合
        List<CfgGodBlessAward> cfgAwards = GodBlessTool.getAwards(20).stream().filter(tmp -> !awardedCfgIds.contains(tmp.getId())).collect(Collectors.toList());
        for (CfgGodBlessAward cfgAward : cfgAwards) {
            Integer cfgId = cfgAward.getId();
            Integer index = cfgAward.getIndex();
            RDActivityItem rdActivity = new RDActivityItem();
            rdActivity.setId(cfgId);
            int status = getStatus(cfgId, index, awardedIndexList).getValue();
            rdActivity.setStatus(status);
            List<Award> awards = cfgAward.getAwards();
            rdActivity.setAwards(awards);
            rdActivity.setIndex(index);
            rdActivity.setType(cfgAward.getType());
            rdActivities.add(rdActivity);
        }
        rd.setItems(rdActivities);
        Date lastUpdateTime = userGodBlessRecord.getLastUpdateTime();
        // 今天已经领取过了
        if (null != lastUpdateTime && DateUtil.getTodayInt() == DateUtil.toDateInt(lastUpdateTime)) {
            rd.setIsTodayOpened(1);
        }
        boolean isShow = gameUserService.getMultiItems(uid, UserReceipt.class).stream()
                .anyMatch(tmp -> tmp.getDeliveryTime().after(BEGIN_TIME));
        rd.setTotalProgress(isShow ? 1 : 0);
        return rd;
    }

    private AwardStatus getStatus(int cfgId, int index, List<Integer> awardedIndexList) {
        // 已领取
        if (awardedIndexList.contains(index)) {
            return AwardStatus.AWARDED;
        }
        CfgGodBlessAward cfgAward = GodBlessTool.getAwardsById(cfgId);
        // 未领取的普通奖励，统一返回不能领取
        if (cfgAward.getType().equals(10)) {
            return AwardStatus.UNAWARD;
        }
        // 连线奖励判断状态
        switch (index) {
            case 26:
            case 27:
            case 28:
            case 29:
            case 30:
                List<Integer> condition = Arrays.asList(index - 5, index - 10, index - 15, index - 20, index - 25);
                if (awardedIndexList.containsAll(condition)) {
                    return AwardStatus.ENABLE_AWARD;
                }
                return AwardStatus.UNAWARD;
            case 31:
                if (awardedIndexList.containsAll(Arrays.asList(21, 22, 23, 24, 25))) {
                    return AwardStatus.ENABLE_AWARD;
                }
                return AwardStatus.UNAWARD;
            case 32:
                if (awardedIndexList.containsAll(Arrays.asList(16, 17, 18, 19, 20))) {
                    return AwardStatus.ENABLE_AWARD;
                }
                return AwardStatus.UNAWARD;
            case 33:
                if (awardedIndexList.containsAll(Arrays.asList(11, 12, 13, 14, 15))) {
                    return AwardStatus.ENABLE_AWARD;
                }
                return AwardStatus.UNAWARD;
            case 34:
                if (awardedIndexList.containsAll(Arrays.asList(6, 7, 8, 9, 10))) {
                    return AwardStatus.ENABLE_AWARD;
                }
                return AwardStatus.UNAWARD;
            case 35:
                if (awardedIndexList.containsAll(Arrays.asList(1, 2, 3, 4, 5))) {
                    return AwardStatus.ENABLE_AWARD;
                }
                return AwardStatus.UNAWARD;
            default:
                return AwardStatus.UNAWARD;
        }
    }

    private UserGodBlessRecord getUserGodBlessRecord(long uid) {
        UserGodBlessRecord record = gameUserService.getSingleItem(uid, UserGodBlessRecord.class);
        if (null == record) {
            record = (UserGodBlessRecord) syncLockUtil.doSafe(String.valueOf(uid), tmp -> {
                UserGodBlessRecord godBlessRecord = gameUserService.getSingleItem(uid, UserGodBlessRecord.class);
                if (null == godBlessRecord) {
                    godBlessRecord = UserGodBlessRecord.getInstance(uid);
                    gameUserService.addItem(uid, godBlessRecord);
                }
                return godBlessRecord;
            });
        }
        return record;
    }
}
