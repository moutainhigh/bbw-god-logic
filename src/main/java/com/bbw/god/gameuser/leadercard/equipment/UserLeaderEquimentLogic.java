package com.bbw.god.gameuser.leadercard.equipment;

import com.bbw.common.PowerRandom;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.event.BaseEventParam;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.treasure.CfgTreasureEntity;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.game.config.treasure.TreasureTool;
import com.bbw.god.game.config.treasure.TreasureType;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.leadercard.event.EPLeaderEquipmentAddLv;
import com.bbw.god.gameuser.leadercard.event.LeaderCardEventPublisher;
import com.bbw.god.gameuser.res.ResChecker;
import com.bbw.god.gameuser.res.ResEventPublisher;
import com.bbw.god.gameuser.treasure.TreasureChecker;
import com.bbw.god.gameuser.treasure.UserTreasure;
import com.bbw.god.gameuser.treasure.UserTreasureService;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.rd.RDCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 装备业务逻辑
 *
 * @author suhq
 * @date 2021-03-26 17:25
 **/
@Service
public class UserLeaderEquimentLogic {
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private UserTreasureService userTreasureService;
    @Autowired
    private UserLeaderEquimentService userLeaderEquimentService;

    /**
     * 穿戴装备
     *
     * @param uid
     * @param equipmentId
     */
    public RDCommon take(long uid, int equipmentId) {
        // 检查转备有效性
        TreasureChecker.checkIsExist(equipmentId);
        CfgTreasureEntity treasure = TreasureTool.getTreasureById(equipmentId);
        if (treasure.getType() != TreasureType.EQUIPMENT.getValue()) {
            throw ExceptionForClientTip.fromi18nKey("leader.equipment.not.valid");
        }
        //检查装备
        TreasureChecker.checkIsEnough(equipmentId, 1, uid);
        UserLeaderEquipment leaderEquipment = getLeaderEquipment(uid, equipmentId);
        RDCommon rd = new RDCommon();
        if (null == leaderEquipment){
            // 穿戴装备
            UserLeaderEquipment userLeaderEquipment = UserLeaderEquipment.getInstance(uid, equipmentId);
            gameUserService.addItem(uid, userLeaderEquipment);
        }else {
            // 穿戴装备
            leaderEquipment.setIsPutOn(1);
            gameUserService.updateItem(leaderEquipment);
        }
        return rd;
    }

    /**
     * 取下装备
     * @param uid
     * @param equipmentId
     * @return
     */
    public RDCommon takeOffLeaderEquipment(long uid, int equipmentId){
        UserLeaderEquipment leaderEquipment = getLeaderEquipment(uid, equipmentId);
        if (null == leaderEquipment){
            throw ExceptionForClientTip.fromi18nKey("leader.equipment.not.valid");
        }
        RDCommon rd = new RDCommon();
        //穿装备不扣除装备，针对版本前已穿上的玩家补回道具
        UserTreasure userTreasure = userTreasureService.getUserTreasure(uid, equipmentId);
        if (userTreasure == null){
            TreasureEventPublisher.pubTAddEvent(uid, equipmentId, 1, WayEnum.LEADER_EQUIPMENT_ACTIVE, rd);
        }
        //取下装备
        leaderEquipment.setIsPutOn(0);
        gameUserService.updateItem(leaderEquipment);
        return rd;
    }
    /**
     * 装备强化
     *
     * @param uid
     * @param equipmentId
     * @return
     */
    public RDEquipmentStrength strength(long uid, int equipmentId) {
        //检查是否激活
        UserLeaderEquipment leaderEquipment = getLeaderEquipment(uid, equipmentId);
        if (null == leaderEquipment) {
            throw ExceptionForClientTip.fromi18nKey("leader.equipment.not.active");
        }
        //检查等级
        int maxLevel = CfgEquipmentTool.getMaxLevelLimit();
        if (leaderEquipment.getLevel() >= maxLevel) {
            throw ExceptionForClientTip.fromi18nKey("leader.equipment.streagth.level.top");
        }
        int levelLimit = CfgEquipmentTool.getLevelLimit(leaderEquipment.getQuality());
        if (leaderEquipment.getLevel() >= levelLimit) {
            throw ExceptionForClientTip.fromi18nKey("leader.equipment.streagth.level.limited");
        }
        RDEquipmentStrength rd = new RDEquipmentStrength();
        GameUser gu = gameUserService.getGameUser(uid);
        CfgEquipmentStrengthen strengthenConf = CfgEquipmentTool.getEquipmentStrengthen(leaderEquipment.getLevel() + 1);
        // 扣除铜钱
        ResChecker.checkCopper(gu, strengthenConf.getNeedCopper());
        ResEventPublisher.pubCopperDeductEvent(uid, Long.valueOf(strengthenConf.getNeedCopper()), WayEnum.LEADER_EQUIPMENT_STRENGTH, rd);
        // 概率成功
        boolean isStrengthSuccess = strengthenConf.getSuccessRate() >= PowerRandom.getRandomBySeed(10000);
        if (isStrengthSuccess) {
            leaderEquipment.addLevel(1);
            gameUserService.updateItem(leaderEquipment);
            EPLeaderEquipmentAddLv ep = EPLeaderEquipmentAddLv.instance(new BaseEventParam(uid), equipmentId, leaderEquipment.getLevel());
            LeaderCardEventPublisher.pubLeaderEquipmentAddLvEvent(ep);
        }
        rd.setIsSuccess(isStrengthSuccess ? 1 : 0);
        return rd;
    }

    /**
     * 获取装备信息
     *
     * @param uid
     * @return
     */
    public RDEquipmentsInfo getEquipmentsInfo(long uid) {
        List<UserLeaderEquipment> userLeaderEquipments = userLeaderEquimentService.getLeaderEquipments(uid);
        RDEquipmentsInfo rd = new RDEquipmentsInfo();
        rd.addEquipmentInfo(userLeaderEquipments);
        return rd;
    }

    /**
     * 星图升星
     *
     * @param uid
     * @param equipmentId
     */
    public RDEquipmentStarMapUpdate updateEquipmentStatMap(long uid, int equipmentId,int protect) {
        boolean isProtected = protect == 1;
        //检查是否激活
        UserLeaderEquipment leaderEquipment = getLeaderEquipment(uid, equipmentId);
        if (null == leaderEquipment) {
            throw ExceptionForClientTip.fromi18nKey("leader.equipment.not.take");
        }
        //检查品质
        int maxQuality = CfgEquipmentTool.getMaxQuality();
        if (leaderEquipment.getQuality() >= maxQuality) {
            throw ExceptionForClientTip.fromi18nKey("leader.equipment.quality.top");
        }
        int nextStar = leaderEquipment.getStarMapProgress() + 1;
        CfgEquipmentStarMap equipmentStarMap = CfgEquipmentTool.getEquipmentStarMap(leaderEquipment.getQuality(), nextStar);
        //添加碎星保护符
        if(isProtected){
            equipmentStarMap.getNeeds().add(new Award(TreasureEnum.XING_TU_BAO_HF.getValue(), AwardEnum.FB, 1));
        }
        //检查所需道具
        for (Award need : equipmentStarMap.getNeeds()) {
            TreasureChecker.checkIsEnough(need.getAwardId(), need.getNum(), uid);
        }
        RDEquipmentStarMapUpdate rd = new RDEquipmentStarMapUpdate();
        //扣除道具
        for (Award need : equipmentStarMap.getNeeds()) {
            TreasureEventPublisher.pubTDeductEvent(uid, need.getAwardId(), need.getNum(), WayEnum.LEADER_EQUIPMENT_STAR_MAP_UPDATE, rd);
        }
        // 概率成功
        boolean isStrengthSuccess = equipmentStarMap.getSuccessRate() >= PowerRandom.getRandomBySeed(100);
        int result = isStrengthSuccess ? 1 : 0;
        if (isStrengthSuccess) {
            leaderEquipment.addStarProgress();
            gameUserService.updateItem(leaderEquipment);
        } else {
            boolean isToDeduct = 10 >= PowerRandom.getRandomBySeed(100);
            if (isToDeduct && !isProtected) {
                leaderEquipment.deductStarProgress();
                gameUserService.updateItem(leaderEquipment);
                result = -1;
            }
        }
        rd.setResult(result);
        return rd;
    }

    public UserLeaderEquipment getLeaderEquipment(long uid, int equipmentId) {
        List<UserLeaderEquipment> userLeaderEquipments = gameUserService.getMultiItems(uid, UserLeaderEquipment.class);
        return userLeaderEquipments.stream().filter(tmp -> tmp.getEquipmentId() == equipmentId).findFirst().orElse(null);
    }
}
