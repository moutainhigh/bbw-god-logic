package com.bbw.god.game.dfdj.store;

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
import com.bbw.god.game.dfdj.DfdjDateService;
import com.bbw.god.game.dfdj.config.ZoneType;
import com.bbw.god.game.dfdj.zone.DfdjZone;
import com.bbw.god.game.dfdj.zone.DfdjZoneService;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 巅峰对决商店
 *
 * @author suhq
 * @date 2021-01-08 16:11
 **/
@Service
public class DfdjStoreProgress extends AbstractStoreProcessor {
    private static int ChuanQJZ_MALL_ID = 200070;
    private static int MiZ_MALL_ID = 200080;

    @Autowired
    private DfdjZoneService dfdjZoneService;
    @Autowired
    private GameDataService gameDataService;
    @Autowired
    private DfdjDateService dfdjDateService;

    @Override
    public boolean isMatch(int mallType) {
        return StoreEnum.DFDJ.getType() == mallType;
    }

    @Override
    public RDStore getGoodsList(long guId) {
        List<RDStoreGoodsInfo> rdShopProducts = new ArrayList<>();
        List<CfgMallEntity> malls = MallTool.getMallConfig().getDfdjMalls();
        List<DfdjZoneMallRecord> zoneMallRecords = getCurZoneMallRecords(gameUserService.getOriServer(guId).getGroupId());
        DfdjZone dfdjZone = getDfdjZone(guId);
        for (CfgMallEntity mall : malls) {
            RDStoreGoodsInfo goodsInfo = new RDStoreGoodsInfo();
            goodsInfo.setItem(mall.getItem());
            goodsInfo.setMallId(mall.getId());
            goodsInfo.setRealId(mall.getGoodsId());
            //设置仙豆购买信息
            RDStoreGoodsInfo.BuyType beanBuyType = new RDStoreGoodsInfo.BuyType();
            beanBuyType.setConsume(ConsumeType.GOLD_BEAN.getValue());
            beanBuyType.setBoughtTimes(0);
            beanBuyType.setLimit(mall.getLimit());
            beanBuyType.setPrice(mall.getPrice());
            //战区限制设置购买次数
            if (isZoneLimit(mall)) {
                DfdjZoneMallRecord record = zoneMallRecords.stream().filter(tmp -> tmp.getZoneType() == dfdjZone.getZone() && tmp.getMallId() == mall.getId().intValue()).findFirst().orElse(null);
                beanBuyType.setBoughtTimes(record.getNum());
                goodsInfo.setRealId(record.getGoodId());
            }
            goodsInfo.addBuyType(beanBuyType);
            rdShopProducts.add(goodsInfo);
        }
        RDStore rdStore = new RDStore();
        rdStore.setIntegralGoods(rdShopProducts);
        rdStore.setCurrency(userTreasureService.getTreasureNum(guId, TreasureEnum.GOLD_BEAN.getValue()));
        return rdStore;
    }

    @Override
    public RDCommon buyGoods(long uid, int mallId, int buyNum, Integer consume) {
        RDCommon rd = new RDCommon();
        CfgMallEntity mall = MallTool.getMall(mallId);
        int needBean = mall.getPrice() * buyNum;
        TreasureChecker.checkIsEnough(TreasureEnum.GOLD_BEAN.getValue(), needBean, uid);
        //无限制兑换
        if (mallId != ChuanQJZ_MALL_ID && mallId != MiZ_MALL_ID) {
            TreasureEventPublisher.pubTAddEvent(uid, mall.getGoodsId(), buyNum, WayEnum.DFDJ_SHOP_BUY, rd);
            //消耗仙豆
            TreasureEventPublisher.pubTDeductEvent(uid, TreasureEnum.GOLD_BEAN.getValue(), needBean, WayEnum.DFDJ_SHOP_BUY, rd);
            return rd;
        }
        //战区限制兑换
        List<DfdjZoneMallRecord> zoneMallRecords = getCurZoneMallRecords(gameUserService.getOriServer(uid).getGroupId());
        DfdjZone dfdjZone = getDfdjZone(uid);
        DfdjZoneMallRecord record = zoneMallRecords.stream().filter(tmp -> tmp.getZoneType() == dfdjZone.getZone() && tmp.getMallId() == mall.getId().intValue()).findFirst().orElse(null);
        if (record.getNum() >= mall.getLimit()) {
            throw new ExceptionForClientTip("mall.is.outOfLimit", mall.getLimit().toString());
        }
        //消耗金豆
        TreasureEventPublisher.pubTDeductEvent(uid, TreasureEnum.GOLD_BEAN.getValue(), needBean, WayEnum.DFDJ_SHOP_BUY, rd);

        record.addNum(buyNum);
        gameDataService.updateGameData(record);
        TreasureEventPublisher.pubTAddEvent(uid, record.getGoodId(), buyNum, WayEnum.DFDJ_SHOP_BUY, rd);
        return rd;
    }

    /**
     * 是否战区限制购买
     *
     * @param mall
     * @return
     */
    private boolean isZoneLimit(CfgMallEntity mall) {
        if (mall.getId() == ChuanQJZ_MALL_ID || mall.getId() == MiZ_MALL_ID) {
            return true;
        }
        return false;
    }

    /**
     * 获取当前战区记录
     *
     * @return
     */
    private List<DfdjZoneMallRecord> getCurZoneMallRecords(int serverGroup) {
        Date now = DateUtil.now();
        List<DfdjZoneMallRecord> records = gameDataService.getGameDatas(DfdjZoneMallRecord.class);
        List<DfdjZoneMallRecord> curRecords = records.stream()
                .filter(tmp -> tmp.getExpireDate().after(now) && (null != tmp.getServerGroup() && serverGroup == tmp.getServerGroup()))
                .collect(Collectors.toList());

        if (records.size() == 0 || curRecords.size() == 0) {
            curRecords = newZoneRecords(serverGroup, records);
        }
        return curRecords;
    }

    private List<DfdjZoneMallRecord> newZoneRecords(int serverGroup, List<DfdjZoneMallRecord> records) {
        List<DfdjZoneMallRecord> newRecords = new ArrayList<>();
        for (ZoneType zoneType : ZoneType.values()) {
            //传奇卷轴战区记录
            DfdjZoneMallRecord record1 = DfdjZoneMallRecord.instance(serverGroup, zoneType.getValue(), ChuanQJZ_MALL_ID, TreasureEnum.LEGEND_SKILL_SCROLL_BOX.getValue());
            newRecords.add(record1);
            //秘传战区记录
            newRecords.add(buildMiZMallRecord(serverGroup, zoneType.getValue(), records));
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
    private DfdjZoneMallRecord buildMiZMallRecord(int serverGroup, int zoneType, List<DfdjZoneMallRecord> records) {
        List<Integer> miZhuanRecords = records.stream().filter(tmp -> tmp.getZoneType() == zoneType && tmp.getMallId() == MiZ_MALL_ID).map(DfdjZoneMallRecord::getGoodId).collect(Collectors.toList());
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
        DfdjZoneMallRecord record = DfdjZoneMallRecord.instance(serverGroup, zoneType, MiZ_MALL_ID, skillScroll);
        return record;
    }

    /**
     * 获取神仙大会zone,如果当前时间<beanExpiredDate,返回上个赛季的战区，否则返回当前战区
     *
     * @param uid
     * @return
     */
    private DfdjZone getDfdjZone(long uid) {
        CfgServerEntity server = ServerTool.getServer(gameUserService.getActiveSid(uid));
        if (DateUtil.now().before(dfdjDateService.getBeanExpireDateAsThisMonth())) {
            return dfdjZoneService.getLastZone(server);
        }
        return dfdjZoneService.getCurOrLastZone(uid);
    }

}
