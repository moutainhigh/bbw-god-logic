package com.bbw.god.server.guild;

import com.bbw.common.DateUtil;
import com.bbw.common.ID;
import com.bbw.god.gameuser.UserDataType;
import com.bbw.god.gameuser.UserSingleObj;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 玩家行会
 *
 * @author lwb
 * @version 1.0
 * @date 2019年5月15日
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class UserGuild extends UserSingleObj implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long guildId = 0l;
    private Integer guildLv = 0;
    private Date operateTime = null;// 行会变更时间 如加入和退会
    private List<WordsStatus> words = new ArrayList<WordsStatus>();//留言
    private Integer contrbution = 0;//总贡献
    private Integer weekContrbution = 0;//周贡献
    private Integer weekContrbutionDate;//周贡献每周首次统计时间
    private GuildShopInfo shopInfo;
    private Integer taskRemind = 0;
    private UserGuildTaskInfo taskInfo = null;// 下版本可删除

    public static UserGuild instance(long uid) {
        UserGuild userGuild = new UserGuild();
        userGuild.setGameUserId(uid);
        userGuild.setWeekContrbutionDate(DateUtil.toDateInt(DateUtil.getWeekBeginDateTime(new Date())));
        userGuild.setId(ID.INSTANCE.nextId());
        return userGuild;
    }

    @Data
    public static class ShopLimit implements Serializable {
        private static final long serialVersionUID = 1L;
        private Integer Id = null;
        private Integer bought = 1;

        public static ShopLimit instance(int shopId) {
            ShopLimit limit = new ShopLimit();
            limit.setId(shopId);
            return limit;
        }
    }

    @Data
    public static class WordsStatus implements Serializable {
        private static final long serialVersionUID = 1L;
        private Long uid;
        private Date writeDate;
        private boolean isRead;
    }


    @Data
    public static class GuildShopInfo {
        private Integer buildDate;
        private List<ShopLimit> shops;

        public GuildShopInfo() {
            buildDate = DateUtil.toDateInt(new Date());
            shops = new ArrayList<ShopLimit>();
        }
    }

    @Override
    public UserDataType gainResType() {
        return UserDataType.Guild_User_Info;
    }

    public int getShopBought(int shopId) {
        for (ShopLimit limit : shopInfo.getShops()) {
            if (limit.getId() == shopId) {
                return limit.getBought();
            }
        }
        return 0;
    }

    public void addShopBought(int shopId) {
        for (ShopLimit limit : shopInfo.getShops()) {
            if (limit.getId() == shopId) {
                limit.setBought(limit.getBought() + 1);
                return;
            }
        }
        ShopLimit limit = ShopLimit.instance(shopId);
        shopInfo.getShops().add(limit);
    }
}
