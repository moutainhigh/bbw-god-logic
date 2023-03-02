package com.bbw.god.city.chengc;

import com.bbw.god.city.chengc.in.RDCityInInfo;
import com.bbw.god.fight.RDFightsInfo;
import com.bbw.god.game.combat.data.param.CombatPVEParam;
import com.bbw.god.game.config.city.CfgCityEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 城池 信息缓存
 * @author：lwb
 * @date: 2020/12/18 14:22
 * @version: 1.0
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChengChiInfoCache implements Serializable {
    private static final long serialVersionUID = -5726248296315031104L;
    //城池基础信息
    private Integer cityId;//城池ID
    private Integer cityLv;//当前城池级别;
    private Integer cityProperty;//城池的五行属性
    private Integer area;//区域；
    private Integer hv = 0;// 城池阶数
    private boolean ownCity = false;//是否拥有城池
    //需要用到的参数
    private List<RDTradeInfo.RDCitySpecial> citySpecials = new ArrayList<>();// 城市出售特产
    private List<RDTradeInfo.RDSellingSpecial> sellingSpecials = new ArrayList<>();//玩家在本城池卖的特产价格
    private boolean attack = false;// 是否攻城
    private boolean training = false;// 是否练兵
    private boolean promote = false;// 是否振兴
    private boolean Investigated = false;//是否侦察
    /** 轮回是否已战 */
    private boolean transmigration = false;
    private int attackTimes = 0;
    private Integer tcpLv = 0;//特产铺等级
    private Integer remainSpecialsRefreshTimes = 1;// 刷新可交易的特产
    private Integer discount = 0;// 商会特权获取的折扣
    private Integer premiumRate = 0;// 商会特权获取的溢价
    private boolean hadInitSpecials = false;//是否初始化过了特产
    //战斗相关参数
    private Integer cityBuff = 0;//城池BUFF
    private int[] levelProgress = null;//关卡信息
    private CombatPVEParam fightParam = null;//战斗初始化信息
    private CombatPVEParam promoteFightParam=null;//战斗初始化信息
    private RDFightsInfo trainingInfo=null;//练兵对手信息

    private RDCityInInfo cityInInfo=null;//城内信息
    private Integer chengChiGain=1;//城池收益倍数

    public static ChengChiInfoCache instance(CfgCityEntity cfgCityEntity){
        ChengChiInfoCache cache=new ChengChiInfoCache();
        cache.setCityId(cfgCityEntity.getId());
        cache.setCityLv(cfgCityEntity.getLevel());
        cache.setCityProperty(cfgCityEntity.getProperty());
        cache.setLevelProgress(new int[cfgCityEntity.getLevel()-1]);
        return cache;
    }

    public void updateByUserCity(UserCity userCity){
        if (userCity==null){
            return;
        }
        levelProgress=userCity.getProcess();
        hv=userCity.getHierarchy();
        tcpLv=userCity.getTcp();
    }

    public int checkNextAttackLevel(){
        if (cityLv==1){
            return cityLv;
        }
        for (int i = 0; i < levelProgress.length; i++) {
            if (levelProgress[i]==0){
                return i+1;
            }
        }
        return cityLv;
    }

    public void addAttackTimes(){
        attackTimes++;
    }
    /**
     * 是否是攻打城池（指非攻打关卡）
     * @return
     */
    public boolean ifAttackCity(boolean isNightmare){
        if (isNightmare){
            return levelProgress!=null || levelProgress[0]==0;
        }
        return cityLv==checkNextAttackLevel();
    }


}
