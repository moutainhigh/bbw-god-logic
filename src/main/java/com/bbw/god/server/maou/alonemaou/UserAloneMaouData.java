package com.bbw.god.server.maou.alonemaou;

import com.bbw.common.ID;
import com.bbw.god.game.config.TypeEnum;
import com.bbw.god.gameuser.UserDataType;
import com.bbw.god.gameuser.UserSingleObj;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author suhq
 * @description: 玩家独占魔王攻打记录
 * @date 2019-12-17 16:30
 **/
@Data
@EqualsAndHashCode(callSuper = true)
public class UserAloneMaouData extends UserSingleObj {
    private List<Integer> attackCards = new ArrayList<>();//编组卡牌
    private List<Integer> maouKilledRecord = new ArrayList<>();//魔王击杀记录

    public static UserAloneMaouData getInstance(long uid) {
        UserAloneMaouData obj = new UserAloneMaouData();
        obj.setId(ID.INSTANCE.nextId());
        obj.setGameUserId(uid);
        return obj;
    }


    /**
     * 是否首次击杀魔王
     *
     * @param type
     * @param level
     * @return
     */
    public boolean firstKilled(int type, int level) {
        int maouIndex = AloneMaouTool.getMaouIndex(type, level);
        return !this.maouKilledRecord.contains(maouIndex);
    }

    /**
     * 添加魔王击杀记录
     *
     * @param type
     * @param level
     */
    public void addKilledMaou(int type, int level) {
        int maouIndex = AloneMaouTool.getMaouIndex(type, level);
        if (!this.maouKilledRecord.contains(maouIndex)) {
            this.maouKilledRecord.add(maouIndex);
        }
    }

    /**
     * 属性层级魔王是否被击杀过
     *
     * @param maouIndex
     * @return
     */
    public boolean ifEverKilled(int maouIndex) {
        return this.maouKilledRecord.contains(maouIndex);
    }

    /**
     * 是否击杀完对应层级的所有魔王
     *
     * @param level
     * @return
     */
    public boolean isKillAll(int level) {
        int gold = TypeEnum.Gold.getValue() * 10 + level;
        int wood = TypeEnum.Wood.getValue() * 10 + level;
        int water = TypeEnum.Water.getValue() * 10 + level;
        int fire = TypeEnum.Fire.getValue() * 10 + level;
        int earth = TypeEnum.Earth.getValue() * 10 + level;
        List<Integer> needList = Arrays.asList(gold, wood, water, fire, earth);
        return this.getMaouKilledRecord().containsAll(needList);
    }


    /**
     * 获取击杀对应等级的魔王数量
     *
     * @param level
     * @return
     */
    public int getKillMaouNum(int level) {
        int num = 0;
        for (int i = 1; i <= 5; i++) {
            Integer record = i * 100 + level;
            if (this.getMaouKilledRecord().contains(record)) {
                num++;
            }
        }
        return num;
    }

    /**
     * 获取玩家各属性最高的独战魔王权限
     *
     * @return
     */
    public List<Integer> getMaouAuthList() {
        List<Integer> authList = new ArrayList<>();
        Map<Integer, List<Integer>> collect = this.getMaouKilledRecord().stream().collect(Collectors.groupingBy(l -> l / 100));
        Set<Integer> keySet = collect.keySet();
        for (Integer key : keySet) {
            Integer max = collect.get(key).stream().max(Integer::compareTo).get();
            authList.add(max);
        }
        return authList;
    }

    @Override
    public UserDataType gainResType() {
        return UserDataType.ALONE_MAOU_DATA;
    }
}
