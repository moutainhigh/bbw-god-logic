package com.bbw.god.detail.service;

import com.baomidou.mybatisplus.service.IService;
import com.bbw.db.PageUtils;
import com.bbw.god.detail.entity.LoginDetailEntity;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 充值明细表
 *
 * @author shaojun
 * @email lsj@bamboowind.cn
 * @date 2018-08-26 12:06:02
 */
public interface LoginDetailService extends IService<LoginDetailEntity> {

    PageUtils queryPage(Map<String, Object> params);

	/**
	 * 获取时间段内，某个区服下所有登陆的玩家信息，根据uid分组
	 *
	 * @param start
	 * @param end
	 * @param startDate
	 * @param endDate
	 * @param sid
	 * @return
	 */
    List<LoginDetailEntity> getList(String start,String end,String startDate,String endDate,Integer sid);

	/**
	 * 获取数据用于插入在新服月统计的表中
	 *
	 * @param date
	 * @return
	 */
	List<LoginDetailEntity> getSidAndCid(Integer date);

	/**
	 * 获取数据用于插入在新服月统计的表中
	 *
	 * @param sid
	 * @param cid
	 * @param date
	 * @return
	 */
    Map<String,Object> getloginData(Integer sid,Integer cid,Integer date);

	/**
	 * 获取区服自开服以来的新增设备总数
	 *
	 * @param sid
	 * @param cid
	 * @param endDate
	 * @return
	 */
    Integer getAddDev(Integer sid,Integer cid,Integer endDate);

	/**
	 * 获取某个区服的某个渠道下的当日新增设备数据
	 *
	 * @param sid
	 * @param cid
	 * @param date
	 * @return
	 */
    List<Map<String,Object>> getNewDev(Integer sid,Integer cid,Integer date);

	/**
	 * 根据uid获取最近的登录时间（2019-01-01后）
	 *
	 * @param uid
	 * @return
	 */
	Date getLatestLoginTimeByUid(Long uid);

	/**
	 * 根据区服id和日期获取所有在该区服当日登录的所有玩家id集合
	 *
	 * @param sid
	 * @param date
	 * @return
	 */
	Set<Long> getUidBySidAndDate(Integer sid, String date);

	/**
	 * 根据日期获取所有在该区服当日登录的所有玩家id集合
	 *
	 * @param date YYYY-MM-DD
	 * @return
	 */
	Set<Long> getUidByDate(String date);

	/**
	 * 获取时间段内登陆过的玩家
	 *
	 * @param start
	 * @param end
	 * @return
	 */
	Set<Long> getUidBetweenDate(String start, String end);
}

