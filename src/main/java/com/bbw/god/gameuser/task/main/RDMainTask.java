package com.bbw.god.gameuser.task.main;

import com.bbw.god.game.award.Award;
import com.bbw.god.gameuser.task.RDTaskItem;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author suhq
 * @description 主线任务返回数据
 * @date 2019-11-20 10:04
 **/
public class RDMainTask extends RDTaskItem implements Serializable {
    private static final long serialVersionUID = 1L;

    public static RDMainTask instance(UserMainTask udt, Integer realId, Integer status, Integer maxVal) {
        RDMainTask rdMainTask = new RDMainTask();
        rdMainTask.setId(realId);
        rdMainTask.setStatus(status);
        String[] strArr = {String.valueOf(udt.getAwardedIndex() + 1)};
        rdMainTask.setTitleFormats(strArr);
        rdMainTask.setProgress(udt.getEnableAwardIndex());
        rdMainTask.setTotalProgress(maxVal);
        return rdMainTask;
    }

}
