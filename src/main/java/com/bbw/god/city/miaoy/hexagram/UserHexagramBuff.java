package com.bbw.god.city.miaoy.hexagram;

import com.bbw.common.ID;
import com.bbw.god.gameuser.UserDataType;
import com.bbw.god.gameuser.UserSingleObj;
import lombok.Data;

/**
 * 玩家卦象buff
 * @author liuwenbin
 */
@Data
public class UserHexagramBuff extends UserSingleObj {

    private Integer hexagramId;
    private Integer effectTimes;

    public static UserHexagramBuff getInstance(long uid,int hexagramId,int effectTimes){
        UserHexagramBuff buff=new UserHexagramBuff();
        buff.setHexagramId(hexagramId);
        buff.setEffectTimes(effectTimes);
        buff.setGameUserId(uid);
        buff.setId(ID.INSTANCE.nextId());
        return buff;
    }

    /**
     * 是否有效
     * @return
     */
    public boolean ifActive(){
        return effectTimes>0;
    }

    public void deductTimes(int deduct){
        if (deduct>0){
            effectTimes-=deduct;
        }else {
            effectTimes+=deduct;
        }
    }

    public void resetBuff(int hexagramId,int effectTimes){
        this.hexagramId=hexagramId;
        this.effectTimes=effectTimes;
    }

    @Override
    public UserDataType gainResType() {
        return UserDataType.USER_HEXAGRAM_BUFF;
    }
}
