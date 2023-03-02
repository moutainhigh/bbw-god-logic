package com.bbw.god.game.zxz.cfg.foursaints;

import com.bbw.god.game.config.Cfg;
import com.bbw.god.game.zxz.cfg.CfgZxzEntryEntity;
import com.bbw.god.game.zxz.cfg.ZxzEntryTool;
import com.bbw.god.game.zxz.enums.ZxzEntryTypeEnum;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 四圣挑战工具类
 * @author: hzf
 * @create: 2022-12-26 11:17
 **/
public class CfgFourSaintsTool {
    /**
     * 获取配置类
     * @return
     */
    public static CfgFourSaintsEntity getCfg() {
        return  Cfg.I.getUniqueConfig(CfgFourSaintsEntity.class);
    }
    /**
     * 根据挑战类型获取奖励
     * @param challengeType
     * @return
     */
    public static CfgFourSaintsEntity.CfgFourSaintsAwad getFourSaintsAwad(Integer challengeType){
        return  getCfg().getFourSaintsAwads().stream()
                .filter(tmp -> tmp.getChallengeType().equals(challengeType))
                .findFirst().orElse(null);
    }

    /**
     * 获取挑战规则
     * @param challengeType
     * @return
     */
    public static CfgFourSaintsEntity.CfgFourSaintsChallenge getFourSaintsChallenge(Integer challengeType){
        return getCfg().getFourSaintsChallenges().stream()
                .filter(tmp -> tmp.getChallengeType().equals(challengeType))
                .findFirst().orElse(null);
    }
    /**
     * 获取挑战规则
     * @param clearanceScore
     * @param difficulty
     * @return
     */
    public static List<CfgFourSaintsEntity.CfgFourSaintsChallenge> getFourSaintsChallenge(Integer clearanceScore,Integer difficulty){
        return getCfg().getFourSaintsChallenges().stream()
                .filter(tmp -> tmp.getUnlockDifficulty().equals(difficulty) && tmp.getUnlockNeedScore() <= clearanceScore)
                .collect(Collectors.toList());
    }

    /**
     * 获取野怪随机规则
     * @return
     */
    public static List<CfgFourSaintsEntity.CfgFourSaintsDefenderCardRule> getDefenderCardRules(Integer challengeType){
        return getCfg().getDefenderCardRules().stream()
                .filter(tmp -> tmp.getChallengeType().equals(challengeType))
                .collect(Collectors.toList());
    }

    /**
     * 获取单个关卡规则
     * @param defenderId
     * @return
     */
    public static CfgFourSaintsEntity.CfgFourSaintsDefenderCardRule getDefenderCardRule(Integer defenderId){
        Integer challengeType = getChallengeType(defenderId);
       return getDefenderCardRules(challengeType).stream()
                .filter(tmp -> tmp.getDefenderId().equals(defenderId))
                .findFirst().orElse(null);
    }
    /**
     * 获取词条随机规则（集合）
     * @return
     */
    public static List<CfgFourSaintsEntity.CfgEntryRandom> getEntryRandoms(){
        return getCfg().getEntryRandoms();
    }

    /**
     * 获取词条随机规则
     * @param challengeType
     * @return
     */
    public static CfgFourSaintsEntity.CfgEntryRandom getEntryRandom(Integer challengeType){
        return getEntryRandoms().stream()
                .filter(tmp -> tmp.getChallengeType().equals(challengeType))
                .findFirst().orElse(null);
    }

    /**
     * 获取词条池
     * @return
     */
    public static List<Integer> getEntryPool(){
        List<CfgZxzEntryEntity> entrys = ZxzEntryTool.getEntryByType(ZxzEntryTypeEnum.ENTRY_TYPE_10.getEntryType());
        List<Integer> entryIds = entrys.stream().map(CfgZxzEntryEntity::getEntryId).collect(Collectors.toList());
        List<Integer> filterEntryIds = CfgFourSaintsTool.getCfg().getFilterEntryIds();
        List<Integer> entryPool = entryIds.stream().filter(tmp -> !filterEntryIds.contains(tmp)).collect(Collectors.toList());
        return entryPool;
    }

    /**
     * 获取挑战类型
     * @param defenderId
     * @return
     */
    public static Integer getChallengeType(Integer defenderId){
        return Integer.parseInt(String.valueOf(defenderId).substring(0, 2));

    }

    /**
     * 获取第几关
     * @param defenderId
     * @return
     */
    public static Integer defender(Integer defenderId){
        return Integer.parseInt(String.valueOf(defenderId).substring(4, 5));
    }

    /**
     * 获取词条随机加值规则
     * @return
     */
    public static List<CfgFourSaintsEntity.CfgEntryRandomLv> getEntryRandomLvs(){
        return CfgFourSaintsTool.getCfg().getEntryRandomAddLvRules();
    }

    /**
     * 获取全部挑战的名称和头像
     * @return
     */
    public static List<CfgFourSaintsEntity.CfgNameAndHeadImg> getNameAndHeadImgs(){
        return CfgFourSaintsTool.getCfg().getNameAndHeadImgRules();
    }

    /**
     * 获取挑战的名称和头像
     * @param challengeType
     * @param kind
     * @return
     */
    public static CfgFourSaintsEntity.CfgNameAndHeadImg getNameAndHeadImg(Integer challengeType,Integer kind){
      return   getNameAndHeadImgs().stream()
                .filter(tmp -> tmp.getChallengeType().equals(challengeType) && tmp.getKind().equals(kind))
                .findFirst().orElse(null);
    }

    /**
     * 获取卡牌技能随机限制
     * @return
     */
    public static List<CfgFourSaintsEntity.CfgRandomCardSkillLimit> getRandomCardSkillLimits(){
        return getCfg().getRandomCardSkillLimitRules();
    }
    /**
     * 获取卡牌技能随机限制
     * @return
     */
    public static CfgFourSaintsEntity.CfgRandomCardSkillLimit getRandomCardSkillLimit(Integer cardId){
        return getRandomCardSkillLimits().stream()
                .filter(tmp -> tmp.getCardId().equals(cardId))
                .findFirst().orElse(null);
    }


    /**
     * 获取自动刷新时间
     * @return
     */
    public static CfgFourSaintsEntity.CfgAutoRefreshRule getAutoRefreshRule(){
        return getCfg().getAutoRefreshRules();
    }

}
