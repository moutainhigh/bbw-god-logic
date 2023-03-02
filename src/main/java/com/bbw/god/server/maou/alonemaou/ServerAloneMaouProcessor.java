package com.bbw.god.server.maou.alonemaou;

import com.bbw.common.DateUtil;
import com.bbw.common.ListUtil;
import com.bbw.common.PowerRandom;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardService;
import com.bbw.god.game.config.CfgAloneMaou;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.card.CardTool;
import com.bbw.god.game.config.card.CfgCardEntity;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.card.UserCard;
import com.bbw.god.gameuser.card.UserCardService;
import com.bbw.god.gameuser.res.ResChecker;
import com.bbw.god.gameuser.res.ResEventPublisher;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.rd.RDCommon;
import com.bbw.god.rd.RDSuccess;
import com.bbw.god.server.maou.IServerMaouProcessor;
import com.bbw.god.server.maou.ServerMaouKind;
import com.bbw.god.server.maou.ServerMaouStatus;
import com.bbw.god.server.maou.ServerMaouStatusInfo;
import com.bbw.god.server.maou.alonemaou.attackinfo.AloneMaouAttackSummary;
import com.bbw.god.server.maou.alonemaou.attackinfo.AloneMaouAttackSummaryService;
import com.bbw.god.server.maou.alonemaou.attackinfo.AloneMaouLevelInfo;
import com.bbw.god.server.maou.alonemaou.attackinfo.AloneMaouLevelService;
import com.bbw.god.server.maou.alonemaou.event.AloneMaouEventPublisher;
import com.bbw.god.server.maou.alonemaou.rd.RDAloneMaouAttack;
import com.bbw.god.server.maou.alonemaou.rd.RDAloneMaouAttackingInfo;
import com.bbw.god.server.maou.alonemaou.rd.RDAloneMaouAwards;
import com.bbw.god.server.maou.alonemaou.rd.RDAloneMaouInfo;
import com.bbw.god.server.maou.bossmaou.ServerBossMaou;
import com.bbw.god.server.maou.bossmaou.ServerBossMaouService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * 独战魔王逻辑
 *
 * @author suhq
 * @date 2019年12月19日 上午11:46:27
 */
@Slf4j
@Service
public class ServerAloneMaouProcessor implements IServerMaouProcessor<ServerAloneMaou> {
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private ServerAloneMaouService aloneMaouService;
    @Autowired
    private AloneMaouLevelService maouLevelService;
    @Autowired
    private AloneMaouAttackSummaryService attackSummaryService;// 玩家攻击魔王的信息
    @Autowired
    private ServerAloneMaouAttackService serverAloneMaouAttackService;
    @Autowired
    private AwardService awardService;
    @Autowired
    private UserCardService userCardService;
    @Autowired
    private ServerBossMaouService bossMaouService;

    /**
     * 获得魔王信息
     *
     * @param uid
     * @param sid
     * @return
     */
    @Override
    public RDAloneMaouInfo getMaou(long uid, int sid) {
        AloneMaouParam param = getAloneMaouParam(uid, sid);
        if (param.getMaou() == null) {
            return RDAloneMaouInfo.getInstanceAsMaouOver();
        }
        checkAndRepairData(uid, param);
        Date now = DateUtil.now();
        ServerBossMaou bossMaou = bossMaouService.getBossMaous(sid, now).stream().filter(tmp ->
                tmp.ifMe(now)).findFirst().orElse(null);
        Integer bossMaouNextOpenTime = null;
        Integer bossMaouStatus = null;
        Long bossMaouRemainTime = null;
        if (null == bossMaou) {
            int time = DateUtil.toHMSInt(DateUtil.now()) / 100;
            bossMaouNextOpenTime = time >= 1130 && time <= 1830 ? 1830 : 1130;
        } else {
            Date endTime = bossMaou.getEndTime();
            bossMaouRemainTime = endTime.getTime() - now.getTime();
            bossMaouStatus = ServerMaouStatus.ATTACKING.getValue();
            if (bossMaouRemainTime > 30 * 60 * 1000) {
                bossMaouRemainTime -= 30 * 60 * 1000;
                bossMaouStatus = ServerMaouStatus.ASSEMBLY.getValue();
            }
        }
        return RDAloneMaouInfo.getInstance(param, bossMaouRemainTime, bossMaouStatus, bossMaouNextOpenTime);
    }

    /**
     * 获取所有的独占魔王奖励信息
     *
     * @param uid 玩家id
     * @param sid 区服id
     * @return
     */
    public RDAloneMaouAwards getAwards(long uid, int sid) {
        AloneMaouParam param = getAloneMaouParam(uid, sid);
        ServerAloneMaou maou = param.getMaou();
        List<Award> awards = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            List<Award> awardList = AloneMaouTool.getMaouLeveAward(maou.getType(), i);
            awards.addAll(awardList);
        }
        int passedLevel = this.aloneMaouService.getAttackedLevel(uid, sid, DateUtil.now()) - 1;
        return RDAloneMaouAwards.getInstance(passedLevel, awards);
    }

    /**
     * 进入魔王攻打界面
     *
     * @param uid
     * @param sid
     * @return
     */
    @Override
    public RDAloneMaouAttackingInfo getAttackingInfo(long uid, int sid) {
        AloneMaouParam param = getAloneMaouParam(uid, sid);
        if (param.getMaou() == null) {
            return RDAloneMaouAttackingInfo.getInstanceAsMaouOver();
        }
        ServerAloneMaou aloneMaou = param.getMaou();
        AloneMaouLevelInfo maouLevelInfo = param.getLevelInfo();
        if (null == maouLevelInfo) {
            throw new ExceptionForClientTip("maoualone.already.refreash");
        }
        //第七层魔王要同属性卡牌
        List<CfgCardEntity> cfgCardEntities = CardTool.getCards(param.getUserMaouData().getAttackCards());
        if (maouLevelInfo.getMaouLevel() == 7) {
            boolean isMatchType = cfgCardEntities.stream().anyMatch(tmp -> tmp.getType().intValue() == aloneMaou.getType());
            if (!isMatchType) {
                throw new ExceptionForClientTip("maoualone.card.no.match.type");
            }
        }
        RDAloneMaouAttackingInfo rd = RDAloneMaouAttackingInfo.getInstance(param, isDoublePassAward(param));
        return rd;
    }

    /**
     * 独战魔王编组
     *
     * @param uid
     * @param maouCards
     * @return
     */
    @Override
    public RDSuccess setMaouCards(long uid, String maouCards) {
        if (maouCards.contains("undefined")) {
            throw new ExceptionForClientTip("maoualone.card.unvalid");
        }
        int num = maouCards.split(",").length;
        CfgAloneMaou config = AloneMaouTool.getConfig();
        //检查编组数量
        int cardLimit = config.getCardLimit();
        if (num != cardLimit) {
            throw new ExceptionForClientTip("maoualone.card.unvalidNum");
        }
        // 判断卡牌的有效性
        List<UserCard> cards = userCardService.getUserCards(uid, ListUtil.parseStrToInts(maouCards));
        if (cards == null || cards.size() != num) {
            throw new ExceptionForClientTip("maou.card.unvalid");
        }

        //更新数据
        UserAloneMaouData uamd = this.gameUserService.getSingleItem(uid, UserAloneMaouData.class);
        uamd.setAttackCards(ListUtil.parseStrToInts(maouCards));
        this.gameUserService.updateItem(uamd);
        return new RDSuccess();
    }

    /**
     * 重置魔王级别
     *
     * @param uid
     * @return
     */
    public RDCommon resetMaouLevel(long uid, int sid) {
        GameUser gu = this.gameUserService.getGameUser(uid);
        Optional<ServerAloneMaou> samOptional = this.aloneMaouService.getCurAloneMaou(sid);
        if (!samOptional.isPresent()) {
            throw new ExceptionForClientTip("maoualone.not.coming");
        }
        ServerAloneMaou maou = samOptional.get();
        AloneMaouAttackSummary myAttack = this.attackSummaryService.getMyAttackInfo(uid, maou);
        CfgAloneMaou config = AloneMaouTool.getConfig();
        if (myAttack.getRemainResetTimes() <= 0) {
            throw new ExceptionForClientTip("maoualone.already.reset");
        }
        ResChecker.checkGold(gu, config.getResetGold());
        RDCommon rd = new RDCommon();
        ResEventPublisher.pubGoldDeductEvent(uid, config.getResetGold(), WayEnum.MAOU_ALONE_RESET, rd);

        //清除已经存在魔王等级数据
        this.maouLevelService.removeMaouLevelInfo(uid, maou);
        //初始化魔王等级数据
        this.aloneMaouService.initMaouLevelInfo(uid, maou);
        //重置玩家魔王数据
        myAttack.reset(false);
        this.attackSummaryService.setMyAttackInfo(uid, maou, myAttack);
        return rd;
    }

    @Override
    public ServerMaouStatusInfo getMaouStatus(ServerAloneMaou maou) {
        if (maou == null) {
            return new ServerMaouStatusInfo(ServerMaouStatus.OVER.getValue());
        }
        Date now = DateUtil.now();
        // 攻打期间
        if (now.after(maou.getBeginTime()) && now.before(maou.getEndTime())) {
            Long maouLeaveRemainTime = maou.getEndTime().getTime() - now.getTime();
            return new ServerMaouStatusInfo(ServerMaouStatus.ATTACKING.getValue(), maouLeaveRemainTime.intValue());
        }
        return new ServerMaouStatusInfo(ServerMaouStatus.OVER.getValue());
    }

    @Override
    public RDAloneMaouAttack attack(long uid, int sid, int attackTypeInt) {
        AloneMaouParam param = getAloneMaouParam(uid, sid);
        ServerAloneMaou aloneMaou = param.getMaou();
        if (aloneMaou == null) {
            return RDAloneMaouAttack.getInstanceAsMaouOver();
        }
        RDAloneMaouAttack rd = new RDAloneMaouAttack();
        BeforeAloneAttack beforeAloneAttack = this.serverAloneMaouAttackService.beforeAttack(uid, attackTypeInt, param);
        // 打掉魔王血量
        int beatedBlood = this.serverAloneMaouAttackService.attacking(uid, attackTypeInt, param, rd);

        // 发放攻击奖励
        // 增加经验
        int addedExp = beatedBlood / 40;
        ResEventPublisher.pubExpAddEvent(uid, addedExp, WayEnum.MAOU_ALONE_FIGHT, rd);
        // 获得铜钱
        int addedCopper = addedExp * PowerRandom.getRandomBetween(90, 110) / 100;
        ResEventPublisher.pubCopperAddEvent(uid, addedCopper, WayEnum.FIGHT_MAOU, rd);
        //消耗元宝
        boolean isUseGold = beforeAloneAttack.getNeedGold() > 0;
        if (isUseGold) {
            ResEventPublisher.pubGoldDeductEvent(uid, beforeAloneAttack.getNeedGold(), WayEnum.MAOU_ALONE_FIGHT, rd);
        }

        //彩蛋奖励
        AloneMaouAttackSummary myAttack = param.getMyAttack();
        int maouDiceProb = AloneMaouTool.getMaouDiceProb(myAttack.getAccBlood());
        if (PowerRandom.getRandomBySeed(100) <= maouDiceProb) {
            TreasureEventPublisher.pubTAddEvent(uid, TreasureEnum.MoWTZ.getValue(), 1, WayEnum.MAOU_ALONE_FIGHT, rd);
            myAttack.setAccBlood(0);
        }

        // 击杀奖励
        AloneMaouLevelInfo maouLevelInfo = param.getLevelInfo();
        if (maouLevelInfo.isKilled()) {
            //更新已击杀的魔王
            //层数奖励
            sendPassAwards(uid, param, rd);
            UserAloneMaouData uamd = param.getUserMaouData();
            boolean firstKilled = uamd.firstKilled(aloneMaou.getType(), maouLevelInfo.getMaouLevel());
            uamd.addKilledMaou(aloneMaou.getType(), maouLevelInfo.getMaouLevel());
            this.gameUserService.updateItem(uamd);
            //增加一次免费
            myAttack.addFreeTime();
            myAttack.setNextMaouLevel(maouLevelInfo.getMaouLevel() + 1);
            this.attackSummaryService.setMyAttackInfo(uid, aloneMaou, myAttack);
            rd.setAddAloneMaouFreeTimes(1);
            //击杀送魔王骰子
            TreasureEventPublisher.pubTAddEvent(uid, TreasureEnum.MoWTZ.getValue(), 1, WayEnum.MAOU_ALONE_FIGHT, rd);

            AloneMaouEventPublisher.pubKilledEvent(aloneMaou, maouLevelInfo, firstKilled, rd);
        } else if (maouLevelInfo.ifLeave()) {
            maouLevelInfo.reset();
            //更新魔王
            this.maouLevelService.saveMaouLevelInfo(param.getMaou(), maouLevelInfo);
        } else {
            this.attackSummaryService.setMyAttackInfo(uid, aloneMaou, myAttack);
        }
        rd.setAttackingInfo(RDAloneMaouAttackingInfo.getInstance(getAloneMaouParam(uid, sid), isDoublePassAward(param)));
        return rd;
    }

    @Override
    public boolean isMatch(int sid) {
        Optional<ServerAloneMaou> samOptional = this.aloneMaouService.getCurAloneMaou(sid);
        if (samOptional.isPresent()) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isMatchByMaouKind(int maouKind) {
        return ServerMaouKind.ALONE_MAOU.getValue() == maouKind;
    }

    /**
     * 获取独战魔王参数
     *
     * @param uid
     * @param sid
     * @return
     */
    private AloneMaouParam getAloneMaouParam(long uid, int sid) {
        //获取独战魔王配置信息
        CfgAloneMaou config = AloneMaouTool.getConfig();
        //获取本期魔王
        Optional<ServerAloneMaou> samOptional = this.aloneMaouService.getCurAloneMaou(sid);
        if (!samOptional.isPresent()) {
            return new AloneMaouParam();
        }
        ServerAloneMaou maou = samOptional.get();
        //获取玩家独战魔王信息
        UserAloneMaouData uamd = this.gameUserService.getSingleItem(uid, UserAloneMaouData.class);
        //获取当前独战魔王等级
        AloneMaouLevelInfo levelInfo = this.aloneMaouService.getCurLevelMaou(uid, maou);
        AloneMaouAttackSummary myAttack = this.attackSummaryService.getMyAttackInfo(uid, maou);
        return new AloneMaouParam(uamd, maou, levelInfo, myAttack, config);
    }

    private void checkAndRepairData(long uid, AloneMaouParam param) {
        //如果没有玩家独战魔王配置数据，则初始化玩家独战魔王配置数据
        if (param.getUserMaouData() == null) {
            UserAloneMaouData uamd = UserAloneMaouData.getInstance(uid);
            this.gameUserService.addItem(uid, uamd);
            param.setUserMaouData(uamd);
        }
        //如果没有个人攻打信息，则初始化攻打信息
        if (param.getMyAttack() == null) {
            AloneMaouAttackSummary myAttack = AloneMaouAttackSummary.getInstance(uid, param.getMaou().getId());
            this.attackSummaryService.setMyAttackInfo(uid, param.getMaou(), myAttack);
            param.setMyAttack(myAttack);
        }
        //如果未初始化魔王层级信息，则执行初始化
        if (param.getLevelInfo() == null) {
            //重置玩家独战魔王数据
            AloneMaouAttackSummary myAttack = AloneMaouAttackSummary.getInstance(uid, param.getMaou().getId());
            myAttack.reset(true);
            this.attackSummaryService.setMyAttackInfo(uid, param.getMaou(), myAttack);
            param.setMyAttack(myAttack);
            //初始化攻打记录
            this.aloneMaouService.initMaouLevelInfo(uid, param.getMaou());
            AloneMaouLevelInfo levelInfo = this.aloneMaouService.getCurLevelMaou(uid, param.getMaou());
            param.setLevelInfo(levelInfo);

        }
    }

    /**
     * 通关建立是否翻倍
     *
     * @param maouParam
     * @return
     */
    private boolean isDoublePassAward(AloneMaouParam maouParam) {
        int maouType = maouParam.getMaou().getType();
        int maouLevel = maouParam.getLevelInfo().getMaouLevel();
        int maouIndex = AloneMaouTool.getMaouIndex(maouType, maouLevel);
        if (!maouParam.getUserMaouData().getMaouKilledRecord().contains(maouIndex) && maouLevel != 8) {
            return true;
        }
        return false;
    }

    private void sendPassAwards(long uid, AloneMaouParam param, RDCommon rd) {
        int maouType = param.getMaou().getType();
        int maouLevel = param.getLevelInfo().getMaouLevel();
        List<Award> maouLeveAwards = AloneMaouTool.getMaouLeveAward(maouType, maouLevel);
        if (ListUtil.isNotEmpty(maouLeveAwards)) {
            boolean isDoublePassWard = isDoublePassAward(param);
            if (isDoublePassWard) {
                maouLeveAwards.stream().forEach(tmp -> tmp.setNum(tmp.getNum() * 2));
            }
            this.awardService.fetchAward(uid, maouLeveAwards, WayEnum.MAOU_ALONE_FIGHT, "", rd);
        }
        AloneMaouEventPublisher.pubPassLevelEvent(uid);
    }

}
