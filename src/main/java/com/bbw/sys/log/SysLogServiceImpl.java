package com.bbw.sys.log;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.bbw.db.PageUtils;
import com.bbw.db.Query;
import com.bbw.sys.dao.SysLogDao;

@Service("sysLogService")
public class SysLogServiceImpl extends ServiceImpl<SysLogDao, SysLogEntity> implements SysLogService {

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		String key = (String) params.get("key");

		Page<SysLogEntity> page = this.selectPage(new Query<SysLogEntity>(params).getPage(), new EntityWrapper<SysLogEntity>().like(StringUtils.isNotBlank(key), "username", key));

		return new PageUtils(page);
	}
}
