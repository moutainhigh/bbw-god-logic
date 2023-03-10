/**
 * Project Name:mobpush-api-java-client
 * File Name:MobPushClient.java
 * Package Name:mob.push.api
 * Date: 2018年2月2日
 * Time: 下午6:11:41
 *
*/

package com.bbw.mc.push.mob.api;

import java.util.List;

import com.bbw.mc.push.mob.api.area.AreaClient;
import com.bbw.mc.push.mob.api.device.DeviceClient;
import com.bbw.mc.push.mob.api.exception.ApiException;
import com.bbw.mc.push.mob.api.model.Area;
import com.bbw.mc.push.mob.api.model.PushStats;
import com.bbw.mc.push.mob.api.model.PushWork;
import com.bbw.mc.push.mob.api.push.PushClient;
import com.bbw.mc.push.mob.api.stats.StatsClient;

/**
 * ClassName:MobPushClient Date: 2018年2月2日 Time: 下午6:11:41
 * 
 * @author hlliu
 */
public class MobPushClient {

	PushClient _pushClient = new PushClient();

	StatsClient _statsClient = new StatsClient();

	DeviceClient _deviceClient = new DeviceClient();

	AreaClient _areaClient = new AreaClient();

	/**
	 * 推送接口 -- 发送推送
	 * 
	 * @param pushWork
	 * @return batchId 创建ID
	 * @throws ApiException
	 */
	public String push(PushWork pushWork) throws ApiException {
		return _pushClient.sendPush(pushWork);
	}

	/**
	 * 推送接口 -- 查询推送 根据BatchId 根据创建id查询推送消息详情
	 * 
	 * @param batchId
	 * @return PushWork
	 * @throws ApiException
	 */
	public PushWork pushById(String batchId) throws ApiException {
		return _pushClient.getPushByBatchId(batchId);
	}

	/**
	 * 推送接口 -- 查询推送 根据workno 根据自定义编号查询消息详情
	 * 
	 * @param workno
	 * @return PushWork
	 * @throws ApiException
	 */
	public PushWork pushByWorkno(String workno) throws ApiException {
		return _pushClient.getPushByWorkno(workno);
	}

	/**
	 * 推送统计-- 查询推送统计 根据创建id查询推送统计
	 * 
	 * @param batchId
	 * @return PushStats
	 * @throws ApiException
	 */
	public PushStats statsById(String batchId) throws ApiException {
		return _statsClient.getStatsByBatchId(batchId);
	}

	/**
	 * 推送统计-- 查询推送统计 根据自定义编号查询推送统计
	 * 
	 * @param workno
	 * @return PushStats
	 * @throws ApiException
	 */
	public PushStats statsByWorkno(String workno) throws ApiException {
		return _statsClient.getStatsByWorkno(workno);
	}

	/**
	 * 查询标签 根据设备registrationId查询标签信息
	 * 
	 * @param registrationId
	 * @return tags 标签集合
	 * @throws ApiException
	 */
	public String[] tagsByRegistrationId(String registrationId) throws ApiException {
		return _deviceClient.getDeviceTags(registrationId);
	}

	/**
	 * 设备绑定标签
	 * 
	 * @param tags
	 * @param registrationId
	 * @return int （仅200表示成功）
	 * @throws ApiException
	 */
	public int tagsAdd(String[] tags, String registrationId) throws ApiException {
		return _deviceClient.addDeviceTags(tags, registrationId);
	}

	/**
	 * 删除指定设备标签
	 * 
	 * @param tags
	 * @param registrationId
	 * @return int （仅200表示成功）
	 * @throws ApiException
	 */
	public int tagsRemove(String[] tags, String registrationId) throws ApiException {
		return _deviceClient.removeDeviceTags(tags, registrationId);
	}

	/**
	 * 清除指定标签
	 * 
	 * @param registrationId
	 * @return tags 标签
	 * @throws ApiException
	 */
	public int tagsClean(String registrationId) throws ApiException {
		return _deviceClient.cleanDeviceTags(registrationId);
	}

	/**
	 * 获取指定设备别名
	 * 
	 * @param registrationId
	 * @return alias 别名
	 * @throws ApiException
	 */
	public String aliasByRegistrationId(String registrationId) throws ApiException {
		return _deviceClient.getDeviceAlias(registrationId);
	}

	/**
	 * 设备绑定别名
	 * 
	 * @param alias
	 * @param registrationId
	 * @return int （仅200表示成功）
	 * @throws ApiException
	 */
	public int setAlias(String alias, String registrationId) throws ApiException {
		return _deviceClient.setDeviceAlias(alias, registrationId);
	}

	/**
	 * 清除设备别名
	 * 
	 * @param registrationId
	 * @return int （仅200表示成功）
	 * @throws ApiException
	 */
	public int cleanAlias(String registrationId) throws ApiException {
		return _deviceClient.cleanDeviceAlias(registrationId);
	}

	/**
	 * 获取地理位置列表 -- 中国下省份列表
	 * 
	 * @return List<Area>
	 * @throws ApiException
	 */
	public List<Area> area() throws ApiException {
		return _areaClient.getArea();
	}

	/**
	 * 获取地理位置列表 -- 子级列表
	 * 
	 * @param parentId
	 * @return List<Area>
	 * @throws ApiException
	 */
	public List<Area> area(String parentId) throws ApiException {
		return _areaClient.getArea(parentId);
	}
}
