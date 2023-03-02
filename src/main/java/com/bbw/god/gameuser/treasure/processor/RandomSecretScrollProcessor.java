package com.bbw.god.gameuser.treasure.processor;

import com.bbw.common.PowerRandom;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.gameuser.biyoupalace.cfg.*;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.rd.RDCommon;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 随机秘传卷轴
 *
 * @author fzj
 * @date 2021/8/27 14:00
 */
@Service
public class RandomSecretScrollProcessor {
    private static final List<Integer> IGNORE = BYPalaceTool.getBYPalaceExclusiveSkills();

    /**
     * 下发随机秘传卷轴
     *
     * @param uid
     * @param way
     * @param rd
     */
    public void deliverRandomSecretScroll(long uid, int num, WayEnum way, RDCommon rd) {
        for (int i = 0; i < num; i++) {
            //获得所有秘传
            List<CfgBYPalaceWeightEntity> secretScrolls = BYPalaceTool.getWeightEntity(ChapterType.SecretBiography.getValue())
                    .stream().filter(s -> !IGNORE.contains(s.getId())).collect(Collectors.toList());
            //秘传权重总合
            int sumWeights = secretScrolls.stream().mapToInt(CfgBYPalaceWeightEntity::getWeight).sum();
            //秘传权重集合
            List<Integer> weightList = secretScrolls.stream().map(CfgBYPalaceWeightEntity::getWeight).collect(Collectors.toList());
            //根据权重随机一本秘传
            int indexByWeight = PowerRandom.getIndexByProbs(weightList, sumWeights);
            CfgBYPalaceWeightEntity secretScroll = secretScrolls.get(indexByWeight);
            //发放秘传
            TreasureEventPublisher.pubTAddEvent(uid, secretScroll.getId(), 1, way, rd);
        }
    }
}
