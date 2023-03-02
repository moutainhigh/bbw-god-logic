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
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 随机高级卷轴
 *
 * @author lwb
 */
@Service
public class RandomSkillScrollProcessor {
    private static List<String> ignore = TreasureTool.getTreasureById(BYPalaceTool.getBYPalaceExclusiveSkills())
            .stream().map(CfgTreasureEntity::getName).collect(Collectors.toList());
    @Autowired
    private UserTreasureRecordService userTreasureRecordService;
    @Autowired
    private GameUserService gameUserService;

    /**
     * 获取随机卷轴
     *
     * @return
     */
    private int getSkillScroll() {
        //随机高级卷轴 70%获得五篇的技能卷轴，30%获得秘传（纯随机）
        if (PowerRandom.hitProbability(30)) {
            //30%获得秘传,秘传1和秘传2的配置是一样的 所以选其一即可
            return BYPalaceTool.randomChapterSkillScrollId(Chapter.SB1.getValue(), ignore);
        }
        //70%获得五篇的技能卷轴
        return BYPalaceTool.randomChapterSkillScrollId(Chapter.Five.getValue(), ignore);
    }

    /**
     * 下发随机高级卷轴
     *
     * @param uid
     * @param way
     * @param rd
     */
    public void deliverRandomSkillScroll(long uid, int num, WayEnum way, RDCommon rd) {
        UserTreasureRecord utr = userTreasureRecordService.getOrCreateRecord(uid, TreasureEnum.RANDOM_ADVANCED_SCROLL.getValue(),0);
        int sbProgress = utr.gainGuaranteProgress(Chapter.SB1.getName());
        int realId = 0;
        List<EVTreasure> eVTreasures = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            if (sbProgress >= 7) {
                realId = BYPalaceTool.randomChapterSkillScrollId(Chapter.SB1.getValue(), ignore);
            } else {
                realId = getSkillScroll();
                sbProgress++;
            }
            List<String> sbSkills = BYPalaceTool.getBYPSkillEntityList(Chapter.SB1.getValue()).get(0).getSkills();
            String treasureName = TreasureTool.getTreasureById(realId).getName();
            if (sbSkills.contains(treasureName)) {
                sbProgress = 0;
            }
            eVTreasures.add(new EVTreasure(realId, 1));
        }
        TreasureEventPublisher.pubTAddEvent(uid, eVTreasures, way, rd);
        utr.setGuaranteProgress(Chapter.SB1.getName(), sbProgress);
        gameUserService.updateItem(utr);
    }
}
