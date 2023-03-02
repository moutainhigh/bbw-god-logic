package com.bbw.god.game.combat;

import com.bbw.db.redis.RedisValueUtil;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.fight.FightTypeEnum;
import com.bbw.god.fight.processor.YeGFightProcessor;
import com.bbw.god.game.combat.data.Combat;
import com.bbw.god.game.combat.data.CombatInfo;
import com.bbw.god.game.combat.data.Player;
import com.bbw.god.game.combat.data.PlayerId;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 战斗常用方法
 *
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-06-18 16:07
 */
@Service
@Slf4j
public class CombatRedisService {

    @Autowired
    private RedisValueUtil<Combat> combatRedis;// 战斗数据 即为每回合的实时数据
    @Autowired
    private RedisValueUtil<CombatInfo> combatInfoRedis;// 战斗初始化信息

    @Autowired
    private YeGFightProcessor yeGFightProcessor;
    private static final String COMBAT_KEY = "combat:";
    private static final String COMBAT_INFO_KEY = "combatInfo:";

    /**
     * 保存战斗数据
     * @param combat
     * @return
     */
    public Combat save(Combat combat) {
        String key = COMBAT_KEY+combat.getId();
        try{
            combatRedis.set(key, combat);
        }catch (Exception e){
            combatRedis.set(key, combat);
        }
        if (combat.hadEnded()) {
            combatRedis.expire(key, 5, TimeUnit.MINUTES);
            String infoKey = COMBAT_INFO_KEY+combat.getId();
            combatInfoRedis.expire(infoKey, 5, TimeUnit.MINUTES);
        } else {
            combatRedis.expire(key, 3, TimeUnit.HOURS);
        }
        return combat;
    }


    /**
     * 保存战斗初始数据
     * @param combat
     * @return
     */
    public void saveCombatInfo(CombatInfo info) {
        String key = COMBAT_INFO_KEY+info.getId();
        try{
            combatInfoRedis.set(key, info);
        }catch (Exception e){
            combatInfoRedis.set(key, info);
        }
        combatRedis.expire(key, 3, TimeUnit.HOURS);
    }

    /**
     * 获取战斗信息
     * @param combatId
     * @return
     */
    public Combat get(long combatId) {
        String key = COMBAT_KEY+combatId;
        Combat combat = combatRedis.get(key);
        int num=10;
        while (combat==null && num>0){
            combat = combatRedis.get(key);
            num--;
        }
        if (null == combat) {
            throw new ExceptionForClientTip("fight.not.exist");
        }
        return combat;
    }

    /**
     * 获取战斗初始信息
     * @param combatId
     * @return
     */
    public CombatInfo getCombatInfo(long combatId) {
        String key = COMBAT_INFO_KEY+combatId;
        CombatInfo combat = combatInfoRedis.get(key);
        if (null == combat) {
            combat = combatInfoRedis.get(key);
        }
        return combat;
    }



    public void escape(int type, long uid) {
        if (FightTypeEnum.YG.getValue() == type) {
            yeGFightProcessor.takeYeGtoYouG(uid);
        }
    }



    /**
     * 获取双方玩家打掉的卡牌和血量
     *
     * @param combatId
     * @return
     */
    public List<CombatResInfo> combatResult(long combatId) {
        Combat combat = get(combatId);
        List<CombatResInfo> list = new ArrayList<>();
        if (combat==null){
            return list;
        }
        List<PlayerId> ids = Arrays.asList(PlayerId.P2, PlayerId.P1);
        for (PlayerId pid : ids) {
            Player p1 = combat.getPlayer(pid);
            CombatResInfo info = new CombatResInfo();
            long uid = 0l;
            if (pid.equals(PlayerId.P1)) {
                uid = combat.getPlayer(PlayerId.P2).getUid();
            } else {
                uid = combat.getPlayer(PlayerId.P1).getUid();
            }
            info.setUid(uid);
            info.setOppoLostCard(p1.getLoseCardNum());
            info.setOppoLostHp(p1.getMaxHp() - p1.getHp());
            list.add(info);
        }
        return list;
    }


}
