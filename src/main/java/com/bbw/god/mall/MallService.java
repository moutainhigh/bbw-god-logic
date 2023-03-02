package com.bbw.god.mall;

import com.bbw.cache.UserCacheService;
import com.bbw.common.DateUtil;
import com.bbw.common.ListUtil;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.activity.ActivityService;
import com.bbw.god.activity.IActivity;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.activity.config.ActivityTool;
import com.bbw.god.activity.server.ServerActivityService;
import com.bbw.god.db.entity.CfgActivityEntity;
import com.bbw.god.game.config.mall.*;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.UserLoginInfo;
import com.bbw.god.rd.RDCommon;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MallService {
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private ActivityService activityService;
    @Autowired
    private UserCacheService userCacheService;
    @Autowired
    private ServerActivityService serverActivityService;

    /**
     * 获取已激活并生效中的助力礼包
     *
     * @param guId
     * @return
     */
    public List<Integer> getActiveZLLB(long guId) {
        List<Integer> activeIds = new ArrayList<>();
        List<UserMallRecord> umrs = getUserMallRecord(guId, MallEnum.ZLLB);
        if (umrs != null && umrs.size() > 0) {
            for (UserMallRecord umr : umrs) {
                CfgMallEntity mall = MallTool.getMall(umr.getBaseId());
                if (umr.getNum() < mall.getLimit() && umr.getDateTime().getTime() > System.currentTimeMillis()) {
                    activeIds.add(umr.getBaseId());
                }
            }
        }

        return activeIds;
    }

    /**
     * 激活助力礼包
     *
     * @param guId
     * @param zlMallId
     * @param rd
     */
    public void addZLRecord(long guId, int zlMallId, RDCommon rd) {
        UserMallRecord umRecord = getRecord(guId, zlMallId);
        if (umRecord == null) {
            umRecord = UserMallRecord.instanceZLRecord(guId, zlMallId);
            addRecord(umRecord);
            // rd.setActiveZLLB(zlMallId);
        }
    }

    /**
     * 获取当前购买记录
     *
     * @param guId
     * @param mallId
     * @return
     */
    public UserMallRecord getUserMallRecord(long guId, int mallId) {
        List<UserMallRecord> umrs = getRecords(guId);
        umrs = umrs.stream().filter(umr -> umr.getBaseId() == mallId && umr.ifValid()).collect(Collectors.toList());
        if (ListUtil.isNotEmpty(umrs)) {
            return umrs.get(umrs.size() - 1);
        }
        return null;
    }

    @NonNull
    public List<UserMallRecord> getRecords(long guId) {
        List<UserMallRecord> umrs = userCacheService.getUserDatas(guId, UserMallRecord.class);
        return umrs;
    }

    public UserMallRecord getRecord(long uid, int mallId) {
        return userCacheService.getCfgItem(uid, mallId, UserMallRecord.class);
    }

    public void addRecord(UserMallRecord umr) {
        userCacheService.addUserData(umr);
    }

    public void addRecords(List<UserMallRecord> umrs) {
        userCacheService.addUserDatas(umrs);
    }

    public void delRecords(List<UserMallRecord> umrs) {
        userCacheService.delUserDatas(umrs);
    }

    public void delRecord(UserMallRecord umr) {
        userCacheService.delUserData(umr);
    }


    /**
     * 获得商品记录
     *
     * @param guId
     * @param mallEnum
     * @return
     */
    @NonNull
    public List<UserMallRecord> getUserMallRecord(long guId, MallEnum mallEnum) {
        List<UserMallRecord> umrs = getRecords(guId).stream().filter(umr -> umr.getType() == mallEnum.getValue()).collect(Collectors.toList());
        return umrs;
    }

    /**
     * 获得有效的商品记录，即未过期的
     *
     * @param guId
     * @param mallEnum
     * @return
     */
    @NonNull
    public List<UserMallRecord> getUserValidMallRecord(long guId, MallEnum mallEnum) {
        List<UserMallRecord> umrs = getRecords(guId).stream().filter(umr -> umr.getType() == mallEnum.getValue() && umr.ifValid()).collect(Collectors.toList());
        return umrs;
    }

    /**
     * 商品结束时间，如果过去则返回现在
     *
     * @param guId
     * @param mall
     * @return
     */
    public Date getMallEndDate(long guId, CfgMallEntity mall) {
        // 新手七天礼包
        if (mall.getPeroid() == MallPeroidEnum.NEWER_DAY7.getValue()) {
            // TODO:可能存在空指针异常gameUserService.getSingleItem
            UserLoginInfo uLoginInfo = gameUserService.getSingleItem(guId, UserLoginInfo.class);
            return DateUtil.addSeconds(uLoginInfo.getEnrollTime(), DateUtil.SECOND_ONE_WEEK);
        }
        // 限时活动
        if (mall.getPeroid() == MallPeroidEnum.LIMIT_ACTIVITY_TIME.getValue()) {
            CfgActivityEntity activityByType = ActivityTool.getActivityByType(ActivityEnum.HOLIDAY_GIFT_PACK_51);
            if (activityByType == null) {
                return null;
            }
            return serverActivityService.getSa(gameUserService.getActiveSid(guId), activityByType).getEnd();
        }
        // 限时礼包
        if (mall.getPeroid() > MallPeroidEnum.LIMIT_TIME.getValue()) {
            return DateUtil.fromDateLong(mall.getPeroid());
        }
        return null;
    }

    /**
     * 获得可购买的商品goodsIds
     *
     * @param uid
     * @return
     */
    public List<Integer> getAbleBuyActivityMalls(long uid) {
        List<CfgMallEntity> mallEntities = MallTool.getMallConfig().getActivityMalls();
        List<Integer> goodsId = new ArrayList<>();
        for (CfgMallEntity mall : mallEntities) {
            int remainTimes = getActivityMallRemainTimes(uid, mall.getGoodsId());
            if (remainTimes > 0) {
                goodsId.add(mall.getGoodsId());
            }
        }
        return goodsId;
    }

    /**
     * 获得活动礼包的剩余购买次数
     *
     * @param uid
     * @param goodsId
     * @return
     */
    public int getActivityMallRemainTimes(long uid, int goodsId) {
        IActivity a = getMallBagActivity(uid, goodsId);
        if (a == null) {
            return 0;
        }
        ActivityEnum activity = ActivityEnum.fromValue(a.gainType());
        FavorableBagEnum favorableBag = null;
        switch (activity) {
            case GOD_POWER_SWEEP:
                favorableBag = FavorableBagEnum.GOD_POWER_SWEEP_LB;
                break;
            case BORN:
                favorableBag = FavorableBagEnum.BORN;
                break;
            default:
                return 0;
        }
        CfgMallEntity mall = MallTool.getMall(favorableBag.getType(), favorableBag.getValue());
        UserMallRecord umr = getUserMallRecord(uid, mall.getId());
        int remainTimes = mall.getLimit();
        if (umr != null) {
            // 本期记录
            if (DateUtil.isBetweenIn(umr.getDateTime(), a.gainBegin(), a.gainEnd())) {
                remainTimes -= umr.getNum();
            } else {
                // 删除往期的记录
                delRecord(umr);
            }
        }
        return remainTimes;
    }

    /**
     * @param uid
     * @param goodsId
     * @return
     */
    public IActivity getMallBagActivity(long uid, int goodsId) {
        int caType = MallTool.getMallConfig().getMallActivityBagMap().get(goodsId);
        int sId = gameUserService.getActiveSid(uid);
        IActivity a = activityService.getActivity(sId, ActivityEnum.fromValue(caType));
        return a;
    }

    /**
     * 购买商品
     *
     * @param guId
     * @param mall
     * @param buyNum
     * @return
     */
    public UserMallRecord checkRecord(long guId, CfgMallEntity mall, int buyNum,List<UserMallRecord> records) {
        int mallId = mall.getId();
        if (mall.getLimit() > 0) {// 有限制的商品
            if (buyNum > mall.getLimit()) {
                throw new ExceptionForClientTip("mall.is.outOfLimit", mall.getLimit().toString());
            }
            UserMallRecord record = null;
            if (records != null) {
                record = records.stream().filter(r -> r.getBaseId() == mallId).findFirst().orElse(null);
                // 是否有对应的有效纪录
                if (record != null) {
                    // 是否有购买次数
                    if (record.getNum() >= mall.getLimit()) {
                        if (mall.getPeroid() == 1) {
                            throw new ExceptionForClientTip("mall.unable.buy.today");
                        } else if (mall.getPeroid() == 7) {
                            throw new ExceptionForClientTip("mall.unable.buy.thisWeek");
                        } else {
                            throw new ExceptionForClientTip("mall.is.outOfLimit", mall.getLimit().toString());
                        }
                    }
                    // 是否超过限购次数
                    if (record.getNum() + buyNum > mall.getLimit()) {
                        throw new ExceptionForClientTip("mall.is.outOfLimit", mall.getLimit().toString());
                    }
                    // 是否超过限制时间
                    if (mall.getType() == MallEnum.ZLLB.getValue() && record.getDateTime().getTime() < System.currentTimeMillis()) {
                        throw new ExceptionForClientTip("mall.is.outOfDate");
                    }

                }
            }
            return record;
        } else {
            return getRecord(guId, mallId);
        }
    }

}