package com.bbw.god.gm.coder;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bbw.App;
import com.bbw.common.DateUtil;
import com.bbw.common.ID;
import com.bbw.common.Rst;
import com.bbw.common.StrUtil;
import com.bbw.god.game.CR;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sun.tools.attach.BsdAttachProvider;
import sun.tools.attach.HotSpotVirtualMachine;
import sun.tools.attach.LinuxAttachProvider;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.Date;
import java.util.Properties;

/**
 * 程序员服务器信息接口
 *
 * @author suhq
 * @date 2020-10-27 10:17
 **/
@Slf4j
@RestController
@RequestMapping(value = "/coder")
public class CoderServerInfoCtrl {
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private App app;


    /**
     * 获取服务器系统信息
     *
     * @return
     */
    @RequestMapping(CR.Coder.GAIN_SERVER_INFO)
    public Rst serverInfo() {
        Properties props = System.getProperties();
        // java版本
        String javaVersion = props.getProperty("java.version");
        // 操作系统名称
        String osName = props.getProperty("os.name") + props.getProperty("os.version");
        // 用户的主目录
        String userHome = props.getProperty("user.home");
        // 用户的当前工作目录
        String userDir = props.getProperty("user.dir");
        // 服务器IP
        String serverIP = request.getLocalAddr();
        // 客户端IP
        String clientIP = request.getRemoteHost();
        // WEB服务器
        String webVersion = request.getServletContext().getServerInfo();
        // CPU个数
        String cpu = Runtime.getRuntime().availableProcessors() + "核";
        // 虚拟机内存总量
        String totalMemory = (Runtime.getRuntime().totalMemory() / 1024 / 1024) + "M";
        // 虚拟机空闲内存量
        String freeMemory = (Runtime.getRuntime().freeMemory() / 1024 / 1024) + "M";
        // 虚拟机使用的最大内存量
        String maxMemory = (Runtime.getRuntime().maxMemory() / 1024 / 1024) + "M";
        // 网站根目录
        String webRootPath = request.getSession().getServletContext().getRealPath("");
        Rst rst = new Rst();
        rst.put("java版本", javaVersion);
        rst.put("操作系统名称", osName);
        rst.put("userHome", userHome);
        rst.put("userDir", userDir);
        rst.put("clientIP", clientIP);
        rst.put("serverIP", serverIP);
        rst.put("cpu", cpu);
        rst.put("虚拟机内存总量", totalMemory);
        rst.put("虚拟机空闲内存量", freeMemory);
        rst.put("虚拟机使用的最大内存量", maxMemory);
        rst.put("webVersion", webVersion);
        rst.put("webRootPath", webRootPath);
        rst.put("ID生成器工作序号", ID.INSTANCE.getMachineId());
        return rst;
    }

    @RequestMapping(value = "/beanInfo")
    public JSONObject beanInfo(int minBeanNum, String clazz) {
        JSONObject js = new JSONObject();
        try {

            JSONArray jsonArray = new JSONArray();
            RuntimeMXBean bean = ManagementFactory.getRuntimeMXBean();
            String name = bean.getName();
            int index = name.indexOf('@');
            String pid = name.substring(0, index);
            // 这里要区分操作系统
            HotSpotVirtualMachine machine = null;
            if (app.runAsDev()) {
                machine = (HotSpotVirtualMachine) new BsdAttachProvider().attachVirtualMachine(pid);
            } else {
                machine = (HotSpotVirtualMachine) new LinuxAttachProvider().attachVirtualMachine(pid);
            }
            // 获取所有的bean信息
            InputStream is = machine.heapHisto("-all");
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            int readed;
            byte[] buff = new byte[1024];
            while ((readed = is.read(buff)) > 0) {
                os.write(buff, 0, readed);
            }
            is.close();
            machine.detach();
            // 输出
            String[] beanInfos = os.toString().split("\n");
            for (String beanInfo : beanInfos) {
                // 特殊的行直接返回
                if (beanInfo.length() == 0 || beanInfo.contains("--------") || beanInfo.contains("instances")) {
                    jsonArray.add(beanInfo);
                    continue;
                }
                String[] info = beanInfo.split(" ");// 9: 2086 6491736 [J
                String newBeanInfo = "";
                int i = 0;
                int beanNum = 0;
                boolean isTheClazz = true;
                for (String str : info) {
                    // 去空格
                    if (StrUtil.isBlank(str)) {
                        continue;
                    }
                    i++;
                    if (i == 2 && StrUtil.isDigit(str)) {
                        beanNum = Integer.valueOf(str);
                    }
                    if (i == 4 && StrUtil.isNotBlank(clazz) && !str.contains(clazz)) {
                        isTheClazz = false;
                    }
                    newBeanInfo += str + ",";
                }
                if (beanNum < minBeanNum) {
                    continue;
                }
                if (!isTheClazz) {
                    continue;
                }

                newBeanInfo = newBeanInfo.substring(0, newBeanInfo.length() - 1);
                jsonArray.add(newBeanInfo);
            }
            js.put("beanInfo", jsonArray);
        } catch (Exception e) {
            e.printStackTrace();
            js.put("error", e.getMessage());
        }
        return js;
    }

    private boolean isToday(String date) {
        Date today = DateUtil.now();
        return DateUtil.toDateString(today).equals(date);
    }
}
