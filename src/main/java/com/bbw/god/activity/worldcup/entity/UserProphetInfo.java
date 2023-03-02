package com.bbw.god.activity.worldcup.entity;

import com.bbw.common.ID;
import com.bbw.god.cache.tmp.AbstractTmpData;
import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 玩家我是预言家数据
 * @author: hzf
 * @create: 2022-11-12 02:26
 **/
@Data
public class UserProphetInfo extends AbstractTmpData implements Serializable {
    private static final long serialVersionUID = 7073473097670406575L;

    /** id场次标识---->投注国家*/
    private Map<String, Integer> betRecords;
    /** 道具是否扣除 */
    private boolean ifNeedTreasure;
    /** 玩家id */
    private Long gameUserId;

    public static UserProphetInfo instance(long uid,Map<String, Integer> betRecords){
        UserProphetInfo userProphetInfo = new UserProphetInfo();
        userProphetInfo.setId(ID.INSTANCE.nextId());
        userProphetInfo.setGameUserId(uid);
        userProphetInfo.setBetRecords(betRecords);
        userProphetInfo.setIfNeedTreasure(false);
        return userProphetInfo;
    }
    /**
     * 添加投注记录
     * @param identAndBetCountryList
     */
    public void addBetRecord(List<String> identAndBetCountryList){
        for (String vaule : identAndBetCountryList) {
            String[] split = vaule.split("@");
            String id = split[0];
            Integer betCountry = Integer.parseInt(split[1]);
            betRecords.put(id,betCountry);
        }
    }
    public void ResetBetRecord(){
        betRecords = new HashMap<>();

    }
    public Integer gainBetRecord(String id){
        if (null == betRecords) {
            return 0;
        }
        Integer betRecord  = betRecords.get(id);
        if (null == betRecord || 0 == betRecord) {
            return 0;
        }
        return betRecord;
    }

}
