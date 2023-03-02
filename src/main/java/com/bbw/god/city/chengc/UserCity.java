package com.bbw.god.city.chengc;

import com.bbw.common.DateUtil;
import com.bbw.common.ID;
import com.bbw.god.game.config.city.BuildingEnum;
import com.bbw.god.game.config.city.CfgCityEntity;
import com.bbw.god.game.config.city.CityConfig;
import com.bbw.god.game.config.city.CityTool;
import com.bbw.god.gameuser.UserCfgObj;
import com.bbw.god.gameuser.UserDataType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Date;

/**
 * 用户占领的城池
 *
 * @author suhq 2018年10月8日 下午2:18:49
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class UserCity extends UserCfgObj {

    private Integer hierarchy = 0;
    private Integer fy = 1;
    private Integer kc = 0;
    private Integer qz = 0;
    private Integer tcp = 0;
    private Integer jxz = 0;
    private Integer lbl = 0;
    private Integer dc = 0;
    private Integer ldf = 0;
    private Integer ft;
    /** 法坛修缮值 */
    private Integer ftRepairValue;
    private Date createTime;
    private boolean own = true;//拥有
    private int[] process;

    public static UserCity fromCfgCity(Long uid, CfgCityEntity cfgCity) {
        UserCity usercity = new UserCity();
        usercity.setId(ID.INSTANCE.nextId());
        usercity.setGameUserId(uid);
        usercity.setBaseId(cfgCity.getId());
        usercity.setName(cfgCity.getName());
        usercity.setCreateTime(DateUtil.now());
        return usercity;
    }

    public CfgCityEntity gainCity() {
        return CityTool.getCityById(this.getBaseId());
    }

    /**
     * 该占城是否可振兴
     *
     * @return
     */
    public boolean ifAblePromote() {
        return this.hierarchy < CityConfig.bean().getCcData().getTopCCHierarchy() && ifUpdateFull();
    }

    /**
     * 该占城是否已升满
     *
     * @return
     */
    public boolean ifUpdateFull() {
        int fullLevel = CityConfig.bean().getCcData().getTopCCHierarchy() + this.hierarchy;
        return this.fy >= fullLevel && this.tcp >= fullLevel && this.jxz >= fullLevel && this.qz >= fullLevel && this.ldf >= fullLevel && this.dc >= fullLevel && this.lbl >= fullLevel && this.kc >= fullLevel;
    }

    /**
     * 该占城是否已升到顶级
     *
     * @return
     */
    public boolean ifUpdateTop() {
        int top = 10;
        return this.fy >= top && this.tcp >= top && this.jxz >= top && this.qz >= top && this.ldf >= top && this.dc >= top && this.lbl >= top && this.kc >= top;
    }

    /**
     * 该城所有建筑是否都升到5级
     *
     * @return
     */
    public boolean ifUpdate5() {
        int bLevel = 5;
        return this.fy == bLevel && this.tcp == bLevel && this.jxz == bLevel && this.qz == bLevel && this.ldf == bLevel && this.dc == bLevel && this.lbl == bLevel && this.kc == bLevel;
    }

    /**
     * 该城所有建筑是否都升到level级
     *
     * @return
     */
    public boolean ifUpdate(int level) {
        return this.fy == level && this.tcp == level && this.jxz == level && this.qz == level && this.ldf == level && this.dc == level && this.lbl == level && this.kc == level;
    }

    public boolean ifUpdateExceed(int level) {
        return this.fy >= level && this.tcp >= level && this.jxz >= level && this.qz >= level && this.ldf >= level && this.dc >= level && this.lbl >= level && this.kc >= level;
    }

    public boolean ifUpdateExceed5() {
        int bLevel = 5;
        return this.fy >= bLevel && this.tcp >= bLevel && this.jxz >= bLevel && this.qz >= bLevel && this.ldf >= bLevel && this.dc >= bLevel && this.lbl >= bLevel && this.kc >= bLevel;
    }

    public void addHierarchy() {
        int topCCHierarchy = CityConfig.bean().getCcData().getTopCCHierarchy();
        if (hierarchy >= topCCHierarchy) {
            return;
        }
        this.hierarchy++;
    }

    /**
     * 获取建筑物等级
     *
     * @param buildingValue BuildingEnum中的value
     * @return 建筑物对应等级
     */
    public int getBuildingLevel(int buildingValue) {
        int level = 0;
        BuildingEnum buildingEnum = BuildingEnum.fromValue(buildingValue);
        switch (buildingEnum) {
            case LBL:
                level = this.lbl;
                break;
            case KC:
                level = this.kc;
                break;
            case QZ:
                level = this.qz;
                break;
            case JXZ:
                level = this.jxz;
                break;
            case LDF:
                level = this.ldf;
                break;
            case FY:
                level = this.fy;
                break;
            case DC:
                level = this.dc;
                break;
            case TCP:
                level = this.tcp;
                break;
            case FT:
                level = this.ft;
                break;
            default:
                break;
        }
        return level;
    }

    /**
     * 是否可以一键领取
     *
     * @return
     */
    public boolean ableGainAllByOneClick() {
        return this.lbl > 0 || this.qz > 0 || this.jxz > 0 || this.kc > 0 || this.ldf > 0;
    }

    @Override
    public UserDataType gainResType() {
        return UserDataType.CITY;
    }
}
