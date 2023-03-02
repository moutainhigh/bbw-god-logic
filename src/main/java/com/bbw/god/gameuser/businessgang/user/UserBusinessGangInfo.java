package com.bbw.god.gameuser.businessgang.user;

import com.bbw.common.ID;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.gameuser.UserDataType;
import com.bbw.god.gameuser.UserSingleObj;
import com.bbw.god.gameuser.businessgang.BusinessGangCfgTool;
import com.bbw.god.gameuser.businessgang.cfg.CfgBusinessGangData;
import com.bbw.god.gameuser.businessgang.cfg.CfgBusinessGangEntity;
import com.bbw.god.gameuser.businessgang.cfg.CfgNpcInfo;
import lombok.Data;

import java.io.Serializable;
import java.util.*;

/**
 * 玩家商帮数据
 *
 * @author fzj
 * @date 2022/1/13 17:06
 */
@Data
public class UserBusinessGangInfo extends UserSingleObj implements Serializable {
    private static final long serialVersionUID = 1L;
    /** 已开放的商帮 初始只开启正财 */
    private List<Integer> openedBusinessGangs = Collections.singletonList(1000);
    /** 当前所在商帮 */
    private Integer currentBusinessGang = 0;
    /** npc及对应好感度 */
    private Map<String, Integer> npcToFavorability;
    /** 商帮及对应声望 */
    private Map<String, Integer> businessGangToPrestige;
    /** 最后一次加入商帮的时间 */
    private Date lastJoinGangTime;
    /** 最后一次衰减声望的时间 */
    private Date lastDecayTime;

    public static UserBusinessGangInfo getInstance(long uid) {
        UserBusinessGangInfo userBusinessGangInfo = new UserBusinessGangInfo();
        userBusinessGangInfo.setId(ID.INSTANCE.nextId());
        userBusinessGangInfo.setGameUserId(uid);
        Map<String, Integer> npcFavorability = new HashMap<>();
        CfgBusinessGangEntity businessGangInfo = BusinessGangCfgTool.getBusinessGangInfo();
        List<CfgNpcInfo> npcInfo = businessGangInfo.getNpcInfo();
        for (CfgNpcInfo cfgNpcInfo : npcInfo) {
            npcFavorability.put(cfgNpcInfo.getName(), 0);
        }
        userBusinessGangInfo.setNpcToFavorability(npcFavorability);
        Map<String, Integer> businessGangPrestige = new HashMap<>();
        List<CfgBusinessGangData> businessGangData = businessGangInfo.getBusinessGangData();
        for (CfgBusinessGangData cfgBusinessGangData : businessGangData) {
            businessGangPrestige.put(cfgBusinessGangData.getName(), 0);
        }
        userBusinessGangInfo.setBusinessGangToPrestige(businessGangPrestige);
        return userBusinessGangInfo;
    }

    /**
     * 获取好感度
     *
     * @param npcId
     * @return
     */
    public Integer getFavorability(int npcId) {
        CfgNpcInfo npcInfo = BusinessGangCfgTool.getNpcInfo(npcId);
        Integer favorability = getNpcToFavorability().get(npcInfo.getName());
        return favorability == null ? 0 : favorability;
    }

    /**
     * 增加好感度
     *
     * @param npcId
     * @param addNum
     */
    public void addFavorability(int npcId, int addNum) {
        CfgNpcInfo npcInfo = BusinessGangCfgTool.getNpcInfo(npcId);
        Integer favorability = getFavorability(npcId);
        getNpcToFavorability().put(npcInfo.getName(), favorability + addNum);
    }

    /**
     * 增加好感度
     *
     * @param npcId
     * @param num
     */
    public void setNpcFavorability(int npcId, int num) {
        CfgNpcInfo npcInfo = BusinessGangCfgTool.getNpcInfo(npcId);
        getNpcToFavorability().put(npcInfo.getName(), num);
    }

    /**
     * 设置好感度
     *
     * @param npcId
     * @param favorability
     */
    public void setFavorability(int npcId, int favorability) {
        CfgNpcInfo npcInfo = BusinessGangCfgTool.getNpcInfo(npcId);
        getNpcToFavorability().put(npcInfo.getName(), favorability);
    }

    /**
     * 获取声望
     *
     * @param gangId
     * @return
     */
    public Integer getPrestige(int gangId) {
        CfgBusinessGangData gangData = BusinessGangCfgTool.getBusinessGangData(gangId);
        Integer prestige = getBusinessGangToPrestige().get(gangData.getName());
        return prestige == null ? 0 : prestige;
    }

    /**
     * 增加声望
     *
     * @param gangId
     * @param addNum
     */
    public void addPrestige(int gangId, int addNum) {
        CfgBusinessGangData gangData = BusinessGangCfgTool.getBusinessGangData(gangId);
        Integer prestige = getPrestige(gangData.getBusinessGangId());
        getBusinessGangToPrestige().put(gangData.getName(), prestige + addNum);
    }

    /**
     * 扣除声望
     *
     * @param gangId
     * @param deductNum
     */
    public void deductPrestige(int gangId, int deductNum) {
        CfgBusinessGangData gangData = BusinessGangCfgTool.getBusinessGangData(gangId);
        Integer prestige = getPrestige(gangData.getBusinessGangId());
        getBusinessGangToPrestige().put(gangData.getName(), prestige - deductNum);
    }

    /**
     * 解锁商帮
     *
     * @param gangId
     */
    public void unlockGang(int gangId) {
        getOpenedBusinessGangs().add(gangId);
    }

    @Override
    public UserDataType gainResType() {
        return UserDataType.USER_BUSINESS_GANG_INFO;
    }
}
