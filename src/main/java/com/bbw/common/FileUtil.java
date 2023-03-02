package com.bbw.common;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

/**
 * 文件工具类
 * 
 * @author suhq
 * @date 2019年3月29日 上午11:29:45
 */
@Slf4j
public class FileUtil {

	/**
	 * 读取文件的行数，不包含空行 <br/>
	 * 文件不存在返回empty集合
	 * 
	 * @param name
	 * @return
	 */
	public static List<String> readFileLines(String name) {
		URI uri = getFileURI(name);
		if (uri == null) {
			log.error(name + "不存在或者是一个路径");
			return new ArrayList<>();
		}
		return readFileLines(uri);
	}

	public static List<String> readFileLines(URI uri) {
		Path path = Paths.get(uri);
		if (!Files.exists(path) || Files.isDirectory(path)) {
			log.error(uri.getPath() + "不存在或者是一个路径");
			return new ArrayList<>();
		}
		try {
			return Files.readAllLines(path);
		} catch (IOException e) {
			log.error(uri.getPath() + "文件读取失败。错误信息：" + e.getMessage());
		}
		return new ArrayList<>();
	}

	/**
	 * 追加文本
	 * @param fileName 全路径
	 * @param content
	 */
	public static void appendFileLines(String fileName,String content) {
		Path path = Paths.get(fileName);
		try{
			if(!Files.exists(path)){
				Files.createFile(path);
			}
			content += System.getProperty("line.separator");
			Files.write(path,content.getBytes(), StandardOpenOption.APPEND);
		}catch (IOException e){
			e.printStackTrace();
		}

	}

	/**
	 * 追加文本
	 * @param fileName 全路径
	 * @param contents
	 */
	public static void appendFileLines(String fileName, Collection<String> contents) {
		if (null == contents || contents.size() == 0) {
			return;
		}
		Path path = Paths.get(fileName);
		try{
			if(!Files.exists(path)){
				Files.createFile(path);
			}
			String lines = "";
			for (String content : contents) {
				lines += content + System.getProperty("line.separator");
			}

			Files.write(path,lines.getBytes(), StandardOpenOption.APPEND);
		}catch (IOException e){
			e.printStackTrace();
		}

	}

	/**
	 * 获取文件uri，使用uri兼容windows
	 * 
	 * @param name
	 * @return
	 */
	private static URI getFileURI(String name) {
		try {
			URI uri = FileUtil.class.getClassLoader().getResource(name).toURI();
			return uri;
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return null;
	}

}
