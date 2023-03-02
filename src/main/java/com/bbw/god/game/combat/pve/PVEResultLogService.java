package com.bbw.god.game.combat.pve;

import com.bbw.App;
import com.bbw.god.city.UserCityService;
import com.bbw.god.db.entity.InsGamePveDetailEntity;
import com.bbw.god.detail.async.PveDetailAsyncHandler;
import com.bbw.god.fight.FightTypeEnum;
import com.bbw.god.game.combat.CombatRedisService;
import com.bbw.god.game.combat.data.Combat;
import com.bbw.god.game.combat.data.CombatInfo;
import com.bbw.god.game.combat.data.Player;
import com.bbw.god.game.combat.video.service.CombatVideoService;
import com.bbw.god.game.config.city.CfgCityEntity;
import com.bbw.god.game.config.city.CityTool;
import com.bbw.god.gameuser.GameUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * pve明细记录服务
 *
 * @author: suhq
 * @date: 2021/11/2 4:31 下午
 */
@Slf4j
@Service
public class PVEResultLogService {
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private CombatVideoService videoService;
    @Autowired
    private UserCityService userCityService;
    @Autowired
    private CombatRedisService redisService;
    @Autowired
    private App app;
    @Autowired
    private PveDetailAsyncHandler pveDetailAsyncHandler;

    /**
     * 记录PVE战斗结果
     *
     * @param combat
     */
    public void logPVEResult(Combat combat) {
        if (app.runAsDevFZJ()) {
            return;
        }
        try {
            InsGamePveDetailEntity entity = new InsGamePveDetailEntity();
            entity.setId(combat.getId());
            Player userPlayer = combat.getP1();//默认 P1是玩家
            Player aiPlayer = combat.getP2();//默认AI 是P2
            entity.setFightType(combat.getFightType().getValue());
            if (userPlayer.getLv() >= 4 && userPlayer.getLv() <= 6) {
                //只记录4~6级的玩家
                entity.setIsAuto(combat.isAuto() ? 1 : 0);
                entity.setIsSkip(combat.isSkip() ? 1 : 0);
            }
            entity.setSid(gameUserService.getActiveSid(userPlayer.getUid()));
            entity.setAiLv(aiPlayer.getLv());
            entity.setLv(userPlayer.getLv());
            entity.setRound(combat.getRound());
            entity.setUid(userPlayer.getUid());
            entity.setUseWeapon(userPlayer.getUserWeaponIdStr());
            entity.setResultType(combat.getCombatResultType().getVal());
            entity.setIsWin(combat.getWinnerId() == 1 ? 1 : 0);
            if (aiPlayer.getCardFromUid() > 0) {
                entity.setIsPlayer(1);
            }
            if (FightTypeEnum.ATTACK.equals(combat.getFightType()) || FightTypeEnum.TRAINING.equals(combat.getFightType()) || FightTypeEnum.PROMOTE.equals(combat.getFightType())) {
                CombatInfo combatInfo = redisService.getCombatInfo(combat.getId());
                // 设置城池名称和ID
                CfgCityEntity city = CityTool.getCityById(combatInfo.getCityId());
                entity.setSiteId(city.getId());
                entity.setSite(city.getName());
                entity.setCityHv(combatInfo.getCityHv());
                entity.setCityLv(city.getLevel());
                int seq = 0;
                if (combat.getFightType().equals(FightTypeEnum.ATTACK)) {
                    seq = userCityService.getOwnCityNumAsLevel(userPlayer.getUid(), city.getLevel());
                } else if (FightTypeEnum.PROMOTE.equals(combat.getFightType())) {
                    int newHv = entity.getCityHv() + entity.getIsWin();
                    seq = userCityService.getCityNumAsHierarchy(userPlayer.getUid(), city.getLevel(), newHv);
                    entity.setCityHv(newHv);
                }
                // 设置当前级别的第几座城池
                entity.setCityOrder(seq);
            }
            pveDetailAsyncHandler.log(entity, combat);
            if (FightTypeEnum.needLogToPVEDetail(combat.getFightType())) {
                videoService.saveMonitor(entity.getId(), combat, userPlayer.getUid());
            }
        } catch (Exception e) {
            log.error("PVE战斗记录保存失败！如果是本地练兵请忽略，当前战斗类型：" + combat.getFightType().getName());
            log.error(e.getMessage());
        }
    }

    /**
     * 保存封神台记录
     *
     * @param combat
     * @param isGameFst
     */
    public void logPVEResultForFst(Combat combat, boolean isGameFst) {
        if (app.runAsDevFZJ()) {
            return;
        }
        try {
            InsGamePveDetailEntity entity = new InsGamePveDetailEntity();
            entity.setId(combat.getId());
            Player userPlayer = combat.getP1();//默认 P1是玩家
            Player aiPlayer = combat.getP2();//默认AI 是P2
            entity.setFightType(combat.getFightType().getValue());
            entity.setSid(gameUserService.getActiveSid(userPlayer.getUid()));
            entity.setAiLv(aiPlayer.getLv());
            entity.setLv(userPlayer.getLv());
            entity.setRound(combat.getRound());
            entity.setUid(userPlayer.getUid());
            entity.setResultType(combat.getCombatResultType().getVal());
            entity.setIsWin(combat.getWinnerId() == 1 ? 1 : 0);
            if (aiPlayer.getCardFromUid() > 0) {
                entity.setIsPlayer(1);
            }
            pveDetailAsyncHandler.log(entity, combat, isGameFst);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
