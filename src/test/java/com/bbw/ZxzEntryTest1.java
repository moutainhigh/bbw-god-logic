package com.bbw;

import com.bbw.common.PowerRandom;
import com.bbw.god.game.zxz.cfg.CfgZxzEntryEntity;
import com.bbw.god.game.zxz.cfg.ZxzEntryTool;
import com.bbw.god.game.zxz.cfg.foursaints.CfgFourSaintsEntity;
import com.bbw.god.game.zxz.cfg.foursaints.CfgFourSaintsTool;
import com.bbw.god.game.zxz.entity.ZxzEntry;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 诛仙阵词条随机
 * @author: hzf
 * @create: 2022-12-27 10:26
 **/
public class ZxzEntryTest1 extends BaseTest{

    @Test
    public void testRandomEntry() {
        // 等级存量
        int lvStock = 139;
        // 等级上限
        int lvLimit = 5;
        // 词条池
        List<CfgZxzEntryEntity> entrys = ZxzEntryTool.getEntryByType(10);
        List<Integer> entryIds = entrys.stream().map(CfgZxzEntryEntity::getEntryId).collect(Collectors.toList());
        //要过滤的词条id
        List<Integer> filterEntryIds = Arrays.asList(331209,331409);
        //参加随机词条的id
        List<Integer> joinEntryIds = entryIds.stream().filter(tmp -> !filterEntryIds.contains(tmp)).collect(Collectors.toList());

        //随机词条
        long begin = System.currentTimeMillis();
        List<ZxzEntry> randomResults = randomEntry(joinEntryIds, lvLimit, lvStock);
        System.out.println("随机词条耗时：" + (System.currentTimeMillis() - begin));

        //判断结果准确性
        int sumLvAsResult = randomResults.stream().mapToInt(ZxzEntry::getEntryLv).sum();
        Assert.assertEquals(lvStock, sumLvAsResult);


        //上限的数量
        int limitLvNum = 0;
        //第三等级上限的数量
        int limitLv3Num = 0;
        for (ZxzEntry randomResult : randomResults) {
            System.out.println(randomResult.toString());
            if (randomResult.getEntryLv() > lvLimit) {
                System.err.println(randomResult + " 随机结果超过词条等级上限：" + lvLimit);
            }
            if (randomResult.getEntryLv() == lvLimit) {
                limitLvNum++;
            }
            if (randomResult.getEntryLv() == lvLimit - 2) {
                limitLv3Num++;
            }
        }
        System.err.println(" 随机结果等于词条等级顶级：" + limitLvNum);
        System.err.println(" 随机结果等于词条等级第三等级：" + limitLv3Num);

    }



    //等级上限的数量
    private int  maxNum = getMaxLvLimitNum();
    //第三等级的等级上限数量
    private int  threeNum =  getThreeLvLimitNum();
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
        boolean exist = false;
        long begin  = System.currentTimeMillis();
        do {

            doRandomEntry(randomResults, entryIds, lvLimit, remainLvStock);
            //已随机的等级总量
            int sumLv = randomResults.stream().mapToInt(ZxzEntry::getEntryLv).sum();
            remainLvStock = lvStock - sumLv;
            //过滤 等级为上限 或者 为 上限 - 2（第三等级）
            List<Integer> filterLvs = new ArrayList<>();
            filterLvs.add(lvLimit);
            filterLvs.add(lvLimit-2);

            //如：等级上限的数量已经满了，上限 - 1 的等级 也过滤
            if (this.maxNum <= 0) {
                filterLvs.add(lvLimit-1);
            }
            List<Integer> collect = randomResults.stream().filter(tmp ->filterLvs.contains(tmp.getEntryLv())).map(ZxzEntry::getEntryId).collect(Collectors.toList());
            entryIds.removeAll(collect);
            exist = remainLvStock > 0;
            long useDate = System.currentTimeMillis() - begin;
            if (useDate - 5000 > 0) {
                exist = false;
            }
        } while (exist);

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
        int lvLimit3 = lvLimit - 2;
        int lvLimit4 = lvLimit - 3;
        System.out.println("-----------------do random entry, lvStock = " + lvStock);
        System.out.println("-----------------do random entry, maxNum = " + this.maxNum);
        System.out.println("-----------------do random entry, threeNum = " + this.threeNum);
        if (lvStock <= 0) {
            return;
        }
        List<Integer> excludeEntryIds = new ArrayList<>();
        while (true) {
            //存量等级为0退出循环
            if (lvStock <= 0) {
                break;
            }
            //如果全部随机完，退出
            if (excludeEntryIds.containsAll(entryIds)) {
                break;
            }
            //随机一个词条
            Integer randomEntryId = PowerRandom.getRandomFromList(entryIds, excludeEntryIds);
            //添加随机过的词条
            excludeEntryIds.add(randomEntryId);
            Optional<ZxzEntry> entryOptional = randomResults.stream().filter(tmp -> tmp.getEntryId().equals(randomEntryId)).findFirst();
            //当前等级
            int currentEntryLevel = entryOptional.isPresent() ? entryOptional.get().getEntryLv() : 0;

            //随机添加等级
            int addEntryLv = 0;

            //随机加多少等级
            CfgFourSaintsEntity.CfgEntryRandomLv cfgEntryRandomLv = getRandomLv();
            addEntryLv = cfgEntryRandomLv.getAddLv();


            //addEntryLv如果为0
            if (addEntryLv == 0) {
                continue;
            }
            //超过剩下的存量
            if (addEntryLv > lvStock) {
                continue;
            }
            //获取第四档位的等级的词条id
            List<Integer> collect = randomResults.stream().filter(tmp -> tmp.getEntryLv() == lvLimit4).map(ZxzEntry::getEntryId).collect(Collectors.toList());
            //第四档位的等级与剩下的词条id相等
            boolean limit4Equal =   entryIds.containsAll(collect);

            //第三档位的数量已经全部使用完，但是还剩下一个存量
            if (limit4Equal && this.threeNum <= 0 && lvStock == 1) {
                this.threeNum ++;
            }
            //控制等级第三档的数量
            if (this.threeNum <= 0 && currentEntryLevel + addEntryLv  == lvLimit3 || currentEntryLevel == lvLimit3) {
                continue;
            }
            //优先处理第三档的词条等级数量
            if (this.threeNum > 0 && currentEntryLevel + addEntryLv >= lvLimit3) {
                addEntryLv = lvLimit3 - currentEntryLevel;
            }
            //排除随机出来大于顶级
            if (this.maxNum <= 0 && currentEntryLevel + addEntryLv >= lvLimit) {
                continue;
            }
            //超额处理
            if (this.threeNum <= 0 && this.maxNum > 0 && currentEntryLevel + addEntryLv >= lvLimit) {
                addEntryLv = lvLimit - currentEntryLevel;
            }



            // 处理第三等级可以随机到的数量
            if (currentEntryLevel + addEntryLv == lvLimit3) {
                this.threeNum --;
            }
            // 处理第一等级可以随机的到的数量
            if (currentEntryLevel + addEntryLv == lvLimit) {
                this.maxNum --;
            }
            //处理等级
            if (entryOptional.isPresent()) {
                entryOptional.get().setEntryLv(currentEntryLevel + addEntryLv);
            } else {
                ZxzEntry zxzEntry = new ZxzEntry(randomEntryId, addEntryLv);
                randomResults.add(zxzEntry);
            }
            lvStock -= addEntryLv;
        }

    }

    public CfgFourSaintsEntity.CfgEntryRandomLv getRandomLv(){
        List<CfgFourSaintsEntity.CfgEntryRandomLv> entryRandomLvs = CfgFourSaintsTool.getEntryRandomLvs();
        //取出所有的概率
        List<Integer> probabilitys = entryRandomLvs.stream()
                .map(CfgFourSaintsEntity.CfgEntryRandomLv::getProbability)
                .collect(Collectors.toList());
        //获取中的索引
        int index = PowerRandom.getIndexByProbs(probabilitys, 100);
        return entryRandomLvs.get(index);
        }
    public int getMaxLvLimitNum(){
        int probability = PowerRandom.getRandomBetween(10, 30);
        return (int) 41 * probability /100;
    }

    public int getThreeLvLimitNum(){
        int probability = PowerRandom.getRandomBetween(30, 50);
        return (int) 41 * probability /100;
    }
}
