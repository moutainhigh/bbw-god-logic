package com.bbw.god.game.wanxianzhen;

import com.bbw.common.*;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.config.Cfg;
import com.bbw.god.game.wanxianzhen.service.WanXianLogic;
import com.bbw.god.game.wanxianzhen.service.WanXianSeasonService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.bbw.god.game.config.CfgInterface.FILE_UNIQE_KEY;

/**
 * 万仙阵工具类
 * @author lwb
 * @date 2020/4/22 16:01
 */
public class WanXianTool {

    /**
     * 获取当前赛季编号 周一到周六为周一的日期，周日下午4点以后为下一周ID
     * @return
     */
    public static int getThisSeason() {
        Date openDate = DateUtil.toDate(new Date(), 16, 0, 0);
        if (DateUtil.getToDayWeekDay() == 7 && DateUtil.getSecondsBetween(new Date(), openDate) <= 0) {
            return DateUtil.toDateInt(DateUtil.addDays(new Date(), 1));
        }
        int season = DateUtil.toDateInt(DateUtil.getThisWeekBeginDateTime());
        return season;
    }

    /**
     * 通过指定赛季 获取赛季编号
     * @param type
     * @param order 为1时表示第一届
     * @return
     */
    public static int getSeasonByOrder(int type,Integer order,int gid) {
        if (order == null) {
            Date date = DateUtil.addDays(DateUtil.fromDateInt(getThisSeason()), -7);
            return DateUtil.toDateInt(date);
        }

        Optional<CfgWanXian.RaceConfig> raceConfigOp = getRaceConfig(gid);
        if (!raceConfigOp.isPresent()){
            //该平台没有配置
        }
        CfgWanXian.RaceConfig raceConfig=raceConfigOp.get();
        int firstSeason=raceConfig.getFirstSeason();
        if (WanXianLogic.TYPE_SPECIAL_RACE==type) {
			firstSeason=raceConfig.getFirstSpecialSeason();
		}
        if (order < 1) {
            return firstSeason;
        }
        Date date = DateUtil.addDays(DateUtil.fromDateInt(firstSeason), 7 * (order - 1));
        return DateUtil.toDateInt(date);
    }
    /**
     * 获取万仙阵全部配置
     * @return
     */
    public static CfgWanXian getCfgWanXian() {
        return Cfg.I.getUniqueConfig(CfgWanXian.class);
    }

    /**
     * 根据平台号获取万仙阵配置
     * @param gid
     * @return
     */
    public static Optional<CfgWanXian.RaceConfig> getRaceConfig(int gid) {
        CfgWanXian cfgWanXian=getCfgWanXian();
        Optional<CfgWanXian.RaceConfig> raceConfig = cfgWanXian.getRaceConfig().stream().filter(p -> p.getGroupId() == gid).findFirst();
        return raceConfig;
    }

    /**
     * 获取所有开放万仙阵的平台号
     *
     * @return
     */
    public static List<Integer> getOpenedServerGroups() {
        CfgWanXian cfgWanXian = getCfgWanXian();
        int thisSeason = getThisSeason();
        List<Integer> list = cfgWanXian.getRaceConfig().stream()
                                       .filter(p -> p.ifSeasonOpen(thisSeason) && p.getFirstSeason() <= thisSeason)
                                       .map(CfgWanXian.RaceConfig::getGroupId)
                                       .collect(Collectors.toList());
        List<Integer> doJobIds = new ArrayList<>();
        for (Integer id : list) {
            int gid = getTargetGid(id);
            if (!doJobIds.contains(gid)) {
                doJobIds.add(gid);
            }
        }
        return doJobIds;
    }

    /**
     * 获取邮件
     *
     * @param wanXianEnum
     * @return
     */
    public static CfgWanXian.WanXianEmail getEmail(int type, WanXianEmailEnum wanXianEnum) {
        int id = wanXianEnum.getVal();
        CfgWanXian cfgWanXian = getCfgWanXian();
        CfgWanXian.WanXianEmail em = null;
        for (CfgWanXian.WanXianEmail email : cfgWanXian.getEmails()) {
            if (email.getId() == id) {
                em = CloneUtil.clone(email);
                break;
            }
        }
        String typeStr = "常规赛";
        if (type == 2000) {
            typeStr = "特色赛";
        }
        if (em != null) {
            em.setTitle(String.format(em.getTitle(), typeStr));
        }
        return em;
    }

    public static List<CfgWanXian.WanXianSeasonAward> getRankAwards(int type) {
        CfgWanXian cfgWanXian = getCfgWanXian();
        CfgWanXian.WanXianAward award = null;
        int thisSeason=getThisSeason();
        if (WanXianLogic.TYPE_REGULAR_RACE != type) {
            if(cfgWanXian.getAwardSpecialList().size()>1){
                for (int i=1;i<cfgWanXian.getAwardSpecialList().size();i++){
                    CfgWanXian.WanXianAward item=cfgWanXian.getAwardSpecialList().get(i);
                    if (item.getBegin()<=thisSeason && item.getEnd()>=thisSeason){
                        award=item;
                        break;
                    }
                }
            }
            if (award==null){
                award=cfgWanXian.getAwardSpecialList().get(0);
            }
        } else {
            if(cfgWanXian.getAwardList().size()>1){
                for (int i=1;i<cfgWanXian.getAwardList().size();i++){
                    CfgWanXian.WanXianAward item=cfgWanXian.getAwardList().get(i);
                    if (item.getBegin()<=thisSeason && item.getEnd()>=thisSeason){
                        award=item;
                        break;
                    }
                }
            }
            if (award==null){
                award=cfgWanXian.getAwardList().get(0);
            }
        }
        return ListUtil.copyList(award.getSeasonAwards(), CfgWanXian.WanXianSeasonAward.class);
    }

    /**
     * 根据排名获得奖励
     *
     * @param rank
     * @return
     */
    public static List<Award> getRankAwards(int rank, int type) {
        for (CfgWanXian.WanXianSeasonAward award : getRankAwards(type)) {
            if (award.getMinRank() <= rank && rank <= award.getMaxRank()) {
                return ListUtil.copyList(award.getAwards(), Award.class);
            }
        }
        return new ArrayList<>();
    }

    public static List<Award> getAwards(WanXianEmailEnum xianEmailEnum, int type) {
        for (CfgWanXian.WanXianSeasonAward award : getRankAwards(type)) {
            if (award.getPid() == xianEmailEnum.getAwardPid()) {
                return ListUtil.copyList(award.getAwards(), Award.class);
            }
        }
        return new ArrayList<>();
    }

    /**
     * 获取当前是第几个赛季
     *
     * @return
     */
    public static int getSeasonOrder(int type,int gid) {
        Optional<CfgWanXian.RaceConfig> raceConfigOptional = getRaceConfig(gid);
        if (!raceConfigOptional.isPresent()){
            return -1;
        }
        CfgWanXian.RaceConfig config=raceConfigOptional.get();
        Date date1 = DateUtil.fromDateInt(getThisSeason());
        Date date2 = null;
        if (WanXianLogic.TYPE_SPECIAL_RACE == type) {
            date2 = DateUtil.fromDateInt(config.getFirstSpecialSeason());
        } else {
            date2 = DateUtil.fromDateInt(config.getFirstSeason());
        }
        int days = DateUtil.getDaysBetween(date2, date1);
        int order = days / 7+1;
        return order <= 0 ? 1 : order;
    }

    /**
     * 根据当前时间获取倒计时 和显示的结果和类型
     *
     * @param rd
     */
    public static void getCountdown(int gid,int type,RDWanXian rd) {
        int today = DateUtil.getToDayWeekDay();
        Date nowDate = new Date();
        Date endDate = null;
        Optional<CfgWanXian.RaceConfig> configOptional = getRaceConfig(gid);
        if (!configOptional.isPresent() || !configOptional.get().ifSeasonOpen(getThisSeason()) || configOptional.get().getFirstSeason()>getThisSeason()){
            //还没开放
            throw new ExceptionForClientTip("wanxian.close.signup");
        }
        if (today == 1 || today == 2) {
            if (today == 1) {
                // 周一 12点前显示报名界面
                endDate = DateUtil.toDate(nowDate, 12, 0, 0);
                if (setRdCountDown(nowDate, endDate, WanXianCountDownType.END_SIGN_UP, rd)) {
                    rd.setWxType(WanXianPageType.SING_UP.getVal());
                    return;
                }
            }
            // 周一 周二 13点开始每15分钟显示一轮资格赛结果
            for (int i = 0; i < 4; i++) {
                endDate = DateUtil.toDate(nowDate, 13, i * 15, 0);
                if (setRdCountDown(nowDate, endDate, WanXianCountDownType.NEXT, rd)) {
                    rd.setWxType(WanXianPageType.QUALIFYING_RACE.getVal());
                    rd.setWxShowRace(WanXianEmailEnum.fromVal(13000 + (i - 1) * 15 * 10 + today));
                    if (i == 0) {
                        if (today == 1) {
                            // 周一13点前不显示结果
                            rd.setWxShowRace(null);
                        } else {
                            // 周二13点前显示周一最后一轮结果
                            rd.setWxShowRace(WanXianEmailEnum.EMAIL_QUALIFYING_RACE_4);
                        }
                    }
                    return;
                }
            }
            // 13点45分以后，如果是周一则显示 下一轮的倒计时，显示周一最后一轮结果
            if (today == 1) {
                endDate = DateUtil.toDate(DateUtil.addDays(nowDate, 1), 13, 0, 0);
                if (setRdCountDown(nowDate, endDate, WanXianCountDownType.NEXT, rd)) {
                    rd.setWxType(WanXianPageType.QUALIFYING_RACE.getVal());
                    rd.setWxShowRace(WanXianEmailEnum.EMAIL_QUALIFYING_RACE_4);
                    return;
                }
            }
            // 周二下午13:45~16:00显示 倒计时改为 淘汰分组倒计时，页面保留资格赛页面,显示周二最后一轮结果
            endDate = DateUtil.toDate(nowDate, 16, 0, 0);
            if (setRdCountDown(nowDate, endDate, WanXianCountDownType.ELIMINATION, rd)) {
                rd.setWxType(WanXianPageType.QUALIFYING_RACE.getVal());
                rd.setWxShowRace(WanXianEmailEnum.EMAIL_QUALIFYING_RACE_8);
                return;
            }
            // 周二16:00后显示淘汰赛界面 倒计时改为下一轮倒计时
            endDate = DateUtil.toDate(DateUtil.addDays(nowDate, 1), 13, 0, 0);
            if (setRdCountDown(nowDate, endDate, WanXianCountDownType.NEXT, rd)) {
                rd.setWxType(WanXianPageType.ELIMINATION_SERIES_RACE.getVal());
                return;
            }
        }
        // 周三
        if (today == 3 || today == 4 || today == 5) {
            endDate = DateUtil.toDate(nowDate, 13, 0, 0);
            if (setRdCountDown(nowDate, endDate, WanXianCountDownType.NEXT, rd)) {
                rd.setWxType(WanXianPageType.ELIMINATION_SERIES_RACE.getVal());
                if (today > 3) {
                    // 周三不显示结果 周四和周五 13点前 显示前一天的结果
                    rd.setWxShowRace(WanXianEmailEnum.fromVal(13000 + (today - 1)));
                }
                return;
            }
            // 周五 16点以后显示8强预测界面
            if (today == 5) {
                // 周五13点到16点 显示 预测时间倒计时，页面保留为淘汰赛界面
                endDate = DateUtil.toDate(nowDate, 16, 0, 0);
                if (setRdCountDown(nowDate, endDate, WanXianCountDownType.BEGIN_CHAMPION_PREDICTION, rd)) {
                    rd.setWxType(WanXianPageType.ELIMINATION_SERIES_RACE.getVal());
                    rd.setWxShowRace(WanXianEmailEnum.EMAIL_ELIMINATION_SERIES_RACE_3);
                    return;
                }
                // 周五16点到周六12点前显示预测界面
                endDate = DateUtil.toDate(DateUtil.addDays(nowDate, 1), 12, 0, 0);
                if (setRdCountDown(nowDate, endDate, WanXianCountDownType.END_CHAMPION_PREDICTION, rd)) {
                    rd.setWxType(WanXianPageType.GROUP_STAGE_CP.getVal());
                    rd.setWxShowRace(WanXianEmailEnum.EMAIL_ELIMINATION_SERIES_RACE_3);
                    return;
                }
            }
            // 周三 和周 四 下午13点以后 倒计时为下一轮时间,结果显示为今日结果
            endDate = DateUtil.toDate(DateUtil.addDays(nowDate, 1), 13, 0, 0);
            if (setRdCountDown(nowDate, endDate, WanXianCountDownType.NEXT, rd)) {
                rd.setWxType(WanXianPageType.ELIMINATION_SERIES_RACE.getVal());
                rd.setWxShowRace(WanXianEmailEnum.fromVal(13000 + today));
                return;
            }
        }
        // 周六 小组赛界面时间：周六12:00-周六16:00
        // 4强下注界面时间：周六16:00-周日12:00
        if (today == 6) {
            // 周六12点前显示小组赛预测界面
            endDate = DateUtil.toDate(nowDate, 12, 0, 0);
            if (setRdCountDown(nowDate, endDate, WanXianCountDownType.END_CHAMPION_PREDICTION, rd)) {
                rd.setWxType(WanXianPageType.GROUP_STAGE_CP.getVal());
                rd.setWxShowRace(WanXianEmailEnum.EMAIL_ELIMINATION_SERIES_RACE_3);
                return;
            }
            // 小组赛界面时间：周六12:00-周六16:00
            // 周六13点后 到 13点50分 显示小组赛界面和对应结果
            for (int i = 0; i < 6; i++) {
                endDate = DateUtil.toDate(nowDate, 13, i * 10, 0);
                if (setRdCountDown(nowDate, endDate, WanXianCountDownType.NEXT, rd)) {
                    if (i >= 1) {
                        rd.setWxShowRace(WanXianEmailEnum.fromVal(13006 + (i - 1) * 100));
                    }
                    rd.setWxType(WanXianPageType.GROUP_STAGE.getVal());
                    return;
                }
            }
            // 周六下午13:50分到16点 显示小组赛界面 倒计时改为预测倒计时
            endDate = DateUtil.toDate(nowDate, 16, 0, 0);
            if (setRdCountDown(nowDate, endDate, WanXianCountDownType.BEGIN_CHAMPION_PREDICTION, rd)) {
                rd.setWxType(WanXianPageType.GROUP_STAGE.getVal());
                rd.setWxShowRace(WanXianEmailEnum.EMAIL_GROUP_STAGE_6);
                return;
            }
            // 周六16点至周日12点显示4强预测界面
            endDate = DateUtil.toDate(DateUtil.addDays(nowDate, 1), 12, 0, 0);
            if (setRdCountDown(nowDate, endDate, WanXianCountDownType.END_CHAMPION_PREDICTION, rd)) {
                rd.setWxType(WanXianPageType.FINALS_RACE_CP.getVal());
                rd.setWxShowRace(WanXianEmailEnum.EMAIL_GROUP_STAGE_6);
                return;
            }
        }
        // 周日 4强下注界面时间：周日12:00前
        endDate = DateUtil.toDate(nowDate, 12, 0, 0);
        if (setRdCountDown(nowDate, endDate, WanXianCountDownType.END_CHAMPION_PREDICTION, rd)) {
            rd.setWxType(WanXianPageType.FINALS_RACE_CP.getVal());
            rd.setWxShowRace(WanXianEmailEnum.EMAIL_GROUP_STAGE_6);
            return;
        }
        // 总决赛界面时间：周天12:00-周日16:00
        for (int i = 0; i < 4; i++) {
            endDate = DateUtil.toDate(nowDate, 13, i * 15, 0);
            if (i>=3){
                //需要判断是否有第三场
                WanXianSeasonService wanXianSeasonService=SpringContextUtil.getBean(WanXianSeasonService.class);
                String times=wanXianSeasonService.getVal(gid,type,"raceOver");
                if (times!=null && Integer.parseInt(times)==1){
                    break;
                }
            }
            if (setRdCountDown(nowDate, endDate, WanXianCountDownType.NEXT, rd)) {
                rd.setWxType(WanXianPageType.FINALS_RACE.getVal());
                if (i == 0) {
                    rd.setWxShowRace(null);
                }else if (i == 1) {
                    rd.setWxShowRace(WanXianEmailEnum.EMAIL_FINAL_RACE_1);
                }else if (i==2){
                    rd.setWxShowRace(WanXianEmailEnum.EMAIL_FINAL_RACE_2);
                }else {
                    rd.setWxShowRace(WanXianEmailEnum.EMAIL_FINAL_RACE_3);
                }
                return;
            }
        }
        // 周日13:15分后 到16点显示 赛季结束倒计时
        endDate = DateUtil.toDate(nowDate, 16, 0, 0);
        if (setRdCountDown(nowDate, endDate, WanXianCountDownType.END_SEASON, rd)) {
            rd.setWxType(WanXianPageType.FINALS_RACE.getVal());
            rd.setWxShowRace(WanXianEmailEnum.EMAIL_FINAL_RACE_4);
            return;
        }
        // 周日16点 到周一12点显示报名界面 和倒计时
        endDate = DateUtil.toDate(DateUtil.addDays(nowDate, 1), 12, 0, 0);
        if (setRdCountDown(nowDate, endDate, WanXianCountDownType.END_SIGN_UP, rd)) {
            rd.setWxType(WanXianPageType.SING_UP.getVal());
        }
    }

    public static void setShowRaceEnum(Integer wxtype, RDWanXian rd) {
        WanXianPageType pageType = WanXianPageType.fromVal(wxtype);
        if (pageType == null) {
            return;
        }
        if (rd.getCurrentWxType() != null && rd.getCurrentWxType() == wxtype.intValue()) {
            rd.setWxType(wxtype);
            return;
        }
        if (rd.getWxType().intValue() == WanXianPageType.SING_UP.getVal()) {
            rd.setNewSeason(1);
            return;
        }
        rd.setNewSeason(0);
        rd.setCountdown(null);
        rd.setCountdownName(null);
        WanXianEmailEnum show = null;
        switch (pageType) {
            case QUALIFYING_RACE:
                show = WanXianEmailEnum.EMAIL_QUALIFYING_RACE_8;
                break;
            case ELIMINATION_SERIES_RACE:
                show = WanXianEmailEnum.EMAIL_ELIMINATION_SERIES_RACE_3;
                break;
            case GROUP_STAGE:
                show = WanXianEmailEnum.EMAIL_GROUP_STAGE_6;
                break;
            case FINALS_RACE:
                show = WanXianEmailEnum.EMAIL_FINAL_RACE_2;
                break;
        }
        rd.setWxShowRace(show);
        rd.setWxType(wxtype);
    }

    private static boolean setRdCountDown(Date nowDate, Date endDate, WanXianCountDownType type, RDWanXian rd) {
        long countDown = DateUtil.millisecondsInterval(endDate, nowDate);
        if (countDown > 0) {
            rd.setCountdownName(type.getMemo() + ":");
            rd.setCountdown(countDown);
            return true;
        }
        return false;
    }

    public static int getCurrentSpecialType(int gid) {
       return getSpecialTypeBySeason(getThisSeason(),gid);
    }

    public static int getNextSpecialType(int gid) {
        int newSeason=DateUtil.toDateInt(DateUtil.addDays(DateUtil.fromDateInt(getThisSeason()),7));
        return getSpecialTypeBySeason(newSeason,gid);
    }

    private static int getSpecialTypeBySeason(int season,int gid){
        Optional<CfgWanXian.RaceConfig> configOptional = getRaceConfig(gid);
        if (!configOptional.isPresent()){
            return 0;
        }
        CfgWanXian.RaceConfig config=configOptional.get();
        List<CfgWanXian.WanXianRacePlan> plans = config.getSpecialSeasonPlans();
        CfgWanXian.WanXianRacePlan plan = plans.get(0);
        if (plans.size() > 0) {
            for (int i = 1; i < plans.size(); i++) {
                CfgWanXian.WanXianRacePlan p = plans.get(i);
                if (season >= p.getBeginSeason() && season <= p.getEndSeason()) {
                    plan = p;
                    break;
                }
            }
        }
        int order = 0;
        Date begin = DateUtil.fromDateInt(plan.getBeginSeason());
        if (season > plan.getBeginSeason()) {
            for (int i = 0; i < 10000; i++) {
                int dateInt = DateUtil.toDateInt(DateUtil.addDays(begin, i * 7));
                if (dateInt == season) {
                    order = i;
                    break;
                }
            }
        }
        order = order % plan.getPlans().size();
        return plan.getPlans().get(order);
    }
    public static CfgWanXianBox getCfgWanXianBox() {
        return Cfg.I.get(FILE_UNIQE_KEY, CfgWanXianBox.class);
    }

    /**
     * 获取召唤师等级
     * @param type
     * @return
     */
    public static int getPlayerLv(int type){
        //默认100级
        return getCfgWanXian().getDefaultPlayerLv();
    }

    /**
     * 获取背水战的玩家血量
     * @param type
     * @return
     */
    public static int getPlayerHp(int type,int gid){
        if (WanXianLogic.TYPE_SPECIAL_RACE==type){
            int specialType=getCurrentSpecialType(gid);
            if (( WanXianSpecialType.BEI_SHUI.getVal()==specialType || WanXianSpecialType.MAGIC.getVal()==specialType)){
                return 18*10000;
            }else if (WanXianSpecialType.GONG_CHENG.getVal()==specialType){
                return 80*10000;
            }
            //特色赛默认血量
            return 77240;
        }
        //常规赛默认血量
        return 38620;
    }
    /**
     * 获取卡牌等级
     * @param type
     * @return
     */
    public static int getCardLv(int type,int gid){
        if (WanXianLogic.TYPE_SPECIAL_RACE==type){
            int specialType=getCurrentSpecialType(gid);
            if ( WanXianSpecialType.XIN_SHOU.getVal()==specialType){
                return 10;
            }
        }
        return getCfgWanXian().getDefaultCardLv();
    }

    /**
     * 获取卡牌阶数
     * @param type
     * @return
     */
    public static int getCardHv(int type,int gid){
        if (WanXianLogic.TYPE_SPECIAL_RACE==type){
            int specialType=getCurrentSpecialType(gid);
            if (WanXianSpecialType.XIN_SHOU.getVal()==specialType){
                return 0;
            }
        }
        return getCfgWanXian().getDefaultCardHv();
    }

    /**
     * 根据当前gid 获取实际参与的gid
     * @param gid
     * @return
     */
    public static int getTargetGid(int gid){
        if (gid==17){
            return 16;
        }
        return gid;
    }
    
    /**
     * 随机获取万仙阵机器人
     * @param specialType 特色赛事
     * @param num  数量
     * @return
     */
    public static List<CfgWanXianRobot.RobotInfo> getRandomList(int specialType,int num){
        CfgWanXianRobot cfgWanXianRobot = Cfg.I.get(specialType, CfgWanXianRobot.class);
        return PowerRandom.getRandomsFromList(cfgWanXianRobot.getGuCards(),num);
    }
}
