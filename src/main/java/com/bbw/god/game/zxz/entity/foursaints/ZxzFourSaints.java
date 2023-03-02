package com.bbw.god.game.zxz.entity.foursaints;

import com.bbw.god.game.zxz.constant.ZxzConstant;
import com.bbw.god.game.zxz.entity.ZxzEntry;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 诛仙阵 四圣挑战类型
 * @author: hzf
 * @create: 2022-12-26 11:39
 **/
@Data
public class ZxzFourSaints implements Serializable {
    private static final long serialVersionUID = 7073473097670406575L;
    /** 四圣挑战类型 */
    private Integer challengeType;
    /** 限制类型 */
    private List<Integer> limitTypes;
    private List<ZxzFourSaintsDefender> fourSaintsDefenders;
    /** 装配在玩家身上 词条 id@等级 */
    private List<String> userEntries = new ArrayList<>();


    /**
     * 词条 List<String> ->List<ZxzEntry>
     * @return
     */
    public List<ZxzEntry> gainUserEntrys() {
        List<ZxzEntry> zEntries = new ArrayList<>();
        for (String entry : userEntries) {
            String[] entryInfo = entry.split(ZxzConstant.SPLIT_CHAR);
            Integer entryId = Integer.valueOf(entryInfo[0]);
            Integer entryLv = Integer.valueOf(entryInfo[1]);
            ZxzEntry zxzEntry = new ZxzEntry();
            zxzEntry.setEntryId(entryId);
            zxzEntry.setEntryLv(entryLv);
            zEntries.add(zxzEntry);
        }
        return zEntries;
    }



    public static ZxzFourSaints instance(Integer challengeType,List<Integer> attributeLimits,List<ZxzFourSaintsDefender> zxzFourSaintsDefenders,List<String> userEntries){
        ZxzFourSaints zxzFourSaints = new ZxzFourSaints();
        zxzFourSaints.setChallengeType(challengeType);
        zxzFourSaints.setUserEntries(userEntries);
        zxzFourSaints.setLimitTypes(attributeLimits);
        zxzFourSaints.setFourSaintsDefenders(zxzFourSaintsDefenders);
        return zxzFourSaints;
    }

}
