package com.bbw.god.pay;

import com.bbw.god.rd.RDSuccess;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-03-30 10:46
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class RDPayCallback extends RDSuccess {
	private String message = "成功！";
	private boolean done = false;
	//ios 充值返回需要的参数】
	/** 下发元宝数量 **/
	private Integer addedGold;
	/** 下发钻石数量 **/
	private Integer addedDiamond;
	private String productName;
	private Integer ykRemainDays = 0;
}
