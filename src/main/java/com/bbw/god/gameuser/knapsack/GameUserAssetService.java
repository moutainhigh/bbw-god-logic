package com.bbw.god.gameuser.knapsack;

import com.bbw.god.game.config.treasure.CfgTreasureEntity;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.game.config.treasure.TreasureTool;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.card.UserCard;
import com.bbw.god.gameuser.card.UserCardService;
import com.bbw.god.gameuser.special.UserSpecial;
import com.bbw.god.gameuser.special.UserSpecialService;
import com.bbw.god.gameuser.treasure.UserTreasure;
import com.bbw.god.gameuser.treasure.UserTreasureEffect;
import com.bbw.god.gameuser.treasure.UserTreasureEffectService;
import com.bbw.god.gameuser.treasure.UserTreasureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 玩家资产获取
 *
 * @author lwb
 * @date 2020/4/3 10:33
 */
@Service
public class GameUserAssetService {
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private UserTreasureService userTreasureService;
    @Autowired
    private UserTreasureEffectService userTreasureEffectService;
    @Autowired
    private UserSpecialService userSpecialService;
    @Autowired
    private UserCardService userCardService;

    /**
     * 根据类型获取玩家的道具资源
     *
     * @param type
     * @param uid
     * @return
     */
    public RDUserAsset getUserAssetList(UserAssetEnum type, long uid) {
        RDUserAsset rd = new RDUserAsset();
        List<RDUserAsset.UserAsset> list = new ArrayList<>();
        if (UserAssetEnum.SPECIAL_LOCAL_PRODUCT.getType() == type.getType()) {
            //特产 特殊获取
            List<UserSpecial> userSpecials = userSpecialService.getOwnSpecials(uid);
            if (userSpecials != null && !userSpecials.isEmpty()) {
                for (UserSpecial userSpecial : userSpecials) {
                    list.add(RDUserAsset.UserAsset.instance(userSpecial.getBaseId(), 1));
                }
            }
            rd.setUserAssets(list);
            return rd;
        }
        //以下玩家的物品皆有包含在法宝中，卡牌灵石除外
        List<UserTreasure> userTreasures = getUserTreasures(uid, type);
        if (userTreasures != null && !userTreasures.isEmpty()) {
            for (UserTreasure ut : userTreasures) {
                list.add(RDUserAsset.UserAsset.instance(ut.getBaseId(), ut.gainTotalNum()));
            }
        }
        if (UserAssetEnum.LINGSHI.getType() == type.getType()) {
            //获取灵石时需要补充卡牌灵石
            List<UserCard> userCards = userCardService.getUserCards(uid);
            for (UserCard card : userCards) {
                if (card.getLingshi() > 0) {
                    list.add(RDUserAsset.UserAsset.instance(card.getBaseId(), card.getLingshi()));
                }
            }
        }
        rd.setUserAssets(list);
        return rd;
    }

    /**
     * 获取快捷地图法宝以及对应的状态
     *
     * @param uid
     * @return
     */
    public RDUserAsset getFastMapTreasureList(long uid) {
        RDUserAsset rd = new RDUserAsset();
        List<RDUserAsset.UserAsset> list = new ArrayList<>();
        List<Integer> mapTreasureIds = getTreasuresIds(UserAssetEnum.FAST_MAP_TREASURE);
        List<UserTreasure> userTreasures = getUserTreasures(uid, UserAssetEnum.FAST_MAP_TREASURE);
        //添加法宝使用效果
        List<UserTreasureEffect> effects = userTreasureEffectService.getTreasureEffects(uid);
        for (Integer id : mapTreasureIds) {
            RDUserAsset.UserAsset userAsset = RDUserAsset.UserAsset.instance(id);
            if (id == TreasureEnum.MBX.getValue()) {
                int status = gameUserService.getGameUser(uid).getSetting().getActiveMbx();
                userAsset.setStatus(status);
            } else {
                Optional<UserTreasureEffect> effectOp = effects.stream().filter(p -> p.getBaseId() == id.intValue()).findFirst();
                if (effectOp.isPresent()) {
                    userAsset.setStatus(effectOp.get().getRemainEffect());
                }
            }
            Optional<UserTreasure> optional = userTreasures.stream().filter(p -> p.getBaseId() == id.intValue()).findFirst();
            if (optional.isPresent()) {
                userAsset.setNum(optional.get().gainTotalNum());
            }
            list.add(userAsset);
        }
        rd.setUserAssets(list);
        return rd;
    }

    /**
     * 根据道具分类 获取玩家的法宝实体对象
     *
     * @param uid
     * @param type
     * @return
     */
    private List<UserTreasure> getUserTreasures(long uid, UserAssetEnum type) {
        List<Integer> ids = getTreasuresIds(type);
        if (ids == null || ids.isEmpty()) {
            return new ArrayList<>();
        }
        List<UserTreasure> userTreasures = userTreasureService.getAllUserTreasures(uid);
        if (userTreasures == null) {
            userTreasures = new ArrayList<>();
        }
        return userTreasures.stream().filter(p -> ids.contains(p.getBaseId()) && p.gainTotalNum() > 0).collect(Collectors.toList());
    }

    /**
     * 根据配置的分类 获取对应类型的法宝id集合
     *
     * @param type
     * @return
     */
    private List<Integer> getTreasuresIds(UserAssetEnum type) {
        List<Integer> types = type.getTreasureTypes();
        if (types.isEmpty()) {
            return null;
        }
        List<CfgTreasureEntity> cfgList = TreasureTool.getAllTreasures();
        return cfgList.stream().filter(p -> types.contains(p.getType())).map(CfgTreasureEntity::getId).collect(Collectors.toList());
    }

    /**
     * 获取玩家拥有的头像ID
     *
     * @param uid
     * @return
     */
    public List<Integer> getAllHeadIcons(long uid) {
        List<Integer> ids = new ArrayList<>();
        ids.add(TreasureEnum.HEAD_ICON_Normal.getValue());
        List<UserTreasure> userTreasures = userTreasureService.getAllUserTreasures(uid);
        if (userTreasures == null || userTreasures.isEmpty()) {
            return ids;
        }
        List<Integer> cfgHeadIconIds = TreasureTool.getAllIconIds();
        for (UserTreasure treasure:userTreasures) {
			if (treasure!=null && cfgHeadIconIds.contains(treasure.getBaseId())) {
				ids.add(treasure.getBaseId());
			}
		}
        return ids;
    }

    /**
     * 获取玩家拥有的特殊头像Id集
     *
     * @param uid
     * @return
     */
    public List<Integer> getAllHeadIds(long uid) {
        List<Integer> ids = new ArrayList<>();
        List<UserTreasure> userTreasures = userTreasureService.getAllUserTreasures(uid);
        if (userTreasures == null || userTreasures.isEmpty()) {
            return ids;
        }
        List<Integer> cfgHeadIds = TreasureTool.getAllHeadIds();
        return userTreasures.stream().filter(p -> cfgHeadIds.contains(p.getBaseId())).map(UserTreasure::getBaseId).collect(Collectors.toList());
    }
}
