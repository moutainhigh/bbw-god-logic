package com.bbw.god.game.combat.attackstrategy.yaozu;

import com.bbw.common.ListUtil;
import com.bbw.common.StrUtil;
import com.bbw.god.cache.TimeLimitCacheUtil;
import com.bbw.god.db.entity.AttackYaoZuStrategyEntity;
import com.bbw.god.db.service.AttackYaoZuStrategyService;
import com.bbw.god.detail.async.StrategyLogAsyncHandler;
import com.bbw.god.fight.FightTypeEnum;
import com.bbw.god.game.combat.attackstrategy.StrategyEnum;
import com.bbw.god.game.combat.attackstrategy.StrategyVO;
import com.bbw.god.game.combat.attackstrategy.service.AbstractStrategyLogic;
import com.bbw.god.game.combat.data.Combat;
import com.bbw.god.game.combat.data.CombatInfo;
import com.bbw.god.game.combat.data.Player;
import com.bbw.god.game.combat.video.CombatVideo;
import com.bbw.god.game.combat.video.RDVideo;
import com.bbw.god.game.combat.video.service.CombatVideoService;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.yaozu.ArriveYaoZuCache;
import com.bbw.god.gameuser.yaozu.YaoZuTool;
import com.bbw.oss.OSSService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 妖族攻略
 *
 * @author fzj
 * @date 2021/9/24 13:40
 */
@Slf4j
@Service
public class YaoZuStrategyLogic extends AbstractStrategyLogic {

    @Autowired
    private AttackYaoZuStrategyService attackYaoZuStrategyService;
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private YaoZuStrategyService yaoZuStrategyService;
    @Autowired
    CombatVideoService combatVideoService;
    @Autowired
    private StrategyLogAsyncHandler strategyLogAsyncHandler;

    @Override
    public boolean matchFight(FightTypeEnum fightType) {
        return fightType == FightTypeEnum.YAOZU_FIGHT;
    }

    @Override
    protected boolean isToSave(Combat combat, CombatInfo combatInfo) {
        Player p1 = combat.getP1();
        if (p1.getUid() < 0) {
            return false;
        }
        return true;
    }

    @Override
    protected void doSave(Combat combat, CombatInfo combatInfo) {
        Player user = combat.getP1();
        int gid = gameUserService.getActiveGid(user.getUid());
        int sid = gameUserService.getActiveSid(user.getUid());
        ArriveYaoZuCache cache = TimeLimitCacheUtil.getArriveCache(user.getUid(), ArriveYaoZuCache.class);
        AttackYaoZuStrategyEntity strategyEntity = AttackYaoZuStrategyEntity.getInstance(combatInfo, combat, gid, sid, cache);
        strategyLogAsyncHandler.log(combat.getFightType(), strategyEntity);
    }

    @Override
    public <T> void saveAndUpload(T t) {
        AttackYaoZuStrategyEntity entity =  (AttackYaoZuStrategyEntity) t;
        //补充保存录像
        Optional<CombatVideo> combatVideoOp = combatVideoService.getCombatVideo(entity.getId());
        if (!combatVideoOp.isPresent()) {
            return;
        }
        String yaoZuName = entity.getAiNickname();
        if (entity.getOntology() == 1) {
            yaoZuName = entity.getAiNickname() + "_ontology";
        }
        String ossPath = OSSService.getYaoZuOssPath(entity.getGid(), yaoZuName, combatVideoOp.get().getId());
        String ossURl = OSSService.uploadVideo(combatVideoOp.get(), ossPath);
        if (StrUtil.isBlank(ossURl)) {
            return;
        }
        entity.setRecordedUrl(ossURl);
        attackYaoZuStrategyService.insert(entity);
        yaoZuStrategyService.addNewest(entity);
    }

    /**
     * 获取策略列表
     * @param uid
     * @param gid
     * @param baseId
     * @param strategyEnum
     * @return
     */
    @Override
    public RDVideo listStrategy(long uid, int gid, int baseId, StrategyEnum strategyEnum) {
        int yaoZuId = baseId;
        int seq = YaoZuTool.getYaoZu(yaoZuId).getYaoZuType();
        RDVideo video = new RDVideo();
        List<StrategyVO> voList = yaoZuStrategyService.getStrategyVOList(yaoZuId, seq, strategyEnum, gid);
        for (StrategyVO vo : voList) {
            vo.setUrl(encodeOssUrl(vo.getUrl()));
            vo.setFightType(FightTypeEnum.YAOZU_FIGHT.getValue());
        }
        video.setStrategyVOList(voList);
        return video;
    }

    /**
     * 获取精选策略列表
     * @param uid
     * @param gid
     * @param baseId
     * @return
     */
    @Override
    public RDVideo listBetterStrategy(long uid, int gid, int baseId) {
        int yaoZuId = baseId;
        int seq = YaoZuTool.getYaoZu(yaoZuId).getYaoZuType();
        List<StrategyVO> list = new ArrayList<>();
        Map<String, Long> map = new HashMap<>();
        for (StrategyEnum strategyEnum : StrategyEnum.values()) {
            if (strategyEnum.equals(StrategyEnum.NEWEST)) {
                continue;
            }
            List<StrategyVO> voList = null;
            int showNum = strategyEnum.getShowNum() * 2;
            voList = yaoZuStrategyService.getStrategyVOList(yaoZuId, seq, strategyEnum, gid);
            voList = voList.stream().sorted(Comparator.comparing(StrategyVO::getDatetimeInt).reversed()).collect(Collectors.toList());
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
                vo.setFightType(FightTypeEnum.YAOZU_FIGHT.getValue());
                list.add(vo);
                map.put(vo.getP1().getUid() + "_" + vo.getP2().getNickname(), vo.getP1().getUid());
                showNum--;
            }
        }
        RDVideo video = new RDVideo();
        for (StrategyVO vo : list) {
            vo.setUrl(encodeOssUrl(vo.getUrl()));
        }
        video.setStrategyVOList(list);
        return video;
    }


    private String encodeOssUrl(String srcUrl) {
        try {
            String yaoZuName = srcUrl.split("/")[6];
            String yaoZuNameEncode = URLEncoder.encode(yaoZuName, "utf-8");
            String encodeUrl = srcUrl.replace(yaoZuName, yaoZuNameEncode);
            return encodeUrl;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return srcUrl;
    }
}
