package com.bbw.god.game.zxz.entity.foursaints;

import com.bbw.common.ListUtil;
import com.bbw.god.game.config.card.equipment.randomrule.CardXianJueRandomRule;
import com.bbw.god.game.config.card.equipment.randomrule.CardZhiBaoRandomRule;
import com.bbw.god.game.config.card.equipment.randomrule.CfgCardEquipmentRandomRuleTool;
import com.bbw.god.game.zxz.cfg.CfgLingZhuangEntryEntity;
import com.bbw.god.game.zxz.cfg.ZxzEntryTool;
import com.bbw.god.game.zxz.constant.ZxzConstant;
import com.bbw.god.game.zxz.entity.ZxzAbstractDefender;
import com.bbw.god.game.zxz.entity.ZxzCard;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 诛仙阵四圣挑战野怪关卡
 * @author: hzf
 * @create: 2022-12-26 11:44
 **/
@Data
public class ZxzFourSaintsDefender extends ZxzAbstractDefender implements Serializable {
    private static final long serialVersionUID = 7073473097670406575L;
    /** 关卡Id **/
    private Integer defenderId;
    /** 野怪种类 */
    private Integer kind;

}
