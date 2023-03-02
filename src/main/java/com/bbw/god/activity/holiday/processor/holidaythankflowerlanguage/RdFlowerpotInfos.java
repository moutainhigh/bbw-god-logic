package com.bbw.god.activity.holiday.processor.holidaythankflowerlanguage;

import com.bbw.god.rd.RDCommon;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 感恩花语活动
 *
 * @author: huanghb
 * @date: 2022/11/16 9:43
 */
@Data
public class RdFlowerpotInfos extends RDCommon implements Serializable {
    private static final long serialVersionUID = 1L;
    /*奖池信息*/
    private List<RdFlowerpotInfo> flowerpotInfos;

    @Data
    public static class RdFlowerpotInfo implements Serializable {
        private static final long serialVersionUID = 1L;
        /*花盆id*/
        private Integer flowerpotId;
        /*花id*/
        private Integer flowerId = 0;
        /*花数量*/
        private Integer flowerNum = 0;
    }

    /**
     * 初始化
     *
     * @param userThankFlowerLanguage
     * @return
     */
    public static RdFlowerpotInfos instance(UserThankFlowerLanguage userThankFlowerLanguage) {
        Integer[] flowerPotInfos = userThankFlowerLanguage.getFlowerpotInfos();
        RdFlowerpotInfos rdFlowerpotInfos = new RdFlowerpotInfos();
        List<RdFlowerpotInfo> rdFlowerpotInfoList = new ArrayList<>();
        for (int i = 0; i < flowerPotInfos.length; i++) {
            RdFlowerpotInfo rDFlowerpotInfo = new RdFlowerpotInfo();
            rDFlowerpotInfo.setFlowerpotId(i);
            rDFlowerpotInfo.setFlowerId(userThankFlowerLanguage.getFlowerId(i));
            rDFlowerpotInfo.setFlowerNum(userThankFlowerLanguage.getFlowerNum(i));
            rdFlowerpotInfoList.add(rDFlowerpotInfo);
        }
        rdFlowerpotInfos.setFlowerpotInfos(rdFlowerpotInfoList);
        return rdFlowerpotInfos;
    }
}
