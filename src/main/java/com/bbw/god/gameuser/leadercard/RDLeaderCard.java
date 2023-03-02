package com.bbw.god.gameuser.leadercard;

import com.bbw.god.gameuser.leadercard.equipment.UserLeaderEquipment;
import com.bbw.god.rd.RDCommon;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

/**
 * @author：lwb
 * @date: 2021/3/22 14:11
 * @version: 1.0
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RDLeaderCard extends RDCommon {
    /**
     * 初始化技能列表
     */
    private List<Integer> initSkills=null;
    /**
     * 卡牌信息
     */
    private RDLeaderCardInfo cardInfo=null;

    /**
     * 装备
     */
    private UserLeaderEquipment[] equips=null;
    /**
     * 宠物
     */
    private int[] beasts =null;

    /**
     * 扣除的加点数量
     */
    private Integer deductedLeaderCardPoint=null;
    /**
     * 获得的进阶额外加成
     */
    private Integer addHvExtraAddition=null;
    /**
     * 剩余加点数
     */
    private Integer leaderCardPoint=null;
    /**
     * 额外的进阶加成
     */
    private Integer hvExtraAddition=null;
    /**
     * 下一阶级额外的进阶加成
     */
    private Integer nextHvExtraAddition=null;

    /**
     * 当前进阶进度
     */
    private Integer currentExtraAddition=null;

    /**
     * 攻击加点数
     */
    private Integer addAtkPoint = null;
    /**
     * 防御加点数
     */
    private Integer addHpPoint = null;
    /**
     * 当前加点可获得的加成量
     */
    private Integer addHpAtkVal = null;
    /**
     * 技能
     */
    private Integer skill=null;

    /**
     * 当前经验
     */
    private Long exp=null;
    /**
     * 下一级需要的经验
     */
    private Long nextLvExp=null;

    private Integer hp=null;
    private Integer atk=null;
    private Integer hv=null;
    private Integer lv=null;
    private Integer star=null;
    /**
     * 需要突破
     */
    private Integer needBreach=null;
    private Integer needConsume=null;
    /**
     * 拥有的属性
     */
    private List<Integer> ownProperty= null ;


    public static RDLeaderCard getInstance(UserLeaderCard leaderCard){
        RDLeaderCard rd=new RDLeaderCard();
        rd.setCardInfo(RDLeaderCardInfo.getInstance(leaderCard));
        return rd;
    }
}
