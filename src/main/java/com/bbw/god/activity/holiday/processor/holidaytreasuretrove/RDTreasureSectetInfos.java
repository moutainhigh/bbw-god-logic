package com.bbw.god.activity.holiday.processor.holidaytreasuretrove;

import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.game.config.mall.CfgMallEntity;
import com.bbw.god.game.config.mall.MallTool;
import com.bbw.god.mall.RDMallList;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 藏宝秘境信息
 *
 * @author: huanghb
 * @date: 2021/12/17 16:08
 */
@Data
public class RDTreasureSectetInfos extends RDMallList implements Serializable {
    private static final long serialVersionUID = 1L;
    /*宝藏值*/
    private Integer treasureTroveValue;
    /*购买次数*/
    private Integer purchaseNum;
    /*宝藏令数量*/
    private Integer treasureTroveOrderNum;
    /*是否开启大奖池*/
    private boolean openBigTreasure;
    private long remainTime;
    private Integer curType;
    /*奖池信息*/
    private List<RDTreasureSectetInfo> treasureSectetInfos;

    @Data
    public static class RDTreasureSectetInfo implements Serializable {
        private static final long serialVersionUID = 1L;
        /*奖品Id*/
        private Integer id;
        /*是否大奖*/
        private boolean bigAward;
        /*奖品信息*/
        private Award award;
    }

    /**
     * 根据奖池初始化藏宝秘境
     *
     * @param userTreasureTrove
     * @return
     */
    public static RDTreasureSectetInfos instance(UserTreasureTrove userTreasureTrove) {
        Integer[] mallIds = userTreasureTrove.getMallIds();
        RDTreasureSectetInfos rdTreasureSectetInfos = new RDTreasureSectetInfos();
        List<RDTreasureSectetInfos.RDTreasureSectetInfo> rdTreasureSectetInfoList = new ArrayList<>();
        for (Integer mallId : mallIds) {
            RDTreasureSectetInfo rdTreasureSectetInfo = new RDTreasureSectetInfo();
            if (0 == mallId || 1 == mallId) {
                mallId = 0;
            }
            rdTreasureSectetInfo.setId(mallId);
            if (0 == mallId) {
                rdTreasureSectetInfoList.add(rdTreasureSectetInfo);
                continue;
            }
            CfgTreasureTrove.TroveAward troveAward = TreasureTroveTool.getTroveAward(mallId);
            rdTreasureSectetInfo.setBigAward(troveAward.getBigAward());
            CfgMallEntity cfgMallEntity = MallTool.getMall(mallId);
            Award award = Award.instance(cfgMallEntity.getGoodsId(), AwardEnum.fromValue(cfgMallEntity.getItem()), cfgMallEntity.getNum());
            rdTreasureSectetInfo.setAward(award);
            rdTreasureSectetInfo.setBigAward(troveAward.getBigAward());
            rdTreasureSectetInfoList.add(rdTreasureSectetInfo);
        }
        rdTreasureSectetInfos.setTreasureSectetInfos(rdTreasureSectetInfoList);
        return rdTreasureSectetInfos;
    }
}
