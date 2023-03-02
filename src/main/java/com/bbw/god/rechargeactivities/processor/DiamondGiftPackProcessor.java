package com.bbw.god.rechargeactivities.processor;

import com.bbw.common.DateUtil;
import com.bbw.common.IpUtil;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.game.award.AwardStatus;
import com.bbw.god.game.config.CfgProductGroup;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.mall.CfgMallEntity;
import com.bbw.god.game.config.mall.MallTool;
import com.bbw.god.gameuser.statistic.StatisticTypeEnum;
import com.bbw.god.gameuser.statistic.resource.copper.CopperResStatisticService;
import com.bbw.god.gameuser.statistic.resource.copper.CopperStatistic;
import com.bbw.god.pay.RDProductList;
import com.bbw.god.rechargeactivities.RDRechargeActivity;
import com.bbw.god.rechargeactivities.RechargeActivityEnum;
import com.bbw.god.rechargeactivities.RechargeActivityItemEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 钻石礼包
 *
 * @author: huanghb
 * @date: 2022/6/14 15:32
 */
@Service
public class DiamondGiftPackProcessor extends AbstractRechargeActivityProcessor {
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private CopperResStatisticService statisticService;

    @Override
    public RechargeActivityEnum getParent() {
        return RechargeActivityEnum.DIAMOND_PACK;
    }

    @Override
    public RechargeActivityItemEnum getCurrentEnum() {
        return RechargeActivityItemEnum.DIAMOND_GIFT_PACK;
    }

    @Override
    public boolean isShow(long uid) {
        return true;
    }

    /**
     * 获取活动信息
     *
     * @param uid
     * @return
     */
    @Override
    public RDRechargeActivity listAwards(long uid) {
        List<CfgMallEntity> fMalls = MallTool.getMallConfig().getDiamondRechargeMalls();
        //获取到可用的所有产品列表
        CfgProductGroup productGroup = productService.getAppProductGroup(gameUserService.getActiveSid(uid));
        RDProductList rdp = productService.getProductsList(productGroup, gameUserService.getGameUser(uid), IpUtil.getIpAddr(request));
        RDRechargeActivity rd = new RDRechargeActivity();
        rd.setFirstBought(rdp.getFirstBought());
        List<RDRechargeActivity.GoldPackInfo> goodsInfos = new ArrayList<>();
        for (CfgMallEntity mallEntity : fMalls) {
            Optional<RDProductList.RDProduct> rdProduct = rdp.getProducts().stream().filter(p -> p.getId() == mallEntity.getGoodsId()).findFirst();
            if (rdProduct.isPresent()) {
                goodsInfos.add(RDRechargeActivity.GoldPackInfo.instance(rdProduct.get(), mallEntity));
            }
        }
        goodsInfos = goodsInfos.stream().sorted(Comparator.comparing(RDRechargeActivity.GoldPackInfo::getPrice)).collect(Collectors.toList());
        rd.setProducts(goodsInfos);
        CopperStatistic statistic = statisticService.fromRedis(uid, StatisticTypeEnum.GAIN, DateUtil.getTodayInt());
        Map<WayEnum, Long> totalMap = statistic.getTotalMap();
        Long value = totalMap.get(WayEnum.DIAMOND_BAG_TIP);
        int goldTipBoxStatus = null == value ? AwardStatus.ENABLE_AWARD.getValue() : AwardStatus.AWARDED.getValue();
        rd.setGoldBagTipBoxStatus(goldTipBoxStatus);
        return rd;
    }

    @Override
    public RDRechargeActivity gainAwards(long uid, int pid) {
        CopperStatistic statistic = statisticService.fromRedis(uid, StatisticTypeEnum.GAIN, DateUtil.getTodayInt());
        Map<WayEnum, Long> totalMap = statistic.getTotalMap();
        Long value = totalMap.get(WayEnum.DIAMOND_BAG_TIP);
        if (null != value) {
            throw new ExceptionForClientTip("rechargeActivity.cant.award");
        }
        RDRechargeActivity rd = new RDRechargeActivity();
        Award award = new Award(AwardEnum.TQ, 10000);
        this.awardService.fetchAward(uid, Arrays.asList(award), WayEnum.DIAMOND_BAG_TIP, "", rd);
        return rd;
    }

    @Override
    public int getCanGainAwardNum(long uid) {
        return 0;
    }
}
