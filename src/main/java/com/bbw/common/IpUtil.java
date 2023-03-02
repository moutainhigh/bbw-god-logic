package com.bbw.common;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2018年8月26日 上午10:47:20
 */
public class IpUtil {
	private static final Logger LOGGER = LoggerFactory.getLogger(IpUtil.class);

	public static String getIpAddr(HttpServletRequest request) {
		String ipAddress = "0.0.0.0";
		try {
			ipAddress = request.getHeader("x-forwarded-for");
			if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
				ipAddress = request.getHeader("Proxy-Client-IP");
			}
			if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
				ipAddress = request.getHeader("WL-Proxy-Client-IP");
			}
			if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
				ipAddress = request.getRemoteAddr();
				if (ipAddress.equals("127.0.0.1")) {
					try {
						// 根据网卡取本机配置的IP
						InetAddress inet = InetAddress.getLocalHost();
						ipAddress = inet.getHostAddress();
					} catch (UnknownHostException e) {
						LOGGER.error(e.getMessage(), e);
					}
				}
			}
			// 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
			if (ipAddress != null && ipAddress.length() > 15) { // "***.***.***.***".length()
																// = 15
				if (ipAddress.indexOf(",") > 0) {
					ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
				}
			}
		} catch (Exception e) {
			ipAddress = "0.0.0.0";
		}
		// ipAddress = this.getRequest().getRemoteAddr();

		return ipAddress;
	}

	/**
	 * 获取本机的ip4地址
	 * 
	 * @return
	 */
	public static String getInet4Address() {
		Enumeration<NetworkInterface> nis;
		String ip = "192.168.0.1";
		try {
			nis = NetworkInterface.getNetworkInterfaces();
			for (; nis.hasMoreElements();) {
				NetworkInterface ni = nis.nextElement();
				Enumeration<InetAddress> ias = ni.getInetAddresses();
				for (; ias.hasMoreElements();) {
					InetAddress ia = ias.nextElement();
					// ia instanceof Inet6Address && !ia.equals("")
					if (ia instanceof Inet4Address && !ia.getHostAddress().equals("127.0.0.1")) {
						ip = ia.getHostAddress();
					}
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}
		return ip;
	}

	/**
	 * ip地址转成long型数字 将IP地址转化成整数的方法如下： 1、通过String的split方法按.分隔得到4个长度的数组 2、通过左移位操作（<<）给每一段的数字加权，第一段的权为2的24次方，第二段的权为2的16次方，第三段的权为2的8次方，最后一段的权为1
	 * 
	 * @param strIp
	 * @return
	 */
	public static long ipToLong(String strIp) {
		String[] ip = strIp.split("\\.");
		return (Long.parseLong(ip[0]) << 24) + (Long.parseLong(ip[1]) << 16) + (Long.parseLong(ip[2]) << 8) + Long.parseLong(ip[3]);
	}

	/**
	 * 判断IP是否在指定IP段内
	 * 
	 * @param ip : 判断IP
	 * @param ipRange IP段（以'-'分隔）
	 * @return
	 */
	public static boolean ipInRange(String ip, String ipRange) {
		if ("*".equals(ipRange)) {
			return true;
		}
		if (!ipRange.contains("-")) {
			return ip.equals(ipRange);
		}
		int idx = ipRange.indexOf('-');
		String beginIP = ipRange.substring(0, idx);
		String endIP = ipRange.substring(idx + 1);
		return ipToLong(beginIP) <= ipToLong(ip) && ipToLong(ip) <= ipToLong(endIP);
	}

	/**
	 * 判断IP是否在指定IP段内
	 * 
	 * @param ip
	 * @param ipRanges
	 * @return
	 */
	public static boolean ipInRange(String ip, List<String> ipRanges) {
		boolean isTheIP = false;
		if (ListUtil.isNotEmpty(ipRanges)) {
			for (String ipRange : ipRanges) {
				if (ipInRange(ip, ipRange)) {
					isTheIP = true;
					break;
				}
			}
		}
		return isTheIP;
	}
}