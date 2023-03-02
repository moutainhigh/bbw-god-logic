package com.bbw.god.gameuser.card.equipment;

import com.bbw.cache.UserCacheService;
import com.bbw.common.ListUtil;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.card.equipment.cfg.CardEquipmentAddition;
import com.bbw.god.gameuser.card.equipment.data.UserCardXianJue;
import com.bbw.god.server.ServerUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 卡牌仙诀服务工具类
 *
 * @author: huanghb
 * @date: 2022/9/15 14:16
 */
@Service
public class UserCardXianJueService {
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private UserCacheService userCacheService;
    @Autowired
    private ServerUserService serverUserService;

    /**
     * 获取玩家所有仙诀记录，
     *
     * @param uid
     * @return
     */
    public List<UserCardXianJue> getUserCardXianJues(long uid) {
        List<UserCardXianJue> userCardXianJues = this.userCacheService.getUserDatas(uid, UserCardXianJue.class);
        return userCardXianJues;
    }

    /**
     * 获取玩家所有指定类型仙诀记录，
     *
     * @param uid
     * @return
     */
    public List<UserCardXianJue> getUserCardXianJuesByXianJueType(long uid, Integer xianJueType) {
        List<UserCardXianJue> userCardXianJues = getUserCardXianJues(uid);
        return userCardXianJues.stream().filter(tmp -> tmp.getXianJueType().equals(xianJueType)).collect(Collectors.toList());
    }

    /**
     * 获取玩家指定仙诀记录
     *
     * @param uid
     * @param cardId
     * @param xianJueType
     * @return
     */
    public UserCardXianJue getUserCardXianJue(long uid, Integer cardId, Integer xianJueType) {
        List<UserCardXianJue> userCardXianJues = getUserCardXianJues(uid);
        if (ListUtil.isEmpty(userCardXianJues)) {
            return null;
        }
        //获得封神和未封神的卡牌id
        UserCardXianJue userCardXianJue = userCardXianJues.stream()
                .filter(tmp -> tmp.ifPutOnCard(cardId) && tmp.getXianJueType().equals(xianJueType))
                .findFirst()
                .orElse(null);
        return userCardXianJue;
    }

    /**
     * 获取玩家指定卡牌所有仙诀记录
     *
     * @param uid
     * @param cardId
     * @return
     */
    public List<UserCardXianJue> getUserCardXianJues(long uid, Integer cardId) {
        List<UserCardXianJue> userCardXianJues = getUserCardXianJues(uid);
        if (ListUtil.isEmpty(userCardXianJues)) {
            return new ArrayList<>();
        }
        return userCardXianJues.stream().filter(tmp -> tmp.ifPutOnCard(cardId)).collect(Collectors.toList());
    }

    /**
     * 获取玩家指定多张卡牌所有仙诀记录
     *
     * @param uid
     * @param cardIds
     * @return
     */
    public List<UserCardXianJue> getUserCardXianJues(long uid, List<Integer> cardIds) {
        List<UserCardXianJue> userCardXianJues = getUserCardXianJues(uid);
        if (ListUtil.isEmpty(userCardXianJues)) {
            return new ArrayList<>();
        }
        return userCardXianJues.stream().filter(tmp -> {
            return cardIds.stream().anyMatch(cardId -> tmp.ifPutOnCard(cardId));
        }).collect(Collectors.toList());
    }

    /**
     * 获取玩家指定仙诀记录
     *
     * @param uid
     * @param xianJueDataId
     * @return
     */
    public UserCardXianJue getUserCardXianJue(long uid, long xianJueDataId) {
        Optional<UserCardXianJue> optional = gameUserService.getUserData(uid, xianJueDataId, UserCardXianJue.class);
        if (optional.isPresent()) {
            return optional.get();
        }
        return null;
    }

    /**
     * 返回仙诀的加成集
     *
     * @param userCardXianJue
     * @return
     */
    public List<CardEquipmentAddition> getAdditions(UserCardXianJue userCardXianJue) {
        // 基本加成
        List<CardEquipmentAddition> additions = CfgXianJueTool.getBaseXianJueAddition(userCardXianJue.getXianJueType());
        if (0 >= userCardXianJue.getLevel()) {
            return additions;
        }
        // 等级加成
        List<CardEquipmentAddition> levelAdditions = CfgXianJueTool.getLevelAddition(userCardXianJue.getLevel(), userCardXianJue.getQuality(), userCardXianJue.getStarMapProgress());
        List<CardEquipmentAddition> levelAdditionToAdd = new ArrayList<>();
        for (CardEquipmentAddition addition : additions) {
            Optional<CardEquipmentAddition> optional = levelAdditions.stream().filter(tmp -> tmp.getType().intValue() == addition.getType()).findFirst();
            if (optional.isPresent()) {
                levelAdditionToAdd.add(optional.get());
            }
        }
        additions.addAll(levelAdditionToAdd);
        return additions;
    }

    /**
     * 更新仙诀
     *
     * @param userCardXianJue
     */
    public void cacheXianJue(UserCardXianJue userCardXianJue) {
        userCacheService.addUserData(userCardXianJue);
    }
}
