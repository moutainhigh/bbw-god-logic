package com.bbw.god.gameuser.leadercard.equipment;

import com.bbw.common.ListUtil;
import com.bbw.god.gameuser.GameUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 装备服务
 *
 * @author suhq
 * @date 2021-03-26 17:25
 **/
@Service
public class UserLeaderEquimentService {
    @Autowired
    private GameUserService gameUserService;

    /**
     * 获取装备战斗加成
     *
     * @param uid
     * @return
     */
    public FightAddition getAdditions(long uid) {
        List<UserLeaderEquipment> leaderEquipments = getLeaderEquipments(uid);
        if (ListUtil.isEmpty(leaderEquipments)) {
            return new FightAddition();
        }
        List<Addition> additions = new ArrayList<>();
        for (UserLeaderEquipment leaderEquipment : leaderEquipments) {
            additions.addAll(getAdditions(leaderEquipment));
        }

        Map<Integer, Integer> typAdditions = additions.stream().collect(Collectors.groupingBy(Addition::getType, Collectors.summingInt(Addition::getValue)));
        Integer attckAddition = typAdditions.getOrDefault(AdditionType.ATTACK.getValue(), 0);
        Integer defenceAddition = typAdditions.getOrDefault(AdditionType.DEFENCE.getValue(), 0);
        Integer bloodAddition = typAdditions.getOrDefault(AdditionType.BLOOD.getValue(), 0);
        return new FightAddition(attckAddition, defenceAddition, bloodAddition);
    }

    /**
     * 获得已穿戴的装备信息
     *
     * @param uid
     * @return
     */
    public UserLeaderEquipment[] getTakedEquipments(long uid) {
        UserLeaderEquipment[] equipments = new UserLeaderEquipment[]{null, null, null, null};
        List<UserLeaderEquipment> leaderEquipments = getLeaderEquipments(uid);
        if (ListUtil.isEmpty(leaderEquipments)) {
            return equipments;
        }
        for (UserLeaderEquipment leaderEquipment : leaderEquipments) {
            int index = CfgEquipmentTool.getEquipmentPosition(leaderEquipment.getEquipmentId());
            equipments[index] = leaderEquipment;
        }
        return equipments;
    }

    /**
     * 返回装备的加成集，包含重复的addition
     *
     * @param leaderEquipment
     * @return
     */
    private List<Addition> getAdditions(UserLeaderEquipment leaderEquipment) {
        // 基本加成
        List<Addition> additions = CfgEquipmentTool.getBaseEquipmentAddition(leaderEquipment.getEquipmentId());
        if (leaderEquipment.getLevel() > 0) {
            // 等级加成
            List<Addition> levelAdditions = CfgEquipmentTool.getLevelAddition(leaderEquipment.getLevel(), leaderEquipment.getQuality(), leaderEquipment.getStarMapProgress());
            List<Addition> levelAdditionToAdd = new ArrayList<>();
            for (Addition addition : additions) {
                Optional<Addition> optional = levelAdditions.stream().filter(tmp -> tmp.getType().intValue() == addition.getType()).findFirst();
                if (optional.isPresent()) {
                    levelAdditionToAdd.add(optional.get());
                }
            }
            additions.addAll(levelAdditionToAdd);
        }

        return additions;
    }

    public List<UserLeaderEquipment> getLeaderEquipments(long uid) {
        List<UserLeaderEquipment> leaderEquipments = gameUserService.getMultiItems(uid, UserLeaderEquipment.class)
                .stream().filter(eq -> eq.getIsPutOn() == 1).collect(Collectors.toList());
        return leaderEquipments;
    }

}
