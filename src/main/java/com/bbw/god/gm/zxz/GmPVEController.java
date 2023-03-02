package com.bbw.god.gm.zxz;

import com.bbw.common.lock.SyncLockUtil;
import com.bbw.god.game.CR;
import com.bbw.god.game.combat.CombatRedisService;
import com.bbw.god.game.combat.data.RDCombat;
import com.bbw.god.game.combat.data.RDTempResult;
import com.bbw.god.game.combat.pve.PVEFightTaskLogic;
import com.bbw.god.game.combat.pve.PVELogic;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.achievement.UserAchievementLogic;
import com.bbw.god.gameuser.guide.v1.NewerGuideCombatLogic;
import com.bbw.god.gameuser.treasure.CPUseTreasure;
import com.bbw.god.gameuser.treasure.UserTreasureLogic;
import com.bbw.god.rd.RDCommon;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 战斗模拟接口
 * @author: hzf
 * @create: 2022-09-28 23:06
 **/
@Slf4j
@RestController
public class GmPVEController {
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

    @Autowired
    private UserTreasureLogic treasureLogic;
    private long uid = 220601009600038L;

    @GetMapping("gm/combat!attackCity")
    public RDCombat combatAttackCitya(int type, @RequestParam(defaultValue = "-1") long opponentId, Integer newerGuide, Long fightTaskId) {
        long begin = System.currentTimeMillis();
        RDCombat rdc = null;
        if (null != newerGuide) {
            rdc = newerGuideCombatLogic.initFightData(type, uid, opponentId, newerGuide);
        } else if (null != fightTaskId && fightTaskId > 0) {
            rdc = pveFightTaskLogic.initFightData(type, uid, fightTaskId);
        } else {
            rdc = pveLogic.initFightData(type, uid, opponentId, false);
        }
        long timeMillis = System.currentTimeMillis() - begin;
        if (timeMillis > 500) {
        }
        return rdc;
    }
    /**
     * 速战
     *
     * @param combatId
     * @return
     */
    @GetMapping("gm/combat!autoEndCombat")
    public RDCombat aotuEndCombat(long combatId, @RequestParam(defaultValue = "") String moveToBattle) {
        long begin = System.currentTimeMillis();
        RDCombat rdc = pveLogic.rapidStrike(combatId, uid, moveToBattle);
        long timeMillis = System.currentTimeMillis() - begin;
        if (timeMillis > 500) {
            log.info("速战" + rdc.getRound() + "回合，耗时：" + timeMillis + "毫秒");
        }
        return rdc;
    }
    /**
     * 投降
     *
     * @param combatId
     * @return
     */
    @GetMapping("gm/combat!surrender")
    public RDTempResult combatSurrender(long combatId) {
        return pveLogic.surrender(combatId, uid);
    }
    @GetMapping("gm/treasure!useTreasure")
    public RDCommon useTreasure(CPUseTreasure param) {
        return treasureLogic.useMapTreasure(uid, param);
    }

}
