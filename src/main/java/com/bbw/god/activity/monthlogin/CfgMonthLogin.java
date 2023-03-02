package com.bbw.god.activity.monthlogin;

import com.bbw.god.game.config.CfgInterface;
import lombok.Data;

import java.util.List;

/**
 * @author：lwb
 * @date: 2021/2/26 9:12
 * @version: 1.0
 */
@Data
public class CfgMonthLogin implements CfgInterface {

    private String key;
    private List<EventInfo> activityEvents;
    private List<EventInfo> goodEvents;
    private List<EventInfo> badEvents;

    @Override
    public String getId() {
        return key;
    }

    @Override
    public int getSortId() {
        return 1;
    }

    @Data
    public static class EventInfo{
        private int id;
        private String title;
        private String memo;
        private Integer probability;
        private List<Integer> activitys;
        private List<Integer> days;
    }
}