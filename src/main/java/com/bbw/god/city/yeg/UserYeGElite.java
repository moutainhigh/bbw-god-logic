package com.bbw.god.city.yeg;

import com.bbw.common.ID;
import com.bbw.god.gameuser.UserDataType;
import com.bbw.god.gameuser.UserSingleObj;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @author 作者 ：lwb
 * @version 创建时间：2020年2月27日 下午3:08:00
 * 类说明
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class UserYeGElite extends UserSingleObj implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer attackJ = 10;// 金类精英野怪的所有卡牌初始为10级
    private Integer attackM = 10;// 木类精英野怪的所有卡牌初始为10级
    private Integer attackS = 10;// 水类精英野怪的所有卡牌初始为10级
    private Integer attackH = 10;// 火类精英野怪的所有卡牌初始为10级
    private Integer attackT = 10;// 土类精英野怪的所有卡牌初始为10级

    public static UserYeGElite instance(long uid) {
        UserYeGElite uy = new UserYeGElite();
        uy.setGameUserId(uid);
        uy.setId(ID.INSTANCE.nextId());
        return uy;
    }

    @Override
    public UserDataType gainResType() {
        return UserDataType.YeG_ELITE;
    }

}
