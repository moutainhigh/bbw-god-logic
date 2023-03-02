package com.bbw.god.gm;

import com.bbw.common.DateUtil;
import com.bbw.common.LM;
import com.bbw.common.ListUtil;
import com.bbw.common.Rst;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.activity.config.ActivityScopeEnum;
import com.bbw.god.activity.config.ActivityTool;
import com.bbw.god.activity.game.GameActivity;
import com.bbw.god.activity.game.GameActivityGeneratorService;
import com.bbw.god.activity.game.GameActivityService;
import com.bbw.god.controller.AbstractController;
import com.bbw.god.db.entity.CfgActivityEntity;
import com.bbw.god.db.entity.CfgServerEntity;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.game.config.server.ServerTool;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.game.data.GameDataService;
import com.bbw.god.gameuser.UserLoginInfo;
import com.bbw.god.gameuser.mail.UserMail;
import com.bbw.god.server.ServerUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 活动相关管理服务
 *
 * @author suhq
 * @date 2019年4月13日 下午1:49:30
 */
@Slf4j
@RestController
@RequestMapping("/gm")
public class GMActivityGameCtrl extends AbstractController {
    @Autowired
    private GameActivityGeneratorService gameActivityGeneratorService;
    @Autowired
    private GameActivityService gameActivityService;
    @Autowired
    private GameDataService gameDataService;
    @Autowired
    private ServerUserService serverUserService;

    @Value("${game-data-result-days:30}")
    private int prepareDays;// 提前生成多少天的结果数据

    @GetMapping("server!showGameActivities")
    public Rst showGameActivities(String sinceDate) {
        Rst rst = Rst.businessOK();
        Date sinceDateObj = DateUtil.fromDateTimeString(sinceDate);
        List<GameActivity> gas = gameDataService.getGameDatas(GameActivity.class);
        gas.sort(Comparator.comparing(GameActivity::getType).thenComparing((o1, o2) -> o1.gainBegin().compareTo(o1.gainBegin())));
        List<String> strs =
                gas.stream().filter(tmp -> tmp.gainBegin().after(sinceDateObj)).map(tmp -> tmp.toDesString()).collect(Collectors.toList());
        rst.put("全服活动实例数", strs.size());
        rst.put("全服活动实例", strs);
        return rst;
    }

    /**
     * 删除指定的的活动，如每日、签到
     *
     * @param sinceDate
     * @return
     */
    @GetMapping("server!delSpecialFutureActivities")
    public Rst delSpecialFutureActivities(String sinceDate) {
        Rst rst = Rst.businessOK();
        Date sinceDateObj = DateUtil.fromDateTimeString(sinceDate);
        checkDelSinceDate(sinceDateObj);

        List<GameActivity> gas = gameDataService.getGameDatas(GameActivity.class);
        List<GameActivity> gasToDel = gas.stream().filter(tmp -> tmp.getBegin().after(sinceDateObj)).collect(Collectors.toList());
        gameActivityService.delGameActivities(gasToDel);
        if (ListUtil.isNotEmpty(gasToDel)) {
            List<String> strs = gas.stream().map(tmp -> tmp.toDesString()).collect(Collectors.toList());
            rst.put("全服未来的活动实例数:", strs.size());
            rst.put("全服未来的活动实例:", strs);
        }
        return rst;
    }

    @GetMapping("server!appendGameActivity")
    public Rst appendGameActivity(int days) {
        if (days < 0 || days > 31) {
            return Rst.businessFAIL("最多只能追加一个月的数据");
        }
        this.gameActivityGeneratorService.appendActivities(days);
        return Rst.businessOK();
    }


    @GetMapping("server!updateGameActivityParentType")
    public Rst updateGameActivityParentType(String groups, int activityType, int parentType) {
        List<CfgActivityEntity> cas = ActivityTool.getActivitiesByType(ActivityEnum.fromValue(activityType));
        if (ListUtil.isEmpty(cas)) {
            return Rst.businessFAIL("错误的活动类型");
        }
        List<Integer> groupIds = ListUtil.parseStrToInts(groups);
        List<GameActivity> gasToUpdate = gameDataService.getGameDatas(GameActivity.class).stream()
                .filter(ga -> ga.getType() == activityType && groupIds.contains(ga.getServerGroup()))
                .collect(Collectors.toList());
        gasToUpdate.forEach(ga -> ga.setParentType(parentType));
        gameActivityService.updateGameActivities(gasToUpdate);
        return Rst.businessOK();
    }

    @GetMapping("server!delGameActivities")
    public Rst delGameActivities(String groups, int activityType, String sinceDate) {
        List<CfgActivityEntity> cas = ActivityTool.getActivitiesByType(ActivityEnum.fromValue(activityType));
        if (ListUtil.isEmpty(cas)) {
            return Rst.businessFAIL("错误的活动类型");
        }
        Date date = DateUtil.fromDateTimeString(sinceDate);
        //checkDelSinceDate(date);
        List<Integer> groupIds = ListUtil.parseStrToInts(groups);
        List<GameActivity> gasToDel = gameDataService.getGameDatas(GameActivity.class).stream()
                .filter(ga -> ga.getType() == activityType && groupIds.contains(ga.getServerGroup()) && ga.getBegin().after(date))
                .collect(Collectors.toList());
        gameActivityService.delGameActivities(gasToDel);
        return Rst.businessOK();
    }

    /**
     * 创建活动实例
     *
     * @param groups
     * @param types
     * @param begin
     * @param end
     * @return
     */
    @GetMapping("server!addGameActivity")
    public Rst addActivity(String groups, String types, String begin, String end, String sign) {
        List<Integer> groupIds = ListUtil.parseStrToInts(groups);
        if (groupIds.size() == 0) {
            return Rst.businessFAIL("groups不能为空");
        }
        if (groupIds.contains(0) && groupIds.size() > 1) {
            return Rst.businessFAIL("groups中0和其他任意值是互斥的，不允许含0的同时还含有其他数值");
        }
        List<Integer> typeInts = ListUtil.parseStrToInts(types);
        if (ListUtil.isEmpty(typeInts)) {
            return Rst.businessFAIL("types不能为空");
        }
        typeInts.stream().forEach(typeInt -> {
            ActivityEnum activityEnum = ActivityEnum.fromValue(typeInt);
            if (activityEnum == null) {
                throw ExceptionForClientTip.fromMsg("不存在活动枚举type:" + typeInt);
            }
            List<CfgActivityEntity> cas = ActivityTool.getActivitiesByType(activityEnum);
            if (ListUtil.isEmpty(cas)) {
                throw ExceptionForClientTip.fromMsg("该活动未配置");
            }
            CfgActivityEntity ca = cas.get(0);
            if (ca.getScope() != ActivityScopeEnum.GAME.getValue()) {
                throw ExceptionForClientTip.fromMsg(typeInt + "不是全服活动");
            }

        });
        Date now = DateUtil.now();
        Date beginDate = DateUtil.fromDateTimeString(begin);
        Date endDate = DateUtil.fromDateTimeString(end);
        if (beginDate.after(endDate)) {
            return Rst.businessFAIL("活动开始时间需早于结束时间");
        }
        if (now.after(beginDate) || now.after(endDate)) {
            return Rst.businessFAIL("活动时间必需晚于当前时间");
        }
        List<ActivityEnum> typeEnums = typeInts.stream().map(typeInt -> ActivityEnum.fromValue(typeInt)).collect(Collectors.toList());
        List<CfgActivityEntity> cas = typeEnums.stream().map(typeEnum -> ActivityTool.getActivitiesByType(typeEnum).get(0)).collect(Collectors.toList());
        List<GameActivity> gas = gameDataService.getGameDatas(GameActivity.class);
        // 如果已存在生效中的实例，则删除
        List<GameActivity> gasToDel = getGameActivitiesToDel(gas, 0, cas, beginDate);
        if (groupIds.size() > 1) {
            for (int groupId : groupIds) {
                gasToDel.addAll(getGameActivitiesToDel(gas, groupId, cas, beginDate));
            }
        }
        gameActivityService.delGameActivities(gasToDel);

        //新增新实例
        List<GameActivity> gasToAdd = new ArrayList<>();
        for (int groupId : groupIds) {
            for (CfgActivityEntity ca : cas) {
                if (GMActivityCtrl.dayActivityIds.contains(ca.getType())) {
                    int days = DateUtil.getDaysBetween(beginDate, endDate);
                    for (int i = 0; i <= days; i++) {
                        GameActivity gameActivity = null;
                        if (i == 0) {
                            gameActivity = GameActivity.fromActivity(ca, beginDate, DateUtil.getDateEnd(beginDate));
                        } else {
                            Date b = DateUtil.addDays(beginDate, i);
                            b = DateUtil.getDateBegin(b);
                            Date e = DateUtil.getDateEnd(b);
                            gameActivity = GameActivity.fromActivity(ca, b, e);
                        }
                        gameActivity.setServerGroup(groupId);
                        if (null != sign){
                            gameActivity.setSign(sign);
                        }
                        gasToAdd.add(gameActivity);
                    }

                } else {
                    GameActivity gameActivity = GameActivity.fromActivity(ca, beginDate, endDate);
                    gameActivity.setServerGroup(groupId);
                    if (null != sign){
                        gameActivity.setSign(sign);
                    }
                    gasToAdd.add(gameActivity);
                }
                log.info("{}初始化完成{}~{}", ca.getName(), begin, end);
            }
        }
        gameActivityService.addGameActivities(gasToAdd);
        return Rst.businessOK();
    }

    private List<GameActivity> getGameActivitiesToDel(List<GameActivity> gas, int group, List<CfgActivityEntity> cas, Date begin) {
        List<GameActivity> gasToDel = new ArrayList<>();
        for (CfgActivityEntity ca : cas) {
            // 如果已存在生效中的实例，则删除
            List<GameActivity> toDels = gas.stream()
                    .filter(tmp -> tmp.getServerGroup() == group && tmp.getType().intValue() == ca.getType() && tmp.getEnd().after(begin))
                    .collect(Collectors.toList());
            gasToDel.addAll(toDels);
        }
        return gasToDel;
    }

    private void checkDelSinceDate(Date sinceDate) {
        Date now = DateUtil.now();
        if (DateUtil.getDaysBetween(now, sinceDate) <= 60) {
            throw ExceptionForClientTip.fromMsg("不能删除未来60天内的活动数据");
        }
    }

    /**
     * 根据dataId 关闭全服活动
     * @param dataIds
     * @param beginTime
     * @param endTime
     * @return
     */
    @GetMapping("server!closeGameActivity")
    public Rst closeGameActivity(String dataIds,String beginTime,String endTime){
        Date beginDate = DateUtil.fromDateTimeString(beginTime);
        Date endDate = DateUtil.fromDateTimeString(endTime);
        if (beginDate.after(endDate)) {
            return Rst.businessFAIL("活动开始时间需早于结束时间");
        }
        //dataId 集合
        List<Long> dataIdList = ListUtil.parseStrToLongs(dataIds);
        List<GameActivity> gameActivities = new ArrayList<>();
        for (Long dataId : dataIdList) {
            GameActivity gameActivity = gameDataService.getGameData(dataId, GameActivity.class);
            if (null == gameActivity) {
                continue;
            }
            gameActivity.setBegin(beginDate);
            gameActivity.setEnd(endDate);
            gameActivities.add(gameActivity);
        }
        gameDataService.updateGameDatas(gameActivities);
        return Rst.businessOK();
    }
    @GetMapping("/game/sendQuestionnaireAward")
    public Rst sendQuestionnaireAward(int serverGroup) {
        Rst rst = Rst.businessOK();
        List<Long> uids = getQuestionnaireUids(serverGroup, rst);
        List<String> failNicknames = new ArrayList<>();
        List<UserMail> userMails = new ArrayList<>();
        Date date = DateUtil.fromDateTimeString("2020-12-04 00:00:00");
        List<Award> awards = new ArrayList<>();
        awards.add(new Award(TreasureEnum.XZY.getValue(), AwardEnum.FB, 1));
        awards.add(new Award(TreasureEnum.SHSJT.getValue(), AwardEnum.FB, 1));
        awards.add(new Award(TreasureEnum.QXC.getValue(), AwardEnum.FB, 1));
        awards.add(new Award(TreasureEnum.QL.getValue(), AwardEnum.FB, 1));
        awards.add(new Award(TreasureEnum.BBX.getValue(), AwardEnum.FB, 1));
        for (Long uid : uids) {
            UserLoginInfo loginInfo = gameUserService.getSingleItem(uid, UserLoginInfo.class);
            if (null != loginInfo) {
                Date lastLoginTime = loginInfo.getLastLoginTime();
                if (lastLoginTime.after(date)) {
                    String title = LM.I.getMsgByUid(uid,"mail.questionnaire.award.title");
                    String content = LM.I.getMsgByUid(uid,"mail.questionnaire.award.content");
                    UserMail userMail = UserMail.newAwardMail(title, content, uid, awards);
                    userMails.add(userMail);
                    continue;
                }
            }
            String nickname = gameUserService.getGameUser(uid).getRoleInfo().getNickname();
            failNicknames.add(nickname);
        }
        rst.put("failNicknames", failNicknames);
        gameUserService.updateItems(userMails);
        return rst;
    }

    private List<Long> getQuestionnaireUids(int serverGroup, Rst rst) {
        List<Long> uids = new ArrayList<>();
        List<String> notExistsUsers = new ArrayList<>();
        String filePath = "";
        if (20 == serverGroup) {
            filePath = "config/game/questionnaire/qudao.txt";
        } else if (16 == serverGroup) {
            filePath = "config/game/questionnaire/mailiang.txt";
        } else if (50 == serverGroup) {
            filePath = "config/game/questionnaire/youdong.txt";
        }
        URL url = this.getClass().getClassLoader().getResource(filePath);
        if (null == url) {
            return new ArrayList<>();
        }
        FileReader fileReader = null;
        BufferedReader bufferedReader = null;
        try {
            File file = new File(URLDecoder.decode(url.getFile(), "UTF-8"));
            fileReader = new FileReader(file);
            bufferedReader = new BufferedReader(fileReader);
            String str = "";
            while (null != (str = bufferedReader.readLine())) {
                String[] split = str.split(",");
                String serverName = split[0];
                String nickname = split[1];
                CfgServerEntity server = null;
                try {
                    server = ServerTool.getServer(serverName);
                } catch (Exception e) {
                    notExistsUsers.add(str);
                    continue;
                }
                Optional<Long> optional = serverUserService.getUidByNickName(server.getMergeSid(), nickname);
                if (optional.isPresent()) {
                    uids.add(optional.get());
                } else {
                    notExistsUsers.add(str);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            try {
                if (null != bufferedReader) {
                    bufferedReader.close();
                }
                if (null != fileReader) {
                    fileReader.close();
                }
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }
        rst.put("notExistsUsers", notExistsUsers);
        return uids;
    }
}
