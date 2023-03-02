package com.bbw.god.city.cunz;

import com.bbw.god.city.RDCityInfo;
import com.bbw.god.rd.RDCommon;
import com.bbw.god.rd.RDCommon.RDCardInfo;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * 到达村庄
 *
 * @author suhq
 * @date 2019年3月18日 下午3:52:23
 */
@Getter
@Setter
@ToString(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RDArriveChunZ extends RDCityInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer addedGoldEle = null;// 金元素
    private Integer addedWoodEle = null;// 木元素
    private Integer addedWaterEle = null;// 水元素
    private Integer addedFireEle = null;// 火元素
    private Integer addedEarthEle = null;// 土元素
    private List<RDCardInfo> cards = null;// 卡牌
    private Integer cunZTalk;//坊间怪谈
    private Integer cunZTaskId;//村庄任务
    /** 秘闻id */
    private Integer secretAchievementId;
    /** 未验证成就id */
    private List<Integer> notVerifySecretAchievement;
    /** 已验证成就id */
    private List<Integer> verifySecretAchievement;

    public static RDArriveChunZ fromRDCommon(RDCommon rd) {
        RDArriveChunZ rdArrive = new RDArriveChunZ();
        rdArrive.setAddedGoldEle(rd.getAddedGoldEle());
        rdArrive.setAddedWoodEle(rd.getAddedWoodEle());
        rdArrive.setAddedWaterEle(rd.getAddedWaterEle());
        rdArrive.setAddedFireEle(rd.getAddedFireEle());
        rdArrive.setAddedEarthEle(rd.getAddedEarthEle());
        rdArrive.setCards(rd.getCards());

        // 顶层村庄获得的元素、卡牌数据置空
        rd.setAddedGoldEle(null);
        rd.setAddedWoodEle(null);
        rd.setAddedWaterEle(null);
        rd.setAddedFireEle(null);
        rd.setAddedEarthEle(null);
        rd.setCards(null);

        return rdArrive;
    }

}
