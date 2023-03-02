package com.bbw.oss;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.bbw.common.DateUtil;
import com.bbw.common.JSONUtil;
import com.bbw.god.game.combat.video.CombatVideo;
import com.bbw.god.game.wanxianzhen.WanXianTool;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * @author 作者 ：lwb
 * @version 创建时间：2019年10月10日 上午10:28:49
 * 类说明 阿里OSS连接信息
 */
@Slf4j
@Service
public class OSSService {

	/**
	 * 上传战斗视频
	 *
	 * @param combatVideo 要保存的录像
	 * @param ossPath     oss上的路径
	 * @return
	 */
	public static String uploadVideo(CombatVideo combatVideo, String ossPath) {
//		App app = SpringContextUtil.getBean(App.class);
//		if (!app.runAsProd()) {
//			return "";
//		}
		long begin = System.currentTimeMillis();
		String json = JSONUtil.toJson(combatVideo.getDatas());
		OSS oss = new OSSClientBuilder().build(OSSConfig.endpoint, OSSConfig.accessKeyId, OSSConfig.accessKeySecret);
		try {
			byte[] bytes = json.getBytes();
			@Cleanup
			InputStream inputStream = new ByteArrayInputStream(bytes);
			oss.putObject(OSSConfig.bucketName, ossPath, inputStream);
			long end = System.currentTimeMillis();
			log.info("执行战斗视频上传操作耗时：" + (end - begin) + ",地址：" + ossPath);
			return OSSConfig.downStr + ossPath;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			oss.shutdown();
		}
		return "";
	}

	/**
	 * 获取玩家保存的录像的oss路径
	 * 前面加OSSConfig.downStr才是完整地址
	 *
	 * @param combatId
	 * @return
	 */
	public static String getUserCombatOssPath(long combatId) {
		String ossPath = OSSConfig.fileRoot + "user/" + combatId + ".json";
		return ossPath;
	}

	/**
	 * 获取4级5级攻城和振兴战斗录像的oss路径
	 * 前面加OSSConfig.downStr才是完整地址
	 *
	 * @param detailId
	 * @return
	 */
	public static String getCityCombatMonitorOssPath(String cityName, long detailId) {
		String ossPath = OSSConfig.fileRoot + "monitor/" + cityName + "/" + detailId + ".json";
		return ossPath;
	}

	/**
	 * 获取攻城策略oss路径
	 * 前面加OSSConfig.downStr才是完整地址
	 *
	 * @param gid
	 * @param cityName
	 * @param combatVideoId
	 * @return
	 */
	public static String getAttackCityStrategyOssPath(int gid, String cityName, long combatVideoId) {
		String ossPath = OSSConfig.fileRoot + "attackCityStrategy/" + gid + "/" + cityName + "/" + DateUtil.getTodayInt() + "/" + combatVideoId + ".json";
		return ossPath;
	}

	/**
	 * 获取万仙阵oss路径
	 * 前面加OSSConfig.downStr才是完整地址
	 *
	 * @param wanXianType
	 * @param fileName
	 * @return
	 */
	public static String getWanXianOssPath(int wanXianType, String fileName) {
		String ossPath = OSSConfig.fileRoot + "wanxian/" + wanXianType + "/" + WanXianTool.getThisSeason() + "/" + fileName + ".json";
		return ossPath;
	}

	/**
	 * 获取fst视频在oss上的路径
	 * 前面加OSSConfig.downStr才是完整地址
	 *
	 * @param isGameFst
	 * @param firstUid
	 * @param combatId
	 * @return
	 */
	public static String getFstOssPath(boolean isGameFst, long firstUid, long combatId) {
		String fstName = isGameFst ? "gameFst" : "serverFst";
		String ossPath = OSSConfig.fileRoot + fstName + "/" + DateUtil.getTodayInt() + "/" + firstUid + "/" + combatId + ".json";
		return ossPath;
	}

	/**
	 * 获取轮回视频的oss路径
	 * 前面加OSSConfig.downStr才是完整地址
	 *
	 * @param uid
	 * @param combatId
	 * @return
	 */
	public static String getTransmigrationOssPath(long uid, long combatId) {
		String ossPath = OSSConfig.fileRoot + "transmigration/" + DateUtil.getTodayInt() + "/" + uid + "/" + combatId + ".json";
		return ossPath;
	}

	/**
	 * 获取妖族来袭视频的oss路径
	 * 前面加OSSConfig.downStr才是完整地址
	 * @param gid
	 * @param yaoZuName
	 * @param combatVideoId
	 * @return
	 */
	public static String getYaoZuOssPath(int gid, String yaoZuName, long combatVideoId) {
		String ossPath = OSSConfig.fileRoot + "attackYaoZuStrategy/" + gid + "/" + yaoZuName + "/" + DateUtil.getTodayInt() + "/" + combatVideoId + ".json";
		return ossPath;
	}
	/**
	 * 获取万仙阵oss访问地址
	 *
	 * @param wanxianType
	 * @param fileName
	 * @return
	 */
	public static String getWanxianUrl(int wanxianType, String fileName) {
		return OSSConfig.downStr + getWanXianOssPath(wanxianType, fileName);
	}
}
