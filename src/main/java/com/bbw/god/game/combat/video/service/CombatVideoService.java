package com.bbw.god.game.combat.video.service;

import com.bbw.common.*;
import com.bbw.db.redis.RedisHashUtil;
import com.bbw.db.redis.RedisValueUtil;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.db.entity.InsGameCityWarEntity;
import com.bbw.god.db.service.InsGameCityWarService;
import com.bbw.god.fight.FightTypeEnum;
import com.bbw.god.game.combat.CombatRedisService;
import com.bbw.god.game.combat.data.*;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.pvp.RDPVPcombat;
import com.bbw.god.game.combat.video.CombatVideo;
import com.bbw.god.game.combat.video.CombatVideoAsyncHandler;
import com.bbw.god.game.combat.video.RDVideo;
import com.bbw.god.game.combat.video.UserCombatVideo;
import com.bbw.god.game.config.card.CardTool;
import com.bbw.god.game.config.city.CityTool;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.card.RDShareCardGroup;
import com.bbw.god.gameuser.card.ShareWayEnum;
import com.bbw.god.gameuser.card.UserCardGroupShareService;
import com.bbw.god.gameuser.leadercard.LeaderCardTool;
import com.bbw.god.gameuser.res.ResChecker;
import com.bbw.god.server.guild.service.GuildUserService;
import com.bbw.oss.OSSService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author 作者 ：lwb
 * @version 创建时间：2019年11月25日 下午3:36:03
 * 类说明  战斗记录
 */
@Slf4j
@Service
public class CombatVideoService {
    @Autowired
    private RedisValueUtil<CombatVideo> valueRedis;
    @Autowired
    private InsGameCityWarService insGameCityWarService;
    @Autowired
    private CombatRedisService combatService;
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private GuildUserService guildService;
    private static String basekey = "game:share:video";
    @Autowired
    private RedisHashUtil<String, String> hashUtil;

    private static final Integer maxCapacity = 5;//玩家可以存储的视频数量
    @Autowired
    private UserCardGroupShareService userCardGroupShareService;
    @Autowired
    private CombatRedisService redisService;
    @Autowired
    private CombatVideoAsyncHandler combatVideoAsyncHandler;

    /**
     * 初始化录像文件
     *
     * @param combat
     * @param uid
     * @return
     */
    public void initVideo(Combat combat) {
        CombatVideo combatVideo = new CombatVideo();
        combatVideo.setId(combat.getId());
        saveCombatVideo(combatVideo);
    }

    /**
     * 添加回合数据
     *
     * @param combat
     * @param round
     */
    public void addRoundData(Combat combat, int round) {
        RDCombat res = RDCombat.fromCombat(combat);
        Optional<CombatVideo> optional = getCombatVideo(combat.getId());
        if (!optional.isPresent()) {
            return;
        }
        CombatVideo video = optional.get();
        video.addRoundData(res, round);
        saveCombatVideo(video);
    }

    /**
     * 添加法宝使用
     *
     * @param rdc
     * @param combat
     * @param playerId
     */
    public void addPvPTrRoundData(RDPVPcombat rdc, Combat combat, PlayerId playerId) {
        long uid = combat.getPlayer(PlayerId.P1).getUid();
        Optional<RDTempResult> rdtOptional = rdc.getRdtList().stream().filter(p -> p.getPlayingId() == uid).findFirst();
        if (!rdtOptional.isPresent()) {
            return;
        }
        RDTempResult rdCombat = rdtOptional.get();
        addRoundData(rdCombat, combat.getId(), playerId, rdCombat.getWid(), combat.getRound());
    }

    /**
     * 添加录像回合数据
     *
     * @param dataId
     */
    public void addRoundData(List<RDCombat> rdCombats, Long dataId, int round) {
        Optional<CombatVideo> optional = getCombatVideo(dataId);
        if (!optional.isPresent()) {
            return;
        }
        CombatVideo video = optional.get();
        for (RDCombat rdCombat : rdCombats) {
            video.addRoundData(rdCombat, round++);
        }
        saveCombatVideo(video);
    }

    /**
     * 添加录像法宝回合数据
     *
     * @param rdCombat
     * @param dataId
     */
    public void addRoundData(RDTempResult rdCombat, Long dataId, PlayerId playerId, int wid, int round) {
        Optional<CombatVideo> optional = getCombatVideo(dataId);
        if (!optional.isPresent()) {
            return;
        }
        CombatVideo video = optional.get();
        video.addRoundData(rdCombat, wid, playerId, round);
        saveCombatVideo(video);
    }

    /**
     * 添加投降数据
     *
     * @param round
     * @param vid
     */
    public void addSurrender(int round, Long vid, String name) {
        Optional<CombatVideo> optional = getCombatVideo(vid);
        if (!optional.isPresent()) {
            return;
        }
        CombatVideo video = optional.get();
        video.addSurrender(round, name);
        saveCombatVideo(video);
    }

    /**
     * 保存监控数据
     *
     * @param detailId
     * @param combat
     * @param uid
     */
    public void saveMonitor(Long detailId, Combat combat, long uid) {
        detailId = detailId == null ? ID.getNextDetailId() : detailId;
        Player player = combat.getPlayerByUid(uid);
        if (needMonitor(combat) && combat.getWinnerId() == player.getId().getValue()) {
            // 当符合监听条件 且 玩家胜利的则保存该场战斗
            combatVideoAsyncHandler.monitor(detailId, combat);
        }
        if (combat.getId() != null) {
            hasEndCombatVideo(combat.getId());
        }
    }

    /**
     * 保存4级5级攻城和进阶监控数据
     *
     * @param combat
     * @param detailId
     */
    public void save4LvCityWar(Combat combat, Long detailId) {
        long begin = System.currentTimeMillis();
        Long vId = combat.getId();
        Optional<CombatVideo> optional = getCombatVideo(vId);
        if (!optional.isPresent()) {
            return;
        }
        CombatVideo video = optional.get();
        CombatInfo combatInfo = redisService.getCombatInfo(vId);
        Integer cityId = combatInfo.getCityId();
        String city = CityTool.getCityById(cityId).getName();
        Long uid = combat.getP1().getUid();//PVE  p1就是玩家
        List<InsGameCityWarEntity> list = new ArrayList<InsGameCityWarEntity>();
        for (BattleCard card : combatInfo.getP1().getDrawCards()) {
            InsGameCityWarEntity entity = new InsGameCityWarEntity();
            entity.setUid(uid);
            entity.setCity(city);
            entity.setCityId(cityId);
            entity.setDetailId(detailId);
            if (card.getImgId() == LeaderCardTool.getLeaderCardId()) {
                entity.setCard_name("主角卡");
            } else {
                entity.setCard_name(CardTool.getCardById(card.getImgId()).getName());
            }
            entity.setCard_lv(card.getLv());
            entity.setCard_hv(card.getHv());
            entity.setCardJson(JSONUtil.toJson(card));
            list.add(entity);
        }
        // 保存玩家的攻城初始化卡牌数据到数据库
        insGameCityWarService.insertBatch(list, list.size());
        long end = System.currentTimeMillis();
        log.info("保存符合条件的城战数据耗时：{}", (end - begin));
        OSSService.uploadVideo(video, OSSService.getCityCombatMonitorOssPath(city, detailId));
    }

    /**
     * 获取redis中的录像数据
     *
     * @param vId
     * @return
     */
    public Optional<CombatVideo> getCombatVideo(Long vId) {
        if (vId == null) {
            return Optional.empty();
        }
        CombatVideo combat = valueRedis.get(getKey(vId));
        return Optional.ofNullable(combat);
    }

    /**
     * 缓存录像数据到redis
     *
     * @param video
     */
    public void saveCombatVideo(CombatVideo video) {
        valueRedis.set(getKey(video.getId()), video);
        valueRedis.expire(getKey(video.getId()), 3, TimeUnit.HOURS);
    }

    /**
     * 录像完成后 过期时间修改为15分钟
     *
     * @param vid
     */
    public void hasEndCombatVideo(Long vid) {
        valueRedis.expire(getKey(vid), 5, TimeUnit.MINUTES);
    }

    /**
     * 检查是否是 需要监控的战斗类型和级别（目前只监控4、5级城攻城和进阶）
     *
     * @param combat
     * @return
     */
    private boolean needMonitor(Combat combat) {
//		//只记录4、5级城的攻城和振兴
        boolean fightType = combat.getFightType().equals(FightTypeEnum.ATTACK) || combat.getFightType().equals(FightTypeEnum.PROMOTE);
        CombatInfo combatInfo = redisService.getCombatInfo(combat.getId());
        if (!fightType || combatInfo.getCityId() == null) {
            return false;
        }
        int level = CityTool.getCityById(combatInfo.getCityId()).getLevel();
        return level >= 4;
    }

    private String getKey(long dataId) {
        StringBuilder sb = new StringBuilder();
        sb.append("combatVideo:");
        sb.append(dataId);
        return sb.toString();
    }

    /**
     * 玩家手动保存录像
     *
     * @param combatId
     * @param uid
     */
    public Rst saveVideo(long combatId, long uid) {
        Combat combat = combatService.get(combatId);
        if (combat == null || combat.getId() == null) {
            //录像已过期
            throw new ExceptionForClientTip("combat.video.not.exit");
        }
        Optional<CombatVideo> optional = getCombatVideo(combat.getId());
        if (!optional.isPresent()) {
            //录像已过期
            throw new ExceptionForClientTip("combat.video.not.exit");
        }
        CombatVideo video = optional.get();
        UserCombatVideo userCombatVideo = gameUserService.getSingleItem(uid, UserCombatVideo.class);
        if (userCombatVideo == null) {
            userCombatVideo = new UserCombatVideo(uid);
            gameUserService.addItem(uid, userCombatVideo);
        }
        if (userCombatVideo.getVideos().size() >= maxCapacity) {
            //录像已满
            return Rst.businessFAIL(1001, "可保存的录像数量已满！");
        }
        Optional<UserCombatVideo.CombatData> op = userCombatVideo.getVideos().stream().filter(p -> p.getDataId() == combatId).findFirst();
        if (op.isPresent()) {
            //录像重复保存
            throw new ExceptionForClientTip("combat.video.exit");
        }
        String url = OSSService.uploadVideo(video, OSSService.getUserCombatOssPath(combatId));
        if (StrUtil.isBlank(url)) {
            throw new ExceptionForClientTip("combat.video.error.save");
        }
        UserCombatVideo.CombatData data = UserCombatVideo.CombatData.instance(video, combat);
        data.setUrl(url);
        userCombatVideo.getVideos().add(data);
        gameUserService.updateItem(userCombatVideo);
        return Rst.businessOK();
    }

    /**
     * 删除保存的视频
     *
     * @param uid
     * @param dataid
     */
    public void delVideo(long uid, long dataid) {
        UserCombatVideo userCombatVideo = gameUserService.getSingleItem(uid, UserCombatVideo.class);
        if (userCombatVideo != null) {
            List<UserCombatVideo.CombatData> datas = userCombatVideo.getVideos().stream().filter(p -> p.getDataId() != dataid).collect(Collectors.toList());
            userCombatVideo.setVideos(datas);
            gameUserService.updateItem(userCombatVideo);
        }
    }

    public RDVideo shareVideo(long uid, long dataid, int way) {
        UserCombatVideo userCombatVideo = gameUserService.getSingleItem(uid, UserCombatVideo.class);
        if (userCombatVideo == null) {
            throw new ExceptionForClientTip("combat.video.not.exit");
        }
        Optional<UserCombatVideo.CombatData> data = userCombatVideo.getVideos().stream().filter(p -> p.getDataId() == dataid).findFirst();
        if (!data.isPresent()) {
            throw new ExceptionForClientTip("combat.video.not.exit");
        }
        if (ShareWayEnum.fromVal(way).equals(ShareWayEnum.ALLSERVER)) {
            //跨服分享 10W铜钱  此处不需要扣钱，只需要检验铜钱是否足够即可
            int needCopper = 10 * 10000;
            ResChecker.checkCopper(gameUserService.getGameUser(uid), needCopper);
        } else if (way == ShareWayEnum.GUILD.getVal() && !guildService.hasGuild(uid)) {
            throw new ExceptionForClientTip("guild.user.not.join");
        }
        String key = getHashKey();
        Long num = hashUtil.getSize(key);
        String shareId = getFiledKey(uid, num);
        hashUtil.putField(key, shareId, JSONUtil.toJson(data.get()));
        //分享视频有效期 3天
        hashUtil.expire(key, 3, TimeUnit.DAYS);
        RDVideo rd = new RDVideo();
        rd.setShareId(shareId);
        return rd;
    }

    /**
     * map 的key
     *
     * @return
     */
    public String getHashKey() {
        return basekey + ":" + DateUtil.getTodayInt();
    }

    /**
     * 单字段的KEY
     *
     * @param uid
     * @param index
     * @return
     */
    public String getFiledKey(long uid, Long index) {
        long dateInt = DateUtil.getTodayInt();
        String uidKey = String.valueOf(uid).substring(10);
        return dateInt + uidKey + index;
    }

    /**
     * 查看玩家保存的录像
     *
     * @param uid
     * @return
     */
    public RDVideo getUseRdVideoList(long uid) {
        UserCombatVideo userCombatVideo = gameUserService.getSingleItem(uid, UserCombatVideo.class);
        RDVideo rd = new RDVideo();
        if (userCombatVideo == null) {
            rd.setVideos(new ArrayList<UserCombatVideo.CombatData>());
        } else {
            rd.setVideos(userCombatVideo.getVideos());
        }
        rd.setCapacity(maxCapacity);
        return rd;
    }

    /**
     * 收藏
     *
     * @param uid
     * @param shareId
     */
    public void collectVideo(long uid, String shareId) {
        UserCombatVideo userCombatVideo = gameUserService.getSingleItem(uid, UserCombatVideo.class);
        if (userCombatVideo == null) {
            userCombatVideo = new UserCombatVideo(uid);
            gameUserService.addItem(uid, userCombatVideo);
        }
        if (userCombatVideo.getVideos().size() >= maxCapacity) {
            //录像已满
            throw new ExceptionForClientTip("combat.video.is.full");
        }
        if (StrUtil.isBlank(shareId) || shareId.length() < 8) {
            throw new ExceptionForClientTip("combat.video.not.exit");
        }
        String date = shareId.substring(0, 8);
        String key = basekey + ":" + date;
        String jsonStr = hashUtil.getField(key, shareId);
        if (StrUtil.isBlank(jsonStr)) {
            throw new ExceptionForClientTip("combat.video.not.exit");
        }
        UserCombatVideo.CombatData data = JSONUtil.fromJson(jsonStr, UserCombatVideo.CombatData.class);
        Optional<UserCombatVideo.CombatData> op = userCombatVideo.getVideos().stream().filter(p -> p.getDataId().equals(data.getDataId())).findFirst();
        if (op.isPresent()) {
            //录像重复保存
            throw new ExceptionForClientTip("combat.video.exit");
        }
        userCombatVideo.getVideos().add(data);
        gameUserService.updateItem(userCombatVideo);
    }

    /**
     * 胜利分享
     *
     * @param uid
     * @param combatId
     * @param groupName
     * @return
     */
    public RDVideo shareVictory(long uid, long combatId, String groupName) {
        Combat combat = combatService.get(combatId);
        if (combat == null || combat.getId() == null) {
            //录像已过期
            throw new ExceptionForClientTip("combat.video.not.exit");
        }
        Optional<CombatVideo> optional = getCombatVideo(combat.getId());
        if (!optional.isPresent()) {
            //录像已过期
            throw new ExceptionForClientTip("combat.video.not.exit");
        }
        CombatVideo video = optional.get();
        String url = OSSService.uploadVideo(video, OSSService.getUserCombatOssPath(combatId));
        //分享
        RDShareCardGroup rdshare = userCardGroupShareService.shareCardGroup(uid, groupName, ShareWayEnum.WORLD.getVal());
        RDVideo rd = new RDVideo();
        rd.setShareId(rdshare.getShareId());
        rd.setUrl(url);
        return rd;
    }
}
