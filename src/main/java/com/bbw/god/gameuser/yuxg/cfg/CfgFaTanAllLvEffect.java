package com.bbw.god.gameuser.yuxg.cfg;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 法坛总等级对应效果加成
 *
 * @author: hzf
 * @create: 2022-11-25 17:04
 **/
@Data
public class CfgFaTanAllLvEffect implements Serializable {
    private static final long serialVersionUID = 3095926888734005517L;
    /** 法坛等级 */
    private int faTanLv;
    /** 要提高符图、玉髓产出的概率*/
    private List<Integer> fuTanLiftingProbability;
    /** 需要的符图数量 */
    private Integer needFuTuNum;
    /** 需要的许愿的值 */
    private Integer needWishingValue;

    public int gainReduceFuTuNum() {
        return null == needFuTuNum ? 0 : needFuTuNum;
    }

    public int gainReduceWishingValue() {
        return  null == needWishingValue ? 0 : needWishingValue;
    }

    public List<Integer> gainFuTanLiftingProbability() {
        return null == fuTanLiftingProbability ? new ArrayList<>() : fuTanLiftingProbability ;
    }
}
