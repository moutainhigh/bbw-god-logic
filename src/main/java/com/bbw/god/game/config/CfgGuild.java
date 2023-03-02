package com.bbw.god.game.config;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bbw.god.game.award.Award;
import com.bbw.god.server.guild.GuildShop;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author lwb
 * @version 1.0
 * @date 2019年5月16日
 */
@Data
public class CfgGuild implements CfgInterface {
    private String key;
    private List<TaskInfo> tasks;
    private List<BoxReward> boxRewards;
    private List<ProductAward> taskRewards;
    private List<GuildShop> products;
    private List<Level> levels;
    private Integer maxLevel;// 满级数

    @Data
    public static class TaskInfo implements Serializable {
        private static final long serialVersionUID = -3236458475591048757L;
        private Integer id;
        private Integer attr;
        private String content;
        private Integer target;
    }

    @Data
    public static class BoxReward implements Serializable {
        private static final long serialVersionUID = 1L;
        private Integer lv;
        private Integer copper;
        private Integer contrbution;
        private Integer exp;
    }

    @Data
    public static class TaskReward {
        private Integer lv;
        private Integer contrbution;
        private Integer exp;
    }

    @Data
    public static class Level implements Serializable {
		private static final long serialVersionUID = 4388389565096475434L;
		private Integer lv;
        private Integer limitPeople;
        private Integer baseExp;
        private Integer targetExp;
        private Integer openBoxTimes;
    }

    @Data
    public static class ProductAward implements Serializable {
		private static final long serialVersionUID = -8057398379787864391L;
		private Integer productId;// 产品ID
        private String memo;
        private List<Award> awardList;// 产品组
    }

    @Override
    public Serializable getId() {
        return key;
    }

    @Override
    public int getSortId() {
        return 0;
    }

    public List<GuildShop> getProducts() {
        JSONArray array = JSONArray.parseArray(JSONArray.toJSONString(products));
        return JSONObject.parseArray(array.toJSONString(), GuildShop.class);
    }
}
