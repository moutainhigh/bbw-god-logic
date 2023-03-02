package com.bbw.god.game.zxz.entity;

import com.bbw.common.ID;
import com.bbw.common.ListUtil;
import com.bbw.god.game.zxz.cfg.CfgZxzEntryEntity;
import com.bbw.god.game.zxz.cfg.CfgZxzLevel;
import com.bbw.god.game.zxz.cfg.ZxzEntryTool;
import com.bbw.god.game.zxz.cfg.ZxzTool;
import com.bbw.god.game.zxz.constant.ZxzConstant;
import com.bbw.god.game.zxz.service.ZxzAnalysisService;
import com.bbw.god.gameuser.UserData;
import com.bbw.god.gameuser.UserDataType;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 玩家词条数据
 * @author: hzf
 * @create: 2022-09-17 16:54
 **/
@Data
public class UserEntryInfo extends UserData  implements Serializable {
    private static final long serialVersionUID = 7073473097670406575L;

    /** 难度类型 */
    private Integer difficulty;

    /**解锁的词条等级 */
    private List<Integer> entryGears;
    /** 词条id@词条等级@词条档位  entryId@entryLv@gear*/
    private List<String> userEntry = new ArrayList<>();


    public static UserEntryInfo instance(long uId, Integer difficulty, List<Integer> entryGears){
        UserEntryInfo userEntryInfo = new UserEntryInfo();
        userEntryInfo.setId(ID.INSTANCE.nextId());
        userEntryInfo.setGameUserId(uId);
        userEntryInfo.setDifficulty(difficulty);
        userEntryInfo.setEntryGears(entryGears);
        userEntryInfo.setUserEntry(new ArrayList<>());
        return userEntryInfo;
    }

    /**
     * 升级词条
     *
     * @param entryId
     */
    public void upgradeEntryLv(Integer entryId) {
        CfgZxzLevel cfgZxzLevel = ZxzTool.getZxzLevel(difficulty);
        CfgZxzEntryEntity cfgZxzEntry = ZxzEntryTool.getEntryById(entryId);
        //获取该区域的词条等级上限
        Integer entryLvLimit = cfgZxzLevel.getEntryLvLimit();
        //获取初始词条等级
        Integer entryInitLv = cfgZxzLevel.getEntryInitLv();
        //档位
        Integer gare = cfgZxzEntry.getGear();
        //词条等级上限
        Integer highestLv = cfgZxzEntry.getHighestLv();

        List<UserEntry> userEntries = ZxzAnalysisService.gainUserEntrys(userEntry);

        UserEntry userEntry = userEntries.stream().
                filter(entry -> entry.getEntryId().equals(entryId))
                .findFirst().orElse(null);
        if (null == userEntry) {
            String uEntry = UserEntry.getInstance(entryId, entryInitLv + 1, gare).toString();
            this.userEntry.add(uEntry);
            return;
        }
        if (userEntry.getEntryLv() >= entryLvLimit || userEntry.getEntryLv() >= highestLv) {
            return;
        }
        String oldEntryInfo = userEntry.toString();
        userEntry.setEntryLv(userEntry.getEntryLv() + 1);
        String newEntryInfo = userEntry.toString();
        this.userEntry.remove(oldEntryInfo);
        this.userEntry.add(newEntryInfo);
    }
    public void addEntryGear(Integer gear){
        entryGears.add(gear);
    }




    @Data
    public static class UserEntry{
        /** 词条Id */
        private Integer entryId;
        /** 词条等级 */
        private Integer entryLv;
        /** 词条档位 */
        private Integer gear;

        public static UserEntry getInstance(Integer entryId, Integer entryLv, Integer gear) {
            UserEntry userEntry = new UserEntry();
            userEntry.setEntryId(entryId);
            userEntry.setEntryLv(entryLv);
            userEntry.setGear(gear);
            return userEntry;

        }

        @Override
        public String toString() {
            return entryId + ZxzConstant.SPLIT_CHAR + entryLv + ZxzConstant.SPLIT_CHAR + gear;
        }

    }

    @Override
    public UserDataType gainResType() {
        return UserDataType.USER_ENTRY;
    }
}
