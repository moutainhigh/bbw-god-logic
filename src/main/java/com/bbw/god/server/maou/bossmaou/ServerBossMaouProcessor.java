package com.bbw.god.server.maou.bossmaou;

import com.bbw.common.DateUtil;
import com.bbw.common.ListUtil;
import com.bbw.common.PowerRandom;
import com.bbw.common.Rst;
import com.bbw.common.lock.RedisLockUtil;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.game.config.CfgBossMaou;
import com.bbw.god.game.config.TypeEnum;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.card.UserCard;
import com.bbw.god.gameuser.card.UserCardService;
import com.bbw.god.gameuser.res.ResChecker;
import com.bbw.god.gameuser.res.ResEventPublisher;
import com.bbw.god.rd.RDSuccess;
import com.bbw.god.server.maou.IServerMaouProcessor;
import com.bbw.god.server.maou.ServerMaouKind;
import com.bbw.god.server.maou.ServerMaouStatus;
import com.bbw.god.server.maou.ServerMaouStatusInfo;
import com.bbw.god.server.maou.alonemaou.ServerAloneMaou;
import com.bbw.god.server.maou.alonemaou.ServerAloneMaouService;
import com.bbw.god.server.maou.attack.MaouAttackService;
import com.bbw.god.server.maou.attack.MaouAttackType;
import com.bbw.god.server.maou.bossmaou.attackinfo.*;
import com.bbw.god.server.maou.bossmaou.event.BossMaouEventPublisher;
import com.bbw.god.server.maou.bossmaou.rd.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.stream.Collectors;

import static com.bbw.god.game.data.redis.RedisKeyConst.SPLIT;

/**
 * @author suhq
 * @description: 魔王boss处理逻辑
 * @date 2019-12-23 10:34
 **/
@Service
public class ServerBossMaouProcessor implements IServerMaouProcessor<ServerBossMaou> {
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private ServerBossMaouService bossMaouService;
    @Autowired
    private BossMaouAttackSummaryService attackSummaryService;
    @Autowired
    private BossMaouAttackDetailService attackDetailService;
    @Autowired
    private ServerBosssMaouRoundService maouRoundService;
    @Autowired
    private ServerAloneMaouService aloneMaouService;
    @Autowired
    private MaouAttackService maouAttackService;
    @Autowired
    private UserCardService userCardService;
    @Autowired
    private RedisLockUtil redisLockUtil;

    @Override
    public RDBossMaouInfoList getMaou(long uid, int sid) {
        Integer maouType = bossMaouService.getConfirmMaouType(uid);
        List<RDBossMaouInfo> bossMaouInfoList = new ArrayList<>();
        Boolean isLimitDay = DateUtil.getDayOfMonth(DateUtil.now()) % 10 == 5;
        // 获取配置对象
        CfgBossMaou config = BossMaouTool.getConfig();
        // 封装魔王数据
        Date now = DateUtil.now();
        int hmsInt = DateUtil.toHMSInt(now);
        List<ServerBossMaou> sbmList = bossMaouService.getBossMaous(sid, now);
        Long remainTime = null;
        Integer nextOpenTime = null;
        // 筛选出对应魔王数据
        if ((hmsInt >= 113000 && hmsInt <= 123000) || (hmsInt >= 183000 && hmsInt <= 193000)) {
            sbmList = sbmList.stream().filter(tmp -> tmp.ifMe(now)).collect(Collectors.toList());
            Date endTime = sbmList.get(0).getEndTime();
            remainTime = endTime.getTime() - DateUtil.now().getTime();
        } else {
            int nextBeginTime = hmsInt >= 113000 && hmsInt <= 183000 ? 183000 : 113000;
            nextOpenTime = nextBeginTime / 100;
            sbmList = sbmList.stream().filter(tmp ->
                    DateUtil.toHMSInt(tmp.getBeginTime()) == nextBeginTime).collect(Collectors.toList());
        }

        for (ServerBossMaou serverBossMaou : sbmList) {
            addBossMaouInfo(maouType, config, serverBossMaou, bossMaouInfoList);
        }
        UserBossMaouData ubm = gameUserService.getSingleItem(uid, UserBossMaouData.class);
        if (null == ubm) {
            ubm = UserBossMaouData.getInstance(uid);
            gameUserService.addItem(uid, ubm);
        }
        int aloneMaouLevel = this.aloneMaouService.getAttackedLevel(uid, sid, DateUtil.now()) - 1;
        return RDBossMaouInfoList.getInstance(ubm, aloneMaouLevel, bossMaouInfoList, isLimitDay, remainTime,
                nextOpenTime);
    }

    /**
     * 封装数据
     *
     * @param config           魔王配置对象
     * @param sbm              区服魔王
     * @param bossMaouInfoList 魔王信息集合
     */
    private void addBossMaouInfo(Integer selectedMaouType, CfgBossMaou config, ServerBossMaou sbm,
                                 List<RDBossMaouInfo> bossMaouInfoList) {
        ServerMaouStatusInfo maouStatus = getMaouStatus(sbm);
        int maouLevel = sbm.getBaseMaouId() / 10 * 10;
        CfgBossMaou.RankerAward rankerAward = config.getRankerAwards().stream().filter(tmp ->
                tmp.getMinRank() == 1 && tmp.getMaouLevel().equals(maouLevel)).findFirst().orElse(null);
        List<Award> awards = rankerAward.getAwards();
        List<Award> awardList = new ArrayList<>(awards);
        awardList.add(new Award(sbm.getCardAward(), AwardEnum.KP, 1));
        Boolean isMySelected = selectedMaouType != null && selectedMaouType == sbm.gainBossMaouLevel();
        BossMaouRoundDetail curRoundDetail = maouRoundService.getCurRoundDetail(sbm);
        bossMaouInfoList.add(RDBossMaouInfo.getInstance(isMySelected, sbm, curRoundDetail, maouStatus, awardList));
    }

    /**
     * 确认魔王类型
     *
     * @param uid      玩家id
     * @param maouType 魔王类型
     * @return
     */
    public Rst confirmMaouType(long uid, int maouType) {
        if (null != bossMaouService.getConfirmMaouType(uid)) {
            return Rst.businessFAIL("您已经选择过了！");
        }
        if (BossMaouLevel.ShenYMZ.getValue() == maouType) {
            int sid = gameUserService.getActiveSid(uid);
            int aloneMaouLevel = this.aloneMaouService.getAttackedLevel(uid, sid, DateUtil.now());
            if (aloneMaouLevel < 3) {
                return Rst.businessFAIL("独占魔王通关层数不足，无法选择深渊魔尊！");
            }
        }
        bossMaouService.confirmMaouType(uid, maouType);
        return Rst.businessOK();
    }

    @Override
    public RDBossMaouAttackingInfo getAttackingInfo(long uid, int sid) {
        if (null == bossMaouService.getConfirmMaouType(uid)) {
            throw new ExceptionForClientTip("maouboss.not.confirm");
        }

        long now = System.currentTimeMillis();
        BossMaouParam maouParam = getBossMaouParam(uid, sid);
        if (maouParam.getServerBossMaou() == null) {
            return RDBossMaouAttackingInfo.getInstanceAsMaouOver();
        }
        ServerBossMaou maou = maouParam.getServerBossMaou();
        BossMaouRoundDetail roundDetail = maouParam.getRoundDetail();
        if (roundDetail == null) {
            throw new ExceptionForClientTip("maouboss.attack.is.over");
        }

        RDBossMaouAttackingInfo rd = new RDBossMaouAttackingInfo();
        rd.setRemainBlood(maou.getRemainBlood());
        if (maou.getRemainBlood() == 0) {
            rd.setMaouStatus(ServerMaouStatus.KILLED.getValue());
        }
        rd.setTotalBlood(maou.getTotalBlood());
        long maouEndTime = maou.getEndTime().getTime() - now;
        rd.setMaouRemainTime(maouEndTime > 0 ? maouEndTime : 0L);
        rd.setMaouType(roundDetail.getType());
        rd.setRoundRemainTime(roundDetail.getRoundEnd().getTime() - now);

        List<BossMaouAttackSummary> rankers = this.attackSummaryService.getAttackInfoSorted(maou);
        BossMaouAttackSummary myAttackInfo = null;
        Optional<BossMaouAttackSummary> optional = rankers.stream().filter(tmp -> tmp.getGuId() == uid).findFirst();
        if (optional.isPresent()) {
            myAttackInfo = optional.get();
        } else {
            GameUser user = this.gameUserService.getGameUser(uid);
            myAttackInfo = this.attackSummaryService.getMyAttackInfo(user, maou);
        }
        rd.setMyBeatedTotalBlood(myAttackInfo.getBeatedBlood());
        rd.setMyFreeTimesCurRound(this.bossMaouService.getRoundFreeTimes(myAttackInfo, roundDetail));
        rd.setMyRank(this.bossMaouService.getMaouRank(uid, rankers));

        rd.setRankers(RDBossMaouRanker.getInstances(rankers));
        return rd;
    }

    @Override
    public RDSuccess setMaouCards(long uid, String maouCards) {
        if (maouCards.contains("undefined,")) {
            maouCards = maouCards.replace("undefined,", "");
        } else if (maouCards.contains(",undefined")) {
            maouCards = maouCards.replace(",undefined", "");
        }
        String[] typeCards = maouCards.split(";");
        CfgBossMaou config = BossMaouTool.getConfig();
        //检查编组数量
        int cardLimit = config.getCardLimitPerType();
        HashMap<String, List<Integer>> deckCards = new HashMap<>();
        for (int i = 0; i < typeCards.length; i++) {
            int type = i * 10;
            List<Integer> cardIds = ListUtil.parseStrToInts(typeCards[i]);
            if (cardIds.size() > cardLimit) {
                throw new ExceptionForClientTip("maouboss.card.outOfLimit", cardLimit);
            }
            // 判断卡牌的有效性
            List<UserCard> cards = userCardService.getUserCards(uid, cardIds);
            if (cards.size() != cardIds.size()) {
                throw new ExceptionForClientTip("maouboss.card.unvalid");
            }
            // 魔尊要判断各属性卡组的有效性
            if (TypeEnum.Null.getValue() != type) {
                boolean isTypeUnvalid = cards.stream().anyMatch(tmp -> tmp.gainCard().getType() != type);
                if (isTypeUnvalid) {
                    throw new ExceptionForClientTip("maouboss.card.unvalid");
                }
            }
            deckCards.put(type + "", cardIds);
        }
        //更新数据
        UserBossMaouData ubmd = this.gameUserService.getSingleItem(uid, UserBossMaouData.class);
        ubmd.setDeckCards(deckCards);
        this.gameUserService.updateItem(ubmd);
        return new RDSuccess();
    }

    /**
     * 获得魔王到来的状态
     *
     * @return
     */
    @Override
    @NotNull
    public ServerMaouStatusInfo getMaouStatus(ServerBossMaou maou) {
        if (maou == null) {
            return new ServerMaouStatusInfo(ServerMaouStatus.OVER.getValue());
        }
        Date now = DateUtil.now();
        // 集结期
        if (now.after(maou.getBeginTime()) && now.before(maou.getAttackTime())) {
            return new ServerMaouStatusInfo(ServerMaouStatus.ASSEMBLY.getValue(),
                    maou.getAttackTime().getTime() - now.getTime());
        }
        // 攻打期间
        if (now.after(maou.getAttackTime()) && now.before(maou.getEndTime())) {
            // 判定状态，已被击杀 或者 攻打中
            ServerMaouStatus status = maou.isKilled() ? ServerMaouStatus.KILLED : ServerMaouStatus.ATTACKING;
            Long maouLeaveRemainTime = maou.getEndTime().getTime() - now.getTime();
            return new ServerMaouStatusInfo(status.getValue(), maouLeaveRemainTime.intValue());
        }
        return new ServerMaouStatusInfo(ServerMaouStatus.OVER.getValue());
    }

    /**
     * 获得魔王boss奖励
     *
     * @return
     */
    public RDMaouRankerAwards getRankerAwards() {
        CfgBossMaou config = BossMaouTool.getConfig();
        RDMaouRankerAwards rd = RDMaouRankerAwards.getInstance(config.getRankerAwards());
        return rd;
    }

    /**
     * 获得排行
     *
     * @param uid
     * @param sid
     * @return
     */
    public RDMaouRankers getRankers(long uid, int sid) {
        Date now = DateUtil.now();
        Optional<ServerBossMaou> bossMaouOptional = this.bossMaouService.getCurBossMaou(uid, sid);
        //独战魔王阶段
        if (!bossMaouOptional.isPresent()) {
            Date rankDate = now;

            Optional<ServerAloneMaou> aloneMaouOptional = this.aloneMaouService.getCurAloneMaou(sid);
            if (aloneMaouOptional.isPresent()) {
                rankDate = aloneMaouOptional.get().getBeginTime();
            }
            //独战魔王日的魔王
            List<ServerBossMaou> bossMaous = this.bossMaouService.getBossMaous(uid, sid, rankDate);
            bossMaous = bossMaous.stream().filter(tmp -> tmp.getBeginTime().before(now)).collect(Collectors.toList());
            //取最近的魔王
            if (ListUtil.isNotEmpty(bossMaous)) {
                bossMaouOptional = Optional.of(bossMaous.get(bossMaous.size() - 1));
            }
        }

        if (!bossMaouOptional.isPresent()) {
            return RDMaouRankers.getInstance(new ArrayList<>());
        }
        List<BossMaouAttackSummary> rankers = this.attackSummaryService.getAttackInfoSorted(bossMaouOptional.get());
        CfgBossMaou config = BossMaouTool.getConfig();
        int index = rankers.size() >= config.getRankerNumToShow() ? config.getRankerNumToShow() : rankers.size();
        List<BossMaouAttackSummary> rankersToShow = rankers.subList(0, index);
        RDMaouRankers rd = RDMaouRankers.getInstance(rankersToShow);
        BossMaouAttackSummary attackSummary = null;
        for (int i = 0; i < rankers.size(); i++) {
            attackSummary = rankers.get(i);
            if (attackSummary.getGuId() == uid) {
                rd.setMyBeatedBlood(attackSummary.getBeatedBlood());
                rd.setMyBeatedTimes(attackSummary.getAttackTimes());
                rd.setMyRanking(i + 1);
                break;
            }
        }

        return rd;
    }

    @Override
    public RDBossMaouAttackingInfo attack(long uid, int sid, int attackTypeInt) {
        if (null == bossMaouService.getConfirmMaouType(uid)) {
            throw new ExceptionForClientTip("maouboss.not.confirm");
        }

        BossMaouParam bossMaouParam = getBossMaouParam(uid, sid);
        ServerBossMaou bossMaou = bossMaouParam.getServerBossMaou();
        //魔王是否已结束
        if (bossMaou == null) {
            return RDBossMaouAttackingInfo.getInstanceAsMaouOver();
        }

        BeforeBossAttackResult beforeBossAttackResult = beforeAttack(uid, attackTypeInt, bossMaouParam);
        BossMaouRoundDetail bossMaouRoundDetail = bossMaouParam.getRoundDetail();
        int cardType = getCardTypeByMaouType(bossMaouRoundDetail.getType());
        List<Integer> attackCardIds = bossMaouParam.getUserMaouData().getAttackingCard(cardType);
        List<UserCard> attackCards = userCardService.getUserCards(uid, attackCardIds);
        // 打掉魔王血量
        int beatedBlood = this.maouAttackService.getBeatedBlood(attackCards, bossMaouRoundDetail.getType());
        beatedBlood *= beforeBossAttackResult.getAttackTimes();
        // 并发情况下，代码从检查状态完毕执行到这里，魔王状态可能已经被改变，需要在同步锁内再判定一下
        // ----同步锁开始----
        String key = getMaouAttackLockKey(sid, bossMaou.getBaseMaouId());
        int finalBeatedBlood = beatedBlood;
        beatedBlood = (int) redisLockUtil.doSafe(key, obj -> {
            long begin = System.currentTimeMillis();
            // 获取魔王总的失血量
            Integer lostBlood = this.bossMaouService.getLostBlood(bossMaou);
            // 失血量 > 总血量,失血过多，死了。
            if (lostBlood >= bossMaou.getTotalBlood()) {
                throw new ExceptionForClientTip("maou.already.die");
            }
            int remainBlood = bossMaou.getTotalBlood() - lostBlood;
            int blood = Math.min(finalBeatedBlood, remainBlood);
            // 本次攻击后魔王总的失血量
            lostBlood = this.bossMaouService.getLostBloodAfterAttack(bossMaou, blood);
            // 魔王失血
            bossMaou.lostBlood(uid, lostBlood);
            // 更新魔王状态
            this.bossMaouService.updateServerData(bossMaou);
//            System.out.println(System.currentTimeMillis() + "-----------time：" + (System.currentTimeMillis() - begin));
            return blood;
        });

        // ----同步锁结束----
        // 保存攻击明细
        BossMaouAttackDetail attackDetail = BossMaouAttackDetail.instance(uid, bossMaou.getId(),
                bossMaouRoundDetail.getRound(), beatedBlood, beforeBossAttackResult.getAttackTimes(), attackTypeInt);
        this.attackDetailService.saveAttackDetail(bossMaou, attackDetail);
        // 更新排行记录
        BossMaouAttackSummary myAttack = beforeBossAttackResult.getMyAttackInfo();
        myAttack.setLevel(beforeBossAttackResult.getGu().getLevel());
        myAttack.setLastAttackRound(bossMaouRoundDetail.getRound());
        myAttack.incBeatedBlood(beatedBlood);
        myAttack.incAttackTimes(beforeBossAttackResult.getAttackTimes());
        myAttack.setLastAttackTime(System.currentTimeMillis());
        // 保存攻打信息
        this.attackSummaryService.setMyAttackInfo(uid, bossMaou, myAttack);

        RDBossMaouAttackingInfo rd = getAttackingInfo(uid, sid);
        rd.setBeatedBlood(beatedBlood);
        //发布魔王击杀事件
        if (bossMaou.isKilled()) {
            BossMaouEventPublisher.pubKilledEvent(bossMaou, rd);
        }
        //发放攻击奖励
        // 增加经验
        int addedExp = beatedBlood / 20;
        ResEventPublisher.pubExpAddEvent(uid, addedExp, WayEnum.MAOU_BOSS_FIGHT, rd);
        // 获得铜钱
        int addedCopper = addedExp * PowerRandom.getRandomBetween(90, 110) / 100;
        ResEventPublisher.pubCopperAddEvent(uid, addedCopper, WayEnum.MAOU_BOSS_FIGHT, rd);
        // 消耗元宝
        int needGold = beforeBossAttackResult.getNeedGold();
        if (needGold > 0) {
            ResEventPublisher.pubGoldDeductEvent(uid, needGold, WayEnum.MAOU_BOSS_FIGHT, rd);
        }
        BossMaouEventPublisher.pubAttackMaouEvent(beatedBlood, uid);
        return rd;
    }

    /**
     * 获取魔王攻击锁的key
     *
     * @param sid    区服id
     * @param maouId 魔王id
     * @return
     */
    private String getMaouAttackLockKey(int sid, int maouId) {
        return "game" + SPLIT + "maouBoss" + SPLIT + "lock" + SPLIT + "sid" + SPLIT + sid + "maouId" + SPLIT + maouId;
    }

    private BeforeBossAttackResult beforeAttack(long uid, int attackTypeInt, BossMaouParam bossMaouParam) {
        ServerBossMaou bossMaou = bossMaouParam.getServerBossMaou();
        //魔王是否已被击杀
        if (bossMaou.isKilled()) {
            throw new ExceptionForClientTip("maou.already.die");
        }
        BossMaouRoundDetail roundDetail = bossMaouParam.getRoundDetail();
        if (roundDetail == null) {
            throw new ExceptionForClientTip("maouboss.attack.is.over");
        }
        int cardType = getCardTypeByMaouType(roundDetail.getType());
        List<Integer> attackCardIds = bossMaouParam.getUserMaouData().getAttackingCard(cardType);
        //是否有对应的属性卡牌编组
        if (ListUtil.isEmpty(attackCardIds)) {
            throw new ExceptionForClientTip("maouboss.card.not.group");
        }

        MaouAttackType attackType = MaouAttackType.fromValue(attackTypeInt);
        int needGold = getNeedGold(attackType);//使用元宝数
        int attackTimes = getAttackTimes(attackType);//攻击结算次数
        GameUser gu = this.gameUserService.getGameUser(uid);
        ResChecker.checkGold(gu, needGold);
        //我的攻击情况
        BossMaouAttackSummary myAttackInfo = this.attackSummaryService.getMyAttackInfo(gu, bossMaou);
        Long now = System.currentTimeMillis();
        //不使用元宝，并且未到可以出手到时间，允许误差100毫秒
        if (needGold == 0 && myAttackInfo.getLastAttackTime() > roundDetail.getRoundBegin().getTime()) {
            throw new ExceptionForClientTip("maou.need.wait");
        }
        //使用元宝，但是在3秒内出手，也是非法的
//        if (needGold > 0 && (now - myAttackInfo.getLastAttackTime()) < 3 * 1000) {
//            throw new ExceptionForClientTip("maou.unvalid.attack");
//        }

        return new BeforeBossAttackResult(gu, myAttackInfo, needGold, attackTimes);
    }


    private BossMaouParam getBossMaouParam(long uid, int sid) {
        //获取魔王Boss配置信息
        CfgBossMaou config = BossMaouTool.getConfig();


        //获取本期魔王
        Optional<ServerBossMaou> samOptional = this.bossMaouService.getCurBossMaou(uid, sid);
        if (!samOptional.isPresent()) {
            return new BossMaouParam();
        }
        ServerBossMaou sbm = samOptional.get();
        //获取玩家魔王boss信息
        UserBossMaouData ubmd = this.gameUserService.getSingleItem(uid, UserBossMaouData.class);
        BossMaouRoundDetail roundDetail = this.maouRoundService.getCurRoundDetail(sbm);
        return new BossMaouParam(ubmd, sbm, roundDetail, config);
    }

    /**
     * 打魔王需要的元宝
     *
     * @param attackType
     * @return
     */
    private int getNeedGold(MaouAttackType attackType) {
        CfgBossMaou config = BossMaouTool.getConfig();
        if (attackType == MaouAttackType.ONE_ATTACKS_WITH_GOLD) {
            return config.getGoldToAttack();
        }
        if (attackType == MaouAttackType.DOUBLE_ATTACKS_WITH_GOLD) {
            return config.getGoldToDoubleAttack();
        }
        return 0;
    }

    /**
     * 获得攻打次数
     *
     * @param attackType
     * @return
     */
    private int getAttackTimes(MaouAttackType attackType) {
        if (attackType == MaouAttackType.DOUBLE_ATTACKS_WITH_GOLD) {
            return BossMaouTool.getConfig().getDoubleTimes();
        }
        return 1;
    }

    private int getCardTypeByMaouType(int maouType) {
        TypeEnum typeEnum = TypeEnum.fromValue(maouType);
        switch (typeEnum) {
            case Gold:
                return TypeEnum.Fire.getValue();
            case Wood:
                return TypeEnum.Gold.getValue();
            case Water:
                return TypeEnum.Earth.getValue();
            case Fire:
                return TypeEnum.Water.getValue();
            case Earth:
                return TypeEnum.Wood.getValue();
            default:
                return maouType;
        }
    }

    @Override
    public boolean isMatch(int sid) {
        return true;
    }

    @Override
    public boolean isMatchByMaouKind(int maouKind) {
        return ServerMaouKind.BOSS_MAOU.getValue() == maouKind;
    }

    @Data
    @AllArgsConstructor
    public static class BossMaouParam {
        private UserBossMaouData userMaouData;
        private ServerBossMaou serverBossMaou;
        private BossMaouRoundDetail roundDetail;
        private CfgBossMaou config;

        BossMaouParam() {
        }
    }

    @Data
    @AllArgsConstructor
    public static class BeforeBossAttackResult {
        private GameUser gu;
        private BossMaouAttackSummary myAttackInfo;
        private int needGold;
        private int attackTimes;
    }
}
