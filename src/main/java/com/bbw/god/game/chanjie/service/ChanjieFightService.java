package com.bbw.god.game.chanjie.service;

import com.bbw.common.DateUtil;
import com.bbw.common.ID;
import com.bbw.common.JSONUtil;
import com.bbw.common.LM;
import com.bbw.god.db.entity.InsGamePvpDetailEntity;
import com.bbw.god.detail.async.PvpDetailAsyncHandler;
import com.bbw.god.event.BaseEventParam;
import com.bbw.god.fight.FightTypeEnum;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.game.chanjie.*;
import com.bbw.god.game.chanjie.event.ChanjieEventPublisher;
import com.bbw.god.game.chanjie.event.EPChanjieFight;
import com.bbw.god.game.config.Cfg;
import com.bbw.god.game.config.CfgChanjie;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.mail.MailService;
import com.bbw.god.gameuser.res.ResEventPublisher;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author 作者 ：lwb
 * @version 创建时间：2019年9月6日 上午11:00:58
 * <p>
 * 阐截斗法战斗结算
 */
@Service
public class ChanjieFightService {
    @Autowired
    private ChanjieRedisService redisService;
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private ChanjieService chanjieService;
    @Autowired
    private ChanjieUserService chanjieUserService;
    @Autowired
    private ChanjieSundayFightService chanjieSundayFightService;
    @Autowired
    private MailService mailService;
    @Autowired
    private PvpDetailAsyncHandler pvpDetailAsyncHandler;

    public List<ChanjieRd> submitChanJieFightResult(long winnerUid, boolean winnerOnline, long loserUid, boolean loserOnline, int gid) {
        log(winnerUid, loserUid, gid);
        List<ChanjieRd> list = new ArrayList<>();
        if (DateUtil.isWeekDay(7)) {
            list = doSunDayFightResult(winnerUid, winnerOnline, loserUid, loserOnline, gid);
        } else {
            list = doNomalFightResult(winnerUid, winnerOnline, loserUid, loserOnline, gid);
        }
        return list;
    }

    /**
     * 周一至 周六结算
     *
     * @param winnerUid
     * @param loserUid
     * @param gid
     * @param
     * @return
     */
    private List<ChanjieRd> doNomalFightResult(long winnerUid, boolean winnerOnline, long loserUid, boolean loserOnline, int gid) {
        ChanjieUserInfo winner = chanjieUserService.getUserInfo(winnerUid);
        ChanjieUserInfo loser = chanjieUserService.getUserInfo(loserUid);
        List<ChanjieRd> rds = new ArrayList<ChanjieRd>();
        ChanjieRd winRd = winnerAward(winner, winnerOnline, loser.getHonorLv(), gid);
        rds.add(winRd);
        doResultEvent(winner, loser, gid, winRd.getAddedHonor());
        rds.add(loserAward(loser, loserOnline, gid));
        return rds;
    }

    public ChanjieRd winnerAward(ChanjieUserInfo winner, boolean winnerOnline, int loserHv, int gid) {
        long winnerUid = winner.getGameUserId();
        winner.addVictory();
        CfgChanjie cfgChanjie = Cfg.I.getUniqueConfig(CfgChanjie.class);
        double multiple = 0.0;
        // 获取倍数 以及 胜利方获得的荣誉点
        int addHornor = 0;
        for (CfgChanjie.HonorAward honorAwd : cfgChanjie.getHonorlv()) {
            if (honorAwd.getLv().equals(winner.getHonorLv())) {
                multiple += honorAwd.getMultiple();
            }
            if (honorAwd.getLv() == loserHv) {
                multiple += honorAwd.getMultiple();
                addHornor = honorAwd.getHonor();
            }
        }
        if (DateUtil.isWeekDay(6)) {
            // 周6 双倍荣誉积分
            addHornor = addHornor * 2;
        }
        winner.addHonor(addHornor);// 加积分
        gameUserService.updateItem(winner);
        // 更新胜方在榜单中的荣誉点
        String keyStr = ChanjieTools.getZsetKey(winner.getReligiousType(), ChanjieType.KEY_RANKING_ZSET, gid);
        redisService.addZset(keyStr, ChanjieTools.getScore(winner.getHonor()), winnerUid);
        chanjieService.addReligiouWinNum(winner.getReligiousType(), gid);
        // 发送胜利方奖励 最终奖励 =（自己倍数+对手倍数）*基础奖励 倍数直接去除小数位 基础奖励：5000铜钱、1个神砂
        ChanjieRd winnerRd = new ChanjieRd();
        BigDecimal bd = new BigDecimal(multiple);
        int mp = bd.intValue();
        if (winnerOnline) {
            ResEventPublisher.pubCopperAddEvent(winnerUid, 5000 * mp, WayEnum.CHANJIE_FIGHT, winnerRd);
            TreasureEventPublisher.pubTAddEvent(winnerUid, 10020, 1 * mp, WayEnum.CHANJIE_FIGHT, winnerRd);
        } else {
            List<Award> awards = new ArrayList<>();
            awards.add(new Award(AwardEnum.TQ, 5000 * mp));
            awards.add(new Award(10020, AwardEnum.FB, 1 * mp));
            String title = LM.I.getMsgByUid(winnerUid, "mail.chanJie.fight.results.title");
            String content = LM.I.getMsgByUid(winnerUid, "mail.chanJie.fight.win.results.content", addHornor);
            mailService.sendAwardMail(title, content, winnerUid, awards);
        }
        winnerRd.setPlayingId(winnerUid);
        winnerRd.setAddedHonor(addHornor);
        return winnerRd;
    }

    public ChanjieRd loserAward(ChanjieUserInfo loser, boolean loserOnline, int gid) {
        // 扣血,增加失败场次,中断连胜
        if (!DateUtil.isWeekDay(6)) {
            loser.deductBlood();
        }
        loser.addDefeat();
        loser.stopVictoryStats();
        loser.addHonor(ChanjieTools.LOSER_HONER);
        gameUserService.updateItem(loser);
        // 发送失败方奖励 基础奖励：5000铜钱、1个神砂、1积分
        String keyStr = ChanjieTools.getZsetKey(loser.getReligiousType(), ChanjieType.KEY_RANKING_ZSET, gid);
        redisService.addZset(keyStr, ChanjieTools.getScore(loser.getHonor()), loser.getGameUserId());
        ChanjieRd loserRd = new ChanjieRd();
        if (loserOnline) {
            ResEventPublisher.pubCopperAddEvent(loser.getGameUserId(), 5000, WayEnum.CHANJIE_FIGHT, loserRd);
            TreasureEventPublisher.pubTAddEvent(loser.getGameUserId(), 10020, 1, WayEnum.CHANJIE_FIGHT, loserRd);
        } else {
            List<Award> awards = new ArrayList<>();
            awards.add(new Award(AwardEnum.TQ, 5000));
            awards.add(new Award(10020, AwardEnum.FB, 1));
            String title = LM.I.getMsgByUid(loser.getGameUserId(), "mail.chanJie.fight.results.title");
            String content = LM.I.getMsgByUid(loser.getGameUserId(), "mail.chanJie.fight.lose.results.content", ChanjieTools.LOSER_HONER);
            mailService.sendAwardMail(title, content, loser.getGameUserId(), awards);
        }
        loserRd.setPlayingId(loser.getGameUserId());
        loserRd.setAddedHonor(ChanjieTools.LOSER_HONER);
        return loserRd;
    }

    private void doResultEvent(ChanjieUserInfo winner, ChanjieUserInfo loser, int gid, int winAddHonor) {
        // 发布击败事件
        EPChanjieFight fight = EPChanjieFight.instance(new BaseEventParam(winner.getGameUserId()), loser.getHonorLv(),
                loser.getReligiousId(), loser.getGameUserId(), loser.getReligiousId());
        ChanjieEventPublisher.pubFightEvent(fight);
        // 处理教派奇人
        // 更新今日胜场、积分、匹配到的仙人
        this.addSpecialHonor(winner, ChanjieType.KEY_SPECIAL_YRYY, 1, gid);
        this.addSpecialHonor(winner, ChanjieType.KEY_SPECIAL_DDST, winAddHonor, gid);
        this.addSpecialHonor(loser, ChanjieType.KEY_SPECIAL_DDST, ChanjieTools.LOSER_HONER, gid);
        if (ChanjieTools.FIGHT_XIAN_REN_LV <= loser.getHonorLv()) {
            this.addSpecialHonor(winner, ChanjieType.KEY_SPECIAL_TXZR, 1, gid);
        }
        if (ChanjieTools.FIGHT_XIAN_REN_LV <= winner.getHonorLv()) {
            this.addSpecialHonor(loser, ChanjieType.KEY_SPECIAL_TXZR, 1, gid);
        }
        // 判断是否符合 荣誉榜的记录要求
        if (loser.getHonorLv() >= ChanjieTools.FIGHT_HONOR_LV) {
            String loserReligious = ChanjieType.getType(loser.getReligiousId()).getMemo();// 获取教派名称
            String winnerReligious = ChanjieType.getType(winner.getReligiousId()).getMemo();// 获取教派名称
            GameUser wgu = gameUserService.getGameUser(winner.getGameUserId());
            GameUser lgu = gameUserService.getGameUser(loser.getGameUserId());
            String nicknameStr = ChanjieTools.getServerNamePrefix(wgu.getServerId()) + "."
                    + wgu.getRoleInfo().getNickname();
            String nicknameStr2 = ChanjieTools.getServerNamePrefix(lgu.getServerId()) + "."
                    + lgu.getRoleInfo().getNickname();
            String winLog = String.format("【%s】击败了%s%s【%s】", nicknameStr, loserReligious, loser.getHeadName(), nicknameStr2);
            redisService.fightLogLeftPush(winner.getReligiousType(), winLog, gid);
            String loseLog = String.format("【%s】被%s%s【%s】击败了", nicknameStr2, winnerReligious, winner.getHeadName(), nicknameStr);
            redisService.fightLogLeftPush(loser.getReligiousType(), loseLog, gid);
        }
    }

    /**
     * 增加教派奇人的数据 （连胜除外）
     *
     * @param info
     * @param type
     * @param num
     */
    public void addSpecialHonor(ChanjieUserInfo info, ChanjieType type, int num, int gid) {
        String key = ChanjieTools.getDailyKey(ChanjieType.getType(info.getReligiousId()), new Date(), type, gid);
        long uid = info.getGameUserId();
        Double so = redisService.getZSetScore(key, uid);
        long old = so.longValue();
        long score = old / 10000000 + num;// 获取真实分数+新增
        long add = ChanjieTools.getScore(score) - old;
        redisService.incrementScore(key, add, uid);
    }

    /**
     * 周日乱斗封神 结算
     *
     * @param winnerUid
     * @param loserUid
     * @param gid
     * @param
     * @return
     */
    private List<ChanjieRd> doSunDayFightResult(long winnerUid, boolean winnerOnline, long loserUid, boolean loserOnline, int gid) {
        ChanjieUserInfo winner = chanjieUserService.getUserInfo(winnerUid);
        ChanjieUserInfo loser = chanjieUserService.getUserInfo(loserUid);
        List<ChanjieRd> rds = new ArrayList<ChanjieRd>();
        rds.add(LDFXfightwinnerResult(winner, winnerOnline, loserUid, gid));
        rds.add(LDFXfightLoserResult(loser, loserOnline, gid));
        return rds;
    }

    /**
     * 胜利 1个进阶宝袋
     *
     * @param winner
     * @param gid
     * @return
     */
    private ChanjieRd LDFXfightwinnerResult(ChanjieUserInfo winner, boolean winnerOnline, long loserUid, int gid) {
        long winnerUid = winner.getGameUserId();
        winner.addVictory();
        gameUserService.updateItem(winner);
        String logStr = "【%s】击败【%s】，获得首胜！";
        switch (winner.getVictory()) {
            case 2:
                logStr = "【%s】击败【%s】，获得2连胜！";
                break;
            case 3:
                logStr = "【%s】击败【%s】，获得3连胜！";
                break;
            case 4:
                logStr = "【%s】击败【%s】，获得4连胜！";
                break;
        }
        String nickname = gameUserService.getGameUser(winnerUid).getRoleInfo().getNickname();
        String wname = ChanjieTools.getServerNamePrefix(gameUserService.getActiveSid(winnerUid)) + nickname;

        String loserNickname = gameUserService.getGameUser(loserUid).getRoleInfo().getNickname();
        String lname = ChanjieTools.getServerNamePrefix(gameUserService.getActiveSid(loserUid)) + loserNickname;

        String logString = String.format(logStr, wname, lname);
        redisService.fightLogLeftPush(null, logString, gid);
        // 更新排名
        String rankKeyStr = ChanjieTools.getZsetKey(null, ChanjieType.KEY_RANKING_ZSET, gid);
        // 检查该玩家是否出局 即缩圈出局
        if (!chanjieUserService.hasInLDFXfightloseRanking(winnerUid, gid)) {
            // 未出局则更新积分
            redisService.addZset(rankKeyStr, ChanjieTools.getScore(winner.getVictory()), winnerUid);
        }
        // 胜利检查
        if (winner.getVictory() == 4) {
            // 最终胜利 停止战斗
            chanjieSundayFightService.stopFight(winnerUid, gid);
        }
        ChanjieRd wrd = new ChanjieRd();
        // 胜利 1个进阶宝袋 570
        if (winnerOnline) {
            TreasureEventPublisher.pubTAddEvent(winnerUid, 570, 1, WayEnum.CHANJIE_FIGHT, wrd);
        } else {
            List<Award> awards = new ArrayList<>();
            awards.add(new Award(570, AwardEnum.FB, 1));
            String title = LM.I.getMsgByUid(winner.getGameUserId(), "mail.chanJie.fight.results.title");
            String content = LM.I.getMsgByUid(winner.getGameUserId(), "mail.chanJie.fight.win.award.content");
            mailService.sendAwardMail(title, content, winner.getGameUserId(), awards);
        }
        wrd.setPlayingId(winnerUid);
        return wrd;
    }

    /**
     * 战斗失败结果处理 失败 5W铜钱、3个神砂
     *
     * @return
     */
    private ChanjieRd LDFXfightLoserResult(ChanjieUserInfo loser, boolean loserOnline, int gid) {
        ChanjieRd rd = new ChanjieRd();
        loser.deductBlood();
        gameUserService.updateItem(loser);
        // 出局
        chanjieSundayFightService.gameout(loser.getGameUserId(), gid);
        // 5W铜钱、3个神砂10020
        if (loserOnline) {
            ResEventPublisher.pubCopperAddEvent(loser.getGameUserId(), 50000, WayEnum.CHANJIE_FIGHT, rd);
            TreasureEventPublisher.pubTAddEvent(loser.getGameUserId(), 10020, 3, WayEnum.CHANJIE_FIGHT, rd);
        } else {
            List<Award> awards = new ArrayList<>();
            awards.add(new Award(AwardEnum.TQ, 50000));
            awards.add(new Award(10020, AwardEnum.FB, 3));
            String title = LM.I.getMsgByUid(loser.getGameUserId(), "mail.chanJie.fight.results.title");
            String content = LM.I.getMsgByUid(loser.getGameUserId(), "mail.chanJie.fight.lose.award.content");
            mailService.sendAwardMail(title, content, loser.getGameUserId(), awards);
        }
        rd.setPlayingId(loser.getGameUserId());
        return rd;
    }

    /**
     * 战斗日志持久化
     *
     * @param winnerUid
     * @param loserUid
     * @param gid
     * @param
     */
    private void log(long winnerUid, long loserUid, int gid) {
        ChanjieUserInfo winner = chanjieUserService.getUserInfo(winnerUid);
        ChanjieUserInfo loser = chanjieUserService.getUserInfo(loserUid);
        InsGamePvpDetailEntity detailData = new InsGamePvpDetailEntity();
        detailData.setId(ID.INSTANCE.nextId());
        detailData.setServerGroup(gid);
        detailData.setFightType(FightTypeEnum.CJDF.getValue());
        detailData.setFightTypeName(FightTypeEnum.CJDF.getName());
        detailData.setRoomId(0);
        detailData.setUser1(winnerUid);
        detailData.setUser2(loserUid);
        detailData.setWinner(winnerUid);
        ChanjieDetail detail = new ChanjieDetail();
        detail.setHeadName1(winner.getHeadName());
        detail.setHonerLV1(winner.getHonorLv());
        detail.setHeadName2(loser.getHeadName());
        detail.setHonerLV2(loser.getHonorLv());
        detailData.setFightTime(DateUtil.toDateTimeLong());
        detailData.setDataJson(JSONUtil.toJson(detail));
        pvpDetailAsyncHandler.log(detailData);
    }
}
