package com.bbw.god.gameuser;

import com.bbw.App;
import com.bbw.common.ListUtil;
import com.bbw.exception.CoderException;
import com.bbw.god.db.entity.CfgServerEntity;
import com.bbw.god.db.pool.UserDataPool;
import com.bbw.god.db.service.InsRoleInfoService;
import com.bbw.god.game.config.server.ServerTool;
import com.bbw.god.gameuser.redis.GameUserDataRedisUtil;
import com.bbw.god.gameuser.redis.GameUserRedisUtil;
import com.bbw.god.gameuser.redis.UserRedisKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2018-11-24 09:50
 */
@Slf4j
@Service("gameUserService")
public class GameUserServiceImpl implements GameUserService {
    private static ConcurrentHashMap<Long, Integer> uidActiveSid = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<Long, String> uidMapAccount = new ConcurrentHashMap<>();
    @Autowired
    private UserDataPool dataPool;
    @Autowired
    private GameUserDataRedisUtil userDataRedis;
    @Autowired
    private GameUserRedisUtil userRedis;
    @Autowired
    private InsRoleInfoService roleInfoService;
    @Autowired
    private App app;

    @Override
    public void addItem(long uid, UserData data) {
        if (uid != data.getGameUserId()) {
            log.error("用户资源数据添加错误。UID=[" + uid + "]。UserData=" + data);
            throw CoderException.high("用户资源数据添加错误。");
        }
        userDataRedis.toRedis(data);
        // 添加到等待持久化到数据池
        String key = UserRedisKey.getUserDataKey(data);
        dataPool.toInsertPool(key);
    }

    @Override
    public <T extends UserData> void addItems(List<T> dataList) {
        userDataRedis.toRedis(dataList);
        Set<String> keys = new HashSet<>(dataList.size());
        for (UserData data : dataList) {
            keys.add(UserRedisKey.getUserDataKey(data));
        }
        if (null == keys || keys.isEmpty()) {
            return;
        }
        // 玩家ID.资源类型.资源ID
        String[] values = keys.toArray(new String[0]);
        dataPool.toInsertPool(values);
    }

    @Override
    public void deleteItem(UserData data) {
        if (null == data) {
            throw CoderException.normal("数据不存在！");
        }
        userDataRedis.deleteFromRedis(data);
        // 添加到等待持久化到数据池
        String key = UserRedisKey.getUserDataKey(data);
        dataPool.toDeletePool(key);
    }

    @Override
    public <T extends UserData> void deleteItems(long uid, List<T> objs) {
        if (ListUtil.isEmpty(objs)) {
            return;
        }
        List<Long> dataIds = objs.stream().map(UserData::getId).collect(Collectors.toList());
        deleteItems(uid, dataIds, objs.get(0).getClass());

    }

    @Override
    public <T extends UserData> void deleteItems(long uid, List<Long> dataIds, Class<T> clazz) {
        if (null == dataIds || dataIds.isEmpty()) {
            return;
        }
        Set<String> keys = userDataRedis.deleteFromRedis(uid, dataIds, clazz);
        if (null == keys || keys.isEmpty()) {
            return;
        }
        // 玩家ID.资源类型.资源ID
        String[] values = keys.toArray(new String[0]);
        dataPool.toDeletePool(values);
    }

    @Override
    public <T extends UserCfgObj> T getCfgItem(long gameUserId, int baseId, Class<T> objClass) {
        List<T> items = getMultiItems(gameUserId, objClass);
        if (items == null || items.isEmpty()) {
            return null;
        }
        Optional<T> opt = items.stream().filter(item -> item.getBaseId() != null && item.getBaseId().intValue() == baseId).findFirst();
        return opt.orElse(null);
    }

    @Override
    public <T extends UserCfgObj> List<T> getCfgItems(long gameUserId, List<Integer> baseIds, Class<T> objClass) {
        List<T> items = getMultiItems(gameUserId, objClass);
        if (null == items || items.isEmpty()) {
            return new ArrayList<T>();
        }
        List<T> objs = new ArrayList<>();
        for (Integer baseId : baseIds) {
            Optional<T> opt = items.stream().filter(item -> item.getBaseId().intValue() == baseId.intValue()).findFirst();
            opt.ifPresent(obj -> objs.add(obj));
        }
        objs.stream().sorted(Comparator.comparing(UserData::getId));
        return objs;
    }

    @Override
    public GameUser getGameUserWithUserData(long uid) {
//        System.out.println("==============do getGameUserWithUserData:" + uid);
        GameUser user = userRedis.fromRedis(uid, true);
        return user;
    }

    @Override
    public GameUser getGameUser(long uid) {
//        System.out.println("==============do getGameUserWithUserData:" + gameUserId);
        GameUser user = userRedis.fromRedis(uid, false);
        return user;
    }

    @Override
    public boolean isInRedis(long uid) {
//        System.out.println("==============do isInRedis:" + uid);
        return userRedis.existsUser(uid);
    }

    @Override
    public <T extends UserData> List<T> getMultiItems(long gameUserId, Class<T> clazz) {
        // log.error(System.currentTimeMillis() + "==========get data from redis：" + clazz.getName());
        if (gameUserId < 0) {
            return new ArrayList<>();
        }
        List<T> objs = userDataRedis.fromRedis(gameUserId, clazz);
        try {
            if (!objs.isEmpty()) {
                long start = System.currentTimeMillis();

                objs.sort(Comparator.comparing(UserData::getId));

                long end = System.currentTimeMillis();
                if (end - start > 100) {
                    log.error("{}批量获取{},获取数量{},耗时{}", gameUserId, clazz.getSimpleName(), objs.size(), end - start);
                }
            }
        } catch (Exception e) {
            String msg = "获取uid=" + gameUserId + "的" + clazz.getSimpleName() + "数据异常！";
            throw CoderException.high(msg, e);
        }
        return objs;
    }

    @Override
    public <T extends UserSingleObj> T getSingleItem(long gameUserId, Class<T> clazz) {
        if (gameUserId < 0) {
            return null;
        }
        List<T> datas = userDataRedis.fromRedis(gameUserId, clazz);
        if (datas.size() > 0) {
            return datas.get(0);
        }
        return null;
    }

    @Override
    public <T extends UserData> Optional<T> getUserData(long uid, long dataId, Class<T> clazz) {
        if (uid < 0) {
            return Optional.empty();
        }
        T obj = userDataRedis.fromRedis(uid, clazz, dataId);
        if (null == obj) {
            String msg = "不存在的对象。uid=[" + uid + "] Class=[" + clazz + "] dataId=[" + dataId + "]";
            //出现data为0，意味着业务数据有问题
            if (dataId == 0) {
                log.error(msg, new Exception("获取userdata失败"));
            } else {
                log.warn(msg);
            }
            return Optional.empty();
        }
        return Optional.of(obj);
    }

    @Override
    public <T extends UserData> List<T> getUserDatas(long uid, Collection<Long> dataIds, Class<T> clazz) {
        try {
            if (uid < 0) {
                return new ArrayList<>();
            }
            List<T> objs = userDataRedis.fromRedis(uid, clazz, dataIds);
            if (!objs.isEmpty()) {
                long start = System.currentTimeMillis();

                objs.sort(Comparator.comparing(UserData::getId));

                long end = System.currentTimeMillis();
                if (end - start > 100) {
                    log.error("{}批量获取{},获取数量{},耗时{}", uid, clazz.getSimpleName(), objs.size(), end - start);
                }
            }
            return objs;
        } catch (Exception e) {
            String msg = "获取uid=" + uid + "的" + clazz.getSimpleName() + "数据异常！";
            throw CoderException.high(msg, e);
        }
    }

    @Override
    public <T extends UserCfgObj> boolean isExistCfgItem(long gameUserId, int baseId, Class<T> objClass) {
        T t = getCfgItem(gameUserId, baseId, objClass);
        return t != null;
    }

    @Override
    public void updateItem(UserData data) {
        if (null == data) {
            return;
            // throw CoderException.normal("数据不存在！");
        }
        userDataRedis.toRedis(data);
        // 添加到等待持久化到数据池
        String key = UserRedisKey.getUserDataKey(data);
        dataPool.toUpdatePool(key);
    }

    @Override
    public <T extends UserData> void updateItems(List<T> dataList) {
        userDataRedis.toRedis(dataList);
        Set<String> keys = new HashSet<>(dataList.size());
        for (UserData data : dataList) {
            keys.add(UserRedisKey.getUserDataKey(data));
        }
        if (null == keys || keys.isEmpty()) {
            return;
        }
        // 玩家ID.资源类型.资源ID
        String[] values = keys.toArray(new String[0]);
        dataPool.toUpdatePool(values);
    }

    @Override
    public int getActiveSid(long uid) {
        Integer sid = uidActiveSid.get(uid);
        if (null != sid) {
            return sid;
        }
        sid = ServerTool.getActiveSid(uid);
        if (sid <= 0) {
            return sid;
        }
        uidActiveSid.put(uid, sid);
        return sid;
    }

    @Override
    public int getActiveGid(long uid) {
        int sid = getActiveSid(uid);
        return ServerTool.getServerGroup(sid);
    }

    @Override
    public CfgServerEntity getOriServer(long uid) {
        return ServerTool.getOriServer(uid);
    }

    @Override
    public <T extends UserData> Long getDataCount(Long uid, Class<T> objClass) {
        return userDataRedis.getSize(uid, objClass);
    }

    @Override
    public void setActiveSid(Long uid, Integer sid) {
        uidActiveSid.put(uid, sid);
    }

    @Override
    public String getAccount(long uid) {
        String account = uidMapAccount.get(uid);
        if (null == account) {
            uidMapAccount.put(uid, getGameUser(uid).getRoleInfo().getUserName());
        }
        return uidMapAccount.get(uid);
    }
}
