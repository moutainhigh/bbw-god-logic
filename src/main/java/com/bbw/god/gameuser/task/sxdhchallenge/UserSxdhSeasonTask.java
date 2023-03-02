package com.bbw.god.gameuser.task.sxdhchallenge;

import com.bbw.common.ID;
import com.bbw.god.gameuser.UserDataType;
import com.bbw.god.gameuser.task.CfgTaskEntity;
import com.bbw.god.gameuser.task.UserTask;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * 神仙大会赛季挑战
 *
 * @author suhq
 * @date 2020-04-27 09:42
 **/
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
public class UserSxdhSeasonTask extends UserTask implements Serializable {

    private static final long serialVersionUID = 1L;
    private Date generateTime;// 任务生成时间yyyyMMddHHmmss

    public static UserSxdhSeasonTask fromTask(long guId, CfgTaskEntity task, Date seasonBeginDate) {
        UserSxdhSeasonTask ut = new UserSxdhSeasonTask();
        ut.setId(ID.INSTANCE.nextId());
        ut.setGameUserId(guId);
        ut.setBaseId(task.getId());
        ut.setNeedValue(task.getValue());
        ut.setName(task.getName());
        ut.setGenerateTime(seasonBeginDate);
        return ut;
    }

    @Override
    public UserDataType gainResType() {
        return UserDataType.SXDH_SEASON_TASK;
    }
}
