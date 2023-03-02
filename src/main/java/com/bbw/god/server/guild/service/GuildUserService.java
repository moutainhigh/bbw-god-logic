package com.bbw.god.server.guild.service;

import com.bbw.common.DateUtil;
import com.bbw.common.ID;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.rd.RDCommon;
import com.bbw.god.server.guild.GuildConstant;
import com.bbw.god.server.guild.GuildInfo;
import com.bbw.god.server.guild.UserGuild;
import com.bbw.god.server.guild.UserGuild.GuildShopInfo;
import com.bbw.god.server.guild.UserGuildTaskInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

/**
 * @author 作者 ：lwb
 * @version 创建时间：2019年10月18日 下午3:04:50 类说明 行会用户相关操作
 */
@Service
@Slf4j
public class GuildUserService {
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private GuildInfoService guildInfoService;

    private static final int Open_Lv = 12;

    /**
     * 是否加入了行会
     *
     * @param uid
     * @return
     */
    public boolean hasGuild(long uid) {
        if (!opend(uid)) {
            return false;
        }
        Optional<UserGuild> userGuildOp = getUserGuildOp(uid);
        if (userGuildOp.isPresent() && userGuildOp.get().getGuildId() > 0) {
            return true;
        }
        return false;
    }

    /**
     * 获取玩家当前行会的名称
     *
     * @param sid
     * @param uid
     * @return
     */
    public String getGuildName(int sid, long uid) {
        UserGuild userGuild = gameUserService.getSingleItem(uid, UserGuild.class);
        if (null == userGuild || userGuild.getGuildId() == 0) {
            return null;
        }
        try {
            GuildInfo info = guildInfoService.getGuildInfoBydataId(sid, userGuild.getGuildId());
            return info.getGuildName();
        } catch (Exception e) {
            //可能行会不存在，如数据迁移问题导致
        }
        return null;
    }

    /**
     * 是否达到开放要求
     *
     * @param uid
     * @return
     */
    public boolean opend(long uid) {
        int level = gameUserService.getGameUser(uid).getLevel();
        if (level >= Open_Lv) {
            return true;
        }
        return false;
    }

    public Optional<UserGuild> getUserGuildOp(long uid) {
        UserGuild userGuild = gameUserService.getSingleItem(uid, UserGuild.class);
        if (userGuild == null) {
            if (opend(uid)) {
                userGuild = UserGuild.instance(uid);
                gameUserService.addItem(uid, userGuild);
                return Optional.of(userGuild);
            }
            return Optional.empty();
        }
        if (userGuild.getTaskInfo() != null) {
            // 说明是旧版任务数据
            UserGuildTaskInfo userGuildTaskInfo = userGuild.getTaskInfo();
            userGuildTaskInfo.setId(ID.INSTANCE.nextId());
            userGuildTaskInfo.setGameUserId(uid);
            gameUserService.addItem(uid, userGuildTaskInfo);
            userGuild.setTaskInfo(null);
        }
        if (userGuild.getContrbution() > 0) {
            int num = userGuild.getContrbution();
            log.info(uid + "迁移行会贡献：" + userGuild.getContrbution());
            userGuild.setContrbution(0);
            TreasureEventPublisher.pubTAddEvent(uid, TreasureEnum.GUILD_CONTRIBUTE.getValue(),
                    num, WayEnum.UPDATE, new RDCommon());
            gameUserService.updateItem(userGuild);
        }
        int weekBegin = DateUtil.toDateInt(DateUtil.getWeekBeginDateTime(new Date()));
        if (userGuild.getWeekContrbutionDate() == null || userGuild.getWeekContrbutionDate() != weekBegin) {
            userGuild.setWeekContrbutionDate(weekBegin);
            userGuild.setWeekContrbution(0);
            gameUserService.updateItem(userGuild);
        }
        // 检查限购
        if (userGuild.getShopInfo() == null || userGuild.getShopInfo().getBuildDate() != DateUtil.getTodayInt()) {
            userGuild.setShopInfo(new GuildShopInfo());
            gameUserService.updateItem(userGuild);
        }
        return Optional.of(userGuild);
    }

    /**
     * 存在则返回对象，不存在则 抛出异常
     *
     * @param uid
     * @return
     */
    public UserGuild getUserGuild(long uid) {
        Optional<UserGuild> uOptional = getUserGuildOp(uid);
        if (uOptional.isPresent()) {
            return uOptional.get();
        }
        throw new ExceptionForClientTip("guild.not.creat", GuildConstant.OPEN_LEVEL);
    }

    public int getGuildLv(long uid) {
        Optional<UserGuild> usOptional = getUserGuildOp(uid);
        if (usOptional.isPresent()) {
            return usOptional.get().getGuildLv();
        }
        return 0;
    }
}
