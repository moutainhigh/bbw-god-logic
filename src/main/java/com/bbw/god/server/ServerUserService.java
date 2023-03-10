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
    private RedisValueUtil<Object> valueRedis;// ??????
    @Autowired
    private DelHistoryDataService delHistoryDataService;
    @Autowired
    private UpdateRoleInfoAsyncHandler updateRoleInfoAsyncHandler;

    /**
     * ?????????????????????
     *
     * @param param
     * @return
     */
    public GameUser newGameUser(RoleVO param, int cid, Integer accountRegDate) {
        // ??????????????????
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
        //?????????????????????????????????????????????????????????????????????????????????????????????
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
        // ??????????????????????????????????????????
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
            log.warn("???????????????????????????????????????????????????????????????uids=" + uids);
        }
        return userList;
    }

    /**
     * ?????????????????????GameUser??????
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
     * ???????????????redis??????????????????????????????????????????redis?????????????????????
     *
     * @param uid
     */
    public void unloadGameUser(long uid) {
        // ???????????????Redis?????????????????????
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
        // ?????????????????????????????????????????????
        deleteDbDataBeforeUnload(uid, Arrays.asList(UserDataType.SPECIAL, UserDataType.TREASURE));
        Set<Long> uids = new HashSet<>();
        uids.add(uid);
        // ??????GameUser??????????????????
        playerPool.dbUpdatePlayer(uids);
        HashSet<String> userDataKeys = new HashSet<>();
        for (UserDataType udt : UserDataType.values()) {
            if (!userDataRedis.hasLoadFromDb(uid, udt)) {
                continue;
            }
            //??????????????????????????????
            userDataKeys.addAll(userDataRedis.getUserDataKeys(uid, udt.getEntityClass()));
        }
        // ????????????????????????????????????
        dataPool.dbUpdate(userDataKeys);
        userRedis.removeUserRedis(uid);
    }

    public void unloadGameUsers(List<Long> uids, String desc) {
        // ?????????GameUser??????
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
        log.info("??????{}????????????{}???????????????,??????{}??????!", desc, uids.size(), (end - begin));
//        try {
//            begin = System.currentTimeMillis();
//            List<Long> usersInRedis = uids.stream().filter(tmp -> userRedis.existsUser(tmp)).collect(Collectors.toList());
//            statisticServiceFactory.saveToDb(usersInRedis, DateUtil.getTodayInt());
//            statisticServiceFactory.delFromRedis(usersInRedis);
//            end = System.currentTimeMillis();
//            log.info("??????{}????????????{}????????????????????????,??????{}??????!", desc, uids.size(), (end - begin));
//        } catch (Exception e) {
//            log.error(e.getMessage(), e);
//        }
    }

    /**
     * ????????????????????????
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
     * ??????????????????ID??????????????????
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
     * ????????????
     *
     * @param uid
     * @return
     */
    public boolean isOnline(long uid) {
        try {
            return gameOnlineService.isOnlineWithinFiveMinute(gameUserService.getActiveSid(uid), uid);
        } catch (Exception e) {
            log.error("??????????????????????????????");
            log.error(e.getMessage());
        }
        return false;
    }

    /**
     * ?????????????????????[minLevel,maxLevel]???????????????
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
     * ??????????????????
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
     * ??????????????????????????????ID
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
     * ??????????????????????????????ID
     *
     * @param originSid
     * @param username
     * @return
     */
    public Optional<Long> getUidByOriginSidAndUsername(int originSid, String username) {
        //?????????????????????
        String cacheKey = "uidCache" + RedisKeyConst.SPLIT + originSid + "_" + username;
        Long uid = GameDataTimeLimitCacheUtil.getFromCache(cacheKey, Long.class);
        if (null != uid) {
            return Optional.ofNullable(uid);
        }
        //???MySQL??????
        InsRoleInfoEntity role = roleInfo.selectOne(new EntityWrapper<InsRoleInfoEntity>().eq("origin_sid", originSid).eq("username", username));
        if (null == role) {
            return Optional.empty();
        }
        uid = role.getUid();
        //?????????Redis
        GameDataTimeLimitCacheUtil.cache(cacheKey, uid, DateUtil.SECOND_ONE_WEEK);
        return Optional.ofNullable(role.getUid());
    }

    /**
     * /** ???????????????[min,max]???????????????ID
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
     * ????????????????????????ID
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
     * ??????????????????????????????????????????
     *
     * @param sid
     * @param nickname ??????
     * @return ??????????????????????????????
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
     * ??????????????????????????????????????????ID????????????null???
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
     * ???????????????????????????ID
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
     * ?????????????????????UID
     *
     * @param uid
     * @return
     */
    public boolean existsUid(Long uid) {
        InsRoleInfoEntity role = roleInfo.selectById(uid);
        return null != role;
    }

    /**
     * ???????????????????????????ID
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
     * ?????????????????????????????????????????????
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
     * ????????????????????????????????????????????????????????????
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
            // ??????????????????Redis,?????????????????????
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
                LogUtil.logDeletedUserDatas(userPushesToDel, "?????????????????????");
                gameUserService.deleteItems(uid, userPushesToDel);
            }
        }
    }

}
