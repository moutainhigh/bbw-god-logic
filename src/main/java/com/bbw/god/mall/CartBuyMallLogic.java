package com.bbw.god.mall;

import com.bbw.common.DateUtil;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.ConsumeType;
import com.bbw.god.detail.async.MallDetailAsyncHandler;
import com.bbw.god.detail.async.MallDetailEventParam;
import com.bbw.god.event.BaseEventParam;
import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.mall.CfgMallEntity;
import com.bbw.god.game.config.mall.MallEnum;
import com.bbw.god.game.config.mall.MallTool;
import com.bbw.god.game.config.treasure.TreasureTool;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.res.ResChecker;
import com.bbw.god.gameuser.res.ResEventPublisher;
import com.bbw.god.mall.event.MallEventPublisher;
import com.bbw.god.mall.processor.AbstractMallProcessor;
import com.bbw.god.mall.processor.MallProcessorFactory;
import com.bbw.god.rd.RDCommon;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 购物车业务处理类
 * @author longwh
 * @date 2022/9/26 10:18
 */
@Service
public class CartBuyMallLogic {

    @Autowired
    private MallProcessorFactory mallProcessorFactory;
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private MallService mallService;
    @Autowired
    private MallDetailAsyncHandler mallDetailAsyncHandler;

    /**
     * 购物车结账
     * @param guId 用户id
     * @param malls 购物车结算请求信息
     * @return
     */
    public RDCommon cartBuy(long guId, String malls) {
        // 参数解析
        CartBuyInfo cartBuyInfo = parseCartBuyParam(malls);
        // 获取购买总量
        int buyNum = cartBuyInfo.getBuyMallInfos().stream().mapToInt(CartBuyInfo.BuyMallInfo::getBuyNum).sum();
        // 校验购买总量
        if (buyNum <= 0 || buyNum > MallTool.getMallConfig().getMaxBuyNum()) {
            throw new ExceptionForClientTip("mall.not.valid.num");
        }
        // 获取用户信息
        GameUser gu = gameUserService.getGameUser(guId);
        // 统一校验体力商品
        cartBuyInfo.getBuyMallInfos().forEach(mallInfo -> {
            if (mallInfo.getMall().getItem() == AwardEnum.TL.getValue()){
                // 计算购买体力
                int buyDice = mallInfo.getMall().getNum() * mallInfo.getBuyNum();
                // 玉麒麟体力值
                Integer yqlDice = TreasureTool.getTreasureConfig().getTreasureEffectYQL();
                // 判断是否已到达体力上限，或者此次购买体力视为使用玉麒麟后 是否 超过体力上限
                if (gu.ifMaxDice() || gu.ifMaxDice(buyDice/yqlDice)){
                    throw new ExceptionForClientTip("gu.dice.outOfLimit");
                }
            }
        });

        // 获取所有神秘商店的商品
        List<UserMallRecord> smRecords = mallService.getRecords(guId).stream().filter(umr ->
                umr.getType() == MallEnum.SM.getValue()).collect(Collectors.toList());
        // 读取今天类型为神秘的UserMallRecord
        List<UserMallRecord> finalSmRecords = smRecords.stream()
                .filter(record -> DateUtil.isToday(record.getDateTime()))
                .sorted(Comparator.comparingLong(UserMallRecord::getId).reversed())
                .limit(MallTool.getMallConfig().getMyteriousNum())
                .collect(Collectors.toList());

        //创建 响应
        RDCommon rd = new RDCommon();

        // 校验购买记录
        checkUserMallRecord(gu, cartBuyInfo, finalSmRecords);
        // 统一检查权限
        //mallEntityList.forEach(mall -> mallProcessorFactory.getMallProcessor(mall.getType()).checkAuth(guId, mall));
        // 统计校验用户余额
        cartBuyCheckUserRes(gu, cartBuyInfo, finalSmRecords);
        // 统一校验限购情况
        cartBuyCheckNumLimit(guId, cartBuyInfo, finalSmRecords);

        // 获取该商品 处理器 processor
        AbstractMallProcessor mallProcessor = mallProcessorFactory.getMallProcessor(
                cartBuyInfo.getBuyMallInfos().get(0).getMall().getType());

        // 遍历购车商品，结算
        for (CartBuyInfo.BuyMallInfo buyMallInfo : cartBuyInfo.getBuyMallInfos()) {
            // 获取该商品的cfgMallEntity
            CfgMallEntity mall = buyMallInfo.getMall();
            // 获取该商品 购买记录
            UserMallRecord userMallRecord = finalSmRecords.stream().filter(r ->
                    r.getId().equals(buyMallInfo.getRecordId())).findFirst().orElse(null);

            // 扣除资源
            cartBuyMallHandle(gu, mall, buyMallInfo.getBuyNum(), rd);
            // 发放
            mallProcessor.deliver(guId, mall, buyMallInfo.getBuyNum(), rd);
            // 限购的处理购买纪录
            if (mall.getLimit() != 0) {
                if (userMallRecord == null) {
                    throw new ExceptionForClientTip("cart_mall.overdue");
                } else {
                    userMallRecord.addNum(buyMallInfo.getBuyNum());
                    gameUserService.updateItem(userMallRecord);
                }
            }
            BaseEventParam bep = new BaseEventParam(guId, WayEnum.MALL_BUY, rd);
            MallEventPublisher.pubMallbuySendEvent(mall.getGoodsId(), mall.getType(), buyMallInfo.getBuyNum(), bep);
        }

        return rd;
    }

    /**
     * 解析购物车结算 请求参数
     * @param param 商品信息
     * @return
     */
    private CartBuyInfo parseCartBuyParam(String param){
        // 商品列表分割符
        String splitMall = ";";
        // 购买信息分割符
        String splitMallInfo = ",";
        // 参数校验
        if (StringUtils.isEmpty(param) || !StringUtils.contains(param, splitMallInfo)) {
            throw new ExceptionForClientTip("request.param.not.valid");
        }
        // 解析结算请求信息 (结构) 商品id:数量#商品id:数量
        // 参数分割 获取商品信息数组
        String[] malls = param.split(splitMall);
        // 创建购物车商品信息列表
        List<CartBuyInfo.BuyMallInfo> buyMallInfos = new ArrayList<>();
        // 遍历商品数组
        for (String mallStr : malls) {
            CartBuyInfo.BuyMallInfo buyMallInfo = new CartBuyInfo.BuyMallInfo();
            // 分割 获取商品信息
            String[] infoArr = mallStr.split(splitMallInfo);
            // 获取记录id
            long recordId = Long.parseLong(infoArr[0]);
            buyMallInfo.setRecordId(recordId);
            // 获取商品id
            int mallId = Integer.parseInt(infoArr[1]);
            // 添加商品 cfgEntity信息
            buyMallInfo.setMall(MallTool.getMall(mallId));
            // 添加购买数量
            int num = Integer.parseInt(infoArr[2]);
            // 参数校验
            if (num <= 0) {
                throw new ExceptionForClientTip("request.param.not.valid");
            }

            buyMallInfo.setBuyNum(num);

            buyMallInfos.add(buyMallInfo);
        }

        // 按照购买记录id执行分组
        Map<Long, List<CartBuyInfo.BuyMallInfo>> idMaps = buyMallInfos.stream()
                .collect(Collectors.groupingBy(CartBuyInfo.BuyMallInfo::getRecordId));
        // 创建分组后的商品购买信息
        List<CartBuyInfo.BuyMallInfo> groupMalls = new ArrayList<>();
        for (Long recordId : idMaps.keySet()) {
            CartBuyInfo.BuyMallInfo countMallInfo = new CartBuyInfo.BuyMallInfo();
            // 统计购买数量
            int buyNum = 0;
            for (CartBuyInfo.BuyMallInfo buyMallInfo : buyMallInfos) {
                if (recordId == buyMallInfo.getRecordId()){
                    // 同一购买记录 统计购买数量
                    buyNum += buyMallInfo.getBuyNum();
                    // 设置商品信息
                    if (countMallInfo.getMall() == null){
                        countMallInfo.setMall(buyMallInfo.getMall());
                    }
                }
            }
            // 设置结果信息
            countMallInfo.setRecordId(recordId);
            countMallInfo.setBuyNum(buyNum);
            groupMalls.add(countMallInfo);
        }
        // 创建商品信息类
        CartBuyInfo cartBuyInfo = new CartBuyInfo();
        cartBuyInfo.setBuyMallInfos(groupMalls);

        return cartBuyInfo;
    }

    /**
     * 购物车结账 统一商品限购校验
     * @param guId
     * @param cartBuyInfo
     * @param records
     */
    private void cartBuyCheckNumLimit(long guId, CartBuyInfo cartBuyInfo, List<UserMallRecord> records){
        for (CartBuyInfo.BuyMallInfo buyMallInfo : cartBuyInfo.getBuyMallInfos()) {
            if (buyMallInfo.getMall().getLimit() == 0) {
                continue;
            }
            // 获取cfgMall
            CfgMallEntity mall = buyMallInfo.getMall();
            // 获取buyNum
            int buyNum = buyMallInfo.getBuyNum();
            if (buyMallInfo.getBuyNum() > mall.getLimit()) {
                throw new ExceptionForClientTip("mall.is.outOfLimit", mall.getLimit().toString());
            }
            UserMallRecord record;
            if (records != null) {
                // 根据recordId获取记录
                record = records.stream().filter(r -> r.getId() == buyMallInfo.getRecordId()).findFirst().orElse(null);
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
        }
    }

    /**
     * 校验购买记录
     *
     * @param gu
     * @param cartBuyInfo
     * @param records
     */
    private void checkUserMallRecord(GameUser gu, CartBuyInfo cartBuyInfo, List<UserMallRecord> records){
        for (CartBuyInfo.BuyMallInfo buyMallInfo : cartBuyInfo.getBuyMallInfos()) {
            // 获取商品记录信息
            UserMallRecord userMallRecord = records.stream().filter(record ->
                    buyMallInfo.getRecordId() == record.getId()).findFirst().orElse(null);
            // 校验购买记录
            if (userMallRecord == null) {
                // 商品过期
                throw new ExceptionForClientTip("cart_mall.overdue");
            }
        }
    }

    /**
     * 购物车结账 统一用户资源校验
     * @param gu
     * @param cartBuyInfo
     * @param records
     */
    private void cartBuyCheckUserRes(GameUser gu, CartBuyInfo cartBuyInfo, List<UserMallRecord> records){
        // 铜钱总价
        int countCopper  = 0;
        // 元宝总价
        int countGold  = 0;
        // 钻石总价
        int countDiamond  = 0;
        for (CartBuyInfo.BuyMallInfo buyMallInfo : cartBuyInfo.getBuyMallInfos()) {
            // 获取商品记录信息
            UserMallRecord userMallRecord = records.stream().filter(record ->
                    record.getBaseId().equals(buyMallInfo.getMall().getId())).findFirst().orElse(null);
            // 获取支付类型
            ConsumeType type = buyMallInfo.getMall().gainConsumeType();
            // 计算商品应支付金额：商品现价 * 购买量 * 折扣（百分率）
            int needPay = buyMallInfo.getMall().getPrice() * buyMallInfo.getBuyNum() * userMallRecord.getDiscount() / 100;
            // 累计所需支付资源数量
            switch (type){
                case COPPER:
                    countCopper += needPay;
                    break;
                case GOLD:
                    countGold += needPay;
                    break;
                case DIAMOND:
                    countDiamond += needPay;
                    break;
                default:
                    break;
            }
        }
        // 校验账户余额
        if (countCopper > 0){
            ResChecker.checkCopper(gu, countCopper);
        }
        if (countGold > 0){
            ResChecker.checkGold(gu, countGold);
        }
        if (countDiamond > 0){
            ResChecker.checkDiamond(gu, countDiamond);
        }
    }

    /**
     * 购物车结账 用户资源扣除
     * @param gu
     * @param mall
     * @param buyNum
     * @param rd
     */
    private void cartBuyMallHandle(GameUser gu, CfgMallEntity mall, int buyNum, RDCommon rd){
        // 获取商品记录信息
        UserMallRecord userMallRecord = mallService.getUserMallRecord(gu.getId(), mall.getId());
        // 获取支付类型
        ConsumeType type = mall.gainConsumeType();
        // 计算商品应支付金额：商品现价 * 购买量 * 折扣（百分率）
        int needPay = mall.getPrice() * buyNum * userMallRecord.getDiscount() / 100;

        long guId = gu.getId();
        // 扣除铜钱、元宝
        switch (type) {
            case GOLD:
                ResEventPublisher.pubGoldDeductEvent(guId, needPay, WayEnum.MALL_BUY, rd);
                mallDetailAsyncHandler.log(new MallDetailEventParam(guId, mall, buyNum, needPay, gu.getGold()));
                break;
            case COPPER:
                ResEventPublisher.pubCopperDeductEvent(guId, (long) (needPay), WayEnum.MALL_BUY, rd);
                mallDetailAsyncHandler.log(new MallDetailEventParam(guId, mall, buyNum, needPay, gu.getCopper()));
                break;
            case DIAMOND:
                ResEventPublisher.pubDiamondDeductEvent(guId, needPay, WayEnum.MALL_BUY, rd);
                mallDetailAsyncHandler.log(new MallDetailEventParam(guId, mall, buyNum, needPay, gu.getDiamond()));
                break;
            default:break;
        }
    }
}