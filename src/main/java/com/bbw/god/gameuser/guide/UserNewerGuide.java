package com.bbw.god.gameuser.guide;

import com.bbw.common.ID;
import com.bbw.god.gameuser.UserDataType;
import com.bbw.god.gameuser.UserSingleObj;
import com.bbw.god.gameuser.guide.v3.NewerGuideEnum;
import lombok.Data;

/**
 * @author suhq
 * @description 玩家新手引导数据
 * @date 2019-12-27 06:30
 **/
@Data
public class UserNewerGuide extends UserSingleObj {
    private Integer newerGuide = NewerGuideEnum.START.getStep();//新手引导步数
    private Boolean isPassNewerGuide = false;//是否通过新手引导

    public static UserNewerGuide getInstance(long uid, int newerGuide, boolean isPassNewerGuide) {
        UserNewerGuide instance = new UserNewerGuide();
        instance.setId(ID.INSTANCE.nextId());
        instance.setGameUserId(uid);
        instance.setNewerGuide(newerGuide);
        instance.setIsPassNewerGuide(isPassNewerGuide);
        return instance;
    }

    public void updateNewerGuide(int newerGuide) {
        setNewerGuide(newerGuide);
        //兼容旧版本 步骤是10的
        if (newerGuide == NewerGuideEnum.YE_GUAI.getStep() || newerGuide == 10) {
            setIsPassNewerGuide(true);
        }
    }

    @Override
    public UserDataType gainResType() {
        return UserDataType.NEWER_GUIDE;
    }
}
