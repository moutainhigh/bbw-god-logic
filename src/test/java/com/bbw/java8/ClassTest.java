package com.bbw.java8;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

import org.junit.Test;

import com.bbw.common.StrUtil;

import sun.tools.attach.BsdAttachProvider;
import sun.tools.attach.HotSpotVirtualMachine;

public class ClassTest {

	@Test
	public void test() {
		// System.out.println(CfgYeDiEventEntity.class.getName());
		try {
			RuntimeMXBean bean = ManagementFactory.getRuntimeMXBean();
			String name = bean.getName();
			int index = name.indexOf('@');
			String pid = name.substring(0, index);
			// 这里要区分操作系统
			HotSpotVirtualMachine machine = (HotSpotVirtualMachine) new BsdAttachProvider().attachVirtualMachine(pid);
			InputStream is = machine.heapHisto("-all");
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			int readed;
			byte[] buff = new byte[1024];
			while ((readed = is.read(buff)) > 0) {
				os.write(buff, 0, readed);
			}

			is.close();

			machine.detach();
			String[] beanInfos = os.toString().split("\n");
			for (String beanInfo : beanInfos) {
				// System.out.println(beanInfo);
				if (beanInfo.length() == 0 || beanInfo.contains("--------")) {
					continue;
				}
				String[] info = beanInfo.split(" ");
				String newBeanInfo = "";
				int i = 0;
				int beanNum = 0;
				for (String str : info) {
					if (StrUtil.isBlank(str)) {
						continue;
					}
					i++;
					if (i == 2 && StrUtil.isDigit(str)) {
						beanNum = Integer.valueOf(str);
					}
					newBeanInfo += str + ",";
				}
				if (beanNum < 100) {
					continue;
				}
				newBeanInfo = newBeanInfo.substring(0, newBeanInfo.length() - 1);
				System.out.println(newBeanInfo);
			}

		} catch (Exception e) {
			e.printStackTrace();

		}

	}

}
