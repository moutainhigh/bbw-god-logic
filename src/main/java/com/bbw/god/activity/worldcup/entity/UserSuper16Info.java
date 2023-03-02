package com.bbw.god.activity.worldcup.entity;

import com.bbw.common.ID;
import com.bbw.god.cache.tmp.AbstractTmpData;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 玩家超级16强数据
 * @author: hzf
 * @create: 2022-11-12 02:19
 **/
@Data
public class UserSuper16Info extends AbstractTmpData implements Serializable {
    private static final long serialVersionUID = 7073473097670406575L;

    private long gameUserId;
    /** 投注记录 分组 eg:A----> 投注记录 */
    private Map<String, BetRecord> betRecords;

    public static UserSuper16Info instance(long uid,Map<String, BetRecord> betRecords){
        UserSuper16Info userSuper16Info = new UserSuper16Info();
        userSuper16Info.setGameUserId(uid);
        userSuper16Info.setId(ID.INSTANCE.nextId());
        userSuper16Info.setBetRecords(betRecords);
        return userSuper16Info;
    }


    /**
     * 添加投注记录
     * @param group
     * @param betRecord
     */
    public void addBetRecord(String group,BetRecord betRecord){
        betRecords.put(group,betRecord);
    }

    /**
     * 重置投注记录
     * @param group
     */
    public void resetBetRecord(String group){
        BetRecord betRecord = betRecords.get(group);
        betRecord.setBetCountrys(null);
        betRecords.put(group,betRecord);
    }

    /**
     * 根据分组查询投注记录
     * @param group
     * @return
     */
    public BetRecord gainBetRecord(String group){
        if (null == betRecords) {
            return new BetRecord();
        }
        BetRecord betRecord = betRecords.get(group);
        if (null == betRecord) {
            return new BetRecord();
        }
        return betRecord;
    }
    public List<Integer> gainbetCountrys(String group){
        BetRecord betRecord = gainBetRecord(group);
        if (null == betRecord.getBetCountrys()) {
            return new ArrayList<>();
        }
        return betRecord.getBetCountrys();
    }
    public boolean ifNeedTreasure(String group){
        BetRecord betRecord = gainBetRecord(group);
        if (null == betRecord) {
            return false;
        }
        return betRecord.isIfNeedTreasure();
    }

    @Data
    public static class BetRecord{
        /** 投注的国家 */
        List<Integer> betCountrys;
        private boolean ifNeedTreasure;


        public void addBetCountrys(List<Integer> betCountrys){
            this.betCountrys = betCountrys;
        }
    }


}
