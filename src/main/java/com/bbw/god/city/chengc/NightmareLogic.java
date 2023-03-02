package com.bbw.god.city.chengc;

import com.bbw.common.ListUtil;
import com.bbw.common.PowerRandom;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.cache.TimeLimitCacheUtil;
import com.bbw.god.city.UserCityService;
import com.bbw.god.game.combat.data.param.CPCardGroup;
import com.bbw.god.game.config.card.CardTool;
import com.bbw.god.game.config.card.CfgDeifyCardEntity;
import com.bbw.god.game.config.city.CfgCityEntity;
import com.bbw.god.game.config.city.CityTool;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.card.*;
import com.bbw.god.gameuser.leadercard.LeaderCardTool;
import com.bbw.god.gameuser.leadercard.service.LeaderCardService;
import com.bbw.god.nightmarecity.chengc.UserNightmareCity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 梦魇世界逻辑
 * @author：lwb
 * @date: 2020/12/24 10:19
 * @version: 1.0
 */
@Service
public class NightmareLogic {
    @Autowired
    private UserCityService userCityService;
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private UserCardService userCardService;
    @Autowired
    private LeaderCardService leaderCardService;
    public static final int HU_WEI_CARDS=0;
    public static final int JIN_WEI_CARDS=1;

    /**
     * 获取当前所在城池的攻城卡组
     *
     * @param uid
     * @return
     */
    public RDCardGroups getAttackCardGroup(long uid) {
        ChengChiInfoCache cache = TimeLimitCacheUtil.getChengChiInfoCache(uid);
        int cityId = cache.getCityId();
        UserNightmareCity nightmareCity = userCityService.getUserNightmareCity(uid, cityId);
        updateFengShenCardId(uid, nightmareCity);
        RDCardGroups rd = new RDCardGroups();
        rd.setCardGroupStatus(0);
        if (nightmareCity != null) {
            boolean present = leaderCardService.getUserLeaderCardOp(uid).isPresent();
            if (!nightmareCity.getHuWeiCards().contains(LeaderCardTool.getLeaderCardId()) && present) {
                nightmareCity.getHuWeiCards().add(LeaderCardTool.getLeaderCardId());

            }
            if (!nightmareCity.getJinWeiCards().contains(LeaderCardTool.getLeaderCardId()) && present) {
                nightmareCity.getJinWeiCards().add(LeaderCardTool.getLeaderCardId());
            }
            rd.addCardIds(CardGroupWay.NIGHTMARE_JIN_WEI, nightmareCity.getJinWeiCards(), nightmareCity.getJinWeiFuCe());
            rd.addCardIds(CardGroupWay.NIGHTMARE_HU_WEI, nightmareCity.getHuWeiCards(), nightmareCity.getHuWeiFuCe());
        }
        boolean ifAttackCity = cache.ifAttackCity(true);
        if (ifAttackCity) {
            rd.setCardGroupStatus(-1);
        }
        return rd;
    }

    /**
     * 同步卡组:同步规则 优先同步 同属性 高级城 胜利的卡组
     *
     * @param uid
     * @return
     */
    public RDCardGroups synAttackCardGroup(long uid) {
        ChengChiInfoCache cache = TimeLimitCacheUtil.getChengChiInfoCache(uid);
//        if (cache.ifAttackCity(true)){
//            //不允许同步
//            throw new ExceptionForClientTip("nightmare.cardGroup.no.syn");
//        }
        int cityId = cache.getCityId();
        //同属性的所有城池卡组
        Map<Integer, List<Integer>> listMap = CityTool.getAllCitiesId(cache.getCityProperty());
        List<UserNightmareCity> nightmareCities = userCityService.getUserNightmareCities(uid);
        //优先获取拥有的城池
        UserNightmareCity targetNightmareCity=null;
        boolean mustOwn=nightmareCities.stream().filter(p->p.isOwn() && p.gainCity().getProperty().equals(cache.getCityProperty())).findFirst().isPresent();
        //同步卡组阵容时，优先同步 同属性 高级城 胜利的卡组
        if (ListUtil.isNotEmpty(nightmareCities)){
            for (int i = 5; i >0 ; i--) {
                List<Integer> list = listMap.get(i);
                List<UserNightmareCity> collect = nightmareCities.stream().filter(p -> (p.isOwn() || !mustOwn) && list.contains(p.getBaseId())).collect(Collectors.toList());
                if (ListUtil.isNotEmpty(collect)){
                    targetNightmareCity= PowerRandom.getRandomFromList(collect);
                    break;
                }
            }
        }
        if (targetNightmareCity==null){
           //没有可以选择的
            throw new ExceptionForClientTip("nightmare.cardGroup.not.syn.city");
        }
        updateFengShenCardId(uid,targetNightmareCity);
        UserNightmareCity nightmareCity = userCityService.getUserNightmareCity(uid, cityId);
        RDCardGroups rd = new RDCardGroups();
        rd.setCardGroupStatus(0);
        if (nightmareCity==null){
            CfgCityEntity city = CityTool.getCityById(cityId);
            nightmareCity=UserNightmareCity.getInstance(city,uid);
            gameUserService.addItem(uid,nightmareCity);
        }
        boolean isPresentLeaderCard = leaderCardService.getUserLeaderCardOp(uid).isPresent();
        nightmareCity.setHuWeiCards(targetNightmareCity.getHuWeiCards());
        nightmareCity.setHuWeiFuCe(targetNightmareCity.getHuWeiFuCe());
        nightmareCity.setJinWeiCards(targetNightmareCity.getJinWeiCards());
        nightmareCity.setJinWeiFuCe(targetNightmareCity.getJinWeiFuCe());
        if (!nightmareCity.getJinWeiCards().contains(LeaderCardTool.getLeaderCardId()) && isPresentLeaderCard) {
            nightmareCity.getJinWeiCards().add(0, LeaderCardTool.getLeaderCardId());
        }
        gameUserService.updateItem(nightmareCity);
        rd.addCardIds(CardGroupWay.NIGHTMARE_JIN_WEI, nightmareCity.getJinWeiCards(), nightmareCity.getJinWeiFuCe());
        rd.addCardIds(CardGroupWay.NIGHTMARE_HU_WEI, nightmareCity.getHuWeiCards(), nightmareCity.getHuWeiFuCe());
        return rd;
    }

    /**
     * 设置攻城卡组
     *
     * @param uid
     * @param cardIds
     * @param type
     */
    public void setAttackCardGroup(long uid, String cardIds, int type) {
        ChengChiInfoCache cache = TimeLimitCacheUtil.getChengChiInfoCache(uid);
        int cityId = cache.getCityId();
        List<Integer> ids = CardParamParser.parseGroupParam(cardIds);
        if (ListUtil.isEmpty(ids)) {
            //不允许保存空数组
            throw new ExceptionForClientTip("card.grouping.not.blank");
        }
        Integer property = cache.getCityProperty();
        if (!CardChecker.isSameType(property, ids)) {
            throw new ExceptionForClientTip("card.grouping.not.the.same.type");
        }
        UserNightmareCity nightmareCity = userCityService.getUserNightmareCity(uid, cityId);
        if (nightmareCity == null) {
            CfgCityEntity city = CityTool.getCityById(cityId);
            nightmareCity = UserNightmareCity.getInstance(city, uid);
            gameUserService.addItem(uid, nightmareCity);
        }
        List<Integer> cardsByType = nightmareCity.getCardsByType(HU_WEI_CARDS == type ? JIN_WEI_CARDS : HU_WEI_CARDS);
        if (!cardsByType.isEmpty()) {
            Optional<Integer> optional = ids.stream().filter(p -> LeaderCardTool.getLeaderCardId() != p && cardsByType.contains(p)).findFirst();
            if (optional.isPresent()) {
                //不允许保存相同卡牌
                throw new ExceptionForClientTip("nightmare.card.no.repeat");
            }
        }
        if (JIN_WEI_CARDS == type) {
            nightmareCity.setJinWeiCards(ids);
        } else {
            nightmareCity.setHuWeiCards(ids);
        }
        gameUserService.updateItem(nightmareCity);
    }

    /**
     * 设置符册
     *
     * @param uid
     * @param fuCeId
     * @param cardGroupWay
     */
    public void setAttackFuCe(long uid, Integer fuCeId, CardGroupWay cardGroupWay) {
        ChengChiInfoCache cache = TimeLimitCacheUtil.getChengChiInfoCache(uid);
        int cityId = cache.getCityId();
        UserNightmareCity nightmareCity = userCityService.getUserNightmareCity(uid, cityId);
        if (nightmareCity == null) {
            CfgCityEntity city = CityTool.getCityById(cityId);
            nightmareCity = UserNightmareCity.getInstance(city, uid);
            gameUserService.addItem(uid, nightmareCity);
        }
        if (CardGroupWay.NIGHTMARE_JIN_WEI == cardGroupWay) {
            nightmareCity.setJinWeiFuCe(fuCeId);
        } else {
            nightmareCity.setHuWeiFuCe(fuCeId);
        }
        gameUserService.updateItem(nightmareCity);
    }

    /**
     * 获取玩家梦魇攻城卡组
     *
     * @param uid
     * @param cityId  城池ID
     * @param isHuwei 是否是护卫军
     * @return
     */
    public CPCardGroup getUserNightmareCityCards(long uid, int cityId, boolean isHuwei) {
        UserNightmareCity nightmareCity = userCityService.getUserNightmareCity(uid, cityId);
        if (nightmareCity == null) {
            CfgCityEntity city = CityTool.getCityById(cityId);
            nightmareCity = UserNightmareCity.getInstance(city, uid);
            gameUserService.addItem(uid, nightmareCity);
        }
        if (ListUtil.isEmpty(nightmareCity.getJinWeiCards())) {
            throw new ExceptionForClientTip("nightmare.cardGroup.empty");
        }
        if (nightmareCity.getJinWeiCards().size() == 1 && nightmareCity.getJinWeiCards().contains(LeaderCardTool.getLeaderCardId())) {
            throw new ExceptionForClientTip("nightmare.cardGroup.empty");
        }
        updateFengShenCardId(uid, nightmareCity);
        List<UserCard> userCards = userCardService.getUserCards(uid, nightmareCity.getJinWeiCards());
        return CPCardGroup.getInstanceByUserCards(uid, nightmareCity.getJinWeiFuCe(), userCards);

    }

    /**
     * 更新封神卡的信息
     * @param uid
     * @param nightmareCity
     */
    private void updateFengShenCardId(long uid,UserNightmareCity nightmareCity){
        if (nightmareCity==null){
            return;
        }
        boolean needUpdate=false;
        List<Integer> list = CardTool.getAllDeifyCards().stream().map(CfgDeifyCardEntity::getId).collect(Collectors.toList());
        if (ListUtil.isNotEmpty(nightmareCity.getHuWeiCards())){
            List<Integer> newIds=new ArrayList<>();
            for (int id : nightmareCity.getHuWeiCards()) {
                if (list.contains(id+10000)){
                    UserCard userCard = userCardService.getUserCard(uid, id + 10000);
                    if (userCard!=null){
                        newIds.add(userCard.getBaseId());
                        needUpdate=true;
                        continue;
                    }
                }
                newIds.add(id);
            }
            nightmareCity.setHuWeiCards(newIds);
        }
        if (ListUtil.isNotEmpty(nightmareCity.getJinWeiCards())){
            List<Integer> newIds2=new ArrayList<>();
            for (int id : nightmareCity.getJinWeiCards()) {
                if (list.contains(id+10000)){
                    UserCard userCard = userCardService.getUserCard(uid, id + 10000);
                    if (userCard!=null){
                        newIds2.add(userCard.getBaseId());
                        needUpdate=true;
                        continue;
                    }
                }
                newIds2.add(id);
            }
            nightmareCity.setJinWeiCards(newIds2);
        }
        if (needUpdate){
            gameUserService.updateItem(nightmareCity);
        }
    }
}
