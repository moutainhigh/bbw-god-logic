package com.bbw.god.activity.holiday.config;

import com.bbw.god.game.award.Award;
import com.bbw.god.game.config.Cfg;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 杂货小铺工具类
 * @author: hzf
 * @create: 2022-12-09 09:08
 **/
public class GroceryShopTool {
    /**
     * 获得配置
     * @return
     */
    public static CfgGroceryShop getCfg() {
        return Cfg.I.getUniqueConfig(CfgGroceryShop.class);
    }

    /**
     * 获取大奖的奖励
     * @return
     */
    public static List<Award> getGrandPrixAward(){
        List<Award> awards = new ArrayList<>();
        List<CfgGroceryShop.CfgBlindBoxGrandPrix> blindBoxGrandPrixs = getCfg().getBlindBoxGrandPrixs();
        blindBoxGrandPrixs.forEach(grandPrix -> {
            awards.add(grandPrix.getAward());
        });
        return awards;
    }

    /**
     * 判断道具是不是大奖里面的
     * @param treasureId
     * @return
     */
    public static boolean ifGrandPrix(Integer treasureId){
        List<Integer> treasureIds = getCfg().getBlindBoxGrandPrixs()
                .stream().map(CfgGroceryShop.CfgBlindBoxGrandPrix::getTreasureId)
                .collect(Collectors.toList());
        //判断是否大奖
        if (treasureIds.contains(treasureId)) {
            return true;
        }
        return false;
    }

    /**
     * 根据大奖id，获取对应大奖的配置
     * @param treasureId
     * @return
     */
    public static CfgGroceryShop.CfgBlindBoxGrandPrix getGrandPrix(Integer treasureId){
        List<CfgGroceryShop.CfgBlindBoxGrandPrix> blindBoxGrandPrixs = getCfg().getBlindBoxGrandPrixs();
        return blindBoxGrandPrixs.stream()
                .filter(tmp -> tmp.getTreasureId().equals(treasureId))
                .findFirst().orElse(null);
    }

    /**
     * 获取盲盒奖励
     * @return
     */
    public static List<CfgGroceryShop.CfgBlindBoxAward> getCfgBlindBoxAwards(){
        return getCfg().getBlindBoxAwards();
    }

    /**
     * 根据道具id 获取盲盒配置
     * @param treasureId
     * @return
     */
    public static CfgGroceryShop.CfgBlindBoxAward getBlindBoxAwards(Integer treasureId){
       return getCfgBlindBoxAwards().stream()
                .filter(tmp -> tmp.getTreasureId().equals(treasureId))
                .findFirst().orElse(null);
    }

}
