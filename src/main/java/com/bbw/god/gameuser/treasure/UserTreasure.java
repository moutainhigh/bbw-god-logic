package com.bbw.god.gameuser.treasure;

import com.bbw.common.DateUtil;
import com.bbw.common.ID;
import com.bbw.common.ListUtil;
import com.bbw.common.SpringContextUtil;
import com.bbw.god.detail.AwardDetail;
import com.bbw.god.detail.DetailData;
import com.bbw.god.detail.disruptor.DetailEventHandler;
import com.bbw.god.event.BaseEventParam;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.treasure.CfgTreasureEntity;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.UserCfgObj;
import com.bbw.god.gameuser.UserDataType;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.rd.RDCommon;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户道具
 *
 * @author suhq 2018年10月8日 下午2:21:07
 */
@Slf4j
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class UserTreasure extends UserCfgObj implements Serializable {
    private static GameUserService gameUserService = SpringContextUtil.getBean(GameUserService.class);
    private static final long serialVersionUID = 1L;
    private Integer ownNum = 0;//永久数量
    private List<LimitInfo> limitInfos;
    private Date lastGetTime;

    public static UserTreasure instance(long guId, CfgTreasureEntity cfgTreasure, int addNum) {
        UserTreasure uTreasure = new UserTreasure();
        uTreasure.setId(ID.INSTANCE.nextId());
        uTreasure.setBaseId(cfgTreasure.getId());
        uTreasure.setName(cfgTreasure.getName());
        uTreasure.setGameUserId(guId);
        uTreasure.setOwnNum(addNum);
        uTreasure.setLastGetTime(DateUtil.now());
        return uTreasure;
    }

    public static UserTreasure instanceAsTimeLimit(long guId, CfgTreasureEntity cfgTreasure, int addNum, Date expireTime) {
        UserTreasure uTreasure = new UserTreasure();
        uTreasure.setId(ID.INSTANCE.nextId());
        uTreasure.setBaseId(cfgTreasure.getId());
        uTreasure.setName(cfgTreasure.getName());
        uTreasure.setGameUserId(guId);
        uTreasure.addTimeLimitNum(addNum, expireTime);
        uTreasure.setLastGetTime(DateUtil.now());
        return uTreasure;
    }

    @Override
    public UserDataType gainResType() {
        return UserDataType.TREASURE;
    }

    public List<LimitInfo> gainLimitInfosExcludeExpired() {
        if (ListUtil.isNotEmpty(limitInfos)) {
            //去除失效的
            Long nowLong = DateUtil.toDateTimeLong();
            List<LimitInfo> expires = limitInfos.stream().filter(tmp -> tmp.getExpireTime() <= nowLong).collect(Collectors.toList());
            limitInfos = limitInfos.stream().filter(tmp -> tmp.getExpireTime() > nowLong).collect(Collectors.toList());
            limitInfos.removeIf(tmp -> DateUtil.getMonthsBetween(DateUtil.fromDateLong(tmp.getExpireTime()), DateUtil.now()) >= 6);
            if (ListUtil.isNotEmpty(expires)) {
                gameUserService.updateItem(this);
                try {
                    long expireNum = expires.stream().mapToInt(LimitInfo::getTimeLimitNum).sum();
                    if (expireNum > 0) {
                        AwardDetail awardDetail = AwardDetail.fromTreasure(getBaseId(), expireNum);
                        BaseEventParam bep = new BaseEventParam(this.getGameUserId(), WayEnum.EXPIRE, new RDCommon());
                        TreasureEventPublisher.pubTreasureExpiredEvent(this.getBaseId(), expireNum, bep);
                        // 明细
                        GameUser gu = gameUserService.getGameUser(gameUserId);
                        DetailData detail = DetailData.instance(gu, WayEnum.EXPIRE, awardDetail);
                        detail.setAfterValue((long) gainTotalNum());
                        DetailEventHandler.getInstance().log(detail);
                    }
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }

        }
        return limitInfos;
    }

    /**
     * 获得可用总数
     *
     * @return
     */
    public int gainTotalNum() {
        return ownNum + gainTimeLimitNum();
    }

    /**
     * 添加永久数量
     *
     * @param num
     */
    public void addNum(int num) {
        this.ownNum += num;
        this.lastGetTime = DateUtil.now();
    }

    /**
     * 添加时限数量
     *
     * @param num
     * @param expireTime
     */
    public void addTimeLimitNum(int num, Date expireTime) {
        List<LimitInfo> limitInfos = gainLimitInfosExcludeExpired();
        if (ListUtil.isEmpty(limitInfos)) {
            limitInfos = new ArrayList<>();
        }
        long expireTimeLong = DateUtil.toDateTimeLong(expireTime);
        LimitInfo limitInfo = limitInfos.stream().filter(tmp -> tmp.getExpireTime() == expireTimeLong).findFirst().orElse(null);
        if (limitInfo == null) {
            limitInfo = new LimitInfo(num, expireTimeLong);
            limitInfos.add(limitInfo);
        } else {
            limitInfo.addLimitNum(num);
        }
        this.limitInfos = limitInfos;
        this.lastGetTime = DateUtil.now();
    }

    /**
     * 扣除数量，优先扣除会过期的数量
     *
     * @param num
     */
    public void deductNum(int num) {
        int deductOwnNum = 0;
        int timeLimitNum = gainTimeLimitNum();
        if (timeLimitNum > 0) {
            List<LimitInfo> limitInfos = gainLimitInfosExcludeExpired();
            int remainToDeduct = num;
            for (LimitInfo limitInfo : limitInfos) {
                remainToDeduct = limitInfo.deductLimitNum(remainToDeduct);
                if (remainToDeduct == 0) {
                    break;
                }
            }
            deductOwnNum = remainToDeduct;
        } else {
            deductOwnNum = num;
        }
        if (deductOwnNum > 0) {
            this.ownNum -= deductOwnNum;
            if (this.ownNum < 0) {
                this.ownNum = 0;
            }
        }

    }

    private int gainTimeLimitNum() {
        List<LimitInfo> limitInfos = gainLimitInfosExcludeExpired();
        int timeLimitNum = 0;
        long dateLong = DateUtil.toDateTimeLong();
        if (ListUtil.isNotEmpty(limitInfos)) {
            timeLimitNum = limitInfos.stream().filter(tmp -> tmp.getExpireTime() > dateLong).mapToInt(LimitInfo::getTimeLimitNum).sum();
        }
        return timeLimitNum;
    }


    @Data
    @AllArgsConstructor
    public static class LimitInfo implements Serializable {
        private static final long serialVersionUID = -9095938871702723223L;
        private Integer timeLimitNum = 0;//有时限的数量
        private Long expireTime;//时限数过期时间

        public void addLimitNum(int num) {
            timeLimitNum += num;
        }

        /**
         * @param num
         * @return 返回需要继续扣除的
         */
        public int deductLimitNum(int num) {
            int remainToDeduct = 0;
            timeLimitNum -= num;
            if (timeLimitNum < 0) {
                remainToDeduct = -timeLimitNum;
                timeLimitNum = 0;
            }
            return remainToDeduct;
        }
    }
}
