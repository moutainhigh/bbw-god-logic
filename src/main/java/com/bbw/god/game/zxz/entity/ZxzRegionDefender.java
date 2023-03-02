package com.bbw.god.game.zxz.entity;

import com.bbw.common.ListUtil;
import com.bbw.god.game.config.card.equipment.randomrule.CardXianJueRandomRule;
import com.bbw.god.game.config.card.equipment.randomrule.CardZhiBaoRandomRule;
import com.bbw.god.game.config.card.equipment.randomrule.CfgCardEquipmentRandomRuleTool;
import com.bbw.god.game.zxz.cfg.CfgLingZhuangEntryEntity;
import com.bbw.god.game.zxz.cfg.ZxzEntryTool;
import com.bbw.god.game.zxz.constant.ZxzConstant;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 关卡数据
 * @author: hzf
 * @create: 2022-09-14 17:02
 **/
@Data
public class ZxzRegionDefender extends ZxzAbstractDefender implements Serializable {
    private static final long serialVersionUID = 7073473097670406575L;
    /** 关卡Id **/
    private String defenderId;
    /** 种类 */
    private Integer kind;

    /**
     * 设置关卡ID
     *
     * @param regionId
     * @param defender
     */
    public void setDefenderId(Integer regionId, Integer defender) {
        this.defenderId = regionId.toString() + defender;
    }


}
