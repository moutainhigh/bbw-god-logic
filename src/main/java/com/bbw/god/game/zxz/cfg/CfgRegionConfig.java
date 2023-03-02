package com.bbw.god.game.zxz.cfg;

import lombok.Data;

import java.io.Serializable;

/**
 * 区域配置信息
 * @author: hzf
 * @create: 2022-09-18 08:59
 **/
@Data
public class CfgRegionConfig implements Serializable {
    private static final long serialVersionUID = 6283485026406890074L;

    /** 进度 */
    private Integer progress;
    /** 精英宝箱 */
    private Integer eliteDropBoxNum;
    /** 首领宝箱 */
    private Integer chiefDropBoxNum;
    /** 复活次数 */
    private Integer reviveFrequency;

}
