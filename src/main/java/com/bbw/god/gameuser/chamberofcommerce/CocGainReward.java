package com.bbw.god.gameuser.chamberofcommerce;

/**
 * @author lwb
 * @version 1.0
 * @date 2019年4月16日
 */
public class CocGainReward {

	/**
	 * 领取奖励
	 *
	 * @param rewards
	 * @param rd
	 * @param info
	 * @return
	 */
//	public static RDSuccess gainReward(List<CocReward> rewards, RDCoc rd, UserCocInfo info, int honor) {
//		Long uid = info.getGameUserId();
//		for (CocReward reward : rewards) {
//			int quantity = reward.getQuantity();
//			switch (reward.getRealId()) {
//				case CocConstant.REWARD_ID_QD:
//					//钱袋
//					int addCopper = 0;
//					for (int i = 0; i < quantity; i++) {
//						addCopper += new Random().nextInt(100001) + 100000;//钱袋开启10W~20W铜钱
//					}
//					if (addCopper > 0) {
//						ResEventPublisher.pubCopperAddEvent(uid, addCopper, WayEnum.Chamber_Of_Commerce_QD, rd);
//					}
//					break;
//				case CocConstant.REWARD_ID_JF:
//				if (quantity > 0) {
//					TreasureEventPublisher.pubTAddEvent(uid, TreasureEnum.SHJF.getValue(), quantity,
//							WayEnum.Chamber_Of_Commerce_TASK, new RDCommon());
//				}
//					rd.setAddCofcScore(quantity);
//					//刷新头衔等级
//					CfgCoc cocShop = Cfg.I.getUniqueConfig(CfgCoc.class);
//					List<CfgCocHonorItem> honors = cocShop.getHonorList();
//					for (CfgCocHonorItem item : honors) {
//						if (item.getTarget() <= honor && item.getLevel() > info.getHonorLevel()) {
//							info.setUnclaimed(CocConstant.UNCLIAIEMD_T);
//							info.setHonorLevel(item.getLevel());//升级
//							EPCocUpLv upLVevent = EPCocUpLv.instance(new BaseEventParam(info.getGameUserId()), item.getLevel());
//							CocEventPublisher.pubUplevelAddEvent(upLVevent);
//						}
//					}
//					break;
//				case CocConstant.REWARD_ID_JB:
//				if (quantity > 0) {
//					TreasureEventPublisher.pubTAddEvent(uid, TreasureEnum.SHJB.getValue(), quantity,
//							WayEnum.Chamber_Of_Commerce_TASK, new RDCommon());
//				}
//					rd.setAddCofcGold(quantity);
//					break;
//			}
//		}
//		return rd;
//	}
//
//	/**
//	 * 发放奖励
//	 *
//	 * @param shop
//	 * @param uid
//	 * @param rd
//	 */
//	public static void sendProduce(CocReward shop, long uid, RDCoc rd) {
//		switch (shop.getType()) {
//			case CocConstant.TYPE_CARD:
//				CardEventPublisher.pubCardAddEvent(uid, shop.getRealId(), WayEnum.Chamber_Of_Commerce_SHOP, "商会商城购买", rd);
//				break;
//			case CocConstant.TYPE_TREASURE:
//				if (shop.getRealId() == CocConstant.REWARD_ID_QD) {
//					//目前商城可购买 并且商会的物品中只有钱袋
//					int quantity = shop.getQuantity();
//					int addCopper = 0;
//					for (int i = 0; i < quantity; i++) {
//						addCopper += new Random().nextInt(100001) + 100000;//钱袋开启10W~20W铜钱
//					}
//					if (addCopper > 0) {
//						ResEventPublisher.pubCopperAddEvent(uid, addCopper, WayEnum.Chamber_Of_Commerce_QD, rd);
//					}
//				} else
//					TreasureEventPublisher.pubTAddEvent(uid, shop.getRealId(), shop.getQuantity(), WayEnum.Chamber_Of_Commerce_SHOP, rd);
//				break;
//		}
//	}
//
//	public static void finishedTask(CocTask task, RDCoc rd, UserCocInfo info, int honor) {
//		CfgCoc cfg = Cfg.I.getUniqueConfig(CfgCoc.class);
//		List<CocReward> reWards = cfg.getTaskReWards(task, true);
//
//		gainReward(reWards, rd, info, honor);
//	}

}
