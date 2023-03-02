package com.bbw.god.activity.holiday.lottery.service.bocake;

import com.bbw.common.CloneUtil;
import com.bbw.common.LM;
import com.bbw.common.ListUtil;
import com.bbw.common.PowerRandom;
import com.bbw.common.lock.SyncLockUtil;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.activity.holiday.lottery.*;
import com.bbw.god.activity.holiday.lottery.event.HolidayEventPublisher;
import com.bbw.god.activity.holiday.lottery.rd.*;
import com.bbw.god.activity.holiday.lottery.service.HolidayLotteryService;
import com.bbw.god.db.entity.CfgServerEntity;
import com.bbw.god.event.BaseEventParam;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.game.award.AwardService;
import com.bbw.god.game.award.AwardTool;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.mail.UserMail;
import com.bbw.god.gameuser.treasure.UserTreasureService;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.rd.RDCommon;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.bbw.god.game.data.redis.RedisKeyConst.SPLIT;

/**
 * 博饼类抽奖Service
 *
 * @author: huanghb
 * @date: 2022/1/11 14:08
 */
@Service
@Slf4j
public abstract class HolidayBoCakeService implements HolidayLotteryService {
    @Autowired
    private SyncLockUtil syncLockUtil;
    @Autowired
    private UserTreasureService userTreasureService;
    @Autowired
    private AwardService awardService;
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private HolidayBoCakeRedisService boCakeRedisService;
    /** 缓存超时时间 */
    private static final long CACHE_TIME_OUT = 10 * 24 * 60 * 60;
    /** 状元奖券后四位词库 */
    private static final String THESAURUS = "0123456789QWERTYUIPASDFGHJKLZXCVBNM";


    /**
     * 获取节日抽奖信息
     *
     * @param uid 玩家id
     * @return
     */
    @Override
    public RDCommon getHolidayLotteryInfo(long uid, HolidayLotteryParam param) {
        UserHolidayBoCake holidayBoCake = getCurUserHolidayLottery(uid);
        List<String> zhuangYuanNOList = holidayBoCake.getZhuangYuanNOList();
        if (isResultShowTime()) {
            Integer groupId = gameUserService.getActiveGid(uid);
            WangZhongWang wangZhongWang = boCakeRedisService.getWangZhongWangfromRedis(groupId, getMyId());
            Map<String, Long> zhuangYuanMap = boCakeRedisService.getZhuangYuanMap(groupId, getMyId());
            List<RDDrawResult.ResultInfo> firstInfos = getResultInfos(wangZhongWang.getFirstPrize(), zhuangYuanMap);
            List<RDDrawResult.ResultInfo> secondInfos = getResultInfos(wangZhongWang.getSecondPrize(), zhuangYuanMap);
            List<RDDrawResult.ResultInfo> thirdInfos = getResultInfos(wangZhongWang.getThirdPrize(), zhuangYuanMap);
            List<RDDrawResult.ResultInfo> fourthInfos = getResultInfos(wangZhongWang.getFourthPrize(), zhuangYuanMap);
            List<ZhuangYuanNOInfo> zhuangYuanInfos = ZhuangYuanNOInfo.getMyZhuangYuanInfos(wangZhongWang, zhuangYuanNOList);
            return new RDDrawResult(firstInfos, secondInfos, thirdInfos, fourthInfos, zhuangYuanInfos);
        }
        List<Integer> awardedIds = holidayBoCake.getAwardedIds();
        List<RDHolidayLotteryAwards> myRecords = getMyRecords(awardedIds);
        Integer groupId = gameUserService.getActiveGid(uid);
        List<String> gameRecords = boCakeRedisService.getGameRecords(groupId, getMyId());
        List<Integer> lastResult = holidayBoCake.getLastResult();
        List<ZhuangYuanNOInfo> list = zhuangYuanNOList.stream().map(ZhuangYuanNOInfo::new).collect(Collectors.toList());
        return RDHolidayLotteryInfo.getInstance(lastResult, list, myRecords, gameRecords);
    }

    /**
     * 是否结果展示时间
     *
     * @return
     */
    public abstract boolean isResultShowTime();

    /**
     * 获取中奖信息
     *
     * @param prizeList
     * @param zhuangYuanMap
     * @return
     */
    private List<RDDrawResult.ResultInfo> getResultInfos(List<String> prizeList, Map<String, Long> zhuangYuanMap) {
        List<RDDrawResult.ResultInfo> resultInfos = new ArrayList<>();
        //不存在中奖号码
        if (ListUtil.isEmpty(prizeList)) {
            return resultInfos;
        }
        for (String number : prizeList) {
            Long prizeUid = zhuangYuanMap.get(number);
            if (null == prizeUid) {
                log.error("uid为空，对应的number为{}", number);
                continue;
            }
            CfgServerEntity server = gameUserService.getOriServer(prizeUid);
            if (null == server) {
                log.error("服务器信息为空，对应的uid为{}", prizeUid);
                continue;
            }
            String serverName = server.getShortName();
            String nickname = gameUserService.getGameUser(prizeUid).getRoleInfo().getNickname();
            nickname = serverName + " " + nickname;
            resultInfos.add(new RDDrawResult.ResultInfo(number, nickname));
        }
        return resultInfos;
    }


    /**
     * 获取我的纪录
     *
     * @param cfgIds 已领取的配置id集合
     * @return
     */
    protected List<RDHolidayLotteryAwards> getMyRecords(List<Integer> cfgIds) {
        List<RDHolidayLotteryAwards> myRecords = new ArrayList<>();
        for (Integer cfgId : cfgIds) {
            CfgHolidayLotteryAwards cfg = HolidayLotteryTool.getById(cfgId);
            List<RDHolidayLotteryAward> awardList = cfg.getAwards().stream().map(tmp ->
                    RDHolidayLotteryAward.getInstance(tmp, cfg.getLevel())).collect(Collectors.toList());
            myRecords.add(new RDHolidayLotteryAwards(awardList));
        }
        return myRecords;
    }

    /**
     * 抽奖
     *
     * @param uid 玩家id
     * @return
     */
    @Override
    public RDDrawHolidayLottery draw(long uid, HolidayLotteryParam param) {
        // 检查资源
        checkForDraw(uid, param);
        RDDrawHolidayLottery rd = new RDDrawHolidayLottery();
        // 扣除资源
        TreasureEventPublisher.pubTDeductEvent(uid, getLotteryPropsType().getValue(), 1, WayEnum.HOLIDAY_LOTTERY_DRAW, rd);
        // 获取博饼丢骰子的结果集
        UserHolidayBoCake holidayBoCake = getCurUserHolidayLottery(uid);
        List<Integer> drawResultList = getDrawResultList(holidayBoCake);
        int resultLevel = getResultLevel(drawResultList);
        rd.setResultLevel(resultLevel);
        // 抽到状元
        Award firstZhuangYuanAward = null;
        if (ResultLevelEnum.ZY.getValue() == resultLevel) {
            firstZhuangYuanAward = getFirstZhuangYuanAward(holidayBoCake);
            String zhuangYuanNO = generateZhuangYuanNO(uid);
            holidayBoCake.addZhuangYuanNO(zhuangYuanNO);
            rd.setZhuangYuanNO(zhuangYuanNO);
        }
        // 探花以上
        if (ResultLevelEnum.TH.getValue() >= resultLevel) {
            boCakeRedisService.addGameRecord(uid, resultLevel, getMyId());
        }
        // 发放奖励
        CfgHolidayLotteryAwards cfg = HolidayLotteryTool.getByAwardLevel(getMyId(), resultLevel);
        List<Award> awards = cfg.getAwards();
        List<Award> cloneAward = CloneUtil.cloneList(awards);
        //首次抽中状元奖签时添加奖励
        if (null != firstZhuangYuanAward) {
            cloneAward.add(firstZhuangYuanAward);
        }
        awardService.fetchAward(uid, cloneAward, WayEnum.HOLIDAY_LOTTERY_DRAW, "", rd);
        // 修改数据并保存
        holidayBoCake.setLastResult(drawResultList);
        holidayBoCake.receiveAward(cfg.getId());
        toRedis(uid, holidayBoCake);
        // 发布事件
        BaseEventParam bep = new BaseEventParam(uid, WayEnum.HOLIDAY_LOTTERY_DRAW, rd);
        HolidayEventPublisher.pubHolidayLotteryDrawEvent(bep, HolidayLotteryType.ZQBB, resultLevel);
        // 返回给客户端
        rd.setResultList(drawResultList);
        return rd;
    }

    /**
     * 获得第一次状元额外奖励
     *
     * @param holidayBoCake
     * @return
     */
    protected Award getFirstZhuangYuanAward(UserHolidayBoCake holidayBoCake) {
        return null;
    }

    /**
     * 获得抽奖道具类型
     *
     * @return
     */
    public abstract TreasureEnum getLotteryPropsType();

    /**
     * 获得抽奖结果（结果级别）
     *
     * @param drawResultList
     * @return
     */
    abstract int getResultLevel(List<Integer> drawResultList);

    /**
     * 抽奖检查
     *
     * @param uid 玩家id
     */
    @Override
    public void checkForDraw(long uid, HolidayLotteryParam param) {
        int treasureNum = userTreasureService.getTreasureNum(uid, getLotteryPropsType().getValue());
        if (treasureNum < 1) {
            throw new ExceptionForClientTip("treasure.not.enough", getLotteryPropsType().getName());
        }
    }

    /**
     * 奖励预览
     *
     * @return
     */
    @Override
    public RDCommon previewAwards(HolidayLotteryParam param) {
        RDPreviewAwards rd = new RDPreviewAwards();
        List<Award> awards = new ArrayList<>();
        List<CfgHolidayLotteryAwards> lotteryAwards = HolidayLotteryTool.getAll(getMyId()).stream()
                .sorted(Comparator.comparing(CfgHolidayLotteryAwards::getOrder)).collect(Collectors.toList());
        for (CfgHolidayLotteryAwards lotteryAward : lotteryAwards) {
            awards.addAll(lotteryAward.getAwards());
        }
        rd.setAwards(awards);
        return rd;
    }

    /**
     * 从redis中获取数据并转化成BaseUserHolidayLottery对象
     *
     * @param uid
     * @return
     */
    @Override
    public UserHolidayBoCake fromRedis(long uid) {
        return boCakeRedisService.fromRedis(uid, getMyId());
    }

    /**
     * 将BaseUserHolidayLottery对象保存到redis中
     *
     * @param uid                玩家id
     * @param userHolidayLottery 保存的数据对象
     */
    @Override
    public void toRedis(long uid, BaseUserHolidayLottery userHolidayLottery) {
        boCakeRedisService.toRedis(uid, userHolidayLottery, getMyId(), CACHE_TIME_OUT);
    }

    /**
     * 获得当前用户的奖券
     *
     * @param uid
     * @return
     */
    private UserHolidayBoCake getCurUserHolidayLottery(long uid) {
        UserHolidayBoCake holidayBoCake = fromRedis(uid);
        if (null == holidayBoCake) {
            holidayBoCake = (UserHolidayBoCake) syncLockUtil.doSafe(String.valueOf(uid), tmp -> {
                UserHolidayBoCake userHolidayBoCake = fromRedis(uid);
                if (null == userHolidayBoCake) {
                    userHolidayBoCake = UserHolidayBoCake.getInstance(uid);
                }
                return userHolidayBoCake;
            });
        }
        return holidayBoCake;
    }

    /**
     * 获取抽奖结果集
     *
     * @param holidayBoCake
     * @return
     */
    private List<Integer> getDrawResultList(UserHolidayBoCake holidayBoCake) {
        List<Integer> drawResult = new ArrayList<>();
        //已领取的奖品配置id集合
        List<Integer> awardedIds = holidayBoCake.getAwardedIds();
        //已抽奖次数-从最后一次状元开始算起
        int alreadyDrawTimes = 0;
        if (ListUtil.isNotEmpty(awardedIds)) {
            //返回最后一次状元奖励下标
            int index = awardedIds.lastIndexOf(getMyId() * 100 + ResultLevelEnum.ZY.getValue());
            //没有状元奖励返回0 公式为awardedIds.size() - index-1 -1：awardedIds.size()数据个数需减1转换为最大下标
            alreadyDrawTimes = index >= 0 ? awardedIds.size() - 1 - index : awardedIds.size();
            //获得距地上一次获得状元抽奖次数
        }
        // 如果120次了还没抽出来状元过，则第120次必定是状元
        if (getMinGuaranteeNum() == alreadyDrawTimes) {
            drawResult.addAll(getGuaranteedChampionResult());
            for (int i = 0; i < 2; i++) {
                int random = PowerRandom.getRandomBySeed(6);
                drawResult.add(random);
            }
            return drawResult;
        }
        // 否则全随机抽
        for (int i = 0; i < 6; i++) {
            int random = PowerRandom.getRandomBySeed(6);
            drawResult.add(random);
        }
        return drawResult;
    }

    /**
     * 获得保底次数
     *
     * @return
     */
    private int getMinGuaranteeNum() {
        return 120;
    }

    /**
     * 获得保底状元的结果
     *
     * @return
     */
    abstract List<Integer> getGuaranteedChampionResult();

    /**
     * 生成状元奖券的编号
     *
     * @return
     */
    private String generateZhuangYuanNO(long uid) {
        Integer groupId = gameUserService.getActiveGid(uid);
        Map<String, Long> redisMap = boCakeRedisService.getZhuangYuanMap(groupId, getMyId());
        String number;
        do {
            StringBuilder randomThesaurus = new StringBuilder();
            for (int i = 0; i < 4; i++) {
                // 如果已经有字母了，只添加数字
                boolean containsLetter = randomThesaurus.toString().matches(".*[A-Z]+.*");
                if (containsLetter) {
                    int randomIndex = PowerRandom.getRandomBySeed(10) - 1;
                    char c = THESAURUS.charAt(randomIndex);
                    randomThesaurus.append(c);
                    continue;
                }
                // 最后一次还没有字母，本次必须是字母
                if (3 == i && !containsLetter) {
                    String str = THESAURUS.substring(10);
                    int randomIndex = PowerRandom.getRandomBySeed(str.length()) - 1;
                    char c = str.charAt(randomIndex);
                    randomThesaurus.append(c);
                    continue;
                }
                // 全部随机添加
                int randomIndex = PowerRandom.getRandomBySeed(THESAURUS.length()) - 1;
                char c = THESAURUS.charAt(randomIndex);
                randomThesaurus.append(c);
            }
            number = "No" + SPLIT + "FJ815" + randomThesaurus;
        } while (redisMap.containsKey(number));
        redisMap.put(number, uid);
        boCakeRedisService.cacheZhuangYuanNoInfo(uid, groupId, getMyId(), number, CACHE_TIME_OUT);
        return number;
    }


    /**
     * 王中王抽奖
     *
     * @param groupId
     */
    public void drawWangZhongWang(int groupId) {
        Map<String, Long> zhuangYuanMap = boCakeRedisService.getZhuangYuanMap(groupId, getMyId());
        Map<Long, List<Map.Entry<String, Long>>> result = zhuangYuanMap.entrySet().stream().collect(Collectors.groupingBy(Map.Entry::getValue));
        WangZhongWang wangZhongWang = WangZhongWang.instanceWangZhongWang(zhuangYuanMap);
        boCakeRedisService.setWangZhongWangToRedis(groupId, getMyId(), wangZhongWang);
        List<String> firstPrize = wangZhongWang.getFirstPrize();
        List<String> secondPrize = wangZhongWang.getSecondPrize();
        List<String> thirdPrize = wangZhongWang.getThirdPrize();
        List<String> fourthPrize = wangZhongWang.getFourthPrize();
        Set<Long> uids = result.keySet();
        List<UserMail> mailList = new ArrayList<>();
        for (Long uid : uids) {
            List<Award> awardList = new ArrayList<>();
            List<Map.Entry<String, Long>> entryList = result.get(uid);
            for (Map.Entry<String, Long> entry : entryList) {
                String number = entry.getKey();
                WangZhongWangLevelEnum level = WangZhongWangLevelEnum.getLevel(number, firstPrize, secondPrize, thirdPrize, fourthPrize);
                List<Award> awards = getAwardsByLevel(level);
                AwardTool.addOrUpdateNumById(awardList, awards);
            }
            addUserMail(uid, awardList, mailList);
            // 扣除当前所拥有的全部状元奖券
            int num = userTreasureService.getTreasureNum(uid, TreasureEnum.CHAMPION_TICKET.getValue());
            userTreasureService.delTreasure(uid, TreasureEnum.CHAMPION_TICKET.getValue(), num, WayEnum.HOLIDAY_LOTTERY_DRAW);
        }
        gameUserService.updateItems(mailList);
    }

    /**
     * 根据王中王等级类别获得奖励
     *
     * @param level
     * @return
     */
    private List<Award> getAwardsByLevel(WangZhongWangLevelEnum level) {
        switch (level) {
            case FIRST:
                return Arrays.asList(new Award(TreasureEnum.IMPERIAL_SECRET_SCROLL_BOX.getValue(), AwardEnum.FB, 1));
            case SECOND:
                return Arrays.asList(new Award(TreasureEnum.RANDOM_SECRET_SCROLL.getValue(), AwardEnum.FB, 1));
            case THIRD:
                return Arrays.asList(new Award(TreasureEnum.TongTCJ.getValue(), AwardEnum.FB, 100));
            case FOURTH:
                return Arrays.asList(new Award(TreasureEnum.SNATCH_TREASURE_TICKET.getValue(), AwardEnum.FB, 20));
            default:
                return Arrays.asList(new Award(TreasureEnum.HY.getValue(), AwardEnum.FB, 20));
        }
    }

    /**
     * 添加用户邮件
     *
     * @param uid
     * @param awards
     * @param mailList
     */
    private void addUserMail(long uid, List<Award> awards, List<UserMail> mailList) {
        try {
            String title = LM.I.getMsgByUid(uid, "mail.mid-autumn.festival.lottery.title");
            String msg = LM.I.getMsgByUid(uid, "mail.mid-autumn.festival.lottery.content");
            UserMail userMail = UserMail.newAwardMail(title, msg, uid, awards);
            mailList.add(userMail);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}

