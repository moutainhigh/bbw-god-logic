package com.bbw.god.mall.store;

import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.card.UserCard;
import com.bbw.god.gameuser.card.UserCardService;
import com.bbw.god.gameuser.treasure.UserTreasureService;
import com.bbw.god.mall.MallLogic;
import com.bbw.god.mall.RDMallInfo;
import com.bbw.god.mall.RDMallList;
import com.bbw.god.mall.RDMaouMallInfo;
import com.bbw.god.rd.RDCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 商店逻辑
 *
 * @author lwb
 * @date 2020/3/24 9:45
 */
@Service
public class StoreLogic {
    @Autowired
    private UserCardService userCardService;
    @Autowired
    private StoreProcessorFactory storeProcessorFactory;
    @Autowired
    private MallLogic mallLogic;
    @Autowired
    private UserTreasureService userTreasureService;

    /**
     * 获取商品列表
     *
     * @return
     */
    public RDStore getGoodsList(long uid, int type) {
        if (StoreEnum.cfgByMallConfig(type)) {
            //配置为商城基础数据
            return getGoodsByCfgMall(uid, type);
        }
        //配置为单独配置
        AbstractStoreProcessor storeProcessor = storeProcessorFactory.getStoreProcessor(type);
        return storeProcessor.getGoodsList(uid);
    }

    /**
     * 购买商品
     *
     * @param uid
     * @param mallId  商品ID（非实际ID）
     * @param buyNum  (数量，默认1)
     * @param consume （购买单位，选出，默认为配置的第一种）
     * @return
     */
    public RDCommon buyGoods(long uid, int mallId, int buyNum, int type, Integer consume) {
        if (StoreEnum.cfgByMallConfig(type)) {
            //配置为商城基础数据
            return mallLogic.buy(uid, mallId, buyNum);
        }
        //配置为单独配置
        AbstractStoreProcessor storeProcessor = storeProcessorFactory.getStoreProcessor(type);
        return storeProcessor.buyGoods(uid, mallId, buyNum, consume);
    }

    /**
     * 普通类型的商城物品，无需权限的
     *
     * @param uid
     * @param type
     * @return
     */
    private RDStore getGoodsByCfgMall(long uid, int type) {
        if (StoreEnum.MAOU.getType() == type) {
            return getMaouGoodsByCfgMall(uid, type);
        }
        List<RDStoreGoodsInfo> goodsInfoList = new ArrayList<>();
        RDMallList rdMallList = mallLogic.getProducts(uid, type);
        for (RDMallInfo mallInfo : rdMallList.getMallGoods()) {
            RDStoreGoodsInfo.BuyType buyType = RDStoreGoodsInfo.BuyType.instance(mallInfo);
            RDStoreGoodsInfo rdsg = RDStoreGoodsInfo.instance(mallInfo, buyType);
            goodsInfoList.add(rdsg);
        }
        RDStore rdStore = new RDStore();
        rdStore.setIntegralGoods(goodsInfoList);
        if (StoreEnum.ZXZ.getType() == type) {
            rdStore.setCurrency(userTreasureService.getTreasureNum(uid, TreasureEnum.ZXZ_POINT.getValue()));
        } else if (StoreEnum.FST.getType() == type) {
            rdStore.setCurrency(userTreasureService.getTreasureNum(uid, TreasureEnum.FST_POINT.getValue()));
            //特殊过滤孔雀明王
            UserCard card = userCardService.getUserCard(uid, 10430);
            if (card!=null){
                Optional<RDStoreGoodsInfo> optional = goodsInfoList.stream().filter(p -> p.getRealId() == 430 && p.getItem() == 40).findFirst();
                if (optional.isPresent()){
                    optional.get().setRealId(10430);
                }
            }
        }
        return rdStore;
    }

    /**
     * 魔王商店需要权限特殊处理
     *
     * @param uid
     * @param type
     * @return
     */
    private RDStore getMaouGoodsByCfgMall(long uid, int type) {
        List<RDStoreGoodsInfo> goodsInfoList = new ArrayList<>();
        RDMallList rdMallList = mallLogic.getProducts(uid, type);
        List<Integer> authList = rdMallList.getUserAuthList();
        Map<Integer, Integer> maps = new HashMap<>();
        if (authList != null && !authList.isEmpty()) {
            for (Integer val : authList) {
                int maouType = val / 100 * 10;
                int maouLevel = val % 10;
                maouLevel = maouLevel == 0 ? 10 : maouLevel;
                maps.put(maouType, maouLevel);
            }
        }
        for (RDMallInfo mallInfo : rdMallList.getMallGoods()) {
            RDMaouMallInfo rdMaouMallInfo = (RDMaouMallInfo) mallInfo;
            RDStoreGoodsInfo.BuyType buyType = RDStoreGoodsInfo.BuyType.instance(mallInfo);
            buyType.setPrice(rdMaouMallInfo.getMaouSoul());
            if (rdMaouMallInfo.getAuthority() != null) {
                //需要设置权限
                Integer level = maps.get(rdMaouMallInfo.getMaouType());
                if (level == null || level < rdMaouMallInfo.getMaouLevel()) {
                    String maouStr = "魔王";
                    switch (rdMaouMallInfo.getMaouType()) {
                        case 10:
                            maouStr = "金之魔王";
                            break;
                        case 20:
                            maouStr = "木之魔王";
                            break;
                        case 30:
                            maouStr = "水之魔王";
                            break;
                        case 40:
                            maouStr = "火之魔王";
                            break;
                        case 50:
                            maouStr = "土之魔王";
                            break;
                    }
                    buyType.setPermit("击败第" + rdMaouMallInfo.getMaouLevel() + "层" + maouStr);
                }
            }
            RDStoreGoodsInfo rdsg = RDStoreGoodsInfo.instance(mallInfo, buyType);
            goodsInfoList.add(rdsg);
        }
        RDStore rdStore = new RDStore();
        rdStore.setIntegralGoods(goodsInfoList);
        rdStore.setCurrency(userTreasureService.getTreasureNum(uid, TreasureEnum.MoWH.getValue()));
        return rdStore;
    }

}
