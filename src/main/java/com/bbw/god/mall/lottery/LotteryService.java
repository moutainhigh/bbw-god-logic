package com.bbw.god.mall.lottery;

import com.bbw.common.*;
import com.bbw.common.lock.RedisLockUtil;
import com.bbw.god.db.entity.CfgServerEntity;
import com.bbw.god.event.BaseEventParam;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardTool;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.server.ServerTool;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.game.data.GameData;
import com.bbw.god.game.data.GameDataService;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.achievement.AchievementServiceFactory;
import com.bbw.god.gameuser.achievement.BaseAchievementService;
import com.bbw.god.gameuser.achievement.UserAchievementInfo;
import com.bbw.god.gameuser.mail.UserMail;
import com.bbw.god.gameuser.treasure.TreasureChecker;
import com.bbw.god.gameuser.treasure.UserTreasureService;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.mall.lottery.event.LotteryEventPublisher;
import com.bbw.god.rd.RDCommon;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.bbw.god.game.data.redis.RedisKeyConst.SPLIT;

/**
 * @author suchaobin
 * @description 奖券service
 * @date 2020/7/6 10:36
 **/
@Service
@Slf4j
public class LotteryService {
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private GameDataService gameDataService;
    @Autowired
    private UserTreasureService userTreasureService;
    @Autowired
    private RedisLockUtil redisLockUtil;
    @Autowired
    private AchievementServiceFactory achievementServiceFactory;
    @Autowired
    private final static int LOTTERT_NUMBER_BOTTOM_LIMIT = 1;
    private final static int LOTTERT_NUMBER_TOP_LIMIT = 36;

    /**
     * 进入奖券界面
     *
     * @param uid
     * @return
     */
    public RDLotteryInfo enterLottery(long uid) {
        // 获取开奖时间
        Date todayBegin = DateUtil.getDateBegin(DateUtil.now());
        Date date = DateUtil.addHours(todayBegin, 22);
        long remainTime = date.getTime() - DateUtil.now().getTime();
        if (remainTime < 0) {
            remainTime = DateUtil.addHours(date, 24).getTime() - DateUtil.now().getTime();
        }
        int sid = gameUserService.getActiveSid(uid);
        int group = ServerTool.getServerGroup(sid);
        GameLottery gameLottery = getCurGameLottery(group);
        List<Integer> boughtNumbers = gameLottery.gainBoughtNumbers();
        List<Integer> myNumbers = gameLottery.gainMyNumbers(uid);
        boughtNumbers.removeAll(myNumbers);
        return new RDLotteryInfo(remainTime, boughtNumbers, myNumbers);
    }

    /**
     * 获取当前的平台奖券对象信息
     *
     * @return
     */
    public GameLottery getCurGameLottery(int group) {
        GameLottery gameLottery = gameDataService.getGameDatas(GameLottery.class).stream()
                .filter(l -> l.isValid() && l.getGroupId() == group).findFirst().orElse(null);
        // 加锁并且双重检查，保证多线程情况下只会生成一条对应实例
        if (null == gameLottery) {
            gameLottery = (GameLottery) redisLockUtil.doSafe(getLockInstanceKey(group), obj -> {
                // 业务操作
                GameLottery lottery = gameDataService.getGameDatas(GameLottery.class).stream().filter(l ->
                        l.isValid() && l.getGroupId().equals(group)).findFirst().orElse(null);
                if (null == lottery) {
                    lottery = GameLottery.getInstance(group);
                    gameDataService.addGameData(lottery);
                }
                return lottery;
            });
        }
        return gameLottery;
    }

    /**
     * 下注（包括锁和业务逻辑）
     *
     * @param uid
     * @param numbers
     * @return
     */
    public Rst bet(long uid, String numbers) {
        // 先进行检验，不合格直接返回，不占用资源尝试获取锁
        List<Integer> lotteryNumbers = ListUtil.parseStrToInts(numbers);
        if (ListUtil.isEmpty(lotteryNumbers)) {
            return Rst.businessFAIL("传入的号码为空！");
        }
        List<Integer> violationNumber = lotteryNumbers.stream()
                .filter(tmp -> tmp < LOTTERT_NUMBER_BOTTOM_LIMIT || tmp > LOTTERT_NUMBER_TOP_LIMIT)
                .collect(Collectors.toList());
        if (ListUtil.isNotEmpty(violationNumber)) {
            return Rst.businessFAIL("不在规则内的号码！");
        }
        //消耗道具检查
        TreasureChecker.checkIsEnough(TreasureEnum.LOTTERY_TICKET.getValue(), lotteryNumbers.size(), uid);

        int sid = gameUserService.getActiveSid(uid);
        int group = ServerTool.getServerGroup(sid);

        GameLottery gameLottery = getCurGameLottery(group);
        if (gameLottery.isDrawing()) {
            return Rst.businessFAIL("奖券正在开奖中，无法购买！");
        }

        return (Rst) redisLockUtil.doSafe(getLockBetKey(group),
                obj -> betNumber(uid, lotteryNumbers));
    }

    /**
     * 下注（针对业务逻辑）
     *
     * @param uid
     * @param lotteryNumbers
     */
    private Rst betNumber(long uid, List<Integer> lotteryNumbers) {
        int sid = gameUserService.getActiveSid(uid);
        int group = ServerTool.getServerGroup(sid);
        GameLottery gameLottery = getCurGameLottery(group);
        if (!gameLottery.bet(uid, lotteryNumbers)) {
            return Rst.businessFAIL("所选择号码已被其他玩家购买，请重新选择！");
        }
        gameDataService.updateGameData(gameLottery);
        // 扣除资源
        TreasureEventPublisher.pubTDeductEvent(uid, TreasureEnum.LOTTERY_TICKET.getValue(), lotteryNumbers.size(),
                WayEnum.LOTTERY_BET, new RDCommon());
        // 判断是否需要开奖（36个数字都被购买后立即开奖）
        if (36 == gameLottery.getBetMap().size()) {
            // 发送奖励发放事件
            LotteryEventPublisher.pubLotteryAwardSendEvent(group, new BaseEventParam());
            return Rst.businessOK("本期奖券结束，将在一分钟后开奖！");
        }
        return Rst.businessOK();
    }

    /**
     * 发送奖励
     */
    public void sendAward(int group) {
        try {
            GameLottery gameLottery = getCurGameLottery(group);
            List<Long> uids = gameLottery.gainBetUids();
            // 获取开奖结果
            LotteryResult result = gameLottery.getResult();
            // 获取一等奖和二等奖的昵称
            String firstNickname = getNicknames(gameLottery, result, LotteryLevel.FIRST.getLevel()).get(0);
            String secondNickname = getNicknames(gameLottery, result, LotteryLevel.SECOND.getLevel()).get(0);
            // 发放奖励
            for (Long uid : uids) {
                List<Award> awards = getMyAwards(uid, gameLottery, result);
                sendMailAward(uid, firstNickname, secondNickname, result, awards);
            }
            // 修改当前奖券状态
            gameLottery.setValid(false);
            gameDataService.updateGameData(gameLottery);
            // 成就处理
            dealLotteryAchievement(gameLottery, result);
            // 清除过期数据
            cleanGameLottery();
            log.info("奖券id={},平台id={}的奖券奖励发放完成", gameLottery.getId(), group);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 处理成就
     *
     * @param gameLottery
     * @param result
     */
    private void dealLotteryAchievement(GameLottery gameLottery, LotteryResult result) {
        Integer firstPrizeNumber = result.getFirstPrizeNumbers().get(0);
        Map<String, String> betMap = gameLottery.getBetMap();
        String uid = betMap.get(String.valueOf(firstPrizeNumber));
        if (StrUtil.isEmpty(uid)){
            return;
        }
        Collection<String> values = betMap.values();
        if (values==null || values.isEmpty()){
            return;
        }
        // 中一等奖的玩家下注次数
        long count = values.stream().filter(uid::equals).count();
        if (2 == count || 35 == count) {
            BaseAchievementService service14940 = achievementServiceFactory.getById(14940);
            UserAchievementInfo info = gameUserService.getSingleItem(Long.parseLong(uid), UserAchievementInfo.class);
            service14940.achieve(Long.parseLong(uid), 1, info, new RDCommon());
        }
    }

    /**
     * 获取昵称集合
     *
     * @param gameLottery
     * @param result
     * @param level
     * @return
     */
    private List<String> getNicknames(GameLottery gameLottery, LotteryResult result, int level) {
        List<Integer> numbers = new ArrayList<>();
        LotteryLevel lotteryLevel = LotteryLevel.fromValue(level);
        switch (lotteryLevel) {
            case FIRST:
                numbers = result.getFirstPrizeNumbers();
                break;
            case SECOND:
                numbers = result.getSecondPrizeNumbers();
                break;
            case THIRD:
                numbers = result.getThirdPrizeNumbers();
                break;
            case FOURTH:
                numbers = result.getFourthPrizeNumbers();
                break;
            case FIFTH:
                numbers = result.getFifthPrizeNumbers();
                break;
            case PARTICIPATE:
                numbers = result.getParticipatePrizeNumbers();
                break;
            default:
                break;
        }
        List<String> nicknameList = new ArrayList<>();
        for (Integer number : numbers) {
            String uidStr = gameLottery.getBetMap().get(String.valueOf(number));
            Long uid = uidStr == null ? null : Long.parseLong(uidStr);
            String nickname = "空缺";
            if (null != uid) {
                int sid = gameUserService.getActiveSid(uid);
                nickname = ServerTool.getServerShortName(sid) + " "
                        + gameUserService.getGameUser(uid).getRoleInfo().getNickname();
            }
            nicknameList.add(nickname);
        }
        return nicknameList;
    }

    /**
     * 获取本期奖券开奖时，我应该获得所有奖励
     *
     * @param uid
     * @param gameLottery
     * @param result
     * @return
     */
    private List<Award> getMyAwards(long uid, GameLottery gameLottery, LotteryResult result) {
        List<Award> awardList = new ArrayList<>();
        List<Integer> myNumbers = gameLottery.gainMyNumbers(uid);
        for (Integer number : myNumbers) {
            Integer level = result.getAwardLevel(number).getLevel();
            CfgLotteryAward cfgLotteryAward = LotteryTool.getLotteryAwardByLevel(level);
            List<Award> awards = cfgLotteryAward.getAwards();
            AwardTool.addOrUpdateNumById(awardList, awards);
        }
        return awardList;
    }

    /**
     * 发送邮件奖励
     *
     * @param first
     * @param second
     * @param result
     */
    private void sendMailAward(long uid, String first, String second, LotteryResult result, List<Award> awards) {
        try {
            List<UserMail> mailList = new ArrayList<>();
            int sid = gameUserService.getActiveSid(uid);
            int group = ServerTool.getServerGroup(sid);
            GameLottery gameLottery = getCurGameLottery(group);
            String title = LM.I.getMsgByUid(uid,"mail.lottery.results.title");
            List<Integer> myNumbers = gameLottery.gainMyNumbers(uid);
            String msg = getMailMsg(first, second, result, myNumbers,uid);
            UserMail userMail = UserMail.newAwardMail(title, msg, uid, awards);
            mailList.add(userMail);
            gameUserService.updateItems(mailList);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 邮件内容
     *
     * @param firstName
     * @param secondName
     * @param result
     * @param myNumbers
     * @return
     */
    private String getMailMsg(String firstName, String secondName, LotteryResult result, List<Integer> myNumbers,long uid) {
        String msg = LM.I.getMsgByUid(uid,"mail.lottery.first.results",result.getFirstPrizeNumbers().toString(),firstName);
        msg += LM.I.getMsgByUid(uid,"mail.lottery.second.results",result.getSecondPrizeNumbers().toString(),secondName);
        msg += LM.I.getMsgByUid(uid,"mail.lottery.third.results",result.getThirdPrizeNumbers().toString(),"");
        msg += LM.I.getMsgByUid(uid,"mail.lottery.fourth.results",result.getFourthPrizeNumbers().toString(),"");
        msg += LM.I.getMsgByUid(uid,"mail.lottery.fifth.results",result.getFifthPrizeNumbers().toString(),"");
        msg += LM.I.getMsgByUid(uid,"mail.lottery.results.num",myNumbers.toString(),"");
        return msg;
    }

    /**
     * 获取用来当做下注的redis锁的key
     *
     * @param number
     * @return
     */
    private String getLockBetKey(int group) {
        return "game" + SPLIT + "lottery" + SPLIT + "lock" + SPLIT + group + SPLIT + "xyjq";
    }

    /**
     * 获取用来当做初始化gameLottery的redis锁的key
     *
     * @param group
     * @return
     */
    private String getLockInstanceKey(int group) {
        return "game" + SPLIT + "lottery" + SPLIT + "lock" + SPLIT + group;
    }

    /**
     * 清除过期数据
     */
    private void cleanGameLottery() {
        List<GameLottery> invalidList = gameDataService.getGameDatas(GameLottery.class).stream()
                .filter(gl -> !gl.isValid() && DateUtil.getDaysBetween(gl.getBeginTime(), DateUtil.now()) >= 7)
                .collect(Collectors.toList());
        List<Long> dataIds = invalidList.stream().map(GameData::getId).collect(Collectors.toList());
        if (ListUtil.isNotEmpty(dataIds)) {
            gameDataService.deleteGameDatas(dataIds, GameLottery.class);
        }
    }

    /**
     * 检查奖券奖励发放是否成功。注意，该方法仅可用在服务器启动时！！！
     */
    public void checkSendAward() {
        List<Integer> groupIds = ServerTool.getAvailableServers().stream()
                .map(CfgServerEntity::getGroupId).distinct().collect(Collectors.toList());
        for (Integer group : groupIds) {
            GameLottery gameLottery = getCurGameLottery(group);
            // 启动时有效且在开奖中的话，说明开奖失败了
            if (gameLottery.isDrawing() && gameLottery.isValid()) {
                LotteryEventPublisher.pubLotteryAwardSendEvent(group, new BaseEventParam());
            }
        }
    }
}
