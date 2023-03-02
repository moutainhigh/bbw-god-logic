package com.bbw.god.gameuser.leadercard;

import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.controller.AbstractController;
import com.bbw.god.game.CR;
import com.bbw.god.game.config.treasure.CfgTreasureEntity;
import com.bbw.god.game.config.treasure.TreasureTool;
import com.bbw.god.game.config.treasure.TreasureType;
import com.bbw.god.gameuser.leadercard.beast.RDBeastInfo;
import com.bbw.god.gameuser.leadercard.beast.UserLeaderBeastLogic;
import com.bbw.god.gameuser.leadercard.equipment.RDEquipmentStarMapUpdate;
import com.bbw.god.gameuser.leadercard.equipment.RDEquipmentStrength;
import com.bbw.god.gameuser.leadercard.equipment.RDEquipmentsInfo;
import com.bbw.god.gameuser.leadercard.equipment.UserLeaderEquimentLogic;
import com.bbw.god.gameuser.leadercard.fashion.RDFashionList;
import com.bbw.god.gameuser.leadercard.fashion.UserLeaderFashionLogic;
import com.bbw.god.rd.RDCommon;
import com.bbw.god.rd.RDSuccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 分身卡装备、神兽操作接口
 *
 * @author suhq
 * @date 2021-03-29 10:35
 **/
@RestController
public class LeaderEquipmentController extends AbstractController {
    @Autowired
    private UserLeaderBeastLogic userLeaderBeastLogic;
    @Autowired
    private UserLeaderEquimentLogic userLeaderEquimentLogic;
    @Autowired
    private UserLeaderFashionLogic userLeaderFashionLogic;

    @RequestMapping(CR.LeaderCard.TAKE)
    public RDCommon take(int id) {
        CfgTreasureEntity treasure = TreasureTool.getTreasureById(id);
        if (treasure.getType() == TreasureType.EQUIPMENT.getValue()) {
            return userLeaderEquimentLogic.take(getUserId(), id);
        } else if (treasure.getType() == TreasureType.FASHION.getValue()) {
            return userLeaderFashionLogic.take(getUserId(), id);
        }
        return userLeaderBeastLogic.take(getUserId(), id);
    }

    @RequestMapping(CR.LeaderCard.TAKE_OFF)
    public RDCommon takeOff(int id) {
        CfgTreasureEntity treasure = TreasureTool.getTreasureById(id);
        if (treasure.getType() == TreasureType.EQUIPMENT.getValue()) {
            return userLeaderEquimentLogic.takeOffLeaderEquipment(getUserId(), id);
        }
        return userLeaderBeastLogic.takeOff(getUserId(), id);
    }

    /**
     * 神兽禁用技能
     *
     * @param id
     * @param skillId
     * @param active  0 禁用；1激活
     * @return
     */
    @RequestMapping(CR.LeaderCard.ACTIVE_SKILL)
    public RDSuccess activeSkill(int id, int skillId, int active) {
        return userLeaderBeastLogic.activeSkill(getUserId(), id, skillId, active == 1);
    }

    @RequestMapping(CR.LeaderCard.GET_BEAST_INFO)
    public RDBeastInfo getBeastInfo(int id) {
        return userLeaderBeastLogic.getBeastInfo(getUserId(), id);
    }

    @RequestMapping(CR.LeaderCard.STRENGTHEN_EQUIPMENT)
    public RDEquipmentStrength strength(int id) {
        return userLeaderEquimentLogic.strength(getUserId(), id);
    }

    @RequestMapping(CR.LeaderCard.GET_EQUIPMENTS_INFO)
    public RDEquipmentsInfo getEquipmentsInfo() {
        return userLeaderEquimentLogic.getEquipmentsInfo(getUserId());
    }

    @RequestMapping(CR.LeaderCard.UPDATE_EQUIPMENT_STAR_MAP)
    public RDEquipmentStarMapUpdate updateEquipmentStatMap(int id, int protect) {
        return userLeaderEquimentLogic.updateEquipmentStatMap(getUserId(), id, protect);
    }

    @RequestMapping(CR.LeaderCard.GET_FASHIONS)
    public RDFashionList listFashions() {
        return userLeaderFashionLogic.getFashions(getUserId());
    }

    @RequestMapping(CR.LeaderCard.UPDATE_FASHION)
    public RDCommon updateFashion(int id) {
        return userLeaderFashionLogic.update(getUserId(), id);
    }

}
