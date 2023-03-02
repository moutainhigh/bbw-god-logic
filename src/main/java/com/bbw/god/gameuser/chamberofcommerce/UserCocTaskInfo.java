package com.bbw.god.gameuser.chamberofcommerce;

import com.bbw.common.ID;
import com.bbw.god.gameuser.UserDataType;
import com.bbw.god.gameuser.UserSingleObj;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author 作者 ：lwb
 * @version 创建时间：2020年3月2日 下午4:22:45
 * 类说明
 */
@Data
public class UserCocTaskInfo extends UserSingleObj implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<CocTask> cocTasks;
    private int refreshCount = 0;// 刷新次数
    private int finishedCount = 3;// 可完成任务数量
    private Integer taskBuildDate = 20200301;// 任务生成时间

    public static UserCocTaskInfo instance(long uid) {
        UserCocTaskInfo info = new UserCocTaskInfo();
        info.setId(ID.INSTANCE.nextId());
        info.setGameUserId(uid);
        return info;
    }

    @Override
    public UserDataType gainResType() {
        return UserDataType.Chamber_Of_Commerce_User_Task_Info;
    }
}