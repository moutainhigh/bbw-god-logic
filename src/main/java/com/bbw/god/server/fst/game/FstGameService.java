package com.bbw.god.server.fst.game;

import com.bbw.common.DateUtil;
import com.bbw.common.LM;
import com.bbw.common.ListUtil;
import com.bbw.common.StrUtil;
import com.bbw.db.redis.RedisHashUtil;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.event.BaseEventParam;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.game.combat.event.CombatEventPublisher;
import com.bbw.god.game.combat.event.EPCombatAchievement;
import com.bbw.god.game.config.CfgFst;
import com.bbw.god.game.config.server.ServerTool;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.game.data.GameDataService;
import com.bbw.god.game.data.GameDataType;
import com.bbw.god.game.data.redis.GameRedisKey;
import com.bbw.god.game.data.redis.RedisKeyConst;
import com.bbw.god.gameuser.card.CardGroup;
import com.bbw.god.gameuser.card.CardGroupWay;
import com.bbw.god.gameuser.card.UserCardGroupService;
import com.bbw.god.gameuser.mail.MailService;
import com.bbw.god.server.fst.*;
import com.bbw.god.server.fst.robot.FstRobotService;
import com.bbw.god.server.fst.server.FstServerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.bbw.god.server.fst.FstTool.REDIS_KEY;

/**
 * 说明：跨服封神台
 *
 * @author lwb
 * date 2021-06-29
 */
@Service
public class FstGameService extends FstService {
    @Autowired
    private FstServerService fstServerService;
    @Autowired
    private GameDataService gameDataService;
    @Autowired
    private MailService mailService;
    @Autowired
    private FstRobotService fstRobotService;
    @Autowired
    private RedisHashUtil<String,String> hashUtil;
    @Autowired
    private UserCardGroupService userCardGroupService;
    //跨服封神台
    private static final FstRankingType[] types={FstRankingType.REN,FstRankingType.HUANG,FstRankingType.XUAN,FstRankingType.DI,FstRankingType.TIAN};
    private static final Integer[] achievements={15490,15500,15510,15520};//结算成就
    private static final String STATE_SETTLE_DATE_TIME="settle_date";
    private static final String STATE_SETTLE="is_settle";
    private static final String STATE_OPEN_RANKING="is_openRanking";
    
    
    @Override
    public FstType getFstType() {
        return FstType.GAME;
    }

    /**
     * 获取榜单所有人
     * @param gid
     * @param rankingType
     * @return
     */
    public List getFstRankers(int gid,FstRankingType rankingType){
        String key=getFstRankingKey(gid,rankingType);
        Set<Long> ranking = rankingList.range(key);
        List<Long> uids = ranking.stream().collect(Collectors.toList());
        return uids;
    }
    /**
     * 封神台榜单key
     * game:fstRanking:gid:fstzset:榜单类型
     * @param gid
     * @return
     */
    private String getFstRankingKey(int gid,FstRankingType rankingType) {
        if (gid==17){
            gid=16;
        }
        return GameRedisKey.getDataTypeKey(GameDataType.GAME_FST,String.valueOf(gid),REDIS_KEY)+ RedisKeyConst.SPLIT+rankingType.getType();
    }
    /**
     * 封神台上次结算的榜单
     * game:fstRanking:gid:fstzset:settle:榜单类型
     * @param gid
     * @param rankingType
     * @return
     */
    private String getFstSettleRankingKey(int gid,FstRankingType rankingType) {
        if (gid==17){
            gid=16;
        }
        return GameRedisKey.getDataTypeKey(GameDataType.GAME_FST,String.valueOf(gid),REDIS_KEY)+ RedisKeyConst.SPLIT+"settle"+RedisKeyConst.SPLIT+rankingType.getType();
    }
    
    /**
     * 封神台相关状态
     * game:fstRanking:gid:fstzset:state
     * @param gid
     * @return
     */
    private String getFstStateKey(int gid){
        if (gid==17){
            gid=16;
        }
        return GameRedisKey.getDataTypeKey(GameDataType.GAME_FST,String.valueOf(gid),REDIS_KEY)+ RedisKeyConst.SPLIT+"state";
    }
    
    /**
     * 获取封神台战斗状态的key
     * @param gid
     * @return
     */
    private String getFstFightStateKey(int gid){
        if (gid==17){
            gid=16;
        }
        return GameRedisKey.getDataTypeKey(GameDataType.GAME_FST,String.valueOf(gid),REDIS_KEY)+ RedisKeyConst.SPLIT+"fightState";
    }
    
    
    @Override
    public boolean isUnlock(long uid) {
        Optional<FstGameRanking> optional = getFstGameRankingOp(uid);
        if (optional.isPresent() && optional.get().getRankingType()>0){
            return true;
        }
        int rank = fstServerService.getFstRank(uid);
        return rank>0 && rank<=FstTool.getCfg().getUnlockRank();
    }
    @Override
    public boolean checkCardGroupState(long uid) {
        //必须配置3套攻击卡组  3套防御卡组；
        CardGroupWay[] attack={CardGroupWay.GAME_FST_ATTACK1,CardGroupWay.GAME_FST_ATTACK2,CardGroupWay.GAME_FST_ATTACK3};
        boolean attackHasLeaderCard=false;
        for (CardGroupWay groupWay : attack) {
            CardGroup cGroup = userCardGroupService.getFierceFightingCards(uid, groupWay);
            if (ListUtil.isEmpty(cGroup.getCardIds())) {
                return false;
            }
            if (cGroup.hasLeaderCard()) {
                attackHasLeaderCard = true;
            }
        }
        boolean defenseHasLeaderCard=false;
        CardGroupWay[] defense={CardGroupWay.GAME_FST_DEFENSE1,CardGroupWay.GAME_FST_DEFENSE2,CardGroupWay.GAME_FST_DEFENSE3};
        for (CardGroupWay groupWay : defense) {
            CardGroup cGroup = userCardGroupService.getFierceFightingCards(uid, groupWay);
            if (ListUtil.isEmpty(cGroup.getCardIds())) {
                return false;
            }
            if (cGroup.hasLeaderCard()) {
                defenseHasLeaderCard = true;
            }
        }
        //玄榜以上还需要具备分身卡
        Optional<FstGameRanking> op = getFstGameRankingOp(uid);
        if (op.isPresent()){
            if (op.get().getRankingType()>= FstRankingType.XUAN.getType()) {
                return attackHasLeaderCard & defenseHasLeaderCard;
            }
        }
        return true;
    }
    
    /**
     * 是否需要装备分身卡
     * @param uid
     * @return
     */
    private boolean needAddLeaderCard(long uid){
        //玄榜以上还需要具备分身卡
        Optional<FstGameRanking> op = getFstGameRankingOp(uid);
        if (!op.isPresent() || op.get().getRankingType() < FstRankingType.XUAN.getType()){
            return false;
        }
        //必须配置3套攻击卡组  3套防御卡组；
        CardGroupWay[] attack={CardGroupWay.GAME_FST_ATTACK1,CardGroupWay.GAME_FST_ATTACK2,CardGroupWay.GAME_FST_ATTACK3};
        boolean attackHasLeaderCard=false;
        for (CardGroupWay groupWay : attack) {
            CardGroup cGroup = userCardGroupService.getFierceFightingCards(uid, groupWay);
            if (ListUtil.isEmpty(cGroup.getCardIds())) {
                continue;
            }
            if (cGroup.hasLeaderCard()) {
                attackHasLeaderCard = true;
            }
        }
        boolean defenseHasLeaderCard=false;
        CardGroupWay[] defense={CardGroupWay.GAME_FST_DEFENSE1,CardGroupWay.GAME_FST_DEFENSE2,CardGroupWay.GAME_FST_DEFENSE3};
        for (CardGroupWay groupWay : defense) {
            CardGroup cGroup = userCardGroupService.getFierceFightingCards(uid, groupWay);
            if (ListUtil.isEmpty(cGroup.getCardIds())) {
                continue;
            }
            if (cGroup.hasLeaderCard()) {
                defenseHasLeaderCard = true;
            }
        }
        return !(defenseHasLeaderCard && attackHasLeaderCard);
    }

    @Override
    public RDFst intoFst(long uid) {
        RDFst rd = RDFst.getIntoFst();
        // 获取封神台 实际排名
        rd.setRankingType(FstRankingType.REN.getType());
        rd.setIsPromotion(0);
        Optional<FstGameRanking> optional = getFstGameRankingOp(uid);
        rd.setIsPromotionRank(0);
        CfgFst.GameFstPromotion promotion = FstTool.getGameFstPromotion(FstRankingType.REN);
        List<FstFightMsg> msgs=new ArrayList<>();
        int gid=gameUserService.getActiveGid(uid);
        String dateTime = hashUtil.getField(getFstStateKey(gid), STATE_SETTLE_DATE_TIME);
        Date preSettleDate=DateUtil.fromDateTimeString(dateTime);
        if (optional.isPresent() && optional.get().getRankingType()>0){
            FstGameRanking myFstRanking = optional.get();
            //当前榜单可获得的值
            FstRankingType fstRankingType=FstRankingType.fromVal(myFstRanking.getRankingType());
            rd.setRankingType(fstRankingType.getType());
            promotion = FstTool.getGameFstPromotion(fstRankingType);
            int fstRank = getFstRank(uid, fstRankingType,true);
            rd.setMyRank(fstRank);
            rd.setMyAblePoint(getPointByRank(fstRank,fstRankingType));
            //可用挑战次数
            rd.setPvpTimes(FstTool.getCfg().getFreeTimes()-myFstRanking.getTodayFightTimes());
            if (myFstRanking.getRankingType()>0){
                rd.setRankingType(FstRankingType.fromVal(myFstRanking.getRankingType()).getType());
            }
            if (fstRank>0){
                rd.setIsPromotionRank(getPromotionRankState(fstRank,promotion));
            }
            if (ListUtil.isNotEmpty(myFstRanking.getVideoLogs())){
                for (int i = myFstRanking.getVideoLogs().size()-1; i >=0; i--) {
                    FstVideoLog log = myFstRanking.getVideoLogs().get(i);
                    msgs.add(FstFightMsg.getInstance(log,getRankingUserNickName(log.getOppo())));
                }
            }
            if (DateUtil.millisecondsInterval(myFstRanking.getShowSettleTip(),preSettleDate)<0){
                myFstRanking.setShowSettleTip(new Date());
                String field = hashUtil.getField(getFstStateKey(gid), STATE_OPEN_RANKING);
                if (StrUtil.isNotBlank(field) && field.equals("1")){
                    rd.setShowPopType(FstPopType.OPEN_RANKING.getType());
                }else {
                    rd.setShowPopType(FstPopType.SETTLE_RANKING.getType());
                }
                gameDataService.updateGameData(myFstRanking);
            }
            if (myFstRanking.getRankingType()> myFstRanking.getPreRankingType()){
                rd.setIsPromotion(1);
            }else if (myFstRanking.getRankingType()< myFstRanking.getPreRankingType()){
                rd.setIsPromotion(-1);
            }
        }
        //获取榜单
        rd.setRanks(getRankList(rd.getMyRank(),uid));
        for (FstRankerParam param : rd.getRanks()) {
            param.setAblePoints(getPointByRank(param.getPvpRanking(),FstRankingType.fromVal(rd.getRankingType())));
            param.setIsPromotionRank(getPromotionRankState(param.getPvpRanking(),promotion));
        }
        rd.setPreRanks(getRangeRankList(1,3, uid));
        rd.setFightMsgs(msgs);
        rd.setRemainTime(Math.max(0,FstTool.getNextSettleTime(DateUtil.toDateInt(preSettleDate))));
        rd.setNeedUserLeaderCard(needAddLeaderCard(uid)?1:0);
        return rd;
    }

    public Optional<FstGameRanking> getFstGameRankingOp(long uid) {
        FstGameRanking gameData = gameDataService.getGameData(uid, FstGameRanking.class);
        if (gameData!=null){
            if (gameData.getLastUpdateDate()!=DateUtil.getTodayInt()){
                gameData.resetToDayData();
            }
            int size = gameData.getVideoLogs().size();
            if (size>50){
                List<FstVideoLog> logs = gameData.getVideoLogs().subList(size - 50, size);
                gameData.setVideoLogs(logs);
            }
            gameDataService.updateGameData(gameData);
        }
        return Optional.ofNullable(gameData);
    }

    public FstGameRanking getOrCreateFstGameRanking(long uid) {
        Optional<FstGameRanking> optional = getFstGameRankingOp(uid);
        if (optional.isPresent()){
            return optional.get();
        }
        FstGameRanking fstGameRanking=FstGameRanking.getInstance(uid);
        gameDataService.addGameData(fstGameRanking);
        return fstGameRanking;
    }

    @Override
    public int getFstRankWithIntoRanking(Long uid) {
        return 0;
    }

    @Override
    public int getFstRank(Long uid) {
        Optional<FstGameRanking> op = getFstGameRankingOp(uid);
        if (op.isPresent()){
            FstGameRanking ranking=op.get();
            return getFstRank(ranking.getId(),FstRankingType.fromVal(ranking.getRankingType()),true);
        }
        return -1;
    }

    public int getFstRank(Long uid,FstRankingType type,boolean now) {
        if (type.getType()<=0){
            return -1;
        }
        int gid= gameUserService.getActiveGid(uid);
        String key=now?getFstRankingKey(gid, type):getFstSettleRankingKey(gid,type);
        Long rank = rankingList.rank(key, uid);
        if (rank==null){
            return -1;
        }
        if (FstRankingType.REN.equals(type) && rank>=120){
            return -1;
        }
        return rank.intValue()+1;
    }

    @Override
    public List<FstRankerParam> getRankList(int myRank, long uid) {
        if (myRank==-1){
            myRank=121;
        }
        CfgFst config = FstTool.getCfg();
        Integer showCount = config.getShowCount();
        int begin=1;
        int end=showCount+1;
        if (myRank>showCount){
            begin=myRank-showCount;
            end=myRank-1;
        }
        List<FstRankerParam> rd=new ArrayList<>(6);
        List<FstRankerParam> list = getRangeRankList(begin, end, uid);
        for (FstRankerParam param : list) {
            if (param.getId()==uid){
                continue;
            }
            param.setFightAble(1);
            rd.add(param);
        }
        return rd;
    }

    @Override
    public List<FstRankerParam> getRangeRankList(int begin, int end, Long uid) {
        Optional<FstGameRanking> op = getFstGameRankingOp(uid);
        FstRankingType rankType=FstRankingType.REN;
        if (op.isPresent()){
            rankType=FstRankingType.fromVal(op.get().getRankingType());
        }
        String fstKey = getFstRankingKey(gameUserService.getActiveGid(uid), rankType);
        Set<Long> rangeRank = rankingList.range(fstKey, begin-1, end-1);
        int rank=begin;
        List<FstRankerParam> params=new ArrayList<>();
        for (Long id : rangeRank) {
            params.add(getFstRankerParam(id,rank,false,rankType));
            rank++;
        }
        return params;
    }

    @Override
    public int getPointByRank(int rank, FstRankingType type) {
        List<CfgFst.RankingAward> awards = FstTool.getGameRankingAwards(type);
        for (CfgFst.RankingAward award : awards) {
            if (award.getMin()<=rank && rank<=award.getMax()){
                return award.getNum();
            }
        }
        if (type.equals(FstRankingType.REN)){
            return awards.get(awards.size()-1).getNum();
        }
        return 0;
    }

    @Override
    public int getRemainChallengeNum(long guId) {
        return 0;
    }

    /**
     * 显示榜单
     * @param isPreRanking 是否显示的是之前的榜单  false表示实时的
     * @return
     */
    public RDFst ranking(long uid,boolean isPreRanking,FstRankingType rankingType){
        RDFst rd=new RDFst();
        List<FstRankerParam> list=new ArrayList<>();
        rd.setRanks(list);
        rankingType=rankingType==null?FstRankingType.TIAN:rankingType;
        int gid=gameUserService.getActiveGid(uid);
        String key=isPreRanking?getFstSettleRankingKey(gid,rankingType):getFstRankingKey(gid,rankingType);
        CfgFst.GameFstPromotion promotion = FstTool.getGameFstPromotion(rankingType);
        Set<Long> uidSet = rankingList.range(key,0,promotion.getSettle()-1);
        int rank=1;
        for (Long id : uidSet) {
            FstRankerParam param = FstRankerParam.getInstance(id, rank++, getRankingUserNickName(id));
            if (id>0){
                param.setNickname(ServerTool.getServerShortName(gameUserService.getActiveSid(id))+"·"+param.getNickname());
            }
            param.setIsPromotionRank(getPromotionRankState(param.getPvpRanking(),promotion));
            list.add(param);
        }
        FstGameRanking my = getOrCreateFstGameRanking(uid);
        if (isPreRanking){
            rd.setRankingType(my.getPreRankingType());
            rd.setMyRank(my.getPreRank());
        }else {
            rd.setRankingType(my.getRankingType());
            rd.setMyRank(getFstRank(uid));
        }
        rd.setIsPromotionRank(getPromotionRankState(rd.getMyRank(),promotion));
        return rd;
    }


    public boolean addToRanking(int gid, FstRankingType type, double[] ranks,Long[] ids){
        String fstKey = getFstRankingKey(gid, type);
        return rankingList.add(fstKey,ranks,ids);
    }

    public boolean addToRanking(int gid, FstRankingType type,int rank,Long id){
        String fstKey = getFstRankingKey(gid, type);
        return rankingList.add(fstKey,id,rank);
    }
    /**
     * 玩家加入到跨服封神台
     * @param uid
     * @return
     */
    public boolean joinToGameFst(long uid){
        FstGameRanking ranking = getOrCreateFstGameRanking(uid);
        if (ranking.getRankingType()>0){
            //已加入
            return true;
        }
        int gid= gameUserService.getActiveGid(uid);
        if (!ranking.getRankingType().equals(FstRankingType.REN.getType())){
            removeAll(gid, FstRankingType.fromVal(ranking.getRankingType()),uid);
            ranking.setRankingType(FstRankingType.REN.getType());
        }
        ranking.setShowPop(FstPopType.JOIN_TO_GAME_FST.getType());
        gameDataService.updateGameData(ranking);
        return addToRanking(gid,FstRankingType.REN,121,uid);
    }

    /**
     * 从对应的榜单中移除
     * @param gid
     * @param rankingType
     * @param uid
     */
    public void removeAll(int gid,FstRankingType rankingType,Long ...uid){
        rankingList.remove(getFstRankingKey(gid,rankingType),uid);
    }
    
    /**
     * 进行晋级结算
     * 榜单	    天榜	地榜	玄榜	黄榜	人榜
     * 晋级区		10	20	30	50
     * 保级区	10	20	30	40
     * 降级区	10	20	30	50
     * 总人数	20	50	80	120	不限人数
     * @param gid
     */
    public String doPromotion(int gid){
        String stateKey = getFstStateKey(gid);
        String dateStr = hashUtil.getField(stateKey, STATE_SETTLE_DATE_TIME);
        if (StrUtil.isBlank(dateStr)){
            return "结算异常，该平台可能没有初始化数据";
        }
        if (FstTool.getNextSettleTime(DateUtil.toDateInt(DateUtil.fromDateTimeString(dateStr)))>0) {
            return "今日不是结算日";
        }
        hashUtil.putField(stateKey,STATE_SETTLE,"1");
        //结算的玩家数量
        int count=0;
        //结算奖励
        for (int i = 0; i < types.length; i++) {
            FstRankingType rankingType=types[i];
            String key=getFstRankingKey(gid,rankingType);
            CfgFst.GameFstPromotion promotion = FstTool.getGameFstPromotion(rankingType);
            if (rankingList.size(key)>0) {
                //说明该榜单已经开启了,则结算
                count+= doSendAward(rankingType, gid, promotion.getSettle());
            }else {
                //该榜单未开启，需要检测是否开启榜单
                if (count>=promotion.getUnlock()){
                    openRanking(gid,i);
                    //含有新榜单的开启 无需进行名次变动，因为一次只会开启一个榜单 所以直接return即可
                    hashUtil.putField(stateKey,STATE_SETTLE,"0");
                    hashUtil.putField(stateKey,STATE_OPEN_RANKING,"1");
                    hashUtil.putField(stateKey,STATE_SETTLE_DATE_TIME,DateUtil.toDateTimeString(DateUtil.now()));
                    checkAchievement(gid);
                    return "结算正常-新榜单开启";
                }
                break;
            }
        }
        // 榜单变动
        rankingPromotion(gid,0);
        hashUtil.putField(stateKey,STATE_SETTLE_DATE_TIME,DateUtil.toDateTimeString(DateUtil.now()));
        hashUtil.putField(stateKey,STATE_SETTLE,"0");
        hashUtil.putField(stateKey,STATE_OPEN_RANKING,"0");
        checkAchievement(gid);
        return "结算正常-榜单变动";
    }
    
    /**
     * 检查加入榜单成就
     * @param gid
     */
   private void checkAchievement(int gid){
       //成就，结算时，获得跨服封神台天榜第一
       //进入跨服封神台黄榜，进入跨服封神台玄榜，进入跨服封神台地榜，进入跨服封神台天榜
       for (int i = 1; i < types.length; i++) {
           FstRankingType type=types[i];
           Set<Long> range = rankingList.range(getFstRankingKey(gid,type));
           if (range.isEmpty()){
               return;
           }
           for (Long uid : range) {
               if (uid>0){
                   EPCombatAchievement ep = EPCombatAchievement.instance(new BaseEventParam(uid), achievements[i-1]);
                   CombatEventPublisher.pubCombatAchievement(ep);
               }
           }
           if (type.equals(FstRankingType.TIAN)){
               //结算时，获得跨服封神台天榜第一
               Long next = range.iterator().next();
               if (next>0){
                   EPCombatAchievement ep = EPCombatAchievement.instance(new BaseEventParam(next), 15530);
                   CombatEventPublisher.pubCombatAchievement(ep);
               }
           }
       }
   }
    
    /**
     * 奖励结算
     * @param rankingType
     * @param gid
     * @param end
     * @return
     */
    private Integer doSendAward(FstRankingType rankingType, int gid, int end){
        Date settleEndDate = FstTool.getSettleEndDateTime(DateUtil.getTodayInt());
        String key=getFstRankingKey(gid,rankingType);
        String settleRankingKey=getFstSettleRankingKey(gid,rankingType);
        // 先清空旧的
        rankingList.remove(settleRankingKey);
        Set<Long> ranking = rankingList.range(key);
        List<Long> list = ranking.stream().collect(Collectors.toList());
        long uid=0L;
        int validNum=0;
        int settleIntervalSeconds = FstTool.getCfg().getGameFstSettleIntervalDay() * 24 * 3600;
        for (int i = 0; i < end; i++) {
            uid=list.get(i);
            if (uid>0){
                validNum++;
                FstGameRanking gameRanking = getOrCreateFstGameRanking(uid);
                String title= LM.I.getMsgByUid(uid,"mail.fst.game.results.title");
                String content=LM.I.getMsgByUid(uid,"mail.fst.game.results.win.content",rankingType.getMemo(),i+1);
                //判断是否在本轮战斗过
                if(DateUtil.getSecondsBetween (gameRanking.getLastChallengeDate(), settleEndDate) <= settleIntervalSeconds){
                    Award award=Award.instance(TreasureEnum.FST_MEDAL.getValue(), AwardEnum.FB,FstTool.getGameRankingAward(rankingType,i+1));
                    mailService.sendAwardMail(title,content,uid,Arrays.asList(award));
                }
                gameRanking.setPreRankingType(rankingType.getType());
                gameRanking.setPreRank(i+1);
                gameDataService.updateGameData(gameRanking);
            }
            rankingList.add(settleRankingKey,uid,i+1);
        }
        if (rankingType.equals(FstRankingType.REN) && list.size()>end){
            Award award=Award.instance(TreasureEnum.FST_MEDAL.getValue(), AwardEnum.FB,FstTool.getGameRankingAward(rankingType,121));
            List<Award> awards = Arrays.asList(award);
            for (int i = end; i < list.size(); i++) {
                uid=list.get(i);
                if (uid>0){
                    FstGameRanking gameRanking = getOrCreateFstGameRanking(uid);
                    //判断是否在本轮战斗过
                    String title= LM.I.getMsgByUid(uid,"mail.fst.game.results.title");
                    String content=LM.I.getMsgByUid(uid,"mail.fst.game.results.lose.content");
                    if(DateUtil.getSecondsBetween(gameRanking.getLastChallengeDate(), settleEndDate) <= settleIntervalSeconds) {
                        mailService.sendAwardMail(title, content, uid, awards);
                    }
                    gameRanking.setPreRankingType(rankingType.getType());
                    gameRanking.setPreRank(-1);
                    gameDataService.updateGameData(gameRanking);
                }
                rankingList.add(settleRankingKey,uid,i+1);
            }
        }
        return validNum;
    }
    
    /**
     * 开启榜单
     * @param gid
     * @param index  当前类型下标
     */
    private void openRanking(int gid,int index){
        FstRankingType cur=types[index];
        String curRankingKey=getFstRankingKey(gid,cur);
        CfgFst.GameFstPromotion promotion = FstTool.getGameFstPromotion(cur);
        if (cur.equals(FstRankingType.REN)){
            //如果是人榜，则将120名以后的机器人补充到前面
            //先获取 120名内有效的ID
            Set<Long> range = rankingList.rangeByScore(curRankingKey, 1, 120);
            //从第一个编号开始遍历机器人，因为机器人默认编号是1~120，不再有效列表的则补充到有效列表中即可
            int need=120-range.size();
            for (int i = 1; i <= 120 ; i++) {
                if (need<=0){
                    break;
                }
                Long robotId = FstRobotService.getGameRobotId(i);
                if (range.contains(robotId)){
                    continue;
                }
                rankingList.add(curRankingKey,robotId,range.size()+i);
                need--;
            }
            return;
        }
        Long curCount=rankingList.size(curRankingKey);
        //将前一个榜单的人数拿出
        CfgFst.GameFstPromotion prePromotion = FstTool.getGameFstPromotion(types[index-1]);
        String preRankingKey=getFstRankingKey(gid,types[index-1]);
        Set<Long> preRankingSet = rankingList.range(preRankingKey, 0, prePromotion.getSettle() - 1);
        //新榜单需要的人
        int restRank=1;
        int newRank=curCount.intValue()+1;
        List<Long> list=new ArrayList<>();
        for (Long uid : preRankingSet) {
            if (uid>0 && newRank<=promotion.getSettle()){
                rankingList.add(curRankingKey,uid,newRank++);
                rankingList.remove(preRankingKey,uid);
                list.add(uid);
            }else {
                rankingList.add(preRankingKey,uid,restRank++);
            }
        }
        //更新玩家榜单信息
        for (Long uid : list) {
            if (uid<0){
                continue;
            }
            FstGameRanking ranking = getOrCreateFstGameRanking(uid);
            ranking.setRankingType(cur.getType());
            gameDataService.updateGameData(ranking);
        }
        openRanking(gid,index-1);
    }
    
    /**
     * 榜单晋级变动
     * @param gid
     * @param index
     */
    public void rankingPromotion(int gid,int index){
        //当前榜单
        FstRankingType cur=types[index];
        if (FstRankingType.TIAN.equals(cur)){
            return;
        }
        String curRankingKey=getFstRankingKey(gid,cur);
        //高一级的榜单
        FstRankingType next=types[index+1];
        String nextRankingKey=getFstRankingKey(gid,next);
        if (rankingList.size(nextRankingKey)==0){
            //未开启
            return;
        }
        //获取当前榜单 晋级名单
        CfgFst.GameFstPromotion promotion = FstTool.getGameFstPromotion(cur);
        //需要晋级的人数
        int needPromotion = promotion.getPromotion();
        Set<Long> promotionSet = rankingList.range(curRankingKey, 0, needPromotion - 1);
        //该榜单中的晋级真人数量
        List<Long> promotionList=promotionSet.stream().filter(p->p>0).collect(Collectors.toList());
        if (promotionList.size()>0){
            //获取高一级榜单的淘汰名单
            CfgFst.GameFstPromotion nextPromotion = FstTool.getGameFstPromotion(next);
            //淘汰的名次开始
            int eliminateBeginRank=nextPromotion.getSettle()-needPromotion;
            //所有涉及到淘汰的名单
            Set<Long> eliminateSet = rankingList.range(nextRankingKey, eliminateBeginRank, nextPromotion.getSettle()-1);
            List<Long> eliminateList=new ArrayList<>(needPromotion);
            int need=needPromotion-promotionList.size();
            for (Long id : eliminateSet) {
                if (need>0){
                    //晋级人数不够  则只淘汰部分人
                    promotionList.add(id);
                    need--;
                }else {
                    eliminateList.add(id);
                }
            }
            eliminateList.addAll(promotionSet.stream().filter(p->p<0).collect(Collectors.toList()));
            //将晋级名单的ID 从 当前榜单移除，将淘汰名单中的ID 从原榜单移除
            rankingList.remove(curRankingKey,promotionList.toArray(new Long[needPromotion]));
            rankingList.remove(nextRankingKey,eliminateList.toArray(new Long[needPromotion]));
            //将各自名单重新加入到对应的榜单中
            for (int i = 0; i < eliminateList.size(); i++) {
                Long eliminateUid = eliminateList.get(i);
                if (eliminateUid>0){
                    FstGameRanking gameRanking = getOrCreateFstGameRanking(eliminateUid);
                    gameRanking.setRankingType(cur.getType());
                    gameDataService.updateGameData(gameRanking);
                }
                rankingList.add(curRankingKey,eliminateUid,i+1);
            }
            for (int i = 0; i < promotionList.size(); i++) {
                Long promotionUid = promotionList.get(i);
                if (promotionUid>0){
                    FstGameRanking gameRanking = getOrCreateFstGameRanking(promotionUid);
                    gameRanking.setRankingType(next.getType());
                    gameDataService.updateGameData(gameRanking);
                }
                rankingList.add(nextRankingKey,promotionUid,eliminateBeginRank+i+1);
            }
        }
        rankingPromotion(gid,index+1);
    }
    
    /**
     * 初始化跨服封神台
     *  默认120个机器人
     *  机器人ID 为 -900001~-900120
     * @param gid
     */
    @Override
    public boolean initFst(int gid){
        Date initDate=DateUtil.toDate(DateUtil.now(),0,0,0);
        hashUtil.putField(getFstStateKey(gid),STATE_SETTLE_DATE_TIME, DateUtil.toDateTimeString(initDate));
        Integer robotsNum = FstTool.getCfg().getGameRobotsNum();
        double[] ranks=new double[robotsNum];
        Long[] ids=new Long[robotsNum];
        for (Integer i = 0; i < robotsNum; i++) {
            ranks[i]=i+1;
            ids[i]=FstRobotService.getGameRobotId(i+1);
        }
        fstRobotService.initRobot();
        return addToRanking(gid, FstRankingType.REN,ranks,ids);
    }
    
    @Override
    public boolean swapRanking (Long uid1,FstVideoLog log1, Long uid2,FstVideoLog log2) {
        FstGameRanking ranking = getOrCreateFstGameRanking(uid1);
        FstRankingType rankingType = FstRankingType.fromVal(ranking.getRankingType());
        String rankingKey = getFstRankingKey(gameUserService.getActiveGid(uid1), rankingType);
        Long rank1 = rankingList.rank(rankingKey, uid1);
        Long rank2 = rankingList.rank(rankingKey, uid2);
        if (rank1==null || rank2==null){
            return false;
        }
        if (rank1<rank2){
            return false;
        }
        rank1++;
        rank2++;
        rankingList.add(rankingKey,uid1,rank2);
        rankingList.add(rankingKey,uid2,rank1);
        log1.setRank(rank1>rank2?rank2.intValue():-rank2.intValue());
        log2.setRank(rank1>rank2?-rank1.intValue():rank1.intValue());
        return true;
    }
    
    @Override
    public boolean checkFightState (long p1, long p2) {
        String stateKey = getFstStateKey(gameUserService.getActiveGid(p1));
        String state = hashUtil.getField(stateKey, FstGameService.STATE_SETTLE);
        String curSettleTimeStr = hashUtil.getField(stateKey, FstGameService.STATE_SETTLE_DATE_TIME);
        Date curSettleTime=DateUtil.fromDateTimeString(curSettleTimeStr);
        if (StrUtil.isNotBlank(state) && state.equals("1") || FstTool.getNextSettleTime(DateUtil.toDateInt(curSettleTime))<0){
            throw new ExceptionForClientTip("fst.do.settle");
        }
        //先检查是否在同一个榜单
        Integer gid = gameUserService.getActiveGid(p1);
        gid=gid==17?16:gid;
        Optional<FstGameRanking> op = getFstGameRankingOp(p1);
        if (!op.isPresent()){
            //自身必须要加入到榜单中
            throw new ExceptionForClientTip("fst.fighter.not.in.ranking");
        }
        FstGameRanking ranking=op.get();
        if (FstTool.getCfg().getFreeTimes()-ranking.getTodayFightTimes()<=0) {
            //没有挑战次数
            throw new ExceptionForClientTip("fst.not.fight.times");
        }
        FstRankingType type = FstRankingType.fromVal(ranking.getRankingType());
        String rankingKey = getFstRankingKey(gid, type);
        Long rank = rankingList.rank(rankingKey, p1);
        if (rank == null) {
            throw new ExceptionForClientTip("fst.fighter.not.in.ranking");
        }
        Long rank2 = rankingList.rank(rankingKey, p2);
        if (rank2 == null || (type.equals(FstRankingType.REN) && rank2 >= 120)) {
            //被挑战者必须要入榜
            throw new ExceptionForClientTip("fst.fighter.not.in.ranking");
        }
        //对手名次校验
        CfgFst config = FstTool.getCfg();
        Integer showCount = config.getShowCount();
        //入榜的才有该限制（还需再优化）
        if (rank < 120 && rank - rank2 > showCount) {
            throw new ExceptionForClientTip("fst.cant.attack.this.rank");
        }
        String fightStateKey = getFstFightStateKey(gid);
        synchronized (gid) {
            Long p1State = hasFightStateHUtil.getField(fightStateKey, p1);
            long millis = System.currentTimeMillis();
            if (p1State != null && (millis - p1State) < FIGHT_TIME_OUT) {
                //5分钟内说明 还在战斗
                return false;
            }
            Long p2State = hasFightStateHUtil.getField(fightStateKey, p2);
            if (p2State != null && (millis - p2State) < FIGHT_TIME_OUT) {
                //5分钟内说明 还在战斗
                return false;
            }
            hasFightStateHUtil.putField(fightStateKey,p1,millis);
            hasFightStateHUtil.putField(fightStateKey,p2,millis);
        }
        ranking.deductChallengeNum();
        gameDataService.updateGameData(ranking);
        return true;
    }
    
    @Override
    public void removeFightState(long p1, long p2) {
        String fightStateKey = getFstFightStateKey(gameUserService.getActiveGid(p1));
        hasFightStateHUtil.removeField(fightStateKey, p1);
        hasFightStateHUtil.removeField(fightStateKey, p2);
    }
    
    @Override
    public boolean hasJoinFst(long uid) {
        Optional<FstGameRanking> op = getFstGameRankingOp(uid);
        if (!op.isPresent()){
            return false;
        }
        return op.get().getRankingType()>0;
    }
    
    private int getPromotionRankState(int myRank,CfgFst.GameFstPromotion promotion){
        if (myRank<=promotion.getPromotion()){
            //晋级区
            return 1;
        }
        if (FstRankingType.REN.getType()==promotion.getType()){
            return 0;
        }
        if (myRank> (promotion.getPromotion()+ promotion.getStanding())&& myRank<=promotion.getSettle()){
            //降级区
            return -1;
        }
        return 0;
    }
}
