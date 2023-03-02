package com.bbw.god.activity.worldcup.entity;

import com.bbw.common.ID;
import com.bbw.god.cache.tmp.AbstractTmpData;
import com.bbw.god.gameuser.UserDataType;
import com.bbw.god.gameuser.UserSingleObj;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * 玩家决战8强
 * @author: hzf
 * @create: 2022-11-12 02:25
 **/
@Data
public class UserDroiyan8Info extends AbstractTmpData implements Serializable {
    private static final long serialVersionUID = 7073473097670406575L;
    /** 玩家id */
    private Long gameUserId;
    /** 标识 ---> 投注记录 */
    private Map<String, BetRecord> betRecords;

    public static UserDroiyan8Info instance(long uid,Map<String, BetRecord> betRecords){
        UserDroiyan8Info userDroiyan8Info = new UserDroiyan8Info();
        userDroiyan8Info.setId(ID.INSTANCE.nextId());
        userDroiyan8Info.setGameUserId(uid);
        userDroiyan8Info.setBetRecords(betRecords);
        return userDroiyan8Info;
    }

    /**
     * 根据标识查询投注记录
     * @param Id
     * @return
     */
    public BetRecord gainBetRecord(String Id){
        if (null == betRecords) {
            return new BetRecord();
        }
        BetRecord betRecord = betRecords.get(Id);
        if (null == betRecord) {
            return new BetRecord();
        }
        return betRecord;
    }

    /**
     * 添加投注记录
     * @param id
     * @param betRecord
     */
    public void addBetRecord(String id, BetRecord betRecord){
        betRecords.put(id,betRecord);
    }

    /**
     * 重置投注记录
     * @param id
     */
    public void resetBetRecord(String id){
        BetRecord betRecord = betRecords.get(id);
        betRecord.setBetCountry(null);
        betRecords.put(id,betRecord);
    }

    /**
     * 获取投注记录
     * @param id 场次标识
     * @return
     */
    public Integer gainbetCountry(String id){
        BetRecord betRecord = gainBetRecord(id);
        if (null == betRecord.getBetCountry() || 0 == betRecord.getBetCountry()) {
            return 0;
        }
        return betRecord.getBetCountry();
    }
    public boolean ifNeedTreasure(String id){
        BetRecord betRecord = gainBetRecord(id);
        if (null == betRecord) {
            return false;
        }
        return betRecord.isIfNeedTreasure();
    }

    @Data
    public static class BetRecord{
        /** 投注的国家 */
        private Integer betCountry;
        /** 道具是否扣除 */
        private boolean ifNeedTreasure;

        /** 添加投注 */
        public void  addBetCountry(Integer betCountry){
            this.betCountry = betCountry;
        }
    }
}
