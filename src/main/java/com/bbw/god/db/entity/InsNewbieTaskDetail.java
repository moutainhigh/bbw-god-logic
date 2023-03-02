package com.bbw.god.db.entity;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.bbw.common.DateUtil;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author suchaobin
 * @description 新手任务明细
 * @date 2020/12/17 15:35
 **/
@Data
@TableName("ins_newbie_task_detail")
public class InsNewbieTaskDetail implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableId
    private Integer id;
    private Long uid;
    private Integer sid;
    private Integer step;
    private String stepName;
    private String version;
    private Date optime = DateUtil.now();

    public static InsNewbieTaskDetail getInstance(long uid, int sid, int step, String stepName, String version) {
        InsNewbieTaskDetail detail = new InsNewbieTaskDetail();
        detail.setUid(uid);
        detail.setSid(sid);
        detail.setStep(step);
        detail.setStepName(stepName);
        detail.setVersion(version);
        return detail;
    }
}
