package com.bbw.god.game.sxdh;

import com.bbw.common.DateUtil;
import com.bbw.common.ID;
import com.bbw.god.gameuser.UserData;
import com.bbw.god.gameuser.UserDataType;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 神仙大会商店购买记录
 *
 * @author suhq
 * @date 2019-06-26 09:18:07
 */
@Deprecated
@Data
public class SxdhShopRecord extends UserData implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer goodId;// 商品
    private Integer num = 0;// 购买数量
    private Date lastBuyTime;// 最近一次购买时间

    public static SxdhShopRecord instance(long uid, int goodId, int num) {
        SxdhShopRecord record = new SxdhShopRecord();
        record.setId(ID.INSTANCE.nextId());
        record.setGameUserId(uid);
        record.setGoodId(goodId);
        record.setNum(num);
        record.setLastBuyTime(DateUtil.now());
        return record;
    }

    public void addBought(int add) {
        num += add;
        lastBuyTime = DateUtil.now();
    }

    @Override
    public UserDataType gainResType() {
        return UserDataType.SXDH_SHOP_RECORD;
    }

}
