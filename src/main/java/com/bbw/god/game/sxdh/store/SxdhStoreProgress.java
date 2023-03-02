package com.bbw.god.game.sxdh.store;

import com.bbw.common.DateUtil;
import com.bbw.common.PowerRandom;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.ConsumeType;
import com.bbw.god.db.entity.CfgServerEntity;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.mall.CfgMallEntity;
import com.bbw.god.game.config.mall.MallTool;
import com.bbw.god.game.config.server.ServerTool;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.game.config.treasure.TreasureTool;
import com.bbw.god.game.data.GameDataService;
import com.bbw.god.game.sxdh.SxdhDateService;
import com.bbw.god.game.sxdh.SxdhZone;
import com.bbw.god.game.sxdh.SxdhZoneService;
import com.bbw.god.game.sxdh.config.ZoneType;
import com.bbw.god.gameuser.biyoupalace.cfg.BYPalaceTool;
import com.bbw.god.gameuser.biyoupalace.cfg.CfgBYPalaceSkillEntity;
import com.bbw.god.gameuser.biyoupalace.cfg.Chapter;
import com.bbw.god.gameuser.biyoupalace.cfg.ChapterType;
import com.bbw.god.gameuser.treasure.TreasureChecker;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.mall.store.AbstractStoreProcessor;
import com.bbw.god.mall.store.RDStore;
import com.bbw.god.mall.store.RDStoreGoodsInfo;
import com.bbw.god.mall.store.StoreEnum;
import com.bbw.god.rd.RDCommon;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author lwb
 * @date 2020/3/24 11:23
 */
@Slf4j
@Service
public class SxdhStoreProgress extends AbstractStoreProcessor {
    public static int ChuanQJZ_MALL_ID = 150070;
    public static int MiZ_MALL_ID = 150080;
    public static int RANDOM_SECRET_SCROLL = 150090;
    /** 常规战区限制购买商品，包括传奇卷轴,随机秘传 */
    private static List<Integer> NORMAL_ZONE_LIMIT_MALL_IDS = Arrays.asList(ChuanQJZ_MALL_ID, RANDOM_SECRET_SCROLL);
    @Autowired
    private SxdhZoneService sxdhZoneService;
    @Autowired
    private GameDataService gameDataService;
    @Autowired
    private SxdhDateService sxdhDateService;

    @Override
    public boolean isMatch(int mallType) {
        return StoreEnum.SXDH.getType() == mallType;
    }

    @Override
    public RDStore getGoodsList(long guId) {
        List<RDStoreGoodsInfo> rdShopProducts = new ArrayList<>();
        List<CfgMallEntity> malls = MallTool.getMallConfig().getSxdhMalls();
        List<SxdhZoneMallRecord> zoneMallRecords = getCurZoneMallRecords(gameUserService.getOriServer(guId).getGroupId());
        SxdhZone sxdhZone = getSxdhZone(guId);
        for (CfgMallEntity mall : malls) {
            RDStoreGoodsInfo goodsInfo = new RDStoreGoodsInfo();
            goodsInfo.setItem(mall.getItem());
            goodsInfo.setMallId(mall.getId());
            goodsInfo.setRealId(mall.getGoodsId());
            //设置仙豆购买信息
            RDStoreGoodsInfo.BuyType beanBuyType = new RDStoreGoodsInfo.BuyType();
            beanBuyType.setConsume(ConsumeType.BEAN.getValue());
            beanBuyType.setBoughtTimes(0);
            beanBuyType.setLimit(mall.getLimit());
            beanBuyType.setPrice(mall.getPrice());
            //战区限制设置购买次数
            if (isZoneLimit(mall)) {
                SxdhZoneMallRecord record = zoneMallRecords.stream().filter(tmp -> tmp.getZoneType() == sxdhZone.getZone() && tmp.getMallId() == mall.getId().intValue()).findFirst().orElse(null);
                beanBuyType.setBoughtTimes(record.getNum());
                goodsInfo.setRealId(record.getGoodId());
            }
            goodsInfo.addBuyType(beanBuyType);
            rdShopProducts.add(goodsInfo);
        }
        RDStore rdStore = new RDStore();
        rdStore.setIntegralGoods(rdShopProducts);
        rdStore.setCurrency(userTreasureService.getTreasureNum(guId, TreasureEnum.XIAN_DOU.getValue()));
        return rdStore;
    }

    @Override
    public RDCommon buyGoods(long uid, int mallId, int buyNum, Integer consume) {
        RDCommon rd = new RDCommon();
        CfgMallEntity mall = MallTool.getMall(mallId);
        int needBean = mall.getPrice() * buyNum;
        TreasureChecker.checkIsEnough(TreasureEnum.XIAN_DOU.getValue(), needBean, uid);
        //无限制兑换
        if (!isZoneLimit(mall)) {
            TreasureEventPublisher.pubTAddEvent(uid, mall.getGoodsId(), buyNum, WayEnum.SXDH_SHOP_BUY, rd);
            //消耗仙豆
            TreasureEventPublisher.pubTDeductEvent(uid, TreasureEnum.XIAN_DOU.getValue(), needBean, WayEnum.SXDH_SHOP_BUY, rd);
            return rd;
        }
        //战区限制兑换
        List<SxdhZoneMallRecord> zoneMallRecords = getCurZoneMallRecords(gameUserService.getOriServer(uid).getGroupId());
        SxdhZone sxdhZone = getSxdhZone(uid);
        SxdhZoneMallRecord record = zoneMallRecords.stream().filter(tmp -> tmp.getZoneType() == sxdhZone.getZone() && tmp.getMallId() == mall.getId().intValue()).findFirst().orElse(null);
        if (record.getNum() >= mall.getLimit()) {
            throw new ExceptionForClientTip("mall.is.outOfLimit", mall.getLimit().toString());
        }
        //消耗仙豆
        TreasureEventPublisher.pubTDeductEvent(uid, TreasureEnum.XIAN_DOU.getValue(), needBean, WayEnum.SXDH_SHOP_BUY, rd);

        record.addNum(buyNum);
        gameDataService.updateGameData(record);
        TreasureEventPublisher.pubTAddEvent(uid, record.getGoodId(), buyNum, WayEnum.SXDH_SHOP_BUY, rd);

        log.info(uid + "在神仙大会战区" + sxdhZone.getZone() + "兑换商品：" + mallId);
        return rd;
    }

    /**
     * 是否战区限制购买
     *
     * @param mall
     * @return
     */
    private boolean isZoneLimit(CfgMallEntity mall) {
        if (mall.getId() == ChuanQJZ_MALL_ID || mall.getId() == MiZ_MALL_ID || mall.getId() == RANDOM_SECRET_SCROLL) {
            return true;
        }
        return false;
    }

    /**
     * 获取当前战区记录
     *
     * @return
     */
    private List<SxdhZoneMallRecord> getCurZoneMallRecords(int serverGroup) {
        Date now = DateUtil.now();
        List<SxdhZoneMallRecord> records = gameDataService.getGameDatas(SxdhZoneMallRecord.class);
        List<SxdhZoneMallRecord> curRecords = records.stream()
                .filter(tmp -> tmp.getExpireDate().after(now) && (null != tmp.getServerGroup() && serverGroup == tmp.getServerGroup()))
                .collect(Collectors.toList());

        if (records.size() == 0 || curRecords.size() == 0) {
            curRecords = newZoneRecords(serverGroup, records);
        }
        return curRecords;
    }

    private List<SxdhZoneMallRecord> newZoneRecords(int serverGroup, List<SxdhZoneMallRecord> records) {
        List<SxdhZoneMallRecord> newRecords = new ArrayList<>();
        for (ZoneType zoneType : ZoneType.values()) {
            //秘传战区记录
            newRecords.add(buildMiZMallRecord(serverGroup, zoneType.getValue(), records));
            for (int mallId : NORMAL_ZONE_LIMIT_MALL_IDS) {
                SxdhZoneMallRecord record = SxdhZoneMallRecord.instance(serverGroup, zoneType.getValue(), mallId);
                newRecords.add(record);
            }
        }
        gameDataService.addGameDatas(newRecords);
        return newRecords;
    }


    /**
     * 新的秘传战区记录，前后一个不重复
     *
     * @param zoneType
     * @param records
     * @return
     */
    public SxdhZoneMallRecord buildMiZMallRecord(int serverGroup, int zoneType, List<SxdhZoneMallRecord> records) {
        List<Integer> miZhuanRecords = records.stream().filter(tmp -> tmp.getZoneType() == zoneType && tmp.getMallId() == MiZ_MALL_ID).map(SxdhZoneMallRecord::getGoodId).collect(Collectors.toList());
        Set<Integer> excludeSkillScroll = new HashSet<>();
        excludeSkillScroll.add(21214);
        if (miZhuanRecords.size() > 0) {
            excludeSkillScroll.add(miZhuanRecords.get(miZhuanRecords.size() - 1));
        }
        CfgBYPalaceSkillEntity byPalaceSkill = BYPalaceTool.getBYPSkillEntity(ChapterType.SecretBiography.getValue(), Chapter.SB1.getValue());
        int skillScroll = 0;
        do {
            String skillScrollName = PowerRandom.getRandomFromList(byPalaceSkill.getSkills());
            skillScroll = TreasureTool.getTreasureByName(skillScrollName).getId();
        } while (excludeSkillScroll.contains(skillScroll));
        SxdhZoneMallRecord record = SxdhZoneMallRecord.instance(serverGroup, zoneType, MiZ_MALL_ID, skillScroll);
        return record;
    }

    /**
     * 获取神仙大会zone,如果当前时间<beanExpiredDate,返回上个赛季的战区，否则返回当前战区
     *
     * @param uid
     * @return
     */
    private SxdhZone getSxdhZone(long uid) {
        CfgServerEntity server = ServerTool.getServer(gameUserService.getActiveSid(uid));
        if (DateUtil.now().before(sxdhDateService.getBeanExpireDateAsThisMonth())) {
            SxdhZone lastZone = sxdhZoneService.getLastZone(server);
            //新开服没有上赛季，返回上赛季第一战区
            if (null == lastZone) {
                sxdhZoneService.getLastZone(server.getGroupId(), ZoneType.ZONE_ONE.getValue());
            }
        }
        return sxdhZoneService.getCurOrLastZone(uid);
    }

}
