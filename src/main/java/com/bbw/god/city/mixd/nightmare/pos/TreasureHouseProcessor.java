package com.bbw.god.city.mixd.nightmare.pos;

import com.bbw.common.PowerRandom;
import com.bbw.god.city.mixd.nightmare.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 说明：
 * 宝库
 * 巡使驻地	20%	生成4格巡使格、1格特殊宝箱、1格传送格
 *
 * 秘藏宝藏	10%	生成1格聚宝盆，开启后，随机获得45~80元宝、外加一格传送
 * 	        35%	生成1格元素脉，开启后，随机获得45~135元素（种类随机）、外加一格传送
 * 	        35%	生成1格摇钱树，开启后，随机获得45~100万铜钱、外加一格传送
 * date 2021-05-27
 * @author liuwenbin
 */
@Service
public class TreasureHouseProcessor extends AbstractMiXianPosProcessor {
    private static final List<Integer> xunShiPos= Arrays.asList(18,22,24,28);
    private static final Integer specialBoxPos= 23;
    private static final Integer gatePos=5;
    private static final Integer playerPos=46;
    private static final List<Integer> awardsPos=Arrays.asList(17, 18, 19, 22, 23, 24, 27, 28, 29);
    @Autowired
    private NightmareMiXianLogic nightmareMiXianLogic;

    @Override
    public boolean match(NightmareMiXianPosEnum miXianPosEnum) {
        return NightmareMiXianPosEnum.TREASURE_HOUSE.equals(miXianPosEnum);
    }

    @Override
    public void touchPos(UserNightmareMiXian nightmareMiXian,RDNightmareMxd rd, MiXianLevelData.PosData posData) {
        if (nightmareMiXian.getTreasureHouseData()==null){
            //初始化
            MiXianLevelData levelData = build(nightmareMiXian.getCurrentLevel());
            nightmareMiXian.setTreasureHouseData(levelData);
        }
    }

    private MiXianLevelData build(int level){
        int seed = PowerRandom.getRandomBySeed(100);

        MiXianLevelData miXianLevelData=MiXianLevelData.getInstance();
        miXianLevelData.setPos(playerPos);
        //20%	生成4格巡使格、1格特殊宝箱、1格传送格
        int sum=20;
        List<MiXianLevelData.PosData> posDatas=new ArrayList<>();
        if (sum>=seed){
            int xunShiLeader=0;
            for (int i = 1; i <=50 ; i++) {
                if (xunShiPos.contains(i)){
                    if (nightmareMiXianLogic.isBuildXunShiLeader(level,xunShiLeader)){
                        //巡使头领
                        posDatas.add(MiXianLevelData.PosData.getInstance(i,NightmareMiXianPosEnum.XUN_SHI_LEADER,true));
                        xunShiLeader++;
                        continue;
                    }
                    posDatas.add(MiXianLevelData.PosData.getInstance(i,NightmareMiXianPosEnum.XUN_SHI_XD,true));
                }else if (gatePos==i){
                    posDatas.add(MiXianLevelData.PosData.getInstance(i,NightmareMiXianPosEnum.TREASURE_HOUSE_GATE,true));
                }else if (specialBoxPos==i){
                    posDatas.add(MiXianLevelData.PosData.getInstance(i,NightmareMiXianPosEnum.BOX_SPECIAL,true));
                } else {
                    posDatas.add(MiXianLevelData.PosData.getInstance(i,NightmareMiXianPosEnum.EMPTY,true));
                }
            }
            miXianLevelData.setPosDatas(posDatas);
            return miXianLevelData;
        }
        //10%	生成1格聚宝盆、1格传送格
        sum+=10;
        if (sum>=seed){
            for (int i = 1; i <50 ; i++) {
                if (awardsPos.contains(i)){
                    posDatas.add(MiXianLevelData.PosData.getInstance(i,NightmareMiXianPosEnum.GOLD,true));
                    continue;
                }
                posDatas.add(MiXianLevelData.PosData.getInstance(i,NightmareMiXianPosEnum.EMPTY,true));
            }
            posDatas.add(MiXianLevelData.PosData.getInstance(50,NightmareMiXianPosEnum.TREASURE_HOUSE_GATE,true));
            miXianLevelData.setPosDatas(posDatas);
            return miXianLevelData;
        }
        //35%	生成1格元素脉、1格传送格
        sum+=35;
        if (sum>=seed){
            for (int i = 1; i <50 ; i++) {
                if (awardsPos.contains(i)){
                    posDatas.add(MiXianLevelData.PosData.getInstance(i,NightmareMiXianPosEnum.ELE,true));
                    continue;
                }
                posDatas.add(MiXianLevelData.PosData.getInstance(i,NightmareMiXianPosEnum.EMPTY,true));
            }
            posDatas.add(MiXianLevelData.PosData.getInstance(50,NightmareMiXianPosEnum.TREASURE_HOUSE_GATE,true));
            miXianLevelData.setPosDatas(posDatas);
            return miXianLevelData;
        }
        //35%	生成1格摇钱树、1格传送格
        for (int i = 1; i <50 ; i++) {
            if (awardsPos.contains(i)){
                posDatas.add(MiXianLevelData.PosData.getInstance(i,NightmareMiXianPosEnum.COPPER,true));
                continue;
            }
            posDatas.add(MiXianLevelData.PosData.getInstance(i,NightmareMiXianPosEnum.EMPTY,true));
        }
        posDatas.add(MiXianLevelData.PosData.getInstance(50,NightmareMiXianPosEnum.TREASURE_HOUSE_GATE,true));
        miXianLevelData.setPosDatas(posDatas);
        return miXianLevelData;
    }
}
