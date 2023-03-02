package com.bbw.god.gameuser.treasure.processor;

import com.bbw.common.ListUtil;
import com.bbw.common.PowerRandom;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardService;
import com.bbw.god.game.config.Cfg;
import com.bbw.god.game.config.CfgMxdZhenShou;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.treasure.CPUseTreasure;
import com.bbw.god.gameuser.treasure.RDUseMapTreasure;
import com.bbw.god.gameuser.treasure.TreasureChecker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 迷仙洞镇守礼包
 * @author lwb
 */
@Service
public class MxdZhenShouProcessor extends TreasureUseProcessor {
    @Autowired
    private AwardService awardService;

    public MxdZhenShouProcessor() {
        this.treasureEnum = TreasureEnum.MXD_ZHEN_SHOU;
        this.isAutoBuy = false;
    }

    @Override
    public void check(GameUser gu, CPUseTreasure param) {
        TreasureChecker.checkIsEnough(this.treasureEnum.getValue(), 1, gu.getId());
    }

    /**
     * 是否宝箱类
     *
     * @return
     */
    @Override
    public boolean isChestType() {
        return true;
    }

    @Override
    public void effect(GameUser gu, CPUseTreasure param, RDUseMapTreasure rd) {
        CfgMxdZhenShou config = Cfg.I.getUniqueConfig(CfgMxdZhenShou.class);
        int sumProbability = config.getRandomAwards().stream().collect(Collectors.summingInt(Award::getProbability));
        int seed = PowerRandom.getRandomBySeed(sumProbability);
        int sum = 0;
        List<Award> gainAwards = new ArrayList<>();
        for (Award award : config.getRandomAwards()) {
            sum += award.getProbability();
            if (sum>=seed){
                gainAwards.add(award);
                break;
            }
        }
        if (ListUtil.isNotEmpty(gainAwards)){
            awardService.fetchAward(gu.getId(),gainAwards, WayEnum.MXD_ZHEN_SHOU,WayEnum.MXD_ZHEN_SHOU.getName(),rd);
        }
    }

}
