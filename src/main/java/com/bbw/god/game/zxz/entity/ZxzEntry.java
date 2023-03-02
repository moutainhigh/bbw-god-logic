package com.bbw.god.game.zxz.entity;

import com.bbw.common.ListUtil;
import com.bbw.god.game.combat.data.CombatBuff;
import com.bbw.god.game.zxz.constant.ZxzConstant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 诛仙阵词条
 * @author: hzf
 * @create: 2022-09-22 21:40
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ZxzEntry implements Serializable {
    private static final long serialVersionUID = 7073473097670406575L;

    /** 词条Id */
    private Integer entryId;
    /** 词条等级 */
    private Integer entryLv;

    @Override
    public String toString() {
        return entryId + ZxzConstant.SPLIT_CHAR + entryLv;
    }

    /**
     * List<ZxzEntry> ====> List<String>
     * @param zxzEntries
     * @return
     */
    public static List<String> gainEntryToString(List<ZxzEntry> zxzEntries){
        List<String> entries = new ArrayList<String>();
        for (ZxzEntry zxzEntry : zxzEntries) {
            entries.add(zxzEntry.toString());
        }
        return entries;
    }
    /**
     * 获取词条加成
     * @param entrys
     * @return
     */
    public  List<CombatBuff> gainEntryCombatBuff(List<ZxzEntry> entrys){
        List<CombatBuff> combatBuffs = new ArrayList<>();
        if (ListUtil.isEmpty(entrys)) {
            return combatBuffs;
        }
        for (ZxzEntry entry : entrys) {
            CombatBuff entryCombatBuff = new CombatBuff();
            entryCombatBuff.setRuneId(entry.getEntryId());
            entryCombatBuff.setLevel(entry.getEntryLv());
            combatBuffs.add(entryCombatBuff);
        }
        return combatBuffs;
    }
}
