package com.bbw.god.gm;

import com.bbw.common.Rst;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.city.chengc.difficulty.UserAttackDifficulty;
import com.bbw.god.city.chengc.difficulty.UserAttackDifficultyLogic;
import com.bbw.god.controller.AbstractController;
import com.bbw.god.db.async.UpdateRoleInfoAsyncHandler;
import com.bbw.god.db.entity.CfgServerEntity;
import com.bbw.god.db.entity.InsRoleInfoEntity;
import com.bbw.god.db.pool.PlayerPool;
import com.bbw.god.game.config.server.ServerTool;
import com.bbw.god.game.sxdh.SxdhFighter;
import com.bbw.god.game.sxdh.SxdhService;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.UserData;
import com.bbw.god.gameuser.UserDataType;
import com.bbw.god.gameuser.redis.GameUserRedisUtil;
import com.bbw.god.gameuser.redis.UserRedisKey;
import com.bbw.god.gameuser.unique.UserZxz;
import com.bbw.god.login.repairdata.RepairNightmareService;
import com.bbw.god.server.ServerService;
import com.bbw.god.server.ServerUserService;
import com.bbw.god.server.fst.FstRanking;
import com.bbw.god.server.fst.server.FstServerService;
import com.bbw.god.server.guild.UserGuild;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 迁移账号相关接口
 *
 * @author suhq
 * @date 2019年4月12日 上午11:55:43
 */
@Slf4j
@RestController
@RequestMapping("/gm")
public class GMTransferCtrl extends AbstractController {
    @Autowired
    private ServerUserService serverUserService;
    @Autowired
    private GameUserRedisUtil userRedis;
    @Autowired
    private PlayerPool playerPool;
    @Autowired
    private SxdhService sxdhService;
    @Autowired
    private ServerService serverService;
    @Autowired
    private FstServerService fstService;
    @Autowired
    protected UserAttackDifficultyLogic attackDifficultyLogic;
    @Autowired
    private RepairNightmareService nightmareService;
    @Autowired
    private UpdateRoleInfoAsyncHandler updateRoleInfoAsyncHandler;

    /**
     * 转移角色数据到新区服新账号。
     * 不转移【活动】数据、冲榜数据、封神台排名、行会非个人数据、好友相关数据、神仙大会排名、阐截斗法。
     * 诛仙阵重置、神仙大会称号积分重置 转移后的角色信息（昵称、性别、头像、属性等）以目标区服角色为准
     *
     * @param fromServer
     * @param fromNickname
     * @param toServer
     * @param toNickname
     * @return
     */
    @GetMapping("server!transferAccount")
    public Rst transferAccount(String fromServer, String fromNickname, String toServer, String toNickname) {
        GameUser fromUser = this.getGameUser(fromServer, fromNickname);
        GameUser toUser = this.getGameUser(toServer, toNickname);
        toUser.setCopper(fromUser.getCopper());
        toUser.setGold(fromUser.getGold());
        toUser.setGoldEle(fromUser.getGoldEle());
        toUser.setWoodEle(fromUser.getWoodEle());
        toUser.setWaterEle(fromUser.getWaterEle());
        toUser.setFireEle(fromUser.getFireEle());
        toUser.setEarthEle(fromUser.getEarthEle());
        toUser.setDice(fromUser.getDice());
        toUser.setLevel(fromUser.getLevel());
        toUser.setExperience(fromUser.getExperience());
        toUser.getStatus().setGuideStatus(fromUser.getStatus().getGuideStatus());
        toUser.getStatus().setEndFightBuyTime(fromUser.getStatus().getEndFightBuyTime());
        toUser.getStatus().setFirstBought(fromUser.getStatus().isFirstBought());
        toUser.getStatus().setGrowTaskCompleted(fromUser.getStatus().isGrowTaskCompleted());
        toUser.getStatus().setJkEndTime(fromUser.getStatus().getJkEndTime());
        toUser.getStatus().setJkAwardTime(fromUser.getStatus().getJkAwardTime());
        toUser.getStatus().setYkEndTime(fromUser.getStatus().getYkEndTime());
        toUser.getStatus().setYkAwardTime(fromUser.getStatus().getYkAwardTime());
        toUser.getStatus().setSalaryCopperTime(fromUser.getStatus().getSalaryCopperTime());
        toUser.getStatus().setDiceBuyTimes(fromUser.getStatus().getDiceBuyTimes());
        toUser.getStatus().setDiceLastBuyTime(fromUser.getStatus().getDiceLastBuyTime());
        toUser.getStatus().setLastTianlingAwardTime(fromUser.getStatus().getLastTianlingAwardTime());
        toUser.getStatus().setSatisfaction(fromUser.getStatus().getSatisfaction());
        toUser.getSetting().setAttackMaouInfo(fromUser.getSetting().getAttackMaouInfo());
        toUser.getSetting().setDefaultDeck(fromUser.getSetting().getDefaultDeck());
        toUser.getSetting().setActiveMbx(fromUser.getSetting().getActiveMbx());
        this.userRedis.toRedis(toUser);
        this.playerPool.addToUpdatePool(toUser.getId());

        InsRoleInfoEntity role = new InsRoleInfoEntity();
        role.setUid(toUser.getId());
        role.setLevel(toUser.getLevel());
        updateRoleInfoAsyncHandler.setRoleInfo(role, 4);

        List<UserDataType> userDataTypes = this.getUserDataTypes();
        for (UserDataType dataType : userDataTypes) {
            // 删除原数据
            List<UserData> toUDs = this.gameUserService.getMultiItems(toUser.getId(), (Class<UserData>) dataType.getEntityClass());
            this.gameUserService.deleteItems(toUser.getId(), toUDs);
            // 更新新数据
            List<UserData> fromUDs = this.gameUserService.getMultiItems(fromUser.getId(), (Class<UserData>) dataType.getEntityClass());
            fromUDs.forEach(tmp -> {
                tmp.setGameUserId(toUser.getId());
                if (dataType == UserDataType.Guild_User_Info) {
                    UserGuild userGuild = (UserGuild) tmp;
                    userGuild.setGuildId(0L);
                    userGuild.setGuildLv(0);
                } else if (dataType == UserDataType.ZXZ) {
                    UserZxz userZxz = (UserZxz) tmp;
                    userZxz.setGuardians(UserZxz.DEFAULT_GUARDIANS);
                } else if (dataType == UserDataType.SXDH_FIGHTER) {
                    SxdhFighter sxdhFighter = (SxdhFighter) tmp;
                    sxdhFighter.setWinTimes(0);
                    sxdhFighter.setStreak(0);
                    String fighterKey = UserRedisKey.getUserDataKey(sxdhFighter);
                    int serverGroup = ServerTool.getServerGroup(toUser.getServerId());
                    if (!this.sxdhService.isJoinedSxdh(fighterKey, serverGroup)) {
                        this.sxdhService.joinSxdh(fighterKey, serverGroup);
                    }
                }
            });
            this.gameUserService.updateItems(fromUDs);
        }
        // 更新封神台
        Optional<FstRanking> fromFstRankingOptional = this.fstService.getFstRanking(fromUser.getId());
        if (fromFstRankingOptional.isPresent()) {
            FstRanking fromRstRanking = fromFstRankingOptional.get();
            this.fstService.intoFstRanking(toUser.getId());
            FstRanking toFstRanking = this.fstService.getFstRanking(toUser.getId()).get();
            toFstRanking.setTodayFightTimes(fromRstRanking.getTodayFightTimes());
            this.serverService.updateServerData(toFstRanking);
        }
        /**
         * 计算攻城信息
         */
        UserAttackDifficulty difficulty = attackDifficultyLogic.getAttackDifficulty(getUserId());
        attackDifficultyLogic.resettleAttackDifficulty(difficulty);
        /**
         * 补发神物
         */
        nightmareService.addShenWu(getUserId());
        return Rst.businessOK();
    }

    /**
     * 获取要迁移的玩家对象
     *
     * @param serverName
     * @param nickname
     * @return
     */
    private GameUser getGameUser(String serverName, String nickname) {
        CfgServerEntity server = ServerTool.getServer(serverName);
        if (server == null) {
            throw ExceptionForClientTip.fromMsg("无效的区服-" + serverName);
        }
        Optional<Long> uidOptional = this.serverUserService.getUidByNickName(server.getMergeSid(), nickname);
        if (!uidOptional.isPresent()) {
            throw ExceptionForClientTip.fromMsg(serverName + " 不存在角色 " + nickname);
        }
        long uid = uidOptional.get();
        GameUser gu = this.gameUserService.getGameUser(uid);
        return gu;
    }

    /**
     * 获取要迁移的数据类型
     *
     * @return
     */
    private List<UserDataType> getUserDataTypes() {
        List<UserDataType> dataTypes = new ArrayList<>();
        dataTypes.add(UserDataType.ACTIVITY);
        dataTypes.add(UserDataType.ACHIEVEMENT);
        dataTypes.add(UserDataType.ACHIEVEMENT_AWARDED);
        dataTypes.add(UserDataType.CARD);
        dataTypes.add(UserDataType.CARD_GROUP);
        dataTypes.add(UserDataType.SPECIAL);
        dataTypes.add(UserDataType.CITY);
        dataTypes.add(UserDataType.CITY_SETTING);
        dataTypes.add(UserDataType.TREASURE);
        dataTypes.add(UserDataType.TREASURE_EFFECT);
        dataTypes.add(UserDataType.TREASURE_RECORD);
        dataTypes.add(UserDataType.MALL_RECORD);
        dataTypes.add(UserDataType.MALL_SM_REFRESH);
        dataTypes.add(UserDataType.CARD_POOL);
        dataTypes.add(UserDataType.DAILY_TASK);
        dataTypes.add(UserDataType.MAIN_TASK);
        dataTypes.add(UserDataType.GROW_TASK);
        dataTypes.add(UserDataType.ZXZ);
        dataTypes.add(UserDataType.MAIL);
        dataTypes.add(UserDataType.TYF_FILL_RECORD);
        dataTypes.add(UserDataType.LOGIN_INFO);
        dataTypes.add(UserDataType.RECEIPT);
        dataTypes.add(UserDataType.HELP_ABOUT);
        dataTypes.add(UserDataType.Chamber_Of_Commerce_User_Info);
        // dataTypes.add(UserDataType.ChanjieUserInfo);
        dataTypes.add(UserDataType.Guild_User_Info);// guildId、guildLv置0
        dataTypes.add(UserDataType.SXDH_FIGHTER);
        dataTypes.add(UserDataType.SXDH_SHOP_RECORD);
        dataTypes.add(UserDataType.BI_YOU_PALACE);
        return dataTypes;
    }

}
