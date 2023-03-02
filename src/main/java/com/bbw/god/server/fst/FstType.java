package com.bbw.god.server.fst;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

/**
 * 说明：
 *
 * @author lwb
 * date 2021-06-29
 */
@AllArgsConstructor
@Getter
public enum FstType implements Serializable {
    SERVER(10,"区服"),
    GAME(20,"跨服");
    private int type;
    private String memo;
    public static FstType fromVal(Integer type){
        if (type==null || SERVER.getType()==type.intValue()){
            return SERVER;
        }
        return GAME;
    }
}
