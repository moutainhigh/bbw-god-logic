package com.bbw.god.rd;

import com.bbw.god.city.RDCityInfo;
import com.bbw.god.city.chengc.RDTradeInfo.RDSellingSpecial;
import com.bbw.god.city.chengc.in.RDBuildingUpdateInfo;
import com.bbw.god.city.chengc.in.RDCityInInfo;
import com.bbw.god.exchange.RDExchangeList.RDExchangeGoodInfo;
import com.bbw.god.gameuser.achievement.RDAchievementList.RDAchievement;
import com.bbw.god.gameuser.special.RDSpecialBuinessInfo.RDSpecialPrice;
import com.bbw.god.gameuser.task.daily.RDDailyTask;
import com.bbw.god.gameuser.task.main.RDMainTask;
import com.bbw.god.server.flx.RDFlxBetResults.RDFlxBetResult;
import com.bbw.god.server.fst.RDFstRankerList;
import com.bbw.god.server.monster.RDMonsterList.RDMonsterInfo;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@Deprecated
class ReturnData extends RDSuccess implements Serializable {

	private static final long serialVersionUID = 1L;
	// private List<RDValue<Integer>> addedGolds = null;
	// private List<RDValue<RDCopperInfo>> addedCoppers = null;
	// private List<RDValue<Integer>> addedDices = null;
	// private List<RDValue<RDEleInfo>> addedEles = null;
	// private List<RDValue<RDCardInfo>> cards = null;
	// private List<RDValue<RDTreasureInfo>> treasures = null;
	// private List<RDValue<RDSpecialInfo>> specials = null;
	private List<RDValue<Integer>> rates = null;// 城内暴击率
	private List<RDValue<String>> msgs = null;// 城内一键领取

	private List<Integer> randoms = null;// 摇骰子的结果
	private Integer mbxRemainForcross = null;
	private Integer direction = null;// 1，2，3，4 表示上左右下
	private List<Integer> poss = null;
	private List<Integer> dirs = null;

	private Integer godRemainStep = null;
	private Integer godRemainTime = null;
	private Integer attachGod = null;
	// 胜利信息
	private String winDes = null;
	// 城池到达信息
	private RDCityInfo cityInfo = null;

	// 玩家等级信息
	private Integer expRate = null;
	private Integer guExp = null;
	private Integer addedGuExp = null;
	private Integer guLevel = null;

	// private RDFightsInfo fightersInfo = null;// 获得对手卡牌信息
	private RDBuildingUpdateInfo buildingUpdateInfo = null;// 升级建筑
	private RDCityInInfo manorInfo = null;// 进入城内
	// private RDCardUpdateInfo cardUpdateInfo = null;// 升级卡牌
	private Integer satisfaction = null;// 女娲庙捐献好感度
	private Integer result = 0;// 庙宇结果
	private List<RDFlxBetResult> lastBetResults = null;// 富临轩最近开奖结果
	private List<RDSpecialPrice> specialCities = null;// 系统特产出售城池的价格
	private List<RDSpecialPrice> sellingCities = null;// 玩家特产卖出的城池价格
	private List<RDSellingSpecial> boughtSpecials = null;// 交易购买特产给客户端的返回
	private List<RDSellingSpecial> sellingSpecials = null;// 玩家卖出特产后返回剩余特产的价格信息
	private Integer useNum = null;// 使用法宝消耗的数量
	private Integer cszRemain = null;// 财神珠剩余步数
	private Integer lbRemain = null;// 落宝金钱剩余步数
	private Integer updateFull = null;// 建筑是否升满
	private RDFstRankerList fstInfo = null;// 封神台信息
	private List<RDExchangeGoodInfo> goods = null;// 可兑换的物品
	private Integer pvpTimes;// 封神台次数
	private List<RDMonsterInfo> monsters = null;// 好友怪物
	private List<Integer> taskIds = null;// 达成的成就
	private List<RDAchievement> achievements = null;// 所有成就列表
	private Integer dailyTaskStatus = null;// 每日任务达成通知
	private List<RDDailyTask> dailyTasks = null;// 每日任务列表
	private List<RDMainTask> mainTasks = null;// 主线任务
	private Integer task = null;// 新手进阶任务通知
	private Integer curTask = null;// 新手进阶任务到第几个
	private Integer process = null;// 新手进阶进度
	private List<Integer> taskStatus = null;// 新手进阶状态
	private Integer mainTaskStatus = null;// 主线任务达成通知
	private RDMainTask nextMainTask = null;// 下一个可领取的状态
	private Integer activeZLLB = null;// 激活的助力礼包
	private Integer diceBuyTimes = null;// 体力购买次数
	private Integer levelCopper = null;// 等级铜钱
	private Integer manorCopper = null;// 封地铜钱
	private List<Integer> unfilledSpecialIds = null;// 为捐赠的特产
	private Integer weekCopperRank;// 富豪榜排行
	private Integer pvpRank;// 封神台排行
	private Integer tyfFillCount;// 太一府捐赠数
	private Integer addedJxq;// 获得的聚仙旗数，失去为负数

	// public void addCopper(RDCopperInfo rdCopperInfo, int way) {
	// if (addedCoppers == null) {
	// addedCoppers = new ArrayList<>();
	// }
	// addedCoppers.add(new RDValue<RDCopperInfo>(rdCopperInfo, way));
	// }
	//
	// public void addGold(int value, int way) {
	// if (addedGolds == null) {
	// addedGolds = new ArrayList<>();
	// }
	// addedGolds.add(new RDValue<Integer>(value, way));
	// }
	//
	// public void addDice(int value, int way) {
	// if (addedDices == null) {
	// addedDices = new ArrayList<>();
	// }
	// addedDices.add(new RDValue<Integer>(value, way));
	// }
	//
	// public void addEle(RDEleInfo rdEleInfo, int way) {
	// if (addedEles == null) {
	// addedEles = new ArrayList<>();
	// }
	// addedEles.add(new RDValue<RDEleInfo>(rdEleInfo, way));
	// }
	//
	// public void addCard(RDCardInfo rdCardInfo, int way) {
	// if (cards == null) {
	// cards = new ArrayList<>();
	// }
	// cards.add(new RDValue<RDCardInfo>(rdCardInfo, way));
	// }
	//
	// public void addTreasure(RDTreasureInfo rdTreasureInfo, int way) {
	// if (treasures == null) {
	// treasures = new ArrayList<>();
	// }
	// treasures.add(new RDValue<RDTreasureInfo>(rdTreasureInfo, way));
	// }
	//
	// public void addSpecial(RDSpecialInfo rdSpecialInfo, int way) {
	// if (specials == null) {
	// specials = new ArrayList<>();
	// }
	// specials.add(new RDValue<RDSpecialInfo>(rdSpecialInfo, way));
	// }
	//
	// public void addRate(Integer rate, int way) {
	// if (rates == null) {
	// rates = new ArrayList<>();
	// }
	// rates.add(new RDValue<Integer>(rate, way));
	// }
	//
	// public void addMsg(String msg, int way) {
	// if (msgs == null) {
	// msgs = new ArrayList<>();
	// }
	// msgs.add(new RDValue<String>(msg, way));
	// }
	//
	// // 不能以get开头，否则在对象转json时该方法将自动调用（覆盖lombok的get方法）
	// public RDCityInfo gainCityInfo() {
	// if (cityInfo == null) {
	// cityInfo = new RDCityInfo();
	// }
	// return cityInfo;
	// }
	//
	// public RDBuildingUpdateInfo gainBuildingUpdateInfo() {
	// if (buildingUpdateInfo == null) {
	// buildingUpdateInfo = new RDBuildingUpdateInfo();
	// }
	// return buildingUpdateInfo;
	// }

	// /**
	// * 添加新成就
	// *
	// * @param achievementId
	// */
	// public void addAchievement(int achievementId) {
	// if (taskIds == null) {
	// taskIds = new ArrayList<>();
	// }
	// taskIds.add(achievementId);
	// }

	// @JsonInclude(value = JsonInclude.Include.NON_EMPTY, content =
	// JsonInclude.Include.NON_NULL)
	// private Map<Integer, Integer> addedEles = null;

}
