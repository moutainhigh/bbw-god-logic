package com.bbw.god.gameuser.businessgang.digfortreasure;

import com.bbw.common.ListUtil;
import com.bbw.god.rd.RDSuccess;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 所有挖宝信息
 *
 * @author: huanghb
 * @date: 2022/10/25 17:25
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RdDigTreasureInfos extends RDSuccess implements Serializable {
    private static final long serialVersionUID = 7409189386250719040L;
    private List<RdDigTreasureInfo> rdDigTreasureInfos = new ArrayList<>();

    /**
     * 挖宝返回信息初始化
     *
     * @param userDigTreasure
     * @return
     */
    public static RdDigTreasureInfos getInstance(List<UserDigTreasure> userDigTreasure) {
        RdDigTreasureInfos rd = new RdDigTreasureInfos();
        if (ListUtil.isEmpty(userDigTreasure)) {
            return new RdDigTreasureInfos();
        }
        List<RdDigTreasureInfo> rdDigTreasureInfos = userDigTreasure.stream().map(RdDigTreasureInfo::getInstance).collect(Collectors.toList());
        rd.setRdDigTreasureInfos(rdDigTreasureInfos);
        return rd;
    }

    @Data
    public static class RdDigTreasureInfo implements Serializable {
        private static final long serialVersionUID = 7409189386250719040L;
        /** 挖宝地点 */
        private Integer pos;
        /** 我的挖宝--当前层数 */
        private Integer floor;

        /**
         * 挖宝返回信息初始化
         *
         * @param userDigTreasure
         * @return
         */
        public static RdDigTreasureInfo getInstance(UserDigTreasure userDigTreasure) {
            RdDigTreasureInfo rd = new RdDigTreasureInfo();
            rd.setPos(userDigTreasure.getBaseId());
            rd.setFloor(userDigTreasure.getCurrentFloor());
            return rd;
        }
    }
}
