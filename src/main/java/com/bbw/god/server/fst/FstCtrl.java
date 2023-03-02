package com.bbw.god.server.fst;

import com.bbw.god.controller.AbstractController;
import com.bbw.god.game.CR;
import com.bbw.god.server.fst.game.FstRankingType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 封神台相关接口
 *
 */
@RestController
public class FstCtrl extends AbstractController {
	@Autowired
	private FstLogic fstLogic;

	/**
	 * 进入封神台
	 * 
	 * @return
	 */
	@GetMapping(CR.FST.INTO)
	public RDFst intoFst(Integer type) {
		return fstLogic.intoFst(getUserId(),FstType.fromVal(type));
	}

	/**
	 * 领取积分
	 *
	 * @return
	 */
	@GetMapping(CR.FST.GAIN_INCREMENT_POINT)
	public RDFst gainIncrementPoints() {
		return  fstLogic.gainIncrementPoints(getUserId());
	}
	
	/**
	 * 实时榜单 or 上次结算的榜单
	 * @param isPreRanking
	 * @return
	 */
	@GetMapping(CR.FST.RANK)
	public RDFst ranking(Integer isPreRanking,Integer type){
		FstRankingType rankingType=FstRankingType.TIAN;
		if (type!=null){
			rankingType=FstRankingType.fromVal(type);
		}
		return fstLogic.ranking(getUserId(),isPreRanking!=null && isPreRanking==1,rankingType);
	}
	/**
	 * 查看对战日志
	 * @param isGameFst
	 * @return
	 */
	@GetMapping(CR.FST.FIGHT_LOG)
	public RDFst fightLog(Integer isGameFst,Long id){
		return fstLogic.fightLog(getUserId(),isGameFst!=null && isGameFst==1,id);
	}
	
	/**
	 *  查看玩家卡组
	 * @param isGameFst 是否是封神台
	 * @param isAttack 是否是攻击卡组
	 * @return
	 */
	@GetMapping(CR.FST.GET_CARD_GROUP)
	public RDFst getUserCardGroup(Long uid,Integer isGameFst,Integer isAttack){
		return fstLogic.getUserCardGroup(uid,isGameFst!=null && isGameFst==1,isAttack!=null && isAttack==1);
	}
	
}
