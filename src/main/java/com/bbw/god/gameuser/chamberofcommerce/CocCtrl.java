package com.bbw.god.gameuser.chamberofcommerce;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.controller.AbstractController;
import com.bbw.god.game.CR;
import com.bbw.god.game.config.CfgCoc.CocShop;
import com.bbw.god.gameuser.chamberofcommerce.server.UserCocExpTaskService;
import com.bbw.god.gameuser.chamberofcommerce.server.UserCocInfoService;
import com.bbw.god.gameuser.chamberofcommerce.server.UserCocStoreProcessor;
import com.bbw.god.gameuser.chamberofcommerce.server.UserCocStoreService;
import com.bbw.god.gameuser.chamberofcommerce.server.UserCocTaskService;
import com.bbw.god.rd.RDCommon;

/** 
 * 商会系统入口
* @author lwb  
* @date 2019年4月14日  
* @version 1.0  
*/
@RestController
public class CocCtrl  extends AbstractController{
	private static final int OPTION_ACCEPT=0;//接任务
	private static final int OPTION_GAIN=1;//完成任务
	private static final int OPTION_CANCEL=2;//为取消任务
	@Autowired
	private UserCocTaskService userCocTaskService;
	@Autowired 
	private UserCocExpTaskService userCocExpTaskService;
	@Autowired
	private UserCocStoreService userCocStoreService;
	@Autowired
	private UserCocStoreProcessor userCocStoreProcessor;
	//获取商会 任务列表
	@GetMapping(CR.ChamberOfCommerce.LIST_TASK)
	public RDCoc list() {
		long uid=getUserId();
		return userCocTaskService.list(uid);
	}
	
	@GetMapping(CR.ChamberOfCommerce.GET_LV)
	public RDCoc getLv() {
		RDCoc rd=new RDCoc();
		rd.setHonorLevel(0);
		UserCocInfo info=gameUserService.getSingleItem(getUserId(),UserCocInfo.class);
		if (info!=null && null!=info.getHonorLevel()) {
			rd.setHonorLevel(info.getHonorLevel());
		}
		return rd;
	}
	//任务操作
	@GetMapping(CR.ChamberOfCommerce.OPTION_TASK)
	public RDCoc option(int taskId,int option) {
		long uid=getUserId();
		if (OPTION_ACCEPT==option) {
			return userCocTaskService.acceptTask(uid, taskId);
		}
		
		if(OPTION_GAIN==option){
			return userCocTaskService.gainAward(uid, taskId);
		}
		
		if (OPTION_CANCEL==option) {
			return userCocTaskService.cancelTask(uid, taskId);
		}
		
		return new RDCoc();
	}
	//刷新任务
	@GetMapping(CR.ChamberOfCommerce.REFRESH_TASK)
	public RDCoc refresh() {
		long uid=getUserId();
		return userCocTaskService.refreshAll(uid);
	}
	//增加任务可完成次数
	@GetMapping(CR.ChamberOfCommerce.ADD_TASK)
	public RDCoc addTask() {
		long uid=getUserId();
		return userCocTaskService.addTask(uid);
	}
	//获取跑商任务列表
	@GetMapping(CR.ChamberOfCommerce.LIST_EXPERIENCE)
	public RDCoc listExp() {
		long uid=getUserId();
		return 	userCocExpTaskService.list(uid);
	}
	//领取跑商任务奖励
	@GetMapping(CR.ChamberOfCommerce.EXPERIENCE_GET_REWARD)
	public RDCoc gainExpReward(int expId) {
		long uid=getUserId();
		return userCocExpTaskService.gainReward(uid, expId);
	}
	//获取商会商城商品列表
	@GetMapping(CR.ChamberOfCommerce.LIST_SHOP)
	public RDCoc listShop() {
		long uid=getUserId();
		return userCocStoreService.list(uid);
	}
	//购买商品
	@GetMapping(CR.ChamberOfCommerce.BUY_SHOP)
	public RDCommon buyShop(int propId, int goodId) {
		long uid=getUserId();
		if (CocConstant.TYPE_GIFT == propId) {
			return userCocStoreService.buy(uid, goodId);
		}else {
			Optional<CocShop> shopOp = CocTools.getCocShopList().stream().filter(p -> p.getGoodId() == goodId)
					.findFirst();
			if (!shopOp.isPresent()) {
				// 商品不存在
				throw new ExceptionForClientTip("coc.gift.not.exist");
			}
			return userCocStoreProcessor.buyGoods(uid, shopOp.get().getId(), 1,null);
		}
	}
	//获取头衔
	@GetMapping(CR.ChamberOfCommerce.LIST_HONOR)
	public RDCoc listHonor() {
		long uid=getUserId();
		return userCocStoreService.getHonorList(uid);
	}
	//已接受任务信息
	@GetMapping(CR.ChamberOfCommerce.ACCEPTED_TASK_INFO)
	public RDCoc acceptedTaskInfo() {
		long uid=getUserId();
		return userCocTaskService.getAcceptedTask(uid);
	}
}
