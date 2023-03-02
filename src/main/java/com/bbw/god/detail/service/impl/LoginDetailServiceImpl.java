package com.bbw.god.detail.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.bbw.common.DateUtil;
import com.bbw.common.MapUtil;
import com.bbw.db.PageUtils;
import com.bbw.db.Query;
import com.bbw.god.db.entity.CfgServerEntity;
import com.bbw.god.detail.dao.LoginDetailDao;
import com.bbw.god.detail.entity.LoginDetailEntity;
import com.bbw.god.detail.service.LoginDetailService;
import com.bbw.god.game.config.server.ServerTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Service("loginDetailService")
public class LoginDetailServiceImpl extends ServiceImpl<LoginDetailDao, LoginDetailEntity> implements LoginDetailService {
	@Autowired
	private LoginDetailDao loginDetailDao;

	@Autowired
	private JdbcTemplate jdbc;

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		Page<LoginDetailEntity> page = this.selectPage(new Query<LoginDetailEntity>(params).getPage(), new EntityWrapper<LoginDetailEntity>());

		return new PageUtils(page);
	}

	@Override
	public List<LoginDetailEntity> getList(String start, String end, String startDate, String endDate,Integer sid) {
		return loginDetailDao.getListBetweenTime(start, end, startDate, endDate,sid);
	}

	@Override
	public List<LoginDetailEntity> getSidAndCid(Integer date) {
		Date fromDateInt = DateUtil.fromDateInt(date);
		String start = DateUtil.toDateTimeString(DateUtil.toDate(fromDateInt, "00:00:00"));
		String end = DateUtil.toDateTimeString(DateUtil.toDate(fromDateInt, "23:59:59"));
		String sql = "SELECT serverid,channel FROM `god_detail`.`login_detail` WHERE op_datetime BETWEEN '" + start + "' and '" + end + "' and serverid is not null and deviceid != 'IOS_USER' and deviceid != 'nodevice' and uid > 100000000000000 and channel in (77000,78000,79000) GROUP BY serverid,channel ";
		List<LoginDetailEntity> Loginlist = jdbc.query(sql, new Object[]{}, new BeanPropertyRowMapper<>(LoginDetailEntity.class));
		return Loginlist;
	}

	@Override
	public Map<String, Object> getloginData(Integer sid, Integer cid, Integer date) {
		Date fromDateInt = DateUtil.fromDateInt(date);
		String start = DateUtil.toDateTimeString(DateUtil.toDate(fromDateInt, "00:00:00"));
		String end = DateUtil.toDateTimeString(DateUtil.toDate(fromDateInt, "23:59:59"));
		String sql = "SELECT count(DISTINCT account) as login_ac_num,count(DISTINCT deviceid) as login_dev_num FROM `god_detail`.`login_detail` WHERE op_datetime BETWEEN '" + start + "' and '" + end + "' and serverid is not null and deviceid != 'IOS_USER' and deviceid != 'nodevice' and uid > 100000000000000 and serverid =" + sid + " and channel = " + cid + " GROUP BY serverid,channel ";
		return jdbc.queryForMap(sql);
	}

	@Override
	public Integer getAddDev(Integer sid, Integer cid, Integer endDate) {
		Date beginTime;
		CfgServerEntity server = ServerTool.getServer(sid);
		if (server != null) {
			beginTime = server.getBeginTime();
		} else {
			beginTime = DateUtil.fromDateInt(20160101);
		}
		String start = DateUtil.toDateTimeString(beginTime);
		String end = DateUtil.toDateTimeString(DateUtil.fromDateInt(endDate));
		String sql = "SELECT count(DISTINCT deviceid) as add_dev FROM `god_detail`.`login_detail` WHERE op_datetime BETWEEN '" + start + "' and '" + end + "' and serverid is not null and deviceid != 'IOS_USER' and deviceid != 'nodevice' and uid > 100000000000000 and serverid =" + sid + " and channel = " + cid + " GROUP BY serverid,channel ";
		Integer addDev = 0;
		List<Map<String, Object>> list = jdbc.queryForList(sql);
		if (list.size() > 0 && MapUtil.isNotEmpty(list.get(0)) && list.get(0).get("add_dev") != null) {
			addDev = Integer.parseInt(((Long) jdbc.queryForMap(sql).get("add_dev")).toString());
		}
		return addDev;
	}

	@Override
	public List<Map<String, Object>> getNewDev(Integer sid, Integer cid, Integer date) {
		String dateTimeString = DateUtil.toDateTimeString(DateUtil.fromDateInt(date));
		date = Integer.parseInt(date.toString().substring(2));
		String sql = "SELECT account FROM `god_detail`.`login_detail` WHERE serverid is not null and deviceid != 'IOS_USER' and deviceid != 'nodevice' and uid > 100000000000000 and uid like '%" + date + "%' and serverid =" + sid + " and channel = " + cid + " GROUP BY serverid,channel,deviceid ";
		return jdbc.queryForList(sql);
	}

	@Override
	public Date getLatestLoginTimeByUid(Long uid) {
		EntityWrapper<LoginDetailEntity> wrapper = new EntityWrapper<>();
		wrapper.setSqlSelect("max(op_datetime) as op_datetime").eq("uid",uid).ge("op_datetime","2019-01-01");
		if (this.selectOne(wrapper) != null) {
			return this.selectOne(wrapper).getOpDatetime();
		}
		return DateUtil.fromDateInt(20190101);
	}

	@Override
	public Set<Long> getUidBySidAndDate(Integer sid, String date) {
		EntityWrapper<LoginDetailEntity> wrapper = new EntityWrapper<>();
		wrapper.eq("serverid",sid).like("op_datetime",date);
		List<LoginDetailEntity> list = this.selectList(wrapper);
		Set<Long> uids = new HashSet<>();
		list.forEach(s->uids.add(s.getUid()));
		return uids;
	}

	@Override
	public Set<Long> getUidByDate(String date) {
		EntityWrapper<LoginDetailEntity> wrapper = new EntityWrapper<>();
		wrapper.like("op_datetime", date).ne("uid", 0);
		List<LoginDetailEntity> list = this.selectList(wrapper);
		Set<Long> uids = new HashSet<>();
		list.forEach(s -> uids.add(s.getUid()));
		return uids;
	}

	@Override
	public Set<Long> getUidBetweenDate(String start, String end) {
		EntityWrapper<LoginDetailEntity> wrapper = new EntityWrapper<>();
		wrapper.between("op_datetime", start, end).ne("uid", 0);
		List<LoginDetailEntity> list = this.selectList(wrapper);
		Set<Long> uids = new HashSet<>();
		list.forEach(s -> uids.add(s.getUid()));
		return uids;
	}
}
