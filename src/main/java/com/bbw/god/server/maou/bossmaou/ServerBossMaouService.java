package com.bbw.god.server.maou.bossmaou;

import com.bbw.common.DateUtil;
import com.bbw.common.LM;
import com.bbw.common.ListUtil;
import com.bbw.db.redis.RedisHashUtil;
import com.bbw.god.db.entity.CfgServerEntity;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.game.config.CfgBossMaou;
import com.bbw.god.game.config.server.ServerTool;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.mail.UserMail;
import com.bbw.god.server.ServerDataService;
import com.bbw.god.server.maou.bossmaou.attackinfo.*;
import com.bbw.god.server.maou.bossmaou.event.BossMaouEventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.bbw.god.game.data.redis.RedisKeyConst.SPLIT;

/**
 * 魔王Boss服务类
 *
 * @author suhq
 * @date 2019年2月12日 下午5:19:50
 */
@Slf4j
@Service
public class ServerBossMaouService extends ServerDataService {
    @Autowired
    private BossMaouAttackSummaryService attackSummaryService;
    @Autowired
    private BossMaouBloodService maouBloodService;
    @Autowired
    private BossMaouAttackDetailService attackDetailService;
    @Autowired
    private BossMaouAutoAttackService autoAttackService;
    @Autowired
    private ServerBosssMaouRoundService maouRoundService;
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private RedisHashUtil<Long, Integer> redisHashUtil;
    @Autowired
    private ServerDataService serverDataService;
    @Autowired
    private ServerBossMaouDayConfigService serverBossMaouDayConfigService;
    // 确认魔王的数据保存3天
    private static final long TIME_OUT = 3 * 24 * 60 * 60;

    /**
     * 获得当前服务器生效的魔王
     *
     * @param sid
     * @return
     */
    public Optional<ServerBossMaou> getCurBossMaou(long uid, int sid) {
        Date now = DateUtil.now();
        List<ServerBossMaou> maous = getBossMaous(uid, sid, now);
        Optional<ServerBossMaou> optional = maous.stream().filter(tmp -> tmp.ifMe(now)).findFirst();
        return optional;
    }

    /**
     * 获取下一个魔王
     *
     * @param sbm
     * @return
     */
    public Optional<ServerBossMaou> getNextBossMaou(ServerBossMaou sbm) {
        CfgBossMaou.BossMaou bossMaouConfig = BossMaouTool.getBossMaouConfig(sbm.getBaseMaouId());
        BossMaouLevel maouLevel = BossMaouLevel.fromValue(bossMaouConfig.getMaouLevel());
        Date beginTime = sbm.getBeginTime();
        //检查今日魔王
        List<ServerBossMaou> maous = getBossMaous(sbm.getSid(), beginTime);
        Optional<ServerBossMaou> optional = maous.stream().filter(tmp ->
                tmp.getBeginTime().after(beginTime) && tmp.ifMatch(maouLevel)).findFirst();
        if (optional.isPresent()) {
            return optional;
        }
        //第二天魔王
        Date nextDay = DateUtil.addDays(beginTime, 1);
        this.serverBossMaouDayConfigService.check(ServerTool.getServer(sbm.getSid()), nextDay);

        return getBossMaous(sbm.getSid(), nextDay).stream().filter(tmp -> tmp.ifMatch(maouLevel))
                .min(Comparator.comparing(ServerBossMaou::getBaseMaouId));
    }

    /**
     * 获得某个玩家某一天的魔王boss
     *
     * @param uid
     * @param sid
     * @param date
     * @return
     */
    public List<ServerBossMaou> getBossMaous(long uid, int sid, Date date) {
        List<ServerBossMaou> maous = getBossMaous(sid, date);
        Integer maouType = getConfirmMaouType(uid);
        if (null == maouType) {
            return new ArrayList<>();
        }
        return maous.stream().filter(tmp -> tmp.gainBossMaouLevel() == maouType).collect(Collectors.toList());
    }

    /**
     * 获得某个区服某一天的魔王
     *
     * @param sid
     * @param date
     * @return
     */
    public List<ServerBossMaou> getBossMaous(int sid, Date date) {
        String loopKey = ServerBossMaou.getLoopKey(date);
        List<ServerBossMaou> maous = this.serverDataService.getServerDatas(sid, ServerBossMaou.class, loopKey);
        return maous;
    }

    /**
     * 获取玩家打魔王的排名
     *
     * @param uid
     * @param maouRankers
     * @return
     */
    public int getMaouRank(long uid, List<BossMaouAttackSummary> maouRankers) {

        if (maouRankers == null || maouRankers.size() == 0) {
            return 0;
        }
        for (int i = 0; i < maouRankers.size(); i++) {
            BossMaouAttackSummary maouRanker = maouRankers.get(i);
            if (maouRanker.getGuId() == uid) {
                return i + 1;
            }
        }
        return 0;
    }

    /**
     * 获得回合免费次数
     *
     * @param attackInfo
     * @param roundDetail
     * @return
     */
    public int getRoundFreeTimes(BossMaouAttackSummary attackInfo, BossMaouRoundDetail roundDetail) {
        if (roundDetail == null) {
            return 0;
        }

        return attackInfo.getLastAttackRound() < roundDetail.getRound() ? 1 : 0;
    }

    public Integer getLostBlood(ServerBossMaou maou) {
        return this.maouBloodService.getLostBlood(maou);
    }

    /**
     * 本次攻击后，魔王总的失血量
     *
     * @param maou
     * @param beatedBlood
     * @return
     */
    public Integer getLostBloodAfterAttack(ServerBossMaou maou, int beatedBlood) {
        return this.maouBloodService.incBlood(maou, beatedBlood);
    }

    /**
     * 获取攻打第一名但玩家昵称
     *
     * @param maou
     * @return
     */
    public Optional<String> getNO1AttackerName(ServerBossMaou maou) {
        List<BossMaouAttackSummary> list = this.attackSummaryService.getAttackInfoSorted(maou);
        if (list.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(list.get(0).getNickname());
    }

    /**
     * 发送某天，某个区服的魔王奖励。已经发送过的会被忽略。
     *
     * @param date
     * @param server
     */
    public void sendMaouAwards(Date date, CfgServerEntity server) {
        List<ServerBossMaou> sbms = getBossMaous(server.getMergeSid(), date);
        sbms = sbms.stream().filter(tmp -> tmp.getBeginTime().before(date)).collect(Collectors.toList());
        for (ServerBossMaou sbm : sbms) {
            BossMaouEventPublisher.pubAwardSendEvent(sbm);
        }
    }

    /**
     * 发放魔王奖励
     */
    public void sendMaouAwards(ServerBossMaou maou) {
        if (maou.hasSendedAward()) {
            log.info("魔王奖励已发放！");
            return;
        }
        // 更新状态
        maou.setSendAwardTime(DateUtil.now());
        this.updateServerData(maou);
        List<BossMaouAttackSummary> maouRankers = this.attackSummaryService.getAttackInfoSorted(maou);
        if (ListUtil.isEmpty(maouRankers)) {
            log.info("没有人打魔王，不需要发送奖励！");
            return;
        }
        CfgBossMaou.BossMaou bossMaouConfig = BossMaouTool.getBossMaouConfig(maou.getBaseMaouId());
        String bossName = BossMaouLevel.fromValue(bossMaouConfig.getMaouLevel()).getName();
        int rank = 1;
        log.info("开始发放魔王奖励，需要发放的玩家数：" + maouRankers.size());
        List<UserMail> mailList = new ArrayList<>(maouRankers.size());
        for (BossMaouAttackSummary maouRanker : maouRankers) {
            if (maouRanker.getAwardMailId() > 0) {//已经发送过了
                continue;
            }
            // 攻打魔王奖励邮件内容
            String title = LM.I.getMsgByUid(maouRanker.getGuId(),"mail.server.maou.attack.award.title" ,maou.getDateInt() ,bossName);
            String contentTmp = LM.I.getMsgByUid(maouRanker.getGuId(),"mail.server.maou.attack.award.contentTmp" ,DateUtil.toDateTimeString(maou.getAttackTime()) ,bossName);
            String content = LM.I.getMsgByUid(maouRanker.getGuId(),"mail.maou.attack.award.content" ,contentTmp ,maouRanker.getBeatedBlood() ,rank);
            List<Award> awards = getRankAward(BossMaouLevel.fromValue(bossMaouConfig.getMaouLevel()), rank, maou.getCardAward());
            UserMail mail = UserMail.newAwardMail(title, content, maouRanker.getGuId(), awards);
            mailList.add(mail);
            maouRanker.setAwardMailId(mail.getId());
            this.attackSummaryService.setMyAttackInfo(maouRanker.getGuId(), maou, maouRanker);
            rank++;
        }
        this.gameUserService.addItems(mailList);
        log.info("发放完魔王奖励，发放的邮件数：" + mailList.size());

    }

    /**
     * 根据上次魔王攻打情况初始化本轮血量,以及回合信息
     *
     * @param maou
     */
    public void initNextMaou(ServerBossMaou maou) {
        long attackContinueTime = maou.getSendAwardTime().getTime() - maou.getAttackTime().getTime();
        final int attackContinueSeconds = (int) attackContinueTime / 1000;
        final int lostBloodRate = 100 - (int) (maou.getRemainBlood() * 1.0 / maou.getTotalBlood() * 100);
        int incBlood = BossMaouTool.getIncBlood(attackContinueSeconds, lostBloodRate);
        int totalBlood = maou.getTotalBlood() + incBlood;
        int minBlood = BossMaouTool.getConfig().getMinBlood();
        int maxBlood = BossMaouTool.getConfig().getMaxBlood();
        // 不能小于最低值
        totalBlood = Math.max(totalBlood, minBlood);
        // 不能高于最大值
        totalBlood = Math.min(totalBlood, maxBlood);
        ServerBossMaou nextMaou = getNextBossMaou(maou).get();
        nextMaou.setTotalBlood(totalBlood);
        nextMaou.setRemainBlood(totalBlood);
        this.serverDataService.updateServerData(nextMaou);
    }

    public void timeOutTmpData(ServerBossMaou maou) {
        try {
            this.attackSummaryService.expireData(maou);
            this.attackDetailService.expireData(maou);
            this.maouBloodService.expireData(maou);
            this.autoAttackService.expireData(maou);
            this.maouRoundService.expireData(maou);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 魔王排名奖励
     *
     * @param maouLevel
     * @param rank
     * @param cardAward
     * @return
     */
    private List<Award> getRankAward(BossMaouLevel maouLevel, int rank, int cardAward) {
        CfgBossMaou config = BossMaouTool.getConfig();
        CfgBossMaou.RankerAward rankerAward = config.getRankerAwards().stream().filter(tmp -> tmp.ifMatch(maouLevel,
                rank)).findFirst().get();
        List<Award> awards = rankerAward.getAwards();
        if (rank == 1) {
            awards.add(0, new Award(cardAward, AwardEnum.KP, 1));
        }
        return awards;
    }

    /**
     * 确认魔王存在redis中的key
     *
     * @param maouBeginTime
     * @return
     */
    private String getConfirmMaouTypeKey(Date maouBeginTime) {
        long dateTime = DateUtil.toDateTimeLong(maouBeginTime);
        return "game" + SPLIT + "maouBoss" + SPLIT + dateTime + SPLIT + "confirm";
    }

    public String getConfirmMaouTypeKey() {
        Date now = DateUtil.now();
        int hour = DateUtil.getHourOfDay(now);
        if (12 == hour) {
            return getConfirmMaouTypeKey(DateUtil.toDate(now, 12, 0, 0));
        }
        return getConfirmMaouTypeKey(DateUtil.toDate(now, 19, 0, 0));
    }

    /**
     * 返回玩家确认后的魔王类型，未确认返回null
     *
     * @param uid 玩家id
     * @return
     */
    public Integer getConfirmMaouType(long uid) {
        String key = getConfirmMaouTypeKey();
        return redisHashUtil.getField(key, uid);
    }

    public boolean isMaouBossTime() {
        CfgBossMaou config = BossMaouTool.getConfig();
        final int hmsInt = DateUtil.toHMSInt(DateUtil.now());
        List<CfgBossMaou.BossMaou> maous = config.getMaous();
        return maous.stream().anyMatch(maou -> maou.getBeginTime() <= hmsInt && hmsInt <= maou.getEndTime());
//        int hmsInt = DateUtil.toHMSInt(DateUtil.now());
//        if (hmsInt >= 113000 && hmsInt <= 123000) {
//            return true;
//        }
//        return hmsInt >= 183000 && hmsInt <= 193000;
    }

    /**
     * 确认魔王
     *
     * @param uid      玩家id
     * @param maouType 魔王类型
     */
    public void confirmMaouType(long uid, int maouType) {
        String key = getConfirmMaouTypeKey();
        redisHashUtil.putField(key, uid, maouType, TIME_OUT);
    }

    /**
     * 将编组中的卡牌id替换
     *
     * @param uid       玩家id
     * @param oldCardId 旧卡id
     * @param newCardId 新卡id
     */
    public void replaceCard(long uid, int oldCardId, int newCardId) {
        UserBossMaouData ubm = gameUserService.getSingleItem(uid, UserBossMaouData.class);
        if (null == ubm) {
            return;
        }
        HashMap<String, List<Integer>> deckCards = ubm.getDeckCards();
        Set<String> keySet = deckCards.keySet();
        for (String key : keySet) {
            List<Integer> list = deckCards.get(key);
            if (list.contains(oldCardId)) {
                list.remove((Integer) oldCardId);
                list.add(newCardId);
            }
        }
        gameUserService.updateItem(ubm);
    }

    /**
     * 将编组中的多张卡牌id替换
     *
     * @param uid        玩家id
     * @param oldCardIds 旧卡id集合
     * @param newCardIds 新卡id集合
     */
    public void replaceCards(long uid, List<Integer> oldCardIds, List<Integer> newCardIds) {
        UserBossMaouData ubm = gameUserService.getSingleItem(uid, UserBossMaouData.class);
        if (null == ubm) {
            return;
        }
        HashMap<String, List<Integer>> deckCards = ubm.getDeckCards();
        Set<String> keySet = deckCards.keySet();
        for (String key : keySet) {
            List<Integer> list = deckCards.get(key);
            for (Integer oldCardId : oldCardIds) {
                if (list.contains(oldCardId)) {
                    list.remove(oldCardId);
                    list.add(newCardIds.get(oldCardIds.indexOf(oldCardId)));
                }
            }
        }
        gameUserService.updateItem(ubm);
    }
}
