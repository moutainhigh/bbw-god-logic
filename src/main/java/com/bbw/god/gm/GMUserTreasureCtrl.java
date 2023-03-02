package com.bbw.god.gm;

import com.bbw.common.*;
import com.bbw.god.controller.AbstractController;
import com.bbw.god.db.entity.CfgServerEntity;
import com.bbw.god.db.pool.DetailDataDAO;
import com.bbw.god.detail.LogUtil;
import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.server.ServerTool;
import com.bbw.god.game.config.treasure.CfgTreasureEntity;
import com.bbw.god.game.config.treasure.TreasureTool;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.treasure.UserTreasure;
import com.bbw.god.gameuser.treasure.UserTreasureService;
import com.bbw.god.gameuser.treasure.event.EVTreasure;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.rd.RDCommon;
import com.bbw.god.server.ServerUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 玩家数据相关的操作
 *
 * @author suhq
 * @date 2019年4月13日 下午1:49:18
 */
@RestController
@RequestMapping("/gm")
public class GMUserTreasureCtrl extends AbstractController {
    @Autowired
    private UserGmService userGmService;
    @Autowired
    private UserTreasureService userTreasureService;
    @Autowired
    private ServerUserService serverUserService;
    @Autowired
    private GameUserService gameUserService;


    /**
     * 添加法宝
     *
     * @param sId
     * @param nickname
     * @param treasures
     * @param num
     * @return
     */
    @RequestMapping("user!addTreasures")
    public Rst addTreasures(int sId, String nickname, String treasures, String like, int num) {
        if (!StrUtil.isBlank(like)) {
            return userGmService.addLikeTreasures(sId, nickname, like, num);
        }
        return this.userGmService.addTreasures(sId, nickname, treasures, num);
    }

    /**
     * 添加法宝
     *
     * @param uids      用户id集合
     * @param treasures
     * @param num
     * @return
     */
    @GetMapping("user!batchaAddTreasures")
    public Rst addTreasures(String uids, String treasures, int num) {
        List<Long> uidList = ListUtil.parseStrToLongs(uids);
        for (Long uid : uidList) {
            GameUser gameUser = gameUserService.getGameUser(uid);
            addTreasures(gameUser.getId(), treasures, num);
        }
        return Rst.businessOK();
    }

    /**
     * 补发扣除的道具
     *
     * @param uids
     * @param from 进行累计的开始时间 eg：20221101
     * @param to   进行累计的结束时间 eg：20221110
     */
    @GetMapping("user!reissueTreasures")
    public Rst addTreasures(String uids, int from, int to) {
        List<UserTotalAwarded> userTotalAwardeds = totalAwarded(uids, 60, 50430, from, to);
        for (UserTotalAwarded userTotalAwarded : userTotalAwardeds) {
            addTreasures(userTotalAwarded.getUid(), "竞猜券", (int) userTotalAwarded.getNum());
        }
        return Rst.businessOK();
    }

    public void addTreasures(long uid, String treasures, int num) {
        List<CfgTreasureEntity> ctes = TreasureTool.getAllTreasures();
        if (!"所有".equals(treasures)) {
            String[] split = treasures.split(",");
            List<String> list = Arrays.asList(split);
            ctes = ctes.stream().filter(t -> list.contains(t.getName())).collect(Collectors.toList());
        }
        RDCommon rd = new RDCommon();
        if (num > 0) {
            List<EVTreasure> evTreasures = ctes.stream().map(t -> new EVTreasure(t.getId(), num))
                    .collect(Collectors.toList());
            TreasureEventPublisher.pubTAddEvent(uid, evTreasures, WayEnum.NONE, rd);
        } else {
            ctes.stream().forEach(t -> TreasureEventPublisher.pubTDeductEvent(uid, t.getId(), -num, WayEnum.NONE, rd));
        }
    }

    public List<UserTotalAwarded> totalAwarded(String uids, int awardType, int awardId, int from, int to) {
        List<UserTotalAwarded> userTotalAwardeds = new ArrayList<>();
        List<Long> uidList = ListUtil.parseStrToLongs(uids, ",");
        Map<Integer, List<Long>> sidUidMap = uidList.stream().collect(Collectors.groupingBy(tmp -> gameUserService.getActiveSid(tmp)));
        AwardEnum award = AwardEnum.fromValue(awardType);
        for (Map.Entry<Integer, List<Long>> entry : sidUidMap.entrySet()) {
            int sid = entry.getKey();
            DetailDataDAO detailDataDAO = SpringContextUtil.getBean(DetailDataDAO.class, sid);
            for (Long uid : entry.getValue()) {
                UserTotalAwarded userTotalAwarded = new UserTotalAwarded();
                Long value = detailDataDAO.dbTotalAwarded(uid, award, awardId, from, to);
                userTotalAwarded.setUid(uid);
                userTotalAwarded.setNum(value);
                userTotalAwardeds.add(userTotalAwarded);
            }
        }
        return userTotalAwardeds;
    }

    /**
     * 批量添加法宝
     *
     * @param sId
     * @param nickname
     * @param treasureNums
     * @return
     */
    @RequestMapping("user!addPatchTreasures")
    public Rst addPatchTreasures(int sId, String nickname, String treasureNums) {
        String[] treasures = treasureNums.split(";");
        if (treasures.length > 0) {
            for (String treasure : treasures) {
                String[] treasureInfo = treasure.split(",");
                this.userGmService.addTreasures(sId, nickname, treasureInfo[0], Integer.valueOf(treasureInfo[1]));
            }
        }
        return Rst.businessOK();
    }

    @RequestMapping("user!delXRBD")
    public Rst delXRBD(String uids) {
        String[] splits = uids.split(",");
        for (String split : splits) {
            if (StrUtil.isNotBlank(split)) {
                long uid = Long.parseLong(split);
                List<UserTreasure> userTreasures = userTreasureService.getAllUserTreasures(uid);
                List<UserTreasure> collect = userTreasures.stream().filter(p -> p.getBaseId() >= 11301 && p.getBaseId() <= 11330).collect(Collectors.toList());
                if (collect.isEmpty()) {
                    continue;
                }
                LogUtil.logDeletedUserDatas(collect, "删除玩家仙人袋子");
                gameUserService.deleteItems(uid, collect);
            }
        }
        return Rst.businessOK();
    }

    @RequestMapping("user!addTreasuresByMail")
    public Rst addTreasuresByMail(int sId, String nickname, String treasures, Integer like, int num) {
        return this.userGmService.addTreasuresByMail(sId, nickname, treasures, num, like != null && like == 1);
    }

    /**
     * 更新道具过期时间
     *
     * @param serverGroup     区服组
     * @param lastLoginDaysIn 多少天内登录的玩家
     * @param treasures       要修正的道具
     * @param srcExpiredTime  要修正的过期时间
     * @param newExpiredTime  目标过期时间
     * @return
     */
    @RequestMapping("user!updateTreasureExpiredTime")
    public Rst addTreasuresByMail(int serverGroup, int lastLoginDaysIn, String treasures, String srcExpiredTime, String newExpiredTime) {
        // 时间解析
        Date srcExpiredDate = DateUtil.fromDateTimeString(srcExpiredTime);
        long srcExpiredDateLong = DateUtil.toDateTimeLong(srcExpiredDate);
        Date newExpiredDate = DateUtil.fromDateTimeString(newExpiredTime);
        long newExpiredDateLong = DateUtil.toDateTimeLong(newExpiredDate);

        // 道具解析
        List<Integer> treasureIds = Arrays.asList(treasures.split(",")).stream()
                .map(tmp -> TreasureTool.getTreasureByName(tmp).getId())
                .collect(Collectors.toList());
        List<CfgServerEntity> servers = ServerTool.getGroupServers(serverGroup);
        List<UserTreasure> needToUpdates = new ArrayList<>();
        for (CfgServerEntity server : servers) {
            Set<Long> uidsInDays = serverUserService.getUidsInDays(server.getMergeSid(), lastLoginDaysIn);
            for (Long uid : uidsInDays) {
                List<UserTreasure> uts = userTreasureService.getUserTreasures(uid, treasureIds);
                if (ListUtil.isEmpty(uts)) {
                    continue;
                }
                for (UserTreasure ut : uts) {
                    List<UserTreasure.LimitInfo> limitInfos = ut.getLimitInfos();
                    if (ListUtil.isEmpty(limitInfos)) {
                        continue;
                    }
                    UserTreasure.LimitInfo limitInfo = limitInfos.stream().filter(tmp -> tmp.getExpireTime() == srcExpiredDateLong).findFirst().orElse(null);
                    if (null != limitInfo) {
                        limitInfo.setExpireTime(newExpiredDateLong);
                        needToUpdates.add(ut);
                    }
                }
            }
        }
        Rst rst = Rst.businessOK();
        if (ListUtil.isNotEmpty(needToUpdates)) {
            gameUserService.updateItems(needToUpdates);
            rst.put("更新量", needToUpdates.size());
        }
        return rst;
    }

    @RequestMapping("user!addExpiredTreasure")
    public Rst addTreasuresByMail(long uid, String treasures, String expiredTime, int num) {
        // 时间解析
        Date expiredDate = DateUtil.fromDateTimeString(expiredTime);

        // 道具解析
        List<Integer> treasureIds = Arrays.asList(treasures.split(",")).stream()
                .map(tmp -> TreasureTool.getTreasureByName(tmp).getId())
                .collect(Collectors.toList());
        List<UserTreasure> uts = userTreasureService.getAllUserTreasures(uid);
        for (Integer treasureId : treasureIds) {
            UserTreasure ut = uts.stream().filter(tmp -> tmp.getBaseId().equals(treasureId)).findFirst().orElse(null);
            if (null == ut) {
                ut = UserTreasure.instance(uid, TreasureTool.getTreasureById(treasureId), 0);
                gameUserService.addItem(uid, ut);
            }
            ut.addTimeLimitNum(num, expiredDate);
            gameUserService.updateItem(ut);
        }
        Rst rst = Rst.businessOK();
        return rst;
    }
}
