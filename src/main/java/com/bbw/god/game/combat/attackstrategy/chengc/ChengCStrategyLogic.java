package com.bbw.god.game.combat.attackstrategy.chengc;

import com.bbw.common.ListUtil;
import com.bbw.common.StrUtil;
import com.bbw.god.cache.TimeLimitCacheUtil;
import com.bbw.god.city.UserCityService;
import com.bbw.god.city.chengc.ChengChiInfoCache;
import com.bbw.god.db.entity.AttackCityStrategyEntity;
import com.bbw.god.db.service.AttackCityStrategyService;
import com.bbw.god.detail.async.StrategyLogAsyncHandler;
import com.bbw.god.fight.FightTypeEnum;
import com.bbw.god.game.combat.attackstrategy.StrategyConfig;
import com.bbw.god.game.combat.attackstrategy.StrategyEnum;
import com.bbw.god.game.combat.attackstrategy.StrategyVO;
import com.bbw.god.game.combat.attackstrategy.service.AbstractStrategyLogic;
import com.bbw.god.game.combat.data.Combat;
import com.bbw.god.game.combat.data.CombatInfo;
import com.bbw.god.game.combat.data.Player;
import com.bbw.god.game.combat.video.CombatVideo;
import com.bbw.god.game.combat.video.RDVideo;
import com.bbw.god.game.combat.video.service.CombatVideoService;
import com.bbw.god.game.config.WorldType;
import com.bbw.god.game.config.city.CfgCityEntity;
import com.bbw.god.game.config.city.CityTool;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.oss.OSSService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 攻城攻略
 *
 * @author: suhq
 * @date: 2021/9/22 10:45 上午
 */
@Slf4j
@Service
public class ChengCStrategyLogic extends AbstractStrategyLogic {

    @Autowired
    private AttackCityStrategyService attackCityStrategyService;
    @Autowired
    private CombatVideoService combatVideoService;
    @Autowired
    private ChengCStrategyService chengCStrategyService;
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private UserCityService userCityService;
    @Autowired
    private StrategyLogAsyncHandler strategyLogAsyncHandler;


    @Override
    public boolean matchFight(FightTypeEnum fightType) {
        return fightType == FightTypeEnum.ATTACK;
    }

    @Override
    protected boolean isToSave(Combat combat, CombatInfo combatInfo) {
        int strategyLv = StrategyConfig.CITY_MIN_LV;
        if (combatInfo.getWorldType() != WorldType.NORMAL.getValue()) {
            strategyLv = StrategyConfig.NIGHTMARE_CITY_MIN_LV;
        }
        if (combatInfo.getCityLevel() < strategyLv) {
            return false;
        }
        // 设置城池名称和ID
        Player user = combat.getP1();
        if (user.getUid() < 0) {
            return false;
        }
        ChengChiInfoCache cache = TimeLimitCacheUtil.getChengChiInfoCache(user.getUid());
        if (!cache.isOwnCity() && combatInfo.getWorldType() == WorldType.NORMAL.getValue()) {
            //不是攻城
            return false;
        }
        return true;
    }

    @Override
    protected void doSave(Combat combat, CombatInfo combatInfo) {
        Player user = combat.getP1();
        CfgCityEntity city = CityTool.getCityById(combatInfo.getCityId());
        Integer seq = null;
        if (combatInfo.getWorldType() != WorldType.NORMAL.getValue()) {
            seq = userCityService.getOwnNightmareCityNumAsLevel(user.getUid(), city.getLevel());
        } else {
            seq = userCityService.getOwnCityNumAsLevel(user.getUid(), city.getLevel());
        }
        int gid = gameUserService.getActiveGid(user.getUid());
        int sid = gameUserService.getActiveSid(user.getUid());

        AttackCityStrategyEntity strategyEntity = AttackCityStrategyEntity.getInstance(combatInfo, combat, gid, sid, seq);
        strategyLogAsyncHandler.log(combat.getFightType(), strategyEntity);
    }

    @Override
    public <T> void saveAndUpload(T t) {
        AttackCityStrategyEntity entity = (AttackCityStrategyEntity) t;
        //补充保存录像\
        Optional<CombatVideo> combatVideoOp = combatVideoService.getCombatVideo(entity.getId());
        if (!combatVideoOp.isPresent()) {
            return;
        }
        String cityName = entity.getCity();
        if (entity.getNightmare() == 1) {
            cityName = entity.getCity() + "_my";
        }
        String ossPath = OSSService.getAttackCityStrategyOssPath(entity.getGid(), cityName, combatVideoOp.get().getId());
        String ossURl = OSSService.uploadVideo(combatVideoOp.get(), ossPath);
        if (StrUtil.isBlank(ossURl)) {
            return;
        }
        entity.setRecordedUrl(ossURl);
        attackCityStrategyService.insert(entity);
        chengCStrategyService.addNewest(entity);
    }

    @Override
    public RDVideo listStrategy(long uid, int gid, int baseId, StrategyEnum strategyEnum) {
        int cityId = baseId;
        int level = CityTool.getCityById(cityId).getLevel();
        int seq = 1;
        if (!StrategyEnum.NEWEST.equals(strategyEnum)) {
            seq = userCityService.getOwnCityNumAsLevel(uid, level);
        }
        RDVideo video = new RDVideo();
        List<StrategyVO> voList = null;
        if (gameUserService.getGameUser(uid).getStatus().intoNightmareWord()) {
            voList = chengCStrategyService.getNightmareStrategyVOList(cityId, seq, strategyEnum, gid);
        } else {
            voList = chengCStrategyService.getStrategyVOList(cityId, seq, strategyEnum, gid);
            voList = voList.stream().sorted(Comparator.comparing(StrategyVO::getDatetimeInt).reversed()).collect(Collectors.toList());
        }
        for (StrategyVO vo : voList) {
            vo.setFightType(FightTypeEnum.ATTACK.getValue());
        }
        video.setStrategyVOList(voList);
        return video;
    }

    @Override
    public RDVideo listBetterStrategy(long uid, int gid, int cityId) {
        int level = CityTool.getCityById(cityId).getLevel();
        int seq = 1;
        List<StrategyVO> list = new ArrayList<>();
        boolean isNightMare = gameUserService.getGameUser(uid).getStatus().intoNightmareWord();
        if (isNightMare) {
            seq = Math.max(userCityService.getOwnNightmareCityNumAsLevel(uid, level), 1);
        } else {
            seq = Math.max(userCityService.getOwnCityNumAsLevel(uid, level), 1);
        }
        Map<String, Long> map = new HashMap<>();
        for (StrategyEnum strategyEnum : StrategyEnum.values()) {
            if (strategyEnum.equals(StrategyEnum.NEWEST)) {
                continue;
            }
            List<StrategyVO> voList = null;
            int showNum = strategyEnum.getShowNum();
            if (isNightMare) {
                showNum *= 2;
                voList = chengCStrategyService.getNightmareStrategyVOList(cityId, seq, strategyEnum, gid);
            } else {
                voList = chengCStrategyService.getStrategyVOList(cityId, seq, strategyEnum, gid);
                voList = voList.stream().sorted(Comparator.comparing(StrategyVO::getDatetimeInt).reversed()).collect(Collectors.toList());
            }
            if (ListUtil.isEmpty(voList)) {
                continue;
            }
            for (StrategyVO vo : voList) {
                if (map.get(vo.getP1().getUid() + "_" + vo.getP2().getNickname()) != null) {
                    continue;
                }
                if (showNum <= 0) {
                    break;
                }
                vo.setFightType(FightTypeEnum.ATTACK.getValue());
                list.add(vo);
                map.put(vo.getP1().getUid() + "_" + vo.getP2().getNickname(), vo.getP1().getUid());
                showNum--;
            }
        }
        RDVideo video = new RDVideo();
        video.setStrategyVOList(list);
        return video;
    }
}
