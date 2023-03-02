package com.bbw.god.gameuser.task.fshelper;

import com.bbw.common.DateUtil;
import com.bbw.common.ID;
import com.bbw.common.ListUtil;
import com.bbw.god.gameuser.UserDataType;
import com.bbw.god.gameuser.UserSingleObj;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author 作者 ：lwb
 * @version 创建时间：2020年2月13日 上午11:38:11
 * 类说明
 */
@Data
public class FsHepler extends UserSingleObj implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<Task> tasks = new ArrayList<>();

    public void addTask(Task task) {
        tasks.add(task);
    }

    public void delTask(FsTaskEnum type, int taskId) {
        if (ListUtil.isEmpty(tasks)) {
            return;
        }
        int index = -1;
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            if (task.getTaskId() == taskId && task.getTaskType() == type.getVal()) {
                index = i;
                break;
            }
        }
        if (index >= 0) {
            tasks.remove(index);
        }
    }

    public static FsHepler instance(long uid) {
        FsHepler hepler = new FsHepler();
        hepler.setGameUserId(uid);
        hepler.setId(ID.INSTANCE.nextId());
        return hepler;
    }

    @Override
    public UserDataType gainResType() {
        return UserDataType.FS_HEPLER;
    }

    @Data
    public static class Task implements Serializable {
        private static final long serialVersionUID = 1L;
        private Integer taskType;
        private Integer taskId;
        private Date expire;

        public static Task instance(FsTaskEnum taskType, int taskId) {
            Task t = new Task();
            t.setTaskId(taskId);
            t.setTaskType(taskType.getVal());
            Date expireD = DateUtil.getDateEnd(new Date());
            t.setExpire(expireD);
            return t;
        }
    }
}
