package com.bbw.god.gameuser.shake;

import com.bbw.common.CloneUtil;
import com.bbw.god.game.config.Cfg;
import com.bbw.god.gameuser.CfgShakeProp;
import lombok.Data;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 摇骰子路径随机参数
 *
 * @author suhq
 * @date 2020-10-29 10:20
 **/
@Data
public class ShakeRandomParam {
    private static String DEFAULT_CONFIG = "默认";
    private static Map<NumInterval, String> intervalConfigMap = new HashMap<>();

    static {
        List<CfgShakeProp> shakeProps = Cfg.I.get(CfgShakeProp.class);
        for (CfgShakeProp shakeProp : shakeProps) {
            String key = shakeProp.getKey();
            if (key.contains("等级[")) {
                String nums[] = key.substring(key.indexOf("[") + 1, key.indexOf("]")).split(",");
                NumInterval numInterval = new NumInterval(Integer.valueOf(nums[0]), Integer.valueOf(nums[1]));
                intervalConfigMap.put(numInterval, key);
            }
        }

    }

    private String shakePropConfig;
    private Integer guLevel = 1;

    public CfgShakeProp getShakePropConfig() {
//        long begin = System.currentTimeMillis();
        String name = shakePropConfig;
        if (StringUtils.isBlank(name)) {
            for (NumInterval numInterval : intervalConfigMap.keySet()) {
                if (numInterval.isBetween(guLevel)) {
                    name = intervalConfigMap.get(numInterval);
                    break;
                }
            }
        }
        if (name == null) {
            name = DEFAULT_CONFIG;
        }
        CfgShakeProp shakeProp = Cfg.I.get(name, CfgShakeProp.class);
        CfgShakeProp result = CloneUtil.clone(shakeProp);
//        System.out.println("获取配置文件信息耗时：" + (System.currentTimeMillis() - begin));
        return result;
    }

    @Data
    static class NumInterval {
        private int min;
        private int max;

        public NumInterval(int min, int max) {
            this.min = min;
            this.max = max;
        }

        public boolean isBetween(int num) {
            return num >= min && num <= max;
        }
    }
}
