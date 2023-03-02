package com.bbw.god.gameuser.businessgang.cfg;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 商帮基础数据
 *
 * @author fzj
 * @date 2022/1/17 10:23
 */
@Data
public class CfgBusinessGangData implements Serializable {
    private static final long serialVersionUID = 3095926888734005517L;
    /** 商帮id */
    private Integer businessGangId;
    /** 商帮名称  */
    private String name;
    /** 敌对帮派 */
    private List<Integer> hostilityGang;
}
