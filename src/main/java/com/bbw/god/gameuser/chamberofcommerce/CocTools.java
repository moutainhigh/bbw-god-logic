package com.bbw.god.gameuser.chamberofcommerce;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.bbw.common.ListUtil;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.config.Cfg;
import com.bbw.god.game.config.CfgCoc;
import com.bbw.god.game.config.CfgCoc.CfgCocHonorItem;
import com.bbw.god.game.config.CfgCoc.CfgCocPrivilegeItem;
import com.bbw.god.game.config.CfgCoc.CocShop;
import com.bbw.god.game.config.CfgCoc.Head;
import com.bbw.god.game.config.CfgCoc.ProductAward;
import com.bbw.god.game.config.treasure.TreasureEnum;
/** 
* @author 作者 ：lwb
* @version 创建时间：2020年3月12日 下午2:59:59 
* 类说明 
*/
public class CocTools {

	public static CfgCoc getCfgCoc() {
		CfgCoc cocShop = Cfg.I.getUniqueConfig(CfgCoc.class);
		return cocShop;
	}

	public static List<CocShop> getCocShopList() {
		List<CocShop> shoplist = getCfgCoc().getShopList();
		return ListUtil.copyList(shoplist, CocShop.class);
	}

	public static Optional<CocShop> getCocShopById(int id) {
		List<CocShop> shops = getCocShopList();
		return shops.stream().filter(p -> p.getId() == id).findFirst();
	}

	public static Optional<CocShop> getCocGift(int shopId) {
		CfgCoc cocShop = getCfgCoc();
		List<CocShop> cocShops = cocShop.getGiftList();
		return cocShops.stream().filter(p -> p.getId() == shopId).findFirst();
	}

	public static List<Award> getCocGiftAward(int giftId) {
		CfgCoc cocShop = getCfgCoc();
		List<ProductAward> productAwards = cocShop.getProductAwards();
		Optional<ProductAward> pOptional = productAwards.stream().filter(p -> p.getProductId() == giftId).findFirst();
		if (pOptional.isPresent()) {
			return ListUtil.copyList(pOptional.get().getAwardList(), Award.class);
		}
		return new ArrayList<Award>();
	}

	public static List<Award> getCocExpTaskAwards(int expid) {
		CfgCoc cfgCoc = getCfgCoc();
		List<ProductAward> productAwards = cfgCoc.getExpTaskAwards();
		Optional<ProductAward> pOptional = productAwards.stream().filter(p -> p.getProductId() == expid).findFirst();
		if (pOptional.isPresent()) {
			return ListUtil.copyList(pOptional.get().getAwardList(), Award.class);
		}
		return new ArrayList<Award>();
	}

	public static List<Award> getCocTaskAwards(int taskLv, boolean urgent) {
		CfgCoc cfgCoc = getCfgCoc();
		List<ProductAward> productAwards = cfgCoc.getCocTaskRewards();
		Optional<ProductAward> pOptional = productAwards.stream().filter(p -> p.getProductId() == taskLv).findFirst();
		if (pOptional.isPresent()) {
			List<Award> awards = ListUtil.copyList(pOptional.get().getAwardList(), Award.class);
			if (urgent) {
				for (Award award : awards) {
					if (award.getAwardId() == TreasureEnum.SHJF.getValue()) {
						continue;
					}
					award.setNum(award.getNum() * 2);
				}
			}
			return awards;
		}
		return new ArrayList<Award>();
	}
	/**
	 * 获取特权加值
	 * 
	 * @param type
	 * @param lv
	 * @return
	 */
	public static int getAddByPrivilege(CocPrivilegeEnum cocPrivilegeEnum, int lv) {
		if (cocPrivilegeEnum == null) {
			return 0;
		}
		CfgCoc cfgCoc = getCfgCoc();
		int add = 0;
		for (CfgCocPrivilegeItem item : cfgCoc.getPrivilegeList()) {
			if (item.getType() == cocPrivilegeEnum.getType() && item.getLevel() <= lv && add < item.getQuantity()) {
				add = item.getQuantity();
			}
		}
		return add;
	}

	/**
	 * 获取历练奖励
	 * 
	 * @param taskid
	 * @return
	 */
	public static List<Award> getExpTaskRewards(int taskid) {
		CfgCoc cocShop = getCfgCoc();
		for (ProductAward paward : cocShop.getExpTaskAwards()) {
			if (paward.getProductId() == taskid) {
				return ListUtil.copyList(paward.getAwardList(), Award.class);
			}
		}
		return new ArrayList<Award>();
	}

	/**
	 * 根据积分获取对应的等级
	 * 
	 * @param honor
	 * @return
	 */
	public static int getLvByHonor(int honor) {
		int lv = 1;
		CfgCoc cfgCoc = getCfgCoc();
		for (CfgCocHonorItem item : cfgCoc.getHonorList()) {
			if (item.getTarget() <= honor) {
				lv = item.getLevel();
			}
		}
		return lv;
	}

	/**
	 * 获取等级对应的头衔列表
	 * 
	 * @param lv
	 * @return
	 */
	public static List<CfgCocHonorItem> getHonorItemsByLv(int lv) {
		CfgCoc cfgCoc = getCfgCoc();
		return cfgCoc.getHonorList().stream().filter(p -> p.getLevel() <= lv).collect(Collectors.toList());
	}

	public static Optional<Head> getHeadByLv(int lv) {
		CfgCoc cfgCoc = getCfgCoc();
		return cfgCoc.getHeadList().stream().filter(p -> p.getLv() == lv).findFirst();
	}
}
