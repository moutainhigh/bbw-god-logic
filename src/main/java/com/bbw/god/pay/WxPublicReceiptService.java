package com.bbw.god.pay;

import com.bbw.common.DateUtil;
import com.bbw.common.ListUtil;
import com.bbw.god.gameuser.pay.UserReceipt;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 微信公众号充值服务
 *
 * @author: suhq
 * @date: 2022/1/5 4:38 下午
 */
@Slf4j
@Service
public class WxPublicReceiptService {
    @Autowired
    private ReceiptService receiptService;

    /**
     * 返回首购重置后的购买记录，需要玩家的原始区服ID。每周首冲翻倍
     *
     * @param uid
     * @param sid
     * @return
     */
    public List<UserReceipt> getValidUserReceipt(long uid, int sid) {
        List<UserReceipt> receipts = receiptService.getValidUserReceipt(uid, sid);
        if (ListUtil.isEmpty(receipts)) {
            return receipts;
        }
        return receipts.stream()
                .filter(tmp -> tmp.ifWxPublicPay() && DateUtil.isThisWeek(tmp.getDeliveryTime()))
                .collect(Collectors.toList());
    }
}
