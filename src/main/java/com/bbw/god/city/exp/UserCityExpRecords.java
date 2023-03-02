package com.bbw.god.city.exp;

import com.bbw.common.ID;
import com.bbw.god.gameuser.UserDataType;
import com.bbw.god.gameuser.UserSingleObj;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

/**
 * 建筑提前体验
 *
 * @author: suhq
 * @date: 2021/11/9 3:31 下午
 */
@Getter
@Setter
public class UserCityExpRecords extends UserSingleObj {
    /** 建筑体验记录 cityId -> 体验次数 */
    private Map<String, Integer> expRecords = new HashMap<>();

    public static UserCityExpRecords instance(long guId) {
        UserCityExpRecords expRecord = new UserCityExpRecords();
        expRecord.setId(ID.INSTANCE.nextId());
        expRecord.setGameUserId(guId);
        return expRecord;
    }

    /**
     * 获取记录
     *
     * @param cityId
     * @return
     */
    public int getRecord(int cityId) {
        Integer record = expRecords.getOrDefault(cityId + "", 0);
        return record;
    }

    /**
     * 添加记录
     *
     * @param cityId
     */
    public void addRecord(int cityId) {
        Integer record = getRecord(cityId);
        record++;
        expRecords.put(cityId + "", record);

    }

    @Override
    public UserDataType gainResType() {
        return UserDataType.CITY_EXP_RECORDS;
    }
}
