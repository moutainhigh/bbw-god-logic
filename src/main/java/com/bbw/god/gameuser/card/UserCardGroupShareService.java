package com.bbw.god.gameuser.card;

import com.bbw.common.DateUtil;
import com.bbw.common.JSONUtil;
import com.bbw.common.StrUtil;
import com.bbw.db.redis.RedisHashUtil;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.game.config.card.CardEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.leadercard.UserLeaderCard;
import com.bbw.god.gameuser.leadercard.beast.UserLeaderBeastService;
import com.bbw.god.gameuser.leadercard.equipment.UserLeaderEquimentService;
import com.bbw.god.gameuser.leadercard.service.LeaderCardService;
import com.bbw.god.gameuser.res.ResChecker;
import com.bbw.god.server.guild.service.GuildUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @author 作者 ：lwb
 * @version 创建时间：2019年11月11日 上午9:18:23
 * 类说明  卡组分享
 */
@Service
public class UserCardGroupShareService {
	@Autowired
	private RedisHashUtil<String, String> hashUtil;
	@Autowired
	private UserCardService userCardService;
	@Autowired
	private GameUserService userService;
	@Autowired
	private LeaderCardService leaderCardService;
	@Autowired
	private UserLeaderBeastService leaderBeastService;
	@Autowired
	private UserLeaderEquimentService leaderEquimentService;
	@Autowired
	private GuildUserService guildUserService;
	private static String basekey = "game:share:cardGroup";

	public RDShareCardGroup shareCardGroup(long uid, String groupName, int way) {
		RDShareCardGroup shareCardGroup = new RDShareCardGroup();
		UserCardGroup usingCardGroup = userCardService.getUserCardGroup(uid, groupName);
		List<UserCard> userCards=new ArrayList<>();
		if (usingCardGroup != null) {
			userCards=userCardService.getUserCards(uid, usingCardGroup.getCards());
			if (usingCardGroup.getCards().contains(CardEnum.LEADER_CARD.getCardId())) {
				Optional<UserLeaderCard> op = leaderCardService.getUserLeaderCardOp(uid);
				if (op.isPresent()){
					UserLeaderCard leaderCard = op.get();
					GameUser gu= userService.getGameUser(uid);
					shareCardGroup.addCard(leaderCard,leaderEquimentService.getTakedEquipments(uid),leaderBeastService.getTakedBeasts(uid),gu.getRoleInfo().getNickname());
				}
			}
        }
		if (userCards == null || userCards.isEmpty()) {
			throw new ExceptionForClientTip("card.group.share.error.empty");
		}
		if (ShareWayEnum.fromVal(way).equals(ShareWayEnum.ALLSERVER)) {
			//跨服分享 10W铜钱  此处不需要扣钱，只需要检验铜钱是否足够即可
			int needCopper = 10 * 10000;
			ResChecker.checkCopper(userService.getGameUser(uid), needCopper);
		} else if (way == ShareWayEnum.GUILD.getVal() && !guildUserService.hasGuild(uid)) {
			throw new ExceptionForClientTip("guild.user.not.join");
		}

		shareCardGroup.addCards(userCards);
		shareCardGroup.setName(groupName);
		shareCardGroup.setUid(uid);
		String key = getHashKey();
		Long num = hashUtil.getSize(key);
		String shareId = getFiledKey(uid, num);
		hashUtil.putField(key, shareId, JSONUtil.toJson(shareCardGroup));
		//分享卡组有效期 3天
		hashUtil.expire(key, 3, TimeUnit.DAYS);

		RDShareCardGroup rd = new RDShareCardGroup();
		rd.setShareId(shareId);
		return rd;
	}

	/**
	 * 根据ID获取分享的卡组
	 *
	 * @param id
	 * @return
	 */
	public RDShareCardGroup getShareCardGroupById(String id) {
		if (StrUtil.isBlank(id) || id.length() < 8) {
			throw new ExceptionForClientTip("card.group.share.expire");
		}
		String date = id.substring(0, 8);
		String key = basekey + ":" + date;
		String jsonStr = hashUtil.getField(key, id);
		if (StrUtil.isBlank(jsonStr)) {
			throw new ExceptionForClientTip("card.group.share.expire");
		}
		RDShareCardGroup shareCardGroup = JSONUtil.fromJson(jsonStr, RDShareCardGroup.class);
		shareCardGroup.setShareId(id);
		return shareCardGroup;
	}

	/**
	 * map 的key
	 *
	 * @return
	 */
	public String getHashKey() {
		return basekey + ":" + DateUtil.getTodayInt();
	}

	/**
	 * 单字段的KEY
	 *
	 * @param uid
	 * @param index
	 * @return
	 */
	public String getFiledKey(long uid, Long index) {
		long dateInt = DateUtil.getTodayInt();
		String uidKey = String.valueOf(uid).substring(10);
		return dateInt + uidKey + index;
	}

	/**
	 * 收藏卡组
	 *
	 * @param uid 玩家id
	 * @param id  分享卡组的id
	 */
	public void collectShareCardGroup(long uid, String id) {
		RDShareCardGroup cardGroup = getShareCardGroupById(id);
		collectCardGroup(cardGroup,uid);
	}

	public void collectCardGroup(RDShareCardGroup cardGroup,long uid){
		String server = userService.getOriServer(cardGroup.getUid()).getShortName();
		String nickname = userService.getGameUser(cardGroup.getUid()).getRoleInfo().getNickname();
		cardGroup.setName(String.format("%s(%s %s)", cardGroup.getName(), server, nickname));
		UserCollectCardGroup userCollectCardGroup = userService.getSingleItem(uid, UserCollectCardGroup.class);
		if (null == userCollectCardGroup) {
			userCollectCardGroup = UserCollectCardGroup.getInstance(uid);
			userService.addItem(uid, userCollectCardGroup);
		}
		userCollectCardGroup.addCardGroup(cardGroup);
		userService.updateItem(userCollectCardGroup);
	}
	/**
	 * 获取收藏卡组信息
	 *
	 * @param uid 玩家id
	 * @return 收藏卡组信息
	 */
	public RDCollectCardGroups getCollectCardGroups(long uid) {
		UserCollectCardGroup userCollectCardGroup = userService.getSingleItem(uid, UserCollectCardGroup.class);
		if (null == userCollectCardGroup) {
			userCollectCardGroup = UserCollectCardGroup.getInstance(uid);
			userService.addItem(uid, userCollectCardGroup);
		}
		return RDCollectCardGroups.getInstance(userCollectCardGroup.getCardGroups());
	}

	/**
	 * 取消收藏卡组
	 *
	 * @param uid     玩家id
	 * @param shareId 分享卡组的id
	 */
	public void delCollectCardGroup(long uid, String shareId) {
		UserCollectCardGroup userCollectCardGroup = userService.getSingleItem(uid, UserCollectCardGroup.class);
		if (null == userCollectCardGroup) {
			throw new ExceptionForClientTip("card.group.not.collect");
		}
		userCollectCardGroup.delCardGroup(shareId);
		userService.updateItem(userCollectCardGroup);
	}
}
