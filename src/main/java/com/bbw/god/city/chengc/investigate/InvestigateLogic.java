package com.bbw.god.city.chengc.investigate;

import com.bbw.common.ListUtil;
import com.bbw.common.PowerRandom;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.cache.TimeLimitCacheUtil;
import com.bbw.god.city.chengc.ChengChiInfoCache;
import com.bbw.god.fight.RDFightResult;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.city.ChengC;
import com.bbw.god.game.config.city.CityTool;
import com.bbw.god.game.config.special.SpecialTool;
import com.bbw.god.game.config.treasure.CfgTreasureEntity;
import com.bbw.god.game.config.treasure.TreasureTool;
import com.bbw.god.gameuser.res.ResEventPublisher;
import com.bbw.god.gameuser.special.event.EVSpecialAdd;
import com.bbw.god.gameuser.special.event.SpecialEventPublisher;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 *
 *  FIGHT("开战", 10, 45),
 * 	GAIN_ONE_STAR_TREASURE("随机获得一星法宝", 20, 20),
 * 	GAIN_TWO_STAR_TREASURE("随机获得二星法宝", 30, 5),
 * 	GAIN_THREE_STAR_TREASURE("随机获得三星法宝", 40, 1),
 * 	GAIN_SPECIAL("获得本城池初始特产", 50, 15),
 * 	GAIN_ONE_LEVEL_TCP_SPECIAL("获得本城池1级特产铺特产", 60, 5),
 * 	GAIN_TWO_LEVEL_TCP_SPECIAL("获得本城池2级特产铺特产", 70, 3),
 * 	GAIN_GOLD_10("获得10元宝", 80, 3),
 * 	GAIN_GOLD_20("获得20元宝", 90, 2),
 * 	GAIN_GOLD_30("获得30元宝", 100, 1),
 *
 *
 * 城池侦查
 * @author：lwb
 * @date: 2020/12/23 15:20
 * @version: 1.0
 */
@Service
public class InvestigateLogic {
    /**
     * 侦查城池
     * @param uid
     * @return
     */
    public RDInvestigate investigate(long uid){
        ChengChiInfoCache cache = TimeLimitCacheUtil.getChengChiInfoCache(uid);
        if (cache.isInvestigated()) {
            throw new ExceptionForClientTip("city.cc.already.investigate");
        }
        if (cache.isOwnCity()) {
            throw new ExceptionForClientTip("city.cc.can.not.investigate");
        }
        InvestigateEnum investigateEnum=InvestigateEnum.getRandomInvestigateEnum();
        RDInvestigate rd=RDInvestigate.getInstance(!InvestigateEnum.FIGHT.equals(investigateEnum),investigateEnum.getValue());
        switch (investigateEnum){
            case GAIN_GOLD_10:
            case GAIN_GOLD_20:
            case GAIN_GOLD_30:
                //获得元宝
                ResEventPublisher.pubGoldAddEvent(uid, investigateEnum.getParam(), WayEnum.INVESTIGATE, rd);break;
            case GAIN_ONE_STAR_TREASURE:
            case GAIN_TWO_STAR_TREASURE:
            case GAIN_THREE_STAR_TREASURE:
                //获得随机法宝
                CfgTreasureEntity treasure = TreasureTool.getRandomOldTreasure(investigateEnum.getParam());
                TreasureEventPublisher.pubTAddEvent(uid, treasure.getId(), 1, WayEnum.INVESTIGATE, rd);break;
            case GAIN_SPECIAL:
            case GAIN_ONE_LEVEL_TCP_SPECIAL:
            case GAIN_TWO_LEVEL_TCP_SPECIAL:
                //获得本城池特产
                ChengC chengc = CityTool.getChengc(cache.getCityId());
                String[] specials = chengc.getSpecials().split(",");
                List<Integer> specialIds=new ArrayList<>();
                for (int i = 0; i < specials.length; i++) {
                    int needTcpLv=SpecialTool.getCitySpecialUnlockTcpLvByUnlockIndex(cache.getCityLv(),i);
                    if (needTcpLv==investigateEnum.getParam()){
                        specialIds.add(Integer.valueOf(specials[i]));
                    }
                }
                if (ListUtil.isNotEmpty(specialIds)){
                    Integer random = PowerRandom.getRandomFromList(specialIds);
                    List<EVSpecialAdd> specialList = new ArrayList<>();
                    specialList.add(new EVSpecialAdd(random));
                    SpecialEventPublisher.pubSpecialAddEvent(uid, specialList, WayEnum.INVESTIGATE, rd);
                }
            default:
                //战斗
                TimeLimitCacheUtil.removeCache(uid, RDFightResult.class);
        }
        cache.setInvestigated(true);
        TimeLimitCacheUtil.setChengChiInfoCache(uid,cache);
        return rd;
    }
}
