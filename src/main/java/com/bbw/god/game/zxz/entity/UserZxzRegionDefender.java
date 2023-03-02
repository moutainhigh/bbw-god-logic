package com.bbw.god.game.zxz.entity;

import com.bbw.god.game.zxz.enums.ZxzDefenderKindEnum;
import lombok.Data;

/**
 * 玩家关卡数据
 * @author: hzf
 * @create: 2022-09-17 10:11
 **/
@Data
public class UserZxzRegionDefender {

    /** 关卡id */
    private String defenderId;
    /** 诛仙阵：状态信息：参考 ZxzStatusEnum 枚举类 */
    private Integer status;
    /** 诛仙阵：野怪种类信息：参考 ZxzDefenderKindEnum 枚举类 */
    private Integer kind;
    /** 是否领取宝箱 */
    private Integer awarded;

    public Integer gainAwarded() {
        return null == awarded ? 0 :awarded;
    }


    /**
     * 判断是不是boss关卡
     * @return
     */
    public boolean ifKingChief(){
        return kind.equals(ZxzDefenderKindEnum.KIND_30.getKind());
    }

    /**
     * 判断是不是精英关卡
     * @return
     */
    public boolean ifKingElite(){
        return kind.equals(ZxzDefenderKindEnum.KIND_20.getKind());
    }

    /**
     * 判断宝箱是否被领取
     * @return
     */
    public boolean ifReceiveBox(){
       return gainAwarded().equals(0);
    }
    /**
     * 领取宝箱奖励
     */
    public void receiveBoxAwarded(){
        awarded = 1;
    }


    /**
     * 设置关卡ID
     *
     * @param regionId
     * @param defender
     */
    public void setDefenderId(Integer regionId, Integer defender) {
        this.defenderId = regionId.toString() + defender;
    }

}
