package com.bbw.god.game.combat.runes.service;

/**
 * @author lwb
 * @date 2020/9/16 14:45
 */
public interface IBaseRunesService {
    int getRunesId();

    default int getInt(Float val){
        return val.intValue();
    }
}
