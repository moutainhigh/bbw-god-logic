package com.bbw.god.db.entity;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 充值明细
 * 
 * @author shaojun
 * @email lsj@bamboowind.cn
 * @date 2019-03-30 08:11:54
 */
@Data
@TableName("ins_receipt")
public class InsReceiptEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	@TableId(type = IdType.INPUT)
	private Long id; //订单ID
	private Integer sid; //支付时候的区服ID。
	private Long uid; //玩家ID
	private Integer pid; //产品ID
	private Integer price; //产品价格
	private String productName; //产品ID
	private Integer quantity; //产品数量
	private Date purchaseDate; //购买时间
	private Integer status; //1付款订单。0:未付款订单
	private Integer payType; //支付方式:0:渠道的支付方式；1:微信支付；2:支付宝支付。   
	private Long userReceiptId; //下发的用户收据
	private Integer dispatchGolds; //实际下发元宝数量
	private Integer dispatchDiamonds; //实际下发钻石数量
	private Integer gmop = 0;//0正常订单，1管理员操作

	/**
	 * 未支付
	 *
	 * @return
	 */
	public boolean noPay() {
		return 1 != status;
	}

}
