package com.bbw.god.uac.service.impl;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.bbw.common.StrUtil;
import com.bbw.db.PageUtils;
import com.bbw.db.Query;
import com.bbw.god.uac.GodUACCache;
import com.bbw.god.uac.dao.BasePlatDao;
import com.bbw.god.uac.entity.BasePlatEntity;
import com.bbw.god.uac.service.BasePlatService;

@Service("basePlatService")
public class BasePlatServiceImpl extends ServiceImpl<BasePlatDao, BasePlatEntity> implements BasePlatService {
	private static final Logger logger = LoggerFactory.getLogger(BasePlatServiceImpl.class);

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		Page<BasePlatEntity> page = this.selectPage(new Query<BasePlatEntity>(params).getPage(), new EntityWrapper<BasePlatEntity>());

		return new PageUtils(page);
	}

	/*
	 * (non-Javadoc)
	 * @see com.bbw.god.uc.service.BasePlatService#fetchOne(int)
	 */
	@Override
	public BasePlatEntity fetchOne(int id) {
		//如果数据还未缓存，则全部载入
		if (GodUACCache.allBasePlatEntity.isEmpty()) {
			loadAll();
		}
		for (BasePlatEntity entity : GodUACCache.allBasePlatEntity) {
			if (id == entity.getId().intValue()) {
				return entity;
			}
		}
		// 缓存没有找到，直接查找数据库，并加入缓存
		BasePlatEntity entity = selectById(id);
		if (null != entity) {
			GodUACCache.add(GodUACCache.allBasePlatEntity, entity);
		}
		return null;
	}

	private void loadAll() {
		List<BasePlatEntity> all = this.selectList(new EntityWrapper<BasePlatEntity>());
		GodUACCache.addAll(GodUACCache.allBasePlatEntity, all);
		logger.debug("缓存base_plat表所有数据！");
	}

	/*
	 * (non-Javadoc)
	 * @see com.bbw.god.uac.service.BasePlatService#fetchOne(java.lang.String)
	 */
	@Override
	public BasePlatEntity fetchOne(String plat_code) {
		if (StrUtil.isNull(plat_code)) {
			return null;
		}
		//如果数据还未缓存，则全部载入
		if (GodUACCache.allBasePlatEntity.isEmpty()) {
			loadAll();
		}
		for (BasePlatEntity entity : GodUACCache.allBasePlatEntity) {
			if (entity.getPlatCode().equals(plat_code)) {
				return entity;
			}
		}
		// 缓存没有找到，直接查找数据库，并加入缓存
		BasePlatEntity entity = selectOne(new EntityWrapper<BasePlatEntity>().eq("plat_code", plat_code));
		if (null != entity) {
			GodUACCache.add(GodUACCache.allBasePlatEntity,entity);
			return entity;
		}
		logger.error("没有配置渠道plat_code=" + plat_code);
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.bbw.god.uc.service.BasePlatService#platCodeToPlatId(java.lang.String)
	 */
	@Override
	public int platCodeToPlatId(String plat_code) {
		BasePlatEntity entity = fetchOne(plat_code);
		if (null != entity) {
			return entity.getId().intValue();
		}
		return -1;
	}

}
