package com.bbw.god.gameuser.yuxg.rd;

import com.bbw.god.rd.RDSuccess;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 设置
 *
 * @author: suhq
 * @date: 2021/10/21 10:14 上午
 */
@Data
public class RDYuXGSetting extends RDSuccess {
    private static final long serialVersionUID = 6594160183214906324L;
    /** 设置标识 */
    private List<RDSetting> settings;

    /**
     * 设置标识
     *
     * @author: suhq
     * @date: 2021/10/21 2:52 下午
     */
    @Data
    public static class RDSetting implements Serializable {
        private static final long serialVersionUID = -4216527940689976757L;
        private String settingName;
        private Integer status;
    }

    public static RDSetting getInstance(Map.Entry<String, Integer> setting) {
        RDSetting rd = new RDSetting();
        rd.setSettingName(setting.getKey());
        rd.setStatus(setting.getValue());
        return rd;
    }

}
