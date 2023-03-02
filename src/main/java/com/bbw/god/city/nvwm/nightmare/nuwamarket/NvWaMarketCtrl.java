package com.bbw.god.city.nvwm.nightmare.nuwamarket;

import com.bbw.common.SensitiveWordUtil;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.city.nvwm.nightmare.nuwamarket.rd.RDBoothInfo;
import com.bbw.god.city.nvwm.nightmare.nuwamarket.rd.RDNvWaMarketInfos;
import com.bbw.god.city.nvwm.nightmare.nuwamarket.rd.RDTradeRecordInfo;
import com.bbw.god.controller.AbstractController;
import com.bbw.god.game.CR;
import com.bbw.god.login.LoginPlayer;
import com.bbw.god.rd.RDSuccess;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 女娲集市
 *
 * @author fzj
 * @date 2022/5/24 15:27
 */
@Api(tags = {"女娲集市接口"})
@RestController
public class NvWaMarketCtrl extends AbstractController {
    @Autowired
    NvWaMarketLogic nvWaMarketLogic;

    @ApiOperation(value = "进入女娲集市")
    @GetMapping(CR.NightmareNvWM.ENTER_NVW_MARKET)
    public RDNvWaMarketInfos enterNvWMarket(int page) {
        return nvWaMarketLogic.enterNvWMarket(getUserId(), page);
    }

    @ApiOperation(value = "获得交易记录")
    @GetMapping(CR.NightmareNvWM.TRANSACTION_RECORD)
    public RDTradeRecordInfo getTradeRecord() {
        return nvWaMarketLogic.getTradeRecord(getUserId());
    }

    @ApiOperation(value = "发送消息")
    @GetMapping(CR.NightmareNvWM.SEND_MESSAGE)
    public RDSuccess sendMessage(Integer boothNum, String message) {
        return nvWaMarketLogic.sendMessage(getUserId(), boothNum, message);
    }

    @ApiOperation(value = "搜索摊位")
    @GetMapping(CR.NightmareNvWM.SEARCH_BOOTH)
    public RDSuccess searchBooth(String message) {
        return nvWaMarketLogic.searchBooth(message);
    }

    @ApiOperation(value = "租赁摊位")
    @GetMapping(CR.NightmareNvWM.RENTAL_BOOTH)
    public RDSuccess rentalBooth() {
        return nvWaMarketLogic.rentalBooth(getUserId());
    }

    @ApiOperation(value = "我的摊位")
    @GetMapping(CR.NightmareNvWM.MY_BOOTH)
    public RDBoothInfo myBooth() {
        return nvWaMarketLogic.getUserBooth(getUserId());
    }

    @ApiOperation(value = "更改摊位状态")
    @GetMapping(CR.NightmareNvWM.UPDATE_BOOTH_STATUS)
    public RDSuccess updateBoothStatus() {
        return nvWaMarketLogic.updateBoothStatus(getUserId());
    }

    @ApiOperation(value = "上架商品")
    @GetMapping(CR.NightmareNvWM.LISTINGS)
    public RDSuccess listings(String productInfo) {
        return nvWaMarketLogic.listings(getUserId(), productInfo);
    }

    @ApiOperation(value = "下架商品")
    @GetMapping(CR.NightmareNvWM.TAKE_DOWN)
    public RDSuccess takeDown(long productId) {
        return nvWaMarketLogic.takeDown(getUserId(), productId);
    }

    @ApiOperation(value = "更改商品")
    @GetMapping(CR.NightmareNvWM.MODIFY_PRODUCT)
    public RDSuccess modifyProduct(long productId, String productInfo) {
        return nvWaMarketLogic.modifyProduct(getUserId(), productId, productInfo);
    }

    @ApiOperation(value = "更改出价")
    @GetMapping(CR.NightmareNvWM.MODIFY_BARGAIN)
    public RDSuccess modifyBargain(long productId, String bargainInfo) {
        return nvWaMarketLogic.modifyBargain(getUserId(), productId, bargainInfo);
    }

    @ApiOperation(value = "设置标语")
    @GetMapping(CR.NightmareNvWM.SET_BOOTH_SLOGAN)
    public RDSuccess setBoothSlogan(String slogan) {
        LoginPlayer user = getUser();
        if (SensitiveWordUtil.isNotPass(slogan, user.getChannelId(), user.getOpenId())) {
            throw new ExceptionForClientTip("createrole.not.sensitive.words");
        }
        return nvWaMarketLogic.setBoothSlogan(getUserId(), slogan);
    }

    @ApiOperation(value = "交易")
    @GetMapping(CR.NightmareNvWM.TRADE)
    public RDSuccess trade(int boothNo, long productNo, int priceNo, String oldProductInfo) {
        return nvWaMarketLogic.trade(getUserId(), boothNo, productNo, priceNo, oldProductInfo);
    }

    @ApiOperation(value = "摊位详情")
    @GetMapping(CR.NightmareNvWM.BOOTH_DETAILS)
    public RDSuccess boothDetails(int boothNo) {
        return nvWaMarketLogic.getBoothInfo(boothNo);
    }

    @ApiOperation(value = "获得模板")
    @GetMapping(CR.NightmareNvWM.GET_PRICE_MODEL)
    public RDSuccess getPriceModel() {
        return nvWaMarketLogic.getPriceModel(getUserId());
    }

    @ApiOperation(value = "设置模板")
    @GetMapping(CR.NightmareNvWM.SET_PRICE_MODEL)
    public RDSuccess setPriceModel(String priceModel) {
        return nvWaMarketLogic.setPriceModel(getUserId(), priceModel);
    }

    @ApiOperation(value = "续租摊位")
    @GetMapping(CR.NightmareNvWM.LEASE_RENEWAL)
    public RDSuccess leaseRenewal() {
        return nvWaMarketLogic.leaseRenewal(getUserId());
    }

    @ApiOperation(value = "讨价还价")
    @GetMapping(CR.NightmareNvWM.BARGAIN)
    public RDSuccess bargain(int boothNo, long productNo, String bargain, String message, String oldProductInfo) {
        LoginPlayer user = getUser();
        if (SensitiveWordUtil.isNotPass(message, user.getChannelId(), user.getOpenId())) {
            throw new ExceptionForClientTip("createrole.not.sensitive.words");
        }
        return nvWaMarketLogic.bargain(getUserId(), boothNo, productNo, bargain, message, oldProductInfo);
    }

    @ApiOperation(value = "同意还价")
    @GetMapping(CR.NightmareNvWM.AGREE_PRICE)
    public RDSuccess agreePrice(long bargainId) {
        return nvWaMarketLogic.agreePrice(getUserId(), bargainId);
    }

    @ApiOperation(value = "拒绝还价")
    @GetMapping(CR.NightmareNvWM.REFUSE_PRICE)
    public RDSuccess refusePrice(long bargainId) {
        return nvWaMarketLogic.refusePrice(getUserId(), bargainId);
    }

    @ApiOperation(value = "撤销还价")
    @GetMapping(CR.NightmareNvWM.REVOKE_PRICE)
    public RDSuccess revokePrice(long bargainId) {
        return nvWaMarketLogic.revokePrice(getUserId(), bargainId);
    }

    @ApiOperation(value = "还价列表")
    @GetMapping(CR.NightmareNvWM.BARGAIN_LIST)
    public RDSuccess getBargainList(int type) {
        return nvWaMarketLogic.getBargainList(getUserId(), type);
    }
}
