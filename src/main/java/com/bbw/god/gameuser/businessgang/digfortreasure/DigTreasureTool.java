package com.bbw.god.gameuser.businessgang.digfortreasure;

import com.bbw.common.DateUtil;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.game.config.Cfg;
import com.bbw.god.game.config.city.CfgRoadEntity;
import com.bbw.god.game.config.city.RoadTool;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 挖宝工具类
 *
 * @author: huanghb
 * @date: 2022/1/21 17:20
 */
public class DigTreasureTool {
    /**
     * 获得挖宝配置信息
     *
     * @return
     */
    protected static CfgDigTreasure getCfg() {
        return Cfg.I.getUniqueConfig(CfgDigTreasure.class);
    }

    /**
     * 获得随机奖励配置
     *
     * @return
     */
    protected static List<CfgDigTreasure.TreasureTrove> getTreasureTroves() {
        CfgDigTreasure cfg = getCfg();
        return cfg.getTreasureTroves();
    }

    /**
     * 获得当前位置已挖到所有宝藏
     *
     * @param treasureTroveIds 宝藏id集合
     * @param resultAwardIds   最终奖励id数组
     * @return
     */
    protected static List<Award> getCurrentPosDugTreasureTroveAwawds(List<Integer> treasureTroveIds, Integer[] resultAwardIds) {
        List<Award> awards = new ArrayList<>();
        for (Integer treasureTroveId : treasureTroveIds) {
            //是否有奖励
            boolean isHasAward = 0 == treasureTroveId;
            if (isHasAward) {
                continue;
            }
            CfgDigTreasure.TreasureTrove treasureTrove = getTreasureTroves().stream().filter(tmp -> tmp.getId() == treasureTroveId).findFirst().orElse(null);
            //是否没有宝藏信息
            boolean isOwnTreasureInfo = null != treasureTrove;
            if (!isOwnTreasureInfo) {
                continue;
            }
            int resultAwardIndex = treasureTroveIds.indexOf(treasureTrove.getId());
            //resultAward是否有奖励id eg:专门处理随机卷轴之类道具展示文艺
            int resultAwardId = null == resultAwardIds ? 0 : resultAwardIds[resultAwardIndex];
            if (0 == resultAwardId) {
                awards.add(treasureTrove.getAwards().get(0));
                continue;
            }
            awards.add(new Award(resultAwardId, AwardEnum.FB, 1));
        }
        return awards;
    }

    /**
     * 获得当前位置当前层挖到的宝藏id
     *
     * @param treasureTroveId
     * @return
     */
    protected static List<Award> getCurrentPosAwawd(int treasureTroveId) {
        CfgDigTreasure.TreasureTrove treasureTrove = getTreasureTroves().stream().filter(tmp -> treasureTroveId == tmp.getId()).findFirst().orElse(null);
        return treasureTrove.getAwards();
    }

    /**
     * 获得挖宝需要的铲子id
     *
     * @return
     */
    protected static List<Integer> getDigTreasureNeedShovelIds() {
        CfgDigTreasure cfgDigTreasure = getCfg();
        return cfgDigTreasure.getFloors().stream().map(CfgDigTreasure.Floor::getDigTreasureNeedShovelId).collect(Collectors.toList());
    }

    /**
     * 获得指定楼层宝藏id集合
     *
     * @param floor
     * @return
     */
    protected static List<Integer> getFloorTreasureTroveIds(Integer floor) {
        List<CfgDigTreasure.TreasureTrove> treasureTroves = getTreasureTroves().stream()
                .filter(tmp -> tmp.getFloor() == floor).collect(Collectors.toList());
        return treasureTroves.stream().map(CfgDigTreasure.TreasureTrove::getId).collect(Collectors.toList());
    }

    /**
     * 获得永久开启需要的好感动
     *
     * @return
     */
    protected static Integer getPermanentOpenNeedFavorability() {
        CfgDigTreasure cfgDigTreasure = getCfg();
        return cfgDigTreasure.getPermanentOpenNeedFavorability();
    }

    /**
     * 获得临时开启需要的好感动
     *
     * @return
     */
    protected static Integer getTempOpenNeedFavorability() {
        CfgDigTreasure cfgDigTreasure = getCfg();
        return cfgDigTreasure.getTempOpenNeedFavorability();
    }

    /**
     * 对挖宝有影响的法宝id
     *
     * @return
     */
    public static List<Integer> getOwnEffectTReasureIds() {
        CfgDigTreasure cfgDigTreasure = getCfg();
        return cfgDigTreasure.getOwnEffectTReasureIds();
    }

    /**
     * 是否需要刷新
     *
     * @param generationTime
     * @return
     */
    public static boolean isNeedRefresh(Date generationTime) {
        //下一次刷新时间
        Date weekEndTime = DateUtil.getWeekEndDateTime(generationTime);
        Date nextRefreshTime = DateUtil.addHours(weekEndTime, DigTreasureTool.getWeekBeginHour());
        return DateUtil.now().getTime() >= nextRefreshTime.getTime();
    }

    /**
     * 获得周开始小时
     *
     * @return
     */
    public static int getWeekBeginHour() {
        return getCfg().getWeekBeginHour();
    }

    /**
     * 随机所有道路id集合
     */
    public static List<Integer> randomAllRoadIds() {
        List<Integer> roadIds = RoadTool.getRoads().stream().map(CfgRoadEntity::getId).collect(Collectors.toList());
        Collections.shuffle(roadIds);
        return roadIds;
    }
}
