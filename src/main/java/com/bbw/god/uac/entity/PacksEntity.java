package com.bbw.god.uac.entity;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;

import lombok.Data;

/**
 * 礼包
 * 
 * @author shaojun
 * @email lsj@bamboowind.cn
 * @date 2018-10-23 11:15:39
 */
@Data
@TableName("godmanager.packs")
public class PacksEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	@TableId
	private Integer id; //
	private String name; //礼包名称
	private Integer plat; //渠道。base_plat.id
	private Integer type; //类型。20微信绑定礼包。30微信每周礼包
	private String awards; //礼包内容
	private String memo; //备注
	private Date rowUpdateTime; //最近更新时间
}
