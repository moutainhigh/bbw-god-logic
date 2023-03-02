package com.bbw.god.server.guild;

import com.bbw.common.ID;
import com.bbw.god.game.config.CfgGuild.BoxReward;
import com.bbw.god.gameuser.UserDataType;
import com.bbw.god.gameuser.UserSingleObj;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author 作者 ：lwb
 * @version 创建时间：2020年3月16日 下午3:19:19
 * 类说明
 */
@Data
public class UserGuildTaskInfo extends UserSingleObj implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<GuildTask> tasks = new ArrayList<GuildTask>();
    private Integer buildDate = 20200101;// 生成任务时间
    private Integer refreshCount = 0;// 任务刷新次数
    private Integer complete = GuildConstant.COMPLETE;// 任务完成次数
    private List<BoxReward> box = new ArrayList<BoxReward>();// 可开宝箱 大小即为可开数量
    private Integer gainBoxNum = 0;// 获得宝箱数

    public static UserGuildTaskInfo instance(long uid) {
        UserGuildTaskInfo userGuildTaskInfo = new UserGuildTaskInfo();
        userGuildTaskInfo.setGameUserId(uid);
        userGuildTaskInfo.setId(ID.INSTANCE.nextId());
        return userGuildTaskInfo;
    }

    public void addBox(BoxReward boxReward) {
        gainBoxNum++;
        box.add(boxReward);
    }

    /**
     * 正在进行的任务
     * @return
     */
    public Optional<GuildTask> doingTask(){
        for (GuildTask task : tasks) {
            if (task.ifAccept()){
                return Optional.of(task);
            }
        }
        return Optional.empty();
    }
    @Override
    public UserDataType gainResType() {
        return UserDataType.Guild_User_TaskInfo;
    }
}
