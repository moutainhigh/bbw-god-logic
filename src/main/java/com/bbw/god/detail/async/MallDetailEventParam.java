package com.bbw.god.detail.async;

import com.bbw.god.ConsumeType;
import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.game.config.card.CfgCardEntity;
import com.bbw.god.game.config.mall.CfgMallEntity;
import lombok.Data;

import java.io.Serializable;

/**
 * 商城购买明细
 *
 * @author suhq
 * @date 2020-05-25 10:08
 **/
@Data
public class MallDetailEventParam implements Serializable {
    private static final long serialVersionUID = 6878427491153506056L;
    private Long uid; //玩家ID
    private Integer item;//商品分类
    private Integer mallId = 0;// 商品id
    private Integer goodId;// 购买道具ID
    private String goodName;// 购买道具名称
    private Integer price;// 价格
    private Integer buyNum;// 购买数量
    private Integer pay;// 实际支付
    private ConsumeType unit;//货币单位
    private Long ownMoney;//购买时拥有的货币数

    public MallDetailEventParam(long uid, CfgMallEntity mallEntity, int buyNum, int pay, long ownMoney) {
        this.uid = uid;
        this.item = AwardEnum.FB.getValue();
        this.mallId = mallEntity.getId();
        this.goodId = mallEntity.getGoodsId();
        this.goodName = mallEntity.getName();
        this.price = mallEntity.getPrice();
        this.buyNum = buyNum;
        this.pay = pay;
        this.unit = ConsumeType.fromValue(mallEntity.getUnit());
        this.ownMoney = ownMoney;
    }

    public MallDetailEventParam(long uid, CfgCardEntity cardEntity, int price, int buyNum, int pay, ConsumeType unit, long ownMoney) {
        this.uid = uid;
        this.item = AwardEnum.KP.getValue();
        this.goodId = cardEntity.getId();
        this.goodName = cardEntity.getName();
        this.price = price;
        this.buyNum = buyNum;
        this.pay = pay;
        this.unit = unit;
        this.ownMoney = ownMoney;
    }

    public MallDetailEventParam(long uid, int item, int goodId, String goodName, int price, int buyNum, int pay, ConsumeType unit, long ownMoney) {
        this.uid = uid;
        this.item = item;
        this.goodId = goodId;
        this.goodName = goodName;
        this.price = price;
        this.buyNum = buyNum;
        this.pay = pay;
        this.unit = unit;
        this.ownMoney = ownMoney;
    }

}
