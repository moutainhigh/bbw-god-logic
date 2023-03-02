package com.bbw.god.city.chengc;

import com.bbw.god.city.RDCityInfo;
import com.bbw.god.city.chengc.in.RDCityInInfo;
import com.bbw.god.fight.RDFightsInfo;
import com.bbw.god.game.combat.attackstrategy.StrategyVO;
import com.bbw.god.game.combat.runes.RunesEnum;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 到达城池
 *
 * @author suhq
 * @date 2019年3月18日 下午3:52:52
 */
@Getter
@Setter
@ToString(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RDArriveChengC extends RDCityInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	/** 城池护符 */
	private  List<Integer> buffList = Arrays.asList(RunesEnum.GYJL.getRunesId(),RunesEnum.TDLH.getRunesId(),RunesEnum.FWFS.getRunesId());
	//摇骰子 到达时需要返回的参数
	private Integer ownCity = null;// 城池是否已攻下

	//客户端请求当前城池信息：城池id 成就相关内容 是否侦察 当前城池战斗次数 进度 是否展示攻略 攻城符数组
	private List<AchievementInfo> achievementInfos=null;//成就相关内容
	private Integer investigated = null;// 是否侦查过 1是/0否
	private Integer attackTimes=null;//当前城池战斗次数（只计算攻打次数）
	private int[] levelProgress=null;//进度
	private Integer showStrategy=null;//是否展示攻略 1是/0否
	private List<Integer> buffs=null;//攻城符数组
	//点击查看城池需要的信息

	private Double difficult = null;// 难度
	private String condition = null;// 攻城条件
	private Integer areaId = null;// 城池所在区域
	private Integer hierarchy = null;// 城池阶数
	private String attackProgress = null;// 该级别城池的攻城进度
	private String citySpecials = null;// 城池出售特产
	private Integer toTrade = null;// 是否已点击城池交易
	private Integer toAttack = null;// 是否攻城
	private Integer toTraining = null;// 是否攻城
	private Integer toPromote = null;// 是否振兴
	private Integer remainSpecialsRefreshTimes = null;// 刷新可交易的特产
	private Integer cocCityTradeProfit = null;// 商会折扣溢价特权获取次数
	private Integer discount = null;// 商会特权获取的折扣
	private Integer premiumRate = null;// 商会特权获取的溢价
	private Integer fightType = null;// 20攻城；30练兵；60振兴
	private RDFightsInfo fightsInfo = null;// 对手信息
	private RDFightsInfo investigateFightsInfo = null;// 对手信息
	// @JSONField(serialize = false)
	private RDCityInInfo manorInfo = null;// 城内信息
	private String cityArea = null;// 城区
	private Boolean isAlreadyInvestigate = null;// 是否侦查过
	private Integer cityBuff = 0;//城池BUFF
	private Integer hasCardGroup = null;
	/** 攻略 */
	private List<StrategyVO> video = new ArrayList<>();
	private Integer transmigrationScore = null;

	@Data

	public static class AchievementInfo implements Serializable {
		private static final long serialVersionUID = 2568307636227701279L;
		private int id;//成就ID
		private int process;//当前进度

		public static AchievementInfo instance(int id, int process) {
			AchievementInfo info = new AchievementInfo();
			info.setId(id);
			info.setProcess(process);
			return info;
		}
	}
}
