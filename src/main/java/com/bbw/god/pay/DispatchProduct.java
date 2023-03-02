package com.bbw.god.pay;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.bbw.common.DateUtil;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.exception.PayException;
import com.bbw.god.activity.processor.FirstRechargeProcessor;
import com.bbw.god.activity.processor.NightmareFirstRechargeProcessor;
import com.bbw.god.db.entity.InsReceiptEntity;
import com.bbw.god.db.pool.LogicDataDao;
import com.bbw.god.db.service.InsReceiptService;
import com.bbw.god.event.BaseEventParam;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.config.CfgProductGroup.CfgProduct;
import com.bbw.god.game.config.CfgProductGroup.ProductAward;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.mail.MailService;
import com.bbw.god.gameuser.mail.UserMail;
import com.bbw.god.gameuser.pay.UserPayInfo;
import com.bbw.god.gameuser.pay.UserPayInfoService;
import com.bbw.god.gameuser.pay.UserReceipt;
import com.bbw.god.gameuser.privilege.PrivilegeService;
import com.bbw.god.gameuser.res.ResEventPublisher;
import com.bbw.god.gameuser.res.ResWayType;
import com.bbw.god.gameuser.res.diamond.EPDiamondAdd;
import com.bbw.god.gameuser.res.gold.EPGoldAdd;
import com.bbw.god.gameuser.treasure.UserTreasureService;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.random.box.BoxService;
import com.bbw.god.rd.RDCommon;
import com.bbw.god.rechargeactivities.RechargeActivitiesLogic;
import com.bbw.god.rechargeactivities.wartoken.WarTokenLogic;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * 区服产品下发操作类 使用以下语句获取bean DispatchProduct dispatch = SpringContextUtil.getBean(DispatchProduct.class, sid);
 *
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2018-10-31 15:27
 */
@Service
@Scope("prototype")
@Lazy
@Slf4j
public class DispatchProduct implements LogicDataDao {
    @Autowired
    private InsReceiptService insReceiptService;
    @Autowired
    private ReceiptService receiptService;
    @Autowired
    private WxPublicReceiptService wxPublicReceiptService;
    @Autowired
    private ProductService productService;
    @Autowired
    private MailService mailService;
    @Autowired
    private PrivilegeService privilegeService;
    @Autowired
    private UserTreasureService userTreasureService;
    @Autowired
    private RechargeActivitiesLogic rechargeActivitiesLogic;
    @Autowired
    private FirstRechargeProcessor firstRechargeProcessor;
    @Autowired
    private NightmareFirstRechargeProcessor nightmareFirstRechargeProcessor;
    @Autowired
    private UserPayInfoService userPayInfoService;
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private WarTokenLogic warTokenLogic;
    @Autowired
    private BoxService boxService;

    private int sid;

    @SuppressWarnings("unused")
    private DispatchProduct() {

    }

    public DispatchProduct(int sid) {
        this.sid = sid;
    }

    @Override
    public int getServerId() {
        return this.sid;
    }

    public InsReceiptEntity dbGetInsReceiptEntity(Long orderId) throws PayException {
        InsReceiptEntity receipt = this.insReceiptService.selectById(orderId);
        // 订单不存在
        if (null == receipt) {
            throw PayException.get(PayException.ZF_ORDER_NO_ERROR, orderId);
        }
        return receipt;
    }

    public void dbInsertInsReceiptEntity(InsReceiptEntity receiptEntity) throws PayException {
        this.insReceiptService.insert(receiptEntity);
    }

    /**
     * 获得一段时间内的已发放的订单
     *
     * @param begin
     * @param end
     * @return
     */
    public List<InsReceiptEntity> dbGetDispatchedReceipts(Date begin, Date end) {
        String beginStr = DateUtil.toDateTimeString(begin);
        String endStr = DateUtil.toDateTimeString(end);
        EntityWrapper<InsReceiptEntity> wrapper = new EntityWrapper<>();
        wrapper.where("user_receipt_id>0  AND purchase_date >= '" + beginStr + "' AND purchase_date<='" + endStr + "'");
        List<InsReceiptEntity> receipts = this.insReceiptService.selectList(wrapper);
        return receipts;
    }

    public UserReceipt dbDispatch(InsReceiptEntity receipt, GameUser user) throws PayException {
        Long orderId = receipt.getId();
        // 已经发放
        if (receipt.getUserReceiptId() > 0) {
            throw PayException.get(PayException.ORDER_DUPLICATE, orderId);
        }
        // 未支付
        if (receipt.noPay()) {
            throw PayException.get(PayException.ORDER_INFO_ERROR, orderId);
        }
        // 下发产品
        CfgProduct product = this.productService.getCfgProduct(receipt.getPid(), receipt.getPayType());
        // 检查优惠券
        checkVoucher(user, product);
        UserReceipt userReceipt = UserReceipt.from(receipt, product, user.getServerId());
        try {
            if (product.isWarToken()) {
                warTokenLogic.dispatch(userReceipt, product.getId(), user.getId());
                receipt.setUserReceiptId(userReceipt.getId());
                receipt.setDispatchGolds(0);
                this.insReceiptService.updateById(receipt);
                return userReceipt;
            }
            // 月卡
            if (product.isYueKa()) {
                dispatchYueKa(receipt, product, user, userReceipt);
                receipt.setUserReceiptId(userReceipt.getId());
                receipt.setDispatchGolds(userReceipt.getDispatchGolds());
                this.insReceiptService.updateById(receipt);
                UserMail receiptMail = UserMail.newRiceiptMail(userReceipt);
                this.mailService.send(receiptMail);
                this.privilegeService.sendHeadBox(user);
                return userReceipt;
            }

            // 季卡
            if (product.isJiKa() || product.isForeverJiKa() || product.isUpgradeForeverJiKa()) {
                dispatchJiKa(receipt, product, user, userReceipt);
                receipt.setUserReceiptId(userReceipt.getId());
                receipt.setDispatchGolds(userReceipt.getDispatchGolds());
                this.insReceiptService.updateById(receipt);
                UserMail receiptMail = UserMail.newRiceiptMail(userReceipt);
                this.mailService.send(receiptMail);
                this.privilegeService.sendHeadBox(user);
                return userReceipt;
            }
            // 速战卡
            if (product.isSuZhanKa()) {
                dispatchSuZhanKa(user, product, userReceipt);
                receipt.setUserReceiptId(userReceipt.getId());
                receipt.setDispatchGolds(userReceipt.getDispatchGolds());
                this.insReceiptService.updateById(receipt);
                UserMail receiptMail = UserMail.newRiceiptMail(userReceipt);
                this.mailService.send(receiptMail);
                this.privilegeService.sendHeadBox(user);
                return userReceipt;
            }
            // 直冲产品
            if (product.getIsZhiChong()) {
                dispatchZhiCong(user, product, userReceipt, receipt);
                receipt.setUserReceiptId(userReceipt.getId());
                this.insReceiptService.updateById(receipt);
                return userReceipt;
            }
            // 虚拟币充值
            dispatchCurrency(user, product, userReceipt);
            receipt.setUserReceiptId(userReceipt.getId());
            receipt.setDispatchGolds(userReceipt.getDispatchGolds());
            receipt.setDispatchDiamonds(userReceipt.getDispatchDiamonds());
            this.insReceiptService.updateById(receipt);
            UserMail receiptMail = UserMail.newRiceiptMail(userReceipt);
            this.mailService.send(receiptMail);
            return userReceipt;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new PayException(user.getId() + "充值" + product.getName() + ",下发发生未知的充值错误！" + e.getMessage(), e);
        }
    }

    /**
     * 检查优惠券
     *
     * @param user
     * @param product
     */
    private void checkVoucher(GameUser user, CfgProduct product) {
        // 1元萌新礼包直接扣除抵用券
        if (99001211 == product.getId()) {
            int treasureNum = userTreasureService.getTreasureNum(user.getId(), TreasureEnum.NEWER_VOUCHER.getValue());
            if (treasureNum < 1) {
                throw new ExceptionForClientTip("treasure.not.enough", TreasureEnum.NEWER_VOUCHER.getName());
            }
            TreasureEventPublisher.pubTDeductEvent(user.getId(), TreasureEnum.NEWER_VOUCHER.getValue(), 1,
                    WayEnum.RECHARGE, new RDCommon());
        }
    }

    // 直冲产品
    private void dispatchZhiCong(GameUser user, CfgProduct product, UserReceipt userReceipt, InsReceiptEntity receipt) {
        boolean needDontSendEmil = rechargeActivitiesLogic.updateRechargeStatus(user.getId(), product);
        Optional<ProductAward> optional = this.productService.getProductAwardOptional(product.getId());
        if (optional.isPresent()) {
            ProductAward productAward = optional.get();
            userReceipt.setResult(productAward.getMemo());
            if (!needDontSendEmil) {
                //不是领取方式的直冲  需要已邮件发送奖励
                List<Award> awards = new ArrayList<>();
                awards.addAll(productAward.getAwardList());
                String content = productAward.getMemo();
                this.mailService.sendAwardMail(product.getName(), content, user.getId(), awards);
            }
        } else {
            userReceipt.setResult("");
        }
    }

    /**
     * 下发虚拟币
     *
     * @param user
     * @param product
     * @param userReceipt
     */
    private void dispatchCurrency(GameUser user, CfgProduct product, UserReceipt userReceipt) {
        // 充值产品定义的元宝数量,
        int quantity = product.getQuantity();
        String addMsg = "产品" + userReceipt.gainDispatchItem().getName() + "[" + product.getQuantity() + "]";
        // 首购时间重置后是否有购买记录
        List<UserReceipt> userReceipts = null;
        boolean isEverBuyForWxPublic = false;
        if (!userReceipt.ifWxPublicPay()) {
            userReceipts = receiptService.getValidUserReceipt(user.getId(), user.getServerId());
        } else {
            userReceipts = wxPublicReceiptService.getValidUserReceipt(user.getId(), user.getServerId());
            isEverBuyForWxPublic = receiptService.getValidUserReceipt(user.getId(), user.getServerId()).stream()
                    .filter(r -> r.getProductId().intValue() == product.getId() && r.getDeliveryTime().after(ReceiptService.FIRST_RECHARGE_RESET_TIME))
                    .findAny().isPresent();
        }
        Optional<UserReceipt> buy = userReceipts.stream().filter(r -> r.getProductId().intValue() == product.getId()
                && r.getDeliveryTime().after(ReceiptService.FIRST_RECHARGE_RESET_TIME)).findAny();
        // 符合首购条件
        int extraQuantity = 0;
        //梦魇世界首冲
        if (nightmareFirstRechargeProcessor.isFirstRechargeItem(product.getId())) {
            //梦魇世界首冲元宝
            if (nightmareFirstRechargeProcessor.isFirst(user)) {
                int rate = product.getFirstRate();
                extraQuantity = quantity * rate - quantity;
                addMsg += ",梦魇世界首充[" + rate + "]倍";
            } else {
                if (product.getExtraNum() > 0) {
                    extraQuantity = product.getExtraNum();
                    addMsg += ",赠送数量[" + product.getExtraNum() + "]";
                }
            }
        } else if (firstRechargeProcessor.isFirstRechargeItem(product.getId())) {
            //李家父子首充元宝
            if (firstRechargeProcessor.isFirst(user)) {
                int rate = product.getFirstRate();
                extraQuantity = quantity * rate - quantity;
                addMsg += ",李家父子首充[" + rate + "]倍";
            } else {
                if (product.getExtraNum() > 0) {
                    extraQuantity = product.getExtraNum();
                    addMsg += ",赠送数量[" + product.getExtraNum() + "]";
                }
            }
        } else {
            //虚拟币充值额外赠送
            boolean isWxPublicDiamondWeekFirstBuy = !buy.isPresent() && productService.ifPayForDiamond(product.getId()) && userReceipt.ifWxPublicPay() && isEverBuyForWxPublic;
            if (isWxPublicDiamondWeekFirstBuy) {
                extraQuantity = productService.getExtraDiamondForWXMP(product.getId());
                addMsg += ",赠送数量[" + extraQuantity + "]";
            } else if ((!buy.isPresent() && product.getFirstRate() > 1)) {
                int rate = product.getFirstRate();
                extraQuantity = quantity * rate - quantity;
                addMsg += ",首充[" + rate + "]倍";
            } else {
                // 重复购买，加额外赠送数量
                if (product.getExtraNum() > 0) {
                    extraQuantity = product.getExtraNum();
                    addMsg += ",赠送数量[" + product.getExtraNum() + "]";
                }
            }
        }
        int dispatchNum = quantity + extraQuantity;
        addMsg += ",总数[" + dispatchNum + "]。";
        userReceipt.setResult(addMsg);
        userReceipt.addDispatchNum(dispatchNum);
        UserPayInfo userPayInfo = userPayInfoService.getUserPayInfo(user.getId());
        userPayInfo.setFirstBought(true);
        gameUserService.updateItem(userPayInfo);

        //触发虚拟币下发事件
        if (userReceipt.getDispatchGolds() > 0) {
            BaseEventParam bep = new BaseEventParam(user.getId(), WayEnum.RECHARGE, new RDCommon());
            EPGoldAdd evGoldAdd = new EPGoldAdd(bep, quantity);
            evGoldAdd.addGold(ResWayType.Presentation, extraQuantity);
            ResEventPublisher.pubGoldAddEvent(evGoldAdd);
        } else if (userReceipt.getDispatchDiamonds() > 0) {
            BaseEventParam bep = new BaseEventParam(user.getId(), WayEnum.RECHARGE, new RDCommon());
            EPDiamondAdd evDiamondAdd = new EPDiamondAdd(bep, quantity);
            evDiamondAdd.addDiamond(ResWayType.Presentation, extraQuantity);
            ResEventPublisher.pubDiamondAddEvent(evDiamondAdd);
        }
    }

    // 速战卡
    private void dispatchSuZhanKa(GameUser user, CfgProduct product, UserReceipt userReceipt) {
        UserPayInfo userPayInfo = userPayInfoService.getUserPayInfo(user.getId());
        userPayInfo.setEndFightBuyTime(DateUtil.now());
        userPayInfo.setFirstBought(true);
        gameUserService.updateItem(userPayInfo);
        // 充值产品定义的元宝数量,
        int quantity = product.getQuantity();
        userReceipt.setDispatchGolds(quantity);
        ResEventPublisher.pubGoldAddEvent(user.getId(), quantity, WayEnum.RECHARGE, new RDCommon());
        String msg = "速战卡,赠送元宝数量[" + quantity + "]。";
        userReceipt.setResult(msg);
    }

    // 月卡
    private void dispatchYueKa(InsReceiptEntity receipt, CfgProduct product, GameUser user, UserReceipt userReceipt) {
        UserPayInfo userPayInfo = userPayInfoService.getUserPayInfo(user.getId());
        Date ykEndTime = userPayInfo.getYkEndTime();
        if (ykEndTime == null || ykEndTime.before(DateUtil.now())) {// 之前未购买，或者今天到期,
            // 或者超期后再购买
            // 包括今天，共30天
            ykEndTime = DateUtil.addDays(DateUtil.now(), 29);
        } else {
            ykEndTime = DateUtil.addDays(ykEndTime, 30);
        }
        userPayInfo.setYkEndTime(ykEndTime);
        gameUserService.updateItem(userPayInfo);
        // 充值产品定义的元宝数量,
        int quantity = product.getQuantity();
        userReceipt.setDispatchGolds(quantity);
        ResEventPublisher.pubGoldAddEvent(user.getId(), quantity, WayEnum.RECHARGE, new RDCommon());
        String msg = "月卡,赠送元宝数量[" + quantity + "]。";
        userReceipt.setResult(msg);
    }

    // 季卡
    private void dispatchJiKa(InsReceiptEntity receipt, CfgProduct product, GameUser user, UserReceipt userReceipt) {
        UserPayInfo userPayInfo = userPayInfoService.getUserPayInfo(user.getId());
        String msgContent = "季卡,赠送元宝数量[%s]。";
        if (product.isForeverJiKa() || product.isUpgradeForeverJiKa()) {
            userPayInfo.setJkEndTime(DateUtil.fromDateTimeString("2099-01-01 00:00:00"));
            msgContent = "永久季卡,赠送元宝数量[%s]。";
            userPayInfo.setJkAwardTime(null);
        } else {
            Date jkEndTime = userPayInfo.getJkEndTime();
            if (jkEndTime == null || jkEndTime.before(DateUtil.now())) {// 之前未购买，或者今天到期,
                // 或者超期后再购买
                // 包括今天，共30天
                jkEndTime = DateUtil.addDays(DateUtil.now(), 89);
            } else {
                jkEndTime = DateUtil.addDays(jkEndTime, 90);
            }
            userPayInfo.setJkEndTime(jkEndTime);
        }
        gameUserService.updateItem(userPayInfo);
        // 充值产品定义的元宝数量,
        int quantity = product.getQuantity();
        userReceipt.setDispatchGolds(quantity);
        ResEventPublisher.pubGoldAddEvent(user.getId(), quantity, WayEnum.RECHARGE, new RDCommon());
        String msg = String.format(msgContent, quantity);
        userReceipt.setResult(msg);
    }
}
