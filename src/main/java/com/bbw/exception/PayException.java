package com.bbw.exception;

import com.bbw.common.LM;

/**
 * 渠道支付错误
 *
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2018-10-19 14:44
 */
public class PayException extends GodException {

    private static final long serialVersionUID = 4995635070950366079L;

    /**
     * 重复订单
     */
    public static final int ORDER_DUPLICATE = 107;
    /**
     * 订单号错误
     */
    public static final int ZF_ORDER_NO_ERROR = 101;
    /**
     * 充值用户错误
     */
    public static final int GAME_USER_ERROR = 102;
    /**
     * 充值产品错误
     */
    public static final int PRODUCT_ERROR = 103;
    /**
     * 订单信息不一致
     */
    public static final int ORDER_INFO_ERROR = 104;
    /**
     * 签名错误
     */
    public static final int SIGNATURE_ERROR = 105;
    /**
     * 请求订单出错
     */
    public static final int REQ_ORDER_ERROR = 106;

    private static String getMsg(int code) {
        switch (code) {
            case PayException.ORDER_DUPLICATE:
                return "pay.order.duplicate";
            case PayException.ZF_ORDER_NO_ERROR:
                return "pay.order.zf.orderNo.error";
            case PayException.GAME_USER_ERROR:
                return "pay.order.user.error";
            case PayException.PRODUCT_ERROR:
                return "pay.order.product.error";
            case PayException.ORDER_INFO_ERROR:
                return "pay.order.info.error";
            case PayException.SIGNATURE_ERROR:
                return "pay.order.signature.error";
            case PayException.REQ_ORDER_ERROR:
                return "pay.order.req.error";
        }
        return "pay.order.error";
    }

    public static PayException get(int code, Long orderId) {
        PayException pe = new PayException(String.format(LM.I.getMsg(getMsg(code)), orderId));
        pe.setCode(code);
        return pe;
    }

    private PayException(int code) {
        super(LM.I.getMsg("pay.error"), code);
    }

    private PayException(String msg) {
        super(msg);
    }

    public PayException(String msg, Throwable e) {
        super(msg, e);
    }

    public PayException(String msg, int code) {
        super(msg, code);
    }

    public PayException(String msg, int code, Throwable e) {
        super(msg, code, e);
    }

    public static PayException get(String msg) {
        PayException pe = new PayException(msg);
        return pe;
    }
}
