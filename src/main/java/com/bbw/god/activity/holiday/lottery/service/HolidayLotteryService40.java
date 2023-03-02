package com.bbw.god.activity.holiday.lottery.service;

import com.bbw.common.ListUtil;
import com.bbw.common.PowerRandom;
import com.bbw.db.redis.RedisValueUtil;
import com.bbw.god.activity.holiday.lottery.*;
import com.bbw.god.activity.holiday.lottery.rd.RDHolidayLotteryInfo;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.game.award.AwardService;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.treasure.TreasureChecker;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.rd.RDCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.bbw.god.game.data.redis.RedisKeyConst.SPLIT;

/**
 * 说明：
 * 丰财聚宝 抽奖
 *
 * @author lwb
 * date 2021-04-20
 */
@Service
public class HolidayLotteryService40 implements HolidayLotteryService {
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private AwardService awardService;
    /** 初始化奖池元宝数额 */
    private static final int INIT_GOLD = 800;
    /** 最少元宝数量 */
    private static final int LEAST_GOLD = 300;
    /** 抽奖一次需要元宝 */
    private static final int ONCE_NEED_GOLD = 5;
    /** 抽奖次数上限*/
    private static final int LOTTERY_TIMES_LIMIT = 1100;

    @Override
    public int getMyId() {
        return HolidayLotteryType.FCJB.getValue();
    }

    /**
     * 进入抽奖界面
     * @param uid 玩家id
     * @param param
     * @return
     */
    @Override
    public RDHolidayLotteryInfo getHolidayLotteryInfo(long uid, HolidayLotteryParam param) {
        int gid= gameUserService.getActiveGid(uid);
        RDHolidayLotteryInfo info=new RDHolidayLotteryInfo();
        int totalGold = getTotalGold(gid);
        info.setTotalPoolGold(totalGold);
        return info;
    }

    @Override
    public RDHolidayLotteryInfo draw(long uid, HolidayLotteryParam param) {
        int gid= gameUserService.getActiveGid(uid);
        if (gid==17){
            gid=16;
        }
        RDHolidayLotteryInfo rd=new RDHolidayLotteryInfo();
        rd.setDrawResults(new ArrayList<>());
        for (int i = 0; i < param.getDrawTimes(); i++) {
            TreasureChecker.checkIsEnough(TreasureEnum.LUCKY_COIN.getValue(), 1, uid);
            TreasureEventPublisher.pubTDeductEvent(uid, TreasureEnum.LUCKY_COIN.getValue(), 1, WayEnum.HOLIDAY_LOTTERY_DRAW, rd);
            List<Award> awards = getAwardsByTimes(gid,rd);
            awardService.fetchAward(uid,awards,WayEnum.HOLIDAY_LOTTERY_DRAW,"在节日活动中",rd);
        }
        rd.setTotalPoolGold(getTotalGold(gid));
        return rd;
    }

    @Override
    public void checkForDraw(long uid, HolidayLotteryParam param) {
        int needCoin=param.getDrawTimes();
        TreasureChecker.checkIsEnough(TreasureEnum.LUCKY_COIN.getValue(), needCoin,uid);
    }

    @Override
    public RDCommon previewAwards(HolidayLotteryParam param) {
        return new RDCommon();
    }

    @Override
    public BaseUserHolidayLottery fromRedis(long uid) {
        return null;
    }

    @Override
    public void toRedis(long uid, BaseUserHolidayLottery userHolidayLottery) {
        return;
    }

    @Autowired
    private RedisValueUtil<Integer> valueUtil;

    /**
     * 获取总的抽奖次数:过期时间为10天
     *
     * @return
     */
    public int increaseTimes(int gid){
        Long increment = valueUtil.increment(getGameHolidayDrawTimesKey(gid), 1);
        valueUtil.expire(getGameHolidayDrawTimesKey(gid), 10, TimeUnit.DAYS);
        return increment.intValue();
    }

    /**
     * 获取奖池真实余额
     * @param gid
     * @return
     */
    public int getTotalGold(Integer gid){
        String key=getGameHolidayTotalGoldKey(gid);
        Integer total = valueUtil.get(key);
        if (total==null){
            synchronized (gid){
                total = valueUtil.get(key);
                if (total==null){
                    total=INIT_GOLD;
                    valueUtil.set(key, INIT_GOLD);
                }
            }
        }
        return total;
    }
    /**
     * 获取抽奖次数的Key
     * @param group
     * @return
     */
    private String getGameHolidayDrawTimesKey(int group) {
        if (group==17){
            group=16;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("game");
        sb.append(SPLIT);
        sb.append(group);
        sb.append(SPLIT);
        sb.append("holiday");
        sb.append(SPLIT);
        sb.append("lottery");
        sb.append(SPLIT);
        sb.append(getMyId());
        sb.append(SPLIT);
        sb.append("drawTimes");
        return  sb.toString();//"game"+ SPLIT + group + SPLIT + "holiday" + SPLIT + "lottery" + SPLIT + getMyId() + SPLIT + "drawTimes";
    }

    /**
     *  获取奖池的Key
     * @param group
     * @return
     */
    private String getGameHolidayTotalGoldKey(int group) {
        if (group==17){
            group=16;
        }
        return "game" + SPLIT + group + SPLIT + "holiday" + SPLIT + "lottery" + SPLIT + getMyId() + SPLIT + "totalGlod";
    }

    /**
     * 获取当前抽奖的奖励
     * @return
     */
    public List<Award> getAwardsByTimes(int gid,RDHolidayLotteryInfo rd){
        int times = increaseTimes(gid);
        if (times > LOTTERY_TIMES_LIMIT) {
            //1100一个轮回
            times = times % LOTTERY_TIMES_LIMIT == 0 ? LOTTERY_TIMES_LIMIT : times % LOTTERY_TIMES_LIMIT;
        }
        List<CfgHolidayLotteryAwards> lotteryAwards = HolidayLotteryTool.getAll(getMyId());
        CfgHolidayLotteryAwards lotteryAward=null;
        List<Integer> props=new ArrayList<>();
        for (CfgHolidayLotteryAwards item : lotteryAwards) {
            if (item.getAccTotal()!=null && item.getAccTotal().contains(times)){
                lotteryAward=item;
                break;
            }
            props.add(item.getProp());
        }
        if (lotteryAward==null){
            int index = PowerRandom.hitProbabilityIndex(props);
            lotteryAward=lotteryAwards.get(index);
        }
        rd.getDrawResults().add(lotteryAward.getId());
        return buildAwardsByLotteryAward(gid,lotteryAward);
    }

    /**
     * 解析奖励
     * @param lotteryAward
     * @return
     */
    private List<Award> buildAwardsByLotteryAward(Integer gid,CfgHolidayLotteryAwards lotteryAward){
        if (ListUtil.isNotEmpty(lotteryAward.getAwards())){
            valueUtil.increment(getGameHolidayTotalGoldKey(gid),ONCE_NEED_GOLD);
            return lotteryAward.getAwards();
        }
        double percentage=0.0;
        switch (lotteryAward.getId()){
            //50%奖池
            case 4001:percentage=0.5;break;
            //20%奖池
            case 4002:percentage=0.2;break;
            //10%奖池
            case 4003:percentage=0.1;break;
            //5%奖池
            case 4004:percentage=0.05;break;
            default:return new ArrayList<>();
        }
        int gainGold=0;
        String key=getGameHolidayTotalGoldKey(gid);
        synchronized (gid){
            Integer total = valueUtil.get(key);
            if (total == null || total <= LEAST_GOLD) {
                valueUtil.set(key, INIT_GOLD);
                gainGold = getProduct(INIT_GOLD, percentage);
            } else {
                gainGold = getProduct(total, percentage);
                total -= gainGold;
                total = total <= LEAST_GOLD ? INIT_GOLD : total + ONCE_NEED_GOLD;
                valueUtil.set(key, total);
            }
        }
        return Arrays.asList(new Award(AwardEnum.YB,gainGold));
    }

    private int getProduct(int num1,double percentage){
        Double result=num1*percentage;
        return result.intValue();
    }
}
