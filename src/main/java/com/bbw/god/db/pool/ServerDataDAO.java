package com.bbw.god.db.pool;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.bbw.god.db.entity.InsServerDataEntity;
import com.bbw.god.db.service.InsServerDataService;

import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 * 区服操作类
 * 使用一下语句获取bean
 *   ServerDataDAO sdd = SpringContextUtil.getBean(ServerDataDAO.class, serverId);
 * </pre>
 * 
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2018-10-21 22:05
 */
@Slf4j
@Service
@Scope("prototype")
@Lazy
public class ServerDataDAO {
	private static int batchSize = 1000;
	private int serverId;
	@Autowired
	private InsServerDataService insServerDataService;

	@SuppressWarnings("unused")
	private ServerDataDAO() {

	}

	public ServerDataDAO(int serverId) {
		this.serverId = serverId;
	}

	public int getServerId() {
		return serverId;
	}

	/**
	 * 根据数据类型载入所有数据
	 * 
	 * @param uid
	 * @param dataType
	 * @return
	 */
	public List<InsServerDataEntity> dbSelectServerDataByType(String dataType) {
		EntityWrapper<InsServerDataEntity> wrapper = new EntityWrapper<>();
		wrapper.where("sid = {0} AND data_type={1}", this.getServerId(), dataType);
		return insServerDataService.selectList(wrapper);
	}

	public List<InsServerDataEntity> dbSelectServerDataByType(int sid, String dataType) {
		EntityWrapper<InsServerDataEntity> wrapper = new EntityWrapper<>();
		wrapper.where("sid = {0} AND data_type={1}", sid, dataType);
		return insServerDataService.selectList(wrapper);
	}

	public List<InsServerDataEntity> dbInsertServerDataBatch(List<InsServerDataEntity> resList) {
		try {
			// insServerDataService.insertBatch(resList, batchSize);
			insServerDataService.insertOrUpdateBatch(resList, batchSize);
			return new ArrayList<>();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			log.error("-------------ins_server_data  Insert失败，逐条保存！-------------");
			// 保存失败则逐条保存
			List<InsServerDataEntity> failList = new ArrayList<>();
			for (InsServerDataEntity entity : resList) {
				try {
					boolean b = insServerDataService.insertOrUpdate(entity);
					if (!b) {
						failList.add(entity);
						log.error(entity.toString());
					}
				} catch (Exception ee) {
					if (!ee.getMessage().contains("Duplicate entry")) {
						failList.add(entity);
					}
					log.error(entity.toString());
					log.error(ee.getMessage(), ee);
				}
			}
			return failList;
		}
	}

	public List<InsServerDataEntity> dbUpdateServerDataBatch(List<InsServerDataEntity> resList) {
		try {
			insServerDataService.updateBatchById(resList, batchSize);
			return new ArrayList<>();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			log.error("-------------ins_server_data  update 失败，逐条保存！-------------");
			// 保存失败则逐条保存
			List<InsServerDataEntity> failList = new ArrayList<>();
			for (InsServerDataEntity entity : resList) {
				try {
					boolean b = insServerDataService.updateById(entity);
					if (!b) {
						failList.add(entity);
						log.error(entity.toString());
					}
				} catch (Exception ee) {
					failList.add(entity);
					log.error(entity.toString());
					log.error(ee.getMessage(), ee);
				}
			}
			return failList;
		}
	}

	public boolean dbDeleteServerDataBatch(Collection<Long> resKeyList) {
		return insServerDataService.deleteBatchIds(resKeyList);
	}
}
