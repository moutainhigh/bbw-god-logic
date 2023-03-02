package com.bbw.god.city;

import com.bbw.cache.UserCacheService;
import com.bbw.common.ListUtil;
import com.bbw.common.PowerRandom;
import com.bbw.god.city.chengc.UserCity;
import com.bbw.god.city.miaoy.hexagram.HexagramBuffEnum;
import com.bbw.god.city.miaoy.hexagram.HexagramBuffService;
import com.bbw.god.game.config.city.CfgCityEntity;
import com.bbw.god.game.config.city.CityTool;
import com.bbw.god.game.transmigration.UserTransmigrationService;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.nightmarecity.chengc.UserNightmareCity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 城市服务工具类
 *
 * @author suhq
 * @date 2018年11月24日 下午7:53:28
 */
@Slf4j
@Service
public class UserCityService {
    @Autowired
    private UserCacheService userCacheService;
    @Autowired
    private HexagramBuffService hexagramBuffService;
    @Autowired
    private UserTransmigrationService userTransmigrationService;

    /**
     * 获取玩家单个城池记录，游戏业务逻辑必须调用该方法！！！<br/>
     * 不能直接使用GameUserService
     *
     * @param uid
     * @param cityId
     * @return
     */
    @SuppressWarnings("unchecked")
    public UserCity getUserCity(long uid, int cityId) {
        return userCacheService.getCfgItem(uid, cityId, UserCity.class);
    }

    /**
     * 获取玩家所有城池记录，游戏业务逻辑必须调用该方法！！！<br/>
     * 不能直接使用GameUserService
     *
     * @param uid
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<UserCity> getUserCities(long uid) {
        return userCacheService.getUserDatas(uid, UserCity.class);
    }

    /**
     * 获取玩家所有城池记录，游戏业务逻辑必须调用该方法！！！<br/>
     * 不能直接使用GameUserService
     *
     * @param uid
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<UserCity> getUserOwnCities(long uid) {
        return getUserCities(uid).stream().filter(p->p.isOwn()).collect(Collectors.toList());
    }

    /**
     * 获取玩家所有城池记录，游戏业务逻辑必须调用该方法！！！<br/>
     * 不能直接使用GameUserService
     *
     * @param uid
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<UserNightmareCity> getUserNightmareCities(long uid) {
        return userCacheService.getUserDatas(uid, UserNightmareCity.class);
    }

    /**
     * 删除梦魇城池
     *
     * @param uid
     */
    public void delUserNightmareCities(long uid) {
        List<UserNightmareCity> datas = userCacheService.getUserDatas(uid, UserNightmareCity.class);
        userCacheService.delUserDatas(datas);
    }

    /**
     * 获取玩家所有城池记录，游戏业务逻辑必须调用该方法！！！<br/>
     * 不能直接使用GameUserService
     *
     * @param uid
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<UserNightmareCity> getUserOwnNightmareCities(long uid) {
        return getUserNightmareCities(uid).stream().filter(p->p.isOwn()).collect(Collectors.toList());
    }

    /**
     * 获取梦魇城池信息
     * @param uid
     * @param cityId
     * @return
     */
    public UserNightmareCity getUserNightmareCity(long uid,int cityId){
        return userCacheService.getCfgItem(uid, cityId, UserNightmareCity.class);
    }

    /**
     * 获得某一属性的梦魇城池的占有数
     *
     * @param guId
     * @param country
     * @return
     */
    public int getOwnNightmareCityNumAsCountry(long guId, int country) {
        long ownNightmareCityNum = getUserOwnNightmareCities(guId).stream().filter(userCity -> userCity.gainCity().getCountry() == country).count();
        return (int) ownNightmareCityNum;
    }

    /**
     * 是否攻下梦魇某区的1、2、3级城
     *
     * @param guId
     * @param country
     * @return
     */
    public boolean isOwnLowNightmareCityAsCountry(long guId, int country) {
        List<UserNightmareCity> userNightmareCity = getOwnNightmareCitiesByCountry(guId,country);
        List<CfgCityEntity> cfgCity = CityTool.getCCCities(country);
        int oneNightmareCityNum = (int)userNightmareCity.stream().filter(userCity -> userCity.gainCity().getLevel() == 1).count();
        int oneCityNum = (int)cfgCity.stream().filter(city ->city.isCC() && city.getLevel() == 1).count();
        if(oneNightmareCityNum != oneCityNum){
            return false;
        }
        int twoNightmareCityNum = (int)userNightmareCity.stream().filter(userCity -> userCity.gainCity().getLevel() == 2).count();
        int twoCityNum = (int)cfgCity.stream().filter(city ->city.isCC() && city.getLevel() == 2).count();
        if(twoNightmareCityNum != twoCityNum){
            return false;
        }
        int threeNightmareCityNum = (int)userNightmareCity.stream().filter(userCity -> userCity.gainCity().getLevel() == 3).count();
        int threeCityNum = (int)cfgCity.stream().filter(city ->city.isCC() && city.getLevel() == 3).count();
        if(threeNightmareCityNum != threeCityNum){
            return false;
        }
        return true;
    }

    /**
     * 获得某一级别的城池的占有数
     *
     * @param guId
     * @param cityLevel
     * @return
     */
    public int getOwnCityNumAsLevel(long guId, int cityLevel) {
        long ownCityNum = getUserOwnCities(guId).stream()
                .filter(userCity ->userCity.isOwn() && userCity.gainCity().getLevel() == cityLevel).count();
        return (int) ownCityNum;
    }

    public int getOwnNightmareCityNumAsLevel(long guId, int cityLevel) {
        long ownCityNum = getUserOwnNightmareCities(guId).stream()
                .filter(userCity ->userCity.isOwn() && userCity.gainCity().getLevel() == cityLevel).count();
        return (int) ownCityNum;
    }
    /**
     * 获得满某一阶数的城池数
     *
     * @param uid
     * @param hierarchy
     * @return
     */
    public int getCityNumAsHierarchy(long uid, int cityLevel, int hierarchy) {
        long cityNum = getUserOwnCities(uid).stream()
                .filter(userCity ->userCity.isOwn() && userCity.getHierarchy() >= hierarchy && userCity.gainCity().getLevel() == cityLevel)
                .count();
        return (int) cityNum;
    }

    /**
     * 获得所有建筑升到5级的城池数
     *
     * @param guId
     * @param level
     * @return
     */
    public int getCityUpdateToLevelNum(long guId, int level) {
        long cityNum = getUserOwnCities(guId).stream().filter(tmp -> tmp.ifUpdateExceed(level)).count();
        return (int) cityNum;
    }
    /**
     * 获得某一属性的城池的占有数
     *
     * @param guId
     * @param country
     * @return
     */
    public int getOwnCityNumAsCountry(long guId, int country) {
        long ownCityNum = getUserOwnCities(guId).stream()
                .filter(userCity -> userCity.gainCity().getCountry() == country).count();
        return (int) ownCityNum;
    }

    /**
     * 获取某个地区拥有的所有城池
     *
     * @param uid
     * @param country
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<UserCity> getOwnCitiesByCountry(long uid, int country) {
        List<UserCity> cityCities = getUserOwnCities(uid);
        if (ListUtil.isEmpty(cityCities)) {
            return new ArrayList<>();
        }
        List<UserCity> rdCities = cityCities.stream().filter(userCity -> userCity.gainCity().getCountry() == country)
                .collect(Collectors.toList());
        if (ListUtil.isEmpty(rdCities)) {
            return new ArrayList<>();
        }
        return rdCities;
    }

    /**
     * 获取某个地区拥有的所有梦魇城池
     *
     * @param uid
     * @param country
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<UserNightmareCity> getOwnNightmareCitiesByCountry(long uid, int country) {
        List<UserNightmareCity> cityCities = getUserOwnNightmareCities(uid);
        if (ListUtil.isEmpty(cityCities)) {
            return new ArrayList<>();
        }
        List<UserNightmareCity> rdCities = cityCities.stream().filter(userNightmareCity -> userNightmareCity.gainCity().getCountry() == country)
                .collect(Collectors.toList());
        if (ListUtil.isEmpty(rdCities)) {
            return new ArrayList<>();
        }
        return rdCities;
    }

    /**
     * 某个区域是否全部拥有
     * @param uid
     * @param isNightmare
     * @param country  区域
     * @return
     */
    public boolean isOwnAllCity(long uid,boolean isNightmare,int country){
        List<CfgCityEntity> ccCities = CityTool.getCountryCities(country).stream().filter(p->p.isCC()).collect(Collectors.toList());
        int total=ccCities.size();
        if (isNightmare){
            return getOwnNightmareCitiesByCountry(uid,country).size()==total;
        }
        return getOwnCitiesByCountry(uid,country).size()==total;
    }

    /**
     * 是否攻下所有城池
     * @param uid
     * @param isNightmare  是否是梦魇世界
     * @return
     */
    public boolean isOwnAllCity(long uid,boolean isNightmare){
        if (isNightmare){
            List<UserNightmareCity> cityCities = getUserOwnNightmareCities(uid);
            if (ListUtil.isEmpty(cityCities)) {
                return false;
            }
            return cityCities.size()==85;
        }else {
            List<UserCity> ownCities = getUserOwnCities(uid);
            if (ListUtil.isEmpty(ownCities)) {
                return false;
            }
            return ownCities.size()==85;
        }

    }
    /**
     * 随机玩家拥有的城池
     *
     * @param uid
     * @return
     */
    public CfgCityEntity getRandomUserOwnCity(long uid) {
        List<UserCity> userCities = getUserOwnCities(uid);
        if (ListUtil.isEmpty(userCities)) {
            return null;
        }
        UserCity userCityObj = PowerRandom.getRandomFromList(userCities);
        return userCityObj.gainCity();
    }


    /**
     * 梦魇世界经验加成BUFF
     *
     * @param gu
     */
    public double addNightmareCopperBuff(GameUser gu) {
        try {
            return addNightmareExpBuff(gu);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public double addNightmareExpBuff(GameUser gu) {
        if (!gu.getStatus().ifNotInFsdlWorld() || hexagramBuffService.isHexagramBuff(gu.getId(), HexagramBuffEnum.HEXAGRAM_56.getId())) {
            return 0;
        }
        long uid = gu.getId();
        try {
            double add = settleNightmareBuffAddNum(uid);
            add += userTransmigrationService.getTranmigrationBuffAdd(uid);
            if (add == 0) {
                return add;
            }
            BigDecimal b = new BigDecimal(add);
            add = b.setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();
            return add;
        }catch (Exception e){
            e.printStackTrace();
            log.error(gu.getId()+"玩家梦魇经验BUFF加成错误");
            log.error("玩家梦魇经验BUFF加成错误："+e.getMessage());
            return 0;
        }
    }

    private double settleNightmareBuffAddNum(long uid){
        int num = getUserOwnNightmareCities(uid).size();
        return CityTool.getNightmareAdd(num);
    }
}
