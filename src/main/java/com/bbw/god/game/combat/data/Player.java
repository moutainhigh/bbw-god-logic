package com.bbw.god.game.combat.data;

import com.bbw.common.JSONUtil;
import com.bbw.common.ListUtil;
import com.bbw.common.StrUtil;
import com.bbw.exception.CoderException;
import com.bbw.god.fight.FightTypeEnum;
import com.bbw.god.game.combat.PositionService;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.param.CPlayerInitParam;
import com.bbw.god.game.combat.data.weapon.Weapon;
import com.bbw.god.game.combat.data.weapon.WeaponLog;
import com.bbw.god.game.combat.runes.RunesEnum;
import com.bbw.god.game.config.treasure.TreasureEnum;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 玩家
 *
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-07-04 18:11
 */
@Slf4j
@Data
@NoArgsConstructor
public class Player implements Serializable {
	private static final long serialVersionUID = 5489888843213462754L;
	private static final String[] BATTLE_POSITION_NAME = {"云台", "先锋", "前军", "中军", "后军", "军师"};
	private Long combatId=-1L;
	private PlayerId id = PlayerId.P1;// 玩家标识
	private String name;// 昵称
	private int lv = 1;// 等级
	private int imgId = 10;// 头像
	private Integer iconId = TreasureEnum.HEAD_ICON_Normal.getValue();// 头像框
	private int maxHp = 0;// 最大血量
	private int hp = 0;// 当前血量
	private int beginHp = 0;// 开局时的血量
	private int maxMp = 0;// 最大魔法值
	private int lvMp = 0;// 等级魔法值
	private int mp = 0;// 当前魔法值
	private int bloodBarNum = 1;//血条数
	private Long uid = -1l;// 玩家ID AI为-1
	private Long cardFromUid = -1l;// 来自哪个玩家的卡牌
	private Integer god = 0;// 战斗神仙
	private int cardInitId = 1000;// 初始化卡牌ID，为了避免后续新增卡牌（如招魂幡）Id与现有重复，需保存该参数
	/** 战斗护符对象集（新增） */
	private List<CombatBuff> runes = new ArrayList<>();
	/** 战斗护符集（新增玩家护符前的字段，这个冗余是出于性能考虑） */
	private List<Integer> buffs = new ArrayList<>();

	private Integer minCardHv = 0;
	private boolean unlockAllPosBuff = false;
	private TimesLimit banYunTai;
	/** 天官赐福，普通攻城才有的 */
	private boolean ownTGCF = false;
	/** 鬼蜮伎俩，梦魇对手才有的 */
	private boolean ownGYJL = false;
	/** 妖族血脉，妖族战斗使用 */
	private boolean ownYZXM = false;
	private String specialCards = "";


	/**
	 * 战场卡牌数组。依次为：云台、先锋、前军、中军、后军、军师、
	 */
	private BattleCard[] playingCards = new BattleCard[CombatConfig.MAX_BATTLE_CARD];// 战场卡牌
	private BattleCard[] handCards = new BattleCard[CombatConfig.MAX_IN_HAND];// 手牌
	private List<BattleCard> drawCards = new ArrayList<>();// 抓牌堆
	private List<BattleCard> discard = new ArrayList<>();// 坟场（弃牌堆）
	private List<BattleCard> reinforceCards = new ArrayList<>();// reinforcements援军，不在牌堆里其他卡牌
	private List<BattleCard> degenerator = new ArrayList<>();// 异次元，坟地的卡牌可以被移除到异次元
	private List<Weapon> weapons = new ArrayList<>();// 拥有的战斗法宝
	private List<WeaponLog> weaponLog = new ArrayList<>();// 战斗法宝使用记录（即客户端请求使用，等待下回合生效的法宝记录）
	private List<Weapon> weaponsInUse = new ArrayList<>();// 已经成功使用的法宝
	private Statistics statistics = new Statistics();// 统计信息，后续根据业务变化再重构

	public static Player instance(CPlayerInitParam cpp, int initHp, int initMp){
		Player player=new Player(cpp.getNickname(), cpp.getLv(), cpp.getHeadImg());
		player.setUid(cpp.getUid());
		player.setIconId(cpp.getHeadIcon());
		player.setCardFromUid(cpp.getCardFromUid());
		player.setMaxMp(initMp);
		if (cpp.getInitHP()!=null && cpp.getInitHP()>0){
			initHp=cpp.getInitHP();
		}
		player.setMp(initMp);
		player.setHp(initHp);
		if (cpp.getHp()!=null && cpp.getHp()>0){
			player.setHp(cpp.getHp());
		}
		player.setMaxHp(initHp);
		player.setBeginHp(player.getHp());
		player.setWeapons(cpp.getWeapons());
		return player;
	}

	public Player(String nickname, int lv, int headImg) {
		this.name = nickname;
		this.lv = lv;
		this.imgId = headImg;
	}

	public void setId(PlayerId id) {
		this.id = id;
		cardInitId = id.getValue() * 1000;
	}
	public void setDeployCardFlag(int round) {
		if (round > 64) {
			throw CoderException.high("战斗回合数超过64!");
		}
		this.statistics.deployCardsFlag += 1 << (round - 1);
	}

	/**
	 * 计算 场上+手牌+牌组 卡牌数量
	 * @return
	 */
	public int countAliveCard(){
		int count=0;
		for (BattleCard card:playingCards){
			if (card!=null && card.isAlive()){
				count++;
			}
		}
		for (BattleCard card:handCards){
			if (card!=null){
				count++;
			}
		}
		count+=drawCards.size();
		return count;
	}

	public void resetPos() {
		// 战场
		for (int index = 0; index < this.playingCards.length; index++) {
			if (null != playingCards[index]) {
				playingCards[index].setPos(PositionService.getBattleCardPos(this.id, index));
			}
		}
		// 手牌
		for (int index = 0; index < this.handCards.length; index++) {
			if (null != handCards[index]) {
				handCards[index].setPos(PositionService.getHandCardPos(this.id, index));
			}
		}
		// 牌堆
		int drawCardsBeginPos = PositionService.getDrawCardsBeginPos(this.getId());
		for (int index = 0; index < this.drawCards.size(); index++) {
			if (null != drawCards.get(index)) {
				int pos = index + drawCardsBeginPos;
				drawCards.get(index).setPos(pos);
			}
		}
		// 坟场
		int discardBeginPos = PositionService.getDiscardBeginPos(this.getId());
		for (int index = 0; index < this.discard.size(); index++) {
			if (null != discard.get(index)) {
				discard.get(index).setPos(discardBeginPos + index);
			}
		}
		// 援军 和异次元无需重置位置
	}

	public boolean isKilled() {
		return 1 > hp;
	}

	/**
	 * 云台上没有卡牌
	 *
	 * @return
	 */
	public boolean yunTaiIsEmpty() {
		if (isBanYunTai()) {
			return false;
		}
		return null == playingCards[0];
	}

	public BattleCard[] getPlayingCards() {
		for (int index = 0; index < this.playingCards.length; index++) {
			if (null != playingCards[index]) {
				playingCards[index].setPos(PositionService.getBattleCardPos(this.id, index));
			}
		}
		return this.playingCards;
	}

	public BattleCard getPlayingCards(int index) {
		if (null != playingCards[index]) {
			playingCards[index].setPos(PositionService.getBattleCardPos(this.id, index));
		}
		return playingCards[index];
	}

	/**
	 * 获取战场卡牌
	 * @param includeYunTai 是否含云台
	 * @return
	 */
	public List<BattleCard> getPlayingCards(boolean includeYunTai) {
		return getPlayingCards(includeYunTai,new ArrayList<>());
	}

	/**
	 * 获取战场卡牌
	 * @param includeYunTai 是否含云台
	 * @param excludes  排除的卡牌ID
	 * @return
	 */
	public List<BattleCard> getPlayingCards(boolean includeYunTai,List<Integer> excludes) {
		List<BattleCard> cards=new ArrayList<>();
		int yunTaiPos = PositionService.getYunTaiPos(getId());
		for (BattleCard card:playingCards){
			if (card==null || excludes.contains(card.getImgId()) || ( !includeYunTai && card.getPos()==yunTaiPos)){
				continue;
			}
			cards.add(card);
		}
		return cards;
	}
	/**
	 * 通过组合技ID 获取相同组合技的上阵卡牌
	 *
	 * @param groupId
	 * @return
	 */
	public List<BattleCard> getPlayingCardsByGroupSkillId(int groupId) {
		List<BattleCard> groupCards = new ArrayList<>();
		for (BattleCard card : playingCards) {
			if (null != card && card.getGroupId() == groupId) {
				groupCards.add(card);
			}
		}
		return groupCards;
	}

	public BattleCard[] getHandCards() {
		for (int index = 0; index < this.handCards.length; index++) {
			if (null != handCards[index]) {
				handCards[index].setPos(PositionService.getHandCardPos(this.id, index));
			}
		}
		return this.handCards;
	}

	public List<BattleCard> getHandCardList() {
		List<BattleCard> list=new ArrayList<>();
		for (int index = 0; index < this.handCards.length; index++) {
			if (null != handCards[index]) {
				handCards[index].setPos(PositionService.getHandCardPos(this.id, index));
				list.add(handCards[index]);
			}
		}
		return list;
	}

	public BattleCard getHandCards(int index) {
		if (null != handCards[index]) {
			handCards[index].setPos(PositionService.getHandCardPos(this.id, index));
		}
		return handCards[index];
	}

	/**
	 * 手牌满了
	 *
	 * @return
	 */
	public boolean handCardsIsFull() {
		return getHandCardsCount() == handCards.length;
	}

	/**
	 * 获取手牌数量
	 *
	 * @return
	 */
	public int getHandCardsCount() {
		int count = 0;
		for (int i = 0; i < handCards.length; i++) {
			if (null != handCards[i]) {
				count++;
			}
		}
		return count;
	}
	/**
	 * 获取战场牌数量
	 *
	 * @return
	 */
	public int getPlayingCardsCount() {
		int count = 0;
		for (int i = 0; i < playingCards.length; i++) {
			if (null != playingCards[i]) {
				count++;
			}
		}
		return count;
	}
	/**
	 * 整理手牌
	 */
	public void sortHandCards() {
		for (int emptyIndex = 0; emptyIndex < handCards.length; emptyIndex++) {
			if (null == handCards[emptyIndex]) {
				for (int noEmpty = emptyIndex + 1; noEmpty < handCards.length; noEmpty++) {
					if (null != handCards[noEmpty]) {
						handCards[emptyIndex] = handCards[noEmpty];
						handCards[noEmpty] = null;
						break;
					}
				}
			}
		}
	}

	/**
	 * 补充手牌，返回手牌的下标索引
	 *
	 * @param card
	 * @return 手牌的下标索引
	 */
	public int addHandCard(BattleCard card) {
		if (handCardsIsFull()) {
			throw CoderException.high(String.format("手牌已满%d张!", handCards.length));
		}
		for (int i = 0; i < handCards.length; i++) {
			// 找到最近一个空位，插入后返回
			if (null == handCards[i]) {
				handCards[i] = card;
				return i;
			}
		}
		throw CoderException.high(String.format("手牌未满，却找不到合适的空位!"));
	}

	/**
	 * 增加mp
	 *
	 * @param value
	 */
	public void incMp(int value) {
		// if (mp + value < 0) {
		// throw CoderException.high("法力值扣除异常！需要扣除" + value + ",最多只有" + mp);
		// }
		mp += value;
		mp = Math.max(mp, 0);
		mp = Math.min(mp, maxMp);
	}

	/**
	 * 增加maxMp
	 *
	 * @param value
	 */
	public void incMaxMp(int value) {
		maxMp += value;
		maxMp = Math.max(maxMp, 0);
	}

	/**
	 * 增加hp
	 *
	 * @param value
	 */
	public void incHp(int value) {
		if (hp<=0) {
			return;
		}
		hp += value;
		hp = Math.max(hp, 0);
		hp = Math.min(hp, maxHp);
	}
	public void incMaxHp(int value) {
		maxHp += value;
		maxHp = Math.max(maxHp, 0);
		hp = Math.min(hp, maxHp);
	}

	/**
	 * 重置召唤师血量 包含 Hp maxHp beginHp
	 *
	 * @param val
	 */
	public void resetHp(int val) {
		maxHp = val;
		hp = val;
		beginHp = val;
	}

	/**
	 * 重置召唤师MP 包含 Mp maxMp
	 *
	 * @param val
	 */
	public void resetMp(int val) {
		maxMp = val;
		mp = val;
	}

	/**
	 * 添加法宝
	 *
	 * @param weapons
	 */
	public void addWeapons(List<Weapon> weapons) {
		if (null != weapons && !weapons.isEmpty()) {
			this.weapons.addAll(weapons);
		}
	}

	/**
	 * 添加法宝使用记录
	 *
	 * @param log
	 */
	public void addWeaponLog(WeaponLog log) {
		this.weaponLog.add(log);
	}

	/**
	 * 增加生效的法宝记录（即使用成功的法宝），并从玩家拥有的法宝中扣除对应的数目
	 *
	 * @param weapon
	 */
	public void addEffectWeaponLog(Weapon weapon) {
		decWeapons(weapon);
		for (Weapon w : weaponsInUse) {
			if (w.getId() == weapon.getId()) {
				w.addNum(weapon.getNum());
				return;
			}
		}
		weaponsInUse.add(weapon);
	}

	/**
	 * 扣除法宝
	 *
	 * @param weapon
	 */
	public void decWeapons(Weapon weapon) {
		Iterator<Weapon> iterator = weapons.iterator();
		while (iterator.hasNext()) {
			Weapon wp = iterator.next();
			if (wp.getId() == weapon.getId()) {
				wp.decNum(weapon.getNum());
				if (wp.getNum() <= 0) {
					iterator.remove();
				}
				return;
			}
		}
	}

	/**
	 * 获取武器使用次数
	 * 未扣除法宝的+已扣除的
	 * @param weaponId
	 * @return
	 */
	public long sumWeaponUseTimes(int weaponId) {

		int sum=0;
		for (WeaponLog weaponLog:weaponLog){
			if (weaponLog.getWeaponId()==weaponId && !weaponLog.isDeductWeapon()){
				sum++;
			}
		}
		for (Weapon weapon:weaponsInUse){
			if (weapon.getId()==weaponId){
				return sum+=weapon.getNum();
			}
		}
		return sum;
	}

	/**
	 * 计算当前回合开始使用的法宝
	 * @param weaponId
	 * @param round
	 * @return
	 */
	public int sumCurrentRoundWeaponEffectTimes(int weaponId,int round){
		int sum=0;
		for (WeaponLog weaponLog:weaponLog){
			if (weaponLog.getWeaponId()==weaponId && weaponLog.getBeginRound()==round){
				sum++;
			}
		}
		return sum;
	}

	/**
	 * 成功使用的法宝数量
	 * @return
	 */
	public int getUserWeaponNum() {
		int num=0;
		for (Weapon weapon:weaponsInUse) {
			num+=weapon.getNum();
		}
		return num;
	}

	/**
	 * 成功使用的所有法宝id
	 *
	 * @return
	 */
	public String getUserWeaponIdStr() {
		String str = "";
		for (Weapon weapon : weaponsInUse) {
			str += ";" + weapon.getId() + "*" + weapon.getNum();
		}
		if (!StrUtil.isBlank(str)) {
			str = str.substring(1);
		}
		return str;
	}
	/**
	 * 是否还有可用卡牌：战场卡牌不为空、手牌不为空、牌堆不为空
	 * @return
	 */
	public boolean hasActiveCards() {
		return getPlayingCardsCount() > 0 || getHandCardsCount() > 0 || getDrawCards().size() > 0;
	}

	/**
	 * 获取空的阵位数据下标标识
	 *
	 * @param includeYunTai 是否包含云台
	 * @return
	 */
	@NonNull
	public int[] getEmptyBattlePos(boolean includeYunTai) {
		List<Integer> emptyPos = new ArrayList<>(CombatConfig.MAX_BATTLE_CARD);
		if (isBanYunTai()) {
			includeYunTai = false;
		}
		for (int i = 0; i < playingCards.length; i++) {
			if ((!includeYunTai) && 0 == i) {
				continue;
			}
			if (null != playingCards[i]) {
				continue;
			}
			emptyPos.add(i);
		}
		if (emptyPos.isEmpty()) {
			return new int[0];
		}
		return PositionService.getBattleCardPos(id, emptyPos);
	}

	/**
	 * 获取解锁的阵位数据下标标识
	 *
	 * @return
	 */
	@NonNull
	public List<Integer> getUnlockBattleIndex(int round, FightTypeEnum fightTypeEnum) {
		List<Integer> unlockPos = new ArrayList<>(CombatConfig.MAX_BATTLE_CARD);
		int index = 0;
		if (isBanYunTai()) {
			index = 1;
		}
		for (int i = index; i < playingCards.length; i++) {
			if (null != playingCards[i]) {
				continue;
			}
			// 是否解锁
			int pos = PositionService.getBattleCardPos(id, i);
			if (unlockAllPosBuff || PositionService.positionUnlock(round, id, pos, fightTypeEnum)) {
				unlockPos.add(i);
			}
		}
		return unlockPos;
	}

	/**
	 * 清除 没有生效次数 且 未成功扣除法宝的法宝日志
	 */
	public void clearDisabledWeaponLog() {
		List<WeaponLog> logs = weaponLog.stream().filter(p -> p.getResidueTimes() > 0 && p.isDeductWeapon()).collect(Collectors.toList());
		weaponLog.clear();
		weaponLog.addAll(logs);
	}

	public int getGodId() {
		return this.god == null ? 0 : this.god;
	}

	public int getLoseCardNum(){
		int lose=0;
		for (BattleCard card:discard){
			if (card!=null){
				lose++;
			}
		}
		for (BattleCard card:degenerator){
			if (card!=null){
				lose++;
			}
		}
		return lose;
	}


	public void printPlayingCardsInfo(){
		String content="战场卡牌：";
		for (BattleCard card:playingCards){
			if (card!=null){
				content+=card.getName()+":技能"+ JSONUtil.toJson(card.getSkills())+"---------\n";
			}
		}
		log.error(content);
	}

	/**
	 * 获取云台上的卡
	 * @return
	 */
	public Optional<BattleCard> findYunTaiPosCard(){
		if (playingCards[0]==null){
			return Optional.empty();
		}
		return Optional.of(playingCards[0]);
	}
	/**
	 * 获取新增卡牌的唯一标识Id
	 *
	 * @return
	 */
	public int cardInitId() {
		return cardInitId++;
	}

	/**
	 * 根据ID获取到战斗法宝
	 * @param wid
	 * @return
	 */
	public Optional<Weapon> findWeaponById(int wid){
		for (Weapon weapon:weapons){
			if (weapon.getId()==wid){
				return Optional.of(weapon);
			}
		}
		return Optional.empty();
	}

	/**
	 * 获取需要添加动画的记录
	 * <br/> 已扣除的法宝 但是剩余次数为-1的
	 * @return
	 */
	public List<WeaponLog> findNeedAddAnimationLog(){
		return weaponLog.stream().filter(p->p.isDeductWeapon() && p.getResidueTimes()==-1).collect(Collectors.toList());
	}

	/**
	 * 生效次数大于0的
	 * @return
	 */
	public List<WeaponLog> findNeedTakeEffectLog(){
		return weaponLog.stream().filter(p->p.getResidueTimes()>0).collect(Collectors.toList());
	}

	/**
	 * 更新为更高的血量
	 *
	 * @param hp
	 */
	public void updateHighHp(int hp) {
		if (beginHp > hp) {
			return;
		}
		maxHp = hp;
		beginHp = hp;
		this.hp = hp;
	}

	/**
	 * 初始化战斗护符
	 *
	 * @param runes
	 */
	public void initBuffs(List<CombatBuff> runes) {
		if (ListUtil.isEmpty(runes)) {
			return;
		}
		this.runes = runes;
		this.buffs = runes.stream().map(CombatBuff::getRuneId).collect(Collectors.toList());
	}

	/**
	 * 获取buff
	 *
	 * @param buff
	 * @return
	 */
	public CombatBuff gainBuff(Integer buff) {
		return runes.stream().filter(tmp -> tmp.getRuneId() == buff).findFirst().orElse(null);
	}

	/**
	 * 是否持有某个buff
	 *
	 * @param rune
	 * @return
	 */
	public boolean hasBuff(RunesEnum rune) {
		if (ListUtil.isEmpty(buffs)) {
			return false;
		}
		return buffs.contains(rune.getRunesId());
	}

	/**
	 * 是否持有某个状态
	 *
	 * @param skillId
	 * @return
	 */
	public boolean hasStatus(int skillId) {
		if (ListUtil.isEmpty(buffs)) {
			return false;
		}
		boolean status = runes.stream().anyMatch(tmp -> tmp.getRuneId() == skillId && !tmp.ifInvalid());
		return status;
	}

	/**
	 * 是否封禁云台
	 *
	 * @return
	 */
	public boolean isBanYunTai() {
		if (null == banYunTai) {
			return false;
		}
		return !banYunTai.isForbid();
	}

	/**
	 * 获取被击杀的卡牌
	 *
	 * @return
	 */
	public List<BattleCard> gainKilledCard() {
		List<BattleCard> killedCards = new ArrayList<>();
		for (BattleCard card : getDiscard()) {
			if (card == null) {
				continue;
			}
			killedCards.add(card);
		}
		for (BattleCard card : getDegenerator()) {
			if (card == null) {
				continue;
			}
			killedCards.add(card);
		}
		return killedCards;
	}

	/**
	 * 获取牌堆的所有卡牌的防御之和
	 *
	 * @return
	 */
	public int gainBloodByDrawCards() {
		int hp = 0;
		for (BattleCard battleCard : drawCards) {
			hp += battleCard.getHp();
		}
		return hp;
	}

	@Data
	public class Statistics implements Serializable {
		private static final long serialVersionUID = -1380103152340665837L;
		private int zhaoCaiEffectTimes = 0;// 生财技能使用次数
		private int handCardRoundMpAddtion = 0;// 得道等影响手牌法力(加值)
		private int nextRoundHandCardRoundMp = -1;// 疾军等影响手牌法力(重置值,优先级大于handCardRoundMpAddtion)
		private int mp = 0;//玩家下回合开始时法力值变化
		private int initCardMp = 0;//卡牌法力基础值加成
		private long deployCardsFlag = 0;// 是否有上牌
		private int handCardUpLimit = 5;
		private boolean gainJYD = false;
		private Integer yiDaoRen = 0;//分身蚊道人数量
		private boolean simultaneously2LongWen = false;//我方场上同时存在2只龙蚊
		/** 整场战斗技能(eg:祭鞭,麒麟)触发次数 */
		private Map<Integer, Integer> skillEffectTimes;
		/** 初始卡组的分组信息：属性 -> 卡牌ID */
		private Map<Integer, List<Integer>> initialTypeMapCards;
		/** 财神经验加成 */
		private int caiShenAddRate = 0;

		/**
		 * 更新技能生效次数
		 *
		 * @param skill
		 */
		public void addSkillEffectTime(CombatSkillEnum skill) {
			if (null == skillEffectTimes) {
				skillEffectTimes = new HashMap<>();
			}
			Integer effectTimes = gainSkillEffectTime(skill);
			effectTimes++;
			skillEffectTimes.put(skill.getValue(), effectTimes);
		}

		/**
		 * 获取技能生效次数
		 *
		 * @param skill
		 * @return
		 */
		public int gainSkillEffectTime(CombatSkillEnum skill) {
			if (null == skillEffectTimes) {
				return 0;
			}
			Integer effectTimes = skillEffectTimes.getOrDefault(skill.getValue(), 0);
			return effectTimes;
		}


		public void addYiDaoRen() {
			yiDaoRen++;
		}

		public void resetNextRoundHp() {
			nextRoundHandCardRoundMp = -1;
		}

		/**
		 * 获取初始卡组某个属性卡牌的张数
		 *
		 * @param type
		 * @return
		 */
		public Integer gainInitialTypeCardNum(int type) {
			if (null == initialTypeMapCards) {
				return 0;
			}
			List<Integer> cardIds = initialTypeMapCards.get(type);
			if (ListUtil.isEmpty(cardIds)) {
				return 0;
			}
			return cardIds.size();
		}
	}
}