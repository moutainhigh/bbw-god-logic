package com.bbw.god.gm.wanxian;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.bbw.common.DateUtil;
import com.bbw.common.PowerRandom;
import com.bbw.common.Rst;
import com.bbw.common.SpringContextUtil;
import com.bbw.god.db.entity.WanXianUserCardsEntity;
import com.bbw.god.db.service.InsRoleInfoService;
import com.bbw.god.db.service.WanXianUserCardsService;
import com.bbw.god.game.config.card.CardTool;
import com.bbw.god.game.config.card.CfgCardEntity;
import com.bbw.god.game.config.server.ServerTool;
import com.bbw.god.game.wanxianzhen.RDWanXianJob;
import com.bbw.god.game.wanxianzhen.UserWanXian;
import com.bbw.god.game.wanxianzhen.WanXianSpecialType;
import com.bbw.god.game.wanxianzhen.service.*;
import com.bbw.god.game.wanxianzhen.service.race.WanXianEliminationSeriesRace;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.page.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author lwb 万仙阵入口
 * @date 2020/4/22 10:17
 */
@Slf4j
@RestController
@RequestMapping("/gm")
public class GMWanXianCtrl {
    @Autowired
    private WanXianLogic wanXianLogic;
    @Autowired
    private InsRoleInfoService insRoleInfoService;
    @Autowired
    private WanXianRaceFactory wanXianRaceFactory;
    @Autowired
    private WanXianWinRankService wanXianWinRankService;
    @Autowired
    private WanXianScoreRankService wanXianScoreRankService;
    @Autowired
    private WanXianSeasonService wanXianSeasonService;
    @Autowired
    private WanXianJobService wanXianJobService;

    @RequestMapping("wanxian!bm")
    public Rst bm(int type) {
        List<Long> uids = insRoleInfoService.getAllUidsByServer(99);
        int num = 0;
        List<CfgCardEntity> allCards = CardTool.getAllCards();
        for (Long uid : uids) {
            if (gameUserService.getGameUser(uid).getLevel() < 10) {
                continue;
            }
            if (num == 64) {
                break;
            }
            if (uid == 190416009900005L || uid == 190516009900015L) {
                continue;
            }
            List<Integer> carIds = PowerRandom.getRandomsFromList(20, allCards).stream().map(CfgCardEntity::getId).collect(Collectors.toList());
            String str = carIds.toString();
            wanXianLogic.saveCardGroup(uid, type, str.substring(1, str.length() - 1), 1);
            wanXianLogic.signUpRace(type, uid, 1);
            num++;
        }
        wanXianLogic.updateWanXianCardInfo(1, type, RDWanXianJob.instance(1, type));
        return Rst.businessOK();
    }

    /**
     * 战斗
     *
     * @param gid     平台号
     * @param weekday 星期几
     * @param type    类型2000为特色赛  1000为常规赛
     * @return
     */
    @RequestMapping("wanxian!fight1")
    public Rst fight(int weekday, int gid, int type) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                long time1 = System.currentTimeMillis();
                WanXianRaceFactory factory = SpringContextUtil.getBean(WanXianRaceFactory.class);
                factory.matchByWeekDay(weekday).beginTodayAllRace(weekday, gid, type);
                System.err.println("执行赛事耗时：" + (System.currentTimeMillis() - time1));
            }
        }).start();
        return Rst.businessOK();
    }

    /**
     * 清除玩家信息的
     *
     * @param gid     平台号
     * @param weekday 星期几
     * @param type    类型2000为特色赛  1000为常规赛
     * @return
     */
    @RequestMapping("wanxian!clear")
    public Rst clear(int gid, int weekday, int type) {
        wanXianRaceFactory.matchByWeekDay(weekday).clear(gid, type, weekday);
        if (weekday == 1 || weekday == 2) {
            wanXianWinRankService.rest(gid, type);
        }
        return Rst.businessOK();
    }

    @RequestMapping("wanxian!up")
    public Rst up(int gid, int type) {
        wanXianLogic.updateWanXianCardInfo(gid, type, RDWanXianJob.instance(gid, type));
        return Rst.businessOK();
    }

    @Autowired
    private WanXianScoreRankService wanXianRankService;
    @Autowired
    private GameUserService gameUserService;

    @RequestMapping("wanxian!check")
    public Rst check(int gid, int type) {
        List<Long> uids = wanXianRankService.getAllItemKeys(wanXianRankService.getBaseKey(gid, type));
        String msg = "";
        for (Long uid : uids) {
            UserWanXian wanXian = wanXianLogic.getOrCreateUserWanXian(uid, type);
            if (wanXian.getRaceCards() == null || wanXian.getRaceCards().isEmpty()) {
                GameUser gu = gameUserService.getGameUser(uid);
                msg += ";" + uid;
                log.error(type + "卡牌缺失玩家：UID:" + uid + "名字：" + gu.getRoleInfo().getNickname() + ",区服" + ServerTool.getServerShortName(gu.getServerId()));
                wanXianLogic.repairUserCards(uid, gid, type);
            }
        }
        return Rst.businessOK(msg);
    }

    @RequestMapping("wanxian!sendMail")
    public Rst sendMail(int gid, int type) {
        int weekday = DateUtil.getToDayWeekDay();
        wanXianRaceFactory.matchByWeekDay(weekday).sendMail(gid, type);
        return Rst.businessOK();
    }

    @RequestMapping("wanxian!sendEMMail")
    public Rst sendEMMail(int gid, Integer order, int type) {
        int weekday = DateUtil.getToDayWeekDay();
        wanXianRaceFactory.matchByWeekDay(weekday).sendEliminateMail(gid, type, order);
        return Rst.businessOK();
    }

    @Autowired
    private WanXianEliminationSeriesRace wanXianEliminationSeriesRace;

    @RequestMapping("wanxian!repairCP")
    public Rst repairCp(int gid, int weekday, int type) {
        List<Long> aUids = wanXianScoreRankService.getAllItemKeys(wanXianScoreRankService.getGroupStageBaseKey(gid, type, null, "A"));
        List<Long> bUids = wanXianScoreRankService.getAllItemKeys(wanXianScoreRankService.getGroupStageBaseKey(gid, type, null, "B"));
        aUids.addAll(bUids);
        wanXianEliminationSeriesRace.buildChampionPrediction(gid, type, aUids, 8);
        return Rst.businessOK();
    }

    @Autowired
    private WanXianUserCardsService wanXianUserCardsService;

    @RequestMapping("wanxian!repairCards")
    public Rst repairUserCards(int gid, int type, int totype, int season) {
        List<Long> uids = wanXianScoreRankService.getHistorySeasonRankBySeason(gid, type, season);
        Map<String, String> cards = new HashMap<String, String>();
        for (Long uid : uids) {
            EntityWrapper er = new EntityWrapper<>();
            er.eq("uid", uid);
            er.eq("season", season);
            er.eq("wxtype", totype);
            WanXianUserCardsEntity entity = wanXianUserCardsService.selectOne(er);
            if (entity != null) {
                cards.put(uid.toString(), entity.getCards());
            }
        }
        if (!cards.isEmpty()) {
            wanXianSeasonService.addHistorySeasonCardGroups(gid, totype, season, cards);
        }
        return Rst.businessOK();
    }

    @RequestMapping("wanxian!getJionEMuids")
    public R getJionEMUids(int gid, int season) {
        String key = "game:wanXian:" + season + ":" + gid + ":score_13452";
        List<Long> uidsList = wanXianScoreRankService.getKeysByRank(key, 1, 64);
        return R.ok().put("data", uidsList);
    }

    /**
     * 重新执行某一天没有执行的所有定时操作
     *
     * @param weekDay
     */
    @RequestMapping("wanxian!redoAllUndoJob")
    public void doJob(int weekDay) {
        wanXianJobService.fightJob(weekDay, false);
        wanXianJobService.sendMailJob(weekDay);
        wanXianJobService.sendEliminationMailJob(weekDay, "1");
    }

    /**
     * 重新执行当日的所有定时操作
     */
    @RequestMapping("wanxian!redoAllJobToday")
    public void doJob() {
        int weekday = DateUtil.getToDayWeekDay();
        wanXianJobService.fightJob(weekday, false);
        wanXianJobService.sendMailJob(weekday);
        wanXianJobService.sendEliminationMailJob(weekday, "1");
    }

    /**
     * 补发战报
     */
    @RequestMapping("wanxian!redoMailSendJobToday")
    public void sendMail() {
        int weekday = DateUtil.getToDayWeekDay();
        wanXianJobService.sendMailJob(weekday);
        wanXianJobService.sendEliminationMailJob(weekday, "1");
    }

    /**
     * 该方法用于核对特色赛上牌是否符合要求，未写在里面的说明上牌没有要求
     *
     * @param type        万仙阵类型
     * @param cardId      卡牌id
     * @param specialType 特色赛类型
     * @return true为符合要求 false为不符合要求
     */
    public boolean validSpecialCardGroup(int type, int cardId, WanXianSpecialType specialType) {
        if (WanXianLogic.TYPE_SPECIAL_RACE != type) {
            return true;
        }
        switch (specialType) {
            case JIN://金系赛（仅能使用金系神将参赛）
                return !(cardId % 10000 > 199);
            case MU://木系（仅能使用木系神将参赛）
                return !(cardId % 10000 < 200 || cardId % 10000 > 299);
            case XIAO_BING://小兵赛（仅能使用1星神将和2星神将参赛，玩家等级调为40级）
                CfgCardEntity cardEntity = CardTool.getCardById(cardId);
                return cardEntity.getStar() <= 2;
            case SHUI://水系（仅能使用水系神将参赛）
                return !(cardId % 10000 < 300 || cardId % 10000 > 399);
            case ZHONG_JIAN://中坚赛（仅能使用3星神将和4星神将参赛，玩家等级调为80级）
                CfgCardEntity cardEntity2 = CardTool.getCardById(cardId);
                return cardEntity2.getStar() > 2 && cardEntity2.getStar() < 5;
            case HUO://火系赛（仅能使用火系神将参赛）
                return !(cardId % 10000 < 400 || cardId % 10000 > 499);
            case PING_MIN://平民赛（无法使用五张王者神将参与比赛）
                List<Integer> wzCards = Arrays.asList(126, 226, 325, 425, 525, 10325);
                return !wzCards.contains(cardId);
            case TU://土系赛（仅能使用土系神将参赛）
                return !(cardId % 10000 < 500);
        }
        return true;
    }
}
