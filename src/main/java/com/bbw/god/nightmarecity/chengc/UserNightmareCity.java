package com.bbw.god.nightmarecity.chengc;

import com.bbw.common.ID;
import com.bbw.common.ListUtil;
import com.bbw.god.city.chengc.NightmareLogic;
import com.bbw.god.game.config.city.CfgCityEntity;
import com.bbw.god.game.config.city.CityTool;
import com.bbw.god.gameuser.UserCfgObj;
import com.bbw.god.gameuser.UserDataType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 梦魇城池信息
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class UserNightmareCity extends UserCfgObj {
    private Date ownTime;
    private boolean own = false;//拥有
    private int[] process = {0};
    private List<Integer> huWeiCards;// 护卫卡组
    private Integer huWeiFuCe = 0;
    private List<Integer> jinWeiCards;// 禁卫卡组
    private Integer jinWeiFuCe = 0;


    public static UserNightmareCity getInstance(CfgCityEntity cityEntity, long uid) {
        UserNightmareCity userNightMareCity = new UserNightmareCity();
        userNightMareCity.setBaseId(cityEntity.getId());
        userNightMareCity.setName(cityEntity.getName());
        userNightMareCity.setId(ID.INSTANCE.nextId());
        userNightMareCity.setGameUserId(uid);
        return userNightMareCity;
    }

    /**
     * 重置
     */
    public void reset(){
        ownTime=null;
        own=false;
        process[0]=0;
    }

    @Override
    public UserDataType gainResType() {
        return UserDataType.NIGHT_MARE_CITY;
    }

    public List<Integer> getHuWeiCards() {
        if (ListUtil.isEmpty(huWeiCards)){
            return new ArrayList<>();
        }
        return huWeiCards;
    }

    public List<Integer> getJinWeiCards() {
        if (ListUtil.isEmpty(jinWeiCards)){
            return new ArrayList<>();
        }
        return jinWeiCards;
    }

    public CfgCityEntity gainCity() {
        return CityTool.getCityById(this.getBaseId());
    }
    /**
     * 根据类型获得卡组
     * @param type
     * @return
     */
    public List<Integer> getCardsByType(int type){
        if (type== NightmareLogic.HU_WEI_CARDS){
            return getHuWeiCards();
        }
        return getJinWeiCards();
    }

}
