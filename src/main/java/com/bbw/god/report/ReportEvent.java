package com.bbw.god.report;

import com.bbw.god.notify.rednotice.ModuleEnum;
import lombok.Data;

import java.io.Serializable;

/**
 * 上报事件实体
 *
 * @author: suhq
 * @date: 2021/8/17 5:57 下午
 */
@Data
public abstract class ReportEvent implements Serializable {
    private static final long serialVersionUID = 3133606249120040894L;
    /** 上报者 */
    private Reporter reporter;
    /** 上报事件类型 */
    private ReportEventType event;
    /** 对应事件的业务类型 */
    private ModuleEnum businessType;
    /** 对应事件的业务子类型 */
    private String businessChildType;

}
