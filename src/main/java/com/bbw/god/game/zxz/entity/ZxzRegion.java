package com.bbw.god.game.zxz.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 诛仙阵区域配置
 * @author: hzf
 * @create: 2022-09-14 17:09
 **/
@Data
public class ZxzRegion implements Serializable {
    private static final long serialVersionUID = 7073473097670406575L;

    /** 诛仙阵区域Id*/
    private Integer regionId;
    /** 关卡数据 */
    private List<ZxzRegionDefender> defenders;

    /** 词条 id@等级 */
    private List<String> entries = new ArrayList<>();

    /**
     * 词条 List<String> ->List<ZxzEntry>
     * @return
     */
    public List<ZxzEntry> gainEntrys() {
        List<ZxzEntry> zEntries = new ArrayList<>();
        for (String entry : entries) {
            String[] entryInfo = entry.split("@");
            Integer entryId = Integer.valueOf(entryInfo[0]);
            Integer entryLv = Integer.valueOf(entryInfo[1]);
            ZxzEntry zxzEntry = new ZxzEntry();
            zxzEntry.setEntryId(entryId);
            zxzEntry.setEntryLv(entryLv);
            zEntries.add(zxzEntry);
        }
        return zEntries;
    }



}
