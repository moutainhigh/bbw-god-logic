package com.bbw.god.db.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.bbw.common.DateUtil;
import com.bbw.god.db.entity.InsReceiptEntity;
import com.bbw.god.db.pool.LogicDataDao;

/**
 * 区服产品下发操作类 使用以下语句获取bean 
 * LogicInsReceiptService logicInsReceiptService = SpringContextUtil.getBean(LogicInsReceiptService.class, sid);
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-04-30 14:30
 */
@Service
@Scope("prototype")
@Lazy
public class LogicInsReceiptService implements LogicDataDao {
	private int sid;
	@Autowired
	private InsReceiptService insReceiptService;

	@SuppressWarnings("unused")
	private LogicInsReceiptService() {

	}

	public LogicInsReceiptService(int sid) {
		this.sid = sid;
	}

	@Override
	public int getServerId() {
		return sid;
	}

	public List<InsReceiptEntity> dbGetAll() {
		return insReceiptService.selectList(new EntityWrapper<InsReceiptEntity>());
	}

	public boolean dbDeleteNoPay() {
		Date today = DateUtil.getDateBegin(DateUtil.now());
		return insReceiptService.delete(new EntityWrapper<InsReceiptEntity>().where("status=0 AND purchase_date<'" + DateUtil.toDateTimeString(today) + "'"));
	}

	public List<InsReceiptEntity> dbGetSuccess() {
		return insReceiptService.selectList(new EntityWrapper<InsReceiptEntity>().eq("status", "1"));
	}

	public boolean dbUpdateBatchById(List<InsReceiptEntity> entityList) {
		return insReceiptService.updateBatchById(entityList);
	}

	public boolean dbUpdateById(InsReceiptEntity entity) {
		return insReceiptService.updateById(entity);
	}

}
