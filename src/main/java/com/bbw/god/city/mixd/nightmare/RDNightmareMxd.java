package com.bbw.god.city.mixd.nightmare;

import com.bbw.god.fight.RDFightsInfo;
import com.bbw.god.game.award.Award;
import com.bbw.god.rd.RDCommon;
import lombok.Data;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 说明：
 *
 * @author lwb
 * date 2021-05-27
 */
@Data
public class RDNightmareMxd extends RDCommon {

    private Integer inTreasureHouse;
    /**
     * 关卡数据
     */
    private String levelData;
    /**
     * 玩家位置
     */
    private Integer pos;
    /**
     * 当前所在位置格子类型
     */
    private Integer currentPosType;
    private Integer currentLevel;
    /**
     * 血量
     */
    private Integer blood;
    /**
     * 奖励
     */
    private List<Award> gainAwards;
    /**
     * 是否显示所有位置
     */
    private Integer showAll=0;
    /**
     * AI的ID
     */
    private Long opponentId;
    /**
     * 扣血、加血
     */
    private Integer addedBlood;
    /**
     * 背包物品
     */
    private List<Award> bag;

    /**
     * 剩余挑战层数
     */
    private Integer remainChallengeLayers;

    /**
     * 挑战层数最近增长时间
     */
    private Long layersLastIncTime;

    private List<RDFightsInfo.RDFightCard> aiCards;//AI卡组
    private List<Integer> myCards;//我的卡组
    private List<CardUsed> myUsedCards;//我的镇守卡牌
    private String levelOwnerName;

    private String smeltMsg="";
    /**
     * 添加返回关卡数据
     * @param miXian
     */
    public void addLevelData(UserNightmareMiXian miXian){
        String content="%s#%s;";
        levelData="";
        List<MiXianLevelData.PosData> collect = null;
        if (miXian.isInTreasureHouse()){
            collect = miXian.getTreasureHouseData().getPosDatas().stream().sorted(Comparator.comparing(MiXianLevelData.PosData::getPos)).collect(Collectors.toList());
        }else{
           collect = miXian.getLevelData().getPosDatas().stream().sorted(Comparator.comparing(MiXianLevelData.PosData::getPos)).collect(Collectors.toList());
        }
        for (MiXianLevelData.PosData posData : collect) {
            if (posData.isShow()){
                levelData+=String.format(content,posData.getTye(),1) ;
            }else if (posData.getTye()!=NightmareMiXianPosEnum.EMPTY.getType()){
                levelData+=String.format(content,NightmareMiXianPosEnum.EVENT.getType(),0) ;
            }else {
                levelData+=String.format(content,posData.getTye(),0) ;
            }
        }
    }

    public static RDNightmareMxd getFightResultInstance(int blood){
        RDNightmareMxd rd=new RDNightmareMxd();
        rd.setAddedBlood(blood);
        rd.setGainAwards(new ArrayList<>());
        rd.setShowAll(0);
        return rd;
    }
    @Data
    public static class CardUsed{
        private Integer id;
        private Integer mxdLevel;
    }

    public void setGainAwards(List<Award> gainAwards) {
        this.gainAwards = gainAwards;
    }
}