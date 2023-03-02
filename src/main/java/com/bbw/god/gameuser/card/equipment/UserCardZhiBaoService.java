package com.bbw.god.gameuser.card.equipment;

import com.bbw.cache.UserCacheService;
import com.bbw.common.ListUtil;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.card.equipment.cfg.CardEquipmentAddition;
import com.bbw.god.gameuser.card.equipment.data.UserCardZhiBao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 卡牌装备服务工具类
 *
 * @author: huanghb
 * @date: 2022/9/15 14:16
 */
@Service
public class UserCardZhiBaoService {
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private UserCacheService userCacheService;

    /**
     * 获取玩家所有至宝记录，
     *
     * @param uid
     * @return
     */
    public List<UserCardZhiBao> getUserCardZhiBaos(long uid) {
        List<UserCardZhiBao> userCardZhiBaos = this.userCacheService.getUserDatas(uid, UserCardZhiBao.class);
        return userCardZhiBaos;
    }

    /**
     * 扣除指定至宝
     *
     * @param uid
     * @param zhiBaoDataId
     * @return
     */
    public void deductZhiBao(long uid, long zhiBaoDataId) {
        UserCardZhiBao userCardZhiBao = getUserCardZhiBao(uid, zhiBaoDataId);
        if (null == userCardZhiBao) {
            return;
        }
        userCacheService.delUserData(userCardZhiBao);
        return;
    }

    /**
     * 获取玩家指定卡牌所有至宝记录
     *
     * @param uid
     * @param cardId
     * @return
     */
    public List<UserCardZhiBao> getUserCardZhiBaos(long uid, Integer cardId) {
        List<UserCardZhiBao> userCardZhiBaos = getUserCardZhiBaos(uid);
        if (ListUtil.isEmpty(userCardZhiBaos)) {
            return new ArrayList<>();
        }
        return userCardZhiBaos.stream().filter(tmp -> tmp.ifPutOnCard(cardId)).collect(Collectors.toList());
    }

    /**
     * 获取玩家指定多张卡牌所有至宝记录
     *
     * @param uid
     * @param cardIds
     * @return
     */
    public List<UserCardZhiBao> getUserCardZhiBaos(long uid, List<Integer> cardIds) {
        List<UserCardZhiBao> userCardZhiBaos = getUserCardZhiBaos(uid);
        if (ListUtil.isEmpty(userCardZhiBaos)) {
            return new ArrayList<>();
        }
        return userCardZhiBaos.stream().filter(tmp -> {
            return cardIds.stream().anyMatch(cardId -> tmp.ifPutOnCard(cardId));
        }).collect(Collectors.toList());
    }

    /**
     * 获取指定至宝记录，
     *
     * @param uid
     * @return
     */
    public UserCardZhiBao getUserCardZhiBao(long uid, Integer cardId, Integer zhiBaoId) {
        List<UserCardZhiBao> userCardZhiBaos = getUserCardZhiBaos(uid);
        if (ListUtil.isEmpty(userCardZhiBaos)) {
            return null;
        }
        UserCardZhiBao userCardZhiBao = userCardZhiBaos.stream()
                .filter(tmp -> tmp.getCardId().equals(cardId) && tmp.getZhiBaoId().equals(zhiBaoId))
                .findFirst()
                .orElse(null);
        return userCardZhiBao;
    }

    /**
     * 获取指定装配位置至宝记录，
     *
     * @param uid
     * @return
     */
    public UserCardZhiBao getUserCardZhiBaoByZhiBaoType(long uid, Integer cardId, int zhiBaoType) {
        List<UserCardZhiBao> userCardZhiBaos = getUserCardZhiBaos(uid);
        if (ListUtil.isEmpty(userCardZhiBaos)) {
            return null;
        }
        UserCardZhiBao userCardZhiBao = userCardZhiBaos.stream()
                .filter(tmp -> tmp.ifPutOnCard(cardId) && (tmp.getZhiBaoId() / 100) == zhiBaoType)
                .findFirst()
                .orElse(null);
        return userCardZhiBao;
    }

    /**
     * 获取指定至宝记录
     *
     * @param uid
     * @param zhiBaoDataId
     * @return
     */
    public UserCardZhiBao getUserCardZhiBao(long uid, long zhiBaoDataId) {
        if (0 == zhiBaoDataId) {
            return null;
        }
        Optional<UserCardZhiBao> optional = gameUserService.getUserData(uid, zhiBaoDataId, UserCardZhiBao.class);
        if (optional.isPresent()) {
            return optional.get();
        }
        return null;
    }

    /**
     * 登录时获取至宝
     *
     * @param uid
     * @return
     */
    public List<UserCardZhiBao> getUserCardZhiBaosAsLogin(long uid) {
        List<UserCardZhiBao> userCardZhiBaos = getUserCardZhiBaos(uid);
        return userCardZhiBaos;
    }

    /**
     * 获得至宝技能
     *
     * @param userCardZhiBao
     * @return
     */
    private List<Integer> getZhiBaoskills(UserCardZhiBao userCardZhiBao) {
        List<Integer> skills = new ArrayList<>();
        for (int i = 0; i < userCardZhiBao.getSkillGroup().length; i++) {
            if (0 == userCardZhiBao.getSkillGroup()[i]) {
                continue;
            }
            skills.add(userCardZhiBao.getSkillGroup()[i]);
        }
        return skills;
    }

    /**
     * 获得已穿戴的至宝信息
     *
     * @param uid
     * @param cardId
     * @return
     */
    public UserCardZhiBao[] getTakedZhiBaos(long uid, Integer cardId) {
        UserCardZhiBao[] zhiBaos = new UserCardZhiBao[]{null, null};
        List<UserCardZhiBao> userCardZhiBaos = getUserCardZhiBaos(uid, cardId);
        if (ListUtil.isEmpty(userCardZhiBaos)) {
            return zhiBaos;
        }
        for (int i = 0; i < userCardZhiBaos.size(); i++) {
            zhiBaos[i] = userCardZhiBaos.get(i);
        }
        return zhiBaos;
    }


    /**
     * 返回至宝的加成集
     *
     * @param userCardZhiBao
     * @return
     */
    public List<CardEquipmentAddition> getAdditions(UserCardZhiBao userCardZhiBao) {
        // 至宝属性加成
        List<CardEquipmentAddition> additions = userCardZhiBao.gainAdditions();
        return additions;
    }

    /**
     * 更新至宝
     *
     * @param userCardZhiBao
     */
    public void cacheZhiBao(UserCardZhiBao userCardZhiBao) {
        userCacheService.addUserData(userCardZhiBao);
    }

}
