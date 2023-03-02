package com.bbw.god.gameuser.yuxg;

import com.bbw.common.ListUtil;
import com.bbw.god.gameuser.yuxg.rd.RdWishingDetailed;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户玉虚宫许愿清单
 * @author: hzf
 * @create: 2022-11-25 16:10
 **/
@Data
public class UserYuXGWishingDetailed  implements Serializable {
    private static final long serialVersionUID = 998187098802155732L;

    /** 当前符坛 */
    private int fuTan;
    /** 许愿值 */
    private Integer wishingValue;
    /** 许愿的符图 id 集合 */
    private List<Integer> fuTuIds;

    public UserYuXGWishingDetailed(int fuTan, Integer wishingValue, List<Integer> fuTuIds) {
        this.fuTan = fuTan;
        this.wishingValue = wishingValue;
        this.fuTuIds = fuTuIds;
    }

    public UserYuXGWishingDetailed() {
    }

    public Integer getWishingValue() {
        return null == wishingValue ? 0 : wishingValue;
    }

    public List<Integer> getFuTuIds() {
        return ListUtil.isEmpty(fuTuIds) ? new ArrayList<>() : fuTuIds;
    }

    public List<UserYuXGWishingDetailed>  instance(){
        List<UserYuXGWishingDetailed> rdWishingDetailedList = new ArrayList<>();
        UserYuXGWishingDetailed rdFuTan1 = new UserYuXGWishingDetailed(1,0,new ArrayList<>());
        UserYuXGWishingDetailed rdFuTan2 = new UserYuXGWishingDetailed(2,0,new ArrayList<>());
        UserYuXGWishingDetailed rdFuTan3 = new UserYuXGWishingDetailed(3,0,new ArrayList<>());
        UserYuXGWishingDetailed rdFuTan4 = new UserYuXGWishingDetailed(4,0,new ArrayList<>());
        UserYuXGWishingDetailed rdFuTan5 = new UserYuXGWishingDetailed(5,0,new ArrayList<>());
        rdWishingDetailedList.add(rdFuTan1);
        rdWishingDetailedList.add(rdFuTan2);
        rdWishingDetailedList.add(rdFuTan3);
        rdWishingDetailedList.add(rdFuTan4);
        rdWishingDetailedList.add(rdFuTan5);
        return rdWishingDetailedList;
    }

    /**
     * 添加许愿值
     * @param faTanTotalLevel
     */
    public void addWishingValue(int faTanTotalLevel){
        wishingValue++;
    }

    /**
     * 领取必得奖励
     */
    public void receiveMustGetAward(){
        wishingValue = 0;
    }
    /**
     * eg：1@符图id,符图id,符图id
     * 客户端传的许愿清单转化成 List<UserYuXGWishingDetailed>
     * @param wishingDetaileds
     * @return
     */
    public List<UserYuXGWishingDetailed> gainUserYuXGWishingDetaileds(List<String> wishingDetaileds){
        List<UserYuXGWishingDetailed> userYuXGWishingDetailedList = new ArrayList<>();
        for (String wishingDetailed  : wishingDetaileds) {
            UserYuXGWishingDetailed detailed = new UserYuXGWishingDetailed();
            String[] split = wishingDetailed.split("@");
            String fuTan = split[0];
            String fuTuIds = split[1];
            detailed.setWishingValue(0);
            detailed.setFuTan(Integer.parseInt(fuTan));
            detailed.setFuTuIds(ListUtil.parseStrToInts(fuTuIds));
            userYuXGWishingDetailedList.add(detailed);
        }
        return userYuXGWishingDetailedList;
    }
}
