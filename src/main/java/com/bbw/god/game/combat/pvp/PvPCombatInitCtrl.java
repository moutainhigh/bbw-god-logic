package com.bbw.god.game.combat.pvp;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bbw.common.DateUtil;
import com.bbw.common.HttpRequestUtil;
import com.bbw.common.JSONUtil;
import com.bbw.god.cache.TimeLimitCacheUtil;
import com.bbw.god.fight.FightTypeEnum;
import com.bbw.god.game.chanjie.service.ChanjieUserService;
import com.bbw.god.game.combat.CombatRedisService;
import com.bbw.god.game.combat.data.Combat;
import com.bbw.god.game.combat.video.service.CombatVideoService;
import com.bbw.god.game.config.server.ServerTool;
import com.bbw.god.game.dfdj.config.DfdjRankType;
import com.bbw.god.game.dfdj.fight.DfdjFightCache;
import com.bbw.god.game.dfdj.rank.DfdjRankService;
import com.bbw.god.game.dfdj.zone.DfdjZone;
import com.bbw.god.game.dfdj.zone.DfdjZoneService;
import com.bbw.god.game.sxdh.SxdhMechineService;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.card.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lwb
 * @version 1.0
 * @date 2019年8月25日
 */
@Slf4j
@RestController
public class PvPCombatInitCtrl {
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private CombatRedisService combatService;
    @Autowired
    private CombatPVPInitService combatPVPInitService;
    @Autowired
    private ChanjieUserService chanjieUserService;
    @Autowired
    private CombatVideoService videoService;
    @Autowired
    private SxdhMechineService sxdhMechineService;
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private UserCardGroupService userCardGroupService;
    @Autowired
    private UserCardService userCardService;
    @Autowired
    private DfdjRankService dfdjRankService;
    @Autowired
    private DfdjZoneService dfdjZoneService;

    /**
     * 初始化战斗数据
     *
     * @return
     */
    @RequestMapping("combatPVP!pvpInit")
    public RDPVPcombat combatInit() {
        long begin = System.currentTimeMillis();
        CPPVPInit initParam = parsePvpInitParam();
        if (initParam.fightType == FightTypeEnum.CJDF.getValue()) {
            initParam.p1.setHeadImg(1);
            initParam.p2.setHeadImg(1);
            if (!DateUtil.isWeekDay(7)) {
                initParam.p1.setNickname(chanjieUserService.getUserInfo(initParam.p1.getUid()).getHeadName());
                initParam.p2.setNickname(chanjieUserService.getUserInfo(initParam.p2.getUid()).getHeadName());
            } else {
                initParam.p1.setNickname("封神召唤师");
                initParam.p2.setNickname("封神召唤师");
            }
        } else {
            initParam.p1.setUseTreasures(sxdhMechineService.getMechinesToUse(initParam.p1.getUid(), initParam.roomId));
            initParam.p2.setUseTreasures(sxdhMechineService.getMechinesToUse(initParam.p2.getUid(), initParam.roomId));
        }
        Combat combat = combatPVPInitService.initCombat(initParam.p1, initParam.p2, initParam.fightType);
        combatService.save(combat);
        RDPVPcombat rdpvp = new RDPVPcombat(combat);
        long end = System.currentTimeMillis();
        long time = end - begin;
        if (time > 500) {
            log.info("[初始化战斗]业务耗时:" + time);
        }
        videoService.addRoundData(combat, 0);
        return rdpvp;
    }

    @RequestMapping("combatPVP!pvpInitByUserCardGroup")
    public RDPVPcombat pvpInitByUserCardGroup() {
        long begin = System.currentTimeMillis();

        CPPVPInit initParam = parsePvpInitParam();
        supplementInfo(initParam.p1, initParam.p1.getCardGroupId());
        supplementInfo(initParam.p2, initParam.p2.getCardGroupId());
        Combat combat = combatPVPInitService.initCombat(initParam.p1, initParam.p2, initParam.fightType);
        combatService.save(combat);
        RDPVPcombat rdpvp = new RDPVPcombat(combat);
        long end = System.currentTimeMillis();
        long time = end - begin;
        if (time > 500) {
            log.info("[初始化战斗]业务耗时:" + time);
        }
        videoService.addRoundData(combat, 0);
        return rdpvp;
    }

    @RequestMapping("combatPVP!pvpInitAsDfdj")
    public RDPVPcombat pvpInitAsDfdj() {
        long begin = System.currentTimeMillis();
        CPPVPInit initParam = parsePvpInitParam();
        UserCardGroup group1 = userCardGroupService.getUserCardGroups(initParam.p1.getUid(), CardGroupWay.DFDJ_FIGHT).stream().findFirst().orElse(null);
        UserCardGroup group2 = userCardGroupService.getUserCardGroups(initParam.p2.getUid(), CardGroupWay.DFDJ_FIGHT).stream().findFirst().orElse(null);
        supplementInfo(initParam.p1, group1.getId());
        supplementInfo(initParam.p2, group2.getId());
        Combat combat = combatPVPInitService.initCombat(initParam.p1, initParam.p2, initParam.fightType);
        combatService.save(combat);
        DfdjZone zone = dfdjZoneService.getCurOrLastZone(initParam.p1.getUid());
        int score1 = dfdjRankService.getScore(zone, DfdjRankType.PHASE_RANK, initParam.p1.getUid());
        int score2 = dfdjRankService.getScore(zone, DfdjRankType.PHASE_RANK, initParam.p2.getUid());
        int rank1 = dfdjRankService.getRank(zone, DfdjRankType.PHASE_RANK, initParam.p1.getUid());
        int rank2 = dfdjRankService.getRank(zone, DfdjRankType.PHASE_RANK, initParam.p2.getUid());
        DfdjFightCache dfdjFightCache = new DfdjFightCache(initParam.p1.getUid(), score1, rank1, initParam.p2.getUid(), score2, rank2);
        TimeLimitCacheUtil.setDfdjFightCache(initParam.p1.getUid(), dfdjFightCache);
        TimeLimitCacheUtil.setDfdjFightCache(initParam.p2.getUid(), dfdjFightCache);
        RDPVPcombat rdpvp = new RDPVPcombat(combat);
        long end = System.currentTimeMillis();
        long time = end - begin;
        if (time > 500) {
            log.info("[初始化战斗]业务耗时:" + time);
        }
        videoService.addRoundData(combat, 0);
        return rdpvp;
    }

    private void supplementInfo(PvPCombatParam param, long cardGroupId) {
        GameUser gameUser = gameUserService.getGameUser(param.getUid());
        String shortName = ServerTool.getServerShortName(gameUser.getServerId());
        param.setNickname(shortName + "-" + gameUser.getRoleInfo().getNickname());
        param.setLv(gameUser.getLevel());
        param.setHeadImg(gameUser.getRoleInfo().getHead());
        UserCardGroup group = userCardGroupService.getUserCardGroupById(param.getUid(), cardGroupId);
        List<PvPCombatParam.PvpCard> pvpCards = new ArrayList<>();
        for (Integer cardId : group.getCards()) {
            UserCard userCard = userCardService.getUserCard(param.getUid(), cardId);
            if (userCard != null) {
                pvpCards.add(PvPCombatParam.PvpCard.instance(userCard));
            }
        }
        param.setCards(pvpCards);
    }

    private CPPVPInit parsePvpInitParam() {
        String body = HttpRequestUtil.getRequestBody(request);
        JSONObject params = JSON.parseObject(body);
        String first = params.getString("first");
        String second = params.getString("second");
        int fightType = params.getIntValue("type");
        int roomId = params.getIntValue("roomId");
        PvPCombatParam p1 = JSONUtil.fromJson(first, PvPCombatParam.class);
        PvPCombatParam p2 = JSONUtil.fromJson(second, PvPCombatParam.class);
        return new CPPVPInit(p1, p2, roomId, fightType);
    }

    @Getter
    private static class CPPVPInit {
        private PvPCombatParam p1;
        private PvPCombatParam p2;
        private Integer roomId;
        private Integer fightType;

        public CPPVPInit(PvPCombatParam p1, PvPCombatParam p2, int roomId, int fightType) {
            this.p1 = p1;
            this.p2 = p2;
            this.roomId = roomId;
            this.fightType = fightType;
        }
    }
}
