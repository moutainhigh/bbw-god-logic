package com.bbw.god.gameuser.card.equipment.cfg;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 仙诀加成
 *
 * @author: huanghb
 * @date: 2022/9/17 9:20
 */
@Data
public class CfgXianJueEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    /** 仙诀类型 */
    private Integer xianJueType;
    /** 仙诀名称 */
    private String name;
    /** 仙诀加成 加成=》加成值 */
    private List<CardEquipmentAddition> additions;
}
