package com.bbw.god.activity;

import com.bbw.common.ID;
import com.bbw.god.gameuser.UserDataType;
import com.bbw.god.gameuser.UserSingleObj;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author suchaobin
 * @description 玩家上仙祝福记录
 * @date 2020/10/20 9:14
 **/
@Data
@EqualsAndHashCode(callSuper = true)
public class UserGodBlessRecord extends UserSingleObj implements Serializable {
    private static final long serialVersionUID = -5860191911818459238L;
    // 奖励记录，格式为cfgId,index;cfgId,index;
    private Map<String, String> recordMap = new HashMap<>();
    private Date lastUpdateTime;

    public static UserGodBlessRecord getInstance(long uid) {
        UserGodBlessRecord record = new UserGodBlessRecord();
        record.setGameUserId(uid);
        record.setId(ID.INSTANCE.nextId());
        return record;
    }

    /**
     * 添加奖励记录
     *
     * @param cfgId
     * @param index
     */
    public void addRecord(int cfgId, int index) {
        this.recordMap.put(String.valueOf(cfgId), String.valueOf(index));
    }

    public List<Integer> gainAwardedCfgIds() {
        Set<String> keySet = this.recordMap.keySet();
        return keySet.stream().map(Integer::parseInt).collect(Collectors.toList());
    }

    /**
     * 玩家资源类型
     *
     * @return
     */
    @Override
    public UserDataType gainResType() {
        return UserDataType.USER_GOD_BLESS_RECORD;
    }
}
