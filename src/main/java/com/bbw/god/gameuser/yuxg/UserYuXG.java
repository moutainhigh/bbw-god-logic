package com.bbw.god.gameuser.yuxg;

import com.bbw.common.ID;
import com.bbw.common.ListUtil;
import com.bbw.god.gameuser.UserDataType;
import com.bbw.god.gameuser.UserSingleObj;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 玩家玉虚宫数据
 *
 * @author fzj
 * @date 2021/10/29 11:20
 */
@Data
public class UserYuXG extends UserSingleObj implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final int NOT_USE_SHEN_SHUI = 0;
    /** 符坛 默认开启前三个符坛*/
    private List<Integer> fuTan = Arrays.asList(1, 2, 3);
    /** 当前符坛 */
    private Integer curFuTan = 1;
    /** 熔炼值 */
    private Integer meltValue = 0;
    /** 是否使用神水 0-未使用、1-使用 */
    private int activeShenShui;
    /** 许愿清单*/
    private List<UserYuXGWishingDetailed> wishingDetailed;
    /** 当前的许愿值 */
    private int currentWishingValue;

    public List<UserYuXGWishingDetailed> gainWishingDetaileds() {
        return ListUtil.isEmpty(wishingDetailed) ? new ArrayList<>() : wishingDetailed;
    }

    /**
     * 获取符坛
     * @param fuTan
     * @return
     */
    public UserYuXGWishingDetailed getUserYuXGWishingDetailed(int fuTan) {
        if (ListUtil.isEmpty(wishingDetailed)){
            return null;
        }
       return this.wishingDetailed.stream().filter(wishing -> wishing.getFuTan() == fuTan).findFirst().orElse(null);
    }

    /**
     * 获取符坛的许愿值
     * @param fuTan
     * @return
     */
    public Integer getUserFuTanWishingValue(int fuTan){
        UserYuXGWishingDetailed userYuXGWishingDetailed = getUserYuXGWishingDetailed(fuTan);
        return null == userYuXGWishingDetailed ? 0 : userYuXGWishingDetailed.getWishingValue();
    }

    /**
     * 获取符坛对应的许愿符图
     * @param fuTan
     * @return
     */
    public List<Integer> getUserFuTanWishingFuTu(int fuTan){
        UserYuXGWishingDetailed userYuXGWishingDetailed = getUserYuXGWishingDetailed(fuTan);
        return null == userYuXGWishingDetailed ? new ArrayList<>() : userYuXGWishingDetailed.getFuTuIds();
    }

    /**
     * 重置许愿
     */
    public void resetWishing(){
        for (UserYuXGWishingDetailed wishingDetailed : wishingDetailed) {
            wishingDetailed.setFuTuIds(new ArrayList<>());
        }
    }

    /**
     * 判断是否满足许愿条件
     * @param userYuXGWishingDetaileds
     * @param faTanTotalLv
     * @return
     */
    public boolean ifSatisfyFuTuNum(List<UserYuXGWishingDetailed> userYuXGWishingDetaileds,int faTanTotalLv){
        if (userYuXGWishingDetaileds.size() != 5){
            return false;
        }
        int wishingFuTuNum = YuXGTool.getWishingFuTuNum(faTanTotalLv);
        for (UserYuXGWishingDetailed userYuXGWishingDetailed : userYuXGWishingDetaileds) {
         boolean ifWishingNum =  userYuXGWishingDetailed.getFuTuIds().size() < wishingFuTuNum || userYuXGWishingDetailed.getFuTuIds().size() > YuXGTool.getYuXGInfo().getDefaultFutuNum();
            if (ifWishingNum) {
                return false;
            }
        }
        return true;

    }

    public static UserYuXG getInstance(long uid) {
        UserYuXG userYuXG = new UserYuXG();
        userYuXG.setId(ID.INSTANCE.nextId());
        userYuXG.setGameUserId(uid);
        userYuXG.setActiveShenShui(NOT_USE_SHEN_SHUI);
        return userYuXG;
    }
    @Override
    public UserDataType gainResType() {
        return UserDataType.USER_YUXG;
    }
}