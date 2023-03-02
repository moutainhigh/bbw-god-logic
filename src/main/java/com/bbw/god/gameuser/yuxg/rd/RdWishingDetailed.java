package com.bbw.god.gameuser.yuxg.rd;

import com.bbw.common.ListUtil;
import com.bbw.god.gameuser.yuxg.UserYuXGWishingDetailed;
import com.bbw.god.rd.RDSuccess;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 返回许愿清单集合
 * @author: hzf
 * @create: 2022-11-28 09:23
 **/
@Data
public class RdWishingDetailed extends RDSuccess implements Serializable {
    private static final long serialVersionUID = 998187098802155732L;
    private List<RdDetailed> wishingDetailed;

    public static List<RdDetailed> instance(){
        List<RdDetailed> rdWishingDetailedList = new ArrayList<>();
        RdDetailed rdFuTan1 = new RdDetailed(1,0,new ArrayList<>());
        RdDetailed rdFuTan2 = new RdDetailed(2,0,new ArrayList<>());
        RdDetailed rdFuTan3 = new RdDetailed(3,0,new ArrayList<>());
        RdDetailed rdFuTan4 = new RdDetailed(4,0,new ArrayList<>());
        RdDetailed rdFuTan5 = new RdDetailed(5,0,new ArrayList<>());
        rdWishingDetailedList.add(rdFuTan1);
        rdWishingDetailedList.add(rdFuTan2);
        rdWishingDetailedList.add(rdFuTan3);
        rdWishingDetailedList.add(rdFuTan4);
        rdWishingDetailedList.add(rdFuTan5);
        return rdWishingDetailedList;
    }

    @Data
    public static class RdDetailed{
        /** 当前符坛 */
        private int fuTan;
        /**许愿值 */
        private Integer wishingValue;
        /** 许愿的符图 id 集合*/
        private List<Integer> fuTuIds;


        public RdDetailed(int fuTan, Integer wishingValue, List<Integer> fuTuIds) {
            this.fuTan = fuTan;
            this.wishingValue = wishingValue;
            this.fuTuIds = fuTuIds;
        }

        public RdDetailed() {
        }

        /**
         * 获取许愿清单 有加符图信息
         * @param detaileds
         * @return
         */
        public static List<RdDetailed> getRdWishingDetaileds(List<UserYuXGWishingDetailed> detaileds) {
            if (ListUtil.isEmpty(detaileds)) {
                return new ArrayList<>();
            }
            List<RdDetailed> rdDetaileds = new ArrayList<>();
            for (UserYuXGWishingDetailed detailed : detaileds) {
                RdDetailed rd = new RdDetailed();
                rd.setFuTan(detailed.getFuTan());
                rd.setWishingValue(detailed.getWishingValue());
                rd.setFuTuIds(detailed.getFuTuIds());
                rdDetaileds.add(rd);
            }
            return rdDetaileds;
        }
        /**
         * 获取许愿清单 没有加符图信息
         * @param detaileds
         * @return
         */
        public static List<RdDetailed> getWishingDetailedExcludeFuTu(List<UserYuXGWishingDetailed> detaileds) {
            if (ListUtil.isEmpty(detaileds)) {
                return new ArrayList<>();
            }
            List<RdDetailed> rdDetaileds = new ArrayList<>();
            for (UserYuXGWishingDetailed detailed : detaileds) {
                RdDetailed rd = new RdDetailed();
                rd.setFuTan(detailed.getFuTan());
                rd.setWishingValue(detailed.getWishingValue());
                rdDetaileds.add(rd);
            }
            return rdDetaileds;
        }

        public static RdDetailed getDetailed(UserYuXGWishingDetailed detailed) {
            RdDetailed rd = new RdDetailed();
            if (null == detailed) {
                return null;
            }
            rd.setFuTan(detailed.getFuTan());
            rd.setWishingValue(detailed.getWishingValue());
            rd.setFuTuIds(detailed.getFuTuIds());
            return rd;
        }
    }
}
