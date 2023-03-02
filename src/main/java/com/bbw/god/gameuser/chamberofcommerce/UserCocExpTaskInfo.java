package com.bbw.god.gameuser.chamberofcommerce;

import com.bbw.common.ID;
import com.bbw.god.gameuser.UserDataType;
import com.bbw.god.gameuser.UserSingleObj;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author 作者 ：lwb
 * @version 创建时间：2020年3月2日 下午4:28:31
 * 类说明
 */
@Data
public class UserCocExpTaskInfo extends UserSingleObj implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<CocExpTask> expList;
    private Long tradeTotal = 0L;// 特产卖出数量
    private Long trainWinTotal = 0L;// 练兵胜利次数
    private Long specialTotal = 0L;// 特殊任务：每日完成初级以上任务次数 不包含初级
    private Integer taskBuildDate = 20200301;// 任务生成时间

    /**
     * 获取当前进度的任务
     *
     * @return
     */
    public CocExpTask getCurrentCocExpTask(int type) {
        CocExpTask task = null;
        for (CocExpTask cocExpTask : expList) {
            if (cocExpTask.getStatus() == CocConstant.EXP_TASK_STATUS_GONE || type != cocExpTask.getType()) {
                continue;
            }
            if (task != null && task.getTarget() < cocExpTask.getTarget()) {
                continue;
            }
            task = cocExpTask;
        }
        return task;
    }

    public static UserCocExpTaskInfo instance(long uid) {
        UserCocExpTaskInfo info = new UserCocExpTaskInfo();
        info.setGameUserId(uid);
        info.setId(ID.INSTANCE.nextId());
        return info;
    }

    @Override
    public UserDataType gainResType() {
        return UserDataType.Chamber_Of_Commerce_User_ExpTask_Info;
    }
}
