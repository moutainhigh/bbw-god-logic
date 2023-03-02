package com.bbw.god.uac.service.impl;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.bbw.cache.LocalCache;
import com.bbw.db.PageUtils;
import com.bbw.db.Query;
import com.bbw.god.security.Password;
import com.bbw.god.uac.dao.AccountDao;
import com.bbw.god.uac.entity.AccountEntity;
import com.bbw.god.uac.service.AccountService;

@Service("accountService")
public class AccountServiceImpl extends ServiceImpl<AccountDao, AccountEntity> implements AccountService {
	private static final Logger logger = LoggerFactory.getLogger(AccountServiceImpl.class);
	private static final String cacheType = "account";

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		Page<AccountEntity> page = this.selectPage(new Query<AccountEntity>(params).getPage(), new EntityWrapper<AccountEntity>());
		return new PageUtils(page);
	}

	@Override
	public boolean existsAccount(String accountKey) {
		boolean b = LocalCache.getInstance().containsKey(cacheType, accountKey);
		if (b) {
			return true;
		}
		int count = this.selectCount(new EntityWrapper<AccountEntity>().eq("email", accountKey));
		return count > 0;
	}

	@Override
	public String modify(String email, String password, String newPassword) {

		//if(!Util.emailValidate(email)) return "请输入正确的电子邮箱!" ;
		//if(!Util.textHasContent(password) || password.length()<6 || password.length()> 20) return "密码长度应为6-20!";
		if (StringUtils.isBlank(newPassword) || newPassword.length() < 6 || newPassword.length() > 20)
			return "密码长度应为6-20!";
		if (password.equals(newPassword)) {
			return "新旧密码不能一样!";
		}

		try {
			AccountEntity account = this.selectOne(new EntityWrapper<AccountEntity>().eq("email", email).eq("password", Password.getSecretPassword(password)));
			if (null == account) {
				return "账号或密码错误!";
			}
			account.setPassword(Password.getSecretPassword(newPassword));
			this.updateById(account);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return "";
	}

	@Override
	public AccountEntity findByAccount(String accountKey) {
		AccountEntity accountEntity = LocalCache.getInstance().get(cacheType, accountKey);
		if (null != accountEntity) {
			return accountEntity;
		}
		accountEntity = selectOne(new EntityWrapper<AccountEntity>().eq("email", accountKey));
		if (null != accountEntity) {
			LocalCache.getInstance().put(cacheType, accountKey, accountEntity, LocalCache.ONE_DAY, true);
		}
		return accountEntity;
	}

	@Override
	public AccountEntity findByEmail(String email) {
		return findByAccount(email);
	}

	@Override
	public int getRegNum(int day, int channel) {
		return this.selectCount(new EntityWrapper<AccountEntity>().where("plat={0} and enroll_time BETWEEN {1} and {2}", channel, day, day + 1));
	}

	@Override
	public boolean updateById(AccountEntity entity) {
		boolean b = super.updateById(entity);
		if (b) {
			LocalCache.getInstance().put(cacheType, entity.getEmail(), entity, LocalCache.ONE_DAY, true);
		}
		return b;
	}

	@Override
	public boolean insert(AccountEntity entity) {
		boolean b = super.insert(entity);
		if (b) {
			AccountEntity accountEntity = selectOne(new EntityWrapper<AccountEntity>().eq("email", entity.getEmail()));
			LocalCache.getInstance().put(cacheType, entity.getEmail(), accountEntity, LocalCache.ONE_DAY, true);
		}
		return b;
	}

}
