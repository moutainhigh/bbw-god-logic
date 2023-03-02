package com.bbw.god.gameuser.chamberofcommerce.server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bbw.common.ListUtil;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.event.BaseEventParam;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.config.CfgCoc.CfgCocHonorItem;
import com.bbw.god.game.config.CfgCoc.CocShop;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.chamberofcommerce.CocConstant;
import com.bbw.god.gameuser.chamberofcommerce.CocPrivilegeEnum;
import com.bbw.god.gameuser.chamberofcommerce.CocTools;
import com.bbw.god.gameuser.chamberofcommerce.RDCoc;
import com.bbw.god.gameuser.chamberofcommerce.RDCoc.CocReward;
import com.bbw.god.gameuser.chamberofcommerce.RDCoc.RDCocHonorGift;
import com.bbw.god.gameuser.chamberofcommerce.UserCocInfo;
import com.bbw.god.gameuser.chamberofcommerce.UserCocInfo.CocLimtShopItem;
import com.bbw.god.gameuser.chamberofcommerce.UserCocInfo.CocShopLimt;
import com.bbw.god.gameuser.chamberofcommerce.event.CocEventPublisher;
import com.bbw.god.gameuser.chamberofcommerce.event.EPBuyCocBag;
import com.bbw.god.gameuser.res.ResChecker;
import com.bbw.god.gameuser.res.ResEventPublisher;

/**
* @author lwb  
* @date 2019年5月28日  
* @version 1.0  
*/
@Service
public class UserCocStoreService {
	@Autowired
	private UserCocInfoService cocInfoService;
	@Autowired
	private GameUserService gameUserService;
	@Autowired
	private UserCocAwardService userCocAwardService;
	/** 永久限购商品 */
	private final static List<Integer> PERMANENT_LIMIT_GOODS = Arrays.asList(1009, 1010, 1011, 1012, 1013, 1014, 1015);

	/**
	 * 获取头衔礼包列表
	 * 
	 * @param uid
	 * @return
	 */
	public RDCoc getHonorList(long uid) {
		cocInfoService.updateCocLv(uid);
		UserCocInfo info = cocInfoService.getUserCocInfo(uid);
		RDCoc rd = new RDCoc();
		List<CfgCocHonorItem> honorItems = CocTools.getHonorItemsByLv(info.getHonorLevel() + 1);
		List<RDCocHonorGift> honorList = new ArrayList<RDCocHonorGift>();
		int canBuyNum = -1;// 扣除1级头衔
		for (CfgCocHonorItem item : honorItems) {
			RDCocHonorGift honor = RDCocHonorGift.instance(item);
			List<Award> awards = CocTools.getCocGiftAward(item.getGiftId());
			List<CocReward> rewards = new ArrayList<>();
			for (Award award : awards) {
				CocReward reward = CocReward.instance(award, item.getGiftId());
				rewards.add(reward);
			}
			if (info.boughtGift(item.getGiftId())) {
				honor.setStatus(CocConstant.STATUS_HAVE_BUY);
			} else {
				if (item.getLevel() > info.getHonorLevel()) {
					honor.setStatus(CocConstant.STATUS_CANT_BUY);
				} else {
					canBuyNum++;
				}
			}
			honor.setRewards(rewards);
			honorList.add(honor);
		}
		rd.setHonorList(honorList);
		if (canBuyNum != info.getUnclaimed()) {
			info.setUnclaimed(canBuyNum);
			gameUserService.updateItem(info);
		}
		return rd;
	}

	/**
	 * 获取商会商店列表
	 * @param uid
	 * @return
	 */
	public RDCoc list(long uid) {
		RDCoc rd = new RDCoc();
		cocInfoService.updateCocLv(uid);
		Optional<UserCocInfo> info = cocInfoService.getUserCocInfoOp(uid);
		if (info.isPresent()) {
			rd.setCocLv(info.get().getHonorLevel());
		} else {
			rd.setCocLv(0);
		}
		rd.setShopList(getUserShopList(uid));
		return rd;
	}

	/**
	 * 购买 礼包
	 * @param uid
	 * @param produceId
	 * @param type  1000时为头衔礼包 其余为商品
	 * @return
	 */
	public RDCoc buy(long uid, int produceId) {
		UserCocInfo info = cocInfoService.getUserCocInfo(uid);
		Optional<CocShop> oCocShop = CocTools.getCocGift(produceId);
		if (!oCocShop.isPresent()) {
			throw new ExceptionForClientTip("coc.gift.not.exist");
		}
		CocShop cocShop = oCocShop.get();
		if (cocShop.getMinLevel() > info.getHonorLevel()) {
			// 当玩家购买等级不够时，尝试更新下等级
			throw new ExceptionForClientTip("coc.level.toolower");
		}
		UserCocInfo.CocShopLimt userLimit = info.getCocShopLimt();
		RDCoc rd = new RDCoc();
		//购买礼包
		List<Integer> honorGiftList = userLimit.getHonorGiftsBuyLogs();
		if (!ListUtil.isEmpty(honorGiftList) && honorGiftList.contains(produceId)) {
			throw new ExceptionForClientTip("coc.gift.exist");
		}
		int needGold = cocShop.getPrice();
		//购买
		ResChecker.checkGold(gameUserService.getGameUser(uid), needGold);
		ResEventPublisher.pubGoldDeductEvent(uid, needGold, WayEnum.Chamber_Of_Commerce_GIFT, rd);
		List<Award> awards = CocTools.getCocGiftAward(cocShop.getGoodId());
		userCocAwardService.gainReward(awards, uid, rd, WayEnum.Chamber_Of_Commerce_GIFT);
		honorGiftList.add(produceId);
		info.setUnclaimed(info.getUnclaimed() - 1);
		gameUserService.updateItem(info);
		CocEventPublisher.pubBuyCocBagEvent(EPBuyCocBag.instance(new BaseEventParam(uid, WayEnum.Chamber_Of_Commerce_GIFT, rd), produceId));
		return rd;
	}
	//获取用户的 商品列表（包含限购和解锁信息）
	public List<CocShop> getUserShopList(long uid) {
		List<CocShop> shoplist = CocTools.getCocShopList();
		Optional<UserCocInfo> infoOp = cocInfoService.getUserCocInfoOp(uid);
		if (!infoOp.isPresent()) {
			return shoplist;
		}
		UserCocInfo info = infoOp.get();
		CocShopLimt shopLimt = info.getCocShopLimt();
		List<CocLimtShopItem> limitList = shopLimt.getLimits();
		for (CocShop shop : shoplist) {
			if (shop.getLimitCount()>0){
				for (CocLimtShopItem item : limitList) {
					if (item.getShopId().intValue() == shop.getId().intValue()) {
						shop.setBought(item.getBought());
					}
				}
				CocPrivilegeEnum cocPrivilegeEnum = CocPrivilegeEnum.getPrivilegeEnumByShopId(shop.getId());
				int limit = CocTools.getAddByPrivilege(cocPrivilegeEnum, info.getHonorLevel());
				shop.setLimitCount(shop.getLimitCount() + limit);// 增加玩家的可购买次数
			}
		}

		return shoplist;
	}

	/**
	 * 获取用户的购买限制
	 * @param uid
	 * @return
	 */
	public void initCocShopLimt(UserCocInfo info) {
		UserCocInfo.CocShopLimt shopLimt = info.getCocShopLimt();
		if (shopLimt == null) {
			return;
		}
		shopLimt.getLimits().removeIf(s -> !PERMANENT_LIMIT_GOODS.contains(s.getShopId()));
		gameUserService.updateItem(info);
	}
}
