package com.bbw.god.game.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationObserver;

import java.io.File;

/**
 * 文件变化监听器
 * <p>
 * 在Apache的Commons-IO中有关于文件的监控功能的代码. 文件监控的原理如下： 由文件监控类FileAlterationMonitor中的线程不停的扫描文件观察器FileAlterationObserver，
 * 如果有文件的变化，则根据相关的文件比较器，判断文件时新增，还是删除，还是更改。（默认为1000毫秒执行一次扫描）
 */
@Slf4j
public class FileListener extends FileAlterationListenerAdaptor {

    private boolean isConfigFile(File file) {
        if (file.getName().endsWith(".yml")) {
            return true;
        }
        return false;
    }

    /**
     * 文件创建执行
     */
    public void onFileCreate(File file) {
        if (isConfigFile(file)) {
            log.info("[新建]:" + file.getAbsolutePath());
            Class<? extends CfgInterface> clazz = FileConfigDao.getDirClass(file.getPath(), file.getParent());
            Cfg.I.get(clazz);
        }
    }

    /**
     * 文件创建修改
     */
    public void onFileChange(File file) {
        try {
            if (isConfigFile(file)) {
                log.info("[修改]:" + file.getAbsolutePath());
                System.out.println("[修改]:" + file.getAbsolutePath());
                String filePath = file.getPath().replaceAll("\\\\", "/");
                String parentPath = file.getParent().replaceAll("\\\\", "/");
                Class<? extends CfgInterface> clazz = FileConfigDao.getDirClass(filePath, parentPath);
                Cfg.I.reloadWithoutClear(clazz);
                Cfg.I.prepareList();
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            e.printStackTrace();
        }

    }

    /**
     * 文件删除
     */
    public void onFileDelete(File file) {
        if (isConfigFile(file)) {
            log.info("[修改]:" + file.getAbsolutePath());
            System.out.println("[修改]:" + file.getAbsolutePath());
            Class<? extends CfgInterface> clazz = FileConfigDao.getDirClass(file.getPath(), file.getParent());
            Cfg.I.reload(clazz);
        }
    }

    /**
     * 目录创建
     */
    public void onDirectoryCreate(File directory) {
        log.info("[新建]:" + directory.getAbsolutePath());
    }

    /**
     * 目录修改
     */
    public void onDirectoryChange(File directory) {
        log.info("[修改]:" + directory.getAbsolutePath());
    }

    /**
     * 目录删除
     */
    public void onDirectoryDelete(File directory) {
        log.info("[删除]:" + directory.getAbsolutePath());
    }

    public void onStart(FileAlterationObserver observer) {
        super.onStart(observer);
    }

    public void onStop(FileAlterationObserver observer) {
        super.onStop(observer);
    }

}