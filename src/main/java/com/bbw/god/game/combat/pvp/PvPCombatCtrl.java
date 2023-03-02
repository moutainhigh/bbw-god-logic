package com.bbw.god.game.combat.pvp;

import com.bbw.common.JSONUtil;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.fight.FightTypeEnum;
import com.bbw.god.game.combat.CombatRedisService;
import com.bbw.god.game.combat.data.Combat;
import com.bbw.god.game.combat.data.PlayerId;
import com.bbw.god.game.combat.data.RDCombat;
import com.bbw.god.game.combat.data.RDTempResult;
import com.bbw.god.game.combat.pve.PVEResultService;
import com.bbw.god.game.combat.pvp.PvPCombatParam.UptoCard;
import com.bbw.god.game.combat.video.service.CombatVideoService;
import com.bbw.god.game.combat.weapon.WeaponLogic;
import com.bbw.god.rd.RDCommon;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lwb
 * @version 1.0
 * @date 2019年8月25日
 */
@Slf4j
@RestController
public class PvPCombatCtrl {
    @Autowired
    private CombatRedisService combatService;
    @Autowired
    private PVPService pVPService;
    @Autowired
    private WeaponLogic weaponLogic;
    @Autowired
    private CombatVideoService videoService;
    @Value("${bbw-god.open-useWeapon:false}")
    private boolean isOpenWeaponCJ;// 是否开放阐截斗法法宝使用
    @Autowired
    private PVEResultService pveResultService;

    /**
     * 请求下一回合
     *
     * @param combatId
     * @param first
     * @param second
     * @return
     */
    @RequestMapping("combatPVP!pvpNextRound")
    public RDPVPcombat nextRound(long combatId, String first, String second) throws UnsupportedEncodingException {
        long begin = System.currentTimeMillis();
        first = URLDecoder.decode(first, "utf-8");
        second = URLDecoder.decode(second, "utf-8");
//        log.error(first);
//        log.error(second);
        UptoCard p1 = JSONUtil.fromJson(first, PvPCombatParam.UptoCard.class);
        UptoCard p2 = JSONUtil.fromJson(second, PvPCombatParam.UptoCard.class);
        Combat combat = combatService.get(combatId);
        if (combat == null) {
            log.error("combatPVP!pvpNextRound无效的战斗,combatId:" + combatId + ",first:" + first + ",second:" + second);
            throw ExceptionForClientTip.fromMsg("无效的回合数据");
        }
        if (combat.hadEnded()) {
            return new RDPVPcombat(combat);
        }
        int round = combat.getRound();
        pVPService.nextRound(combat, p1, p2);
        if (combat.hadEnded()) {
            pveResultService.checkCombatAchievement(combat);
            pveResultService.checkResultEvent(combat);
        }
        RDPVPcombat rdpvp = new RDPVPcombat(combat);
        combatService.save(combat);
        long end = System.currentTimeMillis();
        long time = end - begin;
        if (time > 100) {
            log.info("[回合]业务耗时:" + time);
        }
        videoService.addRoundData(combat, round);
        return rdpvp;
    }

    /**
     * 客户端使用法宝
     *
     * @param combatId
     * @param playingId
     * @param wid
     * @param pos
     * @return
     */
    @GetMapping("combat!pvpUseWeapon")
    public RDTempResult useWeapon(long combatId, long playingId, int wid, @RequestParam(defaultValue = "-1") String pos,
                                  Integer round) {
        Combat combat = combatService.get(combatId);
        if (combat.getFightType().equals(FightTypeEnum.CJDF) && !isOpenWeaponCJ) {
            throw new ExceptionForClientTip("combat.weapon.not.open");
        }
        if (round == null || round != combat.getRound()) {
            // 法宝期望使用的回合与当前回合不符合，则不生效该次请求
            throw new ExceptionForClientTip("combat.weapon.timeout");
        }
        List<Integer> targetPos = new ArrayList<Integer>();
        try {
            if (!pos.contains("-1")) {
                String[] poses = pos.split("N");
                for (String str : poses) {
                    targetPos.add(Integer.parseInt(str));
                }
            }
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new ExceptionForClientTip("combat.player.use.weapon.fail",wid);
        }
        PlayerId playerId = combat.getPlayerByUid(playingId).getId();
        if (playerId == PlayerId.P2) {
            List<Integer> newPos = new ArrayList<Integer>();
            for (Integer mpos : targetPos) {
                // 坐标转换
                int npos = mpos > 1000 ? mpos - 1000 : mpos + 1000;
                newPos.add(npos);
            }
            targetPos = newPos;
        }
        RDTempResult rd = weaponLogic.useWeapon(combat, playerId, wid, targetPos);
        combatService.save(combat);
        return rd;
    }

    /**
     * 强联网请求使用法宝（特殊法宝=>需要变更卡牌的）
     *
     * @param combatId
     * @param playingId
     * @param wid
     * @param pos
     * @return
     */
    @GetMapping("combatPVP!useWeapon")
    public RDPVPcombat useWeaponByNet(long combatId, long playingId, int wid,
                                      @RequestParam(defaultValue = "-1") String pos, Integer round) {
        Combat combat = combatService.get(combatId);
        if (combat.getFightType().equals(FightTypeEnum.CJDF) && !isOpenWeaponCJ) {
            throw new ExceptionForClientTip("combat.weapon.not.open");
        }
        if (round == null || round != combat.getRound()) {
            // 法宝期望使用的回合与当前回合不符合，则不生效该次请求
            throw new ExceptionForClientTip("combat.weapon.timeout");
        }
        List<Integer> targetPos = new ArrayList<Integer>();
        try {
            if (!pos.contains("-1")) {
                String[] poses = pos.split("N");
                for (String str : poses) {
                    targetPos.add(Integer.parseInt(str));
                }
            }
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new ExceptionForClientTip("combat.player.use.weapon.fail",wid);
        }
        PlayerId playerId = combat.getPlayerByUid(playingId).getId();
        if (playerId == PlayerId.P2) {
            List<Integer> newPos = new ArrayList<Integer>();
            for (Integer mpos : targetPos) {
                // 坐标转换
                int npos = mpos > 1000 ? mpos - 1000 : mpos + 1000;
                newPos.add(npos);
            }
            targetPos = newPos;
        }
        RDTempResult rd = weaponLogic.useWeapon(combat, playerId, wid, targetPos);
        rd.setPlayingId(playingId);
        rd.setWid(wid);
        combatService.save(combat);
        long oppuid = combat.getOppoPlayerByUid(playingId).getUid();
        RDPVPcombat rds = RDPVPcombat.fromWeaponToAddCard(rd, oppuid);
        videoService.addPvPTrRoundData(rds, combat, playerId);
        return rds;
    }

    /**
     * 认输
     *
     * @param combatId
     * @param winnerId
     * @return
     */
    @GetMapping("combatPVP!surrender")
    public RDCommon combatSurrender(long combatId, long winnerId) {
        Combat combat = combatService.get(combatId);
        int win = combat.getPlayerByUid(winnerId).getId().getValue();
        combat.setWinnerId(win);
        combatService.save(combat);
        PlayerId winid = combat.getPlayerByUid(winnerId).getId();
        PlayerId loser = winid.equals(PlayerId.P1) ? PlayerId.P2 : PlayerId.P1;
        String name = combat.getPlayer(loser).getName();
        videoService.addSurrender(combat.getRound(), combat.getId(), name);
        pveResultService.checkResultEvent(combat);
        return new RDCommon();
    }

    /**
     * 数据恢复
     *
     * @param combatId
     * @param uid
     * @return
     */
    @GetMapping("combatPVP!recoverAttack")
    public RDCombat recoverCombat(long combatId, long uid) {
        Combat combat = combatService.get(combatId);
        if (combat.getPlayerByUid(uid).getId() == PlayerId.P1) {
            RDCombat rdc = RDCombat.fromCombat(combat);
            return rdc;
        }

        RDCombat rdc = RDCombat.getP2fromCombat(combat);
        return rdc;
    }
}
