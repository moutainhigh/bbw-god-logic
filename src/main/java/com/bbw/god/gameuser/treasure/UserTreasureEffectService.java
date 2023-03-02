package com.bbw.god.gameuser.treasure;

import com.bbw.cache.UserCacheService;
import com.bbw.common.ListUtil;
import com.bbw.god.fight.RDFightResult;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.treasure.CfgTreasureEntity;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.game.config.treasure.TreasureTool;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class UserTreasureEffectService {
    @Autowired
    private UserCacheService userCacheService;

    public List<UserTreasureEffect> getAllEffects(long uid) {
        return userCacheService.getUserDatas(uid, UserTreasureEffect.class);
    }

    public UserTreasureEffect getEffect(long uid, int treasureId) {
        return userCacheService.getCfgItem(uid, treasureId, UserTreasureEffect.class);
    }

    public void addTreasureEffect(UserTreasureEffect ute) {
        userCacheService.addUserData(ute);
    }

    public void delTreasureEffects(List<UserTreasureEffect> effects) {
        userCacheService.delUserDatas(effects);
    }

    /**
     * 获得道具效果记录，如果有重复，则删除重复的记录
     *
     * @param guId
     * @return
     */
    public List<UserTreasureEffect> getTreasureEffects(long guId) {
        List<UserTreasureEffect> utEffects = getAllEffects(guId);
        if (ListUtil.isNotEmpty(utEffects)) {

            List<Integer> effectTreasureIds = new ArrayList<>();
            List<UserTreasureEffect> needsToDel = new ArrayList<>();
            for (UserTreasureEffect ute : utEffects) {
                if (effectTreasureIds.contains(ute.getBaseId())) {
                    needsToDel.add(ute);
                    log.error("{}有道具效果{}", guId, ute);
                    continue;
                }
                effectTreasureIds.add(ute.getBaseId());
            }
            // 去重
            if (needsToDel.size() > 0) {
                log.error("{}有{}个重复的道具效果{},现进行去重", guId);
                delTreasureEffects(needsToDel);
                utEffects = getAllEffects(guId);
            }

        }
        return utEffects;
    }

    /**
     * 法宝是否生效中
     *
     * @param uid
     * @param treasureId
     * @return
     */
    public boolean isTreasureEffect(long uid, int treasureId) {
        UserTreasureEffect ute = getEffect(uid, treasureId);
        return isTreasureEffect(ute);
    }

    /**
     * 法宝是否生效中
     *
     * @param effect
     * @return
     */
    public boolean isTreasureEffect(UserTreasureEffect effect) {
        return effect != null && (effect.getRemainEffect() > 0 || (effect.getRemainEffect() == TreasureEnum.MBX.getValue() && effect.getRemainEffect() == -1));
    }

    /**
     * 落宝金钱效果
     *
     * @param gu
     * @param rd
     */
    public void effectAsLBJQ(GameUser gu, RDFightResult rd) {
        long uid = gu.getId();
        if (isTreasureEffect(uid, TreasureEnum.LBJQ.getValue())) {
            if (rd.getWin() == 1) {
                CfgTreasureEntity treasure = TreasureTool.getRandomGood(50, 100, 250);
                TreasureEventPublisher.pubTAddEvent(uid, treasure.getId(), 1, WayEnum.EFFECT_LBJQ, rd);
            }

            TreasureEventPublisher.pubTEffectDeductEvent(uid, TreasureEnum.LBJQ.getValue(), 1, WayEnum.NONE);
        }
    }

}
