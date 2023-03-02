package com.bbw.god.mall.lottery;

import com.bbw.common.DateUtil;
import com.bbw.common.ID;
import com.bbw.common.ListUtil;
import com.bbw.common.MapUtil;
import com.bbw.god.game.data.GameData;
import com.bbw.god.game.data.GameDataType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author suchaobin
 * @description 奖券下注信息
 * @date 2020/7/6 11:15
 **/
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class GameLottery extends GameData {
    private Date beginTime = DateUtil.now();// 开始时间
    private Integer groupId;// 平台
    private Map<String, String> betMap = new HashMap<>(); // 下注map，key是压奖的数字，val是对应压奖的玩家id
    private boolean isDrawing = false;// 是否正在开奖
    private boolean isValid = true;// 是否有效，开奖后会修改成无效状态
    private LotteryResult result;// 开奖结果

    public static GameLottery getInstance(int groupId) {
        GameLottery gameLottery = new GameLottery();
        gameLottery.setId(ID.INSTANCE.nextId());
        gameLottery.setGroupId(groupId);
        return gameLottery;
    }

    public boolean bet(long uid, List<Integer> lotteryNumbers) {
        List<String> uids = new ArrayList<>();
        if (MapUtil.isNotEmpty(this.betMap)) {
            uids = this.betMap.entrySet().stream()
                    .filter(tmp -> lotteryNumbers.contains(Integer.parseInt(tmp.getKey())))
                    .map(Map.Entry::getValue).collect(Collectors.toList());
        }

        if (ListUtil.isNotEmpty(uids)) {
            return false;
        }
        for (Integer number : lotteryNumbers) {
            this.betMap.put(String.valueOf(number), String.valueOf(uid));
        }
        return true;
    }

    public List<Long> gainBetUids() {
        List<Long> uids = new ArrayList<>();
        Set<String> keySet = betMap.keySet();
        for (String key : keySet) {
            uids.add(Long.parseLong(betMap.get(key)));
        }
        return uids.stream().distinct().collect(Collectors.toList());
    }

    public List<Integer> gainMyNumbers(long uid) {
        List<Integer> boughtNumbers = new ArrayList<>();
        Set<String> keySet = betMap.keySet();
        for (String key : keySet) {
            Long val = Long.parseLong(betMap.get(key));
            if (val.equals(uid)) {
                boughtNumbers.add(Integer.parseInt(key));
            }
        }
        return boughtNumbers.stream().sorted(Integer::compareTo).collect(Collectors.toList());
    }

    public List<Integer> gainBoughtNumbers() {
        Set<String> keySet = betMap.keySet();
        return new ArrayList<>(keySet).stream().map(Integer::parseInt).collect(Collectors.toList());
    }

    @Override
    public GameDataType gainDataType() {
        return GameDataType.LOTTERY;
    }
}
