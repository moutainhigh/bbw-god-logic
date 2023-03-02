package com.bbw.god.rechargeactivities.processor;

import com.bbw.common.DateUtil;
import com.bbw.common.ListUtil;
import com.bbw.common.PowerRandom;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.game.config.CfgDailyShake;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.mall.CfgMallEntity;
import com.bbw.god.game.config.mall.MallEnum;
import com.bbw.god.game.config.mall.MallTool;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.game.config.treasure.TreasureTool;
import com.bbw.god.gameuser.biyoupalace.cfg.BYPalaceTool;
import com.bbw.god.gameuser.biyoupalace.cfg.CfgBYPalaceSkillEntity;
import com.bbw.god.mall.UserMallRecord;
import com.bbw.god.rechargeactivities.RDRechargeActivity;
import com.bbw.god.rechargeactivities.RechargeActivityEnum;
import com.bbw.god.rechargeactivities.RechargeActivityItemEnum;
import com.bbw.god.rechargeactivities.data.WeeklyRedisCacheData;
import com.bbw.god.rechargeactivities.processor.dailyshake.DailyShakeService;
import com.bbw.god.rechargeactivities.service.RechargeActivityRedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 周礼包
 *
 * @author lwb
 * @date 2020/7/1 17:21
 */
@Service
public class WeeklyGiftPackProcessor extends AbstractRechargeActivityProcessor {
    @Autowired
    private RechargeActivityRedisService rechargeActivityRedisService;
    @Autowired
    private DailyShakeService dailyShakeService;
    //练技礼包ID
    private static final int LIAN_JI_GIFT_PACK_ID = 100328;
    //修体礼包ID
    private static final int XIU_TI_GIFT_PACK_ID = 100198;
    /**
     * 1元宝的礼包
     */
    private static final int ONE_GOLD_GIFT_PACK_ID = 100000;
    private static final String[] necessity = {"金-威风", "木-复活", "水-回魂", "火-死咒", "土-龙息"};
    private static final List<String> ignore = Arrays.asList("火-混元");

    @Override
    public RechargeActivityEnum getParent() {
        return RechargeActivityEnum.DIAMOND_PACK;
    }

    @Override
    public RechargeActivityItemEnum getCurrentEnum() {
        return RechargeActivityItemEnum.WEEKLY_GIFT_PACK;
    }

    /**
     * 获取展示的等级所需值
     *
     * @return
     */
    @Override
    protected int getShowNeedLevel() {
        return 20;
    }

    /**
     * 获取展示的充值所需值
     *
     * @return
     */
    @Override
    protected int getShowNeedRecharge() {
        return 30;
    }

    @Override
    public int getCanGainAwardNum(long uid) {
        return 0;
    }

    @Override
    public RDRechargeActivity listAwards(long uid) {
        RDRechargeActivity rd = new RDRechargeActivity();
        List<CfgMallEntity> fMalls = MallTool.getMallConfig().getWeekRechargeMalls();
        List<RDRechargeActivity.GiftPackInfo> goodsInfos = toRdGoodsInfoList(uid, fMalls, MallEnum.WEEK_RECHARGE_BAG, false);
        for (RDRechargeActivity.GiftPackInfo info : goodsInfos) {
            CfgDailyShake.Welfare welfare = dailyShakeService.getWelfare(uid);
            if (null != welfare && welfare.getMallIds().contains(info.getMallId())) {
                info.setWelfareId(welfare.getId());
            }
            if (info.getMallId() != LIAN_JI_GIFT_PACK_ID && info.getMallId() != XIU_TI_GIFT_PACK_ID) {
                continue;
            }
            //补充可选的奖励
            UserMallRecord zhaoMuMallRecord = mallService.getUserMallRecord(uid, info.getMallId());
            CfgMallEntity mall = MallTool.getMall(info.getMallId());
            info.setExtraAwardStatus(0);
            if (zhaoMuMallRecord == null || zhaoMuMallRecord.getNum() < mall.getLimit()) {
                //可选择奖励
                int buyTimes = zhaoMuMallRecord == null ? 0 : zhaoMuMallRecord.getNum();
                info.setExtraAwards(getAwards(info.getMallId(), buyTimes));
            }
            if (zhaoMuMallRecord != null && ListUtil.isNotEmpty(zhaoMuMallRecord.getPickedAwards())) {
                //已选的奖励补充进去
                info.getAwards().addAll(zhaoMuMallRecord.getPickedAwards());
                info.setExtraAwardStatus(zhaoMuMallRecord.getNum() >= mall.getLimit() ? null : 1);
            }
        }
        rd.setGoodsList(goodsInfos);
        rd.setCountdown(DateUtil.millisecondsInterval(DateUtil.getThisWeekEndDateTime(),new Date()));
        return rd;
    }

    @Override
    public RDRechargeActivity pickAwards(long uid, Integer mallId, String awardIds) {
        if (mallId != LIAN_JI_GIFT_PACK_ID && mallId != XIU_TI_GIFT_PACK_ID) {
            //该项没有可选择的奖励
            throw new ExceptionForClientTip("rechargeActivity.not.extraAwards");
        }
        UserMallRecord zhaoMuMallRecord = mallService.getUserMallRecord(uid, mallId);
        if (zhaoMuMallRecord == null && (mallId == XIU_TI_GIFT_PACK_ID || mallId == LIAN_JI_GIFT_PACK_ID)) {
            zhaoMuMallRecord = UserMallRecord.instance(uid, mallId, MallEnum.WEEK_RECHARGE_BAG.getValue(), 0);
            mallService.addRecord(zhaoMuMallRecord);
        }
        if (zhaoMuMallRecord == null) {
            //该项没有可选择的奖励
            throw new ExceptionForClientTip("rechargeActivity.not.extraAwards");
        }
        CfgMallEntity mall = MallTool.getMall(mallId);
        if (zhaoMuMallRecord.getNum() >= mall.getLimit()) {
            //已购买不可再换
            throw new ExceptionForClientTip("rechargeActivity.picked.Awards");
        }
        List<Integer> awardIdList = ListUtil.parseStrToInts(awardIds);
        Optional<Award> optionalAward = getAwards(mallId, zhaoMuMallRecord.getNum()).stream().filter(p -> awardIdList.contains(p.getAwardId())).findFirst();
        if (!optionalAward.isPresent()) {
            throw new ExceptionForClientTip("rechargeActivity.not.extraAwards");
        }
        zhaoMuMallRecord.setPickedAwards(Arrays.asList(optionalAward.get()));
        gameUserService.updateItem(zhaoMuMallRecord);
        return new RDRechargeActivity();
    }

    /**
     * 购买礼包
     *
     * @param uid
     * @param mallId
     * @return
     */
    @Override
    public RDRechargeActivity buyAwards(long uid, int mallId) {
        RDRechargeActivity rd = new RDRechargeActivity();
        List<CfgMallEntity> fMalls = MallTool.getMallConfig().getWeekRechargeMalls();
        buyAwards(fMalls, mallId, uid, rd, WayEnum.WEEKLY_DIAMOND_GIFT_PACK, MallEnum.WEEK_RECHARGE_BAG);
        return rd;
    }

    /**
     * 获得价格
     *
     * @param cfgMallEntity
     * @param isWelfare
     * @param welfare
     * @return
     */
    @Override
    protected int getPrice(long uid, CfgMallEntity cfgMallEntity, boolean isWelfare, CfgDailyShake.Welfare welfare) {
        //获得价格
        int price = cfgMallEntity.getPrice();
        //福利加成
        if (!isWelfare) {
            return price;
        }
        int welfarAddIndex = welfare.getMallIds().indexOf(cfgMallEntity.getId());
        price = welfare.getWelfareAdds().get(welfarAddIndex);
        //删除福利加成
        dailyShakeService.setWelfare(uid, null);
        return price;
    }

    /**
     * 获得的奖励
     *
     * @param cfgMallEntity
     * @param isWelfare
     * @return
     */
    @Override
    protected List<Award> getAwards(long uid, CfgMallEntity cfgMallEntity, UserMallRecord userMallRecord, boolean isWelfare, CfgDailyShake.Welfare welfare) {
        List<Award> awards = new ArrayList<>();
        awards.addAll(productService.getProductAward(getProductGoodsId(cfgMallEntity.getGoodsId())).getAwardList());
        //不是可选礼包直接返回
        if (cfgMallEntity.getId() != XIU_TI_GIFT_PACK_ID && cfgMallEntity.getId() != LIAN_JI_GIFT_PACK_ID) {
            return awards;
        }
        List<Award> pickedAwards = userMallRecord.getPickedAwards();
        if (ListUtil.isEmpty(pickedAwards)) {
            throw new ExceptionForClientTip("rechargeActivity.not.select.Awards");
        }
        awards.addAll(pickedAwards);
        return awards;
    }

    /**
     * 指定符篆：洪力符*1 或 宙元符*1
     * 指定技能卷轴：随机高级卷轴*1 或 金系五篇卷轴*1 或 木系五篇卷轴*1 或 水系五篇卷轴*1 或 火系五篇卷轴*1 或 土系五篇卷轴*1
     * ②　各系五篇卷轴每周随机刷出，但必定会出现以下的一本金-威风、木-复活、水-回魂、火-死咒、土-龙息
     *
     * @param mallId
     * @param buyTimes 第几次购买
     * @return
     */
    private List<Award> getAwards(int mallId, int buyTimes) {
        List<Award> awards = new ArrayList<>();
        if (mallId == XIU_TI_GIFT_PACK_ID) {
            //洪力符*1 或 宙元符*1
            awards.add(Award.instance(TreasureEnum.HongLF.getValue(), AwardEnum.FB, 1));
            awards.add(Award.instance(TreasureEnum.ZhouYF.getValue(), AwardEnum.FB, 1));
        } else if (mallId == LIAN_JI_GIFT_PACK_ID) {
            Optional<WeeklyRedisCacheData> optional = rechargeActivityRedisService.getWeeklyLianJiAwards();
            if (!optional.isPresent()) {
                rechargeActivityRedisService.saveWeeklyLianJiAwards(makeLJAwardPool(), makeLJAwardPool());
            } else {
                WeeklyRedisCacheData weeklyRedisCacheData = optional.get();
                if (ListUtil.isEmpty(weeklyRedisCacheData.getAwards2())) {
                    weeklyRedisCacheData.setAwards2(makeLJAwardPool());
                    rechargeActivityRedisService.updateWeeklyLianJiAwards(weeklyRedisCacheData);
                }
                if (buyTimes == 0) {
                    awards = weeklyRedisCacheData.getAwards();
                } else {
                    awards = weeklyRedisCacheData.getAwards2();
                }
            }
        }
        return awards;
    }

    /**
     * 练级礼包礼包奖励可选池
     *
     * @return
     */
    private List<Award> makeLJAwardPool() {
        List<Award> awards = new ArrayList<>();
        awards.add(Award.instance(11730, AwardEnum.FB, 1));
        List<CfgBYPalaceSkillEntity> byPalaceSkillEntities = BYPalaceTool.getBYPSkillEntityList(5);
        List<String> skillNames = new ArrayList<>();
        String skill = PowerRandom.getRandomFromArray(necessity);
        for (CfgBYPalaceSkillEntity entity : byPalaceSkillEntities) {
            if (entity.getType().contains(skill.substring(0, 1))) {
                skillNames.add(skill);
                continue;
            }
            String skillName = PowerRandom.getRandomFromList(entity.getSkills());
            int maxTimes = 100;
            while (ignore.contains(skillName) && maxTimes > 0) {
                skillName = PowerRandom.getRandomFromList(entity.getSkills());
                maxTimes--;
            }
            skillNames.add(skillName);
        }
        for (String str : skillNames) {
            int tId = TreasureTool.getTreasureByName(str).getId();
            awards.add(Award.instance(tId, AwardEnum.FB, 1));
        }
        return awards;
    }
}
