package com.bbw.god.db.entity;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import com.bbw.common.DateUtil;
import com.bbw.common.ID;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 商城购买明细
 *
 * @author suhq
 * @date 2020-05-25 10:08
 **/
@Data
@TableName("ins_mall_detail")
public class InsMallDetailEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableId(type = IdType.INPUT)
    private Long id;
    private Integer serverGroup;
    private Integer sid;
    private Long uid; //玩家ID
    private Integer guLv;// 玩家等级
    private Integer item;// 商品分类
    private Integer mallId;// 商品id
    private Integer goodId;// 购买道具ID
    private String goodName;// 购买道具名称
    private Integer price;// 价格
    private Integer buyNum;// 购买数量
    private Integer pay;// 实际支付
    private Integer unit;//货币单位
    private String unitName;//货币名称
    private Long ownMoney;//购买时拥有的货币数
    private Integer rechargeAmount;//购买时累计充值金额
    private Date buyTime;//购买时间
    private Integer roleLifeMinutes;//自角色创建到现在的时间(分钟)
    private String roleLife;//自角色创建到现在的时间

    public InsMallDetailEntity() {
        this.id = ID.getNextDetailId();
        this.buyTime = DateUtil.now();
    }

}
