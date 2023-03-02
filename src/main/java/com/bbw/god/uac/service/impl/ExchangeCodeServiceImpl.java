package com.bbw.god.uac.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.bbw.god.uac.dao.ExchangeCodeDao;
import com.bbw.god.uac.entity.ExchangeCodeEntity;
import com.bbw.god.uac.service.ExchangeCodeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service("exchangeCodeService")
public class ExchangeCodeServiceImpl extends ServiceImpl<ExchangeCodeDao, ExchangeCodeEntity> implements ExchangeCodeService {
	/**
	 * 获取礼包兑换码
	 *
	 * @param playerAccount 玩家账号
	 * @return
	 * @see com.bbw.god.Const.Packs
	 */
	public ExchangeCodeEntity getValidWechatWeeklyByUser(String playerAccount) {
		return selectOne(new EntityWrapper<ExchangeCodeEntity>().where("user={0} and packs=3 and status=0", playerAccount));
	}

	public boolean isPackValid(ExchangeCodeEntity exchangeCode) {
		return exchangeCode != null && (exchangeCode.getValidTime() == null || exchangeCode.getValidTime().getTime() > System.currentTimeMillis());
	}
}
