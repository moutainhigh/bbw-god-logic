package com.bbw.god.game.wanxianzhen.service.race;

import com.bbw.common.DateUtil;
import com.bbw.common.JSONUtil;
import com.bbw.common.ListUtil;
import com.bbw.god.db.entity.WanXianFightDetailEntity;
import com.bbw.god.db.entity.WanXianRankEntity;
import com.bbw.god.game.combat.data.Combat;
import com.bbw.god.game.combat.data.Player;
import com.bbw.god.game.combat.pve.PVELogic;
import com.bbw.god.game.combat.video.CombatVideo;
import com.bbw.god.game.combat.video.CombatVideoSaveAsyncHandler;
import com.bbw.god.game.config.server.ServerTool;
import com.bbw.god.game.wanxianzhen.RDWanXian;
import com.bbw.god.game.wanxianzhen.UserWanXian;
import com.bbw.god.game.wanxianzhen.WanXianEmailEnum;
import com.bbw.god.game.wanxianzhen.event.WanXianLogDbHandler;
import com.bbw.god.game.wanxianzhen.service.*;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.card.special.UserSpecialCardRankLogic;
import com.bbw.god.gameuser.mail.MailService;
import com.bbw.oss.OSSService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * @author lwb 万仙阵比赛逻辑
 * @date 2020/4/22 11:00
 */
@Slf4j
public abstract class AbstractWanXianRace {
    @Autowired
    protected WanXianLogic wanXianLogic;
    @Autowired
    protected WanXianWinRankService wanXianWinRankService;
    @Autowired
    protected WanXianScoreRankService wanXianScoreRankService;
    @Autowired
    protected WanXianSeasonService wanXianSeasonService;
    @Autowired
    protected WanXianPVELogic wanXianPVELogic;
    @Autowired
    protected MailService mailService;
    @Autowired
    protected GameUserService gameUserService;
    @Autowired
    protected PVELogic pveLogic;
    @Autowired
    protected WanXianFightLogsService wanXianFightLogsService;
    @Autowired
    protected WanXianQualifyingRace wanXianQualifyingRace;
    @Autowired
    private UserSpecialCardRankLogic rankLogic;
    @Autowired
    private CombatVideoSaveAsyncHandler combatVideoSaveAsyncHandler;
    @Autowired
    private WanXianLogDbHandler wanXianLogDbHandler;

    public abstract void getMainPageInfo(long uid, RDWanXian rd, Integer param, int type);

    public abstract boolean todayRace(int weekDay);

    public abstract int getWanxianType();

    /**
     * 开始比赛
     */
    public abstract void beginTodayAllRace(int weekday, int gid, int type);

    /**
     * 执行战斗逻辑
     *
     * @param gid
     * @param type
     * @param emailEnum
     * @param fight
     * @return
     */
    public Combat doFightLogic(int gid, int type, WanXianEmailEnum emailEnum, RDWanXian.RDFightLog fight) {
        long p1 = fight.getP1().getUid();
        long p2 = fight.getP2().getUid();
        Combat combat = wanXianPVELogic.initCombatData(p1, p2, type, gid);
        if (fight.getP1().getHp() > 0) {
            Player p1Player = combat.getPlayerByUid(p1);
            p1Player.resetHp(p1Player.getHp() + fight.getP1().getHp());
        }
        if (fight.getP2().getHp() > 0) {
            Player p2Player = combat.getPlayerByUid(p2);
            p2Player.resetHp(p2Player.getHp() + fight.getP2().getHp());
        }
        CombatVideo video = new CombatVideo();
        String ossFileName = gid + "/" + fight.getVidKey();
        video.setSaveUrl(ossFileName);
        fight.setVid(OSSService.getWanxianUrl(type, ossFileName));
        try {
            pveLogic.wanXianFight(combat, video);
        } catch (Exception e) {
            log.error(e.getMessage());
            log.info("战斗错误，尝试重新战斗一次");
            combat = wanXianPVELogic.initCombatData(p1, p2, type, gid);
            video = new CombatVideo();
            video.setSaveUrl(ossFileName);
            pveLogic.wanXianFight(combat, video);
        }
        //保存记录战斗,存储视频
        fight.setWinner(combat.getWinnerId());
        saveFightLog(gid, type, emailEnum, fight, video);
        combatVideoSaveAsyncHandler.save(video, OSSService.getWanXianOssPath(type, ossFileName));
        //保存战斗记录到MYSQL
        WanXianFightDetailEntity logDetail = WanXianFightDetailEntity.instance(combat, fight);
        if (WanXianLogic.TYPE_SPECIAL_RACE == type) {
            logDetail.setWxType(wanXianSeasonService.getCurrentSpecialType(gid).getVal());
        }
        wanXianLogDbHandler.logFightLog(logDetail);
        return combat;
    }

    /**
     * 保存战斗结果记录
     *
     * @param gid
     * @param type
     * @param emailEnum
     * @param log
     * @param combatVideo
     */
    public void saveFightLog(int gid, int type, WanXianEmailEnum emailEnum, RDWanXian.RDFightLog log, CombatVideo combatVideo) {
        RDWanXian.RDUser winner = log.getWinner() == 1 ? log.getP1() : log.getP2();
        RDWanXian.RDUser loser = log.getWinner() == 1 ? log.getP2() : log.getP1();
        if (winner.getUid() > 0) {
            UserWanXian userWanXian = wanXianLogic.getOrCreateUserWanXian(winner.getUid(), type);
            userWanXian.addWinUid(loser.getUid());
            userWanXian.getFightLogs().add(log.getVidKey());
            gameUserService.updateItem(userWanXian);
        }
        if (loser.getUid() > 0) {
            UserWanXian userWanXian = wanXianLogic.getOrCreateUserWanXian(loser.getUid(), type);
            userWanXian.addFailUid(winner.getUid());
            userWanXian.getFightLogs().add(log.getVidKey());
            gameUserService.updateItem(userWanXian);
        }
    }

    /**
     * 获取对手
     *
     * @param uids
     * @param index
     * @return
     */
    protected Long getOppont(List<Long> uids, int index) {
        int seq = uids.size() <= index ? index - uids.size() : index;
        return uids.get(seq);
    }

    public void clear(int gid, int type, int weekday) {
    }

    /**
     * 发送淘汰邮件
     *
     * @param gid
     * @param type
     * @param order
     */
    public abstract void sendEliminateMail(int gid, int type, int order);

    /**
     * 发送战报邮件
     *
     * @param gid
     * @param type
     */
    public void sendMail(int gid, int type) {
        String title = "万仙阵常规赛-";
        if (type == WanXianLogic.TYPE_SPECIAL_RACE) {
            title = "万仙阵特色赛-";
        }
        List<Long> uids = new ArrayList<>();
        int weekday = DateUtil.getToDayWeekDay();
        if (weekday == 1 || weekday == 2) {
            uids = wanXianScoreRankService.getAllItemKeys(wanXianScoreRankService.getBaseKey(gid, type));
            title += "资格赛" + weekday + "日战报";
        } else if (weekday == 3 || weekday == 4 || weekday == 5) {
            title += "淘汰赛" + (weekday - 2) + "日战报";
            int begin = 0;
            int end = 3;
            if (weekday == 4) {
                begin = 4;
                end = 5;
            } else if (weekday == 5) {
                begin = 6;
                end = 6;
            }
            for (int i = 1; i <= 8; i++) {
                List<RDWanXian.RDFightLog> fights = wanXianSeasonService.getFightUsers(gid, type, "group_" + i);
                for (int j = begin; j <= end; j++) {
                    uids.add(fights.get(j).getP1().getUid());
                    uids.add(fights.get(j).getP2().getUid());
                }
            }
        } else if (weekday == 6) {
            title += "小组赛战报";
            List<Long> aGroup = wanXianScoreRankService.getKeysByRank(wanXianScoreRankService.getGroupStageBaseKey(gid, type, WanXianEmailEnum.EMAIL_GROUP_STAGE_6, "A"), 1, 4);
            List<Long> bGroup = wanXianScoreRankService.getKeysByRank(wanXianScoreRankService.getGroupStageBaseKey(gid, type, WanXianEmailEnum.EMAIL_GROUP_STAGE_6, "B"), 1, 4);
            uids.addAll(aGroup);
            uids.addAll(bGroup);
        }
        if (weekday == 7) {
            title += "决赛战报";
            List<RDWanXian.RDFightLog> fights = wanXianSeasonService.getFightUsers(gid, type, WanXianFinalsRace.FINALS_GROUP_KEY);
            if (ListUtil.isNotEmpty(fights)) {
                for (int i = 0; i < 2; i++) {
                    uids.add(fights.get(i).getP1().getUid());
                    uids.add(fights.get(i).getP2().getUid());
                }
            }
        }
        for (Long uid : uids) {
            Optional<UserWanXian> op = wanXianLogic.getUserWanXian(uid, type);
            if (op.isPresent()) {
                String content = "在本日万仙阵战斗中，你获胜了%s场，失败了%s场。\n";
                UserWanXian userWanXian = op.get();
                int win = 0;
                if (userWanXian.getFightWin() != null && !userWanXian.getFightWin().isEmpty()) {
                    content += "你战胜了的选手如下：";
                    Map<Long, Long> map = userWanXian.getFightWin().stream().collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
                    for (Map.Entry<Long, Long> entry : map.entrySet()) {
                        content += "\n【" + ServerTool.getServerShortName(gameUserService.getActiveSid(entry.getKey())) + "." + gameUserService.getGameUser(entry.getKey()).getRoleInfo().getNickname() + "】" + entry.getValue() + "次";
                        win += entry.getValue();
                    }
                }
                int lose = 0;
                if (userWanXian.getFightFail() != null && !userWanXian.getFightFail().isEmpty()) {
                    content += "\n你遗憾输给了以下选手：";
                    Map<Long, Long> map = userWanXian.getFightFail().stream().collect(Collectors.groupingBy(p -> p, Collectors.counting()));
                    for (Map.Entry<Long, Long> entry : map.entrySet()) {
                        content += "\n【" + ServerTool.getServerShortName(gameUserService.getActiveSid(entry.getKey())) + "." + gameUserService.getGameUser(entry.getKey()).getRoleInfo().getNickname() + "】" + entry.getValue() + "次";
                        lose += entry.getValue();
                    }
                }
                mailService.sendSystemMail(title, String.format(content, win, lose), userWanXian.getGameUserId());
                userWanXian.setShowLogMenu(true);
                gameUserService.updateItem(userWanXian);
            }
        }
    }

    /**
     * 拷贝卡组
     *
     * @param gid
     * @param orderEm
     * @param currentEm
     * @return
     */
    public String copyRank(int gid, int type, WanXianEmailEnum orderEm, WanXianEmailEnum currentEm) {
        String orderKey = wanXianScoreRankService.getSoreRankKey(gid, type, orderEm);
        int count = wanXianScoreRankService.getCount(orderKey);
        Set<ZSetOperations.TypedTuple<Long>> keysVals = wanXianScoreRankService.getKeysValsByRank(orderKey, 1, count);
        Iterator<ZSetOperations.TypedTuple<Long>> iterator = keysVals.iterator();
        Long[] keys = new Long[count];
        double[] scores = new double[count];
        int i = 0;
        while (iterator.hasNext()) {
            ZSetOperations.TypedTuple<Long> item = iterator.next();
            keys[i] = item.getValue();
            scores[i] = item.getScore();
            i++;
        }
        String currentKey = wanXianScoreRankService.getSoreRankKey(gid, type, currentEm);
        if (currentEm.getVal() == WanXianEmailEnum.EMAIL_QUALIFYING_RACE_8.getVal()) {
            currentKey += "_old";
        }
        wanXianScoreRankService.addKeyVals(currentKey, keys, scores);
        return currentKey;
    }

    public void copyRank(String fromKey, String toKey) {
        int count = wanXianScoreRankService.getCount(fromKey);
        Set<ZSetOperations.TypedTuple<Long>> keysVals = wanXianScoreRankService.getKeysValsByRank(fromKey, 1, count);
        Iterator<ZSetOperations.TypedTuple<Long>> iterator = keysVals.iterator();
        Long[] keys = new Long[count];
        double[] scores = new double[count];
        int i = 0;
        while (iterator.hasNext()) {
            ZSetOperations.TypedTuple<Long> item = iterator.next();
            keys[i] = item.getValue();
            scores[i] = item.getScore();
            i++;
        }
        if (wanXianScoreRankService.getCount(toKey) > 0) {
            wanXianScoreRankService.delRank(toKey);
        }
        wanXianScoreRankService.addKeyVals(toKey, keys, scores);
    }

    /**
     * 设置预测结果  每名选手与其他选手进行4局，2局先手，2局后手，胜利得1分，失败得0分，最后将8名玩家排名。
     *
     * @param promotionUids
     * @param order
     */
    public void buildChampionPrediction(int gid, int type, List<Long> promotionUids, int order) {
        List<RDWanXian.RDUser> cp = new ArrayList<>();
        for (int i = 0; i < promotionUids.size(); i++) {
            RDWanXian.RDUser user = RDWanXian.RDUser.instance(promotionUids.get(i), 0);
            user.setOrder(i + 1);
            cp.add(user);
        }
        //排序战斗，每人与对手打4场，每人先手2场
        for (int i = 0; i < promotionUids.size() - 1; i++) {
            for (int k = (i + 1); k < promotionUids.size(); k++) {
                List<Long> winList = new ArrayList<>(4);
                long p1 = promotionUids.get(i);
                long p2 = promotionUids.get(k);
                for (int m = 0; m < 4; m++) {
                    if (m < 2) {
                        winList.add(doOrderFight(p1, p2, type, gid));
                    } else {
                        winList.add(doOrderFight(p2, p1, type, gid));
                    }
                }
                Long p1WinNum = winList.stream().filter(p -> p == p1).count();
                cp.stream().filter(p -> p.getUid() == p1).findFirst().get().addScore(p1WinNum.intValue());
                cp.stream().filter(p -> p.getUid() == p2).findFirst().get().addScore(4 - p1WinNum.intValue());
            }
        }
        cp = cp.stream().sorted(Comparator.comparing(RDWanXian.RDUser::getScoreInt).reversed()).collect(Collectors.toList());
        //生成预测倍率  分数从高到低
        int bet = 0;
        int maxbet = 3;
        if (order == 4) {
            maxbet = 2;
        }
        String pre = "-1";
        for (int i = 0; i < order; i++) {
            RDWanXian.RDUser user = cp.get(i);
            if (!pre.equals(user.getScore())) {
                pre = user.getScore();
                bet = maxbet + i;
            }
            user.setMultiple(bet);
            log.info(type + "万仙阵" + order + "强预测排名结果：" + gameUserService.getGameUser(user.getUid()).getRoleInfo().getNickname() + "_积分" + user.getScoreInt());
        }
        cp = cp.stream().sorted(Comparator.comparing(RDWanXian.RDUser::getOrder)).collect(Collectors.toList());
        wanXianSeasonService.addVal(gid, type, "groupStage_cp_" + order, JSONUtil.toJson(cp));
    }

    private long doOrderFight(long p1, long p2, int type, int gid) {
        Combat combat = wanXianPVELogic.initCombatData(p1, p2, type, gid);
        try {
            pveLogic.wanXianFight(combat, null);
        } catch (Exception e) {
            log.error(e.getMessage());
            log.info("战斗错误，尝试重新战斗一次");
            combat = wanXianPVELogic.initCombatData(p1, p2, type, gid);
            pveLogic.wanXianFight(combat, null);
        }

        return combat.getWinnerId() == 1 ? p1 : p2;
    }

    public void logRank(int gid, int type, WanXianEmailEnum emailEnum, List<Long> uids) {
        String qualifingKey = wanXianScoreRankService.getSoreRankKey(gid, type, WanXianEmailEnum.EMAIL_QUALIFYING_RACE_8);
        String groupA = wanXianScoreRankService.getGroupStageBaseKey(gid, type, WanXianEmailEnum.EMAIL_GROUP_STAGE_6, "A");
        String groupB = wanXianScoreRankService.getGroupStageBaseKey(gid, type, WanXianEmailEnum.EMAIL_GROUP_STAGE_6, "B");
        List<WanXianRankEntity> logs = new ArrayList<>();
        int rank = 64;
        switch (emailEnum) {
            case EMAIL_ELIMINATION_SERIES_RACE_1:
                rank = 64;
                break;
            case EMAIL_ELIMINATION_SERIES_RACE_2:
                rank = 32;
                break;
            case EMAIL_ELIMINATION_SERIES_RACE_3:
                rank = 16;
                break;
            case EMAIL_GROUP_STAGE_6:
                rank = 8;
                break;
            case EMAIL_FINAL_RACE_1:
                rank = 4;
                break;
            case EMAIL_FINAL_RACE_2:
                rank = 2;
                break;
        }
        int batchRank = rank;//批次
        try {
            //记录
            for (Long uid : uids) {
                WanXianRankEntity entity = new WanXianRankEntity();
                entity.setGid(gid);
                entity.setUid(uid);
                entity.setWxType(wanXianLogic.getTypeRace(type, gid));
                entity.setQualifyingRank(wanXianScoreRankService.getRankByKey(qualifingKey, uid));
                entity.setQualifyingScore(wanXianScoreRankService.getValByKey(qualifingKey, uid));
                entity.setRank(rank);
                rank--;
                Optional<UserWanXian> op = wanXianLogic.getUserWanXian(uid, type);
                if (op.isPresent()) {
                    rankLogic.logSpecialCardRankByWanXian(uid, batchRank, type, op.get().getRaceCards());
                }
                if (WanXianEmailEnum.EMAIL_GROUP_STAGE_6.equals(emailEnum) || WanXianEmailEnum.EMAIL_FINAL_RACE_1.equals(emailEnum) || WanXianEmailEnum.EMAIL_FINAL_RACE_2.equals(emailEnum)) {
                    if (op.isPresent()) {
                        int tempRank = wanXianScoreRankService.getRankByKey(groupA, uid);
                        int score = 0;
                        if (tempRank > 0) {
                            score = wanXianScoreRankService.getValByKey(groupA, uid);
                            entity.setGroupName("A");
                        } else {
                            tempRank = wanXianScoreRankService.getRankByKey(groupB, uid);
                            score = wanXianScoreRankService.getValByKey(groupB, uid);
                            entity.setGroupName("B");
                        }
                        entity.setGroupRank(tempRank);
                        entity.setGroupScore(score);
                    }
                }
                logs.add(entity);
            }
            wanXianLogDbHandler.logRanks(logs);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public void addHistoryRank(Long uid, int gid, int type, int rank) {
        wanXianScoreRankService.addHistorySeason(gid, type, uid, rank);
        int[] types = {WanXianLogic.TYPE_REGULAR_RACE, WanXianLogic.TYPE_SPECIAL_RACE};
        for (int item : types) {
            Optional<UserWanXian> op = wanXianLogic.getUserWanXian(uid, item);
            if (op.isPresent() && op.get().hasSignUpRegularRace()) {
                wanXianSeasonService.addHistorySeasonCardGroup(gid, item, uid, JSONUtil.toJson(op.get().getRaceCards()));
            }
        }
    }
}
