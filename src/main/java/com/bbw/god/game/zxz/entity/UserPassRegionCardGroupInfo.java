package com.bbw.god.game.zxz.entity;

import com.bbw.common.DateUtil;
import com.bbw.common.ID;
import com.bbw.god.game.combat.runes.RunesEnum;
import com.bbw.god.game.zxz.cfg.CfgZxzEntryEntity;
import com.bbw.god.game.zxz.cfg.ZxzEntryTool;
import com.bbw.god.game.zxz.constant.ZxzConstant;
import com.bbw.god.game.zxz.enums.ZxzEntryTypeEnum;
import com.bbw.god.game.zxz.service.ZxzAnalysisService;
import com.bbw.god.gameuser.UserData;
import com.bbw.god.gameuser.UserDataType;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 用户诛仙阵区域通关卡组
 * @author: hzf
 * @create: 2022-10-10 14:49
 **/
@Data
public class UserPassRegionCardGroupInfo extends UserData implements Serializable {
    private static final long serialVersionUID = 7073473097670406575L;
    /** 难度类型 */
    private Integer difficulty;
    /** 区域Id */
    private Integer regionId;
    /** 卡牌数据 */
    private List<UserPassRegionCard> cards = new ArrayList<>();
    /** 诛仙阵 主角卡信息 */
    private ZxzUserLeaderCard zxzUserLeaderCard;
    /** 符图数据 */
    private List<ZxzFuTu> runes;
    /** 符册名称 */
    private String fuceName;
    /** 词条 id@等级 */
    private List<String> entries = new ArrayList<>();
    /** 区域等级 */
    private Integer regionLv;
    /** 开始时间 */
    private long beginTime;
    /** 结束时间 */
    private long endTime;

    /**
     * 判断通关卡组是否是有效
     * @return
     */
    public boolean ifValid(){
        //获取当前的时间戳
        long currentTime = System.currentTimeMillis();
        if (currentTime >= getBeginTime() && currentTime <= getEndTime() ) {
            return true;
        }
        return false;
    }
    public boolean ifValid(Integer beginDate){
        int beginTime = DateUtil.toDateInt(new Date(getBeginTime()));
        int endTime = DateUtil.toDateInt(new Date(getEndTime()));
        if (beginDate >= beginTime && beginDate <= endTime ) {
            return true;
        }
        return false;
    }


    /**
     * 添加实例
     * @param uid
     * @param zxzCardGroup
     * @param entries
     * @param regionLv
     * @param fuCeName
     * @param beginTime
     * @param endTime
     * @return
     */
    public static UserPassRegionCardGroupInfo instance(long uid, UserZxzCardGroupInfo zxzCardGroup, List<String> entries,Integer regionLv,String fuCeName, long beginTime, long endTime){
        UserPassRegionCardGroupInfo uPassCardGroup = new UserPassRegionCardGroupInfo();
        uPassCardGroup.setId(ID.INSTANCE.nextId());
        uPassCardGroup.setGameUserId(uid);
        uPassCardGroup.setDifficulty(zxzCardGroup.getDifficulty());
        uPassCardGroup.setRegionId(zxzCardGroup.getRegionId());
        uPassCardGroup.setCards(gainUserPassRegionCard(zxzCardGroup.getCards()));
        if (null != uPassCardGroup.getZxzUserLeaderCard()) {
            uPassCardGroup.setZxzUserLeaderCard(zxzCardGroup.getZxzUserLeaderCard());
        }
        List<ZxzFuTu> zxzFuTus = ZxzAnalysisService.gainRunes(zxzCardGroup.getRunes());
        uPassCardGroup.setRunes(zxzFuTus);
        uPassCardGroup.setFuceName(fuCeName);
        uPassCardGroup.setEntries(entries);
        uPassCardGroup.setRegionLv(regionLv);
        uPassCardGroup.setBeginTime(beginTime);
        uPassCardGroup.setEndTime(endTime);
        return uPassCardGroup;
    }

    /**
     * 更新区域通关卡组
     * @param zxzCardGroup
     * @param entries
     * @param regionLv
     * @param fuCeName
     */
    public void updateUserPassRegionCardGroup(UserZxzCardGroupInfo zxzCardGroup,List<String> entries,Integer regionLv,String fuCeName){
        this.cards = gainUserPassRegionCard(zxzCardGroup.getCards());
        this.entries = entries;
        this.regionLv = regionLv;
        this.fuceName = fuCeName;
    }


    /**
     * 获取通关的卡组
     * @param userZxzCards
     * @return
     */
    public static List<UserPassRegionCard> gainUserPassRegionCard(List<UserZxzCard> userZxzCards){
        List<UserPassRegionCard> userPassRegionCardList = new ArrayList<>();
        for (UserZxzCard userZxzCard : userZxzCards) {
            UserPassRegionCard uPassCard = new UserPassRegionCard();
            uPassCard.setAttackSymbol(userZxzCard.getAttackSymbol());
            uPassCard.setDefenceSymbol(userZxzCard.getDefenceSymbol());
            uPassCard.setZhiBaos(userZxzCard.getZhiBaos());
            uPassCard.setXianJues(userZxzCard.getXianJues());
            uPassCard.setCardId(userZxzCard.getCardId());
            uPassCard.setLv(userZxzCard.getLv());
            uPassCard.setHv(userZxzCard.getHv());
            uPassCard.setSkills(userZxzCard.getSkills());
            userPassRegionCardList.add(uPassCard);
        }
        return userPassRegionCardList;
    }
    /**
     * 词条 List<String> ->List<ZxzEntry>
     * @return
     */
    public List<ZxzEntry> gainEntrys() {
        List<ZxzEntry> zEntries = new ArrayList<>();
        for (String entry : entries) {
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
    public Integer computeRegionLv(){
        int regionLv = 0;
        //词条集合
        List<ZxzEntry> zxzEntries = gainEntrys();
        for (ZxzEntry zxzEntry : zxzEntries) {
            CfgZxzEntryEntity entry = ZxzEntryTool.getEntryById(zxzEntry.getEntryId());
            if (entry.getType().equals(ZxzEntryTypeEnum.ENTRY_TYPE_10.getEntryType())) {
                regionLv += zxzEntry.getEntryLv();
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

    @Override
    public UserDataType gainResType() {
        return UserDataType.USER_PASS_CARD_GROUP;
    }
}
