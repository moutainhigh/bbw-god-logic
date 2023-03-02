package com.bbw.god.game.combat.pve;

import com.bbw.common.Rst;
import com.bbw.common.lock.SyncLockUtil;
import com.bbw.exception.CoderException;
import com.bbw.god.controller.AbstractController;
import com.bbw.god.game.CR;
import com.bbw.god.game.combat.CombatRedisService;
import com.bbw.god.game.combat.data.Combat;
import com.bbw.god.game.combat.data.RDCombat;
import com.bbw.god.game.combat.data.RDTempResult;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.achievement.UserAchievementInfo;
import com.bbw.god.gameuser.achievement.UserAchievementLogic;
import com.bbw.god.gameuser.guide.v1.NewerGuideCombatLogic;
import com.bbw.god.rd.RDCommon;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

/**
 * 战斗控制器
 *
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-07-05 00:40
 */
@Slf4j
@RestController
public class PVECombatCtrl extends AbstractController {
    @Autowired
    private CombatRedisService combatService;
    @Autowired
    private PVELogic pveLogic;
    @Autowired
    private NewerGuideCombatLogic newerGuideCombatLogic;
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private SyncLockUtil syncLockUtil;
    @Autowired
    private UserAchievementLogic userAchievementLogic;
    @Autowired
    private PVEFightTaskLogic pveFightTaskLogic;

    /**
     * 发起战斗
     *
     * @param type       战斗类型
     * @param opponentId 对手Id
     * @return
     */
    @GetMapping(CR.CombatPVE.ATTACK)
    public RDCombat combatAttackCity(int type, @RequestParam(defaultValue = "-1") long opponentId, Integer newerGuide, Long fightTaskId) {
        long begin = System.currentTimeMillis();
        RDCombat rdc = null;
        if (null != newerGuide) {
            rdc = newerGuideCombatLogic.initFightData(type, getUserId(), opponentId, newerGuide);
        } else if (null != fightTaskId && fightTaskId > 0) {
            rdc = pveFightTaskLogic.initFightData(type, getUserId(), fightTaskId);
        } else {
            rdc = pveLogic.initFightData(type, getUserId(), opponentId, false);
        }
        long timeMillis = System.currentTimeMillis() - begin;
        if (timeMillis > 500) {
            log.info("[初始化战斗]业务耗时:" + timeMillis);
        }
        return rdc;
    }

    /**
     * 再战
     *
     * @param type 战斗类型
     * @return
     */
    @GetMapping(CR.CombatPVE.AGAIN)
    public RDCombat combatAgainAttackCity(int type) {
        long begin = System.currentTimeMillis();
        RDCombat rdc = pveLogic.initFightData(type, getUserId(), -1, true);
        long timeMillis = System.currentTimeMillis() - begin;
        if (timeMillis > 500) {
            log.info("[再战初始化战斗]业务耗时:" + timeMillis);
        }
        return rdc;
    }

    /**
     * 速战
     *
     * @param combatId
     * @return
     */
    @GetMapping(CR.CombatPVE.RAPID_STRIKE)
    public RDCombat aotuEndCombat(long combatId, @RequestParam(defaultValue = "") String moveToBattle) {
        long begin = System.currentTimeMillis();
        RDCombat rdc = pveLogic.rapidStrike(combatId, getUserId(), moveToBattle);
        long timeMillis = System.currentTimeMillis() - begin;
        if (timeMillis > 500) {
            log.info("速战" + rdc.getRound() + "回合，耗时：" + timeMillis + "毫秒");
        }
        return rdc;
    }

    /**
     * 使用法宝
     *
     * @param combatId
     * @param wid
     * @param pos
     * @return
     */
    @GetMapping(CR.CombatPVE.USE_WEAPON)
    public RDTempResult useWeapon(long combatId, int wid, @RequestParam(defaultValue = "-1") String pos) {
        return pveLogic.useWeapon(combatId, wid, pos);
    }

    /**
     * 下一回合
     *
     * @param combatId
     * @param moveToBattle
     * @return
     */
    @RequestMapping(CR.CombatPVE.NEXT_ROUND)
    public RDCombat nextRound(long combatId, String moveToBattle, @RequestParam(defaultValue = "0") int autoDeploy, Integer round) {
        long begin = System.currentTimeMillis();
        RDCombat rdc = pveLogic.nextRound(combatId, getUserId(), autoDeploy, moveToBattle, round);
        long timeMillis = System.currentTimeMillis() - begin;
        if (timeMillis > 500) {
            log.info("[回合]业务耗时:" + timeMillis);
        }
        return rdc;
    }

    /**
     * 恢复数据
     *
     * @param combatId
     * @return
     */
    @GetMapping(CR.CombatPVE.RECOVER_DATA)
    public RDCombat recoverCombat(long combatId) {
        Combat combat = combatService.get(combatId);
        RDCombat rdc = RDCombat.fromCombat(combat);
        rdc.getP1().setSpecialCars(combat.getP1().getSpecialCards());
        rdc.getP2().setSpecialCars(combat.getP2().getSpecialCards());
        if (combat.hadEnded()) {
            rdc.setResult(combat.getResult());
        }
        log.info("玩家【" + getUserId() + "】恢复数据,回合" + combat.getRound());
        return rdc;
    }

    /**
     * 投降
     *
     * @param combatId
     * @return
     */
    @GetMapping(CR.CombatPVE.SURRENDER)
    public RDTempResult combatSurrender(long combatId) {
        return pveLogic.surrender(combatId, getUserId());
    }

    /**
     * 逃跑 目前逃跑接口 只用于打野怪 其他类型的战斗未实现对应的方法
     *
     * @param type
     * @return
     */
    @GetMapping(CR.CombatPVE.ESCAPE)
    public RDCommon escape(int type) {
        combatService.escape(type, getUserId());
        return new RDCommon();
    }

    @GetMapping(CR.CombatPVE.ACCOMPLISH_ACHIEVEMENT)
    public Rst accomplishAchievement(int achievementId) {
        long uid = getUserId();
        List<Integer> validIds = Arrays.asList(14960, 14970);
        if (!validIds.contains(achievementId)) {
            String msg = String.format("成就完成接口被调用，非法的成就id=%s，uid=%s", achievementId, uid);
            throw CoderException.high(msg);
        }
        UserAchievementInfo info = gameUserService.getSingleItem(uid, UserAchievementInfo.class);
        if (null == info) {
            info = (UserAchievementInfo) syncLockUtil.doSafe(String.valueOf(uid), tmp -> {
                UserAchievementInfo userAchievementInfo = gameUserService.getSingleItem(uid, UserAchievementInfo.class);
                if (null == userAchievementInfo) {
                    userAchievementInfo = userAchievementLogic.initUserAchievementInfo(uid);
                    gameUserService.addItem(uid, userAchievementInfo);
                }
                return userAchievementInfo;
            });
        }
        info.accomplishAchievement(achievementId);
        gameUserService.updateItem(info);
        return Rst.businessOK();
    }
}
