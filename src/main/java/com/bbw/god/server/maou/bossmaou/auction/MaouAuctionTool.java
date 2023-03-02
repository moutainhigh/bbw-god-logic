package com.bbw.god.server.maou.bossmaou.auction;

import com.bbw.common.DateUtil;
import com.bbw.common.PowerRandom;
import com.bbw.god.db.entity.CfgServerEntity;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.game.config.Cfg;
import com.bbw.god.game.config.treasure.TreasureTool;
import com.bbw.god.gameuser.treasure.processor.ChuanQJZBoxProcessor;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author suchaobin
 * @description 拍卖工具类
 * @date 2020/7/23 11:16
 **/
public class MaouAuctionTool {
    /**
     * 获取所有的拍卖商品配置对象
     *
     * @return
     */
    private static List<CfgMaouAuction> getCfgAuctions() {
        return Cfg.I.get(CfgMaouAuction.class);
    }

    /**
     * 通过配置id获取配置类对象
     *
     * @param id 配置id
     * @return
     */
    public static CfgMaouAuction getCfgAuction(Integer id) {
        return getCfgAuctions().stream().filter(cfg -> cfg.getId().equals(id)).findFirst().orElse(null);
    }

    /**
     * 获取随机拍卖物品
     *
     * @param server 区服
     * @return
     */
    public static CfgMaouAuction getRandomAuction(CfgServerEntity server) {
        Date beginTime = server.getBeginTime();
        int daysBetween = DateUtil.getDaysBetween(beginTime, DateUtil.now());
        List<CfgMaouAuction> cfgMaouAuctions = getCfgAuctions();
        // 开服第一天，魔王拍卖必定五星卡
        if (0 == daysBetween) {
            cfgMaouAuctions = cfgMaouAuctions.stream().filter(tmp -> tmp.getId() == 10).collect(Collectors.toList());
            cfgMaouAuctions.get(0).setProp(100);
        }
        // 开服超过2周
        if (daysBetween >= 14) {
            for (CfgMaouAuction cfgMaouAuction : cfgMaouAuctions) {
                switch (cfgMaouAuction.getName()) {
                    case "普五卡牌":
                        cfgMaouAuction.setProp(0);
                        break;
                    case "四星灵石":
                    case "五星灵石":
                        cfgMaouAuction.setProp(18);
                        break;
                    case "3篇卷轴":
                        cfgMaouAuction.setProp(5);
                        break;
                    default:
                        break;
                }
            }
        }
        return getRandomAuction(cfgMaouAuctions);
    }

    private static CfgMaouAuction getRandomAuction(List<CfgMaouAuction> list) {
        int random = PowerRandom.getRandomBySeed(100);
        int sum = 0;
        for (CfgMaouAuction cfgMaouAuction : list) {
            sum += cfgMaouAuction.getProp();
            if (sum >= random) {
                return cfgMaouAuction;
            }
        }
        return list.get(0);
    }

    /**
     * 根据配置类获取奖励对象
     *
     * @param cfgMaouAuction 拍卖配置对象
     * @return
     */
    public static Award getAward(CfgMaouAuction cfgMaouAuction) {
        Integer awardId = null;
        int item = 60;
        switch (cfgMaouAuction.getName()) {
            case "普五卡牌":
                item = 40;
                awardId = PowerRandom.getRandomFromList(Arrays.asList(101, 236, 302, 401, 502));
                break;
            case "3篇卷轴":
                awardId = PowerRandom.getRandomFromList(ChuanQJZBoxProcessor.getAwardIds(3));
                break;
            case "4篇卷轴":
                awardId = PowerRandom.getRandomFromList(ChuanQJZBoxProcessor.getAwardIds(4));
                break;
            case "5篇卷轴":
                awardId = PowerRandom.getRandomFromList(ChuanQJZBoxProcessor.getAwardIds(5));
                break;
            default:
                awardId = TreasureTool.getTreasureByName(cfgMaouAuction.getName()).getId();
                break;
        }
        return new Award(awardId, AwardEnum.fromValue(item), cfgMaouAuction.getNum());
    }
}
