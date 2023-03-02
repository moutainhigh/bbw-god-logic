package com.bbw.god.gameuser.task.businessgang;

import com.bbw.common.ID;
import com.bbw.common.MapUtil;
import com.bbw.god.game.config.special.CfgSpecialEntity;
import com.bbw.god.game.config.special.SpecialTool;
import com.bbw.god.gameuser.UserDataType;
import com.bbw.god.gameuser.task.CfgTaskEntity;
import com.bbw.god.gameuser.task.TaskGroupEnum;
import com.bbw.god.gameuser.task.TaskStatusEnum;
import com.bbw.god.gameuser.task.UserTask;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 玩家商帮特产运送任务
 *
 * @author fzj
 * @date 2022/1/14 14:20
 */
@Data
public class UserBusinessGangSpecialtyShippingTask extends UserTask implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer group;
    /** 目标城池 */
    private Integer targetCity;
    /** 目标等级 */
    private Integer targetLv;
    /** 目标城区 */
    private Integer targetCityArea;
    /** 目标特产及当前进度 */
    private Map<String, Integer> targetAndProgress;
    /** 是否加急 */
    private boolean urgent;

    public static UserBusinessGangSpecialtyShippingTask getInstance(long uid, TaskGroupEnum taskGroup, CfgTaskEntity task, boolean urgent) {
        UserBusinessGangSpecialtyShippingTask ut = new UserBusinessGangSpecialtyShippingTask();
        ut.setId(ID.INSTANCE.nextId());
        ut.setGameUserId(uid);
        ut.setGroup(taskGroup.getValue());
        ut.setBaseId(task.getId());
        ut.setStatus(TaskStatusEnum.DOING.getValue());
        ut.setNeedValue(task.getValue());
        ut.setUrgent(urgent);
        return ut;
    }

    /**
     * 获得所有任务特产id
     *
     * @return
     */
    public List<Integer> getAllTaskSpecialIds() {
        List<Integer> specialIds = new ArrayList<>();
        if (MapUtil.isEmpty(targetAndProgress)) {
            return specialIds;
        }
        List<String> specialsName = new ArrayList<>(targetAndProgress.keySet());
        return SpecialTool.getSpecials().stream().filter(s -> specialsName.contains(s.getName())).map(CfgSpecialEntity::getId).collect(Collectors.toList());
    }

    /**
     * 设置目标及进度
     *
     * @param special
     */
    public void setTarget(String special) {
        getTargetAndProgress().put(special, 0);
    }

    @Override
    public UserDataType gainResType() {
        return UserDataType.USER_BUSINESS_GANG_SPECIALTY_SHIPPING_TASK;
    }
}
