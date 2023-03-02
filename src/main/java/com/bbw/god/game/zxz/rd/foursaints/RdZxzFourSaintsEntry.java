package com.bbw.god.game.zxz.rd.foursaints;

import com.bbw.god.game.zxz.entity.ZxzEntry;
import com.bbw.god.rd.RDSuccess;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 诛仙阵 四圣挑战词条信息
 * @author: hzf
 * @create: 2022-12-28 14:30
 **/
@Data
public class RdZxzFourSaintsEntry extends RDSuccess {
    /** 词条信息 */
    private List<RdFourSaintsEntry> fourSaintsEntrys;

    @Data
    public static class RdFourSaintsEntry{
        /** 词条id */
        private Integer entryId;
        /** 词条等级 */
        private Integer entryLv;

        public static RdFourSaintsEntry getInstance(ZxzEntry zxzEntry){
            RdFourSaintsEntry rdFourSaintsEntry = new RdFourSaintsEntry();
            rdFourSaintsEntry.setEntryId(zxzEntry.getEntryId());
            rdFourSaintsEntry.setEntryLv(zxzEntry.getEntryLv());
            return rdFourSaintsEntry;
        }

        public static List<RdFourSaintsEntry> gainEntries(List<ZxzEntry> zxzEntries){
            List<RdFourSaintsEntry> rdFourSaintsEntries = new ArrayList<>();
            for (ZxzEntry zxzEntry : zxzEntries) {
                RdFourSaintsEntry rdFourSaintsEntry = RdFourSaintsEntry.getInstance(zxzEntry);
                rdFourSaintsEntries.add(rdFourSaintsEntry);
            }
            return rdFourSaintsEntries;
        }
    }
}
