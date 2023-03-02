package com.bbw.god.uac.service.impl;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.bbw.db.PageUtils;
import com.bbw.db.Query;
import com.bbw.god.Const;
import com.bbw.god.uac.dao.AccountBindDao;
import com.bbw.god.uac.entity.AccountBindEntity;
import com.bbw.god.uac.service.AccountBindService;

@Service("accountBindService")
public class AccountBindServiceImpl extends ServiceImpl<AccountBindDao, AccountBindEntity> implements AccountBindService {
	@Override
	public List<AccountBindEntity> getWechatBindAccountEntity(String bindKey) {
		return getAccountBindEntity(Const.AccountBind.BIND_TYPE_WECHAT, bindKey);
	}

	@Override
	public List<AccountBindEntity> getMobilephoneBindAccountEntity(String bindKey) {
		return getAccountBindEntity(Const.AccountBind.BIND_TYPE_MOBILEPHONE, bindKey);
	}

	@Override
	public List<AccountBindEntity> getAlipaytBindAccountEntity(String bindKey) {
		return getAccountBindEntity(Const.AccountBind.BIND_TYPE_ALIPAY, bindKey);
	}

	@Override
	public List<AccountBindEntity> getAccountBindEntity(int bindType, String bindKey) {
		return selectList(new EntityWrapper<AccountBindEntity>().eq("bind_type", bindType).eq("bind_key", bindKey));
	}

	@Override
	public boolean isPlayerAccountBinded(String playerAccount, int bindType) {
		AccountBindEntity AccountBindEntity = this.selectOne(new EntityWrapper<AccountBindEntity>().eq("bind_type", bindType).eq("player_account", playerAccount));
		return null != AccountBindEntity ? true : false;
	}

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		Page<AccountBindEntity> page = this.selectPage(new Query<AccountBindEntity>(params).getPage(), new EntityWrapper<AccountBindEntity>());

		return new PageUtils(page);
	}

	@Override
	public Optional<AccountBindEntity> getWechatBindGuessAccountEntity(String bindKey) {
		List<AccountBindEntity> list = getWechatBindAccountEntity(bindKey);
		if (null == list || list.isEmpty()) {
			return Optional.empty();
		}
		for (AccountBindEntity entity : list) {
			if (entity.getPlayerAccount().endsWith(Const.GUESS_ACCOUNT_KEY)) {
				return Optional.of(entity);
			}
		}
		return Optional.empty();
	}

}
