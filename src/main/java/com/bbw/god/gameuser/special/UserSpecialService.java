package com.bbw.god.gameuser.special;

import com.bbw.cache.UserCacheService;
import com.bbw.common.LM;
import com.bbw.common.ListUtil;
import com.bbw.common.PowerRandom;
import com.bbw.common.lock.RedisLockUtil;
import com.bbw.god.activity.ActivityService;
import com.bbw.god.activity.IActivity;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.city.chengc.RDTradeInfo;
import com.bbw.god.city.taiyf.TaiYFProcessor;
import com.bbw.god.event.BaseEventParam;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.game.config.city.CfgCityEntity;
import com.bbw.god.game.config.special.CfgSpecialEntity;
import com.bbw.god.game.config.special.SpecialTool;
import com.bbw.god.game.config.special.SpecialTypeEnum;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.bag.UserBagBuyRecord;
import com.bbw.god.gameuser.bag.UserBagService;
import com.bbw.god.gameuser.businessgang.BusinessGangCfgTool;
import com.bbw.god.gameuser.businessgang.BusinessGangService;
import com.bbw.god.gameuser.businessgang.cfg.CfgGiftEntity;
import com.bbw.god.gameuser.chamberofcommerce.server.UserCocTaskService;
import com.bbw.god.gameuser.mail.MailService;
import com.bbw.god.gameuser.privilege.PrivilegeService;
import com.bbw.god.gameuser.special.event.EPPocketSpecial;
import com.bbw.god.gameuser.special.event.SpecialEventPublisher;
import com.bbw.god.gameuser.task.TaskStatusEnum;
import com.bbw.god.gameuser.task.businessgang.UserBusinessGangSpecialtyShippingTask;
import com.bbw.god.gameuser.task.businessgang.UserSpecialtyShippingTaskService;
import com.bbw.god.server.special.ServerSpecialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserSpecialService {
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private ServerSpecialService serverSpecialService;
    @Autowired
    private PrivilegeService privilegeService;
    @Autowired
    private ActivityService activityService;
    @Autowired
    private MailService mailService;
    @Autowired
    private RedisLockUtil redisLockUtil;
    @Autowired
    private UserCocTaskService userCocTaskService;
    @Autowired
    private TaiYFProcessor taiYFProcessor;
    @Autowired
    private UserBagService userBagService;
    @Autowired
    private UserCacheService userCacheService;
    @Autowired
    private UserSpecialtyShippingTaskService userSpecialtyShippingTaskService;
    @Autowired
    private BusinessGangService businessGangService;

    /** 灵芝的ID */
    public static final int LING_ZHI_ID = 47;
    /** 灵芝购买上限 */
    public static final int LING_ZHI_BUY_LIMIY = 200;

    public List<UserSpecial> getSpecials(long uid) {
        return userCacheService.getUserDatas(uid, UserSpecial.class);
    }

    public void addSpecials(List<UserSpecial> uss) {
        userCacheService.addUserDatas(uss);
    }

    public void delSpecials(long uid, List<Long> specialDataIds) {
        userCacheService.delUserDatas(uid, specialDataIds, UserSpecial.class);
    }

    public void delSpecials(List<UserSpecial> specialsToDel) {
        userCacheService.delUserDatas(specialsToDel);
    }


    /**
     * 获取玩家拥有的所有特产
     *
     * @param uid
     * @return
     */
    public List<UserSpecial> getOwnSpecials(long uid) {
        List<UserSpecial> userSpecialList = getSpecials(uid);
        int sid = gameUserService.getOriServer(uid).getMergeSid();
        IActivity a = activityService.getActivity(sid, ActivityEnum.TCHC);
        if (null != a) {
            return userSpecialList;
        }
        // 活动不存在，删除合成特产的数据
        List<UserSpecial> toDelDatas = userSpecialList.stream().filter(tmp ->
                SpecialTool.getSpecialById(tmp.getBaseId()).getType() == SpecialTypeEnum.SYNTHETIC.getValue()).collect(Collectors.toList());
        if (ListUtil.isEmpty(toDelDatas)) {
            return userSpecialList;
        }
        delSpecials(toDelDatas);
        // 发送邮件
        String title = LM.I.getMsgByUid(uid, "mmail.activity.special.outdate.title", "凛冬将至合成");
        String content = LM.I.getMsgByUid(uid, "mail.activity.special.outdata.content", "凛冬将至");
        int num = 0;
        for (UserSpecial userSpecial : toDelDatas) {
            CfgSpecialEntity cfgSpecial = SpecialTool.getSpecialById(userSpecial.getBaseId());
            List<Integer> materialIds = cfgSpecial.getMaterialIds();
            Integer materialId1 = materialIds.get(0);
            Integer materialId2 = materialIds.get(1);
            num += SpecialTool.getSpecialById(materialId1).getBuyPrice(100) + SpecialTool.getSpecialById(materialId2).getBuyPrice(100);
        }
        Award award = new Award(AwardEnum.TQ, num);
        mailService.sendAwardMail(title, content, uid, Arrays.asList(award));
        userSpecialList = getSpecials(uid);
        return userSpecialList;
    }

    /**
     * 只获取需要格子的特产
     *
     * @param uid
     * @return
     */
    public List<UserSpecial> getOwnWithUseBagSpecials(long uid) {
        List<UserSpecial> ownSpecials = getOwnSpecials(uid);
        if (ListUtil.isEmpty(ownSpecials)) {
            return new ArrayList<>();
        }
        List<Integer> ids = SpecialTool.getAllExcludeBagSpecialIds();
        return ownSpecials.stream().filter(p -> !ids.contains(p.getBaseId())).collect(Collectors.toList());
    }

    /**
     * 获取非口袋中的特产
     *
     * @param uid
     * @return
     */
    public List<UserSpecial> getUnLockSpecials(long uid) {
        List<UserSpecial> userSpecials = getOwnSpecials(uid);
        List<Long> pocketList = getLockSpecailDataIds(uid);
        return userSpecials.stream().filter(p -> !pocketList.contains(p.getId())).collect(Collectors.toList());
    }

    /**
     * 获取随机事件可以影响的特产
     *
     * @param uid
     * @return
     */
    public List<UserSpecial> getRandomEventSpecials(long uid) {
        List<Integer> excludeRandomEventSpecialIds = SpecialTool.getAllExcludeRandomEventSpecialIds();
        List<UserSpecial> unLockSpecials = getUnLockSpecials(uid);
        return unLockSpecials.stream().filter(p -> !excludeRandomEventSpecialIds.contains(p.getBaseId())).collect(Collectors.toList());
    }

    /**
     * 获取口袋中的所有口袋特产
     *
     * @param uid
     * @return
     */
    public List<UserSpecial> getLockSpecials(long uid) {
        List<UserSpecial> specials = new ArrayList<UserSpecial>();
        List<UserPocket> pocketSpecials = userCacheService.getUserDatas(uid, UserPocket.class);
        for (UserPocket pocketSpecial : pocketSpecials) {
            Optional<UserSpecial> optional = getOwnSpecialByDataId(uid, pocketSpecial.getUserSpecialDataId());
            if (!optional.isPresent()) {
                userCacheService.delUserData(pocketSpecial);
                continue;
            }
            specials.add(optional.get());
        }
        return specials;
    }

    /**
     * 获取所有上锁特产的存储Id
     *
     * @param uid
     * @return
     */
    public List<Long> getLockSpecialIds(long uid) {
        List<Long> specials = new ArrayList<Long>();
        List<UserPocket> pocketSpecials = userCacheService.getUserDatas(uid, UserPocket.class);
        for (UserPocket pocketSpecial : pocketSpecials) {
            Optional<UserSpecial> optional = getOwnSpecialByDataId(uid, pocketSpecial.getUserSpecialDataId());
            if (!optional.isPresent()) {
                userCacheService.delUserData(pocketSpecial);
                continue;
            }
            specials.add(optional.get().getId());
        }
        return specials;
    }

    public List<UserPocket> getPocketsBydataIds(long uid, List<Long> dataIds) {
        List<UserPocket> userPockets = new ArrayList<UserPocket>();
        List<UserPocket> pocketSpecials = userCacheService.getUserDatas(uid, UserPocket.class);
        for (UserPocket pocket : pocketSpecials) {
            Optional<UserSpecial> optional = getOwnSpecialByDataId(uid, pocket.getUserSpecialDataId());
            if (!optional.isPresent()) {
                userCacheService.delUserData(pocket);
                continue;
            }
            if (dataIds.contains(pocket.getUserSpecialDataId())) {
                userPockets.add(pocket);
            }
        }
        return userPockets;
    }

    /**
     * 根据Id获取特产
     *
     * @param uid
     * @param usIds
     * @return
     */
    public List<UserSpecial> getAllSpecialsById(long uid, List<Long> usIds) {
        List<UserSpecial> userSpecials = getOwnSpecials(uid).stream().filter(us -> usIds.contains(us.getId())).collect(Collectors.toList());
        return userSpecials;
    }


    /**
     * 获取口袋的空格
     *
     * @return
     */
    public int getPocketEmptySize(Long uid) {
        int pocketSize = privilegeService.getPocketNums(gameUserService.getGameUser(uid));
        if (pocketSize == 0) {
            return 0;
        }
        List<UserSpecial> pocketSpecials = getLockSpecials(uid);
        if (pocketSpecials == null || pocketSpecials.isEmpty()) {
            return pocketSize;
        }
        return pocketSize - pocketSpecials.size();
    }


    public void checkSpecailAndUnLock(long uid, List<Long> dataIds) {
        List<Long> pocketList = getLockSpecailDataIds(uid);
        List<Long> deleteList = pocketList.stream().filter(p -> dataIds.contains(p)).collect(Collectors.toList());
        if (!deleteList.isEmpty()) {
            EPPocketSpecial ep = new EPPocketSpecial(new BaseEventParam(uid), deleteList);
            SpecialEventPublisher.pubSpecialUnLockEvent(ep);
        }
    }

    private List<Long> getLockSpecailDataIds(long uid) {
        List<UserPocket> pockets = userCacheService.getUserDatas(uid, UserPocket.class);
        List<Long> idsList = new ArrayList<Long>();
        if (pockets != null && !pockets.isEmpty()) {
            idsList = pockets.stream().map(UserPocket::getUserSpecialDataId).collect(Collectors.toList());
        }
        return idsList;
    }

    /**
     * 根据特产ID获取玩家特产
     *
     * @param uid
     * @param specialId
     * @return
     */
    public UserSpecial getOwnSpecialBySpecialId(long uid, int specialId) {
        UserSpecial userSpecial = userCacheService.getCfgItem(uid, specialId, UserSpecial.class);
        return userSpecial;
    }

    /**
     * 根据特产ID获取玩家特产
     *
     * @param uid
     * @param dataId
     * @return
     */
    public Optional<UserSpecial> getOwnSpecialByDataId(long uid, Long dataId) {
        Optional<UserSpecial> userSpecial = gameUserService.getUserData(uid, dataId, UserSpecial.class);
        return userSpecial;
    }

    /**
     * 获得玩家某一特产的所有的特产
     *
     * @param uid
     * @param specialId
     * @return
     */
    public List<UserSpecial> getOwnUnLockSpecialsByBaseId(long uid, int specialId) {
        return getUnLockSpecials(uid).stream().filter(userSpecialObj -> userSpecialObj.getBaseId() == specialId)
                .collect(Collectors.toList());
    }

    /**
     * 获取玩家拥有的任意n个特产
     *
     * @param uid
     * @param num
     * @return
     */
    public List<UserSpecial> getRandomEventSpecials(long uid, int num) {
        List<UserSpecial> userSpecialObjs = getRandomEventSpecials(uid);
        if (userSpecialObjs.size() == 0) {
            return userSpecialObjs;
        }
        num = num > userSpecialObjs.size() ? userSpecialObjs.size() : num;
        return PowerRandom.getRandomIndexsForList(userSpecialObjs.size(), num).stream().map(index -> userSpecialObjs.get(index)).collect(Collectors.toList());
    }

    /**
     * 获得特产上限
     *
     * @param gu
     * @return
     */
    public Integer getSpecialLimit(GameUser gu) {
        int limit = Math.min(12 + gu.getLevel() / 3, SpecialTool.getSpecialCfg().getSpecialMaxLimit());
        limit += privilegeService.getExtraSpecialExpand(gu.getId());
        // 购买的格子
        UserBagBuyRecord buyRecord = userBagService.getCurBuyRecord(gu.getId());
        limit += buyRecord.getBuyTimes();
        return limit;
    }

    /**
     * 获得特产上限
     *
     * @param uid
     * @return
     */
    public Integer getSpecialLimit(long uid) {
        return getSpecialLimit(gameUserService.getGameUser(uid));
    }


    /**
     * 获取玩家的背包空闲格子数量
     *
     * @param uid
     * @return
     */
    public int getSpecialFreeSize(long uid) {
        int limit = getSpecialLimit(gameUserService.getGameUser(uid));
        List<UserSpecial> ownSpecials = getOwnWithUseBagSpecials(uid);

        return Math.max(0, limit - ownSpecials.size());
    }

    /**
     * 按特产价格比例排序特产
     *
     * @param gu
     * @param userSpecials
     */
    public void sortUserSpecialAsPriceRate(GameUser gu, List<UserSpecial> userSpecials) {
        CfgCityEntity city = gu.gainCurCity();
        userSpecials.sort((o1, o2) -> {
            if (o1.getBaseId() - o2.getBaseId() == 0) {
                return 0;
            }
            CfgSpecialEntity special1 = SpecialTool.getSpecialById(o1.getBaseId());
            CfgSpecialEntity special2 = SpecialTool.getSpecialById(o2.getBaseId());
            int sellingPrice1 = serverSpecialService.getSellingPrice(special1, city);
            int sellingPrice2 = serverSpecialService.getSellingPrice(special2, city);
            double rate1 = sellingPrice1 * 1.00 / special1.getPrice();
            double rate2 = sellingPrice2 * 1.00 / special2.getPrice();
            // System.out.println(rate1 + "," + rate2);
            if (rate2 > rate1) {
                return 1;
            } else if (rate2 < rate1) {
                return -1;
            } else {
                return special2.getPrice() - special1.getPrice();
            }
        });
    }

    /**
     * 获取当前玩家特产设置
     *
     * @param uid 玩家id
     * @return
     */
    public UserSpecialSetting getCurUserSpecialSetting(long uid) {
        UserSpecialSetting setting = gameUserService.getSingleItem(uid, UserSpecialSetting.class);
        if (null == setting) {
            setting = (UserSpecialSetting) redisLockUtil.doSafe(String.valueOf(uid), tmp -> {
                UserSpecialSetting specialSetting = gameUserService.getSingleItem(uid, UserSpecialSetting.class);
                if (null == specialSetting) {
                    specialSetting = UserSpecialSetting.getInstance(uid);
                    gameUserService.addItem(uid, specialSetting);
                }
                return specialSetting;
            });
        }
        return setting;
    }

    /**
     * 过滤一键购买的特产
     *
     * @param uid
     * @param
     * @return
     */
    public List<Integer> getFilterAutoBuySpecialIds(long uid) {
        UserSpecialSetting setting = getCurUserSpecialSetting(uid);
        //自动购买特产
        List<Integer> autoBuySpecialIds = setting.getAutoBuySpecialIds();
        //自动购买特产铺好感度道具
        if (setting.getIfBuyBusinessGangGifts()) {
            List<CfgGiftEntity> cfgGiftEntities = BusinessGangCfgTool.getAllGiftInfos();
            List<Integer> businessGangGiftIds = cfgGiftEntities.stream().map(CfgGiftEntity::getGiftId).collect(Collectors.toList());
            autoBuySpecialIds.addAll(businessGangGiftIds);
        }
        //自动购买铜铲子
        if (setting.getIfBuyCopperShovel()) {
            autoBuySpecialIds.add(TreasureEnum.COPPER_SHOVEL.getValue());
        }
        //自动购买节日道具
        if (setting.getIfBuyHolidayProps()) {
            List<Integer> autoBuyHolidayPropIds = SpecialTool.getAutoBuyHolidayPropIds();
            autoBuySpecialIds.addAll(autoBuyHolidayPropIds);
        }
        //自动购买灵芝
        if (setting.getIfBuyLingzhi()) {
            autoBuySpecialIds.add(LING_ZHI_ID);
        }
        return autoBuySpecialIds;
    }


    /**
     * 过滤一键出售的特产
     *
     * @param uid
     * @param specials
     * @return
     */
    public List<RDTradeInfo.RDSellingSpecial> getFilterAutoSellSpecialIds(long uid, List<RDTradeInfo.RDSellingSpecial> specials) {
        UserSpecialSetting setting = getCurUserSpecialSetting(uid);
        // 过滤商帮任务特产
        if (!setting.getIfSellCocTaskSpecial()) {
            List<UserBusinessGangSpecialtyShippingTask> allTasks = userSpecialtyShippingTaskService.getAllTasks(uid).stream()
                    .filter(u -> u.getStatus() == TaskStatusEnum.DOING.getValue()).collect(Collectors.toList());
            for (UserBusinessGangSpecialtyShippingTask task : allTasks) {
                List<Integer> specialsId = businessGangService.getShippingTaskSpecials(task);
                specials = specials.stream().filter(tmp -> !specialsId.contains(tmp.getId())).collect(Collectors.toList());
            }
        }
        // 过滤太一府特产
        if (!setting.getIfSellTyfSpecial()) {
            GameUser gu = gameUserService.getGameUser(uid);
            List<Integer> ids = taiYFProcessor.getUnFilledSpecialIds(gu);
            specials = specials.stream().filter(tmp -> !ids.contains(tmp.getId())).collect(Collectors.toList());
        }
        //过滤口袋上锁的特产
        if (!setting.getIfSellLockSpecial()) {
            specials = specials.stream().filter(p -> !p.checkLock()).collect(Collectors.toList());
        }
        return specials.stream().filter(p -> p.getId().intValue() != 47).collect(Collectors.toList());
    }
}
