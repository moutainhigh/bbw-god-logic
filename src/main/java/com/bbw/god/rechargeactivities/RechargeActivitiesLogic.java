package com.bbw.god.rechargeactivities;

import com.bbw.common.ListUtil;
import com.bbw.common.lock.SyncLockUtil;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.config.CfgProductGroup;
import com.bbw.god.game.config.mall.CfgMallEntity;
import com.bbw.god.game.config.mall.MallEnum;
import com.bbw.god.game.config.mall.MallTool;
import com.bbw.god.mall.MallService;
import com.bbw.god.mall.UserMallRecord;
import com.bbw.god.rechargeactivities.processor.AbstractRechargeActivityProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author lwb
 * @date 2020/7/1 15:47
 */
@Service
public class RechargeActivitiesLogic {
    @Autowired
    private RechargeActivityProcessorFactory factory;
    @Autowired
    private SyncLockUtil syncLockUtil;
    @Autowired
    private MallService mallService;

    /**
     * 获取奇珍最外层标签
     *
     * @param uid
     * @return
     */
    public RDRechargeActivity listParentActivities(long uid) {
        List<Integer> showParents = new ArrayList<>(16);
        for (RechargeActivityEnum activityEnum : RechargeActivityEnum.values()) {
            if (isShowParentType(uid, activityEnum)) {
                showParents.add(activityEnum.getType());
            }
        }
        RDRechargeActivity rd = new RDRechargeActivity();
        showParents = showParents.stream().sorted(Comparator.comparing(Integer::intValue)).collect(Collectors.toList());
        rd.setShowTypes(showParents);
        return rd;
    }

    /**
     * 获取可显示的活动项标签
     *
     * @param uid
     * @param parentType
     * @return
     */
    public RDRechargeActivity listActivities(long uid, int parentType) {
        List<RechargeActivityItemEnum> showItems = getShowChildren(uid, RechargeActivityEnum.fromVal(parentType));
        RDRechargeActivity rd = new RDRechargeActivity();
        List<Integer> showTypes = new ArrayList<>(16);
        if (!showItems.isEmpty()) {
            for (RechargeActivityItemEnum item : showItems) {
                showTypes.add(item.getType());
            }
            showTypes = showTypes.stream().sorted(Comparator.comparing(Integer::intValue)).collect(Collectors.toList());
        }
        if (parentType == 3000) {
            showTypes = showTypes.stream().sorted(Comparator.comparing(Integer::intValue).reversed()).collect(Collectors.toList());
        }
        rd.setShowTypes(showTypes);
        return rd;
    }

    /**
     * 获取活动的具体奖励
     *
     * @param uid
     * @param itemType
     * @return
     */
    public RDRechargeActivity listAwards(long uid, int itemType) {
        AbstractRechargeActivityProcessor processor = factory.getProcessorsByItemType(RechargeActivityItemEnum.fromVal(itemType));
        RDRechargeActivity rd = processor.listAwards(uid);
        return rd;
    }

    /**
     * 领取奖励
     *
     * @param uid
     * @param itemType
     * @param pid
     * @return
     */
    public RDRechargeActivity gainAwards(long uid, int itemType, Integer pid) {
        if (pid == null) {
            pid = 0;
        }
        AbstractRechargeActivityProcessor processor = factory.getProcessorsByItemType(RechargeActivityItemEnum.fromVal(itemType));
        Integer finalPid = pid;
        RDRechargeActivity rd = (RDRechargeActivity) syncLockUtil.doSafe(String.valueOf(uid), tmp -> processor.gainAwards(uid, finalPid));
        return rd;
    }

    /**
     * 元宝购买
     *
     * @param uid
     * @param itemType
     * @param mallId
     * @return
     */
    public RDRechargeActivity buyAwards(long uid, int itemType, int mallId) {
        AbstractRechargeActivityProcessor processor = factory.getProcessorsByItemType(RechargeActivityItemEnum.fromVal(itemType));
        return processor.buyAwards(uid, mallId);
    }

    /**
     * 选择奖励
     *
     * @param uid
     * @param itemType
     * @param pid
     * @param awardIds
     * @return
     */
    public RDRechargeActivity pickAwards(long uid, int itemType, int pid, String awardIds) {
        AbstractRechargeActivityProcessor processor = factory.getProcessorsByItemType(RechargeActivityItemEnum.fromVal(itemType));
        return processor.pickAwards(uid, pid, awardIds);
    }

    /**
     * 是否显示该父项
     *
     * @param uid
     * @return
     */
    public boolean isShowParentType(long uid, RechargeActivityEnum parentType) {
        if (RechargeActivityEnum.NONE == parentType) {
            return false;
        }
        List<AbstractRechargeActivityProcessor> processors = factory.getProcessorsByParentType(parentType);
        if (processors.isEmpty()) {
            return false;
        }
        for (AbstractRechargeActivityProcessor processor : processors) {
            if (processor.isShow(uid)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取可以显示的子条目
     *
     * @param uid
     * @return
     */
    public List<RechargeActivityItemEnum> getShowChildren(long uid, RechargeActivityEnum parentType) {
        List<RechargeActivityItemEnum> list = new ArrayList<>(16);
        List<AbstractRechargeActivityProcessor> processors = factory.getProcessorsByParentType(parentType);
        if (processors.isEmpty()) {
            return list;
        }
        for (AbstractRechargeActivityProcessor processor : processors) {
            if (processor.isShow(uid)) {
                list.add(processor.getCurrentEnum());
            }
        }
        return list;
    }

    /**
     * 获取已经选择的奖励
     *
     * @param itemEnum
     * @param productId
     * @param uid
     * @param buyDate
     * @return
     */
    public List<Award> getPickedAward(RechargeActivityItemEnum itemEnum, int productId, long uid, Date buyDate) {
        List<CfgMallEntity> fMalls = new ArrayList<>();
        if (RechargeActivityItemEnum.DAILY_GIFT_PACK.equals(itemEnum)) {
            fMalls = MallTool.getMallConfig().getDailyRechargeMalls();
        } else if (RechargeActivityItemEnum.WEEKLY_GIFT_PACK.equals(itemEnum)) {
            fMalls = MallTool.getMallConfig().getWeekRechargeMalls();
        }
        List<Award> awards = new ArrayList<>();
        productId = productId - 99000000;
        for (CfgMallEntity entity : fMalls) {
            if (entity.getGoodsId() == productId) {
                List<UserMallRecord> umrs = mallService.getRecords(uid);
                umrs = umrs.stream().filter(umr -> umr.getBaseId().equals(entity.getId()) && umr.ifValid(buyDate)).collect(Collectors.toList());
                if (ListUtil.isNotEmpty(umrs)) {
                    umrs = umrs.stream().sorted(Comparator.comparing(UserMallRecord::getDateTime).reversed()).collect(Collectors.toList());
                    awards = umrs.get(0).getPickedAwards();
                    return awards == null ? new ArrayList<>() : awards;
                }
            }
        }
        return awards;
    }

    public boolean updateRechargeStatus(long uid, CfgProductGroup.CfgProduct product) {
        int shortId = product.getId() - 99000000;
        RechargeActivityItemEnum itemEnum = null;
        MallEnum mallEnum = null;
        if (1000 < shortId && shortId < 1100) {
            //日礼包
            itemEnum = RechargeActivityItemEnum.DAILY_GIFT_PACK;
            mallEnum = MallEnum.DAILY_RECHARGE_BAG;
        } else if (1100 < shortId && shortId < 1200) {
            //周礼包
            itemEnum = RechargeActivityItemEnum.WEEKLY_GIFT_PACK;
            mallEnum = MallEnum.WEEK_RECHARGE_BAG;
        } else if (1400 < shortId && shortId < 1500) {
            //特惠礼包
            itemEnum = RechargeActivityItemEnum.TE_HUI_PACK;
            mallEnum = MallEnum.TE_HUI_RECHARGE_BAG;
        } else if (1600 < shortId && shortId < 1700) {
            //特惠礼包
            itemEnum = RechargeActivityItemEnum.ROLE_TIME_LIMIT_BAG;
            mallEnum = MallEnum.ROLE_TIME_LIMIT_BAG;
        }
        if (itemEnum == null) {
            return false;
        }
        CfgMallEntity mall = MallTool.getMall(mallEnum.getValue(), shortId);
        AbstractRechargeActivityProcessor processor = factory.getProcessorsByItemType(itemEnum);
        return processor.updateRechargeStatus(uid, mall.getId());
    }

    /**
     * 一键领取
     *
     * @param uid
     * @param itemType
     * @return
     */
    public RDRechargeActivity gainAllAvailableAwards(long uid, int itemType) {
        AbstractRechargeActivityProcessor processor = factory.getProcessorsByItemType(RechargeActivityItemEnum.fromVal(itemType));
        return processor.gainAllAvailableAwards(uid);
    }

    /**
     * 单个刷新
     *
     * @param uid
     * @param itemType
     * @return
     */
    public RDRechargeActivity refreshItem(long uid, int itemType, int id) {
        AbstractRechargeActivityProcessor processor = factory.getProcessorsByItemType(RechargeActivityItemEnum.fromVal(itemType));
        return processor.refreshItem(uid, id);
    }
}
