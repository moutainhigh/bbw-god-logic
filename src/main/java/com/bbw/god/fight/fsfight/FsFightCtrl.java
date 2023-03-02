package com.bbw.god.fight.fsfight;

import com.bbw.common.IpUtil;
import com.bbw.common.ListUtil;
import com.bbw.exception.CoderException;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.city.mixd.nightmare.NightmareMiXianService;
import com.bbw.god.controller.AbstractController;
import com.bbw.god.db.entity.CfgServerEntity;
import com.bbw.god.fight.FightTypeEnum;
import com.bbw.god.fight.fsfight.RDAllCards.RDFsFightCard;
import com.bbw.god.game.CR;
import com.bbw.god.game.chanjie.ChanjieRd;
import com.bbw.god.game.chanjie.service.ChanjieFightService;
import com.bbw.god.game.chanjie.service.ChanjieUserService;
import com.bbw.god.game.config.Cfg;
import com.bbw.god.game.config.CfgServerGroup;
import com.bbw.god.game.config.card.CardTool;
import com.bbw.god.game.config.card.CfgCardEntity;
import com.bbw.god.game.config.server.ServerTool;
import com.bbw.god.game.dfdj.DfdjLogic;
import com.bbw.god.game.transmigration.GameTransmigrationService;
import com.bbw.god.game.zxz.service.ZxzCardInfoService;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.card.OppCardService;
import com.bbw.god.gameuser.card.RDCardStrengthen;
import com.bbw.god.gameuser.card.UserCard;
import com.bbw.god.gameuser.card.UserCardService;
import com.bbw.god.gameuser.card.equipment.UserCardXianJueService;
import com.bbw.god.gameuser.card.equipment.UserCardZhiBaoService;
import com.bbw.god.gameuser.card.equipment.data.UserCardXianJue;
import com.bbw.god.gameuser.card.equipment.data.UserCardZhiBao;
import com.bbw.god.gameuser.card.equipment.rd.RdCardXianJueInfo;
import com.bbw.god.gameuser.card.equipment.rd.RdCardZhiBao;
import com.bbw.god.gameuser.leadercard.LeaderCardTool;
import com.bbw.god.gameuser.leadercard.service.LeaderCardService;
import com.bbw.god.gameuser.yaozu.YaoZuService;
import com.bbw.god.rd.RDCommon;
import com.bbw.god.rd.RDSuccess;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 玩家竞技调用接口
 *
 * @author suhq
 * @date 2019年3月14日 上午9:19:57
 */
@Slf4j
@RestController
public class FsFightCtrl extends AbstractController {
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private FsFightLogic fsFightLogic;
    @Autowired
    private ChanjieUserService chanjieUserService;
    @Autowired
    private ChanjieFightService chanjieService;
    @Autowired
    private GameTransmigrationService gameTransmigrationService;
    @Autowired
    private YaoZuService yaoZuService;
    @Autowired
    private NightmareMiXianService nightmareMiXianService;

    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private UserCardService userCardService;
    @Autowired
    private DfdjLogic dfdjLogic;
    @Autowired
    private LeaderCardService leaderCardService;
    @Autowired
    private UserCardXianJueService userCardXianJueService;
    @Autowired
    private UserCardZhiBaoService userCardZhiBaoService;
    @Autowired
    private ZxzCardInfoService zxzCardInfoService;
    @Autowired
    private OppCardService oppCardService;

    /**
     * 玩家登陆玩家竞技调用
     *
     * @param guId
     * @param bean
     * @param ticket
     * @param joinDate
     * @return
     */
    @GetMapping(CR.FsFight.GET_GU_INFO)
    public RDFsFighter listGuInfoForFsFight(long guId, String bean, String ticket, String joinDate) {
        RDFsFighter rd = fsFightLogic.getGuInfo(guId, bean, ticket, joinDate);
        return rd;
    }

    /**
     * 神仙大会匹配到机器人调用
     *
     * @param uid
     * @return
     */
    @GetMapping(CR.FsFight.GET_ROBOT_INFO)
    public RDFsRoboter gainRobotInfoForFsFight(long uid) {
        RDFsRoboter rdFsRoboter = fsFightLogic.getRobotInfo(uid);
        return rdFsRoboter;
    }

    /**
     * 批量机器人匹配
     *
     * @param uids
     * @return
     */
    @GetMapping(CR.FsFight.GET_ROBOTS_INFO)
    public RDFsRoboterList gainRobotsInfoForFsFight(String uids) {
        RDFsRoboterList rd = new RDFsRoboterList();
        List<Long> uidLongs = ListUtil.parseStrToLongs(uids);
        for (Long uid : uidLongs) {
            try {
                RDFsRoboter robotInfo = fsFightLogic.getRobotInfo(uid);
                rd.addRoboter(uid, robotInfo);
            } catch (Exception e) {
                log.error("批量获取机器人时，获取" + uid + "的机器人出现异常(未中断其他机器人匹配)", e);
            }
        }
        return rd;
    }

    /**
     * 同步购买灵石
     *
     * @param guId
     * @param gold
     * @param pros 购买产品的细腻，eg:810,2;820,2
     * @return
     */
    @GetMapping(CR.FsFight.SYNC_BUY)
    public RDSuccess syncBuy(long guId, int gold, String pros) {
        checkIp(guId);
        return fsFightLogic.syncBuy(guId, gold, pros);
    }

    /**
     * 神仙大会同步刷新卡牌
     *
     * @param guId
     * @param refreshCardNum
     * @param refreshTimes
     * @return
     */
    @GetMapping(CR.FsFight.SYNC_SXDH_CARD_REFRESH)
    public RDCommon syncSxdhCardRefresh(long guId, int refreshCardNum, int refreshTimes) {
        checkIp(guId);
        //放置客户端误传
        if (refreshTimes <= 0) {
            refreshTimes = 1;
        }
        return fsFightLogic.syncSxdhCardRefresh(guId, refreshCardNum, refreshTimes);
    }

    /**
     * 同步法宝，扣除战斗中使用的法宝
     *
     * @param guId
     * @param treasures
     * @return
     */
    @GetMapping(CR.FsFight.SYNC_TREASURES)
    public RDSuccess syncTreasuresForFight(long guId, String treasures) {
        checkIp(guId);
        return fsFightLogic.syncDeductTreasures(guId, treasures);
    }

    /**
     * 神仙大会成功匹配后同步门票
     *
     * @param guId
     * @return
     */
    @GetMapping(CR.FsFight.SYNC_TICKET)
    public RDSuccess syncTicket(long guId) {
        checkIp(guId);
        return fsFightLogic.syncTicket(guId);
    }

    /**
     * 神仙大会加入匹配队列前调用
     *
     * @param guId
     * @return
     */
    @GetMapping(CR.FsFight.TO_MATCH)
    public RDSuccess toMatch(long guId, int fightType) {
        checkIp(guId);
        FightTypeEnum fightTypeEnum = FightTypeEnum.fromValue(fightType);
        switch (fightTypeEnum) {
            case SXDH:
                return fsFightLogic.toMatch(guId);
            case CJDF:
                int gid = ServerTool.getServerGroup(gameUserService.getActiveSid(guId));
                return chanjieUserService.checkEligibility(guId, gid);
            case DFDJ:
                return dfdjLogic.checkEligibility(guId);
            default:
                throw new ExceptionForClientTip("fight.type.error");
        }
    }

    /**
     * 神仙大会结算
     *
     * @param
     * @return
     */
    @GetMapping(CR.FsFight.SUBMIT_SXDH_FIGHT_RESULT)
    public RDSxdhFightResult submitSxdhFightResult(CPFsFightSubmit param) throws UnsupportedEncodingException {
        long guId = param.getWinner();
        if (guId < 0) {
            guId = param.getLoser();
        }
        checkIp(guId);
        param.setExtra(URLDecoder.decode(param.getExtra(), "utf-8"));
        return fsFightLogic.submitSxdhFightResult(param);
    }

    @GetMapping(CR.FsFight.SUBMIT_DFDJ_FIGHT_RESULT)
    public RDDfdjFightResult submitDfdjFightResult(CPFsFightSubmit param) throws UnsupportedEncodingException {
        long guId = param.getWinner();
        if (guId < 0) {
            guId = param.getLoser();
        }
        checkIp(guId);
        return fsFightLogic.submitDfdjFightResult(param);
    }

    /**
     * 玩家竞技数据同步接口只限玩家竞技所在服务器访问
     *
     * @param guId
     */
    private void checkIp(long guId) {
        String ip = IpUtil.getIpAddr(request);
//        log.info(request.getContextPath() + ", ip = " + ip);
        CfgServerEntity server = Cfg.I.get(gameUserService.getActiveSid(guId), CfgServerEntity.class);
        if (!server.isDevTest()) {
            CfgServerGroup group = Cfg.I.get(server.getGroupId(), CfgServerGroup.class);
            if (group == null) {
                throw CoderException.high(String.format("未配置区服组信息（server_group_{}）", server.getGroupId()));
            }
            if (!group.getFsFightIps().contains(ip)) {
                throw CoderException.high(String.format("玩家%s非法的请求，IP=%s", guId, ip));
            }

        }
    }

    // 阐截战斗资格校验
    @RequestMapping(CR.FsFight.CHANJIE_CHECK_ELIGIBILITY)
    public ChanjieRd checkEligibility(long uid) {
        int gid = ServerTool.getServerGroup(gameUserService.getActiveSid(uid));
        return chanjieUserService.checkEligibility(uid, gid);
    }

    // 阐截 战斗结算
    @RequestMapping(CR.FsFight.CHANJIE_FIGHT_RESULT)
    public ChanjieRd fight(Long winnerUid, Integer winnerOnline, Long loserUid, Integer loserOnline) {
        ChanjieRd rd = new ChanjieRd();
        int gid = ServerTool.getServerGroup(gameUserService.getActiveSid(winnerUid));
        winnerOnline = winnerOnline == null ? 1 : winnerOnline;
        loserOnline = loserOnline == null ? 1 : loserOnline;
        rd.setAwards(chanjieService.submitChanJieFightResult(winnerUid, winnerOnline == 1, loserUid, loserOnline == 1, gid));
        return rd;
    }

    /**
     * 获得卡牌信息
     *
     * @param uid
     * @param cardId
     * @param fightType
     * @param extraParam 额外参数
     * @return
     */
    @RequestMapping(CR.FsFight.GET_CARD_INFO)
    public RDCardStrengthen getCardInfo(long uid, int cardId, int fightType,String extraParam) {
        //诛仙阵战斗查看卡牌需要特殊处理
        if (fightType == FightTypeEnum.ZXZ.getValue()) {
            return  zxzCardInfoService.getZxzCardInfo(uid,cardId,extraParam);
        }
        //诛仙阵四圣查看卡牌需要特殊处理
        if (fightType == FightTypeEnum.ZXZ_FOUR_SAINTS.getValue()) {
            return  zxzCardInfoService.getZxzFourSaintsCardInfo(uid,cardId,extraParam);
        }
        if (cardId == LeaderCardTool.getLeaderCardId()) {
            return leaderCardService.getCardFightInfo(uid);
        }
        RDCardStrengthen rd = new RDCardStrengthen();
        UserCard uc = null;
        if (uid > 0 && fightType != FightTypeEnum.CJDF.getValue()) {
            List<UserCard> cards = oppCardService.getOppAllCards(uid);
            uc =cards.stream().filter(tmp->tmp.getBaseId() == cardId).findFirst().orElse(null);
        }
        CfgCardEntity cc = CardTool.getCardById(cardId);
        if (uc == null) {
            //轮回大陆查看敌方卡牌详情
            if (fightType == FightTypeEnum.TRANSMIGRATION_FIGHT.getValue()) {
                return gameTransmigrationService.getCardInfo(cardId,extraParam);
            }
            //妖族查看敌方卡牌详情
            if (fightType == FightTypeEnum.YAOZU_FIGHT.getValue()) {
                return yaoZuService.getCardInfo(cardId,extraParam);
            }
            //梦魇迷仙洞查询敌方卡牌详情
            if (fightType == FightTypeEnum.MXD.getValue()) {
                return nightmareMiXianService.getCardInfo(cardId,extraParam);
            }
            rd.setCardId(cardId);
            rd.setSkill0(cc.getZeroSkill());
            rd.setSkill5(cc.getFiveSkill());
            rd.setSkill10(cc.getTenSkill());
            rd.setAttackSymbol(0);
            rd.setDefenceSymbol(0);
        } else {
            rd.setCardId(cardId);
            rd.setSkill0(uc.gainSkill0());
            rd.setSkill5(uc.gainSkill5());
            rd.setSkill10(uc.gainSkill10());
            rd.setAttackSymbol(uc.gainAttackSymbol());
            rd.setDefenceSymbol(uc.gainDefenceSymbol());
            rd.setIsUseSkillScroll(uc.ifUseSkillScroll() ? 1 : 0);
            //获得至宝穿戴信息
            List<UserCardZhiBao> userCardZhiBaos = userCardZhiBaoService.getUserCardZhiBaos(uid, cardId);
            if (ListUtil.isNotEmpty(userCardZhiBaos)) {
                rd.setZhiBaos(userCardZhiBaos.stream().map(RdCardZhiBao::instance).collect(Collectors.toList()));
            }
            //获得仙诀穿戴信息
            List<UserCardXianJue> userCardXianJues = userCardXianJueService.getUserCardXianJues(uid, cardId);
            if (ListUtil.isNotEmpty(userCardXianJues)) {
                rd.setXianJues(userCardXianJues.stream().map(RdCardXianJueInfo::instance).collect(Collectors.toList()));
            }
        }
        return rd;
    }

    @RequestMapping(CR.FsFight.GET_ALL_CARDS)
    public RDAllCards getAllCards() {
        RDAllCards rd = new RDAllCards();
        List<CfgCardEntity> allCards = CardTool.getAllCardsIncludeDeifyCards();
        allCards.forEach(tmp -> {
            RDFsFightCard rdCard = new RDFsFightCard();
            rdCard.setId(tmp.getId());
            Integer star = CardTool.getCardStarForFight(tmp.getId(), tmp.getStar());
            rdCard.setStar(star);
            rdCard.setAttack(tmp.getAttack());
            rdCard.setHp(tmp.getHp());
            rdCard.setGroup(tmp.getGroup());
            rdCard.setZero_skill(tmp.getZeroSkill());
            rdCard.setFive_skill(tmp.getFiveSkill());
            rdCard.setTen_skill(tmp.getTenSkill());
            if (tmp.getId() > 10000) {
                rd.addFsCard(rdCard);
            } else {
                rd.addCard(rdCard);
            }
        });
        return rd;
    }

}
