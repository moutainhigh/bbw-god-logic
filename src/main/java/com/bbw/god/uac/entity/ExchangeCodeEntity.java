package com.bbw.god.uac.entity;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 礼包兑换记录
 * 
 * @author shaojun
 * @email lsj@bamboowind.cn
 * @date 2018-10-23 11:15:40
 */
@Data
@TableName("godmanager.exchange_code")
public class ExchangeCodeEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	@TableId
	private Integer id; //
	private Integer codeGroup; //
	private String code; //礼包码
	private String user; //玩家。account.email
	private String server; //server.name+game_user.id
	private Integer packs; //礼包。packs.id
	private Boolean status; //状态。1已领取，0未领取
	private Date validTime; //截止时间
	private Date time; //生效时间

	public static ExchangeCodeEntity getInstance(String code, Date validTime, Boolean status, Integer packs) {
		ExchangeCodeEntity exchangeCodeEntity = new ExchangeCodeEntity();
		exchangeCodeEntity.setCode(code);
		exchangeCodeEntity.setValidTime(validTime);
		exchangeCodeEntity.setPacks(packs);
		exchangeCodeEntity.setStatus(status);
		return exchangeCodeEntity;
	}

	public static ExchangeCodeEntity getInstance(String code, Date validTime, Boolean status, Integer packs, Integer codeGroup) {
		ExchangeCodeEntity exchangeCodeEntity = new ExchangeCodeEntity();
		exchangeCodeEntity.setCode(code);
		exchangeCodeEntity.setValidTime(validTime);
		exchangeCodeEntity.setPacks(packs);
		exchangeCodeEntity.setStatus(status);
		exchangeCodeEntity.setCodeGroup(codeGroup);
		return exchangeCodeEntity;
	}
}
