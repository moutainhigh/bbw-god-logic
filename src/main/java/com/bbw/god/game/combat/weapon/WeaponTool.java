package com.bbw.god.game.combat.weapon;

import com.bbw.god.game.config.Cfg;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

/**
 *
 * 战斗法宝工具
 * @author：lwb
 * @date: 2020/11/24 17:23
 * @version: 1.0
 */
@Slf4j
public class WeaponTool {
    /**
     * 根据Id获取法宝
     * @param id
     * @return
     */
    public static Optional<CfgWeapon> getWeaponById(int id){
        List<CfgWeapon> allCfgWeapons= getAllCfgWeapons();
        for (CfgWeapon weapon:allCfgWeapons){
            if (weapon.getId()==id){
                return Optional.of(weapon);
            }
        }
        log.error("请检查战斗法宝配置未找的法宝ID："+id);
        return Optional.empty();
    }

    public static List<CfgWeapon> getAllCfgWeapons(){
        return Cfg.I.get(CfgWeapon.class);
    }
}
