package com.bbw.god.detail.entity;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.bbw.common.DateUtil;
import com.bbw.god.gameuser.GameUser;

import java.io.Serializable;
import java.util.Date;

/**
 * 登录明细表
 * 
 * @author shaojun
 * @email lsj@bamboowind.cn
 * @date 2018-08-26 12:39:16
 */
@TableName("god_detail.login_detail")
public class LoginDetailEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	@TableId
	private Integer id;
	/**
	 * 玩家账号。对应account.email。
	 */
	private String account;
	/**
	 * 时间
	 */
	private Date opDatetime;
	/**
	 * 设备标识
	 */
	private String deviceid;
	/**
	 * ip地址
	 */
	private String ip;
	/**
	 * 服务器id.对应server.id。允许空值。
	 */
	private Integer serverid;
	/**
	 * 渠道。对应base_plat.plat。允许空值。
	 */
	private Integer channel;

	private Long uid;

	public static LoginDetailEntity getInstance(GameUser gu, String deviceId, String ip) {
		LoginDetailEntity entity = new LoginDetailEntity();
		entity.setAccount(gu.getRoleInfo().getUserName());
		entity.setOpDatetime(DateUtil.now());
		entity.setDeviceid(deviceId);
		entity.setIp(ip);
		entity.setServerid(gu.getServerId());
		entity.setChannel(gu.getRoleInfo().getChannelId());
		entity.setUid(gu.getId());
		return entity;
	}

	/**
	 * 设置：
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * 获取：
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * 设置：玩家账号。对应account.email。
	 */
	public void setAccount(String account) {
		this.account = account;
	}

	/**
	 * 获取：玩家账号。对应account.email。
	 */
	public String getAccount() {
		return account;
	}

	/**
	 * 设置：时间
	 */
	public void setOpDatetime(Date opDatetime) {
		this.opDatetime = opDatetime;
	}

	/**
	 * 获取：时间
	 */
	public Date getOpDatetime() {
		return opDatetime;
	}

	/**
	 * 设置：设备标识
	 */
	public void setDeviceid(String deviceid) {
		this.deviceid = deviceid;
	}

	/**
	 * 获取：设备标识
	 */
	public String getDeviceid() {
		return deviceid;
	}

	/**
	 * 设置：ip地址
	 */
	public void setIp(String ip) {
		this.ip = ip;
	}

	/**
	 * 获取：ip地址
	 */
	public String getIp() {
		return ip;
	}

	/**
	 * 设置：服务器id.对应server.id。允许空值。
	 */
	public void setServerid(Integer serverid) {
		this.serverid = serverid;
	}

	/**
	 * 获取：服务器id.对应server.id。允许空值。
	 */
	public Integer getServerid() {
		return serverid;
	}

	/**
	 * 设置：渠道。对应base_plat.plat。允许空值。
	 */
	public void setChannel(Integer channel) {
		this.channel = channel;
	}

	/**
	 * 获取：渠道。对应base_plat.plat。允许空值。
	 */
	public Integer getChannel() {
		return channel;
	}

	public Long getUid() {
		return uid;
	}

	public void setUid(Long uid) {
		this.uid = uid;
	}

	@Override
	public String toString() {
		return "LoginDetailEntity [id=" + id + ", account=" + account + ", opDatetime=" + opDatetime + ", deviceid=" + deviceid + ", ip=" + ip + ", serverid=" + serverid + ", channel=" + channel + ", uid=" + uid + "]";
	}

}
