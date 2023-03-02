package com.bbw.god.city.nvwm.nightmare.nuwamarket;

import com.bbw.common.DateUtil;
import com.bbw.common.ID;
import com.bbw.common.LM;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.city.UserCityService;
import com.bbw.god.city.nvwm.nightmare.nuwamarket.marketenum.*;
import com.bbw.god.city.nvwm.nightmare.nuwamarket.rd.*;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.game.award.AwardService;
import com.bbw.god.game.award.giveback.GiveBackAwards;
import com.bbw.god.game.award.giveback.GiveBackPool;
import com.bbw.god.game.config.TypeEnum;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.server.ServerTool;
import com.bbw.god.game.config.treasure.CfgTreasureEntity;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.game.config.treasure.TreasureTool;
import com.bbw.god.game.data.GameDataService;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.mail.MailService;
import com.bbw.god.gameuser.mail.MailType;
import com.bbw.god.gameuser.mail.UserMail;
import com.bbw.god.gameuser.nightmarenvwam.NightmareNvWamCfgTool;
import com.bbw.god.gameuser.nightmarenvwam.cfg.CfgNightmareNvmOutputEntity;
import com.bbw.god.gameuser.redis.UserRedisKey;
import com.bbw.god.gameuser.treasure.HonorCurrencyService;
import com.bbw.god.gameuser.treasure.TreasureChecker;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.rd.RDCommon;
import com.bbw.god.rd.RDSuccess;
import com.bbw.god.validator.GodValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 女娲集市逻辑
 *
 * @author fzj
 * @date 2022/5/9 9:01
 */
@Service
@Slf4j
public class NvWaMarketLogic {
    private static final int MAX_MILLIS = 100;
    @Autowired
    private NvWaMarketService nvWaMarketService;
    @Autowired
    private GameNvWaMarketNumService gameNvWaMarketNumService;
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private MailService mailService;
    @Autowired
    private UserNvWaMarketService userNvWaMarketService;
    @Autowired
    private GameDataService gameDataService;
    @Autowired
    private AwardService awardService;
    @Autowired
    private GiveBackPool giveBackPool;
    @Autowired
    private NvWaMarketRedisLockService nvWaMarketRedisLockService;
    @Autowired
    private HonorCurrencyService honorCurrencyService;
    @Autowired
    private GameNvWaBoothCacheService gameNvWaBoothCacheService;
    @Autowired
    private UserCityService userCityService;

    /**
     * 进入女娲集市
     */
    public RDNvWaMarketInfos enterNvWMarket(long uid, int page) {
        //检查是否解锁梦魇女娲庙
        GameUser gu = gameUserService.getGameUser(uid);
        if (!isActive(gu)) {
            throw new ExceptionForClientTip("activity.function.not.open");
        }
        //摊位id
        List<Long> boothIds = gameNvWaMarketNumService.getBoothIds(page);
        RDNvWaMarketInfos rd = new RDNvWaMarketInfos();
        if (boothIds.isEmpty()) {
            return rd;
        }
        List<GameNvWaBooth> allBooth = new ArrayList<>();
        for (long boothId : boothIds) {
            GameNvWaBooth nvWaMarket = nvWaMarketService.getBooth(boothId);
            allBooth.add(nvWaMarket);
        }
        List<RDNvWaMarketInfos.RDNvWaMarketInfo> rdNvWaMarketInfos = new ArrayList<>();
        for (GameNvWaBooth gameNuWaMarket : allBooth) {
            RDNvWaMarketInfos.RDNvWaMarketInfo rdNvWaMarketInfo = RDNvWaMarketInfos.getInstance(gameNuWaMarket);
            rdNvWaMarketInfos.add(rdNvWaMarketInfo);
            if (uid == gameNuWaMarket.getUid()) {
                rdNvWaMarketInfo.setUserBooth(true);
            }
        }
        rd.setRdNvWaMarketInfos(rdNvWaMarketInfos);
        int allBoothNum = gameNvWaMarketNumService.getAllUnexpiredBoothId().size();
        rd.setTotalBoothNum(allBoothNum);
        return rd;
    }

    /**
     * 是否激活建筑功能
     *
     * @param gu
     * @return
     */
    private boolean isActive(GameUser gu) {
        boolean isActive = userCityService.isOwnLowNightmareCityAsCountry(gu.getId(), TypeEnum.Wood.getValue());
        return isActive && gu.getStatus().ifNotInFsdlWorld();
    }

    /**
     * 搜索摊位
     *
     * @param message
     * @return
     */
    public RDNvWaMarketInfos searchBooth(String message) {
        boolean isBoothId = message.matches("-?[0-9]+.?[0-9]*");
        RDNvWaMarketInfos rd = new RDNvWaMarketInfos();
        List<RDNvWaMarketInfos.RDNvWaMarketInfo> rdNvWaMarketInfos = new ArrayList<>();
        //摊位号搜索
        if (isBoothId) {
            Integer msg = Integer.valueOf(message);
            Long boothId = gameNvWaMarketNumService.getBoothId(msg);
            if (null == boothId) {
                return rd;
            }
            GameNvWaBooth booth = nvWaMarketService.getBooth(boothId);
            if (null == booth) {
                return rd;
            }
            RDNvWaMarketInfos.RDNvWaMarketInfo rdNvWaMarketInfo = RDNvWaMarketInfos.getInstance(booth);
            rdNvWaMarketInfos.add(rdNvWaMarketInfo);
            rd.setRdNvWaMarketInfos(rdNvWaMarketInfos);
            rd.setTotalBoothNum(1);
            return rd;
        }
        //道具名称搜索
        Long began = System.currentTimeMillis();
        Optional<CfgTreasureEntity> entity = TreasureTool.getAllTreasures().stream().filter(t -> message.equals(t.getName())).findFirst();
        if (!entity.isPresent()) {
            return rd;
        }
        CfgTreasureEntity treasureEntity = entity.get();
        Integer treasureId = treasureEntity.getId();
        List<CfgNightmareNvmOutputEntity> output = NightmareNvWamCfgTool.getNightmareNvmOutput()
                .stream().filter(n -> n.getTreasureId().equals(treasureId)).collect(Collectors.toList());
        if (output.isEmpty()) {
            return rd;
        }
        List<Long> treasureBooths = gameNvWaBoothCacheService.getTreasureBooths(treasureId);
        if (treasureBooths.isEmpty()) {
            return rd;
        }
        for (Long boothId : treasureBooths) {
            GameNvWaBooth booth = nvWaMarketService.getBooth(boothId);
            if (null == booth) {
                continue;
            }
            boolean isHasTreasure = booth.getProductInfos().stream().anyMatch(s -> treasureId.equals(s.getGoods().getId()));
            if (!isHasTreasure) {
                continue;
            }
            RDNvWaMarketInfos.RDNvWaMarketInfo rdNvWaMarketInfo = RDNvWaMarketInfos.getInstance(booth);
            rdNvWaMarketInfos.add(rdNvWaMarketInfo);
        }
        rd.setRdNvWaMarketInfos(rdNvWaMarketInfos);
        rd.setTotalBoothNum(rdNvWaMarketInfos.size());
        Long end = System.currentTimeMillis();
        long time = end - began;
        log.info("女娲集市摊位查询耗时：" + time + "毫秒");
        if (time > MAX_MILLIS) {
            log.error("女娲集市摊位查询耗时过长，耗时：" + time + "毫秒");
        }
        return rd;
    }

    /**
     * 获得摊位信息
     *
     * @param boothNo
     * @return
     */
    public RDBoothInfo getBoothInfo(Integer boothNo) {
        GameNvWaBooth booth = nvWaMarketService.getBoothByNo(boothNo);
        return getRDBooth(booth);
    }

    /**
     * 获得摊位
     *
     * @param booth
     * @return
     */
    public RDBoothInfo getRDBooth(GameNvWaBooth booth) {
        RDBoothInfo rd = new RDBoothInfo();
        rd.setBoothNo(booth.getBoothNo());
        //获得摊主信息
        Long bootId = booth.getUid();
        GameUser gameUser = gameUserService.getGameUser(bootId);
        GameUser.RoleInfo roleInfo = gameUser.getRoleInfo();
        String ownerInfo = ServerTool.getServerShortName(gameUser.getServerId()) + "·" + roleInfo.getNickname();
        rd.setOwnerInfo(ownerInfo);
        rd.setHead(roleInfo.getHead());
        rd.setRemainTime(DateUtil.millisecondsInterval(booth.getLeaseEndTime(), DateUtil.now()));
        rd.setStatus(booth.getBoothStatus());
        rd.setMessage(booth.getSlogan());
        //商品信息
        List<GameNvWaBooth.ProductInfo> productInfos = booth.getProductInfos();
        List<GameNvWaMarketBargain> boothBargain = nvWaMarketService.getBoothBargain(booth.getBoothNo());
        List<RDBoothInfo.RDProductInfo> rdProductInfos = new ArrayList<>();
        for (GameNvWaBooth.ProductInfo productInfo : productInfos) {
            RDBoothInfo.RDProductInfo rdProductInfo = RDBoothInfo.getInstance(productInfo);
            rdProductInfos.add(rdProductInfo);
            if (boothBargain.isEmpty()) {
                continue;
            }
            boolean isHasBargain = boothBargain.stream().anyMatch(n -> n.getProductId().equals(productInfo.getProductId())
                    && n.getStatus() == BargainProductEnum.UNDECIDED.getValue());
            rdProductInfo.setHasBargain(isHasBargain);
        }
        rd.setProductInfos(rdProductInfos);
        return rd;
    }

    /**
     * 发送消息
     *
     * @param uid
     * @param boothNo
     * @param message
     */
    public RDSuccess sendMessage(long uid, Integer boothNo, String message) {
        GameNvWaBooth booth = nvWaMarketService.getBoothByNo(boothNo);
        //不能为空
        if ("".equals(message)) {
            throw new ExceptionForClientTip("mail.content.not.empty");
        }
        //收件人
        Long receiverId = booth.getUid();
        //发送邮件
        String title = LM.I.getMsgByUid(receiverId, "nightmareNvWaM.nvWaMarket.mail.title");
        UserMail mail = new UserMail();
        mail.setMailId(UserRedisKey.getNewUserDataId());
        mail.setTitle(title);
        mail.setContent(message);
        mail.setReceiverId(receiverId);
        mail.setGameUserId(uid);
        mail.setSenderId(uid);
        GameUser gameUser = gameUserService.getGameUser(uid);
        GameUser.RoleInfo roleInfo = gameUser.getRoleInfo();
        String nickname = ServerTool.getServerShortName(gameUser.getServerId()) + "·" + roleInfo.getNickname();
        mail.setSenderNickName(nickname);
        mail.setType(MailType.PLAYER);
        GodValidator.validateEntity(mail);
        mailService.send(mail);
        return new RDSuccess();
    }

    /**
     * 获得我的摊位
     *
     * @param uid
     */
    public RDBoothInfo getUserBooth(long uid) {
        Long userBoothId = gameNvWaMarketNumService.getUserBoothId(uid);
        if (null == userBoothId) {
            return new RDBoothInfo();
        }
        GameNvWaBooth nvWaMarket = nvWaMarketService.getBooth(userBoothId);
        if (nvWaMarket.isExpired()) {
            //处理过期摊位
            handlingExpiredBooth(nvWaMarket);
            //删除摊位数据
            gameDataService.deleteGameData(nvWaMarket);
            return new RDBoothInfo();
        }
        //更新还价信息
        List<GameNvWaBooth.BargainInfo> bargainInfos = nvWaMarket.getBargainInfos();
        List<GameNvWaMarketBargain> bargains = new ArrayList<>();
        for (GameNvWaBooth.BargainInfo bargainInfo : bargainInfos) {
            GameNvWaMarketBargain nvWaMarketBargain = nvWaMarketService.getNvWaMarketBargain(bargainInfo.getBargainId());
            nvWaMarketBargain.updateBargainStatus();
            bargains.add(nvWaMarketBargain);
        }
        if (!bargains.isEmpty()) {
            gameDataService.updateGameDatas(bargains);
        }
        if (!bargainInfos.isEmpty()) {
            gameDataService.updateGameData(nvWaMarket);
        }
        return getRDBooth(nvWaMarket);
    }

    /**
     * 处理过期摊位
     *
     * @param nvWaMarket
     */
    public void handlingExpiredBooth(GameNvWaBooth nvWaMarket) {
        //编号信息清空
        gameNvWaMarketNumService.emptyBoothNo(nvWaMarket.getBoothNo());
        //剩余货品回退
        List<GameNvWaBooth.ProductInfo> productInfos = nvWaMarket.getProductInfos()
                .stream().filter(p -> p.getGoods().getNum() != 0).collect(Collectors.toList());
        if (!productInfos.isEmpty()) {
            //加入待返还列表
            List<GoodsInfo> goodsInfos = productInfos.stream().map(GameNvWaBooth.ProductInfo::getGoods).collect(Collectors.toList());
            List<GoodsInfo> goods = goodsInfos.stream().filter(g -> g.getNum() != 0).collect(Collectors.toList());
            if (goods.isEmpty()) {
                return;
            }
            List<Award> returnAward = GoodsInfo.getAwards(goods, AwardEnum.FB);
            GiveBackAwards giveBackAwards = GiveBackAwards.instance(nvWaMarket.getUid(), productInfos.get(0).getProductId(), returnAward, "女娲集市", "摊位过期道具返还");
            giveBackPool.toGiveBackPool(giveBackAwards);
        }
        //处理还价信息
        List<GameNvWaBooth.BargainInfo> bargainInfos = nvWaMarket.getBargainInfos();
        if (bargainInfos.isEmpty()) {
            return;
        }
        List<GameNvWaMarketBargain> untreatedBargains = new ArrayList<>();
        for (GameNvWaBooth.BargainInfo bargainInfo : bargainInfos) {
            GameNvWaMarketBargain nvWaMarketBargain = nvWaMarketService.getNvWaMarketBargain(bargainInfo.getBargainId());
            if (nvWaMarketBargain.isDealWith()) {
                continue;
            }
            untreatedBargains.add(nvWaMarketBargain);
        }
        List<GiveBackAwards> list;
        if (!untreatedBargains.isEmpty()) {
            //加入待返还列表
            list = nvWaMarketService.addPendOperate(untreatedBargains);
            giveBackPool.toGiveBackPool(list);
        }
        //删除对应还价信息
        List<Long> bargainIds = bargainInfos.stream().map(GameNvWaBooth.BargainInfo::getBargainId).collect(Collectors.toList());
        gameDataService.deleteGameDatas(bargainIds, GameNvWaMarketBargain.class);
    }

    /**
     * 获得交易记录
     *
     * @param uid
     * @return
     */
    public RDTradeRecordInfo getTradeRecord(long uid) {
        List<UserNvWaTradeRecord> nvWaMarket = userNvWaMarketService.getUserNvWaMarketTaredRecord(uid);
        if (nvWaMarket.isEmpty()) {
            return new RDTradeRecordInfo();
        }
        RDTradeRecordInfo rd = new RDTradeRecordInfo();
        List<RDTradeRecord> rdTradeRecords = new ArrayList<>();
        for (UserNvWaTradeRecord tradeRecord : nvWaMarket) {
            rdTradeRecords.add(RDTradeRecord.getInstance(tradeRecord));
        }
        List<RDTradeRecord> tradeRecords = rdTradeRecords.stream()
                .sorted(Comparator.comparing(RDTradeRecord::getTradeDate).reversed()).collect(Collectors.toList());
        rd.setRdTradeRecords(tradeRecords);
        return rd;
    }

    /**
     * 租赁摊位
     *
     * @param uid
     * @return
     */
    public RDCommon rentalBooth(long uid) {
        //检查是否已经有摊位
        gameNvWaMarketNumService.checkBooth(uid);

        Integer rentalPrice = NightmareNvWamCfgTool.getRentalPrice();
        //扣除铜币
        RDCommon rd = new RDCommon();
//        honorCurrencyService.honorCurrencyDeductConvert(uid, TreasureEnum.HONOR_COPPER_COIN.getValue(), rentalPrice, rd);
        TreasureChecker.checkIsEnough(TreasureEnum.HONOR_COPPER_COIN.getValue(), rentalPrice, uid);
        TreasureEventPublisher.pubTDeductEvent(uid, TreasureEnum.HONOR_COPPER_COIN.getValue(), rentalPrice, WayEnum.NV_W_MARKET, rd);

        Integer leaseTimeLimit = NightmareNvWamCfgTool.getLeaseTimeLimit();
        Date expiredDate = DateUtil.addHours(DateUtil.now(), leaseTimeLimit);
        long boothId = ID.INSTANCE.nextId();
        Integer boothNo = nvWaMarketRedisLockService.rentalBoothLock(uid, expiredDate, boothId);
        GameNvWaBooth gameNvWaMarket = GameNvWaBooth.getInstance(boothId, boothNo, expiredDate, uid);

        gameDataService.addGameData(gameNvWaMarket);
        return rd;
    }

    /**
     * 更新摊位状态
     *
     * @param uid
     * @return
     */
    public RDSuccess updateBoothStatus(long uid) {
        GameNvWaBooth booth = nvWaMarketService.getUserBooth(uid);
        //检查是否过期
        if (booth.isExpired()) {
            throw new ExceptionForClientTip("nightmareNvWaM.nvWaMarket.booth.expired");
        }
        //更新状态
        booth.updateBoothStatus();
        gameDataService.updateGameData(booth);
        return new RDSuccess();
    }


    /**
     * 下架商品
     *
     * @param uid
     * @param productId
     */
    public RDCommon takeDown(long uid, long productId) {
        GameNvWaBooth booth = nvWaMarketService.getUserBooth(uid);
        //检查摊位状态
        Integer boothStatus = booth.getBoothStatus();
        if (boothStatus != BoothStatusEnum.CLOSE_BOOTH.getValue()) {
            throw new ExceptionForClientTip("nightmareNvWaM.nvWaMarket.booth.price.limit");
        }
        //回退商品
        GameNvWaBooth.ProductInfo productInfo = booth.getProductInfos()
                .stream().filter(p -> productId == p.getProductId()).findFirst().orElse(null);
        RDCommon rd = new RDCommon();
        if (null == productInfo) {
            return rd;
        }
        GoodsInfo goods = productInfo.getGoods();
        TreasureEventPublisher.pubTAddEvent(uid, goods.getId(), goods.getNum(), WayEnum.PRODUCT_RETURN, rd);
        //下架商品
        booth.delProductInfo(productId);
        gameDataService.updateGameData(booth);
        //更新要价状态
        List<GameNvWaMarketBargain> nvWaMarketBargains = nvWaMarketService.getProductBargain(booth.getBoothNo(), productId)
                .stream().filter(n -> !n.isDealWith()).collect(Collectors.toList());
        if (!nvWaMarketBargains.isEmpty()) {
            nvWaMarketBargains.forEach(n -> dealWithRefusePrice(n, n.getId(), booth.getBoothNo()));
            gameDataService.updateGameDatas(nvWaMarketBargains);
        }
        return rd;
    }

    /**
     * 上架商品
     *
     * @param uid
     * @param productInfo productId_2_4:goodId1_1,goodId2_2;goodId3_1
     * @return
     */
    public RDCommon listings(long uid, String productInfo) {
        GameNvWaBooth gameNvWaMarket = nvWaMarketService.getUserBooth(uid);
        Integer boothStatus = gameNvWaMarket.getBoothStatus();
        if (boothStatus != BoothStatusEnum.CLOSE_BOOTH.getValue()) {
            throw new ExceptionForClientTip("nightmareNvWaM.nvWaMarket.booth.price.limit");
        }
        //检查商品数量
        Product product = Product.getInstance(productInfo);
        Integer productId = product.getGoodsId();
        nvWaMarketService.checkNvWaMarketGoods(productId);
        Integer productNum = product.getNum();
        Integer minSellNum = product.getMinSellNum();
        TreasureChecker.checkIsEnough(productId, productNum, uid);
        //检查出价数量
        if (isPriceWayLimit(productInfo)) {
            throw new ExceptionForClientTip("nightmareNvWaM.nvWaMarket.booth.price.limit");
        }
        List<GameNvWaBooth.ProductPrice> productPrices = parseProductPrice(productInfo.split(":")[1]);
        GameNvWaBooth.ProductInfo addProductInfo = gameNvWaMarket.addProductInfo(productId, productNum, minSellNum, productPrices);
        //扣除道具
        RDProuduct rd = new RDProuduct();
        rd.setProductId(addProductInfo.getProductId());
        TreasureEventPublisher.pubTDeductEvent(uid, productId, productNum, WayEnum.NV_W_MARKET, rd);
        //更新数据
        gameDataService.updateGameData(gameNvWaMarket);
        return rd;
    }

    /**
     * 设置标语
     *
     * @param uid
     * @param slogan
     * @return
     */
    public RDSuccess setBoothSlogan(long uid, String slogan) {
        GameNvWaBooth gameNvWaMarket = nvWaMarketService.getUserBooth(uid);
        Integer boothStatus = gameNvWaMarket.getBoothStatus();
        if (boothStatus != BoothStatusEnum.CLOSE_BOOTH.getValue()) {
            throw new ExceptionForClientTip("nightmareNvWaM.nvWaMarket.booth.price.limit");
        }
        gameNvWaMarket.setSlogan(slogan);
        gameDataService.updateGameData(gameNvWaMarket);
        return new RDSuccess();
    }

    /**
     * 更改商品
     *
     * @param uid
     * @param productId
     * @param productInfo
     * @return
     */
    public RDCommon modifyProduct(long uid, long productId, String productInfo) {
        GameNvWaBooth booth = nvWaMarketService.getUserBooth(uid);
        Integer boothStatus = booth.getBoothStatus();
        if (boothStatus != BoothStatusEnum.CLOSE_BOOTH.getValue()) {
            throw new ExceptionForClientTip("nightmareNvWaM.nvWaMarket.booth.price.limit");
        }
        RDCommon rd = new RDCommon();
        //回退商品
        GameNvWaBooth.ProductInfo product = booth.getProductInfos()
                .stream().filter(p -> productId == p.getProductId()).findFirst().orElse(null);
        if (null != product) {
            GoodsInfo goods = product.getGoods();
            TreasureEventPublisher.pubTAddEvent(uid, goods.getId(), goods.getNum(), WayEnum.PRODUCT_RETURN, rd);
        }
        //检查商品
        Product pro = Product.getProduct(productInfo);
        booth.updateProduct(productId, pro);
        Integer goodsId = pro.getGoodsId();
        nvWaMarketService.checkNvWaMarketGoods(goodsId);
        Integer num = pro.getNum();
        TreasureChecker.checkIsEnough(goodsId, num, uid);
        //扣除商品
        TreasureEventPublisher.pubTDeductEvent(uid, goodsId, num, WayEnum.NV_W_MARKET, rd);

        //更新要价状态
        List<GameNvWaMarketBargain> nvWaMarketBargains = nvWaMarketService.getProductBargain(booth.getBoothNo(), productId)
                .stream().filter(n -> !n.isDealWith()).collect(Collectors.toList());
        if (!nvWaMarketBargains.isEmpty()) {
            nvWaMarketBargains.forEach(n -> dealWithRefusePrice(n, n.getId(), booth.getBoothNo()));
            gameDataService.updateGameDatas(nvWaMarketBargains);
        }
        gameDataService.updateGameData(booth);
        return rd;
    }


    /**
     * 更新出价
     *
     * @param uid
     * @param productId
     * @param bargainInfo
     * @return
     */
    public RDSuccess modifyBargain(long uid, long productId, String bargainInfo) {
        GameNvWaBooth booth = nvWaMarketService.getUserBooth(uid);
        Integer boothStatus = booth.getBoothStatus();
        if (boothStatus != BoothStatusEnum.CLOSE_BOOTH.getValue()) {
            throw new ExceptionForClientTip("nightmareNvWaM.nvWaMarket.booth.price.limit");
        }
        //更新出价
        List<GameNvWaBooth.ProductPrice> productPrices = parseProductPrice(bargainInfo);
        booth.updatePrice(productId, productPrices);
        gameDataService.updateGameData(booth);
        return new RDSuccess();
    }

    /**
     * 解析出价
     *
     * @param bargainInfo
     * @return
     */
    private List<GameNvWaBooth.ProductPrice> parseProductPrice(String bargainInfo) {
        List<GameNvWaBooth.ProductPrice> productPrices = new ArrayList<>();
        String[] priceWays = bargainInfo.split(";");
        for (int i = 0; i < priceWays.length; i++) {
            String priceInfo = priceWays[i];
            String[] price = priceInfo.split(",");
            GameNvWaBooth.ProductPrice productPrice = new GameNvWaBooth.ProductPrice();
            productPrice.setTradeStatus(TradeStatusEnum.NO_TRADE.getValue());
            for (String priceNums : price) {
                int priceId = Integer.parseInt(priceNums.split("_")[0]);
                nvWaMarketService.checkNvWaMarketGoods(priceId);
            }
            productPrice.setPrice(priceInfo);
            productPrices.add(productPrice);
        }
        return productPrices;
    }

    /**
     * 是否超出出价上限
     *
     * @param productInfo
     * @return
     */
    private boolean isPriceWayLimit(String productInfo) {
        //最多要价数量
        Integer maxPriceWay = NightmareNvWamCfgTool.getMaxPriceWay();
        //检查出价数量
        String[] priceWay = productInfo.split(":")[1].split(";");
        return priceWay.length > maxPriceWay;
    }

    /**
     * 续租
     *
     * @param uid
     */
    public RDCommon leaseRenewal(long uid) {
        GameNvWaBooth booth = nvWaMarketService.getUserBooth(uid);
        //检查荣耀铜币
        Integer rentalPrice = NightmareNvWamCfgTool.getRentalPrice();
        TreasureChecker.checkIsEnough(TreasureEnum.HONOR_COPPER_COIN.getValue(), rentalPrice, uid);
        //扣除铜币
        RDCommon rd = new RDCommon();
//        honorCurrencyService.honorCurrencyDeductConvert(uid, TreasureEnum.HONOR_COPPER_COIN.getValue(), rentalPrice, rd);
        TreasureEventPublisher.pubTDeductEvent(uid, TreasureEnum.HONOR_COPPER_COIN.getValue(), rentalPrice, WayEnum.NV_W_MARKET, rd);
        //更新时间
        Integer leaseTimeLimit = NightmareNvWamCfgTool.getLeaseTimeLimit();
        Date expiredDate = DateUtil.addHours(booth.getLeaseEndTime(), leaseTimeLimit);
        long newDate = DateUtil.toDateTimeLong(expiredDate);
        gameNvWaMarketNumService.updateBoothNo(booth.getBoothNo(), uid, newDate, booth.getId());
        booth.setLeaseEndTime(expiredDate);
        gameDataService.updateGameData(booth);
        return rd;
    }

    /**
     * 交易
     *
     * @param uid
     * @param boothNo
     * @param productNo
     * @param priceNo
     * @return
     */
    public RDCommon trade(long uid, int boothNo, long productNo, int priceNo, String oldProductInfo) {
        GameNvWaBooth booth = nvWaMarketService.getBoothByNo(boothNo);
        Long boothId = booth.getId();
        if (uid == boothId) {
            throw new ExceptionForClientTip("nightmareNvWaM.nvWaMarket.booth.not.buy");
        }
        if (booth.getBoothStatus() != BoothStatusEnum.OPEN_BOOTH.getValue()) {
            throw new ExceptionForClientTip("nightmareNvWaM.nvWaMarket.booth.not.business");
        }
        //检查资源
        GameNvWaBooth.ProductInfo productInfo = booth.getProductInfos().stream().filter(p -> productNo == p.getProductId()).findFirst().orElse(null);
        GameNvWaBooth.ProductPrice productPrice = productInfo.getProductPrices().get(priceNo);
        if (null == productPrice || productPrice.getTradeStatus() == TradeStatusEnum.ALREADY_TRADED.getValue()) {
            throw new ExceptionForClientTip("nightmareNvWaM.nvWaMarket.booth.invalid.price");
        }
        //检查商品一致性
        nvWaMarketService.checkProductInfoConsistent(priceNo, productInfo, oldProductInfo);
        //检查商品是否足够
        boolean enoughProductNum = nvWaMarketRedisLockService.tradeGoodsCheckLock(uid, productNo, booth);
        if (!enoughProductNum) {
            throw new ExceptionForClientTip("nightmareNvWaM.nvWaMarket.booth.product.not.enough");
        }

        String price = productPrice.getPrice();
        List<GoodsInfo> goodsPrice = GoodsInfo.getGoods(price);
        for (GoodsInfo goods : goodsPrice) {
            Integer awardId = goods.getId();
            int num = goods.getNum();
            TreasureChecker.checkIsEnough(awardId, num, uid);
        }
        nvWaMarketRedisLockService.tradeGoodsDelLock(uid, productNo, booth, priceNo);
        gameDataService.updateGameData(booth);
        RDCommon rd = new RDCommon();
        //扣除资源
        for (GoodsInfo goods : goodsPrice) {
            TreasureEventPublisher.pubTDeductEvent(uid, goods.getId(), goods.getNum(), WayEnum.NV_W_MARKET, rd);
        }
        GoodsInfo goods = productInfo.getGoods();
        goods.setNum(productInfo.getMinSellNum());
        //发放资源
        TreasureEventPublisher.pubTAddEvent(uid, goods.getId(), productInfo.getMinSellNum(), WayEnum.NV_W_MARKET, rd);
        //邮件发放出价
        Long userId = booth.getUid();
        String title = LM.I.getMsgByUid(userId, "nightmareNvWaM.nvWaMarket.mail.title");
        CfgTreasureEntity treasureById = TreasureTool.getTreasureById(goods.getId());
        String content = LM.I.getMsgByUid(userId, "nightmareNvWaM.nvWaMarket.mail.message", treasureById.getName());
        UserMail userMail = UserMail.newAwardMail(title, content, userId, GoodsInfo.getAwards(goodsPrice, AwardEnum.FB));
        gameUserService.addItem(userId, userMail);

        //添加交易记录
        addTradRecord(uid, TradeTypeEnum.BUY, userId, goods, goodsPrice);
        return rd;
    }


    /**
     * 还价
     *
     * @param uid
     * @param boothNo
     * @param productNo
     * @param bargain   id_1,id2_2
     * @param message
     * @return
     */
    public RDCommon bargain(long uid, int boothNo, long productNo, String bargain, String message, String oldProductInfo) {
        GameNvWaBooth booth = nvWaMarketService.getBoothByNo(boothNo);
        //检查还价数量
        int bargainSize = Math.toIntExact(nvWaMarketService.getBoothNvWaMarketBargain(boothNo).stream().filter(b -> !b.isDealWith()).count());
        Integer maxBargainNum = NightmareNvWamCfgTool.getMaxBargainNum();
        if (bargainSize >= maxBargainNum) {
            throw new ExceptionForClientTip("nightmareNvWaM.nvWaMarket.booth.bargain.limit");
        }
        GameNvWaBooth.ProductInfo boothProduct = booth.getProduct(productNo);
        //检查商品一致性
        nvWaMarketService.checkProductInfoConsistent(null, boothProduct, oldProductInfo);
        //检查讨价
        String[] bargainInfos = bargain.split(",");
        List<GoodsInfo> goods = new ArrayList<>();
        for (String bar : bargainInfos) {
            int treasureId = Integer.parseInt(bar.split("_")[0]);
            nvWaMarketService.checkNvWaMarketGoods(treasureId);
            int treasureNum = Integer.parseInt(bar.split("_")[1]);
            TreasureChecker.checkIsEnough(treasureId, treasureNum, uid);
            goods.add(new GoodsInfo(treasureId, treasureNum));
        }
        //添加讨价信息
        Integer bargainLimit = NightmareNvWamCfgTool.getNightmareNvm().getBargainLimit();
        Date expireTime = DateUtil.addHours(DateUtil.now(), bargainLimit);
        long timeMillis = System.currentTimeMillis();
        booth.addBargainInfo(timeMillis, expireTime);
        GoodsInfo product = boothProduct.getGoods();
        if (null == product) {
            throw new ExceptionForClientTip("nightmareNvWaM.nvWaMarket.booth.product.not.enough");
        }
        gameDataService.updateGameData(booth);
        product.setNum(boothProduct.getMinSellNum());
        nvWaMarketService.addNvWaMarketBargain(timeMillis, uid, boothNo, productNo, product, expireTime, goods, message);
        //扣除资源
        RDCommon rd = new RDCommon();
        for (Award treasure : GoodsInfo.getAwards(goods, AwardEnum.FB)) {
            TreasureEventPublisher.pubTDeductEvent(uid, treasure.getAwardId(), treasure.getNum(), WayEnum.NV_W_MARKET_SUSPENDED, rd);
        }
        return rd;
    }

    /**
     * 同意还价
     *
     * @param uid
     * @param bargainId
     * @return
     */
    public RDCommon agreePrice(long uid, long bargainId) {
        GameNvWaBooth booth = nvWaMarketService.getUserBooth(uid);
        GameNvWaBooth.BargainInfo bargainInfo = booth.getBargainInfos()
                .stream().filter(b -> bargainId == b.getBargainId()).findFirst().orElse(null);
        if (null == bargainInfo) {
            throw new ExceptionForClientTip("nightmareNvWaM.nvWaMarket.booth.invalid.price");
        }
        //检查要价是否过期
        if (nvWaMarketService.isExpired(bargainInfo.getExpireTime())) {
            throw new ExceptionForClientTip("nightmareNvWaM.nvWaMarket.booth.bargain.expired");
        }
        //检查商品是否足够
        GameNvWaMarketBargain nvWaMarketBargain = nvWaMarketService.getNvWaMarketBargain(bargainId);
        //检查是否处理
        nvWaMarketRedisLockService.bargainDealWithLock(bargainId, nvWaMarketBargain);

        long productNo = nvWaMarketBargain.getProductId();
        boolean enoughProductNum = booth.isEnoughProductNum(productNo);
        if (!enoughProductNum) {
            throw new ExceptionForClientTip("nightmareNvWaM.nvWaMarket.booth.product.not.enough");
        }
        //扣除商品
        booth.delProduct(productNo);
        gameDataService.updateGameData(booth);
        //获得还价道具
        RDCommon rd = new RDCommon();
        List<GoodsInfo> price = nvWaMarketBargain.getPrice();
        List<Award> bargain = GoodsInfo.getAwards(price, AwardEnum.FB);
        awardService.fetchAward(uid, bargain, WayEnum.NV_W_MARKET, "", rd);
        //邮件发送商品
        GoodsInfo goods = nvWaMarketBargain.getProduct();
        long sponsor = nvWaMarketBargain.getSponsor();
        String title = LM.I.getMsgByUid(sponsor, "nightmareNvWaM.nvWaMarket.mail.title");
        String treasureName = TreasureTool.getTreasureById(goods.getId()).getName();
        String content = LM.I.getMsgByUid(sponsor, "nightmareNvWaM.nvWaMarket.mail.trade.message", booth.getBoothNo(), treasureName);
        List<Award> product = new ArrayList<>();
        product.add(new Award(goods.getId(), AwardEnum.FB, goods.getNum()));
        UserMail userMail = UserMail.newAwardMail(title, content, sponsor, product);
        gameUserService.addItem(sponsor, userMail);
        //更新要价状态
        nvWaMarketBargain.updateBargainStatus(BargainProductEnum.AGREE.getValue());
        gameDataService.updateGameData(nvWaMarketBargain);

        //添加交易记录
        addTradRecord(uid, TradeTypeEnum.SELL, sponsor, goods, price);
        return rd;
    }

    /**
     * 拒绝要价
     *
     * @param uid
     * @param bargainId
     * @return
     */
    public RDSuccess refusePrice(long uid, long bargainId) {
        GameNvWaBooth booth = nvWaMarketService.getUserBooth(uid);
        GameNvWaBooth.BargainInfo bargainInfo = booth.getBargainInfos()
                .stream().filter(b -> bargainId == b.getBargainId()).findFirst().orElse(null);
        if (null == bargainInfo) {
            throw new ExceptionForClientTip("nightmareNvWaM.nvWaMarket.booth.invalid.price");
        }
        //邮件返还商品
        GameNvWaMarketBargain nvWaMarketBargain = nvWaMarketService.getNvWaMarketBargain(bargainId);
        dealWithRefusePrice(nvWaMarketBargain, bargainId, booth.getBoothNo());
        gameDataService.updateGameData(booth);
        gameDataService.updateGameData(nvWaMarketBargain);
        return new RDSuccess();
    }

    /**
     * 处理拒绝要价
     *
     * @param nvWaMarketBargain
     * @param bargainId
     * @param boothNo
     */
    private void dealWithRefusePrice(GameNvWaMarketBargain nvWaMarketBargain, long bargainId, int boothNo){
        //检查是否处理
        nvWaMarketRedisLockService.bargainDealWithLock(bargainId, nvWaMarketBargain);
        long sponsor = nvWaMarketBargain.getSponsor();
        GoodsInfo product = nvWaMarketBargain.getProduct();
        String treasureName = TreasureTool.getTreasureById(product.getId()).getName();
        String title = LM.I.getMsgByUid(sponsor, "nightmareNvWaM.nvWaMarket.mail.title");
        String content = LM.I.getMsgByUid(sponsor, "nightmareNvWaM.nvWaMarket.mail.trade.default.message", boothNo, treasureName);
        List<GoodsInfo> price = nvWaMarketBargain.getPrice();
        List<Award> bargain = GoodsInfo.getAwards(price, AwardEnum.FB);
        UserMail userMail = UserMail.newAwardMail(title, content, sponsor, bargain);
        gameUserService.addItem(sponsor, userMail);
        //更新要价状态
        nvWaMarketBargain.updateBargainStatus(BargainProductEnum.REFUSE.getValue());
    }

    /**
     * 获得模板
     *
     * @param uid
     * @return
     */
    public RDNvWaPriceModel getPriceModel(long uid) {
        nvWaMarketService.getUserBooth(uid);
        //获取模板
        UserNvWaPriceModel userNvWaPriceModel = userNvWaMarketService.getOrCreatUserNvWaPriceModel(uid);
        return RDNvWaPriceModel.getInstance(userNvWaPriceModel);
    }

    /**
     * 设置模板
     *
     * @param uid
     * @param priceModel
     * @return
     */
    public RDSuccess setPriceModel(long uid, String priceModel) {
        if ("".equals(priceModel)) {
            return new RDSuccess();
        }
        nvWaMarketService.getUserBooth(uid);
        //获取模板
        UserNvWaPriceModel userNvWaPriceModel = userNvWaMarketService.getOrCreatUserNvWaPriceModel(uid);
        userNvWaPriceModel.updatePriceModel(priceModel);
        gameUserService.updateItem(userNvWaPriceModel);
        return new RDSuccess();
    }

    /**
     * 添加交易记录
     *
     * @param uid
     * @param tradeType
     * @param counterparty
     * @param product
     * @param prices
     */
    public void addTradRecord(long uid, TradeTypeEnum tradeType, long counterparty, GoodsInfo product, List<GoodsInfo> prices) {
        //交易记录添加
        GameUser gameCounterparty = gameUserService.getGameUser(counterparty);
        GameUser.RoleInfo counterpartyRoleInfo = gameCounterparty.getRoleInfo();
        String counterpartyInfo = ServerTool.getServerShortName(gameCounterparty.getServerId()) + "·" + counterpartyRoleInfo.getNickname();
        UserNvWaTradeRecord nvWaTradeRecord = UserNvWaTradeRecord.getInstance(uid, tradeType.getValue(), counterpartyInfo, product, prices);
        nvWaMarketService.tradeRecordLimitProcess(uid);
        gameUserService.addItem(uid, nvWaTradeRecord);

        //对方交易记录添加
        GameUser gameUser = gameUserService.getGameUser(uid);
        TradeTypeEnum type = TradeTypeEnum.getAnotherValue(tradeType.getValue());
        GameUser.RoleInfo roleInfo = gameUser.getRoleInfo();
        String userNiceName = ServerTool.getServerShortName(gameUser.getServerId()) + "·" + roleInfo.getNickname();
        UserNvWaTradeRecord userNvWaTradeRecord = UserNvWaTradeRecord.getInstance(counterparty, type.getValue(), userNiceName, product, prices);
        nvWaMarketService.tradeRecordLimitProcess(counterparty);
        gameUserService.addItem(counterparty, userNvWaTradeRecord);
    }

    /**
     * 讨价还价列表
     *
     * @param uid
     * @return
     */
    public RDBargainInfos getBargainList(long uid, int type) {
        Long userBoothId = gameNvWaMarketNumService.getUserUnexpiredBoothId(uid);
        GameNvWaBooth booth = null;
        if (null != userBoothId) {
            booth = nvWaMarketService.getBooth(userBoothId);
        }
        RDBargainInfos rd = new RDBargainInfos();
        BargainTypeEnum bargainType = BargainTypeEnum.fromValue(type);
        if (null == bargainType) {
            return rd;
        }
        //我的还价
        if (null != booth && bargainType == BargainTypeEnum.COUNTER_OFFER) {
            List<GameNvWaBooth.BargainInfo> bargainInfos = booth.getBargainInfos();
            for (GameNvWaBooth.BargainInfo bargainInfo : bargainInfos) {
                GameNvWaMarketBargain nvWaMarketBargain = nvWaMarketService.getNvWaMarketBargain(bargainInfo.getBargainId());
                GoodsInfo boothProduct = nvWaMarketBargain.getProduct();
                if (null == boothProduct) {
                    continue;
                }
                RDBargainInfos.RDBargain counterOffer = RDBargainInfos.getInstance(nvWaMarketBargain, boothProduct, null);
                rd.getBargain().add(counterOffer);
            }
            List<RDBargainInfos.RDBargain> bargains = rd.getBargain().stream()
                    .sorted(Comparator.comparing(RDBargainInfos.RDBargain::getExpireTime).reversed()
                            .thenComparing(RDBargainInfos.RDBargain::getStatus).reversed()).collect(Collectors.toList());
            rd.setBargain(bargains);
        }
        //我的讨价
        if (bargainType == BargainTypeEnum.BARGAIN) {
            List<GameNvWaMarketBargain> userNvWaMarketBargain = nvWaMarketService.getUserNvWaMarketBargain(uid);
            for (GameNvWaMarketBargain nvWaMarketBargain : userNvWaMarketBargain) {
                Long boothId = gameNvWaMarketNumService.getBoothId(nvWaMarketBargain.getBoothNo());
                if (null == boothId) {
                    continue;
                }
                GameNvWaBooth nvWaBooth = nvWaMarketService.getBooth(boothId);
                if (null == nvWaBooth) {
                    continue;
                }
                GoodsInfo boothProduct = nvWaMarketBargain.getProduct();
                if (null == boothProduct) {
                    continue;
                }
                RDBargainInfos.RDBargain bargain = RDBargainInfos.getInstance(nvWaMarketBargain, boothProduct, nvWaBooth);
                rd.getCounterOffer().add(bargain);
            }
            List<RDBargainInfos.RDBargain> counterOffer = rd.getCounterOffer().stream()
                    .sorted(Comparator.comparing(RDBargainInfos.RDBargain::getExpireTime).reversed()
                            .thenComparing(RDBargainInfos.RDBargain::getStatus).reversed()).collect(Collectors.toList());
            rd.setCounterOffer(counterOffer);
        }
        return rd;
    }

    /**
     * 撤销还价
     *
     * @param uid
     * @param bargainId
     */
    public RDCommon revokePrice(long uid, long bargainId) {
        GameNvWaMarketBargain nvWaMarketBargain = nvWaMarketService.getNvWaMarketBargain(bargainId);
        if (null == nvWaMarketBargain) {
            throw new ExceptionForClientTip("nightmareNvWaM.nvWaMarket.booth.invalid.price");
        }
        //检查是否本人讨价信息
        long sponsor = nvWaMarketBargain.getSponsor();
        if (sponsor != uid) {
            throw new ExceptionForClientTip("nightmareNvWaM.nvWaMarket.booth.not.user.price");
        }
        //检查是否过期
        if (nvWaMarketBargain.isExpired()) {
            throw new ExceptionForClientTip("nightmareNvWaM.nvWaMarket.booth.bargain.expired");
        }
        //检查是否被处理
        nvWaMarketRedisLockService.bargainDealWithLock(bargainId, nvWaMarketBargain);

        //更新要价状态
        nvWaMarketBargain.updateBargainStatus(BargainProductEnum.REVOKE.getValue());

        //回退商品
        List<GoodsInfo> price = nvWaMarketBargain.getPrice();
        List<Award> bargain = GoodsInfo.getAwards(price, AwardEnum.FB);
        RDCommon rd = new RDCommon();
        awardService.fetchAward(uid, bargain, WayEnum.PRODUCT_RETURN, "", rd);

        gameDataService.updateGameData(nvWaMarketBargain);
        return rd;
    }

}
