package com.bbw.god.uac.service;

import com.baomidou.mybatisplus.service.IService;
import com.bbw.god.uac.entity.ExchangeCodeEntity;

/**
 * 
 *
 * @author shaojun
 * @email lsj@bamboowind.cn
 * @date 2018-04-26 15:19:23
 */
public interface ExchangeCodeService extends IService<ExchangeCodeEntity> {
	ExchangeCodeEntity getValidWechatWeeklyByUser(String playerAccount);

	boolean isPackValid(ExchangeCodeEntity exchangeCode);
}
