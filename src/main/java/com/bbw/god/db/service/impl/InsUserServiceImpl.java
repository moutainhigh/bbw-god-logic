package com.bbw.god.db.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.bbw.db.Query;
import com.bbw.god.db.dao.InsUserDao;
import com.bbw.god.db.entity.InsUserEntity;
import com.bbw.god.db.service.InsUserService;

@Service("insUserService")
public class InsUserServiceImpl extends ServiceImpl<InsUserDao, InsUserEntity> implements InsUserService {

	@Override
	public List<InsUserEntity> getRandResultNicknameLike(int sid, int limit, String keyword, Set<Long> exclude) {
		EntityWrapper<InsUserEntity> wrapper = new EntityWrapper<>();
		wrapper.eq("sid", sid);
		wrapper.notIn("uid", exclude);
		wrapper.like("nickname", keyword);
		Map<String, Object> params = new HashMap<>();
		params.put("limit", String.valueOf(limit));
		params.put("page", "1");
		Page<InsUserEntity> page = selectPage(new Query<InsUserEntity>(params).getPage(), wrapper);
		return page.getRecords();
	}

	@Override
	public List<InsUserEntity> getRandResultfromServer(int sid, int limit, Set<Long> exclude) {
		EntityWrapper<InsUserEntity> wrapper = new EntityWrapper<>();
		wrapper.eq("sid", sid);
		wrapper.notIn("uid", exclude);
		wrapper.orderBy("last_update",false);
		Map<String, Object> params = new HashMap<>();
		params.put("limit", String.valueOf(limit));
		params.put("page", "1");
		Page<InsUserEntity> page = selectPage(new Query<InsUserEntity>(params).getPage(), wrapper);
		return page.getRecords();
	}

}
