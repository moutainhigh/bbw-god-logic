package com.bbw.god.game.zxz.rd;

import com.bbw.god.game.zxz.entity.UserEntryInfo;
import com.bbw.god.game.zxz.entity.ZxzEntry;
import com.bbw.god.rd.RDSuccess;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 词条信息
 * @author: hzf
 * @create: 2022-09-23 08:48
 **/
@Data
public class RdZxzEntry extends RDSuccess {

    /** 词条 */
    private List<RdEntry> entrys;
    /** 词条档位 */
    private List<Integer> entryGears;
    /** 词条等级 */
    private Integer entryInitLv;

    @Data
    public static class RdEntry {
        private Integer entryId;
        private Integer entryLv;


        public static List<RdEntry> getInstances(List<UserEntryInfo.UserEntry> userEntries) {
            List<RdZxzEntry.RdEntry> rd = new ArrayList<>();
            for (UserEntryInfo.UserEntry entry : userEntries) {
                RdZxzEntry.RdEntry rdEntry = new RdZxzEntry.RdEntry();
                rdEntry.setEntryId(entry.getEntryId());
                rdEntry.setEntryLv(entry.getEntryLv());
                rd.add(rdEntry);
            }
            return rd;
        }
    }
}
