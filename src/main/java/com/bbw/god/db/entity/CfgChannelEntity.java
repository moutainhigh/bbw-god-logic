package com.bbw.god.db.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import com.bbw.god.game.config.CfgEntityInterface;

import lombok.Data;

/**
 * 
 * 
 * @author shaojun
 * @email lsj@bamboowind.cn
 * @date 2018-11-27 21:57:48
 */
@Data
@TableName("cfg_channel")
public class CfgChannelEntity implements CfgEntityInterface, Serializable {
	private static final long serialVersionUID = 1L;
	@TableId(type = IdType.INPUT)
	private Integer id; //
	private Integer plat; //
	private Integer serverGroup;// 区服组
	private String platCode; //
	private String name; //
	private Boolean supportZfAccount; //

	@Override
	public int getSortId() {
		return this.getId();
	}

	/**
	 * 是否是苹果渠道
	 * 
	 * @return
	 */
	public boolean isIos() {
		return name.contains("ios") || name.contains("IOS") || name.contains("iso");
	}
}
