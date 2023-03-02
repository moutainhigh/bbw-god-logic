package com.bbw.god.game.combat.runes;

import com.bbw.common.ListUtil;
import com.bbw.common.PowerRandom;
import com.bbw.god.game.config.Cfg;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 *
 * 符文相关
 * @author：lwb
 * @date: 2020/11/24 10:23
 * @version: 1.0
 */
public class RunesTool {

    /**
     * 根据符文名称获取符文ID
     * @return 存在符文时返回 符文ID，不存在时返回0
     */
    public static int getRunesIdByRunesName(String runesName){
        Optional<CfgRunes> optional = getRunesByRunesName(runesName);
        if (optional.isPresent()){
            return optional.get().getId();
        }
        return 0;
    }

    /**
     * 根据符文名称获取符文
     * @param runesName
     * @return
     */
    public static Optional<CfgRunes> getRunesByRunesName(String runesName){
        List<CfgRunes> allRunes=getAllRunes();
        for (CfgRunes cfg:allRunes){
            if (cfg.getName().equals(runesName)){
                return Optional.of(cfg);
            }
        }
        return Optional.empty();
    }

    /**
     * 随机获取野怪符文ID
     *
     * @param isElite
     * @return
     */
    public static int getRandomYGRunesId(boolean isElite) {
        List<String> runes = getYGRunes(isElite);
        String randomRunesName = PowerRandom.getRandomFromList(runes);
        return getRunesIdByRunesName(randomRunesName);
    }

    /**
     * 获取随机护身符
     *
     * @param runeTypes
     * @param num
     * @return
     */
    public static List<CfgRunes> getRandomRune(List<Integer> runeTypes, int num) {
        List<CfgRunes> allRunes = getAllRunes();
        List<CfgRunes> runes = allRunes.stream().filter(tmp -> runeTypes.contains(tmp.getRuneType())).collect(Collectors.toList());
        return PowerRandom.getRandomsFromList(runes, num);
    }

    /**
     * 获取所有符文配置
     *
     * @return
     */
    public static List<CfgRunes> getAllRunes() {
        List<CfgRunes> cfgRunes = Cfg.I.get(CfgRunes.class);
        return ListUtil.copyList(cfgRunes, CfgRunes.class);
    }

    /**
     * 获取野怪的符文集合
     * @param isElite 是否是精英怪
     * @return
     */
    private static List<String> getYGRunes(boolean isElite){
        CfgYgRunes cfgYgRunes = Cfg.I.getUniqueConfig(CfgYgRunes.class);
        if (isElite){
            return cfgYgRunes.getEliteRunes();
        }
        return cfgYgRunes.getNormalRunes();
    }

}
