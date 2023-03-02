package com.bbw.god.gameuser.pay;

import com.bbw.common.DateUtil;
import com.bbw.common.ID;
import com.bbw.god.db.entity.InsReceiptEntity;
import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.game.config.CfgProductGroup.CfgProduct;
import com.bbw.god.gameuser.UserData;
import com.bbw.god.gameuser.UserDataType;
import com.bbw.god.pay.PayType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户充值
 *
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-03-29 16:18
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UserReceipt extends UserData implements Serializable {
    public static final UserDataType DATA_TYPE = UserDataType.RECEIPT;
    private static final long serialVersionUID = 1L;
    private Long orderId; //订单号
    private int originSid; //原始区服ID
    private int mergeSid = -1; //合服服ID
    private boolean zhiChong = false; //直冲产品
    private Integer productId; //产品ID
    private String productName; //产品名称
    private Integer price; //产品价格，单位元
    private Integer quantity = 1; //数量
    private Date deliveryTime; //送达时间
    private String result = "";//实际下发结果详细说明
    private Integer dispatchGolds = 0; //实际下发元宝数量
    private Integer dispatchDiamonds = 0; //实际下发钻石数量
    /** 支付方式,参考com.bbw.god.pay.PayType。旧数据为null,不做修复，表示未设置该值。 */
    private Integer payType;

    /**
     * 获取商品下发数量
     *
     * @return
     */
    public Integer gainDispatchNum() {
        return dispatchGolds > 0 ? dispatchGolds : dispatchDiamonds;
    }

    /**
     * 获取下发的虚拟币的类型
     *
     * @return
     */
    public AwardEnum gainDispatchItem() {
        if (ifPayForDiamond()) {
            return AwardEnum.ZS;
        }
        return AwardEnum.YB;
    }

    /**
     * 添加下发数量
     *
     * @param dispatchNum
     */
    public void addDispatchNum(int dispatchNum) {
        if (ifPayForDiamond()) {
            dispatchDiamonds += dispatchNum;
            return;
        }
        dispatchGolds += dispatchNum;
    }

    public int getMergeSid() {
        if (mergeSid < 0) {//兼容
            return originSid;
        }
        return mergeSid;
    }

    /**
     * 是否微信公众号、小程序充值
     *
     * @return
     */
    public boolean ifWxPublicPay() {
        return null != payType && PayType.WxPayJSAPI.getValue() == payType;
    }

    /**
     * 是否充值钻石
     *
     * @return
     */
    public boolean ifPayForDiamond() {
        return null != productId && productId >= 40 && productId <= 49;
    }

    @Override
    public UserDataType gainResType() {
        return DATA_TYPE;
    }

    public static UserReceipt from(InsReceiptEntity entity, CfgProduct product, int mergeSid) {
        UserReceipt r = new UserReceipt();
        r.setId(ID.INSTANCE.nextId());
        r.setOrderId(entity.getId());
        r.setOriginSid(entity.getSid());
        r.setGameUserId(entity.getUid());
        r.setProductId(entity.getPid());
        r.setProductName(product.getName());
        r.setPrice(product.getPrice());
        r.setQuantity(entity.getQuantity());
        r.setZhiChong(product.getIsZhiChong());
        r.setDeliveryTime(DateUtil.now());
        r.setMergeSid(mergeSid);
        r.setPayType(entity.getPayType());
        return r;
    }

}
