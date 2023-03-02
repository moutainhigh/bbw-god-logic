package com.bbw.common;

import java.io.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-01-10 17:57
 */
public class ZipUtil {

    private ZipUtil() {
    }

    public static void doCompress(String srcFile, String zipFile) throws IOException {
        doCompress(new File(srcFile), new File(zipFile));
    }

    /**
     * 文件压缩
     *
     * @param srcFile 目录或者单个文件
     * @param zipFile 压缩后的ZIP文件
     */
    public static void doCompress(List<File> srcFiles, File zipFile) throws IOException {
        ZipOutputStream out = null;
        try {
            out = new ZipOutputStream(new FileOutputStream(zipFile));
            for (File srcFile : srcFiles) {
                doCompress(srcFile, out);
            }
        } catch (Exception e) {
            throw e;
        } finally {
            if (null != out) {
                out.close();//记得关闭资源
            }
        }
    }

    /**
     * 文件压缩
     *
     * @param srcFile 目录或者单个文件
     * @param zipFile 压缩后的ZIP文件
     */
    public static void doCompress(File srcFile, File zipFile) throws IOException {
        ZipOutputStream out = null;
        try {
            out = new ZipOutputStream(new FileOutputStream(zipFile));
            doCompress(srcFile, out);
        } catch (Exception e) {
            throw e;
        } finally {
            if (null != out) {
                out.close();//记得关闭资源
            }
        }
    }

    public static void doCompress(String filelName, ZipOutputStream out) throws IOException {
        doCompress(new File(filelName), out);
    }

    public static void doCompress(File file, ZipOutputStream out) throws IOException {
        doCompress(file, out, "");
    }

    public static void doCompress(File inFile, ZipOutputStream out, String dir) throws IOException {
        if (inFile.isDirectory()) {
            File[] files = inFile.listFiles();
            if (files != null && files.length > 0) {
                for (File file : files) {
                    String name = inFile.getName();
                    if (!"".equals(dir)) {
                        name = dir + "/" + name;
                    }
                    ZipUtil.doCompress(file, out, name);
                }
            }
        } else {
            ZipUtil.doZip(inFile, out, dir);
        }
    }

    public static void doZip(File inFile, ZipOutputStream out, String dir) throws IOException {
        String entryName = null;
        if (!"".equals(dir)) {
            entryName = dir + "/" + inFile.getName();
        } else {
            entryName = inFile.getName();
        }
        ZipEntry entry = new ZipEntry(entryName);
        out.putNextEntry(entry);

        int len = 0;
        byte[] buffer = new byte[1024];
        FileInputStream fis = new FileInputStream(inFile);
        while ((len = fis.read(buffer)) > 0) {
            out.write(buffer, 0, len);
            out.flush();
        }
        out.closeEntry();
        fis.close();
    }

//	public static void main(String[] args) throws IOException {
//		doCompress("D:/java/", "D:/java.zip");
//	}

    /**
     * 将数据直接写入创建的ZIP文件内，过程中不再产生原始文件，最终只有一个zip
     *
     * @param dozipPath
     * @param itemFilePath
     * @param data
     * @throws IOException
     */
    public static void createZipSkipFile(String dozipPath, String itemFilePath, String data) {
        List<String> itemFilePaths = new ArrayList<String>();
        itemFilePaths.add(itemFilePath);
        List<String> datas = new ArrayList<String>();
        datas.add(data);
        try {
            createZipSkipFile(dozipPath, itemFilePaths, datas);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 将多组数据 批量加入到创建的zip文件内，过程中不产生单个文件，最终只有一个zip
     * <br><fonte color="red">文件名集合大小 与 数据集合大小需保持一致，否则缺少部分不会加入到zip中 </fonte>
     *
     * @param dozipPath
     * @param itemFilePaths
     * @param datas
     * @throws IOException
     * @throws InterruptedException
     */
    public static void createZipSkipFile(String dozipPath, List<String> itemFilePaths, List<String> datas) throws IOException, InterruptedException {
        ZipOutputStream zipout = null;
        int end = itemFilePaths.size() > datas.size() ? datas.size() : itemFilePaths.size();
        try {
            zipout = new ZipOutputStream(new FileOutputStream(dozipPath));
            for (int i = 0; i < end; i++) {
                zipout.putNextEntry(new ZipEntry(itemFilePaths.get(i)));
                zipout.write(datas.get(i).getBytes());
            }
        } finally {
            if (zipout != null) {
                zipout.closeEntry();
                zipout.close();
            }

        }
    }

    /**
     * 将多组数据 批量加入到创建的zip文件内，过程中不产生单个文件，最终只有一个zip
     * <br><fonte color="red">文件名集合大小 与 数据集合大小需保持一致，否则缺少部分不会加入到zip中 </fonte>
     *
     * @param itemFilePath
     * @param data
     * @return 返回字节流数组
     */
    public static byte[] createZipBytes(String itemFilePath, String data) {
        List<String> itemFilePaths = new ArrayList<String>();
        itemFilePaths.add(itemFilePath);
        List<String> datas = new ArrayList<String>();
        datas.add(data);
        try {
            return createZipBytes(itemFilePaths, datas);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将多组数据 批量加入到创建的zip文件内，过程中不产生单个文件，最终只有一个zip
     * <br><fonte color="red">文件名集合大小 与 数据集合大小需保持一致，否则缺少部分不会加入到zip中 </fonte>
     *
     * @param itemFilePath
     * @param data
     * @return 返回字节流数组
     */
    public static byte[] createZipBytes(List<String> itemFilePaths, List<String> datas) throws IOException, InterruptedException {
        ZipOutputStream zipout = null;
        int end = itemFilePaths.size() > datas.size() ? datas.size() : itemFilePaths.size();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            zipout = new ZipOutputStream(out);
            for (int i = 0; i < end; i++) {
                zipout.putNextEntry(new ZipEntry(itemFilePaths.get(i)));
                zipout.write(datas.get(i).getBytes());
            }
        } finally {
            if (zipout != null) {
                zipout.closeEntry();
                zipout.close();
            }

        }
        return out.toByteArray();
    }

    public static void releaseZipToFile(File sourceZip, String outFileName) throws IOException {
        ZipFile zfile = new ZipFile(sourceZip);
        Enumeration zList = zfile.entries();
        ZipEntry ze = null;
        byte[] buf = new byte[1024];
        while (zList.hasMoreElements()) {
            //从ZipFile中得到一个ZipEntry
            ze = (ZipEntry) zList.nextElement();
            if (ze.isDirectory()) {
                continue;
            }
            //以ZipEntry为参数得到一个InputStream，并写到OutputStream中
            OutputStream os = new BufferedOutputStream(new FileOutputStream(outFileName));
            InputStream is = new BufferedInputStream(zfile.getInputStream(ze));
            int readLen = 0;
            while ((readLen = is.read(buf, 0, 1024)) != -1) {
                os.write(buf, 0, readLen);
            }
            is.close();
            os.close();
        }
        zfile.close();
    }
}