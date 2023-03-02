package com.bbw.god.activity.rd;

import com.bbw.god.activity.holiday.processor.HolidayGratefulProcessor;
import com.bbw.god.game.award.Award;
import com.bbw.god.gameuser.task.timelimit.cunz.CunZNPCEnum;
import com.bbw.god.rd.RDCommon;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 活动兑换信息
 *
 * @author fzj
 * @date 2021/11/19 17:44
 */
@Data
public class RDActivityExchangeInfo extends RDCommon implements Serializable {
    private static final long serialVersionUID = 1L;
    /** 兑换信息 */
    private List<RDExchangeInfo> exchangeInfo;
    /** 时间信息 */
    private long dateInfo;


    @Data
    public static class RDExchangeInfo {
        /** NPCID */
        private Integer npcId;
        /** 好感度 */
        private Integer gratitude;
        /** 剩余兑换次数 */
        private Integer exchangeTimes;
        /** 奖励 */
        private Award awards;
    }

    public static RDExchangeInfo getInstance(int npcId, int gratitude, int exchangeTimes){
        RDExchangeInfo rdGratitude = new RDExchangeInfo();
        rdGratitude.setNpcId(npcId);
        rdGratitude.setGratitude(gratitude);
        rdGratitude.setExchangeTimes(exchangeTimes);
        if (npcId != CunZNPCEnum.LAO_ZHE.getType()){
            Award award = new Award();
            award.setAwardId(HolidayGratefulProcessor.TREASURE.get(npcId / 10 - 1));
            award.setNum(1);
            if (npcId == CunZNPCEnum.XIAO_HONG.getType()){
                award.setNum(9);
            }
            award.setItem(60);
            rdGratitude.setAwards(award);
        }
        return rdGratitude;
    }

    public static RDExchangeInfo getInstance(int npcId, int gratitude){
        RDExchangeInfo rdGratitude = new RDExchangeInfo();
        rdGratitude.setNpcId(npcId);
        rdGratitude.setGratitude(gratitude);
        return rdGratitude;
    }
}
