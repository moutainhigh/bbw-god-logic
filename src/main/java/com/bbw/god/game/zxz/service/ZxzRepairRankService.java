package com.bbw.god.game.zxz.service;

import com.bbw.common.MathTool;
import com.bbw.god.game.zxz.cfg.ZxzTool;
import com.bbw.god.game.zxz.entity.UserPassRegionCardGroupInfo;
import com.bbw.god.game.zxz.entity.UserZxzRegionInfo;
import com.bbw.god.game.zxz.entity.ZxzEntry;
import com.bbw.god.game.zxz.rank.ZxzRankService;
import com.bbw.god.game.zxz.rank.ZxzRanker;
import com.bbw.god.gameuser.GameUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 修护榜单service
 * @author: hzf
 * @create: 2022-10-21 15:41
 **/
@Service
public class ZxzRepairRankService {
    @Autowired
    private ZxzRankService zxzRankService;
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private ZxzService zxzService;
    public void repairRank(Integer serverGroup,Integer regionId,Integer beginDate){
        int startRank = 1;
        int endRank = 999;
        Integer difficulty = ZxzTool.getDifficulty(regionId);
        List<ZxzRanker> rankers = zxzRankService.getRankers(serverGroup, difficulty, regionId, beginDate, startRank, endRank);

        List<UserZxzRegionInfo> userZxzRegionInfos = new ArrayList<>();
        List<UserPassRegionCardGroupInfo> userPassRegionCardGroupInfos = new ArrayList<>();

        for (ZxzRanker ranker : rankers) {
            Long uid = ranker.getUid();
            UserPassRegionCardGroupInfo userPassRegionCardGroup = zxzService.getUserPassRegionCardGroup(uid, regionId,beginDate);
            if (userPassRegionCardGroup == null) {
                continue;
            }
            if (!ifRepairEntries(userPassRegionCardGroup.gainEntrys())) {
                continue;
            }
            //修正通过卡组词条
            userPassRegionCardGroup.setEntries(repairEntries(userPassRegionCardGroup.gainEntrys()));
            Integer clearanceLv = userPassRegionCardGroup.computeRegionLv();
            userPassRegionCardGroup.setRegionLv(clearanceLv);
            userPassRegionCardGroupInfos.add(userPassRegionCardGroup);

            //修正区域词条
            UserZxzRegionInfo userZxzRegion = zxzService.getUserZxzRegion(uid, regionId);
            userZxzRegion.setEntries(repairEntries(userZxzRegion.gainEntrys()));

            Integer regionLv = userZxzRegion.computeRegionLv();
            userZxzRegion.setClearanceLv(regionLv);
            userZxzRegion.setLastClearanceLv(regionLv);
            userZxzRegionInfos.add(userZxzRegion);

            //重新计算分数
            double score = MathTool.add(clearanceLv, 0.1);
            score = MathTool.subtract(score, 0.00001 * ranker.getRank());
            zxzRankService.setRankValue(uid,difficulty,regionId,beginDate,score);
        }
        gameUserService.updateItems(userZxzRegionInfos);
        gameUserService.updateItems(userPassRegionCardGroupInfos);

    }

    /**
     * 判断词条是否有重复
     * @param zxzEntries
     * @return
     */
    public boolean ifRepairEntries(List<ZxzEntry> zxzEntries){
        for (ZxzEntry entry : zxzEntries) {
            Integer entryId = entry.getEntryId();
            List<ZxzEntry> zxzEntryList = zxzEntries.stream().filter(e -> e.getEntryId().equals(entryId)).collect(Collectors.toList());
            if (zxzEntryList.size() > 1) {
              return true;
            }
        }
        return false;
    }

    /**
     * 修复词条
     * @param zxzEntries
     * @return
     */
    public List<String> repairEntries(List<ZxzEntry> zxzEntries){
        List<String> repair = new ArrayList<>();
        List<ZxzEntry> repairEntries = new ArrayList<>();
        for (ZxzEntry entry : zxzEntries) {
            Integer entryId = entry.getEntryId();
            List<ZxzEntry> zxzEntryList = zxzEntries.stream().filter(e -> e.getEntryId().equals(entryId)).collect(Collectors.toList());
             if (zxzEntryList.size() > 1) {
                 zxzEntryList.sort(Comparator.comparing(ZxzEntry::getEntryLv).reversed());
                 entry = zxzEntryList.stream().limit(1).findFirst().orElse(null);
                 repairEntries.add(entry);
             } else {
                 repairEntries.add(entry);
             }
        }
        List<ZxzEntry> collect = repairEntries.stream().distinct().collect(Collectors.toList());
        for (ZxzEntry repairEntry : collect) {
            repair.add(repairEntry.toString());
        }
        return repair;
    }


}
