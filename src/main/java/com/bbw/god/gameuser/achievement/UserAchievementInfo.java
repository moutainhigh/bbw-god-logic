package com.bbw.god.gameuser.achievement;

import com.alibaba.fastjson.annotation.JSONField;
import com.bbw.common.ID;
import com.bbw.common.ListUtil;
import com.bbw.db.redis.serializer.BitSetDeserializer;
import com.bbw.db.redis.serializer.BitSetSerializer;
import com.bbw.god.gameuser.UserDataType;
import com.bbw.god.gameuser.UserSingleObj;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author suchaobin
 * @description 玩家成就信息
 * @date 2020/5/13 15:29
 **/
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserAchievementInfo extends UserSingleObj implements Serializable {
    private static final long serialVersionUID = -293393547892320274L;

    /**
     * 已完成未领取的成就
     */
    @JSONField(serializeUsing = BitSetSerializer.class, deserializeUsing = BitSetDeserializer.class)
    private BitSet accomplishedIds = new BitSet();
    /**
     * 已领取的成就
     */
    @JSONField(serializeUsing = BitSetSerializer.class, deserializeUsing = BitSetDeserializer.class)
    private BitSet awardedIds = new BitSet();
    /**
     * 最近完成的成就id集合
     */
    private List<Integer> recentAccomplishedIds = new LinkedList<>();
    /**
     * 已验证成就
     */
    private List<Integer> verifyAccomplishedIds = new ArrayList<>();

    public UserAchievementInfo(long uid, List<Integer> accomplishedIds, List<Integer> awardedIds,
                               List<Integer> recentAccomplishedIds) {
        this.id = ID.INSTANCE.nextId();
        this.gameUserId = uid;
        accomplishedIds.forEach(accomplishedId -> this.accomplishedIds.set(accomplishedId));
        awardedIds.forEach(awardedId -> this.awardedIds.set(awardedId));
        this.recentAccomplishedIds = recentAccomplishedIds;
        this.accomplishedIds.andNot(this.awardedIds);
    }

    /**
     * 完成成就
     *
     * @param achievementId 成就id
     */
    public void accomplishAchievement(int achievementId) {
        // 添加到已完成未领取的bitset中
        this.accomplishedIds.set(achievementId);
        // 添加到最近完成成就中
        int limit = AchievementTool.getCfgAchievement().getRecentNumToShow();
        if (this.recentAccomplishedIds.contains(achievementId)) {
            return;
        }
        if (this.recentAccomplishedIds.size() >= limit) {
            this.recentAccomplishedIds.remove(0);
        }
        this.recentAccomplishedIds.add(achievementId);
        this.accomplishedIds.andNot(this.awardedIds);
    }

    /**
     * 领取成就奖励
     *
     * @param achievementId 成就id
     */
    public void awardedAchievement(int achievementId) {
        this.accomplishedIds.clear(achievementId);
        this.awardedIds.set(achievementId);
        this.accomplishedIds.andNot(this.awardedIds);
    }

    /**
     * 获取成就状态
     *
     * @param achievementId 成就id
     * @return
     */
    public int getAchievementStatus(int achievementId) {
        if (this.awardedIds.get(achievementId)) {
            return AchievementStatusEnum.AWARED.getValue();
        } else if (this.accomplishedIds.get(achievementId)) {
            return AchievementStatusEnum.ACCOMPLISHED.getValue();
        }
        return AchievementStatusEnum.NO_ACCOMPLISHED.getValue();
    }

    /**
     * 清除成就
     *
     * @param achievementIds
     */
    public void clearAchievement(List<Integer> achievementIds) {
        if (ListUtil.isEmpty(achievementIds)) {
            return;
        }
        for (Integer achievementId : achievementIds) {
            this.accomplishedIds.clear(achievementId);
            this.awardedIds.clear(achievementId);
        }
        this.recentAccomplishedIds = this.recentAccomplishedIds.stream().filter(p -> !achievementIds.contains(p)).collect(Collectors.toList());
    }

    /**
     * 玩家资源类型
     *
     * @return 玩家资源类型
     */
    @Override
    public UserDataType gainResType() {
        return UserDataType.USER_ACHIEVEMENT_INFO;
    }
}
