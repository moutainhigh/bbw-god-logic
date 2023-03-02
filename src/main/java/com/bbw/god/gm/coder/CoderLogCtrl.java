package com.bbw.god.gm.coder;

import com.bbw.common.*;
import com.bbw.god.gm.rd.RDLog;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.OutputStream;
import java.util.*;

/**
 * 程序员日志接口
 *
 * @author suhq
 * @date 2020-10-27 10:17
 **/
@Slf4j
@RestController
@RequestMapping(value = "/coder")
public class CoderLogCtrl {
    private static String logic1_ip = "172.18.128.5";
    private static String logic2_ip = "172.18.128.6";
    private static Map<String, String> allFileNames = new HashMap<>();
    private static Map<String, String> mainFileNames = new HashMap<>();

    @Autowired
    private HttpServletResponse response;
    @Value("${logging.path:/www/tomcat9/logs/god}")
    private String logPath;

    @Value("${logging.log-root-path:/www/tomcat9/logs/}")
    private String rootLogPath;

    private void init() {
        if (!allFileNames.isEmpty()) {
            return;
        }
        File root = new File(logPath);
        if (!root.isDirectory()) {
            return;
        }
        File[] childField = root.listFiles();
        for (File file : childField) {
            if (file.isDirectory()) {
                String[] fileNames = file.list();
                for (String fileName : fileNames) {
                    if (fileName.indexOf(".gz") < 0 && fileName.indexOf("zip") < 0 && fileName.indexOf("20") < 0) {
                        allFileNames.put(file.getName(), file.getName() + File.separator + fileName);
                        break;
                    }
                }
            }
        }
//        allFileNames.put("error", "godlogic_error.log");

        mainFileNames.put("error", allFileNames.get("error"));
        mainFileNames.put("db", allFileNames.get("db"));
        mainFileNames.put("security", allFileNames.get("security"));
        mainFileNames.put("monitor", allFileNames.get("monitor"));
    }

    @RequestMapping("/showLogs")
    public Rst showLogs(String date) {
        init();
        Rst resRst = Rst.businessOK();
        if (StrUtil.isEmpty(date) || isToday(date)) {
            resRst.put("logs", allFileNames.values());
            return resRst;
        }
        List<String> result = new ArrayList<>();
        Set<Map.Entry<String, String>> entries = allFileNames.entrySet();
        for (Map.Entry<String, String> entry : entries) {
            File file = new File(logPath + File.separator + entry.getKey());
            if (file.exists()) {
                String[] fileNames = file.list();
                for (String fileName : fileNames) {
                    if (fileName.indexOf(date) > -1) {
                        result.add(entry.getKey() + File.separator + fileName);
                        break;
                    }
                }
            } else {
                File root = new File(logPath);
                String[] fileNames = root.list();
                for (String fileName : fileNames) {
                    if (fileName.indexOf(date) > -1) {
                        result.add(fileName);
                        break;
                    }
                }
            }
        }
        resRst.put("logs", result);
        return resRst;
    }

    @RequestMapping("/downlogs")
    public void down(String logstr, String date) {
        if (StrUtil.isBlank(logstr)) {
            return;
        }
        String ip = IpUtil.getInet4Address();
        String logicName = "logic1";
        if (ip.equals(logic2_ip)) {
            logicName = "logic2";
        }
        String[] downloadFileNames = logstr.split("##");
        OutputStream os = null;
        try {
            os = response.getOutputStream();
            String zipFileName = logicName + "." + date + ".log.zip";
            if (1 == downloadFileNames.length) {
                String[] split = downloadFileNames[0].split(File.separator);
                zipFileName = logicName + "." + split[split.length - 1];
                if (!zipFileName.contains("zip")) {
                    zipFileName += ".zip";
                }
            }
            File zipFile = new File(logPath + File.separator + zipFileName);
            ArrayList<File> srcFiles = new ArrayList<>();
            for (int i = 0; i < downloadFileNames.length; i++) {
                File tmp = new File(logPath + File.separator + downloadFileNames[i]);
                if (tmp.exists() && tmp.isFile()) {
                    srcFiles.add(tmp);
                }
            }
            // 压缩文件
            ZipUtil.doCompress(srcFiles, zipFile);
            if (!zipFile.exists() || zipFile.isDirectory()) {
                return;
            }
            response.reset();
            response.setHeader("Content-Disposition", "attachment;filename=" + zipFile.getName());
            response.setContentType("application/octet-stream; charset=utf-8");
            os.write(FileUtils.readFileToByteArray(zipFile));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            IOUtils.closeQuietly(os);
        }
    }

    /**
     * 下载指定日期的accesslog的压缩文件
     *
     * @param date 日期格式为yyyy-MM-dd
     */
    @RequestMapping("/down/accesslog")
    public void downAccessLog(String date) {
        String logicName = "accesslog";
        OutputStream os = null;
        try {
            os = response.getOutputStream();
            String zipFileName = logicName + "." + date + ".log.zip";
            File zipFile = new File(logPath + File.separator + zipFileName);
            ArrayList<File> srcFiles = new ArrayList<>();
            String tomcatPathLog = logPath.substring(0, logPath.length() - 4);
            String localhost_access_file = tomcatPathLog + File.separator + "access/localhost_access_log.%s.txt";
            File localhost_access = new File(String.format(localhost_access_file, date));
            if (localhost_access.exists() && localhost_access.isFile()) {
                srcFiles.add(localhost_access);
            }
            // 压缩文件
            ZipUtil.doCompress(srcFiles, zipFile);
            if (!zipFile.exists() || zipFile.isDirectory()) {
                return;
            }
            response.reset();
            response.setHeader("Content-Disposition", "attachment;filename=" + zipFile.getName());
            response.setContentType("application/octet-stream; charset=utf-8");
            os.write(FileUtils.readFileToByteArray(zipFile));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            IOUtils.closeQuietly(os);
        }
    }

    /**
     * 获取路径对应的列表
     *
     * @param dir
     * @return
     */
    @RequestMapping("/listLogs")
    public RDLog list(String dir) {
        RDLog rdLog = new RDLog();
        String path = rootLogPath;
        if (StrUtil.isNotBlank(dir)) {
            path += dir;
        }
        File dirFile = new File(path);
        if (!dirFile.isDirectory()) {
            return rdLog;
        }
        File[] file_list = dirFile.listFiles();
        for (File file : file_list) {
            if (file.isDirectory()) {
                rdLog.addDir(RDLog.FileInfo.instance(file.getName(), file.getAbsolutePath().substring(rootLogPath.length())));
            } else if (file.isFile()) {
                rdLog.addFile(RDLog.FileInfo.instance(file.getName(), file.getAbsolutePath().substring(rootLogPath.length())));
            }
        }
        return rdLog;
    }

    @RequestMapping("/downloadZip")
    public void downloadZip(String filePath, String fileName) {
        if (StrUtil.isBlank(filePath)) {
            return;
        }
        String srcPath = rootLogPath + filePath;
        File targetFile = new File(srcPath);
        if (!targetFile.isFile()) {
            //不是文件
            return;
        }
        OutputStream os = null;
        try {
            File zipFile = null;
            if (targetFile.getName().endsWith(".zip")) {
                zipFile = targetFile;
            } else {
                zipFile = new File(rootLogPath + fileName + ".zip");
                ZipUtil.doCompress(targetFile, zipFile);
            }
            os = response.getOutputStream();
            response.reset();
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".zip");
            response.setContentType("application/octet-stream; charset=utf-8");
            os.write(FileUtils.readFileToByteArray(zipFile));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            IOUtils.closeQuietly(os);
        }
    }

    private boolean isToday(String date) {
        Date today = DateUtil.now();
        return DateUtil.toDateString(today).equals(date);
    }
}
