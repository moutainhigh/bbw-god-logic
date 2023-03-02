package com.bbw.god.city.mixd.nightmare.pos;

import com.bbw.common.JSONUtil;
import com.bbw.common.ListUtil;
import com.bbw.common.PowerRandom;
import com.bbw.god.city.mixd.nightmare.*;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 说明：
 * 宝箱:从奖励列表中，根据概率收集1份奖励，收集到的奖励会显示在藏宝背包中。
 * @author lwb
 * date 2021-05-27
 */
@Service
@Slf4j
public class BoxProcessor extends AbstractMiXianPosProcessor {

    @Override
    public boolean match(NightmareMiXianPosEnum miXianPosEnum) {
        return NightmareMiXianPosEnum.BOX.equals(miXianPosEnum);
    }

    @Override
    public void touchPos(UserNightmareMiXian nightmareMiXian,RDNightmareMxd rd, MiXianLevelData.PosData posData) {
        CfgNightmareMiXian cfg = NightmareMiXianTool.getCfg();
        List<CfgNightmareMiXian.BoxInfo> infoList = ListUtil.copyList(cfg.getBoxAwards(), CfgNightmareMiXian.BoxInfo.class);
        // 根据每日元宝累计，替换概率
        replaceAwardProByDailyGoldTotal(nightmareMiXian.getDailyGoldNum(), cfg, infoList);
        // 玩家本层以获得过元宝 需要下调元宝获得概率
        if (nightmareMiXian.isFindYbBox()){
            //因为随机方式是权重的 所以元宝的概率由10%，下降到2%，多出来的8%平均分摊到其他奖励项目中  等同于元宝权重下调到200 即可
            for (CfgNightmareMiXian.BoxInfo info : infoList) {
                if (info.getAwardEnum()==AwardEnum.YB.getValue()){
                    info.setProbability(200);
                }
            }
        }
        // 根据概率随机宝箱
        int max = infoList.stream().mapToInt(CfgNightmareMiXian.BoxInfo::getProbability).sum();
        int seed = PowerRandom.getRandomBySeed(max);
        int sum=0;
        CfgNightmareMiXian.BoxInfo boxInfo=infoList.get(0);
        for (CfgNightmareMiXian.BoxInfo info : infoList) {
            sum+=info.getProbability();
            if (sum>=seed){
                boxInfo=info;
                break;
            }
        }
        List<Award> awards = boxInfo.getRandomSubAwards();
        if (awards.stream().anyMatch(p->p.getItem() == AwardEnum.YB.getValue())){
            //是元宝
            nightmareMiXian.setFindYbBox(true);
        }
        List<Award> rdAwardList = checkBoxAwards(awards, nightmareMiXian.getGameUserId());
        if (ListUtil.isEmpty(rdAwardList)){
            log.info("【梦魇迷仙洞】宝箱随机奖励：{}，checkBoxAwards()结果为空！玩家{}数据：{}", JSONUtil.toJson(awards), nightmareMiXian.getGameUserId(), JSONUtil.toJson(nightmareMiXian));
        }
        rd.setGainAwards(rdAwardList);
        nightmareMiXian.addAwardToBag(rdAwardList);
        nightmareMiXian.takeCurrentPosToEmptyType();
    }

    /**
     * 通过每日元宝累计获得数，替换奖励生成概率
     *
     * @param dailyGoldNum 每日元宝累计数
     * @param cfg
     * @param boxInfoList
     */
    public static void replaceAwardProByDailyGoldTotal(int dailyGoldNum, CfgNightmareMiXian cfg, List<CfgNightmareMiXian.BoxInfo> boxInfoList){
        // 获取概率组
        List<CfgNightmareMiXian.ProbabilityGroup> probabilityGroups = cfg.getProbabilityGroups();
        CfgNightmareMiXian.ProbabilityGroup probabilityGroup = probabilityGroups.stream().filter(proGroup ->
                        proGroup.getMinDailyGoldNum() <= dailyGoldNum && dailyGoldNum <= proGroup.getMaxDailyGoldNum())
                .findFirst().orElse(null);
        if (probabilityGroup == null){
            // 未到匹配的概率组 则不进行替换
            return;
        }
        for (CfgNightmareMiXian.BoxInfo boxInfo : boxInfoList) {
            CfgNightmareMiXian.ProbabilityGroup.ProbabilityAward probabilityAward = probabilityGroup.getProbabilities().stream().filter(pro ->
                    pro.getItem().equals(boxInfo.getAwardEnum())).findFirst().orElse(null);
            if (probabilityAward == null){
                // 未匹配到对应奖励的概率 则跳过替换
                continue;
            }
            boxInfo.setProbability(probabilityAward.getProbability());
        }
    }
}