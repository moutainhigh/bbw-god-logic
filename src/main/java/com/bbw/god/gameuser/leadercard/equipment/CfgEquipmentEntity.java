package com.bbw.god.gameuser.leadercard.equipment;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 装备加成
 *
 * @author suhq
 * @date 2021-03-26 13:47
 **/
@Data
public class CfgEquipmentEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer equipmentId;
    private String name;
    private Integer position;
    private List<Addition> additions;
}
