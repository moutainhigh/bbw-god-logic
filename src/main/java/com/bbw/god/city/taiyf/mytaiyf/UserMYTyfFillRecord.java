package com.bbw.god.city.taiyf.mytaiyf;

import com.bbw.common.ID;
import com.bbw.god.gameuser.UserData;
import com.bbw.god.gameuser.UserDataType;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 梦魇太一府捐献记录
 *
 * @author lzc
 * @date 2021-03-19 09:01:58
 */
@Data
public class UserMYTyfFillRecord extends UserData {
    public static final Integer MAX_CONVERT_TIMES = 5;
    public List<Integer> specialIds;// 捐献的特产ID
    public Integer isConvert = 0;// 是否可兑换 0：不能兑换，1：可兑换
    /** 剩余转换次数 */
    public Integer remainConvertTimes = MAX_CONVERT_TIMES;
    public Integer convertLevel = 0;// 可兑换等级
    public Boolean isFillAll = false;// 是否捐献了所有特产

    public static UserMYTyfFillRecord instance(long guId, int specialId) {
        List<Integer> sIds = new ArrayList<Integer>();
        sIds.add(specialId);
        return instance(guId, sIds);
    }

    public static UserMYTyfFillRecord instance(long guId, List<Integer> specialIds) {
        UserMYTyfFillRecord record = new UserMYTyfFillRecord();
        record.setId(ID.INSTANCE.nextId());
        record.setGameUserId(guId);
        record.setSpecialIds(specialIds);
        return record;
    }

    public static UserMYTyfFillRecord next(long guId, List<Integer> specialIds,int isConvert,int convertLevel) {
        UserMYTyfFillRecord record = new UserMYTyfFillRecord();
        record.setId(ID.INSTANCE.nextId());
        record.setGameUserId(guId);
        record.setSpecialIds(specialIds);
        record.setIsConvert(isConvert);
        record.setConvertLevel(convertLevel);
        return record;
    }

    public void addFillSpecial(int specialId) {
        specialIds.add(specialId);
    }

    /**
     * 更新剩余兑换次数
     */
    public void updateRemainConverTimes() {
        remainConvertTimes--;
        if (remainConvertTimes <= 0) {
            resetConvertInfo();
        }
    }

    /**
     * 充值转换信息
     */
    public void resetConvertInfo() {
        setIsConvert(0);
        setConvertLevel(0);
        setRemainConvertTimes(5);
    }

    @Override
    public UserDataType gainResType() {
        return UserDataType.MYTYF_FILL_RECORD;
    }

}
