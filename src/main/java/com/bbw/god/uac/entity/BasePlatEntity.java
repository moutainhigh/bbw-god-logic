package com.bbw.god.uac.entity;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;

import lombok.Data;

/**
 * 渠道
 * 
 * @author shaojun
 * @email lsj@bamboowind.cn
 * @date 2018-10-23 11:15:39
 */
@Data
@TableName("godmanager.base_plat")
public class BasePlatEntity implements Serializable {
	private static final long serialVersionUID = 1392943399783005374L;
	private static final int w_p = 1;//微信支付二进制标识位
	private static final int z_p = 2;//支付宝二进制标识位
	@TableId
	private Integer id; //
	private Integer groupId; //渠道分组ID
	private Integer plat; //渠道标识
	private String platCode; //渠道代码。客户端使用
	private String name; //渠道名称
	private Boolean supportZfAccount; //是否使用用户中心账号
	private Integer version; //当前官方版本
	private Integer minVersion; //最小支持版本
	private Integer resVersion; //资源版本
	private String downloadUrl; //APP下载地址
	private String shareUrl; //分享地址
	private Boolean selfNotice; //是否独立公告
	private Date time; //创建时间
	private Date rowUpdateTime; //最近修改时间
	private Integer payType = 0; //支付方式。由各种支付方式增加。支付方式标识必须是2的指数

	private Boolean checking; //是否审核状态

	public int getWxPay() {
		return getPayStatus(w_p);
	}

	public int getAliPay() {
		return getPayStatus(z_p);
	}

	private int getPayStatus(int pay) {
		return (payType & pay) != 0 ? 1 : 0;
	}
}
