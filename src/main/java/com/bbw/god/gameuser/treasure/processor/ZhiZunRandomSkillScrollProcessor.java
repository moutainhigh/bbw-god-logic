package com.bbw.god.gameuser.treasure.processor;

import com.bbw.common.PowerRandom;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.treasure.CfgTreasureEntity;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.game.config.treasure.TreasureTool;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.biyoupalace.cfg.BYPalaceTool;
import com.bbw.god.gameuser.biyoupalace.cfg.Chapter;
import com.bbw.god.gameuser.treasure.UserTreasureRecord;
import com.bbw.god.gameuser.treasure.UserTreasureRecordService;
import com.bbw.god.gameuser.treasure.event.EVTreasure;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.rd.RDCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 至尊高级卷轴
 * 5次内没有获得金身技能书，第5次必定获得，如5次内获得金身技能卷轴，则重新计算次数。
 * @author lwb
 */
@Service
public class ZhiZunRandomSkillScrollProcessor {
    private static List<String> ignore= TreasureTool.getTreasureById(BYPalaceTool.getBYPalaceExclusiveSkills())
            .stream().map(CfgTreasureEntity::getName).collect(Collectors.toList());
    @Autowired
    private UserTreasureRecordService userTreasureRecordService;
    @Autowired
    private GameUserService gameUserService;

    /**
     * 获取随机卷轴
     * 5次内没有获得金身技能书[21238]，第5次必定获得，如5次内获得金身技能卷轴，则重新计算次数。
     * @return
     */
    private int getSkillScroll() {

        int seed = PowerRandom.getRandomBySeed(10000);
        int sum = 3000;
//        秘传	30%
        if (sum >= seed) {
            return BYPalaceTool.randomChapterSkillScrollId(Chapter.SB1.getValue(), ignore);
        }
        //70%获得五篇的技能卷轴
        return BYPalaceTool.randomChapterSkillScrollId(Chapter.Five.getValue(), ignore);
    }

    /**
     * 下发至尊高级卷轴的内容
     * 5次内没有获得金身技能书[21238]，第5次必定获得，如5次内获得金身技能卷轴，则重新计算次数。
     *
     * @param uid
     * @param num 数量
     * @param way
     * @param rd
     */
    public void deliverRandomSkillScroll(long uid, int num, WayEnum way, RDCommon rd) {
        int realId = 0;
        UserTreasureRecord utr = userTreasureRecordService.getOrCreateRecord(uid, TreasureEnum.ZHI_ZUN_GAO_JI_JUAN_ZHOU.getValue(),0);
        Integer usedTimes = utr.getUseTimes();
        if (null != usedTimes && usedTimes > 0) {
            utr.setUseTimes(null);
            utr.setGuaranteProgress(TreasureEnum.JIN_SHEN_SKILL_SCROLL.getName(), usedTimes);
        }
        int jinShenProgress = utr.gainGuaranteProgress(TreasureEnum.JIN_SHEN_SKILL_SCROLL.getName());
        int sbProgress = utr.gainGuaranteProgress(Chapter.SB1.getName());
        List<EVTreasure> eVTreasures = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            if (jinShenProgress >= 4) {
                realId = TreasureEnum.JIN_SHEN_SKILL_SCROLL.getValue();
            } else if (sbProgress >= 4) {
                realId = BYPalaceTool.randomChapterSkillScrollId(Chapter.SB1.getValue(), ignore);
                jinShenProgress++;
            } else {
                realId = getSkillScroll();
                sbProgress++;
                jinShenProgress++;
            }
            if (realId == TreasureEnum.JIN_SHEN_SKILL_SCROLL.getValue()) {
                //如5次内获得金身技能卷轴，则重新计算次数
                jinShenProgress = 0;
            } else {
                List<String> sbSkills = BYPalaceTool.getBYPSkillEntityList(Chapter.SB1.getValue()).get(0).getSkills();
                String treasureName = TreasureTool.getTreasureById(realId).getName();
                if (sbSkills.contains(treasureName)) {
                    sbProgress = 0;
                }
            }
            eVTreasures.add(new EVTreasure(realId, 1));
        }
        TreasureEventPublisher.pubTAddEvent(uid, eVTreasures, way, rd);
        utr.setGuaranteProgress(TreasureEnum.JIN_SHEN_SKILL_SCROLL.getName(), jinShenProgress);
        utr.setGuaranteProgress(Chapter.SB1.getName(), sbProgress);
        gameUserService.updateItem(utr);
    }
}
