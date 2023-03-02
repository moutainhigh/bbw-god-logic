package com.bbw.mc.m2c;

import com.bbw.god.login.DynamicMenuEnum;
import com.bbw.god.login.RDNoticeInfo.ActivityShow;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 通知客户端信息
 *
 * @author suhq
 * @date 2019-06-03 10:15:27
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RDM2c {
    private List<String> redNotices = null;//红点通知
    private Integer fsHepler = null;// 任务进度更新
    private List<ActivityShow> dynamicMenuTypes = null;// 动态菜单图标
    private Integer currentHexagram=null;// buff消失通知

    public void addOpenMenu(DynamicMenuEnum type, int num) {
        if (this.dynamicMenuTypes == null) {
            this.dynamicMenuTypes = new ArrayList<>();
        }
        this.dynamicMenuTypes.add(ActivityShow.instance(type.getVal(), num));
    }
}
