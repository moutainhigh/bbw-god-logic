package com.bbw.cache;

import com.bbw.common.DateUtil;
import com.bbw.common.ListUtil;
import com.bbw.common.StrUtil;
import com.bbw.god.activity.UserActivity;
import com.bbw.god.city.chengc.UserCity;
import com.bbw.god.game.wanxianzhen.UserWanXian;
import com.bbw.god.game.zxz.entity.UserZxzCardGroupInfo;
import com.bbw.god.game.zxz.entity.UserZxzRegionInfo;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.UserCfgObj;
import com.bbw.god.gameuser.UserData;
import com.bbw.god.gameuser.UserDataType;
import com.bbw.god.gameuser.businessgang.digfortreasure.UserDigTreasure;
import com.bbw.god.gameuser.card.UserCard;
import com.bbw.god.gameuser.card.equipment.data.UserCardXianJue;
import com.bbw.god.gameuser.card.equipment.data.UserCardZhiBao;
import com.bbw.god.gameuser.leadercard.fashion.UserLeaderFashion;
import com.bbw.god.gameuser.special.UserPocket;
import com.bbw.god.gameuser.special.UserSpecial;
import com.bbw.god.gameuser.task.businessgang.UserBusinessGangSpecialtyShippingTask;
import com.bbw.god.gameuser.task.godtraining.UserGodTrainingTask;
import com.bbw.god.gameuser.task.grow.UserGrowTask;
import com.bbw.god.gameuser.task.main.UserMainTask;
import com.bbw.god.gameuser.treasure.UserTreasure;
import com.bbw.god.gameuser.treasure.UserTreasureEffect;
import com.bbw.god.gameuser.treasure.UserTreasureRecord;
import com.bbw.god.gameuser.yaozu.UserYaoZuInfo;
import com.bbw.god.gameuser.yuxg.UserFuTu;
import com.bbw.god.mall.UserMallRecord;
import com.bbw.god.nightmarecity.chengc.UserNightmareCity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author suhq
 * @description: 玩家数据本地缓存接口
 * @date 2020-02-25 09:54
 **/
@Slf4j
@Service
public class UserCacheService {
    private static String CACHE_TYPE = "userDataCache";
    private static int CACHE_TIME_OUT = 600;
    /** 缓存生效时长。不长于服务启动时间 */
    private static int CACHE_VALID_SECONDS = 10;
    /** 缓存有效时长。为了防止系统调用导致本地缓存失效，相应的缓存数据应该很短。比如充值回调会修改UserMallRecord的数据 */
    private static int CACHE_VALID_SHORT_SECONDS = 3;
    private static List<Class> SUPPORT_DATA = Arrays.asList(
            UserActivity.class,
            UserGrowTask.class,
            UserMainTask.class,
            UserCity.class,
            UserNightmareCity.class,
            UserCard.class,
            UserYaoZuInfo.class,
            UserTreasure.class,
            UserTreasureEffect.class,
            UserTreasureRecord.class,
            UserGodTrainingTask.class,
            UserSpecial.class,
            UserMallRecord.class,
            UserWanXian.class,
            UserFuTu.class,
            UserCardZhiBao.class,
            UserCardXianJue.class,
            UserZxzCardGroupInfo.class,
            UserZxzRegionInfo.class,
            UserDigTreasure.class,
            UserBusinessGangSpecialtyShippingTask.class,
            UserLeaderFashion.class,
            UserPocket.class
    );
    @Autowired
    private GameUserService gameUserService;

    /**
     * 获得玩家数据(基于本地缓存)
     * <br/>
     * <p color="red">
     * 非线程安全，如果存在多线程同时读取同一个玩家的某类数据，会导致本地缓存的数据不符合预期。
     * 如果后续还涉及到数据删除操作可能导致非预期的问题（比如去重操作带来的数据误删除）。
     * </p>
     *
     * @param uid
     * @param clazz
     * @param <T>
     * @return
     */
    public <T extends UserData> List<T> getUserDatas(long uid, Class<T> clazz) {
        if (!isSupportLocalCache(clazz)) {
            return gameUserService.getMultiItems(uid, clazz);
        }
//        log.error(System.currentTimeMillis() + "==========get data from cache:"+clazz.getName());
        List<T> datas = doGetUserDatas(uid, clazz, DateUtil.now());
        //如果获取的数据为null,在获取一次
        if (null == datas) {
            log.error("{}getUserData获取{}为null,重新获取", uid, clazz.getName());
            datas = doGetUserDatas(uid, clazz, DateUtil.now());
        }
        //返回一个新的集合引用
        return datas.stream().collect(Collectors.toList());
    }

    private <T extends UserData> List<T> doGetUserDatas(long uid, Class<T> clazz, Date date) {
        String cacheKey = getCacheKey(uid, clazz, date);
        if (null == LocalCache.getInstance().get(CACHE_TYPE, cacheKey)) {
            //缓存新的数据
//            System.out.println("设置玩家数据本地缓存：" + cacheKey);
            recacheUserDatas(uid, date, clazz);
        }
        List<T> datas = LocalCache.getInstance().get(CACHE_TYPE, cacheKey);
        return datas;
    }

    public <T extends UserCfgObj> List<T> getCfgItems(long uid, List<Integer> baseIds, Class<T> clazz) {
        List<T> ucs = getUserDatas(uid, clazz);
        return ucs.stream().filter(tmp -> baseIds.contains(tmp.getBaseId())).collect(Collectors.toList());
    }

    public <T extends UserCfgObj> T getCfgItem(long gameUserId, int baseId, Class<T> objClass) {
        List<T> items = getUserDatas(gameUserId, objClass);
        Optional<T> opt = items.stream().filter(item -> null != item.getBaseId() && item.getBaseId().intValue() == baseId).findFirst();
        return opt.orElse(null);
    }

    public <T extends UserData> void addUserData(T ud) {
        if (ud == null || !isSupportLocalCache(ud.getClass())) {
            return;
        }
        long uid = ud.getGameUserId();
        this.gameUserService.addItem(uid, ud);
        //更新缓存
        Class<T> clazz = (Class<T>) ud.getClass();
        recacheUserDatas(uid, DateUtil.now(), clazz);
    }

    public <T extends UserData> void addUserDatas(List<T> udsToAdd) {
        if (ListUtil.isEmpty(udsToAdd) || !isSupportLocalCache(udsToAdd.get(0).getClass())) {
            return;
        }
        long uid = udsToAdd.get(0).getGameUserId();
        this.gameUserService.addItems(udsToAdd);
        //更新缓存
        Class<T> clazz = (Class<T>) udsToAdd.get(0).getClass();
        recacheUserDatas(uid, DateUtil.now(), clazz);
    }

    public <T extends UserData> void updateUserData(T ud) {
        if (ud == null || !isSupportLocalCache(ud.getClass())) {
            return;
        }
        long uid = ud.getGameUserId();
        this.gameUserService.updateItem(ud);
        //更新缓存
        Class<T> clazz = (Class<T>) ud.getClass();
        recacheUserDatas(uid, DateUtil.now(), clazz);
    }

    public <T extends UserData> void delUserData(T udToDel) {
        if (udToDel == null || !isSupportLocalCache(udToDel.getClass())) {
            return;
        }
        this.gameUserService.deleteItem(udToDel);
        //更新缓存
        Class<T> clazz = (Class<T>) udToDel.getClass();
        recacheUserDatas(udToDel.getGameUserId(), DateUtil.now(), clazz);

    }

    public <T extends UserData> void delUserDatas(List<T> udsToDel) {
        if (ListUtil.isEmpty(udsToDel) || !isSupportLocalCache(udsToDel.get(0).getClass())) {
            return;
        }
        long uid = udsToDel.get(0).getGameUserId();
        this.gameUserService.deleteItems(uid, udsToDel);
        //更新缓存
        Class<T> clazz = (Class<T>) udsToDel.get(0).getClass();
        recacheUserDatas(uid, DateUtil.now(), clazz);

    }

    public <T extends UserData> void delUserDatas(long uid, List<Long> udsToDel, Class<T> clazz) {
        if (ListUtil.isEmpty(udsToDel) || !isSupportLocalCache(clazz)) {
            return;
        }
        this.gameUserService.deleteItems(uid, udsToDel, clazz);
        //更新缓存
        recacheUserDatas(uid, DateUtil.now(), clazz);

    }

    /**
     * 移除缓存
     *
     * @param uid
     * @param now
     * @param clazz
     * @param <T>
     */
    public <T extends UserData> void removeCache(long uid, Date now, Class<T> clazz) {
        String cacheKey = getCacheKey(uid, clazz, now);
        LocalCache.getInstance().remove(CACHE_TYPE, cacheKey);
    }

    private <T extends UserData> void recacheUserDatas(long uid, Date date, Class<T> clazz) {
        List<T> uds = this.gameUserService.getMultiItems(uid, clazz);
        cacheDatas(uid, uds, date, clazz);
    }

    private <T extends UserData> void cacheDatas(long uid, List<T> datas, Date now, Class<T> clazz) {
        //删除上一次缓存的数据
        String lastCacheKey = getLastCacheKey(uid, clazz);
        if (StrUtil.isNotBlank(lastCacheKey)) {
//                System.out.println("清除过期本地缓存：" + lastCacheKey);
            LocalCache.getInstance().remove(CACHE_TYPE, lastCacheKey);
        }
        String cacheKey = getCacheKey(uid, clazz, now);
        LocalCache.getInstance().put(CACHE_TYPE, cacheKey, datas, CACHE_TIME_OUT);
        //标识最近缓存的key
        cacheLastCacheKey(uid, clazz, cacheKey);
    }

    /**
     * 缓存最近缓存的key
     *
     * @param uid
     * @param clazz
     * @param cacheKey
     * @param <T>
     */
    public <T extends UserData> void cacheLastCacheKey(long uid, Class<T> clazz, String cacheKey) {
        String lastCacheKeyMark = getLastCacheKeyMark(uid, clazz);
        if (StrUtil.isNotBlank(lastCacheKeyMark)) {
            LocalCache.getInstance().put(CACHE_TYPE, lastCacheKeyMark, cacheKey, CACHE_TIME_OUT);
        }
    }

    /**
     * 获得缓存key
     *
     * @param uid
     * @param clazz
     * @param date
     * @param <T>
     * @return
     */
    private <T extends UserData> String getCacheKey(long uid, Class<T> clazz, Date date) {
        UserDataType userDataType = UserDataType.fromClass(clazz);
        String key = "local_usr_" + uid + "_" + userDataType.getRedisKey() + "_" + (DateUtil.toDateTimeLong(date) / getCacheTimeOut(clazz));
        return key;
    }

    private <T extends UserData> String getLastCacheKey(long uid, Class<T> clazz) {
        String lastCacheKeyMark = getLastCacheKeyMark(uid, clazz);
        if (StrUtil.isNotBlank(lastCacheKeyMark)) {
            return LocalCache.getInstance().get(CACHE_TYPE, lastCacheKeyMark);
        }
        return null;
    }

    /**
     * 获取缓存最近的key的标识
     *
     * @param uid
     * @param clazz
     * @param <T>
     * @return
     */
    private <T extends UserData> String getLastCacheKeyMark(long uid, Class<T> clazz) {
        UserDataType userDataType = UserDataType.fromClass(clazz);
        String laskKeyMark = "local_usr_" + uid + "_" + userDataType.getRedisKey() + "_last_key";
        return laskKeyMark;
    }

    /**
     * 是否是支持本地缓存
     *
     * @param clazz
     * @param <T>
     * @return
     */
    private <T extends UserData> boolean isSupportLocalCache(Class<T> clazz) {
        return SUPPORT_DATA.contains(clazz);
    }

    /**
     * 获取缓存过期时间
     *
     * @param clazz
     * @param <T>
     * @return
     */
    private <T extends UserData> int getCacheTimeOut(Class<T> clazz) {
        if (clazz.getName().equals(UserMallRecord.class.getName())) {
            return CACHE_VALID_SHORT_SECONDS;
        }
        return CACHE_VALID_SECONDS;
    }


}
