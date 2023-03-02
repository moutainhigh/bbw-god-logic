package com.bbw.god.gm.service;

import com.bbw.common.ListUtil;
import com.bbw.common.SpringContextUtil;
import com.bbw.god.activityrank.ActivityRankEnum;
import com.bbw.god.activityrank.ActivityRankService;
import com.bbw.god.activityrank.IActivityRank;
import com.bbw.god.city.UserCityService;
import com.bbw.god.city.chengc.UserCity;
import com.bbw.god.db.entity.InsReceiptEntity;
import com.bbw.god.db.entity.InsUserDataEntity;
import com.bbw.god.db.entity.InsUserEntity;
import com.bbw.god.db.pool.DetailDataDAO;
import com.bbw.god.db.pool.PlayerDataDAO;
import com.bbw.god.db.service.InsRoleInfoService;
import com.bbw.god.game.config.city.CfgCityEntity;
import com.bbw.god.gameuser.UserDataType;
import com.bbw.god.pay.DispatchProduct;
import com.bbw.god.server.fst.server.FstServerService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 榜单重新生成服务
 *
 * @author suhq
 * @date 2019-07-11 11:02:10
 */
@Slf4j
@Service
public class ActivityRankRegenerateService {
    @Autowired
    private ActivityRankService activityRankService;
    @Autowired
    private FstServerService fstServerService;
    @Autowired
    private InsRoleInfoService insRoleInfoService;
    @Autowired
    private UserCityService userCityService;

    /**
     * 重新生成榜单
     *
     * @param ar
     * @return 加入榜单的人数
     */
    public int regenerateRankers(IActivityRank ar) {
        List<ActivityRanker> rankers = null;

        ActivityRankEnum type = ActivityRankEnum.fromValue(ar.gainType());
        switch (type) {
            case RECHARGE_RANK:
                rankers = getRechargeRankers(ar);
                break;
            case FUHAO_RANK:
            case ELE_CONSUME_RANK:
            case WIN_BOX_RANK:
                DetailDataDAO detailDataDAO = SpringContextUtil.getBean(DetailDataDAO.class, ar.gainSId());
                rankers = detailDataDAO.dbGetRankersFromDetail(ar);
                break;
            case CHENGCHI_RANK:
                rankers = getChengchiRankers(ar);
                break;
            case GU_LEVEL_RANK:
                rankers = getLevelRankers(ar);
                break;
            case FST_RANK:
                rankers = getFstRankers(ar.gainSId());
                break;
            case ATTACK_RANK:
                rankers = getAttackRankers(ar);
                break;
            // TODO 其他榜单重建
            default:
                rankers = new ArrayList<>();
                break;
        }

        int rankerSize = 0;

        if (ListUtil.isNotEmpty(rankers)) {
            activityRankService.removeRank(ar);
            for (ActivityRanker ranker : rankers) {
                long uid = ranker.getUid();
                activityRankService.setRankValue(uid, ar.gainSId(), ranker.getValue(), type);
                log.info("加入玩家{},{}", uid, ranker.getValue());
            }
            rankerSize = rankers.size();
        }
        return rankerSize;
    }

    /**
     * 封神台榜单
     *
     * @param sId
     * @return
     */
    private List<ActivityRanker> getFstRankers(int sId) {
        List<ActivityRanker> detailSums = new ArrayList<>();
        Set<TypedTuple<Long>> rankers = fstServerService.getAllRankers(sId);
        int rank = 0;
        for (TypedTuple<Long> ranker : rankers) {
            rank++;
            Long uid = Optional.ofNullable(ranker.getValue()).orElse(0L);
            detailSums.add(new ActivityRanker(uid, rank));
        }
        return detailSums;
    }

    /**
     * 获得充值榜
     *
     * @param ar
     * @return
     */
    private List<ActivityRanker> getRechargeRankers(IActivityRank ar) {
        List<ActivityRanker> rankers = new ArrayList<>();
        DispatchProduct dispatchProduct = SpringContextUtil.getBean(DispatchProduct.class, ar.gainSId());
        List<InsReceiptEntity> receipts = dispatchProduct.dbGetDispatchedReceipts(ar.gainBegin(), ar.gainEnd());
        if (ListUtil.isEmpty(receipts)) {
            return new ArrayList<>();
        }
        Map<Long, Integer> uidRechargeMap = receipts.stream().collect(Collectors.groupingBy(InsReceiptEntity::getUid, Collectors.summingInt(InsReceiptEntity::getPrice)));
        for (Long uid : uidRechargeMap.keySet()) {
            int rechargeGold = uidRechargeMap.get(uid);
            rankers.add(new ActivityRanker(uid, rechargeGold));
        }
        rankers.sort(Comparator.comparing(ActivityRanker::getValue));
        return rankers;
    }

    /**
     * 攻城榜
     *
     * @param ar
     * @return
     */
    private List<ActivityRanker> getChengchiRankers(IActivityRank ar) {
        List<ActivityRanker> rankers = new ArrayList<>();

        PlayerDataDAO playerDataDAO = SpringContextUtil.getBean(PlayerDataDAO.class, ar.gainSId());
        List<InsUserDataEntity> allCities = playerDataDAO.dbSelectUserDataByType(UserDataType.CITY.getRedisKey());
        if (ListUtil.isEmpty(allCities)) {
            return new ArrayList<>();
        }
        Map<Long, Long> uidCityNumMap = allCities.stream().collect(Collectors.groupingBy(InsUserDataEntity::getUid, Collectors.counting()));
        for (Long uid : uidCityNumMap.keySet()) {
            rankers.add(new ActivityRanker(uid, uidCityNumMap.get(uid).intValue()));
        }
        rankers.sort(Comparator.comparing(ActivityRanker::getValue));
        return rankers;
    }

    /**
     * 等级榜
     *
     * @param ar
     * @return
     */
    private List<ActivityRanker> getLevelRankers(IActivityRank ar) {
        List<ActivityRanker> rankers = new ArrayList<>();

        PlayerDataDAO playerDataDAO = SpringContextUtil.getBean(PlayerDataDAO.class, ar.gainSId());
        List<InsUserEntity> allUsers = playerDataDAO.dbSelectUsers();
        if (ListUtil.isEmpty(allUsers)) {
            return new ArrayList<>();
        }
        for (InsUserEntity user : allUsers) {
            rankers.add(new ActivityRanker(user.getUid(), user.getLevel()));
        }
        rankers.sort(Comparator.comparing(ActivityRanker::getValue));
        return rankers;
    }

    /**
     * 重新获得王者榜榜单。王者榜的开始时间 <= 开服时间，
     *
     * @param ar
     * @return
     */
    private List<ActivityRanker> getAttackRankers(IActivityRank ar) {
        List<ActivityRanker> rankers = new ArrayList<>();
        List<Long> uids = insRoleInfoService.getAllUidsByServer(ar.gainSId());
        for (Long uid : uids) {
            List<UserCity> userCities = userCityService.getUserCities(uid);
            if (ListUtil.isEmpty(userCities)) {
                continue;
            }
            int addTotalPoints = 0;
            for (UserCity userCity : userCities) {
                int addPoint = (1 + userCity.getHierarchy()) * getAttackPoint(userCity.gainCity());
                addTotalPoints += addPoint;
            }
            rankers.add(new ActivityRanker(uid, addTotalPoints));
        }
        rankers.sort(Comparator.comparing(ActivityRanker::getValue));
        return rankers;
    }

    private int getAttackPoint(CfgCityEntity city) {
        int level = city.getLevel();
        switch (level) {
            case 1:
            case 2:
                return 1;
            case 3:
            case 4:
                return 2;
            case 5:
                return 3;
            default:
                return 0;
        }
    }

    /**
     * 冲榜者
     *
     * @author suhq
     * @date 2019-07-11 11:05:36
     */
    @Data
    @AllArgsConstructor
    public static class ActivityRanker {
        private Long uid;
        private Integer value;
    }

}
