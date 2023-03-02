package com.bbw.god.game.config;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TODO
 *
 * @author suhq
 * @date 2020-09-29 22:40
 **/
public class WayTool {
    private static Map<String, WayEnum> nameWayMap = new HashMap<>();

    public static void init() {
        List<WayEnum> ways = Arrays.asList(WayEnum.values());
        for (WayEnum way : ways) {
            nameWayMap.put(way.getName(), way);
        }
    }

    public static WayEnum fromName(String name) {
        return nameWayMap.get(name);
    }
}
