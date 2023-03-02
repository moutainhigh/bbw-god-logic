package com.bbw.god.city.nvwm.nightmare.nuwamarket;

import com.bbw.common.DateUtil;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.city.nvwm.nightmare.nuwamarket.marketenum.BargainProductEnum;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.game.award.giveback.GiveBackAwards;
import com.bbw.god.game.award.giveback.GiveBackPool;
import com.bbw.god.game.data.GameData;
import com.bbw.god.game.data.GameDataService;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.UserData;
import com.bbw.god.gameuser.nightmarenvwam.NightmareNvWamCfgTool;
import com.bbw.god.gameuser.nightmarenvwam.cfg.CfgNightmareNvmOutputEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 女娲集市服务
 *
 * @author fzj
 * @date 2022/5/9 9:25
 */
@Service
public class NvWaMarketService {
    @Autowired
    private GameDataService gameDataService;
    @Autowired
    private GiveBackPool giveBackPool;
    @Autowired
    private GameNvWaMarketNumService gameNvWaMarketNumService;
    @Autowired
    private NvWaMarketLogic nvWaMarketLogic;
    @Autowired
    private UserNvWaMarketService userNvWaMarketService;
    @Autowired
    private GameUserService gameUserService;

    private static final int MAX_RECORD_NUM = 50;

    /**
     * 获取所有摊位数据
     *
     * @return
     */
    public List<GameNvWaBooth> getAllBooth() {
        return gameDataService.getGameDatas(GameNvWaBooth.class);
    }

    /**
     * 获取摊位
     *
     * @param boothId
     * @return
     */
    public GameNvWaBooth getBooth(long boothId) {
        return gameDataService.getGameData(boothId, GameNvWaBooth.class);
    }

    /**
     * 添加还价信息
     *
     * @param bargainNo
     * @param sponsor
     * @param boothNo
     * @param productNo
     * @param expireTime
     */
    public void addNvWaMarketBargain(long bargainNo, long sponsor, int boothNo, long productNo, GoodsInfo goodsInfo, Date expireTime, List<GoodsInfo> price, String message) {
        GameNvWaMarketBargain marketBargain = GameNvWaMarketBargain.getInstance(bargainNo, sponsor, boothNo, productNo, goodsInfo, expireTime, price, message);
        gameDataService.addGameData(marketBargain);
    }

    /**
     * 获得对应还价信息
     *
     * @param bargainId
     * @return
     */
    public GameNvWaMarketBargain getNvWaMarketBargain(long bargainId) {
        return gameDataService.getGameData(bargainId, GameNvWaMarketBargain.class);
    }

    /**
     * 获得摊位讨价
     *
     * @param boothNo
     * @return
     */
    public List<GameNvWaMarketBargain> getBoothNvWaMarketBargain(int boothNo) {
        List<GameNvWaMarketBargain> gameDatas = getNvWaMarketBargain();
        if (gameDatas.isEmpty()) {
            return new ArrayList<>();
        }
        return gameDatas.stream().filter(g -> g.getBoothNo() == boothNo).collect(Collectors.toList());
    }

    /**
     * 获得玩家讨价信息
     *
     * @param uid
     * @return
     */
    public List<GameNvWaMarketBargain> getUserNvWaMarketBargain(long uid) {
        List<GameNvWaMarketBargain> gameDatas = getNvWaMarketBargain();
        if (gameDatas.isEmpty()) {
            return new ArrayList<>();
        }
        return gameDatas.stream().filter(g -> g.getSponsor() == uid).collect(Collectors.toList());
    }

    /**
     * 获得讨价信息
     *
     * @return
     */
    public List<GameNvWaMarketBargain> getNvWaMarketBargain() {
        return gameDataService.getGameDatas(GameNvWaMarketBargain.class);
    }

    /**
     * 执行加入待还列表操作
     */
    public void executeAddPendOperate(int minutes) {
        List<GameNvWaMarketBargain> nvWaMarketBargain = getNvWaMarketBargain();
        if (nvWaMarketBargain.isEmpty()) {
            return;
        }
        List<GameNvWaMarketBargain> notDealWithList = nvWaMarketBargain.stream().filter(n -> !n.isDealWith()).collect(Collectors.toList());
        if (notDealWithList.isEmpty()) {
            return;
        }
        //过期的
        List<GameNvWaMarketBargain> expired = notDealWithList.stream().filter(n ->
        {
            Date addMinutes = DateUtil.addMinutes(n.getExpireTime(), minutes);
            return DateUtil.now().after(addMinutes);
        }).collect(Collectors.toList());
        if (expired.isEmpty()) {
            return;
        }
        //加入待还列表
        List<GiveBackAwards> list = addPendOperate(expired);
        giveBackPool.toGiveBackPool(list);
        gameDataService.updateGameDatas(expired);
    }

    /**
     * 加入待还列表
     *
     * @param waMarketBargains
     * @return
     */
    public List<GiveBackAwards> addPendOperate(List<GameNvWaMarketBargain> waMarketBargains) {
        List<GiveBackAwards> list = new ArrayList<>();
        for (GameNvWaMarketBargain bargain : waMarketBargains) {
            long sponsor = bargain.getSponsor();
            List<GoodsInfo> price = bargain.getPrice();
            List<Award> awards = GoodsInfo.getAwards(price, AwardEnum.FB);
            //更新状态
            bargain.updateBargainStatus(BargainProductEnum.EXPIRED.getValue());
            GiveBackAwards giveBackAwards = GiveBackAwards.instance(sponsor, bargain.getId(), awards, "女娲集市", "还价道具返还");
            list.add(giveBackAwards);
        }
        return list;
    }

    /**
     * 处理过期摊位
     */
    public void handlingExpiredBooth() {
        List<Long> allExpiredBoothId = gameNvWaMarketNumService.getAllExpiredBoothId();
        if (allExpiredBoothId.isEmpty()) {
            return;
        }
        List<Long> boothIds = new ArrayList<>();
        for (long boothId : allExpiredBoothId) {
            GameNvWaBooth booth = getBooth(boothId);
            if (null == booth) {
                continue;
            }
            nvWaMarketLogic.handlingExpiredBooth(booth);
            boothIds.add(boothId);
        }
        //删除摊位数据
        gameDataService.deleteGameDatas(boothIds, GameNvWaBooth.class);
    }

    /**
     * 过期还价处理
     *
     * @param hours
     */
    public void handlingExpiredBargain(int hours) {
        List<GameNvWaMarketBargain> nvWaMarketBargain = getNvWaMarketBargain();
        if (nvWaMarketBargain.isEmpty()) {
            return;
        }
        //筛选已过期1小时的
        List<GameNvWaMarketBargain> expired = nvWaMarketBargain.stream().filter(n ->
        {
            Date addHours = DateUtil.addHours(n.getExpireTime(), hours);
            return n.isDealWith() && DateUtil.now().after(addHours);
        }).collect(Collectors.toList());
        if (expired.isEmpty()) {
            return;
        }
        List<Integer> boothNos = expired.stream().map(GameNvWaMarketBargain::getBoothNo).distinct().collect(Collectors.toList());
        List<Long> expiredBargain = expired.stream().map(GameData::getId).collect(Collectors.toList());
        List<GameNvWaBooth> handlingBooths = new ArrayList<>();
        for (Integer boothNo : boothNos) {
            Long boothId = gameNvWaMarketNumService.getBoothId(boothNo);
            if (null == boothId) {
                continue;
            }
            GameNvWaBooth booth = getBooth(boothId);
            if (null == booth) {
                continue;
            }
            booth.delBargain(expiredBargain);
            handlingBooths.add(booth);
        }
        //更新删除
        gameDataService.updateGameDatas(handlingBooths);
        gameDataService.deleteGameDatas(expiredBargain, GameNvWaMarketBargain.class);
    }

    /**
     * 检查是否属于女娲集市道具
     *
     * @param treasureId
     */
    public void checkNvWaMarketGoods(int treasureId) {
        List<Integer> treasureIds = NightmareNvWamCfgTool.getNightmareNvmOutput().stream()
                .map(CfgNightmareNvmOutputEntity::getTreasureId).collect(Collectors.toList());
        if (!treasureIds.contains(treasureId)) {
            throw new ExceptionForClientTip("nightmareNvWaM.nvWaMarket.not.treasure");
        }
    }

    /**
     * 获得摊位所有要价信息
     *
     * @param boothNo
     * @return
     */
    public List<GameNvWaMarketBargain> getBoothBargain(int boothNo) {
        List<GameNvWaMarketBargain> nvWaMarketBargain = getNvWaMarketBargain();
        if (nvWaMarketBargain.isEmpty()) {
            return new ArrayList<>();
        }
        return nvWaMarketBargain.stream().filter(n -> n.getBoothNo() == boothNo).collect(Collectors.toList());
    }

    /**
     * 获得某个摊位商品要价信息
     *
     * @param boothNo
     * @param productId
     * @return
     */
    public List<GameNvWaMarketBargain> getProductBargain(int boothNo, long productId) {
        return getBoothBargain(boothNo).stream().filter(n -> n.getProductId() == productId).collect(Collectors.toList());
    }

    /**
     * 获得摊位
     *
     * @param boothNo
     * @return
     */
    public GameNvWaBooth getBoothByNo(Integer boothNo) {
        Long boothFiled = gameNvWaMarketNumService.getBoothId(boothNo);
        if (null == boothFiled) {
            throw new ExceptionForClientTip("nightmareNvWaM.nvWaMarket.booth.expired");
        }
        GameNvWaBooth booth = getBooth(boothFiled);
        //检查是否过期
        if (booth.isExpired()) {
            throw new ExceptionForClientTip("nightmareNvWaM.nvWaMarket.booth.expired");
        }
        return booth;
    }

    /**
     * 获得摊位
     *
     * @param boothId
     * @return
     */
    public GameNvWaBooth getBoothInfo(long boothId) {
        GameNvWaBooth gameNvWaMarket = getBooth(boothId);
        if (null == gameNvWaMarket) {
            throw new ExceptionForClientTip("nightmareNvWaM.nvWaMarket.booth.expired");
        }
        //检查是否过期
        if (gameNvWaMarket.isExpired()) {
            throw new ExceptionForClientTip("nightmareNvWaM.nvWaMarket.booth.expired");
        }
        return gameNvWaMarket;
    }

    /**
     * 获得玩家Id
     *
     * @param uid
     * @return
     */
    public GameNvWaBooth getUserBooth(long uid) {
        Long userBoothId = gameNvWaMarketNumService.getUserUnexpiredBoothId(uid);
        if (null == userBoothId) {
            throw new ExceptionForClientTip("nightmareNvWaM.nvWaMarket.booth.expired");
        }
        return getBooth(userBoothId);
    }

    /**
     * 是否过期
     *
     * @param leaseTime
     * @return
     */
    public boolean isExpired(Date leaseTime) {
        return DateUtil.now().after(leaseTime);
    }

    /**
     * 获得红点
     *
     * @param uid
     * @return
     */
    public int getNvWaMarketNotice(long uid) {
        Long userBoothId = gameNvWaMarketNumService.getUserBoothId(uid);
        if (null == userBoothId) {
            return 0;
        }
        GameNvWaBooth nvWaMarket = getBooth(userBoothId);
        if (null == nvWaMarket) {
            return 0;
        }
        List<GameNvWaMarketBargain> boothBargain = getBoothBargain(nvWaMarket.getBoothNo());
        if (boothBargain.isEmpty()) {
            return 0;
        }
        return Math.toIntExact(boothBargain.stream().filter(b -> !b.isExpired() && !b.isDealWith()).count());
    }


    /**
     * 检查商品一致性
     *
     * @param priceNo
     * @param productInfo
     * @param oldProductInfo
     */
    public void checkProductInfoConsistent(Integer priceNo, GameNvWaBooth.ProductInfo productInfo, String oldProductInfo) {
        if ("".equals(oldProductInfo)) {
            throw new ExceptionForClientTip("nightmareNvWaM.nvWaMarket.productInfo.check");
        }
        //商品一致性判断
        if (null != productInfo) {
            GoodsInfo goods = productInfo.getGoods();
            String oldGoods = oldProductInfo.split(":")[0];
            int oldGoodId = Integer.parseInt(oldGoods.split("_")[0]);
            int oldMinSellNum = Integer.parseInt(oldGoods.split("_")[1]);
            if (oldGoodId != goods.getId() || productInfo.getMinSellNum() != oldMinSellNum) {
                throw new ExceptionForClientTip("nightmareNvWaM.nvWaMarket.productInfo.check");
            }
        }
        //价格一致性判断
        if (null != priceNo) {
            String oldPriceInfo = oldProductInfo.split(":")[1];
            List<GoodsInfo> oldPrice = GoodsInfo.getGoods(oldPriceInfo);
            String price = productInfo.getProductPrices().get(priceNo).getPrice();
            List<GoodsInfo> newPrice = GoodsInfo.getGoods(price);
            boolean isUpdate = newPrice.retainAll(oldPrice);
            if (isUpdate) {
                throw new ExceptionForClientTip("nightmareNvWaM.nvWaMarket.productInfo.check");
            }
        }
    }

    /**
     * 交易记录限制处理
     *
     * @param uid
     */
    public void tradeRecordLimitProcess(long uid) {
        List<UserNvWaTradeRecord> userNvWaMarketTaredRecord = userNvWaMarketService.getUserNvWaMarketTaredRecord(uid);
        if (userNvWaMarketTaredRecord.size() < MAX_RECORD_NUM) {
            return;
        }
        //最早的记录
        UserNvWaTradeRecord userNvWaTradeRecord = userNvWaMarketTaredRecord.stream().min(Comparator.comparing(UserNvWaTradeRecord::getTradeDate)).get();
        gameUserService.deleteItem(userNvWaTradeRecord);
    }
}
