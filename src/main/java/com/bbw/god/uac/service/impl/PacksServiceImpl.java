package com.bbw.god.uac.service.impl;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.bbw.god.Const;
import com.bbw.god.uac.GodUACCache;
import com.bbw.god.uac.dao.PacksDao;
import com.bbw.god.uac.entity.PacksEntity;
import com.bbw.god.uac.service.PacksService;
import com.bbw.db.PageUtils;
import com.bbw.db.Query;

@Service("packsService")
public class PacksServiceImpl extends ServiceImpl<PacksDao, PacksEntity> implements PacksService {
	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		Page<PacksEntity> page = this.selectPage(new Query<PacksEntity>(params).getPage(), new EntityWrapper<PacksEntity>());

		return new PageUtils(page);
	}

	/*
	 * (non-Javadoc)
	 * @see com.bbw.god.uc.service.PacksService#getPackByType(int)
	 */
	@Override
	public PacksEntity getPackByType(int type) {
		PacksEntity packsEntity = selectOne(new EntityWrapper<PacksEntity>().eq("type", type));
		return packsEntity;
	}

	/*
	 * (non-Javadoc)
	 * @see com.bbw.god.uc.service.PacksService#getWechatBindedPack()
	 */
	@Override
	public PacksEntity getWechatBindedPack() {
		return getPackByType(Const.Packs.TYPE_WECHAT_BINDED);
	}

	/*
	 * (non-Javadoc)
	 * @see com.bbw.god.uc.service.PacksService#getWechatWeeklyPack()
	 */
	@Override
	public PacksEntity getWechatWeeklyPack() {
		return getPackByType(Const.Packs.TYPE_WECHAT_WEEKLY);
	}

	/*
	 * (non-Javadoc)
	 * @see com.bbw.god.uc.service.PacksService#fetchOne(int)
	 */
	@Override
	public PacksEntity fetchOne(int id) {
		//从缓存中获取对象
		for (PacksEntity entity : GodUACCache.packsCache) {
			if (id == entity.getId().intValue()) {
				return entity;
			}
		}
		//如果缓存中没有，则从数据库获取
		PacksEntity entity = this.selectById(id);
		if (null != entity) {
			GodUACCache.add(GodUACCache.packsCache,entity);
		}
		return entity;
	}

}
