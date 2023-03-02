package com.bbw.god.game.zxz.entity;

import com.bbw.common.DateUtil;
import com.bbw.common.ID;
import com.bbw.god.game.combat.runes.RunesEnum;
import com.bbw.god.game.zxz.cfg.CfgZxzEntryEntity;
import com.bbw.god.game.zxz.cfg.ZxzEntryTool;
import com.bbw.god.game.zxz.enums.ZxzEntryTypeEnum;
import com.bbw.god.game.zxz.enums.ZxzStatusEnum;
import com.bbw.god.gameuser.UserData;
import com.bbw.god.gameuser.UserDataType;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 玩家区域攻打记录
 * @author: hzf
 * @create: 2022-09-17 09:42
 **/
@Data
public class UserZxzRegionInfo extends UserData implements Serializable {
    private static final long serialVersionUID = 7073473097670406575L;

    /** 难度类型 */
    private Integer difficulty;
    /** 区域id */
    private Integer regionId;
    /** 进度 */
    private Integer progress;
    /** 复活次数 */
    private Integer surviceTimes;
    /** 通关等级（区域等级） */
    private Integer clearanceLv;
    /** 是否进入区域中，进入区域不可以扫荡 */
    private boolean into;
    /** 诛仙阵：状态信息：参考 ZxzStatusEnum 枚举类 */
    private Integer status;
    /** 词条 id@等级 */
    private List<String> entries = new ArrayList<>();

    /** 区域关卡数据 */
    private List<UserZxzRegionDefender> regionDefenders;
    /** 上一次的通关的词条等级 */
    private Integer lastClearanceLv;
    /** 最近一次刷新时间 */
    private Date lastRefreshDate;

    public Integer gainClearanceLv() {
        return null == clearanceLv ? 0 :clearanceLv;
    }



    public UserZxzRegionInfo getInstance(Integer difficulty, Integer regionId, List<UserZxzRegionDefender> regionDefenders, long uId) {
        UserZxzRegionInfo instance = new UserZxzRegionInfo();
        instance.setId(ID.INSTANCE.nextId());
        instance.setGameUserId(uId);
        instance.setDifficulty(difficulty);
        instance.setRegionId(regionId);
        instance.setProgress(0);
        instance.setSurviceTimes(0);
        instance.setClearanceLv(0);
        instance.setStatus(ZxzStatusEnum.ABLE_ATTACK.getStatus());
        instance.setEntries(new ArrayList<>());
        instance.setRegionDefenders(regionDefenders);
        instance.setLastClearanceLv(0);
        instance.setLastRefreshDate(DateUtil.now());
        return instance;
    }

    /**
     * 扫荡
     */
    public void mopUp(){
        into =  true;
        progress = 6;
        for (UserZxzRegionDefender defender : regionDefenders) {
            defender.setStatus(ZxzStatusEnum.PASSED.getStatus());
        }
    }


    /**
     * 添加词条
     *
     * @param entries
     * @return
     */
    public void addEntries(List<String> entries) {
        getEntries().addAll(entries);
    }

    /**
     * 获取关卡
     *
     * @param defenderId
     * @return
     */
    public UserZxzRegionDefender gainRegionDefender(Integer defenderId) {
        return regionDefenders.stream()
                .filter(defender -> defender.getDefenderId().equals(String.valueOf(defenderId)))
                .findFirst().orElse(null);

    }

    /**
     * 删除词条
     * @param entries
     * @return
     */
    public void delEntries(List<String> entries){
        getEntries().removeAll(entries);
    }

    /** 添加复活次数 */
    public void addSurviceTimes(){
        surviceTimes++;
    }

    /**
     * 计算区域等级
     * @return
     */
    public Integer computeRegionLv(){
        int regionLv = 0;
        //词条集合
        List<ZxzEntry> zxzEntries = gainEntrys();
        for (ZxzEntry zxzEntry : zxzEntries) {
            CfgZxzEntryEntity entry = ZxzEntryTool.getEntryById(zxzEntry.getEntryId());
            if (entry.getType().equals(ZxzEntryTypeEnum.ENTRY_TYPE_10.getEntryType())) {
                //判断是不是灵装词条
                if (entry.getEntryId() == RunesEnum.LING_ZHUANG_ENTRY.getRunesId()) {
                    regionLv += zxzEntry.getEntryLv() * 2;
                } else {
                    regionLv += zxzEntry.getEntryLv();
                }
            }
            if (entry.getType().equals(ZxzEntryTypeEnum.ENTRY_TYPE_20.getEntryType())) {
                //判断是不是号令支援词条 号令词条减少40等级
                if (entry.getEntryId() == RunesEnum.HAO_LING_ENTRY.getRunesId()) {
                    regionLv -= zxzEntry.getEntryLv() * 40;
                } else {
                    //支援词条减少四级
                    regionLv -= zxzEntry.getEntryLv() * 4;
                }

            }
        }
        return regionLv;
    }

    /**
     * 判断区域是否被攻打
     * @return
     */
    public boolean ifDifficutyRegionAttack(){
        return isInto();
    }

    /**
     * 判断区域是否通过
     * @return
     */
    public boolean ifRegionClearance(){
        return status == ZxzStatusEnum.PASSED.getStatus();
    }

    /**
     * 获取区域攻打进度
     * @return
     */
    public Integer gainRegionProgress(){
        int sum = 0;
        for (UserZxzRegionDefender defender : regionDefenders) {
            if (defender.getStatus() == ZxzStatusEnum.PASSED.getStatus()) {
                sum += 1;
            }
        }
        return sum;
    }

    /**
     * 领取宝箱
     * @param defenderId
     * @return
     */
    public void receiveDefenderBox(Integer defenderId){
        for (UserZxzRegionDefender regionDefender : regionDefenders) {
            if (regionDefender.getDefenderId().equals(String.valueOf(defenderId))) {
                regionDefender.setAwarded(1);
            }
        }
    }

    /**
     * 刷新区域
     * @param defenders
     */
    public void refreshRegion(List<UserZxzRegionDefender> defenders){
        //处理关卡数据
        for (UserZxzRegionDefender defender : defenders) {
            if (defender.getDefenderId().charAt(3) == '1') {
                defender.setStatus(ZxzStatusEnum.ABLE_ATTACK.getStatus());
            }else {
                defender.setStatus(ZxzStatusEnum.NOT_OPEN.getStatus());
            }
        }
        progress = 0;
        surviceTimes = 0;
        into = false;
        status = ZxzStatusEnum.ABLE_ATTACK.getStatus();
        regionDefenders = defenders;
    }

    /**
     * 自动刷新时候需要刷新宝箱
     * @param defenders
     */
    public void autoRefreshRegion(List<UserZxzRegionDefender> defenders){
        refreshRegion(defenders);
        for (UserZxzRegionDefender defender : defenders) {
            defender.setAwarded(0);
        }
        lastRefreshDate = DateUtil.now();
    }

    /**
     * 每周一0点重置通关等级
     */
    public void resetClearanceLv(){
        clearanceLv = 0;
        lastClearanceLv = 0;
    }

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

    @Override
    public UserDataType gainResType() {
        return UserDataType.USER_ZXZ_REGION;
    }
}
