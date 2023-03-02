package com.bbw.god.activity.worldcup.entity;

import com.bbw.common.ID;
import com.bbw.common.ListUtil;
import com.bbw.god.cache.tmp.AbstractTmpData;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 玩家我是竞猜王数据
 * @author: hzf
 * @create: 2022-11-12 02:27
 **/
@Data
public class UserQuizKingInfo extends AbstractTmpData implements Serializable {
    private static final long serialVersionUID = 7073473097670406575L;

    /** id场次标识 --->投注记录 */
    private Map<String, List<BetRecord>> betRecords;
    /** 玩家id */
    private long gameUserId;

    public static UserQuizKingInfo instance(long uid, Map<String, List<BetRecord>> betRecords){
        UserQuizKingInfo userQuizKingInfo = new UserQuizKingInfo();
        userQuizKingInfo.setId(ID.INSTANCE.nextId());
        userQuizKingInfo.setGameUserId(uid);
        userQuizKingInfo.setBetRecords(betRecords);
        return userQuizKingInfo;
    }

    public void delBetRecord(String id){
        betRecords.remove(id);
    }
    /**
     * 添加投注记录
     * @param id
     * @param newBets
     */
    public void addBetRecord(String id,List<BetRecord> newBets){

        List<BetRecord> betedRecords = gainBetRecords(id);
        if (ListUtil.isEmpty(betedRecords)) {
            betedRecords.addAll(newBets);
        }else {
            for (BetRecord newBet : newBets) {
                BetRecord record = betedRecords.stream().filter(tmp -> tmp.getBetCountry().equals(newBet.getBetCountry())).findFirst().orElse(null);
                if (null != record){
                    record.setBetNum(record.getBetNum() + newBet.getBetNum());
                }else {
                    betedRecords.add(newBet);
                }
            }
        }
        betRecords.put(id, betedRecords);
    }

    /**
     * 根据场次标识（id）获取投注记录
     * @param id
     * @return
     */
    public List<BetRecord> gainBetRecords(String id){
        List<BetRecord> betRecords = this.betRecords.get(id);
        if (ListUtil.isEmpty(betRecords)){
            return new ArrayList<>();
        }
        return betRecords;
    }


    @Data
    public static class BetRecord{
        /** 投注的国家 */
        private Integer betCountry;
        /** 投注的数量 */
        private Integer betNum;


        /**
         * 计算要扣除道具的数量
         * @param betRecords
         * @return
         */
        public Integer countNeedNum(List<BetRecord> betRecords){
            int num = 0 ;
            for (UserQuizKingInfo.BetRecord record : betRecords) {
                num = num + record.getBetNum();
            }
            return  num;
        }

        /**
         * 将 betCountry@betNum,betCountry@betNum, 转成成 List<BetRecord>
         * @param betRecords
         * @return
         */
        public List<BetRecord> gainBetRecord(List<String> betRecords){
            List<BetRecord> betRecordList = new ArrayList<>();
            for (String betRecord : betRecords) {
                BetRecord record = new BetRecord();
                String[] split = betRecord.split("@");
                Integer betCountry = Integer.parseInt(split[0]);
                Integer betNum = Integer.parseInt(split[1]);
                record.setBetCountry(betCountry);
                record.setBetNum(betNum);
                betRecordList.add(record);
            }
            return betRecordList;
        }
    }

}
