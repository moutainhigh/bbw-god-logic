package com.bbw.god.server.guild.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bbw.god.rd.RDCommon;
import com.bbw.god.server.guild.GuildRD;
import com.bbw.god.server.guild.GuildShop;
import com.bbw.god.server.guild.GuildTools;
import com.bbw.god.server.guild.UserGuild;
import com.bbw.god.server.guild.UserGuild.ShopLimit;

/**
* @author lwb  行会商店 与购买
* @date 2019年5月16日  
* @version 1.0  
*/
@Service
public class GuildShopService {

	@Autowired
	private UserGuildStoreProgress userGuildStoreProgress;
	@Autowired
	private GuildUserService guildUserService;
	//商品列表
	public GuildRD list(long uid) {
		UserGuild userGuild = guildUserService.getUserGuild(uid);
		GuildRD rd=new GuildRD();
		List<ShopLimit> userGuildShops = userGuild.getShopInfo().getShops();
		List<GuildShop> list = GuildTools.getCfgGuildShops();
		for(GuildShop shop:list) {
			Optional<ShopLimit> ushop = userGuildShops.stream().filter(p -> p.getId().equals(shop.getId()))
					.findFirst();
			if (ushop.isPresent()) {
				shop.setBought(ushop.get().getBought());
			}
		}
		if (userGuild.getGuildLv()==null || userGuild.getGuildLv()<1) {
			rd.setGuildLv(0);
		}
		rd.setGuildLv(userGuild.getGuildLv());
		rd.setShopList(list);
		return rd;
	}
	//购买商品
	public RDCommon buyProduce(long uid, int pid) {
		return userGuildStoreProgress.buyGoods(uid, pid, 1,null);
	}
}
