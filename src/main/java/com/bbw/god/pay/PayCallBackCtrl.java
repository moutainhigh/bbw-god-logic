
package com.bbw.god.pay;

import com.bbw.coder.CoderNotify;
import com.bbw.common.DateUtil;
import com.bbw.common.SpringContextUtil;
import com.bbw.exception.PayException;
import com.bbw.god.db.async.UpdateRoleInfoAsyncHandler;
import com.bbw.god.db.entity.CfgServerEntity;
import com.bbw.god.db.entity.InsReceiptEntity;
import com.bbw.god.db.entity.InsRoleInfoEntity;
import com.bbw.god.game.CR;
import com.bbw.god.game.config.server.ServerTool;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.pay.UserPayInfo;
import com.bbw.god.gameuser.pay.UserPayInfoService;
import com.bbw.god.gameuser.pay.UserReceipt;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

/**
 * 充值回调
 *
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-05-10 01:11
 */
@Slf4j
@RestController
public class PayCallBackCtrl extends CoderNotify {
    @Autowired
    protected GameUserService gameUserService;
    @Autowired
    private UserPayInfoService userPayInfoService;
    @Autowired
    private UpdateRoleInfoAsyncHandler updateRoleInfoAsyncHandler;

    /**
     * 产品发放成功后通知
     *
     * @param orderId
     * @param sid
     * @param gmop:   0|正常充值。1:GM充值
     * @return
     */
    @GetMapping(CR.Product.NOTIFY)
    public RDPayCallback notify(Long orderId, int sid, @RequestParam(defaultValue = "0") int gmop) {
        String msg = "公服支付回调：sid=[" + sid + "] orderId=[" + orderId + "] gmop=[" + gmop + "]！";
        log.info(msg);
        RDPayCallback rd = new RDPayCallback();
        try {
            CfgServerEntity server = ServerTool.getServer(sid);
            DispatchProduct dispatch = SpringContextUtil.getBean(DispatchProduct.class, server.getMergeSid());
            // 获取公服写过来的订单
            InsReceiptEntity receipt = dispatch.dbGetInsReceiptEntity(orderId);
            receipt.setGmop(gmop);
            // 已经发放
            if (receipt.getUserReceiptId() > 0) {
                throw PayException.get(PayException.ORDER_DUPLICATE, orderId);
            }
            // 用户不存在
            GameUser user = this.gameUserService.getGameUser(receipt.getUid());
            if (null == user) {
                throw PayException.get(PayException.GAME_USER_ERROR, orderId);
            }
            // 订单已经存在，但是已经发放
            List<UserReceipt> userReceipts = this.gameUserService.getMultiItems(receipt.getUid(), UserReceipt.class);
            Optional<UserReceipt> done = userReceipts.stream().filter(tmp -> tmp.getOrderId().longValue() == orderId.longValue()).findAny();
            if (done.isPresent()) {
                throw PayException.get(PayException.ORDER_DUPLICATE, orderId);
            }
            UserReceipt userReceipt = dispatch.dbDispatch(receipt, user);
            this.gameUserService.addItem(userReceipt.getGameUserId(), userReceipt);
            ProductEventPublisher.pubDeliverEvent(userReceipt);
            InsRoleInfoEntity role = new InsRoleInfoEntity();
            role.setUid(user.getId());
            role.setPay(receipt.getPrice());
            //玩家充值
            if (0 == receipt.getGmop()) {
                updateRoleInfoAsyncHandler.setRoleInfo(role, 5);
            }
            log.info("订单[{}]已经正常下发。下发结果[ {} ]", orderId, userReceipt.getResult());
            rd.setDone(true);
            rd.setAddedGold(userReceipt.getDispatchGolds());
            rd.setAddedDiamond(userReceipt.getDispatchDiamonds());
            rd.setProductName(userReceipt.getProductName());
            if (rd.getProductName().contains("月卡")) {
                UserPayInfo userPayInfo = userPayInfoService.getUserPayInfo(user.getId());
                int days = DateUtil.getDaysBetween(DateUtil.now(), userPayInfo.getYkEndTime());
                rd.setYkRemainDays(days);
            }
        } catch (PayException e) {
            rd.setRes(e.getCode());
            rd.setMessage(e.getMsg());
            log.error(e.getMsg(), e);
            if (PayException.ORDER_DUPLICATE != e.getCode()) {
                notifyCoderHigh(msg + e.getMsg(), e);
            }
        } catch (Exception e) {
            e.printStackTrace();
            notifyCoderHigh(msg, e);
            throw new PayException(msg + e.getMessage(), e);
        }
        return rd;
    }

}
