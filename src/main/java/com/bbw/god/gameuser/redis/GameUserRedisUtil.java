package com.bbw.god.gameuser.redis;

import com.alibaba.fastjson.JSON;
import com.bbw.common.DateUtil;
import com.bbw.common.ListUtil;
import com.bbw.common.SpringContextUtil;
import com.bbw.common.StrUtil;
import com.bbw.db.redis.RedisHashUtil;
import com.bbw.db.redis.RedisValueUtil;
import com.bbw.exception.CoderException;
import com.bbw.god.db.entity.InsRoleInfoEntity;
import com.bbw.god.db.entity.InsUserDataEntity;
import com.bbw.god.db.entity.InsUserEntity;
import com.bbw.god.db.pool.PlayerDataDAO;
import com.bbw.god.db.service.InsRoleInfoService;
import com.bbw.god.game.data.redis.RedisKeyConst;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUser.Location;
import com.bbw.god.gameuser.GameUser.RoleInfo;
import com.bbw.god.gameuser.GameUser.Setting;
import com.bbw.god.gameuser.GameUser.Status;
import com.bbw.god.gameuser.UserDataType;
import com.bbw.god.gameuser.card.OppCardService;
import com.bbw.god.gameuser.statistic.StatisticServiceFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * GameUser属性的redis操作帮助类
 *
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2018-11-30 09:43
 */
@Slf4j
@Service
public class GameUserRedisUtil {

    @Autowired
    private RedisHashUtil<String, Integer> hashInteger;// 区服玩家计数器自增长值
    @Autowired
    private RedisHashUtil<String, Object> baseInfoRedis;// 玩家对象基本数据。存放GameUser对象
    @Autowired
    private RedisValueUtil<Object> valueRedis;// 玩家
    @Autowired
    private InsRoleInfoService roleInfoService;
    @Autowired
    private GameUserDataRedisUtil userDataRedis;
    @Autowired
    private StatisticServiceFactory statisticServiceFactory;
    @Autowired
    private OppCardService oppCardService;
    /** 一次性载入所有玩家数据。如果为false则只载入GameUser对象数据，仅在第一获取某一类型UserData的时候再载入此类型全部数据到redis */
    @Value("${load-all-user-data-one-time:true}")
    private boolean loadAll;
    // 以下为临时的key
    public static final String TOKEN_CACHE_KEY = "token";// 登录安全令牌
    public static final String ATTACKHISTORY_CACHE_KEY = "AttackHistory";// 战斗令牌

    public void toRedis(GameUser user) {
        String key = UserRedisKey.getGameUserKey(user.getId());
        Map<String, Object> map = new HashMap<>();
        map.put("id", key);
        map.put("serverId", user.getServerId());
        map.put("level", user.getLevel());
        map.put("experience", user.getExperience());
        if (user.getExperience() <= Integer.MAX_VALUE) {
            map.put("experience", user.getExperience().intValue());
        }
        map.put("gold", user.getGold());
        map.put("diamond", user.getDiamond());
        map.put("copper", user.getCopper());
        if (user.getCopper() <= Integer.MAX_VALUE) {
            map.put("copper", user.getCopper().intValue());
        }
        map.put("dice", user.getDice());
        map.put("goldEle", user.getGoldEle());
        map.put("woodEle", user.getWoodEle());
        map.put("waterEle", user.getWaterEle());
        map.put("fireEle", user.getFireEle());
        map.put("earthEle", user.getEarthEle());
        map.put("location", user.getLocation());
        baseInfoRedis.putAllField(key, map);
        valueRedis.set(UserRedisKey.getUserStatusKey(user.getId()), user.getStatus());
        valueRedis.set(UserRedisKey.getUserSettingKey(user.getId()), user.getSetting());
        valueRedis.set(UserRedisKey.getUserRoleInfoKey(user.getId()), user.getRoleInfo());
    }

    public void updateServerId(Long uid, int sid) {
        baseInfoRedis.putField(UserRedisKey.getGameUserKey(uid), "serverId", sid);
    }

    /**
     * 将用户数据完整载入到redis
     */
    private GameUser dbLoadToRedis(Long uid, boolean isWithUserData) {
        if (existsUser(uid) && userDataRedis.getLoadedDataTypeNum(uid) > 10) {
            throw CoderException.fatal("uid=[" + uid + "]已经载入！");
        }
        long begin = System.currentTimeMillis();
        StringBuilder sb = new StringBuilder();
        sb.append("\n开始载入[" + uid + "]玩家,");
        InsRoleInfoEntity info = roleInfoService.selectById(uid);
        if (null == info) {
            throw CoderException.high("不存在uid=[" + uid + "]的角色信息！");
        }
        PlayerDataDAO pdd = SpringContextUtil.getBean(PlayerDataDAO.class, info.getSid());
        InsUserEntity entity = pdd.dbSelectInsUserEntity(uid);
        if (null == entity) {
            throw CoderException.high("sid=[" + info.getSid() + "]的区服不存在uid=[" + uid + "]的信息！");
        }
        GameUser user = JSON.parseObject(entity.getDataJson(), GameUser.class);
        user.setServerId(info.getSid());
        user.getRoleInfo().setNickname(info.getNickname());
        // 载入GameUser对象
        toRedis(user);
        if (loadAll && isWithUserData) {
            // 载入所有用户数据
            // TODO:先采用一次性从数据库获取所有数据的方式进行，如果有性能问题，再优化成按照数据类型分次分批载入
            List<InsUserDataEntity> entityList = pdd.dbSelectUserData(uid);
            if (ListUtil.isEmpty(entityList)) {
                return user;
            }
            // 按照数据类型分拣一下
            Map<String, List<InsUserDataEntity>> typeListMap = entityList.stream().collect(Collectors.groupingBy(InsUserDataEntity::getDataType));
            // 输出调试信息
            sb.append("\n共[" + typeListMap.keySet().size() + "]种类型数据。");
            for (String dataTypeRedisKey : typeListMap.keySet()) {
                sb.append("\n类型[" + dataTypeRedisKey + "]数据量[" + typeListMap.get(dataTypeRedisKey).size() + "]");
                List<InsUserDataEntity> typeList = typeListMap.get(dataTypeRedisKey);
                userDataRedis.toRedisAsClazz(uid, typeList, UserDataType.fromRedisKey(dataTypeRedisKey).getEntityClass());
            }

            //从数据库加载数据，所以将所有的玩家数据标记为已加载
            for (UserDataType dataType : UserDataType.values()) {
                userDataRedis.setLoadStatus(uid, dataType);
            }
        }
        long end = System.currentTimeMillis();
        sb.append("\n载入[" + uid + "]玩家数据耗时:" + (end - begin));
        log.info(sb.toString());
        return user;
    }

    public GameUser fromRedis(Long uid, boolean isWithUserData) {
//        log.error("==============do fromRedis:" + uid, new Exception());
//        log.error("==============do fromRedis:" + uid);

        if (uid < 10 * 10000) {
            throw CoderException.normal("非法的角色ID=[" + uid + "]");
        }
        if (!existsUser(uid) || userDataRedis.getLoadedDataTypeNum(uid) < 10) {
            GameUser user = dbLoadToRedis(uid, isWithUserData);
            return user;
        }
        // 从redis中获取
        GameUser user = new GameUser();
        user.setId(uid);
        String id = UserRedisKey.getGameUserKey(uid);
        Map<String, Object> map = baseInfoRedis.get(id);
        user.setLocation((GameUser.Location) map.get("location"));
        user.setServerId(StrUtil.getInt(map.get("serverId")));
        user.setLevel(StrUtil.getInt(map.get("level")));
        user.setExperience(StrUtil.getLong(map.get("experience"), 0));
        user.setGold(StrUtil.getInt(map.get("gold")));
        user.setDiamond(StrUtil.getInt(map.get("diamond")));
        user.setCopper(StrUtil.getLong(map.get("copper"), 0));
        user.setDice(StrUtil.getInt(map.get("dice")));
        user.setGoldEle(StrUtil.getInt(map.get("goldEle")));
        user.setWoodEle(StrUtil.getInt(map.get("woodEle")));
        user.setWaterEle(StrUtil.getInt(map.get("waterEle")));
        user.setFireEle(StrUtil.getInt(map.get("fireEle")));
        user.setEarthEle(StrUtil.getInt(map.get("earthEle")));
        return user;
    }

    public void removeUserRedis(Long uid) {
        if (!existsUser(uid)) {
            System.out.println("redis中没有uid=" + uid + "的玩家基本数据！");
            return;
        }
        HashSet<String> keys = getGameUserRedisKeys(uid);
        valueRedis.delete(keys);
    }

    /**
     * 获取GameUser对象除UserData外相关的Key
     *
     * @param uid
     * @return
     */
    public HashSet<String> getGameUserRedisKeys(Long uid) {
        HashSet<String> keys = new HashSet<>();
        if (!existsUser(uid)) {
            return keys;
        }
        String baseKey = UserRedisKey.getGameUserKey(uid);
        keys.add(baseKey);
        keys.add(UserRedisKey.getUserStatusKey(uid));
        keys.add(UserRedisKey.getUserSettingKey(uid));
        keys.add(UserRedisKey.getUserRoleInfoKey(uid));
        // 统计的key
        keys.addAll(statisticServiceFactory.getKeys(uid));
        keys.add(statisticServiceFactory.getLoadKey(uid));
        // -- 临时的key
        String tokenKey = UserRedisKey.getRunTimeVarKey(uid, TOKEN_CACHE_KEY);
        keys.add(tokenKey);
        String attackKey = UserRedisKey.getRunTimeVarKey(uid, ATTACKHISTORY_CACHE_KEY);
        keys.add(attackKey);
        keys.add(oppCardService.getKeyToDel(uid));
        //
        for (UserDataType udt : UserDataType.values()) {
            keys.addAll(userDataRedis.getUserDataKeys(uid, udt.getEntityClass()));
            keys.addAll(userDataRedis.getUserDataRelatedKeys(uid, udt.getEntityClass()));
        }
        return keys;
    }

    /**
     * 判断gameuser是否载入
     *
     * @param uid
     * @return
     */
    public boolean existsUser(long uid) {
//        System.out.println("==============do existsUser:" + uid);
        String key = UserRedisKey.getGameUserKey(uid);
        return baseInfoRedis.exists(key);
    }

    private Long increment(Long uid, String property, long value) {
        Long d = baseInfoRedis.increment(UserRedisKey.getGameUserKey(uid), property, value);
        return d;
    }

    public Long incLevel(Long uid, int add) {
        Long afterValue = increment(uid, "level", add);
        return afterValue;
    }

    public Long incExperience(Long uid, long value) {
        return increment(uid, "experience", value);
    }

    public Long incGold(Long uid, int value) {
        return increment(uid, "gold", value);
    }

    public Long incDiamond(Long uid, int value) {
        return increment(uid, "diamond", value);
    }

    public Long incCopper(Long uid, long value) {
        return increment(uid, "copper", value);
    }

    public Long incDice(Long uid, int value) {
        return increment(uid, "dice", value);
    }

    public Long incGoldEle(Long uid, int value) {
        return increment(uid, "goldEle", value);
    }

    public Long incWoodEle(Long uid, int value) {
        return increment(uid, "woodEle", value);
    }

    public Long incWaterEle(Long uid, int value) {
        return increment(uid, "waterEle", value);
    }

    public Long incFireEle(Long uid, int value) {
        return increment(uid, "fireEle", value);
    }

    public Long incEarthEle(Long uid, int value) {
        return increment(uid, "earthEle", value);
    }

    @Nullable
    public GameUser.Status getUserStatus(Long uid) {
        Object obj = valueRedis.get(UserRedisKey.getUserStatusKey(uid));
        if (null == obj) {
            return null;
        }
        GameUser.Status status = (GameUser.Status) obj;
        return status;
    }

    @Nullable
    public GameUser.Setting getUserSetting(Long uid) {
        Object obj = valueRedis.get(UserRedisKey.getUserSettingKey(uid));
        if (null == obj) {
            return null;
        }
        GameUser.Setting setting = (GameUser.Setting) obj;
        return setting;
    }

    @Nullable
    public GameUser.RoleInfo getUserRoleInfo(Long uid) {
        String key = UserRedisKey.getUserRoleInfoKey(uid);
        Object obj = valueRedis.get(key);
        if (null == obj) {
            return null;
        }
        GameUser.RoleInfo roleInfo = (GameUser.RoleInfo) obj;
        return roleInfo;
    }

    public void updateLocation(Long uid, Location location) {
        baseInfoRedis.putField(UserRedisKey.getGameUserKey(uid), "location", location);
    }

    public void updateStatus(Long uid, Status status) {
        valueRedis.set(UserRedisKey.getUserStatusKey(uid), status);
    }

    public void updateSetting(Long uid, Setting setting) {
        valueRedis.set(UserRedisKey.getUserSettingKey(uid), setting);
    }

    public void updateRoleInfo(Long uid, RoleInfo roleInfo) {
        valueRedis.set(UserRedisKey.getUserRoleInfoKey(uid), roleInfo);
    }

    /**
     * <pre>
     * 获取一个新的玩家ID。
     * 如果修改此处生成方式：
     * 1.必须同步修改UsersDataPool.UserIdEntity.setPlayerId的实现。
     * 2.必须同步修改InsUserDataEntity.fromUserData的实现。
     * </pre>
     *
     * @param loginSid:登录区服的ID
     * @return
     */
    public Long getNewPlayerId(int loginSid) {
        String key = RedisKeyConst.RUNTIME_KEY + RedisKeyConst.SPLIT + "userIdSeq";
        String fieldKey = loginSid + "serverSeq";
        hashInteger.increment(key, fieldKey, 1);
        int lastSeq = hashInteger.getField(key, fieldKey);
        // 6位日期(yyMMdd)+4位原始区服ID+5位区服玩家计数器
        String tpl = "%04d%05d";
        String id = DateUtil.toString(DateUtil.now(), "yyMMdd") + String.format(tpl, loginSid, lastSeq);
        return Long.valueOf(id);
    }
}
