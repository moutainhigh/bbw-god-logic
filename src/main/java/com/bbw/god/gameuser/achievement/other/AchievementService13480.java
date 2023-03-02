//package com.bbw.god.gameuser.achievement.other;
//
//import com.bbw.god.gameuser.achievement.BaseAchievementService;
//import com.bbw.god.gameuser.achievement.UserAchievementInfo;
//import com.bbw.god.gameuser.chamberofcommerce.UserCocInfo;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
///**
// * @author suchaobin
// * @description 成就id=13480的service
// * @date 2020/5/18 10:27
// **/
//@Service
//public class AchievementService13480 extends BaseAchievementService {
//	/**
//	 * 获取当前成就id
//	 *
//	 * @return 当前成就id
//	 */
//	@Override
//	public int getMyAchievementId() {
//		return 13480;
//	}
//
//	/**
//	 * 获取当前成就进度
//	 *
//	 * @param uid  玩家id
//	 * @param info 成就对象信息
//	 * @return 当前成就进度
//	 */
//	@Override
//	public int getMyProgress(long uid, UserAchievementInfo info) {
//		if (isAccomplished(info)) {
//			return getMyNeedValue();
//		}
//		int value = 0;
//		UserCocInfo cocInfo = this.gameUserService.getSingleItem(uid, UserCocInfo.class);
//		if (null != cocInfo && null != cocInfo.getCocShopLimt()) {
//			List<Integer> buyLogs = cocInfo.getCocShopLimt().getHonorGiftsBuyLogs();
//			value = buyLogs == null ? 0 : buyLogs.size();
//		}
//		return value;
//	}
//}
