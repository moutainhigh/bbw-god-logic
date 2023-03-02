package com.bbw.god.server;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.bbw.common.DateUtil;
import com.bbw.common.PowerRandom;
import com.bbw.common.SetUtil;
import com.bbw.common.SpringContextUtil;
import com.bbw.db.redis.RedisValueUtil;
import com.bbw.god.cache.GameDataTimeLimitCacheUtil;
import com.bbw.god.db.async.UpdateRoleInfoAsyncHandler;
import com.bbw.god.db.entity.CfgServerEntity;
import com.bbw.god.db.entity.InsRoleInfoEntity;
import com.bbw.god.db.entity.InsUserEntity;
import com.bbw.god.db.pool.PlayerDataDAO;
import com.bbw.god.db.pool.PlayerPool;
import com.bbw.god.db.pool.UserDataPool;
import com.bbw.god.db.service.InsRoleInfoService;
import com.bbw.god.detail.LogUtil;
import com.bbw.god.game.config.Cfg;
import com.bbw.god.game.config.CfgGame;
import com.bbw.god.game.data.redis.RedisKeyConst;
import com.bbw.god.game.online.GameOnlineService;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.UserDataType;
import com.bbw.god.gameuser.config.GameUserConfig;
import com.bbw.god.gameuser.historydata.DelHistoryDataService;
import com.bbw.god.gameuser.redis.GameUserDataRedisUtil;
import com.bbw.god.gameuser.redis.GameUserRedisUtil;
import com.bbw.god.notify.push.UserPush;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-01-18 16:25
 */
@Slf4j
@Service
public class ServerUserService {
    @Autowired
    private InsRoleInfoService roleInfo;
    @Autowired
    private GameUserRedisUtil userRedis;
    @Autowired
    private GameUserDataRedisUtil userDataRedis;
    @Autowired
    private PlayerPool playerPool;
    @Autowired
    private UserDataPool dataPool;
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private GameOnlineService gameOnlineService;
    @Autowired
    private RedisValueUtil<Object> valueRedis;// 玩家
    @Autowired
    private DelHistoryDataService delHistoryDataService;
    @Autowired
    private UpdateRoleInfoAsyncHandler updateRoleInfoAsyncHandler;

    /**
     * 创建一个新角色
     *
     * @param param
     * @return
     */
    public GameUser newGameUser(RoleVO param, int cid, Integer accountRegDate) {
        // 创建角色信息
        GameUser gu = new GameUser();
        gu.setId(userRedis.getNewPlayerId(param.getServerId()));
        CfgServerEntity loginServer = Cfg.I.get(param.getServerId(), CfgServerEntity.class);
        gu.setServerId(loginServer.getMergeSid());
        gu.setCopper(GameUserConfig.bean().getInitCopper());
        gu.setGold(GameUserConfig.bean().getInitGold());
        gu.setDice(GameUserConfig.bean().getInitDice());
        gu.getRoleInfo().setUserName(param.getUserName());
        gu.getRoleInfo().setNickname(param.getNickname());
        gu.getRoleInfo().setHead(Integer.valueOf(param.getHead()));
        gu.getRoleInfo().setSex(Integer.valueOf(param.getSex()));
        gu.getRoleInfo().setCountry(Integer.valueOf(param.getProperty()));
        gu.getRoleInfo().setMyInvitationCode(param.getMyInviCode());
        gu.getRoleInfo().setEnterInvitationCode(param.getInvitationCode());
        gu.getRoleInfo().setRegTime(DateUtil.now());
        // Optional<CfgChannelEntity> channel =
        // Cfg.I.get(CfgChannelEntity.class).stream().filter(c ->
        // c.getPlatCode().equals(param.getChannelCode())).findFirst();
        // if (channel.isPresent()) {
        // gu.getRoleInfo().setChannelId(channel.get().getId());
        // }
        gu.getRoleInfo().setChannelId(cid);
        gu.getStatus().setSalaryCopperTime(gu.getRoleInfo().getRegTime());
        userRedis.toRedis(gu);
        //创建角色无需从数据库加载数据，所以将所有的玩家数据标记为已加载
        for (UserDataType dataType : UserDataType.values()) {
            userDataRedis.setLoadStatus(gu.getId(), dataType);
        }
        //
        InsRoleInfoEntity roleInfoEntity = InsRoleInfoEntity.fromGameUser(gu, loginServer, accountRegDate);
        // System.out.println(param);
        roleInfoEntity.setRegIp(param.getIp());
        roleInfoEntity.setRegDevice(param.getDeviceId());
        roleInfoEntity.setServerName(loginServer.getName());
        roleInfo.insert(roleInfoEntity);
        InsUserEntity entity = InsUserEntity.fromGameUser(gu);
        PlayerDataDAO pdd = SpringContextUtil.getBean(PlayerDataDAO.class, entity.getSid());
        pdd.dbInsertInsUserEntity(entity);
        // 同步到明细数据库的账号映射中
        updateRoleInfoAsyncHandler.setRoleInfo(roleInfoEntity, 0);
        return gu;
    }

    public GameUser getGameUser(Long uid) {
        GameUser user = userRedis.fromRedis(uid, false);
        return user;
    }

    public List<GameUser> getGameUser(Collection<Long> uids) {
        if (null == uids || uids.isEmpty()) {
            return new ArrayList<GameUser>();
        }
        ArrayList<GameUser> userList = new ArrayList<>(uids.size());
        for (Long uid : uids) {
            GameUser u = getGameUser(uid);
            if (null != u) {
                userList.add(u);
            }
        }
        if (uids.size() != userList.size()) {
            log.warn("期望获取的用户数与实际得到的用户数不一致。uids=" + uids);
        }
        return userList;
    }

    /**
     * 根据邀请码获取GameUser对象
     *
     * @param sId
     * @param invitationCode
     * @return
     */
    public GameUser getGameUser(int sId, String invitationCode) {
        Optional<Long> uid = getUidByInvitationCode(sId, invitationCode);
        if (uid.isPresent()) {
            return this.getGameUser(uid.get());
        }
        return null;
    }

    /**
     * 把玩家当前redis到数据完整保存到数据库后再从redis中移除玩家数据
     *
     * @param uid
     */
    public void unloadGameUser(long uid) {
        // 如果未载入Redis则不做任何处理
        if (!userRedis.existsUser(uid)) {
            HashSet<String> keys = new HashSet<>();
            List<UserDataType> userDataTypes = Arrays.asList(UserDataType.USER_PUSH, UserDataType.MAIL, UserDataType.CARD,
                    UserDataType.CARD_GROUP, UserDataType.CITY, UserDataType.USER_WAR_TOKEN_TASK);
            for (UserDataType userDataType : userDataTypes) {
                if (!userDataRedis.hasLoadFromDb(uid, userDataType)) {
                    continue;
                }
                keys.addAll(userDataRedis.getUserDataKeys(uid, userDataType.getEntityClass()));
                keys.addAll(userDataRedis.getUserDataRelatedKeys(uid, userDataType.getEntityClass()));
            }
            valueRedis.delete(keys);
            return;
        }
        deleteRedisDataBeforeUnload(uid);
        // 删除数据库里面的玩家法宝、特产
        deleteDbDataBeforeUnload(uid, Arrays.asList(UserDataType.SPECIAL, UserDataType.TREASURE));
        Set<Long> uids = new HashSet<>();
        uids.add(uid);
        // 保存GameUser对象到数据库
        playerPool.dbUpdatePlayer(uids);
        HashSet<String> userDataKeys = new HashSet<>();
        for (UserDataType udt : UserDataType.values()) {
            if (!userDataRedis.hasLoadFromDb(uid, udt)) {
                continue;
            }
            //添加要操作的数据类型
            userDataKeys.addAll(userDataRedis.getUserDataKeys(uid, udt.getEntityClass()));
        }
        // 保存所有玩家数据到数据库
        dataPool.dbUpdate(userDataKeys);
        userRedis.removeUserRedis(uid);
    }

    public void unloadGameUsers(List<Long> uids, String desc) {
        // 先保存GameUser对象
        long begin = System.currentTimeMillis();
        for (int i = 0; i < uids.size(); i++) {
            try {
                long uid = uids.get(i);
                log.info("uid:" + uid + " unload data from redis... (" + (i + 1) + "/" + uids.size() + ")");
                unloadGameUser(uid);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
        long end = System.currentTimeMillis();
        log.info("区服{}需要保存{}个玩家数据,耗时{}毫秒!", desc, uids.size(), (end - begin));
//        try {
//            begin = System.currentTimeMillis();
//            List<Long> usersInRedis = uids.stream().filter(tmp -> userRedis.existsUser(tmp)).collect(Collectors.toList());
//            statisticServiceFactory.saveToDb(usersInRedis, DateUtil.getTodayInt());
//            statisticServiceFactory.delFromRedis(usersInRedis);
//            end = System.currentTimeMillis();
//            log.info("区服{}需要保存{}个玩家的统计数据,耗时{}毫秒!", desc, uids.size(), (end - begin));
//        } catch (Exception e) {
//            log.error(e.getMessage(), e);
//        }
    }

    /**
     * 卸载区服所有玩家
     *
     * @param sid
     */
    public void unloadAllUserByServer(int sid) {
        Set<Long> uids = getUidsBySid(sid);
        for (Long uid : uids) {
            unloadGameUser(uid);
        }
    }

    /**
     * 根据区服玩家ID获取玩家昵称
     *
     * @param sid
     * @param uid
     * @return
     */
    public String getNickNameByUid(int sid, Long uid) {
        GameUser user = getGameUser(uid);
        return user.getRoleInfo().getNickname();
    }

    /**
     * 是否在线
     *
     * @param uid
     * @return
     */
    public boolean isOnline(long uid) {
        try {
            return gameOnlineService.isOnlineWithinFiveMinute(gameUserService.getActiveSid(uid), uid);
        } catch (Exception e) {
            log.error("检查玩家是否在线出错");
            log.error(e.getMessage());
        }
        return false;
    }

    /**
     * 获得等级范围内[minLevel,maxLevel]的随机玩家
     *
     * @param sId
     * @param minLevel
     * @param maxLevel
     * @return
     */
    @Nullable
    public GameUser getRandomGu(int sId, int minLevel, int maxLevel) {
        Set<Long> uids = this.getUidsLevelBetween(sId, minLevel, maxLevel);
        if (SetUtil.isEmpty(uids)) {
            return null;
        }
        Long match = PowerRandom.getRandomFromList(new ArrayList<>(uids));
        return this.getGameUser(match);
    }

    /**
     * 获取随机昵称
     *
     * @return
     */
    public String getRandomNickName() {
        List<String> nicknames = Cfg.I.getUniqueConfig(CfgGame.class).gainNicknames();
        String nickName = PowerRandom.getRandomFromList(nicknames);
        while (null == nickName || "".equals(nickName)) {
            nickName = PowerRandom.getRandomFromList(nicknames);
        }
        return nickName;
    }

    /**
     * 根据玩家账号获取玩家ID
     *
     * @param sid
     * @param username
     * @return
     */
    public Optional<Long> getUidByUsername(int sid, String username) {
        InsRoleInfoEntity role = roleInfo.selectOne(new EntityWrapper<InsRoleInfoEntity>().eq("sid", sid).eq("username", username));
        if (null == role) {
            return Optional.empty();
        }
        return Optional.ofNullable(role.getUid());
    }

    /**
     * 根据玩家账号获取玩家ID
     *
     * @param originSid
     * @param username
     * @return
     */
    public Optional<Long> getUidByOriginSidAndUsername(int originSid, String username) {
        //优先从缓存获取
        String cacheKey = "uidCache" + RedisKeyConst.SPLIT + originSid + "_" + username;
        Long uid = GameDataTimeLimitCacheUtil.getFromCache(cacheKey, Long.class);
        if (null != uid) {
            return Optional.ofNullable(uid);
        }
        //从MySQL获取
        InsRoleInfoEntity role = roleInfo.selectOne(new EntityWrapper<InsRoleInfoEntity>().eq("origin_sid", originSid).eq("username", username));
        if (null == role) {
            return Optional.empty();
        }
        uid = role.getUid();
        //缓存到Redis
        GameDataTimeLimitCacheUtil.cache(cacheKey, uid, DateUtil.SECOND_ONE_WEEK);
        return Optional.ofNullable(role.getUid());
    }

    /**
     * /** 获取等级在[min,max]之间的玩家ID
     *
     * @param min
     * @param max
     * @return
     */
    @NonNull
    public Set<Long> getUidsLevelBetween(int sid, int min, int max) {
        String where = "sid=" + sid + " AND level BETWEEN " + min + " AND " + max;
        List<InsRoleInfoEntity> roles = roleInfo.selectList(new EntityWrapper<InsRoleInfoEntity>().where(where));
        if (null == roles) {
            return new HashSet<Long>(0);
        }
        Set<Long> uids = new HashSet<Long>(roles.size());
        roles.forEach(role -> uids.add(role.getUid()));
        return uids;
    }

    @NonNull
    public Set<Long> getUidsLevelBetween(int min, int max) {
        String where = " level BETWEEN " + min + " AND " + max;
        List<InsRoleInfoEntity> roles = roleInfo.selectList(new EntityWrapper<InsRoleInfoEntity>().where(where));
        if (null == roles) {
            return new HashSet<Long>(0);
        }
        Set<Long> uids = new HashSet<Long>(roles.size());
        roles.forEach(role -> uids.add(role.getUid()));
        return uids;
    }

    /**
     * 根据昵称获取玩家ID
     *
     * @param sid
     * @param nickname
     * @return
     */
    public Optional<Long> getUidByNickName(int sid, String nickname) {
        InsRoleInfoEntity role = roleInfo.selectOne(new EntityWrapper<InsRoleInfoEntity>().eq("sid", sid).eq("nickname", nickname));
        if (null == role) {
            return Optional.empty();
        }
        return Optional.ofNullable(role.getUid());
    }

    /**
     * 查询区服中是否存在指定的昵称
     *
     * @param sid
     * @param nickname 昵称
     * @return 返回与条件匹配的昵称
     */
    public List<String> checkServerNickname(int sid, List<String> nickname) {
        EntityWrapper<InsRoleInfoEntity> ew = new EntityWrapper<InsRoleInfoEntity>();
        ew.setSqlSelect("nickname").eq("sid", sid).in("nickname", nickname);
        List<InsRoleInfoEntity> role = roleInfo.selectList(ew);
        if (role.isEmpty()) {
            return new ArrayList<String>();
        }
        List<String> res = role.stream().map(InsRoleInfoEntity::getNickname).distinct().collect(Collectors.toList());
        return res;
    }

    /**
     * 根据关键字查询昵称配置到用户ID，不返回null。
     *
     * @param sid
     * @param keyWord
     * @return
     */
    public List<Long> getUidsNickNameLike(int sid, String keyWord) {
        List<InsRoleInfoEntity> roles = roleInfo.selectList(new EntityWrapper<InsRoleInfoEntity>().eq("sid", sid).like("nickname", keyWord));
        if (null == roles) {
            return new ArrayList<Long>(0);
        }
        ArrayList<Long> uids = new ArrayList<Long>(roles.size());
        roles.forEach(role -> uids.add(role.getUid()));
        return uids;
    }

    /**
     * 根据邀请码获取玩家ID
     *
     * @param sid
     * @param invitationCode
     * @return
     */
    public Optional<Long> getUidByInvitationCode(int sid, String invitationCode) {
        InsRoleInfoEntity role = roleInfo.selectOne(new EntityWrapper<InsRoleInfoEntity>().eq("sid", sid).eq("invi_code", invitationCode));
        if (null == role) {
            return Optional.empty();
        }
        return Optional.ofNullable(role.getUid());
    }

    /**
     * 区服是否存在此UID
     *
     * @param uid
     * @return
     */
    public boolean existsUid(Long uid) {
        InsRoleInfoEntity role = roleInfo.selectById(uid);
        return null != role;
    }

    /**
     * 获取区服的所有玩家ID
     *
     * @param sid
     * @return
     */
    public Set<Long> getUidsBySid(int sid) {
        List<Long> roles = roleInfo.getAllUidsByServer(sid);
        if (null == roles) {
            return new HashSet<Long>(0);
        }
        HashSet<Long> uids = new HashSet<Long>(roles.size());
        uids.addAll(roles);
        return uids;
    }

    /**
     * 最近某个区服多少天内登录的玩家
     *
     * @param sid
     * @param daysIn
     * @return
     */
    public Set<Long> getUidsInDays(int sid, int daysIn) {
        Date endDate = DateUtil.addDays(DateUtil.now(), -daysIn);
        List<Long> roles = roleInfo.getUidsLoginAfter(sid, DateUtil.toDateInt(endDate));
        if (null == roles) {
            return new HashSet<Long>(0);
        }
        HashSet<Long> uids = new HashSet<Long>(roles.size());
        uids.addAll(roles);
        return uids;
    }

    /**
     * 获取某个区服某个渠道的多少天内登录的玩家
     *
     * @param sid
     * @param cid
     * @param daysIn
     * @return
     */
    public Set<Long> getUidsInDays(int sid, int cid, int daysIn) {
        Date endDate = DateUtil.addDays(DateUtil.now(), -daysIn);
        List<Long> roles = roleInfo.getUidsLoginAfter(sid, cid, DateUtil.toDateInt(endDate));
        if (null == roles) {
            return new HashSet<Long>(0);
        }
        HashSet<Long> uids = new HashSet<Long>(roles.size());
        uids.addAll(roles);
        return uids;
    }

    private void deleteDbDataBeforeUnload(long uid, List<UserDataType> types) {
        String typesToDel = "";
        for (UserDataType type : types) {
            // 如果已加载到Redis,则加入删除队列
            if (userDataRedis.hasRoaded(uid, type)) {
                typesToDel += "'" + type.getRedisKey() + "',";
            }
        }
        if (typesToDel.length() > 0) {
            typesToDel = typesToDel.substring(0, typesToDel.length() - 1);
            log.info("uid:" + uid + " delete ins_user_data by types " + typesToDel);
            PlayerDataDAO playerDataDAO = SpringContextUtil.getBean(PlayerDataDAO.class, gameUserService.getActiveSid(uid));
            playerDataDAO.dbDeleteUserDataByTypes(uid, typesToDel);
        }
    }

    private void deleteRedisDataBeforeUnload(long uid) {
        if (userDataRedis.hasLoadFromDb(uid, UserDataType.MAIL)) {
            delHistoryDataService.delExpireMail(uid);
        }
        if (userDataRedis.hasLoadFromDb(uid, UserDataType.USER_WAR_TOKEN_TASK)) {
            delHistoryDataService.delExpiredWarTokenTasks(uid);
        }
        if (userDataRedis.hasLoadFromDb(uid, UserDataType.USER_PUSH)) {
            List<UserPush> userPushes = gameUserService.getMultiItems(uid, UserPush.class);
            if (userPushes.size() > 1) {
                List<UserPush> userPushesToDel = userPushes.subList(0, userPushes.size() - 1);
                LogUtil.logDeletedUserDatas(userPushesToDel, "删除多余的数据");
                gameUserService.deleteItems(uid, userPushesToDel);
            }
        }
    }

}
