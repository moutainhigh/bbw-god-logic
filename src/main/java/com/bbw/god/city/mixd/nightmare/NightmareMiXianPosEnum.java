package com.bbw.god.city.mixd.nightmare;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

/**
 * 说明：
 *
 * @author lwb
 * date 2021-05-26
 */
@Getter
@AllArgsConstructor
public enum NightmareMiXianPosEnum implements Serializable {
    PLAYER(1,"显示"),
    EMPTY(0,"空白"),
    EVENT(100,"事件"),//迷雾时有事件的将用这个代表
    WATER_SPRING(110,"泉水"),
    BOX(120,"宝箱"),
    BOX_SPECIAL(121,"特殊宝箱"),
    BOX_RICH(122,"珍贵宝箱"),
    TREASURE_HOUSE(130,"宝库"),
    GATE(140,"大门"),
    TREASURE_HOUSE_GATE(145,"宝库传送"),
    TRAP(150,"陷阱"),
    FURNACE(160,"熔炉"),
    COPPER(170,"铜钱"),
    ELE(180,"元素"),
    GOLD(190,"元宝"),
    XUN_SHI_XD(210,"巡使小队"),
    XUN_SHI_LEADER(220,"巡使头领"),
    SHOU_WEI(230,"守卫"),
    LEVEL_LEADER(240,"层主"),
    XUN_SHI_JIANG_HUAN(250,"巡使-姜环"),
    XUN_SHI_ZHU_LONG(260,"巡使-烛龙"),
    ;
    private int type;
    private String memo;

    public static NightmareMiXianPosEnum fromType(int type){
        for (NightmareMiXianPosEnum posEnum : values()) {
            if (posEnum.getType()==type){
                return posEnum;
            }
        }
        return null;
    }
}
