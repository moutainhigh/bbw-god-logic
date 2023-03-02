package com.bbw;

import com.bbw.common.PowerRandom;
import com.bbw.god.game.zxz.cfg.CfgZxzEntryEntity;
import com.bbw.god.game.zxz.cfg.ZxzEntryTool;
import com.bbw.god.game.zxz.entity.ZxzEntry;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 诛仙阵词条随机
 * @author: hzf
 * @create: 2022-12-27 10:26
 **/
public class ZxzEntryTest extends BaseTest{

    @Test
    public void testRandomEntry() {
        // 等级存量
        int lvStock = 500;
        // 等级上限
        int lvLimit = 12;
        // 词条池
        List<CfgZxzEntryEntity> entrys = ZxzEntryTool.getEntryByType(10);
        List<Integer> entryIds = entrys.stream().map(CfgZxzEntryEntity::getEntryId).collect(Collectors.toList());

        //随机词条
        long begin = System.currentTimeMillis();
        List<ZxzEntry> randomResults = randomEntry(entryIds, lvLimit, lvStock);
        System.out.println("随机词条耗时：" + (System.currentTimeMillis() - begin));

        //判断结果准确性
        int sumLvAsResult = randomResults.stream().mapToInt(ZxzEntry::getEntryLv).sum();
        Assert.assertEquals(lvStock, sumLvAsResult);

        for (ZxzEntry randomResult : randomResults) {
            System.out.println(randomResult.toString());
            if (randomResult.getEntryLv() > lvLimit) {
                System.err.println(randomResult + " 随机结果超过词条等级上限：" + lvLimit);
            }
        }
    }

    /**
     * 随机词条
     *
     * @param entryIds 要随机的词条集合
     * @param lvLimit  随机等级上限
     * @param lvStock  词条等级存量
     * @return 随机词条及等级
     */
    public List<ZxzEntry> randomEntry(List<Integer> entryIds, Integer lvLimit, int lvStock) {
        List<ZxzEntry> randomResults = new ArrayList<>();
        //剩余等级存量
        int remainLvStock = lvStock;
        do {
            doRandomEntry(randomResults, entryIds, lvLimit, remainLvStock);
            //已随机的等级总量
            int sumLv = randomResults.stream().mapToInt(ZxzEntry::getEntryLv).sum();
            remainLvStock = lvStock - sumLv;
        } while (remainLvStock > 0);

        return randomResults;
    }

    /**
     * 随机词条
     *
     * @param randomResults 随机结果
     * @param entryIds      要随机的词条集合
     * @param lvLimit       随机等级上限
     * @param lvStock       词条等级存量
     * @return
     */
    public void doRandomEntry(List<ZxzEntry> randomResults, List<Integer> entryIds, Integer lvLimit, int lvStock) {
        System.out.println("-----------------do random entry, lvStock = " + lvStock);
        if (lvStock <= 0) {
            return;
        }
        List<Integer> excludeEntryIds = new ArrayList<>();
        while (true) {
            //存量等级为0退出循环
            if (lvStock <= 0) {
                break;
            }
            //如果全部随机玩，退出
            if (excludeEntryIds.containsAll(entryIds)) {
                break;
            }

            //随机一个词条
            Integer randomEntryId = PowerRandom.getRandomFromList(entryIds, excludeEntryIds);
            excludeEntryIds.add(randomEntryId);
            System.out.println("======= random entryId " + randomEntryId);
            Optional<ZxzEntry> entryOptional = randomResults.stream().filter(tmp -> tmp.getEntryId().equals(randomEntryId)).findFirst();
            int currentEntryLevel = entryOptional.isPresent() ? entryOptional.get().getEntryLv() : 0;

            //可随机等级范围
            int canRandomLv = lvLimit - currentEntryLevel;
            if (canRandomLv <= 0) {
                continue;
            }
            //随机的等级不能超过等级存量
            canRandomLv = canRandomLv > lvStock ? lvStock : canRandomLv;

            //随机等级
            int randomLv = PowerRandom.getRandomBetween(1, canRandomLv + 1) - 1;
            System.out.println("======= random lv " + randomLv);

            //处理等级
            if (entryOptional.isPresent()) {
                entryOptional.get().setEntryLv(currentEntryLevel + randomLv);
            } else {
                ZxzEntry zxzEntry = new ZxzEntry(randomEntryId, randomLv);
                randomResults.add(zxzEntry);
            }
            lvStock -= randomLv;
        }

    }
}
