package com.bbw.god.game.combat.pve.fst;

import com.bbw.common.DateUtil;
import com.bbw.common.LM;
import com.bbw.common.ListUtil;
import com.bbw.common.PowerRandom;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.event.BaseEventParam;
import com.bbw.god.fight.FightTypeEnum;
import com.bbw.god.game.combat.data.Combat;
import com.bbw.god.game.combat.data.param.CPCardGroup;
import com.bbw.god.game.combat.data.param.CPlayerInitParam;
import com.bbw.god.game.combat.event.CombatEventPublisher;
import com.bbw.god.game.combat.event.EPCombat;
import com.bbw.god.game.combat.pve.PVELogic;
import com.bbw.god.game.combat.video.CombatVideo;
import com.bbw.god.game.combat.video.CombatVideoSaveAsyncHandler;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.data.GameDataService;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.card.CardGroup;
import com.bbw.god.gameuser.card.CardGroupWay;
import com.bbw.god.gameuser.card.UserCardGroupLogic;
import com.bbw.god.gameuser.card.UserCardGroupService;
import com.bbw.god.gameuser.mail.MailService;
import com.bbw.god.server.ServerDataService;
import com.bbw.god.server.fst.FstLogic;
import com.bbw.god.server.fst.FstRanking;
import com.bbw.god.server.fst.FstVideoLog;
import com.bbw.god.server.fst.RDFst;
import com.bbw.god.server.fst.event.EVFstWin;
import com.bbw.god.server.fst.event.FstEventPublisher;
import com.bbw.god.server.fst.game.FstGameRanking;
import com.bbw.god.server.fst.game.FstGameService;
import com.bbw.god.server.fst.robot.FstRobotService;
import com.bbw.god.server.fst.server.FstServerService;
import com.bbw.oss.OSSConfig;
import com.bbw.oss.OSSService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * 说明：
 * 封神台战斗
 *
 * @author lwb
 * date 2021-07-06
 */
@Service
public class FstCombatLogic {
    @Autowired
    private UserCardGroupLogic userCardGroupLogic;
    @Autowired
    private FstGameService fstGameService;
    @Autowired
    private FstServerService fstServerService;
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private FstRobotService robotService;
    @Autowired
    private FstCombatInitService initService;
    @Autowired
    private PVELogic pveLogic;
    @Autowired
    private FstLogic fstLogic;
    @Autowired
    private GameDataService gameDataService;
    @Autowired
    private ServerDataService serverDataService;
    @Autowired
    private MailService mailService;
    @Autowired
    private UserCardGroupService userCardGroupService;
    @Autowired
    private CombatVideoSaveAsyncHandler combatVideoSaveAsyncHandler;

    /**
     * 区服封神台
     *
     * @param p1
     * @param p2
     * @return
     */
    public RDFst doServerFst(long p1, long p2) {
        if (!fstServerService.checkFightState(p1, p2)) {
            throw new ExceptionForClientTip("fst.fighting");
        }
        try {
            CombatEventPublisher.pubCombatInitEvent(EPCombat.instance(new BaseEventParam(p1), FightTypeEnum.FST.getValue(), p2));
            FstVideoLog p1Log = FstVideoLog.getInstance(true, p2);
            FstVideoLog p2Log = FstVideoLog.getInstance(false, p1);
            CPlayerInitParam p1InitParam = getCPlayerInitParam(p1, CardGroupWay.FIERCE_FIGHTING_ATTACK);
            CPlayerInitParam p2InitParam = getCPlayerInitParam(p2, CardGroupWay.FIERCE_FIGHTING_DEFENSE);
            Combat combat = initService.init(p1InitParam, p2InitParam, true);
            String url = doFight(false, combat, p1InitParam.getUid());
            long winId = combat.getWinnerId() == 1 ? combat.getP1().getUid() : combat.getP2().getUid();
            p1Log.addLog(true, url, winId == p1);
            p2Log.addLog(false, url, winId == p2);
            Optional<FstRanking> optional = fstServerService.getFstRanking(p1);
            int myWinStreak = 0;
            if (optional.isPresent()) {
                FstRanking fstRanking = optional.get();
                fstRanking.getVideoLogs().add(p1Log);
                fstRanking.addChallengeTotalNum();
                if (winId == p1) {
                    fstRanking.operateAsWin();
                    myWinStreak = fstRanking.getWinStreak();
                }
                if (winId == p1) {
                    //挑战者胜利 则需要变换名次
                    fstServerService.swapRanking(p1, p1Log, p2, p2Log);
                    sendDefenseFailMail(p2, p1InitParam.getNickname(), Math.abs(p2Log.getRank()), false);
                    int oldOppWinStreak = 0;
                    // 机器人不会挑战别人 所以机器人没有挑战连胜
                    if (p2 > 0) {
                        Optional<FstRanking> oppFst = fstServerService.getFstRanking(p2);
                        // 对手连胜纪录
                        oldOppWinStreak = oppFst.get().getWinStreak();
                        oppFst.get().resetWinStreak();
                        serverDataService.updateServerData(oppFst.get());
                    }
                    // 广播
                    BaseEventParam bep = new BaseEventParam(p1, WayEnum.FIGHT_FST);
                    EVFstWin ev = new EVFstWin(p2, p1Log.getRank(), oldOppWinStreak, myWinStreak);
                    FstEventPublisher.pubFstWinEvent(bep, ev);
                } else {
                    sendDefenseSuccessMail(p2, p1InitParam.getNickname(), false);
                }
                serverDataService.updateServerData(fstRanking);
            }
            if (p2 > 0) {
                Optional<FstRanking> optional2 = fstServerService.getFstRanking(p2);
                if (optional2.isPresent()) {
                    FstRanking fstRanking = optional2.get();
                    fstRanking.getVideoLogs().add(p2Log);
                    serverDataService.updateServerData(fstRanking);
                }
            }
            RDFst rst = new RDFst();
            rst.setLogs(fstLogic.getLog(p1, p1Log));
            return rst;
        } finally {
            fstServerService.removeFightState(p1, p2);
        }
    }

    /**
     * （1）跨服模式需要玩家编辑3套攻击卡组与3套防守卡组。
     * （2）玩家进行挑战时，己方攻击卡组将与对方防守卡组进行乱序对战。
     * （3）对战的胜利结果，将按照3局2胜的方式判定。
     * （4）先手规则：
     * 1）第一场为防守方先手。
     * 2）第二次为挑战方先手。
     * 3）第三次为防守方先手。
     *
     * @param p1
     * @param p2
     */
    public RDFst doGameFst(long p1, long p2) {
        List<CardGroupWay> attackWays = Arrays.asList(CardGroupWay.GAME_FST_ATTACK1, CardGroupWay.GAME_FST_ATTACK2, CardGroupWay.GAME_FST_ATTACK3);
        for (CardGroupWay way : attackWays) {
            CardGroup cGroup = userCardGroupService.getFierceFightingCards(p1, way);
            if (ListUtil.isEmpty(cGroup.getCardIds())) {
                throw new ExceptionForClientTip("fst.cant.empty.cardgroup");
            }
        }
        if (!fstGameService.checkFightState(p1, p2)) {
            throw new ExceptionForClientTip("fst.fighting");
        }
        try {
            CombatEventPublisher.pubCombatInitEvent(EPCombat.instance(new BaseEventParam(p1), FightTypeEnum.FST.getValue(), p2));
            PowerRandom.shuffle(attackWays);
            List<CardGroupWay> defenseWays = Arrays.asList(CardGroupWay.GAME_FST_DEFENSE1, CardGroupWay.GAME_FST_DEFENSE2, CardGroupWay.GAME_FST_DEFENSE3);
            PowerRandom.shuffle(defenseWays);
            FstVideoLog p1Log = FstVideoLog.getInstance(true, p2);
            FstVideoLog p2Log = FstVideoLog.getInstance(false, p1);
            for (int i = 0; i < 3; i++) {
                CPlayerInitParam p1InitParam = getCPlayerInitParam(p1, attackWays.get(i));
                CPlayerInitParam p2InitParam = getCPlayerInitParam(p2, defenseWays.get(i));
                Combat combat = initService.init(p1InitParam, p2InitParam, i == 1);
                String url = doFight(true, combat, i == 1 ? p1InitParam.getUid() : p2InitParam.getUid());
                long winId = combat.getWinnerId() == 1 ? combat.getP1().getUid() : combat.getP2().getUid();
                p1Log.addLog(i == 1, url, winId == p1);
                p2Log.addLog(i != 1, url, winId == p2);
                if (p1Log.ifDone()) {
                    break;
                }
            }
            FstGameRanking p1GameRanking = fstGameService.getOrCreateFstGameRanking(p1);
            p1GameRanking.getVideoLogs().add(p1Log);
            //记录跨服封神台最近一次挑战时间
            p1GameRanking.setLastChallengeDate(DateUtil.now());
            if (p1Log.isWin()) {
                //挑战者胜利 则需要变换名次
                fstGameService.swapRanking(p1, p1Log, p2, p2Log);
                sendDefenseFailMail(p2, gameUserService.getGameUser(p1).getRoleInfo().getNickname(), Math.abs(p2Log.getRank()), true);
            } else {
                if (p2 > 0) {
                    FstEventPublisher.pubFstGuardWinEvent(new BaseEventParam(p2));
                }
            }
            if (p2 > 0) {
                FstGameRanking p2GameRanking = fstGameService.getOrCreateFstGameRanking(p2);
                p2GameRanking.getVideoLogs().add(p2Log);
                gameDataService.updateGameData(p2GameRanking);
            }
            gameDataService.updateGameData(p1GameRanking);
            FstEventPublisher.pubGameFstFightOverEvent(p1, p1Log.isWin());
            RDFst rst = new RDFst();
            rst.setLogs(fstLogic.getLog(p1, p1Log));
            return rst;
        } finally {
            fstGameService.removeFightState(p1, p2);
        }
    }

    /**
     * 战斗执行
     *
     * @param isGameFst
     * @param combat
     * @param firstUid
     * @return
     */
    private String doFight(boolean isGameFst, Combat combat, long firstUid) {
        CombatVideo video = new CombatVideo();
        video.setId(combat.getId());
        String ossPath = OSSService.getFstOssPath(isGameFst, firstUid, combat.getId());
        video.setSaveUrl(ossPath);
        pveLogic.fstFight(combat, video, isGameFst);
        combatVideoSaveAsyncHandler.save(video, ossPath);
        return OSSConfig.downStr + ossPath;
    }

    /**
     * 获取初始化对象
     *
     * @param uid
     * @param way
     * @return
     */
    private CPlayerInitParam getCPlayerInitParam(long uid, CardGroupWay way) {
        if (uid < 0) {
            return robotService.getRobotInitParam(uid);
        }
        CPCardGroup cp = userCardGroupLogic.getGameFstFightCards(uid, way);
        CPlayerInitParam param = CPlayerInitParam.initParam(gameUserService.getGameUser(uid), cp.getCards(), cp.getBuffs(), new ArrayList<>());
        return param;
    }

    /**
     * 防守成功邮件
     *
     * @param uid  被挑战者
     * @param oppo 挑战者
     */
    private void sendDefenseSuccessMail(long uid, String oppo, boolean isGameFst) {
        //对手许久未登录，未加载mail,不为其发放邮件
        if (uid < 0) {
            return;
        }
        if (!mailService.hasLoadedMail(uid)) {
            return;
        }
        // 给对手发邮件
        String title = isGameFst ? LM.I.getMsg("mail.fst.game.defensive.win.title") : LM.I.getMsg("mail.fst.defensive.win.title");
        String content = LM.I.getMsgByUid(uid, "mail.fst.win.content", oppo);
        mailService.sendSystemMail(title, content, uid);
    }

    /**
     * 防守失败邮件
     *
     * @param uid  被挑战者
     * @param oppo 挑战者
     */
    private void sendDefenseFailMail(long uid, String oppo, int rank, boolean isGameFst) {
        if (uid < 0) {
            return;
        }
        //对手许久未登录，未加载mail,不为其发放邮件
        if (!mailService.hasLoadedMail(uid)) {
            return;
        }
        if (isGameFst) {
            rank = Math.min(121, rank);
        }
        String title = isGameFst ? LM.I.getMsg("mail.fst.game.defensive.fail.title") : LM.I.getMsg("mail.fst.defensive.fail.title");
        String contentWithRankChange = LM.I.getMsgByUid(uid, "mail.fst.fail.content.with.rank.change", oppo, rank);
        String content = rank <= 0 ? LM.I.getMsgByUid(uid, "mail.fst.fail.content", oppo) : contentWithRankChange;
        mailService.sendSystemMail(title, content, uid);
    }

}
