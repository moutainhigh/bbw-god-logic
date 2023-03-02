package com.bbw.god.gameuser.card;

import com.alibaba.fastjson.JSON;
import com.bbw.common.ListUtil;
import com.bbw.common.SpringContextUtil;
import com.bbw.db.redis.RedisHashUtil;
import com.bbw.god.db.entity.InsUserDataEntity;
import com.bbw.god.db.pool.PlayerDataDAO;
import com.bbw.god.game.combat.data.param.CCardParam;
import com.bbw.god.game.combat.data.param.CPCardGroup;
import com.bbw.god.game.data.redis.RedisKeyConst;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.UserData;
import com.bbw.god.gameuser.UserDataType;
import com.bbw.god.gameuser.redis.GameUserDataRedisUtil;
import com.bbw.god.gameuser.redis.UserRedisKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 对手卡牌服务
 *
 * @author suhq
 * @date 2020-11-20 14:47
 **/
@Service
public class OppCardService {
    private static final int KEEP_DAYS = 2 * 24 * 3600;
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private UserCardService userCardService;
    @Autowired
    private UserCardGroupService userCardGroupService;
    @Autowired
    private GameUserDataRedisUtil gameUserDataRedisUtil;

    @Autowired
    private RedisHashUtil<Integer, List<Integer>> redisHashUtil;


    /**
     * 获取对手所有卡牌
     *
     * @param oppId
     * @return
     */
    public List<UserCard> getOppAllCards(long oppId) {
        UserDataType dataType = UserDataType.fromClass(UserCard.class);
        if (gameUserDataRedisUtil.hasLoadFromDb(oppId, dataType)) {
            return userCardService.getUserCards(oppId);
        }
        if (!hasCacheFromDb(oppId, dataType)) {
            cacheUserCards(oppId);
        }
        String cacheKey = getCacheKey(oppId, dataType);
        //TODO 过期操作15天后去掉代码
        redisHashUtil.expire(cacheKey,KEEP_DAYS);
        Map<Integer, List<Integer>> cardMap = redisHashUtil.get(cacheKey);
        List<UserCard> userCards = makeUpUserCards(cardMap);
        return userCards;
    }

    /**
     * 获得防御卡组
     *
     * @param oppId
     * @return
     */
    public CPCardGroup getDefenceCards(long oppId) {
        List<UserCard> userCards = getOppAllCards(oppId);
        CardGroup cGroup = userCardGroupService.getFierceFightingCards(oppId, CardGroupWay.FIERCE_FIGHTING_DEFENSE);
        List<Integer> defenceCardIds = cGroup.getCardIds();
        if (ListUtil.isEmpty(defenceCardIds)) {
            UserCardGroup usinGroup = userCardGroupService.getUsingGroup(oppId, CardGroupWay.Normal_Fight);
            if (null == usinGroup) {
                return new CPCardGroup();
            }
            defenceCardIds = usinGroup.getCards();
            userCardGroupService.setFierceFightingGroup(oppId, defenceCardIds, CardGroupWay.FIERCE_FIGHTING_DEFENSE);
        }
        List<Integer> finalDefenceCardIds = defenceCardIds;
        List<UserCard> list = userCards.stream().filter(tmp -> finalDefenceCardIds.contains(tmp.getBaseId())).collect(Collectors.toList());
        List<CCardParam> cardParams=new ArrayList<>();
        for (UserCard userCard : list) {
            cardParams.add(CCardParam.init(userCard));
        }
        return CPCardGroup.getInstance(oppId, cGroup.getFuCeId(), cardParams);
    }

    /**
     * 清除缓存
     *
     * @param uid
     */
    public void clearCache(long uid) {
        UserDataType dataType = UserDataType.fromClass(UserCard.class);
        redisHashUtil.delete(getCacheKey(uid, dataType));
    }

    /**
     * 获得要清除的key
     *
     * @param uid
     */
    public String getKeyToDel(long uid) {
        UserDataType dataType = UserDataType.fromClass(UserCard.class);
        return getCacheKey(uid, dataType);
    }

    /**
     * 组装userCards
     *
     * @param cardMap
     * @return
     */
    private List<UserCard> makeUpUserCards(Map<Integer, List<Integer>> cardMap) {
        List<UserCard> userCards = new ArrayList<>();
        for (Integer cardId : cardMap.keySet()) {
            List<Integer> cardData = cardMap.get(cardId);
            UserCard userCard = new UserCard();
            userCard.setBaseId(cardId);
            userCard.setLevel(cardData.get(0));
            userCard.setHierarchy(cardData.get(1));
            if (cardData.subList(2, cardData.size() - 1).stream().anyMatch(tmp -> tmp != 0)) {
                UserCard.UserCardStrengthenInfo strengthInfo = new UserCard.UserCardStrengthenInfo();
                if (cardData.get(2) > 0) {
                    strengthInfo.updateCurrentSkill(CardSkillPosEnum.SKILL_0,cardData.get(2));
                }
                if (cardData.get(3) > 0) {
                    strengthInfo.updateCurrentSkill(CardSkillPosEnum.SKILL_5,cardData.get(3));
                }
                if (cardData.get(4) > 0) {
                    strengthInfo.updateCurrentSkill(CardSkillPosEnum.SKILL_10,cardData.get(4));
                }
                if (cardData.get(5) > 0) {
                    strengthInfo.setAttackSymbol(cardData.get(5));
                }
                if (cardData.get(6) > 0) {
                    strengthInfo.setDefenceSymbol(cardData.get(6));
                }
                userCard.setStrengthenInfo(strengthInfo);
            }
            userCards.add(userCard);
        }
        return userCards;
    }
    /**
     * 缓存卡牌
     *
     * @param uid
     */
    private void cacheUserCards(long uid) {
        List<UserCard> cards = getUserDatasFromDB(uid, UserCard.class);
        Map<Integer, List<Integer>> cardMap = new HashMap<>();
        if (ListUtil.isNotEmpty(cards)) {
            for (UserCard card : cards) {
                List<Integer> cardData = new ArrayList<>();
                cardData.add(null == card.getLevel() ? 0 : card.getLevel());
                cardData.add(null == card.getHierarchy() ? 0 : card.getHierarchy());
                int s0 = 0, s5 = 0, s10 = 0, attackSymbol = 0, defenceSymbol = 0;
                UserCard.UserCardStrengthenInfo strengthenInfo = card.getStrengthenInfo();
                if (null != strengthenInfo) {
                    if (null != strengthenInfo.gainSkill0()) {
                        s0 = strengthenInfo.gainSkill0();
                    }
                    if (null != strengthenInfo.gainSkill5()) {
                        s5 = strengthenInfo.gainSkill5();
                    }
                    if (null != strengthenInfo.gainSkill10()) {
                        s10 = strengthenInfo.gainSkill10();
                    }
                    if (null != strengthenInfo.gainAttackSymbol()) {
                        attackSymbol = strengthenInfo.gainAttackSymbol();
                    }
                    if (null != strengthenInfo.gainDefenceSymbol()) {
                        defenceSymbol = strengthenInfo.gainDefenceSymbol();
                    }
                }
                cardData.add(s0);
                cardData.add(s5);
                cardData.add(s10);
                cardData.add(attackSymbol);
                cardData.add(defenceSymbol);
                cardMap.put(card.getBaseId(), cardData);
            }
        }
        String cacheKey = getCacheKey(uid, UserDataType.fromClass(UserCard.class));
        redisHashUtil.putAllField(cacheKey, cardMap);
        redisHashUtil.expire(cacheKey,KEEP_DAYS);
    }


    /**
     * 从数据库中直接获取数据
     *
     * @author suhq
     * @date 2020-11-20 16:06
     **/
    private <T extends UserData> List<T> getUserDatasFromDB(long uid, Class<T> clazz) {
        int sid = gameUserService.getActiveSid(uid);
        PlayerDataDAO pdd = SpringContextUtil.getBean(PlayerDataDAO.class, sid);
        UserDataType dataType = UserDataType.fromClass(clazz);
        List<InsUserDataEntity> dbDatas = pdd.dbSelectUserDataByType(uid, dataType.getRedisKey());
        List<T> datas = new ArrayList<>();
        for (InsUserDataEntity entity : dbDatas) {
            T userData = JSON.parseObject(entity.getDataJson(), clazz);
            datas.add(userData);
        }
        return datas;
    }


    /**
     * 是否已缓存
     *
     * @param uid
     * @param dataType
     * @return
     */
    private boolean hasCacheFromDb(Long uid, UserDataType dataType) {
        String cacheKey = getCacheKey(uid, dataType);
        return redisHashUtil.exists(cacheKey);
    }

    /**
     * 缓存的key
     *
     * @param uid
     * @param userDataType
     * @return usr:[uid]:var:userDataCache:[userDataCache]
     */
    private String getCacheKey(long uid, UserDataType userDataType) {
        return UserRedisKey.getRunTimeVarKey(uid, "userDataCache") + RedisKeyConst.SPLIT + userDataType.getRedisKey();
    }

}
