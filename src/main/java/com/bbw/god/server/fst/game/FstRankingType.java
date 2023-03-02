package com.bbw.god.server.fst.game;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 说明：
 * 封神台榜单类型
 * @author lwb
 * date 2021-06-30
 */
@Getter
@AllArgsConstructor
public enum FstRankingType {
    NONE(-1,false,"无"),
    SERVER(0,false,"区服榜"),
    REN(100,true,"人榜"),
    HUANG(110,true,"黄榜"),
    XUAN(120,true,"玄榜"),
    DI(130,true,"地榜"),
    TIAN(140,true,"天榜");
    private int type;
    private boolean isGameFst;
    private String memo;

    public static FstRankingType fromVal(int val){
        for (FstRankingType item : values()) {
            if (item.getType()==val){
                return item;
            }
        }
        return null;
    }
}
